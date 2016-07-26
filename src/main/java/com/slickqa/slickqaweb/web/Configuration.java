package com.slickqa.slickqaweb.web;

import io.vertx.core.json.JsonObject;

/**
 * Configuration for slick.
 */
public interface Configuration {
    String getUrlBasePath();
    int getPort();
    JsonObject getMongoDBConfig();
    String getAdminUsername();
    String getAdminPassword();
}
