/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.internal.waiters;

import java.time.Duration;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.core.retry.backoff.FixedDelayBackoffStrategy;
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration;

@SdkInternalApi
public final class WaiterConfiguration {
    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final BackoffStrategy DEFAULT_BACKOFF_STRATEGY = FixedDelayBackoffStrategy.create(Duration.ofSeconds(5L));
    private final Integer maxAttempts;
    private final BackoffStrategy backoffStrategy;
    private final Duration waitTimeout;

    public WaiterConfiguration(WaiterOverrideConfiguration overrideConfiguration) {
        Optional<WaiterOverrideConfiguration> configuration = Optional.ofNullable(overrideConfiguration);
        this.backoffStrategy = configuration.flatMap(WaiterOverrideConfiguration::backoffStrategy).orElse(DEFAULT_BACKOFF_STRATEGY);
        this.waitTimeout = configuration.flatMap(WaiterOverrideConfiguration::waitTimeout).orElse(null);
        this.maxAttempts = configuration.flatMap(WaiterOverrideConfiguration::maxAttempts).orElse(3);
    }

    public Duration waitTimeout() {
        return this.waitTimeout;
    }

    public BackoffStrategy backoffStrategy() {
        return this.backoffStrategy;
    }

    public int maxAttempts() {
        return this.maxAttempts;
    }
}

