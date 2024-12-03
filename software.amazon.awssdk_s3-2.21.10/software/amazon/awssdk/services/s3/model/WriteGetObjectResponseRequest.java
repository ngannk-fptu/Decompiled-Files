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
 *  software.amazon.awssdk.core.traits.MapTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait$Format
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructMap
 *  software.amazon.awssdk.core.util.SdkAutoConstructMap
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import software.amazon.awssdk.core.traits.MapTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.TimestampFormatTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructMap;
import software.amazon.awssdk.core.util.SdkAutoConstructMap;
import software.amazon.awssdk.services.s3.model.MetadataCopier;
import software.amazon.awssdk.services.s3.model.ObjectLockLegalHoldStatus;
import software.amazon.awssdk.services.s3.model.ObjectLockMode;
import software.amazon.awssdk.services.s3.model.ReplicationStatus;
import software.amazon.awssdk.services.s3.model.RequestCharged;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class WriteGetObjectResponseRequest
extends S3Request
implements ToCopyableBuilder<Builder, WriteGetObjectResponseRequest> {
    private static final SdkField<String> REQUEST_ROUTE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestRoute").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::requestRoute)).setter(WriteGetObjectResponseRequest.setter(Builder::requestRoute)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-route").unmarshallLocationName("x-amz-request-route").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> REQUEST_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestToken").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::requestToken)).setter(WriteGetObjectResponseRequest.setter(Builder::requestToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-token").unmarshallLocationName("x-amz-request-token").build(), RequiredTrait.create()}).build();
    private static final SdkField<Integer> STATUS_CODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("StatusCode").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::statusCode)).setter(WriteGetObjectResponseRequest.setter(Builder::statusCode)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-status").unmarshallLocationName("x-amz-fwd-status").build()}).build();
    private static final SdkField<String> ERROR_CODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ErrorCode").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::errorCode)).setter(WriteGetObjectResponseRequest.setter(Builder::errorCode)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-error-code").unmarshallLocationName("x-amz-fwd-error-code").build()}).build();
    private static final SdkField<String> ERROR_MESSAGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ErrorMessage").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::errorMessage)).setter(WriteGetObjectResponseRequest.setter(Builder::errorMessage)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-error-message").unmarshallLocationName("x-amz-fwd-error-message").build()}).build();
    private static final SdkField<String> ACCEPT_RANGES_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("AcceptRanges").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::acceptRanges)).setter(WriteGetObjectResponseRequest.setter(Builder::acceptRanges)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-accept-ranges").unmarshallLocationName("x-amz-fwd-header-accept-ranges").build()}).build();
    private static final SdkField<String> CACHE_CONTROL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CacheControl").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::cacheControl)).setter(WriteGetObjectResponseRequest.setter(Builder::cacheControl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-Cache-Control").unmarshallLocationName("x-amz-fwd-header-Cache-Control").build()}).build();
    private static final SdkField<String> CONTENT_DISPOSITION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentDisposition").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::contentDisposition)).setter(WriteGetObjectResponseRequest.setter(Builder::contentDisposition)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-Content-Disposition").unmarshallLocationName("x-amz-fwd-header-Content-Disposition").build()}).build();
    private static final SdkField<String> CONTENT_ENCODING_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentEncoding").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::contentEncoding)).setter(WriteGetObjectResponseRequest.setter(Builder::contentEncoding)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-Content-Encoding").unmarshallLocationName("x-amz-fwd-header-Content-Encoding").build()}).build();
    private static final SdkField<String> CONTENT_LANGUAGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentLanguage").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::contentLanguage)).setter(WriteGetObjectResponseRequest.setter(Builder::contentLanguage)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-Content-Language").unmarshallLocationName("x-amz-fwd-header-Content-Language").build()}).build();
    private static final SdkField<Long> CONTENT_LENGTH_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("ContentLength").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::contentLength)).setter(WriteGetObjectResponseRequest.setter(Builder::contentLength)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-Length").unmarshallLocationName("Content-Length").build()}).build();
    private static final SdkField<String> CONTENT_RANGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentRange").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::contentRange)).setter(WriteGetObjectResponseRequest.setter(Builder::contentRange)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-Content-Range").unmarshallLocationName("x-amz-fwd-header-Content-Range").build()}).build();
    private static final SdkField<String> CONTENT_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentType").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::contentType)).setter(WriteGetObjectResponseRequest.setter(Builder::contentType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-Content-Type").unmarshallLocationName("x-amz-fwd-header-Content-Type").build()}).build();
    private static final SdkField<String> CHECKSUM_CRC32_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumCRC32").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::checksumCRC32)).setter(WriteGetObjectResponseRequest.setter(Builder::checksumCRC32)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-checksum-crc32").unmarshallLocationName("x-amz-fwd-header-x-amz-checksum-crc32").build()}).build();
    private static final SdkField<String> CHECKSUM_CRC32_C_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumCRC32C").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::checksumCRC32C)).setter(WriteGetObjectResponseRequest.setter(Builder::checksumCRC32C)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-checksum-crc32c").unmarshallLocationName("x-amz-fwd-header-x-amz-checksum-crc32c").build()}).build();
    private static final SdkField<String> CHECKSUM_SHA1_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumSHA1").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::checksumSHA1)).setter(WriteGetObjectResponseRequest.setter(Builder::checksumSHA1)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-checksum-sha1").unmarshallLocationName("x-amz-fwd-header-x-amz-checksum-sha1").build()}).build();
    private static final SdkField<String> CHECKSUM_SHA256_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumSHA256").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::checksumSHA256)).setter(WriteGetObjectResponseRequest.setter(Builder::checksumSHA256)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-checksum-sha256").unmarshallLocationName("x-amz-fwd-header-x-amz-checksum-sha256").build()}).build();
    private static final SdkField<Boolean> DELETE_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("DeleteMarker").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::deleteMarker)).setter(WriteGetObjectResponseRequest.setter(Builder::deleteMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-delete-marker").unmarshallLocationName("x-amz-fwd-header-x-amz-delete-marker").build()}).build();
    private static final SdkField<String> E_TAG_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ETag").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::eTag)).setter(WriteGetObjectResponseRequest.setter(Builder::eTag)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-ETag").unmarshallLocationName("x-amz-fwd-header-ETag").build()}).build();
    private static final SdkField<Instant> EXPIRES_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("Expires").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::expires)).setter(WriteGetObjectResponseRequest.setter(Builder::expires)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-Expires").unmarshallLocationName("x-amz-fwd-header-Expires").build()}).build();
    private static final SdkField<String> EXPIRATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Expiration").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::expiration)).setter(WriteGetObjectResponseRequest.setter(Builder::expiration)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-expiration").unmarshallLocationName("x-amz-fwd-header-x-amz-expiration").build()}).build();
    private static final SdkField<Instant> LAST_MODIFIED_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("LastModified").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::lastModified)).setter(WriteGetObjectResponseRequest.setter(Builder::lastModified)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-Last-Modified").unmarshallLocationName("x-amz-fwd-header-Last-Modified").build()}).build();
    private static final SdkField<Integer> MISSING_META_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MissingMeta").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::missingMeta)).setter(WriteGetObjectResponseRequest.setter(Builder::missingMeta)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-missing-meta").unmarshallLocationName("x-amz-fwd-header-x-amz-missing-meta").build()}).build();
    private static final SdkField<Map<String, String>> METADATA_FIELD = SdkField.builder((MarshallingType)MarshallingType.MAP).memberName("Metadata").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::metadata)).setter(WriteGetObjectResponseRequest.setter(Builder::metadata)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-meta-").unmarshallLocationName("x-amz-meta-").build(), MapTrait.builder().keyLocationName("key").valueLocationName("value").valueFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("value").unmarshallLocationName("value").build()}).build()).build()}).build();
    private static final SdkField<String> OBJECT_LOCK_MODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ObjectLockMode").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::objectLockModeAsString)).setter(WriteGetObjectResponseRequest.setter(Builder::objectLockMode)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-object-lock-mode").unmarshallLocationName("x-amz-fwd-header-x-amz-object-lock-mode").build()}).build();
    private static final SdkField<String> OBJECT_LOCK_LEGAL_HOLD_STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ObjectLockLegalHoldStatus").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::objectLockLegalHoldStatusAsString)).setter(WriteGetObjectResponseRequest.setter(Builder::objectLockLegalHoldStatus)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-object-lock-legal-hold").unmarshallLocationName("x-amz-fwd-header-x-amz-object-lock-legal-hold").build()}).build();
    private static final SdkField<Instant> OBJECT_LOCK_RETAIN_UNTIL_DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("ObjectLockRetainUntilDate").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::objectLockRetainUntilDate)).setter(WriteGetObjectResponseRequest.setter(Builder::objectLockRetainUntilDate)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-object-lock-retain-until-date").unmarshallLocationName("x-amz-fwd-header-x-amz-object-lock-retain-until-date").build(), TimestampFormatTrait.create((TimestampFormatTrait.Format)TimestampFormatTrait.Format.ISO_8601)}).build();
    private static final SdkField<Integer> PARTS_COUNT_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("PartsCount").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::partsCount)).setter(WriteGetObjectResponseRequest.setter(Builder::partsCount)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-mp-parts-count").unmarshallLocationName("x-amz-fwd-header-x-amz-mp-parts-count").build()}).build();
    private static final SdkField<String> REPLICATION_STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ReplicationStatus").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::replicationStatusAsString)).setter(WriteGetObjectResponseRequest.setter(Builder::replicationStatus)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-replication-status").unmarshallLocationName("x-amz-fwd-header-x-amz-replication-status").build()}).build();
    private static final SdkField<String> REQUEST_CHARGED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestCharged").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::requestChargedAsString)).setter(WriteGetObjectResponseRequest.setter(Builder::requestCharged)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-request-charged").unmarshallLocationName("x-amz-fwd-header-x-amz-request-charged").build()}).build();
    private static final SdkField<String> RESTORE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Restore").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::restore)).setter(WriteGetObjectResponseRequest.setter(Builder::restore)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-restore").unmarshallLocationName("x-amz-fwd-header-x-amz-restore").build()}).build();
    private static final SdkField<String> SERVER_SIDE_ENCRYPTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ServerSideEncryption").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::serverSideEncryptionAsString)).setter(WriteGetObjectResponseRequest.setter(Builder::serverSideEncryption)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-server-side-encryption").unmarshallLocationName("x-amz-fwd-header-x-amz-server-side-encryption").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerAlgorithm").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::sseCustomerAlgorithm)).setter(WriteGetObjectResponseRequest.setter(Builder::sseCustomerAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-server-side-encryption-customer-algorithm").unmarshallLocationName("x-amz-fwd-header-x-amz-server-side-encryption-customer-algorithm").build()}).build();
    private static final SdkField<String> SSEKMS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSEKMSKeyId").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::ssekmsKeyId)).setter(WriteGetObjectResponseRequest.setter(Builder::ssekmsKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-server-side-encryption-aws-kms-key-id").unmarshallLocationName("x-amz-fwd-header-x-amz-server-side-encryption-aws-kms-key-id").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKeyMD5").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::sseCustomerKeyMD5)).setter(WriteGetObjectResponseRequest.setter(Builder::sseCustomerKeyMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-server-side-encryption-customer-key-MD5").unmarshallLocationName("x-amz-fwd-header-x-amz-server-side-encryption-customer-key-MD5").build()}).build();
    private static final SdkField<String> STORAGE_CLASS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StorageClass").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::storageClassAsString)).setter(WriteGetObjectResponseRequest.setter(Builder::storageClass)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-storage-class").unmarshallLocationName("x-amz-fwd-header-x-amz-storage-class").build()}).build();
    private static final SdkField<Integer> TAG_COUNT_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("TagCount").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::tagCount)).setter(WriteGetObjectResponseRequest.setter(Builder::tagCount)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-tagging-count").unmarshallLocationName("x-amz-fwd-header-x-amz-tagging-count").build()}).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::versionId)).setter(WriteGetObjectResponseRequest.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-version-id").unmarshallLocationName("x-amz-fwd-header-x-amz-version-id").build()}).build();
    private static final SdkField<Boolean> BUCKET_KEY_ENABLED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("BucketKeyEnabled").getter(WriteGetObjectResponseRequest.getter(WriteGetObjectResponseRequest::bucketKeyEnabled)).setter(WriteGetObjectResponseRequest.setter(Builder::bucketKeyEnabled)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-fwd-header-x-amz-server-side-encryption-bucket-key-enabled").unmarshallLocationName("x-amz-fwd-header-x-amz-server-side-encryption-bucket-key-enabled").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(REQUEST_ROUTE_FIELD, REQUEST_TOKEN_FIELD, STATUS_CODE_FIELD, ERROR_CODE_FIELD, ERROR_MESSAGE_FIELD, ACCEPT_RANGES_FIELD, CACHE_CONTROL_FIELD, CONTENT_DISPOSITION_FIELD, CONTENT_ENCODING_FIELD, CONTENT_LANGUAGE_FIELD, CONTENT_LENGTH_FIELD, CONTENT_RANGE_FIELD, CONTENT_TYPE_FIELD, CHECKSUM_CRC32_FIELD, CHECKSUM_CRC32_C_FIELD, CHECKSUM_SHA1_FIELD, CHECKSUM_SHA256_FIELD, DELETE_MARKER_FIELD, E_TAG_FIELD, EXPIRES_FIELD, EXPIRATION_FIELD, LAST_MODIFIED_FIELD, MISSING_META_FIELD, METADATA_FIELD, OBJECT_LOCK_MODE_FIELD, OBJECT_LOCK_LEGAL_HOLD_STATUS_FIELD, OBJECT_LOCK_RETAIN_UNTIL_DATE_FIELD, PARTS_COUNT_FIELD, REPLICATION_STATUS_FIELD, REQUEST_CHARGED_FIELD, RESTORE_FIELD, SERVER_SIDE_ENCRYPTION_FIELD, SSE_CUSTOMER_ALGORITHM_FIELD, SSEKMS_KEY_ID_FIELD, SSE_CUSTOMER_KEY_MD5_FIELD, STORAGE_CLASS_FIELD, TAG_COUNT_FIELD, VERSION_ID_FIELD, BUCKET_KEY_ENABLED_FIELD));
    private final String requestRoute;
    private final String requestToken;
    private final Integer statusCode;
    private final String errorCode;
    private final String errorMessage;
    private final String acceptRanges;
    private final String cacheControl;
    private final String contentDisposition;
    private final String contentEncoding;
    private final String contentLanguage;
    private final Long contentLength;
    private final String contentRange;
    private final String contentType;
    private final String checksumCRC32;
    private final String checksumCRC32C;
    private final String checksumSHA1;
    private final String checksumSHA256;
    private final Boolean deleteMarker;
    private final String eTag;
    private final Instant expires;
    private final String expiration;
    private final Instant lastModified;
    private final Integer missingMeta;
    private final Map<String, String> metadata;
    private final String objectLockMode;
    private final String objectLockLegalHoldStatus;
    private final Instant objectLockRetainUntilDate;
    private final Integer partsCount;
    private final String replicationStatus;
    private final String requestCharged;
    private final String restore;
    private final String serverSideEncryption;
    private final String sseCustomerAlgorithm;
    private final String ssekmsKeyId;
    private final String sseCustomerKeyMD5;
    private final String storageClass;
    private final Integer tagCount;
    private final String versionId;
    private final Boolean bucketKeyEnabled;

    private WriteGetObjectResponseRequest(BuilderImpl builder) {
        super(builder);
        this.requestRoute = builder.requestRoute;
        this.requestToken = builder.requestToken;
        this.statusCode = builder.statusCode;
        this.errorCode = builder.errorCode;
        this.errorMessage = builder.errorMessage;
        this.acceptRanges = builder.acceptRanges;
        this.cacheControl = builder.cacheControl;
        this.contentDisposition = builder.contentDisposition;
        this.contentEncoding = builder.contentEncoding;
        this.contentLanguage = builder.contentLanguage;
        this.contentLength = builder.contentLength;
        this.contentRange = builder.contentRange;
        this.contentType = builder.contentType;
        this.checksumCRC32 = builder.checksumCRC32;
        this.checksumCRC32C = builder.checksumCRC32C;
        this.checksumSHA1 = builder.checksumSHA1;
        this.checksumSHA256 = builder.checksumSHA256;
        this.deleteMarker = builder.deleteMarker;
        this.eTag = builder.eTag;
        this.expires = builder.expires;
        this.expiration = builder.expiration;
        this.lastModified = builder.lastModified;
        this.missingMeta = builder.missingMeta;
        this.metadata = builder.metadata;
        this.objectLockMode = builder.objectLockMode;
        this.objectLockLegalHoldStatus = builder.objectLockLegalHoldStatus;
        this.objectLockRetainUntilDate = builder.objectLockRetainUntilDate;
        this.partsCount = builder.partsCount;
        this.replicationStatus = builder.replicationStatus;
        this.requestCharged = builder.requestCharged;
        this.restore = builder.restore;
        this.serverSideEncryption = builder.serverSideEncryption;
        this.sseCustomerAlgorithm = builder.sseCustomerAlgorithm;
        this.ssekmsKeyId = builder.ssekmsKeyId;
        this.sseCustomerKeyMD5 = builder.sseCustomerKeyMD5;
        this.storageClass = builder.storageClass;
        this.tagCount = builder.tagCount;
        this.versionId = builder.versionId;
        this.bucketKeyEnabled = builder.bucketKeyEnabled;
    }

    public final String requestRoute() {
        return this.requestRoute;
    }

    public final String requestToken() {
        return this.requestToken;
    }

    public final Integer statusCode() {
        return this.statusCode;
    }

    public final String errorCode() {
        return this.errorCode;
    }

    public final String errorMessage() {
        return this.errorMessage;
    }

    public final String acceptRanges() {
        return this.acceptRanges;
    }

    public final String cacheControl() {
        return this.cacheControl;
    }

    public final String contentDisposition() {
        return this.contentDisposition;
    }

    public final String contentEncoding() {
        return this.contentEncoding;
    }

    public final String contentLanguage() {
        return this.contentLanguage;
    }

    public final Long contentLength() {
        return this.contentLength;
    }

    public final String contentRange() {
        return this.contentRange;
    }

    public final String contentType() {
        return this.contentType;
    }

    public final String checksumCRC32() {
        return this.checksumCRC32;
    }

    public final String checksumCRC32C() {
        return this.checksumCRC32C;
    }

    public final String checksumSHA1() {
        return this.checksumSHA1;
    }

    public final String checksumSHA256() {
        return this.checksumSHA256;
    }

    public final Boolean deleteMarker() {
        return this.deleteMarker;
    }

    public final String eTag() {
        return this.eTag;
    }

    public final Instant expires() {
        return this.expires;
    }

    public final String expiration() {
        return this.expiration;
    }

    public final Instant lastModified() {
        return this.lastModified;
    }

    public final Integer missingMeta() {
        return this.missingMeta;
    }

    public final boolean hasMetadata() {
        return this.metadata != null && !(this.metadata instanceof SdkAutoConstructMap);
    }

    public final Map<String, String> metadata() {
        return this.metadata;
    }

    public final ObjectLockMode objectLockMode() {
        return ObjectLockMode.fromValue(this.objectLockMode);
    }

    public final String objectLockModeAsString() {
        return this.objectLockMode;
    }

    public final ObjectLockLegalHoldStatus objectLockLegalHoldStatus() {
        return ObjectLockLegalHoldStatus.fromValue(this.objectLockLegalHoldStatus);
    }

    public final String objectLockLegalHoldStatusAsString() {
        return this.objectLockLegalHoldStatus;
    }

    public final Instant objectLockRetainUntilDate() {
        return this.objectLockRetainUntilDate;
    }

    public final Integer partsCount() {
        return this.partsCount;
    }

    public final ReplicationStatus replicationStatus() {
        return ReplicationStatus.fromValue(this.replicationStatus);
    }

    public final String replicationStatusAsString() {
        return this.replicationStatus;
    }

    public final RequestCharged requestCharged() {
        return RequestCharged.fromValue(this.requestCharged);
    }

    public final String requestChargedAsString() {
        return this.requestCharged;
    }

    public final String restore() {
        return this.restore;
    }

    public final ServerSideEncryption serverSideEncryption() {
        return ServerSideEncryption.fromValue(this.serverSideEncryption);
    }

    public final String serverSideEncryptionAsString() {
        return this.serverSideEncryption;
    }

    public final String sseCustomerAlgorithm() {
        return this.sseCustomerAlgorithm;
    }

    public final String ssekmsKeyId() {
        return this.ssekmsKeyId;
    }

    public final String sseCustomerKeyMD5() {
        return this.sseCustomerKeyMD5;
    }

    public final StorageClass storageClass() {
        return StorageClass.fromValue(this.storageClass);
    }

    public final String storageClassAsString() {
        return this.storageClass;
    }

    public final Integer tagCount() {
        return this.tagCount;
    }

    public final String versionId() {
        return this.versionId;
    }

    public final Boolean bucketKeyEnabled() {
        return this.bucketKeyEnabled;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.requestRoute());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.statusCode());
        hashCode = 31 * hashCode + Objects.hashCode(this.errorCode());
        hashCode = 31 * hashCode + Objects.hashCode(this.errorMessage());
        hashCode = 31 * hashCode + Objects.hashCode(this.acceptRanges());
        hashCode = 31 * hashCode + Objects.hashCode(this.cacheControl());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentDisposition());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentEncoding());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentLanguage());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentLength());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentRange());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentType());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumCRC32());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumCRC32C());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumSHA1());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumSHA256());
        hashCode = 31 * hashCode + Objects.hashCode(this.deleteMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.eTag());
        hashCode = 31 * hashCode + Objects.hashCode(this.expires());
        hashCode = 31 * hashCode + Objects.hashCode(this.expiration());
        hashCode = 31 * hashCode + Objects.hashCode(this.lastModified());
        hashCode = 31 * hashCode + Objects.hashCode(this.missingMeta());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasMetadata() ? this.metadata() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockModeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockLegalHoldStatusAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockRetainUntilDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.partsCount());
        hashCode = 31 * hashCode + Objects.hashCode(this.replicationStatusAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestChargedAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.restore());
        hashCode = 31 * hashCode + Objects.hashCode(this.serverSideEncryptionAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerAlgorithm());
        hashCode = 31 * hashCode + Objects.hashCode(this.ssekmsKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKeyMD5());
        hashCode = 31 * hashCode + Objects.hashCode(this.storageClassAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.tagCount());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.bucketKeyEnabled());
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
        if (!(obj instanceof WriteGetObjectResponseRequest)) {
            return false;
        }
        WriteGetObjectResponseRequest other = (WriteGetObjectResponseRequest)((Object)obj);
        return Objects.equals(this.requestRoute(), other.requestRoute()) && Objects.equals(this.requestToken(), other.requestToken()) && Objects.equals(this.statusCode(), other.statusCode()) && Objects.equals(this.errorCode(), other.errorCode()) && Objects.equals(this.errorMessage(), other.errorMessage()) && Objects.equals(this.acceptRanges(), other.acceptRanges()) && Objects.equals(this.cacheControl(), other.cacheControl()) && Objects.equals(this.contentDisposition(), other.contentDisposition()) && Objects.equals(this.contentEncoding(), other.contentEncoding()) && Objects.equals(this.contentLanguage(), other.contentLanguage()) && Objects.equals(this.contentLength(), other.contentLength()) && Objects.equals(this.contentRange(), other.contentRange()) && Objects.equals(this.contentType(), other.contentType()) && Objects.equals(this.checksumCRC32(), other.checksumCRC32()) && Objects.equals(this.checksumCRC32C(), other.checksumCRC32C()) && Objects.equals(this.checksumSHA1(), other.checksumSHA1()) && Objects.equals(this.checksumSHA256(), other.checksumSHA256()) && Objects.equals(this.deleteMarker(), other.deleteMarker()) && Objects.equals(this.eTag(), other.eTag()) && Objects.equals(this.expires(), other.expires()) && Objects.equals(this.expiration(), other.expiration()) && Objects.equals(this.lastModified(), other.lastModified()) && Objects.equals(this.missingMeta(), other.missingMeta()) && this.hasMetadata() == other.hasMetadata() && Objects.equals(this.metadata(), other.metadata()) && Objects.equals(this.objectLockModeAsString(), other.objectLockModeAsString()) && Objects.equals(this.objectLockLegalHoldStatusAsString(), other.objectLockLegalHoldStatusAsString()) && Objects.equals(this.objectLockRetainUntilDate(), other.objectLockRetainUntilDate()) && Objects.equals(this.partsCount(), other.partsCount()) && Objects.equals(this.replicationStatusAsString(), other.replicationStatusAsString()) && Objects.equals(this.requestChargedAsString(), other.requestChargedAsString()) && Objects.equals(this.restore(), other.restore()) && Objects.equals(this.serverSideEncryptionAsString(), other.serverSideEncryptionAsString()) && Objects.equals(this.sseCustomerAlgorithm(), other.sseCustomerAlgorithm()) && Objects.equals(this.ssekmsKeyId(), other.ssekmsKeyId()) && Objects.equals(this.sseCustomerKeyMD5(), other.sseCustomerKeyMD5()) && Objects.equals(this.storageClassAsString(), other.storageClassAsString()) && Objects.equals(this.tagCount(), other.tagCount()) && Objects.equals(this.versionId(), other.versionId()) && Objects.equals(this.bucketKeyEnabled(), other.bucketKeyEnabled());
    }

    public final String toString() {
        return ToString.builder((String)"WriteGetObjectResponseRequest").add("RequestRoute", (Object)this.requestRoute()).add("RequestToken", (Object)this.requestToken()).add("StatusCode", (Object)this.statusCode()).add("ErrorCode", (Object)this.errorCode()).add("ErrorMessage", (Object)this.errorMessage()).add("AcceptRanges", (Object)this.acceptRanges()).add("CacheControl", (Object)this.cacheControl()).add("ContentDisposition", (Object)this.contentDisposition()).add("ContentEncoding", (Object)this.contentEncoding()).add("ContentLanguage", (Object)this.contentLanguage()).add("ContentLength", (Object)this.contentLength()).add("ContentRange", (Object)this.contentRange()).add("ContentType", (Object)this.contentType()).add("ChecksumCRC32", (Object)this.checksumCRC32()).add("ChecksumCRC32C", (Object)this.checksumCRC32C()).add("ChecksumSHA1", (Object)this.checksumSHA1()).add("ChecksumSHA256", (Object)this.checksumSHA256()).add("DeleteMarker", (Object)this.deleteMarker()).add("ETag", (Object)this.eTag()).add("Expires", (Object)this.expires()).add("Expiration", (Object)this.expiration()).add("LastModified", (Object)this.lastModified()).add("MissingMeta", (Object)this.missingMeta()).add("Metadata", this.hasMetadata() ? this.metadata() : null).add("ObjectLockMode", (Object)this.objectLockModeAsString()).add("ObjectLockLegalHoldStatus", (Object)this.objectLockLegalHoldStatusAsString()).add("ObjectLockRetainUntilDate", (Object)this.objectLockRetainUntilDate()).add("PartsCount", (Object)this.partsCount()).add("ReplicationStatus", (Object)this.replicationStatusAsString()).add("RequestCharged", (Object)this.requestChargedAsString()).add("Restore", (Object)this.restore()).add("ServerSideEncryption", (Object)this.serverSideEncryptionAsString()).add("SSECustomerAlgorithm", (Object)this.sseCustomerAlgorithm()).add("SSEKMSKeyId", (Object)(this.ssekmsKeyId() == null ? null : "*** Sensitive Data Redacted ***")).add("SSECustomerKeyMD5", (Object)this.sseCustomerKeyMD5()).add("StorageClass", (Object)this.storageClassAsString()).add("TagCount", (Object)this.tagCount()).add("VersionId", (Object)this.versionId()).add("BucketKeyEnabled", (Object)this.bucketKeyEnabled()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "RequestRoute": {
                return Optional.ofNullable(clazz.cast(this.requestRoute()));
            }
            case "RequestToken": {
                return Optional.ofNullable(clazz.cast(this.requestToken()));
            }
            case "StatusCode": {
                return Optional.ofNullable(clazz.cast(this.statusCode()));
            }
            case "ErrorCode": {
                return Optional.ofNullable(clazz.cast(this.errorCode()));
            }
            case "ErrorMessage": {
                return Optional.ofNullable(clazz.cast(this.errorMessage()));
            }
            case "AcceptRanges": {
                return Optional.ofNullable(clazz.cast(this.acceptRanges()));
            }
            case "CacheControl": {
                return Optional.ofNullable(clazz.cast(this.cacheControl()));
            }
            case "ContentDisposition": {
                return Optional.ofNullable(clazz.cast(this.contentDisposition()));
            }
            case "ContentEncoding": {
                return Optional.ofNullable(clazz.cast(this.contentEncoding()));
            }
            case "ContentLanguage": {
                return Optional.ofNullable(clazz.cast(this.contentLanguage()));
            }
            case "ContentLength": {
                return Optional.ofNullable(clazz.cast(this.contentLength()));
            }
            case "ContentRange": {
                return Optional.ofNullable(clazz.cast(this.contentRange()));
            }
            case "ContentType": {
                return Optional.ofNullable(clazz.cast(this.contentType()));
            }
            case "ChecksumCRC32": {
                return Optional.ofNullable(clazz.cast(this.checksumCRC32()));
            }
            case "ChecksumCRC32C": {
                return Optional.ofNullable(clazz.cast(this.checksumCRC32C()));
            }
            case "ChecksumSHA1": {
                return Optional.ofNullable(clazz.cast(this.checksumSHA1()));
            }
            case "ChecksumSHA256": {
                return Optional.ofNullable(clazz.cast(this.checksumSHA256()));
            }
            case "DeleteMarker": {
                return Optional.ofNullable(clazz.cast(this.deleteMarker()));
            }
            case "ETag": {
                return Optional.ofNullable(clazz.cast(this.eTag()));
            }
            case "Expires": {
                return Optional.ofNullable(clazz.cast(this.expires()));
            }
            case "Expiration": {
                return Optional.ofNullable(clazz.cast(this.expiration()));
            }
            case "LastModified": {
                return Optional.ofNullable(clazz.cast(this.lastModified()));
            }
            case "MissingMeta": {
                return Optional.ofNullable(clazz.cast(this.missingMeta()));
            }
            case "Metadata": {
                return Optional.ofNullable(clazz.cast(this.metadata()));
            }
            case "ObjectLockMode": {
                return Optional.ofNullable(clazz.cast(this.objectLockModeAsString()));
            }
            case "ObjectLockLegalHoldStatus": {
                return Optional.ofNullable(clazz.cast(this.objectLockLegalHoldStatusAsString()));
            }
            case "ObjectLockRetainUntilDate": {
                return Optional.ofNullable(clazz.cast(this.objectLockRetainUntilDate()));
            }
            case "PartsCount": {
                return Optional.ofNullable(clazz.cast(this.partsCount()));
            }
            case "ReplicationStatus": {
                return Optional.ofNullable(clazz.cast(this.replicationStatusAsString()));
            }
            case "RequestCharged": {
                return Optional.ofNullable(clazz.cast(this.requestChargedAsString()));
            }
            case "Restore": {
                return Optional.ofNullable(clazz.cast(this.restore()));
            }
            case "ServerSideEncryption": {
                return Optional.ofNullable(clazz.cast(this.serverSideEncryptionAsString()));
            }
            case "SSECustomerAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.sseCustomerAlgorithm()));
            }
            case "SSEKMSKeyId": {
                return Optional.ofNullable(clazz.cast(this.ssekmsKeyId()));
            }
            case "SSECustomerKeyMD5": {
                return Optional.ofNullable(clazz.cast(this.sseCustomerKeyMD5()));
            }
            case "StorageClass": {
                return Optional.ofNullable(clazz.cast(this.storageClassAsString()));
            }
            case "TagCount": {
                return Optional.ofNullable(clazz.cast(this.tagCount()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
            case "BucketKeyEnabled": {
                return Optional.ofNullable(clazz.cast(this.bucketKeyEnabled()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<WriteGetObjectResponseRequest, T> g) {
        return obj -> g.apply((WriteGetObjectResponseRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String requestRoute;
        private String requestToken;
        private Integer statusCode;
        private String errorCode;
        private String errorMessage;
        private String acceptRanges;
        private String cacheControl;
        private String contentDisposition;
        private String contentEncoding;
        private String contentLanguage;
        private Long contentLength;
        private String contentRange;
        private String contentType;
        private String checksumCRC32;
        private String checksumCRC32C;
        private String checksumSHA1;
        private String checksumSHA256;
        private Boolean deleteMarker;
        private String eTag;
        private Instant expires;
        private String expiration;
        private Instant lastModified;
        private Integer missingMeta;
        private Map<String, String> metadata = DefaultSdkAutoConstructMap.getInstance();
        private String objectLockMode;
        private String objectLockLegalHoldStatus;
        private Instant objectLockRetainUntilDate;
        private Integer partsCount;
        private String replicationStatus;
        private String requestCharged;
        private String restore;
        private String serverSideEncryption;
        private String sseCustomerAlgorithm;
        private String ssekmsKeyId;
        private String sseCustomerKeyMD5;
        private String storageClass;
        private Integer tagCount;
        private String versionId;
        private Boolean bucketKeyEnabled;

        private BuilderImpl() {
        }

        private BuilderImpl(WriteGetObjectResponseRequest model) {
            super(model);
            this.requestRoute(model.requestRoute);
            this.requestToken(model.requestToken);
            this.statusCode(model.statusCode);
            this.errorCode(model.errorCode);
            this.errorMessage(model.errorMessage);
            this.acceptRanges(model.acceptRanges);
            this.cacheControl(model.cacheControl);
            this.contentDisposition(model.contentDisposition);
            this.contentEncoding(model.contentEncoding);
            this.contentLanguage(model.contentLanguage);
            this.contentLength(model.contentLength);
            this.contentRange(model.contentRange);
            this.contentType(model.contentType);
            this.checksumCRC32(model.checksumCRC32);
            this.checksumCRC32C(model.checksumCRC32C);
            this.checksumSHA1(model.checksumSHA1);
            this.checksumSHA256(model.checksumSHA256);
            this.deleteMarker(model.deleteMarker);
            this.eTag(model.eTag);
            this.expires(model.expires);
            this.expiration(model.expiration);
            this.lastModified(model.lastModified);
            this.missingMeta(model.missingMeta);
            this.metadata(model.metadata);
            this.objectLockMode(model.objectLockMode);
            this.objectLockLegalHoldStatus(model.objectLockLegalHoldStatus);
            this.objectLockRetainUntilDate(model.objectLockRetainUntilDate);
            this.partsCount(model.partsCount);
            this.replicationStatus(model.replicationStatus);
            this.requestCharged(model.requestCharged);
            this.restore(model.restore);
            this.serverSideEncryption(model.serverSideEncryption);
            this.sseCustomerAlgorithm(model.sseCustomerAlgorithm);
            this.ssekmsKeyId(model.ssekmsKeyId);
            this.sseCustomerKeyMD5(model.sseCustomerKeyMD5);
            this.storageClass(model.storageClass);
            this.tagCount(model.tagCount);
            this.versionId(model.versionId);
            this.bucketKeyEnabled(model.bucketKeyEnabled);
        }

        public final String getRequestRoute() {
            return this.requestRoute;
        }

        public final void setRequestRoute(String requestRoute) {
            this.requestRoute = requestRoute;
        }

        @Override
        public final Builder requestRoute(String requestRoute) {
            this.requestRoute = requestRoute;
            return this;
        }

        public final String getRequestToken() {
            return this.requestToken;
        }

        public final void setRequestToken(String requestToken) {
            this.requestToken = requestToken;
        }

        @Override
        public final Builder requestToken(String requestToken) {
            this.requestToken = requestToken;
            return this;
        }

        public final Integer getStatusCode() {
            return this.statusCode;
        }

        public final void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public final Builder statusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public final String getErrorCode() {
            return this.errorCode;
        }

        public final void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public final Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public final String getErrorMessage() {
            return this.errorMessage;
        }

        public final void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public final Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public final String getAcceptRanges() {
            return this.acceptRanges;
        }

        public final void setAcceptRanges(String acceptRanges) {
            this.acceptRanges = acceptRanges;
        }

        @Override
        public final Builder acceptRanges(String acceptRanges) {
            this.acceptRanges = acceptRanges;
            return this;
        }

        public final String getCacheControl() {
            return this.cacheControl;
        }

        public final void setCacheControl(String cacheControl) {
            this.cacheControl = cacheControl;
        }

        @Override
        public final Builder cacheControl(String cacheControl) {
            this.cacheControl = cacheControl;
            return this;
        }

        public final String getContentDisposition() {
            return this.contentDisposition;
        }

        public final void setContentDisposition(String contentDisposition) {
            this.contentDisposition = contentDisposition;
        }

        @Override
        public final Builder contentDisposition(String contentDisposition) {
            this.contentDisposition = contentDisposition;
            return this;
        }

        public final String getContentEncoding() {
            return this.contentEncoding;
        }

        public final void setContentEncoding(String contentEncoding) {
            this.contentEncoding = contentEncoding;
        }

        @Override
        public final Builder contentEncoding(String contentEncoding) {
            this.contentEncoding = contentEncoding;
            return this;
        }

        public final String getContentLanguage() {
            return this.contentLanguage;
        }

        public final void setContentLanguage(String contentLanguage) {
            this.contentLanguage = contentLanguage;
        }

        @Override
        public final Builder contentLanguage(String contentLanguage) {
            this.contentLanguage = contentLanguage;
            return this;
        }

        public final Long getContentLength() {
            return this.contentLength;
        }

        public final void setContentLength(Long contentLength) {
            this.contentLength = contentLength;
        }

        @Override
        public final Builder contentLength(Long contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public final String getContentRange() {
            return this.contentRange;
        }

        public final void setContentRange(String contentRange) {
            this.contentRange = contentRange;
        }

        @Override
        public final Builder contentRange(String contentRange) {
            this.contentRange = contentRange;
            return this;
        }

        public final String getContentType() {
            return this.contentType;
        }

        public final void setContentType(String contentType) {
            this.contentType = contentType;
        }

        @Override
        public final Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public final String getChecksumCRC32() {
            return this.checksumCRC32;
        }

        public final void setChecksumCRC32(String checksumCRC32) {
            this.checksumCRC32 = checksumCRC32;
        }

        @Override
        public final Builder checksumCRC32(String checksumCRC32) {
            this.checksumCRC32 = checksumCRC32;
            return this;
        }

        public final String getChecksumCRC32C() {
            return this.checksumCRC32C;
        }

        public final void setChecksumCRC32C(String checksumCRC32C) {
            this.checksumCRC32C = checksumCRC32C;
        }

        @Override
        public final Builder checksumCRC32C(String checksumCRC32C) {
            this.checksumCRC32C = checksumCRC32C;
            return this;
        }

        public final String getChecksumSHA1() {
            return this.checksumSHA1;
        }

        public final void setChecksumSHA1(String checksumSHA1) {
            this.checksumSHA1 = checksumSHA1;
        }

        @Override
        public final Builder checksumSHA1(String checksumSHA1) {
            this.checksumSHA1 = checksumSHA1;
            return this;
        }

        public final String getChecksumSHA256() {
            return this.checksumSHA256;
        }

        public final void setChecksumSHA256(String checksumSHA256) {
            this.checksumSHA256 = checksumSHA256;
        }

        @Override
        public final Builder checksumSHA256(String checksumSHA256) {
            this.checksumSHA256 = checksumSHA256;
            return this;
        }

        public final Boolean getDeleteMarker() {
            return this.deleteMarker;
        }

        public final void setDeleteMarker(Boolean deleteMarker) {
            this.deleteMarker = deleteMarker;
        }

        @Override
        public final Builder deleteMarker(Boolean deleteMarker) {
            this.deleteMarker = deleteMarker;
            return this;
        }

        public final String getETag() {
            return this.eTag;
        }

        public final void setETag(String eTag) {
            this.eTag = eTag;
        }

        @Override
        public final Builder eTag(String eTag) {
            this.eTag = eTag;
            return this;
        }

        public final Instant getExpires() {
            return this.expires;
        }

        public final void setExpires(Instant expires) {
            this.expires = expires;
        }

        @Override
        public final Builder expires(Instant expires) {
            this.expires = expires;
            return this;
        }

        public final String getExpiration() {
            return this.expiration;
        }

        public final void setExpiration(String expiration) {
            this.expiration = expiration;
        }

        @Override
        public final Builder expiration(String expiration) {
            this.expiration = expiration;
            return this;
        }

        public final Instant getLastModified() {
            return this.lastModified;
        }

        public final void setLastModified(Instant lastModified) {
            this.lastModified = lastModified;
        }

        @Override
        public final Builder lastModified(Instant lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public final Integer getMissingMeta() {
            return this.missingMeta;
        }

        public final void setMissingMeta(Integer missingMeta) {
            this.missingMeta = missingMeta;
        }

        @Override
        public final Builder missingMeta(Integer missingMeta) {
            this.missingMeta = missingMeta;
            return this;
        }

        public final Map<String, String> getMetadata() {
            if (this.metadata instanceof SdkAutoConstructMap) {
                return null;
            }
            return this.metadata;
        }

        public final void setMetadata(Map<String, String> metadata) {
            this.metadata = MetadataCopier.copy(metadata);
        }

        @Override
        public final Builder metadata(Map<String, String> metadata) {
            this.metadata = MetadataCopier.copy(metadata);
            return this;
        }

        public final String getObjectLockMode() {
            return this.objectLockMode;
        }

        public final void setObjectLockMode(String objectLockMode) {
            this.objectLockMode = objectLockMode;
        }

        @Override
        public final Builder objectLockMode(String objectLockMode) {
            this.objectLockMode = objectLockMode;
            return this;
        }

        @Override
        public final Builder objectLockMode(ObjectLockMode objectLockMode) {
            this.objectLockMode(objectLockMode == null ? null : objectLockMode.toString());
            return this;
        }

        public final String getObjectLockLegalHoldStatus() {
            return this.objectLockLegalHoldStatus;
        }

        public final void setObjectLockLegalHoldStatus(String objectLockLegalHoldStatus) {
            this.objectLockLegalHoldStatus = objectLockLegalHoldStatus;
        }

        @Override
        public final Builder objectLockLegalHoldStatus(String objectLockLegalHoldStatus) {
            this.objectLockLegalHoldStatus = objectLockLegalHoldStatus;
            return this;
        }

        @Override
        public final Builder objectLockLegalHoldStatus(ObjectLockLegalHoldStatus objectLockLegalHoldStatus) {
            this.objectLockLegalHoldStatus(objectLockLegalHoldStatus == null ? null : objectLockLegalHoldStatus.toString());
            return this;
        }

        public final Instant getObjectLockRetainUntilDate() {
            return this.objectLockRetainUntilDate;
        }

        public final void setObjectLockRetainUntilDate(Instant objectLockRetainUntilDate) {
            this.objectLockRetainUntilDate = objectLockRetainUntilDate;
        }

        @Override
        public final Builder objectLockRetainUntilDate(Instant objectLockRetainUntilDate) {
            this.objectLockRetainUntilDate = objectLockRetainUntilDate;
            return this;
        }

        public final Integer getPartsCount() {
            return this.partsCount;
        }

        public final void setPartsCount(Integer partsCount) {
            this.partsCount = partsCount;
        }

        @Override
        public final Builder partsCount(Integer partsCount) {
            this.partsCount = partsCount;
            return this;
        }

        public final String getReplicationStatus() {
            return this.replicationStatus;
        }

        public final void setReplicationStatus(String replicationStatus) {
            this.replicationStatus = replicationStatus;
        }

        @Override
        public final Builder replicationStatus(String replicationStatus) {
            this.replicationStatus = replicationStatus;
            return this;
        }

        @Override
        public final Builder replicationStatus(ReplicationStatus replicationStatus) {
            this.replicationStatus(replicationStatus == null ? null : replicationStatus.toString());
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

        public final String getRestore() {
            return this.restore;
        }

        public final void setRestore(String restore) {
            this.restore = restore;
        }

        @Override
        public final Builder restore(String restore) {
            this.restore = restore;
            return this;
        }

        public final String getServerSideEncryption() {
            return this.serverSideEncryption;
        }

        public final void setServerSideEncryption(String serverSideEncryption) {
            this.serverSideEncryption = serverSideEncryption;
        }

        @Override
        public final Builder serverSideEncryption(String serverSideEncryption) {
            this.serverSideEncryption = serverSideEncryption;
            return this;
        }

        @Override
        public final Builder serverSideEncryption(ServerSideEncryption serverSideEncryption) {
            this.serverSideEncryption(serverSideEncryption == null ? null : serverSideEncryption.toString());
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

        public final String getSsekmsKeyId() {
            return this.ssekmsKeyId;
        }

        public final void setSsekmsKeyId(String ssekmsKeyId) {
            this.ssekmsKeyId = ssekmsKeyId;
        }

        @Override
        public final Builder ssekmsKeyId(String ssekmsKeyId) {
            this.ssekmsKeyId = ssekmsKeyId;
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

        public final String getStorageClass() {
            return this.storageClass;
        }

        public final void setStorageClass(String storageClass) {
            this.storageClass = storageClass;
        }

        @Override
        public final Builder storageClass(String storageClass) {
            this.storageClass = storageClass;
            return this;
        }

        @Override
        public final Builder storageClass(StorageClass storageClass) {
            this.storageClass(storageClass == null ? null : storageClass.toString());
            return this;
        }

        public final Integer getTagCount() {
            return this.tagCount;
        }

        public final void setTagCount(Integer tagCount) {
            this.tagCount = tagCount;
        }

        @Override
        public final Builder tagCount(Integer tagCount) {
            this.tagCount = tagCount;
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

        public final Boolean getBucketKeyEnabled() {
            return this.bucketKeyEnabled;
        }

        public final void setBucketKeyEnabled(Boolean bucketKeyEnabled) {
            this.bucketKeyEnabled = bucketKeyEnabled;
        }

        @Override
        public final Builder bucketKeyEnabled(Boolean bucketKeyEnabled) {
            this.bucketKeyEnabled = bucketKeyEnabled;
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
        public WriteGetObjectResponseRequest build() {
            return new WriteGetObjectResponseRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, WriteGetObjectResponseRequest> {
        public Builder requestRoute(String var1);

        public Builder requestToken(String var1);

        public Builder statusCode(Integer var1);

        public Builder errorCode(String var1);

        public Builder errorMessage(String var1);

        public Builder acceptRanges(String var1);

        public Builder cacheControl(String var1);

        public Builder contentDisposition(String var1);

        public Builder contentEncoding(String var1);

        public Builder contentLanguage(String var1);

        public Builder contentLength(Long var1);

        public Builder contentRange(String var1);

        public Builder contentType(String var1);

        public Builder checksumCRC32(String var1);

        public Builder checksumCRC32C(String var1);

        public Builder checksumSHA1(String var1);

        public Builder checksumSHA256(String var1);

        public Builder deleteMarker(Boolean var1);

        public Builder eTag(String var1);

        public Builder expires(Instant var1);

        public Builder expiration(String var1);

        public Builder lastModified(Instant var1);

        public Builder missingMeta(Integer var1);

        public Builder metadata(Map<String, String> var1);

        public Builder objectLockMode(String var1);

        public Builder objectLockMode(ObjectLockMode var1);

        public Builder objectLockLegalHoldStatus(String var1);

        public Builder objectLockLegalHoldStatus(ObjectLockLegalHoldStatus var1);

        public Builder objectLockRetainUntilDate(Instant var1);

        public Builder partsCount(Integer var1);

        public Builder replicationStatus(String var1);

        public Builder replicationStatus(ReplicationStatus var1);

        public Builder requestCharged(String var1);

        public Builder requestCharged(RequestCharged var1);

        public Builder restore(String var1);

        public Builder serverSideEncryption(String var1);

        public Builder serverSideEncryption(ServerSideEncryption var1);

        public Builder sseCustomerAlgorithm(String var1);

        public Builder ssekmsKeyId(String var1);

        public Builder sseCustomerKeyMD5(String var1);

        public Builder storageClass(String var1);

        public Builder storageClass(StorageClass var1);

        public Builder tagCount(Integer var1);

        public Builder versionId(String var1);

        public Builder bucketKeyEnabled(Boolean var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

