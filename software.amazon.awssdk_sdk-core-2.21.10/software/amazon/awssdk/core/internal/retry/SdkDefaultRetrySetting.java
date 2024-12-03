/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.internal.retry;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.exception.ApiCallAttemptTimeoutException;
import software.amazon.awssdk.core.exception.RetryableException;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.conditions.TokenBucketExceptionCostFunction;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class SdkDefaultRetrySetting {
    public static final int TOKEN_BUCKET_SIZE = 500;
    public static final Duration MAX_BACKOFF = Duration.ofSeconds(20L);
    public static final Set<Integer> RETRYABLE_STATUS_CODES;
    public static final Set<Class<? extends Exception>> RETRYABLE_EXCEPTIONS;

    private SdkDefaultRetrySetting() {
    }

    public static Integer maxAttempts(RetryMode retryMode) {
        Integer maxAttempts = SdkSystemSetting.AWS_MAX_ATTEMPTS.getIntegerValue().orElse(null);
        if (maxAttempts == null) {
            switch (retryMode) {
                case LEGACY: {
                    maxAttempts = 4;
                    break;
                }
                case ADAPTIVE: 
                case STANDARD: {
                    maxAttempts = 3;
                    break;
                }
                default: {
                    throw new IllegalStateException("Unsupported RetryMode: " + (Object)((Object)retryMode));
                }
            }
        }
        Validate.isPositive((int)maxAttempts, (String)("Maximum attempts must be positive, but was " + maxAttempts));
        return maxAttempts;
    }

    public static TokenBucketExceptionCostFunction tokenCostFunction(RetryMode retryMode) {
        switch (retryMode) {
            case LEGACY: {
                return Legacy.COST_FUNCTION;
            }
            case ADAPTIVE: 
            case STANDARD: {
                return Standard.COST_FUNCTION;
            }
        }
        throw new IllegalStateException("Unsupported RetryMode: " + (Object)((Object)retryMode));
    }

    public static Integer defaultMaxAttempts() {
        return SdkDefaultRetrySetting.maxAttempts(RetryMode.defaultRetryMode());
    }

    public static Duration baseDelay(RetryMode retryMode) {
        switch (retryMode) {
            case LEGACY: {
                return Legacy.BASE_DELAY;
            }
            case ADAPTIVE: 
            case STANDARD: {
                return Standard.BASE_DELAY;
            }
        }
        throw new IllegalStateException("Unsupported RetryMode: " + (Object)((Object)retryMode));
    }

    public static Duration throttledBaseDelay(RetryMode retryMode) {
        switch (retryMode) {
            case LEGACY: {
                return Legacy.THROTTLED_BASE_DELAY;
            }
            case ADAPTIVE: 
            case STANDARD: {
                return Standard.THROTTLED_BASE_DELAY;
            }
        }
        throw new IllegalStateException("Unsupported RetryMode: " + (Object)((Object)retryMode));
    }

    static {
        HashSet<Integer> retryableStatusCodes = new HashSet<Integer>();
        retryableStatusCodes.add(500);
        retryableStatusCodes.add(502);
        retryableStatusCodes.add(503);
        retryableStatusCodes.add(504);
        RETRYABLE_STATUS_CODES = Collections.unmodifiableSet(retryableStatusCodes);
        HashSet<Class> retryableExceptions = new HashSet<Class>();
        retryableExceptions.add(RetryableException.class);
        retryableExceptions.add(IOException.class);
        retryableExceptions.add(UncheckedIOException.class);
        retryableExceptions.add(ApiCallAttemptTimeoutException.class);
        RETRYABLE_EXCEPTIONS = Collections.unmodifiableSet(retryableExceptions);
    }

    public static final class Standard {
        private static final int MAX_ATTEMPTS = 3;
        private static final Duration BASE_DELAY = Duration.ofMillis(100L);
        private static final Duration THROTTLED_BASE_DELAY = Duration.ofSeconds(1L);
        private static final int THROTTLE_EXCEPTION_TOKEN_COST = 5;
        private static final int DEFAULT_EXCEPTION_TOKEN_COST = 5;
        public static final TokenBucketExceptionCostFunction COST_FUNCTION = TokenBucketExceptionCostFunction.builder().throttlingExceptionCost(5).defaultExceptionCost(5).build();
    }

    public static final class Legacy {
        private static final int MAX_ATTEMPTS = 4;
        private static final Duration BASE_DELAY = Duration.ofMillis(100L);
        private static final Duration THROTTLED_BASE_DELAY = Duration.ofMillis(500L);
        private static final int THROTTLE_EXCEPTION_TOKEN_COST = 0;
        private static final int DEFAULT_EXCEPTION_TOKEN_COST = 5;
        public static final TokenBucketExceptionCostFunction COST_FUNCTION = TokenBucketExceptionCostFunction.builder().throttlingExceptionCost(0).defaultExceptionCost(5).build();
    }
}

