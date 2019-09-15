package net.benfro.fxgui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.benfro.fxgui.docks.node.DockNode;
import net.benfro.fxgui.docks.stations.DockStation;
import net.benfro.fxgui.docks.stations.DockSubStation;
import net.benfro.fxgui.system.AnchorageSystem;
import org.controlsfx.control.StatusBar;

public class DesktopSetup extends Application {

   @Override
   public void start(Stage primaryStage) {

      DockStation station = AnchorageSystem.createCommonStation();

      BorderPane borderPanel = new BorderPane();

      StatusBar statusBar = new StatusBar();
      statusBar.getLeftItems().add(new Button("Info"));
      statusBar.setProgress(.5);

      borderPanel.setBottom(statusBar);

      TitledPane t1 = new TitledPane("T1", new Button("B1"));
      TitledPane t2 = new TitledPane("T2", new Button("B2"));
      TitledPane t3 = new TitledPane("T3", new Button("B3"));
      Accordion accordion = new Accordion();
      accordion.getPanes().addAll(t1, t2, t3);


      borderPanel.setCenter(station);


      Scene scene = new Scene(borderPanel, 1200, 800);

      DockSubStation dockSubStationLeft = AnchorageSystem.createSubStation(station, "");
      dockSubStationLeft.dock(station, DockNode.DockPosition.LEFT);
      dockSubStationLeft.floatableProperty().set(false);
      dockSubStationLeft.maximizableProperty().set(false);
      dockSubStationLeft.closeableProperty().set(false);


      DockSubStation dockSubStationRight = AnchorageSystem.createSubStation(station, "");
      dockSubStationRight.dock(station, DockNode.DockPosition.RIGHT, 0.3);
      dockSubStationRight.floatableProperty().set(false);
      dockSubStationRight.maximizableProperty().set(false);
      dockSubStationRight.closeableProperty().set(false);


      DockNode node1 = AnchorageSystem.createDock("A simple Accordion", accordion);
      node1.dock(dockSubStationLeft, DockNode.DockPosition.CENTER);

      //DockNode node2 = AnchorageSystem.createDock("Tree 2", generateRandomTree());
      //node2.dock(station, DockNode.DockPosition.RIGHT);

      primaryStage.setTitle("Station 1");
      primaryStage.setScene(scene);
      primaryStage.show();

      //makeSecondStage();

      AnchorageSystem.installDefaultStyle();
   }


   public static void main(String[] args) {
      launch(args);
   }
}
