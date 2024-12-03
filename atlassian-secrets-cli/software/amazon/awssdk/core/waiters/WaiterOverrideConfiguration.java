/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.waiters;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class WaiterOverrideConfiguration
implements ToCopyableBuilder<Builder, WaiterOverrideConfiguration> {
    private final Integer maxAttempts;
    private final BackoffStrategy backoffStrategy;
    private final Duration waitTimeout;

    public WaiterOverrideConfiguration(Builder builder) {
        this.maxAttempts = Validate.isPositiveOrNull(builder.maxAttempts, "maxAttempts");
        this.backoffStrategy = builder.backoffStrategy;
        this.waitTimeout = Validate.isPositiveOrNull(builder.waitTimeout, "waitTimeout");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<Integer> maxAttempts() {
        return Optional.ofNullable(this.maxAttempts);
    }

    public Optional<BackoffStrategy> backoffStrategy() {
        return Optional.ofNullable(this.backoffStrategy);
    }

    public Optional<Duration> waitTimeout() {
        return Optional.ofNullable(this.waitTimeout);
    }

    @Override
    public Builder toBuilder() {
        return new Builder().maxAttempts(this.maxAttempts).backoffStrategy(this.backoffStrategy).waitTimeout(this.waitTimeout);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WaiterOverrideConfiguration that = (WaiterOverrideConfiguration)o;
        if (!Objects.equals(this.maxAttempts, that.maxAttempts)) {
            return false;
        }
        if (!Objects.equals(this.backoffStrategy, that.backoffStrategy)) {
            return false;
        }
        return Objects.equals(this.waitTimeout, that.waitTimeout);
    }

    public int hashCode() {
        int result = this.maxAttempts != null ? this.maxAttempts.hashCode() : 0;
        result = 31 * result + (this.backoffStrategy != null ? this.backoffStrategy.hashCode() : 0);
        result = 31 * result + (this.waitTimeout != null ? this.waitTimeout.hashCode() : 0);
        return result;
    }

    public String toString() {
        return ToString.builder("WaiterOverrideConfiguration").add("maxAttempts", this.maxAttempts).add("waitTimeout", this.waitTimeout).add("backoffStrategy", this.backoffStrategy).build();
    }

    public static final class Builder
    implements CopyableBuilder<Builder, WaiterOverrideConfiguration> {
        private BackoffStrategy backoffStrategy;
        private Integer maxAttempts;
        private Duration waitTimeout;

        private Builder() {
        }

        public Builder backoffStrategy(BackoffStrategy backoffStrategy) {
            this.backoffStrategy = backoffStrategy;
            return this;
        }

        public Builder maxAttempts(Integer maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public Builder waitTimeout(Duration waitTimeout) {
            this.waitTimeout = waitTimeout;
            return this;
        }

        @Override
        public WaiterOverrideConfiguration build() {
            return new WaiterOverrideConfiguration(this);
        }
    }
}

