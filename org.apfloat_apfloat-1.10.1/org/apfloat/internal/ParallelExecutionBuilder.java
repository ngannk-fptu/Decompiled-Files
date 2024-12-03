/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.ParallelExecutionStrategy;
import org.apfloat.spi.ExecutionBuilder;
import org.apfloat.spi.ExecutionStrategy;

public class ParallelExecutionBuilder
implements ExecutionBuilder {
    private static ExecutionStrategy executionStrategy = new ParallelExecutionStrategy();

    @Override
    public ExecutionStrategy createExecution() {
        return executionStrategy;
    }
}

