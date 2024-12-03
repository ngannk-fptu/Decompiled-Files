/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.time.Duration;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LazyCleaner {
    private static final Logger LOGGER = Logger.getLogger(LazyCleaner.class.getName());
    private static final LazyCleaner instance = new LazyCleaner(Duration.ofMillis(Long.getLong("pgjdbc.config.cleanup.thread.ttl", 30000L)), "PostgreSQL-JDBC-Cleaner");
    private final ReferenceQueue<Object> queue = new ReferenceQueue();
    private final long threadTtl;
    private final ThreadFactory threadFactory;
    private boolean threadRunning = false;
    private int watchedCount = 0;
    private @Nullable Node<?> first;

    public static LazyCleaner getInstance() {
        return instance;
    }

    public LazyCleaner(Duration threadTtl, String threadName) {
        this(threadTtl, (Runnable runnable) -> {
            Thread thread = new Thread(runnable, threadName);
            thread.setDaemon(true);
            return thread;
        });
    }

    private LazyCleaner(Duration threadTtl, ThreadFactory threadFactory) {
        this.threadTtl = threadTtl.toMillis();
        this.threadFactory = threadFactory;
    }

    public <T extends Throwable> Cleanable<T> register(Object obj, CleaningAction<T> action) {
        assert (obj != action) : "object handle should not be the same as cleaning action, otherwise the object will never become phantom reachable, so the action will never trigger";
        return this.add(new Node<T>(obj, action));
    }

    public synchronized int getWatchedCount() {
        return this.watchedCount;
    }

    public synchronized boolean isThreadRunning() {
        return this.threadRunning;
    }

    private synchronized boolean checkEmpty() {
        if (this.first == null) {
            this.threadRunning = false;
            return true;
        }
        return false;
    }

    private synchronized <T extends Throwable> Node<T> add(Node<T> node) {
        if (this.first != null) {
            ((Node)node).next = (Node)this.first;
            ((Node)this.first).prev = (Node)node;
        }
        this.first = node;
        ++this.watchedCount;
        if (!this.threadRunning) {
            this.threadRunning = this.startThread();
        }
        return node;
    }

    private boolean startThread() {
        Thread thread = this.threadFactory.newThread(new Runnable(){

            @Override
            public void run() {
                block5: while (true) {
                    try {
                        while (true) {
                            Thread.currentThread().setContextClassLoader(null);
                            Thread.currentThread().setUncaughtExceptionHandler(null);
                            Node ref = (Node)LazyCleaner.this.queue.remove(LazyCleaner.this.threadTtl);
                            if (ref == null) {
                                if (!LazyCleaner.this.checkEmpty()) continue;
                                break block5;
                            }
                            try {
                                ref.onClean(true);
                                continue block5;
                            }
                            catch (Throwable e) {
                                if (e instanceof InterruptedException) {
                                    LOGGER.log(Level.WARNING, "Unexpected interrupt while executing onClean", e);
                                    throw e;
                                }
                                LOGGER.log(Level.WARNING, "Unexpected exception while executing onClean", e);
                                continue;
                            }
                            break;
                        }
                    }
                    catch (InterruptedException e) {
                        if (LazyCleaner.this.checkEmpty()) {
                            LOGGER.log(Level.FINE, "Cleanup queue is empty, and got interrupt, will terminate the cleanup thread");
                            break;
                        }
                        LOGGER.log(Level.FINE, "Ignoring interrupt since the cleanup queue is non-empty");
                        continue;
                    }
                    catch (Throwable e) {
                        LOGGER.log(Level.WARNING, "Unexpected exception in cleaner thread main loop", e);
                        continue;
                    }
                    break;
                }
            }
        });
        if (thread != null) {
            thread.start();
            return true;
        }
        LOGGER.log(Level.WARNING, "Unable to create cleanup thread");
        return false;
    }

    private synchronized boolean remove(Node<?> node) {
        if (((Node)node).next == node) {
            return false;
        }
        if (this.first == node) {
            this.first = ((Node)node).next;
        }
        if (((Node)node).next != null) {
            ((Node)((Node)node).next).prev = (Node)((Node)node).prev;
        }
        if (((Node)node).prev != null) {
            ((Node)((Node)node).prev).next = (Node)((Node)node).next;
        }
        ((Node)node).next = (Node)node;
        ((Node)node).prev = (Node)node;
        --this.watchedCount;
        return true;
    }

    private class Node<T extends Throwable>
    extends PhantomReference<Object>
    implements Cleanable<T>,
    CleaningAction<T> {
        private final @Nullable CleaningAction<T> action;
        private @Nullable Node<?> prev;
        private @Nullable Node<?> next;

        Node(Object referent, CleaningAction<T> action) {
            super(referent, LazyCleaner.this.queue);
            this.action = action;
        }

        @Override
        public void clean() throws T {
            this.onClean(false);
        }

        @Override
        public void onClean(boolean leak) throws T {
            if (!LazyCleaner.this.remove(this)) {
                return;
            }
            if (this.action != null) {
                this.action.onClean(leak);
            }
        }
    }

    public static interface CleaningAction<T extends Throwable> {
        public void onClean(boolean var1) throws T;
    }

    public static interface Cleanable<T extends Throwable> {
        public void clean() throws T;
    }
}

