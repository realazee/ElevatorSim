//By Aaron Zheng, Mihir Mirchandani
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
	private GenericQueue<Passengers> upQueue;
	private GenericQueue<Passengers> downQueue;
	
	/**
	 * Instantiates a new floor.
	 *
	 * @param qSize the q size
	 */
	public Floor(int qSize) {
		upQueue = new GenericQueue(qSize);
		downQueue = new GenericQueue(qSize);
		
		//NUM_FLOORS = qSize;
		LOGGER.setLevel(Level.OFF);
		// add additional initialization here
	}
	
	public GenericQueue getUpQueue() {
		return upQueue;
	}

	public void setUpQueue(GenericQueue upQueue) {
		this.upQueue = upQueue;
	}

	public GenericQueue getDownQueue() {
		return downQueue;
	}

	public void setDownQueue(GenericQueue downQueue) {
		this.downQueue = downQueue;
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
	
	
	
	
	//update addUpQueue, addDownQueue, remove, get, anything that edits data within queue
	//wrapper methods
	
	public void addUpQueue(Passengers p) {
		upQueue.add(p);
	}
	
	public void addDownQueue(Passengers p) {
		downQueue.add(p);
	}
	
	public  Passengers removeUpQueue() {
		return upQueue.remove();
	}
	
	public Passengers removeDownQueue() {
		return downQueue.remove();
	}
	
	//isUpQueueEmpty,  isDownQueueEmpty, peakUpQueue, peakDownQueue
	
	public boolean isUpQueueEmpty() {
		return upQueue.isEmpty();
	}
	
	public boolean isDownQueueEmpty() {
		return downQueue.isEmpty();
	}
	
	public Passengers peekUpQueue() {
		return upQueue.peek();
	}
	
	public Passengers peekDownQueue() {
		return downQueue.peek();
	}
	
	
}
