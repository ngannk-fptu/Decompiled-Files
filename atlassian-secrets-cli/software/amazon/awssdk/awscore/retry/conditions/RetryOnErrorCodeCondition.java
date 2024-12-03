/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.retry.conditions;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;

@SdkPublicApi
public final class RetryOnErrorCodeCondition
implements RetryCondition {
    private final Set<String> retryableErrorCodes;

    private RetryOnErrorCodeCondition(Set<String> retryableErrorCodes) {
        this.retryableErrorCodes = retryableErrorCodes;
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        SdkException ex = context.exception();
        if (ex instanceof AwsServiceException) {
            AwsServiceException exception = (AwsServiceException)ex;
            return this.retryableErrorCodes.contains(exception.awsErrorDetails().errorCode());
        }
        return false;
    }

    public static RetryOnErrorCodeCondition create(String ... retryableErrorCodes) {
        return new RetryOnErrorCodeCondition(Arrays.stream(retryableErrorCodes).collect(Collectors.toSet()));
    }

    public static RetryOnErrorCodeCondition create(Set<String> retryableErrorCodes) {
        return new RetryOnErrorCodeCondition(retryableErrorCodes);
    }
}

