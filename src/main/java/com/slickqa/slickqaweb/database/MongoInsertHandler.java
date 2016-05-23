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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
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

    public static final String Address = "slick.db.insert";

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
        this.eb.consumer(MongoInsertHandler.Address, this::handleInsert);
    }

    public void handleInsert(Message<JsonObject> message) {
        // validate json
        String type = message.body().getString("type");
        if(dbTypes.containsKey(type)) {
            DatabaseType dbType = dbTypes.get(type);
            List<String> errors = dbType.validateInsert(message.body().getJsonObject("insert"));
            if(errors != null && errors.size() > 0) {
                message.fail(20, Json.encode(new JsonObject().put("errors", new JsonArray(errors))));
            } else {
                mongo.insert(dbType.getCollectionName(), message.body().getJsonObject("insert"), result -> {
                    if(result.succeeded()) {
                        String id = result.result();
                        mongo.findOne(dbType.getCollectionName(), new JsonObject().put("_id", id), null, findOneResult -> {
                            if(findOneResult.succeeded()) {
                                message.reply(findOneResult.result());
                            } else {
                                message.fail(30, MessageFormat.format("Failed to fetch newly inserted object with id {0}.", id));
                            }
                        });
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
