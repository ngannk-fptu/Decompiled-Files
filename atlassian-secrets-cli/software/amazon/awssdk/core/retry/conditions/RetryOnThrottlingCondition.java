/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.retry.conditions;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.RetryUtils;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.utils.ToString;

@SdkPublicApi
public final class RetryOnThrottlingCondition
implements RetryCondition {
    private RetryOnThrottlingCondition() {
    }

    public static RetryOnThrottlingCondition create() {
        return new RetryOnThrottlingCondition();
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        return RetryUtils.isThrottlingException(context.exception());
    }

    public String toString() {
        return ToString.create("RetryOnThrottlingCondition");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && this.getClass() == o.getClass();
    }

    public int hashCode() {
        return 0;
    }
}

