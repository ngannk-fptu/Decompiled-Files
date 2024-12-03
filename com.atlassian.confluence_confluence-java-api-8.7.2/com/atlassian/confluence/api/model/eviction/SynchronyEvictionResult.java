/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.eviction;

public class SynchronyEvictionResult {
    private final boolean successful;

    public static SynchronyEvictionResult ok() {
        return new SynchronyEvictionResult(true);
    }

    public static SynchronyEvictionResult failed() {
        return new SynchronyEvictionResult(false);
    }

    private SynchronyEvictionResult(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return this.successful;
    }
}

