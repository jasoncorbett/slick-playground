package com.slickqa.slickqaweb.routes.api;

import com.google.inject.Inject;
import com.slickqa.slickqaweb.AutoloadComponent;
import com.slickqa.slickqaweb.Configuration;
import com.slickqa.slickqaweb.database.MongoInsertHandler;
import com.slickqa.slickqaweb.database.MongoQueryHandler;
import com.slickqa.slickqaweb.database.types.Project;
import com.slickqa.slickqaweb.startupComponentType.OnStartup;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * The projects api endpoint
 */
@AutoloadComponent
public class projects implements OnStartup {
    private Router router;
    private EventBus eventBus;
    private Configuration config;
    private Logger log;

    @Inject
    public projects(Router router, EventBus eventBus, Configuration configuration) {
        this.router = router;
        this.eventBus = eventBus;
        this.config = configuration;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void onStartup() {
        router.route(HttpMethod.GET, config.getUrlBasePath() + "api/projects").handler(this::getProjects);
        router.route(HttpMethod.POST, config.getUrlBasePath() + "api/projects").handler(this::addProject);
    }

    public void getProjects(RoutingContext ctx) {
        eventBus.send(MongoQueryHandler.Address, new JsonObject()
                .put("type", Project.type)
                .put("query", new JsonObject()), result -> {
            if(result.succeeded()) {
                ctx.response()
                    .putHeader("Content-Type", "application/json; charset=utf-8")
                    .setStatusCode(200)
                    .end(Json.encodePrettily(result.result().body()));
            } else {
                ctx.response()
                    .putHeader("Content-Type", "application/json; charset=utf-8")
                    .setStatusCode(500)
                    .end(Json.encodePrettily(result.cause()));
            }
        });
    }

    public void addProject(RoutingContext ctx) {
        log.info("Adding project: {0}", Json.encodePrettily(ctx.getBodyAsJson()));
        eventBus.send(MongoInsertHandler.Address, new JsonObject()
                .put("type", Project.type)
                .put("insert", ctx.getBodyAsJson()), result -> {
            if(result.succeeded()) {
                ctx.response()
                        .putHeader("Content-Type", "application/json; charset=utf-8")
                        .setStatusCode(200)
                        .end(Json.encodePrettily(result.result().body()));
            } else {
                ctx.response()
                        .putHeader("Content-Type", "application/json; charset=utf-8")
                        .setStatusCode(500)
                        .end(Json.encodePrettily(result.cause()));
            }
        });
    }
}
