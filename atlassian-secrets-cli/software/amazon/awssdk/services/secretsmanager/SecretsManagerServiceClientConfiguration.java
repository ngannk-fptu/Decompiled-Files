/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager;

import java.net.URI;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.AwsServiceClientConfiguration;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.auth.scheme.SecretsManagerAuthSchemeProvider;
import software.amazon.awssdk.services.secretsmanager.internal.SecretsManagerServiceClientConfigurationBuilder;

@SdkPublicApi
public final class SecretsManagerServiceClientConfiguration
extends AwsServiceClientConfiguration {
    private final SecretsManagerAuthSchemeProvider authSchemeProvider;

    public SecretsManagerServiceClientConfiguration(Builder builder) {
        super(builder);
        this.authSchemeProvider = builder.authSchemeProvider();
    }

    public SecretsManagerAuthSchemeProvider authSchemeProvider() {
        return this.authSchemeProvider;
    }

    public static Builder builder() {
        return SecretsManagerServiceClientConfigurationBuilder.builder();
    }

    public static interface Builder
    extends AwsServiceClientConfiguration.Builder {
        @Override
        public Builder overrideConfiguration(ClientOverrideConfiguration var1);

        @Override
        public ClientOverrideConfiguration overrideConfiguration();

        @Override
        public Builder endpointOverride(URI var1);

        @Override
        public URI endpointOverride();

        @Override
        public Builder endpointProvider(EndpointProvider var1);

        @Override
        public EndpointProvider endpointProvider();

        @Override
        public Builder region(Region var1);

        @Override
        public Region region();

        @Override
        public Builder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> var1);

        @Override
        public IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider();

        @Override
        public Builder putAuthScheme(AuthScheme<?> var1);

        @Override
        public Map<String, AuthScheme<?>> authSchemes();

        public Builder authSchemeProvider(SecretsManagerAuthSchemeProvider var1);

        public SecretsManagerAuthSchemeProvider authSchemeProvider();

        @Override
        public SecretsManagerServiceClientConfiguration build();
    }
}

