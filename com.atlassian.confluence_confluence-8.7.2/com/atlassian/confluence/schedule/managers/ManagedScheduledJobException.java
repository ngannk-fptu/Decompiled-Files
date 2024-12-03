/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.schedule.managers;

public class ManagedScheduledJobException
extends RuntimeException {
    public ManagedScheduledJobException() {
    }

    public ManagedScheduledJobException(String message) {
        super(message);
    }

    public ManagedScheduledJobException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagedScheduledJobException(Throwable cause) {
        super(cause);
    }
}

