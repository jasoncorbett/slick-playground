package com.slickqa.slickqaweb.database;

import com.google.inject.Inject;
import com.slickqa.slickqaweb.AutoloadComponent;
import com.slickqa.slickqaweb.startupComponentType.OnStartup;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Component to handle inserting data into the database
 */
@AutoloadComponent
public class MongoInsertHandler implements OnStartup {
    private MongoClient mongo;
    private EventBus eb;
    private Map<String, DatabaseType> dbTypes;

    @Inject
    public MongoInsertHandler(MongoClient mongo, EventBus eb, Set<DatabaseType> dbTypes) {
        this.mongo = mongo;
        this.eb = eb;
        this.dbTypes = new HashMap<>();
        for(DatabaseType databaseType : dbTypes) {
            this.dbTypes.put(databaseType.getTypeName(), databaseType);
        }
    }

    @Override
    public void onStartup() {
        this.eb.consumer("slick.db.insert", this::handleInsert);
    }

    public void handleInsert(Message<JsonObject> message) {

    }
}
