/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.EventBridgeConfiguration;
import software.amazon.awssdk.services.s3.model.LambdaFunctionConfiguration;
import software.amazon.awssdk.services.s3.model.LambdaFunctionConfigurationListCopier;
import software.amazon.awssdk.services.s3.model.QueueConfiguration;
import software.amazon.awssdk.services.s3.model.QueueConfigurationListCopier;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.services.s3.model.TopicConfiguration;
import software.amazon.awssdk.services.s3.model.TopicConfigurationListCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketNotificationConfigurationResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketNotificationConfigurationResponse> {
    private static final SdkField<List<TopicConfiguration>> TOPIC_CONFIGURATIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("TopicConfigurations").getter(GetBucketNotificationConfigurationResponse.getter(GetBucketNotificationConfigurationResponse::topicConfigurations)).setter(GetBucketNotificationConfigurationResponse.setter(Builder::topicConfigurations)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("TopicConfiguration").unmarshallLocationName("TopicConfiguration").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(TopicConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<List<QueueConfiguration>> QUEUE_CONFIGURATIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("QueueConfigurations").getter(GetBucketNotificationConfigurationResponse.getter(GetBucketNotificationConfigurationResponse::queueConfigurations)).setter(GetBucketNotificationConfigurationResponse.setter(Builder::queueConfigurations)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("QueueConfiguration").unmarshallLocationName("QueueConfiguration").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(QueueConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<List<LambdaFunctionConfiguration>> LAMBDA_FUNCTION_CONFIGURATIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("LambdaFunctionConfigurations").getter(GetBucketNotificationConfigurationResponse.getter(GetBucketNotificationConfigurationResponse::lambdaFunctionConfigurations)).setter(GetBucketNotificationConfigurationResponse.setter(Builder::lambdaFunctionConfigurations)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CloudFunctionConfiguration").unmarshallLocationName("CloudFunctionConfiguration").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(LambdaFunctionConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<EventBridgeConfiguration> EVENT_BRIDGE_CONFIGURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("EventBridgeConfiguration").getter(GetBucketNotificationConfigurationResponse.getter(GetBucketNotificationConfigurationResponse::eventBridgeConfiguration)).setter(GetBucketNotificationConfigurationResponse.setter(Builder::eventBridgeConfiguration)).constructor(EventBridgeConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("EventBridgeConfiguration").unmarshallLocationName("EventBridgeConfiguration").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(TOPIC_CONFIGURATIONS_FIELD, QUEUE_CONFIGURATIONS_FIELD, LAMBDA_FUNCTION_CONFIGURATIONS_FIELD, EVENT_BRIDGE_CONFIGURATION_FIELD));
    private final List<TopicConfiguration> topicConfigurations;
    private final List<QueueConfiguration> queueConfigurations;
    private final List<LambdaFunctionConfiguration> lambdaFunctionConfigurations;
    private final EventBridgeConfiguration eventBridgeConfiguration;

    private GetBucketNotificationConfigurationResponse(BuilderImpl builder) {
        super(builder);
        this.topicConfigurations = builder.topicConfigurations;
        this.queueConfigurations = builder.queueConfigurations;
        this.lambdaFunctionConfigurations = builder.lambdaFunctionConfigurations;
        this.eventBridgeConfiguration = builder.eventBridgeConfiguration;
    }

    public final boolean hasTopicConfigurations() {
        return this.topicConfigurations != null && !(this.topicConfigurations instanceof SdkAutoConstructList);
    }

    public final List<TopicConfiguration> topicConfigurations() {
        return this.topicConfigurations;
    }

    public final boolean hasQueueConfigurations() {
        return this.queueConfigurations != null && !(this.queueConfigurations instanceof SdkAutoConstructList);
    }

    public final List<QueueConfiguration> queueConfigurations() {
        return this.queueConfigurations;
    }

    public final boolean hasLambdaFunctionConfigurations() {
        return this.lambdaFunctionConfigurations != null && !(this.lambdaFunctionConfigurations instanceof SdkAutoConstructList);
    }

    public final List<LambdaFunctionConfiguration> lambdaFunctionConfigurations() {
        return this.lambdaFunctionConfigurations;
    }

    public final EventBridgeConfiguration eventBridgeConfiguration() {
        return this.eventBridgeConfiguration;
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTopicConfigurations() ? this.topicConfigurations() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.hasQueueConfigurations() ? this.queueConfigurations() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.hasLambdaFunctionConfigurations() ? this.lambdaFunctionConfigurations() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.eventBridgeConfiguration());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GetBucketNotificationConfigurationResponse)) {
            return false;
        }
        GetBucketNotificationConfigurationResponse other = (GetBucketNotificationConfigurationResponse)((Object)obj);
        return this.hasTopicConfigurations() == other.hasTopicConfigurations() && Objects.equals(this.topicConfigurations(), other.topicConfigurations()) && this.hasQueueConfigurations() == other.hasQueueConfigurations() && Objects.equals(this.queueConfigurations(), other.queueConfigurations()) && this.hasLambdaFunctionConfigurations() == other.hasLambdaFunctionConfigurations() && Objects.equals(this.lambdaFunctionConfigurations(), other.lambdaFunctionConfigurations()) && Objects.equals(this.eventBridgeConfiguration(), other.eventBridgeConfiguration());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketNotificationConfigurationResponse").add("TopicConfigurations", this.hasTopicConfigurations() ? this.topicConfigurations() : null).add("QueueConfigurations", this.hasQueueConfigurations() ? this.queueConfigurations() : null).add("LambdaFunctionConfigurations", this.hasLambdaFunctionConfigurations() ? this.lambdaFunctionConfigurations() : null).add("EventBridgeConfiguration", (Object)this.eventBridgeConfiguration()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "TopicConfigurations": {
                return Optional.ofNullable(clazz.cast(this.topicConfigurations()));
            }
            case "QueueConfigurations": {
                return Optional.ofNullable(clazz.cast(this.queueConfigurations()));
            }
            case "LambdaFunctionConfigurations": {
                return Optional.ofNullable(clazz.cast(this.lambdaFunctionConfigurations()));
            }
            case "EventBridgeConfiguration": {
                return Optional.ofNullable(clazz.cast(this.eventBridgeConfiguration()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketNotificationConfigurationResponse, T> g) {
        return obj -> g.apply((GetBucketNotificationConfigurationResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private List<TopicConfiguration> topicConfigurations = DefaultSdkAutoConstructList.getInstance();
        private List<QueueConfiguration> queueConfigurations = DefaultSdkAutoConstructList.getInstance();
        private List<LambdaFunctionConfiguration> lambdaFunctionConfigurations = DefaultSdkAutoConstructList.getInstance();
        private EventBridgeConfiguration eventBridgeConfiguration;

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketNotificationConfigurationResponse model) {
            super(model);
            this.topicConfigurations(model.topicConfigurations);
            this.queueConfigurations(model.queueConfigurations);
            this.lambdaFunctionConfigurations(model.lambdaFunctionConfigurations);
            this.eventBridgeConfiguration(model.eventBridgeConfiguration);
        }

        public final List<TopicConfiguration.Builder> getTopicConfigurations() {
            List<TopicConfiguration.Builder> result = TopicConfigurationListCopier.copyToBuilder(this.topicConfigurations);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setTopicConfigurations(Collection<TopicConfiguration.BuilderImpl> topicConfigurations) {
            this.topicConfigurations = TopicConfigurationListCopier.copyFromBuilder(topicConfigurations);
        }

        @Override
        public final Builder topicConfigurations(Collection<TopicConfiguration> topicConfigurations) {
            this.topicConfigurations = TopicConfigurationListCopier.copy(topicConfigurations);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder topicConfigurations(TopicConfiguration ... topicConfigurations) {
            this.topicConfigurations(Arrays.asList(topicConfigurations));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder topicConfigurations(Consumer<TopicConfiguration.Builder> ... topicConfigurations) {
            this.topicConfigurations(Stream.of(topicConfigurations).map(c -> (TopicConfiguration)((TopicConfiguration.Builder)TopicConfiguration.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final List<QueueConfiguration.Builder> getQueueConfigurations() {
            List<QueueConfiguration.Builder> result = QueueConfigurationListCopier.copyToBuilder(this.queueConfigurations);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setQueueConfigurations(Collection<QueueConfiguration.BuilderImpl> queueConfigurations) {
            this.queueConfigurations = QueueConfigurationListCopier.copyFromBuilder(queueConfigurations);
        }

        @Override
        public final Builder queueConfigurations(Collection<QueueConfiguration> queueConfigurations) {
            this.queueConfigurations = QueueConfigurationListCopier.copy(queueConfigurations);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder queueConfigurations(QueueConfiguration ... queueConfigurations) {
            this.queueConfigurations(Arrays.asList(queueConfigurations));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder queueConfigurations(Consumer<QueueConfiguration.Builder> ... queueConfigurations) {
            this.queueConfigurations(Stream.of(queueConfigurations).map(c -> (QueueConfiguration)((QueueConfiguration.Builder)QueueConfiguration.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final List<LambdaFunctionConfiguration.Builder> getLambdaFunctionConfigurations() {
            List<LambdaFunctionConfiguration.Builder> result = LambdaFunctionConfigurationListCopier.copyToBuilder(this.lambdaFunctionConfigurations);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setLambdaFunctionConfigurations(Collection<LambdaFunctionConfiguration.BuilderImpl> lambdaFunctionConfigurations) {
            this.lambdaFunctionConfigurations = LambdaFunctionConfigurationListCopier.copyFromBuilder(lambdaFunctionConfigurations);
        }

        @Override
        public final Builder lambdaFunctionConfigurations(Collection<LambdaFunctionConfiguration> lambdaFunctionConfigurations) {
            this.lambdaFunctionConfigurations = LambdaFunctionConfigurationListCopier.copy(lambdaFunctionConfigurations);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder lambdaFunctionConfigurations(LambdaFunctionConfiguration ... lambdaFunctionConfigurations) {
            this.lambdaFunctionConfigurations(Arrays.asList(lambdaFunctionConfigurations));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder lambdaFunctionConfigurations(Consumer<LambdaFunctionConfiguration.Builder> ... lambdaFunctionConfigurations) {
            this.lambdaFunctionConfigurations(Stream.of(lambdaFunctionConfigurations).map(c -> (LambdaFunctionConfiguration)((LambdaFunctionConfiguration.Builder)LambdaFunctionConfiguration.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final EventBridgeConfiguration.Builder getEventBridgeConfiguration() {
            return this.eventBridgeConfiguration != null ? this.eventBridgeConfiguration.toBuilder() : null;
        }

        public final void setEventBridgeConfiguration(EventBridgeConfiguration.BuilderImpl eventBridgeConfiguration) {
            this.eventBridgeConfiguration = eventBridgeConfiguration != null ? eventBridgeConfiguration.build() : null;
        }

        @Override
        public final Builder eventBridgeConfiguration(EventBridgeConfiguration eventBridgeConfiguration) {
            this.eventBridgeConfiguration = eventBridgeConfiguration;
            return this;
        }

        @Override
        public GetBucketNotificationConfigurationResponse build() {
            return new GetBucketNotificationConfigurationResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketNotificationConfigurationResponse> {
        public Builder topicConfigurations(Collection<TopicConfiguration> var1);

        public Builder topicConfigurations(TopicConfiguration ... var1);

        public Builder topicConfigurations(Consumer<TopicConfiguration.Builder> ... var1);

        public Builder queueConfigurations(Collection<QueueConfiguration> var1);

        public Builder queueConfigurations(QueueConfiguration ... var1);

        public Builder queueConfigurations(Consumer<QueueConfiguration.Builder> ... var1);

        public Builder lambdaFunctionConfigurations(Collection<LambdaFunctionConfiguration> var1);

        public Builder lambdaFunctionConfigurations(LambdaFunctionConfiguration ... var1);

        public Builder lambdaFunctionConfigurations(Consumer<LambdaFunctionConfiguration.Builder> ... var1);

        public Builder eventBridgeConfiguration(EventBridgeConfiguration var1);

        default public Builder eventBridgeConfiguration(Consumer<EventBridgeConfiguration.Builder> eventBridgeConfiguration) {
            return this.eventBridgeConfiguration((EventBridgeConfiguration)((EventBridgeConfiguration.Builder)EventBridgeConfiguration.builder().applyMutation(eventBridgeConfiguration)).build());
        }
    }
}

