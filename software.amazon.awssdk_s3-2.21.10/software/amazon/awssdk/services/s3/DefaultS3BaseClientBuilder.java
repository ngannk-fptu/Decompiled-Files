/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.AwsS3V4Signer
 *  software.amazon.awssdk.awscore.client.builder.AwsDefaultClientBuilder
 *  software.amazon.awssdk.awscore.client.config.AwsClientOption
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
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.AwsS3V4Signer;
import software.amazon.awssdk.awscore.client.builder.AwsDefaultClientBuilder;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.SdkServiceClientConfiguration;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.interceptor.ClasspathInterceptorChainFactory;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3BaseClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.endpoints.S3ClientContextParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;
import software.amazon.awssdk.services.s3.endpoints.internal.S3RequestSetEndpointInterceptor;
import software.amazon.awssdk.services.s3.endpoints.internal.S3ResolveEndpointInterceptor;
import software.amazon.awssdk.services.s3.internal.S3ServiceClientConfigurationBuilder;
import software.amazon.awssdk.services.s3.internal.SdkClientConfigurationUtil;
import software.amazon.awssdk.services.s3.internal.endpoints.UseGlobalEndpointResolver;
import software.amazon.awssdk.services.s3.internal.handlers.AsyncChecksumValidationInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.ConfigureSignerInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.CopySourceInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.CreateBucketInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.CreateMultipartUploadRequestInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.DecodeUrlEncodedResponseInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.DisablePayloadSigningInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.EnableChunkedEncodingInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.EnableTrailingChecksumInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.ExceptionTranslationInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.GetBucketPolicyInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.GetObjectInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.StreamingRequestInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.SyncChecksumValidationInterceptor;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
abstract class DefaultS3BaseClientBuilder<B extends S3BaseClientBuilder<B, C>, C>
extends AwsDefaultClientBuilder<B, C> {
    DefaultS3BaseClientBuilder() {
    }

    protected final String serviceEndpointPrefix() {
        return "s3";
    }

    protected final String serviceName() {
        return "S3";
    }

    protected final SdkClientConfiguration mergeServiceDefaults(SdkClientConfiguration config) {
        return config.merge(c -> c.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER, (Object)this.defaultEndpointProvider()).option((ClientOption)SdkAdvancedClientOption.SIGNER, (Object)this.defaultSigner()).option((ClientOption)SdkClientOption.CRC32_FROM_COMPRESSED_DATA_ENABLED, (Object)false).option((ClientOption)SdkClientOption.SERVICE_CONFIGURATION, S3Configuration.builder().build()));
    }

    protected final SdkClientConfiguration finalizeServiceConfiguration(SdkClientConfiguration config) {
        ArrayList<Object> endpointInterceptors = new ArrayList<Object>();
        endpointInterceptors.add(new S3ResolveEndpointInterceptor());
        endpointInterceptors.add(new S3RequestSetEndpointInterceptor());
        endpointInterceptors.add(new StreamingRequestInterceptor());
        endpointInterceptors.add(new CreateBucketInterceptor());
        endpointInterceptors.add(new CreateMultipartUploadRequestInterceptor());
        endpointInterceptors.add(new EnableChunkedEncodingInterceptor());
        endpointInterceptors.add(new ConfigureSignerInterceptor());
        endpointInterceptors.add(new DecodeUrlEncodedResponseInterceptor());
        endpointInterceptors.add(new GetBucketPolicyInterceptor());
        endpointInterceptors.add(new AsyncChecksumValidationInterceptor());
        endpointInterceptors.add(new SyncChecksumValidationInterceptor());
        endpointInterceptors.add(new EnableTrailingChecksumInterceptor());
        endpointInterceptors.add(new ExceptionTranslationInterceptor());
        endpointInterceptors.add(new GetObjectInterceptor());
        endpointInterceptors.add(new CopySourceInterceptor());
        endpointInterceptors.add(new DisablePayloadSigningInterceptor());
        ClasspathInterceptorChainFactory interceptorFactory = new ClasspathInterceptorChainFactory();
        List interceptors = interceptorFactory.getInterceptors("software/amazon/awssdk/services/s3/execution.interceptors");
        ArrayList additionalInterceptors = new ArrayList();
        interceptors = CollectionUtils.mergeLists(endpointInterceptors, (List)interceptors);
        interceptors = CollectionUtils.mergeLists((List)interceptors, additionalInterceptors);
        interceptors = CollectionUtils.mergeLists((List)interceptors, (List)((List)config.option((ClientOption)SdkClientOption.EXECUTION_INTERCEPTORS)));
        S3Configuration.Builder serviceConfigBuilder = ((S3Configuration)config.option((ClientOption)SdkClientOption.SERVICE_CONFIGURATION)).toBuilder();
        serviceConfigBuilder.profileFile(serviceConfigBuilder.profileFileSupplier() != null ? serviceConfigBuilder.profileFileSupplier() : (Supplier)config.option((ClientOption)SdkClientOption.PROFILE_FILE_SUPPLIER));
        serviceConfigBuilder.profileName(serviceConfigBuilder.profileName() != null ? serviceConfigBuilder.profileName() : (String)config.option((ClientOption)SdkClientOption.PROFILE_NAME));
        if (serviceConfigBuilder.dualstackEnabled() != null) {
            Validate.validState((config.option((ClientOption)AwsClientOption.DUALSTACK_ENDPOINT_ENABLED) == null ? 1 : 0) != 0, (String)"Dualstack has been configured on both S3Configuration and the client/global level. Please limit dualstack configuration to one location.", (Object[])new Object[0]);
        } else {
            serviceConfigBuilder.dualstackEnabled((Boolean)config.option((ClientOption)AwsClientOption.DUALSTACK_ENDPOINT_ENABLED));
        }
        if (serviceConfigBuilder.useArnRegionEnabled() != null) {
            Validate.validState((this.clientContextParams.get(S3ClientContextParams.USE_ARN_REGION) == null ? 1 : 0) != 0, (String)"UseArnRegion has been configured on both S3Configuration and the client/global level. Please limit UseArnRegion configuration to one location.", (Object[])new Object[0]);
        } else {
            serviceConfigBuilder.useArnRegionEnabled((Boolean)this.clientContextParams.get(S3ClientContextParams.USE_ARN_REGION));
        }
        if (serviceConfigBuilder.multiRegionEnabled() != null) {
            Validate.validState((this.clientContextParams.get(S3ClientContextParams.DISABLE_MULTI_REGION_ACCESS_POINTS) == null ? 1 : 0) != 0, (String)"DisableMultiRegionAccessPoints has been configured on both S3Configuration and the client/global level. Please limit DisableMultiRegionAccessPoints configuration to one location.", (Object[])new Object[0]);
        } else if (this.clientContextParams.get(S3ClientContextParams.DISABLE_MULTI_REGION_ACCESS_POINTS) != null) {
            serviceConfigBuilder.multiRegionEnabled((Boolean)this.clientContextParams.get(S3ClientContextParams.DISABLE_MULTI_REGION_ACCESS_POINTS) == false);
        }
        if (serviceConfigBuilder.pathStyleAccessEnabled() != null) {
            Validate.validState((this.clientContextParams.get(S3ClientContextParams.FORCE_PATH_STYLE) == null ? 1 : 0) != 0, (String)"ForcePathStyle has been configured on both S3Configuration and the client/global level. Please limit ForcePathStyle configuration to one location.", (Object[])new Object[0]);
        } else {
            serviceConfigBuilder.pathStyleAccessEnabled((Boolean)this.clientContextParams.get(S3ClientContextParams.FORCE_PATH_STYLE));
        }
        if (serviceConfigBuilder.accelerateModeEnabled() != null) {
            Validate.validState((this.clientContextParams.get(S3ClientContextParams.ACCELERATE) == null ? 1 : 0) != 0, (String)"Accelerate has been configured on both S3Configuration and the client/global level. Please limit Accelerate configuration to one location.", (Object[])new Object[0]);
        } else {
            serviceConfigBuilder.accelerateModeEnabled((Boolean)this.clientContextParams.get(S3ClientContextParams.ACCELERATE));
        }
        S3Configuration finalServiceConfig = (S3Configuration)serviceConfigBuilder.build();
        this.clientContextParams.put(S3ClientContextParams.USE_ARN_REGION, (Object)finalServiceConfig.useArnRegionEnabled());
        this.clientContextParams.put(S3ClientContextParams.DISABLE_MULTI_REGION_ACCESS_POINTS, (Object)(!finalServiceConfig.multiRegionEnabled() ? 1 : 0));
        this.clientContextParams.put(S3ClientContextParams.FORCE_PATH_STYLE, (Object)finalServiceConfig.pathStyleAccessEnabled());
        this.clientContextParams.put(S3ClientContextParams.ACCELERATE, (Object)finalServiceConfig.accelerateModeEnabled());
        UseGlobalEndpointResolver resolver = new UseGlobalEndpointResolver(config);
        SdkClientConfiguration.Builder builder = config.toBuilder();
        builder.option((ClientOption)SdkClientOption.EXECUTION_INTERCEPTORS, (Object)interceptors).option((ClientOption)AwsClientOption.DUALSTACK_ENDPOINT_ENABLED, (Object)finalServiceConfig.dualstackEnabled()).option((ClientOption)SdkClientOption.SERVICE_CONFIGURATION, (Object)finalServiceConfig).option((ClientOption)AwsClientOption.USE_GLOBAL_ENDPOINT, (Object)resolver.resolve((Region)config.option((ClientOption)AwsClientOption.AWS_REGION))).option((ClientOption)SdkClientOption.CLIENT_CONTEXT_PARAMS, (Object)this.clientContextParams.build());
        return builder.build();
    }

    private Signer defaultSigner() {
        return AwsS3V4Signer.create();
    }

    protected final String signingName() {
        return "s3";
    }

    private S3EndpointProvider defaultEndpointProvider() {
        return S3EndpointProvider.defaultProvider();
    }

    public B accelerate(Boolean accelerate) {
        this.clientContextParams.put(S3ClientContextParams.ACCELERATE, (Object)accelerate);
        return (B)((S3BaseClientBuilder)this.thisBuilder());
    }

    public B disableMultiRegionAccessPoints(Boolean disableMultiRegionAccessPoints) {
        this.clientContextParams.put(S3ClientContextParams.DISABLE_MULTI_REGION_ACCESS_POINTS, (Object)disableMultiRegionAccessPoints);
        return (B)((S3BaseClientBuilder)this.thisBuilder());
    }

    public B forcePathStyle(Boolean forcePathStyle) {
        this.clientContextParams.put(S3ClientContextParams.FORCE_PATH_STYLE, (Object)forcePathStyle);
        return (B)((S3BaseClientBuilder)this.thisBuilder());
    }

    public B useArnRegion(Boolean useArnRegion) {
        this.clientContextParams.put(S3ClientContextParams.USE_ARN_REGION, (Object)useArnRegion);
        return (B)((S3BaseClientBuilder)this.thisBuilder());
    }

    public B crossRegionAccessEnabled(Boolean crossRegionAccessEnabled) {
        this.clientContextParams.put(S3ClientContextParams.CROSS_REGION_ACCESS_ENABLED, (Object)crossRegionAccessEnabled);
        return (B)((S3BaseClientBuilder)this.thisBuilder());
    }

    public B serviceConfiguration(S3Configuration serviceConfiguration) {
        this.clientConfiguration.option((ClientOption)SdkClientOption.SERVICE_CONFIGURATION, (Object)serviceConfiguration);
        return (B)((S3BaseClientBuilder)this.thisBuilder());
    }

    public void setServiceConfiguration(S3Configuration serviceConfiguration) {
        this.serviceConfiguration(serviceConfiguration);
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
        S3ServiceClientConfigurationBuilder.BuilderInternal serviceConfigBuilder = S3ServiceClientConfigurationBuilder.builder(config.toBuilder());
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

