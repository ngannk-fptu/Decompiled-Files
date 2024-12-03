/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConcurrencyUtil {
    public static ThreadPoolExecutor createThreadPoolExecutor(int parallelism) {
        return (ThreadPoolExecutor)Executors.newFixedThreadPool(parallelism);
    }

    public static void shutdownExecutionService(ExecutorService service) throws InterruptedException {
        ConcurrencyUtil.shutdownExecutionService(service, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public static void shutdownExecutionService(ExecutorService service, long time, TimeUnit timeUnit) throws InterruptedException {
        service.shutdown();
        service.awaitTermination(time, timeUnit);
    }
}

