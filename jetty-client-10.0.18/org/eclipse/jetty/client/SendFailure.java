/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client;

public class SendFailure {
    public final Throwable failure;
    public final boolean retry;

    public SendFailure(Throwable failure, boolean retry) {
        this.failure = failure;
        this.retry = retry;
    }

    public String toString() {
        return String.format("%s@%x[failure=%s,retry=%b]", this.getClass().getSimpleName(), this.hashCode(), this.failure, this.retry);
    }
}

