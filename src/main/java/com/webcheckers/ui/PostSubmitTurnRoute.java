package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.appl.Message;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.ui.GetGameRoute.GAME_ATTR;
import static com.webcheckers.ui.PostSignInRoute.USER_ATTR;

/**
 * Created by Juna on 11/3/2017.
 */
public class PostSubmitTurnRoute implements Route {

    final static String GAME_OVER_ATTR = "gg";
    private Gson gson;
    GameCenter gameCenter;
    PlayerLobby playerLobby;

    /**
     * Constructor for the Submit Button route.
     * @param gson
     * @param gameCenter
     * @param playerLobby
     */
    public PostSubmitTurnRoute(Gson gson, GameCenter gameCenter, PlayerLobby playerLobby) {
        this.gson = gson;
        this.gameCenter = gameCenter;
        this.playerLobby = playerLobby;
    }

    /**
     * Submit a turn. This finalized one player's turn by committing them to every move they've made this turn and
     * switiching the turn to the other player.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        Session session = request.session();
        Game game = session.attribute(GAME_ATTR);
        game.finishTurn();
        game.startTurn();
        //Check if the game is over.
        if(game.checkGameOver()) {
            //Game is over and the current user is the loser.
            Player loser = session.attribute(USER_ATTR);
            Player winner = gameCenter.getOpponentFromPlayer(loser);
            if(gameCenter.isInGame(loser) && gameCenter.isInGame(winner)) {
                session.attribute(GAME_OVER_ATTR, true);
            }
        }
        return gson.toJson(new Message("Valid Turn", Message.type.info), Message.class);
    }
}
