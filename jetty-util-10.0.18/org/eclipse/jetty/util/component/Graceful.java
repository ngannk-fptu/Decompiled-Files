/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.component;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.component.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Graceful {
    public CompletableFuture<Void> shutdown();

    public boolean isShutdown();

    public static CompletableFuture<Void> shutdown(Container component) {
        Logger log = LoggerFactory.getLogger(component.getClass());
        log.info("Shutdown {}", (Object)component);
        ArrayList<Graceful> gracefuls = new ArrayList<Graceful>();
        if (component instanceof Graceful) {
            gracefuls.add((Graceful)((Object)component));
        }
        gracefuls.addAll(component.getContainedBeans(Graceful.class));
        if (log.isDebugEnabled()) {
            gracefuls.forEach(g -> log.debug("graceful {}", g));
        }
        return CompletableFuture.allOf((CompletableFuture[])gracefuls.stream().map(Graceful::shutdown).toArray(CompletableFuture[]::new));
    }

    public static CompletableFuture<Void> shutdown(ThrowingRunnable runnable) {
        final AtomicReference<Thread> stopThreadReference = new AtomicReference<Thread>();
        CompletableFuture<Void> shutdown = new CompletableFuture<Void>(){

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                Thread thread;
                boolean canceled = super.cancel(mayInterruptIfRunning);
                if (canceled && mayInterruptIfRunning && (thread = (Thread)stopThreadReference.get()) != null) {
                    thread.interrupt();
                }
                return canceled;
            }
        };
        Thread stopThread = new Thread(() -> {
            try {
                runnable.run();
                shutdown.complete(null);
            }
            catch (Throwable t) {
                shutdown.completeExceptionally(t);
            }
        });
        stopThread.setDaemon(true);
        stopThreadReference.set(stopThread);
        stopThread.start();
        return shutdown;
    }

    @FunctionalInterface
    public static interface ThrowingRunnable {
        public void run() throws Exception;
    }

    public static abstract class Shutdown
    implements Graceful {
        final Object _component;
        final AtomicReference<CompletableFuture<Void>> _done = new AtomicReference();

        protected Shutdown(Object component) {
            this._component = component;
        }

        @Override
        public CompletableFuture<Void> shutdown() {
            if (this._done.get() == null) {
                this._done.compareAndSet(null, new CompletableFuture<Void>(){

                    @Override
                    public String toString() {
                        return String.format("Shutdown<%s>@%x", _component, this.hashCode());
                    }
                });
            }
            CompletableFuture<Void> done = this._done.get();
            this.check();
            return done;
        }

        @Override
        public boolean isShutdown() {
            return this._done.get() != null;
        }

        public void check() {
            CompletableFuture<Void> done = this._done.get();
            if (done != null && this.isShutdownDone()) {
                done.complete(null);
            }
        }

        public void cancel() {
            CompletableFuture<Void> done = this._done.get();
            if (done != null && !done.isDone()) {
                done.cancel(true);
            }
            this._done.set(null);
        }

        public abstract boolean isShutdownDone();
    }
}

