package au.csiro.mdebris.R;

import java.io.IOException;
import java.io.PrintStream;

import javax.script.ScriptEngine;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.renjin.script.RenjinScriptEngineFactory;

public abstract class RenjinServletBase extends HttpServlet
{
	
	private static final ThreadLocal<ScriptEngine> ENGINE = new ThreadLocal<ScriptEngine>();
	
	protected ScriptEngine getScriptEngine() 
	{
		ScriptEngine engine = ENGINE.get();
		if(engine == null) {
			// Create a new ScriptEngine for this thread if one does not exist.
			RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
			engine = factory.getScriptEngine();
			ENGINE.set(engine);
		}
		return engine;
	}
	
	
	public void destroy() {
		
	}
	
    protected void handleException(HttpServletResponse resp, Exception e) throws IOException, ServletException {
        PrintStream out = new PrintStream(resp.getOutputStream());
        e.printStackTrace(out);
        out.flush();
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
	
	
}
