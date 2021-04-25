package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class ConveyorBeltView {

    public static void drawConveyorBelt(SpaceView spaceView, FieldAction action) {
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        ConveyorBelt conveyorBelt = (ConveyorBelt) action;

        double[] xList;
        double[] yList;

        graphicsContext.setLineWidth(3.);

        if (conveyorBelt.isDoubleMove()) {
            graphicsContext.setStroke(Color.LIGHTBLUE);
            xList = new double[]{20., 30., 47.5, 30., 20., 33., 10., 10., 33.};
            yList = new double[]{20., 20., 37.5, 55., 55., 42., 42., 33., 33.};
            graphicsContext.strokePolygon(xList, yList, xList.length);

            xList = new double[]{32.5, 42.5, 60., 42.5, 32.5, 50};
            yList = new double[]{20., 20., 37.5, 55., 55., 37.5};
            graphicsContext.strokePolygon(xList, yList, xList.length);
        } else {
            graphicsContext.setStroke(Color.OLIVE);
            xList = new double[]{30., 40., 57.5, 40., 30., 43., 20., 20., 43.};
            yList = new double[]{20., 20., 37.5, 55., 55., 42., 42., 33., 33.};

            graphicsContext.strokePolygon(xList, yList, xList.length);
        }

        // The heading of the arrows is EAST for starters, where the heading enum is SOUTH for starters, so we correct this.
        int headingRotation = conveyorBelt.getHeading().ordinal() + 1 % 4;
        canvas.setRotate(90 * headingRotation % 360);

        spaceView.getChildren().add(canvas);
    }
}
