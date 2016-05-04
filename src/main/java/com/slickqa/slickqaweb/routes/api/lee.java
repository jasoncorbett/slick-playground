package com.slickqa.slickqaweb.routes.api;

import com.slickqa.slickqaweb.routes.AbstractPackageRoutableResource;
import com.slickqa.slickqaweb.routes.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jason on 5/1/2016.
 */
public class lee extends AbstractPackageRoutableResource {

    @Handler
    public void sucks(RoutingContext ctx) {
        JsonObject retval = new JsonObject();
        retval.put("lee", "sucks");
        ctx.response()
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .setStatusCode(200)
                .end(Json.encodePrettily(retval));
    }
}
