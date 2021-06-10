package dk.dtu.compute.se.pisd.roborally.exception;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class FatalMoveException extends MoveException {
    public Player other = null;

    public FatalMoveException(Player player, Space space, Heading heading) {
        super("Move fatal", player, space, heading);
        System.out.println("FatalMoveException thrown for going over edge");
    }

    public FatalMoveException(Player player, Player other, Space space, Heading heading) {
        super("Move fatal", player, space, heading);
        this.other = other;
        System.out.println("FatalMoveException thrown for pushing other player over edge");
    }
}
