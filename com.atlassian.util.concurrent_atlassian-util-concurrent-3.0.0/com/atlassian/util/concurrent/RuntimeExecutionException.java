/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Assertions;
import java.util.concurrent.ExecutionException;

public class RuntimeExecutionException
extends RuntimeException {
    private static final long serialVersionUID = 1573022712345306212L;

    public RuntimeExecutionException(ExecutionException cause) {
        super(Assertions.notNull("cause", cause));
    }

    public RuntimeExecutionException(String message, ExecutionException cause) {
        super(message, Assertions.notNull("cause", cause));
    }
}

