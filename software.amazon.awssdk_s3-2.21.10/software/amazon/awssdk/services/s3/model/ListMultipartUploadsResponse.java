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
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.CommonPrefixListCopier;
import software.amazon.awssdk.services.s3.model.EncodingType;
import software.amazon.awssdk.services.s3.model.MultipartUpload;
import software.amazon.awssdk.services.s3.model.MultipartUploadListCopier;
import software.amazon.awssdk.services.s3.model.RequestCharged;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListMultipartUploadsResponse
extends S3Response
implements ToCopyableBuilder<Builder, ListMultipartUploadsResponse> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::bucket)).setter(ListMultipartUploadsResponse.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Bucket").unmarshallLocationName("Bucket").build()}).build();
    private static final SdkField<String> KEY_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("KeyMarker").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::keyMarker)).setter(ListMultipartUploadsResponse.setter(Builder::keyMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KeyMarker").unmarshallLocationName("KeyMarker").build()}).build();
    private static final SdkField<String> UPLOAD_ID_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("UploadIdMarker").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::uploadIdMarker)).setter(ListMultipartUploadsResponse.setter(Builder::uploadIdMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("UploadIdMarker").unmarshallLocationName("UploadIdMarker").build()}).build();
    private static final SdkField<String> NEXT_KEY_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextKeyMarker").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::nextKeyMarker)).setter(ListMultipartUploadsResponse.setter(Builder::nextKeyMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextKeyMarker").unmarshallLocationName("NextKeyMarker").build()}).build();
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::prefix)).setter(ListMultipartUploadsResponse.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final SdkField<String> DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Delimiter").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::delimiter)).setter(ListMultipartUploadsResponse.setter(Builder::delimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Delimiter").unmarshallLocationName("Delimiter").build()}).build();
    private static final SdkField<String> NEXT_UPLOAD_ID_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextUploadIdMarker").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::nextUploadIdMarker)).setter(ListMultipartUploadsResponse.setter(Builder::nextUploadIdMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextUploadIdMarker").unmarshallLocationName("NextUploadIdMarker").build()}).build();
    private static final SdkField<Integer> MAX_UPLOADS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MaxUploads").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::maxUploads)).setter(ListMultipartUploadsResponse.setter(Builder::maxUploads)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MaxUploads").unmarshallLocationName("MaxUploads").build()}).build();
    private static final SdkField<Boolean> IS_TRUNCATED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsTruncated").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::isTruncated)).setter(ListMultipartUploadsResponse.setter(Builder::isTruncated)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsTruncated").unmarshallLocationName("IsTruncated").build()}).build();
    private static final SdkField<List<MultipartUpload>> UPLOADS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Uploads").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::uploads)).setter(ListMultipartUploadsResponse.setter(Builder::uploads)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Upload").unmarshallLocationName("Upload").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(MultipartUpload::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<List<CommonPrefix>> COMMON_PREFIXES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("CommonPrefixes").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::commonPrefixes)).setter(ListMultipartUploadsResponse.setter(Builder::commonPrefixes)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CommonPrefixes").unmarshallLocationName("CommonPrefixes").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(CommonPrefix::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<String> ENCODING_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("EncodingType").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::encodingTypeAsString)).setter(ListMultipartUploadsResponse.setter(Builder::encodingType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("EncodingType").unmarshallLocationName("EncodingType").build()}).build();
    private static final SdkField<String> REQUEST_CHARGED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestCharged").getter(ListMultipartUploadsResponse.getter(ListMultipartUploadsResponse::requestChargedAsString)).setter(ListMultipartUploadsResponse.setter(Builder::requestCharged)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-charged").unmarshallLocationName("x-amz-request-charged").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, KEY_MARKER_FIELD, UPLOAD_ID_MARKER_FIELD, NEXT_KEY_MARKER_FIELD, PREFIX_FIELD, DELIMITER_FIELD, NEXT_UPLOAD_ID_MARKER_FIELD, MAX_UPLOADS_FIELD, IS_TRUNCATED_FIELD, UPLOADS_FIELD, COMMON_PREFIXES_FIELD, ENCODING_TYPE_FIELD, REQUEST_CHARGED_FIELD));
    private final String bucket;
    private final String keyMarker;
    private final String uploadIdMarker;
    private final String nextKeyMarker;
    private final String prefix;
    private final String delimiter;
    private final String nextUploadIdMarker;
    private final Integer maxUploads;
    private final Boolean isTruncated;
    private final List<MultipartUpload> uploads;
    private final List<CommonPrefix> commonPrefixes;
    private final String encodingType;
    private final String requestCharged;

    private ListMultipartUploadsResponse(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.keyMarker = builder.keyMarker;
        this.uploadIdMarker = builder.uploadIdMarker;
        this.nextKeyMarker = builder.nextKeyMarker;
        this.prefix = builder.prefix;
        this.delimiter = builder.delimiter;
        this.nextUploadIdMarker = builder.nextUploadIdMarker;
        this.maxUploads = builder.maxUploads;
        this.isTruncated = builder.isTruncated;
        this.uploads = builder.uploads;
        this.commonPrefixes = builder.commonPrefixes;
        this.encodingType = builder.encodingType;
        this.requestCharged = builder.requestCharged;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String keyMarker() {
        return this.keyMarker;
    }

    public final String uploadIdMarker() {
        return this.uploadIdMarker;
    }

    public final String nextKeyMarker() {
        return this.nextKeyMarker;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final String delimiter() {
        return this.delimiter;
    }

    public final String nextUploadIdMarker() {
        return this.nextUploadIdMarker;
    }

    public final Integer maxUploads() {
        return this.maxUploads;
    }

    public final Boolean isTruncated() {
        return this.isTruncated;
    }

    public final boolean hasUploads() {
        return this.uploads != null && !(this.uploads instanceof SdkAutoConstructList);
    }

    public final List<MultipartUpload> uploads() {
        return this.uploads;
    }

    public final boolean hasCommonPrefixes() {
        return this.commonPrefixes != null && !(this.commonPrefixes instanceof SdkAutoConstructList);
    }

    public final List<CommonPrefix> commonPrefixes() {
        return this.commonPrefixes;
    }

    public final EncodingType encodingType() {
        return EncodingType.fromValue(this.encodingType);
    }

    public final String encodingTypeAsString() {
        return this.encodingType;
    }

    public final RequestCharged requestCharged() {
        return RequestCharged.fromValue(this.requestCharged);
    }

    public final String requestChargedAsString() {
        return this.requestCharged;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.keyMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.uploadIdMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextKeyMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.prefix());
        hashCode = 31 * hashCode + Objects.hashCode(this.delimiter());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextUploadIdMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.maxUploads());
        hashCode = 31 * hashCode + Objects.hashCode(this.isTruncated());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasUploads() ? this.uploads() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.hasCommonPrefixes() ? this.commonPrefixes() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.encodingTypeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestChargedAsString());
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
        if (!(obj instanceof ListMultipartUploadsResponse)) {
            return false;
        }
        ListMultipartUploadsResponse other = (ListMultipartUploadsResponse)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.keyMarker(), other.keyMarker()) && Objects.equals(this.uploadIdMarker(), other.uploadIdMarker()) && Objects.equals(this.nextKeyMarker(), other.nextKeyMarker()) && Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.delimiter(), other.delimiter()) && Objects.equals(this.nextUploadIdMarker(), other.nextUploadIdMarker()) && Objects.equals(this.maxUploads(), other.maxUploads()) && Objects.equals(this.isTruncated(), other.isTruncated()) && this.hasUploads() == other.hasUploads() && Objects.equals(this.uploads(), other.uploads()) && this.hasCommonPrefixes() == other.hasCommonPrefixes() && Objects.equals(this.commonPrefixes(), other.commonPrefixes()) && Objects.equals(this.encodingTypeAsString(), other.encodingTypeAsString()) && Objects.equals(this.requestChargedAsString(), other.requestChargedAsString());
    }

    public final String toString() {
        return ToString.builder((String)"ListMultipartUploadsResponse").add("Bucket", (Object)this.bucket()).add("KeyMarker", (Object)this.keyMarker()).add("UploadIdMarker", (Object)this.uploadIdMarker()).add("NextKeyMarker", (Object)this.nextKeyMarker()).add("Prefix", (Object)this.prefix()).add("Delimiter", (Object)this.delimiter()).add("NextUploadIdMarker", (Object)this.nextUploadIdMarker()).add("MaxUploads", (Object)this.maxUploads()).add("IsTruncated", (Object)this.isTruncated()).add("Uploads", this.hasUploads() ? this.uploads() : null).add("CommonPrefixes", this.hasCommonPrefixes() ? this.commonPrefixes() : null).add("EncodingType", (Object)this.encodingTypeAsString()).add("RequestCharged", (Object)this.requestChargedAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "KeyMarker": {
                return Optional.ofNullable(clazz.cast(this.keyMarker()));
            }
            case "UploadIdMarker": {
                return Optional.ofNullable(clazz.cast(this.uploadIdMarker()));
            }
            case "NextKeyMarker": {
                return Optional.ofNullable(clazz.cast(this.nextKeyMarker()));
            }
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "Delimiter": {
                return Optional.ofNullable(clazz.cast(this.delimiter()));
            }
            case "NextUploadIdMarker": {
                return Optional.ofNullable(clazz.cast(this.nextUploadIdMarker()));
            }
            case "MaxUploads": {
                return Optional.ofNullable(clazz.cast(this.maxUploads()));
            }
            case "IsTruncated": {
                return Optional.ofNullable(clazz.cast(this.isTruncated()));
            }
            case "Uploads": {
                return Optional.ofNullable(clazz.cast(this.uploads()));
            }
            case "CommonPrefixes": {
                return Optional.ofNullable(clazz.cast(this.commonPrefixes()));
            }
            case "EncodingType": {
                return Optional.ofNullable(clazz.cast(this.encodingTypeAsString()));
            }
            case "RequestCharged": {
                return Optional.ofNullable(clazz.cast(this.requestChargedAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListMultipartUploadsResponse, T> g) {
        return obj -> g.apply((ListMultipartUploadsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private String bucket;
        private String keyMarker;
        private String uploadIdMarker;
        private String nextKeyMarker;
        private String prefix;
        private String delimiter;
        private String nextUploadIdMarker;
        private Integer maxUploads;
        private Boolean isTruncated;
        private List<MultipartUpload> uploads = DefaultSdkAutoConstructList.getInstance();
        private List<CommonPrefix> commonPrefixes = DefaultSdkAutoConstructList.getInstance();
        private String encodingType;
        private String requestCharged;

        private BuilderImpl() {
        }

        private BuilderImpl(ListMultipartUploadsResponse model) {
            super(model);
            this.bucket(model.bucket);
            this.keyMarker(model.keyMarker);
            this.uploadIdMarker(model.uploadIdMarker);
            this.nextKeyMarker(model.nextKeyMarker);
            this.prefix(model.prefix);
            this.delimiter(model.delimiter);
            this.nextUploadIdMarker(model.nextUploadIdMarker);
            this.maxUploads(model.maxUploads);
            this.isTruncated(model.isTruncated);
            this.uploads(model.uploads);
            this.commonPrefixes(model.commonPrefixes);
            this.encodingType(model.encodingType);
            this.requestCharged(model.requestCharged);
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

        public final String getNextKeyMarker() {
            return this.nextKeyMarker;
        }

        public final void setNextKeyMarker(String nextKeyMarker) {
            this.nextKeyMarker = nextKeyMarker;
        }

        @Override
        public final Builder nextKeyMarker(String nextKeyMarker) {
            this.nextKeyMarker = nextKeyMarker;
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

        public final String getNextUploadIdMarker() {
            return this.nextUploadIdMarker;
        }

        public final void setNextUploadIdMarker(String nextUploadIdMarker) {
            this.nextUploadIdMarker = nextUploadIdMarker;
        }

        @Override
        public final Builder nextUploadIdMarker(String nextUploadIdMarker) {
            this.nextUploadIdMarker = nextUploadIdMarker;
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

        public final Boolean getIsTruncated() {
            return this.isTruncated;
        }

        public final void setIsTruncated(Boolean isTruncated) {
            this.isTruncated = isTruncated;
        }

        @Override
        public final Builder isTruncated(Boolean isTruncated) {
            this.isTruncated = isTruncated;
            return this;
        }

        public final List<MultipartUpload.Builder> getUploads() {
            List<MultipartUpload.Builder> result = MultipartUploadListCopier.copyToBuilder(this.uploads);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setUploads(Collection<MultipartUpload.BuilderImpl> uploads) {
            this.uploads = MultipartUploadListCopier.copyFromBuilder(uploads);
        }

        @Override
        public final Builder uploads(Collection<MultipartUpload> uploads) {
            this.uploads = MultipartUploadListCopier.copy(uploads);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder uploads(MultipartUpload ... uploads) {
            this.uploads(Arrays.asList(uploads));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder uploads(Consumer<MultipartUpload.Builder> ... uploads) {
            this.uploads(Stream.of(uploads).map(c -> (MultipartUpload)((MultipartUpload.Builder)MultipartUpload.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final List<CommonPrefix.Builder> getCommonPrefixes() {
            List<CommonPrefix.Builder> result = CommonPrefixListCopier.copyToBuilder(this.commonPrefixes);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setCommonPrefixes(Collection<CommonPrefix.BuilderImpl> commonPrefixes) {
            this.commonPrefixes = CommonPrefixListCopier.copyFromBuilder(commonPrefixes);
        }

        @Override
        public final Builder commonPrefixes(Collection<CommonPrefix> commonPrefixes) {
            this.commonPrefixes = CommonPrefixListCopier.copy(commonPrefixes);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder commonPrefixes(CommonPrefix ... commonPrefixes) {
            this.commonPrefixes(Arrays.asList(commonPrefixes));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder commonPrefixes(Consumer<CommonPrefix.Builder> ... commonPrefixes) {
            this.commonPrefixes(Stream.of(commonPrefixes).map(c -> (CommonPrefix)((CommonPrefix.Builder)CommonPrefix.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
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

        public final String getRequestCharged() {
            return this.requestCharged;
        }

        public final void setRequestCharged(String requestCharged) {
            this.requestCharged = requestCharged;
        }

        @Override
        public final Builder requestCharged(String requestCharged) {
            this.requestCharged = requestCharged;
            return this;
        }

        @Override
        public final Builder requestCharged(RequestCharged requestCharged) {
            this.requestCharged(requestCharged == null ? null : requestCharged.toString());
            return this;
        }

        @Override
        public ListMultipartUploadsResponse build() {
            return new ListMultipartUploadsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListMultipartUploadsResponse> {
        public Builder bucket(String var1);

        public Builder keyMarker(String var1);

        public Builder uploadIdMarker(String var1);

        public Builder nextKeyMarker(String var1);

        public Builder prefix(String var1);

        public Builder delimiter(String var1);

        public Builder nextUploadIdMarker(String var1);

        public Builder maxUploads(Integer var1);

        public Builder isTruncated(Boolean var1);

        public Builder uploads(Collection<MultipartUpload> var1);

        public Builder uploads(MultipartUpload ... var1);

        public Builder uploads(Consumer<MultipartUpload.Builder> ... var1);

        public Builder commonPrefixes(Collection<CommonPrefix> var1);

        public Builder commonPrefixes(CommonPrefix ... var1);

        public Builder commonPrefixes(Consumer<CommonPrefix.Builder> ... var1);

        public Builder encodingType(String var1);

        public Builder encodingType(EncodingType var1);

        public Builder requestCharged(String var1);

        public Builder requestCharged(RequestCharged var1);
    }
}

