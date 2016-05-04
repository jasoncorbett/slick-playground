package com.slickqa.slickqaweb.routes.api;

import com.slickqa.slickqaweb.routes.Routable;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jason on 5/1/2016.
 */
public class lee implements Routable {

    @Override
    public void configureRoutes(String baseUrlPath, Router router) {
        router.route(Routable.joinUrlPieces(baseUrlPath, "api/:projectName/lee")).handler(this::sucks);
    }

    public void sucks(RoutingContext ctx) {
        JsonObject retval = new JsonObject();
        retval.put("lee", "sucks");
        ctx.response()
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .setStatusCode(200)
                .end(Json.encodePrettily(retval));
    }
}
