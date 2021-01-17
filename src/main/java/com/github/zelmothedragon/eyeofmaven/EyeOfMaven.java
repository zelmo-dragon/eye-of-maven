package com.github.zelmothedragon.eyeofmaven;

import com.github.zelmothedragon.eyeofmaven.controller.AutoDeployController;

/**
 * Classe principale.
 *
 * @author MOSELLE Maxime
 */
public final class EyeOfMaven {

    /**
     * Constructeur interne.
     */
    private EyeOfMaven() {
        throw new UnsupportedOperationException("Instance not allowed");
    }

    /**
     * Point d'entrée du programme.
     *
     * @param args Arguments système
     */
    public static void main(final String[] args) {
        var controller = new AutoDeployController();
        controller.start();
    }
}
