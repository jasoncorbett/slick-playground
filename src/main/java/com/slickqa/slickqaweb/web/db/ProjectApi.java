package com.slickqa.slickqaweb.web.db;

import com.google.inject.Inject;
import com.slickqa.slickqaweb.web.AutoloadComponent;
import com.slickqa.slickqaweb.web.startupComponentType.OnStartup;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

/**
 * Project API
 */
@AutoloadComponent
public class ProjectApi implements OnStartup {
    private MongoClient mongo;
    private JsonObject index;
    private Logger log;

    @Inject
    public ProjectApi(MongoClient mongo) {
        this.log = LoggerFactory.getLogger(ProjectApi.class);
        this.mongo = mongo;
        this.index = new JsonObject()
                .put("name", "projects_name_unique")
                .put("key", new JsonObject()
                    .put("name", 1))
                .put("unique", true);
    }
    @Override
    public void onStartup() {
        DBUtil.createIndex(mongo, "projects", index, result -> {
            if(result.succeeded()) {
                log.info("Successfully added index to projects for name");
            } else {
                log.debug("Attempted to add index to projects for name, it failed, probably because it alread exists",
                          result.cause());
            }
        });
    }
}
