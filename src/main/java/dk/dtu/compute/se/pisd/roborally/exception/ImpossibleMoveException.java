package dk.dtu.compute.se.pisd.roborally.exception;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class ImpossibleMoveException extends MoveException {

    public ImpossibleMoveException(Player player, Space space, Heading heading) {
        super("Move impossible", player, space, heading);
    }
}
