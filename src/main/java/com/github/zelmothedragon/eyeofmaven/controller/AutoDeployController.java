package com.github.zelmothedragon.eyeofmaven.controller;

import com.github.zelmothedragon.eyeofmaven.service.ConfigurationService;
import com.github.zelmothedragon.eyeofmaven.service.FileWatchService;
import com.github.zelmothedragon.eyeofmaven.service.MavenService;
import com.github.zelmothedragon.eyeofmaven.util.Chrono;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.apache.maven.shared.invoker.SystemOutLogger;

/**
 *
 * @author MOSELLE Maxime
 */
public class AutoDeployController {

    private final InvokerLogger logger;

    private final Chrono timer;

    public AutoDeployController() {
        this.logger = new SystemOutLogger();
        this.timer = new Chrono();
    }

    public void start() {
        var configurationService = new ConfigurationService();
        var project = configurationService.execute();
        var mavenService = new MavenService(project);
        var fileWatchService = new FileWatchService(project);

        this.logger.info(String.format("Watching project '%s' ...%n", project.getPath()));
        fileWatchService.execute(() -> listener(mavenService));
    }

    private void listener(final MavenService service) {
        this.logger.info("Change dectected !");
        timer.start();

        this.logger.info("Building module...");
        if (service.build()) {

            this.logger.info("Undeploying previous module...");
            service.undeploy();

            this.logger.info("Deploying module...");
            service.deploy();

        } else {
            this.logger.warn("Nothing to deploy.");
        }
        

        var time = timer.stop();
        this.logger.info(String.format("Finished in %s ms. %n%n", time));
    }

}
