/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

public class UpgradeError {
    private String message;
    private Throwable error;

    public UpgradeError(String message) {
        this.message = message;
    }

    public UpgradeError(Throwable error) {
        this(error.getMessage(), error);
    }

    public UpgradeError(String message, Throwable error) {
        this.message = message;
        this.error = error;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return this.message;
    }

    public Throwable getError() {
        return this.error;
    }
}

