import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ElevatorSimController {
	private ElevatorSimulation gui;
	private Building building;
	private int stepCnt = 0;
	private boolean endSim = false;
	
		
	public ElevatorSimController(ElevatorSimulation gui) {
		this.gui = gui;
		building = new Building(6);
		InitializePassengerData("ElevatorTest.csv");

	}
	
	private void InitializePassengerData(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine())!= null) {
				String[] values = line.split(",");
				
				building.passQ.add(new Passengers(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3]), Boolean.parseBoolean(values[4]), Integer.parseInt(values[5])));
				//System.out.println(building.passQ.toString());
				// use values to construct the passenger...
			}
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
	}
	
	public void enableLogging() {
		building.enableLogging();
	}
	
 	public void stepSim() {
		stepCnt++;
		// need to check to see if passengers should show up on floors
		// then updateElevator...
		// and update the GUI...
	}
 
 	
 	
 	
 	

}
