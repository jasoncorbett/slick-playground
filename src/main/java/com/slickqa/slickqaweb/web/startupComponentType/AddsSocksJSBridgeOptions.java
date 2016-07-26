package com.slickqa.slickqaweb.web.startupComponentType;

import com.slickqa.slickqaweb.web.CollectableComponentType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;

/**
 * A class that implements this and is annotated with
 * Startup component will be able to add options to the SocksJS Bridge.
 */
@CollectableComponentType
public interface AddsSocksJSBridgeOptions {
    void addToSocksJSBridgeOptions(BridgeOptions options);
}
