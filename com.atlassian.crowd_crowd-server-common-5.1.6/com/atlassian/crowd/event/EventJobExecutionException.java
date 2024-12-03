/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event;

public class EventJobExecutionException
extends RuntimeException {
    public EventJobExecutionException() {
    }

    public EventJobExecutionException(String s) {
        super(s);
    }

    public EventJobExecutionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EventJobExecutionException(Throwable throwable) {
        super(throwable);
    }
}

