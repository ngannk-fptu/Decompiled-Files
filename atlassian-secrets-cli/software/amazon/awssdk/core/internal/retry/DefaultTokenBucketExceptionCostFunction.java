/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.retry;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.retry.RetryUtils;
import software.amazon.awssdk.core.retry.conditions.TokenBucketExceptionCostFunction;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class DefaultTokenBucketExceptionCostFunction
implements TokenBucketExceptionCostFunction {
    private final Integer throttlingExceptionCost;
    private final int defaultExceptionCost;

    private DefaultTokenBucketExceptionCostFunction(Builder builder) {
        this.throttlingExceptionCost = builder.throttlingExceptionCost;
        this.defaultExceptionCost = Validate.paramNotNull(builder.defaultExceptionCost, "defaultExceptionCost");
    }

    @Override
    public Integer apply(SdkException e) {
        if (this.throttlingExceptionCost != null && RetryUtils.isThrottlingException(e)) {
            return this.throttlingExceptionCost;
        }
        return this.defaultExceptionCost;
    }

    public String toString() {
        return ToString.builder("TokenBucketExceptionCostCalculator").add("throttlingExceptionCost", this.throttlingExceptionCost).add("defaultExceptionCost", this.defaultExceptionCost).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultTokenBucketExceptionCostFunction that = (DefaultTokenBucketExceptionCostFunction)o;
        if (this.defaultExceptionCost != that.defaultExceptionCost) {
            return false;
        }
        return this.throttlingExceptionCost != null ? this.throttlingExceptionCost.equals(that.throttlingExceptionCost) : that.throttlingExceptionCost == null;
    }

    public int hashCode() {
        int result = this.throttlingExceptionCost != null ? this.throttlingExceptionCost.hashCode() : 0;
        result = 31 * result + this.defaultExceptionCost;
        return result;
    }

    public static final class Builder
    implements TokenBucketExceptionCostFunction.Builder {
        private Integer throttlingExceptionCost;
        private Integer defaultExceptionCost;

        @Override
        public TokenBucketExceptionCostFunction.Builder throttlingExceptionCost(int cost) {
            this.throttlingExceptionCost = cost;
            return this;
        }

        @Override
        public TokenBucketExceptionCostFunction.Builder defaultExceptionCost(int cost) {
            this.defaultExceptionCost = cost;
            return this;
        }

        @Override
        public TokenBucketExceptionCostFunction build() {
            return new DefaultTokenBucketExceptionCostFunction(this);
        }
    }
}

