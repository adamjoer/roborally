package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javax.swing.text.IconView;

public class CheckPointView {

    public static void drawCheckPoint(SpaceView spaceView, FieldAction action) {
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();


        graphicsContext.setStroke(Color.LIME);
        graphicsContext.setLineWidth(3.);
        double[] xList = new double[]{42.5, 32.5, 23.26, 16.19, 12.36, 12.36, 16.19, 23.26, 32.5, 42.5, 51.74,
                58.81, 62.64, 62.64, 58.81, 51.74};
        double[] yList = new double[]{62.5, 62.5, 58.67, 51.6, 42.36, 32.36, 23.12, 16.05, 12.23, 12.23, 16.05,
                23.12, 32.36, 42.36, 51.6, 58.67};

        graphicsContext.strokePolygon(xList, yList, xList.length);

        CheckPoint checkPoint = (CheckPoint) action;

        // Increase font size from standard.
        Font font = new Font(18);
        graphicsContext.setFont(font);

        // Draw larger shadow of the number.
        graphicsContext.setStroke(Color.LIGHTGRAY);
        graphicsContext.setLineWidth(4.);
        graphicsContext.strokeText(Integer.toString(checkPoint.getCheckPointNumber()), 32, 43);

        // Draw the number over the shadow.
        graphicsContext.setStroke(Color.LIME);
        graphicsContext.setLineWidth(2.);
        graphicsContext.strokeText(Integer.toString(checkPoint.getCheckPointNumber()), 32, 43);

        spaceView.getChildren().add(canvas);
    }
}
