package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.PitfallSpace;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PitFallSpaceView {

    public static void drawPitFallSpace(SpaceView spaceView, FieldAction action) {
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();


        graphicsContext.setStroke(Color.LIME);
        graphicsContext.setLineWidth(3.);
        double[] xList = new double[]{42.5, 32.5};
        double[] yList = new double[]{62.5, 62.5};

        graphicsContext.strokePolygon(xList, yList, xList.length);

        PitfallSpace pitFallSpace = (PitfallSpace) action;

        // Increase font size from standard.
        Font font = new Font(10);
        graphicsContext.setFont(font);

        // Draw larger shadow of the number.
        graphicsContext.setStroke(Color.YELLOW);
        graphicsContext.setLineWidth(4.);
        graphicsContext.strokeText("PitFall", 8, 43);

        // Draw the number over the shadow.
        graphicsContext.setStroke(Color.ORANGE);
        graphicsContext.setLineWidth(2.);
        graphicsContext.strokeText("PitFall", 8, 43);

        spaceView.getChildren().add(canvas);
    }
}
