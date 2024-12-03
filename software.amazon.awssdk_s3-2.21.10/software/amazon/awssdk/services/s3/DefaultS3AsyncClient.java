/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.Aws4UnsignedPayloadSigner
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.awscore.client.handler.AwsAsyncClientHandler
 *  software.amazon.awssdk.awscore.eventstream.EventStreamAsyncResponseTransformer
 *  software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandler
 *  software.amazon.awssdk.awscore.eventstream.EventStreamTaggedUnionPojoSupplier
 *  software.amazon.awssdk.awscore.eventstream.RestEventStreamAsyncResponseTransformer
 *  software.amazon.awssdk.awscore.internal.AwsProtocolMetadata
 *  software.amazon.awssdk.awscore.internal.AwsServiceProtocol
 *  software.amazon.awssdk.core.RequestOverrideConfiguration
 *  software.amazon.awssdk.core.SdkPlugin
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.SdkPojoBuilder
 *  software.amazon.awssdk.core.SdkProtocolMetadata
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.SdkServiceClientConfiguration$Builder
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.core.async.AsyncResponseTransformer
 *  software.amazon.awssdk.core.async.AsyncResponseTransformerUtils
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.core.client.handler.AsyncClientHandler
 *  software.amazon.awssdk.core.client.handler.ClientExecutionParams
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.trait.HttpChecksum
 *  software.amazon.awssdk.core.metrics.CoreMetric
 *  software.amazon.awssdk.core.runtime.transform.AsyncStreamingRequestMarshaller
 *  software.amazon.awssdk.core.runtime.transform.AsyncStreamingRequestMarshaller$Builder
 *  software.amazon.awssdk.core.runtime.transform.Marshaller
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.metrics.MetricPublisher
 *  software.amazon.awssdk.metrics.NoOpMetricCollector
 *  software.amazon.awssdk.protocols.core.ExceptionMetadata
 *  software.amazon.awssdk.protocols.xml.AwsS3ProtocolFactory
 *  software.amazon.awssdk.protocols.xml.AwsS3ProtocolFactory$Builder
 *  software.amazon.awssdk.protocols.xml.AwsXmlProtocolFactory
 *  software.amazon.awssdk.protocols.xml.XmlOperationMetadata
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.HostnameValidator
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.services.s3;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.Aws4UnsignedPayloadSigner;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.awscore.client.handler.AwsAsyncClientHandler;
import software.amazon.awssdk.awscore.eventstream.EventStreamAsyncResponseTransformer;
import software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandler;
import software.amazon.awssdk.awscore.eventstream.EventStreamTaggedUnionPojoSupplier;
import software.amazon.awssdk.awscore.eventstream.RestEventStreamAsyncResponseTransformer;
import software.amazon.awssdk.awscore.internal.AwsProtocolMetadata;
import software.amazon.awssdk.awscore.internal.AwsServiceProtocol;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.SdkPojoBuilder;
import software.amazon.awssdk.core.SdkProtocolMetadata;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkServiceClientConfiguration;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.AsyncResponseTransformerUtils;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.client.handler.AsyncClientHandler;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.interceptor.trait.HttpChecksum;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.runtime.transform.AsyncStreamingRequestMarshaller;
import software.amazon.awssdk.core.runtime.transform.Marshaller;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.metrics.NoOpMetricCollector;
import software.amazon.awssdk.protocols.core.ExceptionMetadata;
import software.amazon.awssdk.protocols.xml.AwsS3ProtocolFactory;
import software.amazon.awssdk.protocols.xml.AwsXmlProtocolFactory;
import software.amazon.awssdk.protocols.xml.XmlOperationMetadata;
import software.amazon.awssdk.services.s3.S3AsyncClient;
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
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentRequest;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponse;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;
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
import software.amazon.awssdk.services.s3.transform.SelectObjectContentRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.UploadPartCopyRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.UploadPartRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.WriteGetObjectResponseRequestMarshaller;
import software.amazon.awssdk.services.s3.waiters.S3AsyncWaiter;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.HostnameValidator;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
final class DefaultS3AsyncClient
implements S3AsyncClient {
    private static final Logger log = LoggerFactory.getLogger(DefaultS3AsyncClient.class);
    private static final AwsProtocolMetadata protocolMetadata = AwsProtocolMetadata.builder().serviceProtocol(AwsServiceProtocol.REST_XML).build();
    private final AsyncClientHandler clientHandler;
    private final AwsS3ProtocolFactory protocolFactory;
    private final SdkClientConfiguration clientConfiguration;
    private final S3ServiceClientConfiguration serviceClientConfiguration;
    private final ScheduledExecutorService executorService;
    private final Executor executor;

    protected DefaultS3AsyncClient(S3ServiceClientConfiguration serviceClientConfiguration, SdkClientConfiguration clientConfiguration) {
        this.clientHandler = new AwsAsyncClientHandler(clientConfiguration);
        this.clientConfiguration = clientConfiguration;
        this.serviceClientConfiguration = serviceClientConfiguration;
        this.protocolFactory = this.init();
        this.executor = (Executor)clientConfiguration.option((ClientOption)SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR);
        this.executorService = (ScheduledExecutorService)clientConfiguration.option((ClientOption)SdkClientOption.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public S3Utilities utilities() {
        return S3Utilities.create(this.clientConfiguration);
    }

    @Override
    public CompletableFuture<AbortMultipartUploadResponse> abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)abortMultipartUploadRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, abortMultipartUploadRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"AbortMultipartUpload");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(AbortMultipartUploadResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("AbortMultipartUpload").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new AbortMultipartUploadRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)abortMultipartUploadRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$abortMultipartUpload$1(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$abortMultipartUpload$2((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<CompleteMultipartUploadResponse> completeMultipartUpload(CompleteMultipartUploadRequest completeMultipartUploadRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)completeMultipartUploadRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, completeMultipartUploadRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CompleteMultipartUpload");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(CompleteMultipartUploadResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("CompleteMultipartUpload").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new CompleteMultipartUploadRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)completeMultipartUploadRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$completeMultipartUpload$4(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$completeMultipartUpload$5((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<CopyObjectResponse> copyObject(CopyObjectRequest copyObjectRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)copyObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, copyObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CopyObject");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(CopyObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("CopyObject").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new CopyObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)copyObjectRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$copyObject$7(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$copyObject$8((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<CreateBucketResponse> createBucket(CreateBucketRequest createBucketRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)createBucketRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, createBucketRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CreateBucket");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(CreateBucketResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("CreateBucket").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new CreateBucketRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)createBucketRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$createBucket$10(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$createBucket$11((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<CreateMultipartUploadResponse> createMultipartUpload(CreateMultipartUploadRequest createMultipartUploadRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)createMultipartUploadRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, createMultipartUploadRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CreateMultipartUpload");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(CreateMultipartUploadResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("CreateMultipartUpload").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new CreateMultipartUploadRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)createMultipartUploadRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$createMultipartUpload$13(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$createMultipartUpload$14((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketResponse> deleteBucket(DeleteBucketRequest deleteBucketRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucket");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucket").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucket$16(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucket$17((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketAnalyticsConfigurationResponse> deleteBucketAnalyticsConfiguration(DeleteBucketAnalyticsConfigurationRequest deleteBucketAnalyticsConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketAnalyticsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketAnalyticsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketAnalyticsConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketAnalyticsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketAnalyticsConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketAnalyticsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketAnalyticsConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketAnalyticsConfiguration$19(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketAnalyticsConfiguration$20((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketCorsResponse> deleteBucketCors(DeleteBucketCorsRequest deleteBucketCorsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketCorsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketCorsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketCors");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketCorsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketCors").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketCorsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketCorsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketCors$22(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketCors$23((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketEncryptionResponse> deleteBucketEncryption(DeleteBucketEncryptionRequest deleteBucketEncryptionRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketEncryptionRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketEncryptionRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketEncryption");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketEncryptionResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketEncryption").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketEncryptionRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketEncryptionRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketEncryption$25(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketEncryption$26((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketIntelligentTieringConfigurationResponse> deleteBucketIntelligentTieringConfiguration(DeleteBucketIntelligentTieringConfigurationRequest deleteBucketIntelligentTieringConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketIntelligentTieringConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketIntelligentTieringConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketIntelligentTieringConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketIntelligentTieringConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketIntelligentTieringConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketIntelligentTieringConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketIntelligentTieringConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketIntelligentTieringConfiguration$28(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketIntelligentTieringConfiguration$29((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketInventoryConfigurationResponse> deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest deleteBucketInventoryConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketInventoryConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketInventoryConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketInventoryConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketInventoryConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketInventoryConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketInventoryConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketInventoryConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketInventoryConfiguration$31(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketInventoryConfiguration$32((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketLifecycleResponse> deleteBucketLifecycle(DeleteBucketLifecycleRequest deleteBucketLifecycleRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketLifecycleRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketLifecycleRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketLifecycle");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketLifecycleResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketLifecycle").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketLifecycleRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketLifecycleRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketLifecycle$34(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketLifecycle$35((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketMetricsConfigurationResponse> deleteBucketMetricsConfiguration(DeleteBucketMetricsConfigurationRequest deleteBucketMetricsConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketMetricsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketMetricsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketMetricsConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketMetricsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketMetricsConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketMetricsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketMetricsConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketMetricsConfiguration$37(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketMetricsConfiguration$38((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketOwnershipControlsResponse> deleteBucketOwnershipControls(DeleteBucketOwnershipControlsRequest deleteBucketOwnershipControlsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketOwnershipControlsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketOwnershipControlsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketOwnershipControls");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketOwnershipControlsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketOwnershipControls").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketOwnershipControlsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketOwnershipControlsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketOwnershipControls$40(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketOwnershipControls$41((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketPolicyResponse> deleteBucketPolicy(DeleteBucketPolicyRequest deleteBucketPolicyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketPolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketPolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketPolicy");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketPolicyResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketPolicy").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketPolicyRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketPolicyRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketPolicy$43(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketPolicy$44((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketReplicationResponse> deleteBucketReplication(DeleteBucketReplicationRequest deleteBucketReplicationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketReplicationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketReplicationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketReplication");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketReplicationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketReplication").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketReplicationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketReplicationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketReplication$46(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketReplication$47((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketTaggingResponse> deleteBucketTagging(DeleteBucketTaggingRequest deleteBucketTaggingRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketTagging");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketTagging").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketTaggingRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketTagging$49(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketTagging$50((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteBucketWebsiteResponse> deleteBucketWebsite(DeleteBucketWebsiteRequest deleteBucketWebsiteRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteBucketWebsiteRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteBucketWebsiteRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteBucketWebsite");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteBucketWebsiteResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteBucketWebsite").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteBucketWebsiteRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteBucketWebsiteRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteBucketWebsite$52(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteBucketWebsite$53((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteObjectResponse> deleteObject(DeleteObjectRequest deleteObjectRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteObject");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteObject").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteObjectRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteObject$55(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteObject$56((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteObjectTaggingResponse> deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteObjectTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteObjectTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteObjectTagging");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteObjectTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteObjectTagging").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteObjectTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteObjectTaggingRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteObjectTagging$58(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteObjectTagging$59((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteObjectsResponse> deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteObjectsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deleteObjectsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteObjects");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeleteObjectsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteObjects").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteObjectsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(deleteObjectsRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)deleteObjectsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deleteObjects$61(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deleteObjects$62((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeletePublicAccessBlockResponse> deletePublicAccessBlock(DeletePublicAccessBlockRequest deletePublicAccessBlockRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deletePublicAccessBlockRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, deletePublicAccessBlockRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeletePublicAccessBlock");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(DeletePublicAccessBlockResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeletePublicAccessBlock").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeletePublicAccessBlockRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deletePublicAccessBlockRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$deletePublicAccessBlock$64(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$deletePublicAccessBlock$65((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketAccelerateConfigurationResponse> getBucketAccelerateConfiguration(GetBucketAccelerateConfigurationRequest getBucketAccelerateConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketAccelerateConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketAccelerateConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketAccelerateConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketAccelerateConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketAccelerateConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketAccelerateConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketAccelerateConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketAccelerateConfiguration$67(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketAccelerateConfiguration$68((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketAclResponse> getBucketAcl(GetBucketAclRequest getBucketAclRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketAclRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketAclRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketAcl");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketAclResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketAcl").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketAclRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketAclRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketAcl$70(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketAcl$71((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketAnalyticsConfigurationResponse> getBucketAnalyticsConfiguration(GetBucketAnalyticsConfigurationRequest getBucketAnalyticsConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketAnalyticsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketAnalyticsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketAnalyticsConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketAnalyticsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketAnalyticsConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketAnalyticsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketAnalyticsConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketAnalyticsConfiguration$73(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketAnalyticsConfiguration$74((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketCorsResponse> getBucketCors(GetBucketCorsRequest getBucketCorsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketCorsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketCorsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketCors");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketCorsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketCors").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketCorsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketCorsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketCors$76(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketCors$77((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketEncryptionResponse> getBucketEncryption(GetBucketEncryptionRequest getBucketEncryptionRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketEncryptionRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketEncryptionRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketEncryption");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketEncryptionResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketEncryption").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketEncryptionRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketEncryptionRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketEncryption$79(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketEncryption$80((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketIntelligentTieringConfigurationResponse> getBucketIntelligentTieringConfiguration(GetBucketIntelligentTieringConfigurationRequest getBucketIntelligentTieringConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketIntelligentTieringConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketIntelligentTieringConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketIntelligentTieringConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketIntelligentTieringConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketIntelligentTieringConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketIntelligentTieringConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketIntelligentTieringConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketIntelligentTieringConfiguration$82(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketIntelligentTieringConfiguration$83((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketInventoryConfigurationResponse> getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest getBucketInventoryConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketInventoryConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketInventoryConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketInventoryConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketInventoryConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketInventoryConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketInventoryConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketInventoryConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketInventoryConfiguration$85(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketInventoryConfiguration$86((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketLifecycleConfigurationResponse> getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketLifecycleConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketLifecycleConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketLifecycleConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketLifecycleConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketLifecycleConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketLifecycleConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketLifecycleConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketLifecycleConfiguration$88(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketLifecycleConfiguration$89((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketLocationResponse> getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketLocationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketLocationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketLocation");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketLocationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketLocation").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketLocationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketLocationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketLocation$91(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketLocation$92((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketLoggingResponse> getBucketLogging(GetBucketLoggingRequest getBucketLoggingRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketLoggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketLoggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketLogging");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketLoggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketLogging").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketLoggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketLoggingRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketLogging$94(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketLogging$95((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketMetricsConfigurationResponse> getBucketMetricsConfiguration(GetBucketMetricsConfigurationRequest getBucketMetricsConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketMetricsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketMetricsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketMetricsConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketMetricsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketMetricsConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketMetricsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketMetricsConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketMetricsConfiguration$97(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketMetricsConfiguration$98((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketNotificationConfigurationResponse> getBucketNotificationConfiguration(GetBucketNotificationConfigurationRequest getBucketNotificationConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketNotificationConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketNotificationConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketNotificationConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketNotificationConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketNotificationConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketNotificationConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketNotificationConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketNotificationConfiguration$100(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketNotificationConfiguration$101((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketOwnershipControlsResponse> getBucketOwnershipControls(GetBucketOwnershipControlsRequest getBucketOwnershipControlsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketOwnershipControlsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketOwnershipControlsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketOwnershipControls");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketOwnershipControlsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketOwnershipControls").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketOwnershipControlsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketOwnershipControlsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketOwnershipControls$103(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketOwnershipControls$104((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketPolicyResponse> getBucketPolicy(GetBucketPolicyRequest getBucketPolicyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketPolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketPolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketPolicy");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketPolicyResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketPolicy").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketPolicyRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketPolicyRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketPolicy$106(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketPolicy$107((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketPolicyStatusResponse> getBucketPolicyStatus(GetBucketPolicyStatusRequest getBucketPolicyStatusRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketPolicyStatusRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketPolicyStatusRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketPolicyStatus");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketPolicyStatusResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketPolicyStatus").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketPolicyStatusRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketPolicyStatusRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketPolicyStatus$109(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketPolicyStatus$110((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketReplicationResponse> getBucketReplication(GetBucketReplicationRequest getBucketReplicationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketReplicationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketReplicationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketReplication");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketReplicationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketReplication").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketReplicationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketReplicationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketReplication$112(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketReplication$113((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketRequestPaymentResponse> getBucketRequestPayment(GetBucketRequestPaymentRequest getBucketRequestPaymentRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketRequestPaymentRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketRequestPaymentRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketRequestPayment");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketRequestPaymentResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketRequestPayment").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketRequestPaymentRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketRequestPaymentRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketRequestPayment$115(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketRequestPayment$116((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketTaggingResponse> getBucketTagging(GetBucketTaggingRequest getBucketTaggingRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketTagging");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketTagging").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketTaggingRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketTagging$118(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketTagging$119((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketVersioningResponse> getBucketVersioning(GetBucketVersioningRequest getBucketVersioningRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketVersioningRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketVersioningRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketVersioning");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketVersioningResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketVersioning").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketVersioningRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketVersioningRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketVersioning$121(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketVersioning$122((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetBucketWebsiteResponse> getBucketWebsite(GetBucketWebsiteRequest getBucketWebsiteRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getBucketWebsiteRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getBucketWebsiteRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetBucketWebsite");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetBucketWebsiteResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetBucketWebsite").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetBucketWebsiteRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getBucketWebsiteRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getBucketWebsite$124(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getBucketWebsite$125((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public <ReturnT> CompletableFuture<ReturnT> getObject(GetObjectRequest getObjectRequest, AsyncResponseTransformer<GetObjectResponse, ReturnT> asyncResponseTransformer) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObject");
            Pair pair = AsyncResponseTransformerUtils.wrapWithEndOfStreamFuture(asyncResponseTransformer);
            asyncResponseTransformer = (AsyncResponseTransformer)pair.left();
            CompletableFuture endOfStreamFuture = (CompletableFuture)pair.right();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(true));
            HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObject").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(false).requestValidationMode(getObjectRequest.checksumModeAsString()).responseAlgorithms(new String[]{"CRC32", "CRC32C", "SHA256", "SHA1"}).isRequestStreaming(false).build()).withInput((SdkRequest)getObjectRequest), asyncResponseTransformer);
            CompletionStage whenCompleteFuture = null;
            AsyncResponseTransformer finalAsyncResponseTransformer = asyncResponseTransformer;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getObject$129(finalAsyncResponseTransformer, endOfStreamFuture, metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
        }
        catch (Throwable t) {
            AsyncResponseTransformer finalAsyncResponseTransformer = asyncResponseTransformer;
            FunctionalUtils.runAndLogError((Logger)log, (String)"Exception thrown in exceptionOccurred callback, ignoring", () -> finalAsyncResponseTransformer.exceptionOccurred(t));
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getObject$131((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetObjectAclResponse> getObjectAcl(GetObjectAclRequest getObjectAclRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectAclRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getObjectAclRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectAcl");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectAclResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectAcl").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetObjectAclRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getObjectAclRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getObjectAcl$133(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getObjectAcl$134((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetObjectAttributesResponse> getObjectAttributes(GetObjectAttributesRequest getObjectAttributesRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectAttributesRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getObjectAttributesRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectAttributes");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectAttributesResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectAttributes").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetObjectAttributesRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getObjectAttributesRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getObjectAttributes$136(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getObjectAttributes$137((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetObjectLegalHoldResponse> getObjectLegalHold(GetObjectLegalHoldRequest getObjectLegalHoldRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectLegalHoldRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getObjectLegalHoldRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectLegalHold");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectLegalHoldResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectLegalHold").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetObjectLegalHoldRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getObjectLegalHoldRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getObjectLegalHold$139(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getObjectLegalHold$140((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetObjectLockConfigurationResponse> getObjectLockConfiguration(GetObjectLockConfigurationRequest getObjectLockConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectLockConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getObjectLockConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectLockConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectLockConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectLockConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetObjectLockConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getObjectLockConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getObjectLockConfiguration$142(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getObjectLockConfiguration$143((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetObjectRetentionResponse> getObjectRetention(GetObjectRetentionRequest getObjectRetentionRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectRetentionRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getObjectRetentionRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectRetention");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectRetentionResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectRetention").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetObjectRetentionRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getObjectRetentionRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getObjectRetention$145(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getObjectRetention$146((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetObjectTaggingResponse> getObjectTagging(GetObjectTaggingRequest getObjectTaggingRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getObjectTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectTagging");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetObjectTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectTagging").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetObjectTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getObjectTaggingRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getObjectTagging$148(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getObjectTagging$149((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public <ReturnT> CompletableFuture<ReturnT> getObjectTorrent(GetObjectTorrentRequest getObjectTorrentRequest, AsyncResponseTransformer<GetObjectTorrentResponse, ReturnT> asyncResponseTransformer) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getObjectTorrentRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getObjectTorrentRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetObjectTorrent");
            Pair pair = AsyncResponseTransformerUtils.wrapWithEndOfStreamFuture(asyncResponseTransformer);
            asyncResponseTransformer = (AsyncResponseTransformer)pair.left();
            CompletableFuture endOfStreamFuture = (CompletableFuture)pair.right();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetObjectTorrentResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(true));
            HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetObjectTorrent").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetObjectTorrentRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getObjectTorrentRequest), asyncResponseTransformer);
            CompletionStage whenCompleteFuture = null;
            AsyncResponseTransformer finalAsyncResponseTransformer = asyncResponseTransformer;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getObjectTorrent$153(finalAsyncResponseTransformer, endOfStreamFuture, metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
        }
        catch (Throwable t) {
            AsyncResponseTransformer finalAsyncResponseTransformer = asyncResponseTransformer;
            FunctionalUtils.runAndLogError((Logger)log, (String)"Exception thrown in exceptionOccurred callback, ignoring", () -> finalAsyncResponseTransformer.exceptionOccurred(t));
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getObjectTorrent$155((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetPublicAccessBlockResponse> getPublicAccessBlock(GetPublicAccessBlockRequest getPublicAccessBlockRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getPublicAccessBlockRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, getPublicAccessBlockRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetPublicAccessBlock");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(GetPublicAccessBlockResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetPublicAccessBlock").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetPublicAccessBlockRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getPublicAccessBlockRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$getPublicAccessBlock$157(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$getPublicAccessBlock$158((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<HeadBucketResponse> headBucket(HeadBucketRequest headBucketRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)headBucketRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, headBucketRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"HeadBucket");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(HeadBucketResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("HeadBucket").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new HeadBucketRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)headBucketRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$headBucket$160(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$headBucket$161((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<HeadObjectResponse> headObject(HeadObjectRequest headObjectRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)headObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, headObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"HeadObject");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(HeadObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("HeadObject").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new HeadObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)headObjectRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$headObject$163(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$headObject$164((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListBucketAnalyticsConfigurationsResponse> listBucketAnalyticsConfigurations(ListBucketAnalyticsConfigurationsRequest listBucketAnalyticsConfigurationsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listBucketAnalyticsConfigurationsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, listBucketAnalyticsConfigurationsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListBucketAnalyticsConfigurations");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListBucketAnalyticsConfigurationsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListBucketAnalyticsConfigurations").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListBucketAnalyticsConfigurationsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listBucketAnalyticsConfigurationsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$listBucketAnalyticsConfigurations$166(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$listBucketAnalyticsConfigurations$167((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListBucketIntelligentTieringConfigurationsResponse> listBucketIntelligentTieringConfigurations(ListBucketIntelligentTieringConfigurationsRequest listBucketIntelligentTieringConfigurationsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listBucketIntelligentTieringConfigurationsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, listBucketIntelligentTieringConfigurationsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListBucketIntelligentTieringConfigurations");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListBucketIntelligentTieringConfigurationsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListBucketIntelligentTieringConfigurations").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListBucketIntelligentTieringConfigurationsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listBucketIntelligentTieringConfigurationsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$listBucketIntelligentTieringConfigurations$169(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$listBucketIntelligentTieringConfigurations$170((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListBucketInventoryConfigurationsResponse> listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest listBucketInventoryConfigurationsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listBucketInventoryConfigurationsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, listBucketInventoryConfigurationsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListBucketInventoryConfigurations");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListBucketInventoryConfigurationsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListBucketInventoryConfigurations").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListBucketInventoryConfigurationsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listBucketInventoryConfigurationsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$listBucketInventoryConfigurations$172(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$listBucketInventoryConfigurations$173((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListBucketMetricsConfigurationsResponse> listBucketMetricsConfigurations(ListBucketMetricsConfigurationsRequest listBucketMetricsConfigurationsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listBucketMetricsConfigurationsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, listBucketMetricsConfigurationsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListBucketMetricsConfigurations");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListBucketMetricsConfigurationsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListBucketMetricsConfigurations").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListBucketMetricsConfigurationsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listBucketMetricsConfigurationsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$listBucketMetricsConfigurations$175(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$listBucketMetricsConfigurations$176((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListBucketsResponse> listBuckets(ListBucketsRequest listBucketsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listBucketsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, listBucketsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListBuckets");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListBucketsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListBuckets").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListBucketsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listBucketsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$listBuckets$178(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$listBuckets$179((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListMultipartUploadsResponse> listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listMultipartUploadsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, listMultipartUploadsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListMultipartUploads");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListMultipartUploadsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListMultipartUploads").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListMultipartUploadsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listMultipartUploadsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$listMultipartUploads$181(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$listMultipartUploads$182((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListObjectVersionsResponse> listObjectVersions(ListObjectVersionsRequest listObjectVersionsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listObjectVersionsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, listObjectVersionsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListObjectVersions");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListObjectVersionsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListObjectVersions").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListObjectVersionsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listObjectVersionsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$listObjectVersions$184(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$listObjectVersions$185((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListObjectsResponse> listObjects(ListObjectsRequest listObjectsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listObjectsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, listObjectsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListObjects");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListObjectsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListObjects").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListObjectsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listObjectsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$listObjects$187(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$listObjects$188((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListObjectsV2Response> listObjectsV2(ListObjectsV2Request listObjectsV2Request) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listObjectsV2Request, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, listObjectsV2Request.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListObjectsV2");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListObjectsV2Response::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListObjectsV2").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListObjectsV2RequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listObjectsV2Request));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$listObjectsV2$190(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$listObjectsV2$191((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListPartsResponse> listParts(ListPartsRequest listPartsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listPartsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, listPartsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListParts");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(ListPartsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListParts").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListPartsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listPartsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$listParts$193(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$listParts$194((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketAccelerateConfigurationResponse> putBucketAccelerateConfiguration(PutBucketAccelerateConfigurationRequest putBucketAccelerateConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketAccelerateConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketAccelerateConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketAccelerateConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketAccelerateConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketAccelerateConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketAccelerateConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(false).requestAlgorithm(putBucketAccelerateConfigurationRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketAccelerateConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketAccelerateConfiguration$196(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketAccelerateConfiguration$197((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketAclResponse> putBucketAcl(PutBucketAclRequest putBucketAclRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketAclRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketAclRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketAcl");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketAclResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketAcl").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketAclRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketAclRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketAclRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketAcl$199(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketAcl$200((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketAnalyticsConfigurationResponse> putBucketAnalyticsConfiguration(PutBucketAnalyticsConfigurationRequest putBucketAnalyticsConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketAnalyticsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketAnalyticsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketAnalyticsConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketAnalyticsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketAnalyticsConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketAnalyticsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)putBucketAnalyticsConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketAnalyticsConfiguration$202(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketAnalyticsConfiguration$203((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketCorsResponse> putBucketCors(PutBucketCorsRequest putBucketCorsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketCorsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketCorsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketCors");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketCorsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketCors").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketCorsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketCorsRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketCorsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketCors$205(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketCors$206((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketEncryptionResponse> putBucketEncryption(PutBucketEncryptionRequest putBucketEncryptionRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketEncryptionRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketEncryptionRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketEncryption");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketEncryptionResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketEncryption").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketEncryptionRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketEncryptionRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketEncryptionRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketEncryption$208(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketEncryption$209((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketIntelligentTieringConfigurationResponse> putBucketIntelligentTieringConfiguration(PutBucketIntelligentTieringConfigurationRequest putBucketIntelligentTieringConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketIntelligentTieringConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketIntelligentTieringConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketIntelligentTieringConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketIntelligentTieringConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketIntelligentTieringConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketIntelligentTieringConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)putBucketIntelligentTieringConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketIntelligentTieringConfiguration$211(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketIntelligentTieringConfiguration$212((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketInventoryConfigurationResponse> putBucketInventoryConfiguration(PutBucketInventoryConfigurationRequest putBucketInventoryConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketInventoryConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketInventoryConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketInventoryConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketInventoryConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketInventoryConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketInventoryConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)putBucketInventoryConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketInventoryConfiguration$214(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketInventoryConfiguration$215((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketLifecycleConfigurationResponse> putBucketLifecycleConfiguration(PutBucketLifecycleConfigurationRequest putBucketLifecycleConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketLifecycleConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketLifecycleConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketLifecycleConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketLifecycleConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketLifecycleConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketLifecycleConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketLifecycleConfigurationRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketLifecycleConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketLifecycleConfiguration$217(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketLifecycleConfiguration$218((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketLoggingResponse> putBucketLogging(PutBucketLoggingRequest putBucketLoggingRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketLoggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketLoggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketLogging");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketLoggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketLogging").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketLoggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketLoggingRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketLoggingRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketLogging$220(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketLogging$221((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketMetricsConfigurationResponse> putBucketMetricsConfiguration(PutBucketMetricsConfigurationRequest putBucketMetricsConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketMetricsConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketMetricsConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketMetricsConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketMetricsConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketMetricsConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketMetricsConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)putBucketMetricsConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketMetricsConfiguration$223(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketMetricsConfiguration$224((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketNotificationConfigurationResponse> putBucketNotificationConfiguration(PutBucketNotificationConfigurationRequest putBucketNotificationConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketNotificationConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketNotificationConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketNotificationConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketNotificationConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketNotificationConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketNotificationConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)putBucketNotificationConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketNotificationConfiguration$226(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketNotificationConfiguration$227((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketOwnershipControlsResponse> putBucketOwnershipControls(PutBucketOwnershipControlsRequest putBucketOwnershipControlsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketOwnershipControlsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketOwnershipControlsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketOwnershipControls");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketOwnershipControlsResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketOwnershipControls").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketOwnershipControlsRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketOwnershipControlsRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketOwnershipControls$229(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketOwnershipControls$230((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketPolicyResponse> putBucketPolicy(PutBucketPolicyRequest putBucketPolicyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketPolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketPolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketPolicy");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketPolicyResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketPolicy").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketPolicyRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketPolicyRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketPolicyRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketPolicy$232(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketPolicy$233((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketReplicationResponse> putBucketReplication(PutBucketReplicationRequest putBucketReplicationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketReplicationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketReplicationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketReplication");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketReplicationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketReplication").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketReplicationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketReplicationRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketReplicationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketReplication$235(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketReplication$236((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketRequestPaymentResponse> putBucketRequestPayment(PutBucketRequestPaymentRequest putBucketRequestPaymentRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketRequestPaymentRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketRequestPaymentRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketRequestPayment");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketRequestPaymentResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketRequestPayment").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketRequestPaymentRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketRequestPaymentRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketRequestPaymentRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketRequestPayment$238(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketRequestPayment$239((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketTaggingResponse> putBucketTagging(PutBucketTaggingRequest putBucketTaggingRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketTagging");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketTagging").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketTaggingRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketTaggingRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketTagging$241(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketTagging$242((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketVersioningResponse> putBucketVersioning(PutBucketVersioningRequest putBucketVersioningRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketVersioningRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketVersioningRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketVersioning");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketVersioningResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketVersioning").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketVersioningRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketVersioningRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketVersioningRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketVersioning$244(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketVersioning$245((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutBucketWebsiteResponse> putBucketWebsite(PutBucketWebsiteRequest putBucketWebsiteRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putBucketWebsiteRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putBucketWebsiteRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutBucketWebsite");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutBucketWebsiteResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutBucketWebsite").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutBucketWebsiteRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putBucketWebsiteRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putBucketWebsiteRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putBucketWebsite$247(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putBucketWebsite$248((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutObjectResponse> putObject(PutObjectRequest putObjectRequest, AsyncRequestBody requestBody) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObject");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObject").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)((AsyncStreamingRequestMarshaller.Builder)AsyncStreamingRequestMarshaller.builder().delegateMarshaller((Marshaller)new PutObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory))).asyncRequestBody(requestBody).build()).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withAsyncRequestBody(requestBody).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(false).requestAlgorithm(putObjectRequest.checksumAlgorithmAsString()).isRequestStreaming(true).build()).withInput((SdkRequest)putObjectRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putObject$250(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putObject$251((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutObjectAclResponse> putObjectAcl(PutObjectAclRequest putObjectAclRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectAclRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putObjectAclRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObjectAcl");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectAclResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObjectAcl").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutObjectAclRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putObjectAclRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putObjectAclRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putObjectAcl$253(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putObjectAcl$254((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutObjectLegalHoldResponse> putObjectLegalHold(PutObjectLegalHoldRequest putObjectLegalHoldRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectLegalHoldRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putObjectLegalHoldRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObjectLegalHold");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectLegalHoldResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObjectLegalHold").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutObjectLegalHoldRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putObjectLegalHoldRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putObjectLegalHoldRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putObjectLegalHold$256(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putObjectLegalHold$257((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutObjectLockConfigurationResponse> putObjectLockConfiguration(PutObjectLockConfigurationRequest putObjectLockConfigurationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectLockConfigurationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putObjectLockConfigurationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObjectLockConfiguration");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectLockConfigurationResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObjectLockConfiguration").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutObjectLockConfigurationRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putObjectLockConfigurationRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putObjectLockConfigurationRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putObjectLockConfiguration$259(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putObjectLockConfiguration$260((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutObjectRetentionResponse> putObjectRetention(PutObjectRetentionRequest putObjectRetentionRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectRetentionRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putObjectRetentionRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObjectRetention");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectRetentionResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObjectRetention").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutObjectRetentionRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putObjectRetentionRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putObjectRetentionRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putObjectRetention$262(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putObjectRetention$263((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutObjectTaggingResponse> putObjectTagging(PutObjectTaggingRequest putObjectTaggingRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putObjectTaggingRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putObjectTaggingRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutObjectTagging");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutObjectTaggingResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutObjectTagging").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutObjectTaggingRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putObjectTaggingRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putObjectTaggingRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putObjectTagging$265(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putObjectTagging$266((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutPublicAccessBlockResponse> putPublicAccessBlock(PutPublicAccessBlockRequest putPublicAccessBlockRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putPublicAccessBlockRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, putPublicAccessBlockRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutPublicAccessBlock");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(PutPublicAccessBlockResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutPublicAccessBlock").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutPublicAccessBlockRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(true).requestAlgorithm(putPublicAccessBlockRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)putPublicAccessBlockRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$putPublicAccessBlock$268(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$putPublicAccessBlock$269((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<RestoreObjectResponse> restoreObject(RestoreObjectRequest restoreObjectRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)restoreObjectRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, restoreObjectRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"RestoreObject");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(RestoreObjectResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("RestoreObject").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new RestoreObjectRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(false).requestAlgorithm(restoreObjectRequest.checksumAlgorithmAsString()).isRequestStreaming(false).build()).withInput((SdkRequest)restoreObjectRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$restoreObject$271(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$restoreObject$272((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<Void> selectObjectContent(SelectObjectContentRequest selectObjectContentRequest, SelectObjectContentResponseHandler asyncResponseHandler) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)selectObjectContentRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, selectObjectContentRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"SelectObjectContent");
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(SelectObjectContentResponse::builder, XmlOperationMetadata.builder().hasStreamingSuccessResponse(true).build());
            HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
            HttpResponseHandler eventResponseHandler = this.protocolFactory.createResponseHandler((Function)EventStreamTaggedUnionPojoSupplier.builder().putSdkPojoSupplier("Records", SelectObjectContentEventStream::recordsBuilder).putSdkPojoSupplier("Stats", SelectObjectContentEventStream::statsBuilder).putSdkPojoSupplier("Progress", SelectObjectContentEventStream::progressBuilder).putSdkPojoSupplier("Cont", SelectObjectContentEventStream::contBuilder).putSdkPojoSupplier("End", SelectObjectContentEventStream::endBuilder).defaultSdkPojoSupplier(() -> new SdkPojoBuilder((SdkPojo)SelectObjectContentEventStream.UNKNOWN)).build(), XmlOperationMetadata.builder().hasStreamingSuccessResponse(false).build());
            CompletableFuture eventStreamTransformFuture = new CompletableFuture();
            EventStreamAsyncResponseTransformer asyncResponseTransformer = EventStreamAsyncResponseTransformer.builder().eventStreamResponseHandler((EventStreamResponseHandler)asyncResponseHandler).eventResponseHandler(eventResponseHandler).initialResponseHandler(responseHandler).exceptionResponseHandler(errorResponseHandler).future(eventStreamTransformFuture).executor(this.executor).serviceName(this.serviceName()).build();
            RestEventStreamAsyncResponseTransformer restAsyncResponseTransformer = RestEventStreamAsyncResponseTransformer.builder().eventStreamAsyncResponseTransformer(asyncResponseTransformer).eventStreamResponseHandler((EventStreamResponseHandler)asyncResponseHandler).build();
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("SelectObjectContent").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new SelectObjectContentRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)selectObjectContentRequest), (AsyncResponseTransformer)restAsyncResponseTransformer);
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$selectObjectContent$276(asyncResponseHandler, eventStreamTransformFuture, metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return CompletableFutureUtils.forwardExceptionTo(eventStreamTransformFuture, (CompletableFuture)executeFuture);
        }
        catch (Throwable t) {
            FunctionalUtils.runAndLogError((Logger)log, (String)"Exception thrown in exceptionOccurred callback, ignoring", () -> asyncResponseHandler.exceptionOccurred(t));
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$selectObjectContent$278((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<UploadPartResponse> uploadPart(UploadPartRequest uploadPartRequest, AsyncRequestBody requestBody) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)uploadPartRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, uploadPartRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"UploadPart");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(UploadPartResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("UploadPart").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)((AsyncStreamingRequestMarshaller.Builder)AsyncStreamingRequestMarshaller.builder().delegateMarshaller((Marshaller)new UploadPartRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory))).asyncRequestBody(requestBody).build()).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withAsyncRequestBody(requestBody).putExecutionAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM, (Object)HttpChecksum.builder().requestChecksumRequired(false).requestAlgorithm(uploadPartRequest.checksumAlgorithmAsString()).isRequestStreaming(true).build()).withInput((SdkRequest)uploadPartRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$uploadPart$280(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$uploadPart$281((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<UploadPartCopyResponse> uploadPartCopy(UploadPartCopyRequest uploadPartCopyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)uploadPartCopyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, uploadPartCopyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"UploadPartCopy");
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(UploadPartCopyResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("UploadPartCopy").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new UploadPartCopyRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory)).withCombinedResponseHandler(responseHandler).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)uploadPartCopyRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$uploadPartCopy$283(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$uploadPartCopy$284((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<WriteGetObjectResponseResponse> writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest, AsyncRequestBody requestBody) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)writeGetObjectResponseRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultS3AsyncClient.resolveMetricPublishers(clientConfiguration, writeGetObjectResponseRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"S3");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"WriteGetObjectResponse");
            writeGetObjectResponseRequest = this.applySignerOverride(writeGetObjectResponseRequest, (Signer)Aws4UnsignedPayloadSigner.create());
            HttpResponseHandler responseHandler = this.protocolFactory.createCombinedResponseHandler(WriteGetObjectResponseResponse::builder, new XmlOperationMetadata().withHasStreamingSuccessResponse(false));
            String hostPrefix = "{RequestRoute}.";
            HostnameValidator.validateHostnameCompliant((String)writeGetObjectResponseRequest.requestRoute(), (String)"RequestRoute", (String)"writeGetObjectResponseRequest");
            String resolvedHostExpression = String.format("%s.", writeGetObjectResponseRequest.requestRoute());
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("WriteGetObjectResponse").withRequestConfiguration(clientConfiguration).withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)((AsyncStreamingRequestMarshaller.Builder)((AsyncStreamingRequestMarshaller.Builder)AsyncStreamingRequestMarshaller.builder().delegateMarshaller((Marshaller)new WriteGetObjectResponseRequestMarshaller((AwsXmlProtocolFactory)this.protocolFactory))).asyncRequestBody(requestBody).transferEncoding(true)).build()).withCombinedResponseHandler(responseHandler).hostPrefixExpression(resolvedHostExpression).withMetricCollector((MetricCollector)apiCallMetricCollector).withAsyncRequestBody(requestBody).withInput((SdkRequest)writeGetObjectResponseRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultS3AsyncClient.lambda$writeGetObjectResponse$286(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
            return whenCompleteFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultS3AsyncClient.lambda$writeGetObjectResponse$287((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public S3AsyncWaiter waiter() {
        return S3AsyncWaiter.builder().client(this).scheduledExecutorService(this.executorService).build();
    }

    @Override
    public final S3ServiceClientConfiguration serviceClientConfiguration() {
        return this.serviceClientConfiguration;
    }

    public final String serviceName() {
        return "s3";
    }

    private AwsS3ProtocolFactory init() {
        return ((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)((AwsS3ProtocolFactory.Builder)AwsS3ProtocolFactory.builder().registerModeledException(ExceptionMetadata.builder().errorCode("NoSuchUpload").exceptionBuilderSupplier(NoSuchUploadException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidObjectState").exceptionBuilderSupplier(InvalidObjectStateException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("BucketAlreadyOwnedByYou").exceptionBuilderSupplier(BucketAlreadyOwnedByYouException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("NoSuchKey").exceptionBuilderSupplier(NoSuchKeyException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("ObjectAlreadyInActiveTierError").exceptionBuilderSupplier(ObjectAlreadyInActiveTierErrorException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("BucketAlreadyExists").exceptionBuilderSupplier(BucketAlreadyExistsException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("NoSuchBucket").exceptionBuilderSupplier(NoSuchBucketException::builder).build())).registerModeledException(ExceptionMetadata.builder().errorCode("ObjectNotInActiveTierError").exceptionBuilderSupplier(ObjectNotInActiveTierErrorException::builder).build())).clientConfiguration(this.clientConfiguration)).defaultServiceExceptionSupplier(S3Exception::builder)).build();
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

    private <T extends S3Request> T applySignerOverride(T request, Signer signer) {
        if (request.overrideConfiguration().flatMap(c -> c.signer()).isPresent()) {
            return request;
        }
        Consumer<AwsRequestOverrideConfiguration.Builder> signerOverride = b -> ((AwsRequestOverrideConfiguration.Builder)b.signer(signer)).build();
        AwsRequestOverrideConfiguration overrideConfiguration = request.overrideConfiguration().map(c -> ((AwsRequestOverrideConfiguration.Builder)c.toBuilder().applyMutation(signerOverride)).build()).orElse(((AwsRequestOverrideConfiguration.Builder)AwsRequestOverrideConfiguration.builder().applyMutation(signerOverride)).build());
        return (T)((Object)((S3Request)request.toBuilder().overrideConfiguration(overrideConfiguration).build()));
    }

    private static boolean isSignerOverridden(SdkClientConfiguration clientConfiguration) {
        return Boolean.TRUE.equals(clientConfiguration.option((ClientOption)SdkClientOption.SIGNER_OVERRIDDEN));
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

    public void close() {
        this.clientHandler.close();
    }

    private static /* synthetic */ void lambda$writeGetObjectResponse$287(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$writeGetObjectResponse$286(List metricPublishers, MetricCollector apiCallMetricCollector, WriteGetObjectResponseResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$uploadPartCopy$284(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$uploadPartCopy$283(List metricPublishers, MetricCollector apiCallMetricCollector, UploadPartCopyResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$uploadPart$281(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$uploadPart$280(List metricPublishers, MetricCollector apiCallMetricCollector, UploadPartResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$selectObjectContent$278(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$selectObjectContent$276(SelectObjectContentResponseHandler asyncResponseHandler, CompletableFuture eventStreamTransformFuture, List metricPublishers, MetricCollector apiCallMetricCollector, Void r, Throwable e) {
        if (e != null) {
            FunctionalUtils.runAndLogError((Logger)log, (String)"Exception thrown in exceptionOccurred callback, ignoring", () -> asyncResponseHandler.exceptionOccurred(e));
            eventStreamTransformFuture.completeExceptionally(e);
        }
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$restoreObject$272(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$restoreObject$271(List metricPublishers, MetricCollector apiCallMetricCollector, RestoreObjectResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putPublicAccessBlock$269(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putPublicAccessBlock$268(List metricPublishers, MetricCollector apiCallMetricCollector, PutPublicAccessBlockResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putObjectTagging$266(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObjectTagging$265(List metricPublishers, MetricCollector apiCallMetricCollector, PutObjectTaggingResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putObjectRetention$263(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObjectRetention$262(List metricPublishers, MetricCollector apiCallMetricCollector, PutObjectRetentionResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putObjectLockConfiguration$260(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObjectLockConfiguration$259(List metricPublishers, MetricCollector apiCallMetricCollector, PutObjectLockConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putObjectLegalHold$257(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObjectLegalHold$256(List metricPublishers, MetricCollector apiCallMetricCollector, PutObjectLegalHoldResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putObjectAcl$254(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObjectAcl$253(List metricPublishers, MetricCollector apiCallMetricCollector, PutObjectAclResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putObject$251(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putObject$250(List metricPublishers, MetricCollector apiCallMetricCollector, PutObjectResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketWebsite$248(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketWebsite$247(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketWebsiteResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketVersioning$245(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketVersioning$244(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketVersioningResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketTagging$242(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketTagging$241(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketTaggingResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketRequestPayment$239(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketRequestPayment$238(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketRequestPaymentResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketReplication$236(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketReplication$235(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketReplicationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketPolicy$233(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketPolicy$232(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketPolicyResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketOwnershipControls$230(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketOwnershipControls$229(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketOwnershipControlsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketNotificationConfiguration$227(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketNotificationConfiguration$226(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketNotificationConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketMetricsConfiguration$224(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketMetricsConfiguration$223(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketMetricsConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketLogging$221(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketLogging$220(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketLoggingResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketLifecycleConfiguration$218(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketLifecycleConfiguration$217(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketLifecycleConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketInventoryConfiguration$215(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketInventoryConfiguration$214(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketInventoryConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketIntelligentTieringConfiguration$212(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketIntelligentTieringConfiguration$211(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketIntelligentTieringConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketEncryption$209(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketEncryption$208(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketEncryptionResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketCors$206(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketCors$205(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketCorsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketAnalyticsConfiguration$203(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketAnalyticsConfiguration$202(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketAnalyticsConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketAcl$200(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketAcl$199(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketAclResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putBucketAccelerateConfiguration$197(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putBucketAccelerateConfiguration$196(List metricPublishers, MetricCollector apiCallMetricCollector, PutBucketAccelerateConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listParts$194(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listParts$193(List metricPublishers, MetricCollector apiCallMetricCollector, ListPartsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listObjectsV2$191(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listObjectsV2$190(List metricPublishers, MetricCollector apiCallMetricCollector, ListObjectsV2Response r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listObjects$188(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listObjects$187(List metricPublishers, MetricCollector apiCallMetricCollector, ListObjectsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listObjectVersions$185(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listObjectVersions$184(List metricPublishers, MetricCollector apiCallMetricCollector, ListObjectVersionsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listMultipartUploads$182(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listMultipartUploads$181(List metricPublishers, MetricCollector apiCallMetricCollector, ListMultipartUploadsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listBuckets$179(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listBuckets$178(List metricPublishers, MetricCollector apiCallMetricCollector, ListBucketsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listBucketMetricsConfigurations$176(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listBucketMetricsConfigurations$175(List metricPublishers, MetricCollector apiCallMetricCollector, ListBucketMetricsConfigurationsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listBucketInventoryConfigurations$173(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listBucketInventoryConfigurations$172(List metricPublishers, MetricCollector apiCallMetricCollector, ListBucketInventoryConfigurationsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listBucketIntelligentTieringConfigurations$170(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listBucketIntelligentTieringConfigurations$169(List metricPublishers, MetricCollector apiCallMetricCollector, ListBucketIntelligentTieringConfigurationsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listBucketAnalyticsConfigurations$167(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listBucketAnalyticsConfigurations$166(List metricPublishers, MetricCollector apiCallMetricCollector, ListBucketAnalyticsConfigurationsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$headObject$164(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$headObject$163(List metricPublishers, MetricCollector apiCallMetricCollector, HeadObjectResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$headBucket$161(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$headBucket$160(List metricPublishers, MetricCollector apiCallMetricCollector, HeadBucketResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getPublicAccessBlock$158(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getPublicAccessBlock$157(List metricPublishers, MetricCollector apiCallMetricCollector, GetPublicAccessBlockResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getObjectTorrent$155(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectTorrent$153(AsyncResponseTransformer finalAsyncResponseTransformer, CompletableFuture endOfStreamFuture, List metricPublishers, MetricCollector apiCallMetricCollector, Object r, Throwable e) {
        if (e != null) {
            FunctionalUtils.runAndLogError((Logger)log, (String)"Exception thrown in exceptionOccurred callback, ignoring", () -> finalAsyncResponseTransformer.exceptionOccurred(e));
        }
        endOfStreamFuture.whenComplete((r2, e2) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
    }

    private static /* synthetic */ void lambda$getObjectTagging$149(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectTagging$148(List metricPublishers, MetricCollector apiCallMetricCollector, GetObjectTaggingResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getObjectRetention$146(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectRetention$145(List metricPublishers, MetricCollector apiCallMetricCollector, GetObjectRetentionResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getObjectLockConfiguration$143(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectLockConfiguration$142(List metricPublishers, MetricCollector apiCallMetricCollector, GetObjectLockConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getObjectLegalHold$140(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectLegalHold$139(List metricPublishers, MetricCollector apiCallMetricCollector, GetObjectLegalHoldResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getObjectAttributes$137(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectAttributes$136(List metricPublishers, MetricCollector apiCallMetricCollector, GetObjectAttributesResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getObjectAcl$134(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObjectAcl$133(List metricPublishers, MetricCollector apiCallMetricCollector, GetObjectAclResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getObject$131(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getObject$129(AsyncResponseTransformer finalAsyncResponseTransformer, CompletableFuture endOfStreamFuture, List metricPublishers, MetricCollector apiCallMetricCollector, Object r, Throwable e) {
        if (e != null) {
            FunctionalUtils.runAndLogError((Logger)log, (String)"Exception thrown in exceptionOccurred callback, ignoring", () -> finalAsyncResponseTransformer.exceptionOccurred(e));
        }
        endOfStreamFuture.whenComplete((r2, e2) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
    }

    private static /* synthetic */ void lambda$getBucketWebsite$125(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketWebsite$124(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketWebsiteResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketVersioning$122(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketVersioning$121(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketVersioningResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketTagging$119(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketTagging$118(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketTaggingResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketRequestPayment$116(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketRequestPayment$115(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketRequestPaymentResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketReplication$113(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketReplication$112(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketReplicationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketPolicyStatus$110(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketPolicyStatus$109(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketPolicyStatusResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketPolicy$107(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketPolicy$106(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketPolicyResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketOwnershipControls$104(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketOwnershipControls$103(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketOwnershipControlsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketNotificationConfiguration$101(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketNotificationConfiguration$100(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketNotificationConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketMetricsConfiguration$98(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketMetricsConfiguration$97(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketMetricsConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketLogging$95(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketLogging$94(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketLoggingResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketLocation$92(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketLocation$91(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketLocationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketLifecycleConfiguration$89(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketLifecycleConfiguration$88(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketLifecycleConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketInventoryConfiguration$86(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketInventoryConfiguration$85(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketInventoryConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketIntelligentTieringConfiguration$83(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketIntelligentTieringConfiguration$82(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketIntelligentTieringConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketEncryption$80(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketEncryption$79(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketEncryptionResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketCors$77(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketCors$76(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketCorsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketAnalyticsConfiguration$74(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketAnalyticsConfiguration$73(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketAnalyticsConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketAcl$71(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketAcl$70(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketAclResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getBucketAccelerateConfiguration$68(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getBucketAccelerateConfiguration$67(List metricPublishers, MetricCollector apiCallMetricCollector, GetBucketAccelerateConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deletePublicAccessBlock$65(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deletePublicAccessBlock$64(List metricPublishers, MetricCollector apiCallMetricCollector, DeletePublicAccessBlockResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteObjects$62(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteObjects$61(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteObjectsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteObjectTagging$59(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteObjectTagging$58(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteObjectTaggingResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteObject$56(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteObject$55(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteObjectResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketWebsite$53(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketWebsite$52(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketWebsiteResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketTagging$50(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketTagging$49(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketTaggingResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketReplication$47(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketReplication$46(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketReplicationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketPolicy$44(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketPolicy$43(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketPolicyResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketOwnershipControls$41(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketOwnershipControls$40(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketOwnershipControlsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketMetricsConfiguration$38(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketMetricsConfiguration$37(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketMetricsConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketLifecycle$35(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketLifecycle$34(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketLifecycleResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketInventoryConfiguration$32(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketInventoryConfiguration$31(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketInventoryConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketIntelligentTieringConfiguration$29(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketIntelligentTieringConfiguration$28(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketIntelligentTieringConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketEncryption$26(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketEncryption$25(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketEncryptionResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketCors$23(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketCors$22(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketCorsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucketAnalyticsConfiguration$20(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucketAnalyticsConfiguration$19(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketAnalyticsConfigurationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteBucket$17(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteBucket$16(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteBucketResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$createMultipartUpload$14(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$createMultipartUpload$13(List metricPublishers, MetricCollector apiCallMetricCollector, CreateMultipartUploadResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$createBucket$11(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$createBucket$10(List metricPublishers, MetricCollector apiCallMetricCollector, CreateBucketResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$copyObject$8(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$copyObject$7(List metricPublishers, MetricCollector apiCallMetricCollector, CopyObjectResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$completeMultipartUpload$5(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$completeMultipartUpload$4(List metricPublishers, MetricCollector apiCallMetricCollector, CompleteMultipartUploadResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$abortMultipartUpload$2(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$abortMultipartUpload$1(List metricPublishers, MetricCollector apiCallMetricCollector, AbortMultipartUploadResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }
}

