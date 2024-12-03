/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.synchrony.api;

import java.util.Objects;

public enum CollaborativeEditingMode {
    ENABLED("on"),
    DISABLED("off");

    private final String modeString;

    private CollaborativeEditingMode(String modeString) {
        this.modeString = Objects.requireNonNull(modeString);
    }

    public String toString() {
        return this.modeString;
    }

    public static CollaborativeEditingMode fromStatus(boolean sharedDraftsEnabled) {
        return sharedDraftsEnabled ? ENABLED : DISABLED;
    }
}

