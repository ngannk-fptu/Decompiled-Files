/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPreviewApi
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.endpoints.EndpointProvider
 *  software.amazon.awssdk.metrics.MetricPublisher
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPreviewApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.ApiName;
import software.amazon.awssdk.core.CompressionConfiguration;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.Validate;

@Immutable
@SdkPublicApi
public abstract class RequestOverrideConfiguration {
    private final Map<String, List<String>> headers;
    private final Map<String, List<String>> rawQueryParameters;
    private final List<ApiName> apiNames;
    private final Duration apiCallTimeout;
    private final Duration apiCallAttemptTimeout;
    private final Signer signer;
    private final List<MetricPublisher> metricPublishers;
    private final ExecutionAttributes executionAttributes;
    private final EndpointProvider endpointProvider;
    private final CompressionConfiguration compressionConfiguration;
    private final List<SdkPlugin> plugins;

    protected RequestOverrideConfiguration(Builder<?> builder) {
        this.headers = CollectionUtils.deepUnmodifiableMap(builder.headers(), () -> new TreeMap(String.CASE_INSENSITIVE_ORDER));
        this.rawQueryParameters = CollectionUtils.deepUnmodifiableMap(builder.rawQueryParameters());
        this.apiNames = Collections.unmodifiableList(new ArrayList<ApiName>(builder.apiNames()));
        this.apiCallTimeout = Validate.isPositiveOrNull((Duration)builder.apiCallTimeout(), (String)"apiCallTimeout");
        this.apiCallAttemptTimeout = Validate.isPositiveOrNull((Duration)builder.apiCallAttemptTimeout(), (String)"apiCallAttemptTimeout");
        this.signer = builder.signer();
        this.metricPublishers = Collections.unmodifiableList(new ArrayList<MetricPublisher>(builder.metricPublishers()));
        this.executionAttributes = ExecutionAttributes.unmodifiableExecutionAttributes(builder.executionAttributes());
        this.endpointProvider = builder.endpointProvider();
        this.compressionConfiguration = builder.compressionConfiguration();
        this.plugins = Collections.unmodifiableList(new ArrayList<SdkPlugin>(builder.plugins()));
    }

    public Map<String, List<String>> headers() {
        return this.headers;
    }

    public Map<String, List<String>> rawQueryParameters() {
        return this.rawQueryParameters;
    }

    public List<ApiName> apiNames() {
        return this.apiNames;
    }

    public Optional<Duration> apiCallTimeout() {
        return Optional.ofNullable(this.apiCallTimeout);
    }

    public Optional<Duration> apiCallAttemptTimeout() {
        return Optional.ofNullable(this.apiCallAttemptTimeout);
    }

    public Optional<Signer> signer() {
        return Optional.ofNullable(this.signer);
    }

    public List<MetricPublisher> metricPublishers() {
        return this.metricPublishers;
    }

    @SdkPreviewApi
    public List<SdkPlugin> plugins() {
        return this.plugins;
    }

    public ExecutionAttributes executionAttributes() {
        return this.executionAttributes;
    }

    public Optional<EndpointProvider> endpointProvider() {
        return Optional.ofNullable(this.endpointProvider);
    }

    public Optional<CompressionConfiguration> compressionConfiguration() {
        return Optional.ofNullable(this.compressionConfiguration);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RequestOverrideConfiguration that = (RequestOverrideConfiguration)o;
        return Objects.equals(this.headers, that.headers) && Objects.equals(this.rawQueryParameters, that.rawQueryParameters) && Objects.equals(this.apiNames, that.apiNames) && Objects.equals(this.apiCallTimeout, that.apiCallTimeout) && Objects.equals(this.apiCallAttemptTimeout, that.apiCallAttemptTimeout) && Objects.equals(this.signer, that.signer) && Objects.equals(this.metricPublishers, that.metricPublishers) && Objects.equals(this.executionAttributes, that.executionAttributes) && Objects.equals(this.endpointProvider, that.endpointProvider) && Objects.equals(this.compressionConfiguration, that.compressionConfiguration) && Objects.equals(this.plugins, that.plugins);
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.headers);
        hashCode = 31 * hashCode + Objects.hashCode(this.rawQueryParameters);
        hashCode = 31 * hashCode + Objects.hashCode(this.apiNames);
        hashCode = 31 * hashCode + Objects.hashCode(this.apiCallTimeout);
        hashCode = 31 * hashCode + Objects.hashCode(this.apiCallAttemptTimeout);
        hashCode = 31 * hashCode + Objects.hashCode(this.signer);
        hashCode = 31 * hashCode + Objects.hashCode(this.metricPublishers);
        hashCode = 31 * hashCode + Objects.hashCode(this.executionAttributes);
        hashCode = 31 * hashCode + Objects.hashCode(this.endpointProvider);
        hashCode = 31 * hashCode + Objects.hashCode(this.compressionConfiguration);
        hashCode = 31 * hashCode + Objects.hashCode(this.plugins);
        return hashCode;
    }

    public abstract Builder<? extends Builder> toBuilder();

    protected static abstract class BuilderImpl<B extends Builder>
    implements Builder<B> {
        private Map<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
        private Map<String, List<String>> rawQueryParameters = new HashMap<String, List<String>>();
        private List<ApiName> apiNames = new ArrayList<ApiName>();
        private Duration apiCallTimeout;
        private Duration apiCallAttemptTimeout;
        private Signer signer;
        private List<MetricPublisher> metricPublishers = new ArrayList<MetricPublisher>();
        private ExecutionAttributes.Builder executionAttributesBuilder = ExecutionAttributes.builder();
        private EndpointProvider endpointProvider;
        private CompressionConfiguration compressionConfiguration;
        private List<SdkPlugin> plugins = new ArrayList<SdkPlugin>();

        protected BuilderImpl() {
        }

        protected BuilderImpl(RequestOverrideConfiguration sdkRequestOverrideConfig) {
            this.headers(sdkRequestOverrideConfig.headers);
            this.rawQueryParameters(sdkRequestOverrideConfig.rawQueryParameters);
            sdkRequestOverrideConfig.apiNames.forEach(this::addApiName);
            this.apiCallTimeout(sdkRequestOverrideConfig.apiCallTimeout);
            this.apiCallAttemptTimeout(sdkRequestOverrideConfig.apiCallAttemptTimeout);
            this.signer(sdkRequestOverrideConfig.signer().orElse(null));
            this.metricPublishers(sdkRequestOverrideConfig.metricPublishers());
            this.executionAttributes(sdkRequestOverrideConfig.executionAttributes());
            this.endpointProvider(sdkRequestOverrideConfig.endpointProvider);
            this.compressionConfiguration(sdkRequestOverrideConfig.compressionConfiguration);
            this.plugins(sdkRequestOverrideConfig.plugins);
        }

        @Override
        public Map<String, List<String>> headers() {
            return CollectionUtils.unmodifiableMapOfLists(this.headers);
        }

        @Override
        public B putHeader(String name, List<String> values) {
            Validate.paramNotNull(values, (String)"values");
            this.headers.put(name, new ArrayList<String>(values));
            return (B)this;
        }

        @Override
        public B headers(Map<String, List<String>> headers) {
            Validate.paramNotNull(headers, (String)"headers");
            this.headers = CollectionUtils.deepCopyMap(headers);
            return (B)this;
        }

        @Override
        public Map<String, List<String>> rawQueryParameters() {
            return CollectionUtils.unmodifiableMapOfLists(this.rawQueryParameters);
        }

        @Override
        public B putRawQueryParameter(String name, List<String> values) {
            Validate.paramNotNull((Object)name, (String)"name");
            Validate.paramNotNull(values, (String)"values");
            this.rawQueryParameters.put(name, new ArrayList<String>(values));
            return (B)this;
        }

        @Override
        public B rawQueryParameters(Map<String, List<String>> rawQueryParameters) {
            Validate.paramNotNull(rawQueryParameters, (String)"rawQueryParameters");
            this.rawQueryParameters = CollectionUtils.deepCopyMap(rawQueryParameters);
            return (B)this;
        }

        @Override
        public List<ApiName> apiNames() {
            return Collections.unmodifiableList(this.apiNames);
        }

        @Override
        public B addApiName(ApiName apiName) {
            this.apiNames.add(apiName);
            return (B)this;
        }

        @Override
        public B addApiName(Consumer<ApiName.Builder> apiNameConsumer) {
            ApiName.Builder b = ApiName.builder();
            apiNameConsumer.accept(b);
            this.addApiName(b.build());
            return (B)this;
        }

        @Override
        public B apiCallTimeout(Duration apiCallTimeout) {
            this.apiCallTimeout = apiCallTimeout;
            return (B)this;
        }

        public void setApiCallTimeout(Duration apiCallTimeout) {
            this.apiCallTimeout(apiCallTimeout);
        }

        @Override
        public Duration apiCallTimeout() {
            return this.apiCallTimeout;
        }

        @Override
        public B apiCallAttemptTimeout(Duration apiCallAttemptTimeout) {
            this.apiCallAttemptTimeout = apiCallAttemptTimeout;
            return (B)this;
        }

        public void setApiCallAttemptTimeout(Duration apiCallAttemptTimeout) {
            this.apiCallAttemptTimeout(apiCallAttemptTimeout);
        }

        @Override
        public Duration apiCallAttemptTimeout() {
            return this.apiCallAttemptTimeout;
        }

        @Override
        public B signer(Signer signer) {
            this.signer = signer;
            return (B)this;
        }

        public void setSigner(Signer signer) {
            this.signer(signer);
        }

        @Override
        public Signer signer() {
            return this.signer;
        }

        @Override
        public B metricPublishers(List<MetricPublisher> metricPublishers) {
            Validate.paramNotNull(metricPublishers, (String)"metricPublishers");
            this.metricPublishers = new ArrayList<MetricPublisher>(metricPublishers);
            return (B)this;
        }

        @Override
        public B addMetricPublisher(MetricPublisher metricPublisher) {
            Validate.paramNotNull((Object)metricPublisher, (String)"metricPublisher");
            this.metricPublishers.add(metricPublisher);
            return (B)this;
        }

        public void setMetricPublishers(List<MetricPublisher> metricPublishers) {
            this.metricPublishers(metricPublishers);
        }

        @Override
        public List<MetricPublisher> metricPublishers() {
            return this.metricPublishers;
        }

        @Override
        public B executionAttributes(ExecutionAttributes executionAttributes) {
            Validate.paramNotNull((Object)executionAttributes, (String)"executionAttributes");
            this.executionAttributesBuilder = executionAttributes.toBuilder();
            return (B)this;
        }

        @Override
        public <T> B putExecutionAttribute(ExecutionAttribute<T> executionAttribute, T value) {
            this.executionAttributesBuilder.put(executionAttribute, value);
            return (B)this;
        }

        @Override
        public ExecutionAttributes executionAttributes() {
            return this.executionAttributesBuilder.build();
        }

        public void setExecutionAttributes(ExecutionAttributes executionAttributes) {
            this.executionAttributes(executionAttributes);
        }

        @Override
        public B endpointProvider(EndpointProvider endpointProvider) {
            this.endpointProvider = endpointProvider;
            return (B)this;
        }

        public void setEndpointProvider(EndpointProvider endpointProvider) {
            this.endpointProvider(endpointProvider);
        }

        @Override
        public EndpointProvider endpointProvider() {
            return this.endpointProvider;
        }

        @Override
        public B compressionConfiguration(CompressionConfiguration compressionConfiguration) {
            this.compressionConfiguration = compressionConfiguration;
            return (B)this;
        }

        @Override
        public B compressionConfiguration(Consumer<CompressionConfiguration.Builder> compressionConfigurationConsumer) {
            CompressionConfiguration.Builder b = CompressionConfiguration.builder();
            compressionConfigurationConsumer.accept(b);
            this.compressionConfiguration((CompressionConfiguration)b.build());
            return (B)this;
        }

        @Override
        public CompressionConfiguration compressionConfiguration() {
            return this.compressionConfiguration;
        }

        @Override
        public B plugins(List<SdkPlugin> plugins) {
            this.plugins = new ArrayList<SdkPlugin>(plugins);
            return (B)this;
        }

        @Override
        public B addPlugin(SdkPlugin plugin) {
            this.plugins.add(plugin);
            return (B)this;
        }

        @Override
        public List<SdkPlugin> plugins() {
            return Collections.unmodifiableList(this.plugins);
        }
    }

    public static interface Builder<B extends Builder> {
        public Map<String, List<String>> headers();

        default public B putHeader(String name, String value) {
            this.putHeader(name, Collections.singletonList(value));
            return (B)this;
        }

        public B putHeader(String var1, List<String> var2);

        public B headers(Map<String, List<String>> var1);

        public Map<String, List<String>> rawQueryParameters();

        default public B putRawQueryParameter(String name, String value) {
            this.putRawQueryParameter(name, Collections.singletonList(value));
            return (B)this;
        }

        public B putRawQueryParameter(String var1, List<String> var2);

        public B rawQueryParameters(Map<String, List<String>> var1);

        public List<ApiName> apiNames();

        public B addApiName(ApiName var1);

        public B addApiName(Consumer<ApiName.Builder> var1);

        public B apiCallTimeout(Duration var1);

        public Duration apiCallTimeout();

        public B apiCallAttemptTimeout(Duration var1);

        public Duration apiCallAttemptTimeout();

        public B signer(Signer var1);

        public Signer signer();

        public B metricPublishers(List<MetricPublisher> var1);

        public B addMetricPublisher(MetricPublisher var1);

        public List<MetricPublisher> metricPublishers();

        public B executionAttributes(ExecutionAttributes var1);

        public <T> B putExecutionAttribute(ExecutionAttribute<T> var1, T var2);

        public ExecutionAttributes executionAttributes();

        public B endpointProvider(EndpointProvider var1);

        public EndpointProvider endpointProvider();

        public B compressionConfiguration(CompressionConfiguration var1);

        public B compressionConfiguration(Consumer<CompressionConfiguration.Builder> var1);

        public CompressionConfiguration compressionConfiguration();

        @SdkPreviewApi
        public B plugins(List<SdkPlugin> var1);

        @SdkPreviewApi
        public B addPlugin(SdkPlugin var1);

        @SdkPreviewApi
        public List<SdkPlugin> plugins();

        public RequestOverrideConfiguration build();
    }
}

