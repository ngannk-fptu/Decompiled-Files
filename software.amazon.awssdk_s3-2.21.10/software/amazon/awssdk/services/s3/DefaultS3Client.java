/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.Aws4UnsignedPayloadSigner
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.awscore.client.handler.AwsSyncClientHandler
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.awscore.internal.AwsProtocolMetadata
 *  software.amazon.awssdk.awscore.internal.AwsServiceProtocol
 *  software.amazon.awssdk.core.RequestOverrideConfiguration
 *  software.amazon.awssdk.core.SdkPlugin
 *  software.amazon.awssdk.core.SdkProtocolMetadata
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.SdkServiceClientConfiguration$Builder
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.core.client.handler.ClientExecutionParams
 *  software.amazon.awssdk.core.client.handler.SyncClientHandler
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.trait.HttpChecksum
 *  software.amazon.awssdk.core.metrics.CoreMetric
 *  software.amazon.awssdk.core.runtime.transform.Marshaller
 *  software.amazon.awssdk.core.runtime.transform.StreamingRequestMarshaller
 *  software.amazon.awssdk.core.runtime.transform.StreamingRequestMarshaller$Builder
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.core.sync.RequestBody
 *  software.amazon.awssdk.core.sync.ResponseTransformer
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.metrics.MetricPublisher
 *  software.amazon.awssdk.metrics.NoOpMetricCollector
 *  software.amazon.awssdk.protocols.core.ExceptionMetadata
 *  software.amazon.awssdk.protocols.xml.AwsS3ProtocolFactory
 *  software.amazon.awssdk.protocols.xml.AwsS3ProtocolFactory$Builder
 *  software.amazon.awssdk.protocols.xml.AwsXmlProtocolFactory
 *  software.amazon.awssdk.protocols.xml.XmlOperationMetadata
 *  software.amazon.awssdk.utils.HostnameValidator
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.services.s3;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.Aws4UnsignedPayloadSigner;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.awscore.client.handler.AwsSyncClientHandler;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.awscore.internal.AwsProtocolMetadata;
import software.amazon.awssdk.awscore.internal.AwsServiceProtocol;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.SdkProtocolMetadata;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkServiceClientConfiguration;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.client.handler.SyncClientHandler;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.interceptor.trait.HttpChecksum;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.runtime.transform.Marshaller;
import software.amazon.awssdk.core.runtime.transform.StreamingRequestMarshaller;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.metrics.NoOpMetricCollector;
import software.amazon.awssdk.protocols.core.ExceptionMetadata;
import software.amazon.awssdk.protocols.xml.AwsS3ProtocolFactory;
import software.amazon.awssdk.protocols.xml.AwsXmlProtocolFactory;
import software.amazon.awssdk.protocols.xml.XmlOperationMetadata;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ServiceClientConfiguration;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.internal.S3ServiceClientConfigurationBuilder;
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
import software.amazon.awssdk.services.s3.transform.AbortMultipartUploadRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.CompleteMultipartUploadRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.CopyObjectRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.CreateBucketRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.CreateMultipartUploadRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketAnalyticsConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketCorsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketEncryptionRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketIntelligentTieringConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketInventoryConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketLifecycleRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketMetricsConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketOwnershipControlsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketPolicyRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketReplicationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketTaggingRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteBucketWebsiteRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteObjectRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteObjectTaggingRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteObjectsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeletePublicAccessBlockRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketAccelerateConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketAclRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketAnalyticsConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketCorsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketEncryptionRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketIntelligentTieringConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketInventoryConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketLifecycleConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketLocationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketLoggingRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketMetricsConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketNotificationConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketOwnershipControlsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketPolicyRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketPolicyStatusRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketReplicationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketRequestPaymentRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketTaggingRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketVersioningRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetBucketWebsiteRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetObjectAclRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetObjectAttributesRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetObjectLegalHoldRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetObjectLockConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetObjectRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetObjectRetentionRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetObjectTaggingRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetObjectTorrentRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetPublicAccessBlockRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.HeadBucketRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.HeadObjectRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.ListBucketAnalyticsConfigurationsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.ListBucketIntelligentTieringConfigurationsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.ListBucketInventoryConfigurationsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.ListBucketMetricsConfigurationsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.ListBucketsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.ListMultipartUploadsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.ListObjectVersionsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.ListObjectsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.ListObjectsV2RequestMarshaller;
import software.amazon.awssdk.services.s3.transform.ListPartsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketAccelerateConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketAclRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketAnalyticsConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketCorsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketEncryptionRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketIntelligentTieringConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketInventoryConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketLifecycleConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketLoggingRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketMetricsConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketNotificationConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketOwnershipControlsRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketPolicyRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketReplicationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketRequestPaymentRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketTaggingRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketVersioningRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutBucketWebsiteRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutObjectAclRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutObjectLegalHoldRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutObjectLockConfigurationRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutObjectRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutObjectRetentionRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutObjectTaggingRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutPublicAccessBlockRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.RestoreObjectRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.UploadPartCopyRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.UploadPartRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.WriteGetObjectResponseRequestMarshaller;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.utils.HostnameValidator;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
final class DefaultS3Client
implements S3Client {
    private static final Logger log = Logger.loggerFor(DefaultS3Client.class);
    private static final AwsProtocolMetadata protocolMetadata = AwsProtocolMetadata.builder().serviceProtocol(AwsServiceProtocol.REST_XML).build();
    private final SyncClientHandler clientHandler;
    private final AwsS3ProtocolFactory protocolFactory;
    private final SdkClientConfiguration clientConfiguration;
    private final S3ServiceClientConfiguration serviceClientConfiguration;

    protected DefaultS3Client(S3ServiceClientConfiguration serviceClientConfiguration, SdkClientConfiguration clientConfiguration) {
        this.clientHandler = new AwsSyncClientHandler(clientConfiguration);
        this.clientConfiguration = clientConfiguration;
        this.serviceClientConfiguration = serviceClientConfiguration;
        this.protocolFactory = this.init();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AbortMultipartUploadResponse abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest) throws NoSuchUploadException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(AbortMultipartUploadResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)abortMultipartUploadRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, abortMultipartUploadRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"AbortMultipartUpload");
            AbortMultipartUploadResponse abortMultipartUploadResponse = (AbortMultipartUploadResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("AbortMultipartUpload").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)abortMultipartUploadRequest).withMarshaller((Marshaller)new AbortMultipartUploadRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return abortMultipartUploadResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$abortMultipartUpload$0((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteMultipartUploadResponse completeMultipartUpload(CompleteMultipartUploadRequest completeMultipartUploadRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(CompleteMultipartUploadResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)completeMultipartUploadRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, completeMultipartUploadRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CompleteMultipartUpload");
            CompleteMultipartUploadResponse completeMultipartUploadResponse = (CompleteMultipartUploadResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("CompleteMultipartUpload").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)completeMultipartUploadRequest).withMarshaller((Marshaller)new CompleteMultipartUploadRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return completeMultipartUploadResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$completeMultipartUpload$1((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CopyObjectResponse copyObject(CopyObjectRequest copyObjectRequest) throws ObjectNotInActiveTierErrorException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(CopyObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)copyObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, copyObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CopyObject");
            CopyObjectResponse copyObjectResponse = (CopyObjectResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("CopyObject").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)copyObjectRequest).withMarshaller((Marshaller)new CopyObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return copyObjectResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$copyObject$2((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CreateBucketResponse createBucket(CreateBucketRequest createBucketRequest) throws BucketAlreadyExistsException, BucketAlreadyOwnedByYouException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(CreateBucketResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)createBucketRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, createBucketRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CreateBucket");
            CreateBucketResponse createBucketResponse = (CreateBucketResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("CreateBucket").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)createBucketRequest).withMarshaller((Marshaller)new CreateBucketRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return createBucketResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$createBucket$3((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CreateMultipartUploadResponse createMultipartUpload(CreateMultipartUploadRequest createMultipartUploadRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(CreateMultipartUploadResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)createMultipartUploadRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, createMultipartUploadRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CreateMultipartUpload");
            CreateMultipartUploadResponse createMultipartUploadResponse = (CreateMultipartUploadResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("CreateMultipartUpload").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)createMultipartUploadRequest).withMarshaller((Marshaller)new CreateMultipartUploadRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return createMultipartUploadResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$createMultipartUpload$4((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketResponse deleteBucket(DeleteBucketRequest deleteBucketRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucket");
            DeleteBucketResponse deleteBucketResponse = (DeleteBucketResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucket").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketRequest).withMarshaller((Marshaller)new DeleteBucketRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucket$5((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketAnalyticsConfigurationResponse deleteBucketAnalyticsConfiguration(DeleteBucketAnalyticsConfigurationRequest deleteBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketAnalyticsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketAnalyticsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketAnalyticsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketAnalyticsConfiguration");
            DeleteBucketAnalyticsConfigurationResponse deleteBucketAnalyticsConfigurationResponse = (DeleteBucketAnalyticsConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketAnalyticsConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketAnalyticsConfigurationRequest).withMarshaller((Marshaller)new DeleteBucketAnalyticsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketAnalyticsConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketAnalyticsConfiguration$6((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketCorsResponse deleteBucketCors(DeleteBucketCorsRequest deleteBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketCorsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketCorsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketCorsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketCors");
            DeleteBucketCorsResponse deleteBucketCorsResponse = (DeleteBucketCorsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketCors").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketCorsRequest).withMarshaller((Marshaller)new DeleteBucketCorsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketCorsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketCors$7((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketEncryptionResponse deleteBucketEncryption(DeleteBucketEncryptionRequest deleteBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketEncryptionResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketEncryptionRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketEncryptionRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketEncryption");
            DeleteBucketEncryptionResponse deleteBucketEncryptionResponse = (DeleteBucketEncryptionResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketEncryption").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketEncryptionRequest).withMarshaller((Marshaller)new DeleteBucketEncryptionRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketEncryptionResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketEncryption$8((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketIntelligentTieringConfigurationResponse deleteBucketIntelligentTieringConfiguration(DeleteBucketIntelligentTieringConfigurationRequest deleteBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketIntelligentTieringConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketIntelligentTieringConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketIntelligentTieringConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketIntelligentTieringConfiguration");
            DeleteBucketIntelligentTieringConfigurationResponse deleteBucketIntelligentTieringConfigurationResponse = (DeleteBucketIntelligentTieringConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketIntelligentTieringConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketIntelligentTieringConfigurationRequest).withMarshaller((Marshaller)new DeleteBucketIntelligentTieringConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketIntelligentTieringConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketIntelligentTieringConfiguration$9((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketInventoryConfigurationResponse deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest deleteBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketInventoryConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketInventoryConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketInventoryConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketInventoryConfiguration");
            DeleteBucketInventoryConfigurationResponse deleteBucketInventoryConfigurationResponse = (DeleteBucketInventoryConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketInventoryConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketInventoryConfigurationRequest).withMarshaller((Marshaller)new DeleteBucketInventoryConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketInventoryConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketInventoryConfiguration$10((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketLifecycleResponse deleteBucketLifecycle(DeleteBucketLifecycleRequest deleteBucketLifecycleRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketLifecycleResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketLifecycleRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketLifecycleRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketLifecycle");
            DeleteBucketLifecycleResponse deleteBucketLifecycleResponse = (DeleteBucketLifecycleResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketLifecycle").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketLifecycleRequest).withMarshaller((Marshaller)new DeleteBucketLifecycleRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketLifecycleResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketLifecycle$11((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketMetricsConfigurationResponse deleteBucketMetricsConfiguration(DeleteBucketMetricsConfigurationRequest deleteBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketMetricsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketMetricsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketMetricsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketMetricsConfiguration");
            DeleteBucketMetricsConfigurationResponse deleteBucketMetricsConfigurationResponse = (DeleteBucketMetricsConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketMetricsConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketMetricsConfigurationRequest).withMarshaller((Marshaller)new DeleteBucketMetricsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketMetricsConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketMetricsConfiguration$12((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketOwnershipControlsResponse deleteBucketOwnershipControls(DeleteBucketOwnershipControlsRequest deleteBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketOwnershipControlsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketOwnershipControlsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketOwnershipControlsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketOwnershipControls");
            DeleteBucketOwnershipControlsResponse deleteBucketOwnershipControlsResponse = (DeleteBucketOwnershipControlsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketOwnershipControls").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketOwnershipControlsRequest).withMarshaller((Marshaller)new DeleteBucketOwnershipControlsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketOwnershipControlsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketOwnershipControls$13((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketPolicyResponse deleteBucketPolicy(DeleteBucketPolicyRequest deleteBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketPolicyResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketPolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketPolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketPolicy");
            DeleteBucketPolicyResponse deleteBucketPolicyResponse = (DeleteBucketPolicyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketPolicy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketPolicyRequest).withMarshaller((Marshaller)new DeleteBucketPolicyRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketPolicyResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketPolicy$14((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketReplicationResponse deleteBucketReplication(DeleteBucketReplicationRequest deleteBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketReplicationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketReplicationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketReplicationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketReplication");
            DeleteBucketReplicationResponse deleteBucketReplicationResponse = (DeleteBucketReplicationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketReplication").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketReplicationRequest).withMarshaller((Marshaller)new DeleteBucketReplicationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketReplicationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketReplication$15((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketTaggingResponse deleteBucketTagging(DeleteBucketTaggingRequest deleteBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketTagging");
            DeleteBucketTaggingResponse deleteBucketTaggingResponse = (DeleteBucketTaggingResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketTagging").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketTaggingRequest).withMarshaller((Marshaller)new DeleteBucketTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketTaggingResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketTagging$16((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteBucketWebsiteResponse deleteBucketWebsite(DeleteBucketWebsiteRequest deleteBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketWebsiteResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketWebsiteRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteBucketWebsiteRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketWebsite");
            DeleteBucketWebsiteResponse deleteBucketWebsiteResponse = (DeleteBucketWebsiteResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketWebsite").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteBucketWebsiteRequest).withMarshaller((Marshaller)new DeleteBucketWebsiteRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteBucketWebsiteResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteBucketWebsite$17((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteObjectResponse deleteObject(DeleteObjectRequest deleteObjectRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteObject");
            DeleteObjectResponse deleteObjectResponse = (DeleteObjectResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteObject").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteObjectRequest).withMarshaller((Marshaller)new DeleteObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteObjectResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteObject$18((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteObjectTaggingResponse deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteObjectTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteObjectTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteObjectTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteObjectTagging");
            DeleteObjectTaggingResponse deleteObjectTaggingResponse = (DeleteObjectTaggingResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteObjectTagging").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteObjectTaggingRequest).withMarshaller((Marshaller)new DeleteObjectTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteObjectTaggingResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteObjectTagging$19((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteObjectsResponse deleteObjects(DeleteObjectsRequest deleteObjectsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteObjectsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteObjectsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deleteObjectsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteObjects");
            DeleteObjectsResponse deleteObjectsResponse = (DeleteObjectsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteObjects").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteObjectsRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(deleteObjectsRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new DeleteObjectsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deleteObjectsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deleteObjects$20((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeletePublicAccessBlockResponse deletePublicAccessBlock(DeletePublicAccessBlockRequest deletePublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeletePublicAccessBlockResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deletePublicAccessBlockRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, deletePublicAccessBlockRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeletePublicAccessBlock");
            DeletePublicAccessBlockResponse deletePublicAccessBlockResponse = (DeletePublicAccessBlockResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeletePublicAccessBlock").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deletePublicAccessBlockRequest).withMarshaller((Marshaller)new DeletePublicAccessBlockRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return deletePublicAccessBlockResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$deletePublicAccessBlock$21((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketAccelerateConfigurationResponse getBucketAccelerateConfiguration(GetBucketAccelerateConfigurationRequest getBucketAccelerateConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketAccelerateConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketAccelerateConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketAccelerateConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketAccelerateConfiguration");
            GetBucketAccelerateConfigurationResponse getBucketAccelerateConfigurationResponse = (GetBucketAccelerateConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketAccelerateConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketAccelerateConfigurationRequest).withMarshaller((Marshaller)new GetBucketAccelerateConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketAccelerateConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketAccelerateConfiguration$22((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketAclResponse getBucketAcl(GetBucketAclRequest getBucketAclRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketAclResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketAclRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketAclRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketAcl");
            GetBucketAclResponse getBucketAclResponse = (GetBucketAclResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketAcl").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketAclRequest).withMarshaller((Marshaller)new GetBucketAclRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketAclResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketAcl$23((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketAnalyticsConfigurationResponse getBucketAnalyticsConfiguration(GetBucketAnalyticsConfigurationRequest getBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketAnalyticsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketAnalyticsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketAnalyticsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketAnalyticsConfiguration");
            GetBucketAnalyticsConfigurationResponse getBucketAnalyticsConfigurationResponse = (GetBucketAnalyticsConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketAnalyticsConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketAnalyticsConfigurationRequest).withMarshaller((Marshaller)new GetBucketAnalyticsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketAnalyticsConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketAnalyticsConfiguration$24((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketCorsResponse getBucketCors(GetBucketCorsRequest getBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketCorsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketCorsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketCorsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketCors");
            GetBucketCorsResponse getBucketCorsResponse = (GetBucketCorsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketCors").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketCorsRequest).withMarshaller((Marshaller)new GetBucketCorsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketCorsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketCors$25((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketEncryptionResponse getBucketEncryption(GetBucketEncryptionRequest getBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketEncryptionResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketEncryptionRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketEncryptionRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketEncryption");
            GetBucketEncryptionResponse getBucketEncryptionResponse = (GetBucketEncryptionResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketEncryption").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketEncryptionRequest).withMarshaller((Marshaller)new GetBucketEncryptionRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketEncryptionResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketEncryption$26((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketIntelligentTieringConfigurationResponse getBucketIntelligentTieringConfiguration(GetBucketIntelligentTieringConfigurationRequest getBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketIntelligentTieringConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketIntelligentTieringConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketIntelligentTieringConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketIntelligentTieringConfiguration");
            GetBucketIntelligentTieringConfigurationResponse getBucketIntelligentTieringConfigurationResponse = (GetBucketIntelligentTieringConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketIntelligentTieringConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketIntelligentTieringConfigurationRequest).withMarshaller((Marshaller)new GetBucketIntelligentTieringConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketIntelligentTieringConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketIntelligentTieringConfiguration$27((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketInventoryConfigurationResponse getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest getBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketInventoryConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketInventoryConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketInventoryConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketInventoryConfiguration");
            GetBucketInventoryConfigurationResponse getBucketInventoryConfigurationResponse = (GetBucketInventoryConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketInventoryConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketInventoryConfigurationRequest).withMarshaller((Marshaller)new GetBucketInventoryConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketInventoryConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketInventoryConfiguration$28((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketLifecycleConfigurationResponse getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketLifecycleConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketLifecycleConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketLifecycleConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketLifecycleConfiguration");
            GetBucketLifecycleConfigurationResponse getBucketLifecycleConfigurationResponse = (GetBucketLifecycleConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketLifecycleConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketLifecycleConfigurationRequest).withMarshaller((Marshaller)new GetBucketLifecycleConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketLifecycleConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketLifecycleConfiguration$29((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketLocationResponse getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketLocationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketLocationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketLocationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketLocation");
            GetBucketLocationResponse getBucketLocationResponse = (GetBucketLocationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketLocation").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketLocationRequest).withMarshaller((Marshaller)new GetBucketLocationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketLocationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketLocation$30((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketLoggingResponse getBucketLogging(GetBucketLoggingRequest getBucketLoggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketLoggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketLoggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketLoggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketLogging");
            GetBucketLoggingResponse getBucketLoggingResponse = (GetBucketLoggingResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketLogging").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketLoggingRequest).withMarshaller((Marshaller)new GetBucketLoggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketLoggingResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketLogging$31((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketMetricsConfigurationResponse getBucketMetricsConfiguration(GetBucketMetricsConfigurationRequest getBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketMetricsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketMetricsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketMetricsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketMetricsConfiguration");
            GetBucketMetricsConfigurationResponse getBucketMetricsConfigurationResponse = (GetBucketMetricsConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketMetricsConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketMetricsConfigurationRequest).withMarshaller((Marshaller)new GetBucketMetricsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketMetricsConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketMetricsConfiguration$32((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketNotificationConfigurationResponse getBucketNotificationConfiguration(GetBucketNotificationConfigurationRequest getBucketNotificationConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketNotificationConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketNotificationConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketNotificationConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketNotificationConfiguration");
            GetBucketNotificationConfigurationResponse getBucketNotificationConfigurationResponse = (GetBucketNotificationConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketNotificationConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketNotificationConfigurationRequest).withMarshaller((Marshaller)new GetBucketNotificationConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketNotificationConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketNotificationConfiguration$33((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketOwnershipControlsResponse getBucketOwnershipControls(GetBucketOwnershipControlsRequest getBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketOwnershipControlsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketOwnershipControlsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketOwnershipControlsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketOwnershipControls");
            GetBucketOwnershipControlsResponse getBucketOwnershipControlsResponse = (GetBucketOwnershipControlsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketOwnershipControls").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketOwnershipControlsRequest).withMarshaller((Marshaller)new GetBucketOwnershipControlsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketOwnershipControlsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketOwnershipControls$34((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketPolicyResponse getBucketPolicy(GetBucketPolicyRequest getBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketPolicyResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketPolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketPolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketPolicy");
            GetBucketPolicyResponse getBucketPolicyResponse = (GetBucketPolicyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketPolicy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketPolicyRequest).withMarshaller((Marshaller)new GetBucketPolicyRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketPolicyResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketPolicy$35((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketPolicyStatusResponse getBucketPolicyStatus(GetBucketPolicyStatusRequest getBucketPolicyStatusRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketPolicyStatusResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketPolicyStatusRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketPolicyStatusRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketPolicyStatus");
            GetBucketPolicyStatusResponse getBucketPolicyStatusResponse = (GetBucketPolicyStatusResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketPolicyStatus").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketPolicyStatusRequest).withMarshaller((Marshaller)new GetBucketPolicyStatusRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketPolicyStatusResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketPolicyStatus$36((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketReplicationResponse getBucketReplication(GetBucketReplicationRequest getBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketReplicationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketReplicationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketReplicationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketReplication");
            GetBucketReplicationResponse getBucketReplicationResponse = (GetBucketReplicationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketReplication").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketReplicationRequest).withMarshaller((Marshaller)new GetBucketReplicationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketReplicationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketReplication$37((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketRequestPaymentResponse getBucketRequestPayment(GetBucketRequestPaymentRequest getBucketRequestPaymentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketRequestPaymentResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketRequestPaymentRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketRequestPaymentRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketRequestPayment");
            GetBucketRequestPaymentResponse getBucketRequestPaymentResponse = (GetBucketRequestPaymentResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketRequestPayment").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketRequestPaymentRequest).withMarshaller((Marshaller)new GetBucketRequestPaymentRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketRequestPaymentResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketRequestPayment$38((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketTaggingResponse getBucketTagging(GetBucketTaggingRequest getBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketTagging");
            GetBucketTaggingResponse getBucketTaggingResponse = (GetBucketTaggingResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketTagging").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketTaggingRequest).withMarshaller((Marshaller)new GetBucketTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketTaggingResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketTagging$39((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketVersioningResponse getBucketVersioning(GetBucketVersioningRequest getBucketVersioningRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketVersioningResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketVersioningRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketVersioningRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketVersioning");
            GetBucketVersioningResponse getBucketVersioningResponse = (GetBucketVersioningResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketVersioning").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketVersioningRequest).withMarshaller((Marshaller)new GetBucketVersioningRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketVersioningResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketVersioning$40((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetBucketWebsiteResponse getBucketWebsite(GetBucketWebsiteRequest getBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketWebsiteResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketWebsiteRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getBucketWebsiteRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketWebsite");
            GetBucketWebsiteResponse getBucketWebsiteResponse = (GetBucketWebsiteResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketWebsite").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getBucketWebsiteRequest).withMarshaller((Marshaller)new GetBucketWebsiteRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getBucketWebsiteResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getBucketWebsite$41((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <ReturnT> ReturnT getObject(GetObjectRequest getObjectRequest, ResponseTransformer<GetObjectResponse, ReturnT> responseTransformer) throws NoSuchKeyException, InvalidObjectStateException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(true));
        HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObject");
            Object object = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObject").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getObjectRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(false).requestValidationMode(getObjectRequest.checksumModeAsString()).responseAlgorithms(new String[]{"CRC32", "CRC32C", "SHA256", "SHA1"}).isRequestStreaming(false).build()).withMarshaller((Marshaller)new GetObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)), responseTransformer);
            return (ReturnT)object;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getObject$42((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetObjectAclResponse getObjectAcl(GetObjectAclRequest getObjectAclRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectAclResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectAclRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getObjectAclRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectAcl");
            GetObjectAclResponse getObjectAclResponse = (GetObjectAclResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectAcl").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getObjectAclRequest).withMarshaller((Marshaller)new GetObjectAclRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getObjectAclResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getObjectAcl$43((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetObjectAttributesResponse getObjectAttributes(GetObjectAttributesRequest getObjectAttributesRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectAttributesResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectAttributesRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getObjectAttributesRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectAttributes");
            GetObjectAttributesResponse getObjectAttributesResponse = (GetObjectAttributesResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectAttributes").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getObjectAttributesRequest).withMarshaller((Marshaller)new GetObjectAttributesRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getObjectAttributesResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getObjectAttributes$44((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetObjectLegalHoldResponse getObjectLegalHold(GetObjectLegalHoldRequest getObjectLegalHoldRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectLegalHoldResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectLegalHoldRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getObjectLegalHoldRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectLegalHold");
            GetObjectLegalHoldResponse getObjectLegalHoldResponse = (GetObjectLegalHoldResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectLegalHold").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getObjectLegalHoldRequest).withMarshaller((Marshaller)new GetObjectLegalHoldRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getObjectLegalHoldResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getObjectLegalHold$45((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetObjectLockConfigurationResponse getObjectLockConfiguration(GetObjectLockConfigurationRequest getObjectLockConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectLockConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectLockConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getObjectLockConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectLockConfiguration");
            GetObjectLockConfigurationResponse getObjectLockConfigurationResponse = (GetObjectLockConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectLockConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getObjectLockConfigurationRequest).withMarshaller((Marshaller)new GetObjectLockConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getObjectLockConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getObjectLockConfiguration$46((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetObjectRetentionResponse getObjectRetention(GetObjectRetentionRequest getObjectRetentionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectRetentionResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectRetentionRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getObjectRetentionRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectRetention");
            GetObjectRetentionResponse getObjectRetentionResponse = (GetObjectRetentionResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectRetention").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getObjectRetentionRequest).withMarshaller((Marshaller)new GetObjectRetentionRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getObjectRetentionResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getObjectRetention$47((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetObjectTaggingResponse getObjectTagging(GetObjectTaggingRequest getObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getObjectTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectTagging");
            GetObjectTaggingResponse getObjectTaggingResponse = (GetObjectTaggingResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectTagging").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getObjectTaggingRequest).withMarshaller((Marshaller)new GetObjectTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getObjectTaggingResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getObjectTagging$48((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <ReturnT> ReturnT getObjectTorrent(GetObjectTorrentRequest getObjectTorrentRequest, ResponseTransformer<GetObjectTorrentResponse, ReturnT> responseTransformer) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetObjectTorrentResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(true));
        HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectTorrentRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getObjectTorrentRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectTorrent");
            Object object = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectTorrent").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getObjectTorrentRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new GetObjectTorrentRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)), responseTransformer);
            return (ReturnT)object;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getObjectTorrent$49((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetPublicAccessBlockResponse getPublicAccessBlock(GetPublicAccessBlockRequest getPublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetPublicAccessBlockResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getPublicAccessBlockRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, getPublicAccessBlockRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetPublicAccessBlock");
            GetPublicAccessBlockResponse getPublicAccessBlockResponse = (GetPublicAccessBlockResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetPublicAccessBlock").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getPublicAccessBlockRequest).withMarshaller((Marshaller)new GetPublicAccessBlockRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return getPublicAccessBlockResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$getPublicAccessBlock$50((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public HeadBucketResponse headBucket(HeadBucketRequest headBucketRequest) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(HeadBucketResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)headBucketRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, headBucketRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"HeadBucket");
            HeadBucketResponse headBucketResponse = (HeadBucketResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("HeadBucket").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)headBucketRequest).withMarshaller((Marshaller)new HeadBucketRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return headBucketResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$headBucket$51((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public HeadObjectResponse headObject(HeadObjectRequest headObjectRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(HeadObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)headObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, headObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"HeadObject");
            HeadObjectResponse headObjectResponse = (HeadObjectResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("HeadObject").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)headObjectRequest).withMarshaller((Marshaller)new HeadObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return headObjectResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$headObject$52((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListBucketAnalyticsConfigurationsResponse listBucketAnalyticsConfigurations(ListBucketAnalyticsConfigurationsRequest listBucketAnalyticsConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListBucketAnalyticsConfigurationsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listBucketAnalyticsConfigurationsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, listBucketAnalyticsConfigurationsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListBucketAnalyticsConfigurations");
            ListBucketAnalyticsConfigurationsResponse listBucketAnalyticsConfigurationsResponse = (ListBucketAnalyticsConfigurationsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListBucketAnalyticsConfigurations").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listBucketAnalyticsConfigurationsRequest).withMarshaller((Marshaller)new ListBucketAnalyticsConfigurationsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return listBucketAnalyticsConfigurationsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$listBucketAnalyticsConfigurations$53((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListBucketIntelligentTieringConfigurationsResponse listBucketIntelligentTieringConfigurations(ListBucketIntelligentTieringConfigurationsRequest listBucketIntelligentTieringConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListBucketIntelligentTieringConfigurationsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listBucketIntelligentTieringConfigurationsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, listBucketIntelligentTieringConfigurationsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListBucketIntelligentTieringConfigurations");
            ListBucketIntelligentTieringConfigurationsResponse listBucketIntelligentTieringConfigurationsResponse = (ListBucketIntelligentTieringConfigurationsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListBucketIntelligentTieringConfigurations").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listBucketIntelligentTieringConfigurationsRequest).withMarshaller((Marshaller)new ListBucketIntelligentTieringConfigurationsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return listBucketIntelligentTieringConfigurationsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$listBucketIntelligentTieringConfigurations$54((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListBucketInventoryConfigurationsResponse listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest listBucketInventoryConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListBucketInventoryConfigurationsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listBucketInventoryConfigurationsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, listBucketInventoryConfigurationsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListBucketInventoryConfigurations");
            ListBucketInventoryConfigurationsResponse listBucketInventoryConfigurationsResponse = (ListBucketInventoryConfigurationsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListBucketInventoryConfigurations").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listBucketInventoryConfigurationsRequest).withMarshaller((Marshaller)new ListBucketInventoryConfigurationsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return listBucketInventoryConfigurationsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$listBucketInventoryConfigurations$55((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListBucketMetricsConfigurationsResponse listBucketMetricsConfigurations(ListBucketMetricsConfigurationsRequest listBucketMetricsConfigurationsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListBucketMetricsConfigurationsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listBucketMetricsConfigurationsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, listBucketMetricsConfigurationsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListBucketMetricsConfigurations");
            ListBucketMetricsConfigurationsResponse listBucketMetricsConfigurationsResponse = (ListBucketMetricsConfigurationsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListBucketMetricsConfigurations").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listBucketMetricsConfigurationsRequest).withMarshaller((Marshaller)new ListBucketMetricsConfigurationsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return listBucketMetricsConfigurationsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$listBucketMetricsConfigurations$56((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListBucketsResponse listBuckets(ListBucketsRequest listBucketsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListBucketsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listBucketsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, listBucketsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListBuckets");
            ListBucketsResponse listBucketsResponse = (ListBucketsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListBuckets").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listBucketsRequest).withMarshaller((Marshaller)new ListBucketsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return listBucketsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$listBuckets$57((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListMultipartUploadsResponse listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListMultipartUploadsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listMultipartUploadsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, listMultipartUploadsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListMultipartUploads");
            ListMultipartUploadsResponse listMultipartUploadsResponse = (ListMultipartUploadsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListMultipartUploads").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listMultipartUploadsRequest).withMarshaller((Marshaller)new ListMultipartUploadsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return listMultipartUploadsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$listMultipartUploads$58((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListObjectVersionsResponse listObjectVersions(ListObjectVersionsRequest listObjectVersionsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListObjectVersionsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listObjectVersionsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, listObjectVersionsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListObjectVersions");
            ListObjectVersionsResponse listObjectVersionsResponse = (ListObjectVersionsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListObjectVersions").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listObjectVersionsRequest).withMarshaller((Marshaller)new ListObjectVersionsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return listObjectVersionsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$listObjectVersions$59((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListObjectsResponse listObjects(ListObjectsRequest listObjectsRequest) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListObjectsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listObjectsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, listObjectsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListObjects");
            ListObjectsResponse listObjectsResponse = (ListObjectsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListObjects").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listObjectsRequest).withMarshaller((Marshaller)new ListObjectsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return listObjectsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$listObjects$60((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListObjectsV2Response listObjectsV2(ListObjectsV2Request listObjectsV2Request) throws NoSuchBucketException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListObjectsV2Response::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listObjectsV2Request, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, listObjectsV2Request.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListObjectsV2");
            ListObjectsV2Response listObjectsV2Response = (ListObjectsV2Response)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListObjectsV2").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listObjectsV2Request).withMarshaller((Marshaller)new ListObjectsV2RequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return listObjectsV2Response;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$listObjectsV2$61((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListPartsResponse listParts(ListPartsRequest listPartsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListPartsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listPartsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, listPartsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListParts");
            ListPartsResponse listPartsResponse = (ListPartsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListParts").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listPartsRequest).withMarshaller((Marshaller)new ListPartsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return listPartsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$listParts$62((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketAccelerateConfigurationResponse putBucketAccelerateConfiguration(PutBucketAccelerateConfigurationRequest putBucketAccelerateConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketAccelerateConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketAccelerateConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketAccelerateConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketAccelerateConfiguration");
            PutBucketAccelerateConfigurationResponse putBucketAccelerateConfigurationResponse = (PutBucketAccelerateConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketAccelerateConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketAccelerateConfigurationRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(false).requestAlgorithm(putBucketAccelerateConfigurationRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketAccelerateConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketAccelerateConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketAccelerateConfiguration$63((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketAclResponse putBucketAcl(PutBucketAclRequest putBucketAclRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketAclResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketAclRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketAclRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketAcl");
            PutBucketAclResponse putBucketAclResponse = (PutBucketAclResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketAcl").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketAclRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketAclRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketAclRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketAclResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketAcl$64((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketAnalyticsConfigurationResponse putBucketAnalyticsConfiguration(PutBucketAnalyticsConfigurationRequest putBucketAnalyticsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketAnalyticsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketAnalyticsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketAnalyticsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketAnalyticsConfiguration");
            PutBucketAnalyticsConfigurationResponse putBucketAnalyticsConfigurationResponse = (PutBucketAnalyticsConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketAnalyticsConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketAnalyticsConfigurationRequest).withMarshaller((Marshaller)new PutBucketAnalyticsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketAnalyticsConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketAnalyticsConfiguration$65((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketCorsResponse putBucketCors(PutBucketCorsRequest putBucketCorsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketCorsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketCorsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketCorsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketCors");
            PutBucketCorsResponse putBucketCorsResponse = (PutBucketCorsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketCors").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketCorsRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketCorsRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketCorsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketCorsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketCors$66((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketEncryptionResponse putBucketEncryption(PutBucketEncryptionRequest putBucketEncryptionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketEncryptionResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketEncryptionRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketEncryptionRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketEncryption");
            PutBucketEncryptionResponse putBucketEncryptionResponse = (PutBucketEncryptionResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketEncryption").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketEncryptionRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketEncryptionRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketEncryptionRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketEncryptionResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketEncryption$67((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketIntelligentTieringConfigurationResponse putBucketIntelligentTieringConfiguration(PutBucketIntelligentTieringConfigurationRequest putBucketIntelligentTieringConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketIntelligentTieringConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketIntelligentTieringConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketIntelligentTieringConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketIntelligentTieringConfiguration");
            PutBucketIntelligentTieringConfigurationResponse putBucketIntelligentTieringConfigurationResponse = (PutBucketIntelligentTieringConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketIntelligentTieringConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketIntelligentTieringConfigurationRequest).withMarshaller((Marshaller)new PutBucketIntelligentTieringConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketIntelligentTieringConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketIntelligentTieringConfiguration$68((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketInventoryConfigurationResponse putBucketInventoryConfiguration(PutBucketInventoryConfigurationRequest putBucketInventoryConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketInventoryConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketInventoryConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketInventoryConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketInventoryConfiguration");
            PutBucketInventoryConfigurationResponse putBucketInventoryConfigurationResponse = (PutBucketInventoryConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketInventoryConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketInventoryConfigurationRequest).withMarshaller((Marshaller)new PutBucketInventoryConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketInventoryConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketInventoryConfiguration$69((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketLifecycleConfigurationResponse putBucketLifecycleConfiguration(PutBucketLifecycleConfigurationRequest putBucketLifecycleConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketLifecycleConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketLifecycleConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketLifecycleConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketLifecycleConfiguration");
            PutBucketLifecycleConfigurationResponse putBucketLifecycleConfigurationResponse = (PutBucketLifecycleConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketLifecycleConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketLifecycleConfigurationRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketLifecycleConfigurationRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketLifecycleConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketLifecycleConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketLifecycleConfiguration$70((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketLoggingResponse putBucketLogging(PutBucketLoggingRequest putBucketLoggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketLoggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketLoggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketLoggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketLogging");
            PutBucketLoggingResponse putBucketLoggingResponse = (PutBucketLoggingResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketLogging").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketLoggingRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketLoggingRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketLoggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketLoggingResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketLogging$71((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketMetricsConfigurationResponse putBucketMetricsConfiguration(PutBucketMetricsConfigurationRequest putBucketMetricsConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketMetricsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketMetricsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketMetricsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketMetricsConfiguration");
            PutBucketMetricsConfigurationResponse putBucketMetricsConfigurationResponse = (PutBucketMetricsConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketMetricsConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketMetricsConfigurationRequest).withMarshaller((Marshaller)new PutBucketMetricsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketMetricsConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketMetricsConfiguration$72((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketNotificationConfigurationResponse putBucketNotificationConfiguration(PutBucketNotificationConfigurationRequest putBucketNotificationConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketNotificationConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketNotificationConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketNotificationConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketNotificationConfiguration");
            PutBucketNotificationConfigurationResponse putBucketNotificationConfigurationResponse = (PutBucketNotificationConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketNotificationConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketNotificationConfigurationRequest).withMarshaller((Marshaller)new PutBucketNotificationConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketNotificationConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketNotificationConfiguration$73((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketOwnershipControlsResponse putBucketOwnershipControls(PutBucketOwnershipControlsRequest putBucketOwnershipControlsRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketOwnershipControlsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketOwnershipControlsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketOwnershipControlsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketOwnershipControls");
            PutBucketOwnershipControlsResponse putBucketOwnershipControlsResponse = (PutBucketOwnershipControlsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketOwnershipControls").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketOwnershipControlsRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketOwnershipControlsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketOwnershipControlsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketOwnershipControls$74((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketPolicyResponse putBucketPolicy(PutBucketPolicyRequest putBucketPolicyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketPolicyResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketPolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketPolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketPolicy");
            PutBucketPolicyResponse putBucketPolicyResponse = (PutBucketPolicyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketPolicy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketPolicyRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketPolicyRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketPolicyRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketPolicyResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketPolicy$75((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketReplicationResponse putBucketReplication(PutBucketReplicationRequest putBucketReplicationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketReplicationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketReplicationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketReplicationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketReplication");
            PutBucketReplicationResponse putBucketReplicationResponse = (PutBucketReplicationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketReplication").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketReplicationRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketReplicationRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketReplicationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketReplicationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketReplication$76((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketRequestPaymentResponse putBucketRequestPayment(PutBucketRequestPaymentRequest putBucketRequestPaymentRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketRequestPaymentResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketRequestPaymentRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketRequestPaymentRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketRequestPayment");
            PutBucketRequestPaymentResponse putBucketRequestPaymentResponse = (PutBucketRequestPaymentResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketRequestPayment").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketRequestPaymentRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketRequestPaymentRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketRequestPaymentRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketRequestPaymentResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketRequestPayment$77((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketTaggingResponse putBucketTagging(PutBucketTaggingRequest putBucketTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketTagging");
            PutBucketTaggingResponse putBucketTaggingResponse = (PutBucketTaggingResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketTagging").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketTaggingRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketTaggingRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketTaggingResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketTagging$78((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketVersioningResponse putBucketVersioning(PutBucketVersioningRequest putBucketVersioningRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketVersioningResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketVersioningRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketVersioningRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketVersioning");
            PutBucketVersioningResponse putBucketVersioningResponse = (PutBucketVersioningResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketVersioning").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketVersioningRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketVersioningRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketVersioningRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketVersioningResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketVersioning$79((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutBucketWebsiteResponse putBucketWebsite(PutBucketWebsiteRequest putBucketWebsiteRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketWebsiteResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketWebsiteRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putBucketWebsiteRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketWebsite");
            PutBucketWebsiteResponse putBucketWebsiteResponse = (PutBucketWebsiteResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketWebsite").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putBucketWebsiteRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketWebsiteRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutBucketWebsiteRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putBucketWebsiteResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putBucketWebsite$80((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutObjectResponse putObject(PutObjectRequest putObjectRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObject");
            PutObjectResponse putObjectResponse = (PutObjectResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObject").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putObjectRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(false).requestAlgorithm(putObjectRequest.checksumAlgorithmAsString()).isRequestStreaming(true).build()).withRequestBody(requestBody).withMarshaller((Marshaller)((StreamingRequestMarshaller.Builder)StreamingRequestMarshaller.builder().delegateMarshaller((Marshaller)new PutObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory))).requestBody(requestBody).build()));
            return putObjectResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putObject$81((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutObjectAclResponse putObjectAcl(PutObjectAclRequest putObjectAclRequest) throws NoSuchKeyException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectAclResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectAclRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putObjectAclRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObjectAcl");
            PutObjectAclResponse putObjectAclResponse = (PutObjectAclResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObjectAcl").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putObjectAclRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putObjectAclRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutObjectAclRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putObjectAclResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putObjectAcl$82((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutObjectLegalHoldResponse putObjectLegalHold(PutObjectLegalHoldRequest putObjectLegalHoldRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectLegalHoldResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectLegalHoldRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putObjectLegalHoldRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObjectLegalHold");
            PutObjectLegalHoldResponse putObjectLegalHoldResponse = (PutObjectLegalHoldResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObjectLegalHold").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putObjectLegalHoldRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putObjectLegalHoldRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutObjectLegalHoldRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putObjectLegalHoldResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putObjectLegalHold$83((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutObjectLockConfigurationResponse putObjectLockConfiguration(PutObjectLockConfigurationRequest putObjectLockConfigurationRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectLockConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectLockConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putObjectLockConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObjectLockConfiguration");
            PutObjectLockConfigurationResponse putObjectLockConfigurationResponse = (PutObjectLockConfigurationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObjectLockConfiguration").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putObjectLockConfigurationRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putObjectLockConfigurationRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutObjectLockConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putObjectLockConfigurationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putObjectLockConfiguration$84((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutObjectRetentionResponse putObjectRetention(PutObjectRetentionRequest putObjectRetentionRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectRetentionResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectRetentionRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putObjectRetentionRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObjectRetention");
            PutObjectRetentionResponse putObjectRetentionResponse = (PutObjectRetentionResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObjectRetention").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putObjectRetentionRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putObjectRetentionRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutObjectRetentionRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putObjectRetentionResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putObjectRetention$85((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutObjectTaggingResponse putObjectTagging(PutObjectTaggingRequest putObjectTaggingRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putObjectTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObjectTagging");
            PutObjectTaggingResponse putObjectTaggingResponse = (PutObjectTaggingResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObjectTagging").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putObjectTaggingRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putObjectTaggingRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutObjectTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putObjectTaggingResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putObjectTagging$86((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutPublicAccessBlockResponse putPublicAccessBlock(PutPublicAccessBlockRequest putPublicAccessBlockRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutPublicAccessBlockResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putPublicAccessBlockRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, putPublicAccessBlockRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutPublicAccessBlock");
            PutPublicAccessBlockResponse putPublicAccessBlockResponse = (PutPublicAccessBlockResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutPublicAccessBlock").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putPublicAccessBlockRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putPublicAccessBlockRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new PutPublicAccessBlockRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return putPublicAccessBlockResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$putPublicAccessBlock$87((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RestoreObjectResponse restoreObject(RestoreObjectRequest restoreObjectRequest) throws ObjectAlreadyInActiveTierErrorException, AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(RestoreObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)restoreObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, restoreObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"RestoreObject");
            RestoreObjectResponse restoreObjectResponse = (RestoreObjectResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("RestoreObject").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)restoreObjectRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(false).requestAlgorithm(restoreObjectRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withMarshaller((Marshaller)new RestoreObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return restoreObjectResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$restoreObject$88((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UploadPartResponse uploadPart(UploadPartRequest uploadPartRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(UploadPartResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)uploadPartRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, uploadPartRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"UploadPart");
            UploadPartResponse uploadPartResponse = (UploadPartResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("UploadPart").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)uploadPartRequest).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(false).requestAlgorithm(uploadPartRequest.checksumAlgorithmAsString()).isRequestStreaming(true).build()).withRequestBody(requestBody).withMarshaller((Marshaller)((StreamingRequestMarshaller.Builder)StreamingRequestMarshaller.builder().delegateMarshaller((Marshaller)new UploadPartRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory))).requestBody(requestBody).build()));
            return uploadPartResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$uploadPart$89((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UploadPartCopyResponse uploadPartCopy(UploadPartCopyRequest uploadPartCopyRequest) throws AwsServiceException, SdkClientException, S3Exception {
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(UploadPartCopyResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)uploadPartCopyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, uploadPartCopyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"UploadPartCopy");
            UploadPartCopyResponse uploadPartCopyResponse = (UploadPartCopyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("UploadPartCopy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)uploadPartCopyRequest).withMarshaller((Marshaller)new UploadPartCopyRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)));
            return uploadPartCopyResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$uploadPartCopy$90((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public WriteGetObjectResponseResponse writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException, S3Exception {
        writeGetObjectResponseRequest = this.applySignerOverride(writeGetObjectResponseRequest, (Signer)Aws4UnsignedPayloadSigner.create());
        HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(WriteGetObjectResponseResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)writeGetObjectResponseRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3Client.resolveMetricPublishers(clientConfiguration, writeGetObjectResponseRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"WriteGetObjectResponse");
            String hostPrefix = "{RequestRoute}.";
            HostnameValidator.validateHostnameCompliant((String)writeGetObjectResponseRequest.requestRoute(), (String)"RequestRoute", (String)"writeGetObjectResponseRequest");
            String resolvedHostExpression = String.format("%s.", writeGetObjectResponseRequest.requestRoute());
            WriteGetObjectResponseResponse writeGetObjectResponseResponse = (WriteGetObjectResponseResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("WriteGetObjectResponse").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).hostPrefixExpression(resolvedHostExpression).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)writeGetObjectResponseRequest).withRequestBody(requestBody).withMarshaller((Marshaller)((StreamingRequestMarshaller.Builder)((StreamingRequestMarshaller.Builder)StreamingRequestMarshaller.builder().delegateMarshaller((Marshaller)new WriteGetObjectResponseRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory))).requestBody(requestBody).transferEncoding(true)).build()));
            return writeGetObjectResponseResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultS3Client.lambda$writeGetObjectResponse$91((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    @Override
    public S3Utilities utilities() {
        return S3Utilities.create(this.clientConfiguration);
    }

    @Override
    public S3Waiter waiter() {
        return S3Waiter.builder().client(this).build();
    }

    private <T extends S3Request> T applySignerOverride(T request, Signer signer) {
        if (request.overrideConfiguration().flatMap(c -> c.signer()).isPresent()) {
            return request;
        }
        Consumer<AwsRequestOverrideConfiguration.Builder> signerOverride = b -> ((AwsRequestOverrideConfiguration.Builder)b.signer(signer)).build();
        AwsRequestOverrideConfiguration overrideConfiguration = request.overrideConfiguration().map(c -> ((AwsRequestOverrideConfiguration.Builder)c.toBuilder().applyMutation(signerOverride)).build()).orElse(((AwsRequestOverrideConfiguration.Builder)AwsRequestOverrideConfiguration.builder().applyMutation(signerOverride)).build());
        return (T)((Object)((S3Request)request.toBuilder().overrideConfiguration(overrideConfiguration).build()));
    }

    public final String serviceName() {
        return "s3";
    }

    private static List<MetricPublisher> resolveMetricPublishers(SdkClientConfiguration clientConfiguration, RequestOverrideConfiguration requestOverrideConfiguration) {
        List<MetricPublisher> publishers = null;
        if (requestOverrideConfiguration != null) {
            publishers = requestOverrideConfiguration.metricPublishers();
        }
        if (publishers == null || publishers.isEmpty()) {
            publishers = (List)clientConfiguration.option((ClientOption)SdkClientOption.METRIC_PUBLISHERS);
        }
        if (publishers == null) {
            publishers = Collections.emptyList();
        }
        return publishers;
    }

    private SdkClientConfiguration updateSdkClientConfiguration(SdkRequest request, SdkClientConfiguration clientConfiguration) {
        List plugins = request.overrideConfiguration().map(c -> c.plugins()).orElse(Collections.emptyList());
        if (plugins.isEmpty()) {
            return clientConfiguration;
        }
        S3ServiceClientConfigurationBuilder.BuilderInternal serviceConfigBuilder = S3ServiceClientConfigurationBuilder.builder(clientConfiguration.toBuilder());
        serviceConfigBuilder.overrideConfiguration(this.serviceClientConfiguration.overrideConfiguration());
        for (SdkPlugin plugin : plugins) {
            plugin.configureClient((SdkServiceClientConfiguration.Builder)serviceConfigBuilder);
        }
        return serviceConfigBuilder.buildSdkClientConfiguration();
    }

    private AwsS3ProtocolFactory init() {
        return ((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)AwsS3ProtocolFactory.builder().registerModeledException(ExceptionMetadata.builder().errorCode("NoSuchUpload").exceptionBuilderSupplier(NoSuchUploadException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidObjectState").exceptionBuilderSupplier(InvalidObjectStateException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("BucketAlreadyOwnedByYou").exceptionBuilderSupplier(BucketAlreadyOwnedByYouException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("NoSuchKey").exceptionBuilderSupplier(NoSuchKeyException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("ObjectAlreadyInActiveTierError").exceptionBuilderSupplier(ObjectAlreadyInActiveTierErrorException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("BucketAlreadyExists").exceptionBuilderSupplier(BucketAlreadyExistsException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("NoSuchBucket").exceptionBuilderSupplier(NoSuchBucketException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("ObjectNotInActiveTierError").exceptionBuilderSupplier(ObjectNotInActiveTierErrorException::builder).build())).clientConfiguration(this.clientConfiguration)).defaultServiceExceptionSupplier(S3Exception::builder)).build();
    }

    @Override
    public final S3ServiceClientConfiguration serviceClientConfiguration() {
        return this.serviceClientConfiguration;
    }

    public void close() {
        this.clientHandler.close();
    }

    private static /* synthetic */ void lambda$writeGetObjectResponse$91(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$uploadPartCopy$90(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$uploadPart$89(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$restoreObject$88(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putPublicAccessBlock$87(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObjectTagging$86(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObjectRetention$85(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObjectLockConfiguration$84(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObjectLegalHold$83(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObjectAcl$82(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObject$81(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketWebsite$80(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketVersioning$79(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketTagging$78(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketRequestPayment$77(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketReplication$76(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketPolicy$75(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketOwnershipControls$74(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketNotificationConfiguration$73(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketMetricsConfiguration$72(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketLogging$71(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketLifecycleConfiguration$70(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketInventoryConfiguration$69(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketIntelligentTieringConfiguration$68(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketEncryption$67(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketCors$66(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketAnalyticsConfiguration$65(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketAcl$64(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketAccelerateConfiguration$63(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listParts$62(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listObjectsV2$61(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listObjects$60(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listObjectVersions$59(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listMultipartUploads$58(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listBuckets$57(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listBucketMetricsConfigurations$56(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listBucketInventoryConfigurations$55(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listBucketIntelligentTieringConfigurations$54(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listBucketAnalyticsConfigurations$53(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$headObject$52(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$headBucket$51(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getPublicAccessBlock$50(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectTorrent$49(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectTagging$48(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectRetention$47(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectLockConfiguration$46(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectLegalHold$45(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectAttributes$44(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectAcl$43(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObject$42(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketWebsite$41(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketVersioning$40(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketTagging$39(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketRequestPayment$38(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketReplication$37(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketPolicyStatus$36(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketPolicy$35(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketOwnershipControls$34(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketNotificationConfiguration$33(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketMetricsConfiguration$32(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketLogging$31(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketLocation$30(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketLifecycleConfiguration$29(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketInventoryConfiguration$28(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketIntelligentTieringConfiguration$27(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketEncryption$26(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketCors$25(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketAnalyticsConfiguration$24(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketAcl$23(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketAccelerateConfiguration$22(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deletePublicAccessBlock$21(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteObjects$20(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteObjectTagging$19(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteObject$18(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketWebsite$17(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketTagging$16(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketReplication$15(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketPolicy$14(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketOwnershipControls$13(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketMetricsConfiguration$12(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketLifecycle$11(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketInventoryConfiguration$10(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketIntelligentTieringConfiguration$9(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketEncryption$8(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketCors$7(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketAnalyticsConfiguration$6(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucket$5(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$createMultipartUpload$4(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$createBucket$3(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$copyObject$2(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$completeMultipartUpload$1(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$abortMultipartUpload$0(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }
}

