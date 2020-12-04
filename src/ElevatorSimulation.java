import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
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
	//private int currFloor;
	//private int passengers;
	//private int time;

	private final static int STOP = 0;
	private final static int MVTOFLR = 1;
	private final static int OPENDR = 2;
	private final static int OFFLD = 3;
	private final static int BOARD = 4;
	private final static int CLOSEDR = 5;
	private final static int MV1FLR = 6;
	Label time;
	Label occ;
	Label status;

	Timeline tl = new Timeline(new KeyFrame(Duration.millis(100), e -> controller.stepSim()));

	
	
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		BorderPane bp = new BorderPane();
		HBox buttons = new HBox(20);
		buttons.setPrefHeight(50);
		bp.setBottom(buttons);
		TextField input = new TextField();
		Button stepSim = new Button("Step Sim");
		Button step = new Button("Step: ");		
		Button run = new Button("Run");
		RadioButton log = new RadioButton("Logging");
		log.setOnAction(event -> controller.toggleLogging(log.isSelected()));
		stepSim.setOnAction(event -> controller.stepSim());
		step.setOnAction(event -> {
			tl.setCycleCount(Integer.parseInt(input.getText()));
			tl.play();
		});
		run.setOnAction(event -> {
			tl.setCycleCount(Animation.INDEFINITE);
			tl.play();
		});
		buttons.getChildren().addAll(stepSim, step, input, run, log);
		
		HBox top = new HBox(30);
		time = new Label("Time: " + controller.getStepCnt());
		occ = new Label("Elevator Occupance: To Be Implemented");
		status = new Label("Elevator Status: TBI");
		top.getChildren().addAll(time, occ, status);
		VBox onTheTop = new VBox();
		onTheTop.getChildren().addAll(top);

		Scene main = new Scene(bp, 500, 500);
		primaryStage.setScene(main);
		bp.setCenter(onTheTop);
		primaryStage.setTitle("Elevator Simulation");
		primaryStage.show();
		

	}
	
	public void updateGUI() {
 		//gui.time = new Label("Time: " + stepCnt);
		time.setText("Time: "+ controller.getStepCnt());
		System.out.println("step count is currently: " + controller.getStepCnt());
		
	}
	public void stopTimeLine() {
		tl.stop();
		System.exit(0);
	}
 
	public static void main (String[] args) {
		
		Application.launch(args);
	}

}
