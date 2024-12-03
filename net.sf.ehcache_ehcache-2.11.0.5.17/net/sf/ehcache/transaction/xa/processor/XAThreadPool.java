/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa.processor;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XAThreadPool {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public synchronized MultiRunner getMultiRunner() {
        MultiRunner multiRunner = new MultiRunner();
        this.executor.submit(multiRunner);
        return multiRunner;
    }

    public synchronized void shutdown() {
        this.executor.shutdown();
    }

    public static final class MultiRunner
    implements Runnable {
        private final CyclicBarrier startBarrier = new CyclicBarrier(2);
        private final CyclicBarrier endBarrier = new CyclicBarrier(2);
        private volatile Callable callable;
        private volatile boolean released;
        private volatile Object result;
        private volatile Exception exception;

        private MultiRunner() {
        }

        public Object execute(Callable callable) throws ExecutionException, InterruptedException {
            if (this.released) {
                throw new IllegalStateException("MultiRunner has been released");
            }
            if (callable == null) {
                throw new NullPointerException("callable cannot be null");
            }
            try {
                this.callable = callable;
                this.exception = null;
                this.startBarrier.await();
                this.endBarrier.await();
                if (this.exception != null) {
                    throw new ExecutionException("XA execution error", this.exception);
                }
                return this.result;
            }
            catch (BrokenBarrierException e) {
                throw new ExecutionException("error executing " + callable, e);
            }
        }

        public void release() {
            try {
                this.callable = null;
                this.released = true;
                this.startBarrier.await();
            }
            catch (InterruptedException interruptedException) {
            }
            catch (BrokenBarrierException brokenBarrierException) {
                // empty catch block
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    this.startBarrier.await();
                    if (this.callable == null) break;
                    try {
                        this.result = this.callable.call();
                    }
                    catch (Exception e) {
                        this.exception = e;
                    }
                    this.endBarrier.await();
                }
                return;
            }
            catch (InterruptedException e) {
                this.released = true;
            }
            catch (BrokenBarrierException e) {
                this.released = true;
            }
        }
    }
}

