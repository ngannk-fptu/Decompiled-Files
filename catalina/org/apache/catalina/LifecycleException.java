/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

public final class LifecycleException
extends Exception {
    private static final long serialVersionUID = 1L;

    public LifecycleException() {
    }

    public LifecycleException(String message) {
        super(message);
    }

    public LifecycleException(Throwable throwable) {
        super(throwable);
    }

    public LifecycleException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

