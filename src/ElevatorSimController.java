import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.scene.control.Label;

public class ElevatorSimController {
	private ElevatorSimulation gui;
	private Building building;
	private int stepCnt = 0;
	private boolean endSim = false;
	String[] configValues = new String[7];
	
	public int getStepCnt() {
		return stepCnt;
	}
		
	public ElevatorSimController(ElevatorSimulation gui) {
		this.gui = gui;
		InitializeElevatorConfig("ElevatorSimCOnfig.csv");
		building = new Building(Integer.parseInt(configValues[0]),Integer.parseInt(configValues[1]),Integer.parseInt(configValues[3]), Integer.parseInt(configValues[4]),Integer.parseInt(configValues[5]),Integer.parseInt(configValues[6]));
		InitializePassengerData(configValues[2]);

	}
	
	private void InitializePassengerData(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			br.readLine();
			while ((line = br.readLine())!= null) {
				String[] values = line.split(",");
				
				building.passQ.add(new Passengers(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]) -1, Integer.parseInt(values[3]) -1, Boolean.parseBoolean(values[4]), Integer.parseInt(values[5])));
				//System.out.println(building.passQ.toString());
				// use values to construct the passenger...
			}
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
	}
	
	private void InitializeElevatorConfig(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			int i = 0;
			while ((line = br.readLine())!= null) {
				String[] values = line.split(",");
				configValues[i] = values[1];
				i++;
				
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
		building.checkPassengerQueue(stepCnt);
		building.updateElevator(stepCnt);
		gui.updateGUI();
//		System.out.println("Hello World");
		// need to check to see if passengers should show up on floors
		// then updateElevator...
		// and update the GUI...
		
		
	}
 	
 	
 	
 	
 	

}
