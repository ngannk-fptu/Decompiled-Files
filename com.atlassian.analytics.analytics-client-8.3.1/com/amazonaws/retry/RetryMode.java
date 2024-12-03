/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry;

public enum RetryMode {
    LEGACY("legacy"),
    STANDARD("standard"),
    ADAPTIVE("adaptive");

    private final String name;

    private RetryMode(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static RetryMode fromName(String value) {
        if (value == null) {
            return null;
        }
        for (RetryMode retryMode : RetryMode.values()) {
            if (!retryMode.getName().equalsIgnoreCase(value)) continue;
            return retryMode;
        }
        return null;
    }
}

