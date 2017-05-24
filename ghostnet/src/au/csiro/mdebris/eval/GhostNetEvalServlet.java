package au.csiro.mdebris.eval;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.renjin.eval.EvalException;

import au.csiro.mdebris.R.RenjinServletBase;


public class GhostNetEvalServlet extends RenjinServletBase  
{


	  private static String R_FUNC_SCRIPT = null;
	  private static String R_DATA_BASE   = null;
	  private static String FSHOME = null;
	  private static String WS_BASE = null;
	  private static String R_DATA_SCRIPT   = null;
	  private static String R_FUNCTION   = null;
	  private static String JSP_TARGET_BASE   = null;
	  
	  private static final String[] LABELS = new String[]{"Cluster1","Cluster2","Cluster3","Cluster4","Cluster5","Cluster6","Cluster7","Cluster8","Cluster9","Cluster10","Cluster11","Cluster12","Cluster13","Cluster14"};
	  private static final String CATEGORIES_NAME = "DiscreteProbs";
	  private static final String CMEANS_NAME = "ContinuousMeans";
	  private static final String CCSIGMAS_NAME = "ContinuousSigmas";
	  private static final String COEEFICIENTS_NMAE = "coefficient.estimates";
	  
	  
	  private static org.renjin.sexp.Vector CATEGORIES = null;
	  private static org.renjin.sexp.Vector CMEANS = null;
	  private static org.renjin.sexp.Vector CCSIGMAS = null;
	  private static org.renjin.sexp.Vector COEEFICIENTS = null;
	  
	  /*
	   * initializes the R singleton,
	   * reads the context parameters
	   */
	  public void init(ServletConfig config) throws ServletException 
	  {

	    FSHOME = config.getInitParameter("FSHOME");
	    R_FUNC_SCRIPT   = config.getInitParameter("R_FUNC_SCRIPT");
	    R_FUNCTION   = config.getInitParameter("R_FUNCTION");
	    R_DATA_SCRIPT   = config.getInitParameter("R_DATA_SCRIPT");
	    R_DATA_BASE  = config.getInitParameter("R_DATA_BASE");
	    WS_BASE = config.getInitParameter("WS_BASE");
	    JSP_TARGET_BASE = config.getInitParameter("JSP_TARGET_BASE");
	    
	  //TODO-UR disable this
	    System.out.println("R_DATA_BASE:" + R_DATA_BASE);
	    System.out.println("R_FUNC_SCRIPT:" + R_FUNC_SCRIPT);
	    System.out.println("R_DATA_SCRIPT:" + R_DATA_SCRIPT);
	    System.out.println("FSHOME:" + FSHOME);
	    System.out.println("WS_BASE:" + WS_BASE);
	    System.out.println("R_FUNCTION:" + R_FUNCTION);
	    System.out.println("JSP_TARGET_BASE:" + JSP_TARGET_BASE);
	    
	    if (JSP_TARGET_BASE == null)
	    	JSP_TARGET_BASE = "fragments";
	  }
	  
		public void destroy() 
		{
			System.out.println("Attempting to remove data");
			ScriptEngine engine = getScriptEngine();
			
			engine.put(CCSIGMAS_NAME, "");
    		engine.put(CMEANS_NAME, "");
    		engine.put(CATEGORIES_NAME, "");
    		super.destroy();
		}
	  
	  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	  {
/*
 * 
 params: f_mesh_id, f_knots_id, f_sdtwine_id, f_tbmtwine_id 
 */
		
	    /*
	     * generate R script from the fragments
	     * not all are used right now 
	     */
	    String f_mesh_id = req.getParameter("f_mesh_id"); 
	    String f_knots_id = req.getParameter("f_knots_id"); 
	    String f_sdtwine_id = req.getParameter("f_sdtwine_id"); 
	    String f_sizetwine_id = req.getParameter("f_sizetwine_id"); 
	    String f_tbmtwine_id = req.getParameter("f_tbmtwine_id");
	    String f_colour_id = req.getParameter("f_colour_id");
	    String f_strandstwine_id = req.getParameter("f_strandstwine_id");
	  //TODO-UR disable this
	    //String debug = "GhostNetEvalServlet - Have params: "+f_mesh_id+", "+f_knots_id+", "+f_sdtwine_id+", "+f_tbmtwine_id+", "+f_colour_id;
	    //System.out.println(debug);
	    int result = -1;
   	    
	 // Obtain the script engine for this thread
		ScriptEngine engine = getScriptEngine();
		if(engine == null) 
		{
			//TODO-UR disable this
			System.out.println("Renjin Script Engine not found in the classpath.");
		}else
		{
			//TODO-UR disable this
			//System.out.println("Renjin Script Engine found as: "+engine.getClass().getName());
			
			// probably need to make an array
			// set the parameters as var is in the current engine
			//engine.put("meshID", f_mesh_id);
			
			
			
			Reader reader;
			
            try {
            	File f = new File(FSHOME);
            	
            	// read from file 
            	if (CATEGORIES == null)
            	{
	            	reader = new FileReader(new File(f.getAbsolutePath(),R_DATA_SCRIPT));
	            	//set the cwd to the R root to read data
	            	engine.eval("setwd(\""+new File(f.getAbsolutePath(),R_DATA_BASE).getAbsolutePath()+"\")");
	                // exec the script fragment for reading the data
	            	engine.eval(reader);
	                
	            	System.out.println("Attempting to retrieve data");
	            	CATEGORIES = ( org.renjin.sexp.Vector)engine.eval(CATEGORIES_NAME);
	            	CMEANS = ( org.renjin.sexp.Vector)engine.eval(CMEANS_NAME);
	            	CCSIGMAS = ( org.renjin.sexp.Vector)engine.eval(CCSIGMAS_NAME);
	            	
            	}else
            	{
            		System.out.println("Attempting to set data");
            		engine.put(CCSIGMAS_NAME, CCSIGMAS);
            		engine.put(CMEANS_NAME, CMEANS);
            		engine.put(CATEGORIES_NAME, CATEGORIES);
            	}
            	
            	reader = new FileReader(new File(f.getAbsolutePath(),R_FUNC_SCRIPT));
            	engine.eval(reader);
            	
            	// Testing
            	// UR disable
            	//reader = new FileReader(new File(f.getAbsolutePath(),"/tmp/create_fdata.r"));
            	//engine.eval(reader);
            	
                // create tmp workspace
                f = new File(WS_BASE);
                String tmpDir = String.valueOf(System.currentTimeMillis());
                f = new File(f.getAbsolutePath(),tmpDir);
                f.mkdirs();
                // and go there
                engine.eval("setwd(\""+f.getAbsolutePath()+"\")");
                
                //now assign the data and call function
                // execute the calculation with tmp files going into the tmp ws
                
                // the result is a R array
                // test existence of function
                /* v-0.2 */
                if (f_mesh_id != null)
                {
                /*
                 	"Twisted.3.Single"
                 	,"Twisted.5.Single",
                 	"Twisted.4.Single",
                 	"Braided.Single",
                 	"Twisted.3.Double",
                 	"Mono.Single",
                 	"Twisted.13.Single",
                 	"Twisted.2.Single",
                 	"Twisted.2.Double",
                 	"Twisted.5.Double",
                 	"Twisted.4.Double"
                 */
                	String twineType ;
                	if("Twisted".equals(f_tbmtwine_id)) // twisted
                	{
                		twineType = f_tbmtwine_id+"."+f_strandstwine_id+"."+f_sdtwine_id;
                	}else
                		twineType = f_tbmtwine_id+".Single";
                	
                	String reqString = f_mesh_id+","+f_sizetwine_id+",'"+f_colour_id+"','"+twineType+"'";
                	System.out.println("Submitting request String: "+reqString);
                	engine.eval("res = "+R_FUNCTION+"("+reqString+")");
                
                }else
                	engine.eval("res = "+R_FUNCTION+"(40,2,'WHITE','Twisted.4.Single')");
                
                
                org.renjin.sexp.Vector x = (org.renjin.sexp.Vector) engine.eval("res");
                //result = engine.eval("as.integer(res[1])");
                result = x.getElementAsInt(0);
                		
                // this isn't returning a clean array
                //Object probsAcross = engine.eval("tail(res,14) * 100.");
                
                double probsTotal = x.getElementAsDouble(1);//engine.eval("res[2]");
                double probs = x.getElementAsDouble(1+result);
                
                //System.out.println("R results "+probsAcross);
                System.out.println("R index: "+result + " total: "+probsTotal);
                
                if (probs > 0.0001)
                {	

                    req.setAttribute("probsIndex",String.format("%1$.4f",(probs*100)));
                    req.setAttribute("probsTotal",String.format("%1$.4f",(probsTotal* 100.)));
                    
	                //String[] probs = String.valueOf(probsAcross).split(" ");
	                StringBuffer b = new StringBuffer();
	                StringBuffer lb = new StringBuffer();
	                StringBuffer ref = new StringBuffer();
	                
	                b.append("[");
	                lb.append("[");
	                int nprobs = 14;
	                //for(int i = 0;i < probs.length;i++)
	                int count = 0;
	                for(int i = 0;i < nprobs;i++)
	                {
	                	double prob = x.getElementAsDouble(i+2) * 100.;
	                	//System.out.println("Probability at index "+i+ ": "+prob);
	                	if( prob >= 0.0001 )
	                	{	
	                		b.append(String.valueOf(prob));
	                		lb.append("'"+LABELS[i]+"'");
	                		ref.append(JSP_TARGET_BASE+"/Cluster"+i+"/Cluster"+i+".txt");
		                	if( i< nprobs-1)
		                	{
		                		b.append(",");
		                		lb.append(",");
		                		ref.append(",");
		                	}
		                	count++;
	                	}
	                }
	                b.append("]");
	                lb.append("]");
	                System.out.println("plot data: "+b.toString());

	                if(count > 1)
	                {
	                    req.setAttribute("probChartData",b.toString());
		                req.setAttribute("probChartLabels",lb.toString());
		                req.setAttribute("probReferences",ref.toString());
		           //     req.setAttribute("probChartData",b.toString());
		           //     req.setAttribute("probChartLabels","['Cluster1','Cluster2','Cluster3','Cluster4','Cluster5','Cluster6','Cluster7','Cluster8','Cluster9','Cluster10','Cluster11','Cluster12','Cluster13','Cluster14']");
	                }
	                
                }else
                {
                	// we don't have anything decent 
                	// drop to default page
                	result=0;
                }
                //req.setAttribute("GN1_result", "Cluster"+String.valueOf(result)+"/Cluster"+String.valueOf(result)+".txt");
                
                /* */
                /* v-0.1 
                engine.eval("res = "+R_FUNCTION+"(Data1)");
                result = engine.eval("res[1,5]");
                System.out.println("R results "+result);
                //req.setAttribute("GN1_result", String.valueOf(result));
                 
                 */
                f.delete();
            } catch (ScriptException | EvalException e) {
                handleException(res, e);
                return;
            } 
		}
	    // send to JSP
	    // set the result fragment to be displayed in the forward request
		// UR remove - testing 
		/*
	    req.setAttribute("GN1_result", "/fragments/test.frag");
	    req.setAttribute("GN1_loaded", String.valueOf(result));
	    req.setAttribute("probChartData","[12,23,45,23]");
	    req.setAttribute("probChartLabels","['d1','d2','d3','d4']");
	    
	    req.getAttribute("");
	    */

		req.getRequestDispatcher(JSP_TARGET_BASE+"/Cluster"+String.valueOf(result)+".jsp").forward(req, res);
	  }

	  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    this.doGet(req, res);
	  }
}
