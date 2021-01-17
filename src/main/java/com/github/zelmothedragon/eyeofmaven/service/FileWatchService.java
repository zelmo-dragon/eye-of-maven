package com.github.zelmothedragon.eyeofmaven.service;

import com.github.zelmothedragon.eyeofmaven.model.Project;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author MOSELLE Maxime
 */
public class FileWatchService {

    private static final long POLLING_INTERVAL = 500L;

    private final ScheduledExecutorService task;

    private final Project project;

    private volatile boolean running;

    public FileWatchService(final Project project) {
        this.task = Executors.newScheduledThreadPool(1);
        this.project = project;
        this.running = true;
    }

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

    public void stop() {
        this.running = false;
    }

    private boolean checkChange(long previousSize) {
        long currentSize = getTotalSize();
        return currentSize != previousSize;
    }

    private long getTotalSize() {
        try {
            var totalSize = Files
                    .walk(project.getSource())
                    .filter(Files::isRegularFile)
                    .mapToLong(this::getFileSize)
                    .sum();

            totalSize += getFileSize(project.getPom().toPath());

            return totalSize;
        } catch (IOException ex) {
            throw new IllegalStateException("Can not walk in source directory", ex);
        }
    }

    private long getFileSize(final Path file) {
        try {
            return Files.size(file);
        } catch (IOException ex) {
            throw new IllegalStateException("Can not read the file size of: " + file, ex);
        }
    }

}
