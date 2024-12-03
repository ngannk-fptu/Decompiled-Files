/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.synchrony.api.events;

public abstract class AbstractSynchronyWasDownEvent {
    private final long approximateDurationInSeconds;

    AbstractSynchronyWasDownEvent(long approximateDurationInSeconds) {
        this.approximateDurationInSeconds = approximateDurationInSeconds;
    }

    public long getApproximateDurationInSeconds() {
        return this.approximateDurationInSeconds;
    }
}

