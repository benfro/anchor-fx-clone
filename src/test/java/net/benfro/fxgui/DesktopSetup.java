package net.benfro.fxgui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.benfro.fxgui.docks.node.DockNode;
import net.benfro.fxgui.docks.stations.DockStation;
import net.benfro.fxgui.docks.stations.DockSubStation;
import net.benfro.fxgui.system.AnchorageSystem;

public class DesktopSetup extends Application {

   @Override
   public void start(Stage primaryStage) {

      DockStation station = AnchorageSystem.createCommonStation();

      Scene scene = new Scene(station, 1200, 800);

      DockSubStation dockSubStationLeft = AnchorageSystem.createSubStation(station, "SubStationLeft");
      dockSubStationLeft.dock(station, DockNode.DockPosition.LEFT);

      DockSubStation dockSubStationRight = AnchorageSystem.createSubStation(station, "SubStationRight");
      dockSubStationRight.dock(station, DockNode.DockPosition.RIGHT, 0.3);

      //DockNode node1 = AnchorageSystem.createDock("Tree 1", generateRandomTree());
      //node1.dock(station, DockNode.DockPosition.CENTER);

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
