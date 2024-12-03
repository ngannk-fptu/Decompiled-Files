/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.util.concurrent.Future;
import org.apfloat.internal.ParallelRunner;
import org.apfloat.spi.ExecutionStrategy;

public class ParallelExecutionStrategy
implements ExecutionStrategy {
    @Override
    public void wait(Future<?> future) {
        ParallelRunner.wait(future);
    }
}

