/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

final class Magick {
    static final boolean DEBUG = Magick.useDebug();

    private static boolean useDebug() {
        try {
            return "TRUE".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.image.magick.debug"));
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private Magick() {
    }
}

