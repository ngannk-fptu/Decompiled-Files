/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.builder;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.builder.SdkBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public interface CopyableBuilder<B extends CopyableBuilder<B, T>, T extends ToCopyableBuilder<B, T>>
extends SdkBuilder<B, T> {
    default public B copy() {
        return ((ToCopyableBuilder)this.build()).toBuilder();
    }
}

