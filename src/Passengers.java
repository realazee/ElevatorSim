
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
	
	
}
