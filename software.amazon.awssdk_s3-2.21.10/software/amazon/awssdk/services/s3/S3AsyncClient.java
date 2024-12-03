/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.awscore.AwsClient
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.core.async.AsyncResponseTransformer
 */
package software.amazon.awssdk.services.s3;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.DefaultS3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3CrtAsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3ServiceClientConfiguration;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.internal.crt.S3CrtAsyncClient;
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
import software.amazon.awssdk.services.s3.model.SelectObjectContentRequest;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;
import software.amazon.awssdk.services.s3.model.UploadPartCopyResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.model.WriteGetObjectResponseRequest;
import software.amazon.awssdk.services.s3.model.WriteGetObjectResponseResponse;
import software.amazon.awssdk.services.s3.paginators.ListMultipartUploadsPublisher;
import software.amazon.awssdk.services.s3.paginators.ListObjectVersionsPublisher;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Publisher;
import software.amazon.awssdk.services.s3.paginators.ListPartsPublisher;
import software.amazon.awssdk.services.s3.waiters.S3AsyncWaiter;

@SdkPublicApi
@ThreadSafe
public interface S3AsyncClient
extends AwsClient {
    public static final String SERVICE_NAME = "s3";
    public static final String SERVICE_METADATA_ID = "s3";

    default public S3Utilities utilities() {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<AbortMultipartUploadResponse> abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<AbortMultipartUploadResponse> abortMultipartUpload(Consumer<AbortMultipartUploadRequest.Builder> abortMultipartUploadRequest) {
        return this.abortMultipartUpload((AbortMultipartUploadRequest)((Object)((AbortMultipartUploadRequest.Builder)AbortMultipartUploadRequest.builder().applyMutation(abortMultipartUploadRequest)).build()));
    }

    default public CompletableFuture<CompleteMultipartUploadResponse> completeMultipartUpload(CompleteMultipartUploadRequest completeMultipartUploadRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<CompleteMultipartUploadResponse> completeMultipartUpload(Consumer<CompleteMultipartUploadRequest.Builder> completeMultipartUploadRequest) {
        return this.completeMultipartUpload((CompleteMultipartUploadRequest)((Object)((CompleteMultipartUploadRequest.Builder)CompleteMultipartUploadRequest.builder().applyMutation(completeMultipartUploadRequest)).build()));
    }

    default public CompletableFuture<CopyObjectResponse> copyObject(CopyObjectRequest copyObjectRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<CopyObjectResponse> copyObject(Consumer<CopyObjectRequest.Builder> copyObjectRequest) {
        return this.copyObject((CopyObjectRequest)((Object)((CopyObjectRequest.Builder)CopyObjectRequest.builder().applyMutation(copyObjectRequest)).build()));
    }

    default public CompletableFuture<CreateBucketResponse> createBucket(CreateBucketRequest createBucketRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<CreateBucketResponse> createBucket(Consumer<CreateBucketRequest.Builder> createBucketRequest) {
        return this.createBucket((CreateBucketRequest)((Object)((CreateBucketRequest.Builder)CreateBucketRequest.builder().applyMutation(createBucketRequest)).build()));
    }

    default public CompletableFuture<CreateMultipartUploadResponse> createMultipartUpload(CreateMultipartUploadRequest createMultipartUploadRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<CreateMultipartUploadResponse> createMultipartUpload(Consumer<CreateMultipartUploadRequest.Builder> createMultipartUploadRequest) {
        return this.createMultipartUpload((CreateMultipartUploadRequest)((Object)((CreateMultipartUploadRequest.Builder)CreateMultipartUploadRequest.builder().applyMutation(createMultipartUploadRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketResponse> deleteBucket(DeleteBucketRequest deleteBucketRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketResponse> deleteBucket(Consumer<DeleteBucketRequest.Builder> deleteBucketRequest) {
        return this.deleteBucket((DeleteBucketRequest)((Object)((DeleteBucketRequest.Builder)DeleteBucketRequest.builder().applyMutation(deleteBucketRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketAnalyticsConfigurationResponse> deleteBucketAnalyticsConfiguration(DeleteBucketAnalyticsConfigurationRequest deleteBucketAnalyticsConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketAnalyticsConfigurationResponse> deleteBucketAnalyticsConfiguration(Consumer<DeleteBucketAnalyticsConfigurationRequest.Builder> deleteBucketAnalyticsConfigurationRequest) {
        return this.deleteBucketAnalyticsConfiguration((DeleteBucketAnalyticsConfigurationRequest)((Object)((DeleteBucketAnalyticsConfigurationRequest.Builder)DeleteBucketAnalyticsConfigurationRequest.builder().applyMutation(deleteBucketAnalyticsConfigurationRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketCorsResponse> deleteBucketCors(DeleteBucketCorsRequest deleteBucketCorsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketCorsResponse> deleteBucketCors(Consumer<DeleteBucketCorsRequest.Builder> deleteBucketCorsRequest) {
        return this.deleteBucketCors((DeleteBucketCorsRequest)((Object)((DeleteBucketCorsRequest.Builder)DeleteBucketCorsRequest.builder().applyMutation(deleteBucketCorsRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketEncryptionResponse> deleteBucketEncryption(DeleteBucketEncryptionRequest deleteBucketEncryptionRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketEncryptionResponse> deleteBucketEncryption(Consumer<DeleteBucketEncryptionRequest.Builder> deleteBucketEncryptionRequest) {
        return this.deleteBucketEncryption((DeleteBucketEncryptionRequest)((Object)((DeleteBucketEncryptionRequest.Builder)DeleteBucketEncryptionRequest.builder().applyMutation(deleteBucketEncryptionRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketIntelligentTieringConfigurationResponse> deleteBucketIntelligentTieringConfiguration(DeleteBucketIntelligentTieringConfigurationRequest deleteBucketIntelligentTieringConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketIntelligentTieringConfigurationResponse> deleteBucketIntelligentTieringConfiguration(Consumer<DeleteBucketIntelligentTieringConfigurationRequest.Builder> deleteBucketIntelligentTieringConfigurationRequest) {
        return this.deleteBucketIntelligentTieringConfiguration((DeleteBucketIntelligentTieringConfigurationRequest)((Object)((DeleteBucketIntelligentTieringConfigurationRequest.Builder)DeleteBucketIntelligentTieringConfigurationRequest.builder().applyMutation(deleteBucketIntelligentTieringConfigurationRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketInventoryConfigurationResponse> deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest deleteBucketInventoryConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketInventoryConfigurationResponse> deleteBucketInventoryConfiguration(Consumer<DeleteBucketInventoryConfigurationRequest.Builder> deleteBucketInventoryConfigurationRequest) {
        return this.deleteBucketInventoryConfiguration((DeleteBucketInventoryConfigurationRequest)((Object)((DeleteBucketInventoryConfigurationRequest.Builder)DeleteBucketInventoryConfigurationRequest.builder().applyMutation(deleteBucketInventoryConfigurationRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketLifecycleResponse> deleteBucketLifecycle(DeleteBucketLifecycleRequest deleteBucketLifecycleRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketLifecycleResponse> deleteBucketLifecycle(Consumer<DeleteBucketLifecycleRequest.Builder> deleteBucketLifecycleRequest) {
        return this.deleteBucketLifecycle((DeleteBucketLifecycleRequest)((Object)((DeleteBucketLifecycleRequest.Builder)DeleteBucketLifecycleRequest.builder().applyMutation(deleteBucketLifecycleRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketMetricsConfigurationResponse> deleteBucketMetricsConfiguration(DeleteBucketMetricsConfigurationRequest deleteBucketMetricsConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketMetricsConfigurationResponse> deleteBucketMetricsConfiguration(Consumer<DeleteBucketMetricsConfigurationRequest.Builder> deleteBucketMetricsConfigurationRequest) {
        return this.deleteBucketMetricsConfiguration((DeleteBucketMetricsConfigurationRequest)((Object)((DeleteBucketMetricsConfigurationRequest.Builder)DeleteBucketMetricsConfigurationRequest.builder().applyMutation(deleteBucketMetricsConfigurationRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketOwnershipControlsResponse> deleteBucketOwnershipControls(DeleteBucketOwnershipControlsRequest deleteBucketOwnershipControlsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketOwnershipControlsResponse> deleteBucketOwnershipControls(Consumer<DeleteBucketOwnershipControlsRequest.Builder> deleteBucketOwnershipControlsRequest) {
        return this.deleteBucketOwnershipControls((DeleteBucketOwnershipControlsRequest)((Object)((DeleteBucketOwnershipControlsRequest.Builder)DeleteBucketOwnershipControlsRequest.builder().applyMutation(deleteBucketOwnershipControlsRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketPolicyResponse> deleteBucketPolicy(DeleteBucketPolicyRequest deleteBucketPolicyRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketPolicyResponse> deleteBucketPolicy(Consumer<DeleteBucketPolicyRequest.Builder> deleteBucketPolicyRequest) {
        return this.deleteBucketPolicy((DeleteBucketPolicyRequest)((Object)((DeleteBucketPolicyRequest.Builder)DeleteBucketPolicyRequest.builder().applyMutation(deleteBucketPolicyRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketReplicationResponse> deleteBucketReplication(DeleteBucketReplicationRequest deleteBucketReplicationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketReplicationResponse> deleteBucketReplication(Consumer<DeleteBucketReplicationRequest.Builder> deleteBucketReplicationRequest) {
        return this.deleteBucketReplication((DeleteBucketReplicationRequest)((Object)((DeleteBucketReplicationRequest.Builder)DeleteBucketReplicationRequest.builder().applyMutation(deleteBucketReplicationRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketTaggingResponse> deleteBucketTagging(DeleteBucketTaggingRequest deleteBucketTaggingRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketTaggingResponse> deleteBucketTagging(Consumer<DeleteBucketTaggingRequest.Builder> deleteBucketTaggingRequest) {
        return this.deleteBucketTagging((DeleteBucketTaggingRequest)((Object)((DeleteBucketTaggingRequest.Builder)DeleteBucketTaggingRequest.builder().applyMutation(deleteBucketTaggingRequest)).build()));
    }

    default public CompletableFuture<DeleteBucketWebsiteResponse> deleteBucketWebsite(DeleteBucketWebsiteRequest deleteBucketWebsiteRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteBucketWebsiteResponse> deleteBucketWebsite(Consumer<DeleteBucketWebsiteRequest.Builder> deleteBucketWebsiteRequest) {
        return this.deleteBucketWebsite((DeleteBucketWebsiteRequest)((Object)((DeleteBucketWebsiteRequest.Builder)DeleteBucketWebsiteRequest.builder().applyMutation(deleteBucketWebsiteRequest)).build()));
    }

    default public CompletableFuture<DeleteObjectResponse> deleteObject(DeleteObjectRequest deleteObjectRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteObjectResponse> deleteObject(Consumer<DeleteObjectRequest.Builder> deleteObjectRequest) {
        return this.deleteObject((DeleteObjectRequest)((Object)((DeleteObjectRequest.Builder)DeleteObjectRequest.builder().applyMutation(deleteObjectRequest)).build()));
    }

    default public CompletableFuture<DeleteObjectTaggingResponse> deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteObjectTaggingResponse> deleteObjectTagging(Consumer<DeleteObjectTaggingRequest.Builder> deleteObjectTaggingRequest) {
        return this.deleteObjectTagging((DeleteObjectTaggingRequest)((Object)((DeleteObjectTaggingRequest.Builder)DeleteObjectTaggingRequest.builder().applyMutation(deleteObjectTaggingRequest)).build()));
    }

    default public CompletableFuture<DeleteObjectsResponse> deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteObjectsResponse> deleteObjects(Consumer<DeleteObjectsRequest.Builder> deleteObjectsRequest) {
        return this.deleteObjects((DeleteObjectsRequest)((Object)((DeleteObjectsRequest.Builder)DeleteObjectsRequest.builder().applyMutation(deleteObjectsRequest)).build()));
    }

    default public CompletableFuture<DeletePublicAccessBlockResponse> deletePublicAccessBlock(DeletePublicAccessBlockRequest deletePublicAccessBlockRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeletePublicAccessBlockResponse> deletePublicAccessBlock(Consumer<DeletePublicAccessBlockRequest.Builder> deletePublicAccessBlockRequest) {
        return this.deletePublicAccessBlock((DeletePublicAccessBlockRequest)((Object)((DeletePublicAccessBlockRequest.Builder)DeletePublicAccessBlockRequest.builder().applyMutation(deletePublicAccessBlockRequest)).build()));
    }

    default public CompletableFuture<GetBucketAccelerateConfigurationResponse> getBucketAccelerateConfiguration(GetBucketAccelerateConfigurationRequest getBucketAccelerateConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketAccelerateConfigurationResponse> getBucketAccelerateConfiguration(Consumer<GetBucketAccelerateConfigurationRequest.Builder> getBucketAccelerateConfigurationRequest) {
        return this.getBucketAccelerateConfiguration((GetBucketAccelerateConfigurationRequest)((Object)((GetBucketAccelerateConfigurationRequest.Builder)GetBucketAccelerateConfigurationRequest.builder().applyMutation(getBucketAccelerateConfigurationRequest)).build()));
    }

    default public CompletableFuture<GetBucketAclResponse> getBucketAcl(GetBucketAclRequest getBucketAclRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketAclResponse> getBucketAcl(Consumer<GetBucketAclRequest.Builder> getBucketAclRequest) {
        return this.getBucketAcl((GetBucketAclRequest)((Object)((GetBucketAclRequest.Builder)GetBucketAclRequest.builder().applyMutation(getBucketAclRequest)).build()));
    }

    default public CompletableFuture<GetBucketAnalyticsConfigurationResponse> getBucketAnalyticsConfiguration(GetBucketAnalyticsConfigurationRequest getBucketAnalyticsConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketAnalyticsConfigurationResponse> getBucketAnalyticsConfiguration(Consumer<GetBucketAnalyticsConfigurationRequest.Builder> getBucketAnalyticsConfigurationRequest) {
        return this.getBucketAnalyticsConfiguration((GetBucketAnalyticsConfigurationRequest)((Object)((GetBucketAnalyticsConfigurationRequest.Builder)GetBucketAnalyticsConfigurationRequest.builder().applyMutation(getBucketAnalyticsConfigurationRequest)).build()));
    }

    default public CompletableFuture<GetBucketCorsResponse> getBucketCors(GetBucketCorsRequest getBucketCorsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketCorsResponse> getBucketCors(Consumer<GetBucketCorsRequest.Builder> getBucketCorsRequest) {
        return this.getBucketCors((GetBucketCorsRequest)((Object)((GetBucketCorsRequest.Builder)GetBucketCorsRequest.builder().applyMutation(getBucketCorsRequest)).build()));
    }

    default public CompletableFuture<GetBucketEncryptionResponse> getBucketEncryption(GetBucketEncryptionRequest getBucketEncryptionRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketEncryptionResponse> getBucketEncryption(Consumer<GetBucketEncryptionRequest.Builder> getBucketEncryptionRequest) {
        return this.getBucketEncryption((GetBucketEncryptionRequest)((Object)((GetBucketEncryptionRequest.Builder)GetBucketEncryptionRequest.builder().applyMutation(getBucketEncryptionRequest)).build()));
    }

    default public CompletableFuture<GetBucketIntelligentTieringConfigurationResponse> getBucketIntelligentTieringConfiguration(GetBucketIntelligentTieringConfigurationRequest getBucketIntelligentTieringConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketIntelligentTieringConfigurationResponse> getBucketIntelligentTieringConfiguration(Consumer<GetBucketIntelligentTieringConfigurationRequest.Builder> getBucketIntelligentTieringConfigurationRequest) {
        return this.getBucketIntelligentTieringConfiguration((GetBucketIntelligentTieringConfigurationRequest)((Object)((GetBucketIntelligentTieringConfigurationRequest.Builder)GetBucketIntelligentTieringConfigurationRequest.builder().applyMutation(getBucketIntelligentTieringConfigurationRequest)).build()));
    }

    default public CompletableFuture<GetBucketInventoryConfigurationResponse> getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest getBucketInventoryConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketInventoryConfigurationResponse> getBucketInventoryConfiguration(Consumer<GetBucketInventoryConfigurationRequest.Builder> getBucketInventoryConfigurationRequest) {
        return this.getBucketInventoryConfiguration((GetBucketInventoryConfigurationRequest)((Object)((GetBucketInventoryConfigurationRequest.Builder)GetBucketInventoryConfigurationRequest.builder().applyMutation(getBucketInventoryConfigurationRequest)).build()));
    }

    default public CompletableFuture<GetBucketLifecycleConfigurationResponse> getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketLifecycleConfigurationResponse> getBucketLifecycleConfiguration(Consumer<GetBucketLifecycleConfigurationRequest.Builder> getBucketLifecycleConfigurationRequest) {
        return this.getBucketLifecycleConfiguration((GetBucketLifecycleConfigurationRequest)((Object)((GetBucketLifecycleConfigurationRequest.Builder)GetBucketLifecycleConfigurationRequest.builder().applyMutation(getBucketLifecycleConfigurationRequest)).build()));
    }

    default public CompletableFuture<GetBucketLocationResponse> getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketLocationResponse> getBucketLocation(Consumer<GetBucketLocationRequest.Builder> getBucketLocationRequest) {
        return this.getBucketLocation((GetBucketLocationRequest)((Object)((GetBucketLocationRequest.Builder)GetBucketLocationRequest.builder().applyMutation(getBucketLocationRequest)).build()));
    }

    default public CompletableFuture<GetBucketLoggingResponse> getBucketLogging(GetBucketLoggingRequest getBucketLoggingRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketLoggingResponse> getBucketLogging(Consumer<GetBucketLoggingRequest.Builder> getBucketLoggingRequest) {
        return this.getBucketLogging((GetBucketLoggingRequest)((Object)((GetBucketLoggingRequest.Builder)GetBucketLoggingRequest.builder().applyMutation(getBucketLoggingRequest)).build()));
    }

    default public CompletableFuture<GetBucketMetricsConfigurationResponse> getBucketMetricsConfiguration(GetBucketMetricsConfigurationRequest getBucketMetricsConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketMetricsConfigurationResponse> getBucketMetricsConfiguration(Consumer<GetBucketMetricsConfigurationRequest.Builder> getBucketMetricsConfigurationRequest) {
        return this.getBucketMetricsConfiguration((GetBucketMetricsConfigurationRequest)((Object)((GetBucketMetricsConfigurationRequest.Builder)GetBucketMetricsConfigurationRequest.builder().applyMutation(getBucketMetricsConfigurationRequest)).build()));
    }

    default public CompletableFuture<GetBucketNotificationConfigurationResponse> getBucketNotificationConfiguration(GetBucketNotificationConfigurationRequest getBucketNotificationConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketNotificationConfigurationResponse> getBucketNotificationConfiguration(Consumer<GetBucketNotificationConfigurationRequest.Builder> getBucketNotificationConfigurationRequest) {
        return this.getBucketNotificationConfiguration((GetBucketNotificationConfigurationRequest)((Object)((GetBucketNotificationConfigurationRequest.Builder)GetBucketNotificationConfigurationRequest.builder().applyMutation(getBucketNotificationConfigurationRequest)).build()));
    }

    default public CompletableFuture<GetBucketOwnershipControlsResponse> getBucketOwnershipControls(GetBucketOwnershipControlsRequest getBucketOwnershipControlsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketOwnershipControlsResponse> getBucketOwnershipControls(Consumer<GetBucketOwnershipControlsRequest.Builder> getBucketOwnershipControlsRequest) {
        return this.getBucketOwnershipControls((GetBucketOwnershipControlsRequest)((Object)((GetBucketOwnershipControlsRequest.Builder)GetBucketOwnershipControlsRequest.builder().applyMutation(getBucketOwnershipControlsRequest)).build()));
    }

    default public CompletableFuture<GetBucketPolicyResponse> getBucketPolicy(GetBucketPolicyRequest getBucketPolicyRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketPolicyResponse> getBucketPolicy(Consumer<GetBucketPolicyRequest.Builder> getBucketPolicyRequest) {
        return this.getBucketPolicy((GetBucketPolicyRequest)((Object)((GetBucketPolicyRequest.Builder)GetBucketPolicyRequest.builder().applyMutation(getBucketPolicyRequest)).build()));
    }

    default public CompletableFuture<GetBucketPolicyStatusResponse> getBucketPolicyStatus(GetBucketPolicyStatusRequest getBucketPolicyStatusRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketPolicyStatusResponse> getBucketPolicyStatus(Consumer<GetBucketPolicyStatusRequest.Builder> getBucketPolicyStatusRequest) {
        return this.getBucketPolicyStatus((GetBucketPolicyStatusRequest)((Object)((GetBucketPolicyStatusRequest.Builder)GetBucketPolicyStatusRequest.builder().applyMutation(getBucketPolicyStatusRequest)).build()));
    }

    default public CompletableFuture<GetBucketReplicationResponse> getBucketReplication(GetBucketReplicationRequest getBucketReplicationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketReplicationResponse> getBucketReplication(Consumer<GetBucketReplicationRequest.Builder> getBucketReplicationRequest) {
        return this.getBucketReplication((GetBucketReplicationRequest)((Object)((GetBucketReplicationRequest.Builder)GetBucketReplicationRequest.builder().applyMutation(getBucketReplicationRequest)).build()));
    }

    default public CompletableFuture<GetBucketRequestPaymentResponse> getBucketRequestPayment(GetBucketRequestPaymentRequest getBucketRequestPaymentRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketRequestPaymentResponse> getBucketRequestPayment(Consumer<GetBucketRequestPaymentRequest.Builder> getBucketRequestPaymentRequest) {
        return this.getBucketRequestPayment((GetBucketRequestPaymentRequest)((Object)((GetBucketRequestPaymentRequest.Builder)GetBucketRequestPaymentRequest.builder().applyMutation(getBucketRequestPaymentRequest)).build()));
    }

    default public CompletableFuture<GetBucketTaggingResponse> getBucketTagging(GetBucketTaggingRequest getBucketTaggingRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketTaggingResponse> getBucketTagging(Consumer<GetBucketTaggingRequest.Builder> getBucketTaggingRequest) {
        return this.getBucketTagging((GetBucketTaggingRequest)((Object)((GetBucketTaggingRequest.Builder)GetBucketTaggingRequest.builder().applyMutation(getBucketTaggingRequest)).build()));
    }

    default public CompletableFuture<GetBucketVersioningResponse> getBucketVersioning(GetBucketVersioningRequest getBucketVersioningRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketVersioningResponse> getBucketVersioning(Consumer<GetBucketVersioningRequest.Builder> getBucketVersioningRequest) {
        return this.getBucketVersioning((GetBucketVersioningRequest)((Object)((GetBucketVersioningRequest.Builder)GetBucketVersioningRequest.builder().applyMutation(getBucketVersioningRequest)).build()));
    }

    default public CompletableFuture<GetBucketWebsiteResponse> getBucketWebsite(GetBucketWebsiteRequest getBucketWebsiteRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetBucketWebsiteResponse> getBucketWebsite(Consumer<GetBucketWebsiteRequest.Builder> getBucketWebsiteRequest) {
        return this.getBucketWebsite((GetBucketWebsiteRequest)((Object)((GetBucketWebsiteRequest.Builder)GetBucketWebsiteRequest.builder().applyMutation(getBucketWebsiteRequest)).build()));
    }

    default public <ReturnT> CompletableFuture<ReturnT> getObject(GetObjectRequest getObjectRequest, AsyncResponseTransformer<GetObjectResponse, ReturnT> asyncResponseTransformer) {
        throw new UnsupportedOperationException();
    }

    default public <ReturnT> CompletableFuture<ReturnT> getObject(Consumer<GetObjectRequest.Builder> getObjectRequest, AsyncResponseTransformer<GetObjectResponse, ReturnT> asyncResponseTransformer) {
        return this.getObject((GetObjectRequest)((Object)((GetObjectRequest.Builder)GetObjectRequest.builder().applyMutation(getObjectRequest)).build()), asyncResponseTransformer);
    }

    default public CompletableFuture<GetObjectResponse> getObject(GetObjectRequest getObjectRequest, Path destinationPath) {
        return this.getObject(getObjectRequest, AsyncResponseTransformer.toFile((Path)destinationPath));
    }

    default public CompletableFuture<GetObjectResponse> getObject(Consumer<GetObjectRequest.Builder> getObjectRequest, Path destinationPath) {
        return this.getObject((GetObjectRequest)((Object)((GetObjectRequest.Builder)GetObjectRequest.builder().applyMutation(getObjectRequest)).build()), destinationPath);
    }

    default public CompletableFuture<GetObjectAclResponse> getObjectAcl(GetObjectAclRequest getObjectAclRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetObjectAclResponse> getObjectAcl(Consumer<GetObjectAclRequest.Builder> getObjectAclRequest) {
        return this.getObjectAcl((GetObjectAclRequest)((Object)((GetObjectAclRequest.Builder)GetObjectAclRequest.builder().applyMutation(getObjectAclRequest)).build()));
    }

    default public CompletableFuture<GetObjectAttributesResponse> getObjectAttributes(GetObjectAttributesRequest getObjectAttributesRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetObjectAttributesResponse> getObjectAttributes(Consumer<GetObjectAttributesRequest.Builder> getObjectAttributesRequest) {
        return this.getObjectAttributes((GetObjectAttributesRequest)((Object)((GetObjectAttributesRequest.Builder)GetObjectAttributesRequest.builder().applyMutation(getObjectAttributesRequest)).build()));
    }

    default public CompletableFuture<GetObjectLegalHoldResponse> getObjectLegalHold(GetObjectLegalHoldRequest getObjectLegalHoldRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetObjectLegalHoldResponse> getObjectLegalHold(Consumer<GetObjectLegalHoldRequest.Builder> getObjectLegalHoldRequest) {
        return this.getObjectLegalHold((GetObjectLegalHoldRequest)((Object)((GetObjectLegalHoldRequest.Builder)GetObjectLegalHoldRequest.builder().applyMutation(getObjectLegalHoldRequest)).build()));
    }

    default public CompletableFuture<GetObjectLockConfigurationResponse> getObjectLockConfiguration(GetObjectLockConfigurationRequest getObjectLockConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetObjectLockConfigurationResponse> getObjectLockConfiguration(Consumer<GetObjectLockConfigurationRequest.Builder> getObjectLockConfigurationRequest) {
        return this.getObjectLockConfiguration((GetObjectLockConfigurationRequest)((Object)((GetObjectLockConfigurationRequest.Builder)GetObjectLockConfigurationRequest.builder().applyMutation(getObjectLockConfigurationRequest)).build()));
    }

    default public CompletableFuture<GetObjectRetentionResponse> getObjectRetention(GetObjectRetentionRequest getObjectRetentionRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetObjectRetentionResponse> getObjectRetention(Consumer<GetObjectRetentionRequest.Builder> getObjectRetentionRequest) {
        return this.getObjectRetention((GetObjectRetentionRequest)((Object)((GetObjectRetentionRequest.Builder)GetObjectRetentionRequest.builder().applyMutation(getObjectRetentionRequest)).build()));
    }

    default public CompletableFuture<GetObjectTaggingResponse> getObjectTagging(GetObjectTaggingRequest getObjectTaggingRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetObjectTaggingResponse> getObjectTagging(Consumer<GetObjectTaggingRequest.Builder> getObjectTaggingRequest) {
        return this.getObjectTagging((GetObjectTaggingRequest)((Object)((GetObjectTaggingRequest.Builder)GetObjectTaggingRequest.builder().applyMutation(getObjectTaggingRequest)).build()));
    }

    default public <ReturnT> CompletableFuture<ReturnT> getObjectTorrent(GetObjectTorrentRequest getObjectTorrentRequest, AsyncResponseTransformer<GetObjectTorrentResponse, ReturnT> asyncResponseTransformer) {
        throw new UnsupportedOperationException();
    }

    default public <ReturnT> CompletableFuture<ReturnT> getObjectTorrent(Consumer<GetObjectTorrentRequest.Builder> getObjectTorrentRequest, AsyncResponseTransformer<GetObjectTorrentResponse, ReturnT> asyncResponseTransformer) {
        return this.getObjectTorrent((GetObjectTorrentRequest)((Object)((GetObjectTorrentRequest.Builder)GetObjectTorrentRequest.builder().applyMutation(getObjectTorrentRequest)).build()), asyncResponseTransformer);
    }

    default public CompletableFuture<GetObjectTorrentResponse> getObjectTorrent(GetObjectTorrentRequest getObjectTorrentRequest, Path destinationPath) {
        return this.getObjectTorrent(getObjectTorrentRequest, AsyncResponseTransformer.toFile((Path)destinationPath));
    }

    default public CompletableFuture<GetObjectTorrentResponse> getObjectTorrent(Consumer<GetObjectTorrentRequest.Builder> getObjectTorrentRequest, Path destinationPath) {
        return this.getObjectTorrent((GetObjectTorrentRequest)((Object)((GetObjectTorrentRequest.Builder)GetObjectTorrentRequest.builder().applyMutation(getObjectTorrentRequest)).build()), destinationPath);
    }

    default public CompletableFuture<GetPublicAccessBlockResponse> getPublicAccessBlock(GetPublicAccessBlockRequest getPublicAccessBlockRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetPublicAccessBlockResponse> getPublicAccessBlock(Consumer<GetPublicAccessBlockRequest.Builder> getPublicAccessBlockRequest) {
        return this.getPublicAccessBlock((GetPublicAccessBlockRequest)((Object)((GetPublicAccessBlockRequest.Builder)GetPublicAccessBlockRequest.builder().applyMutation(getPublicAccessBlockRequest)).build()));
    }

    default public CompletableFuture<HeadBucketResponse> headBucket(HeadBucketRequest headBucketRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<HeadBucketResponse> headBucket(Consumer<HeadBucketRequest.Builder> headBucketRequest) {
        return this.headBucket((HeadBucketRequest)((Object)((HeadBucketRequest.Builder)HeadBucketRequest.builder().applyMutation(headBucketRequest)).build()));
    }

    default public CompletableFuture<HeadObjectResponse> headObject(HeadObjectRequest headObjectRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<HeadObjectResponse> headObject(Consumer<HeadObjectRequest.Builder> headObjectRequest) {
        return this.headObject((HeadObjectRequest)((Object)((HeadObjectRequest.Builder)HeadObjectRequest.builder().applyMutation(headObjectRequest)).build()));
    }

    default public CompletableFuture<ListBucketAnalyticsConfigurationsResponse> listBucketAnalyticsConfigurations(ListBucketAnalyticsConfigurationsRequest listBucketAnalyticsConfigurationsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListBucketAnalyticsConfigurationsResponse> listBucketAnalyticsConfigurations(Consumer<ListBucketAnalyticsConfigurationsRequest.Builder> listBucketAnalyticsConfigurationsRequest) {
        return this.listBucketAnalyticsConfigurations((ListBucketAnalyticsConfigurationsRequest)((Object)((ListBucketAnalyticsConfigurationsRequest.Builder)ListBucketAnalyticsConfigurationsRequest.builder().applyMutation(listBucketAnalyticsConfigurationsRequest)).build()));
    }

    default public CompletableFuture<ListBucketIntelligentTieringConfigurationsResponse> listBucketIntelligentTieringConfigurations(ListBucketIntelligentTieringConfigurationsRequest listBucketIntelligentTieringConfigurationsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListBucketIntelligentTieringConfigurationsResponse> listBucketIntelligentTieringConfigurations(Consumer<ListBucketIntelligentTieringConfigurationsRequest.Builder> listBucketIntelligentTieringConfigurationsRequest) {
        return this.listBucketIntelligentTieringConfigurations((ListBucketIntelligentTieringConfigurationsRequest)((Object)((ListBucketIntelligentTieringConfigurationsRequest.Builder)ListBucketIntelligentTieringConfigurationsRequest.builder().applyMutation(listBucketIntelligentTieringConfigurationsRequest)).build()));
    }

    default public CompletableFuture<ListBucketInventoryConfigurationsResponse> listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest listBucketInventoryConfigurationsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListBucketInventoryConfigurationsResponse> listBucketInventoryConfigurations(Consumer<ListBucketInventoryConfigurationsRequest.Builder> listBucketInventoryConfigurationsRequest) {
        return this.listBucketInventoryConfigurations((ListBucketInventoryConfigurationsRequest)((Object)((ListBucketInventoryConfigurationsRequest.Builder)ListBucketInventoryConfigurationsRequest.builder().applyMutation(listBucketInventoryConfigurationsRequest)).build()));
    }

    default public CompletableFuture<ListBucketMetricsConfigurationsResponse> listBucketMetricsConfigurations(ListBucketMetricsConfigurationsRequest listBucketMetricsConfigurationsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListBucketMetricsConfigurationsResponse> listBucketMetricsConfigurations(Consumer<ListBucketMetricsConfigurationsRequest.Builder> listBucketMetricsConfigurationsRequest) {
        return this.listBucketMetricsConfigurations((ListBucketMetricsConfigurationsRequest)((Object)((ListBucketMetricsConfigurationsRequest.Builder)ListBucketMetricsConfigurationsRequest.builder().applyMutation(listBucketMetricsConfigurationsRequest)).build()));
    }

    default public CompletableFuture<ListBucketsResponse> listBuckets(ListBucketsRequest listBucketsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListBucketsResponse> listBuckets(Consumer<ListBucketsRequest.Builder> listBucketsRequest) {
        return this.listBuckets((ListBucketsRequest)((Object)((ListBucketsRequest.Builder)ListBucketsRequest.builder().applyMutation(listBucketsRequest)).build()));
    }

    default public CompletableFuture<ListBucketsResponse> listBuckets() {
        return this.listBuckets((ListBucketsRequest)((Object)ListBucketsRequest.builder().build()));
    }

    default public CompletableFuture<ListMultipartUploadsResponse> listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListMultipartUploadsResponse> listMultipartUploads(Consumer<ListMultipartUploadsRequest.Builder> listMultipartUploadsRequest) {
        return this.listMultipartUploads((ListMultipartUploadsRequest)((Object)((ListMultipartUploadsRequest.Builder)ListMultipartUploadsRequest.builder().applyMutation(listMultipartUploadsRequest)).build()));
    }

    default public ListMultipartUploadsPublisher listMultipartUploadsPaginator(ListMultipartUploadsRequest listMultipartUploadsRequest) {
        return new ListMultipartUploadsPublisher(this, listMultipartUploadsRequest);
    }

    default public ListMultipartUploadsPublisher listMultipartUploadsPaginator(Consumer<ListMultipartUploadsRequest.Builder> listMultipartUploadsRequest) {
        return this.listMultipartUploadsPaginator((ListMultipartUploadsRequest)((Object)((ListMultipartUploadsRequest.Builder)ListMultipartUploadsRequest.builder().applyMutation(listMultipartUploadsRequest)).build()));
    }

    default public CompletableFuture<ListObjectVersionsResponse> listObjectVersions(ListObjectVersionsRequest listObjectVersionsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListObjectVersionsResponse> listObjectVersions(Consumer<ListObjectVersionsRequest.Builder> listObjectVersionsRequest) {
        return this.listObjectVersions((ListObjectVersionsRequest)((Object)((ListObjectVersionsRequest.Builder)ListObjectVersionsRequest.builder().applyMutation(listObjectVersionsRequest)).build()));
    }

    default public ListObjectVersionsPublisher listObjectVersionsPaginator(ListObjectVersionsRequest listObjectVersionsRequest) {
        return new ListObjectVersionsPublisher(this, listObjectVersionsRequest);
    }

    default public ListObjectVersionsPublisher listObjectVersionsPaginator(Consumer<ListObjectVersionsRequest.Builder> listObjectVersionsRequest) {
        return this.listObjectVersionsPaginator((ListObjectVersionsRequest)((Object)((ListObjectVersionsRequest.Builder)ListObjectVersionsRequest.builder().applyMutation(listObjectVersionsRequest)).build()));
    }

    default public CompletableFuture<ListObjectsResponse> listObjects(ListObjectsRequest listObjectsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListObjectsResponse> listObjects(Consumer<ListObjectsRequest.Builder> listObjectsRequest) {
        return this.listObjects((ListObjectsRequest)((Object)((ListObjectsRequest.Builder)ListObjectsRequest.builder().applyMutation(listObjectsRequest)).build()));
    }

    default public CompletableFuture<ListObjectsV2Response> listObjectsV2(ListObjectsV2Request listObjectsV2Request) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListObjectsV2Response> listObjectsV2(Consumer<ListObjectsV2Request.Builder> listObjectsV2Request) {
        return this.listObjectsV2((ListObjectsV2Request)((Object)((ListObjectsV2Request.Builder)ListObjectsV2Request.builder().applyMutation(listObjectsV2Request)).build()));
    }

    default public ListObjectsV2Publisher listObjectsV2Paginator(ListObjectsV2Request listObjectsV2Request) {
        return new ListObjectsV2Publisher(this, listObjectsV2Request);
    }

    default public ListObjectsV2Publisher listObjectsV2Paginator(Consumer<ListObjectsV2Request.Builder> listObjectsV2Request) {
        return this.listObjectsV2Paginator((ListObjectsV2Request)((Object)((ListObjectsV2Request.Builder)ListObjectsV2Request.builder().applyMutation(listObjectsV2Request)).build()));
    }

    default public CompletableFuture<ListPartsResponse> listParts(ListPartsRequest listPartsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListPartsResponse> listParts(Consumer<ListPartsRequest.Builder> listPartsRequest) {
        return this.listParts((ListPartsRequest)((Object)((ListPartsRequest.Builder)ListPartsRequest.builder().applyMutation(listPartsRequest)).build()));
    }

    default public ListPartsPublisher listPartsPaginator(ListPartsRequest listPartsRequest) {
        return new ListPartsPublisher(this, listPartsRequest);
    }

    default public ListPartsPublisher listPartsPaginator(Consumer<ListPartsRequest.Builder> listPartsRequest) {
        return this.listPartsPaginator((ListPartsRequest)((Object)((ListPartsRequest.Builder)ListPartsRequest.builder().applyMutation(listPartsRequest)).build()));
    }

    default public CompletableFuture<PutBucketAccelerateConfigurationResponse> putBucketAccelerateConfiguration(PutBucketAccelerateConfigurationRequest putBucketAccelerateConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketAccelerateConfigurationResponse> putBucketAccelerateConfiguration(Consumer<PutBucketAccelerateConfigurationRequest.Builder> putBucketAccelerateConfigurationRequest) {
        return this.putBucketAccelerateConfiguration((PutBucketAccelerateConfigurationRequest)((Object)((PutBucketAccelerateConfigurationRequest.Builder)PutBucketAccelerateConfigurationRequest.builder().applyMutation(putBucketAccelerateConfigurationRequest)).build()));
    }

    default public CompletableFuture<PutBucketAclResponse> putBucketAcl(PutBucketAclRequest putBucketAclRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketAclResponse> putBucketAcl(Consumer<PutBucketAclRequest.Builder> putBucketAclRequest) {
        return this.putBucketAcl((PutBucketAclRequest)((Object)((PutBucketAclRequest.Builder)PutBucketAclRequest.builder().applyMutation(putBucketAclRequest)).build()));
    }

    default public CompletableFuture<PutBucketAnalyticsConfigurationResponse> putBucketAnalyticsConfiguration(PutBucketAnalyticsConfigurationRequest putBucketAnalyticsConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketAnalyticsConfigurationResponse> putBucketAnalyticsConfiguration(Consumer<PutBucketAnalyticsConfigurationRequest.Builder> putBucketAnalyticsConfigurationRequest) {
        return this.putBucketAnalyticsConfiguration((PutBucketAnalyticsConfigurationRequest)((Object)((PutBucketAnalyticsConfigurationRequest.Builder)PutBucketAnalyticsConfigurationRequest.builder().applyMutation(putBucketAnalyticsConfigurationRequest)).build()));
    }

    default public CompletableFuture<PutBucketCorsResponse> putBucketCors(PutBucketCorsRequest putBucketCorsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketCorsResponse> putBucketCors(Consumer<PutBucketCorsRequest.Builder> putBucketCorsRequest) {
        return this.putBucketCors((PutBucketCorsRequest)((Object)((PutBucketCorsRequest.Builder)PutBucketCorsRequest.builder().applyMutation(putBucketCorsRequest)).build()));
    }

    default public CompletableFuture<PutBucketEncryptionResponse> putBucketEncryption(PutBucketEncryptionRequest putBucketEncryptionRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketEncryptionResponse> putBucketEncryption(Consumer<PutBucketEncryptionRequest.Builder> putBucketEncryptionRequest) {
        return this.putBucketEncryption((PutBucketEncryptionRequest)((Object)((PutBucketEncryptionRequest.Builder)PutBucketEncryptionRequest.builder().applyMutation(putBucketEncryptionRequest)).build()));
    }

    default public CompletableFuture<PutBucketIntelligentTieringConfigurationResponse> putBucketIntelligentTieringConfiguration(PutBucketIntelligentTieringConfigurationRequest putBucketIntelligentTieringConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketIntelligentTieringConfigurationResponse> putBucketIntelligentTieringConfiguration(Consumer<PutBucketIntelligentTieringConfigurationRequest.Builder> putBucketIntelligentTieringConfigurationRequest) {
        return this.putBucketIntelligentTieringConfiguration((PutBucketIntelligentTieringConfigurationRequest)((Object)((PutBucketIntelligentTieringConfigurationRequest.Builder)PutBucketIntelligentTieringConfigurationRequest.builder().applyMutation(putBucketIntelligentTieringConfigurationRequest)).build()));
    }

    default public CompletableFuture<PutBucketInventoryConfigurationResponse> putBucketInventoryConfiguration(PutBucketInventoryConfigurationRequest putBucketInventoryConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketInventoryConfigurationResponse> putBucketInventoryConfiguration(Consumer<PutBucketInventoryConfigurationRequest.Builder> putBucketInventoryConfigurationRequest) {
        return this.putBucketInventoryConfiguration((PutBucketInventoryConfigurationRequest)((Object)((PutBucketInventoryConfigurationRequest.Builder)PutBucketInventoryConfigurationRequest.builder().applyMutation(putBucketInventoryConfigurationRequest)).build()));
    }

    default public CompletableFuture<PutBucketLifecycleConfigurationResponse> putBucketLifecycleConfiguration(PutBucketLifecycleConfigurationRequest putBucketLifecycleConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketLifecycleConfigurationResponse> putBucketLifecycleConfiguration(Consumer<PutBucketLifecycleConfigurationRequest.Builder> putBucketLifecycleConfigurationRequest) {
        return this.putBucketLifecycleConfiguration((PutBucketLifecycleConfigurationRequest)((Object)((PutBucketLifecycleConfigurationRequest.Builder)PutBucketLifecycleConfigurationRequest.builder().applyMutation(putBucketLifecycleConfigurationRequest)).build()));
    }

    default public CompletableFuture<PutBucketLoggingResponse> putBucketLogging(PutBucketLoggingRequest putBucketLoggingRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketLoggingResponse> putBucketLogging(Consumer<PutBucketLoggingRequest.Builder> putBucketLoggingRequest) {
        return this.putBucketLogging((PutBucketLoggingRequest)((Object)((PutBucketLoggingRequest.Builder)PutBucketLoggingRequest.builder().applyMutation(putBucketLoggingRequest)).build()));
    }

    default public CompletableFuture<PutBucketMetricsConfigurationResponse> putBucketMetricsConfiguration(PutBucketMetricsConfigurationRequest putBucketMetricsConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketMetricsConfigurationResponse> putBucketMetricsConfiguration(Consumer<PutBucketMetricsConfigurationRequest.Builder> putBucketMetricsConfigurationRequest) {
        return this.putBucketMetricsConfiguration((PutBucketMetricsConfigurationRequest)((Object)((PutBucketMetricsConfigurationRequest.Builder)PutBucketMetricsConfigurationRequest.builder().applyMutation(putBucketMetricsConfigurationRequest)).build()));
    }

    default public CompletableFuture<PutBucketNotificationConfigurationResponse> putBucketNotificationConfiguration(PutBucketNotificationConfigurationRequest putBucketNotificationConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketNotificationConfigurationResponse> putBucketNotificationConfiguration(Consumer<PutBucketNotificationConfigurationRequest.Builder> putBucketNotificationConfigurationRequest) {
        return this.putBucketNotificationConfiguration((PutBucketNotificationConfigurationRequest)((Object)((PutBucketNotificationConfigurationRequest.Builder)PutBucketNotificationConfigurationRequest.builder().applyMutation(putBucketNotificationConfigurationRequest)).build()));
    }

    default public CompletableFuture<PutBucketOwnershipControlsResponse> putBucketOwnershipControls(PutBucketOwnershipControlsRequest putBucketOwnershipControlsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketOwnershipControlsResponse> putBucketOwnershipControls(Consumer<PutBucketOwnershipControlsRequest.Builder> putBucketOwnershipControlsRequest) {
        return this.putBucketOwnershipControls((PutBucketOwnershipControlsRequest)((Object)((PutBucketOwnershipControlsRequest.Builder)PutBucketOwnershipControlsRequest.builder().applyMutation(putBucketOwnershipControlsRequest)).build()));
    }

    default public CompletableFuture<PutBucketPolicyResponse> putBucketPolicy(PutBucketPolicyRequest putBucketPolicyRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketPolicyResponse> putBucketPolicy(Consumer<PutBucketPolicyRequest.Builder> putBucketPolicyRequest) {
        return this.putBucketPolicy((PutBucketPolicyRequest)((Object)((PutBucketPolicyRequest.Builder)PutBucketPolicyRequest.builder().applyMutation(putBucketPolicyRequest)).build()));
    }

    default public CompletableFuture<PutBucketReplicationResponse> putBucketReplication(PutBucketReplicationRequest putBucketReplicationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketReplicationResponse> putBucketReplication(Consumer<PutBucketReplicationRequest.Builder> putBucketReplicationRequest) {
        return this.putBucketReplication((PutBucketReplicationRequest)((Object)((PutBucketReplicationRequest.Builder)PutBucketReplicationRequest.builder().applyMutation(putBucketReplicationRequest)).build()));
    }

    default public CompletableFuture<PutBucketRequestPaymentResponse> putBucketRequestPayment(PutBucketRequestPaymentRequest putBucketRequestPaymentRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketRequestPaymentResponse> putBucketRequestPayment(Consumer<PutBucketRequestPaymentRequest.Builder> putBucketRequestPaymentRequest) {
        return this.putBucketRequestPayment((PutBucketRequestPaymentRequest)((Object)((PutBucketRequestPaymentRequest.Builder)PutBucketRequestPaymentRequest.builder().applyMutation(putBucketRequestPaymentRequest)).build()));
    }

    default public CompletableFuture<PutBucketTaggingResponse> putBucketTagging(PutBucketTaggingRequest putBucketTaggingRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketTaggingResponse> putBucketTagging(Consumer<PutBucketTaggingRequest.Builder> putBucketTaggingRequest) {
        return this.putBucketTagging((PutBucketTaggingRequest)((Object)((PutBucketTaggingRequest.Builder)PutBucketTaggingRequest.builder().applyMutation(putBucketTaggingRequest)).build()));
    }

    default public CompletableFuture<PutBucketVersioningResponse> putBucketVersioning(PutBucketVersioningRequest putBucketVersioningRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketVersioningResponse> putBucketVersioning(Consumer<PutBucketVersioningRequest.Builder> putBucketVersioningRequest) {
        return this.putBucketVersioning((PutBucketVersioningRequest)((Object)((PutBucketVersioningRequest.Builder)PutBucketVersioningRequest.builder().applyMutation(putBucketVersioningRequest)).build()));
    }

    default public CompletableFuture<PutBucketWebsiteResponse> putBucketWebsite(PutBucketWebsiteRequest putBucketWebsiteRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutBucketWebsiteResponse> putBucketWebsite(Consumer<PutBucketWebsiteRequest.Builder> putBucketWebsiteRequest) {
        return this.putBucketWebsite((PutBucketWebsiteRequest)((Object)((PutBucketWebsiteRequest.Builder)PutBucketWebsiteRequest.builder().applyMutation(putBucketWebsiteRequest)).build()));
    }

    default public CompletableFuture<PutObjectResponse> putObject(PutObjectRequest putObjectRequest, AsyncRequestBody requestBody) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutObjectResponse> putObject(Consumer<PutObjectRequest.Builder> putObjectRequest, AsyncRequestBody requestBody) {
        return this.putObject((PutObjectRequest)((Object)((PutObjectRequest.Builder)PutObjectRequest.builder().applyMutation(putObjectRequest)).build()), requestBody);
    }

    default public CompletableFuture<PutObjectResponse> putObject(PutObjectRequest putObjectRequest, Path sourcePath) {
        return this.putObject(putObjectRequest, AsyncRequestBody.fromFile((Path)sourcePath));
    }

    default public CompletableFuture<PutObjectResponse> putObject(Consumer<PutObjectRequest.Builder> putObjectRequest, Path sourcePath) {
        return this.putObject((PutObjectRequest)((Object)((PutObjectRequest.Builder)PutObjectRequest.builder().applyMutation(putObjectRequest)).build()), sourcePath);
    }

    default public CompletableFuture<PutObjectAclResponse> putObjectAcl(PutObjectAclRequest putObjectAclRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutObjectAclResponse> putObjectAcl(Consumer<PutObjectAclRequest.Builder> putObjectAclRequest) {
        return this.putObjectAcl((PutObjectAclRequest)((Object)((PutObjectAclRequest.Builder)PutObjectAclRequest.builder().applyMutation(putObjectAclRequest)).build()));
    }

    default public CompletableFuture<PutObjectLegalHoldResponse> putObjectLegalHold(PutObjectLegalHoldRequest putObjectLegalHoldRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutObjectLegalHoldResponse> putObjectLegalHold(Consumer<PutObjectLegalHoldRequest.Builder> putObjectLegalHoldRequest) {
        return this.putObjectLegalHold((PutObjectLegalHoldRequest)((Object)((PutObjectLegalHoldRequest.Builder)PutObjectLegalHoldRequest.builder().applyMutation(putObjectLegalHoldRequest)).build()));
    }

    default public CompletableFuture<PutObjectLockConfigurationResponse> putObjectLockConfiguration(PutObjectLockConfigurationRequest putObjectLockConfigurationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutObjectLockConfigurationResponse> putObjectLockConfiguration(Consumer<PutObjectLockConfigurationRequest.Builder> putObjectLockConfigurationRequest) {
        return this.putObjectLockConfiguration((PutObjectLockConfigurationRequest)((Object)((PutObjectLockConfigurationRequest.Builder)PutObjectLockConfigurationRequest.builder().applyMutation(putObjectLockConfigurationRequest)).build()));
    }

    default public CompletableFuture<PutObjectRetentionResponse> putObjectRetention(PutObjectRetentionRequest putObjectRetentionRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutObjectRetentionResponse> putObjectRetention(Consumer<PutObjectRetentionRequest.Builder> putObjectRetentionRequest) {
        return this.putObjectRetention((PutObjectRetentionRequest)((Object)((PutObjectRetentionRequest.Builder)PutObjectRetentionRequest.builder().applyMutation(putObjectRetentionRequest)).build()));
    }

    default public CompletableFuture<PutObjectTaggingResponse> putObjectTagging(PutObjectTaggingRequest putObjectTaggingRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutObjectTaggingResponse> putObjectTagging(Consumer<PutObjectTaggingRequest.Builder> putObjectTaggingRequest) {
        return this.putObjectTagging((PutObjectTaggingRequest)((Object)((PutObjectTaggingRequest.Builder)PutObjectTaggingRequest.builder().applyMutation(putObjectTaggingRequest)).build()));
    }

    default public CompletableFuture<PutPublicAccessBlockResponse> putPublicAccessBlock(PutPublicAccessBlockRequest putPublicAccessBlockRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutPublicAccessBlockResponse> putPublicAccessBlock(Consumer<PutPublicAccessBlockRequest.Builder> putPublicAccessBlockRequest) {
        return this.putPublicAccessBlock((PutPublicAccessBlockRequest)((Object)((PutPublicAccessBlockRequest.Builder)PutPublicAccessBlockRequest.builder().applyMutation(putPublicAccessBlockRequest)).build()));
    }

    default public CompletableFuture<RestoreObjectResponse> restoreObject(RestoreObjectRequest restoreObjectRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<RestoreObjectResponse> restoreObject(Consumer<RestoreObjectRequest.Builder> restoreObjectRequest) {
        return this.restoreObject((RestoreObjectRequest)((Object)((RestoreObjectRequest.Builder)RestoreObjectRequest.builder().applyMutation(restoreObjectRequest)).build()));
    }

    default public CompletableFuture<Void> selectObjectContent(SelectObjectContentRequest selectObjectContentRequest, SelectObjectContentResponseHandler asyncResponseHandler) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<Void> selectObjectContent(Consumer<SelectObjectContentRequest.Builder> selectObjectContentRequest, SelectObjectContentResponseHandler asyncResponseHandler) {
        return this.selectObjectContent((SelectObjectContentRequest)((Object)((SelectObjectContentRequest.Builder)SelectObjectContentRequest.builder().applyMutation(selectObjectContentRequest)).build()), asyncResponseHandler);
    }

    default public CompletableFuture<UploadPartResponse> uploadPart(UploadPartRequest uploadPartRequest, AsyncRequestBody requestBody) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<UploadPartResponse> uploadPart(Consumer<UploadPartRequest.Builder> uploadPartRequest, AsyncRequestBody requestBody) {
        return this.uploadPart((UploadPartRequest)((Object)((UploadPartRequest.Builder)UploadPartRequest.builder().applyMutation(uploadPartRequest)).build()), requestBody);
    }

    default public CompletableFuture<UploadPartResponse> uploadPart(UploadPartRequest uploadPartRequest, Path sourcePath) {
        return this.uploadPart(uploadPartRequest, AsyncRequestBody.fromFile((Path)sourcePath));
    }

    default public CompletableFuture<UploadPartResponse> uploadPart(Consumer<UploadPartRequest.Builder> uploadPartRequest, Path sourcePath) {
        return this.uploadPart((UploadPartRequest)((Object)((UploadPartRequest.Builder)UploadPartRequest.builder().applyMutation(uploadPartRequest)).build()), sourcePath);
    }

    default public CompletableFuture<UploadPartCopyResponse> uploadPartCopy(UploadPartCopyRequest uploadPartCopyRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<UploadPartCopyResponse> uploadPartCopy(Consumer<UploadPartCopyRequest.Builder> uploadPartCopyRequest) {
        return this.uploadPartCopy((UploadPartCopyRequest)((Object)((UploadPartCopyRequest.Builder)UploadPartCopyRequest.builder().applyMutation(uploadPartCopyRequest)).build()));
    }

    default public CompletableFuture<WriteGetObjectResponseResponse> writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest, AsyncRequestBody requestBody) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<WriteGetObjectResponseResponse> writeGetObjectResponse(Consumer<WriteGetObjectResponseRequest.Builder> writeGetObjectResponseRequest, AsyncRequestBody requestBody) {
        return this.writeGetObjectResponse((WriteGetObjectResponseRequest)((Object)((WriteGetObjectResponseRequest.Builder)WriteGetObjectResponseRequest.builder().applyMutation(writeGetObjectResponseRequest)).build()), requestBody);
    }

    default public CompletableFuture<WriteGetObjectResponseResponse> writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest, Path sourcePath) {
        return this.writeGetObjectResponse(writeGetObjectResponseRequest, AsyncRequestBody.fromFile((Path)sourcePath));
    }

    default public CompletableFuture<WriteGetObjectResponseResponse> writeGetObjectResponse(Consumer<WriteGetObjectResponseRequest.Builder> writeGetObjectResponseRequest, Path sourcePath) {
        return this.writeGetObjectResponse((WriteGetObjectResponseRequest)((Object)((WriteGetObjectResponseRequest.Builder)WriteGetObjectResponseRequest.builder().applyMutation(writeGetObjectResponseRequest)).build()), sourcePath);
    }

    default public S3AsyncWaiter waiter() {
        throw new UnsupportedOperationException();
    }

    default public S3ServiceClientConfiguration serviceClientConfiguration() {
        throw new UnsupportedOperationException();
    }

    public static S3AsyncClient create() {
        return (S3AsyncClient)S3AsyncClient.builder().build();
    }

    public static S3AsyncClientBuilder builder() {
        return new DefaultS3AsyncClientBuilder();
    }

    public static S3CrtAsyncClientBuilder crtBuilder() {
        return S3CrtAsyncClient.builder();
    }

    public static S3AsyncClient crtCreate() {
        return S3CrtAsyncClient.builder().build();
    }
}

