package com.slickqa.slickqaweb.routes.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.slickqa.slickqaweb.Configuration;
import com.slickqa.slickqaweb.SocksJSBridge;
import com.slickqa.slickqaweb.StartupComponent;
import com.slickqa.slickqaweb.startupComponentType.AddsSocksJSBridgeOptions;
import com.slickqa.slickqaweb.startupComponentType.OnStartup;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Counter;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;

/**
 * This is a component that increments a counter every time it's fetched.
 * It also broadcasts this to the event bus.  It can also be incremented
 * by sending a counter.increment message to the event bus.
 */
@Singleton
@StartupComponent
public class counter implements OnStartup, AddsSocksJSBridgeOptions {
    private EventBus eb;
    private Configuration config;
    private Router router;
    private SharedData sd;

    @Inject
    public counter(EventBus eb, Configuration config, Router router, SharedData sd) {
        this.eb = eb;
        this.config = config;
        this.router = router;
        this.sd = sd;
    }

    @Override
    public void addToSocksJSBridgeOptions(BridgeOptions options) {
        options.addInboundPermitted(new PermittedOptions().setAddress("counter.increment"));
        options.addOutboundPermitted(new PermittedOptions().setAddress("counter.changed"));
    }

    @Override
    public void onStartup() {
        eb.consumer("counter.increment", message -> {
            sd.getCounter("clickcounter", res -> {
                Counter clickcounter = res.result();
                clickcounter.incrementAndGet(count -> {
                    //logger.info(address + ": Recieved request to increment, counter at " + count.result() + "!");
                    JsonObject result = new JsonObject().put("counter", count.result());
                    eb.publish("counter.changed", result);
                    message.reply(result);
                });
            });
        });
        eb.consumer(SocksJSBridge.REGISTER_EVENT_PREFIX + "counter.changed", message -> {
            sd.getCounter("clickcounter", res -> {
                Counter clickcounter = res.result();
                clickcounter.get(count -> {
                    //logger.info("New client, publishing current counter value");
                    eb.publish("counter.changed", new JsonObject().put("counter", count.result()));
                });
            });
        });
        router.route(config.getUrlBasePath() + "api/counter").handler(this::getCount);
    }

    public void getCount(RoutingContext ctx) {
        eb.send("counter.increment", new JsonObject(), message -> {
            ctx.response()
                    .putHeader("Content-Type", "application/json; charset=utf-8")
                    .setStatusCode(200)
                    .end(Json.encodePrettily(message.result().body()));
        });
    }

}
