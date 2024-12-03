/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.client.builder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.CredentialUtils;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.awscore.client.config.AwsAdvancedClientOption;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.awscore.endpoint.DefaultServiceEndpointBuilder;
import software.amazon.awssdk.awscore.endpoint.DualstackEnabledProvider;
import software.amazon.awssdk.awscore.endpoint.FipsEnabledProvider;
import software.amazon.awssdk.awscore.eventstream.EventStreamInitialRequestInterceptor;
import software.amazon.awssdk.awscore.interceptor.HelpfulUnknownHostExceptionInterceptor;
import software.amazon.awssdk.awscore.interceptor.TraceIdExecutionInterceptor;
import software.amazon.awssdk.awscore.internal.defaultsmode.AutoDefaultsModeDiscovery;
import software.amazon.awssdk.awscore.internal.defaultsmode.DefaultsModeConfiguration;
import software.amazon.awssdk.awscore.internal.defaultsmode.DefaultsModeResolver;
import software.amazon.awssdk.awscore.retry.AwsRetryPolicy;
import software.amazon.awssdk.core.client.builder.SdkDefaultClientBuilder;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.regions.ServiceMetadataAdvancedOption;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.StringUtils;

@SdkProtectedApi
public abstract class AwsDefaultClientBuilder<BuilderT extends AwsClientBuilder<BuilderT, ClientT>, ClientT>
extends SdkDefaultClientBuilder<BuilderT, ClientT>
implements AwsClientBuilder<BuilderT, ClientT> {
    private static final Logger log = Logger.loggerFor(AwsClientBuilder.class);
    private static final String DEFAULT_ENDPOINT_PROTOCOL = "https";
    private static final String[] FIPS_SEARCH = new String[]{"fips-", "-fips"};
    private static final String[] FIPS_REPLACE = new String[]{"", ""};
    private final AutoDefaultsModeDiscovery autoDefaultsModeDiscovery;

    protected AwsDefaultClientBuilder() {
        this.autoDefaultsModeDiscovery = new AutoDefaultsModeDiscovery();
    }

    @SdkTestInternalApi
    AwsDefaultClientBuilder(SdkHttpClient.Builder defaultHttpClientBuilder, SdkAsyncHttpClient.Builder defaultAsyncHttpClientFactory, AutoDefaultsModeDiscovery autoDefaultsModeDiscovery) {
        super(defaultHttpClientBuilder, defaultAsyncHttpClientFactory);
        this.autoDefaultsModeDiscovery = autoDefaultsModeDiscovery;
    }

    protected abstract String serviceEndpointPrefix();

    protected abstract String signingName();

    protected abstract String serviceName();

    @Override
    protected final AttributeMap childHttpConfig() {
        return this.serviceHttpConfig();
    }

    @Override
    protected final AttributeMap childHttpConfig(SdkClientConfiguration configuration) {
        AttributeMap attributeMap = this.serviceHttpConfig();
        return this.mergeSmartHttpDefaults(configuration, attributeMap);
    }

    protected AttributeMap serviceHttpConfig() {
        return AttributeMap.empty();
    }

    @Override
    protected final SdkClientConfiguration mergeChildDefaults(SdkClientConfiguration configuration) {
        SdkClientConfiguration config = this.mergeServiceDefaults(configuration);
        config = config.merge(c -> c.option(AwsAdvancedClientOption.ENABLE_DEFAULT_REGION_DETECTION, true).option(SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION, false).option(AwsClientOption.SERVICE_SIGNING_NAME, this.signingName()).option(SdkClientOption.SERVICE_NAME, this.serviceName()).option(AwsClientOption.ENDPOINT_PREFIX, this.serviceEndpointPrefix()));
        return this.mergeInternalDefaults(config);
    }

    protected SdkClientConfiguration mergeServiceDefaults(SdkClientConfiguration configuration) {
        return configuration;
    }

    protected SdkClientConfiguration mergeInternalDefaults(SdkClientConfiguration configuration) {
        return configuration;
    }

    @Override
    protected final SdkClientConfiguration finalizeChildConfiguration(SdkClientConfiguration configuration) {
        configuration = this.finalizeServiceConfiguration(configuration);
        configuration = configuration.toBuilder().option(AwsClientOption.AWS_REGION, this.resolveRegion(configuration)).option(AwsClientOption.DUALSTACK_ENDPOINT_ENABLED, this.resolveDualstackEndpointEnabled(configuration)).option(AwsClientOption.FIPS_ENDPOINT_ENABLED, this.resolveFipsEndpointEnabled(configuration)).build();
        configuration = this.mergeSmartDefaults(configuration);
        IdentityProvider<AwsCredentialsIdentity> identityProvider = this.resolveCredentialsIdentityProvider(configuration);
        SdkClientConfiguration.Builder configBuilder = configuration.toBuilder().option(AwsClientOption.CREDENTIALS_IDENTITY_PROVIDER, identityProvider).option(AwsClientOption.CREDENTIALS_PROVIDER, this.toCredentialsProvider(identityProvider)).option(SdkClientOption.ENDPOINT, this.resolveEndpoint(configuration)).option(SdkClientOption.EXECUTION_INTERCEPTORS, this.addAwsInterceptors(configuration)).option(AwsClientOption.SIGNING_REGION, this.resolveSigningRegion(configuration)).option(SdkClientOption.RETRY_POLICY, this.resolveAwsRetryPolicy(configuration));
        if (identityProvider != null) {
            configBuilder.option(SdkClientOption.IDENTITY_PROVIDERS, ((IdentityProviders.Builder)configuration.option(SdkClientOption.IDENTITY_PROVIDERS).toBuilder()).putIdentityProvider(identityProvider).build());
        }
        return configBuilder.build();
    }

    private SdkClientConfiguration mergeSmartDefaults(SdkClientConfiguration configuration) {
        DefaultsMode defaultsMode = this.resolveDefaultsMode(configuration);
        AttributeMap defaultConfig = DefaultsModeConfiguration.defaultConfig(defaultsMode);
        return configuration.toBuilder().option(AwsClientOption.DEFAULTS_MODE, defaultsMode).build().merge(c -> c.option(SdkClientOption.DEFAULT_RETRY_MODE, defaultConfig.get(SdkClientOption.DEFAULT_RETRY_MODE)).option(ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT, defaultConfig.get(ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT)));
    }

    protected SdkClientConfiguration finalizeServiceConfiguration(SdkClientConfiguration configuration) {
        return configuration;
    }

    private AttributeMap mergeSmartHttpDefaults(SdkClientConfiguration configuration, AttributeMap attributeMap) {
        DefaultsMode defaultsMode = configuration.option(AwsClientOption.DEFAULTS_MODE);
        return attributeMap.merge(DefaultsModeConfiguration.defaultHttpConfig(defaultsMode));
    }

    private Region resolveSigningRegion(SdkClientConfiguration config) {
        return ServiceMetadata.of(this.serviceEndpointPrefix()).signingRegion(config.option(AwsClientOption.AWS_REGION));
    }

    private URI resolveEndpoint(SdkClientConfiguration config) {
        return Optional.ofNullable(config.option(SdkClientOption.ENDPOINT)).orElseGet(() -> this.endpointFromConfig(config));
    }

    private URI endpointFromConfig(SdkClientConfiguration config) {
        return new DefaultServiceEndpointBuilder(this.serviceEndpointPrefix(), DEFAULT_ENDPOINT_PROTOCOL).withRegion(config.option(AwsClientOption.AWS_REGION)).withProfileFile(config.option(SdkClientOption.PROFILE_FILE_SUPPLIER)).withProfileName(config.option(SdkClientOption.PROFILE_NAME)).putAdvancedOption(ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT, config.option(ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT)).withDualstackEnabled(config.option(AwsClientOption.DUALSTACK_ENDPOINT_ENABLED)).withFipsEnabled(config.option(AwsClientOption.FIPS_ENDPOINT_ENABLED)).getServiceEndpoint();
    }

    private Region resolveRegion(SdkClientConfiguration config) {
        return config.option(AwsClientOption.AWS_REGION) != null ? config.option(AwsClientOption.AWS_REGION) : this.regionFromDefaultProvider(config);
    }

    private Region regionFromDefaultProvider(SdkClientConfiguration config) {
        Boolean defaultRegionDetectionEnabled = config.option(AwsAdvancedClientOption.ENABLE_DEFAULT_REGION_DETECTION);
        if (defaultRegionDetectionEnabled != null && !defaultRegionDetectionEnabled.booleanValue()) {
            throw new IllegalStateException("No region was configured, and use-region-provider-chain was disabled.");
        }
        Supplier<ProfileFile> profileFile = config.option(SdkClientOption.PROFILE_FILE_SUPPLIER);
        String profileName = config.option(SdkClientOption.PROFILE_NAME);
        return DefaultAwsRegionProviderChain.builder().profileFile(profileFile).profileName(profileName).build().getRegion();
    }

    private DefaultsMode resolveDefaultsMode(SdkClientConfiguration config) {
        DefaultsMode defaultsMode;
        DefaultsMode defaultsMode2 = defaultsMode = config.option(AwsClientOption.DEFAULTS_MODE) != null ? config.option(AwsClientOption.DEFAULTS_MODE) : DefaultsModeResolver.create().profileFile(config.option(SdkClientOption.PROFILE_FILE_SUPPLIER)).profileName(config.option(SdkClientOption.PROFILE_NAME)).resolve();
        if (defaultsMode == DefaultsMode.AUTO) {
            DefaultsMode finalDefaultsMode = defaultsMode = this.autoDefaultsModeDiscovery.discover(config.option(AwsClientOption.AWS_REGION));
            log.debug(() -> String.format("Resolved %s client's AUTO configuration mode to %s", new Object[]{this.serviceName(), finalDefaultsMode}));
        }
        return defaultsMode;
    }

    private Boolean resolveDualstackEndpointEnabled(SdkClientConfiguration config) {
        return config.option(AwsClientOption.DUALSTACK_ENDPOINT_ENABLED) != null ? config.option(AwsClientOption.DUALSTACK_ENDPOINT_ENABLED) : this.resolveUseDualstackFromDefaultProvider(config);
    }

    private Boolean resolveUseDualstackFromDefaultProvider(SdkClientConfiguration config) {
        Supplier<ProfileFile> profileFile = config.option(SdkClientOption.PROFILE_FILE_SUPPLIER);
        String profileName = config.option(SdkClientOption.PROFILE_NAME);
        return DualstackEnabledProvider.builder().profileFile(profileFile).profileName(profileName).build().isDualstackEnabled().orElse(null);
    }

    private Boolean resolveFipsEndpointEnabled(SdkClientConfiguration config) {
        return config.option(AwsClientOption.FIPS_ENDPOINT_ENABLED) != null ? config.option(AwsClientOption.FIPS_ENDPOINT_ENABLED) : this.resolveUseFipsFromDefaultProvider(config);
    }

    private Boolean resolveUseFipsFromDefaultProvider(SdkClientConfiguration config) {
        Supplier<ProfileFile> profileFile = config.option(SdkClientOption.PROFILE_FILE_SUPPLIER);
        String profileName = config.option(SdkClientOption.PROFILE_NAME);
        return FipsEnabledProvider.builder().profileFile(profileFile).profileName(profileName).build().isFipsEnabled().orElse(null);
    }

    private IdentityProvider<? extends AwsCredentialsIdentity> resolveCredentialsIdentityProvider(SdkClientConfiguration config) {
        return config.option(AwsClientOption.CREDENTIALS_IDENTITY_PROVIDER) != null ? config.option(AwsClientOption.CREDENTIALS_IDENTITY_PROVIDER) : DefaultCredentialsProvider.builder().profileFile(config.option(SdkClientOption.PROFILE_FILE_SUPPLIER)).profileName(config.option(SdkClientOption.PROFILE_NAME)).build();
    }

    private AwsCredentialsProvider toCredentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> identityProvider) {
        return identityProvider instanceof AwsCredentialsProvider ? (AwsCredentialsProvider)identityProvider : CredentialUtils.toCredentialsProvider(identityProvider);
    }

    private RetryPolicy resolveAwsRetryPolicy(SdkClientConfiguration config) {
        RetryPolicy policy = config.option(SdkClientOption.RETRY_POLICY);
        if (policy != null) {
            if (policy.additionalRetryConditionsAllowed()) {
                return AwsRetryPolicy.addRetryConditions(policy);
            }
            return policy;
        }
        RetryMode retryMode = RetryMode.resolver().profileFile(config.option(SdkClientOption.PROFILE_FILE_SUPPLIER)).profileName(config.option(SdkClientOption.PROFILE_NAME)).defaultRetryMode(config.option(SdkClientOption.DEFAULT_RETRY_MODE)).resolve();
        return AwsRetryPolicy.forRetryMode(retryMode);
    }

    @Override
    public final BuilderT region(Region region) {
        Region regionToSet = region;
        Boolean fipsEnabled = null;
        if (region != null) {
            Pair<Region, Optional<Boolean>> transformedRegion = AwsDefaultClientBuilder.transformFipsPseudoRegionIfNecessary(region);
            regionToSet = transformedRegion.left();
            fipsEnabled = transformedRegion.right().orElse(null);
        }
        this.clientConfiguration.option(AwsClientOption.AWS_REGION, regionToSet);
        if (fipsEnabled != null) {
            this.clientConfiguration.option(AwsClientOption.FIPS_ENDPOINT_ENABLED, fipsEnabled);
        }
        return (BuilderT)((AwsClientBuilder)this.thisBuilder());
    }

    public final void setRegion(Region region) {
        this.region(region);
    }

    @Override
    public BuilderT dualstackEnabled(Boolean dualstackEndpointEnabled) {
        this.clientConfiguration.option(AwsClientOption.DUALSTACK_ENDPOINT_ENABLED, dualstackEndpointEnabled);
        return (BuilderT)((AwsClientBuilder)this.thisBuilder());
    }

    public final void setDualstackEnabled(Boolean dualstackEndpointEnabled) {
        this.dualstackEnabled(dualstackEndpointEnabled);
    }

    @Override
    public BuilderT fipsEnabled(Boolean dualstackEndpointEnabled) {
        this.clientConfiguration.option(AwsClientOption.FIPS_ENDPOINT_ENABLED, dualstackEndpointEnabled);
        return (BuilderT)((AwsClientBuilder)this.thisBuilder());
    }

    public final void setFipsEnabled(Boolean fipsEndpointEnabled) {
        this.fipsEnabled(fipsEndpointEnabled);
    }

    public final void setCredentialsProvider(AwsCredentialsProvider credentialsProvider) {
        this.credentialsProvider(credentialsProvider);
    }

    @Override
    public final BuilderT credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> identityProvider) {
        this.clientConfiguration.option(AwsClientOption.CREDENTIALS_IDENTITY_PROVIDER, identityProvider);
        return (BuilderT)((AwsClientBuilder)this.thisBuilder());
    }

    public final void setCredentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> identityProvider) {
        this.credentialsProvider(identityProvider);
    }

    private List<ExecutionInterceptor> addAwsInterceptors(SdkClientConfiguration config) {
        List<ExecutionInterceptor> interceptors = this.awsInterceptors();
        interceptors = CollectionUtils.mergeLists(interceptors, config.option(SdkClientOption.EXECUTION_INTERCEPTORS));
        return interceptors;
    }

    private List<ExecutionInterceptor> awsInterceptors() {
        return Arrays.asList(new HelpfulUnknownHostExceptionInterceptor(), new EventStreamInitialRequestInterceptor(), new TraceIdExecutionInterceptor());
    }

    @Override
    public final BuilderT defaultsMode(DefaultsMode defaultsMode) {
        this.clientConfiguration.option(AwsClientOption.DEFAULTS_MODE, defaultsMode);
        return (BuilderT)((AwsClientBuilder)this.thisBuilder());
    }

    public final void setDefaultsMode(DefaultsMode defaultsMode) {
        this.defaultsMode(defaultsMode);
    }

    private static Pair<Region, Optional<Boolean>> transformFipsPseudoRegionIfNecessary(Region region) {
        String id = region.id();
        String newId = StringUtils.replaceEach(id, FIPS_SEARCH, FIPS_REPLACE);
        if (!newId.equals(id)) {
            log.info(() -> String.format("Replacing input region %s with %s and setting fipsEnabled to true", id, newId));
            return Pair.of(Region.of(newId), Optional.of(true));
        }
        return Pair.of(region, Optional.empty());
    }
}

