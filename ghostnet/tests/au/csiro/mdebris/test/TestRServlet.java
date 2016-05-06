package au.csiro.mdebris.test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import au.csiro.mdebris.R.RFactory;

public class TestRServlet extends HttpServlet  {

	  private static RFactory re  = null;
	  private static String FSHOME = null;

	  
	  
	  
	  /*
	   * initializes the R singleton,
	   * reads the context parameters
	   */
	  public void init(ServletConfig config) throws ServletException {

	    re = RFactory.getInstance();
	    
	    FSHOME = config.getInitParameter("FSHOME");
	    System.out.println("FSHOME:" + FSHOME);
	  }

	  public void destroy() {
	    if(re != null)
	      re.destroy();
	  }

	  
	  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	  {


			System.out.println("Loading REngine...");
			Rengine engine = Rengine.getMainEngine();
			if(engine == null)
			    engine = new Rengine(new String[] {"--vanilla","--no-environ"}, false, null);
			
			System.out.println("Rengine created, waiting for R");
			
			if (!engine.waitForR()) {
			    System.out.println("Cannot load R");
			    return;
			}
			System.out.println("R loaded!");
			
			engine.eval("f1 <- function(a, b) {  return(a / b)  }");
			
			REXP result = engine.eval("f1(10, 2)");
	    
			System.out.println("Result : "+String.valueOf(result));
		    // send to JSP
		    // set the result fragment to be displayed in the forward request
			
		    //req.setAttribute("GN1_result", "");
		    //req.getRequestDispatcher("renderGN1.jsp").forward(req, res);
		    
			PrintWriter writer = res.getWriter();
		    writer.println("<HTML><body>");
		    writer.println("Test result is: "+String.valueOf(result));
		    writer.println("</body></html>");
		    writer.flush();
	  }

	  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	    this.doGet(req, res);
	  }
}