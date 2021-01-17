package com.github.zelmothedragon.eyeofmaven;

import com.github.zelmothedragon.eyeofmaven.controller.AutoDeployController;

/**
 *
 * @author MOSELLE Maxime
 */
public final class EyeOfMaven {

    private EyeOfMaven() {
        throw new UnsupportedOperationException("Instance not allowed");
    }

    public static void main(final String[] args) {
        var controller = new AutoDeployController();
        controller.start();
    }
}
