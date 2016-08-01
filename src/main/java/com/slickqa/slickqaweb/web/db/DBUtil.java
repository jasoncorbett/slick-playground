package com.slickqa.slickqaweb.web.db;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Used to make simple certain actions (like creating indexes) more simple.
 */
public class DBUtil {

    public static void createIndex(MongoClient mongo, String collection, JsonObject index, Handler<AsyncResult<JsonObject>> handler) {
        mongo.runCommand("createIndexes",
                new JsonObject()
                        .put("createIndexes", collection)
                .put("indexes", new JsonArray().add(index)), handler);
    }
}
