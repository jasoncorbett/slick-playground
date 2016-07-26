package com.slickqa.slickqaweb;

import com.slickqa.slickqaweb.auth.SlickAuthVerticle;
import com.slickqa.slickqaweb.web.SlickWebVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

/**
 * This verticle deploys the other verticles.  Most important is that Auth has started before the other web
 * verticles run.
 */
public class SlickDeployerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        JsonObject config = vertx.getOrCreateContext().config();
        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(new SlickAuthVerticle(), options, deploymentResult -> {
            if(deploymentResult.succeeded()) {
                int numberOfWebVerticles = config.getInteger("numberOfWebVerticles", 6);
                for(int i = 0; i < numberOfWebVerticles; i++) {
                    vertx.deployVerticle(new SlickWebVerticle(), new DeploymentOptions().setConfig(config));
                }
            }
        });
    }
}
