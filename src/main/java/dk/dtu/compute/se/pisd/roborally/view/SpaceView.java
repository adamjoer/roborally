/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.*;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class SpaceView extends StackPane implements ViewObserver {

    public static int spaceSize;

    public final Space space;


    public SpaceView(@NotNull Space space, int spaceSize) {
        this.space = space;
        SpaceView.spaceSize = spaceSize;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SpaceView.spaceSize);
        this.setMinWidth(SpaceView.spaceSize);
        this.setMaxWidth(SpaceView.spaceSize);

        this.setPrefHeight(SpaceView.spaceSize);
        this.setMinHeight(SpaceView.spaceSize);
        this.setMaxHeight(SpaceView.spaceSize);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {

        Player player = space.getPlayer();
        if (player != null) {
            double[] doubles = new double[]{0.0, 0.0,
                    0.133, 0.266,
                    0.266, 0.0};
            doubles = scaleDoublesToFit(doubles);
            Polygon arrow = new Polygon(doubles);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }
    }

    private void drawWall(Heading heading) {
        Pane pane = new Pane();
        Rectangle rectangle = new Rectangle(0.0, 0.0, SpaceView.spaceSize, SpaceView.spaceSize);
        rectangle.setFill(Color.TRANSPARENT);
        pane.getChildren().add(rectangle);

        double upLeft = 2;
        double right = SpaceView.spaceSize - 2;
        double down = SpaceView.spaceSize - 2;
        Line line = null;

        switch (heading) {
            case EAST -> line = new Line(right, upLeft, right, down);
            case WEST -> line = new Line(upLeft, upLeft, upLeft, down);
            case NORTH -> line = new Line(upLeft, upLeft, right, upLeft);
            case SOUTH -> line = new Line(upLeft, down, right, down);
        }

        line.setStroke(Color.RED);
        line.setStrokeWidth(5);
        pane.getChildren().add(line);
        this.getChildren().add(pane);
    }

    public double[] mirrorDoubles(double[] doubles) {
        double[] mirroredDoubles = new double[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            mirroredDoubles[i] = 1 - doubles[i];
        }
        return mirroredDoubles;
    }

    public double[] scaleDoublesToFit(double[] doubles) {
        double[] scaledDoubles = new double[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            scaledDoubles[i] = doubles[i] * SpaceView.spaceSize;
        }
        return scaledDoubles;
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();

            for (FieldAction action : space.getActions()) {
                if (action instanceof CheckPoint)
                    CheckPointView.drawCheckPoint(this, action);
                else if (action instanceof ConveyorBelt)
                    ConveyorBeltView.drawConveyorBelt(this, action);
                else if (action instanceof RotatingGear)
                    RotatingGearView.drawRotatingGear(this, action);
                else if (action instanceof RebootSpace)
                    RebootSpaceView.drawRebootSpace(this, action);
                else if (action instanceof PitfallSpace) {
                    PitFallSpaceView.drawPitFallSpace(this, action);
                }
            }
            for (Heading heading : space.getWalls())
                drawWall(heading);
            updatePlayer();
        }
    }
}
