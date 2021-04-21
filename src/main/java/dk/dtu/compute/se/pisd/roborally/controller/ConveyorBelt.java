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
public class ConveyorBelt extends FieldAction {

    private final Heading heading;
    private final boolean doubleMove;

    public ConveyorBelt(Heading heading, boolean isDouble) {
        this.heading = heading;
        this.doubleMove = isDouble;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {

        Board board = gameController.board;
        Space target = board.getNeighbour(space, heading);

        try {
            gameController.moveToSpace(space.getPlayer(), target, heading);

            if (doubleMove) {
                target = board.getNeighbour(space, heading);
                gameController.moveToSpace(space.getPlayer(), target, heading);


            }
        } catch (ImpossibleMoveException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Heading getHeading() {
        return heading;
    }

    public boolean isDoubleMove() {
        return doubleMove;
    }
}
