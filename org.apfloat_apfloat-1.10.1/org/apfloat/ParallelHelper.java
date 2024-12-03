/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.apfloat.Apcomplex;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;

class ParallelHelper {
    private ParallelHelper() {
    }

    public static <T extends Apcomplex> void parallelProduct(T[] x, Queue<T> heap, ProductKernel<T> kernel) {
        ApfloatContext ctx = ApfloatContext.getContext();
        int numberOfProcessors = ctx.getNumberOfProcessors();
        if (x.length >= 1000 && numberOfProcessors > 1) {
            int i;
            long maxSize = (long)((double)ctx.getCacheL1Size() * 2.5 / Math.log(ctx.getDefaultRadix()));
            ArrayList subHeaps = new ArrayList();
            for (i = 0; i < numberOfProcessors; ++i) {
                subHeaps.add(new PriorityQueue<T>(heap));
            }
            i = 0;
            for (T a : x) {
                (((Apcomplex)a).size() <= maxSize ? (Queue)subHeaps.get(i++) : heap).add(a);
                i = i == numberOfProcessors ? 0 : i;
            }
            AtomicInteger index = new AtomicInteger();
            Runnable runnable = () -> {
                Queue subHeap = (Queue)subHeaps.get(index.getAndIncrement());
                long size = 0L;
                while (subHeap.size() > 1 && size <= maxSize) {
                    kernel.run(subHeap);
                    size = ((Apcomplex)subHeap.peek()).size();
                }
                Queue queue = heap;
                synchronized (queue) {
                    heap.addAll(subHeap);
                }
            };
            ParallelHelper.runParallel(runnable, numberOfProcessors - 1);
        } else {
            heap.addAll(Arrays.asList(x));
        }
        while (heap.size() > 1) {
            kernel.run(heap);
        }
    }

    public static <T> T getFuture(Future<T> future) {
        try {
            return future.get();
        }
        catch (InterruptedException ie) {
            throw new ApfloatRuntimeException("Waiting for dispatched task to complete was interrupted", ie);
        }
        catch (ExecutionException ee) {
            throw new ApfloatRuntimeException("Task execution failed", ee);
        }
    }

    public static void runParallel(Runnable runnable) {
        ApfloatContext ctx = ApfloatContext.getContext();
        int numberOfThreads = ctx.getNumberOfProcessors() - 1;
        ParallelHelper.runParallel(runnable, numberOfThreads);
    }

    private static void runParallel(Runnable runnable, int numberOfThreads) {
        ApfloatContext ctx = ApfloatContext.getContext();
        ExecutorService executorService = ctx.getExecutorService();
        ArrayList futures = new ArrayList();
        for (int i = 0; i < numberOfThreads; ++i) {
            futures.add(executorService.submit(runnable));
        }
        runnable.run();
        futures.forEach(ParallelHelper::getFuture);
    }

    @FunctionalInterface
    public static interface ProductKernel<T extends Apcomplex> {
        public void run(Queue<T> var1);
    }
}

