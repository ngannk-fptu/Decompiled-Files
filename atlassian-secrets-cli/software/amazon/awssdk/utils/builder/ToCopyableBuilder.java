/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.builder;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.builder.CopyableBuilder;

@SdkPublicApi
public interface ToCopyableBuilder<B extends CopyableBuilder<B, T>, T extends ToCopyableBuilder<B, T>> {
    public B toBuilder();

    default public T copy(Consumer<? super B> modifier) {
        return (T)((ToCopyableBuilder)this.toBuilder().applyMutation(modifier::accept).build());
    }
}

