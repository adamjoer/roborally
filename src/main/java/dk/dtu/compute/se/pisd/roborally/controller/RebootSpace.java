package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class RebootSpace extends FieldAction{

    @Override
    public boolean doAction(GameController gameController, Space space) {
        return true;
    }
}
