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
	/*
	private int capacity = 15;
	private int ticksPerFloor = 5;
	private int ticksDoorOpenClose = 2;  
	private int passPerTick = 3;
	*/
	private int capacity;
	private int ticksPerFloor;
	private int ticksDoorOpenClose;  
	private int passPerTick;
	
	
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

	
	public Elevator(int capacity, int ticksPerFloor, int ticksDoorOpenClose, int passPerTick) {		
		this.prevState = UNDEF;
		this.currState = STOP;
		this.currFloor = 0;
		this.capacity = capacity;
		this.ticksPerFloor = ticksPerFloor;
		this.ticksDoorOpenClose = ticksDoorOpenClose;
		this.passPerTick = passPerTick;
		
		LOGGER.setLevel(Level.OFF);
	}

	public int getCapacity() {
		return capacity;
	}

	

	public int getTicksPerFloor() {
		return ticksPerFloor;
	}

	

	public int getTicksDoorOpenClose() {
		return ticksDoorOpenClose;
	}

	

	public int getPassPerTick() {
		return passPerTick;
	}



	public int getCurrState() {
		return currState;
	}

	public void setCurrState(int currState) {
		this.currState = currState;
	}

	public int getPrevState() {
		return prevState;
	}

	public void setPrevState(int prevState) {
		this.prevState = prevState;
	}

	public int getPrevFloor() {
		return prevFloor;
	}

	public void setPrevFloor(int prevFloor) {
		this.prevFloor = prevFloor;
	}

	public int getCurrFloor() {
		return currFloor;
	}

	public void setCurrFloor(int currFloor) {
		this.currFloor = currFloor;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getTimeInState() {
		return timeInState;
	}

	public void setTimeInState(int timeInState) {
		this.timeInState = timeInState;
	}

	public int getDoorState() {
		return doorState;
	}

	public void setDoorState(int doorState) {
		this.doorState = doorState;
	}

	public int getPassengers() {
		return passengers;
	}

	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	public int getMoveToFloor() {
		return moveToFloor;
	}

	public void setMoveToFloor(int moveToFloor) {
		this.moveToFloor = moveToFloor;
	}

	public int getMoveToFloorDir() {
		return moveToFloorDir;
	}

	public void setMoveToFloorDir(int moveToFloorDir) {
		this.moveToFloorDir = moveToFloorDir;
	}
	
	//helpers
	
	
	public void updateCurrState(int newCurrState) {
		prevState = currState;
		if(currState == newCurrState) {
			timeInState++;
		}
		else if(!(currState == newCurrState)) {
			currState = newCurrState;
			timeInState = 0;
		}
		
	}
	
	public void moveElevator() {
		timeInState++;
		if(timeInState % ticksPerFloor == 0) {
			currFloor += direction;
		}
		
	}
	public void closeDoor() {
		updateCurrState(CLOSEDR);
		doorState--;
	}
	
	public void openDoor() {
		updateCurrState(OPENDR);
		doorState++;
		
	}
	
	
	public boolean isDoorClosed(){
		if(doorState == 0) {
			return true;
		}
		else {
			return false;
		}
		
	}
	public boolean isDoorOpen() {
		if(doorState == ticksDoorOpenClose) {
			return true;
		}
		else {
			return false;
		}
	}
	
	

	public void enableLogging() {
		LOGGER.setLevel(Level.INFO);
	}

	public void setLoggerFH(FileHandler fh) {
		LOGGER.addHandler(fh);
	}

	public boolean isElevatorFull() {
		return(this.getCapacity() == this.getPassengers()); 
	}
	
	public boolean isCallsFromAbove() {
		return(this.getMoveToFloor() > this.getCurrFloor());
	}
	
	public boolean isCallsFromBelow() {
		return(this.getMoveToFloor() < this.getCurrFloor());
	}
	public boolean isCallsOnCurrFloor(){
		return(this.getMoveToFloor()== this.getCurrFloor());
	}
	
	
	
	
	
}
