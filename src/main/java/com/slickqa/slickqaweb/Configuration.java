package com.slickqa.slickqaweb;

/**
 * Configuration for slick.
 */
public interface Configuration {
    String getUrlBasePath();
    int getPort();
    String getMongoDBHostname();
    int getMongoDBPort();
    String getMongoDBUsername();
    String getMongoDBPassword();
    String getMongoDBName();
}
