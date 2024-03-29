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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS_ON_HAND = 8;
    final public static int NO_CARDS_IN_DECK = 20;

    final public Board board;

    private String name;
    private String color;

    private Space space;
    private Heading heading = EAST;

    private final CommandCardField[] program;
    private final CommandCardField[] cards;

    private final ArrayList<CommandCard> deck = new ArrayList<>(NO_CARDS_IN_DECK);
    private final ArrayList<CommandCard> discardPile = new ArrayList<>(NO_CARDS_IN_DECK);

    // The last checkpoint which the player has landed on
    private int currentCheckPoint = 0;

    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;

        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS_ON_HAND];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
            (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public void checkPointHandler(int checkPoint) {
        if (currentCheckPoint == checkPoint - 1) {
            currentCheckPoint++;
        }
    }

    public boolean isUsingAllRegisters() {
        for (CommandCardField field : program)
            if (field.getCard() == null)
                return false;
        return true;
    }

    public void shuffleDeck() {
        Collections.shuffle(discardPile);
        deck.addAll(discardPile);
        discardPile.clear();
    }

    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    public int getCurrentCheckPoint() {
        return currentCheckPoint;
    }

    public ArrayList<CommandCard> getDeck() {
        return deck;
    }

    public ArrayList<CommandCard> getDiscardPile() {
        return discardPile;
    }

    public ArrayList<CommandCard> getCompleteDeck() {

        ArrayList<CommandCard> completeDeck = new ArrayList<>();

        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            CommandCard card = program[i].getCard();
            if (card != null)
                completeDeck.add(card);
        }

        for (int i = 0; i < Player.NO_CARDS_ON_HAND; i++) {
            CommandCard card = cards[i].getCard();
            if (card != null)
                completeDeck.add(card);
        }

        completeDeck.addAll(deck);
        completeDeck.addAll(discardPile);

        return completeDeck;
    }
}
