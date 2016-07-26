package com.slickqa.slickqaweb;

/**
 * An exception to indicate there was a configuration error.
 */
public class ConfigurationError extends Error {
    public ConfigurationError(String message) {
        super(message);
    }
}
