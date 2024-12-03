/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.retry.RetryMode;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.retry.internal.MaxAttemptsResolver;
import com.amazonaws.retry.v2.RetryPolicy;
import com.amazonaws.retry.v2.RetryPolicyContext;
import com.amazonaws.util.ValidationUtils;

@SdkInternalApi
public class RetryPolicyAdapter
implements RetryPolicy {
    private final com.amazonaws.retry.RetryPolicy legacyRetryPolicy;
    private final ClientConfiguration clientConfiguration;
    private final int maxErrorRetry;
    private final RetryPolicy.BackoffStrategy backoffStrategy;

    public RetryPolicyAdapter(com.amazonaws.retry.RetryPolicy legacyRetryPolicy, ClientConfiguration clientConfiguration) {
        this.legacyRetryPolicy = ValidationUtils.assertNotNull(legacyRetryPolicy, "legacyRetryPolicy");
        this.clientConfiguration = ValidationUtils.assertNotNull(clientConfiguration, "clientConfiguration");
        this.maxErrorRetry = this.resolveMaxErrorRetry();
        this.backoffStrategy = this.resolveBackoffStrategy();
    }

    @Override
    public long computeDelayBeforeNextRetry(RetryPolicyContext context) {
        return this.backoffStrategy.delayBeforeNextRetry((AmazonWebServiceRequest)context.originalRequest(), (AmazonClientException)context.exception(), context.retriesAttempted());
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        return !this.maxRetriesExceeded(context) && this.isRetryable(context);
    }

    public boolean isRetryable(RetryPolicyContext context) {
        return this.legacyRetryPolicy.getRetryCondition().shouldRetry((AmazonWebServiceRequest)context.originalRequest(), (AmazonClientException)context.exception(), context.retriesAttempted());
    }

    public com.amazonaws.retry.RetryPolicy getLegacyRetryPolicy() {
        return this.legacyRetryPolicy;
    }

    private RetryPolicy.BackoffStrategy resolveBackoffStrategy() {
        if (this.legacyRetryPolicy.isBackoffStrategyInRetryModeHonored()) {
            return this.backoffStrategyByRetryMode();
        }
        return this.legacyRetryPolicy.getBackoffStrategy();
    }

    private RetryPolicy.BackoffStrategy backoffStrategyByRetryMode() {
        RetryMode retryMode = this.clientConfiguration.getRetryMode() == null ? this.legacyRetryPolicy.getRetryMode() : this.clientConfiguration.getRetryMode();
        return PredefinedRetryPolicies.getDefaultBackoffStrategy(retryMode);
    }

    private int resolveMaxErrorRetry() {
        if (this.legacyRetryPolicy.isMaxErrorRetryInClientConfigHonored() && this.clientConfiguration.getMaxErrorRetry() >= 0) {
            return this.clientConfiguration.getMaxErrorRetry();
        }
        Integer resolvedMaxAttempts = new MaxAttemptsResolver().maxAttempts();
        if (resolvedMaxAttempts != null) {
            return resolvedMaxAttempts - 1;
        }
        if (this.shouldUseStandardModeDefaultMaxRetry()) {
            return 2;
        }
        return this.legacyRetryPolicy.getMaxErrorRetry();
    }

    private boolean shouldUseStandardModeDefaultMaxRetry() {
        RetryMode retryMode = this.clientConfiguration.getRetryMode() == null ? this.legacyRetryPolicy.getRetryMode() : this.clientConfiguration.getRetryMode();
        return (retryMode.equals((Object)RetryMode.STANDARD) || retryMode.equals((Object)RetryMode.ADAPTIVE)) && this.legacyRetryPolicy.isDefaultMaxErrorRetryInRetryModeHonored();
    }

    public boolean maxRetriesExceeded(RetryPolicyContext context) {
        return context.retriesAttempted() >= this.maxErrorRetry;
    }

    public int getMaxErrorRetry() {
        return this.maxErrorRetry;
    }

    public RetryPolicy.BackoffStrategy getBackoffStrategy() {
        return this.backoffStrategy;
    }
}

