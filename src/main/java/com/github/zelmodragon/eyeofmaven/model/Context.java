package com.github.zelmodragon.eyeofmaven.model;

import com.github.zelmodragon.eyeofmaven.util.Configuration;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Propriété de l'environnement du projet en cours.
 *
 * @author MOSELLE Maxime
 */
public class Context {

    /**
     * Chemin du projet.
     */
    private final String projectPath;

    /**
     * Emplacement racine de Maven.
     */
    private final String mavenHome;

    /**
     * Commande de compilation Maven.
     */
    private final String mavenGoals;

    /**
     * Propriétés additionnelles Java pour la commande Maven.
     */
    private final String mavenProperties;

    /**
     * Emplacement du répertoire de déploiement du serveur d'application.
     */
    private final String serverAutodeployDirectory;

    /**
     * Constructeur.
     *
     * @param configuration Configuration application
     */
    public Context(final Properties configuration) {
        this.projectPath = configuration.getProperty(Configuration.PROJECT_DIRECTORY);
        this.mavenHome = configuration.getProperty(Configuration.MAVEN_HOME);
        this.mavenGoals = configuration.getProperty(Configuration.MAVEN_GOALS);
        this.mavenProperties = configuration.getProperty(Configuration.MAVEN_PROPERTIES);
        this.serverAutodeployDirectory = configuration.getProperty(Configuration.SERVER_AUTODEPLOY_DIRECTORY);
    }

    /**
     *
     * @return Le chemin du projet
     */
    public String getProjectPath() {
        return projectPath;
    }

    /**
     *
     * @return Le nom final du projet après compilation
     */
    public String getFinalName() {
        try {
            var reader = new MavenXpp3Reader();
            var model = reader.read(new FileReader(getPom()));
            var name = model.getBuild().getFinalName();

            if (Objects.isNull(name)) {
                name = String
                        .format(
                                "%s-%s",
                                model.getArtifactId(),
                                model.getVersion()
                        );
            }
            name = String.join(".", name, model.getPackaging());
            return name;
        } catch (IOException | XmlPullParserException ex) {
            throw new IllegalStateException("Can not read pom.xml", ex);
        }
    }

    /**
     * Accesseur.
     *
     * @return Le fichier <i>pom.xml</i> du projet
     */
    public File getPom() {
        return Path.of(projectPath, "pom.xml").toFile();
    }

    /**
     * Accesseur.
     *
     * @return Le chemin du répertoire du code source Maven
     */
    public Path getSource() {
        return Path.of(projectPath, "src");
    }

    /**
     * Accesseur.
     *
     * @return Le chemin du répertoire de compilation Maven
     */
    public Path getTarget() {
        return Path.of(projectPath, "target");
    }

    /**
     * Accesseur.
     *
     * @return Le répertoire racine de Maven
     */
    public File getMavenHome() {
        return new File(mavenHome);
    }

    /**
     * Accesseur.
     *
     * @return Une liste des commandes Maven à exécuter pour la compilation
     */
    public List<String> getMavenGoals() {
        return List.of(mavenGoals.split("\\s+"));
    }

    /**
     * Accesseur.
     *
     * @return Les propriétés additionnelles Java pour la commande Maven
     */
    public Properties getMavenProperties() {
        var config = new Properties();
        for (String property : mavenProperties.split("\\s+")) {
            String[] tuple = property.split("=");
            if (tuple.length == 2) {
                config.put(tuple[0], tuple[1]);
            }
        }
        return config;
    }

    /**
     * Accesseur.
     *
     * @return Le chemin du répertoire de déploiement automatique du serveur
     * d'application
     */
    public Path getServerAutodeployDirectory() {
        return Path.of(serverAutodeployDirectory);
    }

}
