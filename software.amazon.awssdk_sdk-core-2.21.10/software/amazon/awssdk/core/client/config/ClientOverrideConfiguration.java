/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ToBuilderIgnoreField
 *  software.amazon.awssdk.metrics.MetricPublisher
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.core.client.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ToBuilderIgnoreField;
import software.amazon.awssdk.core.CompressionConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class ClientOverrideConfiguration
implements ToCopyableBuilder<Builder, ClientOverrideConfiguration> {
    private final Map<String, List<String>> headers;
    private final RetryPolicy retryPolicy;
    private final List<ExecutionInterceptor> executionInterceptors;
    private final AttributeMap advancedOptions;
    private final Duration apiCallAttemptTimeout;
    private final Duration apiCallTimeout;
    private final ProfileFile defaultProfileFile;
    private final String defaultProfileName;
    private final List<MetricPublisher> metricPublishers;
    private final ExecutionAttributes executionAttributes;
    private final ScheduledExecutorService scheduledExecutorService;
    private final CompressionConfiguration compressionConfiguration;

    private ClientOverrideConfiguration(Builder builder) {
        this.headers = CollectionUtils.deepUnmodifiableMap(builder.headers(), () -> new TreeMap(String.CASE_INSENSITIVE_ORDER));
        this.retryPolicy = builder.retryPolicy();
        this.executionInterceptors = Collections.unmodifiableList(new ArrayList<ExecutionInterceptor>(builder.executionInterceptors()));
        this.advancedOptions = builder.advancedOptions();
        this.apiCallTimeout = Validate.isPositiveOrNull((Duration)builder.apiCallTimeout(), (String)"apiCallTimeout");
        this.apiCallAttemptTimeout = Validate.isPositiveOrNull((Duration)builder.apiCallAttemptTimeout(), (String)"apiCallAttemptTimeout");
        this.defaultProfileFile = builder.defaultProfileFile();
        this.defaultProfileName = builder.defaultProfileName();
        this.metricPublishers = Collections.unmodifiableList(new ArrayList<MetricPublisher>(builder.metricPublishers()));
        this.executionAttributes = ExecutionAttributes.unmodifiableExecutionAttributes(builder.executionAttributes());
        this.scheduledExecutorService = builder.scheduledExecutorService();
        this.compressionConfiguration = builder.compressionConfiguration();
    }

    @ToBuilderIgnoreField(value={"advancedOptions"})
    public Builder toBuilder() {
        return new DefaultClientOverrideConfigurationBuilder().advancedOptions(this.advancedOptions.toBuilder()).headers(this.headers).retryPolicy(this.retryPolicy).apiCallTimeout(this.apiCallTimeout).apiCallAttemptTimeout(this.apiCallAttemptTimeout).executionInterceptors(this.executionInterceptors).defaultProfileFile(this.defaultProfileFile).defaultProfileName(this.defaultProfileName).executionAttributes(this.executionAttributes).metricPublishers(this.metricPublishers).scheduledExecutorService(this.scheduledExecutorService).compressionConfiguration(this.compressionConfiguration);
    }

    public static Builder builder() {
        return new DefaultClientOverrideConfigurationBuilder();
    }

    public Map<String, List<String>> headers() {
        return this.headers;
    }

    public Optional<RetryPolicy> retryPolicy() {
        return Optional.ofNullable(this.retryPolicy);
    }

    public <T> Optional<T> advancedOption(SdkAdvancedClientOption<T> option) {
        return Optional.ofNullable(this.advancedOptions.get(option));
    }

    public List<ExecutionInterceptor> executionInterceptors() {
        return this.executionInterceptors;
    }

    public Optional<ScheduledExecutorService> scheduledExecutorService() {
        return Optional.ofNullable(this.scheduledExecutorService);
    }

    public Optional<Duration> apiCallTimeout() {
        return Optional.ofNullable(this.apiCallTimeout);
    }

    public Optional<Duration> apiCallAttemptTimeout() {
        return Optional.ofNullable(this.apiCallAttemptTimeout);
    }

    public Optional<ProfileFile> defaultProfileFile() {
        return Optional.ofNullable(this.defaultProfileFile);
    }

    public Optional<String> defaultProfileName() {
        return Optional.ofNullable(this.defaultProfileName);
    }

    public List<MetricPublisher> metricPublishers() {
        return this.metricPublishers;
    }

    public ExecutionAttributes executionAttributes() {
        return this.executionAttributes;
    }

    public Optional<CompressionConfiguration> compressionConfiguration() {
        return Optional.ofNullable(this.compressionConfiguration);
    }

    public String toString() {
        return ToString.builder((String)"ClientOverrideConfiguration").add("headers", this.headers).add("retryPolicy", (Object)this.retryPolicy).add("apiCallTimeout", (Object)this.apiCallTimeout).add("apiCallAttemptTimeout", (Object)this.apiCallAttemptTimeout).add("executionInterceptors", this.executionInterceptors).add("advancedOptions", (Object)this.advancedOptions).add("profileFile", (Object)this.defaultProfileFile).add("profileName", (Object)this.defaultProfileName).add("scheduledExecutorService", (Object)this.scheduledExecutorService).add("compressionConfiguration", (Object)this.compressionConfiguration).build();
    }

    private static final class DefaultClientOverrideConfigurationBuilder
    implements Builder {
        private Map<String, List<String>> headers = new HashMap<String, List<String>>();
        private RetryPolicy retryPolicy;
        private List<ExecutionInterceptor> executionInterceptors = new ArrayList<ExecutionInterceptor>();
        private AttributeMap.Builder advancedOptions = AttributeMap.builder();
        private Duration apiCallTimeout;
        private Duration apiCallAttemptTimeout;
        private ProfileFile defaultProfileFile;
        private String defaultProfileName;
        private List<MetricPublisher> metricPublishers = new ArrayList<MetricPublisher>();
        private ExecutionAttributes.Builder executionAttributes = ExecutionAttributes.builder();
        private ScheduledExecutorService scheduledExecutorService;
        private CompressionConfiguration compressionConfiguration;

        private DefaultClientOverrideConfigurationBuilder() {
        }

        @Override
        public Builder headers(Map<String, List<String>> headers) {
            Validate.paramNotNull(headers, (String)"headers");
            this.headers = CollectionUtils.deepCopyMap(headers, () -> new TreeMap(String.CASE_INSENSITIVE_ORDER));
            return this;
        }

        public void setHeaders(Map<String, List<String>> additionalHttpHeaders) {
            this.headers(additionalHttpHeaders);
        }

        @Override
        public Map<String, List<String>> headers() {
            return CollectionUtils.unmodifiableMapOfLists(this.headers);
        }

        @Override
        public Builder putHeader(String header, List<String> values) {
            Validate.paramNotNull((Object)header, (String)"header");
            Validate.paramNotNull(values, (String)"values");
            this.headers.put(header, new ArrayList<String>(values));
            return this;
        }

        @Override
        public Builder retryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public void setRetryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy(retryPolicy);
        }

        @Override
        public RetryPolicy retryPolicy() {
            return this.retryPolicy;
        }

        @Override
        public Builder executionInterceptors(List<ExecutionInterceptor> executionInterceptors) {
            Validate.paramNotNull(executionInterceptors, (String)"executionInterceptors");
            this.executionInterceptors = new ArrayList<ExecutionInterceptor>(executionInterceptors);
            return this;
        }

        @Override
        public Builder addExecutionInterceptor(ExecutionInterceptor executionInterceptor) {
            this.executionInterceptors.add(executionInterceptor);
            return this;
        }

        public void setExecutionInterceptors(List<ExecutionInterceptor> executionInterceptors) {
            this.executionInterceptors(executionInterceptors);
        }

        @Override
        public List<ExecutionInterceptor> executionInterceptors() {
            return Collections.unmodifiableList(this.executionInterceptors);
        }

        @Override
        public ScheduledExecutorService scheduledExecutorService() {
            return this.scheduledExecutorService;
        }

        @Override
        public Builder scheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
            this.scheduledExecutorService = scheduledExecutorService;
            return this;
        }

        @Override
        public <T> Builder putAdvancedOption(SdkAdvancedClientOption<T> option, T value) {
            this.advancedOptions.put(option, value);
            return this;
        }

        @Override
        public Builder advancedOptions(Map<SdkAdvancedClientOption<?>, ?> advancedOptions) {
            this.advancedOptions = AttributeMap.builder();
            this.advancedOptions.putAll(advancedOptions);
            return this;
        }

        private Builder advancedOptions(AttributeMap.Builder attributeMap) {
            this.advancedOptions = attributeMap;
            return this;
        }

        public void setAdvancedOptions(Map<SdkAdvancedClientOption<?>, Object> advancedOptions) {
            this.advancedOptions(advancedOptions);
        }

        @Override
        public AttributeMap advancedOptions() {
            return this.advancedOptions.build();
        }

        @Override
        public Builder apiCallTimeout(Duration apiCallTimeout) {
            this.apiCallTimeout = apiCallTimeout;
            return this;
        }

        public void setApiCallTimeout(Duration apiCallTimeout) {
            this.apiCallTimeout(apiCallTimeout);
        }

        @Override
        public Duration apiCallTimeout() {
            return this.apiCallTimeout;
        }

        @Override
        public Builder apiCallAttemptTimeout(Duration apiCallAttemptTimeout) {
            this.apiCallAttemptTimeout = apiCallAttemptTimeout;
            return this;
        }

        public void setApiCallAttemptTimeout(Duration apiCallAttemptTimeout) {
            this.apiCallAttemptTimeout(apiCallAttemptTimeout);
        }

        @Override
        public Duration apiCallAttemptTimeout() {
            return this.apiCallAttemptTimeout;
        }

        @Override
        public ProfileFile defaultProfileFile() {
            return this.defaultProfileFile;
        }

        @Override
        public Builder defaultProfileFile(ProfileFile defaultProfileFile) {
            this.defaultProfileFile = defaultProfileFile;
            return this;
        }

        @Override
        public String defaultProfileName() {
            return this.defaultProfileName;
        }

        @Override
        public Builder defaultProfileName(String defaultProfileName) {
            this.defaultProfileName = defaultProfileName;
            return this;
        }

        @Override
        public Builder metricPublishers(List<MetricPublisher> metricPublishers) {
            Validate.paramNotNull(metricPublishers, (String)"metricPublishers");
            this.metricPublishers = new ArrayList<MetricPublisher>(metricPublishers);
            return this;
        }

        public void setMetricPublishers(List<MetricPublisher> metricPublishers) {
            this.metricPublishers(metricPublishers);
        }

        @Override
        public Builder addMetricPublisher(MetricPublisher metricPublisher) {
            Validate.paramNotNull((Object)metricPublisher, (String)"metricPublisher");
            this.metricPublishers.add(metricPublisher);
            return this;
        }

        @Override
        public List<MetricPublisher> metricPublishers() {
            return Collections.unmodifiableList(this.metricPublishers);
        }

        @Override
        public Builder executionAttributes(ExecutionAttributes executionAttributes) {
            Validate.paramNotNull((Object)executionAttributes, (String)"executionAttributes");
            this.executionAttributes = executionAttributes.toBuilder();
            return this;
        }

        @Override
        public <T> Builder putExecutionAttribute(ExecutionAttribute<T> executionAttribute, T value) {
            this.executionAttributes.put(executionAttribute, value);
            return this;
        }

        @Override
        public ExecutionAttributes executionAttributes() {
            return this.executionAttributes.build();
        }

        @Override
        public Builder compressionConfiguration(CompressionConfiguration compressionConfiguration) {
            this.compressionConfiguration = compressionConfiguration;
            return this;
        }

        public void setRequestCompressionEnabled(CompressionConfiguration compressionConfiguration) {
            this.compressionConfiguration(compressionConfiguration);
        }

        @Override
        public CompressionConfiguration compressionConfiguration() {
            return this.compressionConfiguration;
        }

        public ClientOverrideConfiguration build() {
            return new ClientOverrideConfiguration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, ClientOverrideConfiguration> {
        default public Builder putHeader(String name, String value) {
            this.putHeader(name, Collections.singletonList(value));
            return this;
        }

        public Builder putHeader(String var1, List<String> var2);

        public Builder headers(Map<String, List<String>> var1);

        public Map<String, List<String>> headers();

        public Builder retryPolicy(RetryPolicy var1);

        default public Builder retryPolicy(Consumer<RetryPolicy.Builder> retryPolicy) {
            return this.retryPolicy(((RetryPolicy.Builder)RetryPolicy.builder().applyMutation(retryPolicy)).build());
        }

        default public Builder retryPolicy(RetryMode retryMode) {
            return this.retryPolicy(RetryPolicy.forRetryMode(retryMode));
        }

        public RetryPolicy retryPolicy();

        public Builder executionInterceptors(List<ExecutionInterceptor> var1);

        public Builder addExecutionInterceptor(ExecutionInterceptor var1);

        public List<ExecutionInterceptor> executionInterceptors();

        public Builder scheduledExecutorService(ScheduledExecutorService var1);

        public ScheduledExecutorService scheduledExecutorService();

        public <T> Builder putAdvancedOption(SdkAdvancedClientOption<T> var1, T var2);

        public Builder advancedOptions(Map<SdkAdvancedClientOption<?>, ?> var1);

        public AttributeMap advancedOptions();

        public Builder apiCallTimeout(Duration var1);

        public Duration apiCallTimeout();

        public Builder apiCallAttemptTimeout(Duration var1);

        public Duration apiCallAttemptTimeout();

        public Builder defaultProfileFile(ProfileFile var1);

        public ProfileFile defaultProfileFile();

        public Builder defaultProfileName(String var1);

        public String defaultProfileName();

        public Builder metricPublishers(List<MetricPublisher> var1);

        public Builder addMetricPublisher(MetricPublisher var1);

        public List<MetricPublisher> metricPublishers();

        public Builder executionAttributes(ExecutionAttributes var1);

        public <T> Builder putExecutionAttribute(ExecutionAttribute<T> var1, T var2);

        public ExecutionAttributes executionAttributes();

        public Builder compressionConfiguration(CompressionConfiguration var1);

        default public Builder compressionConfiguration(Consumer<CompressionConfiguration.Builder> compressionConfiguration) {
            return this.compressionConfiguration((CompressionConfiguration)((CompressionConfiguration.Builder)CompressionConfiguration.builder().applyMutation(compressionConfiguration)).build());
        }

        public CompressionConfiguration compressionConfiguration();
    }
}

