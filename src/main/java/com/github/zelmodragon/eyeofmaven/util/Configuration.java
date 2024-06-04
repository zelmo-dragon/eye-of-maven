package com.github.zelmodragon.eyeofmaven.util;

import java.util.ResourceBundle;

/**
 * Configuration interne.
 *
 * @author MOSELLE Maxime
 */
public final class Configuration {

    /**
     * Clef. Version de l'application.
     */
    public static final String APP_VERSION = "app.version";

    /**
     * Clef. Emplacement et nom du fichier de configuration externe.
     */
    public static final String FILE_NAME = "file.name";

    /**
     * Clef. Emplacement racine du Maven.
     */
    public static final String MAVEN_HOME = "maven.home";

    /**
     * Clef. Commande Maven de compilation.
     */
    public static final String MAVEN_GOALS = "maven.goals";

    /**
     * Clef. Propriétés Java additionnelles pour la commande Maven.
     */
    public static final String MAVEN_PROPERTIES = "maven.properties";

    /**
     * Clef. Emplacement racine du projet a déployer.
     */
    public static final String PROJECT_DIRECTORY = "project.directory";

    /**
     * Clef. Emplacement du répertoire de déploiement automatique du serveur
     * d'application.
     */
    public static final String SERVER_AUTODEPLOY_DIRECTORY = "server.autodeploy.directory";

    /**
     * Accès à la configuration.
     */
    private static final ResourceBundle CONFIGURATION = ResourceBundle.getBundle("application");

    /**
     * Constructeur interne.
     */
    private Configuration() {
        throw new UnsupportedOperationException("Instance not allowed");
    }

    /**
     * Récupérer une configuration.
     *
     * @param key Nom de la configuration (voir les constantes de cette classe)
     * @return La valeur associée
     */
    public static String get(final String key) {
        return CONFIGURATION.getString(key);
    }

}
