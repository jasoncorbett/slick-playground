package com.slickqa.slickqaweb;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.slickqa.slickqaweb.routes.Routable;
import com.slickqa.slickqaweb.routes.RoutableResourceCollection;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.Counter;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.impl.EventBusBridgeImpl;

import java.util.UUID;

/**
 * Starts Slick Web Interface
 */
public class SlickVerticle extends AbstractVerticle {

    private String address = UUID.randomUUID().toString();

    @Inject(optional = true)
    @Named("port")
    private String port = "9000";

    @Inject(optional = true)
    @Named("baseUrlPath")
    private String baseUrlPath = "/";

    @Inject
    private RoutableResourceCollection resources;

    @Inject
    private EventBus eb;

    @Inject
    private SharedData sd;

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

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        PermittedOptions inboundPermitted = new PermittedOptions().setAddress("counter.increment");
        PermittedOptions outboundPermitted = new PermittedOptions().setAddress("counter.changed");
        BridgeOptions options = new BridgeOptions()
                .addInboundPermitted(inboundPermitted)
                .addOutboundPermitted(outboundPermitted);
        sockJSHandler.bridge(options);

        router.route("/eventbus/*").handler(sockJSHandler);
        eb.consumer("counter.increment", message -> {
            sd.getCounter("clickcounter", res -> {
                Counter clickcounter = res.result();
                clickcounter.incrementAndGet(count -> {
                    logger.info(address + ": Recieved request to increment, counter at " + count.result() + "!");
                    eb.publish("counter.changed", new JsonObject().put("counter", count.result()));
                });

            });
        });

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(Integer.parseInt(port));
        logger.info("Slick listening on port " + port);
    }
}
