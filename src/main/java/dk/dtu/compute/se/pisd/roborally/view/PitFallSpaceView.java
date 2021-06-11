package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.PitfallSpace;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PitFallSpaceView {

    public static void drawPitFallSpace(SpaceView spaceView, FieldAction action) {
        Canvas canvas = new Canvas(SpaceView.spaceSize, SpaceView.spaceSize);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        graphicsContext.setFill(Color.LIGHTGREY);
        double[] xList = new double[]{0.200, 0.800, 0.800, 0.200};
        double[] yList = new double[]{0.200, 0.200, 0.800, 0.800};
        xList = spaceView.scaleDoublesToFit(xList);
        yList = spaceView.scaleDoublesToFit(yList);
        graphicsContext.fillPolygon(xList, yList, xList.length);

        graphicsContext.setFill(Color.DARKGREY);
        xList = new double[]{0.250, 0.750, 0.750, 0.250};
        yList = new double[]{0.250, 0.250, 0.750, 0.750};
        xList = spaceView.scaleDoublesToFit(xList);
        yList = spaceView.scaleDoublesToFit(yList);
        graphicsContext.fillPolygon(xList, yList, xList.length);

        spaceView.getChildren().add(canvas);
    }
}
