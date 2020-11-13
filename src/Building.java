import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
	public GenericQueue<Passengers> passQ = new GenericQueue<Passengers>(10); // we need to edit the max passenger count.

	public Building(int numFloors) {
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = 1;

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
		lift = new Elevator(NUM_FLOORS);
		lift.setLoggerFH(fh);
	}
	
	
	public void updateElevator(int time) {
		switch (lift.getCurrState()) {
			case STOP: lift.updateCurrState(currStateStop(time,lift)); break;
			case MVTOFLR: lift.updateCurrState(currStateMvToFlr(time,lift)); break;
			case OPENDR: lift.updateCurrState(currStateOpenDr(time,lift)); break;
			case OFFLD: lift.updateCurrState(currStateOffLd(time,lift)); break;
			case BOARD: lift.updateCurrState(currStateBoard(time,lift)); break;
			case CLOSEDR: lift.updateCurrState(currStateCloseDr(time,lift)); break;
			case MV1FLR: lift.updateCurrState(currStateMv1Flr(time,lift)); break;
		}
		
		/* example logger...
		if (elevatorStateChanged(lift)) 
			LOGGER.info("Time="+time+"   Prev State: " + printState(lift.getPrevState()) + "   Curr State: "+printState(lift.getCurrState())
						+"   PrevFloor: "+(lift.getPrevFloor()+1) + "   CurrFloor: " + (lift.getCurrFloor()+1));
	    */
	}
	
	public void enableLogging() {
		// need to pass this along to both the elevator and floor classes...
		LOGGER.setLevel(Level.INFO);
	}

}
