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

    // XXX: V2
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2
    public void finishProgrammingPhase() {
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
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        activateBoardElements();
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
                    this.moveForward(player);
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
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    private void activateBoardElements() {
        for (Player player : board.getPlayers()) {

            for (FieldAction action : player.getSpace().getActions()) {
                action.doAction(this, player.getSpace());
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
        continuePrograms();
    }

    public void moveForward(@NotNull Player player) {

        // TODO: is this if-statement necessary?
//        if (player.board == board) {

        // Calculate the target space based on the players heading
        Heading heading = player.getHeading();
        Space target = board.getNeighbour(player.getSpace(), heading);

        // If it is possible, ie. getNeighbor didn't return null, move the player to the space
        if (target != null) {

            // moveToSpace pushes any other players already on the target space
            // in the direction that the current player is moving
            // If the move is impossible, ie. any pushed player
            // will hit a wall, ImpossibleMoveException is thrown
            try {
                moveToSpace(player, target, heading);

            } catch (ImpossibleMoveException e) {
                // we don't do anything here for now;
                // we just catch the exception so that
                // we do no pass it on to the caller
                // (which would be very bad style).
            }
        }
//        }
    }

    public void moveToSpace(Player player, Space space, Heading heading) throws ImpossibleMoveException {

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
                throw new ImpossibleMoveException(player, space, heading);
            }
        }

        // Move the player to the new space
        player.setSpace(space);
    }

    public void fastForward(@NotNull Player player, int count) {

        // TODO: Maybe throw an exception if count is less than or equal to zero

        for (int i = 0; i < count; i++) {
            moveForward(player);
        }
    }

    public void backwards(@NotNull Player player) {
        reverse(player);
        moveForward(player);
        reverse(player);
    }

    public void reverse(@NotNull Player player) {
        turnRight(player);
        turnRight(player);
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
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }
}
