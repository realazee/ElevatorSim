import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.lang.Math.*;

public class Building {
	private final static Logger LOGGER = Logger.getLogger(Building.class.getName());
	private FileHandler fh;

	// Elevator State Variables
	private final static int STOP = 0;
	private final static int MVTOFLR = 1;
	private final static int OPENDR = 2;
	private final static int OFFLD = 3;
	private final static int BOARD = 4;
	private final static int CLOSEDR = 5;
	private final static int MV1FLR = 6;

	private final int NUM_FLOORS;
	private final int NUM_ELEVATORS;
	public Floor[] floors;
	private Elevator lift;
	public GenericQueue<Passengers> passQ = new GenericQueue<Passengers>(1000); // we need to edit the max passenger count.

	//mr. murray said that we do not need elevetor
	public Building(int numFloors, int numElevators, int capacity, int ticksPerFloor, int ticksDoorOpenClose, int passPerTick) {
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;

		System.setProperty("java.util.logging.SimpleFormatter.format","%4$-7s %5$s%n");
		LOGGER.setLevel(Level.OFF);
		try {
			fh = new FileHandler("elevator.log");
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create the floors
		floors = new Floor[NUM_FLOORS];
		for (int i = 0; i < NUM_FLOORS; i++) {
			floors[i]= new Floor(10); 
		}
		floors[0].setLoggerFH(fh); // only need to pass the file to one of the floors.
		lift = new Elevator(capacity, ticksPerFloor, ticksDoorOpenClose, passPerTick);
		lift.setLoggerFH(fh);
	}

	private boolean elevatorStateChanged(Elevator lift) {
		if(lift.getPrevState() == lift.getCurrState()) {
			return false;
		}
		return true;
	}
	private String printState(int liftState) {
		switch(liftState) {
			case STOP: return "STOP";
			case MVTOFLR: return "MVTOFLR";
			case OPENDR: return "OPENDR";
			case OFFLD: return "OFFLD";
			case BOARD: return "BOARD";
			case CLOSEDR: return "CLOSEDR";
			case MV1FLR: return "MV1FLR";
			default: return "";
		}
	}
	public void updateElevator(int time) {
		System.out.println("Current state: " + lift.getCurrState());
		
		if (elevatorStateChanged(lift)) 
			LOGGER.info("Time="+time+"   Prev State: " + printState(lift.getPrevState()) + "   Curr State: "+printState(lift.getCurrState())
						+"   PrevFloor: "+(lift.getPrevFloor()+1) + "   CurrFloor: " + (lift.getCurrFloor()+1));
		
		switch (lift.getCurrState()) {
		case STOP: lift.updateCurrState(currStateStop(time,lift)); break;
		case MVTOFLR: lift.updateCurrState(currStateMvToFlr(time,lift)); break;
		case OPENDR: lift.updateCurrState(currStateOpenDr(time,lift)); break;
		case OFFLD: lift.updateCurrState(currStateOffLd(time,lift)); break;
		case BOARD: lift.updateCurrState(currStateBoard(time,lift)); break;
		case CLOSEDR: lift.updateCurrState(currStateCloseDr(time,lift)); break;
		case MV1FLR: lift.updateCurrState(currStateMv1Flr(time,lift)); break;
		}

		
	}
	//add is add to end of queue
	//remove removes the first in the queue, and returns it
	//peek returns the first in the queue but doesnt remove it.
	//returns the next passenger to serve
	private Passengers prioritizeCalls() {
		if(lift.isCallsOnCurrFloor()) {
			if(!floors[lift.getCurrFloor()].getUpQueue().isEmpty() && floors[lift.getCurrFloor()].getDownQueue().isEmpty()) {
				if(floors[lift.getCurrFloor()].getUpQueue().getSize() >= floors[lift.getCurrFloor()].getDownQueue().getSize()) {
					return floors[lift.getCurrFloor()].peekUpQueue(); 
				} else {
					return floors[lift.getCurrFloor()].peekDownQueue(); 
				}

			}
		}

		if(numUpCalls() > numDownCalls()) {
			return lowestPassengerGoingUp();
		}
		if(numUpCalls() < numDownCalls()) {
			return highestPassengerGoingDown();
		}
		if(numUpCalls() == numDownCalls()) {
			int differenceBetweenCurrentFloorAndLowestUp;
			int differenceBetweenCurrentFloorAndHighestDown;
			differenceBetweenCurrentFloorAndLowestUp = Math.abs(lift.getCurrFloor() - lowestPassengerGoingUp().getFromFloor());
			differenceBetweenCurrentFloorAndHighestDown = Math.abs(lift.getCurrFloor() - highestPassengerGoingDown().getFromFloor());
			
			if(differenceBetweenCurrentFloorAndLowestUp < differenceBetweenCurrentFloorAndHighestDown) {
				return lowestPassengerGoingUp();
			}
			else if(differenceBetweenCurrentFloorAndLowestUp > differenceBetweenCurrentFloorAndHighestDown) {
				return highestPassengerGoingDown();
			}
		}

		return lowestPassengerGoingUp();


	}



	private Passengers lowestPassengerGoingUp() {
		int lowestFloorSoFar = floors.length;
		for(int i = floors.length - 1; i > 0; i--) {
			if(!floors[i].isUpQueueEmpty()) {
				lowestFloorSoFar = i;
			}
		}
		if(floors[lowestFloorSoFar].isUpQueueEmpty()) {
			return null;
		}
		return floors[lowestFloorSoFar].peekUpQueue();

	}
	private Passengers highestPassengerGoingDown() {
		int highestFloorSoFar = 0;
		for(int i = 0; i < floors.length; i++) {
			if(!floors[i].isDownQueueEmpty()) {
				highestFloorSoFar = i;
			}
		}
		if(floors[highestFloorSoFar].isUpQueueEmpty()) {
			return null;
		}
		return floors[highestFloorSoFar].peekUpQueue();
	}


	private int numUpCalls() {
		int c = 0;
		for(int i = 0; i < floors.length; i++) {
			if(!floors[i].getUpQueue().isEmpty()) {
				c++;
			}
		}
		return c;
	}
	private int numDownCalls() {
		int c = 0;
		for(int i = 0; i < floors.length; i++) {
			if(!floors[i].getDownQueue().isEmpty()) {
				c++;
			}
		}
		return c;
	}

	public void checkPassengerQueue(int globalTime) {
		if(passQ.peek().getTime() == globalTime) {
			int targetFloor = passQ.peek().getFromFloor();
			if(passQ.peek().isGoingUp()) {
				floors[targetFloor].addUpQueue(passQ.remove());
			}
			else if(passQ.peek().isGoingDown()) {
				floors[targetFloor].addDownQueue(passQ.remove());
			}
		}
	}




	//return the next states.
	public int currStateStop(int time, Elevator lift) {

		if(noOneWaiting()) {
			return STOP;
		}
		else if (isUpCallFromCurrFloor() || isDownCallFromCurrFloor()) {
			setNextDropOffFloor();
			setNextElevatorDirection();
			return OPENDR;
		} 
		setNextPickUpFloor();
		setNextElevatorDirection();
		return MVTOFLR;	
	}

	//isUpCallFromCurrFloor()

	private boolean isUpCallFromCurrFloor() {
		if(floors[lift.getCurrFloor()].isUpQueueEmpty()) {
			return false;
		}
		return true;
	}

	//isDownCallFromCurrFloor
	private boolean isDownCallFromCurrFloor() {
		if(floors[lift.getCurrFloor()].isDownQueueEmpty()) {
			return false;
		}
		return true;
	}

	public boolean noOneWaiting() {
		for(int i = 0; i < floors.length; i++) {
			if(!floors[i].getUpQueue().isEmpty()) {
				return false;
			}
			if(!floors[i].getDownQueue().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	//passengers have been picked up, sets elevator MVTOFLR to passengers target floor.
	private void setNextDropOffFloor() {
		lift.setMoveToFloor(prioritizeCalls().getToFloor());
	}

	//passengers have not been picked up, sets elevator MVTOFLR to floor of prioritizeCalls().
	private void setNextPickUpFloor() {
		lift.setMoveToFloor(prioritizeCalls().getFromFloor());
	}

	private void setNextElevatorDirection() {

		if(lift.getMoveToFloor() > lift.getCurrFloor()) {
			lift.setDirection(1);
		} else if(lift.getMoveToFloor() < lift.getCurrFloor()) {
			lift.setDirection(-1);
		}
	}

	public int currStateMvToFlr(int time, Elevator lift) {
		//state actions
		lift.moveElevator();
		System.out.println("\n\n\n\n");
		System.out.println(lift.getCurrFloor());
		System.out.println(lift.getMoveToFloor());
		System.out.println("\n\n\n\n");
		if(lift.getCurrFloor() == lift.getMoveToFloor()) {
			System.out.println("Who's there?");
			setNextDropOffFloor();
			setNextElevatorDirection();
			return OPENDR;
		}
		return MVTOFLR;

		//		if(lift.getCurrFloor() == lift.getMoveToFloor()) {
		//			return OPENDR;
		//		}
		//		else {
		//			return MVTOFLR;
		//		}
	}


	public int currStateOpenDr(int time, Elevator lift) {
		lift.openDoor();
		if(!lift.isDoorOpen()) {
			return OPENDR;
		}
		else if(lift.isDoorOpen() && passengersGetOffAtCurrFloor()) {
			return OFFLD;
		}
		else {
			return BOARD;
		}
	}
	public boolean passengersGetOffAtCurrFloor() {
		for(int i = 0; i < lift.getOnBoard().size(); i++) {
			if(lift.getOnBoard().get(i).getToFloor() == lift.getCurrFloor()) {
				return true;
			}
		}
		return false;
	}

	//puts everyone in the current floors up or down queue into the elevator
	//if the direction of the elevator is up or down respectively.
	public void board() {



		if(lift.getDirection() == 1) {
			for(int i = 0; i < floors[lift.getCurrFloor()].getUpQueue().getSize(); i++) {
				if(floors[lift.getCurrFloor()].peekUpQueue().getNumber() <= lift.getCapacity() - lift.getOnBoard().size()) {
					amountOfTimeToBoard += amountOfTimeToBoardFirstPassOnCurrFloorInCurrDir();
					lift.getOnBoard().add(floors[lift.getCurrFloor()].removeUpQueue());
				}
			}
		}
		else if(lift.getDirection() == -1) {

			for(int i = 0; i < floors[lift.getCurrFloor()].getDownQueue().getSize(); i++) {
				if(floors[lift.getCurrFloor()].peekDownQueue().getNumber() <= lift.getCapacity() - lift.getOnBoard().size()) {
					amountOfTimeToBoard += amountOfTimeToBoardFirstPassOnCurrFloorInCurrDir();
					lift.getOnBoard().add(floors[lift.getCurrFloor()].removeDownQueue());
				}
			}
		}


	}

	int numTicksToOffload;
	public int currStateOffLd(int time, Elevator lift) {


		if(lift.getTimeInState() == 0) {
			numTicksToOffload = lift.numTicksToOffload();
			lift.offLoad();
		}

		if(lift.getOnBoard().isEmpty() && noCallsInSameDirection() && isCallInOppositeDirectionOnCurrFloor()) {
			if(lift.getDirection() == 1) {
				lift.setDirection(-1);
			}
			else if(lift.getDirection() == -1) {
				lift.setDirection(1);
			}
		}

		if(lift.getTimeInState() < numTicksToOffload) {
			return OFFLD;
		}

		else if(lift.getDirection() == 1 && !floors[lift.getCurrFloor()].isUpQueueEmpty() ||lift.getDirection() == -1 && !floors[lift.getCurrFloor()].isDownQueueEmpty()) {
			return BOARD;
		}
		else {
			return CLOSEDR;
		}

	}
	//	
	//	private boolean isCallsInSameDir() {
	//		if(prioritizeCalls().getToFloor()) {
	//			
	//		}
	//	}
	//	


	public boolean noCallsInSameDirection() {
		for(int i = 0; i < floors.length; i++) {
			if(lift.getDirection() == 1 && !floors[i].isUpQueueEmpty()) {
				return false;
			}
			else if(lift.getDirection() == -1 && !floors[i].isDownQueueEmpty()) {
				return false;
			}
		}
		return true;
	}
	public boolean isCallInOppositeDirectionOnCurrFloor() {
		if(lift.getDirection() == 1 && !floors[lift.getCurrFloor()].isDownQueueEmpty()) {
			return true;
		}
		if(lift.getDirection() == -1 && !floors[lift.getCurrFloor()].isUpQueueEmpty()) {
			return true;
		}
		return false;
	}

	int amountOfTimeToBoard;
	public int currStateBoard(int time, Elevator lift) {
		
		if(lift.getTimeInState() < amountOfTimeToBoard && enoughCapacityToBoardNextPass()) {
			board();
			return BOARD;
		}
		else {
			board();
			return CLOSEDR;
		}
	}


	public boolean enoughCapacityToBoardNextPass() {
		if(lift.getDirection() == 1 && floors[lift.getCurrFloor()].peekUpQueue().getNumber() <= lift.getCapacity() - lift.getOnBoard().size()) {
			return true;
		}
		else if(lift.getDirection() == -1 && floors[lift.getCurrFloor()].peekDownQueue().getNumber() <= lift.getCapacity() - lift.getOnBoard().size()) {
			return true;
		}
		else {
			return false;
		}
	}
	public int amountOfTimeToBoardFirstPassOnCurrFloorInCurrDir() {

		if(lift.getDirection() == 1) {

			if(floors[lift.getCurrFloor()].peekUpQueue().getNumber() <= lift.getCapacity() - lift.getOnBoard().size()) {
				if((floors[lift.getCurrFloor()].peekUpQueue().getNumber() % lift.getPassPerTick()) == 0) {
					return (floors[lift.getCurrFloor()].peekUpQueue().getNumber() / lift.getPassPerTick());
				} else {
					return (floors[lift.getCurrFloor()].peekUpQueue().getNumber() / lift.getPassPerTick()) + 1;
				}

			}
			else {
				return 0;
			}
		}
		else {
			if(floors[lift.getCurrFloor()].peekDownQueue().getNumber() <= lift.getCapacity() - lift.getOnBoard().size()) {
				if((floors[lift.getCurrFloor()].peekDownQueue().getNumber() % lift.getPassPerTick()) == 0) {
					return (floors[lift.getCurrFloor()].peekDownQueue().getNumber() / lift.getPassPerTick());
				} else {
					return (floors[lift.getCurrFloor()].peekDownQueue().getNumber() / lift.getPassPerTick()) + 1;
				}

			}
			else {
				return 0;
			}

		}
	}
	public int currStateCloseDr(int time, Elevator lift) {
		lift.closeDoor();
		if(!lift.isDoorClosed()) {
			return CLOSEDR;
		}
		if(lift.getOnBoard().size() == 0 && noOneWaiting()) {
			return STOP;
		}
		return MV1FLR;
	}



	public int currStateMv1Flr(int time, Elevator lift) {
		int floorBeforeMoving = lift.getCurrFloor();
		lift.moveElevator();
//		if(floorBeforeMoving == lift.getCurrFloor() || lift.numPassengersToOffload() == 0) {
//			return MV1FLR;
//		}
		System.out.println("Previous Floor: " + floorBeforeMoving + " Current Floor: " + lift.getCurrFloor());
		if(floorBeforeMoving != lift.getCurrFloor() && (passengersGetOffAtCurrFloor() || passBoardInSameDir())) {
			return OPENDR;
		}
		return MV1FLR;
	}
	
	public boolean passBoardInSameDir() {
		if(lift.getDirection() == 1 && isUpCallFromCurrFloor()) {
			return true;
		}
		else if(lift.getDirection() == -1 && isDownCallFromCurrFloor()) {
			return true;
		}
		return false;
	}


	public void enableLogging() {
		// need to pass this along to both the elevator and floor classes...
		LOGGER.setLevel(Level.INFO);
	}
	
	public void disableLogging() {
		// need to pass this along to both the elevator and floor classes...
		LOGGER.setLevel(Level.OFF);
	}

}
