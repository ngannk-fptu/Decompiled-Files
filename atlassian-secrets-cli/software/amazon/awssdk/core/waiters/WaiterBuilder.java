/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.waiters;

import java.util.List;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.waiters.WaiterAcceptor;
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration;

@SdkPublicApi
public interface WaiterBuilder<T, B> {
    public B acceptors(List<WaiterAcceptor<? super T>> var1);

    public B addAcceptor(WaiterAcceptor<? super T> var1);

    public B overrideConfiguration(WaiterOverrideConfiguration var1);

    default public B overrideConfiguration(Consumer<WaiterOverrideConfiguration.Builder> overrideConfiguration) {
        WaiterOverrideConfiguration.Builder builder = WaiterOverrideConfiguration.builder();
        overrideConfiguration.accept(builder);
        return this.overrideConfiguration(builder.build());
    }
}

