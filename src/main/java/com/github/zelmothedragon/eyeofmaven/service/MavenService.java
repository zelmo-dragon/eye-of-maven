package com.github.zelmothedragon.eyeofmaven.service;

import com.github.zelmothedragon.eyeofmaven.model.Project;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.apache.maven.shared.invoker.MavenInvocationException;

/**
 *
 * @author MOSELLE Maxime
 */
public class MavenService {

    private final Project project;

    private final DefaultInvocationRequest request;

    private final DefaultInvoker invoker;

    private final InvokerLogger logger;

    public MavenService(Project project) {
        this.project = project;

        this.request = new DefaultInvocationRequest();
        this.request.setBatchMode(true);
        this.request.setShowErrors(false);
        this.request.setThreads(System.getProperty("threads", "1"));
        this.request.setPomFile(project.getPom());
        this.request.setGoals(project.getMavenGoals());
        this.request.setProperties(project.getMavenProperties());

        this.invoker = new DefaultInvoker();
        this.invoker.setMavenHome(project.getMavenHome());
        this.invoker.setOutputHandler(null);
        this.invoker.setLogger(null);

        this.logger = this.invoker.getLogger();
    }

    public boolean build() {
        boolean buildSuccess;
        try {
            this.invoker.execute(request);
            buildSuccess = true;
        } catch (MavenInvocationException ex) {
            this.logger.warn("Build failed.", ex);
            buildSuccess = false;
        }
        return buildSuccess;
    }

    public void undeploy() {
        try {
            Files
                    .find(project.getServerAutodeployDirectory(), 1, this::matchModule)
                    .forEach(this::deleteModule);

        } catch (IOException ex) {
            this.logger.warn("Can not remove module in autodeploy server directory.", ex);
        }
    }

    public void deploy() {
        var module = project.getFinalName();
        var source = project.getTarget().resolve(module);
        var destination = project.getServerAutodeployDirectory().resolve(module);
        try {
            if (Files.exists(source)) {
                Files
                        .copy(source, new FileOutputStream(destination.toFile()));

            } else {
                this.logger.warn("Module not found.");
            }
        } catch (IOException ex) {
            this.logger.warn("Can not deploy module in autodeploy server directory.", ex);
        }
    }

    private boolean matchModule(final Path path, final BasicFileAttributes attributes) {
        return path.endsWith(project.getFinalName());
    }

    private void deleteModule(final Path module) {
        try {
            Files.deleteIfExists(module);
        } catch (IOException ex) {
            this.logger.warn("Can not delete module.", ex);
        }
    }
}
