package com.slickqa.slickqaweb.routes.api;

import com.slickqa.slickqaweb.routes.Routable;
import com.google.inject.Inject;
import com.slickqa.slickqaweb.routes.AbstractPackageRoutableResource;
import com.slickqa.slickqaweb.routes.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jason on 5/1/2016.
 */
public class lee implements Routable {
    @Inject
    private MongoClient mongo;

    @Override
    public void configureRoutes(String baseUrlPath, Router router) {
        router.route(Routable.joinUrlPieces(baseUrlPath, "api/:projectName/lee")).handler(this::sucks);
    }

    @Handler
    public void sucks(RoutingContext ctx) {
        JsonObject retval = new JsonObject();
        //retval.put("stud", "lee");
        ctx.response()
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .setStatusCode(200)
                .end(Json.encodePrettily(retval));
    }
}
