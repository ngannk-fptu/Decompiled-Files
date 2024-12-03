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
package software.amazon.awssdk.services.secretsmanager;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.DefaultSecretsManagerBaseClientBuilder;
import software.amazon.awssdk.services.secretsmanager.DefaultSecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerServiceClientConfiguration;
import software.amazon.awssdk.services.secretsmanager.endpoints.SecretsManagerEndpointProvider;

@SdkInternalApi
final class DefaultSecretsManagerClientBuilder
extends DefaultSecretsManagerBaseClientBuilder<SecretsManagerClientBuilder, SecretsManagerClient>
implements SecretsManagerClientBuilder {
    DefaultSecretsManagerClientBuilder() {
    }

    @Override
    public DefaultSecretsManagerClientBuilder endpointProvider(SecretsManagerEndpointProvider endpointProvider) {
        this.clientConfiguration.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER, (Object)endpointProvider);
        return this;
    }

    protected final SecretsManagerClient buildClient() {
        SdkClientConfiguration clientConfiguration = super.syncClientConfiguration();
        DefaultSecretsManagerClientBuilder.validateClientOptions(clientConfiguration);
        SecretsManagerServiceClientConfiguration serviceClientConfiguration = this.initializeServiceClientConfig(clientConfiguration);
        DefaultSecretsManagerClient client = new DefaultSecretsManagerClient(serviceClientConfiguration, clientConfiguration);
        return client;
    }

    private SecretsManagerServiceClientConfiguration initializeServiceClientConfig(SdkClientConfiguration clientConfig) {
        URI endpointOverride = null;
        if (Boolean.TRUE.equals(clientConfig.option((ClientOption)SdkClientOption.ENDPOINT_OVERRIDDEN))) {
            endpointOverride = (URI)clientConfig.option((ClientOption)SdkClientOption.ENDPOINT);
        }
        return SecretsManagerServiceClientConfiguration.builder().overrideConfiguration(this.overrideConfiguration()).region((Region)clientConfig.option((ClientOption)AwsClientOption.AWS_REGION)).endpointOverride(endpointOverride).endpointProvider((EndpointProvider)clientConfig.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER)).build();
    }
}

