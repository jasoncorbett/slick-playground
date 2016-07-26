package com.slickqa.slickqaweb.web;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.slickqa.slickqaweb.MongoFactory;
import com.slickqa.slickqaweb.web.startupComponentType.OnStartup;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.mongo.MongoAuth;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.Set;

/**
 * Starts Slick Web Interface
 */
public class SlickWebVerticle extends AbstractVerticle {

    @Inject
    private Set<OnStartup> startupSet;

    @Inject
    private Router router;

    @Inject
    Configuration config;

    @Override
    public void start() throws Exception {
        Logger logger = LoggerFactory.getLogger(SlickWebVerticle.class);

        // configure mongo client
        MongoClient mongo = MongoFactory.buildMongo(vertx);

        // create authentication handler
        MongoAuth mongoAuth = MongoFactory.buildMongoAuth(vertx, mongo);

        // startup
        logger.debug("Configuring Guice Injector");
        Injector injector = Guice.createInjector(new SlickGuiceModule(vertx, mongo, mongoAuth));
        injector.injectMembers(this);

        // add body handler to the necessary routes
        BodyHandler bodyHandler = BodyHandler.create();
        router.route(HttpMethod.POST, config.getUrlBasePath() + "api/*").handler(bodyHandler);
        router.route(HttpMethod.PUT, config.getUrlBasePath() + "api/*").handler(bodyHandler);

        // perform startup
        for(OnStartup startupComponent: startupSet) {
            startupComponent.onStartup();
        }

        // handle static resources
        router.route(config.getUrlBasePath() + "*").handler(StaticHandler.create());

        // finally start http server
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(config.getPort());

        logger.info("Slick listening on port " + config.getPort());
    }
}
