/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.ParallelRunnable;

public class ParallelRunner {
    private static Queue<ParallelRunnable> tasks = new ConcurrentLinkedQueue<ParallelRunnable>();

    private ParallelRunner() {
    }

    public static void runParallel(ParallelRunnable parallelRunnable) throws ApfloatRuntimeException {
        tasks.add(parallelRunnable);
        try {
            ParallelRunner.runTasks(parallelRunnable);
        }
        finally {
            tasks.remove(parallelRunnable);
        }
    }

    public static void wait(Future<?> future) {
        Runnable stealer = () -> {
            while (!future.isDone()) {
                ParallelRunnable parallelRunnable = tasks.peek();
                if (parallelRunnable != null) {
                    parallelRunnable.runBatch();
                    continue;
                }
                Thread.yield();
            }
        };
        ParallelRunner.runTasks(stealer);
    }

    private static void runTasks(Runnable runnable) {
        ApfloatContext ctx = ApfloatContext.getContext();
        int numberOfProcessors = ctx.getNumberOfProcessors();
        if (numberOfProcessors > 1) {
            ExecutorService executorService = ctx.getExecutorService();
            for (int i = 0; i < numberOfProcessors - 1; ++i) {
                executorService.execute(runnable);
            }
        }
        runnable.run();
    }
}

