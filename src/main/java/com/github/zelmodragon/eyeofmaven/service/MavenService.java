package com.github.zelmodragon.eyeofmaven.service;

import com.github.zelmodragon.eyeofmaven.model.Context;

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
 * Service Maven.
 *
 * @author MOSELLE Maxime
 */
public class MavenService {

    /**
     * Contexte d'environnement du projet.
     */
    private final Context context;

    /**
     * Commande Maven programmable.
     */
    private final DefaultInvocationRequest request;

    /**
     * Invocation de la commande Maven.
     */
    private final DefaultInvoker invoker;

    /**
     * Journalisation.
     */
    private final InvokerLogger logger;

    /**
     * Constructeur.
     *
     * @param context Contexte d'environnement du projet
     */
    public MavenService(Context context) {
        this.context = context;

        this.request = new DefaultInvocationRequest();
        this.request.setBatchMode(true);
        this.request.setShowErrors(false);
        this.request.setThreads(System.getProperty("threads", "1"));
        this.request.setPomFile(context.getPom());
        this.request.setGoals(context.getMavenGoals());
        this.request.setProperties(context.getMavenProperties());

        this.invoker = new DefaultInvoker();
        this.invoker.setMavenHome(context.getMavenHome());
        this.invoker.setOutputHandler(null);
        this.invoker.setLogger(null);

        this.logger = this.invoker.getLogger();
    }

    /**
     * Compiler le projet.
     *
     * @return La valeur {@code true} si la compilation est un succès, sinon la
     * valeur {@code false} est retournée
     */
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

    /**
     * Annuler le déploiement du projet.
     */
    public void undeploy() {
        try {
            Files
                    .find(context.getServerAutodeployDirectory(), 1, this::matchModule)
                    .forEach(this::deleteModule);

        } catch (IOException ex) {
            this.logger.warn("Can not remove module in autodeploy server directory.", ex);
        }
    }

    /**
     * Déployer le projet.
     */
    public void deploy() {
        var module = context.getFinalName();
        var source = context.getTarget().resolve(module);
        var destination = context.getServerAutodeployDirectory().resolve(module);
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

    /**
     * Rechercher le module déployé
     *
     * @param path Chemin de base pour la recherche
     * @param attributes Attribut de fichier
     * @return La valeur {@code true} si le module est trouvé, sinon la valeur
     * {@code false} est retournée
     */
    private boolean matchModule(final Path path, final BasicFileAttributes attributes) {
        return path.endsWith(context.getFinalName());
    }

    /**
     * Supprimer un module si il existe.
     *
     * @param module Chemin du module à supprimer
     */
    private void deleteModule(final Path module) {
        try {
            Files.deleteIfExists(module);
        } catch (IOException ex) {
            this.logger.warn("Can not delete module.", ex);
        }
    }
}
