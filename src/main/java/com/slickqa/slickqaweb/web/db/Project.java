package com.slickqa.slickqaweb.web.db;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * A static class describing and providing utilities for Projects.
 */
public class Project {

    public static final String AddressNew = "announce.project.new";

    public static String getId(JsonObject project) {
        return project.getString("_id");
    }

    public static String getName(JsonObject project) {
        return project.getString("name");
    }

    public static JsonArray filterByRoleAccess(JsonArray projects, JsonArray roles) {
        JsonArray retval = new JsonArray();
        for(Object item : projects) {
            if(item instanceof JsonObject) {
                JsonObject project = (JsonObject) item;
                if(roles.contains("readable-" + Project.getId(project))) {
                    retval.add(project);
                }
            }
        }
        return retval;
    }
}
