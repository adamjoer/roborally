package dk.dtu.compute.se.pisd.roborally.exception;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class ImpossibleMoveException extends Exception {

    public Player player;
    public Space space;
    public Heading heading;

    public ImpossibleMoveException(Player player, Space space, Heading heading) {
        super("Move impossible");
        this.player = player;
        this.space = space;
        this.heading = heading;
    }
}
