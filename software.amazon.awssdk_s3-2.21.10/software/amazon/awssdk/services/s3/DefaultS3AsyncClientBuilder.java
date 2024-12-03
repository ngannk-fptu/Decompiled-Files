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
import software.amazon.awssdk.services.s3.DefaultS3AsyncClient;
import software.amazon.awssdk.services.s3.DefaultS3BaseClientBuilder;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3ServiceClientConfiguration;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;
import software.amazon.awssdk.services.s3.internal.client.S3AsyncClientDecorator;
import software.amazon.awssdk.services.s3.multipart.MultipartConfiguration;
import software.amazon.awssdk.utils.AttributeMap;

@SdkInternalApi
final class DefaultS3AsyncClientBuilder
extends DefaultS3BaseClientBuilder<S3AsyncClientBuilder, S3AsyncClient>
implements S3AsyncClientBuilder {
    DefaultS3AsyncClientBuilder() {
    }

    @Override
    public DefaultS3AsyncClientBuilder endpointProvider(S3EndpointProvider endpointProvider) {
        this.clientConfiguration.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER, (Object)endpointProvider);
        return this;
    }

    @Override
    public S3AsyncClientBuilder multipartEnabled(Boolean enabled) {
        this.clientContextParams.put(S3AsyncClientDecorator.MULTIPART_ENABLED_KEY, (Object)enabled);
        return this;
    }

    @Override
    public S3AsyncClientBuilder multipartConfiguration(MultipartConfiguration multipartConfig) {
        this.clientContextParams.put(S3AsyncClientDecorator.MULTIPART_CONFIGURATION_KEY, (Object)multipartConfig);
        return this;
    }

    protected final S3AsyncClient buildClient() {
        SdkClientConfiguration clientConfiguration = super.asyncClientConfiguration();
        DefaultS3AsyncClientBuilder.validateClientOptions(clientConfiguration);
        S3ServiceClientConfiguration serviceClientConfiguration = this.initializeServiceClientConfig(clientConfiguration);
        DefaultS3AsyncClient client = new DefaultS3AsyncClient(serviceClientConfiguration, clientConfiguration);
        return new S3AsyncClientDecorator().decorate(client, clientConfiguration, ((AttributeMap.Builder)this.clientContextParams.copy()).build());
    }

    private S3ServiceClientConfiguration initializeServiceClientConfig(SdkClientConfiguration clientConfig) {
        URI endpointOverride = null;
        if (Boolean.TRUE.equals(clientConfig.option((ClientOption)SdkClientOption.ENDPOINT_OVERRIDDEN))) {
            endpointOverride = (URI)clientConfig.option((ClientOption)SdkClientOption.ENDPOINT);
        }
        return S3ServiceClientConfiguration.builder().overrideConfiguration(this.overrideConfiguration()).region((Region)clientConfig.option((ClientOption)AwsClientOption.AWS_REGION)).endpointOverride(endpointOverride).endpointProvider((EndpointProvider)clientConfig.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER)).build();
    }
}

