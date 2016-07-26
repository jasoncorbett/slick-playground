package com.slickqa.slickqaweb;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.mongo.MongoAuth;
import io.vertx.ext.mongo.MongoClient;

/**
 * Created by jason.corbett on 7/25/16.
 */
public class MongoFactory {

    public static MongoClient buildMongo(Vertx vertx) {
        JsonObject defaultMongoConfig = new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("useObjectId", true)
                .put("db_name", "slick");
        return MongoClient.createShared(vertx, vertx.getOrCreateContext().config().getJsonObject("mongo", defaultMongoConfig));
    }

    public static MongoAuth buildMongoAuth(Vertx vertx, MongoClient mongo) {
        JsonObject defaultAuthConfig = new JsonObject()
                .put(MongoAuth.PROPERTY_COLLECTION_NAME, "users")
                .put(MongoAuth.PROPERTY_SALT_STYLE, "EXTERNAL");
        MongoAuth mongoAuth = MongoAuth.create(mongo, vertx.getOrCreateContext().config().getJsonObject("mongoAuth", defaultAuthConfig));
        mongoAuth.getHashStrategy().setExternalSalt(vertx.getOrCreateContext().config().getString("salt"));
        return mongoAuth;
    }
}
