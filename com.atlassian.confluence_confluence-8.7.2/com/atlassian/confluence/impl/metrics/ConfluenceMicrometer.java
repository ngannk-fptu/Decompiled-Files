/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.metrics;

public final class ConfluenceMicrometer {
    private static final boolean ENABLED = Boolean.parseBoolean(System.getProperty("confluence.micrometer.enabled", "true"));

    private ConfluenceMicrometer() {
    }

    public static boolean isMicrometerEnabled() {
        return ENABLED;
    }
}

