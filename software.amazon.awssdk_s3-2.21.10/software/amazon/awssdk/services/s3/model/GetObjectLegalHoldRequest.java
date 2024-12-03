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
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.RequestPayer;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetObjectLegalHoldRequest
extends S3Request
implements ToCopyableBuilder<Builder, GetObjectLegalHoldRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(GetObjectLegalHoldRequest.getter(GetObjectLegalHoldRequest::bucket)).setter(GetObjectLegalHoldRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(GetObjectLegalHoldRequest.getter(GetObjectLegalHoldRequest::key)).setter(GetObjectLegalHoldRequest.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.GREEDY_PATH).locationName("Key").unmarshallLocationName("Key").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(GetObjectLegalHoldRequest.getter(GetObjectLegalHoldRequest::versionId)).setter(GetObjectLegalHoldRequest.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("versionId").unmarshallLocationName("versionId").build()}).build();
    private static final SdkField<String> REQUEST_PAYER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestPayer").getter(GetObjectLegalHoldRequest.getter(GetObjectLegalHoldRequest::requestPayerAsString)).setter(GetObjectLegalHoldRequest.setter(Builder::requestPayer)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-payer").unmarshallLocationName("x-amz-request-payer").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(GetObjectLegalHoldRequest.getter(GetObjectLegalHoldRequest::expectedBucketOwner)).setter(GetObjectLegalHoldRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, KEY_FIELD, VERSION_ID_FIELD, REQUEST_PAYER_FIELD, EXPECTED_BUCKET_OWNER_FIELD));
    private final String bucket;
    private final String key;
    private final String versionId;
    private final String requestPayer;
    private final String expectedBucketOwner;

    private GetObjectLegalHoldRequest(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.key = builder.key;
        this.versionId = builder.versionId;
        this.requestPayer = builder.requestPayer;
        this.expectedBucketOwner = builder.expectedBucketOwner;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String key() {
        return this.key;
    }

    public final String versionId() {
        return this.versionId;
    }

    public final RequestPayer requestPayer() {
        return RequestPayer.fromValue(this.requestPayer);
    }

    public final String requestPayerAsString() {
        return this.requestPayer;
    }

    public final String expectedBucketOwner() {
        return this.expectedBucketOwner;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestPayerAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
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
        if (!(obj instanceof GetObjectLegalHoldRequest)) {
            return false;
        }
        GetObjectLegalHoldRequest other = (GetObjectLegalHoldRequest)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.key(), other.key()) && Objects.equals(this.versionId(), other.versionId()) && Objects.equals(this.requestPayerAsString(), other.requestPayerAsString()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner());
    }

    public final String toString() {
        return ToString.builder((String)"GetObjectLegalHoldRequest").add("Bucket", (Object)this.bucket()).add("Key", (Object)this.key()).add("VersionId", (Object)this.versionId()).add("RequestPayer", (Object)this.requestPayerAsString()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
            case "RequestPayer": {
                return Optional.ofNullable(clazz.cast(this.requestPayerAsString()));
            }
            case "ExpectedBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedBucketOwner()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetObjectLegalHoldRequest, T> g) {
        return obj -> g.apply((GetObjectLegalHoldRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String bucket;
        private String key;
        private String versionId;
        private String requestPayer;
        private String expectedBucketOwner;

        private BuilderImpl() {
        }

        private BuilderImpl(GetObjectLegalHoldRequest model) {
            super(model);
            this.bucket(model.bucket);
            this.key(model.key);
            this.versionId(model.versionId);
            this.requestPayer(model.requestPayer);
            this.expectedBucketOwner(model.expectedBucketOwner);
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

        public final String getKey() {
            return this.key;
        }

        public final void setKey(String key) {
            this.key = key;
        }

        @Override
        public final Builder key(String key) {
            this.key = key;
            return this;
        }

        public final String getVersionId() {
            return this.versionId;
        }

        public final void setVersionId(String versionId) {
            this.versionId = versionId;
        }

        @Override
        public final Builder versionId(String versionId) {
            this.versionId = versionId;
            return this;
        }

        public final String getRequestPayer() {
            return this.requestPayer;
        }

        public final void setRequestPayer(String requestPayer) {
            this.requestPayer = requestPayer;
        }

        @Override
        public final Builder requestPayer(String requestPayer) {
            this.requestPayer = requestPayer;
            return this;
        }

        @Override
        public final Builder requestPayer(RequestPayer requestPayer) {
            this.requestPayer(requestPayer == null ? null : requestPayer.toString());
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
        public GetObjectLegalHoldRequest build() {
            return new GetObjectLegalHoldRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetObjectLegalHoldRequest> {
        public Builder bucket(String var1);

        public Builder key(String var1);

        public Builder versionId(String var1);

        public Builder requestPayer(String var1);

        public Builder requestPayer(RequestPayer var1);

        public Builder expectedBucketOwner(String var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

