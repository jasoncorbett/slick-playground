package com.slickqa.slickqaweb.routes.api;

import com.google.inject.Inject;
import com.slickqa.slickqaweb.Configuration;
import com.slickqa.slickqaweb.startupComponentType.OnStartup;
import com.slickqa.slickqaweb.StartupComponent;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * A test REST api endpoint for Lee who sucks.
 */
@StartupComponent
public class lee implements OnStartup {
    private MongoClient mongo;
    private Router router;
    private Configuration config;

    @Inject
    public lee(MongoClient mongo, Router router, Configuration config) {
        this.mongo = mongo;
        this.router = router;
        this.config = config;
    }

    @Override
    public void onStartup() {
        router.route(config.getUrlBasePath() + "api/:projectName/lee").handler(this::sucks);
    }

    public void sucks(RoutingContext ctx) {
        JsonObject retval = new JsonObject();
        //retval.put("stud", "lee");
        ctx.response()
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .setStatusCode(200)
                .end(Json.encodePrettily(retval));
    }
}
