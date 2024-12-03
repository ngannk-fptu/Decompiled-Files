/*
 * Decompiled with CFR 0.152.
 */
package org.apache.juli;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.LogRecord;
import org.apache.juli.FileHandler;

public class AsyncFileHandler
extends FileHandler {
    static final String THREAD_PREFIX = "AsyncFileHandlerWriter-";
    public static final int OVERFLOW_DROP_LAST = 1;
    public static final int OVERFLOW_DROP_FIRST = 2;
    public static final int OVERFLOW_DROP_FLUSH = 3;
    public static final int OVERFLOW_DROP_CURRENT = 4;
    public static final int DEFAULT_OVERFLOW_DROP_TYPE = 1;
    public static final int DEFAULT_MAX_RECORDS = 10000;
    public static final int OVERFLOW_DROP_TYPE = Integer.parseInt(System.getProperty("org.apache.juli.AsyncOverflowDropType", Integer.toString(1)));
    public static final int MAX_RECORDS = Integer.parseInt(System.getProperty("org.apache.juli.AsyncMaxRecordCount", Integer.toString(10000)));
    private static final LoggerExecutorService LOGGER_SERVICE = new LoggerExecutorService(OVERFLOW_DROP_TYPE, MAX_RECORDS);
    private final Object closeLock = new Object();
    protected volatile boolean closed = false;
    private final LoggerExecutorService loggerService;

    public AsyncFileHandler() {
        this(null, null, null);
    }

    public AsyncFileHandler(String directory, String prefix, String suffix) {
        this(directory, prefix, suffix, null);
    }

    public AsyncFileHandler(String directory, String prefix, String suffix, Integer maxDays) {
        this(directory, prefix, suffix, maxDays, LOGGER_SERVICE);
    }

    AsyncFileHandler(String directory, String prefix, String suffix, Integer maxDays, LoggerExecutorService loggerService) {
        super(directory, prefix, suffix, maxDays);
        this.loggerService = loggerService;
        this.open();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        Object object = this.closeLock;
        synchronized (object) {
            if (this.closed) {
                return;
            }
            this.closed = true;
        }
        this.loggerService.deregisterHandler();
        super.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void open() {
        if (!this.closed) {
            return;
        }
        Object object = this.closeLock;
        synchronized (object) {
            if (!this.closed) {
                return;
            }
            this.closed = false;
        }
        this.loggerService.registerHandler();
        super.open();
    }

    @Override
    public void publish(final LogRecord record) {
        if (!this.isLoggable(record)) {
            return;
        }
        record.getSourceMethodName();
        this.loggerService.execute(new Runnable(){

            @Override
            public void run() {
                if (!AsyncFileHandler.this.closed || AsyncFileHandler.this.loggerService.isTerminating()) {
                    AsyncFileHandler.this.publishInternal(record);
                }
            }
        });
    }

    protected void publishInternal(LogRecord record) {
        super.publish(record);
    }

    static class LoggerExecutorService
    extends ThreadPoolExecutor {
        private static final FileHandler.ThreadFactory THREAD_FACTORY = new FileHandler.ThreadFactory("AsyncFileHandlerWriter-");
        private final AtomicInteger handlerCount = new AtomicInteger();

        LoggerExecutorService(int overflowDropType, int maxRecords) {
            super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(maxRecords), THREAD_FACTORY);
            switch (overflowDropType) {
                default: {
                    this.setRejectedExecutionHandler(new DropLastPolicy());
                    break;
                }
                case 2: {
                    this.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
                    break;
                }
                case 3: {
                    this.setRejectedExecutionHandler(new DropFlushPolicy());
                    break;
                }
                case 4: {
                    this.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
                }
            }
        }

        public LinkedBlockingDeque<Runnable> getQueue() {
            return (LinkedBlockingDeque)super.getQueue();
        }

        public void registerHandler() {
            this.handlerCount.incrementAndGet();
        }

        public void deregisterHandler() {
            int newCount = this.handlerCount.decrementAndGet();
            if (newCount == 0) {
                try {
                    Thread dummyHook = new Thread();
                    Runtime.getRuntime().addShutdownHook(dummyHook);
                    Runtime.getRuntime().removeShutdownHook(dummyHook);
                }
                catch (IllegalStateException ise) {
                    this.shutdown();
                    try {
                        this.awaitTermination(10L, TimeUnit.SECONDS);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    this.shutdownNow();
                }
            }
        }
    }

    private static class DropLastPolicy
    implements RejectedExecutionHandler {
        private DropLastPolicy() {
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                ((LinkedBlockingDeque)((LoggerExecutorService)executor).getQueue()).pollLast();
                executor.execute(r);
            }
        }
    }

    private static class DropFlushPolicy
    implements RejectedExecutionHandler {
        private DropFlushPolicy() {
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            while (!executor.isShutdown()) {
                try {
                    if (!executor.getQueue().offer(r, 1000L, TimeUnit.MILLISECONDS)) continue;
                    break;
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RejectedExecutionException("Interrupted", e);
                }
            }
        }
    }
}

