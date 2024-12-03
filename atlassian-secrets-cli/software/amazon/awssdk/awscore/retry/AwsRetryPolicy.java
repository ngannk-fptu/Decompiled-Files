/*
 * Decompiled with CFR 0.152.
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
        return OrRetryCondition.create(RetryCondition.defaultRetryCondition(), AwsRetryPolicy.awsRetryCondition());
    }

    public static RetryPolicy defaultRetryPolicy() {
        return AwsRetryPolicy.forRetryMode(RetryMode.defaultRetryMode());
    }

    public static RetryPolicy forRetryMode(RetryMode retryMode) {
        return AwsRetryPolicy.addRetryConditions(RetryPolicy.forRetryMode(retryMode));
    }

    public static RetryPolicy addRetryConditions(RetryPolicy condition) {
        return condition.toBuilder().retryCondition(OrRetryCondition.create(condition.retryCondition(), AwsRetryPolicy.awsRetryCondition())).build();
    }

    private static RetryOnErrorCodeCondition awsRetryCondition() {
        return RetryOnErrorCodeCondition.create(AwsErrorCode.RETRYABLE_ERROR_CODES);
    }
}

