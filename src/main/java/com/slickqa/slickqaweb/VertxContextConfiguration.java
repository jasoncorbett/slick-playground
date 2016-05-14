package com.slickqa.slickqaweb;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * Slick configuration that uses Vertx Context configuration to
 * override defaults.
 */
public class VertxContextConfiguration implements Configuration {
    private JsonObject config;

    public VertxContextConfiguration(JsonObject config) {
        this.config = config;
    }

    @Override
    public String getUrlBasePath() {
        return config.getString("urlBasePath", "/");
    }

    @Override
    public int getPort() {
        return config.getInteger("port", 9000);
    }

    @Override
    public String getMongoDBHostname() {
        return config.getString("mongoHostname", "localhost");
    }

    @Override
    public int getMongoDBPort() {
        return config.getInteger("mongoPort", 27017);
    }

    @Override
    public String getMongoDBUsername() {
        return config.getString("mongoUsername", "");
    }

    @Override
    public String getMongoDBPassword() {
        return config.getString("mongoPassword", "");
    }

    @Override
    public String getMongoDBName() {
        return config.getString("mongoDatabase", "slick");
    }
}
