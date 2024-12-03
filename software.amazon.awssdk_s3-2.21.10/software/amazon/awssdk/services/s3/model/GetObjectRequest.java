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
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait$Format
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.time.Instant;
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
import software.amazon.awssdk.core.traits.TimestampFormatTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.ChecksumMode;
import software.amazon.awssdk.services.s3.model.RequestPayer;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetObjectRequest
extends S3Request
implements ToCopyableBuilder<Builder, GetObjectRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(GetObjectRequest.getter(GetObjectRequest::bucket)).setter(GetObjectRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> IF_MATCH_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("IfMatch").getter(GetObjectRequest.getter(GetObjectRequest::ifMatch)).setter(GetObjectRequest.setter(Builder::ifMatch)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("If-Match").unmarshallLocationName("If-Match").build()}).build();
    private static final SdkField<Instant> IF_MODIFIED_SINCE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("IfModifiedSince").getter(GetObjectRequest.getter(GetObjectRequest::ifModifiedSince)).setter(GetObjectRequest.setter(Builder::ifModifiedSince)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("If-Modified-Since").unmarshallLocationName("If-Modified-Since").build()}).build();
    private static final SdkField<String> IF_NONE_MATCH_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("IfNoneMatch").getter(GetObjectRequest.getter(GetObjectRequest::ifNoneMatch)).setter(GetObjectRequest.setter(Builder::ifNoneMatch)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("If-None-Match").unmarshallLocationName("If-None-Match").build()}).build();
    private static final SdkField<Instant> IF_UNMODIFIED_SINCE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("IfUnmodifiedSince").getter(GetObjectRequest.getter(GetObjectRequest::ifUnmodifiedSince)).setter(GetObjectRequest.setter(Builder::ifUnmodifiedSince)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("If-Unmodified-Since").unmarshallLocationName("If-Unmodified-Since").build()}).build();
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(GetObjectRequest.getter(GetObjectRequest::key)).setter(GetObjectRequest.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.GREEDY_PATH).locationName("Key").unmarshallLocationName("Key").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> RANGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Range").getter(GetObjectRequest.getter(GetObjectRequest::range)).setter(GetObjectRequest.setter(Builder::range)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Range").unmarshallLocationName("Range").build()}).build();
    private static final SdkField<String> RESPONSE_CACHE_CONTROL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ResponseCacheControl").getter(GetObjectRequest.getter(GetObjectRequest::responseCacheControl)).setter(GetObjectRequest.setter(Builder::responseCacheControl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("response-cache-control").unmarshallLocationName("response-cache-control").build()}).build();
    private static final SdkField<String> RESPONSE_CONTENT_DISPOSITION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ResponseContentDisposition").getter(GetObjectRequest.getter(GetObjectRequest::responseContentDisposition)).setter(GetObjectRequest.setter(Builder::responseContentDisposition)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("response-content-disposition").unmarshallLocationName("response-content-disposition").build()}).build();
    private static final SdkField<String> RESPONSE_CONTENT_ENCODING_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ResponseContentEncoding").getter(GetObjectRequest.getter(GetObjectRequest::responseContentEncoding)).setter(GetObjectRequest.setter(Builder::responseContentEncoding)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("response-content-encoding").unmarshallLocationName("response-content-encoding").build()}).build();
    private static final SdkField<String> RESPONSE_CONTENT_LANGUAGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ResponseContentLanguage").getter(GetObjectRequest.getter(GetObjectRequest::responseContentLanguage)).setter(GetObjectRequest.setter(Builder::responseContentLanguage)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("response-content-language").unmarshallLocationName("response-content-language").build()}).build();
    private static final SdkField<String> RESPONSE_CONTENT_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ResponseContentType").getter(GetObjectRequest.getter(GetObjectRequest::responseContentType)).setter(GetObjectRequest.setter(Builder::responseContentType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("response-content-type").unmarshallLocationName("response-content-type").build()}).build();
    private static final SdkField<Instant> RESPONSE_EXPIRES_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("ResponseExpires").getter(GetObjectRequest.getter(GetObjectRequest::responseExpires)).setter(GetObjectRequest.setter(Builder::responseExpires)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("response-expires").unmarshallLocationName("response-expires").build(), TimestampFormatTrait.create((TimestampFormatTrait.Format)TimestampFormatTrait.Format.RFC_822)}).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(GetObjectRequest.getter(GetObjectRequest::versionId)).setter(GetObjectRequest.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("versionId").unmarshallLocationName("versionId").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerAlgorithm").getter(GetObjectRequest.getter(GetObjectRequest::sseCustomerAlgorithm)).setter(GetObjectRequest.setter(Builder::sseCustomerAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-algorithm").unmarshallLocationName("x-amz-server-side-encryption-customer-algorithm").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKey").getter(GetObjectRequest.getter(GetObjectRequest::sseCustomerKey)).setter(GetObjectRequest.setter(Builder::sseCustomerKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key").unmarshallLocationName("x-amz-server-side-encryption-customer-key").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKeyMD5").getter(GetObjectRequest.getter(GetObjectRequest::sseCustomerKeyMD5)).setter(GetObjectRequest.setter(Builder::sseCustomerKeyMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key-MD5").unmarshallLocationName("x-amz-server-side-encryption-customer-key-MD5").build()}).build();
    private static final SdkField<String> REQUEST_PAYER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestPayer").getter(GetObjectRequest.getter(GetObjectRequest::requestPayerAsString)).setter(GetObjectRequest.setter(Builder::requestPayer)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-payer").unmarshallLocationName("x-amz-request-payer").build()}).build();
    private static final SdkField<Integer> PART_NUMBER_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("PartNumber").getter(GetObjectRequest.getter(GetObjectRequest::partNumber)).setter(GetObjectRequest.setter(Builder::partNumber)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("partNumber").unmarshallLocationName("partNumber").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(GetObjectRequest.getter(GetObjectRequest::expectedBucketOwner)).setter(GetObjectRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final SdkField<String> CHECKSUM_MODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumMode").getter(GetObjectRequest.getter(GetObjectRequest::checksumModeAsString)).setter(GetObjectRequest.setter(Builder::checksumMode)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-checksum-mode").unmarshallLocationName("x-amz-checksum-mode").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, IF_MATCH_FIELD, IF_MODIFIED_SINCE_FIELD, IF_NONE_MATCH_FIELD, IF_UNMODIFIED_SINCE_FIELD, KEY_FIELD, RANGE_FIELD, RESPONSE_CACHE_CONTROL_FIELD, RESPONSE_CONTENT_DISPOSITION_FIELD, RESPONSE_CONTENT_ENCODING_FIELD, RESPONSE_CONTENT_LANGUAGE_FIELD, RESPONSE_CONTENT_TYPE_FIELD, RESPONSE_EXPIRES_FIELD, VERSION_ID_FIELD, SSE_CUSTOMER_ALGORITHM_FIELD, SSE_CUSTOMER_KEY_FIELD, SSE_CUSTOMER_KEY_MD5_FIELD, REQUEST_PAYER_FIELD, PART_NUMBER_FIELD, EXPECTED_BUCKET_OWNER_FIELD, CHECKSUM_MODE_FIELD));
    private final String bucket;
    private final String ifMatch;
    private final Instant ifModifiedSince;
    private final String ifNoneMatch;
    private final Instant ifUnmodifiedSince;
    private final String key;
    private final String range;
    private final String responseCacheControl;
    private final String responseContentDisposition;
    private final String responseContentEncoding;
    private final String responseContentLanguage;
    private final String responseContentType;
    private final Instant responseExpires;
    private final String versionId;
    private final String sseCustomerAlgorithm;
    private final String sseCustomerKey;
    private final String sseCustomerKeyMD5;
    private final String requestPayer;
    private final Integer partNumber;
    private final String expectedBucketOwner;
    private final String checksumMode;

    private GetObjectRequest(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.ifMatch = builder.ifMatch;
        this.ifModifiedSince = builder.ifModifiedSince;
        this.ifNoneMatch = builder.ifNoneMatch;
        this.ifUnmodifiedSince = builder.ifUnmodifiedSince;
        this.key = builder.key;
        this.range = builder.range;
        this.responseCacheControl = builder.responseCacheControl;
        this.responseContentDisposition = builder.responseContentDisposition;
        this.responseContentEncoding = builder.responseContentEncoding;
        this.responseContentLanguage = builder.responseContentLanguage;
        this.responseContentType = builder.responseContentType;
        this.responseExpires = builder.responseExpires;
        this.versionId = builder.versionId;
        this.sseCustomerAlgorithm = builder.sseCustomerAlgorithm;
        this.sseCustomerKey = builder.sseCustomerKey;
        this.sseCustomerKeyMD5 = builder.sseCustomerKeyMD5;
        this.requestPayer = builder.requestPayer;
        this.partNumber = builder.partNumber;
        this.expectedBucketOwner = builder.expectedBucketOwner;
        this.checksumMode = builder.checksumMode;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String ifMatch() {
        return this.ifMatch;
    }

    public final Instant ifModifiedSince() {
        return this.ifModifiedSince;
    }

    public final String ifNoneMatch() {
        return this.ifNoneMatch;
    }

    public final Instant ifUnmodifiedSince() {
        return this.ifUnmodifiedSince;
    }

    public final String key() {
        return this.key;
    }

    public final String range() {
        return this.range;
    }

    public final String responseCacheControl() {
        return this.responseCacheControl;
    }

    public final String responseContentDisposition() {
        return this.responseContentDisposition;
    }

    public final String responseContentEncoding() {
        return this.responseContentEncoding;
    }

    public final String responseContentLanguage() {
        return this.responseContentLanguage;
    }

    public final String responseContentType() {
        return this.responseContentType;
    }

    public final Instant responseExpires() {
        return this.responseExpires;
    }

    public final String versionId() {
        return this.versionId;
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

    public final RequestPayer requestPayer() {
        return RequestPayer.fromValue(this.requestPayer);
    }

    public final String requestPayerAsString() {
        return this.requestPayer;
    }

    public final Integer partNumber() {
        return this.partNumber;
    }

    public final String expectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    public final ChecksumMode checksumMode() {
        return ChecksumMode.fromValue(this.checksumMode);
    }

    public final String checksumModeAsString() {
        return this.checksumMode;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.ifMatch());
        hashCode = 31 * hashCode + Objects.hashCode(this.ifModifiedSince());
        hashCode = 31 * hashCode + Objects.hashCode(this.ifNoneMatch());
        hashCode = 31 * hashCode + Objects.hashCode(this.ifUnmodifiedSince());
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.range());
        hashCode = 31 * hashCode + Objects.hashCode(this.responseCacheControl());
        hashCode = 31 * hashCode + Objects.hashCode(this.responseContentDisposition());
        hashCode = 31 * hashCode + Objects.hashCode(this.responseContentEncoding());
        hashCode = 31 * hashCode + Objects.hashCode(this.responseContentLanguage());
        hashCode = 31 * hashCode + Objects.hashCode(this.responseContentType());
        hashCode = 31 * hashCode + Objects.hashCode(this.responseExpires());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerAlgorithm());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKeyMD5());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestPayerAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.partNumber());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumModeAsString());
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
        if (!(obj instanceof GetObjectRequest)) {
            return false;
        }
        GetObjectRequest other = (GetObjectRequest)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.ifMatch(), other.ifMatch()) && Objects.equals(this.ifModifiedSince(), other.ifModifiedSince()) && Objects.equals(this.ifNoneMatch(), other.ifNoneMatch()) && Objects.equals(this.ifUnmodifiedSince(), other.ifUnmodifiedSince()) && Objects.equals(this.key(), other.key()) && Objects.equals(this.range(), other.range()) && Objects.equals(this.responseCacheControl(), other.responseCacheControl()) && Objects.equals(this.responseContentDisposition(), other.responseContentDisposition()) && Objects.equals(this.responseContentEncoding(), other.responseContentEncoding()) && Objects.equals(this.responseContentLanguage(), other.responseContentLanguage()) && Objects.equals(this.responseContentType(), other.responseContentType()) && Objects.equals(this.responseExpires(), other.responseExpires()) && Objects.equals(this.versionId(), other.versionId()) && Objects.equals(this.sseCustomerAlgorithm(), other.sseCustomerAlgorithm()) && Objects.equals(this.sseCustomerKey(), other.sseCustomerKey()) && Objects.equals(this.sseCustomerKeyMD5(), other.sseCustomerKeyMD5()) && Objects.equals(this.requestPayerAsString(), other.requestPayerAsString()) && Objects.equals(this.partNumber(), other.partNumber()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner()) && Objects.equals(this.checksumModeAsString(), other.checksumModeAsString());
    }

    public final String toString() {
        return ToString.builder((String)"GetObjectRequest").add("Bucket", (Object)this.bucket()).add("IfMatch", (Object)this.ifMatch()).add("IfModifiedSince", (Object)this.ifModifiedSince()).add("IfNoneMatch", (Object)this.ifNoneMatch()).add("IfUnmodifiedSince", (Object)this.ifUnmodifiedSince()).add("Key", (Object)this.key()).add("Range", (Object)this.range()).add("ResponseCacheControl", (Object)this.responseCacheControl()).add("ResponseContentDisposition", (Object)this.responseContentDisposition()).add("ResponseContentEncoding", (Object)this.responseContentEncoding()).add("ResponseContentLanguage", (Object)this.responseContentLanguage()).add("ResponseContentType", (Object)this.responseContentType()).add("ResponseExpires", (Object)this.responseExpires()).add("VersionId", (Object)this.versionId()).add("SSECustomerAlgorithm", (Object)this.sseCustomerAlgorithm()).add("SSECustomerKey", (Object)(this.sseCustomerKey() == null ? null : "*** Sensitive Data Redacted ***")).add("SSECustomerKeyMD5", (Object)this.sseCustomerKeyMD5()).add("RequestPayer", (Object)this.requestPayerAsString()).add("PartNumber", (Object)this.partNumber()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).add("ChecksumMode", (Object)this.checksumModeAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "IfMatch": {
                return Optional.ofNullable(clazz.cast(this.ifMatch()));
            }
            case "IfModifiedSince": {
                return Optional.ofNullable(clazz.cast(this.ifModifiedSince()));
            }
            case "IfNoneMatch": {
                return Optional.ofNullable(clazz.cast(this.ifNoneMatch()));
            }
            case "IfUnmodifiedSince": {
                return Optional.ofNullable(clazz.cast(this.ifUnmodifiedSince()));
            }
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "Range": {
                return Optional.ofNullable(clazz.cast(this.range()));
            }
            case "ResponseCacheControl": {
                return Optional.ofNullable(clazz.cast(this.responseCacheControl()));
            }
            case "ResponseContentDisposition": {
                return Optional.ofNullable(clazz.cast(this.responseContentDisposition()));
            }
            case "ResponseContentEncoding": {
                return Optional.ofNullable(clazz.cast(this.responseContentEncoding()));
            }
            case "ResponseContentLanguage": {
                return Optional.ofNullable(clazz.cast(this.responseContentLanguage()));
            }
            case "ResponseContentType": {
                return Optional.ofNullable(clazz.cast(this.responseContentType()));
            }
            case "ResponseExpires": {
                return Optional.ofNullable(clazz.cast(this.responseExpires()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
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
            case "RequestPayer": {
                return Optional.ofNullable(clazz.cast(this.requestPayerAsString()));
            }
            case "PartNumber": {
                return Optional.ofNullable(clazz.cast(this.partNumber()));
            }
            case "ExpectedBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedBucketOwner()));
            }
            case "ChecksumMode": {
                return Optional.ofNullable(clazz.cast(this.checksumModeAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetObjectRequest, T> g) {
        return obj -> g.apply((GetObjectRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String bucket;
        private String ifMatch;
        private Instant ifModifiedSince;
        private String ifNoneMatch;
        private Instant ifUnmodifiedSince;
        private String key;
        private String range;
        private String responseCacheControl;
        private String responseContentDisposition;
        private String responseContentEncoding;
        private String responseContentLanguage;
        private String responseContentType;
        private Instant responseExpires;
        private String versionId;
        private String sseCustomerAlgorithm;
        private String sseCustomerKey;
        private String sseCustomerKeyMD5;
        private String requestPayer;
        private Integer partNumber;
        private String expectedBucketOwner;
        private String checksumMode;

        private BuilderImpl() {
        }

        private BuilderImpl(GetObjectRequest model) {
            super(model);
            this.bucket(model.bucket);
            this.ifMatch(model.ifMatch);
            this.ifModifiedSince(model.ifModifiedSince);
            this.ifNoneMatch(model.ifNoneMatch);
            this.ifUnmodifiedSince(model.ifUnmodifiedSince);
            this.key(model.key);
            this.range(model.range);
            this.responseCacheControl(model.responseCacheControl);
            this.responseContentDisposition(model.responseContentDisposition);
            this.responseContentEncoding(model.responseContentEncoding);
            this.responseContentLanguage(model.responseContentLanguage);
            this.responseContentType(model.responseContentType);
            this.responseExpires(model.responseExpires);
            this.versionId(model.versionId);
            this.sseCustomerAlgorithm(model.sseCustomerAlgorithm);
            this.sseCustomerKey(model.sseCustomerKey);
            this.sseCustomerKeyMD5(model.sseCustomerKeyMD5);
            this.requestPayer(model.requestPayer);
            this.partNumber(model.partNumber);
            this.expectedBucketOwner(model.expectedBucketOwner);
            this.checksumMode(model.checksumMode);
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

        public final String getIfMatch() {
            return this.ifMatch;
        }

        public final void setIfMatch(String ifMatch) {
            this.ifMatch = ifMatch;
        }

        @Override
        public final Builder ifMatch(String ifMatch) {
            this.ifMatch = ifMatch;
            return this;
        }

        public final Instant getIfModifiedSince() {
            return this.ifModifiedSince;
        }

        public final void setIfModifiedSince(Instant ifModifiedSince) {
            this.ifModifiedSince = ifModifiedSince;
        }

        @Override
        public final Builder ifModifiedSince(Instant ifModifiedSince) {
            this.ifModifiedSince = ifModifiedSince;
            return this;
        }

        public final String getIfNoneMatch() {
            return this.ifNoneMatch;
        }

        public final void setIfNoneMatch(String ifNoneMatch) {
            this.ifNoneMatch = ifNoneMatch;
        }

        @Override
        public final Builder ifNoneMatch(String ifNoneMatch) {
            this.ifNoneMatch = ifNoneMatch;
            return this;
        }

        public final Instant getIfUnmodifiedSince() {
            return this.ifUnmodifiedSince;
        }

        public final void setIfUnmodifiedSince(Instant ifUnmodifiedSince) {
            this.ifUnmodifiedSince = ifUnmodifiedSince;
        }

        @Override
        public final Builder ifUnmodifiedSince(Instant ifUnmodifiedSince) {
            this.ifUnmodifiedSince = ifUnmodifiedSince;
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

        public final String getRange() {
            return this.range;
        }

        public final void setRange(String range) {
            this.range = range;
        }

        @Override
        public final Builder range(String range) {
            this.range = range;
            return this;
        }

        public final String getResponseCacheControl() {
            return this.responseCacheControl;
        }

        public final void setResponseCacheControl(String responseCacheControl) {
            this.responseCacheControl = responseCacheControl;
        }

        @Override
        public final Builder responseCacheControl(String responseCacheControl) {
            this.responseCacheControl = responseCacheControl;
            return this;
        }

        public final String getResponseContentDisposition() {
            return this.responseContentDisposition;
        }

        public final void setResponseContentDisposition(String responseContentDisposition) {
            this.responseContentDisposition = responseContentDisposition;
        }

        @Override
        public final Builder responseContentDisposition(String responseContentDisposition) {
            this.responseContentDisposition = responseContentDisposition;
            return this;
        }

        public final String getResponseContentEncoding() {
            return this.responseContentEncoding;
        }

        public final void setResponseContentEncoding(String responseContentEncoding) {
            this.responseContentEncoding = responseContentEncoding;
        }

        @Override
        public final Builder responseContentEncoding(String responseContentEncoding) {
            this.responseContentEncoding = responseContentEncoding;
            return this;
        }

        public final String getResponseContentLanguage() {
            return this.responseContentLanguage;
        }

        public final void setResponseContentLanguage(String responseContentLanguage) {
            this.responseContentLanguage = responseContentLanguage;
        }

        @Override
        public final Builder responseContentLanguage(String responseContentLanguage) {
            this.responseContentLanguage = responseContentLanguage;
            return this;
        }

        public final String getResponseContentType() {
            return this.responseContentType;
        }

        public final void setResponseContentType(String responseContentType) {
            this.responseContentType = responseContentType;
        }

        @Override
        public final Builder responseContentType(String responseContentType) {
            this.responseContentType = responseContentType;
            return this;
        }

        public final Instant getResponseExpires() {
            return this.responseExpires;
        }

        public final void setResponseExpires(Instant responseExpires) {
            this.responseExpires = responseExpires;
        }

        @Override
        public final Builder responseExpires(Instant responseExpires) {
            this.responseExpires = responseExpires;
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

        public final Integer getPartNumber() {
            return this.partNumber;
        }

        public final void setPartNumber(Integer partNumber) {
            this.partNumber = partNumber;
        }

        @Override
        public final Builder partNumber(Integer partNumber) {
            this.partNumber = partNumber;
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

        public final String getChecksumMode() {
            return this.checksumMode;
        }

        public final void setChecksumMode(String checksumMode) {
            this.checksumMode = checksumMode;
        }

        @Override
        public final Builder checksumMode(String checksumMode) {
            this.checksumMode = checksumMode;
            return this;
        }

        @Override
        public final Builder checksumMode(ChecksumMode checksumMode) {
            this.checksumMode(checksumMode == null ? null : checksumMode.toString());
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
        public GetObjectRequest build() {
            return new GetObjectRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetObjectRequest> {
        public Builder bucket(String var1);

        public Builder ifMatch(String var1);

        public Builder ifModifiedSince(Instant var1);

        public Builder ifNoneMatch(String var1);

        public Builder ifUnmodifiedSince(Instant var1);

        public Builder key(String var1);

        public Builder range(String var1);

        public Builder responseCacheControl(String var1);

        public Builder responseContentDisposition(String var1);

        public Builder responseContentEncoding(String var1);

        public Builder responseContentLanguage(String var1);

        public Builder responseContentType(String var1);

        public Builder responseExpires(Instant var1);

        public Builder versionId(String var1);

        public Builder sseCustomerAlgorithm(String var1);

        public Builder sseCustomerKey(String var1);

        public Builder sseCustomerKeyMD5(String var1);

        public Builder requestPayer(String var1);

        public Builder requestPayer(RequestPayer var1);

        public Builder partNumber(Integer var1);

        public Builder expectedBucketOwner(String var1);

        public Builder checksumMode(String var1);

        public Builder checksumMode(ChecksumMode var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

