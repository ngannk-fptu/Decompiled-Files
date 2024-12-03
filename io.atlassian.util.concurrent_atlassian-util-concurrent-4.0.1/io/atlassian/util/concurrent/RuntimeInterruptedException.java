/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import java.util.Objects;

public class RuntimeInterruptedException
extends RuntimeException {
    private static final long serialVersionUID = -5025209597479375477L;

    public RuntimeInterruptedException(InterruptedException cause) {
        super(Objects.requireNonNull(cause, "cause"));
    }

    public RuntimeInterruptedException(String message, InterruptedException cause) {
        super(message, Objects.requireNonNull(cause, "cause"));
    }

    @Override
    public InterruptedException getCause() {
        return (InterruptedException)super.getCause();
    }
}

