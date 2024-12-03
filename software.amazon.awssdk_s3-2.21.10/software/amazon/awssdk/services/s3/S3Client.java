/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.awscore.AwsClient
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.core.ResponseBytes
 *  software.amazon.awssdk.core.ResponseInputStream
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.sync.RequestBody
 *  software.amazon.awssdk.core.sync.ResponseTransformer
 *  software.amazon.awssdk.regions.ServiceMetadata
 */
package software.amazon.awssdk.services.s3;

import java.nio.file.Path;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.services.s3.DefaultS3ClientBuilder;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3ServiceClientConfiguration;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketAnalyticsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketAnalyticsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketCorsResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketEncryptionResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketIntelligentTieringConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketIntelligentTieringConfigurationResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketInventoryConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketInventoryConfigurationResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketLifecycleRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketLifecycleResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketMetricsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketMetricsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketOwnershipControlsRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketOwnershipControlsResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketPolicyResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketReplicationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketReplicationResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketWebsiteRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketWebsiteResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.DeletePublicAccessBlockRequest;
import software.amazon.awssdk.services.s3.model.DeletePublicAccessBlockResponse;
import software.amazon.awssdk.services.s3.model.GetBucketAccelerateConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAccelerateConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketAclRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAclResponse;
import software.amazon.awssdk.services.s3.model.GetBucketAnalyticsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAnalyticsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.GetBucketCorsResponse;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionResponse;
import software.amazon.awssdk.services.s3.model.GetBucketIntelligentTieringConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketIntelligentTieringConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketInventoryConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketInventoryConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketLocationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLocationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketLoggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLoggingResponse;
import software.amazon.awssdk.services.s3.model.GetBucketMetricsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketMetricsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketNotificationConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketNotificationConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketOwnershipControlsRequest;
import software.amazon.awssdk.services.s3.model.GetBucketOwnershipControlsResponse;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyResponse;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyStatusRequest;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyStatusResponse;
import software.amazon.awssdk.services.s3.model.GetBucketReplicationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketReplicationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketRequestPaymentRequest;
import software.amazon.awssdk.services.s3.model.GetBucketRequestPaymentResponse;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.GetBucketVersioningRequest;
import software.amazon.awssdk.services.s3.model.GetBucketVersioningResponse;
import software.amazon.awssdk.services.s3.model.GetBucketWebsiteRequest;
import software.amazon.awssdk.services.s3.model.GetBucketWebsiteResponse;
import software.amazon.awssdk.services.s3.model.GetObjectAclRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAclResponse;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.s3.model.GetObjectLegalHoldRequest;
import software.amazon.awssdk.services.s3.model.GetObjectLegalHoldResponse;
import software.amazon.awssdk.services.s3.model.GetObjectLockConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetObjectLockConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRetentionRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRetentionResponse;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.GetObjectTorrentRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTorrentResponse;
import software.amazon.awssdk.services.s3.model.GetPublicAccessBlockRequest;
import software.amazon.awssdk.services.s3.model.GetPublicAccessBlockResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.InvalidObjectStateException;
import software.amazon.awssdk.services.s3.model.ListBucketAnalyticsConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketAnalyticsConfigurationsResponse;
import software.amazon.awssdk.services.s3.model.ListBucketIntelligentTieringConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketIntelligentTieringConfigurationsResponse;
import software.amazon.awssdk.services.s3.model.ListBucketInventoryConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketInventoryConfigurationsResponse;
import software.amazon.awssdk.services.s3.model.ListBucketMetricsConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketMetricsConfigurationsResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsRequest;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ListPartsRequest;
import software.amazon.awssdk.services.s3.model.ListPartsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.NoSuchUploadException;
import software.amazon.awssdk.services.s3.model.ObjectAlreadyInActiveTierErrorException;
import software.amazon.awssdk.services.s3.model.ObjectNotInActiveTierErrorException;
import software.amazon.awssdk.services.s3.model.PutBucketAccelerateConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketAccelerateConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketAclRequest;
import software.amazon.awssdk.services.s3.model.PutBucketAclResponse;
import software.amazon.awssdk.services.s3.model.PutBucketAnalyticsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketAnalyticsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.PutBucketCorsResponse;
import software.amazon.awssdk.services.s3.model.PutBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.PutBucketEncryptionResponse;
import software.amazon.awssdk.services.s3.model.PutBucketIntelligentTieringConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketIntelligentTieringConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketInventoryConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketInventoryConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketLoggingRequest;
import software.amazon.awssdk.services.s3.model.PutBucketLoggingResponse;
import software.amazon.awssdk.services.s3.model.PutBucketMetricsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketMetricsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketOwnershipControlsRequest;
import software.amazon.awssdk.services.s3.model.PutBucketOwnershipControlsResponse;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyResponse;
import software.amazon.awssdk.services.s3.model.PutBucketReplicationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketReplicationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketRequestPaymentRequest;
import software.amazon.awssdk.services.s3.model.PutBucketRequestPaymentResponse;
import software.amazon.awssdk.services.s3.model.PutBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.PutBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.PutBucketVersioningRequest;
import software.amazon.awssdk.services.s3.model.PutBucketVersioningResponse;
import software.amazon.awssdk.services.s3.model.PutBucketWebsiteRequest;
import software.amazon.awssdk.services.s3.model.PutBucketWebsiteResponse;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectAclResponse;
import software.amazon.awssdk.services.s3.model.PutObjectLegalHoldRequest;
import software.amazon.awssdk.services.s3.model.PutObjectLegalHoldResponse;
import software.amazon.awssdk.services.s3.model.PutObjectLockConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutObjectLockConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRetentionRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRetentionResponse;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.PutPublicAccessBlockRequest;
import software.amazon.awssdk.services.s3.model.PutPublicAccessBlockResponse;
import software.amazon.awssdk.services.s3.model.RestoreObjectRequest;
import software.amazon.awssdk.services.s3.model.RestoreObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;
import software.amazon.awssdk.services.s3.model.UploadPartCopyResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.model.WriteGetObjectResponseRequest;
import software.amazon.awssdk.services.s3.model.WriteGetObjectResponseResponse;
import software.amazon.awssdk.services.s3.paginators.ListMultipartUploadsIterable;
import software.amazon.awssdk.services.s3.paginators.ListObjectVersionsIterable;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.services.s3.paginators.ListPartsIterable;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

@SdkPublicApi
@ThreadSafe
public interface S3Client
extends AwsClient {
    public static final String SERVICE_NAME = "s3";
    public static final String SERVICE_METADATA_ID = "s3";

    default public AbortMultipartUploadResponse abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest) throws NoSuchUploadException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public AbortMultipartUploadResponse abortMultipartUpload(Consumer<AbortMultipartUploadRequest.Builder> abortMultipartUploadRequest) throws NoSuchUploadException, AwsServiceException, SdkClientException, S3Exception {
        return this.abortMultipartUpload((AbortMultipartUploadRequest)((Object)((AbortMultipartUploadRequest.Builder)AbortMultipartUploadRequest.builder().applyMutation(abortMultipartUploadRequest)).build()));
    }

    default public CompleteMultipartUploadResponse completeMultipartUpload(CompleteMultipartUploadRequest completeMultipartUploadRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public CompleteMultipartUploadResponse completeMultipartUpload(Consumer<CompleteMultipartUploadRequest.Builder> completeMultipartUploadRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.completeMultipartUpload((CompleteMultipartUploadRequest)((Object)((CompleteMultipartUploadRequest.Builder)CompleteMultipartUploadRequest.builder().applyMutation(completeMultipartUploadRequest)).build()));
    }

    default public CopyObjectResponse copyObject(CopyObjectRequest copyObjectRequest) throws ObjectNotInActiveTierErrorException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public CopyObjectResponse copyObject(Consumer<CopyObjectRequest.Builder> copyObjectRequest) throws ObjectNotInActiveTierErrorException, AwsServiceException, SdkClientException, S3Exception {
        return this.copyObject((CopyObjectRequest)((Object)((CopyObjectRequest.Builder)CopyObjectRequest.builder().applyMutation(copyObjectRequest)).build()));
    }

    default public CreateBucketResponse createBucket(CreateBucketRequest createBucketRequest) throws BucketAlreadyExistsException, BucketAlreadyOwnedByYouException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public CreateBucketResponse createBucket(Consumer<CreateBucketRequest.Builder> createBucketRequest) throws BucketAlreadyExistsException, BucketAlreadyOwnedByYouException, AwsServiceException, SdkClientException, S3Exception {
        return this.createBucket((CreateBucketRequest)((Object)((CreateBucketRequest.Builder)CreateBucketRequest.builder().applyMutation(createBucketRequest)).build()));
    }

    default public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest createMultipartUploadRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public CreateMultipartUploadResponse createMultipartUpload(Consumer<CreateMultipartUploadRequest.Builder> createMultipartUploadRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.createMultipartUpload((CreateMultipartUploadRequest)((Object)((CreateMultipartUploadRequest.Builder)CreateMultipartUploadRequest.builder().applyMutation(createMultipartUploadRequest)).build()));
    }

    default public DeleteBucketResponse deleteBucket(DeleteBucketRequest deleteBucketRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketResponse deleteBucket(Consumer<DeleteBucketRequest.Builder> deleteBucketRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucket((DeleteBucketRequest)((Object)((DeleteBucketRequest.Builder)DeleteBucketRequest.builder().applyMutation(deleteBucketRequest)).build()));
    }

    default public DeleteBucketAnalyticsConfigurationResponse deleteBucketAnalyticsConfiguration(DeleteBucketAnalyticsConfigurationRequest deleteBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketAnalyticsConfigurationResponse deleteBucketAnalyticsConfiguration(Consumer<DeleteBucketAnalyticsConfigurationRequest.Builder> deleteBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketAnalyticsConfiguration((DeleteBucketAnalyticsConfigurationRequest)((Object)((DeleteBucketAnalyticsConfigurationRequest.Builder)DeleteBucketAnalyticsConfigurationRequest.builder().applyMutation(deleteBucketAnalyticsConfigurationRequest)).build()));
    }

    default public DeleteBucketCorsResponse deleteBucketCors(DeleteBucketCorsRequest deleteBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketCorsResponse deleteBucketCors(Consumer<DeleteBucketCorsRequest.Builder> deleteBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketCors((DeleteBucketCorsRequest)((Object)((DeleteBucketCorsRequest.Builder)DeleteBucketCorsRequest.builder().applyMutation(deleteBucketCorsRequest)).build()));
    }

    default public DeleteBucketEncryptionResponse deleteBucketEncryption(DeleteBucketEncryptionRequest deleteBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketEncryptionResponse deleteBucketEncryption(Consumer<DeleteBucketEncryptionRequest.Builder> deleteBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketEncryption((DeleteBucketEncryptionRequest)((Object)((DeleteBucketEncryptionRequest.Builder)DeleteBucketEncryptionRequest.builder().applyMutation(deleteBucketEncryptionRequest)).build()));
    }

    default public DeleteBucketIntelligentTieringConfigurationResponse deleteBucketIntelligentTieringConfiguration(DeleteBucketIntelligentTieringConfigurationRequest deleteBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketIntelligentTieringConfigurationResponse deleteBucketIntelligentTieringConfiguration(Consumer<DeleteBucketIntelligentTieringConfigurationRequest.Builder> deleteBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketIntelligentTieringConfiguration((DeleteBucketIntelligentTieringConfigurationRequest)((Object)((DeleteBucketIntelligentTieringConfigurationRequest.Builder)DeleteBucketIntelligentTieringConfigurationRequest.builder().applyMutation(deleteBucketIntelligentTieringConfigurationRequest)).build()));
    }

    default public DeleteBucketInventoryConfigurationResponse deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest deleteBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketInventoryConfigurationResponse deleteBucketInventoryConfiguration(Consumer<DeleteBucketInventoryConfigurationRequest.Builder> deleteBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketInventoryConfiguration((DeleteBucketInventoryConfigurationRequest)((Object)((DeleteBucketInventoryConfigurationRequest.Builder)DeleteBucketInventoryConfigurationRequest.builder().applyMutation(deleteBucketInventoryConfigurationRequest)).build()));
    }

    default public DeleteBucketLifecycleResponse deleteBucketLifecycle(DeleteBucketLifecycleRequest deleteBucketLifecycleRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketLifecycleResponse deleteBucketLifecycle(Consumer<DeleteBucketLifecycleRequest.Builder> deleteBucketLifecycleRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketLifecycle((DeleteBucketLifecycleRequest)((Object)((DeleteBucketLifecycleRequest.Builder)DeleteBucketLifecycleRequest.builder().applyMutation(deleteBucketLifecycleRequest)).build()));
    }

    default public DeleteBucketMetricsConfigurationResponse deleteBucketMetricsConfiguration(DeleteBucketMetricsConfigurationRequest deleteBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketMetricsConfigurationResponse deleteBucketMetricsConfiguration(Consumer<DeleteBucketMetricsConfigurationRequest.Builder> deleteBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketMetricsConfiguration((DeleteBucketMetricsConfigurationRequest)((Object)((DeleteBucketMetricsConfigurationRequest.Builder)DeleteBucketMetricsConfigurationRequest.builder().applyMutation(deleteBucketMetricsConfigurationRequest)).build()));
    }

    default public DeleteBucketOwnershipControlsResponse deleteBucketOwnershipControls(DeleteBucketOwnershipControlsRequest deleteBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketOwnershipControlsResponse deleteBucketOwnershipControls(Consumer<DeleteBucketOwnershipControlsRequest.Builder> deleteBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketOwnershipControls((DeleteBucketOwnershipControlsRequest)((Object)((DeleteBucketOwnershipControlsRequest.Builder)DeleteBucketOwnershipControlsRequest.builder().applyMutation(deleteBucketOwnershipControlsRequest)).build()));
    }

    default public DeleteBucketPolicyResponse deleteBucketPolicy(DeleteBucketPolicyRequest deleteBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketPolicyResponse deleteBucketPolicy(Consumer<DeleteBucketPolicyRequest.Builder> deleteBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketPolicy((DeleteBucketPolicyRequest)((Object)((DeleteBucketPolicyRequest.Builder)DeleteBucketPolicyRequest.builder().applyMutation(deleteBucketPolicyRequest)).build()));
    }

    default public DeleteBucketReplicationResponse deleteBucketReplication(DeleteBucketReplicationRequest deleteBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketReplicationResponse deleteBucketReplication(Consumer<DeleteBucketReplicationRequest.Builder> deleteBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketReplication((DeleteBucketReplicationRequest)((Object)((DeleteBucketReplicationRequest.Builder)DeleteBucketReplicationRequest.builder().applyMutation(deleteBucketReplicationRequest)).build()));
    }

    default public DeleteBucketTaggingResponse deleteBucketTagging(DeleteBucketTaggingRequest deleteBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketTaggingResponse deleteBucketTagging(Consumer<DeleteBucketTaggingRequest.Builder> deleteBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketTagging((DeleteBucketTaggingRequest)((Object)((DeleteBucketTaggingRequest.Builder)DeleteBucketTaggingRequest.builder().applyMutation(deleteBucketTaggingRequest)).build()));
    }

    default public DeleteBucketWebsiteResponse deleteBucketWebsite(DeleteBucketWebsiteRequest deleteBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteBucketWebsiteResponse deleteBucketWebsite(Consumer<DeleteBucketWebsiteRequest.Builder> deleteBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteBucketWebsite((DeleteBucketWebsiteRequest)((Object)((DeleteBucketWebsiteRequest.Builder)DeleteBucketWebsiteRequest.builder().applyMutation(deleteBucketWebsiteRequest)).build()));
    }

    default public DeleteObjectResponse deleteObject(DeleteObjectRequest deleteObjectRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteObjectResponse deleteObject(Consumer<DeleteObjectRequest.Builder> deleteObjectRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteObject((DeleteObjectRequest)((Object)((DeleteObjectRequest.Builder)DeleteObjectRequest.builder().applyMutation(deleteObjectRequest)).build()));
    }

    default public DeleteObjectTaggingResponse deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteObjectTaggingResponse deleteObjectTagging(Consumer<DeleteObjectTaggingRequest.Builder> deleteObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteObjectTagging((DeleteObjectTaggingRequest)((Object)((DeleteObjectTaggingRequest.Builder)DeleteObjectTaggingRequest.builder().applyMutation(deleteObjectTaggingRequest)).build()));
    }

    default public DeleteObjectsResponse deleteObjects(DeleteObjectsRequest deleteObjectsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeleteObjectsResponse deleteObjects(Consumer<DeleteObjectsRequest.Builder> deleteObjectsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deleteObjects((DeleteObjectsRequest)((Object)((DeleteObjectsRequest.Builder)DeleteObjectsRequest.builder().applyMutation(deleteObjectsRequest)).build()));
    }

    default public DeletePublicAccessBlockResponse deletePublicAccessBlock(DeletePublicAccessBlockRequest deletePublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public DeletePublicAccessBlockResponse deletePublicAccessBlock(Consumer<DeletePublicAccessBlockRequest.Builder> deletePublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.deletePublicAccessBlock((DeletePublicAccessBlockRequest)((Object)((DeletePublicAccessBlockRequest.Builder)DeletePublicAccessBlockRequest.builder().applyMutation(deletePublicAccessBlockRequest)).build()));
    }

    default public GetBucketAccelerateConfigurationResponse getBucketAccelerateConfiguration(GetBucketAccelerateConfigurationRequest getBucketAccelerateConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketAccelerateConfigurationResponse getBucketAccelerateConfiguration(Consumer<GetBucketAccelerateConfigurationRequest.Builder> getBucketAccelerateConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketAccelerateConfiguration((GetBucketAccelerateConfigurationRequest)((Object)((GetBucketAccelerateConfigurationRequest.Builder)GetBucketAccelerateConfigurationRequest.builder().applyMutation(getBucketAccelerateConfigurationRequest)).build()));
    }

    default public GetBucketAclResponse getBucketAcl(GetBucketAclRequest getBucketAclRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketAclResponse getBucketAcl(Consumer<GetBucketAclRequest.Builder> getBucketAclRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketAcl((GetBucketAclRequest)((Object)((GetBucketAclRequest.Builder)GetBucketAclRequest.builder().applyMutation(getBucketAclRequest)).build()));
    }

    default public GetBucketAnalyticsConfigurationResponse getBucketAnalyticsConfiguration(GetBucketAnalyticsConfigurationRequest getBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketAnalyticsConfigurationResponse getBucketAnalyticsConfiguration(Consumer<GetBucketAnalyticsConfigurationRequest.Builder> getBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketAnalyticsConfiguration((GetBucketAnalyticsConfigurationRequest)((Object)((GetBucketAnalyticsConfigurationRequest.Builder)GetBucketAnalyticsConfigurationRequest.builder().applyMutation(getBucketAnalyticsConfigurationRequest)).build()));
    }

    default public GetBucketCorsResponse getBucketCors(GetBucketCorsRequest getBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketCorsResponse getBucketCors(Consumer<GetBucketCorsRequest.Builder> getBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketCors((GetBucketCorsRequest)((Object)((GetBucketCorsRequest.Builder)GetBucketCorsRequest.builder().applyMutation(getBucketCorsRequest)).build()));
    }

    default public GetBucketEncryptionResponse getBucketEncryption(GetBucketEncryptionRequest getBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketEncryptionResponse getBucketEncryption(Consumer<GetBucketEncryptionRequest.Builder> getBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketEncryption((GetBucketEncryptionRequest)((Object)((GetBucketEncryptionRequest.Builder)GetBucketEncryptionRequest.builder().applyMutation(getBucketEncryptionRequest)).build()));
    }

    default public GetBucketIntelligentTieringConfigurationResponse getBucketIntelligentTieringConfiguration(GetBucketIntelligentTieringConfigurationRequest getBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketIntelligentTieringConfigurationResponse getBucketIntelligentTieringConfiguration(Consumer<GetBucketIntelligentTieringConfigurationRequest.Builder> getBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketIntelligentTieringConfiguration((GetBucketIntelligentTieringConfigurationRequest)((Object)((GetBucketIntelligentTieringConfigurationRequest.Builder)GetBucketIntelligentTieringConfigurationRequest.builder().applyMutation(getBucketIntelligentTieringConfigurationRequest)).build()));
    }

    default public GetBucketInventoryConfigurationResponse getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest getBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketInventoryConfigurationResponse getBucketInventoryConfiguration(Consumer<GetBucketInventoryConfigurationRequest.Builder> getBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketInventoryConfiguration((GetBucketInventoryConfigurationRequest)((Object)((GetBucketInventoryConfigurationRequest.Builder)GetBucketInventoryConfigurationRequest.builder().applyMutation(getBucketInventoryConfigurationRequest)).build()));
    }

    default public GetBucketLifecycleConfigurationResponse getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketLifecycleConfigurationResponse getBucketLifecycleConfiguration(Consumer<GetBucketLifecycleConfigurationRequest.Builder> getBucketLifecycleConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketLifecycleConfiguration((GetBucketLifecycleConfigurationRequest)((Object)((GetBucketLifecycleConfigurationRequest.Builder)GetBucketLifecycleConfigurationRequest.builder().applyMutation(getBucketLifecycleConfigurationRequest)).build()));
    }

    default public GetBucketLocationResponse getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketLocationResponse getBucketLocation(Consumer<GetBucketLocationRequest.Builder> getBucketLocationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketLocation((GetBucketLocationRequest)((Object)((GetBucketLocationRequest.Builder)GetBucketLocationRequest.builder().applyMutation(getBucketLocationRequest)).build()));
    }

    default public GetBucketLoggingResponse getBucketLogging(GetBucketLoggingRequest getBucketLoggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketLoggingResponse getBucketLogging(Consumer<GetBucketLoggingRequest.Builder> getBucketLoggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketLogging((GetBucketLoggingRequest)((Object)((GetBucketLoggingRequest.Builder)GetBucketLoggingRequest.builder().applyMutation(getBucketLoggingRequest)).build()));
    }

    default public GetBucketMetricsConfigurationResponse getBucketMetricsConfiguration(GetBucketMetricsConfigurationRequest getBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketMetricsConfigurationResponse getBucketMetricsConfiguration(Consumer<GetBucketMetricsConfigurationRequest.Builder> getBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketMetricsConfiguration((GetBucketMetricsConfigurationRequest)((Object)((GetBucketMetricsConfigurationRequest.Builder)GetBucketMetricsConfigurationRequest.builder().applyMutation(getBucketMetricsConfigurationRequest)).build()));
    }

    default public GetBucketNotificationConfigurationResponse getBucketNotificationConfiguration(GetBucketNotificationConfigurationRequest getBucketNotificationConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketNotificationConfigurationResponse getBucketNotificationConfiguration(Consumer<GetBucketNotificationConfigurationRequest.Builder> getBucketNotificationConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketNotificationConfiguration((GetBucketNotificationConfigurationRequest)((Object)((GetBucketNotificationConfigurationRequest.Builder)GetBucketNotificationConfigurationRequest.builder().applyMutation(getBucketNotificationConfigurationRequest)).build()));
    }

    default public GetBucketOwnershipControlsResponse getBucketOwnershipControls(GetBucketOwnershipControlsRequest getBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketOwnershipControlsResponse getBucketOwnershipControls(Consumer<GetBucketOwnershipControlsRequest.Builder> getBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketOwnershipControls((GetBucketOwnershipControlsRequest)((Object)((GetBucketOwnershipControlsRequest.Builder)GetBucketOwnershipControlsRequest.builder().applyMutation(getBucketOwnershipControlsRequest)).build()));
    }

    default public GetBucketPolicyResponse getBucketPolicy(GetBucketPolicyRequest getBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketPolicyResponse getBucketPolicy(Consumer<GetBucketPolicyRequest.Builder> getBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketPolicy((GetBucketPolicyRequest)((Object)((GetBucketPolicyRequest.Builder)GetBucketPolicyRequest.builder().applyMutation(getBucketPolicyRequest)).build()));
    }

    default public GetBucketPolicyStatusResponse getBucketPolicyStatus(GetBucketPolicyStatusRequest getBucketPolicyStatusRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketPolicyStatusResponse getBucketPolicyStatus(Consumer<GetBucketPolicyStatusRequest.Builder> getBucketPolicyStatusRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketPolicyStatus((GetBucketPolicyStatusRequest)((Object)((GetBucketPolicyStatusRequest.Builder)GetBucketPolicyStatusRequest.builder().applyMutation(getBucketPolicyStatusRequest)).build()));
    }

    default public GetBucketReplicationResponse getBucketReplication(GetBucketReplicationRequest getBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketReplicationResponse getBucketReplication(Consumer<GetBucketReplicationRequest.Builder> getBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketReplication((GetBucketReplicationRequest)((Object)((GetBucketReplicationRequest.Builder)GetBucketReplicationRequest.builder().applyMutation(getBucketReplicationRequest)).build()));
    }

    default public GetBucketRequestPaymentResponse getBucketRequestPayment(GetBucketRequestPaymentRequest getBucketRequestPaymentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketRequestPaymentResponse getBucketRequestPayment(Consumer<GetBucketRequestPaymentRequest.Builder> getBucketRequestPaymentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketRequestPayment((GetBucketRequestPaymentRequest)((Object)((GetBucketRequestPaymentRequest.Builder)GetBucketRequestPaymentRequest.builder().applyMutation(getBucketRequestPaymentRequest)).build()));
    }

    default public GetBucketTaggingResponse getBucketTagging(GetBucketTaggingRequest getBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketTaggingResponse getBucketTagging(Consumer<GetBucketTaggingRequest.Builder> getBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketTagging((GetBucketTaggingRequest)((Object)((GetBucketTaggingRequest.Builder)GetBucketTaggingRequest.builder().applyMutation(getBucketTaggingRequest)).build()));
    }

    default public GetBucketVersioningResponse getBucketVersioning(GetBucketVersioningRequest getBucketVersioningRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketVersioningResponse getBucketVersioning(Consumer<GetBucketVersioningRequest.Builder> getBucketVersioningRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketVersioning((GetBucketVersioningRequest)((Object)((GetBucketVersioningRequest.Builder)GetBucketVersioningRequest.builder().applyMutation(getBucketVersioningRequest)).build()));
    }

    default public GetBucketWebsiteResponse getBucketWebsite(GetBucketWebsiteRequest getBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetBucketWebsiteResponse getBucketWebsite(Consumer<GetBucketWebsiteRequest.Builder> getBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getBucketWebsite((GetBucketWebsiteRequest)((Object)((GetBucketWebsiteRequest.Builder)GetBucketWebsiteRequest.builder().applyMutation(getBucketWebsiteRequest)).build()));
    }

    default public <ReturnT> ReturnT getObject(GetObjectRequest getObjectRequest, ResponseTransformer<GetObjectResponse, ReturnT> responseTransformer) throws NoSuchKeyException, InvalidObjectStateException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public <ReturnT> ReturnT getObject(Consumer<GetObjectRequest.Builder> getObjectRequest, ResponseTransformer<GetObjectResponse, ReturnT> responseTransformer) throws NoSuchKeyException, InvalidObjectStateException, AwsServiceException, SdkClientException, S3Exception {
        return this.getObject((GetObjectRequest)((Object)((GetObjectRequest.Builder)GetObjectRequest.builder().applyMutation(getObjectRequest)).build()), responseTransformer);
    }

    default public GetObjectResponse getObject(GetObjectRequest getObjectRequest, Path destinationPath) throws NoSuchKeyException, InvalidObjectStateException, AwsServiceException, SdkClientException, S3Exception {
        return (GetObjectResponse)((Object)this.getObject(getObjectRequest, ResponseTransformer.toFile((Path)destinationPath)));
    }

    default public GetObjectResponse getObject(Consumer<GetObjectRequest.Builder> getObjectRequest, Path destinationPath) throws NoSuchKeyException, InvalidObjectStateException, AwsServiceException, SdkClientException, S3Exception {
        return this.getObject((GetObjectRequest)((Object)((GetObjectRequest.Builder)GetObjectRequest.builder().applyMutation(getObjectRequest)).build()), destinationPath);
    }

    default public ResponseInputStream<GetObjectResponse> getObject(GetObjectRequest getObjectRequest) throws NoSuchKeyException, InvalidObjectStateException, AwsServiceException, SdkClientException, S3Exception {
        return (ResponseInputStream)this.getObject(getObjectRequest, ResponseTransformer.toInputStream());
    }

    default public ResponseInputStream<GetObjectResponse> getObject(Consumer<GetObjectRequest.Builder> getObjectRequest) throws NoSuchKeyException, InvalidObjectStateException, AwsServiceException, SdkClientException, S3Exception {
        return this.getObject((GetObjectRequest)((Object)((GetObjectRequest.Builder)GetObjectRequest.builder().applyMutation(getObjectRequest)).build()));
    }

    default public ResponseBytes<GetObjectResponse> getObjectAsBytes(GetObjectRequest getObjectRequest) throws NoSuchKeyException, InvalidObjectStateException, AwsServiceException, SdkClientException, S3Exception {
        return (ResponseBytes)this.getObject(getObjectRequest, ResponseTransformer.toBytes());
    }

    default public ResponseBytes<GetObjectResponse> getObjectAsBytes(Consumer<GetObjectRequest.Builder> getObjectRequest) throws NoSuchKeyException, InvalidObjectStateException, AwsServiceException, SdkClientException, S3Exception {
        return this.getObjectAsBytes((GetObjectRequest)((Object)((GetObjectRequest.Builder)GetObjectRequest.builder().applyMutation(getObjectRequest)).build()));
    }

    default public GetObjectAclResponse getObjectAcl(GetObjectAclRequest getObjectAclRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetObjectAclResponse getObjectAcl(Consumer<GetObjectAclRequest.Builder> getObjectAclRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        return this.getObjectAcl((GetObjectAclRequest)((Object)((GetObjectAclRequest.Builder)GetObjectAclRequest.builder().applyMutation(getObjectAclRequest)).build()));
    }

    default public GetObjectAttributesResponse getObjectAttributes(GetObjectAttributesRequest getObjectAttributesRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetObjectAttributesResponse getObjectAttributes(Consumer<GetObjectAttributesRequest.Builder> getObjectAttributesRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        return this.getObjectAttributes((GetObjectAttributesRequest)((Object)((GetObjectAttributesRequest.Builder)GetObjectAttributesRequest.builder().applyMutation(getObjectAttributesRequest)).build()));
    }

    default public GetObjectLegalHoldResponse getObjectLegalHold(GetObjectLegalHoldRequest getObjectLegalHoldRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetObjectLegalHoldResponse getObjectLegalHold(Consumer<GetObjectLegalHoldRequest.Builder> getObjectLegalHoldRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getObjectLegalHold((GetObjectLegalHoldRequest)((Object)((GetObjectLegalHoldRequest.Builder)GetObjectLegalHoldRequest.builder().applyMutation(getObjectLegalHoldRequest)).build()));
    }

    default public GetObjectLockConfigurationResponse getObjectLockConfiguration(GetObjectLockConfigurationRequest getObjectLockConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetObjectLockConfigurationResponse getObjectLockConfiguration(Consumer<GetObjectLockConfigurationRequest.Builder> getObjectLockConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getObjectLockConfiguration((GetObjectLockConfigurationRequest)((Object)((GetObjectLockConfigurationRequest.Builder)GetObjectLockConfigurationRequest.builder().applyMutation(getObjectLockConfigurationRequest)).build()));
    }

    default public GetObjectRetentionResponse getObjectRetention(GetObjectRetentionRequest getObjectRetentionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetObjectRetentionResponse getObjectRetention(Consumer<GetObjectRetentionRequest.Builder> getObjectRetentionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getObjectRetention((GetObjectRetentionRequest)((Object)((GetObjectRetentionRequest.Builder)GetObjectRetentionRequest.builder().applyMutation(getObjectRetentionRequest)).build()));
    }

    default public GetObjectTaggingResponse getObjectTagging(GetObjectTaggingRequest getObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetObjectTaggingResponse getObjectTagging(Consumer<GetObjectTaggingRequest.Builder> getObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getObjectTagging((GetObjectTaggingRequest)((Object)((GetObjectTaggingRequest.Builder)GetObjectTaggingRequest.builder().applyMutation(getObjectTaggingRequest)).build()));
    }

    default public <ReturnT> ReturnT getObjectTorrent(GetObjectTorrentRequest getObjectTorrentRequest, ResponseTransformer<GetObjectTorrentResponse, ReturnT> responseTransformer) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public <ReturnT> ReturnT getObjectTorrent(Consumer<GetObjectTorrentRequest.Builder> getObjectTorrentRequest, ResponseTransformer<GetObjectTorrentResponse, ReturnT> responseTransformer) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getObjectTorrent((GetObjectTorrentRequest)((Object)((GetObjectTorrentRequest.Builder)GetObjectTorrentRequest.builder().applyMutation(getObjectTorrentRequest)).build()), responseTransformer);
    }

    default public GetObjectTorrentResponse getObjectTorrent(GetObjectTorrentRequest getObjectTorrentRequest, Path destinationPath) throws AwsServiceException, SdkClientException, S3Exception {
        return (GetObjectTorrentResponse)((Object)this.getObjectTorrent(getObjectTorrentRequest, ResponseTransformer.toFile((Path)destinationPath)));
    }

    default public GetObjectTorrentResponse getObjectTorrent(Consumer<GetObjectTorrentRequest.Builder> getObjectTorrentRequest, Path destinationPath) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getObjectTorrent((GetObjectTorrentRequest)((Object)((GetObjectTorrentRequest.Builder)GetObjectTorrentRequest.builder().applyMutation(getObjectTorrentRequest)).build()), destinationPath);
    }

    default public ResponseInputStream<GetObjectTorrentResponse> getObjectTorrent(GetObjectTorrentRequest getObjectTorrentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return (ResponseInputStream)this.getObjectTorrent(getObjectTorrentRequest, ResponseTransformer.toInputStream());
    }

    default public ResponseInputStream<GetObjectTorrentResponse> getObjectTorrent(Consumer<GetObjectTorrentRequest.Builder> getObjectTorrentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getObjectTorrent((GetObjectTorrentRequest)((Object)((GetObjectTorrentRequest.Builder)GetObjectTorrentRequest.builder().applyMutation(getObjectTorrentRequest)).build()));
    }

    default public ResponseBytes<GetObjectTorrentResponse> getObjectTorrentAsBytes(GetObjectTorrentRequest getObjectTorrentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return (ResponseBytes)this.getObjectTorrent(getObjectTorrentRequest, ResponseTransformer.toBytes());
    }

    default public ResponseBytes<GetObjectTorrentResponse> getObjectTorrentAsBytes(Consumer<GetObjectTorrentRequest.Builder> getObjectTorrentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getObjectTorrentAsBytes((GetObjectTorrentRequest)((Object)((GetObjectTorrentRequest.Builder)GetObjectTorrentRequest.builder().applyMutation(getObjectTorrentRequest)).build()));
    }

    default public GetPublicAccessBlockResponse getPublicAccessBlock(GetPublicAccessBlockRequest getPublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public GetPublicAccessBlockResponse getPublicAccessBlock(Consumer<GetPublicAccessBlockRequest.Builder> getPublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.getPublicAccessBlock((GetPublicAccessBlockRequest)((Object)((GetPublicAccessBlockRequest.Builder)GetPublicAccessBlockRequest.builder().applyMutation(getPublicAccessBlockRequest)).build()));
    }

    default public HeadBucketResponse headBucket(HeadBucketRequest headBucketRequest) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public HeadBucketResponse headBucket(Consumer<HeadBucketRequest.Builder> headBucketRequest) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        return this.headBucket((HeadBucketRequest)((Object)((HeadBucketRequest.Builder)HeadBucketRequest.builder().applyMutation(headBucketRequest)).build()));
    }

    default public HeadObjectResponse headObject(HeadObjectRequest headObjectRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public HeadObjectResponse headObject(Consumer<HeadObjectRequest.Builder> headObjectRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        return this.headObject((HeadObjectRequest)((Object)((HeadObjectRequest.Builder)HeadObjectRequest.builder().applyMutation(headObjectRequest)).build()));
    }

    default public ListBucketAnalyticsConfigurationsResponse listBucketAnalyticsConfigurations(ListBucketAnalyticsConfigurationsRequest listBucketAnalyticsConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public ListBucketAnalyticsConfigurationsResponse listBucketAnalyticsConfigurations(Consumer<ListBucketAnalyticsConfigurationsRequest.Builder> listBucketAnalyticsConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.listBucketAnalyticsConfigurations((ListBucketAnalyticsConfigurationsRequest)((Object)((ListBucketAnalyticsConfigurationsRequest.Builder)ListBucketAnalyticsConfigurationsRequest.builder().applyMutation(listBucketAnalyticsConfigurationsRequest)).build()));
    }

    default public ListBucketIntelligentTieringConfigurationsResponse listBucketIntelligentTieringConfigurations(ListBucketIntelligentTieringConfigurationsRequest listBucketIntelligentTieringConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public ListBucketIntelligentTieringConfigurationsResponse listBucketIntelligentTieringConfigurations(Consumer<ListBucketIntelligentTieringConfigurationsRequest.Builder> listBucketIntelligentTieringConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.listBucketIntelligentTieringConfigurations((ListBucketIntelligentTieringConfigurationsRequest)((Object)((ListBucketIntelligentTieringConfigurationsRequest.Builder)ListBucketIntelligentTieringConfigurationsRequest.builder().applyMutation(listBucketIntelligentTieringConfigurationsRequest)).build()));
    }

    default public ListBucketInventoryConfigurationsResponse listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest listBucketInventoryConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public ListBucketInventoryConfigurationsResponse listBucketInventoryConfigurations(Consumer<ListBucketInventoryConfigurationsRequest.Builder> listBucketInventoryConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.listBucketInventoryConfigurations((ListBucketInventoryConfigurationsRequest)((Object)((ListBucketInventoryConfigurationsRequest.Builder)ListBucketInventoryConfigurationsRequest.builder().applyMutation(listBucketInventoryConfigurationsRequest)).build()));
    }

    default public ListBucketMetricsConfigurationsResponse listBucketMetricsConfigurations(ListBucketMetricsConfigurationsRequest listBucketMetricsConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public ListBucketMetricsConfigurationsResponse listBucketMetricsConfigurations(Consumer<ListBucketMetricsConfigurationsRequest.Builder> listBucketMetricsConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.listBucketMetricsConfigurations((ListBucketMetricsConfigurationsRequest)((Object)((ListBucketMetricsConfigurationsRequest.Builder)ListBucketMetricsConfigurationsRequest.builder().applyMutation(listBucketMetricsConfigurationsRequest)).build()));
    }

    default public ListBucketsResponse listBuckets(ListBucketsRequest listBucketsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public ListBucketsResponse listBuckets(Consumer<ListBucketsRequest.Builder> listBucketsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.listBuckets((ListBucketsRequest)((Object)((ListBucketsRequest.Builder)ListBucketsRequest.builder().applyMutation(listBucketsRequest)).build()));
    }

    default public ListBucketsResponse listBuckets() throws AwsServiceException, SdkClientException, S3Exception {
        return this.listBuckets((ListBucketsRequest)((Object)ListBucketsRequest.builder().build()));
    }

    default public ListMultipartUploadsResponse listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public ListMultipartUploadsResponse listMultipartUploads(Consumer<ListMultipartUploadsRequest.Builder> listMultipartUploadsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.listMultipartUploads((ListMultipartUploadsRequest)((Object)((ListMultipartUploadsRequest.Builder)ListMultipartUploadsRequest.builder().applyMutation(listMultipartUploadsRequest)).build()));
    }

    default public ListMultipartUploadsIterable listMultipartUploadsPaginator(ListMultipartUploadsRequest listMultipartUploadsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return new ListMultipartUploadsIterable(this, listMultipartUploadsRequest);
    }

    default public ListMultipartUploadsIterable listMultipartUploadsPaginator(Consumer<ListMultipartUploadsRequest.Builder> listMultipartUploadsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.listMultipartUploadsPaginator((ListMultipartUploadsRequest)((Object)((ListMultipartUploadsRequest.Builder)ListMultipartUploadsRequest.builder().applyMutation(listMultipartUploadsRequest)).build()));
    }

    default public ListObjectVersionsResponse listObjectVersions(ListObjectVersionsRequest listObjectVersionsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public ListObjectVersionsResponse listObjectVersions(Consumer<ListObjectVersionsRequest.Builder> listObjectVersionsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.listObjectVersions((ListObjectVersionsRequest)((Object)((ListObjectVersionsRequest.Builder)ListObjectVersionsRequest.builder().applyMutation(listObjectVersionsRequest)).build()));
    }

    default public ListObjectVersionsIterable listObjectVersionsPaginator(ListObjectVersionsRequest listObjectVersionsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return new ListObjectVersionsIterable(this, listObjectVersionsRequest);
    }

    default public ListObjectVersionsIterable listObjectVersionsPaginator(Consumer<ListObjectVersionsRequest.Builder> listObjectVersionsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.listObjectVersionsPaginator((ListObjectVersionsRequest)((Object)((ListObjectVersionsRequest.Builder)ListObjectVersionsRequest.builder().applyMutation(listObjectVersionsRequest)).build()));
    }

    default public ListObjectsResponse listObjects(ListObjectsRequest listObjectsRequest) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public ListObjectsResponse listObjects(Consumer<ListObjectsRequest.Builder> listObjectsRequest) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        return this.listObjects((ListObjectsRequest)((Object)((ListObjectsRequest.Builder)ListObjectsRequest.builder().applyMutation(listObjectsRequest)).build()));
    }

    default public ListObjectsV2Response listObjectsV2(ListObjectsV2Request listObjectsV2Request) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public ListObjectsV2Response listObjectsV2(Consumer<ListObjectsV2Request.Builder> listObjectsV2Request) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        return this.listObjectsV2((ListObjectsV2Request)((Object)((ListObjectsV2Request.Builder)ListObjectsV2Request.builder().applyMutation(listObjectsV2Request)).build()));
    }

    default public ListObjectsV2Iterable listObjectsV2Paginator(ListObjectsV2Request listObjectsV2Request) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        return new ListObjectsV2Iterable(this, listObjectsV2Request);
    }

    default public ListObjectsV2Iterable listObjectsV2Paginator(Consumer<ListObjectsV2Request.Builder> listObjectsV2Request) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        return this.listObjectsV2Paginator((ListObjectsV2Request)((Object)((ListObjectsV2Request.Builder)ListObjectsV2Request.builder().applyMutation(listObjectsV2Request)).build()));
    }

    default public ListPartsResponse listParts(ListPartsRequest listPartsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public ListPartsResponse listParts(Consumer<ListPartsRequest.Builder> listPartsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.listParts((ListPartsRequest)((Object)((ListPartsRequest.Builder)ListPartsRequest.builder().applyMutation(listPartsRequest)).build()));
    }

    default public ListPartsIterable listPartsPaginator(ListPartsRequest listPartsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return new ListPartsIterable(this, listPartsRequest);
    }

    default public ListPartsIterable listPartsPaginator(Consumer<ListPartsRequest.Builder> listPartsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.listPartsPaginator((ListPartsRequest)((Object)((ListPartsRequest.Builder)ListPartsRequest.builder().applyMutation(listPartsRequest)).build()));
    }

    default public PutBucketAccelerateConfigurationResponse putBucketAccelerateConfiguration(PutBucketAccelerateConfigurationRequest putBucketAccelerateConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketAccelerateConfigurationResponse putBucketAccelerateConfiguration(Consumer<PutBucketAccelerateConfigurationRequest.Builder> putBucketAccelerateConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketAccelerateConfiguration((PutBucketAccelerateConfigurationRequest)((Object)((PutBucketAccelerateConfigurationRequest.Builder)PutBucketAccelerateConfigurationRequest.builder().applyMutation(putBucketAccelerateConfigurationRequest)).build()));
    }

    default public PutBucketAclResponse putBucketAcl(PutBucketAclRequest putBucketAclRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketAclResponse putBucketAcl(Consumer<PutBucketAclRequest.Builder> putBucketAclRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketAcl((PutBucketAclRequest)((Object)((PutBucketAclRequest.Builder)PutBucketAclRequest.builder().applyMutation(putBucketAclRequest)).build()));
    }

    default public PutBucketAnalyticsConfigurationResponse putBucketAnalyticsConfiguration(PutBucketAnalyticsConfigurationRequest putBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketAnalyticsConfigurationResponse putBucketAnalyticsConfiguration(Consumer<PutBucketAnalyticsConfigurationRequest.Builder> putBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketAnalyticsConfiguration((PutBucketAnalyticsConfigurationRequest)((Object)((PutBucketAnalyticsConfigurationRequest.Builder)PutBucketAnalyticsConfigurationRequest.builder().applyMutation(putBucketAnalyticsConfigurationRequest)).build()));
    }

    default public PutBucketCorsResponse putBucketCors(PutBucketCorsRequest putBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketCorsResponse putBucketCors(Consumer<PutBucketCorsRequest.Builder> putBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketCors((PutBucketCorsRequest)((Object)((PutBucketCorsRequest.Builder)PutBucketCorsRequest.builder().applyMutation(putBucketCorsRequest)).build()));
    }

    default public PutBucketEncryptionResponse putBucketEncryption(PutBucketEncryptionRequest putBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketEncryptionResponse putBucketEncryption(Consumer<PutBucketEncryptionRequest.Builder> putBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketEncryption((PutBucketEncryptionRequest)((Object)((PutBucketEncryptionRequest.Builder)PutBucketEncryptionRequest.builder().applyMutation(putBucketEncryptionRequest)).build()));
    }

    default public PutBucketIntelligentTieringConfigurationResponse putBucketIntelligentTieringConfiguration(PutBucketIntelligentTieringConfigurationRequest putBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketIntelligentTieringConfigurationResponse putBucketIntelligentTieringConfiguration(Consumer<PutBucketIntelligentTieringConfigurationRequest.Builder> putBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketIntelligentTieringConfiguration((PutBucketIntelligentTieringConfigurationRequest)((Object)((PutBucketIntelligentTieringConfigurationRequest.Builder)PutBucketIntelligentTieringConfigurationRequest.builder().applyMutation(putBucketIntelligentTieringConfigurationRequest)).build()));
    }

    default public PutBucketInventoryConfigurationResponse putBucketInventoryConfiguration(PutBucketInventoryConfigurationRequest putBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketInventoryConfigurationResponse putBucketInventoryConfiguration(Consumer<PutBucketInventoryConfigurationRequest.Builder> putBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketInventoryConfiguration((PutBucketInventoryConfigurationRequest)((Object)((PutBucketInventoryConfigurationRequest.Builder)PutBucketInventoryConfigurationRequest.builder().applyMutation(putBucketInventoryConfigurationRequest)).build()));
    }

    default public PutBucketLifecycleConfigurationResponse putBucketLifecycleConfiguration(PutBucketLifecycleConfigurationRequest putBucketLifecycleConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketLifecycleConfigurationResponse putBucketLifecycleConfiguration(Consumer<PutBucketLifecycleConfigurationRequest.Builder> putBucketLifecycleConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketLifecycleConfiguration((PutBucketLifecycleConfigurationRequest)((Object)((PutBucketLifecycleConfigurationRequest.Builder)PutBucketLifecycleConfigurationRequest.builder().applyMutation(putBucketLifecycleConfigurationRequest)).build()));
    }

    default public PutBucketLoggingResponse putBucketLogging(PutBucketLoggingRequest putBucketLoggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketLoggingResponse putBucketLogging(Consumer<PutBucketLoggingRequest.Builder> putBucketLoggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketLogging((PutBucketLoggingRequest)((Object)((PutBucketLoggingRequest.Builder)PutBucketLoggingRequest.builder().applyMutation(putBucketLoggingRequest)).build()));
    }

    default public PutBucketMetricsConfigurationResponse putBucketMetricsConfiguration(PutBucketMetricsConfigurationRequest putBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketMetricsConfigurationResponse putBucketMetricsConfiguration(Consumer<PutBucketMetricsConfigurationRequest.Builder> putBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketMetricsConfiguration((PutBucketMetricsConfigurationRequest)((Object)((PutBucketMetricsConfigurationRequest.Builder)PutBucketMetricsConfigurationRequest.builder().applyMutation(putBucketMetricsConfigurationRequest)).build()));
    }

    default public PutBucketNotificationConfigurationResponse putBucketNotificationConfiguration(PutBucketNotificationConfigurationRequest putBucketNotificationConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketNotificationConfigurationResponse putBucketNotificationConfiguration(Consumer<PutBucketNotificationConfigurationRequest.Builder> putBucketNotificationConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketNotificationConfiguration((PutBucketNotificationConfigurationRequest)((Object)((PutBucketNotificationConfigurationRequest.Builder)PutBucketNotificationConfigurationRequest.builder().applyMutation(putBucketNotificationConfigurationRequest)).build()));
    }

    default public PutBucketOwnershipControlsResponse putBucketOwnershipControls(PutBucketOwnershipControlsRequest putBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketOwnershipControlsResponse putBucketOwnershipControls(Consumer<PutBucketOwnershipControlsRequest.Builder> putBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketOwnershipControls((PutBucketOwnershipControlsRequest)((Object)((PutBucketOwnershipControlsRequest.Builder)PutBucketOwnershipControlsRequest.builder().applyMutation(putBucketOwnershipControlsRequest)).build()));
    }

    default public PutBucketPolicyResponse putBucketPolicy(PutBucketPolicyRequest putBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketPolicyResponse putBucketPolicy(Consumer<PutBucketPolicyRequest.Builder> putBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketPolicy((PutBucketPolicyRequest)((Object)((PutBucketPolicyRequest.Builder)PutBucketPolicyRequest.builder().applyMutation(putBucketPolicyRequest)).build()));
    }

    default public PutBucketReplicationResponse putBucketReplication(PutBucketReplicationRequest putBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketReplicationResponse putBucketReplication(Consumer<PutBucketReplicationRequest.Builder> putBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketReplication((PutBucketReplicationRequest)((Object)((PutBucketReplicationRequest.Builder)PutBucketReplicationRequest.builder().applyMutation(putBucketReplicationRequest)).build()));
    }

    default public PutBucketRequestPaymentResponse putBucketRequestPayment(PutBucketRequestPaymentRequest putBucketRequestPaymentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketRequestPaymentResponse putBucketRequestPayment(Consumer<PutBucketRequestPaymentRequest.Builder> putBucketRequestPaymentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketRequestPayment((PutBucketRequestPaymentRequest)((Object)((PutBucketRequestPaymentRequest.Builder)PutBucketRequestPaymentRequest.builder().applyMutation(putBucketRequestPaymentRequest)).build()));
    }

    default public PutBucketTaggingResponse putBucketTagging(PutBucketTaggingRequest putBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketTaggingResponse putBucketTagging(Consumer<PutBucketTaggingRequest.Builder> putBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketTagging((PutBucketTaggingRequest)((Object)((PutBucketTaggingRequest.Builder)PutBucketTaggingRequest.builder().applyMutation(putBucketTaggingRequest)).build()));
    }

    default public PutBucketVersioningResponse putBucketVersioning(PutBucketVersioningRequest putBucketVersioningRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketVersioningResponse putBucketVersioning(Consumer<PutBucketVersioningRequest.Builder> putBucketVersioningRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketVersioning((PutBucketVersioningRequest)((Object)((PutBucketVersioningRequest.Builder)PutBucketVersioningRequest.builder().applyMutation(putBucketVersioningRequest)).build()));
    }

    default public PutBucketWebsiteResponse putBucketWebsite(PutBucketWebsiteRequest putBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutBucketWebsiteResponse putBucketWebsite(Consumer<PutBucketWebsiteRequest.Builder> putBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putBucketWebsite((PutBucketWebsiteRequest)((Object)((PutBucketWebsiteRequest.Builder)PutBucketWebsiteRequest.builder().applyMutation(putBucketWebsiteRequest)).build()));
    }

    default public PutObjectResponse putObject(PutObjectRequest putObjectRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutObjectResponse putObject(Consumer<PutObjectRequest.Builder> putObjectRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putObject((PutObjectRequest)((Object)((PutObjectRequest.Builder)PutObjectRequest.builder().applyMutation(putObjectRequest)).build()), requestBody);
    }

    default public PutObjectResponse putObject(PutObjectRequest putObjectRequest, Path sourcePath) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putObject(putObjectRequest, RequestBody.fromFile((Path)sourcePath));
    }

    default public PutObjectResponse putObject(Consumer<PutObjectRequest.Builder> putObjectRequest, Path sourcePath) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putObject((PutObjectRequest)((Object)((PutObjectRequest.Builder)PutObjectRequest.builder().applyMutation(putObjectRequest)).build()), sourcePath);
    }

    default public PutObjectAclResponse putObjectAcl(PutObjectAclRequest putObjectAclRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutObjectAclResponse putObjectAcl(Consumer<PutObjectAclRequest.Builder> putObjectAclRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        return this.putObjectAcl((PutObjectAclRequest)((Object)((PutObjectAclRequest.Builder)PutObjectAclRequest.builder().applyMutation(putObjectAclRequest)).build()));
    }

    default public PutObjectLegalHoldResponse putObjectLegalHold(PutObjectLegalHoldRequest putObjectLegalHoldRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutObjectLegalHoldResponse putObjectLegalHold(Consumer<PutObjectLegalHoldRequest.Builder> putObjectLegalHoldRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putObjectLegalHold((PutObjectLegalHoldRequest)((Object)((PutObjectLegalHoldRequest.Builder)PutObjectLegalHoldRequest.builder().applyMutation(putObjectLegalHoldRequest)).build()));
    }

    default public PutObjectLockConfigurationResponse putObjectLockConfiguration(PutObjectLockConfigurationRequest putObjectLockConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutObjectLockConfigurationResponse putObjectLockConfiguration(Consumer<PutObjectLockConfigurationRequest.Builder> putObjectLockConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putObjectLockConfiguration((PutObjectLockConfigurationRequest)((Object)((PutObjectLockConfigurationRequest.Builder)PutObjectLockConfigurationRequest.builder().applyMutation(putObjectLockConfigurationRequest)).build()));
    }

    default public PutObjectRetentionResponse putObjectRetention(PutObjectRetentionRequest putObjectRetentionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutObjectRetentionResponse putObjectRetention(Consumer<PutObjectRetentionRequest.Builder> putObjectRetentionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putObjectRetention((PutObjectRetentionRequest)((Object)((PutObjectRetentionRequest.Builder)PutObjectRetentionRequest.builder().applyMutation(putObjectRetentionRequest)).build()));
    }

    default public PutObjectTaggingResponse putObjectTagging(PutObjectTaggingRequest putObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutObjectTaggingResponse putObjectTagging(Consumer<PutObjectTaggingRequest.Builder> putObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putObjectTagging((PutObjectTaggingRequest)((Object)((PutObjectTaggingRequest.Builder)PutObjectTaggingRequest.builder().applyMutation(putObjectTaggingRequest)).build()));
    }

    default public PutPublicAccessBlockResponse putPublicAccessBlock(PutPublicAccessBlockRequest putPublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public PutPublicAccessBlockResponse putPublicAccessBlock(Consumer<PutPublicAccessBlockRequest.Builder> putPublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.putPublicAccessBlock((PutPublicAccessBlockRequest)((Object)((PutPublicAccessBlockRequest.Builder)PutPublicAccessBlockRequest.builder().applyMutation(putPublicAccessBlockRequest)).build()));
    }

    default public RestoreObjectResponse restoreObject(RestoreObjectRequest restoreObjectRequest) throws ObjectAlreadyInActiveTierErrorException, AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public RestoreObjectResponse restoreObject(Consumer<RestoreObjectRequest.Builder> restoreObjectRequest) throws ObjectAlreadyInActiveTierErrorException, AwsServiceException, SdkClientException, S3Exception {
        return this.restoreObject((RestoreObjectRequest)((Object)((RestoreObjectRequest.Builder)RestoreObjectRequest.builder().applyMutation(restoreObjectRequest)).build()));
    }

    default public UploadPartResponse uploadPart(UploadPartRequest uploadPartRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public UploadPartResponse uploadPart(Consumer<UploadPartRequest.Builder> uploadPartRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        return this.uploadPart((UploadPartRequest)((Object)((UploadPartRequest.Builder)UploadPartRequest.builder().applyMutation(uploadPartRequest)).build()), requestBody);
    }

    default public UploadPartResponse uploadPart(UploadPartRequest uploadPartRequest, Path sourcePath) throws AwsServiceException, SdkClientException, S3Exception {
        return this.uploadPart(uploadPartRequest, RequestBody.fromFile((Path)sourcePath));
    }

    default public UploadPartResponse uploadPart(Consumer<UploadPartRequest.Builder> uploadPartRequest, Path sourcePath) throws AwsServiceException, SdkClientException, S3Exception {
        return this.uploadPart((UploadPartRequest)((Object)((UploadPartRequest.Builder)UploadPartRequest.builder().applyMutation(uploadPartRequest)).build()), sourcePath);
    }

    default public UploadPartCopyResponse uploadPartCopy(UploadPartCopyRequest uploadPartCopyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public UploadPartCopyResponse uploadPartCopy(Consumer<UploadPartCopyRequest.Builder> uploadPartCopyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.uploadPartCopy((UploadPartCopyRequest)((Object)((UploadPartCopyRequest.Builder)UploadPartCopyRequest.builder().applyMutation(uploadPartCopyRequest)).build()));
    }

    default public WriteGetObjectResponseResponse writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        throw new UnsupportedOperationException();
    }

    default public WriteGetObjectResponseResponse writeGetObjectResponse(Consumer<WriteGetObjectResponseRequest.Builder> writeGetObjectResponseRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        return this.writeGetObjectResponse((WriteGetObjectResponseRequest)((Object)((WriteGetObjectResponseRequest.Builder)WriteGetObjectResponseRequest.builder().applyMutation(writeGetObjectResponseRequest)).build()), requestBody);
    }

    default public WriteGetObjectResponseResponse writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest, Path sourcePath) throws AwsServiceException, SdkClientException, S3Exception {
        return this.writeGetObjectResponse(writeGetObjectResponseRequest, RequestBody.fromFile((Path)sourcePath));
    }

    default public WriteGetObjectResponseResponse writeGetObjectResponse(Consumer<WriteGetObjectResponseRequest.Builder> writeGetObjectResponseRequest, Path sourcePath) throws AwsServiceException, SdkClientException, S3Exception {
        return this.writeGetObjectResponse((WriteGetObjectResponseRequest)((Object)((WriteGetObjectResponseRequest.Builder)WriteGetObjectResponseRequest.builder().applyMutation(writeGetObjectResponseRequest)).build()), sourcePath);
    }

    default public S3Utilities utilities() {
        throw new UnsupportedOperationException();
    }

    default public S3Waiter waiter() {
        throw new UnsupportedOperationException();
    }

    public static S3Client create() {
        return (S3Client)S3Client.builder().build();
    }

    public static S3ClientBuilder builder() {
        return new DefaultS3ClientBuilder();
    }

    public static ServiceMetadata serviceMetadata() {
        return ServiceMetadata.of((String)"s3");
    }

    default public S3ServiceClientConfiguration serviceClientConfiguration() {
        throw new UnsupportedOperationException();
    }
}

