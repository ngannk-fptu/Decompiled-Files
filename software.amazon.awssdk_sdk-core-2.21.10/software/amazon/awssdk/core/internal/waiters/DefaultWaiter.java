/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.internal.waiters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.internal.waiters.WaiterConfiguration;
import software.amazon.awssdk.core.internal.waiters.WaiterExecutor;
import software.amazon.awssdk.core.waiters.Waiter;
import software.amazon.awssdk.core.waiters.WaiterAcceptor;
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
@ThreadSafe
public final class DefaultWaiter<T>
implements Waiter<T> {
    private final WaiterConfiguration waiterConfiguration;
    private final List<WaiterAcceptor<? super T>> waiterAcceptors;
    private final WaiterExecutor<T> waiterExecutor;

    private DefaultWaiter(DefaultBuilder<T> builder) {
        this.waiterConfiguration = new WaiterConfiguration(((DefaultBuilder)builder).overrideConfiguration);
        this.waiterAcceptors = Collections.unmodifiableList(((DefaultBuilder)builder).waiterAcceptors);
        this.waiterExecutor = new WaiterExecutor<T>(this.waiterConfiguration, this.waiterAcceptors);
    }

    @Override
    public WaiterResponse<T> run(Supplier<T> pollingFunction) {
        return this.waiterExecutor.execute(pollingFunction);
    }

    @Override
    public WaiterResponse<T> run(Supplier<T> pollingFunction, WaiterOverrideConfiguration overrideConfiguration) {
        Validate.paramNotNull((Object)overrideConfiguration, (String)"overrideConfiguration");
        return new WaiterExecutor<T>(new WaiterConfiguration(overrideConfiguration), this.waiterAcceptors).execute(pollingFunction);
    }

    public static <T> Waiter.Builder<T> builder() {
        return new DefaultBuilder();
    }

    public static final class DefaultBuilder<T>
    implements Waiter.Builder<T> {
        private List<WaiterAcceptor<? super T>> waiterAcceptors = new ArrayList<WaiterAcceptor<? super T>>();
        private WaiterOverrideConfiguration overrideConfiguration;

        private DefaultBuilder() {
        }

        @Override
        public Waiter.Builder<T> acceptors(List<WaiterAcceptor<? super T>> waiterAcceptors) {
            this.waiterAcceptors = new ArrayList<WaiterAcceptor<T>>(waiterAcceptors);
            return this;
        }

        @Override
        public Waiter.Builder<T> overrideConfiguration(WaiterOverrideConfiguration overrideConfiguration) {
            this.overrideConfiguration = overrideConfiguration;
            return this;
        }

        @Override
        public Waiter.Builder<T> addAcceptor(WaiterAcceptor<? super T> waiterAcceptor) {
            this.waiterAcceptors.add(waiterAcceptor);
            return this;
        }

        @Override
        public Waiter<T> build() {
            return new DefaultWaiter(this);
        }
    }
}

