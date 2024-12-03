/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.PayloadTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.NotificationConfiguration;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class PutBucketNotificationConfigurationRequest
extends S3Request
implements ToCopyableBuilder<Builder, PutBucketNotificationConfigurationRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(PutBucketNotificationConfigurationRequest.getter(PutBucketNotificationConfigurationRequest::bucket)).setter(PutBucketNotificationConfigurationRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<NotificationConfiguration> NOTIFICATION_CONFIGURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("NotificationConfiguration").getter(PutBucketNotificationConfigurationRequest.getter(PutBucketNotificationConfigurationRequest::notificationConfiguration)).setter(PutBucketNotificationConfigurationRequest.setter(Builder::notificationConfiguration)).constructor(NotificationConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NotificationConfiguration").unmarshallLocationName("NotificationConfiguration").build(), PayloadTrait.create(), RequiredTrait.create()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(PutBucketNotificationConfigurationRequest.getter(PutBucketNotificationConfigurationRequest::expectedBucketOwner)).setter(PutBucketNotificationConfigurationRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final SdkField<Boolean> SKIP_DESTINATION_VALIDATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("SkipDestinationValidation").getter(PutBucketNotificationConfigurationRequest.getter(PutBucketNotificationConfigurationRequest::skipDestinationValidation)).setter(PutBucketNotificationConfigurationRequest.setter(Builder::skipDestinationValidation)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-skip-destination-validation").unmarshallLocationName("x-amz-skip-destination-validation").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, NOTIFICATION_CONFIGURATION_FIELD, EXPECTED_BUCKET_OWNER_FIELD, SKIP_DESTINATION_VALIDATION_FIELD));
    private final String bucket;
    private final NotificationConfiguration notificationConfiguration;
    private final String expectedBucketOwner;
    private final Boolean skipDestinationValidation;

    private PutBucketNotificationConfigurationRequest(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.notificationConfiguration = builder.notificationConfiguration;
        this.expectedBucketOwner = builder.expectedBucketOwner;
        this.skipDestinationValidation = builder.skipDestinationValidation;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final NotificationConfiguration notificationConfiguration() {
        return this.notificationConfiguration;
    }

    public final String expectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    public final Boolean skipDestinationValidation() {
        return this.skipDestinationValidation;
    }

    @Override
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
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.notificationConfiguration());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
        hashCode = 31 * hashCode + Objects.hashCode(this.skipDestinationValidation());
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
        if (!(obj instanceof PutBucketNotificationConfigurationRequest)) {
            return false;
        }
        PutBucketNotificationConfigurationRequest other = (PutBucketNotificationConfigurationRequest)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.notificationConfiguration(), other.notificationConfiguration()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner()) && Objects.equals(this.skipDestinationValidation(), other.skipDestinationValidation());
    }

    public final String toString() {
        return ToString.builder((String)"PutBucketNotificationConfigurationRequest").add("Bucket", (Object)this.bucket()).add("NotificationConfiguration", (Object)this.notificationConfiguration()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).add("SkipDestinationValidation", (Object)this.skipDestinationValidation()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "NotificationConfiguration": {
                return Optional.ofNullable(clazz.cast(this.notificationConfiguration()));
            }
            case "ExpectedBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedBucketOwner()));
            }
            case "SkipDestinationValidation": {
                return Optional.ofNullable(clazz.cast(this.skipDestinationValidation()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<PutBucketNotificationConfigurationRequest, T> g) {
        return obj -> g.apply((PutBucketNotificationConfigurationRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String bucket;
        private NotificationConfiguration notificationConfiguration;
        private String expectedBucketOwner;
        private Boolean skipDestinationValidation;

        private BuilderImpl() {
        }

        private BuilderImpl(PutBucketNotificationConfigurationRequest model) {
            super(model);
            this.bucket(model.bucket);
            this.notificationConfiguration(model.notificationConfiguration);
            this.expectedBucketOwner(model.expectedBucketOwner);
            this.skipDestinationValidation(model.skipDestinationValidation);
        }

        public final String getBucket() {
            return this.bucket;
        }

        public final void setBucket(String bucket) {
            this.bucket = bucket;
        }

        @Override
        public final Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public final NotificationConfiguration.Builder getNotificationConfiguration() {
            return this.notificationConfiguration != null ? this.notificationConfiguration.toBuilder() : null;
        }

        public final void setNotificationConfiguration(NotificationConfiguration.BuilderImpl notificationConfiguration) {
            this.notificationConfiguration = notificationConfiguration != null ? notificationConfiguration.build() : null;
        }

        @Override
        public final Builder notificationConfiguration(NotificationConfiguration notificationConfiguration) {
            this.notificationConfiguration = notificationConfiguration;
            return this;
        }

        public final String getExpectedBucketOwner() {
            return this.expectedBucketOwner;
        }

        public final void setExpectedBucketOwner(String expectedBucketOwner) {
            this.expectedBucketOwner = expectedBucketOwner;
        }

        @Override
        public final Builder expectedBucketOwner(String expectedBucketOwner) {
            this.expectedBucketOwner = expectedBucketOwner;
            return this;
        }

        public final Boolean getSkipDestinationValidation() {
            return this.skipDestinationValidation;
        }

        public final void setSkipDestinationValidation(Boolean skipDestinationValidation) {
            this.skipDestinationValidation = skipDestinationValidation;
        }

        @Override
        public final Builder skipDestinationValidation(Boolean skipDestinationValidation) {
            this.skipDestinationValidation = skipDestinationValidation;
            return this;
        }

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration overrideConfiguration) {
            super.overrideConfiguration(overrideConfiguration);
            return this;
        }

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> builderConsumer) {
            super.overrideConfiguration(builderConsumer);
            return this;
        }

        @Override
        public PutBucketNotificationConfigurationRequest build() {
            return new PutBucketNotificationConfigurationRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, PutBucketNotificationConfigurationRequest> {
        public Builder bucket(String var1);

        public Builder notificationConfiguration(NotificationConfiguration var1);

        default public Builder notificationConfiguration(Consumer<NotificationConfiguration.Builder> notificationConfiguration) {
            return this.notificationConfiguration((NotificationConfiguration)((NotificationConfiguration.Builder)NotificationConfiguration.builder().applyMutation(notificationConfiguration)).build());
        }

        public Builder expectedBucketOwner(String var1);

        public Builder skipDestinationValidation(Boolean var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

