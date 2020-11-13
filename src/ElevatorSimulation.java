import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ElevatorSimulation extends Application {
	private ElevatorSimController controller;
	private int currFloor;
	private int passengers;
	private int time;

	private final static int STOP = 0;
	private final static int MVTOFLR = 1;
	private final static int OPENDR = 2;
	private final static int OFFLD = 3;
	private final static int BOARD = 4;
	private final static int CLOSEDR = 5;
	private final static int MV1FLR = 6;

	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("Elevator Simulation");
		primaryStage.show();

	}
	
	public static void main (String[] args) {
		
		Application.launch(args);
	}

}
