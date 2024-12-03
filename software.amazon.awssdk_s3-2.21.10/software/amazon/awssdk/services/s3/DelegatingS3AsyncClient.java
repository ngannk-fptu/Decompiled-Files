/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.SdkClient
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.core.async.AsyncResponseTransformer
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3ServiceClientConfiguration;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadResponse;
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
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.model.SelectObjectContentRequest;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;
import software.amazon.awssdk.services.s3.model.UploadPartCopyResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.model.WriteGetObjectResponseRequest;
import software.amazon.awssdk.services.s3.model.WriteGetObjectResponseResponse;
import software.amazon.awssdk.services.s3.waiters.S3AsyncWaiter;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public abstract class DelegatingS3AsyncClient
implements S3AsyncClient {
    private final S3AsyncClient delegate;

    public DelegatingS3AsyncClient(S3AsyncClient delegate) {
        Validate.paramNotNull((Object)delegate, (String)"delegate");
        this.delegate = delegate;
    }

    @Override
    public S3Utilities utilities() {
        return this.delegate.utilities();
    }

    @Override
    public CompletableFuture<AbortMultipartUploadResponse> abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest) {
        return this.invokeOperation(abortMultipartUploadRequest, request -> this.delegate.abortMultipartUpload((AbortMultipartUploadRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<CompleteMultipartUploadResponse> completeMultipartUpload(CompleteMultipartUploadRequest completeMultipartUploadRequest) {
        return this.invokeOperation(completeMultipartUploadRequest, request -> this.delegate.completeMultipartUpload((CompleteMultipartUploadRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<CopyObjectResponse> copyObject(CopyObjectRequest copyObjectRequest) {
        return this.invokeOperation(copyObjectRequest, request -> this.delegate.copyObject((CopyObjectRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<CreateBucketResponse> createBucket(CreateBucketRequest createBucketRequest) {
        return this.invokeOperation(createBucketRequest, request -> this.delegate.createBucket((CreateBucketRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<CreateMultipartUploadResponse> createMultipartUpload(CreateMultipartUploadRequest createMultipartUploadRequest) {
        return this.invokeOperation(createMultipartUploadRequest, request -> this.delegate.createMultipartUpload((CreateMultipartUploadRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketResponse> deleteBucket(DeleteBucketRequest deleteBucketRequest) {
        return this.invokeOperation(deleteBucketRequest, request -> this.delegate.deleteBucket((DeleteBucketRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketAnalyticsConfigurationResponse> deleteBucketAnalyticsConfiguration(DeleteBucketAnalyticsConfigurationRequest deleteBucketAnalyticsConfigurationRequest) {
        return this.invokeOperation(deleteBucketAnalyticsConfigurationRequest, request -> this.delegate.deleteBucketAnalyticsConfiguration((DeleteBucketAnalyticsConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketCorsResponse> deleteBucketCors(DeleteBucketCorsRequest deleteBucketCorsRequest) {
        return this.invokeOperation(deleteBucketCorsRequest, request -> this.delegate.deleteBucketCors((DeleteBucketCorsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketEncryptionResponse> deleteBucketEncryption(DeleteBucketEncryptionRequest deleteBucketEncryptionRequest) {
        return this.invokeOperation(deleteBucketEncryptionRequest, request -> this.delegate.deleteBucketEncryption((DeleteBucketEncryptionRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketIntelligentTieringConfigurationResponse> deleteBucketIntelligentTieringConfiguration(DeleteBucketIntelligentTieringConfigurationRequest deleteBucketIntelligentTieringConfigurationRequest) {
        return this.invokeOperation(deleteBucketIntelligentTieringConfigurationRequest, request -> this.delegate.deleteBucketIntelligentTieringConfiguration((DeleteBucketIntelligentTieringConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketInventoryConfigurationResponse> deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest deleteBucketInventoryConfigurationRequest) {
        return this.invokeOperation(deleteBucketInventoryConfigurationRequest, request -> this.delegate.deleteBucketInventoryConfiguration((DeleteBucketInventoryConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketLifecycleResponse> deleteBucketLifecycle(DeleteBucketLifecycleRequest deleteBucketLifecycleRequest) {
        return this.invokeOperation(deleteBucketLifecycleRequest, request -> this.delegate.deleteBucketLifecycle((DeleteBucketLifecycleRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketMetricsConfigurationResponse> deleteBucketMetricsConfiguration(DeleteBucketMetricsConfigurationRequest deleteBucketMetricsConfigurationRequest) {
        return this.invokeOperation(deleteBucketMetricsConfigurationRequest, request -> this.delegate.deleteBucketMetricsConfiguration((DeleteBucketMetricsConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketOwnershipControlsResponse> deleteBucketOwnershipControls(DeleteBucketOwnershipControlsRequest deleteBucketOwnershipControlsRequest) {
        return this.invokeOperation(deleteBucketOwnershipControlsRequest, request -> this.delegate.deleteBucketOwnershipControls((DeleteBucketOwnershipControlsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketPolicyResponse> deleteBucketPolicy(DeleteBucketPolicyRequest deleteBucketPolicyRequest) {
        return this.invokeOperation(deleteBucketPolicyRequest, request -> this.delegate.deleteBucketPolicy((DeleteBucketPolicyRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketReplicationResponse> deleteBucketReplication(DeleteBucketReplicationRequest deleteBucketReplicationRequest) {
        return this.invokeOperation(deleteBucketReplicationRequest, request -> this.delegate.deleteBucketReplication((DeleteBucketReplicationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketTaggingResponse> deleteBucketTagging(DeleteBucketTaggingRequest deleteBucketTaggingRequest) {
        return this.invokeOperation(deleteBucketTaggingRequest, request -> this.delegate.deleteBucketTagging((DeleteBucketTaggingRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteBucketWebsiteResponse> deleteBucketWebsite(DeleteBucketWebsiteRequest deleteBucketWebsiteRequest) {
        return this.invokeOperation(deleteBucketWebsiteRequest, request -> this.delegate.deleteBucketWebsite((DeleteBucketWebsiteRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteObjectResponse> deleteObject(DeleteObjectRequest deleteObjectRequest) {
        return this.invokeOperation(deleteObjectRequest, request -> this.delegate.deleteObject((DeleteObjectRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteObjectTaggingResponse> deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) {
        return this.invokeOperation(deleteObjectTaggingRequest, request -> this.delegate.deleteObjectTagging((DeleteObjectTaggingRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeleteObjectsResponse> deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {
        return this.invokeOperation(deleteObjectsRequest, request -> this.delegate.deleteObjects((DeleteObjectsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<DeletePublicAccessBlockResponse> deletePublicAccessBlock(DeletePublicAccessBlockRequest deletePublicAccessBlockRequest) {
        return this.invokeOperation(deletePublicAccessBlockRequest, request -> this.delegate.deletePublicAccessBlock((DeletePublicAccessBlockRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketAccelerateConfigurationResponse> getBucketAccelerateConfiguration(GetBucketAccelerateConfigurationRequest getBucketAccelerateConfigurationRequest) {
        return this.invokeOperation(getBucketAccelerateConfigurationRequest, request -> this.delegate.getBucketAccelerateConfiguration((GetBucketAccelerateConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketAclResponse> getBucketAcl(GetBucketAclRequest getBucketAclRequest) {
        return this.invokeOperation(getBucketAclRequest, request -> this.delegate.getBucketAcl((GetBucketAclRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketAnalyticsConfigurationResponse> getBucketAnalyticsConfiguration(GetBucketAnalyticsConfigurationRequest getBucketAnalyticsConfigurationRequest) {
        return this.invokeOperation(getBucketAnalyticsConfigurationRequest, request -> this.delegate.getBucketAnalyticsConfiguration((GetBucketAnalyticsConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketCorsResponse> getBucketCors(GetBucketCorsRequest getBucketCorsRequest) {
        return this.invokeOperation(getBucketCorsRequest, request -> this.delegate.getBucketCors((GetBucketCorsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketEncryptionResponse> getBucketEncryption(GetBucketEncryptionRequest getBucketEncryptionRequest) {
        return this.invokeOperation(getBucketEncryptionRequest, request -> this.delegate.getBucketEncryption((GetBucketEncryptionRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketIntelligentTieringConfigurationResponse> getBucketIntelligentTieringConfiguration(GetBucketIntelligentTieringConfigurationRequest getBucketIntelligentTieringConfigurationRequest) {
        return this.invokeOperation(getBucketIntelligentTieringConfigurationRequest, request -> this.delegate.getBucketIntelligentTieringConfiguration((GetBucketIntelligentTieringConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketInventoryConfigurationResponse> getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest getBucketInventoryConfigurationRequest) {
        return this.invokeOperation(getBucketInventoryConfigurationRequest, request -> this.delegate.getBucketInventoryConfiguration((GetBucketInventoryConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketLifecycleConfigurationResponse> getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) {
        return this.invokeOperation(getBucketLifecycleConfigurationRequest, request -> this.delegate.getBucketLifecycleConfiguration((GetBucketLifecycleConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketLocationResponse> getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) {
        return this.invokeOperation(getBucketLocationRequest, request -> this.delegate.getBucketLocation((GetBucketLocationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketLoggingResponse> getBucketLogging(GetBucketLoggingRequest getBucketLoggingRequest) {
        return this.invokeOperation(getBucketLoggingRequest, request -> this.delegate.getBucketLogging((GetBucketLoggingRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketMetricsConfigurationResponse> getBucketMetricsConfiguration(GetBucketMetricsConfigurationRequest getBucketMetricsConfigurationRequest) {
        return this.invokeOperation(getBucketMetricsConfigurationRequest, request -> this.delegate.getBucketMetricsConfiguration((GetBucketMetricsConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketNotificationConfigurationResponse> getBucketNotificationConfiguration(GetBucketNotificationConfigurationRequest getBucketNotificationConfigurationRequest) {
        return this.invokeOperation(getBucketNotificationConfigurationRequest, request -> this.delegate.getBucketNotificationConfiguration((GetBucketNotificationConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketOwnershipControlsResponse> getBucketOwnershipControls(GetBucketOwnershipControlsRequest getBucketOwnershipControlsRequest) {
        return this.invokeOperation(getBucketOwnershipControlsRequest, request -> this.delegate.getBucketOwnershipControls((GetBucketOwnershipControlsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketPolicyResponse> getBucketPolicy(GetBucketPolicyRequest getBucketPolicyRequest) {
        return this.invokeOperation(getBucketPolicyRequest, request -> this.delegate.getBucketPolicy((GetBucketPolicyRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketPolicyStatusResponse> getBucketPolicyStatus(GetBucketPolicyStatusRequest getBucketPolicyStatusRequest) {
        return this.invokeOperation(getBucketPolicyStatusRequest, request -> this.delegate.getBucketPolicyStatus((GetBucketPolicyStatusRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketReplicationResponse> getBucketReplication(GetBucketReplicationRequest getBucketReplicationRequest) {
        return this.invokeOperation(getBucketReplicationRequest, request -> this.delegate.getBucketReplication((GetBucketReplicationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketRequestPaymentResponse> getBucketRequestPayment(GetBucketRequestPaymentRequest getBucketRequestPaymentRequest) {
        return this.invokeOperation(getBucketRequestPaymentRequest, request -> this.delegate.getBucketRequestPayment((GetBucketRequestPaymentRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketTaggingResponse> getBucketTagging(GetBucketTaggingRequest getBucketTaggingRequest) {
        return this.invokeOperation(getBucketTaggingRequest, request -> this.delegate.getBucketTagging((GetBucketTaggingRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketVersioningResponse> getBucketVersioning(GetBucketVersioningRequest getBucketVersioningRequest) {
        return this.invokeOperation(getBucketVersioningRequest, request -> this.delegate.getBucketVersioning((GetBucketVersioningRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetBucketWebsiteResponse> getBucketWebsite(GetBucketWebsiteRequest getBucketWebsiteRequest) {
        return this.invokeOperation(getBucketWebsiteRequest, request -> this.delegate.getBucketWebsite((GetBucketWebsiteRequest)((Object)request)));
    }

    @Override
    public <ReturnT> CompletableFuture<ReturnT> getObject(GetObjectRequest getObjectRequest, AsyncResponseTransformer<GetObjectResponse, ReturnT> asyncResponseTransformer) {
        return this.invokeOperation(getObjectRequest, request -> this.delegate.getObject((GetObjectRequest)((Object)request), asyncResponseTransformer));
    }

    @Override
    public CompletableFuture<GetObjectAclResponse> getObjectAcl(GetObjectAclRequest getObjectAclRequest) {
        return this.invokeOperation(getObjectAclRequest, request -> this.delegate.getObjectAcl((GetObjectAclRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetObjectAttributesResponse> getObjectAttributes(GetObjectAttributesRequest getObjectAttributesRequest) {
        return this.invokeOperation(getObjectAttributesRequest, request -> this.delegate.getObjectAttributes((GetObjectAttributesRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetObjectLegalHoldResponse> getObjectLegalHold(GetObjectLegalHoldRequest getObjectLegalHoldRequest) {
        return this.invokeOperation(getObjectLegalHoldRequest, request -> this.delegate.getObjectLegalHold((GetObjectLegalHoldRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetObjectLockConfigurationResponse> getObjectLockConfiguration(GetObjectLockConfigurationRequest getObjectLockConfigurationRequest) {
        return this.invokeOperation(getObjectLockConfigurationRequest, request -> this.delegate.getObjectLockConfiguration((GetObjectLockConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetObjectRetentionResponse> getObjectRetention(GetObjectRetentionRequest getObjectRetentionRequest) {
        return this.invokeOperation(getObjectRetentionRequest, request -> this.delegate.getObjectRetention((GetObjectRetentionRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<GetObjectTaggingResponse> getObjectTagging(GetObjectTaggingRequest getObjectTaggingRequest) {
        return this.invokeOperation(getObjectTaggingRequest, request -> this.delegate.getObjectTagging((GetObjectTaggingRequest)((Object)request)));
    }

    @Override
    public <ReturnT> CompletableFuture<ReturnT> getObjectTorrent(GetObjectTorrentRequest getObjectTorrentRequest, AsyncResponseTransformer<GetObjectTorrentResponse, ReturnT> asyncResponseTransformer) {
        return this.invokeOperation(getObjectTorrentRequest, request -> this.delegate.getObjectTorrent((GetObjectTorrentRequest)((Object)request), asyncResponseTransformer));
    }

    @Override
    public CompletableFuture<GetPublicAccessBlockResponse> getPublicAccessBlock(GetPublicAccessBlockRequest getPublicAccessBlockRequest) {
        return this.invokeOperation(getPublicAccessBlockRequest, request -> this.delegate.getPublicAccessBlock((GetPublicAccessBlockRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<HeadBucketResponse> headBucket(HeadBucketRequest headBucketRequest) {
        return this.invokeOperation(headBucketRequest, request -> this.delegate.headBucket((HeadBucketRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<HeadObjectResponse> headObject(HeadObjectRequest headObjectRequest) {
        return this.invokeOperation(headObjectRequest, request -> this.delegate.headObject((HeadObjectRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<ListBucketAnalyticsConfigurationsResponse> listBucketAnalyticsConfigurations(ListBucketAnalyticsConfigurationsRequest listBucketAnalyticsConfigurationsRequest) {
        return this.invokeOperation(listBucketAnalyticsConfigurationsRequest, request -> this.delegate.listBucketAnalyticsConfigurations((ListBucketAnalyticsConfigurationsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<ListBucketIntelligentTieringConfigurationsResponse> listBucketIntelligentTieringConfigurations(ListBucketIntelligentTieringConfigurationsRequest listBucketIntelligentTieringConfigurationsRequest) {
        return this.invokeOperation(listBucketIntelligentTieringConfigurationsRequest, request -> this.delegate.listBucketIntelligentTieringConfigurations((ListBucketIntelligentTieringConfigurationsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<ListBucketInventoryConfigurationsResponse> listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest listBucketInventoryConfigurationsRequest) {
        return this.invokeOperation(listBucketInventoryConfigurationsRequest, request -> this.delegate.listBucketInventoryConfigurations((ListBucketInventoryConfigurationsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<ListBucketMetricsConfigurationsResponse> listBucketMetricsConfigurations(ListBucketMetricsConfigurationsRequest listBucketMetricsConfigurationsRequest) {
        return this.invokeOperation(listBucketMetricsConfigurationsRequest, request -> this.delegate.listBucketMetricsConfigurations((ListBucketMetricsConfigurationsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<ListBucketsResponse> listBuckets(ListBucketsRequest listBucketsRequest) {
        return this.invokeOperation(listBucketsRequest, request -> this.delegate.listBuckets((ListBucketsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<ListMultipartUploadsResponse> listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest) {
        return this.invokeOperation(listMultipartUploadsRequest, request -> this.delegate.listMultipartUploads((ListMultipartUploadsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<ListObjectVersionsResponse> listObjectVersions(ListObjectVersionsRequest listObjectVersionsRequest) {
        return this.invokeOperation(listObjectVersionsRequest, request -> this.delegate.listObjectVersions((ListObjectVersionsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<ListObjectsResponse> listObjects(ListObjectsRequest listObjectsRequest) {
        return this.invokeOperation(listObjectsRequest, request -> this.delegate.listObjects((ListObjectsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<ListObjectsV2Response> listObjectsV2(ListObjectsV2Request listObjectsV2Request) {
        return this.invokeOperation(listObjectsV2Request, request -> this.delegate.listObjectsV2((ListObjectsV2Request)((Object)request)));
    }

    @Override
    public CompletableFuture<ListPartsResponse> listParts(ListPartsRequest listPartsRequest) {
        return this.invokeOperation(listPartsRequest, request -> this.delegate.listParts((ListPartsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketAccelerateConfigurationResponse> putBucketAccelerateConfiguration(PutBucketAccelerateConfigurationRequest putBucketAccelerateConfigurationRequest) {
        return this.invokeOperation(putBucketAccelerateConfigurationRequest, request -> this.delegate.putBucketAccelerateConfiguration((PutBucketAccelerateConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketAclResponse> putBucketAcl(PutBucketAclRequest putBucketAclRequest) {
        return this.invokeOperation(putBucketAclRequest, request -> this.delegate.putBucketAcl((PutBucketAclRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketAnalyticsConfigurationResponse> putBucketAnalyticsConfiguration(PutBucketAnalyticsConfigurationRequest putBucketAnalyticsConfigurationRequest) {
        return this.invokeOperation(putBucketAnalyticsConfigurationRequest, request -> this.delegate.putBucketAnalyticsConfiguration((PutBucketAnalyticsConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketCorsResponse> putBucketCors(PutBucketCorsRequest putBucketCorsRequest) {
        return this.invokeOperation(putBucketCorsRequest, request -> this.delegate.putBucketCors((PutBucketCorsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketEncryptionResponse> putBucketEncryption(PutBucketEncryptionRequest putBucketEncryptionRequest) {
        return this.invokeOperation(putBucketEncryptionRequest, request -> this.delegate.putBucketEncryption((PutBucketEncryptionRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketIntelligentTieringConfigurationResponse> putBucketIntelligentTieringConfiguration(PutBucketIntelligentTieringConfigurationRequest putBucketIntelligentTieringConfigurationRequest) {
        return this.invokeOperation(putBucketIntelligentTieringConfigurationRequest, request -> this.delegate.putBucketIntelligentTieringConfiguration((PutBucketIntelligentTieringConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketInventoryConfigurationResponse> putBucketInventoryConfiguration(PutBucketInventoryConfigurationRequest putBucketInventoryConfigurationRequest) {
        return this.invokeOperation(putBucketInventoryConfigurationRequest, request -> this.delegate.putBucketInventoryConfiguration((PutBucketInventoryConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketLifecycleConfigurationResponse> putBucketLifecycleConfiguration(PutBucketLifecycleConfigurationRequest putBucketLifecycleConfigurationRequest) {
        return this.invokeOperation(putBucketLifecycleConfigurationRequest, request -> this.delegate.putBucketLifecycleConfiguration((PutBucketLifecycleConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketLoggingResponse> putBucketLogging(PutBucketLoggingRequest putBucketLoggingRequest) {
        return this.invokeOperation(putBucketLoggingRequest, request -> this.delegate.putBucketLogging((PutBucketLoggingRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketMetricsConfigurationResponse> putBucketMetricsConfiguration(PutBucketMetricsConfigurationRequest putBucketMetricsConfigurationRequest) {
        return this.invokeOperation(putBucketMetricsConfigurationRequest, request -> this.delegate.putBucketMetricsConfiguration((PutBucketMetricsConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketNotificationConfigurationResponse> putBucketNotificationConfiguration(PutBucketNotificationConfigurationRequest putBucketNotificationConfigurationRequest) {
        return this.invokeOperation(putBucketNotificationConfigurationRequest, request -> this.delegate.putBucketNotificationConfiguration((PutBucketNotificationConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketOwnershipControlsResponse> putBucketOwnershipControls(PutBucketOwnershipControlsRequest putBucketOwnershipControlsRequest) {
        return this.invokeOperation(putBucketOwnershipControlsRequest, request -> this.delegate.putBucketOwnershipControls((PutBucketOwnershipControlsRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketPolicyResponse> putBucketPolicy(PutBucketPolicyRequest putBucketPolicyRequest) {
        return this.invokeOperation(putBucketPolicyRequest, request -> this.delegate.putBucketPolicy((PutBucketPolicyRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketReplicationResponse> putBucketReplication(PutBucketReplicationRequest putBucketReplicationRequest) {
        return this.invokeOperation(putBucketReplicationRequest, request -> this.delegate.putBucketReplication((PutBucketReplicationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketRequestPaymentResponse> putBucketRequestPayment(PutBucketRequestPaymentRequest putBucketRequestPaymentRequest) {
        return this.invokeOperation(putBucketRequestPaymentRequest, request -> this.delegate.putBucketRequestPayment((PutBucketRequestPaymentRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketTaggingResponse> putBucketTagging(PutBucketTaggingRequest putBucketTaggingRequest) {
        return this.invokeOperation(putBucketTaggingRequest, request -> this.delegate.putBucketTagging((PutBucketTaggingRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketVersioningResponse> putBucketVersioning(PutBucketVersioningRequest putBucketVersioningRequest) {
        return this.invokeOperation(putBucketVersioningRequest, request -> this.delegate.putBucketVersioning((PutBucketVersioningRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutBucketWebsiteResponse> putBucketWebsite(PutBucketWebsiteRequest putBucketWebsiteRequest) {
        return this.invokeOperation(putBucketWebsiteRequest, request -> this.delegate.putBucketWebsite((PutBucketWebsiteRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutObjectResponse> putObject(PutObjectRequest putObjectRequest, AsyncRequestBody requestBody) {
        return this.invokeOperation(putObjectRequest, request -> this.delegate.putObject((PutObjectRequest)((Object)request), requestBody));
    }

    @Override
    public CompletableFuture<PutObjectAclResponse> putObjectAcl(PutObjectAclRequest putObjectAclRequest) {
        return this.invokeOperation(putObjectAclRequest, request -> this.delegate.putObjectAcl((PutObjectAclRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutObjectLegalHoldResponse> putObjectLegalHold(PutObjectLegalHoldRequest putObjectLegalHoldRequest) {
        return this.invokeOperation(putObjectLegalHoldRequest, request -> this.delegate.putObjectLegalHold((PutObjectLegalHoldRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutObjectLockConfigurationResponse> putObjectLockConfiguration(PutObjectLockConfigurationRequest putObjectLockConfigurationRequest) {
        return this.invokeOperation(putObjectLockConfigurationRequest, request -> this.delegate.putObjectLockConfiguration((PutObjectLockConfigurationRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutObjectRetentionResponse> putObjectRetention(PutObjectRetentionRequest putObjectRetentionRequest) {
        return this.invokeOperation(putObjectRetentionRequest, request -> this.delegate.putObjectRetention((PutObjectRetentionRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutObjectTaggingResponse> putObjectTagging(PutObjectTaggingRequest putObjectTaggingRequest) {
        return this.invokeOperation(putObjectTaggingRequest, request -> this.delegate.putObjectTagging((PutObjectTaggingRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<PutPublicAccessBlockResponse> putPublicAccessBlock(PutPublicAccessBlockRequest putPublicAccessBlockRequest) {
        return this.invokeOperation(putPublicAccessBlockRequest, request -> this.delegate.putPublicAccessBlock((PutPublicAccessBlockRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<RestoreObjectResponse> restoreObject(RestoreObjectRequest restoreObjectRequest) {
        return this.invokeOperation(restoreObjectRequest, request -> this.delegate.restoreObject((RestoreObjectRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<Void> selectObjectContent(SelectObjectContentRequest selectObjectContentRequest, SelectObjectContentResponseHandler asyncResponseHandler) {
        return this.invokeOperation(selectObjectContentRequest, request -> this.delegate.selectObjectContent((SelectObjectContentRequest)((Object)request), asyncResponseHandler));
    }

    @Override
    public CompletableFuture<UploadPartResponse> uploadPart(UploadPartRequest uploadPartRequest, AsyncRequestBody requestBody) {
        return this.invokeOperation(uploadPartRequest, request -> this.delegate.uploadPart((UploadPartRequest)((Object)request), requestBody));
    }

    @Override
    public CompletableFuture<UploadPartCopyResponse> uploadPartCopy(UploadPartCopyRequest uploadPartCopyRequest) {
        return this.invokeOperation(uploadPartCopyRequest, request -> this.delegate.uploadPartCopy((UploadPartCopyRequest)((Object)request)));
    }

    @Override
    public CompletableFuture<WriteGetObjectResponseResponse> writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest, AsyncRequestBody requestBody) {
        return this.invokeOperation(writeGetObjectResponseRequest, request -> this.delegate.writeGetObjectResponse((WriteGetObjectResponseRequest)((Object)request), requestBody));
    }

    @Override
    public S3AsyncWaiter waiter() {
        return this.delegate.waiter();
    }

    @Override
    public final S3ServiceClientConfiguration serviceClientConfiguration() {
        return this.delegate.serviceClientConfiguration();
    }

    public final String serviceName() {
        return this.delegate.serviceName();
    }

    public SdkClient delegate() {
        return this.delegate;
    }

    protected <T extends S3Request, ReturnT> CompletableFuture<ReturnT> invokeOperation(T request, Function<T, CompletableFuture<ReturnT>> operation) {
        return operation.apply(request);
    }

    public void close() {
        this.delegate.close();
    }
}

