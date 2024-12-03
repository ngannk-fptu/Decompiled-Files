/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.services.secretsmanager.DefaultSecretsManagerAsyncClient;
import software.amazon.awssdk.services.secretsmanager.DefaultSecretsManagerBaseClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerServiceClientConfiguration;
import software.amazon.awssdk.services.secretsmanager.endpoints.SecretsManagerEndpointProvider;

@SdkInternalApi
final class DefaultSecretsManagerAsyncClientBuilder
extends DefaultSecretsManagerBaseClientBuilder<SecretsManagerAsyncClientBuilder, SecretsManagerAsyncClient>
implements SecretsManagerAsyncClientBuilder {
    DefaultSecretsManagerAsyncClientBuilder() {
    }

    @Override
    public DefaultSecretsManagerAsyncClientBuilder endpointProvider(SecretsManagerEndpointProvider endpointProvider) {
        this.clientConfiguration.option(SdkClientOption.ENDPOINT_PROVIDER, endpointProvider);
        return this;
    }

    @Override
    protected final SecretsManagerAsyncClient buildClient() {
        SdkClientConfiguration clientConfiguration = super.asyncClientConfiguration();
        DefaultSecretsManagerAsyncClientBuilder.validateClientOptions(clientConfiguration);
        SecretsManagerServiceClientConfiguration serviceClientConfiguration = this.initializeServiceClientConfig(clientConfiguration);
        DefaultSecretsManagerAsyncClient client = new DefaultSecretsManagerAsyncClient(serviceClientConfiguration, clientConfiguration);
        return client;
    }

    private SecretsManagerServiceClientConfiguration initializeServiceClientConfig(SdkClientConfiguration clientConfig) {
        URI endpointOverride = null;
        if (Boolean.TRUE.equals(clientConfig.option(SdkClientOption.ENDPOINT_OVERRIDDEN))) {
            endpointOverride = clientConfig.option(SdkClientOption.ENDPOINT);
        }
        return SecretsManagerServiceClientConfiguration.builder().overrideConfiguration(this.overrideConfiguration()).region(clientConfig.option(AwsClientOption.AWS_REGION)).endpointOverride(endpointOverride).endpointProvider(clientConfig.option(SdkClientOption.ENDPOINT_PROVIDER)).build();
    }
}

