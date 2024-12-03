/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.internal;

final class Sys {
    Sys() {
    }

    public static boolean inDevMode() {
        return Boolean.getBoolean("atlassian.dev.mode");
    }
}

