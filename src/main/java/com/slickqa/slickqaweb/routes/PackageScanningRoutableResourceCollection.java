package com.slickqa.slickqaweb.routes;

import com.google.inject.Injector;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Search a java package for all classes that implement Routable.
 */
public class PackageScanningRoutableResourceCollection implements RoutableResourceCollection {

    private String basePackage;

    public PackageScanningRoutableResourceCollection(String basePackage) {
        this.basePackage = basePackage;
    }

    public PackageScanningRoutableResourceCollection() {
        this(PackageScanningRoutableResourceCollection.class.getPackage().getName());
    }

    @Override
    public List<Routable> getRoutableResources(Injector injector) {

        Reflections reflections = new Reflections(basePackage);
        Set<Class<? extends Routable>> classes = reflections.getSubTypesOf(Routable.class);

        List<Routable> retval = new ArrayList<>(classes.size());
        for(Class<? extends Routable> cls : classes) {
            if(!cls.isInterface() && !Modifier.isAbstract(cls.getModifiers()))
                retval.add(injector.getInstance(cls));
        }

        return retval;
    }
}
