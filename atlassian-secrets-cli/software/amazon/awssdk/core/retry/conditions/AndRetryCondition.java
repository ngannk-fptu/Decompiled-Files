/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.retry.conditions;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class AndRetryCondition
implements RetryCondition {
    private final Set<RetryCondition> conditions = new LinkedHashSet<RetryCondition>();

    private AndRetryCondition(RetryCondition ... conditions) {
        Collections.addAll(this.conditions, Validate.notEmpty(conditions, "%s cannot be empty.", new Object[]{"conditions"}));
    }

    public static AndRetryCondition create(RetryCondition ... conditions) {
        return new AndRetryCondition(conditions);
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        return this.conditions.stream().allMatch(r -> r.shouldRetry(context));
    }

    @Override
    public void requestWillNotBeRetried(RetryPolicyContext context) {
        this.conditions.forEach(c -> c.requestWillNotBeRetried(context));
    }

    @Override
    public void requestSucceeded(RetryPolicyContext context) {
        this.conditions.forEach(c -> c.requestSucceeded(context));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AndRetryCondition that = (AndRetryCondition)o;
        return this.conditions.equals(that.conditions);
    }

    public int hashCode() {
        return this.conditions.hashCode();
    }

    public String toString() {
        return ToString.builder("AndRetryCondition").add("conditions", this.conditions).build();
    }
}

