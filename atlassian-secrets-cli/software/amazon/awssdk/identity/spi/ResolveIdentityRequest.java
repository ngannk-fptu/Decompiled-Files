/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.identity.spi;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.identity.spi.IdentityProperty;
import software.amazon.awssdk.identity.spi.internal.DefaultResolveIdentityRequest;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public interface ResolveIdentityRequest
extends ToCopyableBuilder<Builder, ResolveIdentityRequest> {
    public static Builder builder() {
        return DefaultResolveIdentityRequest.builder();
    }

    public <T> T property(IdentityProperty<T> var1);

    public static interface Builder
    extends CopyableBuilder<Builder, ResolveIdentityRequest> {
        public <T> Builder putProperty(IdentityProperty<T> var1, T var2);
    }
}

