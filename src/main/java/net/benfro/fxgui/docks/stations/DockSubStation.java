/*
 * Copyright 2015-2016 Alessio Vinerbi. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package net.benfro.fxgui.docks.stations;


import javafx.scene.Scene;
import javafx.stage.Window;
import net.benfro.fxgui.docks.node.DockNode;
import net.benfro.fxgui.docks.node.ui.DockUIPanel;

/**
 * @author avinerbi
 */
public final class DockSubStation extends DockNode {

   private final DockStation substation;

   public DockStation getSubStation() {
      return substation;
   }

   public Window getStationWindow() {
      return stationProperty().get().getStationScene().getWindow();
   }

   public Scene getStationScene() {
      return stationProperty().get().getStationScene();
   }

   public void putDock(DockNode dockNode, DockPosition position, double percentage) {
      substation.add(dockNode);
      substation.putDock(dockNode, position, percentage);
      dockNode.stationProperty().set(substation);
   }


   public DockSubStation(DockUIPanel uiPanel) {
      super(uiPanel);
      substation = (DockStation) getContent().getNodeContent();
      substation.markAsSubStation(this);
   }

}
