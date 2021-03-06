package com.webcheckers.ui;

import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Player;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import static com.webcheckers.ui.GetSignInRoute.SIGN_IN_HELP_ATTR;
import static com.webcheckers.ui.GetSignInRoute.SIGN_IN_HELP_MESSAGE;
import static spark.Spark.halt;

/**
 * This dictates the action to take once the player has tried to sign in. If the player enters a valid username then
 * it will save the player and return the home page where the player can see other players signed on
 * and choose someone to play with
 * Created by Juna, Disney, Andy, and Ani on 10/12/2017.
 */

public class PostSignInRoute implements Route {
    private final TemplateEngine templateEngine;
    private final PlayerLobby playerLobby;

    static final String INVALID_SIGN_IN_ATTR = "invalidSignInMessage";
    static final String USER_SIGNED_IN_ATTR = "userSignedIn";
    static final String PLAYER_LIST_ATTR = "playerList";
    static final String USER_ATTR = "user";


    public PostSignInRoute(TemplateEngine templateEngine, PlayerLobby playerLobby) {
        Objects.requireNonNull(templateEngine, "template engine cannot be empty");
        Objects.requireNonNull(playerLobby, "playerlobby cannot be empty");
        this.playerLobby = playerLobby;
        this.templateEngine = templateEngine;
    }


    /**
     * Handles the sign in of the player. Validates the input and either renders the home page or redirects to the sign
     * in page if the sign in failed.
     * @param request the HTTP request
     * @param response the HTTP response
     * @return
     * @throws Exception
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        final Map<String, Object> vm = new HashMap<>();
        final Session session = request.session();
        final String userName = request.queryParams("username");
        final String password = request.queryParams("password");
        vm.put(SIGN_IN_HELP_ATTR, SIGN_IN_HELP_MESSAGE);

        if (playerLobby.hasAccount(userName)){
            //User is trying to sign in to a pre-existing account.
            Player player = playerLobby.getPlayer(userName);
            /*if (player.isSignedIn()){
                vm.put(GetSignInRoute.SIGN_IN_MESSAGE_ATTR, GetSignInRoute.SIGN_IN_MESSAGE);
                vm.put(INVALID_SIGN_IN_ATTR, "ERROR. This username has already been taken.");
                return templateEngine.render(new ModelAndView(vm, "signIn.ftl"));
            }*/
            if(player.checkPassword(password)){
                player.signIn();
                session.attribute(USER_SIGNED_IN_ATTR, true);
                session.attribute(USER_ATTR, playerLobby.getPlayer(userName));
                response.redirect(WebServer.HOME_URL);
                halt();
                return null;
            }
            else{
                vm.put(GetSignInRoute.SIGN_IN_MESSAGE_ATTR, GetSignInRoute.SIGN_IN_MESSAGE);
                vm.put(INVALID_SIGN_IN_ATTR, "ERROR. Incorrect password.");
                return templateEngine.render(new ModelAndView(vm, "signIn.ftl"));
            }
        }
        else{
            //User is trying to create a new account
            switch (playerLobby.signInPlayer(userName, password)){
                case SIGNED_IN:
                    //Store the Player Object into the session.
                    session.attribute(USER_SIGNED_IN_ATTR, true);
                    session.attribute(USER_ATTR, playerLobby.getPlayer(userName));
                    response.redirect(WebServer.HOME_URL);
                    halt();
                    return null;
                case INVALID_INPUT:
                    vm.put(GetSignInRoute.SIGN_IN_MESSAGE_ATTR, GetSignInRoute.SIGN_IN_MESSAGE);
                    vm.put(INVALID_SIGN_IN_ATTR, "ERROR. Invalid name.");
                    return templateEngine.render(new ModelAndView(vm, "signIn.ftl"));
                case INVALID_PLAYER:
                    vm.put(GetSignInRoute.SIGN_IN_MESSAGE_ATTR, GetSignInRoute.SIGN_IN_MESSAGE);
                    vm.put(INVALID_SIGN_IN_ATTR, "Player name " + userName + " is already in use");
                    return templateEngine.render(new ModelAndView(vm, "signIn.ftl"));
                case SIGNED_OUT:
                    response.redirect(WebServer.HOME_URL);
                    halt();
                    return null;
                default:
                    throw new NoSuchElementException("Invalid result of sign in received");
            }
        }
    }
}