package com.slickqa.slickqaweb.routes;

import com.google.inject.Injector;

import java.util.List;

/**
 * Describes the ability to get a collection of self configuring routes for Vert.x
 */
public interface RoutableResourceCollection {
    List<Routable> getRoutableResources(Injector injector);
}
