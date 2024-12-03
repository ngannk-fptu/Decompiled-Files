/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.SdkBuilder
 */
package software.amazon.awssdk.services.s3;

import java.net.URI;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.crt.S3CrtHttpConfiguration;
import software.amazon.awssdk.services.s3.crt.S3CrtRetryConfiguration;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@SdkPublicApi
public interface S3CrtAsyncClientBuilder
extends SdkBuilder<S3CrtAsyncClientBuilder, S3AsyncClient> {
    default public S3CrtAsyncClientBuilder credentialsProvider(AwsCredentialsProvider credentialsProvider) {
        return this.credentialsProvider((IdentityProvider<? extends AwsCredentialsIdentity>)credentialsProvider);
    }

    default public S3CrtAsyncClientBuilder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
        throw new UnsupportedOperationException();
    }

    public S3CrtAsyncClientBuilder region(Region var1);

    public S3CrtAsyncClientBuilder minimumPartSizeInBytes(Long var1);

    public S3CrtAsyncClientBuilder targetThroughputInGbps(Double var1);

    public S3CrtAsyncClientBuilder maxConcurrency(Integer var1);

    public S3CrtAsyncClientBuilder endpointOverride(URI var1);

    public S3CrtAsyncClientBuilder checksumValidationEnabled(Boolean var1);

    public S3CrtAsyncClientBuilder initialReadBufferSizeInBytes(Long var1);

    public S3CrtAsyncClientBuilder httpConfiguration(S3CrtHttpConfiguration var1);

    public S3CrtAsyncClientBuilder retryConfiguration(S3CrtRetryConfiguration var1);

    default public S3CrtAsyncClientBuilder httpConfiguration(Consumer<S3CrtHttpConfiguration.Builder> configurationBuilder) {
        Validate.paramNotNull(configurationBuilder, (String)"configurationBuilder");
        return this.httpConfiguration(((S3CrtHttpConfiguration.Builder)S3CrtHttpConfiguration.builder().applyMutation(configurationBuilder)).build());
    }

    public S3CrtAsyncClientBuilder accelerate(Boolean var1);

    public S3CrtAsyncClientBuilder forcePathStyle(Boolean var1);

    default public S3CrtAsyncClientBuilder retryConfiguration(Consumer<S3CrtRetryConfiguration.Builder> retryConfigurationBuilder) {
        Validate.paramNotNull(retryConfigurationBuilder, (String)"retryConfigurationBuilder");
        return this.retryConfiguration((S3CrtRetryConfiguration)((S3CrtRetryConfiguration.Builder)S3CrtRetryConfiguration.builder().applyMutation(retryConfigurationBuilder)).build());
    }

    public S3CrtAsyncClientBuilder crossRegionAccessEnabled(Boolean var1);

    public S3CrtAsyncClientBuilder thresholdInBytes(Long var1);

    public S3AsyncClient build();
}

