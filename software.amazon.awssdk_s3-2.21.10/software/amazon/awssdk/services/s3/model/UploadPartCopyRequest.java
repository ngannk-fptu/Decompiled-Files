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
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.RequestPayer;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class UploadPartCopyRequest
extends S3Request
implements ToCopyableBuilder<Builder, UploadPartCopyRequest> {
    private static final SdkField<String> COPY_SOURCE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySource").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::copySource)).setter(UploadPartCopyRequest.setter(Builder::copySource)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source").unmarshallLocationName("x-amz-copy-source").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> COPY_SOURCE_IF_MATCH_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceIfMatch").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::copySourceIfMatch)).setter(UploadPartCopyRequest.setter(Builder::copySourceIfMatch)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-if-match").unmarshallLocationName("x-amz-copy-source-if-match").build()}).build();
    private static final SdkField<Instant> COPY_SOURCE_IF_MODIFIED_SINCE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("CopySourceIfModifiedSince").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::copySourceIfModifiedSince)).setter(UploadPartCopyRequest.setter(Builder::copySourceIfModifiedSince)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-if-modified-since").unmarshallLocationName("x-amz-copy-source-if-modified-since").build()}).build();
    private static final SdkField<String> COPY_SOURCE_IF_NONE_MATCH_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceIfNoneMatch").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::copySourceIfNoneMatch)).setter(UploadPartCopyRequest.setter(Builder::copySourceIfNoneMatch)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-if-none-match").unmarshallLocationName("x-amz-copy-source-if-none-match").build()}).build();
    private static final SdkField<Instant> COPY_SOURCE_IF_UNMODIFIED_SINCE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("CopySourceIfUnmodifiedSince").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::copySourceIfUnmodifiedSince)).setter(UploadPartCopyRequest.setter(Builder::copySourceIfUnmodifiedSince)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-if-unmodified-since").unmarshallLocationName("x-amz-copy-source-if-unmodified-since").build()}).build();
    private static final SdkField<String> COPY_SOURCE_RANGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceRange").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::copySourceRange)).setter(UploadPartCopyRequest.setter(Builder::copySourceRange)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-range").unmarshallLocationName("x-amz-copy-source-range").build()}).build();
    private static final SdkField<Integer> PART_NUMBER_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("PartNumber").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::partNumber)).setter(UploadPartCopyRequest.setter(Builder::partNumber)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("partNumber").unmarshallLocationName("partNumber").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> UPLOAD_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("UploadId").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::uploadId)).setter(UploadPartCopyRequest.setter(Builder::uploadId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.QUERY_PARAM).locationName("uploadId").unmarshallLocationName("uploadId").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> SSE_CUSTOMER_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerAlgorithm").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::sseCustomerAlgorithm)).setter(UploadPartCopyRequest.setter(Builder::sseCustomerAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-algorithm").unmarshallLocationName("x-amz-server-side-encryption-customer-algorithm").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKey").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::sseCustomerKey)).setter(UploadPartCopyRequest.setter(Builder::sseCustomerKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key").unmarshallLocationName("x-amz-server-side-encryption-customer-key").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKeyMD5").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::sseCustomerKeyMD5)).setter(UploadPartCopyRequest.setter(Builder::sseCustomerKeyMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key-MD5").unmarshallLocationName("x-amz-server-side-encryption-customer-key-MD5").build()}).build();
    private static final SdkField<String> COPY_SOURCE_SSE_CUSTOMER_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceSSECustomerAlgorithm").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::copySourceSSECustomerAlgorithm)).setter(UploadPartCopyRequest.setter(Builder::copySourceSSECustomerAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-server-side-encryption-customer-algorithm").unmarshallLocationName("x-amz-copy-source-server-side-encryption-customer-algorithm").build()}).build();
    private static final SdkField<String> COPY_SOURCE_SSE_CUSTOMER_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceSSECustomerKey").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::copySourceSSECustomerKey)).setter(UploadPartCopyRequest.setter(Builder::copySourceSSECustomerKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-server-side-encryption-customer-key").unmarshallLocationName("x-amz-copy-source-server-side-encryption-customer-key").build()}).build();
    private static final SdkField<String> COPY_SOURCE_SSE_CUSTOMER_KEY_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceSSECustomerKeyMD5").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::copySourceSSECustomerKeyMD5)).setter(UploadPartCopyRequest.setter(Builder::copySourceSSECustomerKeyMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-server-side-encryption-customer-key-MD5").unmarshallLocationName("x-amz-copy-source-server-side-encryption-customer-key-MD5").build()}).build();
    private static final SdkField<String> REQUEST_PAYER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestPayer").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::requestPayerAsString)).setter(UploadPartCopyRequest.setter(Builder::requestPayer)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-payer").unmarshallLocationName("x-amz-request-payer").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::expectedBucketOwner)).setter(UploadPartCopyRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final SdkField<String> EXPECTED_SOURCE_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedSourceBucketOwner").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::expectedSourceBucketOwner)).setter(UploadPartCopyRequest.setter(Builder::expectedSourceBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-source-expected-bucket-owner").unmarshallLocationName("x-amz-source-expected-bucket-owner").build()}).build();
    private static final SdkField<String> DESTINATION_BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("DestinationBucket").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::destinationBucket)).setter(UploadPartCopyRequest.setter(Builder::destinationBucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build()}).build();
    private static final SdkField<String> DESTINATION_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("DestinationKey").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::destinationKey)).setter(UploadPartCopyRequest.setter(Builder::destinationKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.GREEDY_PATH).locationName("Key").unmarshallLocationName("Key").build()}).build();
    private static final SdkField<String> SOURCE_BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SourceBucket").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::sourceBucket)).setter(UploadPartCopyRequest.setter(Builder::sourceBucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SourceBucket").unmarshallLocationName("SourceBucket").build()}).build();
    private static final SdkField<String> SOURCE_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SourceKey").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::sourceKey)).setter(UploadPartCopyRequest.setter(Builder::sourceKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SourceKey").unmarshallLocationName("SourceKey").build()}).build();
    private static final SdkField<String> SOURCE_VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SourceVersionId").getter(UploadPartCopyRequest.getter(UploadPartCopyRequest::sourceVersionId)).setter(UploadPartCopyRequest.setter(Builder::sourceVersionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SourceVersionId").unmarshallLocationName("SourceVersionId").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(COPY_SOURCE_FIELD, COPY_SOURCE_IF_MATCH_FIELD, COPY_SOURCE_IF_MODIFIED_SINCE_FIELD, COPY_SOURCE_IF_NONE_MATCH_FIELD, COPY_SOURCE_IF_UNMODIFIED_SINCE_FIELD, COPY_SOURCE_RANGE_FIELD, PART_NUMBER_FIELD, UPLOAD_ID_FIELD, SSE_CUSTOMER_ALGORITHM_FIELD, SSE_CUSTOMER_KEY_FIELD, SSE_CUSTOMER_KEY_MD5_FIELD, COPY_SOURCE_SSE_CUSTOMER_ALGORITHM_FIELD, COPY_SOURCE_SSE_CUSTOMER_KEY_FIELD, COPY_SOURCE_SSE_CUSTOMER_KEY_MD5_FIELD, REQUEST_PAYER_FIELD, EXPECTED_BUCKET_OWNER_FIELD, EXPECTED_SOURCE_BUCKET_OWNER_FIELD, DESTINATION_BUCKET_FIELD, DESTINATION_KEY_FIELD, SOURCE_BUCKET_FIELD, SOURCE_KEY_FIELD, SOURCE_VERSION_ID_FIELD));
    private final String copySource;
    private final String copySourceIfMatch;
    private final Instant copySourceIfModifiedSince;
    private final String copySourceIfNoneMatch;
    private final Instant copySourceIfUnmodifiedSince;
    private final String copySourceRange;
    private final Integer partNumber;
    private final String uploadId;
    private final String sseCustomerAlgorithm;
    private final String sseCustomerKey;
    private final String sseCustomerKeyMD5;
    private final String copySourceSSECustomerAlgorithm;
    private final String copySourceSSECustomerKey;
    private final String copySourceSSECustomerKeyMD5;
    private final String requestPayer;
    private final String expectedBucketOwner;
    private final String expectedSourceBucketOwner;
    private final String destinationBucket;
    private final String destinationKey;
    private final String sourceBucket;
    private final String sourceKey;
    private final String sourceVersionId;

    private UploadPartCopyRequest(BuilderImpl builder) {
        super(builder);
        this.copySource = builder.copySource;
        this.copySourceIfMatch = builder.copySourceIfMatch;
        this.copySourceIfModifiedSince = builder.copySourceIfModifiedSince;
        this.copySourceIfNoneMatch = builder.copySourceIfNoneMatch;
        this.copySourceIfUnmodifiedSince = builder.copySourceIfUnmodifiedSince;
        this.copySourceRange = builder.copySourceRange;
        this.partNumber = builder.partNumber;
        this.uploadId = builder.uploadId;
        this.sseCustomerAlgorithm = builder.sseCustomerAlgorithm;
        this.sseCustomerKey = builder.sseCustomerKey;
        this.sseCustomerKeyMD5 = builder.sseCustomerKeyMD5;
        this.copySourceSSECustomerAlgorithm = builder.copySourceSSECustomerAlgorithm;
        this.copySourceSSECustomerKey = builder.copySourceSSECustomerKey;
        this.copySourceSSECustomerKeyMD5 = builder.copySourceSSECustomerKeyMD5;
        this.requestPayer = builder.requestPayer;
        this.expectedBucketOwner = builder.expectedBucketOwner;
        this.expectedSourceBucketOwner = builder.expectedSourceBucketOwner;
        this.destinationBucket = builder.destinationBucket;
        this.destinationKey = builder.destinationKey;
        this.sourceBucket = builder.sourceBucket;
        this.sourceKey = builder.sourceKey;
        this.sourceVersionId = builder.sourceVersionId;
    }

    @Deprecated
    public final String copySource() {
        return this.copySource;
    }

    public final String copySourceIfMatch() {
        return this.copySourceIfMatch;
    }

    public final Instant copySourceIfModifiedSince() {
        return this.copySourceIfModifiedSince;
    }

    public final String copySourceIfNoneMatch() {
        return this.copySourceIfNoneMatch;
    }

    public final Instant copySourceIfUnmodifiedSince() {
        return this.copySourceIfUnmodifiedSince;
    }

    public final String copySourceRange() {
        return this.copySourceRange;
    }

    public final Integer partNumber() {
        return this.partNumber;
    }

    public final String uploadId() {
        return this.uploadId;
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

    public final String copySourceSSECustomerAlgorithm() {
        return this.copySourceSSECustomerAlgorithm;
    }

    public final String copySourceSSECustomerKey() {
        return this.copySourceSSECustomerKey;
    }

    public final String copySourceSSECustomerKeyMD5() {
        return this.copySourceSSECustomerKeyMD5;
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

    public final String expectedSourceBucketOwner() {
        return this.expectedSourceBucketOwner;
    }

    @Deprecated
    public final String bucket() {
        return this.destinationBucket;
    }

    public final String destinationBucket() {
        return this.destinationBucket;
    }

    @Deprecated
    public final String key() {
        return this.destinationKey;
    }

    public final String destinationKey() {
        return this.destinationKey;
    }

    public final String sourceBucket() {
        return this.sourceBucket;
    }

    public final String sourceKey() {
        return this.sourceKey;
    }

    public final String sourceVersionId() {
        return this.sourceVersionId;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.copySource());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceIfMatch());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceIfModifiedSince());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceIfNoneMatch());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceIfUnmodifiedSince());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceRange());
        hashCode = 31 * hashCode + Objects.hashCode(this.partNumber());
        hashCode = 31 * hashCode + Objects.hashCode(this.uploadId());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerAlgorithm());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKeyMD5());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceSSECustomerAlgorithm());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceSSECustomerKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceSSECustomerKeyMD5());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestPayerAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedSourceBucketOwner());
        hashCode = 31 * hashCode + Objects.hashCode(this.destinationBucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.destinationKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.sourceBucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.sourceKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.sourceVersionId());
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
        if (!(obj instanceof UploadPartCopyRequest)) {
            return false;
        }
        UploadPartCopyRequest other = (UploadPartCopyRequest)((Object)obj);
        return Objects.equals(this.copySource(), other.copySource()) && Objects.equals(this.copySourceIfMatch(), other.copySourceIfMatch()) && Objects.equals(this.copySourceIfModifiedSince(), other.copySourceIfModifiedSince()) && Objects.equals(this.copySourceIfNoneMatch(), other.copySourceIfNoneMatch()) && Objects.equals(this.copySourceIfUnmodifiedSince(), other.copySourceIfUnmodifiedSince()) && Objects.equals(this.copySourceRange(), other.copySourceRange()) && Objects.equals(this.partNumber(), other.partNumber()) && Objects.equals(this.uploadId(), other.uploadId()) && Objects.equals(this.sseCustomerAlgorithm(), other.sseCustomerAlgorithm()) && Objects.equals(this.sseCustomerKey(), other.sseCustomerKey()) && Objects.equals(this.sseCustomerKeyMD5(), other.sseCustomerKeyMD5()) && Objects.equals(this.copySourceSSECustomerAlgorithm(), other.copySourceSSECustomerAlgorithm()) && Objects.equals(this.copySourceSSECustomerKey(), other.copySourceSSECustomerKey()) && Objects.equals(this.copySourceSSECustomerKeyMD5(), other.copySourceSSECustomerKeyMD5()) && Objects.equals(this.requestPayerAsString(), other.requestPayerAsString()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner()) && Objects.equals(this.expectedSourceBucketOwner(), other.expectedSourceBucketOwner()) && Objects.equals(this.destinationBucket(), other.destinationBucket()) && Objects.equals(this.destinationKey(), other.destinationKey()) && Objects.equals(this.sourceBucket(), other.sourceBucket()) && Objects.equals(this.sourceKey(), other.sourceKey()) && Objects.equals(this.sourceVersionId(), other.sourceVersionId());
    }

    public final String toString() {
        return ToString.builder((String)"UploadPartCopyRequest").add("CopySource", (Object)this.copySource()).add("CopySourceIfMatch", (Object)this.copySourceIfMatch()).add("CopySourceIfModifiedSince", (Object)this.copySourceIfModifiedSince()).add("CopySourceIfNoneMatch", (Object)this.copySourceIfNoneMatch()).add("CopySourceIfUnmodifiedSince", (Object)this.copySourceIfUnmodifiedSince()).add("CopySourceRange", (Object)this.copySourceRange()).add("PartNumber", (Object)this.partNumber()).add("UploadId", (Object)this.uploadId()).add("SSECustomerAlgorithm", (Object)this.sseCustomerAlgorithm()).add("SSECustomerKey", (Object)(this.sseCustomerKey() == null ? null : "*** Sensitive Data Redacted ***")).add("SSECustomerKeyMD5", (Object)this.sseCustomerKeyMD5()).add("CopySourceSSECustomerAlgorithm", (Object)this.copySourceSSECustomerAlgorithm()).add("CopySourceSSECustomerKey", (Object)(this.copySourceSSECustomerKey() == null ? null : "*** Sensitive Data Redacted ***")).add("CopySourceSSECustomerKeyMD5", (Object)this.copySourceSSECustomerKeyMD5()).add("RequestPayer", (Object)this.requestPayerAsString()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).add("ExpectedSourceBucketOwner", (Object)this.expectedSourceBucketOwner()).add("DestinationBucket", (Object)this.destinationBucket()).add("DestinationKey", (Object)this.destinationKey()).add("SourceBucket", (Object)this.sourceBucket()).add("SourceKey", (Object)this.sourceKey()).add("SourceVersionId", (Object)this.sourceVersionId()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "CopySource": {
                return Optional.ofNullable(clazz.cast(this.copySource()));
            }
            case "CopySourceIfMatch": {
                return Optional.ofNullable(clazz.cast(this.copySourceIfMatch()));
            }
            case "CopySourceIfModifiedSince": {
                return Optional.ofNullable(clazz.cast(this.copySourceIfModifiedSince()));
            }
            case "CopySourceIfNoneMatch": {
                return Optional.ofNullable(clazz.cast(this.copySourceIfNoneMatch()));
            }
            case "CopySourceIfUnmodifiedSince": {
                return Optional.ofNullable(clazz.cast(this.copySourceIfUnmodifiedSince()));
            }
            case "CopySourceRange": {
                return Optional.ofNullable(clazz.cast(this.copySourceRange()));
            }
            case "PartNumber": {
                return Optional.ofNullable(clazz.cast(this.partNumber()));
            }
            case "UploadId": {
                return Optional.ofNullable(clazz.cast(this.uploadId()));
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
            case "CopySourceSSECustomerAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.copySourceSSECustomerAlgorithm()));
            }
            case "CopySourceSSECustomerKey": {
                return Optional.ofNullable(clazz.cast(this.copySourceSSECustomerKey()));
            }
            case "CopySourceSSECustomerKeyMD5": {
                return Optional.ofNullable(clazz.cast(this.copySourceSSECustomerKeyMD5()));
            }
            case "RequestPayer": {
                return Optional.ofNullable(clazz.cast(this.requestPayerAsString()));
            }
            case "ExpectedBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedBucketOwner()));
            }
            case "ExpectedSourceBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedSourceBucketOwner()));
            }
            case "DestinationBucket": {
                return Optional.ofNullable(clazz.cast(this.destinationBucket()));
            }
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.destinationBucket()));
            }
            case "DestinationKey": {
                return Optional.ofNullable(clazz.cast(this.destinationKey()));
            }
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.destinationKey()));
            }
            case "SourceBucket": {
                return Optional.ofNullable(clazz.cast(this.sourceBucket()));
            }
            case "SourceKey": {
                return Optional.ofNullable(clazz.cast(this.sourceKey()));
            }
            case "SourceVersionId": {
                return Optional.ofNullable(clazz.cast(this.sourceVersionId()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<UploadPartCopyRequest, T> g) {
        return obj -> g.apply((UploadPartCopyRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String copySource;
        private String copySourceIfMatch;
        private Instant copySourceIfModifiedSince;
        private String copySourceIfNoneMatch;
        private Instant copySourceIfUnmodifiedSince;
        private String copySourceRange;
        private Integer partNumber;
        private String uploadId;
        private String sseCustomerAlgorithm;
        private String sseCustomerKey;
        private String sseCustomerKeyMD5;
        private String copySourceSSECustomerAlgorithm;
        private String copySourceSSECustomerKey;
        private String copySourceSSECustomerKeyMD5;
        private String requestPayer;
        private String expectedBucketOwner;
        private String expectedSourceBucketOwner;
        private String destinationBucket;
        private String destinationKey;
        private String sourceBucket;
        private String sourceKey;
        private String sourceVersionId;

        private BuilderImpl() {
        }

        private BuilderImpl(UploadPartCopyRequest model) {
            super(model);
            this.copySource(model.copySource);
            this.copySourceIfMatch(model.copySourceIfMatch);
            this.copySourceIfModifiedSince(model.copySourceIfModifiedSince);
            this.copySourceIfNoneMatch(model.copySourceIfNoneMatch);
            this.copySourceIfUnmodifiedSince(model.copySourceIfUnmodifiedSince);
            this.copySourceRange(model.copySourceRange);
            this.partNumber(model.partNumber);
            this.uploadId(model.uploadId);
            this.sseCustomerAlgorithm(model.sseCustomerAlgorithm);
            this.sseCustomerKey(model.sseCustomerKey);
            this.sseCustomerKeyMD5(model.sseCustomerKeyMD5);
            this.copySourceSSECustomerAlgorithm(model.copySourceSSECustomerAlgorithm);
            this.copySourceSSECustomerKey(model.copySourceSSECustomerKey);
            this.copySourceSSECustomerKeyMD5(model.copySourceSSECustomerKeyMD5);
            this.requestPayer(model.requestPayer);
            this.expectedBucketOwner(model.expectedBucketOwner);
            this.expectedSourceBucketOwner(model.expectedSourceBucketOwner);
            this.destinationBucket(model.destinationBucket);
            this.destinationKey(model.destinationKey);
            this.sourceBucket(model.sourceBucket);
            this.sourceKey(model.sourceKey);
            this.sourceVersionId(model.sourceVersionId);
        }

        @Deprecated
        public final String getCopySource() {
            return this.copySource;
        }

        @Deprecated
        public final void setCopySource(String copySource) {
            this.copySource = copySource;
        }

        @Override
        @Deprecated
        public final Builder copySource(String copySource) {
            this.copySource = copySource;
            return this;
        }

        public final String getCopySourceIfMatch() {
            return this.copySourceIfMatch;
        }

        public final void setCopySourceIfMatch(String copySourceIfMatch) {
            this.copySourceIfMatch = copySourceIfMatch;
        }

        @Override
        public final Builder copySourceIfMatch(String copySourceIfMatch) {
            this.copySourceIfMatch = copySourceIfMatch;
            return this;
        }

        public final Instant getCopySourceIfModifiedSince() {
            return this.copySourceIfModifiedSince;
        }

        public final void setCopySourceIfModifiedSince(Instant copySourceIfModifiedSince) {
            this.copySourceIfModifiedSince = copySourceIfModifiedSince;
        }

        @Override
        public final Builder copySourceIfModifiedSince(Instant copySourceIfModifiedSince) {
            this.copySourceIfModifiedSince = copySourceIfModifiedSince;
            return this;
        }

        public final String getCopySourceIfNoneMatch() {
            return this.copySourceIfNoneMatch;
        }

        public final void setCopySourceIfNoneMatch(String copySourceIfNoneMatch) {
            this.copySourceIfNoneMatch = copySourceIfNoneMatch;
        }

        @Override
        public final Builder copySourceIfNoneMatch(String copySourceIfNoneMatch) {
            this.copySourceIfNoneMatch = copySourceIfNoneMatch;
            return this;
        }

        public final Instant getCopySourceIfUnmodifiedSince() {
            return this.copySourceIfUnmodifiedSince;
        }

        public final void setCopySourceIfUnmodifiedSince(Instant copySourceIfUnmodifiedSince) {
            this.copySourceIfUnmodifiedSince = copySourceIfUnmodifiedSince;
        }

        @Override
        public final Builder copySourceIfUnmodifiedSince(Instant copySourceIfUnmodifiedSince) {
            this.copySourceIfUnmodifiedSince = copySourceIfUnmodifiedSince;
            return this;
        }

        public final String getCopySourceRange() {
            return this.copySourceRange;
        }

        public final void setCopySourceRange(String copySourceRange) {
            this.copySourceRange = copySourceRange;
        }

        @Override
        public final Builder copySourceRange(String copySourceRange) {
            this.copySourceRange = copySourceRange;
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

        public final String getCopySourceSSECustomerAlgorithm() {
            return this.copySourceSSECustomerAlgorithm;
        }

        public final void setCopySourceSSECustomerAlgorithm(String copySourceSSECustomerAlgorithm) {
            this.copySourceSSECustomerAlgorithm = copySourceSSECustomerAlgorithm;
        }

        @Override
        public final Builder copySourceSSECustomerAlgorithm(String copySourceSSECustomerAlgorithm) {
            this.copySourceSSECustomerAlgorithm = copySourceSSECustomerAlgorithm;
            return this;
        }

        public final String getCopySourceSSECustomerKey() {
            return this.copySourceSSECustomerKey;
        }

        public final void setCopySourceSSECustomerKey(String copySourceSSECustomerKey) {
            this.copySourceSSECustomerKey = copySourceSSECustomerKey;
        }

        @Override
        public final Builder copySourceSSECustomerKey(String copySourceSSECustomerKey) {
            this.copySourceSSECustomerKey = copySourceSSECustomerKey;
            return this;
        }

        public final String getCopySourceSSECustomerKeyMD5() {
            return this.copySourceSSECustomerKeyMD5;
        }

        public final void setCopySourceSSECustomerKeyMD5(String copySourceSSECustomerKeyMD5) {
            this.copySourceSSECustomerKeyMD5 = copySourceSSECustomerKeyMD5;
        }

        @Override
        public final Builder copySourceSSECustomerKeyMD5(String copySourceSSECustomerKeyMD5) {
            this.copySourceSSECustomerKeyMD5 = copySourceSSECustomerKeyMD5;
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

        public final String getExpectedSourceBucketOwner() {
            return this.expectedSourceBucketOwner;
        }

        public final void setExpectedSourceBucketOwner(String expectedSourceBucketOwner) {
            this.expectedSourceBucketOwner = expectedSourceBucketOwner;
        }

        @Override
        public final Builder expectedSourceBucketOwner(String expectedSourceBucketOwner) {
            this.expectedSourceBucketOwner = expectedSourceBucketOwner;
            return this;
        }

        public final String getDestinationBucket() {
            return this.destinationBucket;
        }

        public final void setDestinationBucket(String destinationBucket) {
            this.destinationBucket = destinationBucket;
        }

        @Deprecated
        public final void setBucket(String destinationBucket) {
            this.destinationBucket = destinationBucket;
        }

        @Override
        public final Builder destinationBucket(String destinationBucket) {
            this.destinationBucket = destinationBucket;
            return this;
        }

        @Override
        public final Builder bucket(String destinationBucket) {
            this.destinationBucket = destinationBucket;
            return this;
        }

        public final String getDestinationKey() {
            return this.destinationKey;
        }

        public final void setDestinationKey(String destinationKey) {
            this.destinationKey = destinationKey;
        }

        @Deprecated
        public final void setKey(String destinationKey) {
            this.destinationKey = destinationKey;
        }

        @Override
        public final Builder destinationKey(String destinationKey) {
            this.destinationKey = destinationKey;
            return this;
        }

        @Override
        public final Builder key(String destinationKey) {
            this.destinationKey = destinationKey;
            return this;
        }

        public final String getSourceBucket() {
            return this.sourceBucket;
        }

        public final void setSourceBucket(String sourceBucket) {
            this.sourceBucket = sourceBucket;
        }

        @Override
        public final Builder sourceBucket(String sourceBucket) {
            this.sourceBucket = sourceBucket;
            return this;
        }

        public final String getSourceKey() {
            return this.sourceKey;
        }

        public final void setSourceKey(String sourceKey) {
            this.sourceKey = sourceKey;
        }

        @Override
        public final Builder sourceKey(String sourceKey) {
            this.sourceKey = sourceKey;
            return this;
        }

        public final String getSourceVersionId() {
            return this.sourceVersionId;
        }

        public final void setSourceVersionId(String sourceVersionId) {
            this.sourceVersionId = sourceVersionId;
        }

        @Override
        public final Builder sourceVersionId(String sourceVersionId) {
            this.sourceVersionId = sourceVersionId;
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
        public UploadPartCopyRequest build() {
            return new UploadPartCopyRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, UploadPartCopyRequest> {
        @Deprecated
        public Builder copySource(String var1);

        public Builder copySourceIfMatch(String var1);

        public Builder copySourceIfModifiedSince(Instant var1);

        public Builder copySourceIfNoneMatch(String var1);

        public Builder copySourceIfUnmodifiedSince(Instant var1);

        public Builder copySourceRange(String var1);

        public Builder partNumber(Integer var1);

        public Builder uploadId(String var1);

        public Builder sseCustomerAlgorithm(String var1);

        public Builder sseCustomerKey(String var1);

        public Builder sseCustomerKeyMD5(String var1);

        public Builder copySourceSSECustomerAlgorithm(String var1);

        public Builder copySourceSSECustomerKey(String var1);

        public Builder copySourceSSECustomerKeyMD5(String var1);

        public Builder requestPayer(String var1);

        public Builder requestPayer(RequestPayer var1);

        public Builder expectedBucketOwner(String var1);

        public Builder expectedSourceBucketOwner(String var1);

        public Builder destinationBucket(String var1);

        @Deprecated
        public Builder bucket(String var1);

        public Builder destinationKey(String var1);

        @Deprecated
        public Builder key(String var1);

        public Builder sourceBucket(String var1);

        public Builder sourceKey(String var1);

        public Builder sourceVersionId(String var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

