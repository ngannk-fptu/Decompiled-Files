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
 */
package software.amazon.awssdk.services.sts;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.DefaultStsBaseClientBuilder;
import software.amazon.awssdk.services.sts.DefaultStsClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.StsClientBuilder;
import software.amazon.awssdk.services.sts.StsServiceClientConfiguration;
import software.amazon.awssdk.services.sts.endpoints.StsEndpointProvider;

@SdkInternalApi
final class DefaultStsClientBuilder
extends DefaultStsBaseClientBuilder<StsClientBuilder, StsClient>
implements StsClientBuilder {
    DefaultStsClientBuilder() {
    }

    @Override
    public DefaultStsClientBuilder endpointProvider(StsEndpointProvider endpointProvider) {
        this.clientConfiguration.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER, (Object)endpointProvider);
        return this;
    }

    protected final StsClient buildClient() {
        SdkClientConfiguration clientConfiguration = super.syncClientConfiguration();
        DefaultStsClientBuilder.validateClientOptions(clientConfiguration);
        StsServiceClientConfiguration serviceClientConfiguration = this.initializeServiceClientConfig(clientConfiguration);
        DefaultStsClient client = new DefaultStsClient(serviceClientConfiguration, clientConfiguration);
        return client;
    }

    private StsServiceClientConfiguration initializeServiceClientConfig(SdkClientConfiguration clientConfig) {
        URI endpointOverride = null;
        if (Boolean.TRUE.equals(clientConfig.option((ClientOption)SdkClientOption.ENDPOINT_OVERRIDDEN))) {
            endpointOverride = (URI)clientConfig.option((ClientOption)SdkClientOption.ENDPOINT);
        }
        return StsServiceClientConfiguration.builder().overrideConfiguration(this.overrideConfiguration()).region((Region)clientConfig.option((ClientOption)AwsClientOption.AWS_REGION)).endpointOverride(endpointOverride).endpointProvider((EndpointProvider)clientConfig.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER)).build();
    }
}

