package dk.dtu.compute.se.pisd.roborally.exception;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public abstract class MoveException extends Exception {
    public Player player;
    public Space space;
    public Heading heading;

    public MoveException(String message, Player player, Space space, Heading heading) {
        super(message);
        this.player = player;
        this.space = space;
        this.heading = heading;
    }
}
