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
package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
class Repository implements IRepository {

    private static final String GAME_GAMEID = "gameID";

    private static final String GAME_NAME = "name";

    private static final String GAME_CURRENTPLAYER = "currentPlayer";

    private static final String GAME_PHASE = "phase";

    private static final String GAME_STEP = "step";

    private static final String BOARD_NAME = "boardName";

    private static final String PLAYER_PLAYERID = "playerID";

    private static final String PLAYER_NAME = "name";

    private static final String PLAYER_COLOUR = "colour";

    private static final String PLAYER_GAMEID = "gameID";

    private static final String PLAYER_POSITION_X = "positionX";

    private static final String PLAYER_POSITION_Y = "positionY";

    private static final String PLAYER_HEADING = "heading";

    private static final String CARD_COMMAND = "command";

    private static final String CARD_INDEX = "cardIndex";

    private static final String CARD_GAMEID = "gameID";

    private static final String CARD_PLAYERID = "playerID";

    private Connector connector;

    Repository(Connector connector) {
        this.connector = connector;
    }

    @Override
    public boolean createGameInDB(Board game) {
        if (game.getGameId() == null) {
            Connection connection = connector.getConnection();
            try {
                connection.setAutoCommit(false);

                PreparedStatement ps = getInsertGameStatementRGK();
                // TODO: the name should eventually set by the user
                //       for the game and should be then used
                //       game.getName();
                ps.setString(1, game.boardName + " " + new Date()); // instead of name
                ps.setNull(2, Types.TINYINT); // game.getPlayerNumber(game.getCurrentPlayer())); is inserted after players!
                ps.setInt(3, game.getPhase().ordinal());
                ps.setInt(4, game.getStep());
                ps.setString(5, game.boardName);

                /*
                 * If you have a foreign key constraint for current players,
                 * the check would need to be temporarily disabled, since
                 * MySQL does not have a per transaction validation, but
                 * validates on a per row basis.
                 * Statement statement = connection.createStatement();
                 * statement.execute("SET foreign_key_checks = 0");
                 */

                int affectedRows = ps.executeUpdate();
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (affectedRows == 1 && generatedKeys.next()) {
                    game.setGameId(generatedKeys.getInt(1));
                }
                generatedKeys.close();

                // Enable foreign key constraint check again:
                // statement.execute("SET foreign_key_checks = 1");
                // statement.close();

                createPlayersInDB(game);
                createCardsInDB(game);

                // since current player is a foreign key, it can oly be
                // inserted after the players are created, since MySQL does
                // not have a per transaction validation, but validates on
                // a per row basis.
                ps = getSelectGameStatementU();
                ps.setInt(1, game.getGameId());

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
                    rs.updateRow();
                } else {
                    // TODO error handling
                }
                rs.close();

                connection.commit();
                connection.setAutoCommit(true);
                return true;
            } catch (SQLException e) {
                // TODO error handling
                e.printStackTrace();
                System.err.println("Some DB error");

                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException e1) {
                    // TODO error handling
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public boolean updateGameInDB(Board game) {
        assert game.getGameId() != null;

        Connection connection = connector.getConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement ps = getSelectGameStatementU();
            ps.setInt(1, game.getGameId());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
                rs.updateInt(GAME_PHASE, game.getPhase().ordinal());
                rs.updateInt(GAME_STEP, game.getStep());
                rs.updateRow();
            } else {
                // TODO error handling
            }
            rs.close();

            updatePlayersInDB(game);
            updateCardsInDB(game);

            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            // TODO error handling
            e.printStackTrace();
            System.err.println("Some DB error");

            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e1) {
                // TODO error handling
                e1.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public Board loadGameFromDB(int id) {
        Board game;
        try {
            // TODO here, we could actually use a simpler statement
            //      which is not updatable, but reuse the one from
            //      above for the pupose
            PreparedStatement ps = getSelectGameStatementU();
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            int playerNo = -1;
            if (rs.next()) {

                game = LoadBoard.loadBoard(rs.getString(BOARD_NAME));
                if (game == null) {
                    return null;
                }
                playerNo = rs.getInt(GAME_CURRENTPLAYER);
                // TODO currently we do not set the games name (needs to be added)
                game.setPhase(Phase.values()[rs.getInt(GAME_PHASE)]);
                game.setStep(rs.getInt(GAME_STEP));
            } else {
                // TODO error handling
                return null;
            }
            rs.close();

            game.setGameId(id);
            loadPlayersFromDB(game);

            if (playerNo >= 0 && playerNo < game.getPlayersNumber()) {
                game.setCurrentPlayer(game.getPlayer(playerNo));
            } else {
                // TODO  error handling
                return null;
            }

            loadCardsFromDB(game);
            return game;

        } catch (SQLException e) {
            // TODO error handling
            e.printStackTrace();
            System.err.println("Some DB error");
        }
        return null;
    }

    @Override
    public List<GameInDB> getGames() {
        // TODO when there many games in the DB, fetching all available games
        //      from the DB is a bit extreme; eventually there should a
        //      methods that can filter the returned games in order to
        //      reduce the number of the returned games.
        List<GameInDB> result = new ArrayList<>();
        try {
            PreparedStatement ps = getSelectGameIdsStatement();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(GAME_GAMEID);
                String name = rs.getString(GAME_NAME);
                result.add(new GameInDB(id, name));
            }
            rs.close();
        } catch (SQLException e) {
            // TODO proper error handling
            e.printStackTrace();
        }
        return result;
    }

    private void createPlayersInDB(Board game) throws SQLException {
        // TODO code should be more defensive
        PreparedStatement ps = getSelectPlayersStatementU();
        ps.setInt(1, game.getGameId());

        ResultSet rs = ps.executeQuery();
        for (int i = 0; i < game.getPlayersNumber(); i++) {
            Player player = game.getPlayer(i);
            rs.moveToInsertRow();
            rs.updateInt(PLAYER_GAMEID, game.getGameId());
            rs.updateInt(PLAYER_PLAYERID, i);
            rs.updateString(PLAYER_NAME, player.getName());
            rs.updateString(PLAYER_COLOUR, player.getColor());
            rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
            rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
            rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
            rs.insertRow();
        }

        rs.close();
    }

    private void loadPlayersFromDB(Board game) throws SQLException {
        PreparedStatement ps = getSelectPlayersASCStatement();
        ps.setInt(1, game.getGameId());

        ResultSet rs = ps.executeQuery();
        int i = 0;
        while (rs.next()) {
            int playerId = rs.getInt(PLAYER_PLAYERID);
            if (i++ == playerId) {
                // TODO this should be more defensive
                String name = rs.getString(PLAYER_NAME);
                String colour = rs.getString(PLAYER_COLOUR);
                Player player = new Player(game, colour, name);
                game.addPlayer(player);

                int x = rs.getInt(PLAYER_POSITION_X);
                int y = rs.getInt(PLAYER_POSITION_Y);
                player.setSpace(game.getSpace(x, y));
                int heading = rs.getInt(PLAYER_HEADING);
                player.setHeading(Heading.values()[heading]);

            } else {
                // TODO error handling
                System.err.println("Game in DB does not have a player with id " + i + "!");
            }
        }
        rs.close();
    }

    private void updatePlayersInDB(Board game) throws SQLException {
        PreparedStatement ps = getSelectPlayersStatementU();
        ps.setInt(1, game.getGameId());

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int playerId = rs.getInt(PLAYER_PLAYERID);
            // TODO should be more defensive
            Player player = game.getPlayer(playerId);
            // rs.updateString(PLAYER_NAME, player.getName()); // not needed: player's names does not change
            rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
            rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
            rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
            // TODO error handling
            // TODO take care of case when number of players changes, etc
            rs.updateRow();
        }
        rs.close();

        // TODO error handling/consistency check: check whether all players were updated
    }

    private void createCardsInDB(Board game) throws SQLException {
        PreparedStatement preparedStatement = getSelectCardsStatementU();
        preparedStatement.setInt(1, game.getGameId());

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Command> commands = Arrays.asList(Command.values());
        for (int i = 0; i < game.getPlayersNumber(); i++) {
            Player player = game.getPlayer(i);

            ArrayList<CommandCard> completeDeck = player.getCompleteDeck();

            int cardIndex = 0;
            for (CommandCard commandCard : completeDeck) {
                Command command = commandCard.command;
                int commandIndex = commands.indexOf(command);

                resultSet.moveToInsertRow();

                resultSet.updateInt(CARD_COMMAND, commandIndex);
                resultSet.updateInt(CARD_INDEX, cardIndex);
                resultSet.updateInt(CARD_GAMEID, player.board.getGameId());
                resultSet.updateInt(CARD_PLAYERID, i);

                resultSet.insertRow();

                cardIndex++;
            }
        }

        resultSet.close();
    }

    private void loadCardsFromDB(Board game) throws SQLException {
        PreparedStatement preparedStatement = getSelectCardsStatementU();
        preparedStatement.setInt(1, game.getGameId());

        ResultSet resultSet = preparedStatement.executeQuery();

        Command[] commands = Command.values();

        while (resultSet.next()) {
            int playerID = resultSet.getInt(CARD_PLAYERID);
            int cardIndex = resultSet.getInt(CARD_INDEX);
            Command command = commands[resultSet.getInt(CARD_COMMAND)];

            Player player = game.getPlayer(playerID);

            player.getDeck().add(cardIndex, new CommandCard(command));
        }

        resultSet.close();
    }

    private void updateCardsInDB(Board game) throws SQLException {
        PreparedStatement preparedStatement = getSelectCardsStatementU();
        preparedStatement.setInt(1, game.getGameId());

        ResultSet resultSet = preparedStatement.executeQuery();

        List<Command> commands = Arrays.asList(Command.values());

        ArrayList<ArrayList<CommandCard>> lists = new ArrayList<>(game.getPlayersNumber());

        for (int i = 0; i < game.getPlayersNumber(); i++) {
            lists.add(game.getPlayer(i).getCompleteDeck());
        }

        while (resultSet.next()) {
            int playerID = resultSet.getInt(CARD_PLAYERID);
            int cardIndex = resultSet.getInt(CARD_INDEX);
            Command command = lists.get(playerID).get(cardIndex).command;

            resultSet.updateInt(CARD_COMMAND, commands.indexOf(command));

            resultSet.updateRow();
        }

        resultSet.close();
    }

    private static final String SQL_INSERT_GAME =
            "INSERT INTO Game(name, currentPlayer, phase, step, boardName) VALUES (?, ?, ?, ?, ?)";

    private PreparedStatement insert_game_stmt = null;

    private PreparedStatement getInsertGameStatementRGK() {
        if (insert_game_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                insert_game_stmt = connection.prepareStatement(
                        SQL_INSERT_GAME,
                        Statement.RETURN_GENERATED_KEYS);
            } catch (SQLException e) {
                // TODO error handling
                e.printStackTrace();
            }
        }
        return insert_game_stmt;
    }

    private static final String SQL_SELECT_GAME =
            "SELECT * FROM Game WHERE gameID = ?";

    private PreparedStatement select_game_stmt = null;

    private PreparedStatement getSelectGameStatementU() {
        if (select_game_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_game_stmt = connection.prepareStatement(
                        SQL_SELECT_GAME,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                // TODO error handling
                e.printStackTrace();
            }
        }
        return select_game_stmt;
    }

    private static final String SQL_SELECT_PLAYERS =
            "SELECT * FROM Player WHERE gameID = ?";

    private PreparedStatement select_players_stmt = null;

    private PreparedStatement getSelectPlayersStatementU() {
        if (select_players_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_players_stmt = connection.prepareStatement(
                        SQL_SELECT_PLAYERS,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                // TODO error handling
                e.printStackTrace();
            }
        }
        return select_players_stmt;
    }

    private static final String SQL_SELECT_PLAYERS_ASC =
            "SELECT * FROM Player WHERE gameID = ? ORDER BY playerID ASC";

    private PreparedStatement select_players_asc_stmt = null;

    private PreparedStatement getSelectPlayersASCStatement() {
        if (select_players_asc_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                // This statement does not need to be updatable
                select_players_asc_stmt = connection.prepareStatement(
                        SQL_SELECT_PLAYERS_ASC);
            } catch (SQLException e) {
                // TODO error handling
                e.printStackTrace();
            }
        }
        return select_players_asc_stmt;
    }

    private static final String SQL_SELECT_CARDS = "SELECT * FROM Card WHERE gameID = ? ORDER BY playerID ASC, cardIndex ASC";

    private PreparedStatement select_cards_stmt = null;

    private PreparedStatement getSelectCardsStatementU() {
        if (select_cards_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_cards_stmt = connection.prepareStatement(
                        SQL_SELECT_CARDS,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return select_cards_stmt;
    }

    private static final String SQL_SELECT_GAMES =
            "SELECT gameID, name FROM Game";

    private PreparedStatement select_games_stmt = null;

    private PreparedStatement getSelectGameIdsStatement() {
        if (select_games_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_games_stmt = connection.prepareStatement(
                        SQL_SELECT_GAMES);
            } catch (SQLException e) {
                // TODO error handling
                e.printStackTrace();
            }
        }
        return select_games_stmt;
    }


}
