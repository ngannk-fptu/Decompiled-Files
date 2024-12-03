/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.retry.conditions;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.internal.capacity.TokenBucket;
import software.amazon.awssdk.core.internal.retry.SdkDefaultRetrySetting;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.core.retry.conditions.TokenBucketExceptionCostFunction;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public class TokenBucketRetryCondition
implements RetryCondition {
    private static final Logger log = Logger.loggerFor(TokenBucketRetryCondition.class);
    private static final ExecutionAttribute<Capacity> LAST_ACQUIRED_CAPACITY = new ExecutionAttribute("TokenBucketRetryCondition.LAST_ACQUIRED_CAPACITY");
    private static final ExecutionAttribute<Integer> RETRY_COUNT_OF_LAST_CAPACITY_ACQUISITION = new ExecutionAttribute("TokenBucketRetryCondition.RETRY_COUNT_OF_LAST_CAPACITY_ACQUISITION");
    private final TokenBucket capacity;
    private final TokenBucketExceptionCostFunction exceptionCostFunction;

    private TokenBucketRetryCondition(Builder builder) {
        this.capacity = new TokenBucket(Validate.notNull(builder.tokenBucketSize, "tokenBucketSize", new Object[0]));
        this.exceptionCostFunction = Validate.notNull(builder.exceptionCostFunction, "exceptionCostFunction", new Object[0]);
    }

    public static TokenBucketRetryCondition create() {
        return TokenBucketRetryCondition.forRetryMode(RetryMode.defaultRetryMode());
    }

    public static TokenBucketRetryCondition forRetryMode(RetryMode retryMode) {
        return TokenBucketRetryCondition.builder().tokenBucketSize(500).exceptionCostFunction(SdkDefaultRetrySetting.tokenCostFunction(retryMode)).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Optional<Capacity> getCapacityForExecution(ExecutionAttributes attributes) {
        return Optional.ofNullable(attributes.getAttribute(LAST_ACQUIRED_CAPACITY));
    }

    public int tokensAvailable() {
        return this.capacity.currentCapacity();
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        int costOfFailure = (Integer)this.exceptionCostFunction.apply(context.exception());
        Validate.isTrue(costOfFailure >= 0, "Cost of failure must not be negative, but was " + costOfFailure, new Object[0]);
        Optional<Capacity> capacity = this.capacity.tryAcquire(costOfFailure);
        capacity.ifPresent(c -> {
            context.executionAttributes().putAttribute(LAST_ACQUIRED_CAPACITY, c);
            context.executionAttributes().putAttribute(RETRY_COUNT_OF_LAST_CAPACITY_ACQUISITION, context.retriesAttempted());
            log.trace(() -> "Successfully acquired token bucket capacity to retry this request. Acquired: " + ((Capacity)c).capacityAcquired + ". Remaining: " + ((Capacity)c).capacityRemaining);
        });
        boolean hasCapacity = capacity.isPresent();
        if (!hasCapacity) {
            log.debug(() -> "This request will not be retried because the client has experienced too many recent call failures.");
        }
        return hasCapacity;
    }

    @Override
    public void requestWillNotBeRetried(RetryPolicyContext context) {
        Integer lastAcquisitionRetryCount = context.executionAttributes().getAttribute(RETRY_COUNT_OF_LAST_CAPACITY_ACQUISITION);
        if (lastAcquisitionRetryCount != null && context.retriesAttempted() == lastAcquisitionRetryCount.intValue()) {
            Capacity lastAcquiredCapacity = context.executionAttributes().getAttribute(LAST_ACQUIRED_CAPACITY);
            Validate.validState(lastAcquiredCapacity != null, "Last acquired capacity should not be null.", new Object[0]);
            this.capacity.release(lastAcquiredCapacity.capacityAcquired());
        }
    }

    @Override
    public void requestSucceeded(RetryPolicyContext context) {
        Capacity lastAcquiredCapacity = context.executionAttributes().getAttribute(LAST_ACQUIRED_CAPACITY);
        if (lastAcquiredCapacity == null || lastAcquiredCapacity.capacityAcquired() == 0) {
            this.capacity.release(1);
        } else {
            this.capacity.release(lastAcquiredCapacity.capacityAcquired());
        }
    }

    public String toString() {
        return ToString.builder("TokenBucketRetryCondition").add("capacity", this.capacity.currentCapacity() + "/" + this.capacity.maxCapacity()).add("exceptionCostFunction", this.exceptionCostFunction).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TokenBucketRetryCondition that = (TokenBucketRetryCondition)o;
        if (!this.capacity.equals(that.capacity)) {
            return false;
        }
        return this.exceptionCostFunction.equals(that.exceptionCostFunction);
    }

    public int hashCode() {
        int result = this.capacity.hashCode();
        result = 31 * result + this.exceptionCostFunction.hashCode();
        return result;
    }

    public static final class Capacity {
        private final int capacityAcquired;
        private final int capacityRemaining;

        private Capacity(Builder builder) {
            this.capacityAcquired = Validate.notNull(builder.capacityAcquired, "capacityAcquired", new Object[0]);
            this.capacityRemaining = Validate.notNull(builder.capacityRemaining, "capacityRemaining", new Object[0]);
        }

        public static Builder builder() {
            return new Builder();
        }

        public int capacityAcquired() {
            return this.capacityAcquired;
        }

        public int capacityRemaining() {
            return this.capacityRemaining;
        }

        public static class Builder {
            private Integer capacityAcquired;
            private Integer capacityRemaining;

            private Builder() {
            }

            public Builder capacityAcquired(Integer capacityAcquired) {
                this.capacityAcquired = capacityAcquired;
                return this;
            }

            public Builder capacityRemaining(Integer capacityRemaining) {
                this.capacityRemaining = capacityRemaining;
                return this;
            }

            public Capacity build() {
                return new Capacity(this);
            }
        }
    }

    public static final class Builder {
        private Integer tokenBucketSize;
        private TokenBucketExceptionCostFunction exceptionCostFunction;

        private Builder() {
        }

        public Builder tokenBucketSize(int tokenBucketSize) {
            this.tokenBucketSize = tokenBucketSize;
            return this;
        }

        public Builder exceptionCostFunction(TokenBucketExceptionCostFunction exceptionCostFunction) {
            this.exceptionCostFunction = exceptionCostFunction;
            return this;
        }

        public TokenBucketRetryCondition build() {
            return new TokenBucketRetryCondition(this);
        }
    }
}

