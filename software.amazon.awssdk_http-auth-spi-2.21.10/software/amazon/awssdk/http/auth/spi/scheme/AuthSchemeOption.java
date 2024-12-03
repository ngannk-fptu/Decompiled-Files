/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.identity.spi.IdentityProperty
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.http.auth.spi.scheme;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.spi.internal.scheme.DefaultAuthSchemeOption;
import software.amazon.awssdk.http.auth.spi.signer.SignerProperty;
import software.amazon.awssdk.identity.spi.IdentityProperty;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public interface AuthSchemeOption
extends ToCopyableBuilder<Builder, AuthSchemeOption> {
    public static Builder builder() {
        return DefaultAuthSchemeOption.builder();
    }

    public String schemeId();

    public <T> T identityProperty(IdentityProperty<T> var1);

    public <T> T signerProperty(SignerProperty<T> var1);

    public void forEachIdentityProperty(IdentityPropertyConsumer var1);

    public void forEachSignerProperty(SignerPropertyConsumer var1);

    public static interface Builder
    extends CopyableBuilder<Builder, AuthSchemeOption> {
        public Builder schemeId(String var1);

        public <T> Builder putIdentityProperty(IdentityProperty<T> var1, T var2);

        public <T> Builder putIdentityPropertyIfAbsent(IdentityProperty<T> var1, T var2);

        public <T> Builder putSignerProperty(SignerProperty<T> var1, T var2);

        public <T> Builder putSignerPropertyIfAbsent(SignerProperty<T> var1, T var2);
    }

    @FunctionalInterface
    public static interface SignerPropertyConsumer {
        public <T> void accept(SignerProperty<T> var1, T var2);
    }

    @FunctionalInterface
    public static interface IdentityPropertyConsumer {
        public <T> void accept(IdentityProperty<T> var1, T var2);
    }
}

