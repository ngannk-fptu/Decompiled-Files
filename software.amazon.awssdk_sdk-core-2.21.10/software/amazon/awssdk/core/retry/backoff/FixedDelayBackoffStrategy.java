/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.retry.backoff;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class FixedDelayBackoffStrategy
implements BackoffStrategy {
    private final Duration fixedBackoff;

    private FixedDelayBackoffStrategy(Duration fixedBackoff) {
        this.fixedBackoff = Validate.isNotNegative((Duration)fixedBackoff, (String)"fixedBackoff");
    }

    @Override
    public Duration computeDelayBeforeNextRetry(RetryPolicyContext context) {
        return this.fixedBackoff;
    }

    public static FixedDelayBackoffStrategy create(Duration fixedBackoff) {
        return new FixedDelayBackoffStrategy(fixedBackoff);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FixedDelayBackoffStrategy that = (FixedDelayBackoffStrategy)o;
        return this.fixedBackoff.equals(that.fixedBackoff);
    }

    public int hashCode() {
        return this.fixedBackoff.hashCode();
    }

    public String toString() {
        return ToString.builder((String)"FixedDelayBackoffStrategy").add("fixedBackoff", (Object)this.fixedBackoff).build();
    }
}

