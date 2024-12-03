/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.ThreadFactoryBuilder;

@SdkProtectedApi
public final class ExecutorUtils {
    private ExecutorUtils() {
    }

    public static ExecutorService newSingleDaemonThreadExecutor(int queueCapacity, String threadNameFormat) {
        return new ThreadPoolExecutor(0, 1, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueCapacity), new ThreadFactoryBuilder().daemonThreads(true).threadNamePrefix(threadNameFormat).build());
    }

    public static Executor unmanagedExecutor(Executor executor) {
        return new UnmanagedExecutor(executor);
    }

    private static class UnmanagedExecutor
    implements Executor {
        private final Executor executor;

        private UnmanagedExecutor(Executor executor) {
            this.executor = executor;
        }

        @Override
        public void execute(Runnable command) {
            this.executor.execute(command);
        }
    }
}

