/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

public final class RuntimeAvailableProcessors {
    private static volatile int currentAvailableProcessors = Runtime.getRuntime().availableProcessors();
    private static volatile int defaultAvailableProcessors = Runtime.getRuntime().availableProcessors();

    private RuntimeAvailableProcessors() {
    }

    public static int get() {
        return currentAvailableProcessors;
    }

    public static void override(int availableProcessors) {
        currentAvailableProcessors = availableProcessors;
    }

    public static void overrideDefault(int availableProcessors) {
        defaultAvailableProcessors = availableProcessors;
    }

    public static void resetOverride() {
        currentAvailableProcessors = defaultAvailableProcessors;
    }
}

