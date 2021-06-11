package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ConveyorBeltView {

    public static void drawConveyorBelt(SpaceView spaceView, FieldAction action) {
        Canvas canvas = new Canvas(spaceView.getSpaceSize(), spaceView.getSpaceSize());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        ConveyorBelt conveyorBelt = (ConveyorBelt) action;

        double[] xList;
        double[] yList;

        graphicsContext.setLineWidth(3.);

        if (conveyorBelt.isDoubleMove()) {
            graphicsContext.setStroke(Color.LIGHTBLUE);
            xList = new double[]{0.267, 0.400, 0.633, 0.400, 0.267, 0.440, 0.133, 0.133, 0.440};
            yList = new double[]{0.267, 0.267, 0.500, 0.733, 0.733, 0.560, 0.560, 0.440, 0.440};
            xList = spaceView.scaleDoublesToFit(xList);
            yList = spaceView.scaleDoublesToFit(yList);
            graphicsContext.strokePolygon(xList, yList, xList.length);

            xList = new double[]{0.433, 0.567, 0.800, 0.567, 0.433, 0.667};
            yList = new double[]{0.267, 0.267, 0.500, 0.733, 0.733, 0.500};
            xList = spaceView.scaleDoublesToFit(xList);
            yList = spaceView.scaleDoublesToFit(yList);
            graphicsContext.strokePolygon(xList, yList, xList.length);
        } else {
            graphicsContext.setStroke(Color.OLIVE);
            xList = new double[]{0.400, 0.533, 0.767, 0.533, 0.400, 0.573, 0.267, 0.267, 0.573};
            yList = new double[]{0.267, 0.267, 0.500, 0.733, 0.733, 0.560, 0.560, 0.440, 0.440};
            xList = spaceView.scaleDoublesToFit(xList);
            yList = spaceView.scaleDoublesToFit(yList);

            graphicsContext.strokePolygon(xList, yList, xList.length);
        }

        // The heading of the arrows is EAST for starters, where the heading enum is SOUTH for starters, so we correct this.
        int headingRotation = conveyorBelt.getHeading().ordinal() + 1 % 4;
        canvas.setRotate(90 * headingRotation % 360);

        spaceView.getChildren().add(canvas);
    }
}
