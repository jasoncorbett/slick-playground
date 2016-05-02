package com.slickqa.slickqaweb.routes;

import io.vertx.ext.web.Router;

/**
 * This interface represents a class that can describe what routes it has.
 */
public interface Routable {
    void configureRoutes(String baseUrlPath, Router router);
}
