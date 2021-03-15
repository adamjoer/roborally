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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enumerator with the possible commands.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public enum Command {

    // This is a very simplistic way of realizing different commands.

    FORWARD("Fwd"),
    RIGHT("Turn Right"),
    LEFT("Turn Left"),
    FAST_FORWARD("2x Fwd"),
    TRIPLE_FORWARD("3x Fwd"),
    REVERSE("U-turn"),
    BACKWARDS("Bwd"),

    // XXX Assignment V3
    OPTION_LEFT_RIGHT("Left OR Right", LEFT, RIGHT);

    final public String displayName;

    // XXX Assignment V3
    // Command(String displayName) {
    //     this.displayName = displayName;
    // }
    //
    // replaced by the code below:

    final private List<Command> options;

    /**
     * Makes a new command that gets executed in a command card.
     * <p>
     * If there are several options, the player
     * wil have to choose between one of them in GUI.
     *
     * @param displayName The name that will be displayed in GUI for this specific command card
     * @param options     Zero or more commands that the player can choose between when this command is executed
     */
    Command(String displayName, Command... options) {
        this.displayName = displayName;
        this.options = Collections.unmodifiableList(Arrays.asList(options));
    }

    /**
     * Method for checking if this command is interactive,
     * i.e. the player needs to choose between options
     *
     * @return True if the player needs to choose between options, false otherwise
     */
    public boolean isInteractive() {
        return !options.isEmpty();
    }

    /**
     * Method for getting all the commands bound to this command.
     * Used for getting all the options if the command is interactive.
     *
     * @return A list of all the options bound to this command
     */
    public List<Command> getOptions() {
        return options;
    }

}
