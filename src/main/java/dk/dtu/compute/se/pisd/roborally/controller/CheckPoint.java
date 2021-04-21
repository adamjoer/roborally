package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;

public class CheckPoint extends FieldAction {

    private final int checkPointNumber;

    public CheckPoint(int checkPointNumber) {
        this.checkPointNumber = checkPointNumber;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        space.getPlayer().checkPointHandler(checkPointNumber);
        return true;
    }

    public int getCheckPointNumber() {
        return checkPointNumber;
    }
}