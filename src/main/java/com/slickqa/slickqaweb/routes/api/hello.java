package com.slickqa.slickqaweb.routes.api;

import com.google.inject.Inject;
import com.slickqa.slickqaweb.Configuration;
import com.slickqa.slickqaweb.startupComponentType.OnStartup;
import com.slickqa.slickqaweb.StartupComponent;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * hello REST endpoint
 */
@StartupComponent
public class hello implements OnStartup {
    private Router router;
    private Configuration config;

    @Inject
    public hello(Router router, Configuration config) {
        this.router = router;
        this.config = config;
    }

    @Override
    public void onStartup() {
        router.route(config.getUrlBasePath() + "api/:packageName/hello").handler(this::sayHello);
    }

    public void sayHello(RoutingContext ctx) {
        JsonObject retval = new JsonObject();
        retval.put("hello", "world");
        ctx.response()
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .setStatusCode(200)
                .end(Json.encodePrettily(retval));
    }
}
