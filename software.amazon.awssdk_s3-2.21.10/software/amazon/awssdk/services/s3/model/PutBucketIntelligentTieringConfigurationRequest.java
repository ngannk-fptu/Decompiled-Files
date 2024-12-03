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
import software.amazon.awssdk.services.s3.model.IntelligentTieringConfiguration;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class PutBucketIntelligentTieringConfigurationRequest
extends S3Request
implements ToCopyableBuilder<Builder, PutBucketIntelligentTieringConfigurationRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(PutBucketIntelligentTieringConfigurationRequest.getter(PutBucketIntelligentTieringConfigurationRequest::bucket)).setter(PutBucketIntelligentTieringConfigurationRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Id").getter(PutBucketIntelligentTieringConfigurationRequest.getter(PutBucketIntelligentTieringConfigurationRequest::id)).setter(PutBucketIntelligentTieringConfigurationRequest.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("id").unmarshallLocationName("id").build(), RequiredTrait.create()}).build();
    private static final SdkField<IntelligentTieringConfiguration> INTELLIGENT_TIERING_CONFIGURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("IntelligentTieringConfiguration").getter(PutBucketIntelligentTieringConfigurationRequest.getter(PutBucketIntelligentTieringConfigurationRequest::intelligentTieringConfiguration)).setter(PutBucketIntelligentTieringConfigurationRequest.setter(Builder::intelligentTieringConfiguration)).constructor(IntelligentTieringConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IntelligentTieringConfiguration").unmarshallLocationName("IntelligentTieringConfiguration").build(), PayloadTrait.create(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, ID_FIELD, INTELLIGENT_TIERING_CONFIGURATION_FIELD));
    private final String bucket;
    private final String id;
    private final IntelligentTieringConfiguration intelligentTieringConfiguration;

    private PutBucketIntelligentTieringConfigurationRequest(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.id = builder.id;
        this.intelligentTieringConfiguration = builder.intelligentTieringConfiguration;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String id() {
        return this.id;
    }

    public final IntelligentTieringConfiguration intelligentTieringConfiguration() {
        return this.intelligentTieringConfiguration;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.id());
        hashCode = 31 * hashCode + Objects.hashCode(this.intelligentTieringConfiguration());
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
        if (!(obj instanceof PutBucketIntelligentTieringConfigurationRequest)) {
            return false;
        }
        PutBucketIntelligentTieringConfigurationRequest other = (PutBucketIntelligentTieringConfigurationRequest)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.id(), other.id()) && Objects.equals(this.intelligentTieringConfiguration(), other.intelligentTieringConfiguration());
    }

    public final String toString() {
        return ToString.builder((String)"PutBucketIntelligentTieringConfigurationRequest").add("Bucket", (Object)this.bucket()).add("Id", (Object)this.id()).add("IntelligentTieringConfiguration", (Object)this.intelligentTieringConfiguration()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Id": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
            case "IntelligentTieringConfiguration": {
                return Optional.ofNullable(clazz.cast(this.intelligentTieringConfiguration()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<PutBucketIntelligentTieringConfigurationRequest, T> g) {
        return obj -> g.apply((PutBucketIntelligentTieringConfigurationRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String bucket;
        private String id;
        private IntelligentTieringConfiguration intelligentTieringConfiguration;

        private BuilderImpl() {
        }

        private BuilderImpl(PutBucketIntelligentTieringConfigurationRequest model) {
            super(model);
            this.bucket(model.bucket);
            this.id(model.id);
            this.intelligentTieringConfiguration(model.intelligentTieringConfiguration);
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

        public final String getId() {
            return this.id;
        }

        public final void setId(String id) {
            this.id = id;
        }

        @Override
        public final Builder id(String id) {
            this.id = id;
            return this;
        }

        public final IntelligentTieringConfiguration.Builder getIntelligentTieringConfiguration() {
            return this.intelligentTieringConfiguration != null ? this.intelligentTieringConfiguration.toBuilder() : null;
        }

        public final void setIntelligentTieringConfiguration(IntelligentTieringConfiguration.BuilderImpl intelligentTieringConfiguration) {
            this.intelligentTieringConfiguration = intelligentTieringConfiguration != null ? intelligentTieringConfiguration.build() : null;
        }

        @Override
        public final Builder intelligentTieringConfiguration(IntelligentTieringConfiguration intelligentTieringConfiguration) {
            this.intelligentTieringConfiguration = intelligentTieringConfiguration;
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
        public PutBucketIntelligentTieringConfigurationRequest build() {
            return new PutBucketIntelligentTieringConfigurationRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, PutBucketIntelligentTieringConfigurationRequest> {
        public Builder bucket(String var1);

        public Builder id(String var1);

        public Builder intelligentTieringConfiguration(IntelligentTieringConfiguration var1);

        default public Builder intelligentTieringConfiguration(Consumer<IntelligentTieringConfiguration.Builder> intelligentTieringConfiguration) {
            return this.intelligentTieringConfiguration((IntelligentTieringConfiguration)((IntelligentTieringConfiguration.Builder)IntelligentTieringConfiguration.builder().applyMutation(intelligentTieringConfiguration)).build());
        }

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

