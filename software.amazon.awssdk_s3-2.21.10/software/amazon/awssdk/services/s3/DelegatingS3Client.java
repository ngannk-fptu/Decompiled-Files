/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.core.SdkClient
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.sync.RequestBody
 *  software.amazon.awssdk.core.sync.ResponseTransformer
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3;

import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
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
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;
import software.amazon.awssdk.services.s3.model.UploadPartCopyResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.model.WriteGetObjectResponseRequest;
import software.amazon.awssdk.services.s3.model.WriteGetObjectResponseResponse;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public abstract class DelegatingS3Client
implements S3Client {
    private final S3Client delegate;

    public DelegatingS3Client(S3Client delegate) {
        Validate.paramNotNull((Object)delegate, (String)"delegate");
        this.delegate = delegate;
    }

    @Override
    public AbortMultipartUploadResponse abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest) throws NoSuchUploadException, AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(abortMultipartUploadRequest, request -> this.delegate.abortMultipartUpload((AbortMultipartUploadRequest)((Object)request)));
    }

    @Override
    public CompleteMultipartUploadResponse completeMultipartUpload(CompleteMultipartUploadRequest completeMultipartUploadRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(completeMultipartUploadRequest, request -> this.delegate.completeMultipartUpload((CompleteMultipartUploadRequest)((Object)request)));
    }

    @Override
    public CopyObjectResponse copyObject(CopyObjectRequest copyObjectRequest) throws ObjectNotInActiveTierErrorException, AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(copyObjectRequest, request -> this.delegate.copyObject((CopyObjectRequest)((Object)request)));
    }

    @Override
    public CreateBucketResponse createBucket(CreateBucketRequest createBucketRequest) throws BucketAlreadyExistsException, BucketAlreadyOwnedByYouException, AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(createBucketRequest, request -> this.delegate.createBucket((CreateBucketRequest)((Object)request)));
    }

    @Override
    public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest createMultipartUploadRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(createMultipartUploadRequest, request -> this.delegate.createMultipartUpload((CreateMultipartUploadRequest)((Object)request)));
    }

    @Override
    public DeleteBucketResponse deleteBucket(DeleteBucketRequest deleteBucketRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketRequest, request -> this.delegate.deleteBucket((DeleteBucketRequest)((Object)request)));
    }

    @Override
    public DeleteBucketAnalyticsConfigurationResponse deleteBucketAnalyticsConfiguration(DeleteBucketAnalyticsConfigurationRequest deleteBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketAnalyticsConfigurationRequest, request -> this.delegate.deleteBucketAnalyticsConfiguration((DeleteBucketAnalyticsConfigurationRequest)((Object)request)));
    }

    @Override
    public DeleteBucketCorsResponse deleteBucketCors(DeleteBucketCorsRequest deleteBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketCorsRequest, request -> this.delegate.deleteBucketCors((DeleteBucketCorsRequest)((Object)request)));
    }

    @Override
    public DeleteBucketEncryptionResponse deleteBucketEncryption(DeleteBucketEncryptionRequest deleteBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketEncryptionRequest, request -> this.delegate.deleteBucketEncryption((DeleteBucketEncryptionRequest)((Object)request)));
    }

    @Override
    public DeleteBucketIntelligentTieringConfigurationResponse deleteBucketIntelligentTieringConfiguration(DeleteBucketIntelligentTieringConfigurationRequest deleteBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketIntelligentTieringConfigurationRequest, request -> this.delegate.deleteBucketIntelligentTieringConfiguration((DeleteBucketIntelligentTieringConfigurationRequest)((Object)request)));
    }

    @Override
    public DeleteBucketInventoryConfigurationResponse deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest deleteBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketInventoryConfigurationRequest, request -> this.delegate.deleteBucketInventoryConfiguration((DeleteBucketInventoryConfigurationRequest)((Object)request)));
    }

    @Override
    public DeleteBucketLifecycleResponse deleteBucketLifecycle(DeleteBucketLifecycleRequest deleteBucketLifecycleRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketLifecycleRequest, request -> this.delegate.deleteBucketLifecycle((DeleteBucketLifecycleRequest)((Object)request)));
    }

    @Override
    public DeleteBucketMetricsConfigurationResponse deleteBucketMetricsConfiguration(DeleteBucketMetricsConfigurationRequest deleteBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketMetricsConfigurationRequest, request -> this.delegate.deleteBucketMetricsConfiguration((DeleteBucketMetricsConfigurationRequest)((Object)request)));
    }

    @Override
    public DeleteBucketOwnershipControlsResponse deleteBucketOwnershipControls(DeleteBucketOwnershipControlsRequest deleteBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketOwnershipControlsRequest, request -> this.delegate.deleteBucketOwnershipControls((DeleteBucketOwnershipControlsRequest)((Object)request)));
    }

    @Override
    public DeleteBucketPolicyResponse deleteBucketPolicy(DeleteBucketPolicyRequest deleteBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketPolicyRequest, request -> this.delegate.deleteBucketPolicy((DeleteBucketPolicyRequest)((Object)request)));
    }

    @Override
    public DeleteBucketReplicationResponse deleteBucketReplication(DeleteBucketReplicationRequest deleteBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketReplicationRequest, request -> this.delegate.deleteBucketReplication((DeleteBucketReplicationRequest)((Object)request)));
    }

    @Override
    public DeleteBucketTaggingResponse deleteBucketTagging(DeleteBucketTaggingRequest deleteBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketTaggingRequest, request -> this.delegate.deleteBucketTagging((DeleteBucketTaggingRequest)((Object)request)));
    }

    @Override
    public DeleteBucketWebsiteResponse deleteBucketWebsite(DeleteBucketWebsiteRequest deleteBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteBucketWebsiteRequest, request -> this.delegate.deleteBucketWebsite((DeleteBucketWebsiteRequest)((Object)request)));
    }

    @Override
    public DeleteObjectResponse deleteObject(DeleteObjectRequest deleteObjectRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteObjectRequest, request -> this.delegate.deleteObject((DeleteObjectRequest)((Object)request)));
    }

    @Override
    public DeleteObjectTaggingResponse deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteObjectTaggingRequest, request -> this.delegate.deleteObjectTagging((DeleteObjectTaggingRequest)((Object)request)));
    }

    @Override
    public DeleteObjectsResponse deleteObjects(DeleteObjectsRequest deleteObjectsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deleteObjectsRequest, request -> this.delegate.deleteObjects((DeleteObjectsRequest)((Object)request)));
    }

    @Override
    public DeletePublicAccessBlockResponse deletePublicAccessBlock(DeletePublicAccessBlockRequest deletePublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(deletePublicAccessBlockRequest, request -> this.delegate.deletePublicAccessBlock((DeletePublicAccessBlockRequest)((Object)request)));
    }

    @Override
    public GetBucketAccelerateConfigurationResponse getBucketAccelerateConfiguration(GetBucketAccelerateConfigurationRequest getBucketAccelerateConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketAccelerateConfigurationRequest, request -> this.delegate.getBucketAccelerateConfiguration((GetBucketAccelerateConfigurationRequest)((Object)request)));
    }

    @Override
    public GetBucketAclResponse getBucketAcl(GetBucketAclRequest getBucketAclRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketAclRequest, request -> this.delegate.getBucketAcl((GetBucketAclRequest)((Object)request)));
    }

    @Override
    public GetBucketAnalyticsConfigurationResponse getBucketAnalyticsConfiguration(GetBucketAnalyticsConfigurationRequest getBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketAnalyticsConfigurationRequest, request -> this.delegate.getBucketAnalyticsConfiguration((GetBucketAnalyticsConfigurationRequest)((Object)request)));
    }

    @Override
    public GetBucketCorsResponse getBucketCors(GetBucketCorsRequest getBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketCorsRequest, request -> this.delegate.getBucketCors((GetBucketCorsRequest)((Object)request)));
    }

    @Override
    public GetBucketEncryptionResponse getBucketEncryption(GetBucketEncryptionRequest getBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketEncryptionRequest, request -> this.delegate.getBucketEncryption((GetBucketEncryptionRequest)((Object)request)));
    }

    @Override
    public GetBucketIntelligentTieringConfigurationResponse getBucketIntelligentTieringConfiguration(GetBucketIntelligentTieringConfigurationRequest getBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketIntelligentTieringConfigurationRequest, request -> this.delegate.getBucketIntelligentTieringConfiguration((GetBucketIntelligentTieringConfigurationRequest)((Object)request)));
    }

    @Override
    public GetBucketInventoryConfigurationResponse getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest getBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketInventoryConfigurationRequest, request -> this.delegate.getBucketInventoryConfiguration((GetBucketInventoryConfigurationRequest)((Object)request)));
    }

    @Override
    public GetBucketLifecycleConfigurationResponse getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketLifecycleConfigurationRequest, request -> this.delegate.getBucketLifecycleConfiguration((GetBucketLifecycleConfigurationRequest)((Object)request)));
    }

    @Override
    public GetBucketLocationResponse getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketLocationRequest, request -> this.delegate.getBucketLocation((GetBucketLocationRequest)((Object)request)));
    }

    @Override
    public GetBucketLoggingResponse getBucketLogging(GetBucketLoggingRequest getBucketLoggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketLoggingRequest, request -> this.delegate.getBucketLogging((GetBucketLoggingRequest)((Object)request)));
    }

    @Override
    public GetBucketMetricsConfigurationResponse getBucketMetricsConfiguration(GetBucketMetricsConfigurationRequest getBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketMetricsConfigurationRequest, request -> this.delegate.getBucketMetricsConfiguration((GetBucketMetricsConfigurationRequest)((Object)request)));
    }

    @Override
    public GetBucketNotificationConfigurationResponse getBucketNotificationConfiguration(GetBucketNotificationConfigurationRequest getBucketNotificationConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketNotificationConfigurationRequest, request -> this.delegate.getBucketNotificationConfiguration((GetBucketNotificationConfigurationRequest)((Object)request)));
    }

    @Override
    public GetBucketOwnershipControlsResponse getBucketOwnershipControls(GetBucketOwnershipControlsRequest getBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketOwnershipControlsRequest, request -> this.delegate.getBucketOwnershipControls((GetBucketOwnershipControlsRequest)((Object)request)));
    }

    @Override
    public GetBucketPolicyResponse getBucketPolicy(GetBucketPolicyRequest getBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketPolicyRequest, request -> this.delegate.getBucketPolicy((GetBucketPolicyRequest)((Object)request)));
    }

    @Override
    public GetBucketPolicyStatusResponse getBucketPolicyStatus(GetBucketPolicyStatusRequest getBucketPolicyStatusRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketPolicyStatusRequest, request -> this.delegate.getBucketPolicyStatus((GetBucketPolicyStatusRequest)((Object)request)));
    }

    @Override
    public GetBucketReplicationResponse getBucketReplication(GetBucketReplicationRequest getBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketReplicationRequest, request -> this.delegate.getBucketReplication((GetBucketReplicationRequest)((Object)request)));
    }

    @Override
    public GetBucketRequestPaymentResponse getBucketRequestPayment(GetBucketRequestPaymentRequest getBucketRequestPaymentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketRequestPaymentRequest, request -> this.delegate.getBucketRequestPayment((GetBucketRequestPaymentRequest)((Object)request)));
    }

    @Override
    public GetBucketTaggingResponse getBucketTagging(GetBucketTaggingRequest getBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketTaggingRequest, request -> this.delegate.getBucketTagging((GetBucketTaggingRequest)((Object)request)));
    }

    @Override
    public GetBucketVersioningResponse getBucketVersioning(GetBucketVersioningRequest getBucketVersioningRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketVersioningRequest, request -> this.delegate.getBucketVersioning((GetBucketVersioningRequest)((Object)request)));
    }

    @Override
    public GetBucketWebsiteResponse getBucketWebsite(GetBucketWebsiteRequest getBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getBucketWebsiteRequest, request -> this.delegate.getBucketWebsite((GetBucketWebsiteRequest)((Object)request)));
    }

    @Override
    public <ReturnT> ReturnT getObject(GetObjectRequest getObjectRequest, ResponseTransformer<GetObjectResponse, ReturnT> responseTransformer) throws NoSuchKeyException, InvalidObjectStateException, AwsServiceException, SdkClientException, S3Exception {
        return (ReturnT)this.invokeOperation(getObjectRequest, request -> this.delegate.getObject((GetObjectRequest)((Object)request), responseTransformer));
    }

    @Override
    public GetObjectAclResponse getObjectAcl(GetObjectAclRequest getObjectAclRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getObjectAclRequest, request -> this.delegate.getObjectAcl((GetObjectAclRequest)((Object)request)));
    }

    @Override
    public GetObjectAttributesResponse getObjectAttributes(GetObjectAttributesRequest getObjectAttributesRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getObjectAttributesRequest, request -> this.delegate.getObjectAttributes((GetObjectAttributesRequest)((Object)request)));
    }

    @Override
    public GetObjectLegalHoldResponse getObjectLegalHold(GetObjectLegalHoldRequest getObjectLegalHoldRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getObjectLegalHoldRequest, request -> this.delegate.getObjectLegalHold((GetObjectLegalHoldRequest)((Object)request)));
    }

    @Override
    public GetObjectLockConfigurationResponse getObjectLockConfiguration(GetObjectLockConfigurationRequest getObjectLockConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getObjectLockConfigurationRequest, request -> this.delegate.getObjectLockConfiguration((GetObjectLockConfigurationRequest)((Object)request)));
    }

    @Override
    public GetObjectRetentionResponse getObjectRetention(GetObjectRetentionRequest getObjectRetentionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getObjectRetentionRequest, request -> this.delegate.getObjectRetention((GetObjectRetentionRequest)((Object)request)));
    }

    @Override
    public GetObjectTaggingResponse getObjectTagging(GetObjectTaggingRequest getObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getObjectTaggingRequest, request -> this.delegate.getObjectTagging((GetObjectTaggingRequest)((Object)request)));
    }

    @Override
    public <ReturnT> ReturnT getObjectTorrent(GetObjectTorrentRequest getObjectTorrentRequest, ResponseTransformer<GetObjectTorrentResponse, ReturnT> responseTransformer) throws AwsServiceException, SdkClientException, S3Exception {
        return (ReturnT)this.invokeOperation(getObjectTorrentRequest, request -> this.delegate.getObjectTorrent((GetObjectTorrentRequest)((Object)request), responseTransformer));
    }

    @Override
    public GetPublicAccessBlockResponse getPublicAccessBlock(GetPublicAccessBlockRequest getPublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(getPublicAccessBlockRequest, request -> this.delegate.getPublicAccessBlock((GetPublicAccessBlockRequest)((Object)request)));
    }

    @Override
    public HeadBucketResponse headBucket(HeadBucketRequest headBucketRequest) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(headBucketRequest, request -> this.delegate.headBucket((HeadBucketRequest)((Object)request)));
    }

    @Override
    public HeadObjectResponse headObject(HeadObjectRequest headObjectRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(headObjectRequest, request -> this.delegate.headObject((HeadObjectRequest)((Object)request)));
    }

    @Override
    public ListBucketAnalyticsConfigurationsResponse listBucketAnalyticsConfigurations(ListBucketAnalyticsConfigurationsRequest listBucketAnalyticsConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(listBucketAnalyticsConfigurationsRequest, request -> this.delegate.listBucketAnalyticsConfigurations((ListBucketAnalyticsConfigurationsRequest)((Object)request)));
    }

    @Override
    public ListBucketIntelligentTieringConfigurationsResponse listBucketIntelligentTieringConfigurations(ListBucketIntelligentTieringConfigurationsRequest listBucketIntelligentTieringConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(listBucketIntelligentTieringConfigurationsRequest, request -> this.delegate.listBucketIntelligentTieringConfigurations((ListBucketIntelligentTieringConfigurationsRequest)((Object)request)));
    }

    @Override
    public ListBucketInventoryConfigurationsResponse listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest listBucketInventoryConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(listBucketInventoryConfigurationsRequest, request -> this.delegate.listBucketInventoryConfigurations((ListBucketInventoryConfigurationsRequest)((Object)request)));
    }

    @Override
    public ListBucketMetricsConfigurationsResponse listBucketMetricsConfigurations(ListBucketMetricsConfigurationsRequest listBucketMetricsConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(listBucketMetricsConfigurationsRequest, request -> this.delegate.listBucketMetricsConfigurations((ListBucketMetricsConfigurationsRequest)((Object)request)));
    }

    @Override
    public ListBucketsResponse listBuckets(ListBucketsRequest listBucketsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(listBucketsRequest, request -> this.delegate.listBuckets((ListBucketsRequest)((Object)request)));
    }

    @Override
    public ListMultipartUploadsResponse listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(listMultipartUploadsRequest, request -> this.delegate.listMultipartUploads((ListMultipartUploadsRequest)((Object)request)));
    }

    @Override
    public ListObjectVersionsResponse listObjectVersions(ListObjectVersionsRequest listObjectVersionsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(listObjectVersionsRequest, request -> this.delegate.listObjectVersions((ListObjectVersionsRequest)((Object)request)));
    }

    @Override
    public ListObjectsResponse listObjects(ListObjectsRequest listObjectsRequest) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(listObjectsRequest, request -> this.delegate.listObjects((ListObjectsRequest)((Object)request)));
    }

    @Override
    public ListObjectsV2Response listObjectsV2(ListObjectsV2Request listObjectsV2Request) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(listObjectsV2Request, request -> this.delegate.listObjectsV2((ListObjectsV2Request)((Object)request)));
    }

    @Override
    public ListPartsResponse listParts(ListPartsRequest listPartsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(listPartsRequest, request -> this.delegate.listParts((ListPartsRequest)((Object)request)));
    }

    @Override
    public PutBucketAccelerateConfigurationResponse putBucketAccelerateConfiguration(PutBucketAccelerateConfigurationRequest putBucketAccelerateConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketAccelerateConfigurationRequest, request -> this.delegate.putBucketAccelerateConfiguration((PutBucketAccelerateConfigurationRequest)((Object)request)));
    }

    @Override
    public PutBucketAclResponse putBucketAcl(PutBucketAclRequest putBucketAclRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketAclRequest, request -> this.delegate.putBucketAcl((PutBucketAclRequest)((Object)request)));
    }

    @Override
    public PutBucketAnalyticsConfigurationResponse putBucketAnalyticsConfiguration(PutBucketAnalyticsConfigurationRequest putBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketAnalyticsConfigurationRequest, request -> this.delegate.putBucketAnalyticsConfiguration((PutBucketAnalyticsConfigurationRequest)((Object)request)));
    }

    @Override
    public PutBucketCorsResponse putBucketCors(PutBucketCorsRequest putBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketCorsRequest, request -> this.delegate.putBucketCors((PutBucketCorsRequest)((Object)request)));
    }

    @Override
    public PutBucketEncryptionResponse putBucketEncryption(PutBucketEncryptionRequest putBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketEncryptionRequest, request -> this.delegate.putBucketEncryption((PutBucketEncryptionRequest)((Object)request)));
    }

    @Override
    public PutBucketIntelligentTieringConfigurationResponse putBucketIntelligentTieringConfiguration(PutBucketIntelligentTieringConfigurationRequest putBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketIntelligentTieringConfigurationRequest, request -> this.delegate.putBucketIntelligentTieringConfiguration((PutBucketIntelligentTieringConfigurationRequest)((Object)request)));
    }

    @Override
    public PutBucketInventoryConfigurationResponse putBucketInventoryConfiguration(PutBucketInventoryConfigurationRequest putBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketInventoryConfigurationRequest, request -> this.delegate.putBucketInventoryConfiguration((PutBucketInventoryConfigurationRequest)((Object)request)));
    }

    @Override
    public PutBucketLifecycleConfigurationResponse putBucketLifecycleConfiguration(PutBucketLifecycleConfigurationRequest putBucketLifecycleConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketLifecycleConfigurationRequest, request -> this.delegate.putBucketLifecycleConfiguration((PutBucketLifecycleConfigurationRequest)((Object)request)));
    }

    @Override
    public PutBucketLoggingResponse putBucketLogging(PutBucketLoggingRequest putBucketLoggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketLoggingRequest, request -> this.delegate.putBucketLogging((PutBucketLoggingRequest)((Object)request)));
    }

    @Override
    public PutBucketMetricsConfigurationResponse putBucketMetricsConfiguration(PutBucketMetricsConfigurationRequest putBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketMetricsConfigurationRequest, request -> this.delegate.putBucketMetricsConfiguration((PutBucketMetricsConfigurationRequest)((Object)request)));
    }

    @Override
    public PutBucketNotificationConfigurationResponse putBucketNotificationConfiguration(PutBucketNotificationConfigurationRequest putBucketNotificationConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketNotificationConfigurationRequest, request -> this.delegate.putBucketNotificationConfiguration((PutBucketNotificationConfigurationRequest)((Object)request)));
    }

    @Override
    public PutBucketOwnershipControlsResponse putBucketOwnershipControls(PutBucketOwnershipControlsRequest putBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketOwnershipControlsRequest, request -> this.delegate.putBucketOwnershipControls((PutBucketOwnershipControlsRequest)((Object)request)));
    }

    @Override
    public PutBucketPolicyResponse putBucketPolicy(PutBucketPolicyRequest putBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketPolicyRequest, request -> this.delegate.putBucketPolicy((PutBucketPolicyRequest)((Object)request)));
    }

    @Override
    public PutBucketReplicationResponse putBucketReplication(PutBucketReplicationRequest putBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketReplicationRequest, request -> this.delegate.putBucketReplication((PutBucketReplicationRequest)((Object)request)));
    }

    @Override
    public PutBucketRequestPaymentResponse putBucketRequestPayment(PutBucketRequestPaymentRequest putBucketRequestPaymentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketRequestPaymentRequest, request -> this.delegate.putBucketRequestPayment((PutBucketRequestPaymentRequest)((Object)request)));
    }

    @Override
    public PutBucketTaggingResponse putBucketTagging(PutBucketTaggingRequest putBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketTaggingRequest, request -> this.delegate.putBucketTagging((PutBucketTaggingRequest)((Object)request)));
    }

    @Override
    public PutBucketVersioningResponse putBucketVersioning(PutBucketVersioningRequest putBucketVersioningRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketVersioningRequest, request -> this.delegate.putBucketVersioning((PutBucketVersioningRequest)((Object)request)));
    }

    @Override
    public PutBucketWebsiteResponse putBucketWebsite(PutBucketWebsiteRequest putBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putBucketWebsiteRequest, request -> this.delegate.putBucketWebsite((PutBucketWebsiteRequest)((Object)request)));
    }

    @Override
    public PutObjectResponse putObject(PutObjectRequest putObjectRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putObjectRequest, request -> this.delegate.putObject((PutObjectRequest)((Object)request), requestBody));
    }

    @Override
    public PutObjectAclResponse putObjectAcl(PutObjectAclRequest putObjectAclRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putObjectAclRequest, request -> this.delegate.putObjectAcl((PutObjectAclRequest)((Object)request)));
    }

    @Override
    public PutObjectLegalHoldResponse putObjectLegalHold(PutObjectLegalHoldRequest putObjectLegalHoldRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putObjectLegalHoldRequest, request -> this.delegate.putObjectLegalHold((PutObjectLegalHoldRequest)((Object)request)));
    }

    @Override
    public PutObjectLockConfigurationResponse putObjectLockConfiguration(PutObjectLockConfigurationRequest putObjectLockConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putObjectLockConfigurationRequest, request -> this.delegate.putObjectLockConfiguration((PutObjectLockConfigurationRequest)((Object)request)));
    }

    @Override
    public PutObjectRetentionResponse putObjectRetention(PutObjectRetentionRequest putObjectRetentionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putObjectRetentionRequest, request -> this.delegate.putObjectRetention((PutObjectRetentionRequest)((Object)request)));
    }

    @Override
    public PutObjectTaggingResponse putObjectTagging(PutObjectTaggingRequest putObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putObjectTaggingRequest, request -> this.delegate.putObjectTagging((PutObjectTaggingRequest)((Object)request)));
    }

    @Override
    public PutPublicAccessBlockResponse putPublicAccessBlock(PutPublicAccessBlockRequest putPublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(putPublicAccessBlockRequest, request -> this.delegate.putPublicAccessBlock((PutPublicAccessBlockRequest)((Object)request)));
    }

    @Override
    public RestoreObjectResponse restoreObject(RestoreObjectRequest restoreObjectRequest) throws ObjectAlreadyInActiveTierErrorException, AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(restoreObjectRequest, request -> this.delegate.restoreObject((RestoreObjectRequest)((Object)request)));
    }

    @Override
    public UploadPartResponse uploadPart(UploadPartRequest uploadPartRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(uploadPartRequest, request -> this.delegate.uploadPart((UploadPartRequest)((Object)request), requestBody));
    }

    @Override
    public UploadPartCopyResponse uploadPartCopy(UploadPartCopyRequest uploadPartCopyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(uploadPartCopyRequest, request -> this.delegate.uploadPartCopy((UploadPartCopyRequest)((Object)request)));
    }

    @Override
    public WriteGetObjectResponseResponse writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        return this.invokeOperation(writeGetObjectResponseRequest, request -> this.delegate.writeGetObjectResponse((WriteGetObjectResponseRequest)((Object)request), requestBody));
    }

    @Override
    public S3Utilities utilities() {
        return this.delegate.utilities();
    }

    @Override
    public S3Waiter waiter() {
        return this.delegate.waiter();
    }

    public final String serviceName() {
        return this.delegate.serviceName();
    }

    public SdkClient delegate() {
        return this.delegate;
    }

    protected <T extends S3Request, ReturnT> ReturnT invokeOperation(T request, Function<T, ReturnT> operation) {
        return operation.apply(request);
    }

    @Override
    public final S3ServiceClientConfiguration serviceClientConfiguration() {
        return this.delegate.serviceClientConfiguration();
    }

    public void close() {
        this.delegate.close();
    }
}

