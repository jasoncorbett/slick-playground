package com.slickqa.slickqaweb.database.types;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.slickqa.slickqaweb.AutoloadComponent;
import com.slickqa.slickqaweb.database.DatabaseType;
import com.slickqa.slickqaweb.startupComponentType.OnStartup;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Meta-data information about projects stored in slick's database.
 */
@AutoloadComponent
@Singleton
public class Project implements DatabaseType, OnStartup {
    public static final String name = "name";
    public static final String type = "project";

    private MongoClient mongo;
    private Logger log;

    @Inject
    public Project(MongoClient mongo) {
        this.mongo = mongo;
        log = LoggerFactory.getLogger(Project.class);
    }

    @Override
    public String getTypeName() {
        return type;
    }

    @Override
    public String getCollectionName() {
        return "projects";
    }

    @Override
    public List<String> validateInsert(JsonObject input) {
        List<String> errors = new ArrayList<>();
        if(input == null || input.getString(Project.name) == null || input.getString(Project.name).isEmpty()) {
            errors.add(ErrorUtil.missingRequiredProperty(this, Project.name));
        }
        return errors;
    }

    @Override
    public List<String> validateQuery(JsonObject input) {
        List<String> errors = new ArrayList<>();
        //TODO: validate the query for projects
        return errors;
    }

    @Override
    public List<String> validateUpdate(JsonObject input) {
        return null;
    }

    @Override
    public List<String> validateDelete(JsonObject input) {
        return null;
    }

    @Override
    public void onStartup() {
        // add indexes
        mongo.createCollection(getCollectionName(), result -> {
                if(result.succeeded()) {
                    log.info("Created collection {0}", getCollectionName());
                } else {
                    log.warn("Unable to create Collection {0} because {1}", getCollectionName(), result.cause());
                }
            mongo.runCommand("createIndexes", new JsonObject()
                    .put("createIndexes", getCollectionName())
                    .put("indexes", new JsonArray()
                            .add(new JsonObject()
                                .put("name", getCollectionName() + "_name")
                                .put("key", new JsonObject()
                                        .put(Project.name, 1)
                                )
                                .put("unique", true)
                            )
                    ), indexResult -> {
                if(indexResult.succeeded()) {
                    log.info("Created indexes for collection {0}.", getCollectionName());
                } else {
                    log.warn("Unable to create indexes for collection {0}: {1}.", getCollectionName(), indexResult.cause());
                }
            });
        });
    }

    public static JsonObject findAll() {
        return new JsonObject().put("type", Project.type).put("query", new JsonObject());
    }

    public static JsonObject findById(String id) {
        return new JsonObject().put("type", Project.type).put("query", new JsonObject().put("_id", id));
    }

    public static JsonObject findByName(String name) {
        return new JsonObject().put("type", Project.type).put("query", new JsonObject().put("name", name));
    }
}
