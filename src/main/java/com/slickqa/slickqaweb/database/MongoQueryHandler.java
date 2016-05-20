package com.slickqa.slickqaweb.database;

import com.google.inject.Inject;
import com.slickqa.slickqaweb.AutoloadComponent;
import com.slickqa.slickqaweb.startupComponentType.OnStartup;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Created by jason.corbett on 5/18/16.
 */
@AutoloadComponent
public class MongoQueryHandler implements OnStartup {
    private MongoClient mongo;
    private EventBus eb;

    @Inject
    public MongoQueryHandler(MongoClient mongo, EventBus eb) {
        this.mongo = mongo;
        this.eb = eb;
    }

    @Override
    public void onStartup() {
        this.eb.consumer("slick.db.query", this::handleQuery);
    }

    public void handleQuery(Message<JsonObject> message) {
        // validate query

    }
}
