/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPreviewApi
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.http.ExecutableHttpRequest
 *  software.amazon.awssdk.http.HttpExecuteRequest
 *  software.amazon.awssdk.http.SdkHttpClient
 *  software.amazon.awssdk.http.SdkHttpClient$Builder
 *  software.amazon.awssdk.http.async.AsyncExecuteRequest
 *  software.amazon.awssdk.http.async.SdkAsyncHttpClient
 *  software.amazon.awssdk.http.async.SdkAsyncHttpClient$Builder
 *  software.amazon.awssdk.identity.spi.IdentityProviders
 *  software.amazon.awssdk.metrics.MetricPublisher
 *  software.amazon.awssdk.profiles.Profile
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSupplier
 *  software.amazon.awssdk.profiles.ProfileFileSystemSetting
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.Either
 *  software.amazon.awssdk.utils.ScheduledExecutorUtils
 *  software.amazon.awssdk.utils.ThreadFactoryBuilder
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.client.builder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPreviewApi;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.CompressionConfiguration;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.client.builder.SdkClientBuilder;
import software.amazon.awssdk.core.client.config.ClientAsyncConfiguration;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.interceptor.ClasspathInterceptorChainFactory;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.internal.SdkInternalTestAdvancedClientOption;
import software.amazon.awssdk.core.internal.http.loader.DefaultSdkAsyncHttpClientBuilder;
import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.core.internal.http.pipeline.stages.ApplyUserAgentStage;
import software.amazon.awssdk.core.internal.interceptor.HttpChecksumValidationInterceptor;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.util.SdkUserAgent;
import software.amazon.awssdk.http.ExecutableHttpRequest;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.async.AsyncExecuteRequest;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSupplier;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.Either;
import software.amazon.awssdk.utils.ScheduledExecutorUtils;
import software.amazon.awssdk.utils.ThreadFactoryBuilder;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public abstract class SdkDefaultClientBuilder<B extends SdkClientBuilder<B, C>, C>
implements SdkClientBuilder<B, C> {
    private static final SdkHttpClient.Builder DEFAULT_HTTP_CLIENT_BUILDER = new DefaultSdkHttpClientBuilder();
    private static final SdkAsyncHttpClient.Builder DEFAULT_ASYNC_HTTP_CLIENT_BUILDER = new DefaultSdkAsyncHttpClientBuilder();
    protected final SdkClientConfiguration.Builder clientConfiguration = SdkClientConfiguration.builder();
    protected final AttributeMap.Builder clientContextParams = AttributeMap.builder();
    private final SdkHttpClient.Builder defaultHttpClientBuilder;
    private final SdkAsyncHttpClient.Builder defaultAsyncHttpClientBuilder;
    private ClientOverrideConfiguration clientOverrideConfiguration;
    private SdkHttpClient.Builder httpClientBuilder;
    private SdkAsyncHttpClient.Builder asyncHttpClientBuilder;
    private final List<SdkPlugin> plugins = new ArrayList<SdkPlugin>();

    protected SdkDefaultClientBuilder() {
        this(DEFAULT_HTTP_CLIENT_BUILDER, DEFAULT_ASYNC_HTTP_CLIENT_BUILDER);
    }

    @SdkTestInternalApi
    protected SdkDefaultClientBuilder(SdkHttpClient.Builder defaultHttpClientBuilder, SdkAsyncHttpClient.Builder defaultAsyncHttpClientBuilder) {
        this.defaultHttpClientBuilder = defaultHttpClientBuilder;
        this.defaultAsyncHttpClientBuilder = defaultAsyncHttpClientBuilder;
    }

    public final C build() {
        return this.buildClient();
    }

    protected abstract C buildClient();

    protected final SdkClientConfiguration syncClientConfiguration() {
        this.clientConfiguration.option(SdkClientOption.CLIENT_CONTEXT_PARAMS, this.clientContextParams.build());
        SdkClientConfiguration configuration = this.clientConfiguration.build();
        configuration = this.setOverrides(configuration);
        configuration = this.mergeChildDefaults(configuration);
        configuration = this.mergeGlobalDefaults(configuration);
        configuration = this.invokePlugins(configuration);
        configuration = this.finalizeChildConfiguration(configuration);
        configuration = this.finalizeSyncConfiguration(configuration);
        configuration = this.finalizeConfiguration(configuration);
        return configuration;
    }

    protected final SdkClientConfiguration asyncClientConfiguration() {
        this.clientConfiguration.option(SdkClientOption.CLIENT_CONTEXT_PARAMS, this.clientContextParams.build());
        SdkClientConfiguration configuration = this.clientConfiguration.build();
        configuration = this.setOverrides(configuration);
        configuration = this.mergeChildDefaults(configuration);
        configuration = this.mergeGlobalDefaults(configuration);
        configuration = this.invokePlugins(configuration);
        configuration = this.finalizeChildConfiguration(configuration);
        configuration = this.finalizeAsyncConfiguration(configuration);
        configuration = this.finalizeConfiguration(configuration);
        return configuration;
    }

    protected SdkClientConfiguration setOverrides(SdkClientConfiguration configuration) {
        if (this.clientOverrideConfiguration == null) {
            return configuration;
        }
        SdkClientConfiguration.Builder builder = configuration.toBuilder();
        builder.option(SdkClientOption.SCHEDULED_EXECUTOR_SERVICE, this.clientOverrideConfiguration.scheduledExecutorService().orElse(null));
        builder.option(SdkClientOption.EXECUTION_INTERCEPTORS, this.clientOverrideConfiguration.executionInterceptors());
        builder.option(SdkClientOption.RETRY_POLICY, this.clientOverrideConfiguration.retryPolicy().orElse(null));
        builder.option(SdkClientOption.ADDITIONAL_HTTP_HEADERS, this.clientOverrideConfiguration.headers());
        builder.option(SdkAdvancedClientOption.SIGNER, this.clientOverrideConfiguration.advancedOption(SdkAdvancedClientOption.SIGNER).orElse(null));
        builder.option(SdkAdvancedClientOption.USER_AGENT_SUFFIX, this.clientOverrideConfiguration.advancedOption(SdkAdvancedClientOption.USER_AGENT_SUFFIX).orElse(null));
        builder.option(SdkAdvancedClientOption.USER_AGENT_PREFIX, this.clientOverrideConfiguration.advancedOption(SdkAdvancedClientOption.USER_AGENT_PREFIX).orElse(null));
        builder.option(SdkClientOption.API_CALL_TIMEOUT, this.clientOverrideConfiguration.apiCallTimeout().orElse(null));
        builder.option(SdkClientOption.API_CALL_ATTEMPT_TIMEOUT, this.clientOverrideConfiguration.apiCallAttemptTimeout().orElse(null));
        builder.option(SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION, this.clientOverrideConfiguration.advancedOption(SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION).orElse(null));
        builder.option(SdkClientOption.PROFILE_FILE_SUPPLIER, this.clientOverrideConfiguration.defaultProfileFile().map(ProfileFileSupplier::fixedProfileFile).orElse(null));
        builder.option(SdkClientOption.PROFILE_NAME, this.clientOverrideConfiguration.defaultProfileName().orElse(null));
        builder.option(SdkClientOption.METRIC_PUBLISHERS, this.clientOverrideConfiguration.metricPublishers());
        builder.option(SdkClientOption.EXECUTION_ATTRIBUTES, this.clientOverrideConfiguration.executionAttributes());
        builder.option(SdkAdvancedClientOption.TOKEN_SIGNER, this.clientOverrideConfiguration.advancedOption(SdkAdvancedClientOption.TOKEN_SIGNER).orElse(null));
        builder.option(SdkClientOption.COMPRESSION_CONFIGURATION, this.clientOverrideConfiguration.compressionConfiguration().orElse(null));
        this.clientOverrideConfiguration.advancedOption(SdkInternalTestAdvancedClientOption.ENDPOINT_OVERRIDDEN_OVERRIDE).ifPresent(value -> builder.option(SdkClientOption.ENDPOINT_OVERRIDDEN, value));
        this.clientOverrideConfiguration.advancedOption(SdkAdvancedClientOption.SIGNER).ifPresent(s -> builder.option(SdkClientOption.SIGNER_OVERRIDDEN, true));
        return builder.build();
    }

    protected SdkClientConfiguration mergeChildDefaults(SdkClientConfiguration configuration) {
        return configuration;
    }

    private SdkClientConfiguration mergeGlobalDefaults(SdkClientConfiguration configuration) {
        Supplier profileFileSupplier = Optional.ofNullable(configuration.option(SdkClientOption.PROFILE_FILE_SUPPLIER)).orElseGet(() -> ProfileFileSupplier.fixedProfileFile((ProfileFile)ProfileFile.defaultProfileFile()));
        configuration = configuration.merge(arg_0 -> SdkDefaultClientBuilder.lambda$mergeGlobalDefaults$3((Supplier)profileFileSupplier, arg_0));
        return this.addCompressionConfigGlobalDefaults(configuration);
    }

    private SdkClientConfiguration addCompressionConfigGlobalDefaults(SdkClientConfiguration configuration) {
        Optional profileSetting;
        Profile profile;
        Optional systemSetting;
        Optional<Boolean> requestCompressionEnabled = this.getCompressionEnabled(configuration);
        Optional<Integer> minCompressionThreshold = this.getCompressionThreshold(configuration);
        if (requestCompressionEnabled.isPresent() && minCompressionThreshold.isPresent()) {
            return configuration;
        }
        Boolean compressionEnabled = requestCompressionEnabled.orElse(null);
        Integer compressionThreshold = minCompressionThreshold.orElse(null);
        if (compressionEnabled == null) {
            systemSetting = SdkSystemSetting.AWS_DISABLE_REQUEST_COMPRESSION.getBooleanValue();
            if (systemSetting.isPresent()) {
                compressionEnabled = (Boolean)systemSetting.get() == false;
            } else {
                profile = configuration.option(SdkClientOption.PROFILE_FILE_SUPPLIER).get().profile(configuration.option(SdkClientOption.PROFILE_NAME)).orElse(null);
                if (profile != null && (profileSetting = profile.booleanProperty("disable_request_compression")).isPresent()) {
                    compressionEnabled = (Boolean)profileSetting.get() == false;
                }
            }
        }
        if (compressionThreshold == null) {
            systemSetting = SdkSystemSetting.AWS_REQUEST_MIN_COMPRESSION_SIZE_BYTES.getIntegerValue();
            if (systemSetting.isPresent()) {
                compressionThreshold = (Integer)systemSetting.get();
            } else {
                profile = configuration.option(SdkClientOption.PROFILE_FILE_SUPPLIER).get().profile(configuration.option(SdkClientOption.PROFILE_NAME)).orElse(null);
                if (profile != null && (profileSetting = profile.property("request_min_compression_size_bytes")).isPresent()) {
                    compressionThreshold = Integer.parseInt((String)profileSetting.get());
                }
            }
        }
        CompressionConfiguration compressionConfig = (CompressionConfiguration)CompressionConfiguration.builder().requestCompressionEnabled(compressionEnabled).minimumCompressionThresholdInBytes(compressionThreshold).build();
        return configuration.toBuilder().option(SdkClientOption.COMPRESSION_CONFIGURATION, compressionConfig).build();
    }

    private Optional<Boolean> getCompressionEnabled(SdkClientConfiguration configuration) {
        if (configuration.option(SdkClientOption.COMPRESSION_CONFIGURATION) == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(configuration.option(SdkClientOption.COMPRESSION_CONFIGURATION).requestCompressionEnabled());
    }

    private Optional<Integer> getCompressionThreshold(SdkClientConfiguration configuration) {
        if (configuration.option(SdkClientOption.COMPRESSION_CONFIGURATION) == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(configuration.option(SdkClientOption.COMPRESSION_CONFIGURATION).minimumCompressionThresholdInBytes());
    }

    protected SdkClientConfiguration finalizeChildConfiguration(SdkClientConfiguration configuration) {
        return configuration;
    }

    private SdkClientConfiguration finalizeSyncConfiguration(SdkClientConfiguration config) {
        return config.toBuilder().option(SdkClientOption.SYNC_HTTP_CLIENT, this.resolveSyncHttpClient(config)).option(SdkClientOption.CLIENT_TYPE, ClientType.SYNC).build();
    }

    private SdkClientConfiguration finalizeAsyncConfiguration(SdkClientConfiguration config) {
        return config.toBuilder().option(SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR, this.resolveAsyncFutureCompletionExecutor(config)).option(SdkClientOption.ASYNC_HTTP_CLIENT, this.resolveAsyncHttpClient(config)).option(SdkClientOption.CLIENT_TYPE, ClientType.ASYNC).build();
    }

    private SdkClientConfiguration finalizeConfiguration(SdkClientConfiguration config) {
        RetryPolicy retryPolicy = this.resolveRetryPolicy(config);
        return config.toBuilder().option(SdkClientOption.SCHEDULED_EXECUTOR_SERVICE, this.resolveScheduledExecutorService(config)).option(SdkClientOption.EXECUTION_INTERCEPTORS, this.resolveExecutionInterceptors(config)).option(SdkClientOption.RETRY_POLICY, retryPolicy).option(SdkClientOption.CLIENT_USER_AGENT, this.resolveClientUserAgent(config, retryPolicy)).build();
    }

    @SdkPreviewApi
    protected SdkClientConfiguration invokePlugins(SdkClientConfiguration config) {
        return config;
    }

    private String resolveClientUserAgent(SdkClientConfiguration config, RetryPolicy retryPolicy) {
        return ApplyUserAgentStage.resolveClientUserAgent(config.option(SdkAdvancedClientOption.USER_AGENT_PREFIX), config.option(SdkClientOption.INTERNAL_USER_AGENT), config.option(SdkClientOption.CLIENT_TYPE), config.option(SdkClientOption.SYNC_HTTP_CLIENT), config.option(SdkClientOption.ASYNC_HTTP_CLIENT), retryPolicy);
    }

    private RetryPolicy resolveRetryPolicy(SdkClientConfiguration config) {
        RetryPolicy policy = config.option(SdkClientOption.RETRY_POLICY);
        if (policy != null) {
            return policy;
        }
        RetryMode retryMode = RetryMode.resolver().profileFile(config.option(SdkClientOption.PROFILE_FILE_SUPPLIER)).profileName(config.option(SdkClientOption.PROFILE_NAME)).defaultRetryMode(config.option(SdkClientOption.DEFAULT_RETRY_MODE)).resolve();
        return RetryPolicy.forRetryMode(retryMode);
    }

    private SdkHttpClient resolveSyncHttpClient(SdkClientConfiguration config) {
        Validate.isTrue((config.option(SdkClientOption.SYNC_HTTP_CLIENT) == null || this.httpClientBuilder == null ? 1 : 0) != 0, (String)"The httpClient and the httpClientBuilder can't both be configured.", (Object[])new Object[0]);
        return Either.fromNullable((Object)config.option(SdkClientOption.SYNC_HTTP_CLIENT), (Object)this.httpClientBuilder).map(e -> (SdkHttpClient)e.map(x$0 -> new NonManagedSdkHttpClient((SdkHttpClient)x$0), b -> b.buildWithDefaults(this.childHttpConfig(config)))).orElseGet(() -> this.defaultHttpClientBuilder.buildWithDefaults(this.childHttpConfig(config)));
    }

    private SdkAsyncHttpClient resolveAsyncHttpClient(SdkClientConfiguration config) {
        Validate.isTrue((config.option(SdkClientOption.ASYNC_HTTP_CLIENT) == null || this.asyncHttpClientBuilder == null ? 1 : 0) != 0, (String)"The asyncHttpClient and the asyncHttpClientBuilder can't both be configured.", (Object[])new Object[0]);
        return Either.fromNullable((Object)config.option(SdkClientOption.ASYNC_HTTP_CLIENT), (Object)this.asyncHttpClientBuilder).map(e -> (SdkAsyncHttpClient)e.map(NonManagedSdkAsyncHttpClient::new, b -> b.buildWithDefaults(this.childHttpConfig(config)))).orElseGet(() -> this.defaultAsyncHttpClientBuilder.buildWithDefaults(this.childHttpConfig(config)));
    }

    protected AttributeMap childHttpConfig(SdkClientConfiguration configuration) {
        return this.childHttpConfig();
    }

    @Deprecated
    protected AttributeMap childHttpConfig() {
        return AttributeMap.empty();
    }

    private Executor resolveAsyncFutureCompletionExecutor(SdkClientConfiguration config) {
        Supplier<Executor> defaultExecutor = () -> {
            int processors = Runtime.getRuntime().availableProcessors();
            int corePoolSize = Math.max(8, processors);
            int maxPoolSize = Math.max(64, processors * 2);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000), new ThreadFactoryBuilder().threadNamePrefix("sdk-async-response").build());
            executor.allowCoreThreadTimeOut(true);
            return executor;
        };
        return Optional.ofNullable(config.option(SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR)).orElseGet(defaultExecutor);
    }

    private ScheduledExecutorService resolveScheduledExecutorService(SdkClientConfiguration config) {
        Supplier<ScheduledExecutorService> defaultScheduledExecutor = () -> {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(5, new ThreadFactoryBuilder().threadNamePrefix("sdk-ScheduledExecutor").build());
            return executor;
        };
        return Optional.ofNullable(config.option(SdkClientOption.SCHEDULED_EXECUTOR_SERVICE)).map(ScheduledExecutorUtils::unmanagedScheduledExecutor).orElseGet(defaultScheduledExecutor);
    }

    private List<ExecutionInterceptor> resolveExecutionInterceptors(SdkClientConfiguration config) {
        ArrayList<ExecutionInterceptor> globalInterceptors = new ArrayList<ExecutionInterceptor>();
        globalInterceptors.addAll(this.sdkInterceptors());
        globalInterceptors.addAll(new ClasspathInterceptorChainFactory().getGlobalInterceptors());
        return CollectionUtils.mergeLists(globalInterceptors, config.option(SdkClientOption.EXECUTION_INTERCEPTORS));
    }

    private List<ExecutionInterceptor> sdkInterceptors() {
        return Collections.unmodifiableList(Arrays.asList(new HttpChecksumValidationInterceptor()));
    }

    @Override
    public final B endpointOverride(URI endpointOverride) {
        if (endpointOverride == null) {
            this.clientConfiguration.option(SdkClientOption.ENDPOINT, null);
            this.clientConfiguration.option(SdkClientOption.ENDPOINT_OVERRIDDEN, false);
        } else {
            Validate.paramNotNull((Object)endpointOverride.getScheme(), (String)"The URI scheme of endpointOverride");
            this.clientConfiguration.option(SdkClientOption.ENDPOINT, endpointOverride);
            this.clientConfiguration.option(SdkClientOption.ENDPOINT_OVERRIDDEN, true);
        }
        return this.thisBuilder();
    }

    public final void setEndpointOverride(URI endpointOverride) {
        this.endpointOverride(endpointOverride);
    }

    public final B asyncConfiguration(ClientAsyncConfiguration asyncConfiguration) {
        this.clientConfiguration.option(SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR, asyncConfiguration.advancedOption(SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR));
        return this.thisBuilder();
    }

    public final void setAsyncConfiguration(ClientAsyncConfiguration asyncConfiguration) {
        this.asyncConfiguration(asyncConfiguration);
    }

    @Override
    public final B overrideConfiguration(ClientOverrideConfiguration overrideConfig) {
        this.clientOverrideConfiguration = overrideConfig;
        return this.thisBuilder();
    }

    public final void setOverrideConfiguration(ClientOverrideConfiguration overrideConfiguration) {
        this.overrideConfiguration(overrideConfiguration);
    }

    @Override
    public final ClientOverrideConfiguration overrideConfiguration() {
        if (this.clientOverrideConfiguration == null) {
            return (ClientOverrideConfiguration)ClientOverrideConfiguration.builder().build();
        }
        return this.clientOverrideConfiguration;
    }

    public final B httpClient(SdkHttpClient httpClient) {
        this.clientConfiguration.option(SdkClientOption.SYNC_HTTP_CLIENT, httpClient);
        return this.thisBuilder();
    }

    public final B httpClientBuilder(SdkHttpClient.Builder httpClientBuilder) {
        this.httpClientBuilder = httpClientBuilder;
        return this.thisBuilder();
    }

    public final B httpClient(SdkAsyncHttpClient httpClient) {
        this.clientConfiguration.option(SdkClientOption.ASYNC_HTTP_CLIENT, httpClient);
        return this.thisBuilder();
    }

    public final B httpClientBuilder(SdkAsyncHttpClient.Builder httpClientBuilder) {
        this.asyncHttpClientBuilder = httpClientBuilder;
        return this.thisBuilder();
    }

    public final B metricPublishers(List<MetricPublisher> metricPublishers) {
        this.clientConfiguration.option(SdkClientOption.METRIC_PUBLISHERS, metricPublishers);
        return this.thisBuilder();
    }

    @Override
    public final B addPlugin(SdkPlugin plugin) {
        this.plugins.add((SdkPlugin)Validate.paramNotNull((Object)plugin, (String)"plugin"));
        return this.thisBuilder();
    }

    @Override
    public final List<SdkPlugin> plugins() {
        return this.plugins;
    }

    protected B thisBuilder() {
        return (B)this;
    }

    private static /* synthetic */ void lambda$mergeGlobalDefaults$3(Supplier profileFileSupplier, SdkClientConfiguration.Builder c) {
        c.option(SdkClientOption.EXECUTION_INTERCEPTORS, new ArrayList()).option(SdkClientOption.ADDITIONAL_HTTP_HEADERS, new LinkedHashMap()).option(SdkClientOption.PROFILE_FILE, profileFileSupplier.get()).option(SdkClientOption.PROFILE_FILE_SUPPLIER, profileFileSupplier).option(SdkClientOption.PROFILE_NAME, ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow()).option(SdkAdvancedClientOption.USER_AGENT_PREFIX, SdkUserAgent.create().userAgent()).option(SdkAdvancedClientOption.USER_AGENT_SUFFIX, "").option(SdkClientOption.CRC32_FROM_COMPRESSED_DATA_ENABLED, false).option(SdkClientOption.IDENTITY_PROVIDERS, IdentityProviders.builder().build());
    }

    @SdkTestInternalApi
    public static final class NonManagedSdkAsyncHttpClient
    implements SdkAsyncHttpClient {
        private final SdkAsyncHttpClient delegate;

        NonManagedSdkAsyncHttpClient(SdkAsyncHttpClient delegate) {
            this.delegate = (SdkAsyncHttpClient)Validate.paramNotNull((Object)delegate, (String)"SdkAsyncHttpClient");
        }

        public CompletableFuture<Void> execute(AsyncExecuteRequest request) {
            return this.delegate.execute(request);
        }

        public String clientName() {
            return this.delegate.clientName();
        }

        public void close() {
        }
    }

    @SdkTestInternalApi
    public static final class NonManagedSdkHttpClient
    implements SdkHttpClient {
        private final SdkHttpClient delegate;

        private NonManagedSdkHttpClient(SdkHttpClient delegate) {
            this.delegate = (SdkHttpClient)Validate.paramNotNull((Object)delegate, (String)"SdkHttpClient");
        }

        public ExecutableHttpRequest prepareRequest(HttpExecuteRequest request) {
            return this.delegate.prepareRequest(request);
        }

        public void close() {
        }

        public String clientName() {
            return this.delegate.clientName();
        }
    }
}

