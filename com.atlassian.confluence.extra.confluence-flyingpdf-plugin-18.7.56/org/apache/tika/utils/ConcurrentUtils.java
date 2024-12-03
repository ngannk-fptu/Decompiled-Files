/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.apache.tika.parser.ParseContext;

public class ConcurrentUtils {
    public static Future execute(ParseContext context, Runnable runnable) {
        Future<?> future = null;
        ExecutorService executorService = context.get(ExecutorService.class);
        if (executorService == null) {
            FutureTask<Object> task = new FutureTask<Object>(runnable, null);
            Thread thread = new Thread(task, "Tika Thread");
            thread.start();
            future = task;
        } else {
            future = executorService.submit(runnable);
        }
        return future;
    }
}

