package com.slickqa.slickqaweb.database;


import com.google.inject.Inject;
import com.google.inject.Provider;
import com.slickqa.slickqaweb.Configuration;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Creates an instance of MongoDB Connection based on configuration.
 */
public class MongoDBProvider implements Provider<MongoClient> {
    private Configuration config;
    private Vertx vertx;
    private MongoClient singletonClient;

    @Inject
    public MongoDBProvider(Configuration config, Vertx vertx) {
        singletonClient = null;
        this.config = config;
        this.vertx = vertx;
    }

    @Override
    public MongoClient get() {
        if(singletonClient == null) {
            JsonObject mongoConfig = new JsonObject()
                    .put("connection_string", "mongodb://" + config.getMongoDBHostname() + ":" + config.getMongoDBPort())
                    .put("db_name", config.getMongoDBName());
            singletonClient = MongoClient.createShared(vertx, mongoConfig);
        }
        return singletonClient;
    }
}
