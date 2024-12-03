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
import software.amazon.awssdk.services.s3.model.EncodingType;
import software.amazon.awssdk.services.s3.model.RequestPayer;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListMultipartUploadsRequest
extends S3Request
implements ToCopyableBuilder<Builder, ListMultipartUploadsRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(ListMultipartUploadsRequest.getter(ListMultipartUploadsRequest::bucket)).setter(ListMultipartUploadsRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Delimiter").getter(ListMultipartUploadsRequest.getter(ListMultipartUploadsRequest::delimiter)).setter(ListMultipartUploadsRequest.setter(Builder::delimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("delimiter").unmarshallLocationName("delimiter").build()}).build();
    private static final SdkField<String> ENCODING_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("EncodingType").getter(ListMultipartUploadsRequest.getter(ListMultipartUploadsRequest::encodingTypeAsString)).setter(ListMultipartUploadsRequest.setter(Builder::encodingType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("encoding-type").unmarshallLocationName("encoding-type").build()}).build();
    private static final SdkField<String> KEY_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("KeyMarker").getter(ListMultipartUploadsRequest.getter(ListMultipartUploadsRequest::keyMarker)).setter(ListMultipartUploadsRequest.setter(Builder::keyMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("key-marker").unmarshallLocationName("key-marker").build()}).build();
    private static final SdkField<Integer> MAX_UPLOADS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MaxUploads").getter(ListMultipartUploadsRequest.getter(ListMultipartUploadsRequest::maxUploads)).setter(ListMultipartUploadsRequest.setter(Builder::maxUploads)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("max-uploads").unmarshallLocationName("max-uploads").build()}).build();
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(ListMultipartUploadsRequest.getter(ListMultipartUploadsRequest::prefix)).setter(ListMultipartUploadsRequest.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("prefix").unmarshallLocationName("prefix").build()}).build();
    private static final SdkField<String> UPLOAD_ID_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("UploadIdMarker").getter(ListMultipartUploadsRequest.getter(ListMultipartUploadsRequest::uploadIdMarker)).setter(ListMultipartUploadsRequest.setter(Builder::uploadIdMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("upload-id-marker").unmarshallLocationName("upload-id-marker").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(ListMultipartUploadsRequest.getter(ListMultipartUploadsRequest::expectedBucketOwner)).setter(ListMultipartUploadsRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final SdkField<String> REQUEST_PAYER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestPayer").getter(ListMultipartUploadsRequest.getter(ListMultipartUploadsRequest::requestPayerAsString)).setter(ListMultipartUploadsRequest.setter(Builder::requestPayer)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-payer").unmarshallLocationName("x-amz-request-payer").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, DELIMITER_FIELD, ENCODING_TYPE_FIELD, KEY_MARKER_FIELD, MAX_UPLOADS_FIELD, PREFIX_FIELD, UPLOAD_ID_MARKER_FIELD, EXPECTED_BUCKET_OWNER_FIELD, REQUEST_PAYER_FIELD));
    private final String bucket;
    private final String delimiter;
    private final String encodingType;
    private final String keyMarker;
    private final Integer maxUploads;
    private final String prefix;
    private final String uploadIdMarker;
    private final String expectedBucketOwner;
    private final String requestPayer;

    private ListMultipartUploadsRequest(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.delimiter = builder.delimiter;
        this.encodingType = builder.encodingType;
        this.keyMarker = builder.keyMarker;
        this.maxUploads = builder.maxUploads;
        this.prefix = builder.prefix;
        this.uploadIdMarker = builder.uploadIdMarker;
        this.expectedBucketOwner = builder.expectedBucketOwner;
        this.requestPayer = builder.requestPayer;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String delimiter() {
        return this.delimiter;
    }

    public final EncodingType encodingType() {
        return EncodingType.fromValue(this.encodingType);
    }

    public final String encodingTypeAsString() {
        return this.encodingType;
    }

    public final String keyMarker() {
        return this.keyMarker;
    }

    public final Integer maxUploads() {
        return this.maxUploads;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final String uploadIdMarker() {
        return this.uploadIdMarker;
    }

    public final String expectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    public final RequestPayer requestPayer() {
        return RequestPayer.fromValue(this.requestPayer);
    }

    public final String requestPayerAsString() {
        return this.requestPayer;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.delimiter());
        hashCode = 31 * hashCode + Objects.hashCode(this.encodingTypeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.keyMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.maxUploads());
        hashCode = 31 * hashCode + Objects.hashCode(this.prefix());
        hashCode = 31 * hashCode + Objects.hashCode(this.uploadIdMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestPayerAsString());
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
        if (!(obj instanceof ListMultipartUploadsRequest)) {
            return false;
        }
        ListMultipartUploadsRequest other = (ListMultipartUploadsRequest)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.delimiter(), other.delimiter()) && Objects.equals(this.encodingTypeAsString(), other.encodingTypeAsString()) && Objects.equals(this.keyMarker(), other.keyMarker()) && Objects.equals(this.maxUploads(), other.maxUploads()) && Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.uploadIdMarker(), other.uploadIdMarker()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner()) && Objects.equals(this.requestPayerAsString(), other.requestPayerAsString());
    }

    public final String toString() {
        return ToString.builder((String)"ListMultipartUploadsRequest").add("Bucket", (Object)this.bucket()).add("Delimiter", (Object)this.delimiter()).add("EncodingType", (Object)this.encodingTypeAsString()).add("KeyMarker", (Object)this.keyMarker()).add("MaxUploads", (Object)this.maxUploads()).add("Prefix", (Object)this.prefix()).add("UploadIdMarker", (Object)this.uploadIdMarker()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).add("RequestPayer", (Object)this.requestPayerAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Delimiter": {
                return Optional.ofNullable(clazz.cast(this.delimiter()));
            }
            case "EncodingType": {
                return Optional.ofNullable(clazz.cast(this.encodingTypeAsString()));
            }
            case "KeyMarker": {
                return Optional.ofNullable(clazz.cast(this.keyMarker()));
            }
            case "MaxUploads": {
                return Optional.ofNullable(clazz.cast(this.maxUploads()));
            }
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "UploadIdMarker": {
                return Optional.ofNullable(clazz.cast(this.uploadIdMarker()));
            }
            case "ExpectedBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedBucketOwner()));
            }
            case "RequestPayer": {
                return Optional.ofNullable(clazz.cast(this.requestPayerAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListMultipartUploadsRequest, T> g) {
        return obj -> g.apply((ListMultipartUploadsRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String bucket;
        private String delimiter;
        private String encodingType;
        private String keyMarker;
        private Integer maxUploads;
        private String prefix;
        private String uploadIdMarker;
        private String expectedBucketOwner;
        private String requestPayer;

        private BuilderImpl() {
        }

        private BuilderImpl(ListMultipartUploadsRequest model) {
            super(model);
            this.bucket(model.bucket);
            this.delimiter(model.delimiter);
            this.encodingType(model.encodingType);
            this.keyMarker(model.keyMarker);
            this.maxUploads(model.maxUploads);
            this.prefix(model.prefix);
            this.uploadIdMarker(model.uploadIdMarker);
            this.expectedBucketOwner(model.expectedBucketOwner);
            this.requestPayer(model.requestPayer);
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

        public final String getDelimiter() {
            return this.delimiter;
        }

        public final void setDelimiter(String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public final Builder delimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public final String getEncodingType() {
            return this.encodingType;
        }

        public final void setEncodingType(String encodingType) {
            this.encodingType = encodingType;
        }

        @Override
        public final Builder encodingType(String encodingType) {
            this.encodingType = encodingType;
            return this;
        }

        @Override
        public final Builder encodingType(EncodingType encodingType) {
            this.encodingType(encodingType == null ? null : encodingType.toString());
            return this;
        }

        public final String getKeyMarker() {
            return this.keyMarker;
        }

        public final void setKeyMarker(String keyMarker) {
            this.keyMarker = keyMarker;
        }

        @Override
        public final Builder keyMarker(String keyMarker) {
            this.keyMarker = keyMarker;
            return this;
        }

        public final Integer getMaxUploads() {
            return this.maxUploads;
        }

        public final void setMaxUploads(Integer maxUploads) {
            this.maxUploads = maxUploads;
        }

        @Override
        public final Builder maxUploads(Integer maxUploads) {
            this.maxUploads = maxUploads;
            return this;
        }

        public final String getPrefix() {
            return this.prefix;
        }

        public final void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public final Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public final String getUploadIdMarker() {
            return this.uploadIdMarker;
        }

        public final void setUploadIdMarker(String uploadIdMarker) {
            this.uploadIdMarker = uploadIdMarker;
        }

        @Override
        public final Builder uploadIdMarker(String uploadIdMarker) {
            this.uploadIdMarker = uploadIdMarker;
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
        public ListMultipartUploadsRequest build() {
            return new ListMultipartUploadsRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListMultipartUploadsRequest> {
        public Builder bucket(String var1);

        public Builder delimiter(String var1);

        public Builder encodingType(String var1);

        public Builder encodingType(EncodingType var1);

        public Builder keyMarker(String var1);

        public Builder maxUploads(Integer var1);

        public Builder prefix(String var1);

        public Builder uploadIdMarker(String var1);

        public Builder expectedBucketOwner(String var1);

        public Builder requestPayer(String var1);

        public Builder requestPayer(RequestPayer var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

