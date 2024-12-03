/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.retry.RetryMode
 *  software.amazon.awssdk.core.retry.RetryPolicy
 *  software.amazon.awssdk.core.retry.conditions.OrRetryCondition
 *  software.amazon.awssdk.core.retry.conditions.RetryCondition
 */
package software.amazon.awssdk.awscore.retry;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.internal.AwsErrorCode;
import software.amazon.awssdk.awscore.retry.conditions.RetryOnErrorCodeCondition;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.conditions.OrRetryCondition;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;

@SdkPublicApi
public final class AwsRetryPolicy {
    private AwsRetryPolicy() {
    }

    public static RetryCondition defaultRetryCondition() {
        return OrRetryCondition.create((RetryCondition[])new RetryCondition[]{RetryCondition.defaultRetryCondition(), AwsRetryPolicy.awsRetryCondition()});
    }

    public static RetryPolicy defaultRetryPolicy() {
        return AwsRetryPolicy.forRetryMode(RetryMode.defaultRetryMode());
    }

    public static RetryPolicy forRetryMode(RetryMode retryMode) {
        return AwsRetryPolicy.addRetryConditions(RetryPolicy.forRetryMode((RetryMode)retryMode));
    }

    public static RetryPolicy addRetryConditions(RetryPolicy condition) {
        return condition.toBuilder().retryCondition((RetryCondition)OrRetryCondition.create((RetryCondition[])new RetryCondition[]{condition.retryCondition(), AwsRetryPolicy.awsRetryCondition()})).build();
    }

    private static RetryOnErrorCodeCondition awsRetryCondition() {
        return RetryOnErrorCodeCondition.create(AwsErrorCode.RETRYABLE_ERROR_CODES);
    }
}

