package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class RotatingGear extends FieldAction{

    boolean clockwise;

    public RotatingGear(boolean clockwise) {
        this.clockwise = clockwise;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Heading currentHeading = space.getPlayer().getHeading();
        Heading targetHeading = switch (currentHeading) {
            case NORTH -> (clockwise) ? Heading.EAST : Heading.WEST;
            case EAST -> (clockwise) ? Heading.SOUTH : Heading.NORTH;
            case SOUTH -> (clockwise) ? Heading.WEST : Heading.EAST;
            case WEST -> (clockwise) ? Heading.NORTH : Heading.SOUTH;
        };

        space.getPlayer().setHeading(targetHeading);
        return true;
    }
}
