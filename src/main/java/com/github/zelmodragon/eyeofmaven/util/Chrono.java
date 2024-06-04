package com.github.zelmodragon.eyeofmaven.util;

/**
 * Chronomètre.
 *
 * @author MOSELLE Maxime
 */
public final class Chrono {

    /**
     * Temps en milliseconde depuis de lancement du chronomètre.
     */
    private long startTime;

    /**
     * Constructeur par défaut.
     */
    public Chrono() {
        // RAS
    }

    /**
     * Démarrer le chronomètre.
     */
    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Arrêter le chronomètre.
     *
     * @return Le temps en milliseconde écoulé depuis l'appel de la méthode
     * {@link start}
     */
    public long stop() {
        return System.currentTimeMillis() - startTime;
    }

}
