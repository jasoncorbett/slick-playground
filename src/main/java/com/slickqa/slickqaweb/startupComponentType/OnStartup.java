package com.slickqa.slickqaweb.startupComponentType;

import com.slickqa.slickqaweb.CollectableComponentType;

/**
 * This interface is for a class that wants a method to be called on startup.
 * It should be annotated with the AutoloadComponent annotation for slick to
 * auto load it.
 */
@CollectableComponentType
public interface OnStartup {
    void onStartup();
}
