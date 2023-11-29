package org.example.Core.Repositories;

import org.example.Core.Models.Player;
import org.example.Core.Models.Team;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Repository extends Remote {
    int countTeams() throws RemoteException;
    int countPlayers() throws RemoteException;

    void updateTeam(Team team) throws RemoteException;
    void updatePlayer(Player player) throws RemoteException;

    void movePlayertoTeam(int studentId, int groupId) throws RemoteException;

    void insertTeam(Team team) throws RemoteException;
    void insertPlayer(int teamId, Player player) throws RemoteException;

    void deleteTeam(int id) throws RemoteException;
    void deletePlayer(int id) throws RemoteException;

    Team getTeam(int id) throws RemoteException;
    Player getPlayer(int id) throws RemoteException;

    List<Team> getTeams() throws RemoteException;
    List<Player> getPlayers() throws RemoteException;
}