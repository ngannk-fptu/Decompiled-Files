/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.waiters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.internal.waiters.AsyncWaiterExecutor;
import software.amazon.awssdk.core.internal.waiters.WaiterConfiguration;
import software.amazon.awssdk.core.waiters.AsyncWaiter;
import software.amazon.awssdk.core.waiters.WaiterAcceptor;
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration;
import software.amazon.awssdk.core.waiters.WaiterResponse;

@SdkInternalApi
@ThreadSafe
public final class DefaultAsyncWaiter<T>
implements AsyncWaiter<T> {
    private final ScheduledExecutorService executorService;
    private final List<WaiterAcceptor<? super T>> waiterAcceptors;
    private final AsyncWaiterExecutor<T> handler;

    private DefaultAsyncWaiter(DefaultBuilder<T> builder) {
        this.executorService = ((DefaultBuilder)builder).scheduledExecutorService;
        WaiterConfiguration configuration = new WaiterConfiguration(((DefaultBuilder)builder).overrideConfiguration);
        this.waiterAcceptors = Collections.unmodifiableList(((DefaultBuilder)builder).waiterAcceptors);
        this.handler = new AsyncWaiterExecutor<T>(configuration, this.waiterAcceptors, this.executorService);
    }

    @Override
    public CompletableFuture<WaiterResponse<T>> runAsync(Supplier<CompletableFuture<T>> asyncPollingFunction) {
        return this.handler.execute(asyncPollingFunction);
    }

    @Override
    public CompletableFuture<WaiterResponse<T>> runAsync(Supplier<CompletableFuture<T>> asyncPollingFunction, WaiterOverrideConfiguration overrideConfig) {
        return new AsyncWaiterExecutor<T>(new WaiterConfiguration(overrideConfig), this.waiterAcceptors, this.executorService).execute(asyncPollingFunction);
    }

    public static <T> AsyncWaiter.Builder<T> builder() {
        return new DefaultBuilder();
    }

    public static final class DefaultBuilder<T>
    implements AsyncWaiter.Builder<T> {
        private List<WaiterAcceptor<? super T>> waiterAcceptors = new ArrayList<WaiterAcceptor<? super T>>();
        private ScheduledExecutorService scheduledExecutorService;
        private WaiterOverrideConfiguration overrideConfiguration;

        private DefaultBuilder() {
        }

        @Override
        public AsyncWaiter.Builder<T> scheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
            this.scheduledExecutorService = scheduledExecutorService;
            return this;
        }

        @Override
        public AsyncWaiter.Builder<T> acceptors(List<WaiterAcceptor<? super T>> waiterAcceptors) {
            this.waiterAcceptors = new ArrayList<WaiterAcceptor<T>>(waiterAcceptors);
            return this;
        }

        @Override
        public AsyncWaiter.Builder<T> overrideConfiguration(WaiterOverrideConfiguration overrideConfiguration) {
            this.overrideConfiguration = overrideConfiguration;
            return this;
        }

        @Override
        public AsyncWaiter.Builder<T> addAcceptor(WaiterAcceptor<? super T> waiterAcceptor) {
            this.waiterAcceptors.add(waiterAcceptor);
            return this;
        }

        @Override
        public DefaultAsyncWaiter<T> build() {
            return new DefaultAsyncWaiter(this);
        }
    }
}

