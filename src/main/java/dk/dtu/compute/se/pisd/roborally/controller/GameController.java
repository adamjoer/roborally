/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.exception.MoveException;
import dk.dtu.compute.se.pisd.roborally.exception.FatalMoveException;
import dk.dtu.compute.se.pisd.roborally.exception.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {

        // If the space isn't free, do not move player
        if (space.getPlayer() != null) return;

        // Get current player
        Player currentPlayer = board.getCurrentPlayer();

        // Get next player
        Player nextPlayer = board.getPlayer((board.getPlayerNumber(currentPlayer) + 1) % board.getPlayersNumber());

        // Set the players space to the new space
        currentPlayer.setSpace(space);

        // Increment counter
        board.incrementCounter();

        // Set current player to next player
        board.setCurrentPlayer(nextPlayer);
    }

    public void moveToRebootSpace(Player player) {

        Space rebootSpace = board.getRebootSpace();
        Heading heading = player.getHeading();

        if (player.getSpace() != rebootSpace) {

            // Go over every heading and try to push any potential player already on the reboot space in that direction
            for (int i = 0, n = Heading.values().length; i < n; i++) {
                try {
                    moveToSpace(player, rebootSpace, heading);
                    break;

                } catch (MoveException e) {

                    // If we haven't gone over every possible heading, try the next one
                    if (i != n - 1) {
                        heading = heading.next();
                        continue;
                    }

                    // Otherwise, it is impossible to move the player to the reboot space without creating an infinite loop
                    e.printStackTrace();
                }
            }
        }

        // Add 2 spam cards to player discard pile
        player.getDiscardPile().add(new CommandCard(Command.values()[8]));
        player.getDiscardPile().add(new CommandCard(Command.values()[8]));

        // Remove all cards from the player's registers, and give them new cards from their deck
        givePlayerNewCards(player);
    }

    // XXX: V2
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        // Add programming cards to the players decks, if they don't have any
        if (board.getPlayer(0).getDeck().size() == 0) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);

                for (int j = 0; j < Player.NO_CARDS_IN_DECK; j++) {
                    player.getDeck().add(generateRandomCommandCard());
                }
            }
        }

        for (Player p : board.getPlayers()) {
            givePlayerNewCards(p);
        }
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * (commands.length - 1));
        return new CommandCard(commands[random]);
    }

    // XXX: V2
    public void finishProgrammingPhase() {
        for (Player player : board.getPlayers())
            if (!player.isUsingAllRegisters())
                return;

        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX: V2
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    // Remove the SPAM card completely
                    if (command == Command.SPAM) {
                        currentPlayer.getDiscardPile().remove(card);
                    }

                    if (command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    activateBoardElements();
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveStep(player, player.getHeading());
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player, 2);
                    break;
                case TRIPLE_FORWARD:
                    this.fastForward(player, 3);
                    break;
                case REVERSE:
                    this.reverse(player);
                    break;
                case BACKWARDS:
                    this.backwards(player);
                    break;
                case SPAM:
                    this.spam(player);
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    private void activateBoardElements() {
        int checkPoints = board.getCheckPointCount();

        for (Player player : board.getPlayers()) {
            for (FieldAction action : player.getSpace().getActions()) {
                action.doAction(this, player.getSpace());
            }

            if (player.getCurrentCheckPoint() == checkPoints) {
                board.setPlayerWhoWon(player);

                board.setPhase(Phase.GAME_ENDED);

                break;
            }
        }
    }

    /**
     * Method for executing a command in a interactive command card,
     * and continuing executing the next command cards.
     * <p>
     * The method changes the phase from PLAYER_INTERACTION to ACTIVATION,
     * and goes on to the next command card, so that the player can't choose an option again.
     *
     * @param option The command that will be executed when a specific option is chosen
     */
    public void executeCommandOptionAndContinue(@NotNull Command option) {

        // Change phase to ACTIVATION
        board.setPhase(Phase.ACTIVATION);

        // Execute the option that was chosen
        executeCommand(board.getCurrentPlayer(), option);

        // FIXME: Duplicate code
        int nextPlayerNumber = board.getPlayerNumber(board.getCurrentPlayer()) + 1,
                step = board.getStep();

        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            activateBoardElements();
            step++;
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
                board.setStep(step);
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                startProgrammingPhase();
            }
        }

        // Go on to the next command card
        if (!board.isStepMode())
            continuePrograms();
    }

    public void moveStep(@NotNull Player player, Heading heading) {

        if (player.board == board) {

            // Calculate the target space based on the input heading
            Space target = board.getNeighbour(player.getSpace(), heading);

            // If the target is null, and the player is not going
            // over the edge, there is a wall in the way; don't move
            if (target == null && !goingOverEdge(player.getSpace(), heading))
                return;

            // moveToSpace pushes any other players already on the
            // target space in the direction that the current player
            // is moving. If the move is impossible, ie. any pushed
            // player will hit a wall, ImpossibleMoveException is thrown
            try {
                moveToSpace(player, target, heading);

            } catch (MoveException e) {
                if (e instanceof FatalMoveException) {
                    fatalMoveExceptionHandler((FatalMoveException) e);
                }
                // we don't do anything here for now;
                // we just catch the exception so that we do no pass
                // it on to the caller (which would be very bad style).
            }

        }
    }

    public void moveToSpace(Player player, Space space, Heading heading) throws MoveException {
        if (space == null)
            throw new FatalMoveException(player, null, heading);

        // Get any potential players on target space
        Player other = space.getPlayer();

        // If there is a player, the player needs to be moved
        if (other != null) {

            // Calculate the space the other player needs to be pushed to
            Space target = board.getNeighbour(space, heading);

            // If it is possible to move to the target space, recursively push any other potential players
            if (target != null) {

                // XXX Note that there might be additional problems
                // with infinite recursion here!
                moveToSpace(other, target, heading);

            } else { // If the target space is null, it is impossible to move to; throw exception

                // If the player is going over the edge, the move is fatal
                if (goingOverEdge(space, heading))
                    throw new FatalMoveException(player, other, space, heading);

                else // Otherwise they are just blocked and the move is impossible
                    throw new ImpossibleMoveException(player, space, heading);
            }
        }

        // Move the player to the new space
        player.setSpace(space);
    }

    /**
     * Method for checking whether a space, when moving from in a specified direction
     * will lead beyond the edge of the board.
     *
     * @param space A space from which a player is moving
     * @param heading The heading the player is moving
     * @return
     */
    private boolean goingOverEdge(Space space, Heading heading) {
        return ((space.x == 0 && heading == Heading.WEST) ||
                (space.x == board.width - 1 && heading == Heading.EAST) ||
                (space.y == 0 && heading == Heading.NORTH) ||
                (space.y == board.height - 1 && heading == Heading.SOUTH)) &&
               !space.isBlocked(heading);
    }

    public void fastForward(@NotNull Player player, int count) {

        for (int i = 0; i < count; i++) {
            // Check if the player's program cards are null, which indicate that it has been rebooted
            if (player.getProgramField(0).getCard() != null)
                moveStep(player, player.getHeading());
        }
    }

    public void backwards(@NotNull Player player) {
        moveStep(player, player.getHeading().reverse());
    }

    public void reverse(@NotNull Player player) {
        player.setHeading(player.getHeading().reverse());
    }

    public void turnRight(@NotNull Player player) {

        // Change the players heading
        player.setHeading(player.getHeading().next());
    }

    public void turnLeft(@NotNull Player player) {

        // Change the players heading
        player.setHeading(player.getHeading().prev());
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method for handling when a FatalMoveException is thrown.
     * The method deals with different scenarios, for example a player
     * pushing another over the edge.
     *
     * @param e The FatalMoveException that needs to be handled
     */
    public void fatalMoveExceptionHandler(@NotNull FatalMoveException e) {

        // If e.other is not null, the player is pushing another player over the edge
        if (e.other != null) {

            // Move the other player to the reboot space
            moveToRebootSpace(e.other);

            // Try to move the player to the space
            try {
                moveToSpace(e.player, e.space, e.heading);

            } catch (MoveException eNew) {
                // The most likely scenario here is that e.other is pushed
                // by e.player from reboot space to edge. When this happens,
                // e.other is of course sent to the reboot space, thereby
                // returning to its original position. When e.player then tries
                // to move to the reboot space, e.other is in the way.
                // In this case, it therefore makes sense to ignore the move,
                // since the outcome is preferable to what could otherwise happen,
                // which is an infinite loop.
                if (e.other.getSpace() != board.getRebootSpace()) {
                    e.printStackTrace();
                }
            }

        } else { // Otherwise, the player move over the edge; move to reboot space
            moveToRebootSpace(e.player);
        }
    }

    public void spam(@NotNull Player player) {
        if (player.getDeck().size() == 0) {
            player.shuffleDeck();
        }
        // Take the top card in players deck, and activate it
        player.getDiscardPile().add(player.getDeck().remove(0));
        executeCommand(player, player.getDiscardPile().get(player.getDiscardPile().size() - 1).command);
    }


    /**
     * Method for removing the cards in the players current hand, and then giving them new cards from their deck
     */
    private void givePlayerNewCards(Player player) {
        if (player != null) {
            //Clear the players registers
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                if (field.getCard() != null) {
                    player.getDiscardPile().add(field.getCard());
                }

                field.setCard(null);
                field.setVisible(true);
            }

            for (int j = 0; j < Player.NO_CARDS_ON_HAND; j++) {
                CommandCardField field = player.getCardField(j);

                if (field.getCard() != null) {
                    player.getDiscardPile().add(field.getCard());
                }
            }

            //Give them new cards on their hands
            for (int j = 0; j < Player.NO_CARDS_ON_HAND; j++) {
                // If their deck is empty, shuffle their discard pile, and use that as deck
                if (player.getDeck().size() == 0) {
                    player.shuffleDeck();
                }

                CommandCardField field = player.getCardField(j);

                // Give player a new card
                field.setCard(player.getDeck().remove(0));
                field.setVisible(true);
            }
        }
    }
}
