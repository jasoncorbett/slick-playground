package com.slickqa.slickqaweb.database.types;

import com.slickqa.slickqaweb.database.DatabaseType;

import java.text.MessageFormat;

/**
 * A class which helps with errors.
 */
public class ErrorUtil {
    public static String missingRequiredProperty(DatabaseType type, String propertyName) {
        return MessageFormat.format("The type [{0}] was missing a required property [{1}].", type.getTypeName(), propertyName);
    }
}
