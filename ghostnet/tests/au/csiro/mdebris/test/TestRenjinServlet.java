package au.csiro.mdebris.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.renjin.sexp.StringVector;

import au.csiro.mdebris.R.RenjinServletBase;


public class TestRenjinServlet extends RenjinServletBase 
{

	
	private static String FSHOME = null;
	
	public void init(ServletConfig config) throws ServletException {
	
		//FSHOME = config.getInitParameter("FSHOME");
		//System.out.println("FSHOME:" + FSHOME);
	}
	
	  
	  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	  {
		  PrintWriter writer = res.getWriter();
		  writer.println("<HTML><Body><br>");
		// Obtain the script engine for this thread
			ScriptEngine engine = getScriptEngine();
			if(engine == null) 
			  {
				  writer.println("Renjin Script Engine not found in the classpath.");
			  }else
			  {
				  writer.println("Renjin Script Engine found as: "+engine.getClass().getName());
				  
			// Read the ?sd parameter as the standard deviation 
			// and assign it to the variable "sd" in the R session
			String sd = req.getParameter("sd");
			if(sd == null) {
				engine.put("sd", 1.0);
			} else {
				engine.put("sd", Double.parseDouble(sd));
			}
			
			StringVector result;
			try {
				result = (StringVector)engine.eval(
						"df <- data.frame(x=1:10, y=(1:10)+rnorm(sd, n=10));" +
						"x <- lm(y ~ x, df);" +
						"rjson::toJSON(x$coefficients)");
			} catch (ScriptException e) {
				throw new ServletException(e);
			}
			
			
			
		  
		  
		  res.setContentType("application/json");
		  writer.println(result.getElementAsString(0));
			  
		  }
		  
		  writer.println("<br></body></HTML>");
	  }
	  
	  
	  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		    this.doGet(req, res);
	  }
}
