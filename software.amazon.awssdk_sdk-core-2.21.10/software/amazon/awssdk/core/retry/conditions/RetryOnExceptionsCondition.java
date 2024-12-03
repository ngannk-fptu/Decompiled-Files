/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.core.retry.conditions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.utils.ToString;

@SdkPublicApi
public final class RetryOnExceptionsCondition
implements RetryCondition {
    private final Set<Class<? extends Exception>> exceptionsToRetryOn;

    private RetryOnExceptionsCondition(Set<Class<? extends Exception>> exceptionsToRetryOn) {
        this.exceptionsToRetryOn = new HashSet<Class<? extends Exception>>(exceptionsToRetryOn);
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        SdkException exception = context.exception();
        if (exception == null) {
            return false;
        }
        Predicate<Class> isRetryableException = ex -> ex.isAssignableFrom(exception.getClass());
        Predicate<Class> hasRetryableCause = ex -> exception.getCause() != null && ex.isAssignableFrom(exception.getCause().getClass());
        return this.exceptionsToRetryOn.stream().anyMatch(isRetryableException.or(hasRetryableCause));
    }

    public static RetryOnExceptionsCondition create(Set<Class<? extends Exception>> exceptionsToRetryOn) {
        return new RetryOnExceptionsCondition(exceptionsToRetryOn);
    }

    public static RetryOnExceptionsCondition create(Class<? extends Exception> ... exceptionsToRetryOn) {
        return new RetryOnExceptionsCondition(Arrays.stream(exceptionsToRetryOn).collect(Collectors.toSet()));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RetryOnExceptionsCondition that = (RetryOnExceptionsCondition)o;
        return this.exceptionsToRetryOn.equals(that.exceptionsToRetryOn);
    }

    public int hashCode() {
        return this.exceptionsToRetryOn.hashCode();
    }

    public String toString() {
        return ToString.builder((String)"RetryOnExceptionsCondition").add("exceptionsToRetryOn", this.exceptionsToRetryOn).build();
    }
}

