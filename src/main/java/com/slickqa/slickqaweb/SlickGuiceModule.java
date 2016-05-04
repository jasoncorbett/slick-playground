package com.slickqa.slickqaweb;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.slickqa.slickqaweb.routes.PackageScanningRoutableResourceCollection;
import com.slickqa.slickqaweb.routes.RoutableResourceCollection;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.SharedData;

import java.util.Properties;

/**
 * Configure Guice injection for slick.  Reads Named String values from Vert.x
 *
 */
public class SlickGuiceModule extends AbstractModule {
    private final Vertx vertx;
    private final Context context;

    public SlickGuiceModule(Vertx vertx) {
        this.vertx = vertx;
        this.context = vertx.getOrCreateContext();
    }

    @Override
    protected void configure() {
        bind(EventBus.class).toInstance(vertx.eventBus());
        bind(SharedData.class).toInstance(vertx.sharedData());
        bind(RoutableResourceCollection.class).to(PackageScanningRoutableResourceCollection.class);
        Names.bindProperties(binder(), extractToProperties(context.config()));
    }

    private Properties extractToProperties(JsonObject config) {
        Properties properties = new Properties();
        config.getMap().keySet().stream().forEach((String key) -> {
            properties.setProperty(key, (String) config.getValue(key));
        });
        return properties;
    }
}
