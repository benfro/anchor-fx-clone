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

package net.benfro.fxgui.docks.containers;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import net.benfro.fxgui.docks.containers.interfaces.DockContainableComponent;
import net.benfro.fxgui.docks.containers.interfaces.DockContainer;
import net.benfro.fxgui.docks.containers.subcontainers.DockSplitterContainer;
import net.benfro.fxgui.docks.containers.subcontainers.DockTabberContainer;
import net.benfro.fxgui.docks.node.DockNode;
import net.benfro.fxgui.docks.stations.DockStation;

public class SingleDockContainer extends StackPane implements DockContainer {

    private DockContainer parentContainer;


    @Override
    public void putDock(DockNode nodeToDock, DockNode.DockPosition position, double percentage) {

        if (getChildren().isEmpty()) {
            getChildren().add(nodeToDock);
            nodeToDock.setParentContainer(this);
        } else {
            manageSubContainers(nodeToDock, position, percentage);
        }
    }

    @Override
    public void putDock(DockNode node, DockNode targetNode, DockNode.DockPosition position, double percentage) {

        if (getChildren().get(0) == targetNode) {
            manageSubContainers(node, position, percentage);
        }
    }

    @Override
    public boolean isDockVisible(DockNode node) {
        return true;
    }

    @Override
    public int indexOf(Node node) {
        return (getChildren().get(0) == node) ? 0 : -1;
    }

    @Override
    public void removeNode(Node nodeToRemove) {
        getChildren().remove(nodeToRemove);
        ((DockContainableComponent) nodeToRemove).setParentContainer(null);
    }

    @Override
    public void insertNode(Node nodeToInsert, int index) {
        getChildren().set(index, nodeToInsert);
        ((DockContainableComponent) nodeToInsert).setParentContainer(this);
    }

    @Override
    public void undock(DockNode nodeToUndock) {
        if (getChildren().get(0) == nodeToUndock) {
            getChildren().remove(nodeToUndock);
            nodeToUndock.setParentContainer(null);
        }
    }

    private void manageSubContainers(DockNode nodeToAdd, DockNode.DockPosition positionToDockTo, double percentage) {
        Node existingNode = getChildren().get(0);

        if (positionToDockTo.isAtBorder()) {
            getChildren().remove(existingNode);
            DockSplitterContainer splitter = DockSplitterContainer.createSplitter(existingNode, nodeToAdd, positionToDockTo, percentage);
            getChildren().add(splitter);
            splitter.setParentContainer(this);
        } else if (existingNode instanceof DockTabberContainer) {
            DockTabberContainer tabber = (DockTabberContainer) existingNode;
            tabber.putDock(nodeToAdd, DockNode.DockPosition.CENTER, percentage);
        } else if (existingNode instanceof DockSplitterContainer) {
            positionToDockTo = DockNode.DockPosition.BOTTOM;
            DockSplitterContainer splitter = (DockSplitterContainer) existingNode;
            nodeToAdd.dockTo((DockStation) this, positionToDockTo);
        } else {
            getChildren().remove(existingNode);
            DockTabberContainer tabber = DockTabberContainer.createTabber(existingNode, nodeToAdd, positionToDockTo);
            getChildren().add(tabber);
            tabber.setParentContainer(this);
        }
    }

    @Override
    public void setParentContainer(DockContainer container) {
        this.parentContainer = container;
    }

    @Override
    public DockContainer getParentContainer() {
        return parentContainer;
    }

}
