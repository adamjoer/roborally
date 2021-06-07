package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.RotatingGear;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class RotatingGearView {

    public static void drawRotatingGear(SpaceView spaceView, FieldAction action) {
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();


        graphicsContext.setStroke(Color.LIME);
        graphicsContext.setLineWidth(3.);
        double[] xList = new double[]{42.5, 32.5};
        double[] yList = new double[]{62.5, 62.5};

        graphicsContext.strokePolygon(xList, yList, xList.length);

        RotatingGear rotatingGear = (RotatingGear) action;

        // Increase font size from standard.
        Font font = new Font(10);
        graphicsContext.setFont(font);

        // Draw larger shadow of the number.
        graphicsContext.setStroke(Color.RED);
        graphicsContext.setLineWidth(4.);
        graphicsContext.strokeText("RotatingGear", 5, 43);

        // Draw the number over the shadow.
        graphicsContext.setStroke(Color.GREEN);
        graphicsContext.setLineWidth(2.);
        graphicsContext.strokeText("RotatingGear", 5, 43);

        spaceView.getChildren().add(canvas);
    }
}
