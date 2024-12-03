/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core.retry.backoff;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.internal.retry.SdkDefaultRetrySetting;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.backoff.EqualJitterBackoffStrategy;
import software.amazon.awssdk.core.retry.backoff.FixedDelayBackoffStrategy;
import software.amazon.awssdk.core.retry.backoff.FullJitterBackoffStrategy;

@FunctionalInterface
@SdkPublicApi
public interface BackoffStrategy {
    public static final int RETRIES_ATTEMPTED_CEILING = (int)Math.floor(Math.log(2.147483647E9) / Math.log(2.0));

    public Duration computeDelayBeforeNextRetry(RetryPolicyContext var1);

    default public int calculateExponentialDelay(int retriesAttempted, Duration baseDelay, Duration maxBackoffTime) {
        int cappedRetries = Math.min(retriesAttempted, RETRIES_ATTEMPTED_CEILING);
        return (int)Math.min(baseDelay.multipliedBy(1L << cappedRetries).toMillis(), maxBackoffTime.toMillis());
    }

    public static BackoffStrategy defaultStrategy() {
        return BackoffStrategy.defaultStrategy(RetryMode.defaultRetryMode());
    }

    public static BackoffStrategy defaultStrategy(RetryMode retryMode) {
        return FullJitterBackoffStrategy.builder().baseDelay(SdkDefaultRetrySetting.baseDelay(retryMode)).maxBackoffTime(SdkDefaultRetrySetting.MAX_BACKOFF).build();
    }

    public static BackoffStrategy defaultThrottlingStrategy() {
        return BackoffStrategy.defaultThrottlingStrategy(RetryMode.defaultRetryMode());
    }

    public static BackoffStrategy defaultThrottlingStrategy(RetryMode retryMode) {
        switch (retryMode) {
            case LEGACY: {
                return EqualJitterBackoffStrategy.builder().baseDelay(SdkDefaultRetrySetting.throttledBaseDelay(retryMode)).maxBackoffTime(SdkDefaultRetrySetting.MAX_BACKOFF).build();
            }
            case ADAPTIVE: 
            case STANDARD: {
                return FullJitterBackoffStrategy.builder().baseDelay(SdkDefaultRetrySetting.throttledBaseDelay(retryMode)).maxBackoffTime(SdkDefaultRetrySetting.MAX_BACKOFF).build();
            }
        }
        throw new IllegalStateException("Unsupported RetryMode: " + (Object)((Object)retryMode));
    }

    public static BackoffStrategy none() {
        return FixedDelayBackoffStrategy.create(Duration.ofMillis(1L));
    }
}

