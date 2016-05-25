package com.slickqa.slickqaweb.database;

import com.slickqa.slickqaweb.CollectableComponentType;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * An interface that when implemented provides the necessary
 * meta-data about types stored in the database that they can
 * effectively be used.
 */
@CollectableComponentType
public interface DatabaseType {
    String getTypeName();
    String getCollectionName();
    String getUrlName();
    List<String> validateInsert(JsonObject input);
    List<String> validateQuery(JsonObject input);
    List<String> validateUpdate(JsonObject input);
    List<String> validateDelete(JsonObject input);
    void announceInsert(JsonObject input);
    void announceUpdate(JsonObject old, JsonObject update);
    void announceDelete(JsonObject deleted);
}
