/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.Immutable
 */
package com.github.rholder.retry;

import com.github.rholder.retry.Attempt;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class RetryException
extends Exception {
    private final int numberOfFailedAttempts;
    private final Attempt<?> lastFailedAttempt;

    public RetryException(int numberOfFailedAttempts, @Nonnull Attempt<?> lastFailedAttempt) {
        this("Retrying failed to complete successfully after " + numberOfFailedAttempts + " attempts.", numberOfFailedAttempts, lastFailedAttempt);
    }

    public RetryException(String message, int numberOfFailedAttempts, Attempt<?> lastFailedAttempt) {
        super(message, ((Attempt)Preconditions.checkNotNull(lastFailedAttempt, (Object)"Last attempt was null")).hasException() ? lastFailedAttempt.getExceptionCause() : null);
        this.numberOfFailedAttempts = numberOfFailedAttempts;
        this.lastFailedAttempt = lastFailedAttempt;
    }

    public int getNumberOfFailedAttempts() {
        return this.numberOfFailedAttempts;
    }

    public Attempt<?> getLastFailedAttempt() {
        return this.lastFailedAttempt;
    }
}

