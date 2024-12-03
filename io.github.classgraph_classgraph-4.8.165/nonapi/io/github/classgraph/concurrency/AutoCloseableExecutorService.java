/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.concurrency;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import nonapi.io.github.classgraph.concurrency.InterruptionChecker;
import nonapi.io.github.classgraph.concurrency.SimpleThreadFactory;

public class AutoCloseableExecutorService
extends ThreadPoolExecutor
implements AutoCloseable {
    public final InterruptionChecker interruptionChecker = new InterruptionChecker();

    public AutoCloseableExecutorService(int numThreads) {
        super(numThreads, numThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new SimpleThreadFactory("ClassGraph-worker-", true));
    }

    @Override
    public void afterExecute(Runnable runnable, Throwable throwable) {
        super.afterExecute(runnable, throwable);
        if (throwable != null) {
            this.interruptionChecker.setExecutionException(new ExecutionException("Uncaught exception", throwable));
            this.interruptionChecker.interrupt();
        } else if (runnable instanceof Future) {
            try {
                ((Future)((Object)runnable)).get();
            }
            catch (InterruptedException | CancellationException e) {
                this.interruptionChecker.interrupt();
            }
            catch (ExecutionException e) {
                this.interruptionChecker.setExecutionException(e);
                this.interruptionChecker.interrupt();
            }
        }
    }

    @Override
    public void close() {
        try {
            this.shutdown();
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        boolean terminated = false;
        try {
            terminated = this.awaitTermination(2500L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            this.interruptionChecker.interrupt();
        }
        if (!terminated) {
            try {
                this.shutdownNow();
            }
            catch (SecurityException e) {
                throw new RuntimeException("Could not shut down ExecutorService -- need java.lang.RuntimePermission(\"modifyThread\"), or the security manager's checkAccess method denies access", e);
            }
        }
    }
}

