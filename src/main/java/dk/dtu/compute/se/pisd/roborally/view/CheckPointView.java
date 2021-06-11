package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CheckPointView {

    public static void drawCheckPoint(SpaceView spaceView, FieldAction action) {
        Canvas canvas = new Canvas(spaceView.getSpaceSize(), spaceView.getSpaceSize());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();


        graphicsContext.setStroke(Color.LIME);
        graphicsContext.setLineWidth(3.);
        double[] xList = new double[]{0.567, 0.433, 0.310, 0.216, 0.165, 0.165, 0.216, 0.310, 0.433, 0.567,
                0.690, 0.784, 0.835, 0.835, 0.784, 0.690};
        double[] yList = new double[]{0.833, 0.833, 0.782, 0.688, 0.565, 0.431, 0.308, 0.214, 0.163, 0.163,
                0.214, 0.308, 0.431, 0.565, 0.688, 0.782};
        xList = spaceView.scaleDoublesToFit(xList);
        yList = spaceView.scaleDoublesToFit(yList);
        graphicsContext.strokePolygon(xList, yList, xList.length);

        CheckPoint checkPoint = (CheckPoint) action;

        // Increase font size from standard.
        Font font = new Font(18);
        graphicsContext.setFont(font);

        double[] doubles = new double[]{0.427, 0.573};
        doubles = spaceView.scaleDoublesToFit(doubles);

        // Draw larger shadow of the number.
        graphicsContext.setStroke(Color.LIGHTGRAY);
        graphicsContext.setLineWidth(4.);
        graphicsContext.strokeText(Integer.toString(checkPoint.getCheckPointNumber()), doubles[0], doubles[1]);

        // Draw the number over the shadow.
        graphicsContext.setStroke(Color.LIME);
        graphicsContext.setLineWidth(2.);
        graphicsContext.strokeText(Integer.toString(checkPoint.getCheckPointNumber()), doubles[0], doubles[1]);

        spaceView.getChildren().add(canvas);
    }
}
