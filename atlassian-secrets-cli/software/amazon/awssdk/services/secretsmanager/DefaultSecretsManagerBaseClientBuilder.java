/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager;

import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.awscore.client.builder.AwsDefaultClientBuilder;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.interceptor.ClasspathInterceptorChainFactory;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerBaseClientBuilder;
import software.amazon.awssdk.services.secretsmanager.endpoints.SecretsManagerEndpointProvider;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.SecretsManagerRequestSetEndpointInterceptor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.SecretsManagerResolveEndpointInterceptor;
import software.amazon.awssdk.services.secretsmanager.internal.SdkClientConfigurationUtil;
import software.amazon.awssdk.services.secretsmanager.internal.SecretsManagerServiceClientConfigurationBuilder;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
abstract class DefaultSecretsManagerBaseClientBuilder<B extends SecretsManagerBaseClientBuilder<B, C>, C>
extends AwsDefaultClientBuilder<B, C> {
    DefaultSecretsManagerBaseClientBuilder() {
    }

    @Override
    protected final String serviceEndpointPrefix() {
        return "secretsmanager";
    }

    @Override
    protected final String serviceName() {
        return "SecretsManager";
    }

    @Override
    protected final SdkClientConfiguration mergeServiceDefaults(SdkClientConfiguration config) {
        return config.merge(c -> c.option(SdkClientOption.ENDPOINT_PROVIDER, this.defaultEndpointProvider()).option(SdkAdvancedClientOption.SIGNER, this.defaultSigner()).option(SdkClientOption.CRC32_FROM_COMPRESSED_DATA_ENABLED, false));
    }

    @Override
    protected final SdkClientConfiguration finalizeServiceConfiguration(SdkClientConfiguration config) {
        ArrayList<ExecutionInterceptor> endpointInterceptors = new ArrayList<ExecutionInterceptor>();
        endpointInterceptors.add(new SecretsManagerResolveEndpointInterceptor());
        endpointInterceptors.add(new SecretsManagerRequestSetEndpointInterceptor());
        ClasspathInterceptorChainFactory interceptorFactory = new ClasspathInterceptorChainFactory();
        List<ExecutionInterceptor> interceptors = interceptorFactory.getInterceptors("software/amazon/awssdk/services/secretsmanager/execution.interceptors");
        ArrayList additionalInterceptors = new ArrayList();
        interceptors = CollectionUtils.mergeLists(endpointInterceptors, interceptors);
        interceptors = CollectionUtils.mergeLists(interceptors, additionalInterceptors);
        interceptors = CollectionUtils.mergeLists(interceptors, config.option(SdkClientOption.EXECUTION_INTERCEPTORS));
        SdkClientConfiguration.Builder builder = config.toBuilder();
        builder.option(SdkClientOption.EXECUTION_INTERCEPTORS, interceptors);
        return builder.build();
    }

    private Signer defaultSigner() {
        return Aws4Signer.create();
    }

    @Override
    protected final String signingName() {
        return "secretsmanager";
    }

    private SecretsManagerEndpointProvider defaultEndpointProvider() {
        return SecretsManagerEndpointProvider.defaultProvider();
    }

    @Override
    protected SdkClientConfiguration setOverrides(SdkClientConfiguration configuration) {
        ClientOverrideConfiguration overrideConfiguration = this.overrideConfiguration();
        if (overrideConfiguration == null) {
            return configuration;
        }
        return SdkClientConfigurationUtil.copyOverridesToConfiguration(overrideConfiguration, configuration.toBuilder()).build();
    }

    @Override
    protected SdkClientConfiguration invokePlugins(SdkClientConfiguration config) {
        List<SdkPlugin> plugins = this.plugins();
        if (plugins.isEmpty()) {
            return config;
        }
        SecretsManagerServiceClientConfigurationBuilder.BuilderInternal serviceConfigBuilder = SecretsManagerServiceClientConfigurationBuilder.builder(config.toBuilder());
        serviceConfigBuilder.overrideConfiguration(this.overrideConfiguration());
        for (SdkPlugin plugin : plugins) {
            plugin.configureClient(serviceConfigBuilder);
        }
        this.overrideConfiguration(serviceConfigBuilder.overrideConfiguration());
        return serviceConfigBuilder.buildSdkClientConfiguration();
    }

    protected static void validateClientOptions(SdkClientConfiguration c) {
        Validate.notNull(c.option(SdkAdvancedClientOption.SIGNER), "The 'overrideConfiguration.advancedOption[SIGNER]' must be configured in the client builder.", new Object[0]);
    }
}

