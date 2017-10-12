package com.webcheckers.ui;

import com.webcheckers.appl.PlayerLobby;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Created by Juna on 10/12/2017.
 */

public class PostSignInRoute implements Route {


    private final TemplateEngine templateEngine;
    private final PlayerLobby playerLobby;

    static final String SIGN_IN_MESSAGE_ATTR = "signInMessage";
    static final String SIGN_IN_MESSAGE = "Please enter a valid username: ";
    static final String INVALID_SIGN_IN_ATTR = "invalidSignInMessage";
    static final String PLAYER_SIGNED_IN_ATTR = "plaerSignedIn";


    public PostSignInRoute(TemplateEngine templateEngine, PlayerLobby playerLobby) {
        Objects.requireNonNull(templateEngine, "template engine cannot be empty");
        Objects.requireNonNull(playerLobby, "playerlobby cannot be empty");
        this.playerLobby = playerLobby;
        this.templateEngine = templateEngine;
    }


    @Override
    public Object handle(Request request, Response response) throws Exception {
    	final Map<String,Object> vm = new HashMap<>();
        final String userName = request.queryParams("username");
        switch (playerLobby.signInPlayer(userName)){
            case SIGNED_IN:
                final Session session = request.session();
                session.attribute("player", userName);
                vm.put(PLAYER_SIGNED_IN_ATTR, "fuck you disney. jk i luh ya b");
                break;
            case INVALID_INPUT:
                vm.put(SIGN_IN_MESSAGE_ATTR, SIGN_IN_MESSAGE);
                vm.put(INVALID_SIGN_IN_ATTR, "Please input a valid username");
                return templateEngine.render(new ModelAndView(vm, "signIn.ftl"));
            case INVALID_PLAYER:
                vm.put(SIGN_IN_MESSAGE_ATTR, SIGN_IN_MESSAGE);
                vm.put(INVALID_SIGN_IN_ATTR, "Player name is already in use");
                return templateEngine.render(new ModelAndView(vm, "signIn.ftl"));
            default:
                throw new NoSuchElementException("Invalid result of sign in received");
        }
	    throw new NoSuchElementException("Invalid result of sign in received");
    }
}
