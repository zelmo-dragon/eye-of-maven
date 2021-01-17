package com.github.zelmothedragon.eyeofmaven.service;

import com.github.zelmothedragon.eyeofmaven.util.Configuration;
import com.github.zelmothedragon.eyeofmaven.model.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Service de configuration de l'application.
 *
 * @author MOSELLE Maxime
 */
public class ConfigurationService {

    /**
     * Constructeur par défaut.
     */
    public ConfigurationService() {
        // RAS
    }

    /**
     * Charger la configuration pour le contexte courant. Si la configuration
     * n'existe pas, elle est alors générée.
     *
     * @return Le contexte du projet en cours
     */
    public Context execute() {
        var configuration = new Properties();
        var configurationPath = Configuration.get(Configuration.FILE_NAME);
        if (Files.exists(Path.of(configurationPath))) {
            try (var in = new FileInputStream(configurationPath)) {
                configuration.load(in);
            } catch (IOException ex) {
                throw new IllegalStateException("External configuration file not found", ex);
            }
        } else {
            configuration.put(Configuration.MAVEN_HOME, Configuration.get(Configuration.MAVEN_HOME));
            configuration.put(Configuration.MAVEN_GOALS, Configuration.get(Configuration.MAVEN_GOALS));
            configuration.put(Configuration.MAVEN_PROPERTIES, Configuration.get(Configuration.MAVEN_PROPERTIES));
            configuration.put(Configuration.PROJECT_DIRECTORY, Configuration.get(Configuration.PROJECT_DIRECTORY));
            configuration.put(Configuration.SERVER_AUTODEPLOY_DIRECTORY, Configuration.get(Configuration.SERVER_AUTODEPLOY_DIRECTORY));

            try {
                var out = new FileOutputStream(configurationPath);
                configuration.store(out, "Eye of Maven");
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to write external configuration file", ex);
            }
        }

        return new Context(configuration);

    }

}
