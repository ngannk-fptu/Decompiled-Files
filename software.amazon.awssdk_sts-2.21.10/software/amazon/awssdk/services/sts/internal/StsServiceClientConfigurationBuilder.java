/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.client.config.AwsClientOption
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration$Builder
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.endpoints.EndpointProvider
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthScheme
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.identity.spi.IdentityProviders
 *  software.amazon.awssdk.identity.spi.IdentityProviders$Builder
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.sts.internal;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsServiceClientConfiguration;
import software.amazon.awssdk.services.sts.auth.scheme.StsAuthSchemeProvider;
import software.amazon.awssdk.services.sts.internal.SdkClientConfigurationUtil;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class StsServiceClientConfigurationBuilder {
    public static StsServiceClientConfiguration.Builder builder() {
        return new BuilderImpl();
    }

    public static BuilderInternal builder(SdkClientConfiguration.Builder builder) {
        return new BuilderImpl(builder);
    }

    public static class BuilderImpl
    implements BuilderInternal {
        private final SdkClientConfiguration.Builder internalBuilder;
        private ClientOverrideConfiguration overrideConfiguration;
        private URI endpointOverride;
        private IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider;
        private Map<String, AuthScheme<?>> authSchemes;

        private BuilderImpl() {
            this.internalBuilder = SdkClientConfiguration.builder();
        }

        private BuilderImpl(SdkClientConfiguration.Builder internalBuilder) {
            this.internalBuilder = internalBuilder;
            if (Boolean.TRUE.equals(internalBuilder.option((ClientOption)SdkClientOption.ENDPOINT_OVERRIDDEN))) {
                this.endpointOverride = (URI)internalBuilder.option((ClientOption)SdkClientOption.ENDPOINT);
            }
            this.credentialsProvider = (IdentityProvider)internalBuilder.option((ClientOption)AwsClientOption.CREDENTIALS_IDENTITY_PROVIDER);
            HashMap authSchemes = (HashMap)internalBuilder.option((ClientOption)SdkClientOption.AUTH_SCHEMES);
            if (authSchemes != null) {
                authSchemes = new HashMap(authSchemes);
            }
            this.authSchemes = authSchemes;
        }

        @Override
        public StsServiceClientConfiguration.Builder overrideConfiguration(ClientOverrideConfiguration overrideConfiguration) {
            this.overrideConfiguration = overrideConfiguration;
            return this;
        }

        @Override
        public ClientOverrideConfiguration overrideConfiguration() {
            return this.overrideConfiguration;
        }

        @Override
        public StsServiceClientConfiguration.Builder endpointOverride(URI endpointOverride) {
            this.endpointOverride = endpointOverride;
            return this;
        }

        @Override
        public URI endpointOverride() {
            return this.endpointOverride;
        }

        @Override
        public StsServiceClientConfiguration.Builder endpointProvider(EndpointProvider endpointProvider) {
            this.internalBuilder.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER, (Object)endpointProvider);
            return this;
        }

        @Override
        public EndpointProvider endpointProvider() {
            return (EndpointProvider)this.internalBuilder.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER);
        }

        @Override
        public StsServiceClientConfiguration.Builder region(Region region) {
            this.internalBuilder.option((ClientOption)AwsClientOption.AWS_REGION, (Object)region);
            return this;
        }

        @Override
        public Region region() {
            return (Region)this.internalBuilder.option((ClientOption)AwsClientOption.AWS_REGION);
        }

        @Override
        public StsServiceClientConfiguration.Builder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        @Override
        public IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider() {
            return this.credentialsProvider;
        }

        @Override
        public StsServiceClientConfiguration.Builder putAuthScheme(AuthScheme<?> authScheme) {
            if (this.authSchemes == null) {
                this.authSchemes = new HashMap();
            }
            this.authSchemes.put(authScheme.schemeId(), authScheme);
            return this;
        }

        @Override
        public Map<String, AuthScheme<?>> authSchemes() {
            if (this.authSchemes == null) {
                return Collections.emptyMap();
            }
            return Collections.unmodifiableMap(new HashMap(this.authSchemes));
        }

        @Override
        public StsServiceClientConfiguration.Builder authSchemeProvider(StsAuthSchemeProvider authSchemeProvider) {
            this.internalBuilder.option((ClientOption)SdkClientOption.AUTH_SCHEME_PROVIDER, (Object)authSchemeProvider);
            return this;
        }

        @Override
        public StsAuthSchemeProvider authSchemeProvider() {
            AuthSchemeProvider result = (AuthSchemeProvider)this.internalBuilder.option((ClientOption)SdkClientOption.AUTH_SCHEME_PROVIDER);
            if (result == null) {
                return null;
            }
            return (StsAuthSchemeProvider)Validate.isInstanceOf(StsAuthSchemeProvider.class, (Object)result, (String)("Expected an instance of " + StsAuthSchemeProvider.class.getSimpleName()), (Object[])new Object[0]);
        }

        @Override
        public StsServiceClientConfiguration build() {
            return new StsServiceClientConfiguration(this);
        }

        @Override
        public SdkClientConfiguration buildSdkClientConfiguration() {
            if (this.overrideConfiguration != null) {
                SdkClientConfigurationUtil.copyOverridesToConfiguration(this.overrideConfiguration, this.internalBuilder);
            }
            if (this.endpointOverride != null) {
                this.internalBuilder.option((ClientOption)SdkClientOption.ENDPOINT, (Object)this.endpointOverride);
                this.internalBuilder.option((ClientOption)SdkClientOption.ENDPOINT_OVERRIDDEN, (Object)true);
            }
            if (this.credentialsProvider != null && !this.credentialsProvider.equals(this.internalBuilder.option((ClientOption)AwsClientOption.CREDENTIALS_IDENTITY_PROVIDER))) {
                this.internalBuilder.option((ClientOption)AwsClientOption.CREDENTIALS_IDENTITY_PROVIDER, this.credentialsProvider);
                IdentityProviders identityProviders = (IdentityProviders)this.internalBuilder.option((ClientOption)SdkClientOption.IDENTITY_PROVIDERS);
                identityProviders = identityProviders == null ? (IdentityProviders)IdentityProviders.builder().putIdentityProvider(this.credentialsProvider).build() : (IdentityProviders)((IdentityProviders.Builder)identityProviders.toBuilder()).putIdentityProvider(this.credentialsProvider).build();
                this.internalBuilder.option((ClientOption)SdkClientOption.IDENTITY_PROVIDERS, (Object)identityProviders);
            }
            if (this.authSchemes != null && !this.authSchemes.equals(this.internalBuilder.option((ClientOption)SdkClientOption.AUTH_SCHEMES))) {
                this.internalBuilder.option((ClientOption)SdkClientOption.AUTH_SCHEMES, this.authSchemes());
            }
            return this.internalBuilder.build();
        }
    }

    public static interface BuilderInternal
    extends StsServiceClientConfiguration.Builder {
        public SdkClientConfiguration buildSdkClientConfiguration();
    }
}

