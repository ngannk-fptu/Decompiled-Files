/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core.waiters;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.internal.waiters.DefaultAsyncWaiter;
import software.amazon.awssdk.core.waiters.WaiterBuilder;
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration;
import software.amazon.awssdk.core.waiters.WaiterResponse;

@SdkPublicApi
public interface AsyncWaiter<T> {
    default public CompletableFuture<WaiterResponse<T>> runAsync(Supplier<CompletableFuture<T>> asyncPollingFunction) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<WaiterResponse<T>> runAsync(Supplier<CompletableFuture<T>> asyncPollingFunction, WaiterOverrideConfiguration overrideConfig) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<WaiterResponse<T>> runAsync(Supplier<CompletableFuture<T>> asyncPollingFunction, Consumer<WaiterOverrideConfiguration.Builder> overrideConfig) {
        return this.runAsync(asyncPollingFunction, ((WaiterOverrideConfiguration.Builder)WaiterOverrideConfiguration.builder().applyMutation(overrideConfig)).build());
    }

    public static <T> Builder<T> builder(Class<? extends T> responseClass) {
        return DefaultAsyncWaiter.builder();
    }

    public static interface Builder<T>
    extends WaiterBuilder<T, Builder<T>> {
        public Builder<T> scheduledExecutorService(ScheduledExecutorService var1);

        public AsyncWaiter<T> build();
    }
}

