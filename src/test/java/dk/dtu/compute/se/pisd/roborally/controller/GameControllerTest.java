package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT, "defaultBoard", 0);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null, "Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() + "!");
    }

    @Test
    void pushRobot() {
        Board board = gameController.board;

        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);
        gameController.turnRight(player2);
        Heading expectedHeading = player2.getHeading();

        Space targetSpace = player2.getSpace();
        try {
            gameController.moveToSpace(player1, targetSpace, player1.getHeading());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Space neighbor = board.getNeighbour(player1.getSpace(), player1.getHeading());
        Space player2Space = player2.getSpace();

        // player2 should retain their heading even when pushed.
        Assertions.assertEquals(player2.getHeading(), expectedHeading);
        // player2 should be pushed to the Space just in front of player1
        Assertions.assertEquals(neighbor, player2Space);
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveStep(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void fastForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();


        int x = current.getSpace().x,
                y = current.getSpace().y;

        Heading heading = current.getHeading();

        gameController.fastForward(current, 2);

        switch (heading) {
            case NORTH -> y = (y + 6) % TEST_HEIGHT;
            case EAST -> x = (x + 2) % TEST_WIDTH;
            case SOUTH -> y = (y + 2) % TEST_HEIGHT;
            case WEST -> x = (x + 6) % TEST_WIDTH;
        }

        Assertions.assertEquals(x, current.getSpace().x, "The x-coordinate should be " + x + ", not " + current.getSpace().x);
        Assertions.assertEquals(y, current.getSpace().y, "The y-coordinate should be " + y + ", not " + current.getSpace().y);
    }

    @Test
    void turnLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        Heading startingHeading = current.getHeading();

        gameController.turnLeft(current);

        Assertions.assertEquals(startingHeading.prev(), current.getHeading());
    }

    @Test
    void turnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        Heading startingHeading = current.getHeading();

        gameController.turnRight(current);

        Assertions.assertEquals(startingHeading.next(), current.getHeading());

    }
}