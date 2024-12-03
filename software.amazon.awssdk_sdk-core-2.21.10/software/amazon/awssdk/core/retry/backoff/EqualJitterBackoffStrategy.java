/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.NumericUtils
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.core.retry.backoff;

import java.time.Duration;
import java.util.Random;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.utils.NumericUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class EqualJitterBackoffStrategy
implements BackoffStrategy,
ToCopyableBuilder<Builder, EqualJitterBackoffStrategy> {
    private static final Duration BASE_DELAY_CEILING = Duration.ofMillis(Integer.MAX_VALUE);
    private static final Duration MAX_BACKOFF_CEILING = Duration.ofMillis(Integer.MAX_VALUE);
    private final Duration baseDelay;
    private final Duration maxBackoffTime;
    private final Random random;

    private EqualJitterBackoffStrategy(BuilderImpl builder) {
        this(builder.baseDelay, builder.maxBackoffTime, new Random());
    }

    EqualJitterBackoffStrategy(Duration baseDelay, Duration maxBackoffTime, Random random) {
        this.baseDelay = NumericUtils.min((Duration)Validate.isNotNegative((Duration)baseDelay, (String)"baseDelay"), (Duration)BASE_DELAY_CEILING);
        this.maxBackoffTime = NumericUtils.min((Duration)Validate.isNotNegative((Duration)maxBackoffTime, (String)"maxBackoffTime"), (Duration)MAX_BACKOFF_CEILING);
        this.random = random;
    }

    @Override
    public Duration computeDelayBeforeNextRetry(RetryPolicyContext context) {
        int ceil = this.calculateExponentialDelay(context.retriesAttempted(), this.baseDelay, this.maxBackoffTime);
        return Duration.ofMillis(ceil / 2 + this.random.nextInt(ceil / 2 + 1));
    }

    public Builder toBuilder() {
        return EqualJitterBackoffStrategy.builder().baseDelay(this.baseDelay).maxBackoffTime(this.maxBackoffTime);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EqualJitterBackoffStrategy that = (EqualJitterBackoffStrategy)o;
        if (!this.baseDelay.equals(that.baseDelay)) {
            return false;
        }
        return this.maxBackoffTime.equals(that.maxBackoffTime);
    }

    public int hashCode() {
        int result = this.baseDelay.hashCode();
        result = 31 * result + this.maxBackoffTime.hashCode();
        return result;
    }

    public String toString() {
        return ToString.builder((String)"EqualJitterBackoffStrategy").add("baseDelay", (Object)this.baseDelay).add("maxBackoffTime", (Object)this.maxBackoffTime).build();
    }

    private static final class BuilderImpl
    implements Builder {
        private Duration baseDelay;
        private Duration maxBackoffTime;

        private BuilderImpl() {
        }

        @Override
        public Builder baseDelay(Duration baseDelay) {
            this.baseDelay = baseDelay;
            return this;
        }

        public void setBaseDelay(Duration baseDelay) {
            this.baseDelay(baseDelay);
        }

        @Override
        public Duration baseDelay() {
            return this.baseDelay;
        }

        @Override
        public Builder maxBackoffTime(Duration maxBackoffTime) {
            this.maxBackoffTime = maxBackoffTime;
            return this;
        }

        public void setMaxBackoffTime(Duration maxBackoffTime) {
            this.maxBackoffTime(maxBackoffTime);
        }

        @Override
        public Duration maxBackoffTime() {
            return this.maxBackoffTime;
        }

        @Override
        public EqualJitterBackoffStrategy build() {
            return new EqualJitterBackoffStrategy(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, EqualJitterBackoffStrategy> {
        public Builder baseDelay(Duration var1);

        public Duration baseDelay();

        public Builder maxBackoffTime(Duration var1);

        public Duration maxBackoffTime();

        public EqualJitterBackoffStrategy build();
    }
}

