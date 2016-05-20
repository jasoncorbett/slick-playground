package com.slickqa.slickqaweb;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.slickqa.slickqaweb.database.MongoDBProvider;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Configure Guice injection for slick.  Reads Named String values from Vert.x
 *
 */
public class SlickGuiceModule extends AbstractModule {
    private final Vertx vertx;
    private final Reflections reflections;

    public SlickGuiceModule(Vertx vertx) {
        this.vertx = vertx;
        reflections = new Reflections("com.slickqa");
    }

    @Override
    protected void configure() {
        bind(Vertx.class).toInstance(vertx);
        bind(EventBus.class).toInstance(vertx.eventBus());
        bind(SharedData.class).toInstance(vertx.sharedData());
        bind(Router.class).toInstance(Router.router(vertx));
        bind(Configuration.class).toInstance(new VertxContextConfiguration(vertx.getOrCreateContext().config()));
        bind(MongoClient.class).toProvider(MongoDBProvider.class);
        Set<Class> collectables = new HashSet<Class>();
        for(Class cls : reflections.getTypesAnnotatedWith(CollectableComponentType.class)) {
            if(cls.isInterface()) {
                collectables.add(cls);
            }
        }
        addBindingsFor(collectables);
    }

    protected void addBindingsFor(Set<Class> collectables) {
        Map<Class, Multibinder> binders = new HashMap<>();
        for(Class collectable : collectables) {
            binders.put(collectable, Multibinder.newSetBinder(binder(), collectable));
        }
        for(Class component : reflections.getTypesAnnotatedWith(AutoloadComponent.class)) {
            for(Class collectable : collectables) {
                if(collectable.isAssignableFrom(component) &&
                   !component.isInterface() &&
                   !Modifier.isAbstract(component.getModifiers())) {
                    binders.get(collectable).addBinding().to(component);
                }
            }
        }
    }
}
