// ListIterater can be used to look at the contents of the floor queues for 
// debug/display purposes...
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class Floor.
 */
public class Floor {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Floor.class.getName());
	
	/** The num floors. MUST be initialized in the constructor */
	//private final int NUM_FLOORS;
	
	// add queues to track the up requests and down requests...
	
	/**
	 * Instantiates a new floor.
	 *
	 * @param qSize the q size
	 */
	public Floor(int qSize) {
		//NUM_FLOORS = qSize;
		LOGGER.setLevel(Level.OFF);
		// add additional initialization here
	}
	
	/**
	 * Enable logging.
	 */
	public void enableLogging() {
		LOGGER.setLevel(Level.INFO);
	}
	
	/**
	 * Sets the logger FH.
	 *
	 * @param fh the new logger FH
	 */
	public void setLoggerFH(FileHandler fh) {
		LOGGER.addHandler(fh);
	}

}
