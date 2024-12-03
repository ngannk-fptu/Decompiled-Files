/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.SdkServiceClientConfiguration
 *  software.amazon.awssdk.core.SdkServiceClientConfiguration$Builder
 *  software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
 *  software.amazon.awssdk.endpoints.EndpointProvider
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthScheme
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.regions.Region
 */
package software.amazon.awssdk.awscore;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkServiceClientConfiguration;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.regions.Region;

@SdkPublicApi
public abstract class AwsServiceClientConfiguration
extends SdkServiceClientConfiguration {
    private final Region region;
    private final IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider;

    protected AwsServiceClientConfiguration(Builder builder) {
        super((SdkServiceClientConfiguration.Builder)builder);
        this.region = builder.region();
        this.credentialsProvider = builder.credentialsProvider();
    }

    public Region region() {
        return this.region;
    }

    public IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider() {
        return this.credentialsProvider;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + super.hashCode();
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        result = 31 * result + (this.credentialsProvider != null ? this.credentialsProvider.hashCode() : 0);
        return result;
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        AwsServiceClientConfiguration that = (AwsServiceClientConfiguration)((Object)o);
        return Objects.equals(this.region, that.region) && Objects.equals(this.credentialsProvider, that.credentialsProvider);
    }

    protected static abstract class BuilderImpl
    implements Builder {
        protected ClientOverrideConfiguration overrideConfiguration;
        protected Region region;
        protected URI endpointOverride;
        protected EndpointProvider endpointProvider;
        protected IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider;
        protected Map<String, AuthScheme<?>> authSchemes;

        protected BuilderImpl() {
        }

        protected BuilderImpl(AwsServiceClientConfiguration awsServiceClientConfiguration) {
            this.overrideConfiguration = awsServiceClientConfiguration.overrideConfiguration();
            this.region = awsServiceClientConfiguration.region();
            this.endpointOverride = awsServiceClientConfiguration.endpointOverride().orElse(null);
            this.endpointProvider = awsServiceClientConfiguration.endpointProvider().orElse(null);
        }

        public final ClientOverrideConfiguration overrideConfiguration() {
            return this.overrideConfiguration;
        }

        @Override
        public final Region region() {
            return this.region;
        }

        public final URI endpointOverride() {
            return this.endpointOverride;
        }

        public final EndpointProvider endpointProvider() {
            return this.endpointProvider;
        }

        @Override
        public final Builder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        @Override
        public final IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider() {
            return this.credentialsProvider;
        }

        @Override
        public final Builder putAuthScheme(AuthScheme<?> authScheme) {
            if (this.authSchemes == null) {
                this.authSchemes = new HashMap();
            }
            this.authSchemes.put(authScheme.schemeId(), authScheme);
            return this;
        }

        public final Map<String, AuthScheme<?>> authSchemes() {
            if (this.authSchemes == null) {
                return Collections.emptyMap();
            }
            return Collections.unmodifiableMap(new HashMap(this.authSchemes));
        }
    }

    public static interface Builder
    extends SdkServiceClientConfiguration.Builder {
        default public Region region() {
            throw new UnsupportedOperationException();
        }

        default public Builder region(Region region) {
            throw new UnsupportedOperationException();
        }

        default public Builder overrideConfiguration(ClientOverrideConfiguration clientOverrideConfiguration) {
            throw new UnsupportedOperationException();
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

        default public Builder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
            throw new UnsupportedOperationException();
        }

        default public IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider() {
            throw new UnsupportedOperationException();
        }

        public AwsServiceClientConfiguration build();
    }
}

