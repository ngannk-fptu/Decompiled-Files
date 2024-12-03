/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.identity.spi;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.internal.DefaultIdentityProviders;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public interface IdentityProviders
extends ToCopyableBuilder<Builder, IdentityProviders> {
    public <T extends Identity> IdentityProvider<T> identityProvider(Class<T> var1);

    public static Builder builder() {
        return DefaultIdentityProviders.builder();
    }

    public static interface Builder
    extends CopyableBuilder<Builder, IdentityProviders> {
        public <T extends Identity> Builder putIdentityProvider(IdentityProvider<T> var1);
    }
}

