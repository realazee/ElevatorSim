import java.util.ArrayList;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
//By Aaron Zheng, Mihir Mirchandani
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
	private Label time;
	private Label occ;
	private Label status;
	private int numFloors;
	private GridPane left;
	private GridPane right;
	private GridPane center;
	private ArrayList<Label> upflrQLbls = new ArrayList<Label>();
	private ArrayList<Label> downflrQLbls = new ArrayList<Label>();
	
	
	Timeline tl = new Timeline(new KeyFrame(Duration.millis(1000), e -> controller.stepSim()));
	
	private static Polygon MV_UP_SYMBOL = new Polygon(0,20,12,0,24,20);
	private static Polygon MV_DOWN_SYMBOL = new Polygon(0,0,12,20,24,0);
	private static Circle ELEVATOR_SYMBOL = new Circle(10);
	
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		numFloors = controller.getNumFloors();
		for(int i = 0; i < numFloors; i++) {
			upflrQLbls.add(new Label());
			downflrQLbls.add(new Label());
		}
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
		occ = new Label("Elevator Occupance: ");
		status = new Label("Elevator Status: ");
		top.getChildren().addAll(time, occ, status);
		VBox onTheTop = new VBox();
		onTheTop.getChildren().addAll(top);
		
		left = new GridPane();
		right = new GridPane();
		center = new GridPane();
		
		center.getColumnConstraints().addAll(new ColumnConstraints(10));
		
		for(int i = 0; i < numFloors; i++) {
			center.getRowConstraints().addAll(new RowConstraints(70));
		
		}
		
		bp.setCenter(center);
		left.getColumnConstraints().addAll(new ColumnConstraints(10));
		
		for(int i = 0; i < numFloors; i++) {
			left.getRowConstraints().addAll(new RowConstraints(70));
			left.add(new Label((numFloors - i) + ""), 1, i);
		}
		bp.setLeft(left);
		
		right.getColumnConstraints().addAll(new ColumnConstraints(10));
		
		for(int i = 0; i < numFloors; i++) {
			right.getRowConstraints().addAll(new RowConstraints(70));
			right.add(new Label(("Up: ") ), 1, i);
			//doesnt work
			right.add(upflrQLbls.get(numFloors - i -1), 2, i);
			right.add(new Label((" Down: ") ), 3, i);
			//doesnt work
			right.add(downflrQLbls.get(numFloors - i -1), 4, i);
		}
		bp.setRight(right);
		
		Scene main = new Scene(bp, 500, 500);
		primaryStage.setScene(main);
		bp.setTop(onTheTop);
		primaryStage.setTitle("Elevator Simulation");
		primaryStage.show();
		

	}
	
	public void updateGUI(int stepCnt) {
 		//gui.time = new Label("Time: " + stepCnt);
		time.setText("Time: "+ stepCnt);
		for( int i = 0; i < numFloors; i++) {
			upflrQLbls.get(numFloors - i -1).setText(controller.getFloors()[numFloors - i -1].getUpQueue().getSize()+"");
			downflrQLbls.get(numFloors - i -1).setText(controller.getFloors()[numFloors - i -1].getDownQueue().getSize()+"");
		}
		occ.setText("Elevator Occupance: " + controller.getElevatorOccupance());
		status.setText("Elevator Status: " + controller.getElevatorStatus());
		System.out.println("step count is currently: " + stepCnt);
		
		center.getChildren().clear();
		if(controller.getElevatorStatus().equals("MV1FLR") || controller.getElevatorStatus().equals("MVTOFLR")) {
			if(controller.getElevatorDirection() == 1) {
				center.add(MV_UP_SYMBOL, 1, (numFloors - controller.getElevatorCurrFloor() -1));
			}
			else {
				center.add(MV_DOWN_SYMBOL, 1, (numFloors - controller.getElevatorCurrFloor() -1));
			}
		}
		else {
			center.add(ELEVATOR_SYMBOL, 1, (numFloors - controller.getElevatorCurrFloor() -1));
		}
		
	}
	public void stopTimeLine() {
		tl.stop();
		
	}
 
	public static void main (String[] args) {
		
		Application.launch(args);
	}

}
