/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.client.config.AwsClientOption
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.endpoints.EndpointProvider
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 */
package software.amazon.awssdk.services.s3;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.DefaultS3BaseClientBuilder;
import software.amazon.awssdk.services.s3.DefaultS3Client;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3ServiceClientConfiguration;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;
import software.amazon.awssdk.services.s3.internal.client.S3SyncClientDecorator;
import software.amazon.awssdk.utils.AttributeMap;

@SdkInternalApi
final class DefaultS3ClientBuilder
extends DefaultS3BaseClientBuilder<S3ClientBuilder, S3Client>
implements S3ClientBuilder {
    DefaultS3ClientBuilder() {
    }

    @Override
    public DefaultS3ClientBuilder endpointProvider(S3EndpointProvider endpointProvider) {
        this.clientConfiguration.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER, (Object)endpointProvider);
        return this;
    }

    protected final S3Client buildClient() {
        SdkClientConfiguration clientConfiguration = super.syncClientConfiguration();
        DefaultS3ClientBuilder.validateClientOptions(clientConfiguration);
        S3ServiceClientConfiguration serviceClientConfiguration = this.initializeServiceClientConfig(clientConfiguration);
        DefaultS3Client client = new DefaultS3Client(serviceClientConfiguration, clientConfiguration);
        return new S3SyncClientDecorator().decorate(client, clientConfiguration, ((AttributeMap.Builder)this.clientContextParams.copy()).build());
    }

    private S3ServiceClientConfiguration initializeServiceClientConfig(SdkClientConfiguration clientConfig) {
        URI endpointOverride = null;
        if (Boolean.TRUE.equals(clientConfig.option((ClientOption)SdkClientOption.ENDPOINT_OVERRIDDEN))) {
            endpointOverride = (URI)clientConfig.option((ClientOption)SdkClientOption.ENDPOINT);
        }
        return S3ServiceClientConfiguration.builder().overrideConfiguration(this.overrideConfiguration()).region((Region)clientConfig.option((ClientOption)AwsClientOption.AWS_REGION)).endpointOverride(endpointOverride).endpointProvider((EndpointProvider)clientConfig.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER)).build();
    }
}

