package com.webcheckers.appl;

import com.webcheckers.model.Game;
import com.webcheckers.model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Keeps track of all the players that are signed in. It's an information expert on signed in players :P
 * created by Disney, Andy, Ani, and Johnny
 */

public class PlayerLobby {
    private static final Logger LOG = Logger.getLogger(PlayerLobby.class.getName());

    Map <String, Player> playerMap;
    Map<Player, Player> playerPlayerMap;
    GameCenter gameCenter;
    public enum SignInResult {SIGNED_IN, INVALID_INPUT, INVALID_PLAYER, SIGNED_OUT}

    /**
     * Initilize the playerMap, gameList and playerPlayerMap
     */
    public PlayerLobby(GameCenter gameCenter) {
        playerMap = new HashMap<>();        //associates Strings to Players
        playerPlayerMap = new HashMap<>();  //associates Players to other Players (their opponent)
        this.gameCenter = gameCenter;
    }

    /**
     * Validates the name and signs the player in. Stores the player in the PlayerList
     * @param name String
     * @return SignInResult Enum
     */
    public SignInResult signInPlayer(String name, String password) {
        if (invalidInput(name)) {
            return SignInResult.INVALID_INPUT;
        } else if (hasAccount(name)) {
            return SignInResult.INVALID_PLAYER;
        } else {
            playerMap.put(name, new Player(name, password));
            return SignInResult.SIGNED_IN;
        }
    }

    /**
     * Check if a given player is currently signed in.
     * @param player
     * @return
     */
    public boolean isSignedIn(Player player) {
        for (Player player1 : this.playerMap.values()){
            if (player1.isSignedIn() && player.equals(player1)){
                return true;
            }
        }
        return false;
    }

    /**
     * Signs the player out, removing them from the PlayerList.
     * @param name String
     * @return SignInResult
     */
    public SignInResult signOutPlayer(String name) {
        if (invalidInput(name)) {
            return SignInResult.INVALID_INPUT;
        } else if (!hasAccount(name)) {
            return SignInResult.INVALID_PLAYER;
        } else {
            Player player = playerMap.get(name);
            player.signOut();
            return SignInResult.SIGNED_OUT;
        }
    }

    /**
     * Validates the name. A name cannot be empty and cannot contain the (") character
     * @param name String
     * @return boolean
     */
    private boolean invalidInput(String name) {
        return name.contains("\"") || name.equals("");
    }

    /**
     * Fetches the Player Object from the PlayerList based on the name.
     * @param name String
     * @return Player
     */
    public Player getPlayer(String name) {
        if (!invalidInput(name)) {
            if (hasAccount(name)) {
                return playerMap.get(name);
            }
        }
        return null;
    }

    /**
     * Get's the total number of players signed in.
     * @return String
     */
    public String getNumPlayers() {
        int numPlayers = 0;
        for (Player player : playerMap.values()){
            if(player.isSignedIn()){
                numPlayers++;
            }
        }
        return Integer.toString(numPlayers);
    }

    /**
     * Check that a player is signed in.
     * @param name String
     * @return boolean
     */
    public boolean hasAccount(String name) {
        return playerMap.containsKey(name);
    }

    /**
     * List out the Players so that the HomePage can display them.
     * @param name String
     * @return String
     */
    public String playerList(String name){
        String result = "";
        for (String playerName : playerMap.keySet()){
            Player player = playerMap.get(playerName);
            if (!playerName.equals(name) && player.isSignedIn()) {
                result = result.concat("<form action=\"/startGame\" method=\"GET\"> <input type=\"hidden\" id=\"name\" " +
                        "name=\"opponent\" value=\"" + player.getName() + "\"> <button type=\"submit\" >" +
                        player.getName() + "</button> </div> </form>");
            }
        }
        return result;
    }

    /**
     * adds a player to the list of players in games
     * @param player Player
     */
    public void addToGame(Player player, Player player1){
        playerPlayerMap.put(player, player1);
        playerPlayerMap.put(player1, player);
    }

    /***
     * Fetch the player that is the opponent of a given player.
     * @param player Player whose opponent is wanted.
     * @return
     */
    public Player getPlayerOpponent(Player player){
        return playerPlayerMap.get(player);
    }

    public void endResignedGame(Player player){
        Player opponent = this.getPlayerOpponent(player);
        playerPlayerMap.remove(opponent);
        playerPlayerMap.remove(player);
        gameCenter.removeGame(player);
    }
}
