/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPreviewApi
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthScheme
 *  software.amazon.awssdk.utils.builder.SdkBuilder
 */
package software.amazon.awssdk.core.client.builder;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPreviewApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@SdkPublicApi
public interface SdkClientBuilder<B extends SdkClientBuilder<B, C>, C>
extends SdkBuilder<B, C> {
    public B overrideConfiguration(ClientOverrideConfiguration var1);

    default public B overrideConfiguration(Consumer<ClientOverrideConfiguration.Builder> overrideConfiguration) {
        return this.overrideConfiguration((ClientOverrideConfiguration)((ClientOverrideConfiguration.Builder)ClientOverrideConfiguration.builder().applyMutation(overrideConfiguration)).build());
    }

    public ClientOverrideConfiguration overrideConfiguration();

    public B endpointOverride(URI var1);

    default public B putAuthScheme(AuthScheme<?> authScheme) {
        throw new UnsupportedOperationException();
    }

    @SdkPreviewApi
    default public B addPlugin(SdkPlugin plugin) {
        throw new UnsupportedOperationException();
    }

    @SdkPreviewApi
    default public List<SdkPlugin> plugins() {
        throw new UnsupportedOperationException();
    }
}

