package au.csiro.mdebris.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.csiro.mdebris.R.RFactory;

public class TestEnviron extends HttpServlet 
{
	private static String FSHOME = null;
	  public void init(ServletConfig config) throws ServletException {

	    FSHOME = config.getInitParameter("FSHOME");
	    System.out.println("FSHOME:" + FSHOME);
	  }

	  public void destroy() {

	  }

	  
	  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	  {
		  
		  PrintWriter writer = res.getWriter();
		  
		  writer.println("<HTML><Body><br>");
		  writer.println("System env::<br>");
		  writer.println("<br>");
		  Iterator<?> keys = System.getenv().keySet().iterator();
		  while (keys.hasNext())
		  {
			  Object k = keys.next();
			  writer.println(String.valueOf(k)+": "+String.valueOf(System.getenv().get(k))+"<br>");
		  }
		  writer.println("<br><br><br>");
		  writer.println("System props:: <br>");
		  writer.println("<br>");
		  keys = System.getProperties().keySet().iterator();
		  while (keys.hasNext())
		  {
			  Object k = keys.next();
			  writer.println(String.valueOf(k)+": "+String.valueOf(System.getProperties().get(k))+"<br>");
		  }
		  
		  
		  writer.println("<br></body></HTML>");
	  }
	  
	  
	  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		    this.doGet(req, res);
	  }
}
