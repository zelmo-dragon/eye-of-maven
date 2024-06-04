package com.github.zelmodragon.eyeofmaven.controller;

import com.github.zelmodragon.eyeofmaven.service.ConfigurationService;
import com.github.zelmodragon.eyeofmaven.service.FileWatchService;
import com.github.zelmodragon.eyeofmaven.service.MavenService;
import com.github.zelmodragon.eyeofmaven.util.Chrono;
import com.github.zelmodragon.eyeofmaven.util.Configuration;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.apache.maven.shared.invoker.SystemOutLogger;

/**
 * Contrôleur principal pour le déploiement automatique.
 *
 * @author MOSELLE Maxime
 */
public class AutoDeployController {

    /**
     * Journalisation.
     */
    private final InvokerLogger logger;

    /**
     * Chronomètre.
     */
    private final Chrono timer;

    /**
     * Constructeur par défaut.
     */
    public AutoDeployController() {
        this.logger = new SystemOutLogger();
        this.timer = new Chrono();
    }

    /**
     * Démarrer le contrôleur.
     */
    public void start() {
        this.logger.info(String.format("Running eye-of-maven-%s%n", Configuration.get(Configuration.APP_VERSION)));
        
        var configurationService = new ConfigurationService();
        var context = configurationService.execute();
        var mavenService = new MavenService(context);
        var fileWatchService = new FileWatchService(context);

        this.logger.info(String.format("Watching project '%s' ...%n", context.getProjectPath()));
        fileWatchService.execute(() -> listener(mavenService));
    }

    /**
     * Exécuter le déploiement.
     *
     * @param service Service de déploiement Maven
     */
    private void listener(final MavenService service) {
        this.logger.info("Change detected !");
        timer.start();

        this.logger.info("Building module...");
        if (service.build()) {

            this.logger.info("Undeploy previous module...");
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
