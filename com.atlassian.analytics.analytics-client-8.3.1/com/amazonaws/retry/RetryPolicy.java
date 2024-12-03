/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.annotation.Immutable;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.retry.RetryMode;
import com.amazonaws.retry.internal.RetryModeResolver;

@Immutable
public final class RetryPolicy {
    private static final RetryModeResolver RETRY_MODE_RESOLVER = new RetryModeResolver();
    private final RetryCondition retryCondition;
    private final BackoffStrategy backoffStrategy;
    private final int maxErrorRetry;
    private final boolean honorMaxErrorRetryInClientConfig;
    private final RetryMode retryMode;
    private final boolean honorDefaultMaxErrorRetryInRetryMode;
    private final boolean fastFailRateLimiting;
    private final boolean honorBackoffStrategyInRetryMode;

    public RetryPolicy(RetryCondition retryCondition, BackoffStrategy backoffStrategy, int maxErrorRetry, boolean honorMaxErrorRetryInClientConfig) {
        this(retryCondition, backoffStrategy, maxErrorRetry, honorMaxErrorRetryInClientConfig, false, false);
    }

    @SdkInternalApi
    public RetryPolicy(RetryCondition retryCondition, BackoffStrategy backoffStrategy, int maxErrorRetry, boolean honorMaxErrorRetryInClientConfig, boolean honorDefaultMaxErrorRetryInRetryMode, boolean honorBackoffStrategyInRetryMode) {
        this(retryCondition, backoffStrategy, maxErrorRetry, honorMaxErrorRetryInClientConfig, null, honorDefaultMaxErrorRetryInRetryMode, false, honorBackoffStrategyInRetryMode);
    }

    public RetryPolicy(RetryCondition retryCondition, BackoffStrategy backoffStrategy, int maxErrorRetry, boolean honorMaxErrorRetryInClientConfig, RetryMode retryMode) {
        this(retryCondition, backoffStrategy, maxErrorRetry, honorMaxErrorRetryInClientConfig, retryMode, false, false, false);
    }

    private RetryPolicy(RetryPolicyBuilder builder) {
        this(builder.retryCondition, builder.backoffStrategy, builder.maxErrorRetry, builder.honorMaxErrorRetryInClientConfig, builder.retryMode, builder.honorDefaultMaxErrorRetryInRetryMode, builder.fastFailRateLimiting, builder.honorBackOffStrategyInRetryMode);
    }

    @SdkInternalApi
    RetryPolicy(RetryCondition retryCondition, BackoffStrategy backoffStrategy, int maxErrorRetry, boolean honorMaxErrorRetryInClientConfig, RetryMode retryMode, boolean honorDefaultMaxErrorRetryInRetryMode, boolean fastFailRateLimiting, boolean honorBackoffStrategyInRetryMode) {
        if (retryCondition == null) {
            retryCondition = PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION;
        }
        if (maxErrorRetry < 0) {
            throw new IllegalArgumentException("Please provide a non-negative value for maxErrorRetry.");
        }
        if (backoffStrategy == null) {
            backoffStrategy = PredefinedRetryPolicies.DEFAULT_BACKOFF_STRATEGY;
        }
        this.honorDefaultMaxErrorRetryInRetryMode = honorDefaultMaxErrorRetryInRetryMode;
        this.retryCondition = retryCondition;
        this.maxErrorRetry = maxErrorRetry;
        this.honorMaxErrorRetryInClientConfig = honorMaxErrorRetryInClientConfig;
        this.retryMode = retryMode != null ? retryMode : RETRY_MODE_RESOLVER.retryMode();
        this.honorBackoffStrategyInRetryMode = honorBackoffStrategyInRetryMode;
        this.backoffStrategy = honorBackoffStrategyInRetryMode ? PredefinedRetryPolicies.getDefaultBackoffStrategy(this.retryMode) : backoffStrategy;
        this.fastFailRateLimiting = fastFailRateLimiting;
    }

    public RetryCondition getRetryCondition() {
        return this.retryCondition;
    }

    public BackoffStrategy getBackoffStrategy() {
        return this.backoffStrategy;
    }

    public int getMaxErrorRetry() {
        return this.maxErrorRetry;
    }

    public boolean isMaxErrorRetryInClientConfigHonored() {
        return this.honorMaxErrorRetryInClientConfig;
    }

    public RetryMode getRetryMode() {
        return this.retryMode;
    }

    public boolean isFastFailRateLimiting() {
        return this.fastFailRateLimiting;
    }

    boolean isDefaultMaxErrorRetryInRetryModeHonored() {
        return this.honorDefaultMaxErrorRetryInRetryMode;
    }

    boolean isBackoffStrategyInRetryModeHonored() {
        return this.honorBackoffStrategyInRetryMode;
    }

    public static RetryPolicyBuilder builder() {
        return new RetryPolicyBuilder();
    }

    public static interface BackoffStrategy {
        public static final BackoffStrategy NO_DELAY = new BackoffStrategy(){

            @Override
            public long delayBeforeNextRetry(AmazonWebServiceRequest originalRequest, AmazonClientException exception, int retriesAttempted) {
                return 0L;
            }
        };

        public long delayBeforeNextRetry(AmazonWebServiceRequest var1, AmazonClientException var2, int var3);
    }

    public static interface RetryCondition {
        public static final RetryCondition NO_RETRY_CONDITION = new RetryCondition(){

            @Override
            public boolean shouldRetry(AmazonWebServiceRequest originalRequest, AmazonClientException exception, int retriesAttempted) {
                return false;
            }
        };

        public boolean shouldRetry(AmazonWebServiceRequest var1, AmazonClientException var2, int var3);
    }

    public static final class RetryPolicyBuilder {
        private RetryCondition retryCondition;
        private BackoffStrategy backoffStrategy;
        private int maxErrorRetry;
        private boolean honorMaxErrorRetryInClientConfig;
        private RetryMode retryMode;
        private boolean honorDefaultMaxErrorRetryInRetryMode;
        private boolean fastFailRateLimiting;
        private boolean honorBackOffStrategyInRetryMode;

        public RetryPolicyBuilder withRetryCondition(RetryCondition retryCondition) {
            this.retryCondition = retryCondition;
            return this;
        }

        public void setRetryCondition(RetryCondition retryCondition) {
            this.withRetryCondition(retryCondition);
        }

        public RetryPolicyBuilder withBackoffStrategy(BackoffStrategy backoffStrategy) {
            this.backoffStrategy = backoffStrategy;
            return this;
        }

        public void setBackoffStrategy(BackoffStrategy backoffStrategy) {
            this.withBackoffStrategy(backoffStrategy);
        }

        public RetryPolicyBuilder withMaxErrorRetry(int maxErrorRetry) {
            this.maxErrorRetry = maxErrorRetry;
            return this;
        }

        public void setMaxErrorRetry(int maxErrorRetry) {
            this.withMaxErrorRetry(maxErrorRetry);
        }

        public RetryPolicyBuilder withHonorMaxErrorRetryInClientConfig(boolean honorMaxErrorRetryInClientConfig) {
            this.honorMaxErrorRetryInClientConfig = honorMaxErrorRetryInClientConfig;
            return this;
        }

        public void setHonorMaxErrorRetryInClientConfig(boolean honorMaxErrorRetryInClientConfig) {
            this.withHonorMaxErrorRetryInClientConfig(honorMaxErrorRetryInClientConfig);
        }

        public RetryPolicyBuilder withRetryMode(RetryMode retryMode) {
            this.retryMode = retryMode;
            return this;
        }

        public void setRetryMode(RetryMode retryMode) {
            this.withRetryMode(retryMode);
        }

        public RetryPolicyBuilder withHonorDefaultMaxErrorRetryInRetryMode(boolean honorDefaultMaxErrorRetryInRetryMode) {
            this.honorDefaultMaxErrorRetryInRetryMode = honorDefaultMaxErrorRetryInRetryMode;
            return this;
        }

        public void setHonorDefaultMaxErrorRetryInRetryMode(boolean honorDefaultMaxErrorRetryInRetryMode) {
            this.withHonorDefaultMaxErrorRetryInRetryMode(honorDefaultMaxErrorRetryInRetryMode);
        }

        public RetryPolicyBuilder withFastFailRateLimiting(boolean fastFailRateLimiting) {
            this.fastFailRateLimiting = fastFailRateLimiting;
            return this;
        }

        public void setFastFailRateLimiting(boolean fastFailRateLimiting) {
            this.withFastFailRateLimiting(fastFailRateLimiting);
        }

        public RetryPolicyBuilder withHonorDefaultBackoffStrategyInRetryMode(boolean honorBackOffStrategyInRetryMode) {
            this.honorBackOffStrategyInRetryMode = honorBackOffStrategyInRetryMode;
            return this;
        }

        public void setHonorDefaultBackoffStrategyInRetryMode(boolean honorBackOffStrategyInRetryMode) {
            this.withHonorDefaultBackoffStrategyInRetryMode(honorBackOffStrategyInRetryMode);
        }

        public RetryPolicy build() {
            return new RetryPolicy(this);
        }
    }
}

