/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.retry.conditions;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.internal.retry.SdkDefaultRetrySetting;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class MaxNumberOfRetriesCondition
implements RetryCondition {
    private final int maxNumberOfRetries;

    private MaxNumberOfRetriesCondition(int maxNumberOfRetries) {
        this.maxNumberOfRetries = Validate.isNotNegative(maxNumberOfRetries, "maxNumberOfRetries");
    }

    public static MaxNumberOfRetriesCondition create(int maxNumberOfRetries) {
        return new MaxNumberOfRetriesCondition(maxNumberOfRetries);
    }

    public static MaxNumberOfRetriesCondition forRetryMode(RetryMode retryMode) {
        return MaxNumberOfRetriesCondition.create(SdkDefaultRetrySetting.maxAttempts(retryMode));
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        return context.retriesAttempted() < this.maxNumberOfRetries;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MaxNumberOfRetriesCondition that = (MaxNumberOfRetriesCondition)o;
        return this.maxNumberOfRetries == that.maxNumberOfRetries;
    }

    public int hashCode() {
        return this.maxNumberOfRetries;
    }

    public String toString() {
        return ToString.builder("MaxNumberOfRetriesCondition").add("maxNumberOfRetries", this.maxNumberOfRetries).build();
    }
}

