package com.github.zelmodragon.eyeofmaven.service;

import com.github.zelmodragon.eyeofmaven.model.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service de surveillance de modification de fichiers.
 *
 * @author MOSELLE Maxime
 */
public class FileWatchService {

    /**
     * Interval de temps entre chaque scan.
     */
    private static final long POLLING_INTERVAL = 500L;

    /**
     * Processus d'exécution programmé.
     */
    private final ScheduledExecutorService task;

    /**
     * Contexte d'environnement du projet.
     */
    private final Context context;

    /**
     * Indique si la surveillance est en cours de traitement.
     */
    private volatile boolean running;

    /**
     * Constructeur.
     *
     * @param context Contexte d'environnement du projet
     */
    public FileWatchService(final Context context) {
        this.task = Executors.newScheduledThreadPool(1);
        this.context = context;
        this.running = true;
    }

    /**
     * Démarrer la surveillance du projet.
     *
     * @param listener Traitement à exécuter lorsqu'une modification est détecté
     */
    public void execute(final Runnable listener) {

        var initialSize = getTotalSize();
        boolean changeDetected;
        try {
            while (this.running) {
                final var previousSize = initialSize;
                changeDetected = task
                        .schedule(() -> checkChange(previousSize), POLLING_INTERVAL, TimeUnit.MILLISECONDS)
                        .get();

                if (changeDetected) {
                    initialSize = getTotalSize();
                    listener.run();
                }
            }
            this.task.awaitTermination(POLLING_INTERVAL, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException ex) {
            throw new IllegalStateException("Watching service failed", ex);
        }
    }

    /**
     * Arrêter la surveillance.
     */
    public void stop() {
        this.running = false;
    }

    /**
     * Détecter une modification.
     *
     * @param previousSize Taille du projet précédente
     * @return
     */
    private boolean checkChange(long previousSize) {
        long currentSize = getTotalSize();
        return currentSize != previousSize;
    }

    /**
     * Obtenir la taille total en octet du projet.
     *
     * @return La taille en octet
     */
    private long getTotalSize() {
        try {
            var totalSize = Files
                    .walk(context.getSource())
                    .filter(Files::isRegularFile)
                    .mapToLong(this::getFileSize)
                    .sum();

            totalSize += getFileSize(context.getPom().toPath());

            return totalSize;
        } catch (IOException ex) {
            throw new IllegalStateException("Can not walk in source directory", ex);
        }
    }

    /**
     * Obtenir la taille d'un fichier
     *
     * @param file Chemin vers un fichier
     * @return La taille en octet
     */
    private long getFileSize(final Path file) {
        try {
            return Files.size(file);
        } catch (IOException ex) {
            throw new IllegalStateException("Can not read the file size of: " + file, ex);
        }
    }

}
