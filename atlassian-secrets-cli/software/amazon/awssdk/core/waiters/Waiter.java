/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.waiters;

import java.util.function.Consumer;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.internal.waiters.DefaultWaiter;
import software.amazon.awssdk.core.waiters.WaiterBuilder;
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration;
import software.amazon.awssdk.core.waiters.WaiterResponse;

@SdkPublicApi
public interface Waiter<T> {
    default public WaiterResponse<T> run(Supplier<T> pollingFunction) {
        throw new UnsupportedOperationException();
    }

    default public WaiterResponse<T> run(Supplier<T> pollingFunction, WaiterOverrideConfiguration overrideConfig) {
        throw new UnsupportedOperationException();
    }

    default public WaiterResponse<T> run(Supplier<T> pollingFunction, Consumer<WaiterOverrideConfiguration.Builder> overrideConfig) {
        return this.run(pollingFunction, WaiterOverrideConfiguration.builder().applyMutation(overrideConfig).build());
    }

    public static <T> Builder<T> builder(Class<? extends T> responseClass) {
        return DefaultWaiter.builder();
    }

    public static interface Builder<T>
    extends WaiterBuilder<T, Builder<T>> {
        public Waiter<T> build();
    }
}

