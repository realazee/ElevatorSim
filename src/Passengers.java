
public class Passengers {
	private static int ID=0;
	// These will come from the csv file, and should be initialized in the 
	// constructor.
	private int time;
	private int number;
	private int fromFloor;
	private int toFloor;
	private boolean polite = true;
	private int giveUpTime;
	// this will be initialized in the constructor so that it is unique for each
	// set of Passengers
	private int id;	
	// These fields will be initialized during run time - boardTime is when the group
	// starts getting on the elevator, timeArrived is when the elevator starts offloading
	// at the desired floor
	private int boardTime;
	private int timeArrived;
	
	
	public Passengers(int time, int number, int fromFloor, int toFloor, boolean polite, int giveUpTime) {
		this.time = time;
		this.number = number;
		this.fromFloor = fromFloor;
		this.toFloor = toFloor;
		this.giveUpTime = giveUpTime;
		this.polite = polite;
		id = ID;
		ID++;
	}
	
	public static int getID() {
		return ID;
	}

	public static void setID(int iD) {
		ID = iD;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getFromFloor() {
		return fromFloor;
	}

	public void setFromFloor(int fromFloor) {
		this.fromFloor = fromFloor;
	}

	public int getToFloor() {
		return toFloor;
	}

	public void setToFloor(int toFloor) {
		this.toFloor = toFloor;
	}

	public boolean isPolite() {
		return polite;
	}

	public void setPolite(boolean polite) {
		this.polite = polite;
	}

	public int getGiveUpTime() {
		return giveUpTime;
	}

	public void setGiveUpTime(int giveUpTime) {
		this.giveUpTime = giveUpTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBoardTime() {
		return boardTime;
	}
	public void setBoardTime(int boardTime) {
		this.boardTime = boardTime;
	}
	public int getTimeArrived() {
		return timeArrived;
	}
	public void setTimeArrived(int timeArrived) {
		this.timeArrived = timeArrived;
	}
	public int getDirection() {
		if(toFloor > fromFloor) {
			return 1;
		}
		else {
			return -1;
		}
	}
	public boolean isGoingUp() {
		if(toFloor > fromFloor) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isGoingDown() {
		if(toFloor < fromFloor) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	
}
