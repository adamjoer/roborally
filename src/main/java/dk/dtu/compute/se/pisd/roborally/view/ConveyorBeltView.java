package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ConveyorBeltView {

    public static void drawConveyorBelt(SpaceView spaceView, FieldAction action) {
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        graphicsContext.setStroke(Color.BLACK);

        ConveyorBelt conveyorBelt = (ConveyorBelt) action;

        graphicsContext.strokeText(conveyorBelt.getHeading() + " " + conveyorBelt.isDoubleMove(), 0,20);

        spaceView.getChildren().add(canvas);
    }
}
