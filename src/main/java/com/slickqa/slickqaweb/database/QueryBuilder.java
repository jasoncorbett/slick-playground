package com.slickqa.slickqaweb.database;

/**
 * Easily create queries for the database.
 */
public class QueryBuilder {

    protected QueryBuilder(DatabaseType type) {

    }

    public static QueryBuilder find(DatabaseType type) {
        return new QueryBuilder(type);
    }
}


