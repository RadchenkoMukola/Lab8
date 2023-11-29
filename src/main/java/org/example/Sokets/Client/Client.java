package org.example.Sokets.Client;
import org.example.Core.Models.Team;
import org.example.Core.Models.Player;

import java.io.IOException;

public class Client {
    public static void main(String[] args) {
        try {
            RemoteClientRepository repository = new RemoteClientRepository("localhost", 8080);

            System.out.println("Teams: " + repository.countTeams());
            System.out.println("Players: " + repository.countPlayers());

            Team team = new Team();
            team.setName("New team");
            team.setYear(20);

            //repository.insertTeam(team);

            Player player = new Player();
            player.setName("New Player");
            //repository.insertPlayer(2, player);

            //repository.deleteTeam(1);
            //repository.deletePlayer(3);

            System.out.println("Team: " + repository.getTeam(2));
            System.out.println("Player: " + repository.getPlayer(4));

            System.out.println("Teams: " + repository.getTeams());
            System.out.println("Players: " + repository.getPlayers());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}