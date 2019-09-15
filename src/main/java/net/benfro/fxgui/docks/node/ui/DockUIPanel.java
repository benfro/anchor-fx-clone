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
package net.benfro.fxgui.docks.node.ui;

import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import net.benfro.fxgui.docks.node.DockNode;
import net.benfro.fxgui.system.AnchorageSystem;

import java.util.Objects;

/**
 * @author Alessio
 */
public final class DockUIPanel extends Pane {

    public static final double BAR_HEIGHT = 25;
    private Label titleLabel;
    private Pane barPanel;
    private DockCommandsBox commandsBox;
    private ImageView iconView;

    private Node nodeContent;
    private StackPane contentPanel;
    private DockNode node;
    private Point2D deltaDragging;
    private boolean subStationStype;

    boolean noTitleBar = false;

    private DockUIPanel() {

    }

    public DockUIPanel(String title, Node nodeContent, boolean subStationStype, Image imageIcon) {

        Objects.requireNonNull(nodeContent);
        Objects.requireNonNull(title);

        noTitleBar = title.isEmpty();

        this.subStationStype = subStationStype;
        this.nodeContent = nodeContent;

        getStylesheets().add("anchorfx.css");

        buildNode(title, imageIcon, !noTitleBar);

        if (!noTitleBar)
            installDragEventMananger();
    }

    public void setIcon(Image icon) {
        if (!noTitleBar) {
            Objects.requireNonNull(icon);
            iconView.setImage(icon);
        }
    }

    private void makeCommands() {
        commandsBox = new DockCommandsBox(node);
        barPanel.getChildren().add(commandsBox);

        commandsBox.layoutXProperty().bind(barPanel.prefWidthProperty().subtract(commandsBox.getChildren().size() * 30 + 10));
        commandsBox.setLayoutY(0);

        if (Objects.nonNull(titleLabel))
            titleLabel.prefWidthProperty().bind(commandsBox.layoutXProperty().subtract(10));
    }

    public void setDockNode(DockNode node) {
        this.node = node;
        if (!noTitleBar)
            makeCommands();
    }

    public StringProperty titleProperty() {
        return titleLabel.textProperty();
    }

    private void installDragEventMananger() {

        barPanel.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                node.maximizeOrRestore();
            }
        });

        barPanel.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                manageDragEvent(event);
            }
        });
        barPanel.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                manageReleaseEvent();
            }
        });
    }

    private void manageDragEvent(MouseEvent event) {
        if (!node.draggingProperty().get()) {

            if (!node.maximizingProperty().get()) {
                Bounds bounds = node.localToScreen(barPanel.getBoundsInLocal());

                deltaDragging = new Point2D(event.getScreenX() - bounds.getMinX(),
                        event.getScreenY() - bounds.getMinY());

                node.enableDraggingOnPosition(event.getScreenX() - deltaDragging.getX(), event.getScreenY() - deltaDragging.getY());
            }

        } else {
            if (node.getFloatableStage() != null && !node.getFloatableStage().inResizing() && node.draggingProperty().get()) {

                if (!node.maximizingProperty().get()) {
                    node.moveFloatable(event.getScreenX() - deltaDragging.getX(),
                            event.getScreenY() - deltaDragging.getY());

                    //node.stationProperty().get().searchTargetNode(event.getScreenX(), event.getScreenY());
                    AnchorageSystem.searchTargetNode(event.getScreenX(), event.getScreenY());
                }
            }
        }
    }

    private void manageReleaseEvent() {
        if (node.draggingProperty().get() && !node.maximizingProperty().get()) {
            AnchorageSystem.finalizeDragging();
        }
    }

    private void buildNode(String title, Image iconImage, boolean withTitleBar) {

        Objects.requireNonNull(iconImage);
        if (withTitleBar) {

            Objects.requireNonNull(title);

            String titleBarStyle = (!subStationStype) ? "docknode-title-bar" : "substation-title-bar";
            barPanel = makeBarPanel(titleBarStyle);

            iconView = makeIconView(iconImage);

            String titleTextStyle = (!subStationStype) ? "docknode-title-text" : "substation-title-text";
            titleLabel = new Label(title);
            titleLabel.getStyleClass().add(titleTextStyle);
            barPanel.getChildren().addAll(iconView, titleLabel);
            titleLabel.relocate(25, 5);
        }

        contentPanel = new StackPane();
        contentPanel.getStyleClass().add("docknode-content-panel");
        if (withTitleBar)
            contentPanel.relocate(0, BAR_HEIGHT);
        contentPanel.prefWidthProperty().bind(widthProperty());
        if (withTitleBar) {
            contentPanel.prefHeightProperty().bind(heightProperty().subtract(BAR_HEIGHT));
        } else {
            contentPanel.prefHeightProperty().bind(heightProperty());
        }
        contentPanel.getChildren().add(nodeContent);

        contentPanel.setCache(true);
        contentPanel.setCacheHint(CacheHint.SPEED);

        if (nodeContent instanceof Pane) {
            Pane nodeContentPane = (Pane) nodeContent;
            nodeContentPane.setMinHeight(USE_COMPUTED_SIZE);
            nodeContentPane.setMinWidth(USE_COMPUTED_SIZE);
            nodeContentPane.setMaxWidth(USE_COMPUTED_SIZE);
            nodeContentPane.setMaxHeight(USE_COMPUTED_SIZE);
        }

        if (withTitleBar) {
            getChildren().addAll(barPanel, contentPanel);
        } else {
            getChildren().addAll(contentPanel);
        }
    }

    private ImageView makeIconView(Image iconImage) {
        ImageView iconView = new ImageView(iconImage);
        iconView.setFitWidth(15);
        iconView.setFitHeight(15);
        iconView.setPreserveRatio(false);
        iconView.setSmooth(true);
        iconView.relocate(1, (BAR_HEIGHT - iconView.getFitHeight()) / 2);
        return iconView;
    }

    private Pane makeBarPanel(String titleBarStyle) {
        Pane barPanel = new Pane();
        barPanel.getStyleClass().add(titleBarStyle);
        barPanel.setPrefHeight(BAR_HEIGHT);
        barPanel.relocate(0, 0);
        barPanel.prefWidthProperty().bind(widthProperty());
        return barPanel;
    }

    public StackPane getContentContainer() {
        return contentPanel;
    }

    /**
     * Get the value of nodeContent
     *
     * @return the value of nodeContent
     */
    public Node getNodeContent() {
        return nodeContent;
    }

    public boolean isMenuButtonEnable() {
        return commandsBox.isMenuButtonEnable();
    }

}
