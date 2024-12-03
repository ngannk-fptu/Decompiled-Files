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
import software.amazon.awssdk.services.s3.internal.TaggingAdapter;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.MetadataCopier;
import software.amazon.awssdk.services.s3.model.MetadataDirective;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectLockLegalHoldStatus;
import software.amazon.awssdk.services.s3.model.ObjectLockMode;
import software.amazon.awssdk.services.s3.model.RequestPayer;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.services.s3.model.Tagging;
import software.amazon.awssdk.services.s3.model.TaggingDirective;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class CopyObjectRequest
extends S3Request
implements ToCopyableBuilder<Builder, CopyObjectRequest> {
    private static final SdkField<String> ACL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ACL").getter(CopyObjectRequest.getter(CopyObjectRequest::aclAsString)).setter(CopyObjectRequest.setter(Builder::acl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-acl").unmarshallLocationName("x-amz-acl").build()}).build();
    private static final SdkField<String> CACHE_CONTROL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CacheControl").getter(CopyObjectRequest.getter(CopyObjectRequest::cacheControl)).setter(CopyObjectRequest.setter(Builder::cacheControl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Cache-Control").unmarshallLocationName("Cache-Control").build()}).build();
    private static final SdkField<String> CHECKSUM_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumAlgorithm").getter(CopyObjectRequest.getter(CopyObjectRequest::checksumAlgorithmAsString)).setter(CopyObjectRequest.setter(Builder::checksumAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-checksum-algorithm").unmarshallLocationName("x-amz-checksum-algorithm").build()}).build();
    private static final SdkField<String> CONTENT_DISPOSITION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentDisposition").getter(CopyObjectRequest.getter(CopyObjectRequest::contentDisposition)).setter(CopyObjectRequest.setter(Builder::contentDisposition)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-Disposition").unmarshallLocationName("Content-Disposition").build()}).build();
    private static final SdkField<String> CONTENT_ENCODING_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentEncoding").getter(CopyObjectRequest.getter(CopyObjectRequest::contentEncoding)).setter(CopyObjectRequest.setter(Builder::contentEncoding)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-Encoding").unmarshallLocationName("Content-Encoding").build()}).build();
    private static final SdkField<String> CONTENT_LANGUAGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentLanguage").getter(CopyObjectRequest.getter(CopyObjectRequest::contentLanguage)).setter(CopyObjectRequest.setter(Builder::contentLanguage)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-Language").unmarshallLocationName("Content-Language").build()}).build();
    private static final SdkField<String> CONTENT_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentType").getter(CopyObjectRequest.getter(CopyObjectRequest::contentType)).setter(CopyObjectRequest.setter(Builder::contentType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-Type").unmarshallLocationName("Content-Type").build()}).build();
    private static final SdkField<String> COPY_SOURCE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySource").getter(CopyObjectRequest.getter(CopyObjectRequest::copySource)).setter(CopyObjectRequest.setter(Builder::copySource)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source").unmarshallLocationName("x-amz-copy-source").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> COPY_SOURCE_IF_MATCH_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceIfMatch").getter(CopyObjectRequest.getter(CopyObjectRequest::copySourceIfMatch)).setter(CopyObjectRequest.setter(Builder::copySourceIfMatch)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-if-match").unmarshallLocationName("x-amz-copy-source-if-match").build()}).build();
    private static final SdkField<Instant> COPY_SOURCE_IF_MODIFIED_SINCE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("CopySourceIfModifiedSince").getter(CopyObjectRequest.getter(CopyObjectRequest::copySourceIfModifiedSince)).setter(CopyObjectRequest.setter(Builder::copySourceIfModifiedSince)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-if-modified-since").unmarshallLocationName("x-amz-copy-source-if-modified-since").build()}).build();
    private static final SdkField<String> COPY_SOURCE_IF_NONE_MATCH_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceIfNoneMatch").getter(CopyObjectRequest.getter(CopyObjectRequest::copySourceIfNoneMatch)).setter(CopyObjectRequest.setter(Builder::copySourceIfNoneMatch)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-if-none-match").unmarshallLocationName("x-amz-copy-source-if-none-match").build()}).build();
    private static final SdkField<Instant> COPY_SOURCE_IF_UNMODIFIED_SINCE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("CopySourceIfUnmodifiedSince").getter(CopyObjectRequest.getter(CopyObjectRequest::copySourceIfUnmodifiedSince)).setter(CopyObjectRequest.setter(Builder::copySourceIfUnmodifiedSince)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-if-unmodified-since").unmarshallLocationName("x-amz-copy-source-if-unmodified-since").build()}).build();
    private static final SdkField<Instant> EXPIRES_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("Expires").getter(CopyObjectRequest.getter(CopyObjectRequest::expires)).setter(CopyObjectRequest.setter(Builder::expires)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Expires").unmarshallLocationName("Expires").build()}).build();
    private static final SdkField<String> GRANT_FULL_CONTROL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantFullControl").getter(CopyObjectRequest.getter(CopyObjectRequest::grantFullControl)).setter(CopyObjectRequest.setter(Builder::grantFullControl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-full-control").unmarshallLocationName("x-amz-grant-full-control").build()}).build();
    private static final SdkField<String> GRANT_READ_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantRead").getter(CopyObjectRequest.getter(CopyObjectRequest::grantRead)).setter(CopyObjectRequest.setter(Builder::grantRead)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-read").unmarshallLocationName("x-amz-grant-read").build()}).build();
    private static final SdkField<String> GRANT_READ_ACP_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantReadACP").getter(CopyObjectRequest.getter(CopyObjectRequest::grantReadACP)).setter(CopyObjectRequest.setter(Builder::grantReadACP)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-read-acp").unmarshallLocationName("x-amz-grant-read-acp").build()}).build();
    private static final SdkField<String> GRANT_WRITE_ACP_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantWriteACP").getter(CopyObjectRequest.getter(CopyObjectRequest::grantWriteACP)).setter(CopyObjectRequest.setter(Builder::grantWriteACP)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-write-acp").unmarshallLocationName("x-amz-grant-write-acp").build()}).build();
    private static final SdkField<Map<String, String>> METADATA_FIELD = SdkField.builder((MarshallingType)MarshallingType.MAP).memberName("Metadata").getter(CopyObjectRequest.getter(CopyObjectRequest::metadata)).setter(CopyObjectRequest.setter(Builder::metadata)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-meta-").unmarshallLocationName("x-amz-meta-").build(), MapTrait.builder().keyLocationName("key").valueLocationName("value").valueFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("value").unmarshallLocationName("value").build()}).build()).build()}).build();
    private static final SdkField<String> METADATA_DIRECTIVE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("MetadataDirective").getter(CopyObjectRequest.getter(CopyObjectRequest::metadataDirectiveAsString)).setter(CopyObjectRequest.setter(Builder::metadataDirective)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-metadata-directive").unmarshallLocationName("x-amz-metadata-directive").build()}).build();
    private static final SdkField<String> TAGGING_DIRECTIVE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("TaggingDirective").getter(CopyObjectRequest.getter(CopyObjectRequest::taggingDirectiveAsString)).setter(CopyObjectRequest.setter(Builder::taggingDirective)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-tagging-directive").unmarshallLocationName("x-amz-tagging-directive").build()}).build();
    private static final SdkField<String> SERVER_SIDE_ENCRYPTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ServerSideEncryption").getter(CopyObjectRequest.getter(CopyObjectRequest::serverSideEncryptionAsString)).setter(CopyObjectRequest.setter(Builder::serverSideEncryption)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption").unmarshallLocationName("x-amz-server-side-encryption").build()}).build();
    private static final SdkField<String> STORAGE_CLASS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StorageClass").getter(CopyObjectRequest.getter(CopyObjectRequest::storageClassAsString)).setter(CopyObjectRequest.setter(Builder::storageClass)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-storage-class").unmarshallLocationName("x-amz-storage-class").build()}).build();
    private static final SdkField<String> WEBSITE_REDIRECT_LOCATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("WebsiteRedirectLocation").getter(CopyObjectRequest.getter(CopyObjectRequest::websiteRedirectLocation)).setter(CopyObjectRequest.setter(Builder::websiteRedirectLocation)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-website-redirect-location").unmarshallLocationName("x-amz-website-redirect-location").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerAlgorithm").getter(CopyObjectRequest.getter(CopyObjectRequest::sseCustomerAlgorithm)).setter(CopyObjectRequest.setter(Builder::sseCustomerAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-algorithm").unmarshallLocationName("x-amz-server-side-encryption-customer-algorithm").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKey").getter(CopyObjectRequest.getter(CopyObjectRequest::sseCustomerKey)).setter(CopyObjectRequest.setter(Builder::sseCustomerKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key").unmarshallLocationName("x-amz-server-side-encryption-customer-key").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKeyMD5").getter(CopyObjectRequest.getter(CopyObjectRequest::sseCustomerKeyMD5)).setter(CopyObjectRequest.setter(Builder::sseCustomerKeyMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key-MD5").unmarshallLocationName("x-amz-server-side-encryption-customer-key-MD5").build()}).build();
    private static final SdkField<String> SSEKMS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSEKMSKeyId").getter(CopyObjectRequest.getter(CopyObjectRequest::ssekmsKeyId)).setter(CopyObjectRequest.setter(Builder::ssekmsKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-aws-kms-key-id").unmarshallLocationName("x-amz-server-side-encryption-aws-kms-key-id").build()}).build();
    private static final SdkField<String> SSEKMS_ENCRYPTION_CONTEXT_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSEKMSEncryptionContext").getter(CopyObjectRequest.getter(CopyObjectRequest::ssekmsEncryptionContext)).setter(CopyObjectRequest.setter(Builder::ssekmsEncryptionContext)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-context").unmarshallLocationName("x-amz-server-side-encryption-context").build()}).build();
    private static final SdkField<Boolean> BUCKET_KEY_ENABLED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("BucketKeyEnabled").getter(CopyObjectRequest.getter(CopyObjectRequest::bucketKeyEnabled)).setter(CopyObjectRequest.setter(Builder::bucketKeyEnabled)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-bucket-key-enabled").unmarshallLocationName("x-amz-server-side-encryption-bucket-key-enabled").build()}).build();
    private static final SdkField<String> COPY_SOURCE_SSE_CUSTOMER_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceSSECustomerAlgorithm").getter(CopyObjectRequest.getter(CopyObjectRequest::copySourceSSECustomerAlgorithm)).setter(CopyObjectRequest.setter(Builder::copySourceSSECustomerAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-server-side-encryption-customer-algorithm").unmarshallLocationName("x-amz-copy-source-server-side-encryption-customer-algorithm").build()}).build();
    private static final SdkField<String> COPY_SOURCE_SSE_CUSTOMER_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceSSECustomerKey").getter(CopyObjectRequest.getter(CopyObjectRequest::copySourceSSECustomerKey)).setter(CopyObjectRequest.setter(Builder::copySourceSSECustomerKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-server-side-encryption-customer-key").unmarshallLocationName("x-amz-copy-source-server-side-encryption-customer-key").build()}).build();
    private static final SdkField<String> COPY_SOURCE_SSE_CUSTOMER_KEY_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceSSECustomerKeyMD5").getter(CopyObjectRequest.getter(CopyObjectRequest::copySourceSSECustomerKeyMD5)).setter(CopyObjectRequest.setter(Builder::copySourceSSECustomerKeyMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-server-side-encryption-customer-key-MD5").unmarshallLocationName("x-amz-copy-source-server-side-encryption-customer-key-MD5").build()}).build();
    private static final SdkField<String> REQUEST_PAYER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestPayer").getter(CopyObjectRequest.getter(CopyObjectRequest::requestPayerAsString)).setter(CopyObjectRequest.setter(Builder::requestPayer)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-payer").unmarshallLocationName("x-amz-request-payer").build()}).build();
    private static final SdkField<String> TAGGING_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Tagging").getter(CopyObjectRequest.getter(CopyObjectRequest::tagging)).setter(CopyObjectRequest.setter(Builder::tagging)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-tagging").unmarshallLocationName("x-amz-tagging").build()}).build();
    private static final SdkField<String> OBJECT_LOCK_MODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ObjectLockMode").getter(CopyObjectRequest.getter(CopyObjectRequest::objectLockModeAsString)).setter(CopyObjectRequest.setter(Builder::objectLockMode)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-object-lock-mode").unmarshallLocationName("x-amz-object-lock-mode").build()}).build();
    private static final SdkField<Instant> OBJECT_LOCK_RETAIN_UNTIL_DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("ObjectLockRetainUntilDate").getter(CopyObjectRequest.getter(CopyObjectRequest::objectLockRetainUntilDate)).setter(CopyObjectRequest.setter(Builder::objectLockRetainUntilDate)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-object-lock-retain-until-date").unmarshallLocationName("x-amz-object-lock-retain-until-date").build(), TimestampFormatTrait.create((TimestampFormatTrait.Format)TimestampFormatTrait.Format.ISO_8601)}).build();
    private static final SdkField<String> OBJECT_LOCK_LEGAL_HOLD_STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ObjectLockLegalHoldStatus").getter(CopyObjectRequest.getter(CopyObjectRequest::objectLockLegalHoldStatusAsString)).setter(CopyObjectRequest.setter(Builder::objectLockLegalHoldStatus)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-object-lock-legal-hold").unmarshallLocationName("x-amz-object-lock-legal-hold").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(CopyObjectRequest.getter(CopyObjectRequest::expectedBucketOwner)).setter(CopyObjectRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final SdkField<String> EXPECTED_SOURCE_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedSourceBucketOwner").getter(CopyObjectRequest.getter(CopyObjectRequest::expectedSourceBucketOwner)).setter(CopyObjectRequest.setter(Builder::expectedSourceBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-source-expected-bucket-owner").unmarshallLocationName("x-amz-source-expected-bucket-owner").build()}).build();
    private static final SdkField<String> DESTINATION_BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("DestinationBucket").getter(CopyObjectRequest.getter(CopyObjectRequest::destinationBucket)).setter(CopyObjectRequest.setter(Builder::destinationBucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build()}).build();
    private static final SdkField<String> DESTINATION_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("DestinationKey").getter(CopyObjectRequest.getter(CopyObjectRequest::destinationKey)).setter(CopyObjectRequest.setter(Builder::destinationKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.GREEDY_PATH).locationName("Key").unmarshallLocationName("Key").build()}).build();
    private static final SdkField<String> SOURCE_BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SourceBucket").getter(CopyObjectRequest.getter(CopyObjectRequest::sourceBucket)).setter(CopyObjectRequest.setter(Builder::sourceBucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SourceBucket").unmarshallLocationName("SourceBucket").build()}).build();
    private static final SdkField<String> SOURCE_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SourceKey").getter(CopyObjectRequest.getter(CopyObjectRequest::sourceKey)).setter(CopyObjectRequest.setter(Builder::sourceKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SourceKey").unmarshallLocationName("SourceKey").build()}).build();
    private static final SdkField<String> SOURCE_VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SourceVersionId").getter(CopyObjectRequest.getter(CopyObjectRequest::sourceVersionId)).setter(CopyObjectRequest.setter(Builder::sourceVersionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SourceVersionId").unmarshallLocationName("SourceVersionId").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ACL_FIELD, CACHE_CONTROL_FIELD, CHECKSUM_ALGORITHM_FIELD, CONTENT_DISPOSITION_FIELD, CONTENT_ENCODING_FIELD, CONTENT_LANGUAGE_FIELD, CONTENT_TYPE_FIELD, COPY_SOURCE_FIELD, COPY_SOURCE_IF_MATCH_FIELD, COPY_SOURCE_IF_MODIFIED_SINCE_FIELD, COPY_SOURCE_IF_NONE_MATCH_FIELD, COPY_SOURCE_IF_UNMODIFIED_SINCE_FIELD, EXPIRES_FIELD, GRANT_FULL_CONTROL_FIELD, GRANT_READ_FIELD, GRANT_READ_ACP_FIELD, GRANT_WRITE_ACP_FIELD, METADATA_FIELD, METADATA_DIRECTIVE_FIELD, TAGGING_DIRECTIVE_FIELD, SERVER_SIDE_ENCRYPTION_FIELD, STORAGE_CLASS_FIELD, WEBSITE_REDIRECT_LOCATION_FIELD, SSE_CUSTOMER_ALGORITHM_FIELD, SSE_CUSTOMER_KEY_FIELD, SSE_CUSTOMER_KEY_MD5_FIELD, SSEKMS_KEY_ID_FIELD, SSEKMS_ENCRYPTION_CONTEXT_FIELD, BUCKET_KEY_ENABLED_FIELD, COPY_SOURCE_SSE_CUSTOMER_ALGORITHM_FIELD, COPY_SOURCE_SSE_CUSTOMER_KEY_FIELD, COPY_SOURCE_SSE_CUSTOMER_KEY_MD5_FIELD, REQUEST_PAYER_FIELD, TAGGING_FIELD, OBJECT_LOCK_MODE_FIELD, OBJECT_LOCK_RETAIN_UNTIL_DATE_FIELD, OBJECT_LOCK_LEGAL_HOLD_STATUS_FIELD, EXPECTED_BUCKET_OWNER_FIELD, EXPECTED_SOURCE_BUCKET_OWNER_FIELD, DESTINATION_BUCKET_FIELD, DESTINATION_KEY_FIELD, SOURCE_BUCKET_FIELD, SOURCE_KEY_FIELD, SOURCE_VERSION_ID_FIELD));
    private final String acl;
    private final String cacheControl;
    private final String checksumAlgorithm;
    private final String contentDisposition;
    private final String contentEncoding;
    private final String contentLanguage;
    private final String contentType;
    private final String copySource;
    private final String copySourceIfMatch;
    private final Instant copySourceIfModifiedSince;
    private final String copySourceIfNoneMatch;
    private final Instant copySourceIfUnmodifiedSince;
    private final Instant expires;
    private final String grantFullControl;
    private final String grantRead;
    private final String grantReadACP;
    private final String grantWriteACP;
    private final Map<String, String> metadata;
    private final String metadataDirective;
    private final String taggingDirective;
    private final String serverSideEncryption;
    private final String storageClass;
    private final String websiteRedirectLocation;
    private final String sseCustomerAlgorithm;
    private final String sseCustomerKey;
    private final String sseCustomerKeyMD5;
    private final String ssekmsKeyId;
    private final String ssekmsEncryptionContext;
    private final Boolean bucketKeyEnabled;
    private final String copySourceSSECustomerAlgorithm;
    private final String copySourceSSECustomerKey;
    private final String copySourceSSECustomerKeyMD5;
    private final String requestPayer;
    private final String tagging;
    private final String objectLockMode;
    private final Instant objectLockRetainUntilDate;
    private final String objectLockLegalHoldStatus;
    private final String expectedBucketOwner;
    private final String expectedSourceBucketOwner;
    private final String destinationBucket;
    private final String destinationKey;
    private final String sourceBucket;
    private final String sourceKey;
    private final String sourceVersionId;

    private CopyObjectRequest(BuilderImpl builder) {
        super(builder);
        this.acl = builder.acl;
        this.cacheControl = builder.cacheControl;
        this.checksumAlgorithm = builder.checksumAlgorithm;
        this.contentDisposition = builder.contentDisposition;
        this.contentEncoding = builder.contentEncoding;
        this.contentLanguage = builder.contentLanguage;
        this.contentType = builder.contentType;
        this.copySource = builder.copySource;
        this.copySourceIfMatch = builder.copySourceIfMatch;
        this.copySourceIfModifiedSince = builder.copySourceIfModifiedSince;
        this.copySourceIfNoneMatch = builder.copySourceIfNoneMatch;
        this.copySourceIfUnmodifiedSince = builder.copySourceIfUnmodifiedSince;
        this.expires = builder.expires;
        this.grantFullControl = builder.grantFullControl;
        this.grantRead = builder.grantRead;
        this.grantReadACP = builder.grantReadACP;
        this.grantWriteACP = builder.grantWriteACP;
        this.metadata = builder.metadata;
        this.metadataDirective = builder.metadataDirective;
        this.taggingDirective = builder.taggingDirective;
        this.serverSideEncryption = builder.serverSideEncryption;
        this.storageClass = builder.storageClass;
        this.websiteRedirectLocation = builder.websiteRedirectLocation;
        this.sseCustomerAlgorithm = builder.sseCustomerAlgorithm;
        this.sseCustomerKey = builder.sseCustomerKey;
        this.sseCustomerKeyMD5 = builder.sseCustomerKeyMD5;
        this.ssekmsKeyId = builder.ssekmsKeyId;
        this.ssekmsEncryptionContext = builder.ssekmsEncryptionContext;
        this.bucketKeyEnabled = builder.bucketKeyEnabled;
        this.copySourceSSECustomerAlgorithm = builder.copySourceSSECustomerAlgorithm;
        this.copySourceSSECustomerKey = builder.copySourceSSECustomerKey;
        this.copySourceSSECustomerKeyMD5 = builder.copySourceSSECustomerKeyMD5;
        this.requestPayer = builder.requestPayer;
        this.tagging = builder.tagging;
        this.objectLockMode = builder.objectLockMode;
        this.objectLockRetainUntilDate = builder.objectLockRetainUntilDate;
        this.objectLockLegalHoldStatus = builder.objectLockLegalHoldStatus;
        this.expectedBucketOwner = builder.expectedBucketOwner;
        this.expectedSourceBucketOwner = builder.expectedSourceBucketOwner;
        this.destinationBucket = builder.destinationBucket;
        this.destinationKey = builder.destinationKey;
        this.sourceBucket = builder.sourceBucket;
        this.sourceKey = builder.sourceKey;
        this.sourceVersionId = builder.sourceVersionId;
    }

    public final ObjectCannedACL acl() {
        return ObjectCannedACL.fromValue(this.acl);
    }

    public final String aclAsString() {
        return this.acl;
    }

    public final String cacheControl() {
        return this.cacheControl;
    }

    public final ChecksumAlgorithm checksumAlgorithm() {
        return ChecksumAlgorithm.fromValue(this.checksumAlgorithm);
    }

    public final String checksumAlgorithmAsString() {
        return this.checksumAlgorithm;
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

    public final String contentType() {
        return this.contentType;
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

    public final Instant expires() {
        return this.expires;
    }

    public final String grantFullControl() {
        return this.grantFullControl;
    }

    public final String grantRead() {
        return this.grantRead;
    }

    public final String grantReadACP() {
        return this.grantReadACP;
    }

    public final String grantWriteACP() {
        return this.grantWriteACP;
    }

    public final boolean hasMetadata() {
        return this.metadata != null && !(this.metadata instanceof SdkAutoConstructMap);
    }

    public final Map<String, String> metadata() {
        return this.metadata;
    }

    public final MetadataDirective metadataDirective() {
        return MetadataDirective.fromValue(this.metadataDirective);
    }

    public final String metadataDirectiveAsString() {
        return this.metadataDirective;
    }

    public final TaggingDirective taggingDirective() {
        return TaggingDirective.fromValue(this.taggingDirective);
    }

    public final String taggingDirectiveAsString() {
        return this.taggingDirective;
    }

    public final ServerSideEncryption serverSideEncryption() {
        return ServerSideEncryption.fromValue(this.serverSideEncryption);
    }

    public final String serverSideEncryptionAsString() {
        return this.serverSideEncryption;
    }

    public final StorageClass storageClass() {
        return StorageClass.fromValue(this.storageClass);
    }

    public final String storageClassAsString() {
        return this.storageClass;
    }

    public final String websiteRedirectLocation() {
        return this.websiteRedirectLocation;
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

    public final String ssekmsKeyId() {
        return this.ssekmsKeyId;
    }

    public final String ssekmsEncryptionContext() {
        return this.ssekmsEncryptionContext;
    }

    public final Boolean bucketKeyEnabled() {
        return this.bucketKeyEnabled;
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

    public final String tagging() {
        return this.tagging;
    }

    public final ObjectLockMode objectLockMode() {
        return ObjectLockMode.fromValue(this.objectLockMode);
    }

    public final String objectLockModeAsString() {
        return this.objectLockMode;
    }

    public final Instant objectLockRetainUntilDate() {
        return this.objectLockRetainUntilDate;
    }

    public final ObjectLockLegalHoldStatus objectLockLegalHoldStatus() {
        return ObjectLockLegalHoldStatus.fromValue(this.objectLockLegalHoldStatus);
    }

    public final String objectLockLegalHoldStatusAsString() {
        return this.objectLockLegalHoldStatus;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.aclAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.cacheControl());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumAlgorithmAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentDisposition());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentEncoding());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentLanguage());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentType());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySource());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceIfMatch());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceIfModifiedSince());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceIfNoneMatch());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceIfUnmodifiedSince());
        hashCode = 31 * hashCode + Objects.hashCode(this.expires());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantFullControl());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantRead());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantReadACP());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantWriteACP());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasMetadata() ? this.metadata() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.metadataDirectiveAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.taggingDirectiveAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.serverSideEncryptionAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.storageClassAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.websiteRedirectLocation());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerAlgorithm());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKeyMD5());
        hashCode = 31 * hashCode + Objects.hashCode(this.ssekmsKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.ssekmsEncryptionContext());
        hashCode = 31 * hashCode + Objects.hashCode(this.bucketKeyEnabled());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceSSECustomerAlgorithm());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceSSECustomerKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceSSECustomerKeyMD5());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestPayerAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.tagging());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockModeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockRetainUntilDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockLegalHoldStatusAsString());
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
        if (!(obj instanceof CopyObjectRequest)) {
            return false;
        }
        CopyObjectRequest other = (CopyObjectRequest)((Object)obj);
        return Objects.equals(this.aclAsString(), other.aclAsString()) && Objects.equals(this.cacheControl(), other.cacheControl()) && Objects.equals(this.checksumAlgorithmAsString(), other.checksumAlgorithmAsString()) && Objects.equals(this.contentDisposition(), other.contentDisposition()) && Objects.equals(this.contentEncoding(), other.contentEncoding()) && Objects.equals(this.contentLanguage(), other.contentLanguage()) && Objects.equals(this.contentType(), other.contentType()) && Objects.equals(this.copySource(), other.copySource()) && Objects.equals(this.copySourceIfMatch(), other.copySourceIfMatch()) && Objects.equals(this.copySourceIfModifiedSince(), other.copySourceIfModifiedSince()) && Objects.equals(this.copySourceIfNoneMatch(), other.copySourceIfNoneMatch()) && Objects.equals(this.copySourceIfUnmodifiedSince(), other.copySourceIfUnmodifiedSince()) && Objects.equals(this.expires(), other.expires()) && Objects.equals(this.grantFullControl(), other.grantFullControl()) && Objects.equals(this.grantRead(), other.grantRead()) && Objects.equals(this.grantReadACP(), other.grantReadACP()) && Objects.equals(this.grantWriteACP(), other.grantWriteACP()) && this.hasMetadata() == other.hasMetadata() && Objects.equals(this.metadata(), other.metadata()) && Objects.equals(this.metadataDirectiveAsString(), other.metadataDirectiveAsString()) && Objects.equals(this.taggingDirectiveAsString(), other.taggingDirectiveAsString()) && Objects.equals(this.serverSideEncryptionAsString(), other.serverSideEncryptionAsString()) && Objects.equals(this.storageClassAsString(), other.storageClassAsString()) && Objects.equals(this.websiteRedirectLocation(), other.websiteRedirectLocation()) && Objects.equals(this.sseCustomerAlgorithm(), other.sseCustomerAlgorithm()) && Objects.equals(this.sseCustomerKey(), other.sseCustomerKey()) && Objects.equals(this.sseCustomerKeyMD5(), other.sseCustomerKeyMD5()) && Objects.equals(this.ssekmsKeyId(), other.ssekmsKeyId()) && Objects.equals(this.ssekmsEncryptionContext(), other.ssekmsEncryptionContext()) && Objects.equals(this.bucketKeyEnabled(), other.bucketKeyEnabled()) && Objects.equals(this.copySourceSSECustomerAlgorithm(), other.copySourceSSECustomerAlgorithm()) && Objects.equals(this.copySourceSSECustomerKey(), other.copySourceSSECustomerKey()) && Objects.equals(this.copySourceSSECustomerKeyMD5(), other.copySourceSSECustomerKeyMD5()) && Objects.equals(this.requestPayerAsString(), other.requestPayerAsString()) && Objects.equals(this.tagging(), other.tagging()) && Objects.equals(this.objectLockModeAsString(), other.objectLockModeAsString()) && Objects.equals(this.objectLockRetainUntilDate(), other.objectLockRetainUntilDate()) && Objects.equals(this.objectLockLegalHoldStatusAsString(), other.objectLockLegalHoldStatusAsString()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner()) && Objects.equals(this.expectedSourceBucketOwner(), other.expectedSourceBucketOwner()) && Objects.equals(this.destinationBucket(), other.destinationBucket()) && Objects.equals(this.destinationKey(), other.destinationKey()) && Objects.equals(this.sourceBucket(), other.sourceBucket()) && Objects.equals(this.sourceKey(), other.sourceKey()) && Objects.equals(this.sourceVersionId(), other.sourceVersionId());
    }

    public final String toString() {
        return ToString.builder((String)"CopyObjectRequest").add("ACL", (Object)this.aclAsString()).add("CacheControl", (Object)this.cacheControl()).add("ChecksumAlgorithm", (Object)this.checksumAlgorithmAsString()).add("ContentDisposition", (Object)this.contentDisposition()).add("ContentEncoding", (Object)this.contentEncoding()).add("ContentLanguage", (Object)this.contentLanguage()).add("ContentType", (Object)this.contentType()).add("CopySource", (Object)this.copySource()).add("CopySourceIfMatch", (Object)this.copySourceIfMatch()).add("CopySourceIfModifiedSince", (Object)this.copySourceIfModifiedSince()).add("CopySourceIfNoneMatch", (Object)this.copySourceIfNoneMatch()).add("CopySourceIfUnmodifiedSince", (Object)this.copySourceIfUnmodifiedSince()).add("Expires", (Object)this.expires()).add("GrantFullControl", (Object)this.grantFullControl()).add("GrantRead", (Object)this.grantRead()).add("GrantReadACP", (Object)this.grantReadACP()).add("GrantWriteACP", (Object)this.grantWriteACP()).add("Metadata", this.hasMetadata() ? this.metadata() : null).add("MetadataDirective", (Object)this.metadataDirectiveAsString()).add("TaggingDirective", (Object)this.taggingDirectiveAsString()).add("ServerSideEncryption", (Object)this.serverSideEncryptionAsString()).add("StorageClass", (Object)this.storageClassAsString()).add("WebsiteRedirectLocation", (Object)this.websiteRedirectLocation()).add("SSECustomerAlgorithm", (Object)this.sseCustomerAlgorithm()).add("SSECustomerKey", (Object)(this.sseCustomerKey() == null ? null : "*** Sensitive Data Redacted ***")).add("SSECustomerKeyMD5", (Object)this.sseCustomerKeyMD5()).add("SSEKMSKeyId", (Object)(this.ssekmsKeyId() == null ? null : "*** Sensitive Data Redacted ***")).add("SSEKMSEncryptionContext", (Object)(this.ssekmsEncryptionContext() == null ? null : "*** Sensitive Data Redacted ***")).add("BucketKeyEnabled", (Object)this.bucketKeyEnabled()).add("CopySourceSSECustomerAlgorithm", (Object)this.copySourceSSECustomerAlgorithm()).add("CopySourceSSECustomerKey", (Object)(this.copySourceSSECustomerKey() == null ? null : "*** Sensitive Data Redacted ***")).add("CopySourceSSECustomerKeyMD5", (Object)this.copySourceSSECustomerKeyMD5()).add("RequestPayer", (Object)this.requestPayerAsString()).add("Tagging", (Object)this.tagging()).add("ObjectLockMode", (Object)this.objectLockModeAsString()).add("ObjectLockRetainUntilDate", (Object)this.objectLockRetainUntilDate()).add("ObjectLockLegalHoldStatus", (Object)this.objectLockLegalHoldStatusAsString()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).add("ExpectedSourceBucketOwner", (Object)this.expectedSourceBucketOwner()).add("DestinationBucket", (Object)this.destinationBucket()).add("DestinationKey", (Object)this.destinationKey()).add("SourceBucket", (Object)this.sourceBucket()).add("SourceKey", (Object)this.sourceKey()).add("SourceVersionId", (Object)this.sourceVersionId()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ACL": {
                return Optional.ofNullable(clazz.cast(this.aclAsString()));
            }
            case "CacheControl": {
                return Optional.ofNullable(clazz.cast(this.cacheControl()));
            }
            case "ChecksumAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.checksumAlgorithmAsString()));
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
            case "ContentType": {
                return Optional.ofNullable(clazz.cast(this.contentType()));
            }
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
            case "Expires": {
                return Optional.ofNullable(clazz.cast(this.expires()));
            }
            case "GrantFullControl": {
                return Optional.ofNullable(clazz.cast(this.grantFullControl()));
            }
            case "GrantRead": {
                return Optional.ofNullable(clazz.cast(this.grantRead()));
            }
            case "GrantReadACP": {
                return Optional.ofNullable(clazz.cast(this.grantReadACP()));
            }
            case "GrantWriteACP": {
                return Optional.ofNullable(clazz.cast(this.grantWriteACP()));
            }
            case "Metadata": {
                return Optional.ofNullable(clazz.cast(this.metadata()));
            }
            case "MetadataDirective": {
                return Optional.ofNullable(clazz.cast(this.metadataDirectiveAsString()));
            }
            case "TaggingDirective": {
                return Optional.ofNullable(clazz.cast(this.taggingDirectiveAsString()));
            }
            case "ServerSideEncryption": {
                return Optional.ofNullable(clazz.cast(this.serverSideEncryptionAsString()));
            }
            case "StorageClass": {
                return Optional.ofNullable(clazz.cast(this.storageClassAsString()));
            }
            case "WebsiteRedirectLocation": {
                return Optional.ofNullable(clazz.cast(this.websiteRedirectLocation()));
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
            case "SSEKMSKeyId": {
                return Optional.ofNullable(clazz.cast(this.ssekmsKeyId()));
            }
            case "SSEKMSEncryptionContext": {
                return Optional.ofNullable(clazz.cast(this.ssekmsEncryptionContext()));
            }
            case "BucketKeyEnabled": {
                return Optional.ofNullable(clazz.cast(this.bucketKeyEnabled()));
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
            case "Tagging": {
                return Optional.ofNullable(clazz.cast(this.tagging()));
            }
            case "ObjectLockMode": {
                return Optional.ofNullable(clazz.cast(this.objectLockModeAsString()));
            }
            case "ObjectLockRetainUntilDate": {
                return Optional.ofNullable(clazz.cast(this.objectLockRetainUntilDate()));
            }
            case "ObjectLockLegalHoldStatus": {
                return Optional.ofNullable(clazz.cast(this.objectLockLegalHoldStatusAsString()));
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

    private static <T> Function<Object, T> getter(Function<CopyObjectRequest, T> g) {
        return obj -> g.apply((CopyObjectRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String acl;
        private String cacheControl;
        private String checksumAlgorithm;
        private String contentDisposition;
        private String contentEncoding;
        private String contentLanguage;
        private String contentType;
        private String copySource;
        private String copySourceIfMatch;
        private Instant copySourceIfModifiedSince;
        private String copySourceIfNoneMatch;
        private Instant copySourceIfUnmodifiedSince;
        private Instant expires;
        private String grantFullControl;
        private String grantRead;
        private String grantReadACP;
        private String grantWriteACP;
        private Map<String, String> metadata = DefaultSdkAutoConstructMap.getInstance();
        private String metadataDirective;
        private String taggingDirective;
        private String serverSideEncryption;
        private String storageClass;
        private String websiteRedirectLocation;
        private String sseCustomerAlgorithm;
        private String sseCustomerKey;
        private String sseCustomerKeyMD5;
        private String ssekmsKeyId;
        private String ssekmsEncryptionContext;
        private Boolean bucketKeyEnabled;
        private String copySourceSSECustomerAlgorithm;
        private String copySourceSSECustomerKey;
        private String copySourceSSECustomerKeyMD5;
        private String requestPayer;
        private String tagging;
        private String objectLockMode;
        private Instant objectLockRetainUntilDate;
        private String objectLockLegalHoldStatus;
        private String expectedBucketOwner;
        private String expectedSourceBucketOwner;
        private String destinationBucket;
        private String destinationKey;
        private String sourceBucket;
        private String sourceKey;
        private String sourceVersionId;

        private BuilderImpl() {
        }

        private BuilderImpl(CopyObjectRequest model) {
            super(model);
            this.acl(model.acl);
            this.cacheControl(model.cacheControl);
            this.checksumAlgorithm(model.checksumAlgorithm);
            this.contentDisposition(model.contentDisposition);
            this.contentEncoding(model.contentEncoding);
            this.contentLanguage(model.contentLanguage);
            this.contentType(model.contentType);
            this.copySource(model.copySource);
            this.copySourceIfMatch(model.copySourceIfMatch);
            this.copySourceIfModifiedSince(model.copySourceIfModifiedSince);
            this.copySourceIfNoneMatch(model.copySourceIfNoneMatch);
            this.copySourceIfUnmodifiedSince(model.copySourceIfUnmodifiedSince);
            this.expires(model.expires);
            this.grantFullControl(model.grantFullControl);
            this.grantRead(model.grantRead);
            this.grantReadACP(model.grantReadACP);
            this.grantWriteACP(model.grantWriteACP);
            this.metadata(model.metadata);
            this.metadataDirective(model.metadataDirective);
            this.taggingDirective(model.taggingDirective);
            this.serverSideEncryption(model.serverSideEncryption);
            this.storageClass(model.storageClass);
            this.websiteRedirectLocation(model.websiteRedirectLocation);
            this.sseCustomerAlgorithm(model.sseCustomerAlgorithm);
            this.sseCustomerKey(model.sseCustomerKey);
            this.sseCustomerKeyMD5(model.sseCustomerKeyMD5);
            this.ssekmsKeyId(model.ssekmsKeyId);
            this.ssekmsEncryptionContext(model.ssekmsEncryptionContext);
            this.bucketKeyEnabled(model.bucketKeyEnabled);
            this.copySourceSSECustomerAlgorithm(model.copySourceSSECustomerAlgorithm);
            this.copySourceSSECustomerKey(model.copySourceSSECustomerKey);
            this.copySourceSSECustomerKeyMD5(model.copySourceSSECustomerKeyMD5);
            this.requestPayer(model.requestPayer);
            this.tagging(model.tagging);
            this.objectLockMode(model.objectLockMode);
            this.objectLockRetainUntilDate(model.objectLockRetainUntilDate);
            this.objectLockLegalHoldStatus(model.objectLockLegalHoldStatus);
            this.expectedBucketOwner(model.expectedBucketOwner);
            this.expectedSourceBucketOwner(model.expectedSourceBucketOwner);
            this.destinationBucket(model.destinationBucket);
            this.destinationKey(model.destinationKey);
            this.sourceBucket(model.sourceBucket);
            this.sourceKey(model.sourceKey);
            this.sourceVersionId(model.sourceVersionId);
        }

        public final String getAcl() {
            return this.acl;
        }

        public final void setAcl(String acl) {
            this.acl = acl;
        }

        @Override
        public final Builder acl(String acl) {
            this.acl = acl;
            return this;
        }

        @Override
        public final Builder acl(ObjectCannedACL acl) {
            this.acl(acl == null ? null : acl.toString());
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

        public final String getChecksumAlgorithm() {
            return this.checksumAlgorithm;
        }

        public final void setChecksumAlgorithm(String checksumAlgorithm) {
            this.checksumAlgorithm = checksumAlgorithm;
        }

        @Override
        public final Builder checksumAlgorithm(String checksumAlgorithm) {
            this.checksumAlgorithm = checksumAlgorithm;
            return this;
        }

        @Override
        public final Builder checksumAlgorithm(ChecksumAlgorithm checksumAlgorithm) {
            this.checksumAlgorithm(checksumAlgorithm == null ? null : checksumAlgorithm.toString());
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

        public final String getGrantFullControl() {
            return this.grantFullControl;
        }

        public final void setGrantFullControl(String grantFullControl) {
            this.grantFullControl = grantFullControl;
        }

        @Override
        public final Builder grantFullControl(String grantFullControl) {
            this.grantFullControl = grantFullControl;
            return this;
        }

        public final String getGrantRead() {
            return this.grantRead;
        }

        public final void setGrantRead(String grantRead) {
            this.grantRead = grantRead;
        }

        @Override
        public final Builder grantRead(String grantRead) {
            this.grantRead = grantRead;
            return this;
        }

        public final String getGrantReadACP() {
            return this.grantReadACP;
        }

        public final void setGrantReadACP(String grantReadACP) {
            this.grantReadACP = grantReadACP;
        }

        @Override
        public final Builder grantReadACP(String grantReadACP) {
            this.grantReadACP = grantReadACP;
            return this;
        }

        public final String getGrantWriteACP() {
            return this.grantWriteACP;
        }

        public final void setGrantWriteACP(String grantWriteACP) {
            this.grantWriteACP = grantWriteACP;
        }

        @Override
        public final Builder grantWriteACP(String grantWriteACP) {
            this.grantWriteACP = grantWriteACP;
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

        public final String getMetadataDirective() {
            return this.metadataDirective;
        }

        public final void setMetadataDirective(String metadataDirective) {
            this.metadataDirective = metadataDirective;
        }

        @Override
        public final Builder metadataDirective(String metadataDirective) {
            this.metadataDirective = metadataDirective;
            return this;
        }

        @Override
        public final Builder metadataDirective(MetadataDirective metadataDirective) {
            this.metadataDirective(metadataDirective == null ? null : metadataDirective.toString());
            return this;
        }

        public final String getTaggingDirective() {
            return this.taggingDirective;
        }

        public final void setTaggingDirective(String taggingDirective) {
            this.taggingDirective = taggingDirective;
        }

        @Override
        public final Builder taggingDirective(String taggingDirective) {
            this.taggingDirective = taggingDirective;
            return this;
        }

        @Override
        public final Builder taggingDirective(TaggingDirective taggingDirective) {
            this.taggingDirective(taggingDirective == null ? null : taggingDirective.toString());
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

        public final String getWebsiteRedirectLocation() {
            return this.websiteRedirectLocation;
        }

        public final void setWebsiteRedirectLocation(String websiteRedirectLocation) {
            this.websiteRedirectLocation = websiteRedirectLocation;
        }

        @Override
        public final Builder websiteRedirectLocation(String websiteRedirectLocation) {
            this.websiteRedirectLocation = websiteRedirectLocation;
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

        public final String getSsekmsEncryptionContext() {
            return this.ssekmsEncryptionContext;
        }

        public final void setSsekmsEncryptionContext(String ssekmsEncryptionContext) {
            this.ssekmsEncryptionContext = ssekmsEncryptionContext;
        }

        @Override
        public final Builder ssekmsEncryptionContext(String ssekmsEncryptionContext) {
            this.ssekmsEncryptionContext = ssekmsEncryptionContext;
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

        public final String getTagging() {
            return this.tagging;
        }

        public final void setTagging(String tagging) {
            this.tagging = tagging;
        }

        @Override
        public final Builder tagging(String tagging) {
            this.tagging = tagging;
            return this;
        }

        @Override
        public Builder tagging(Tagging tagging) {
            this.tagging(TaggingAdapter.instance().adapt(tagging));
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
        public CopyObjectRequest build() {
            return new CopyObjectRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, CopyObjectRequest> {
        public Builder acl(String var1);

        public Builder acl(ObjectCannedACL var1);

        public Builder cacheControl(String var1);

        public Builder checksumAlgorithm(String var1);

        public Builder checksumAlgorithm(ChecksumAlgorithm var1);

        public Builder contentDisposition(String var1);

        public Builder contentEncoding(String var1);

        public Builder contentLanguage(String var1);

        public Builder contentType(String var1);

        @Deprecated
        public Builder copySource(String var1);

        public Builder copySourceIfMatch(String var1);

        public Builder copySourceIfModifiedSince(Instant var1);

        public Builder copySourceIfNoneMatch(String var1);

        public Builder copySourceIfUnmodifiedSince(Instant var1);

        public Builder expires(Instant var1);

        public Builder grantFullControl(String var1);

        public Builder grantRead(String var1);

        public Builder grantReadACP(String var1);

        public Builder grantWriteACP(String var1);

        public Builder metadata(Map<String, String> var1);

        public Builder metadataDirective(String var1);

        public Builder metadataDirective(MetadataDirective var1);

        public Builder taggingDirective(String var1);

        public Builder taggingDirective(TaggingDirective var1);

        public Builder serverSideEncryption(String var1);

        public Builder serverSideEncryption(ServerSideEncryption var1);

        public Builder storageClass(String var1);

        public Builder storageClass(StorageClass var1);

        public Builder websiteRedirectLocation(String var1);

        public Builder sseCustomerAlgorithm(String var1);

        public Builder sseCustomerKey(String var1);

        public Builder sseCustomerKeyMD5(String var1);

        public Builder ssekmsKeyId(String var1);

        public Builder ssekmsEncryptionContext(String var1);

        public Builder bucketKeyEnabled(Boolean var1);

        public Builder copySourceSSECustomerAlgorithm(String var1);

        public Builder copySourceSSECustomerKey(String var1);

        public Builder copySourceSSECustomerKeyMD5(String var1);

        public Builder requestPayer(String var1);

        public Builder requestPayer(RequestPayer var1);

        public Builder tagging(String var1);

        public Builder tagging(Tagging var1);

        public Builder objectLockMode(String var1);

        public Builder objectLockMode(ObjectLockMode var1);

        public Builder objectLockRetainUntilDate(Instant var1);

        public Builder objectLockLegalHoldStatus(String var1);

        public Builder objectLockLegalHoldStatus(ObjectLockLegalHoldStatus var1);

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

