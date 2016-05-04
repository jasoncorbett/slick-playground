package com.slickqa.slickqaweb.routes;

import io.vertx.ext.web.Router;

/**
 * This interface represents a class that can describe what routes it has.
 */
public interface Routable {
    void configureRoutes(String baseUrlPath, Router router);

    static String joinUrlPieces(String... routePieces) {
        StringBuilder retval = new StringBuilder();
        if(routePieces.length > 0) {
            if(!routePieces[0].startsWith("/")) {
                retval.append("/");
            }
            for(String piece : routePieces) {
                if(retval.length() > 0 && retval.charAt(retval.length() - 1) != '/') {
                    retval.append("/");
                }
                retval.append(piece);
            }
        }
        return retval.toString();
    }
}
