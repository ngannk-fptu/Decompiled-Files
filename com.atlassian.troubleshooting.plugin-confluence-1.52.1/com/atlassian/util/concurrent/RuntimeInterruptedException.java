/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Assertions;

public class RuntimeInterruptedException
extends RuntimeException {
    private static final long serialVersionUID = -5025209597479375477L;

    public RuntimeInterruptedException(InterruptedException cause) {
        super(Assertions.notNull("cause", cause));
    }

    public RuntimeInterruptedException(String message, InterruptedException cause) {
        super(message, Assertions.notNull("cause", cause));
    }

    public InterruptedException getCause() {
        return (InterruptedException)super.getCause();
    }
}

