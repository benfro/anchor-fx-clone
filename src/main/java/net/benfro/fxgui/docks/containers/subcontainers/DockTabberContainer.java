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

package net.benfro.fxgui.docks.containers.subcontainers;


import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.benfro.fxgui.docks.containers.interfaces.DockContainableComponent;
import net.benfro.fxgui.docks.containers.interfaces.DockContainer;
import net.benfro.fxgui.docks.node.DockNode;

/**
 * @author Alessio
 */
public final class DockTabberContainer extends TabPane implements DockContainer {

   public static DockTabberContainer createTabber(Node existNode, Node newNode, DockNode.DockPosition position) {

      if (existNode instanceof DockNode && newNode instanceof DockNode) {
         DockNode existDockNode = (DockNode) existNode;
         DockNode newDockNode = (DockNode) newNode;

         DockTabberContainer tabber = new DockTabberContainer();
         Tab existTabPanel = new Tab(existDockNode.getContent().titleProperty().get());
         Tab newTabPanel = new Tab(newDockNode.getContent().titleProperty().get());

         existTabPanel.setOnCloseRequest(event -> {

            if (existDockNode.getCloseRequestHandler() == null || existDockNode.getCloseRequestHandler().canClose()) {
               existDockNode.undock();
               event.consume();
            }

         });

         newTabPanel.setOnCloseRequest(event -> {

            if (newDockNode.getCloseRequestHandler() == null || newDockNode.getCloseRequestHandler().canClose()) {
               newDockNode.undock();
               event.consume();
            }

         });

         existTabPanel.closableProperty().bind(existDockNode.closeableProperty());
         newTabPanel.closableProperty().bind(newDockNode.closeableProperty());

         existTabPanel.setContent(existDockNode);
         newTabPanel.setContent(newDockNode);

         DockContainableComponent existContainableComponent = (DockContainableComponent) existNode;
         DockContainableComponent newContainableComponent = (DockContainableComponent) newNode;

         existContainableComponent.setParentContainer(tabber);
         newContainableComponent.setParentContainer(tabber);

         tabber.getTabs().addAll(existTabPanel, newTabPanel);

         tabber.getStyleClass().add("docknode-tab-pane");

         newDockNode.ensureVisibility();

         return tabber;
      }
      return null;
   }

    private DockContainer container;

    @Override
    public void putDock(DockNode node, DockNode.DockPosition position, double percentage) {
        Tab newTab = new Tab(node.getContent().titleProperty().get());
        newTab.closableProperty().bind(node.closeableProperty());
        getTabs().add(newTab);
        newTab.setContent(node);
        node.setParentContainer(this);
        node.ensureVisibility();
    }

    private void createSplitter(DockNode node, DockNode.DockPosition position) {
        DockContainer currentContainer = container;

       DockSplitterContainer splitter = DockSplitterContainer.createSplitter(this, node, position, 0.5);

        int indexOf = currentContainer.indexOf(this);

        currentContainer.insertNode(splitter, indexOf);

        currentContainer.removeNode(this);

        container = splitter;
    }

    private Tab getTabByNode(DockNode node) {
        return getTabs().stream().filter(t -> t.getContent() == node).findFirst().get();
    }

    @Override
    public boolean isDockVisible(DockNode node) {
        Tab nodeTab = getTabByNode(node);
        return getSelectionModel().getSelectedItem() == nodeTab;
    }

    @Override
    public void putDock(DockNode node, DockNode nodeTarget, DockNode.DockPosition position, double percentage) {
        if (position != DockNode.DockPosition.CENTER) {
            createSplitter(node, position);
        } else {

            DockContainableComponent containableComponent = (DockContainableComponent) node;
            if (containableComponent.getParentContainer() != this) {
                Tab newTab = new Tab(node.getContent().titleProperty().get());
                newTab.closableProperty().bind(node.closeableProperty());
                getTabs().add(newTab);
                newTab.setContent(node);
                node.setParentContainer(this);
                node.ensureVisibility();
            }
        }
    }

    @Override
    public int indexOf(Node node) {
        int index = 0;
        boolean found = false;
        for (Tab tab : getTabs()) {
            if (tab.getContent() == node) {
                found = true;
                break;
            }
            index++;
        }

        return (found) ? index : -1;
    }

    @Override
    public void undock(DockNode node) {
        int index = indexOf(node);

        Tab tab = getTabs().get(index);
        getTabs().remove(tab);

        ((DockContainableComponent) node).setParentContainer(null);

        if (getTabs().size() == 1) {
            DockNode remainingNode = (DockNode) getTabs().get(0).getContent();
            getTabs().remove(0);
            int indexInsideParent = getParentContainer().indexOf(this);

            getParentContainer().insertNode(remainingNode, indexInsideParent);
            getParentContainer().removeNode(this);

        }
    }

    @Override
    public void insertNode(Node node, int index) {
        // NOTHING
    }

    @Override
    public void removeNode(Node node) {
        // NOTHING
    }

    @Override
    public void setParentContainer(DockContainer container) {
        this.container = container;
    }

    @Override
    public DockContainer getParentContainer() {
        return container;
    }

    public void manageDragOnSameNode(DockNode node, DockNode.DockPosition position) {

        if (getTabByNode(node) != null && getTabs().size() == 2) {
            DockNode otherNode = (getTabs().get(0).getContent() == node) ? (DockNode) getTabs().get(1).getContent() : (DockNode) getTabs().get(0).getContent();
            node.undock();
            node.dock(otherNode, position);
        } else if (getTabByNode(node) != null && getTabs().size() > 2) {

            node.undock();

            DockContainer currentContainer = container;

           DockSplitterContainer splitter = DockSplitterContainer.createSplitter(this, node, position, 0.5);

            int indexOf = currentContainer.indexOf(this);

            currentContainer.insertNode(splitter, indexOf);

            currentContainer.removeNode(this);

            container = splitter;

            node.stationProperty().get().add(node);
        }
    }

    public void ensureVisibility(DockNode node) {

        Tab tabNode = getTabByNode(node);
        getSelectionModel().select(tabNode);
    }

}
