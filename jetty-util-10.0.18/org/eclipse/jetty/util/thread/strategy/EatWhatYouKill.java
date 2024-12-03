/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.thread.strategy;

import java.util.concurrent.Executor;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.eclipse.jetty.util.thread.strategy.AdaptiveExecutionStrategy;

@Deprecated(forRemoval=true)
public class EatWhatYouKill
extends AdaptiveExecutionStrategy {
    public EatWhatYouKill(ExecutionStrategy.Producer producer, Executor executor) {
        super(producer, executor);
    }
}

