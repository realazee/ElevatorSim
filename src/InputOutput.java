import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class InputOutput {
	static Building building;
	static String[] configValues = new String[7];

	public static void main(String[] args) {

		//building = new Building(6);
		System.out.print("What is your name? ");
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		System.out.println("Your name is " + input);
		//InitializePassengerData(C:\Users\aaron\Downloads\EmployeeDataGUI-20200909T170023Z-001\EmployeeDataGUI\ElevatorTest.csv);
		//InitializePassengerData("ElevatorTest.csv");
		InitializeElevatorConfig("ElevatorSimConfig.csv");
		System.out.println(Arrays.toString(configValues));
		building = new Building(Integer.parseInt(configValues[0]),Integer.parseInt(configValues[1]),Integer.parseInt(configValues[3]), Integer.parseInt(configValues[4]),Integer.parseInt(configValues[5]),Integer.parseInt(configValues[6]), configValues[2]);
		System.out.println(configValues[2]);
		InitializePassengerData(configValues[2]);
	}

	private static void InitializePassengerData(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			br.readLine();
			while ((line = br.readLine())!= null) {
				String[] values = line.split(",");

				building.passQ.add(new Passengers(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3]), Boolean.parseBoolean(values[4]), Integer.parseInt(values[5])));
				System.out.println(building.passQ.toString());
				// use values to construct the passenger...
			}
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
	}
	private static void InitializeElevatorConfig(String filename) {
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
}
