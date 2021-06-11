package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.RebootSpace;
import dk.dtu.compute.se.pisd.roborally.controller.RotatingGear;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class RebootSpaceView {

    public static void drawRebootSpace(SpaceView spaceView, FieldAction action) {
        Canvas canvas = new Canvas(SpaceView.spaceSize, SpaceView.spaceSize);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();


        graphicsContext.setStroke(Color.MAGENTA);
        graphicsContext.setLineWidth(1.5);
        // Gear polygon coordinates as percentages.
        double[] xList = new double[]{
                0.578, 0.586, 0.659, 0.65,
                0.712, 0.73, 0.778, 0.76,
                0.79, 0.812, 0.822, 0.8,
                0.79, 0.811, 0.781, 0.76,
                0.712, 0.726, 0.664, 0.65,
                0.576, 0.581, 0.503, 0.5,
                0.422, 0.414, 0.341, 0.35,
                0.288, 0.27, 0.222, 0.24,
                0.21, 0.188, 0.178, 0.2,
                0.21, 0.189, 0.219, 0.24,
                0.288, 0.274, 0.336, 0.35,
                0.422, 0.419, 0.497, 0.5
        };

        double[] yList = new double[]{
                0.21, 0.189, 0.219, 0.24,
                0.288, 0.274, 0.336, 0.35,
                0.422, 0.419, 0.497, 0.5,
                0.578, 0.586, 0.659, 0.65,
                0.712, 0.73, 0.778, 0.76,
                0.79, 0.812, 0.822, 0.8,
                0.79, 0.811, 0.781, 0.76,
                0.712, 0.726, 0.664, 0.65,
                0.578, 0.581, 0.503, 0.5,
                0.422, 0.414, 0.341, 0.35,
                0.288, 0.27, 0.222, 0.24,
                0.21, 0.188, 0.178, 0.2
        };

        xList = spaceView.scaleDoublesToFit(xList);
        yList = spaceView.scaleDoublesToFit(yList);

        graphicsContext.strokePolygon(xList, yList, xList.length);

        double[] circleParams = new double[]{0.235, 0.53};
        circleParams = spaceView.scaleDoublesToFit(circleParams);
        graphicsContext.strokeOval(circleParams[0], circleParams[0], circleParams[1], circleParams[1]);

        spaceView.getChildren().add(canvas);
    }
}
