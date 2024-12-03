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
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectLockLegalHoldStatus;
import software.amazon.awssdk.services.s3.model.ObjectLockMode;
import software.amazon.awssdk.services.s3.model.RequestPayer;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.services.s3.model.Tagging;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class CreateMultipartUploadRequest
extends S3Request
implements ToCopyableBuilder<Builder, CreateMultipartUploadRequest> {
    private static final SdkField<String> ACL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ACL").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::aclAsString)).setter(CreateMultipartUploadRequest.setter(Builder::acl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-acl").unmarshallLocationName("x-amz-acl").build()}).build();
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::bucket)).setter(CreateMultipartUploadRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> CACHE_CONTROL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CacheControl").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::cacheControl)).setter(CreateMultipartUploadRequest.setter(Builder::cacheControl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Cache-Control").unmarshallLocationName("Cache-Control").build()}).build();
    private static final SdkField<String> CONTENT_DISPOSITION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentDisposition").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::contentDisposition)).setter(CreateMultipartUploadRequest.setter(Builder::contentDisposition)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-Disposition").unmarshallLocationName("Content-Disposition").build()}).build();
    private static final SdkField<String> CONTENT_ENCODING_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentEncoding").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::contentEncoding)).setter(CreateMultipartUploadRequest.setter(Builder::contentEncoding)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-Encoding").unmarshallLocationName("Content-Encoding").build()}).build();
    private static final SdkField<String> CONTENT_LANGUAGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentLanguage").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::contentLanguage)).setter(CreateMultipartUploadRequest.setter(Builder::contentLanguage)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-Language").unmarshallLocationName("Content-Language").build()}).build();
    private static final SdkField<String> CONTENT_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContentType").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::contentType)).setter(CreateMultipartUploadRequest.setter(Builder::contentType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Content-Type").unmarshallLocationName("Content-Type").build()}).build();
    private static final SdkField<Instant> EXPIRES_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("Expires").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::expires)).setter(CreateMultipartUploadRequest.setter(Builder::expires)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Expires").unmarshallLocationName("Expires").build()}).build();
    private static final SdkField<String> GRANT_FULL_CONTROL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantFullControl").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::grantFullControl)).setter(CreateMultipartUploadRequest.setter(Builder::grantFullControl)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-full-control").unmarshallLocationName("x-amz-grant-full-control").build()}).build();
    private static final SdkField<String> GRANT_READ_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantRead").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::grantRead)).setter(CreateMultipartUploadRequest.setter(Builder::grantRead)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-read").unmarshallLocationName("x-amz-grant-read").build()}).build();
    private static final SdkField<String> GRANT_READ_ACP_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantReadACP").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::grantReadACP)).setter(CreateMultipartUploadRequest.setter(Builder::grantReadACP)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-read-acp").unmarshallLocationName("x-amz-grant-read-acp").build()}).build();
    private static final SdkField<String> GRANT_WRITE_ACP_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("GrantWriteACP").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::grantWriteACP)).setter(CreateMultipartUploadRequest.setter(Builder::grantWriteACP)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-grant-write-acp").unmarshallLocationName("x-amz-grant-write-acp").build()}).build();
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::key)).setter(CreateMultipartUploadRequest.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.GREEDY_PATH).locationName("Key").unmarshallLocationName("Key").build(), RequiredTrait.create()}).build();
    private static final SdkField<Map<String, String>> METADATA_FIELD = SdkField.builder((MarshallingType)MarshallingType.MAP).memberName("Metadata").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::metadata)).setter(CreateMultipartUploadRequest.setter(Builder::metadata)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-meta-").unmarshallLocationName("x-amz-meta-").build(), MapTrait.builder().keyLocationName("key").valueLocationName("value").valueFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("value").unmarshallLocationName("value").build()}).build()).build()}).build();
    private static final SdkField<String> SERVER_SIDE_ENCRYPTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ServerSideEncryption").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::serverSideEncryptionAsString)).setter(CreateMultipartUploadRequest.setter(Builder::serverSideEncryption)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption").unmarshallLocationName("x-amz-server-side-encryption").build()}).build();
    private static final SdkField<String> STORAGE_CLASS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StorageClass").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::storageClassAsString)).setter(CreateMultipartUploadRequest.setter(Builder::storageClass)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-storage-class").unmarshallLocationName("x-amz-storage-class").build()}).build();
    private static final SdkField<String> WEBSITE_REDIRECT_LOCATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("WebsiteRedirectLocation").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::websiteRedirectLocation)).setter(CreateMultipartUploadRequest.setter(Builder::websiteRedirectLocation)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-website-redirect-location").unmarshallLocationName("x-amz-website-redirect-location").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerAlgorithm").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::sseCustomerAlgorithm)).setter(CreateMultipartUploadRequest.setter(Builder::sseCustomerAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-algorithm").unmarshallLocationName("x-amz-server-side-encryption-customer-algorithm").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKey").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::sseCustomerKey)).setter(CreateMultipartUploadRequest.setter(Builder::sseCustomerKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key").unmarshallLocationName("x-amz-server-side-encryption-customer-key").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKeyMD5").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::sseCustomerKeyMD5)).setter(CreateMultipartUploadRequest.setter(Builder::sseCustomerKeyMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key-MD5").unmarshallLocationName("x-amz-server-side-encryption-customer-key-MD5").build()}).build();
    private static final SdkField<String> SSEKMS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSEKMSKeyId").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::ssekmsKeyId)).setter(CreateMultipartUploadRequest.setter(Builder::ssekmsKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-aws-kms-key-id").unmarshallLocationName("x-amz-server-side-encryption-aws-kms-key-id").build()}).build();
    private static final SdkField<String> SSEKMS_ENCRYPTION_CONTEXT_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSEKMSEncryptionContext").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::ssekmsEncryptionContext)).setter(CreateMultipartUploadRequest.setter(Builder::ssekmsEncryptionContext)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-context").unmarshallLocationName("x-amz-server-side-encryption-context").build()}).build();
    private static final SdkField<Boolean> BUCKET_KEY_ENABLED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("BucketKeyEnabled").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::bucketKeyEnabled)).setter(CreateMultipartUploadRequest.setter(Builder::bucketKeyEnabled)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-bucket-key-enabled").unmarshallLocationName("x-amz-server-side-encryption-bucket-key-enabled").build()}).build();
    private static final SdkField<String> REQUEST_PAYER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestPayer").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::requestPayerAsString)).setter(CreateMultipartUploadRequest.setter(Builder::requestPayer)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-payer").unmarshallLocationName("x-amz-request-payer").build()}).build();
    private static final SdkField<String> TAGGING_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Tagging").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::tagging)).setter(CreateMultipartUploadRequest.setter(Builder::tagging)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-tagging").unmarshallLocationName("x-amz-tagging").build()}).build();
    private static final SdkField<String> OBJECT_LOCK_MODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ObjectLockMode").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::objectLockModeAsString)).setter(CreateMultipartUploadRequest.setter(Builder::objectLockMode)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-object-lock-mode").unmarshallLocationName("x-amz-object-lock-mode").build()}).build();
    private static final SdkField<Instant> OBJECT_LOCK_RETAIN_UNTIL_DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("ObjectLockRetainUntilDate").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::objectLockRetainUntilDate)).setter(CreateMultipartUploadRequest.setter(Builder::objectLockRetainUntilDate)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-object-lock-retain-until-date").unmarshallLocationName("x-amz-object-lock-retain-until-date").build(), TimestampFormatTrait.create((TimestampFormatTrait.Format)TimestampFormatTrait.Format.ISO_8601)}).build();
    private static final SdkField<String> OBJECT_LOCK_LEGAL_HOLD_STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ObjectLockLegalHoldStatus").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::objectLockLegalHoldStatusAsString)).setter(CreateMultipartUploadRequest.setter(Builder::objectLockLegalHoldStatus)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-object-lock-legal-hold").unmarshallLocationName("x-amz-object-lock-legal-hold").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::expectedBucketOwner)).setter(CreateMultipartUploadRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final SdkField<String> CHECKSUM_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumAlgorithm").getter(CreateMultipartUploadRequest.getter(CreateMultipartUploadRequest::checksumAlgorithmAsString)).setter(CreateMultipartUploadRequest.setter(Builder::checksumAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-checksum-algorithm").unmarshallLocationName("x-amz-checksum-algorithm").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ACL_FIELD, BUCKET_FIELD, CACHE_CONTROL_FIELD, CONTENT_DISPOSITION_FIELD, CONTENT_ENCODING_FIELD, CONTENT_LANGUAGE_FIELD, CONTENT_TYPE_FIELD, EXPIRES_FIELD, GRANT_FULL_CONTROL_FIELD, GRANT_READ_FIELD, GRANT_READ_ACP_FIELD, GRANT_WRITE_ACP_FIELD, KEY_FIELD, METADATA_FIELD, SERVER_SIDE_ENCRYPTION_FIELD, STORAGE_CLASS_FIELD, WEBSITE_REDIRECT_LOCATION_FIELD, SSE_CUSTOMER_ALGORITHM_FIELD, SSE_CUSTOMER_KEY_FIELD, SSE_CUSTOMER_KEY_MD5_FIELD, SSEKMS_KEY_ID_FIELD, SSEKMS_ENCRYPTION_CONTEXT_FIELD, BUCKET_KEY_ENABLED_FIELD, REQUEST_PAYER_FIELD, TAGGING_FIELD, OBJECT_LOCK_MODE_FIELD, OBJECT_LOCK_RETAIN_UNTIL_DATE_FIELD, OBJECT_LOCK_LEGAL_HOLD_STATUS_FIELD, EXPECTED_BUCKET_OWNER_FIELD, CHECKSUM_ALGORITHM_FIELD));
    private final String acl;
    private final String bucket;
    private final String cacheControl;
    private final String contentDisposition;
    private final String contentEncoding;
    private final String contentLanguage;
    private final String contentType;
    private final Instant expires;
    private final String grantFullControl;
    private final String grantRead;
    private final String grantReadACP;
    private final String grantWriteACP;
    private final String key;
    private final Map<String, String> metadata;
    private final String serverSideEncryption;
    private final String storageClass;
    private final String websiteRedirectLocation;
    private final String sseCustomerAlgorithm;
    private final String sseCustomerKey;
    private final String sseCustomerKeyMD5;
    private final String ssekmsKeyId;
    private final String ssekmsEncryptionContext;
    private final Boolean bucketKeyEnabled;
    private final String requestPayer;
    private final String tagging;
    private final String objectLockMode;
    private final Instant objectLockRetainUntilDate;
    private final String objectLockLegalHoldStatus;
    private final String expectedBucketOwner;
    private final String checksumAlgorithm;

    private CreateMultipartUploadRequest(BuilderImpl builder) {
        super(builder);
        this.acl = builder.acl;
        this.bucket = builder.bucket;
        this.cacheControl = builder.cacheControl;
        this.contentDisposition = builder.contentDisposition;
        this.contentEncoding = builder.contentEncoding;
        this.contentLanguage = builder.contentLanguage;
        this.contentType = builder.contentType;
        this.expires = builder.expires;
        this.grantFullControl = builder.grantFullControl;
        this.grantRead = builder.grantRead;
        this.grantReadACP = builder.grantReadACP;
        this.grantWriteACP = builder.grantWriteACP;
        this.key = builder.key;
        this.metadata = builder.metadata;
        this.serverSideEncryption = builder.serverSideEncryption;
        this.storageClass = builder.storageClass;
        this.websiteRedirectLocation = builder.websiteRedirectLocation;
        this.sseCustomerAlgorithm = builder.sseCustomerAlgorithm;
        this.sseCustomerKey = builder.sseCustomerKey;
        this.sseCustomerKeyMD5 = builder.sseCustomerKeyMD5;
        this.ssekmsKeyId = builder.ssekmsKeyId;
        this.ssekmsEncryptionContext = builder.ssekmsEncryptionContext;
        this.bucketKeyEnabled = builder.bucketKeyEnabled;
        this.requestPayer = builder.requestPayer;
        this.tagging = builder.tagging;
        this.objectLockMode = builder.objectLockMode;
        this.objectLockRetainUntilDate = builder.objectLockRetainUntilDate;
        this.objectLockLegalHoldStatus = builder.objectLockLegalHoldStatus;
        this.expectedBucketOwner = builder.expectedBucketOwner;
        this.checksumAlgorithm = builder.checksumAlgorithm;
    }

    public final ObjectCannedACL acl() {
        return ObjectCannedACL.fromValue(this.acl);
    }

    public final String aclAsString() {
        return this.acl;
    }

    public final String bucket() {
        return this.bucket;
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

    public final String contentType() {
        return this.contentType;
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

    public final String key() {
        return this.key;
    }

    public final boolean hasMetadata() {
        return this.metadata != null && !(this.metadata instanceof SdkAutoConstructMap);
    }

    public final Map<String, String> metadata() {
        return this.metadata;
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

    public final ChecksumAlgorithm checksumAlgorithm() {
        return ChecksumAlgorithm.fromValue(this.checksumAlgorithm);
    }

    public final String checksumAlgorithmAsString() {
        return this.checksumAlgorithm;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.cacheControl());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentDisposition());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentEncoding());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentLanguage());
        hashCode = 31 * hashCode + Objects.hashCode(this.contentType());
        hashCode = 31 * hashCode + Objects.hashCode(this.expires());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantFullControl());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantRead());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantReadACP());
        hashCode = 31 * hashCode + Objects.hashCode(this.grantWriteACP());
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasMetadata() ? this.metadata() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.serverSideEncryptionAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.storageClassAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.websiteRedirectLocation());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerAlgorithm());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKeyMD5());
        hashCode = 31 * hashCode + Objects.hashCode(this.ssekmsKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.ssekmsEncryptionContext());
        hashCode = 31 * hashCode + Objects.hashCode(this.bucketKeyEnabled());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestPayerAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.tagging());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockModeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockRetainUntilDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockLegalHoldStatusAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumAlgorithmAsString());
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
        if (!(obj instanceof CreateMultipartUploadRequest)) {
            return false;
        }
        CreateMultipartUploadRequest other = (CreateMultipartUploadRequest)((Object)obj);
        return Objects.equals(this.aclAsString(), other.aclAsString()) && Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.cacheControl(), other.cacheControl()) && Objects.equals(this.contentDisposition(), other.contentDisposition()) && Objects.equals(this.contentEncoding(), other.contentEncoding()) && Objects.equals(this.contentLanguage(), other.contentLanguage()) && Objects.equals(this.contentType(), other.contentType()) && Objects.equals(this.expires(), other.expires()) && Objects.equals(this.grantFullControl(), other.grantFullControl()) && Objects.equals(this.grantRead(), other.grantRead()) && Objects.equals(this.grantReadACP(), other.grantReadACP()) && Objects.equals(this.grantWriteACP(), other.grantWriteACP()) && Objects.equals(this.key(), other.key()) && this.hasMetadata() == other.hasMetadata() && Objects.equals(this.metadata(), other.metadata()) && Objects.equals(this.serverSideEncryptionAsString(), other.serverSideEncryptionAsString()) && Objects.equals(this.storageClassAsString(), other.storageClassAsString()) && Objects.equals(this.websiteRedirectLocation(), other.websiteRedirectLocation()) && Objects.equals(this.sseCustomerAlgorithm(), other.sseCustomerAlgorithm()) && Objects.equals(this.sseCustomerKey(), other.sseCustomerKey()) && Objects.equals(this.sseCustomerKeyMD5(), other.sseCustomerKeyMD5()) && Objects.equals(this.ssekmsKeyId(), other.ssekmsKeyId()) && Objects.equals(this.ssekmsEncryptionContext(), other.ssekmsEncryptionContext()) && Objects.equals(this.bucketKeyEnabled(), other.bucketKeyEnabled()) && Objects.equals(this.requestPayerAsString(), other.requestPayerAsString()) && Objects.equals(this.tagging(), other.tagging()) && Objects.equals(this.objectLockModeAsString(), other.objectLockModeAsString()) && Objects.equals(this.objectLockRetainUntilDate(), other.objectLockRetainUntilDate()) && Objects.equals(this.objectLockLegalHoldStatusAsString(), other.objectLockLegalHoldStatusAsString()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner()) && Objects.equals(this.checksumAlgorithmAsString(), other.checksumAlgorithmAsString());
    }

    public final String toString() {
        return ToString.builder((String)"CreateMultipartUploadRequest").add("ACL", (Object)this.aclAsString()).add("Bucket", (Object)this.bucket()).add("CacheControl", (Object)this.cacheControl()).add("ContentDisposition", (Object)this.contentDisposition()).add("ContentEncoding", (Object)this.contentEncoding()).add("ContentLanguage", (Object)this.contentLanguage()).add("ContentType", (Object)this.contentType()).add("Expires", (Object)this.expires()).add("GrantFullControl", (Object)this.grantFullControl()).add("GrantRead", (Object)this.grantRead()).add("GrantReadACP", (Object)this.grantReadACP()).add("GrantWriteACP", (Object)this.grantWriteACP()).add("Key", (Object)this.key()).add("Metadata", this.hasMetadata() ? this.metadata() : null).add("ServerSideEncryption", (Object)this.serverSideEncryptionAsString()).add("StorageClass", (Object)this.storageClassAsString()).add("WebsiteRedirectLocation", (Object)this.websiteRedirectLocation()).add("SSECustomerAlgorithm", (Object)this.sseCustomerAlgorithm()).add("SSECustomerKey", (Object)(this.sseCustomerKey() == null ? null : "*** Sensitive Data Redacted ***")).add("SSECustomerKeyMD5", (Object)this.sseCustomerKeyMD5()).add("SSEKMSKeyId", (Object)(this.ssekmsKeyId() == null ? null : "*** Sensitive Data Redacted ***")).add("SSEKMSEncryptionContext", (Object)(this.ssekmsEncryptionContext() == null ? null : "*** Sensitive Data Redacted ***")).add("BucketKeyEnabled", (Object)this.bucketKeyEnabled()).add("RequestPayer", (Object)this.requestPayerAsString()).add("Tagging", (Object)this.tagging()).add("ObjectLockMode", (Object)this.objectLockModeAsString()).add("ObjectLockRetainUntilDate", (Object)this.objectLockRetainUntilDate()).add("ObjectLockLegalHoldStatus", (Object)this.objectLockLegalHoldStatusAsString()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).add("ChecksumAlgorithm", (Object)this.checksumAlgorithmAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ACL": {
                return Optional.ofNullable(clazz.cast(this.aclAsString()));
            }
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
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
            case "ContentType": {
                return Optional.ofNullable(clazz.cast(this.contentType()));
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
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "Metadata": {
                return Optional.ofNullable(clazz.cast(this.metadata()));
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
            case "ChecksumAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.checksumAlgorithmAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CreateMultipartUploadRequest, T> g) {
        return obj -> g.apply((CreateMultipartUploadRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String acl;
        private String bucket;
        private String cacheControl;
        private String contentDisposition;
        private String contentEncoding;
        private String contentLanguage;
        private String contentType;
        private Instant expires;
        private String grantFullControl;
        private String grantRead;
        private String grantReadACP;
        private String grantWriteACP;
        private String key;
        private Map<String, String> metadata = DefaultSdkAutoConstructMap.getInstance();
        private String serverSideEncryption;
        private String storageClass;
        private String websiteRedirectLocation;
        private String sseCustomerAlgorithm;
        private String sseCustomerKey;
        private String sseCustomerKeyMD5;
        private String ssekmsKeyId;
        private String ssekmsEncryptionContext;
        private Boolean bucketKeyEnabled;
        private String requestPayer;
        private String tagging;
        private String objectLockMode;
        private Instant objectLockRetainUntilDate;
        private String objectLockLegalHoldStatus;
        private String expectedBucketOwner;
        private String checksumAlgorithm;

        private BuilderImpl() {
        }

        private BuilderImpl(CreateMultipartUploadRequest model) {
            super(model);
            this.acl(model.acl);
            this.bucket(model.bucket);
            this.cacheControl(model.cacheControl);
            this.contentDisposition(model.contentDisposition);
            this.contentEncoding(model.contentEncoding);
            this.contentLanguage(model.contentLanguage);
            this.contentType(model.contentType);
            this.expires(model.expires);
            this.grantFullControl(model.grantFullControl);
            this.grantRead(model.grantRead);
            this.grantReadACP(model.grantReadACP);
            this.grantWriteACP(model.grantWriteACP);
            this.key(model.key);
            this.metadata(model.metadata);
            this.serverSideEncryption(model.serverSideEncryption);
            this.storageClass(model.storageClass);
            this.websiteRedirectLocation(model.websiteRedirectLocation);
            this.sseCustomerAlgorithm(model.sseCustomerAlgorithm);
            this.sseCustomerKey(model.sseCustomerKey);
            this.sseCustomerKeyMD5(model.sseCustomerKeyMD5);
            this.ssekmsKeyId(model.ssekmsKeyId);
            this.ssekmsEncryptionContext(model.ssekmsEncryptionContext);
            this.bucketKeyEnabled(model.bucketKeyEnabled);
            this.requestPayer(model.requestPayer);
            this.tagging(model.tagging);
            this.objectLockMode(model.objectLockMode);
            this.objectLockRetainUntilDate(model.objectLockRetainUntilDate);
            this.objectLockLegalHoldStatus(model.objectLockLegalHoldStatus);
            this.expectedBucketOwner(model.expectedBucketOwner);
            this.checksumAlgorithm(model.checksumAlgorithm);
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
        public CreateMultipartUploadRequest build() {
            return new CreateMultipartUploadRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, CreateMultipartUploadRequest> {
        public Builder acl(String var1);

        public Builder acl(ObjectCannedACL var1);

        public Builder bucket(String var1);

        public Builder cacheControl(String var1);

        public Builder contentDisposition(String var1);

        public Builder contentEncoding(String var1);

        public Builder contentLanguage(String var1);

        public Builder contentType(String var1);

        public Builder expires(Instant var1);

        public Builder grantFullControl(String var1);

        public Builder grantRead(String var1);

        public Builder grantReadACP(String var1);

        public Builder grantWriteACP(String var1);

        public Builder key(String var1);

        public Builder metadata(Map<String, String> var1);

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

        public Builder checksumAlgorithm(String var1);

        public Builder checksumAlgorithm(ChecksumAlgorithm var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

