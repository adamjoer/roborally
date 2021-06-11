package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class RotatingGear extends FieldAction {

    boolean clockwise;

    public RotatingGear(boolean clockwise) {
        this.clockwise = clockwise;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Heading currentHeading = space.getPlayer().getHeading();

        Heading targetHeading = (clockwise) ? currentHeading.next() : currentHeading.prev();

        space.getPlayer().setHeading(targetHeading);
        return true;
    }

    public boolean isClockwise() {
        return clockwise;
    }
}
