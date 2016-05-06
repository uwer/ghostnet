package au.csiro.mdebris.eval;
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

import au.csiro.mdebris.R.RenjinServletBase;


public class GhostNetEvalServlet extends RenjinServletBase  
{


	  private static String R_SCRIPT = null;
	  private static String R_DATA_BASE   = null;
	  private static String FSHOME = null;
	  private static String WS_BASE = null;
	  
	  
	  
	  /*
	   * initializes the R singleton,
	   * reads the context parameters
	   */
	  public void init(ServletConfig config) throws ServletException 
	  {

	    FSHOME = config.getInitParameter("FSHOME");
	    R_SCRIPT   = config.getInitParameter("R_SCRIPT");
	    R_DATA_BASE  = config.getInitParameter("R_DATA_BASE");
	    WS_BASE = config.getInitParameter("WS_BASE");
	    
	  //TODO-UR disable this
	    System.out.println("R_DATA_BASE:" + R_DATA_BASE);
	    System.out.println("R_SCRIPT:" + R_SCRIPT);
	    System.out.println("FSHOME:" + FSHOME);
	    System.out.println("WS_BASE:" + WS_BASE);
	  }
	  
	  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	  {
/*
 * 
 params: f_mesh_id, f_knots_id, f_sdtwine_id, f_tbmtwine_id 
 */
		
	    /*
	     * generate R script from the fragments
	     */
	    String script         = R_SCRIPT;
	    String f_mesh_id = req.getParameter("f_mesh_id"); 
	    String f_knots_id = req.getParameter("f_knots_id"); 
	    String f_sdtwine_id = req.getParameter("f_sdtwine_id"); 
	    String f_tbmtwine_id = req.getParameter("f_tbmtwine_id"); 
	  //TODO-UR disable this
	    System.out.println("GhostNetEvalServlet - Have params: "+f_mesh_id+", "+f_knots_id+", "+f_sdtwine_id+", "+f_tbmtwine_id);

   	    
	 // Obtain the script engine for this thread
		ScriptEngine engine = getScriptEngine();
		if(engine == null) 
		{
			//TODO-UR disable this
			System.out.println("Renjin Script Engine not found in the classpath.");
		}else
		{
			//TODO-UR disable this
			System.out.println("Renjin Script Engine found as: "+engine.getClass().getName());
			
			// probably need to make an array
			engine.put("meshID", f_mesh_id);
			
			Reader reader = new FileReader(script);
            
            try {
                engine.eval(reader);
            } catch (ScriptException e) {
                handleException(res, e);
                return;
            } 
		}
	    // send to JSP
	    // set the result fragment to be displayed in the forward request
	    req.setAttribute("GN1_result", "");
	    req.getRequestDispatcher("renderGN1test.jsp").forward(req, res);

	  }

	  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    this.doGet(req, res);
	  }
}
