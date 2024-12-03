/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.retry.conditions;

import java.util.function.Function;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.internal.retry.DefaultTokenBucketExceptionCostFunction;

@FunctionalInterface
@SdkPublicApi
@ThreadSafe
public interface TokenBucketExceptionCostFunction
extends Function<SdkException, Integer> {
    public static Builder builder() {
        return new DefaultTokenBucketExceptionCostFunction.Builder();
    }

    @NotThreadSafe
    public static interface Builder {
        public Builder throttlingExceptionCost(int var1);

        public Builder defaultExceptionCost(int var1);

        public TokenBucketExceptionCostFunction build();
    }
}

