package com.slickqa.slickqaweb.web;

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
    public JsonObject getMongoDBConfig() {
        return config.getJsonObject("mongo");
    }

    @Override
    public String getAdminUsername() {
        return config.getString("adminUsername", "admin");
    }

    @Override
    public String getAdminPassword() {
        return config.getString("adminPassword");
    }
}
