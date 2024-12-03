/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.Aws4Signer
 *  software.amazon.awssdk.awscore.client.builder.AwsDefaultClientBuilder
 *  software.amazon.awssdk.core.SdkPlugin
 *  software.amazon.awssdk.core.SdkServiceClientConfiguration$Builder
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
 *  software.amazon.awssdk.core.client.config.SdkAdvancedClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration$Builder
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.core.interceptor.ClasspathInterceptorChainFactory
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.sts;

import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.awscore.client.builder.AwsDefaultClientBuilder;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.SdkServiceClientConfiguration;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.interceptor.ClasspathInterceptorChainFactory;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.services.sts.StsBaseClientBuilder;
import software.amazon.awssdk.services.sts.endpoints.StsEndpointProvider;
import software.amazon.awssdk.services.sts.endpoints.internal.StsRequestSetEndpointInterceptor;
import software.amazon.awssdk.services.sts.endpoints.internal.StsResolveEndpointInterceptor;
import software.amazon.awssdk.services.sts.internal.SdkClientConfigurationUtil;
import software.amazon.awssdk.services.sts.internal.StsServiceClientConfigurationBuilder;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
abstract class DefaultStsBaseClientBuilder<B extends StsBaseClientBuilder<B, C>, C>
extends AwsDefaultClientBuilder<B, C> {
    DefaultStsBaseClientBuilder() {
    }

    protected final String serviceEndpointPrefix() {
        return "sts";
    }

    protected final String serviceName() {
        return "Sts";
    }

    protected final SdkClientConfiguration mergeServiceDefaults(SdkClientConfiguration config) {
        return config.merge(c -> c.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER, (Object)this.defaultEndpointProvider()).option((ClientOption)SdkAdvancedClientOption.SIGNER, (Object)this.defaultSigner()).option((ClientOption)SdkClientOption.CRC32_FROM_COMPRESSED_DATA_ENABLED, (Object)false));
    }

    protected final SdkClientConfiguration finalizeServiceConfiguration(SdkClientConfiguration config) {
        ArrayList<Object> endpointInterceptors = new ArrayList<Object>();
        endpointInterceptors.add(new StsResolveEndpointInterceptor());
        endpointInterceptors.add(new StsRequestSetEndpointInterceptor());
        ClasspathInterceptorChainFactory interceptorFactory = new ClasspathInterceptorChainFactory();
        List interceptors = interceptorFactory.getInterceptors("software/amazon/awssdk/services/sts/execution.interceptors");
        ArrayList additionalInterceptors = new ArrayList();
        interceptors = CollectionUtils.mergeLists(endpointInterceptors, (List)interceptors);
        interceptors = CollectionUtils.mergeLists((List)interceptors, additionalInterceptors);
        interceptors = CollectionUtils.mergeLists((List)interceptors, (List)((List)config.option((ClientOption)SdkClientOption.EXECUTION_INTERCEPTORS)));
        SdkClientConfiguration.Builder builder = config.toBuilder();
        builder.option((ClientOption)SdkClientOption.EXECUTION_INTERCEPTORS, (Object)interceptors);
        return builder.build();
    }

    private Signer defaultSigner() {
        return Aws4Signer.create();
    }

    protected final String signingName() {
        return "sts";
    }

    private StsEndpointProvider defaultEndpointProvider() {
        return StsEndpointProvider.defaultProvider();
    }

    protected SdkClientConfiguration setOverrides(SdkClientConfiguration configuration) {
        ClientOverrideConfiguration overrideConfiguration = this.overrideConfiguration();
        if (overrideConfiguration == null) {
            return configuration;
        }
        return SdkClientConfigurationUtil.copyOverridesToConfiguration(overrideConfiguration, configuration.toBuilder()).build();
    }

    protected SdkClientConfiguration invokePlugins(SdkClientConfiguration config) {
        List plugins = this.plugins();
        if (plugins.isEmpty()) {
            return config;
        }
        StsServiceClientConfigurationBuilder.BuilderInternal serviceConfigBuilder = StsServiceClientConfigurationBuilder.builder(config.toBuilder());
        serviceConfigBuilder.overrideConfiguration(this.overrideConfiguration());
        for (SdkPlugin plugin : plugins) {
            plugin.configureClient((SdkServiceClientConfiguration.Builder)serviceConfigBuilder);
        }
        this.overrideConfiguration(serviceConfigBuilder.overrideConfiguration());
        return serviceConfigBuilder.buildSdkClientConfiguration();
    }

    protected static void validateClientOptions(SdkClientConfiguration c) {
        Validate.notNull((Object)c.option((ClientOption)SdkAdvancedClientOption.SIGNER), (String)"The 'overrideConfiguration.advancedOption[SIGNER]' must be configured in the client builder.", (Object[])new Object[0]);
    }
}

