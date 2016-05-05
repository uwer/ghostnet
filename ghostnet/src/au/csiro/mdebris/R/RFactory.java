package au.csiro.mdebris.R;

import org.rosuda.JRI.Rengine;

public class RFactory {

	private static RFactory theInstance;
	private final Rengine engine;

	private RFactory(Rengine engine) {
		this.engine = engine;
	    this.engine.idleDelay = 500;
	    Rengine.DEBUG = 10;
	}

	public static synchronized RFactory getInstance() {

	  if (theInstance == null)
	    	RFactory.initialize("");

	    return theInstance;
	  }

	  public static synchronized void initialize(String loopback) {

	    if (theInstance != null)
	      throw new IllegalStateException("already initialized");

	    theInstance = new RFactory( new Rengine( new String[] {"--vanilla"}, false, null ) );
	  }

	  /* wrapper methods for Rengine */
	  public synchronized long getVersion() {
	    return Rengine.getVersion();
	  }

	  public synchronized void eval(String s) {
	    engine.eval(s);
	  }

	  public static synchronized Rengine getMainEngine() {
	    return RFactory.getMainEngine();
	  }

	  public static synchronized boolean waitForR() {
	    return RFactory.waitForR();
	  }

	  public synchronized void destroy() {
	    engine.end();
	  }
}
