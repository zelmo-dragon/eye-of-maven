package com.github.zelmothedragon.eyeofmaven.util;

import java.util.ResourceBundle;

/**
 *
 * @author MOSELLE Maxime
 */
public final class Configuration {

    public static final String FILE_NAME = "file.name";

    public static final String MAVEN_HOME = "maven.home";
    
    public static final String MAVEN_GOALS = "maven.goals";
    
    public static final String MAVEN_PROPERTIES = "maven.properties";

    public static final String PROJECT_DIRECTORY = "project.directory";

    public static final String SERVER_AUTODEPLOY_DIRECTORY = "server.autodeploy.directory";
    

    private static final ResourceBundle CONFIGURATION = ResourceBundle.getBundle("application");

    private Configuration() {
        throw new UnsupportedOperationException("Instance not allowed");
    }

    public static String get(final String key) {
        return CONFIGURATION.getString(key);
    }

}
