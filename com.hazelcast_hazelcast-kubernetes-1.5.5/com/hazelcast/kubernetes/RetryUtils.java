/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastException
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.logging.Logger
 *  com.hazelcast.util.ExceptionUtil
 */
package com.hazelcast.kubernetes;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.ExceptionUtil;
import java.util.List;
import java.util.concurrent.Callable;

final class RetryUtils {
    static final long INITIAL_BACKOFF_MS = 1500L;
    static final long MAX_BACKOFF_MS = 300000L;
    static final double BACKOFF_MULTIPLIER = 1.5;
    private static final ILogger LOGGER = Logger.getLogger(RetryUtils.class);
    private static final long MS_IN_SECOND = 1000L;

    private RetryUtils() {
    }

    public static <T> T retry(Callable<T> callable, int retries, List<String> nonRetryableKeywords) {
        int retryCount = 0;
        while (true) {
            try {
                return callable.call();
            }
            catch (Exception e) {
                if (++retryCount > retries || RetryUtils.containsAnyOf(e, nonRetryableKeywords)) {
                    throw ExceptionUtil.rethrow((Throwable)e);
                }
                long waitIntervalMs = RetryUtils.backoffIntervalForRetry(retryCount);
                LOGGER.warning(String.format("Couldn't discover Hazelcast members using Kubernetes API, [%s] retrying in %s seconds...", retryCount, waitIntervalMs / 1000L));
                RetryUtils.sleep(waitIntervalMs);
                continue;
            }
            break;
        }
    }

    private static boolean containsAnyOf(Exception e, List<String> nonRetryableKeywords) {
        for (Throwable currentException = e; currentException != null; currentException = currentException.getCause()) {
            String exceptionMessage = currentException.getMessage();
            for (String keyword : nonRetryableKeywords) {
                if (exceptionMessage == null || !exceptionMessage.contains(keyword)) continue;
                return true;
            }
        }
        return false;
    }

    private static long backoffIntervalForRetry(int retryCount) {
        long result = 1500L;
        for (int i = 1; i < retryCount; ++i) {
            if ((result = (long)((double)result * 1.5)) <= 300000L) continue;
            return 300000L;
        }
        return result;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HazelcastException((Throwable)e);
        }
    }
}

