package com.slickqa.slickqaweb.web.startupComponentType;

import com.slickqa.slickqaweb.web.CollectableComponentType;

/**
 * This interface is for a class that wants a method to be called on startup.
 * It should be annotated with the AutoloadComponent annotation for slick to
 * auto load it.
 */
@CollectableComponentType
public interface OnStartup {
    void onStartup();
}
