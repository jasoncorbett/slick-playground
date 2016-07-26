package com.slickqa.slickqaweb.web;

import io.vertx.ext.auth.mongo.MongoAuth;

/**
 * Utility for easily using the roles
 */
public class Role {

    public static String SiteAdmin() {
        return MongoAuth.ROLE_PREFIX + "site-admin";
    }

    public static String ProjectRead(String projectId) {
        return MongoAuth.ROLE_PREFIX + "read-" + projectId;
    }

    public static String ProjectWrite(String projectId) {
        return MongoAuth.ROLE_PREFIX + "write-" + projectId;
    }
}
