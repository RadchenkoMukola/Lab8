package org.example.RMI.Client;

import org.example.Core.Models.Team;
import org.example.Core.Models.Player;
import org.example.Core.Repositories.Repository;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(4097);
            Repository repository = (Repository) registry.lookup("repository");

            System.out.println("Teams: " + repository.countTeams());
            System.out.println("Players: " + repository.countPlayers());

            Team team = new Team();
            team.setName("New Team");
            team.setYear(20);

            repository.insertTeam(team);

            Player player = new Player();
            player.setName("New Player");
            repository.insertPlayer(2, player);

            repository.deleteTeam(1);
            repository.deletePlayer(5);

            System.out.println("Team: " + repository.getTeam(1));
            System.out.println("Player: " + repository.getPlayer(5));

            System.out.println("Teams: " + repository.getTeams());
            System.out.println("Players: " + repository.getPlayers());
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
