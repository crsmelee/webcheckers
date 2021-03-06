package com.webcheckers.ui;

import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;


/**
 * from the home page, this displays the sign in page. This is the initial sign in page if the play has not tried to
 * sign in yet.
 * created by Johnny, Disney, Andy, Ani
 */

public class GetSignInRoute implements Route {
    private static final Logger LOG = Logger.getLogger(GetSignInRoute.class.getName());
    private final TemplateEngine templateEngine;

    static final String SIGN_IN_MESSAGE_ATTR = "signInMessage";
    static final String SIGN_IN_MESSAGE = "This game requires you to make an account. An account must have a username and a password";
    static final String SIGN_IN_HELP_ATTR = "signInHelp";
    static final String SIGN_IN_HELP_MESSAGE = "If you have an account, just sign in with your username and password. Otherwise, I'll automatically make an account for you.";



    /**
     * Create the Spark Route (UI controller) for the
     * {@code GET /} HTTP request.
     *
     * @param templateEngine the HTML template rendering engine
     */
    public GetSignInRoute(TemplateEngine templateEngine) {
        //Validation
        Objects.requireNonNull(templateEngine, "template engine cannot be empty");
        this.templateEngine = templateEngine;
        LOG.config("GetSignInRoute is initialized.");
    }


    /**
     * Render the WebCheckers SignIn page.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return the rendered HTML for the SignIn page
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        LOG.config("GetSignInRoute is invoked.");
        Map<String, Object> vm = new HashMap<>();
        vm.put(SIGN_IN_MESSAGE_ATTR, SIGN_IN_MESSAGE);
        vm.put(SIGN_IN_HELP_ATTR, SIGN_IN_HELP_MESSAGE);
        return templateEngine.render(new ModelAndView(vm, "signIn.ftl"));
    }
}
