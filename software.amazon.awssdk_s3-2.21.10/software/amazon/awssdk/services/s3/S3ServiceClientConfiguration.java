/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.awscore.AwsServiceClientConfiguration
 *  software.amazon.awssdk.awscore.AwsServiceClientConfiguration$Builder
 *  software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
 *  software.amazon.awssdk.endpoints.EndpointProvider
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthScheme
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.regions.Region
 */
package software.amazon.awssdk.services.s3;

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
import software.amazon.awssdk.services.s3.auth.scheme.S3AuthSchemeProvider;
import software.amazon.awssdk.services.s3.internal.S3ServiceClientConfigurationBuilder;

@SdkPublicApi
public final class S3ServiceClientConfiguration
extends AwsServiceClientConfiguration {
    private final S3AuthSchemeProvider authSchemeProvider;

    public S3ServiceClientConfiguration(Builder builder) {
        super((AwsServiceClientConfiguration.Builder)builder);
        this.authSchemeProvider = builder.authSchemeProvider();
    }

    public S3AuthSchemeProvider authSchemeProvider() {
        return this.authSchemeProvider;
    }

    public static Builder builder() {
        return S3ServiceClientConfigurationBuilder.builder();
    }

    public static interface Builder
    extends AwsServiceClientConfiguration.Builder {
        public Builder overrideConfiguration(ClientOverrideConfiguration var1);

        public ClientOverrideConfiguration overrideConfiguration();

        public Builder endpointOverride(URI var1);

        public URI endpointOverride();

        public Builder endpointProvider(EndpointProvider var1);

        public EndpointProvider endpointProvider();

        public Builder region(Region var1);

        public Region region();

        public Builder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> var1);

        public IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider();

        public Builder putAuthScheme(AuthScheme<?> var1);

        public Map<String, AuthScheme<?>> authSchemes();

        public Builder authSchemeProvider(S3AuthSchemeProvider var1);

        public S3AuthSchemeProvider authSchemeProvider();

        public S3ServiceClientConfiguration build();
    }
}

