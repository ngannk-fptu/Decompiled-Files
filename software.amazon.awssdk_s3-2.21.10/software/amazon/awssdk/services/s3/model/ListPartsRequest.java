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

public final class ListPartsRequest
extends S3Request
implements ToCopyableBuilder<Builder, ListPartsRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(ListPartsRequest.getter(ListPartsRequest::bucket)).setter(ListPartsRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(ListPartsRequest.getter(ListPartsRequest::key)).setter(ListPartsRequest.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.GREEDY_PATH).locationName("Key").unmarshallLocationName("Key").build(), RequiredTrait.create()}).build();
    private static final SdkField<Integer> MAX_PARTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MaxParts").getter(ListPartsRequest.getter(ListPartsRequest::maxParts)).setter(ListPartsRequest.setter(Builder::maxParts)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("max-parts").unmarshallLocationName("max-parts").build()}).build();
    private static final SdkField<Integer> PART_NUMBER_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("PartNumberMarker").getter(ListPartsRequest.getter(ListPartsRequest::partNumberMarker)).setter(ListPartsRequest.setter(Builder::partNumberMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("part-number-marker").unmarshallLocationName("part-number-marker").build()}).build();
    private static final SdkField<String> UPLOAD_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("UploadId").getter(ListPartsRequest.getter(ListPartsRequest::uploadId)).setter(ListPartsRequest.setter(Builder::uploadId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("uploadId").unmarshallLocationName("uploadId").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> REQUEST_PAYER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestPayer").getter(ListPartsRequest.getter(ListPartsRequest::requestPayerAsString)).setter(ListPartsRequest.setter(Builder::requestPayer)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-payer").unmarshallLocationName("x-amz-request-payer").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(ListPartsRequest.getter(ListPartsRequest::expectedBucketOwner)).setter(ListPartsRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerAlgorithm").getter(ListPartsRequest.getter(ListPartsRequest::sseCustomerAlgorithm)).setter(ListPartsRequest.setter(Builder::sseCustomerAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-algorithm").unmarshallLocationName("x-amz-server-side-encryption-customer-algorithm").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKey").getter(ListPartsRequest.getter(ListPartsRequest::sseCustomerKey)).setter(ListPartsRequest.setter(Builder::sseCustomerKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key").unmarshallLocationName("x-amz-server-side-encryption-customer-key").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKeyMD5").getter(ListPartsRequest.getter(ListPartsRequest::sseCustomerKeyMD5)).setter(ListPartsRequest.setter(Builder::sseCustomerKeyMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key-MD5").unmarshallLocationName("x-amz-server-side-encryption-customer-key-MD5").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, KEY_FIELD, MAX_PARTS_FIELD, PART_NUMBER_MARKER_FIELD, UPLOAD_ID_FIELD, REQUEST_PAYER_FIELD, EXPECTED_BUCKET_OWNER_FIELD, SSE_CUSTOMER_ALGORITHM_FIELD, SSE_CUSTOMER_KEY_FIELD, SSE_CUSTOMER_KEY_MD5_FIELD));
    private final String bucket;
    private final String key;
    private final Integer maxParts;
    private final Integer partNumberMarker;
    private final String uploadId;
    private final String requestPayer;
    private final String expectedBucketOwner;
    private final String sseCustomerAlgorithm;
    private final String sseCustomerKey;
    private final String sseCustomerKeyMD5;

    private ListPartsRequest(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.key = builder.key;
        this.maxParts = builder.maxParts;
        this.partNumberMarker = builder.partNumberMarker;
        this.uploadId = builder.uploadId;
        this.requestPayer = builder.requestPayer;
        this.expectedBucketOwner = builder.expectedBucketOwner;
        this.sseCustomerAlgorithm = builder.sseCustomerAlgorithm;
        this.sseCustomerKey = builder.sseCustomerKey;
        this.sseCustomerKeyMD5 = builder.sseCustomerKeyMD5;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String key() {
        return this.key;
    }

    public final Integer maxParts() {
        return this.maxParts;
    }

    public final Integer partNumberMarker() {
        return this.partNumberMarker;
    }

    public final String uploadId() {
        return this.uploadId;
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

    public final String sseCustomerAlgorithm() {
        return this.sseCustomerAlgorithm;
    }

    public final String sseCustomerKey() {
        return this.sseCustomerKey;
    }

    public final String sseCustomerKeyMD5() {
        return this.sseCustomerKeyMD5;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.maxParts());
        hashCode = 31 * hashCode + Objects.hashCode(this.partNumberMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.uploadId());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestPayerAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerAlgorithm());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKeyMD5());
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
        if (!(obj instanceof ListPartsRequest)) {
            return false;
        }
        ListPartsRequest other = (ListPartsRequest)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.key(), other.key()) && Objects.equals(this.maxParts(), other.maxParts()) && Objects.equals(this.partNumberMarker(), other.partNumberMarker()) && Objects.equals(this.uploadId(), other.uploadId()) && Objects.equals(this.requestPayerAsString(), other.requestPayerAsString()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner()) && Objects.equals(this.sseCustomerAlgorithm(), other.sseCustomerAlgorithm()) && Objects.equals(this.sseCustomerKey(), other.sseCustomerKey()) && Objects.equals(this.sseCustomerKeyMD5(), other.sseCustomerKeyMD5());
    }

    public final String toString() {
        return ToString.builder((String)"ListPartsRequest").add("Bucket", (Object)this.bucket()).add("Key", (Object)this.key()).add("MaxParts", (Object)this.maxParts()).add("PartNumberMarker", (Object)this.partNumberMarker()).add("UploadId", (Object)this.uploadId()).add("RequestPayer", (Object)this.requestPayerAsString()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).add("SSECustomerAlgorithm", (Object)this.sseCustomerAlgorithm()).add("SSECustomerKey", (Object)(this.sseCustomerKey() == null ? null : "*** Sensitive Data Redacted ***")).add("SSECustomerKeyMD5", (Object)this.sseCustomerKeyMD5()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "MaxParts": {
                return Optional.ofNullable(clazz.cast(this.maxParts()));
            }
            case "PartNumberMarker": {
                return Optional.ofNullable(clazz.cast(this.partNumberMarker()));
            }
            case "UploadId": {
                return Optional.ofNullable(clazz.cast(this.uploadId()));
            }
            case "RequestPayer": {
                return Optional.ofNullable(clazz.cast(this.requestPayerAsString()));
            }
            case "ExpectedBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedBucketOwner()));
            }
            case "SSECustomerAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.sseCustomerAlgorithm()));
            }
            case "SSECustomerKey": {
                return Optional.ofNullable(clazz.cast(this.sseCustomerKey()));
            }
            case "SSECustomerKeyMD5": {
                return Optional.ofNullable(clazz.cast(this.sseCustomerKeyMD5()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListPartsRequest, T> g) {
        return obj -> g.apply((ListPartsRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String bucket;
        private String key;
        private Integer maxParts;
        private Integer partNumberMarker;
        private String uploadId;
        private String requestPayer;
        private String expectedBucketOwner;
        private String sseCustomerAlgorithm;
        private String sseCustomerKey;
        private String sseCustomerKeyMD5;

        private BuilderImpl() {
        }

        private BuilderImpl(ListPartsRequest model) {
            super(model);
            this.bucket(model.bucket);
            this.key(model.key);
            this.maxParts(model.maxParts);
            this.partNumberMarker(model.partNumberMarker);
            this.uploadId(model.uploadId);
            this.requestPayer(model.requestPayer);
            this.expectedBucketOwner(model.expectedBucketOwner);
            this.sseCustomerAlgorithm(model.sseCustomerAlgorithm);
            this.sseCustomerKey(model.sseCustomerKey);
            this.sseCustomerKeyMD5(model.sseCustomerKeyMD5);
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

        public final Integer getMaxParts() {
            return this.maxParts;
        }

        public final void setMaxParts(Integer maxParts) {
            this.maxParts = maxParts;
        }

        @Override
        public final Builder maxParts(Integer maxParts) {
            this.maxParts = maxParts;
            return this;
        }

        public final Integer getPartNumberMarker() {
            return this.partNumberMarker;
        }

        public final void setPartNumberMarker(Integer partNumberMarker) {
            this.partNumberMarker = partNumberMarker;
        }

        @Override
        public final Builder partNumberMarker(Integer partNumberMarker) {
            this.partNumberMarker = partNumberMarker;
            return this;
        }

        public final String getUploadId() {
            return this.uploadId;
        }

        public final void setUploadId(String uploadId) {
            this.uploadId = uploadId;
        }

        @Override
        public final Builder uploadId(String uploadId) {
            this.uploadId = uploadId;
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

        public final String getSseCustomerAlgorithm() {
            return this.sseCustomerAlgorithm;
        }

        public final void setSseCustomerAlgorithm(String sseCustomerAlgorithm) {
            this.sseCustomerAlgorithm = sseCustomerAlgorithm;
        }

        @Override
        public final Builder sseCustomerAlgorithm(String sseCustomerAlgorithm) {
            this.sseCustomerAlgorithm = sseCustomerAlgorithm;
            return this;
        }

        public final String getSseCustomerKey() {
            return this.sseCustomerKey;
        }

        public final void setSseCustomerKey(String sseCustomerKey) {
            this.sseCustomerKey = sseCustomerKey;
        }

        @Override
        public final Builder sseCustomerKey(String sseCustomerKey) {
            this.sseCustomerKey = sseCustomerKey;
            return this;
        }

        public final String getSseCustomerKeyMD5() {
            return this.sseCustomerKeyMD5;
        }

        public final void setSseCustomerKeyMD5(String sseCustomerKeyMD5) {
            this.sseCustomerKeyMD5 = sseCustomerKeyMD5;
        }

        @Override
        public final Builder sseCustomerKeyMD5(String sseCustomerKeyMD5) {
            this.sseCustomerKeyMD5 = sseCustomerKeyMD5;
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
        public ListPartsRequest build() {
            return new ListPartsRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListPartsRequest> {
        public Builder bucket(String var1);

        public Builder key(String var1);

        public Builder maxParts(Integer var1);

        public Builder partNumberMarker(Integer var1);

        public Builder uploadId(String var1);

        public Builder requestPayer(String var1);

        public Builder requestPayer(RequestPayer var1);

        public Builder expectedBucketOwner(String var1);

        public Builder sseCustomerAlgorithm(String var1);

        public Builder sseCustomerKey(String var1);

        public Builder sseCustomerKeyMD5(String var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

