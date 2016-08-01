package com.slickqa.slickqaweb.auth;

import com.slickqa.slickqaweb.ConfigurationError;
import com.slickqa.slickqaweb.MongoFactory;
import com.slickqa.slickqaweb.web.Role;
import com.slickqa.slickqaweb.web.db.DBUtil;
import com.slickqa.slickqaweb.web.db.Project;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.mongo.MongoAuth;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * The auth verticle is one that should only be started once
 */
public class SlickAuthVerticle extends AbstractVerticle {
    private Logger log;
    private MongoClient mongo;
    private MongoAuth mongoAuth;

    @Override
    public void start(Future<Void> future) throws Exception {
        log = LoggerFactory.getLogger(SlickAuthVerticle.class);
        JsonObject deploymentConfig = vertx.getOrCreateContext().config();

        // check for missing configurations
        if(!deploymentConfig.containsKey("salt")) {
            throw new ConfigurationError("Missing property salt, please add a to deployment configuration (use a custom random string of characters).");
        }

        if(!deploymentConfig.containsKey("adminPassword")) {
            throw new ConfigurationError("Missing property adminPassword, please add the admin's password to the deployment configuration.");
        }

        mongo = MongoFactory.buildMongo(vertx);
        mongoAuth = MongoFactory.buildMongoAuth(vertx, mongo);

        JsonObject usersIndex = new JsonObject()
                .put("name", "users_username_unique")
                .put("key", new JsonObject()
                    .put("username", 1))
                .put("unique", true);

        DBUtil.createIndex(mongo, mongoAuth.getCollectionName(), usersIndex, result -> {
            if(result.succeeded()) {
                log.info("Created index on users for username");
            } else {
                log.debug("Creation of index on users failed, probably because it alread existed", result.cause());
            }
        });

        // find projects
        mongo.find("projects", new JsonObject(), projectsResult -> {
            if(projectsResult.succeeded()) {
                List<JsonObject> projects = projectsResult.result();

                JsonArray roles = new JsonArray();
                for(JsonObject project : projects) {
                    roles.add(Role.ProjectRead(Project.getId(project)));
                    roles.add(Role.ProjectWrite(Project.getId(project)));
                }

                // add or modify site admin
                JsonObject siteAdminQuery = new JsonObject()
                        .put(mongoAuth.getUsernameField(), deploymentConfig.getString("adminUsername", "admin"));
                JsonObject siteAdminModification = new JsonObject()
                        .put("$set", new JsonObject()
                                .put(mongoAuth.getUsernameField(), deploymentConfig.getString("adminUsername", "admin"))
                                .put(mongoAuth.getRoleField(), roles)
                                .put(mongoAuth.getPasswordField(), mongoAuth.getHashStrategy().computeHash(deploymentConfig.getString("adminPassword"), null))
                        );
                mongo.updateCollectionWithOptions(mongoAuth.getCollectionName(), siteAdminQuery, siteAdminModification, new UpdateOptions().setUpsert(true), adminUpsertResult -> {
                    if(adminUpsertResult.succeeded()) {

                        // make sure all other site admins have the permissions they should
                        ensureSiteAdminPermissions(future, projects);
                    } else {
                        log.error("Failed to upsert admin object " + siteAdminModification.encodePrettily(), adminUpsertResult.cause());
                        future.fail(adminUpsertResult.cause());
                    }
                });
            } else {
                log.error("Couldn't find a list of projects!", projectsResult.cause());
                future.fail(projectsResult.cause());
            }
        });

        vertx.eventBus().consumer(Project.AddressNew, this::onNewProjectAdded);
    }

    public void onNewProjectAdded(Message<JsonObject> projectMessage) {
        List<JsonObject> projects = new ArrayList<>();
        projects.add(projectMessage.body());
        ensureSiteAdminPermissions(null, projects);
    }

    public void ensureSiteAdminPermissions(Future<Void> whenDone, List<JsonObject> projects) {
        JsonArray roles = new JsonArray();
        for(JsonObject project : projects) {
            roles.add(Role.ProjectRead(Project.getId(project)));
            roles.add(Role.ProjectWrite(Project.getId(project)));
        }

        JsonObject update = new JsonObject()
                .put("$addToSet", new JsonObject()
                        .put(mongoAuth.getRoleField(), new JsonObject()
                                .put("$each", roles)
                        )
                );

        mongo.updateCollectionWithOptions(mongoAuth.getCollectionName(), new JsonObject().put(mongoAuth.getRoleField(), Role.SiteAdmin()), update, new UpdateOptions(false, true), siteAdminResult -> {
            if(siteAdminResult.succeeded()) {
                whenDone.complete();
            } else {
                log.error("Error occurred when trying to add the following roles to site admins " + roles.encodePrettily(), siteAdminResult.cause());
                if(whenDone != null) {
                    whenDone.fail(siteAdminResult.cause());
                }
            }
        });
    }
}
