/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.retry.conditions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class RetryOnStatusCodeCondition
implements RetryCondition {
    private final Set<Integer> statusCodesToRetryOn;

    private RetryOnStatusCodeCondition(Set<Integer> statusCodesToRetryOn) {
        this.statusCodesToRetryOn = new HashSet<Integer>((Collection)Validate.paramNotNull(statusCodesToRetryOn, "statusCodesToRetryOn"));
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        return Optional.ofNullable(context.httpStatusCode()).map(s -> this.statusCodesToRetryOn.stream().anyMatch(code -> code.equals(s))).orElse(false);
    }

    public static RetryOnStatusCodeCondition create(Set<Integer> statusCodesToRetryOn) {
        return new RetryOnStatusCodeCondition(statusCodesToRetryOn);
    }

    public static RetryOnStatusCodeCondition create(Integer ... statusCodesToRetryOn) {
        return new RetryOnStatusCodeCondition(Arrays.stream(statusCodesToRetryOn).collect(Collectors.toSet()));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RetryOnStatusCodeCondition that = (RetryOnStatusCodeCondition)o;
        return this.statusCodesToRetryOn.equals(that.statusCodesToRetryOn);
    }

    public int hashCode() {
        return this.statusCodesToRetryOn.hashCode();
    }

    public String toString() {
        return ToString.builder("RetryOnStatusCodeCondition").add("statusCodesToRetryOn", this.statusCodesToRetryOn).build();
    }
}

