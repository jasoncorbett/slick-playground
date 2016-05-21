package com.slickqa.slickqaweb.database;

import com.google.inject.Inject;
import com.slickqa.slickqaweb.AutoloadComponent;
import com.slickqa.slickqaweb.startupComponentType.OnStartup;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

/**
 * Component to handle querying the mongo database
 */
@AutoloadComponent
public class MongoQueryHandler implements OnStartup {
    private MongoClient mongo;
    private EventBus eb;
    private Map<String, DatabaseType> dbTypes;

    public static final String Address = "slick.db.query";

    @Inject
    public MongoQueryHandler(MongoClient mongo, EventBus eb, Set<DatabaseType> dbTypes) {
        this.mongo = mongo;
        this.eb = eb;
        this.dbTypes = new HashMap<>();
        for(DatabaseType databaseType : dbTypes) {
            this.dbTypes.put(databaseType.getTypeName(), databaseType);
        }
    }

    @Override
    public void onStartup() {
        this.eb.consumer(MongoQueryHandler.Address, this::handleQuery);
    }

    public void handleQuery(Message<JsonObject> message) {
        // validate query
        String type = message.body().getString("type");
        if(dbTypes.containsKey(type)) {
            DatabaseType dbType = dbTypes.get(type);
            List<String> errors = dbType.validateQuery(message.body().getJsonObject("query"));
            if(errors != null && errors.size() > 0) {
                message.fail(20, Json.encode(new JsonObject().put("errors", new JsonArray(errors))));
            } else {
                mongo.find(dbType.getCollectionName(), message.body().getJsonObject("query"), result -> {
                    if(result.succeeded()) {
                        message.reply(new JsonArray(result.result()));
                    } else {
                        message.fail(30, result.cause().getMessage());
                    }
                });
            }
        } else {
            message.fail(10, "Must specify type for query");
        }
    }
}
