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
public final class RetryOnClockSkewCondition
implements RetryCondition {
    private RetryOnClockSkewCondition() {
    }

    public static RetryOnClockSkewCondition create() {
        return new RetryOnClockSkewCondition();
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        return RetryUtils.isClockSkewException(context.exception());
    }

    public String toString() {
        return ToString.create("RetryOnClockSkewCondition");
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

