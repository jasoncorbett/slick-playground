package com.slickqa.slickqaweb.routes;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides an easy way to determine what the url of your route should be based
 * on the java package you are in.  For example something in a sub package
 * of this class of name "api" with a class name "Projects" should be mapped to
 * "/api/projects".  All the functionality for determining it is in static methods,
 *
 */
public abstract class AbstractPackageRoutableResource implements Routable {

    public static String generateUrlForClass(String baseUrlPath, Class<?> cls) {
        String basePackage = AbstractPackageRoutableResource.class.getPackage().getName();
        if(cls == null || !cls.getPackage().getName().startsWith(basePackage))
            return null;
        String packageName = cls.getPackage().getName().substring(basePackage.length() + 1);
        String packageUrl = packageName.replace('.', '/');
        packageUrl = packageUrl.replace('_', '-');
        String classUrl = cls.getSimpleName().replace('_', '-');
        return joinRoutePieces(baseUrlPath, packageUrl, classUrl);
    }

    public static String joinRoutePieces(String... routePieces) {
        StringBuilder retval = new StringBuilder();
        if(routePieces.length > 0) {
            if(!routePieces[0].startsWith("/")) {
                retval.append("/");
            }
            for(String piece : routePieces) {
                if(retval.length() > 0 && retval.charAt(retval.length() - 1) != '/') {
                    retval.append("/");
                }
                retval.append(piece);
            }
        }
        return retval.toString();
    }

    @Override
    public void configureRoutes(String baseUrlPath, Router router) {
        Method[] methods = this.getClass().getMethods();
        String classUrl = generateUrlForClass(baseUrlPath, this.getClass());
        Logger logger = LoggerFactory.getLogger(AbstractPackageRoutableResource.class.getName() + ":configureRoutes");

        for(Method method : methods) {
            if(method.isAnnotationPresent(Handler.class)) {
                Handler info = method.getAnnotation(Handler.class);
                String routeUrl = classUrl;
                if(!info.path().isEmpty()) {
                    routeUrl = joinRoutePieces(routeUrl, info.path());
                }
                logger.info("Adding route for " + routeUrl + " to call " + this.getClass().getName() + "::" + method.getName());
                router.route(info.method(), routeUrl).handler((ctx) -> {
                    try {
                        method.invoke(this, ctx);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        Logger methodLogger = LoggerFactory.getLogger(this.getClass());
                        methodLogger.error("Unable to invoke " + method.getName(), e);
                    }
                });
                if(info.usesBody()) {
                    router.route(info.method(), routeUrl).handler(BodyHandler.create());
                }
            }
        }
    }


}
