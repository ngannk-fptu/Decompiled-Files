/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.builder;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.builder.Buildable;

@SdkPublicApi
public interface SdkBuilder<B extends SdkBuilder<B, T>, T>
extends Buildable {
    public T build();

    default public B applyMutation(Consumer<B> mutator) {
        mutator.accept(this);
        return (B)this;
    }
}

