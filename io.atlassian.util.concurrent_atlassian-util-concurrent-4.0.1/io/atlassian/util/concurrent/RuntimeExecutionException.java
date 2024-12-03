/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class RuntimeExecutionException
extends RuntimeException {
    private static final long serialVersionUID = 1573022712345306212L;

    public RuntimeExecutionException(ExecutionException cause) {
        super(Objects.requireNonNull(cause, "cause"));
    }

    public RuntimeExecutionException(String message, ExecutionException cause) {
        super(message, Objects.requireNonNull(cause, "cause"));
    }
}

