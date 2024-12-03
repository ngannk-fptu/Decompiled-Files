/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.core.retry.conditions;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.utils.ToString;

@SdkPublicApi
public final class OrRetryCondition
implements RetryCondition {
    private final Set<RetryCondition> conditions = new LinkedHashSet<RetryCondition>();

    private OrRetryCondition(RetryCondition ... conditions) {
        Collections.addAll(this.conditions, conditions);
    }

    public static OrRetryCondition create(RetryCondition ... conditions) {
        return new OrRetryCondition(conditions);
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        return this.conditions.stream().anyMatch(r -> r.shouldRetry(context));
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
        OrRetryCondition that = (OrRetryCondition)o;
        return this.conditions.equals(that.conditions);
    }

    public int hashCode() {
        return this.conditions.hashCode();
    }

    public String toString() {
        return ToString.builder((String)"OrRetryCondition").add("conditions", this.conditions).build();
    }
}

