/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core.retry.conditions;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.internal.retry.SdkDefaultRetrySetting;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.conditions.MaxNumberOfRetriesCondition;
import software.amazon.awssdk.core.retry.conditions.OrRetryCondition;
import software.amazon.awssdk.core.retry.conditions.RetryOnClockSkewCondition;
import software.amazon.awssdk.core.retry.conditions.RetryOnExceptionsCondition;
import software.amazon.awssdk.core.retry.conditions.RetryOnStatusCodeCondition;
import software.amazon.awssdk.core.retry.conditions.RetryOnThrottlingCondition;

@FunctionalInterface
@SdkPublicApi
public interface RetryCondition {
    public boolean shouldRetry(RetryPolicyContext var1);

    default public void requestWillNotBeRetried(RetryPolicyContext context) {
    }

    default public void requestSucceeded(RetryPolicyContext context) {
    }

    public static RetryCondition defaultRetryCondition() {
        return OrRetryCondition.create(RetryOnStatusCodeCondition.create(SdkDefaultRetrySetting.RETRYABLE_STATUS_CODES), RetryOnExceptionsCondition.create(SdkDefaultRetrySetting.RETRYABLE_EXCEPTIONS), RetryOnClockSkewCondition.create(), RetryOnThrottlingCondition.create());
    }

    public static RetryCondition none() {
        return MaxNumberOfRetriesCondition.create(0);
    }
}

