package com.slickqa.slickqaweb;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.slickqa.slickqaweb.routes.Routable;
import com.slickqa.slickqaweb.routes.RoutableResourceCollection;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

/**
 * Starts Slick Web Interface
 */
public class SlickVerticle extends AbstractVerticle {

    @Inject(optional = true)
    @Named("port")
    private String port = "9000";

    @Inject(optional = true)
    @Named("baseUrlPath")
    private String baseUrlPath = "/";

    @Inject
    private RoutableResourceCollection resources;

    @Override
    public void start() throws Exception {
        Logger logger = LoggerFactory.getLogger(SlickVerticle.class);

        logger.debug("Configuring Guice Injector");
        Injector injector = Guice.createInjector(new SlickGuiceModule(vertx));
        injector.injectMembers(this);

        Router router = Router.router(vertx);
        for(Routable routable : resources.getRoutableResources(injector)) {
            logger.info("Configuring routes for " + routable.getClass().getName());
            routable.configureRoutes(baseUrlPath, router);
        }

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(Integer.parseInt(port));
        logger.info("Slick listening on port " + port);
    }
}
