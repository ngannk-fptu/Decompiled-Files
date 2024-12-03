/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;

@SdkPublicApi
public abstract class SdkServiceClientConfiguration {
    private final ClientOverrideConfiguration overrideConfiguration;
    private final URI endpointOverride;
    private final EndpointProvider endpointProvider;
    private final Map<String, AuthScheme<?>> authSchemes;

    protected SdkServiceClientConfiguration(Builder builder) {
        this.overrideConfiguration = builder.overrideConfiguration();
        this.endpointOverride = builder.endpointOverride();
        this.endpointProvider = builder.endpointProvider();
        this.authSchemes = builder.authSchemes();
    }

    public ClientOverrideConfiguration overrideConfiguration() {
        return this.overrideConfiguration;
    }

    public Optional<URI> endpointOverride() {
        return Optional.ofNullable(this.endpointOverride);
    }

    public Optional<EndpointProvider> endpointProvider() {
        return Optional.ofNullable(this.endpointProvider);
    }

    public Map<String, AuthScheme<?>> authSchemes() {
        return this.authSchemes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SdkServiceClientConfiguration serviceClientConfiguration = (SdkServiceClientConfiguration)o;
        return Objects.equals(this.overrideConfiguration, serviceClientConfiguration.overrideConfiguration()) && Objects.equals(this.endpointOverride, serviceClientConfiguration.endpointOverride().orElse(null)) && Objects.equals(this.endpointProvider, serviceClientConfiguration.endpointProvider().orElse(null)) && Objects.equals(this.authSchemes, serviceClientConfiguration.authSchemes);
    }

    public int hashCode() {
        int result = this.overrideConfiguration != null ? this.overrideConfiguration.hashCode() : 0;
        result = 31 * result + (this.endpointOverride != null ? this.endpointOverride.hashCode() : 0);
        result = 31 * result + (this.endpointProvider != null ? this.endpointProvider.hashCode() : 0);
        result = 31 * result + (this.authSchemes != null ? this.authSchemes.hashCode() : 0);
        return result;
    }

    public static interface Builder {
        default public ClientOverrideConfiguration overrideConfiguration() {
            throw new UnsupportedOperationException();
        }

        default public URI endpointOverride() {
            throw new UnsupportedOperationException();
        }

        default public EndpointProvider endpointProvider() {
            throw new UnsupportedOperationException();
        }

        default public Builder overrideConfiguration(ClientOverrideConfiguration clientOverrideConfiguration) {
            throw new UnsupportedOperationException();
        }

        default public Builder overrideConfiguration(Consumer<ClientOverrideConfiguration.Builder> consumer) {
            ClientOverrideConfiguration overrideConfiguration = this.overrideConfiguration();
            ClientOverrideConfiguration.Builder builder = overrideConfiguration != null ? overrideConfiguration.toBuilder() : ClientOverrideConfiguration.builder();
            consumer.accept(builder);
            return this.overrideConfiguration((ClientOverrideConfiguration)builder.build());
        }

        default public Builder endpointOverride(URI endpointOverride) {
            throw new UnsupportedOperationException();
        }

        default public Builder endpointProvider(EndpointProvider endpointProvider) {
            throw new UnsupportedOperationException();
        }

        default public Builder putAuthScheme(AuthScheme<?> authScheme) {
            throw new UnsupportedOperationException();
        }

        default public Map<String, AuthScheme<?>> authSchemes() {
            throw new UnsupportedOperationException();
        }

        public SdkServiceClientConfiguration build();
    }
}

