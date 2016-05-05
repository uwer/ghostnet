package au.csiro.mdebris.eval;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.csiro.mdebris.R.RFactory;


public class GhostNetEvalServlet extends HttpServlet  {


	  private static RFactory re  = null;

	  private static String R_SCRIPT = null;
	  private static String R_DATA_BASE   = null;
	  private static String FSHOME = null;
	  private static String WS_BASE = null;
	  
	  
	  
	  /*
	   * initializes the R singleton,
	   * reads the context parameters
	   */
	  public void init(ServletConfig config) throws ServletException {

	    re = RFactory.getInstance();

	    FSHOME = config.getInitParameter("FSHOME");
	    R_SCRIPT   = config.getInitParameter("R_SCRIPT");
	    R_DATA_BASE  = config.getInitParameter("R_DATA_BASE");
	    WS_BASE = config.getInitParameter("WS_BASE");
	    
	    System.out.println("R_DATA_BASE:" + R_DATA_BASE);
	    System.out.println("R_SCRIPT_BASE:" + R_SCRIPT);
	    System.out.println("FSHOME:" + FSHOME);
	    System.out.println("WS_BASE:" + WS_BASE);
	  }

	  public void destroy() {
	    if(re != null)
	      re.destroy();
	  }

	  
	  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	  {

		
	    /*
	     * generate R script from the fragments
	     */
	    String script         = R_SCRIPT;
	    



	    String[] sline = script.split("\n");
	    /*
	     * generate R image
	     */
	    for(String rLine : sline)
	      re.eval(rLine);

	    
	    
	    
	    // send to JSP
	    // set the result fragment to be displayed in the forward request
	    req.setAttribute("GN1_result", "");
	    req.getRequestDispatcher("renderGN1.jsp").forward(req, res);

	  }

	  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    this.doGet(req, res);
	  }
}
