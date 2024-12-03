/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ToBuilderIgnoreField
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.core.retry;

import java.util.Objects;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ToBuilderIgnoreField;
import software.amazon.awssdk.core.internal.retry.SdkDefaultRetrySetting;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.core.retry.conditions.AndRetryCondition;
import software.amazon.awssdk.core.retry.conditions.MaxNumberOfRetriesCondition;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.core.retry.conditions.TokenBucketRetryCondition;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@Immutable
@SdkPublicApi
public final class RetryPolicy
implements ToCopyableBuilder<Builder, RetryPolicy> {
    private final boolean additionalRetryConditionsAllowed;
    private final RetryMode retryMode;
    private final BackoffStrategy backoffStrategy;
    private final BackoffStrategy throttlingBackoffStrategy;
    private final Integer numRetries;
    private final RetryCondition retryCondition;
    private final RetryCondition retryCapacityCondition;
    private final RetryCondition aggregateRetryCondition;
    private Boolean fastFailRateLimiting;

    private RetryPolicy(BuilderImpl builder) {
        this.additionalRetryConditionsAllowed = builder.additionalRetryConditionsAllowed;
        this.retryMode = builder.retryMode;
        this.backoffStrategy = builder.backoffStrategy;
        this.throttlingBackoffStrategy = builder.throttlingBackoffStrategy;
        this.numRetries = builder.numRetries;
        this.retryCondition = builder.retryCondition;
        this.retryCapacityCondition = builder.retryCapacityCondition;
        this.aggregateRetryCondition = this.generateAggregateRetryCondition();
        this.fastFailRateLimiting = builder.isFastFailRateLimiting();
        this.validateFastFailRateLimiting();
    }

    public static RetryPolicy defaultRetryPolicy() {
        return RetryPolicy.forRetryMode(RetryMode.defaultRetryMode());
    }

    public static RetryPolicy forRetryMode(RetryMode retryMode) {
        return RetryPolicy.builder(retryMode).build();
    }

    public static RetryPolicy none() {
        return RetryPolicy.builder().numRetries(0).backoffStrategy(BackoffStrategy.none()).throttlingBackoffStrategy(BackoffStrategy.none()).retryCondition(RetryCondition.none()).additionalRetryConditionsAllowed(false).build();
    }

    public static Builder builder() {
        return new BuilderImpl(RetryMode.defaultRetryMode());
    }

    public static Builder builder(RetryMode retryMode) {
        Validate.paramNotNull((Object)((Object)retryMode), (String)"The retry mode cannot be set as null. If you don't want to set the retry mode, please use the other builder method without setting retry mode, and the default retry mode will be used.");
        return new BuilderImpl(retryMode);
    }

    public RetryMode retryMode() {
        return this.retryMode;
    }

    public Boolean isFastFailRateLimiting() {
        return this.fastFailRateLimiting;
    }

    public boolean additionalRetryConditionsAllowed() {
        return this.additionalRetryConditionsAllowed;
    }

    public RetryCondition aggregateRetryCondition() {
        return this.aggregateRetryCondition;
    }

    public RetryCondition retryCondition() {
        return this.retryCondition;
    }

    public BackoffStrategy backoffStrategy() {
        return this.backoffStrategy;
    }

    public BackoffStrategy throttlingBackoffStrategy() {
        return this.throttlingBackoffStrategy;
    }

    public Integer numRetries() {
        return this.numRetries;
    }

    private RetryCondition generateAggregateRetryCondition() {
        AndRetryCondition aggregate = AndRetryCondition.create(MaxNumberOfRetriesCondition.create(this.numRetries), this.retryCondition);
        if (this.retryCapacityCondition != null) {
            return AndRetryCondition.create(aggregate, this.retryCapacityCondition);
        }
        return aggregate;
    }

    @ToBuilderIgnoreField(value={"retryMode"})
    public Builder toBuilder() {
        return RetryPolicy.builder(this.retryMode).additionalRetryConditionsAllowed(this.additionalRetryConditionsAllowed).numRetries(this.numRetries).retryCondition(this.retryCondition).backoffStrategy(this.backoffStrategy).throttlingBackoffStrategy(this.throttlingBackoffStrategy).retryCapacityCondition(this.retryCapacityCondition).fastFailRateLimiting(this.fastFailRateLimiting);
    }

    public String toString() {
        return ToString.builder((String)"RetryPolicy").add("additionalRetryConditionsAllowed", (Object)this.additionalRetryConditionsAllowed).add("aggregateRetryCondition", (Object)this.aggregateRetryCondition).add("backoffStrategy", (Object)this.backoffStrategy).add("throttlingBackoffStrategy", (Object)this.throttlingBackoffStrategy).add("fastFailRateLimiting", (Object)this.fastFailRateLimiting).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RetryPolicy that = (RetryPolicy)o;
        if (this.additionalRetryConditionsAllowed != that.additionalRetryConditionsAllowed) {
            return false;
        }
        if (!this.aggregateRetryCondition.equals(that.aggregateRetryCondition)) {
            return false;
        }
        if (!this.backoffStrategy.equals(that.backoffStrategy)) {
            return false;
        }
        if (!this.throttlingBackoffStrategy.equals(that.throttlingBackoffStrategy)) {
            return false;
        }
        return Objects.equals(this.fastFailRateLimiting, that.fastFailRateLimiting);
    }

    public int hashCode() {
        int result = this.aggregateRetryCondition.hashCode();
        result = 31 * result + Boolean.hashCode(this.additionalRetryConditionsAllowed);
        result = 31 * result + this.backoffStrategy.hashCode();
        result = 31 * result + this.throttlingBackoffStrategy.hashCode();
        result = 31 * result + Objects.hashCode(this.fastFailRateLimiting);
        return result;
    }

    private void validateFastFailRateLimiting() {
        if (this.fastFailRateLimiting == null) {
            return;
        }
        Validate.isTrue((RetryMode.ADAPTIVE == this.retryMode ? 1 : 0) != 0, (String)"FastFailRateLimiting is enabled, but this setting is only valid for the ADAPTIVE retry mode. The configured mode is %s.", (Object[])new Object[]{this.retryMode.name()});
    }

    private static final class BuilderImpl
    implements Builder {
        private final RetryMode retryMode;
        private boolean additionalRetryConditionsAllowed;
        private Integer numRetries;
        private BackoffStrategy backoffStrategy;
        private BackoffStrategy throttlingBackoffStrategy;
        private RetryCondition retryCondition;
        private RetryCondition retryCapacityCondition;
        private Boolean fastFailRateLimiting;

        private BuilderImpl(RetryMode retryMode) {
            this.retryMode = retryMode;
            this.numRetries = SdkDefaultRetrySetting.maxAttempts(retryMode) - 1;
            this.additionalRetryConditionsAllowed = true;
            this.backoffStrategy = BackoffStrategy.defaultStrategy(retryMode);
            this.throttlingBackoffStrategy = BackoffStrategy.defaultThrottlingStrategy(retryMode);
            this.retryCondition = RetryCondition.defaultRetryCondition();
            this.retryCapacityCondition = TokenBucketRetryCondition.forRetryMode(retryMode);
        }

        @Override
        public Builder additionalRetryConditionsAllowed(boolean additionalRetryConditionsAllowed) {
            this.additionalRetryConditionsAllowed = additionalRetryConditionsAllowed;
            return this;
        }

        public void setadditionalRetryConditionsAllowed(boolean additionalRetryConditionsAllowed) {
            this.additionalRetryConditionsAllowed(additionalRetryConditionsAllowed);
        }

        @Override
        public boolean additionalRetryConditionsAllowed() {
            return this.additionalRetryConditionsAllowed;
        }

        @Override
        public Builder numRetries(Integer numRetries) {
            this.numRetries = numRetries;
            return this;
        }

        public void setNumRetries(Integer numRetries) {
            this.numRetries(numRetries);
        }

        @Override
        public Integer numRetries() {
            return this.numRetries;
        }

        @Override
        public Builder fastFailRateLimiting(Boolean fastFailRateLimiting) {
            this.fastFailRateLimiting = fastFailRateLimiting;
            return this;
        }

        @Override
        public Boolean isFastFailRateLimiting() {
            return this.fastFailRateLimiting;
        }

        @Override
        public Builder backoffStrategy(BackoffStrategy backoffStrategy) {
            this.backoffStrategy = backoffStrategy;
            return this;
        }

        public void setBackoffStrategy(BackoffStrategy backoffStrategy) {
            this.backoffStrategy(backoffStrategy);
        }

        @Override
        public BackoffStrategy backoffStrategy() {
            return this.backoffStrategy;
        }

        @Override
        public Builder throttlingBackoffStrategy(BackoffStrategy throttlingBackoffStrategy) {
            this.throttlingBackoffStrategy = throttlingBackoffStrategy;
            return this;
        }

        @Override
        public BackoffStrategy throttlingBackoffStrategy() {
            return this.throttlingBackoffStrategy;
        }

        public void setThrottlingBackoffStrategy(BackoffStrategy throttlingBackoffStrategy) {
            this.throttlingBackoffStrategy = throttlingBackoffStrategy;
        }

        @Override
        public Builder retryCondition(RetryCondition retryCondition) {
            this.retryCondition = retryCondition;
            return this;
        }

        public void setRetryCondition(RetryCondition retryCondition) {
            this.retryCondition(retryCondition);
        }

        @Override
        public RetryCondition retryCondition() {
            return this.retryCondition;
        }

        @Override
        public Builder retryCapacityCondition(RetryCondition retryCapacityCondition) {
            this.retryCapacityCondition = retryCapacityCondition;
            return this;
        }

        public void setRetryCapacityCondition(RetryCondition retryCapacityCondition) {
            this.retryCapacityCondition(retryCapacityCondition);
        }

        @Override
        public RetryCondition retryCapacityCondition() {
            return this.retryCapacityCondition;
        }

        @Override
        public RetryPolicy build() {
            return new RetryPolicy(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, RetryPolicy> {
        public Builder additionalRetryConditionsAllowed(boolean var1);

        public boolean additionalRetryConditionsAllowed();

        public Builder backoffStrategy(BackoffStrategy var1);

        public BackoffStrategy backoffStrategy();

        public Builder throttlingBackoffStrategy(BackoffStrategy var1);

        public BackoffStrategy throttlingBackoffStrategy();

        public Builder retryCondition(RetryCondition var1);

        public RetryCondition retryCondition();

        public Builder retryCapacityCondition(RetryCondition var1);

        public RetryCondition retryCapacityCondition();

        public Builder numRetries(Integer var1);

        public Integer numRetries();

        public Builder fastFailRateLimiting(Boolean var1);

        public Boolean isFastFailRateLimiting();

        public RetryPolicy build();
    }
}

