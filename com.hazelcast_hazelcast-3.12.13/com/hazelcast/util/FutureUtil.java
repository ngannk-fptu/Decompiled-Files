/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.transaction.TransactionTimedOutException;
import com.hazelcast.util.ExceptionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public final class FutureUtil {
    public static final ExceptionHandler RETHROW_EVERYTHING = new ExceptionHandler(){

        @Override
        public void handleException(Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    };
    public static final ExceptionHandler IGNORE_ALL_EXCEPTIONS = new ExceptionHandler(){

        @Override
        public void handleException(Throwable throwable) {
        }
    };
    public static final ExceptionHandler IGNORE_ALL_EXCEPT_LOG_MEMBER_LEFT = new ExceptionHandler(){

        @Override
        public void handleException(Throwable throwable) {
            if (throwable instanceof MemberLeftException && LOGGER.isFinestEnabled()) {
                LOGGER.finest("Member left while waiting for futures...", throwable);
            }
        }
    };
    public static final ExceptionHandler RETHROW_EXECUTION_EXCEPTION = new ExceptionHandler(){

        @Override
        public void handleException(Throwable throwable) {
            if (throwable instanceof MemberLeftException) {
                if (LOGGER.isFinestEnabled()) {
                    LOGGER.finest("Member left while waiting for futures...", throwable);
                }
            } else if (throwable instanceof ExecutionException) {
                throw new HazelcastException(throwable);
            }
        }
    };
    public static final ExceptionHandler RETHROW_ALL_EXCEPT_MEMBER_LEFT = new ExceptionHandler(){

        @Override
        public void handleException(Throwable throwable) {
            if (throwable instanceof MemberLeftException) {
                if (LOGGER.isFinestEnabled()) {
                    LOGGER.finest("Member left while waiting for futures...", throwable);
                }
            } else {
                throw new HazelcastException(throwable);
            }
        }
    };
    public static final ExceptionHandler RETHROW_TRANSACTION_EXCEPTION = new ExceptionHandler(){

        @Override
        public void handleException(Throwable throwable) {
            if (throwable instanceof TimeoutException) {
                throw new TransactionTimedOutException(throwable);
            }
            throw ExceptionUtil.rethrow(throwable);
        }
    };
    private static final ILogger LOGGER = Logger.getLogger(FutureUtil.class);

    private FutureUtil() {
    }

    @PrivateApi
    public static ExceptionHandler logAllExceptions(final ILogger logger, final String message, final Level level) {
        if (logger.isLoggable(level)) {
            return new ExceptionHandler(){

                @Override
                public void handleException(Throwable throwable) {
                    logger.log(level, message, throwable);
                }
            };
        }
        return IGNORE_ALL_EXCEPTIONS;
    }

    @PrivateApi
    public static ExceptionHandler logAllExceptions(final String message, final Level level) {
        if (LOGGER.isLoggable(level)) {
            return new ExceptionHandler(){

                @Override
                public void handleException(Throwable throwable) {
                    LOGGER.log(level, message, throwable);
                }
            };
        }
        return IGNORE_ALL_EXCEPTIONS;
    }

    @PrivateApi
    public static ExceptionHandler logAllExceptions(final ILogger logger, final Level level) {
        if (logger.isLoggable(level)) {
            return new ExceptionHandler(){

                @Override
                public void handleException(Throwable throwable) {
                    logger.log(level, "Exception occurred", throwable);
                }
            };
        }
        return IGNORE_ALL_EXCEPTIONS;
    }

    @PrivateApi
    public static ExceptionHandler logAllExceptions(final Level level) {
        if (LOGGER.isLoggable(level)) {
            return new ExceptionHandler(){

                @Override
                public void handleException(Throwable throwable) {
                    LOGGER.log(level, "Exception occurred", throwable);
                }
            };
        }
        return IGNORE_ALL_EXCEPTIONS;
    }

    @PrivateApi
    public static <V> Collection<V> returnWithDeadline(Collection<Future<V>> futures, long timeout, TimeUnit timeUnit) {
        return FutureUtil.returnWithDeadline(futures, timeout, timeUnit, IGNORE_ALL_EXCEPT_LOG_MEMBER_LEFT);
    }

    @PrivateApi
    public static <V> Collection<V> returnWithDeadline(Collection<Future<V>> futures, long timeout, TimeUnit timeUnit, ExceptionHandler exceptionHandler) {
        return FutureUtil.returnWithDeadline(futures, timeout, timeUnit, timeout, timeUnit, exceptionHandler);
    }

    @PrivateApi
    public static <V> Collection<V> returnWithDeadline(Collection<Future<V>> futures, long overallTimeout, TimeUnit overallTimeUnit, long perFutureTimeout, TimeUnit perFutureTimeUnit) {
        return FutureUtil.returnWithDeadline(futures, overallTimeout, overallTimeUnit, perFutureTimeout, perFutureTimeUnit, IGNORE_ALL_EXCEPT_LOG_MEMBER_LEFT);
    }

    @PrivateApi
    public static <V> Collection<V> returnWithDeadline(Collection<Future<V>> futures, long overallTimeout, TimeUnit overallTimeUnit, long perFutureTimeout, TimeUnit perFutureTimeUnit, ExceptionHandler exceptionHandler) {
        long overallTimeoutNanos = FutureUtil.calculateTimeout(overallTimeout, overallTimeUnit);
        long perFutureTimeoutNanos = FutureUtil.calculateTimeout(perFutureTimeout, perFutureTimeUnit);
        long deadline = System.nanoTime() + overallTimeoutNanos;
        ArrayList<V> results = new ArrayList<V>(futures.size());
        for (Future<V> future : futures) {
            try {
                long timeoutNanos = FutureUtil.calculateFutureTimeout(perFutureTimeoutNanos, deadline);
                V value = FutureUtil.executeWithDeadline(future, timeoutNanos);
                if (value == null) continue;
                results.add(value);
            }
            catch (Exception e) {
                exceptionHandler.handleException(e);
            }
        }
        return results;
    }

    @PrivateApi
    public static void waitForever(Collection<? extends Future> futuresToWaitFor, ExceptionHandler exceptionHandler) {
        ArrayList<? extends Future> futures = new ArrayList<Future>(futuresToWaitFor);
        do {
            Iterator it = futures.iterator();
            while (it.hasNext()) {
                Future future = (Future)it.next();
                try {
                    future.get();
                }
                catch (Exception e) {
                    exceptionHandler.handleException(e);
                }
                if (!future.isDone() && !future.isCancelled()) continue;
                it.remove();
            }
        } while (!futures.isEmpty());
    }

    @PrivateApi
    public static void waitForever(Collection<? extends Future> futures) {
        FutureUtil.waitForever(futures, IGNORE_ALL_EXCEPT_LOG_MEMBER_LEFT);
    }

    @PrivateApi
    public static void waitWithDeadline(Collection<? extends Future> futures, long timeout, TimeUnit timeUnit) {
        FutureUtil.waitWithDeadline(futures, timeout, timeUnit, IGNORE_ALL_EXCEPT_LOG_MEMBER_LEFT);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @PrivateApi
    public static void waitUntilAllRespondedWithDeadline(Collection<? extends Future> futures, long timeout, TimeUnit timeUnit, ExceptionHandler exceptionHandler) {
        List<Throwable> throwables;
        CollectAllExceptionHandler collector = new CollectAllExceptionHandler(futures.size());
        FutureUtil.waitWithDeadline(futures, timeout, timeUnit, collector);
        List<Throwable> list = throwables = collector.getThrowables();
        synchronized (list) {
            for (Throwable t : throwables) {
                exceptionHandler.handleException(t);
            }
        }
    }

    @PrivateApi
    public static List<Throwable> waitUntilAllResponded(Collection<? extends Future> futures) {
        CollectAllExceptionHandler collector = new CollectAllExceptionHandler(futures.size());
        FutureUtil.waitForever(futures, collector);
        return collector.getThrowables();
    }

    @PrivateApi
    public static void waitWithDeadline(Collection<? extends Future> futures, long timeout, TimeUnit timeUnit, ExceptionHandler exceptionHandler) {
        FutureUtil.waitWithDeadline(futures, timeout, timeUnit, timeout, timeUnit, exceptionHandler);
    }

    @PrivateApi
    public static void waitWithDeadline(Collection<? extends Future> futures, long overallTimeout, TimeUnit overallTimeUnit, long perFutureTimeout, TimeUnit perFutureTimeUnit) {
        FutureUtil.waitWithDeadline(futures, overallTimeout, overallTimeUnit, perFutureTimeout, perFutureTimeUnit, IGNORE_ALL_EXCEPT_LOG_MEMBER_LEFT);
    }

    @PrivateApi
    public static void waitWithDeadline(Collection<? extends Future> futures, long overallTimeout, TimeUnit overallTimeUnit, long perFutureTimeout, TimeUnit perFutureTimeUnit, ExceptionHandler exceptionHandler) {
        long overallTimeoutNanos = FutureUtil.calculateTimeout(overallTimeout, overallTimeUnit);
        long perFutureTimeoutNanos = FutureUtil.calculateTimeout(perFutureTimeout, perFutureTimeUnit);
        long deadline = System.nanoTime() + overallTimeoutNanos;
        for (Future future : futures) {
            try {
                long timeoutNanos = FutureUtil.calculateFutureTimeout(perFutureTimeoutNanos, deadline);
                FutureUtil.executeWithDeadline(future, timeoutNanos);
            }
            catch (Throwable e) {
                exceptionHandler.handleException(e);
            }
        }
    }

    private static <V> V executeWithDeadline(Future<V> future, long timeoutNanos) throws Exception {
        if (timeoutNanos <= 0L) {
            if (future.isDone() || future.isCancelled()) {
                return FutureUtil.retrieveValue(future);
            }
            throw new TimeoutException();
        }
        return future.get(timeoutNanos, TimeUnit.NANOSECONDS);
    }

    private static <V> V retrieveValue(Future<V> future) throws ExecutionException, InterruptedException {
        if (future instanceof InternalCompletableFuture) {
            return (V)((InternalCompletableFuture)future).join();
        }
        return future.get();
    }

    private static long calculateTimeout(long timeout, TimeUnit timeUnit) {
        timeUnit = timeUnit == null ? TimeUnit.SECONDS : timeUnit;
        return timeUnit.toNanos(timeout);
    }

    private static long calculateFutureTimeout(long perFutureTimeoutNanos, long deadline) {
        long remainingNanos = deadline - System.nanoTime();
        return Math.min(remainingNanos, perFutureTimeoutNanos);
    }

    public static boolean allDone(Collection<Future> futures) {
        for (Future f : futures) {
            if (f.isDone()) continue;
            return false;
        }
        return true;
    }

    public static void checkAllDone(Collection<Future> futures) throws Exception {
        for (Future f : futures) {
            if (!f.isDone()) continue;
            f.get();
        }
    }

    public static List<Future> getAllDone(Collection<Future> futures) {
        ArrayList<Future> doneFutures = new ArrayList<Future>();
        for (Future f : futures) {
            if (!f.isDone()) continue;
            doneFutures.add(f);
        }
        return doneFutures;
    }

    public static interface ExceptionHandler {
        public void handleException(Throwable var1);
    }

    private static final class CollectAllExceptionHandler
    implements ExceptionHandler {
        private List<Throwable> throwables;

        private CollectAllExceptionHandler(int count) {
            this.throwables = Collections.synchronizedList(new ArrayList(count));
        }

        @Override
        public void handleException(Throwable throwable) {
            this.throwables.add(throwable);
        }

        public List<Throwable> getThrowables() {
            return this.throwables;
        }
    }
}

