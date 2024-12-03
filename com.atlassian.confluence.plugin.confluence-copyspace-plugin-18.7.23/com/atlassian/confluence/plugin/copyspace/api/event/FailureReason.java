/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.api.event;

import java.util.Objects;

public enum FailureReason {
    UNKNOWN("Unknown"),
    MISSING_ATTACHMENT("Missing attachment"),
    MISSING_SPACE_PERMISSIONS("Missing space permissions"),
    UNABLE_TO_CREATE_HOMEPAGE("Unable to create homepage"),
    UNABLE_TO_REWRITE_LINK("Unable to rewrite dynamic or macros link");

    private final String stringValue;

    private FailureReason(String stringValue) {
        this.stringValue = Objects.requireNonNull(stringValue);
    }

    public String toString() {
        return this.stringValue;
    }
}

