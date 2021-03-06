package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import static com.webcheckers.ui.GetGameRoute.GAME_ATTR;
import static com.webcheckers.ui.GetGameRoute.MESSAGE_ATTR;
import static com.webcheckers.ui.GetGameRoute.MESSAGE;
import static com.webcheckers.ui.GetGameRoute.MESSAGE_ATTR;
import static com.webcheckers.ui.PostCheckTurnRoute.OPPONENT_RESIGNED_ATTR;
import static com.webcheckers.ui.PostResignRoute.RESIGNED_ATTR;
import static com.webcheckers.ui.PostSignInRoute.PLAYER_LIST_ATTR;
import static com.webcheckers.ui.PostSignInRoute.USER_ATTR;
import static com.webcheckers.ui.PostSignInRoute.USER_SIGNED_IN_ATTR;
import static com.webcheckers.ui.PostSubmitTurnRoute.GAME_OVER_ATTR;
import static com.webcheckers.ui.WebServer.GAME_URL;
import static com.webcheckers.ui.WebServer.START_GAME_URL;
import static spark.Spark.halt;

/**
 * The UI Controller to GET the Home page.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * edited by Johnny, Disney, Andy, Ani
 */
public class GetHomeRoute implements Route {
    private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

    private final TemplateEngine templateEngine;

    static final String TITLE_ATTR = "title";
    static final String TITLE_VAL = "Welcome!";
    static final String NUM_PLAYERS_ATTR = "numPlayers";
    static final String IN_GAME_ATTR = "inGame";
    static final String GAMES_ATTR = "gameList";
    private PlayerLobby playerLobby;
    private GameCenter gameCenter;

    /**
     * Create the Spark Route (UI controller) for the
     * {@code GET /} HTTP request.
     *
     * @param templateEngine the HTML template rendering engine
     */
    public GetHomeRoute(final TemplateEngine templateEngine, final PlayerLobby playerLobby, final GameCenter gameCenter) {

        // validation
        Objects.requireNonNull(playerLobby, "playerLobby must not be null");
        Objects.requireNonNull(templateEngine, "templateEngine must not be null");
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        //
        this.templateEngine = templateEngine;
        this.playerLobby = playerLobby;
        this.gameCenter = gameCenter;
        //
        LOG.config("GetHomeRoute is initialized.");
    }

    /**
     * Render the WebCheckers Home page.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return the rendered HTML for the Home page
     */
    @Override
    public Object handle(Request request, Response response) {
        LOG.config("GetHomeRoute is invoked.");
        Map<String, Object> vm = new HashMap<>();
        Session session = request.session();
        vm.put(TITLE_ATTR, TITLE_VAL);
        vm.put(NUM_PLAYERS_ATTR, playerLobby.getNumPlayers());
        if (!session.isNew()) {
            //The user is signed in
            if (session.attribute(USER_SIGNED_IN_ATTR) != null) {
                Player player = session.attribute(USER_ATTR);
                Objects.requireNonNull(player, "player must not be null");
                if (!playerLobby.isSignedIn(player)) {
                    session.removeAttribute(USER_ATTR);
                    session.removeAttribute(USER_SIGNED_IN_ATTR);
                    playerLobby.signOutPlayer(player.getName());
                    return templateEngine.render(new ModelAndView(vm, "home.ftl"));
                }
                if (gameCenter.isInGame(player)) {
                    vm.put(IN_GAME_ATTR, true);
                }
                if (session.attribute(OPPONENT_RESIGNED_ATTR) != null) {
                    //The game is over and the opponent has resigned.
                    session.removeAttribute(OPPONENT_RESIGNED_ATTR);
                    vm.put(MESSAGE_ATTR, true);
                    vm.put(MESSAGE, "Opponent has quit, you have won!");
                } else if (session.attribute(RESIGNED_ATTR) != null) {
                    //The game is over and the opponent has resigned.
                    session.removeAttribute(RESIGNED_ATTR);
                    vm.put(MESSAGE_ATTR, true);
                    vm.put(MESSAGE, "Game is over. You have resigned.");
                }
                if (session.attribute(GAME_OVER_ATTR) != null){
                    session.removeAttribute(GAME_OVER_ATTR);
                    Game game = session.attribute(GAME_ATTR);
                    gameCenter.removeGame(game);
                    if (game.getWinner().equals(player)) {
                        vm.put(MESSAGE_ATTR, true);
                        vm.put("error", "You have won!");
                    } else {
                        vm.put(MESSAGE_ATTR, true);
                        vm.put("error", "You have lost!");
                    }
                }
                vm.put(PostSignInRoute.USER_SIGNED_IN_ATTR, true);
                vm.put(USER_ATTR, player.getName());
                vm.put(GAMES_ATTR, gameCenter.gameList(player.getName()));
                vm.put(PLAYER_LIST_ATTR, playerLobby.playerList(player.getName()));            }
        }
        return templateEngine.render(new ModelAndView(vm, "home.ftl"));
    }
}