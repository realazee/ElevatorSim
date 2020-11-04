//Authors: Mihir Mirchandani and Alex Zheng

import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 */

/**
 * @author ScottM
 * This class will represent an elevator, and will contain
 * configuration information (capacity, speed, etc) as well
 * as state information - such as stopped, direction, and count
 * of passengers targetting each floor...
 */
public class Elevator {
	private final static Logger LOGGER = Logger.getLogger(Elevator.class.getName());
	// Elevator State Variables
	private final static int UNDEF = -1;
	private final static int STOP = 0;
	private final static int MVTOFLR = 1;
	private final static int OPENDR = 2;
	private final static int OFFLD = 3;
	private final static int BOARD = 4;
	private final static int CLOSEDR = 5;
	private final static int MV1FLR = 6;

	// Configuration parameters
	private int capacity = 15;
	private int ticksPerFloor = 5;
	private int ticksDoorOpenClose = 2;  
	private int passPerTick = 3;
	
	//State Variables
	// track the elevator state
	private int currState;
	private int prevState;
	// track what floor you are on, and where you came from
	private int prevFloor;
	private int currFloor;
	// direction 1 = up, -1 = down
	private int direction;
	// timeInState is reset on state entry, used to determine if state is finished
	// or if floor has changed...
	private int timeInState;
	// used to track where the the door is in OPENDR and CLOSEDR states 
	private int doorState;
	// number of passengers on the elevator
	private int passengers;
	// when exiting the stop state, the floor to moveTo and the direction to go in once you
	// get there...
	private int moveToFloor;
	private int moveToFloorDir;

	
	public Elevator(int numFloors) {		
		this.prevState = UNDEF;
		this.currState = STOP;
		
		LOGGER.setLevel(Level.OFF);
	}

	public void enableLogging() {
		LOGGER.setLevel(Level.INFO);
	}

	public void setLoggerFH(FileHandler fh) {
		LOGGER.addHandler(fh);
	}

}
