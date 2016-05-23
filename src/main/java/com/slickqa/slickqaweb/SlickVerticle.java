package com.slickqa.slickqaweb;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.slickqa.slickqaweb.startupComponentType.OnStartup;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.Set;

/**
 * Starts Slick Web Interface
 */
public class SlickVerticle extends AbstractVerticle {

    @Inject
    private Set<OnStartup> startupSet;

    @Inject
    private Router router;

    @Inject
    Configuration config;

    @Override
    public void start() throws Exception {
        Logger logger = LoggerFactory.getLogger(SlickVerticle.class);

        logger.debug("Configuring Guice Injector");
        Injector injector = Guice.createInjector(new SlickGuiceModule(vertx));
        injector.injectMembers(this);
        BodyHandler bodyHandler = BodyHandler.create();
        router.route(HttpMethod.POST, config.getUrlBasePath() + "api/*").handler(bodyHandler);
        router.route(HttpMethod.PUT, config.getUrlBasePath() + "api/*").handler(bodyHandler);

        for(OnStartup startupComponent: startupSet) {
            startupComponent.onStartup();
        }

        router.route(config.getUrlBasePath() + "*").handler(StaticHandler.create());

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(config.getPort());
        logger.info("Slick listening on port " + config.getPort());
    }
}
