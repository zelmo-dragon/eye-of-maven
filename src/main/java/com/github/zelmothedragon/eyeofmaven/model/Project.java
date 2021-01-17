package com.github.zelmothedragon.eyeofmaven.model;

import com.github.zelmothedragon.eyeofmaven.util.Configuration;
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
 *
 * @author MOSELLE Maxime
 */
public class Project {

    private final String path;

    private final String mavenHome;

    private final String mavenGoals;

    private final String mavenProperties;

    private final String serverAutodeployDirectory;

    public Project(final Properties configuration) {
        this.path = configuration.getProperty(Configuration.PROJECT_DIRECTORY);
        this.mavenHome = configuration.getProperty(Configuration.MAVEN_HOME);
        this.mavenGoals = configuration.getProperty(Configuration.MAVEN_GOALS);
        this.mavenProperties = configuration.getProperty(Configuration.MAVEN_PROPERTIES);
        this.serverAutodeployDirectory = configuration.getProperty(Configuration.SERVER_AUTODEPLOY_DIRECTORY);
    }

    public String getPath() {
        return path;
    }

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

    public File getPom() {
        return Path.of(path, "pom.xml").toFile();
    }

    public Path getSource() {
        return Path.of(path, "src");
    }

    public Path getTarget() {
        return Path.of(path, "target");
    }

    public File getMavenHome() {
        return new File(mavenHome);
    }

    public List<String> getMavenGoals() {
        return List.of(mavenGoals.split("\\s+"));
    }

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

    public Path getServerAutodeployDirectory() {
        return Path.of(serverAutodeployDirectory);
    }

}
