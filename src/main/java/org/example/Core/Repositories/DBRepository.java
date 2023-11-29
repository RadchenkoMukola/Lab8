package org.example.Core.Repositories;

import org.example.Core.Models.Player;
import org.example.Core.Models.Team;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBRepository extends UnicastRemoteObject implements Repository {
    private Connection connection;

    public DBRepository(String url, String user, String password) throws RemoteException {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();

            String teamssTable = "CREATE TABLE IF NOT EXISTS teams (id SERIAL PRIMARY KEY, name varchar(30), year int)";
            String playersTable = "CREATE TABLE IF NOT EXISTS players (id SERIAL PRIMARY KEY, team_id int, name varchar(30), age int, CONSTRAINT fk_team FOREIGN KEY(team_id) REFERENCES teams(id))";

            statement.execute(teamssTable);
            statement.execute(playersTable);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countTeams() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) as count FROM teams";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public int countPlayers() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) as count FROM players";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public void updateTeam(Team team) {
        try {
            Team item = getTeam(team.getId());
            if (item != null) {
                Statement statement = connection.createStatement();
                String query = "UPDATE teams SET name='" + team.getName() + "', year=" + team.getYear() + " WHERE id=" + team.getId();
                statement.execute(query);

                for (Player player : item.getPlayers()) {
                    if (!team.hasPlayer(player)) {
                        deletePlayer(player.getId());
                    }
                }
                for (Player player : team.getPlayers()) {
                    if (!item.hasPlayer(player)) {
                        insertPlayer(team.getId(), player);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePlayer(Player player) {
        try {
            Statement statement = connection.createStatement();
            String query = "UPDATE players SET name='" + player.getName() + "', age=" + player.getAge() + " WHERE id=" + player.getId();
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void movePlayertoTeam(int playerId, int teamId) {
        try {
            if (getTeam(teamId) != null) {
                Statement statement = connection.createStatement();
                String query = "UPDATE players SET team_id=" + teamId + " WHERE id=" + playerId;
                statement.execute(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertTeam(Team team) {
        try {
            Statement statement = connection.createStatement();
            String teamQuery = "INSERT INTO teams(name, year) VALUES('" + team.getName() + "'," + team.getYear() + ")";
            statement.execute(teamQuery);

            String findQuery = "SELECT * FROM teams WHERE name='" + team.getName() + "' AND year=" + team.getYear();
            ResultSet resultSet = statement.executeQuery(findQuery);
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                for (Player player : team.getPlayers()) {
                    String playerQuery = "INSERT INTO players(team_id, name, age) VALUES(" + id + ",'" + player.getName() + "'," + player.getAge() + ")";
                    statement.execute(playerQuery);
                }
            }
        } catch (SQLException e) {
            System.out.println("Cannot add team as it already exists");
        }
    }

    @Override
    public void insertPlayer(int teamId, Player player) {
        try {
            Statement statement = connection.createStatement();
            String playersQuery = "INSERT INTO players(team_id, name, age) VALUES(" + teamId + ",'" + player.getName() + "'," + player.getAge() + ")";
            statement.execute(playersQuery);
        } catch (SQLException e) {
            System.out.println("Cannot add player as it already exists");
        }
    }

    @Override
    public void deleteTeam(int id) {
        try {
            Statement statement = connection.createStatement();
            String playersQuery = "DELETE FROM players WHERE team_id = " + id;
            statement.execute(playersQuery);
            String teamQuery = "DELETE FROM teams WHERE id = " + id;
            statement.execute(teamQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deletePlayer(int id) {
        try {
            Statement statement = connection.createStatement();
            String playersQuery = "DELETE FROM players WHERE id = " + id;
            statement.execute(playersQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Team getTeam(int id) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM (SELECT * FROM teams WHERE id = " + id + ") JOIN (SELECT id as player_id, team_id as id, name as player_name, age as player_age FROM players) USING (id)";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Team team = new Team();
                team.setId(resultSet.getInt("id"));
                team.setName(resultSet.getString("name"));
                team.setYear(resultSet.getInt("year"));

                do {
                    Player player = new Player();
                    player.setId(resultSet.getInt("player_id"));
                    player.setName(resultSet.getString("player_name"));
                    player.setAge(resultSet.getInt("player_age"));
                    team.addPlayer(player);
                } while (resultSet.next());

                return team;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Player getPlayer(int id) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM players WHERE id=" + id;

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Player player = new Player();
                player.setId(resultSet.getInt("id"));
                player.setName(resultSet.getString("name"));
                player.setAge(resultSet.getInt("age"));
                return player;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Team> getTeams() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM (SELECT * FROM teams) LEFT JOIN (SELECT id as player_id, team_id as id, name as player_name, age as player_age FROM players) USING (id)";

            List<Team> teams = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Team team = null;

                do {
                    if (team == null || resultSet.getInt("id") != team.getId()) {
                        if (team != null) {
                            teams.add(team);
                        }

                        team = new Team();
                        team.setId(resultSet.getInt("id"));
                        team.setName(resultSet.getString("name"));
                        team.setYear(resultSet.getInt("year"));
                    }

                    Player player = new Player();
                    player.setId(resultSet.getInt("player_id"));
                    if (resultSet.wasNull()) {
                        continue;
                    }
                    player.setName(resultSet.getString("player_name"));
                    player.setAge(resultSet.getInt("player_age"));
                    team.addPlayer(player);
                } while (resultSet.next());

                teams.add(team);
            }

            return teams;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Player> getPlayers() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM players";

            List<Player> players = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Player player = new Player();
                player.setId(resultSet.getInt("id"));
                player.setName(resultSet.getString("name"));
                player.setAge(resultSet.getInt("age"));
                players.add(player);
            }

            return players;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}