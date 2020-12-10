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
	private int numBoarded;
	private int offLdDelay;

	private Floor[] floors;
	private Elevator lift;
	private GenericQueue<Passengers>gaveUpQueue = new GenericQueue<Passengers>(1000);
	private ArrayList<Passengers>arrivedList = new ArrayList<Passengers>();

	public GenericQueue<Passengers> passQ = new GenericQueue<Passengers>(100000); // we need to edit the max passenger count.

	//mr. murray said that we do not need elevetor
	public Building(int numFloors, int numElevators, int capacity, int ticksPerFloor, int ticksDoorOpenClose, int passPerTick, String filename) {
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;

		System.setProperty("java.util.logging.SimpleFormatter.format","%4$-7s %5$s%n");
		LOGGER.setLevel(Level.OFF);
		try {
			fh = new FileHandler(filename.substring(0, filename.length() -4) + ".log");
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create the floors
		floors = new Floor[NUM_FLOORS];
		for (int i = 0; i < NUM_FLOORS; i++) {
			floors[i]= new Floor(100); 
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
	public void logElevatorConfig() {
		LOGGER.info("CONFIG: Capacity="+lift.getCapacity()+" Ticks-Floor="

		+lift.getTicksPerFloor()+" Ticks-Door="+lift.getTicksDoorOpenClose()
		+" Ticks-Passengers="+lift.getPassPerTick());
	}

	private Passengers prioritizeCalls() {
		
		if(passBoardOnCurrFloor()) {
			if(!floors[lift.getCurrFloor()].isUpQueueEmpty() && floors[lift.getCurrFloor()].isDownQueueEmpty()) {
				return floors[lift.getCurrFloor()].peekUpQueue();
			}
			if(floors[lift.getCurrFloor()].isUpQueueEmpty() && !floors[lift.getCurrFloor()].isDownQueueEmpty()) {
				return floors[lift.getCurrFloor()].peekDownQueue();
			}
			if(!floors[lift.getCurrFloor()].isUpQueueEmpty() && !floors[lift.getCurrFloor()].isDownQueueEmpty()) {
				if(floors[lift.getCurrFloor()].getUpQueue().getSize() >= floors[lift.getCurrFloor()].getDownQueue().getSize()) {
					return floors[lift.getCurrFloor()].peekUpQueue();
				}
				else {
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
			if((Math.abs(lift.getCurrFloor() - lowestPassengerGoingUp().getFromFloor())) < (Math.abs(lift.getCurrFloor() - highestPassengerGoingDown().getFromFloor()))){
				return lowestPassengerGoingUp();
			}
			if((Math.abs(lift.getCurrFloor() - lowestPassengerGoingUp().getFromFloor())) > (Math.abs(lift.getCurrFloor() - highestPassengerGoingDown().getFromFloor()))){
				return highestPassengerGoingDown();
			}
		}
		return lowestPassengerGoingUp();
		
		
		
		/*
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

*/
	}



	private Passengers lowestPassengerGoingUp() {
		int lowestFloorSoFar = floors.length;
		for(int i = floors.length - 1; i >= 0; i--) {
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
		if(floors[highestFloorSoFar].isDownQueueEmpty()) {
			return null;
		}
		return floors[highestFloorSoFar].peekDownQueue();
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
		/* Passengers P = passQ.peek()
		 * while(p.getTime() == globalTime) {
		 * p = passQ.peek()	
		 */



		while(!passQ.isEmpty() && passQ.peek().getTime() == globalTime) {

			Passengers p = passQ.peek();
			//System.out.println("LOOPED_____ONCE");
			int targetFloor = p.getFromFloor();
			if(p.isGoingUp()) {
				LOGGER.info("Time="+globalTime+" Called="+p.getNumber()+" Floor="+
						(p.getFromFloor()+1)

						+" Dir="+((p.getToFloor() > p.getFromFloor())?"Up":"Down")+" passID=" + p.getId());
				floors[targetFloor].addUpQueue(passQ.remove());
			}
			else if(p.isGoingDown()) {
				LOGGER.info("Time="+globalTime+" Called="+p.getNumber()+" Floor="+
						(p.getFromFloor()+1)

						+" Dir="+((p.getToFloor() > p.getFromFloor())?"Up":"Down")+" passID=" + p.getId());
				floors[targetFloor].addDownQueue(passQ.remove());
			}
			//			if(passQ.isEmpty()) {S
			//				break;
			//			}

			p = passQ.peek();
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
		if(!(prioritizeCalls() == null)) {
			lift.setMoveToFloor(prioritizeCalls().getToFloor());
		}
	}

	//passengers have not been picked up, sets elevator MVTOFLR to floor of prioritizeCalls().
	private void setNextPickUpFloor() {
		if(!(prioritizeCalls() == null)) {
			lift.setMoveToFloor(prioritizeCalls().getFromFloor());
		}
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

		if(lift.isDoorOpen()) {
			if(lift.isDoorOpen() && passengersGetOffAtCurrFloor()) {
				return OFFLD;
			}
			else {
				return BOARD;
			}
		}
		return OPENDR;
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
	public void board(int time) {




		/*
		if(lift.getDirection() == 1) {
			for(int i = 0; i < floors[lift.getCurrFloor()].getUpQueue().getSize(); i++) {
				if(floors[lift.getCurrFloor()].peekUpQueue().getNumber() <= lift.getCapacity() - lift.getOnBoard().size()) {

					amountOfTimeToBoard += amountOfTimeToBoardFirstPassOnCurrFloorInCurrDir();

					LOGGER.info("Time="+time+" Board="+floors[lift.getCurrFloor()].peekUpQueue().getNumber()+" Floor="+ (lift.getCurrFloor()+1)
							+" Dir="+((lift.getDirection()>0)?"Up":"Down")+" passID=" + floors[lift.getCurrFloor()].peekUpQueue().getId());

					lift.getOnBoard().add(floors[lift.getCurrFloor()].removeUpQueue());
				}
			}
		}
		else if(lift.getDirection() == -1) {

			for(int i = 0; i < floors[lift.getCurrFloor()].getDownQueue().getSize(); i++) {
				if(floors[lift.getCurrFloor()].peekDownQueue().getNumber() <= lift.getCapacity() - lift.getOnBoard().size()) {
					amountOfTimeToBoard += amountOfTimeToBoardFirstPassOnCurrFloorInCurrDir();

					LOGGER.info("Time="+time+" Board="+floors[lift.getCurrFloor()].peekDownQueue().getNumber()+" Floor="+ (lift.getCurrFloor()+1)
							+" Dir="+((lift.getDirection()>0)?"Up":"Down")+" passID=" + floors[lift.getCurrFloor()].peekDownQueue().getId());

					lift.getOnBoard().add(floors[lift.getCurrFloor()].removeDownQueue());
				}
			}
		}
		 */

	}

	int numTicksToOffload;
	public int currStateOffLd(int time, Elevator lift) {
		
		int numPassToOffLd;
		if(lift.getPrevState() != OFFLD) {
			//Determine # of passengers to offload
			numPassToOffLd = lift.numPassengersToOffload();
			//Determine offload delay;
			if(numPassToOffLd % lift.getPassPerTick() == 0) {
				offLdDelay = numPassToOffLd / lift.getPassPerTick();
			}
			else {
				offLdDelay = numPassToOffLd / lift.getPassPerTick() + 1;
			}
			//offload all passenger groups to arrived passengers arraylist
			offLoadAllPassengers(time);
		}
		lift.incrementTimeInState();
		if(lift.getTimeInState() == offLdDelay) {
			if(lift.getDirection() == 1  && !floors[lift.getCurrFloor()].isUpQueueEmpty() || lift.getDirection() == -1  && !floors[lift.getCurrFloor()].isDownQueueEmpty()) {
				return BOARD;
			}
			if(lift.getOnBoard().isEmpty() && noCallsInSameDirection() && isCallInOppositeDirectionOnCurrFloor()) {

				if(lift.getDirection() == 1) {
					lift.setDirection(-1);
					return BOARD;
				}
				else if(lift.getDirection() == -1) {
					lift.setDirection(1);
					return BOARD;
				}
			}
			return CLOSEDR;

		}
		return OFFLD;
		
		
		
		
		
		
		/*

		if(lift.getTimeInState() == 0) {
			numTicksToOffload = lift.numTicksToOffload();
			logArrived(time);
			lift.offLoad(time);
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
		 */

	}
	public void offLoadAllPassengers(int time) {
		for(int i = 0; i < lift.getOnBoard().size(); i++) {
			if(lift.getOnBoard().get(i).getToFloor() == lift.getCurrFloor()) {
				//LOGGER.info("Time="+time+" Arrived="+onBoard.get(i).getNumber()+" Floor="+ (currFloor+1)
					//	+" passID=" + onBoard.get(i).getId());
				lift.getOnBoard().get(i).setTimeArrived(time);
				arrivedList.add(lift.getOnBoard().remove(i));
				i--;
			}
		}
	}
	//	
	//	private boolean isCallsInSameDir() {
	//		if(prioritizeCalls().getToFloor()) {
	//			
	//		}
	//	}
	//	

	//TO BE FIXED: it will always
	public boolean noCallsInSameDirection() {
		int currentFloor = lift.getCurrFloor();
		if(lift.getDirection() == 1) {
			for(int i = currentFloor; i < floors.length; i++) {
				if(!floors[i].isUpQueueEmpty() || !floors[i].isDownQueueEmpty()) {
					return false;
				}
			}
		}

		if(lift.getDirection() == -1) {
			for(int i = currentFloor; i > 0; i--) {
				if(!floors[i].isUpQueueEmpty() || !floors[i].isDownQueueEmpty()) {
					return false;
				}
			}
		}
		return true;

		/*
		for(int i = 0; i < floors.length; i++) {
			if(lift.getDirection() == 1 && !floors[i].isUpQueueEmpty()) {
				System.out.println("NONONO_________________" + i);
				return false;
			}
			else if(lift.getDirection() == -1 && !floors[i].isDownQueueEmpty()) {
				return false;
			}
		}
		System.out.println("YESYESYES_________________");
		return true;
		 */
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

		// while the elevator is not full and passengers to board:
		while(!lift.isFull() && passBoardOnCurrFloor()) {
			//peek passenger at the head of the queue
			Passengers currentlyBoarding;
			if(lift.getDirection() == 1) {
				currentlyBoarding = floors[lift.getCurrFloor()].peekUpQueue();
			}
			else {
				currentlyBoarding = floors[lift.getCurrFloor()].peekDownQueue();
			}
			//if currentlyBoarding.hasGivenUp, move currentlyBoarding to the gaveUpQueue
			if(currentlyBoarding.getGiveUpTime() <= time)  {
				if(lift.getDirection() == 1) {
					gaveUpQueue.add(floors[lift.getCurrFloor()].removeUpQueue());
				}
				else {
					gaveUpQueue.add(floors[lift.getCurrFloor()].removeDownQueue());
				}
			}
			//else if there is not enough room to board the passenger, break;
			else if(lift.getRemainingCapacity() < currentlyBoarding.getNumber()) {
				break;
			}
			else {
				//numBoarded += number of passengers in this group;
				numBoarded += currentlyBoarding.getNumber();
				//setBoardTime(time) for this passenger group
				currentlyBoarding.setBoardTime(time);
				//log the passenger group boarding
				LOGGER.info("Time="+time+" Board="+currentlyBoarding.getNumber()+" Floor="+ (lift.getCurrFloor()+1)
						+" Dir="+((lift.getDirection()>0)?"Up":"Down")+" passID=" + currentlyBoarding.getId());

				//remove from floor queue and put in elevator
				if(lift.getDirection() == 1) {
					lift.getOnBoard().add(floors[lift.getCurrFloor()].removeUpQueue());
				}
				else {
					lift.getOnBoard().add(floors[lift.getCurrFloor()].removeDownQueue());
				}			
			}			
		}
		//calculate the delay time based upon the number of passengers boarded
		int delayTime;
		if(numBoarded % lift.getPassPerTick() == 0) {
			delayTime = numBoarded / lift.getPassPerTick();
		}
		else {
			delayTime = numBoarded / lift.getPassPerTick() + 1;
		}
		//increment timeInState in lift
		lift.incrementTimeInState();

		if(lift.getTimeInState() == delayTime) {
			return CLOSEDR;
		}
		else {
			return BOARD;
		}


		
		/*
		if(!enoughCapacityToBoardNextPass(time) && !floors[lift.getCurrFloor()].isUpQueueEmpty() && !floors[lift.getCurrFloor()].isDownQueueEmpty() ) {
			Passengers skippedPassengers;
			if(lift.getDirection() == 1) {
				skippedPassengers = floors[lift.getCurrFloor()].peekUpQueue(); 
			}
			else {
				skippedPassengers = floors[lift.getCurrFloor()].peekDownQueue();
			}
			LOGGER.info("Time="+time+" Skip="+skippedPassengers.getNumber()+" Floor="+ (lift.getCurrFloor()+1)
					+" Dir="+((lift.getDirection()>0)?"Up":"Down")+" passID=" + skippedPassengers.getId());
		}
		if(lift.getTimeInState() < amountOfTimeToBoard && enoughCapacityToBoardNextPass(time)) {

			board(time);
			return BOARD;
		}
		else {

			board(time);
			return CLOSEDR;
		 */
	}

	public boolean passBoardOnCurrFloor() {
		if(!floors[lift.getCurrFloor()].isUpQueueEmpty() || !floors[lift.getCurrFloor()].isDownQueueEmpty()) {
			return true;

		}
		return false;
	}


	public boolean enoughCapacityToBoardNextPass(int time) {



		if(!floors[lift.getCurrFloor()].isUpQueueEmpty() && lift.getDirection() == 1 && floors[lift.getCurrFloor()].peekUpQueue().getNumber() <= lift.getCapacity() - lift.getOnBoard().size()) {
			return true;
		}
		else if(!floors[lift.getCurrFloor()].isDownQueueEmpty() && lift.getDirection() == -1 && floors[lift.getCurrFloor()].peekDownQueue().getNumber() <= lift.getCapacity() - lift.getOnBoard().size()) {
			return true;
		}
		else {
			//LOGGING PART

			//END LOGGING PART
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
		numBoarded = 0;
		lift.closeDoor();
		//if(!lift.isDoorClosed()) {
		//return CLOSEDR;
		//}
		//politeness
		if(lift.getDirection() == 1) {
			if(!(floors[lift.getCurrFloor()].peekUpQueue() == null) && !floors[lift.getCurrFloor()].peekUpQueue().isPolite()) {
				floors[lift.getCurrFloor()].peekUpQueue().setPolite(true);
				return OPENDR;
				
			}
		}
		if(lift.getDirection() == -1) {
			if(!(floors[lift.getCurrFloor()].peekDownQueue() == null) && !floors[lift.getCurrFloor()].peekDownQueue().isPolite()) {
				floors[lift.getCurrFloor()].peekDownQueue().setPolite(true);
				return OPENDR;
				
			}
		}
		
		
		
		if(lift.isDoorClosed()) {
			if(lift.getOnBoard().size() == 0) {
				if(noOneWaiting()) {
					return STOP;
				}
				if(!noCallsInSameDirection()) {
					return MV1FLR;
				}
				else {
					if(lift.getDirection() == 1) {
						lift.setDirection(-1);
						return MV1FLR;
					}
					else if(lift.getDirection() == -1) {
						lift.setDirection(1);
						return MV1FLR;
					}
				}

			}
			else {
				return MV1FLR;
			}
		}
		return CLOSEDR;

		/*
		if(lift.getOnBoard().isEmpty() && noCallsInSameDirection() && isCallInOppositeDirectionOnCurrFloor()) {

			if(lift.getDirection() == 1) {
				lift.setDirection(-1);
			}
			else if(lift.getDirection() == -1) {
				lift.setDirection(1);
			}
		}
		 */

	}



	public int currStateMv1Flr(int time, Elevator lift) {

		if(lift.getDirection() == 1 && lift.getCurrFloor() == NUM_FLOORS - 1) {
			lift.setDirection(-1);

		}
		else if(lift.getDirection() == -1 && lift.getCurrFloor() ==0) {
			lift.setDirection(1);

		}
		int floorBeforeMoving = lift.getCurrFloor();
		lift.moveElevator();

		if(floorBeforeMoving != lift.getCurrFloor()) {
			if(lift.getOnBoard().isEmpty() && noCallsInSameDirection() && isCallInOppositeDirectionOnCurrFloor()) {

				if(lift.getDirection() == 1) {
					lift.setDirection(-1);
					return OPENDR;
				}
				else if(lift.getDirection() == -1) {
					lift.setDirection(1);
					return OPENDR;
				}
			}

			if(passengersGetOffAtCurrFloor()) {

				return OPENDR;
			}
			if(passBoardInSameDir()) {

				return OPENDR;
			}


			else {

				return MV1FLR;
			}

		}

		return MV1FLR;



		//		if(floorBeforeMoving == lift.getCurrFloor() || lift.numPassengersToOffload() == 0) {
		//			return MV1FLR;
		//		}
		/*
		System.out.println("Previous Floor: " + floorBeforeMoving + " Current Floor: " + lift.getCurrFloor());
		if(floorBeforeMoving != lift.getCurrFloor() && (passengersGetOffAtCurrFloor() || passBoardInSameDir())) {
			return OPENDR;
		}
		if(lift.getOnBoard().isEmpty() && noCallsInSameDirection() && isCallInOppositeDirectionOnCurrFloor()) {

			if(lift.getDirection() == 1) {
				lift.setDirection(-1);
				return OPENDR;
			}
			else if(lift.getDirection() == -1) {
				lift.setDirection(1);
				return OPENDR;
			}
		}
		return MV1FLR;
		 */
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

	public void logArrived(int time) {
		for(int i = 0; i < lift.getOnBoard().size(); i++) {
			if(lift.getOnBoard().get(i).getToFloor() == lift.getCurrFloor()) {
				LOGGER.info("Time="+time+" Arrived="+lift.getOnBoard().get(i).getNumber()+" Floor="+ (lift.getCurrFloor()+1)
						+" passID=" + lift.getOnBoard().get(i).getId());


			}
		}

	}
	//no one in elevator, elevator stopped, no one in floors, no one in passQ
	public boolean detectEndOfSimulation(int time) {
		if(lift.getOnBoard().isEmpty() && passQ.isEmpty() && lift.getPrevState() == STOP) {



			LOGGER.info("Time="+time+"   Prev State: " + printState(lift.getPrevState()) + "   Curr State: "+printState(lift.getCurrState())
			+"   PrevFloor: "+(lift.getPrevFloor()+1) + "   CurrFloor: " + (lift.getCurrFloor()+1));

			LOGGER.info("Time="+time+" Detected End of Simulation");
			fh.flush();
			fh.close();
			return true;
		}
		return false;




	}
	/*
	public void detectEndOfSimulation(int time) {

		if(lift.getOnBoard().isEmpty() && allFloorsEmpty()) {
			LOGGER.info("CONFIG: Capacity="+lift.getCapacity()+" Ticks-Floor="

			+lift.getTicksPerFloor()+" Ticks-Door="+lift.getTicksDoorOpenClose()
			+" Ticks-Passengers="+lift.getPassPerTick());
			lift.setCurrState(STOP);
		}
	}
	public boolean allFloorsEmpty() {
		for(int i = 0; i < floors.length; i++) {
			if(!floors[i].isUpQueueEmpty() && !floors[i].isDownQueueEmpty()) {
				return false;

			}
		}
		return true;
	}
	 */

}
