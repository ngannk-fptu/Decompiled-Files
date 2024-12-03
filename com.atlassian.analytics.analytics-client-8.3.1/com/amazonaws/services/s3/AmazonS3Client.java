/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.client.methods.HttpRequestBase
 *  org.apache.http.entity.ContentType
 */
package com.amazonaws.services.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.Request;
import com.amazonaws.RequestConfig;
import com.amazonaws.ResetException;
import com.amazonaws.Response;
import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.arn.Arn;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.Presigner;
import com.amazonaws.auth.ServiceAwareSigner;
import com.amazonaws.auth.Signer;
import com.amazonaws.auth.SignerFactory;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressInputStream;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.event.SDKProgressPublisher;
import com.amazonaws.handlers.HandlerChainFactory;
import com.amazonaws.handlers.HandlerContextKey;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.internal.AmazonWebServiceRequestAdapter;
import com.amazonaws.internal.DefaultServiceEndpointBuilder;
import com.amazonaws.internal.IdentityEndpointBuilder;
import com.amazonaws.internal.ReleasableInputStream;
import com.amazonaws.internal.ResettableInputStream;
import com.amazonaws.internal.ServiceEndpointBuilder;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.internal.auth.NoOpSignerProvider;
import com.amazonaws.internal.auth.SignerProvider;
import com.amazonaws.metrics.AwsSdkMetrics;
import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3ClientConfigurationFactory;
import com.amazonaws.services.s3.AmazonS3ClientParams;
import com.amazonaws.services.s3.S3AccessPointResource;
import com.amazonaws.services.s3.S3ArnConverter;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.S3CredentialsProviderChain;
import com.amazonaws.services.s3.S3Resource;
import com.amazonaws.services.s3.S3ResourceType;
import com.amazonaws.services.s3.S3ResponseMetadata;
import com.amazonaws.services.s3.UploadObjectObserver;
import com.amazonaws.services.s3.internal.AWSS3V4Signer;
import com.amazonaws.services.s3.internal.BucketNameUtils;
import com.amazonaws.services.s3.internal.CompleteMultipartUploadRetryCondition;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.internal.DeleteObjectTaggingHeaderHandler;
import com.amazonaws.services.s3.internal.DeleteObjectsResponse;
import com.amazonaws.services.s3.internal.DigestValidationInputStream;
import com.amazonaws.services.s3.internal.DualstackEndpointBuilder;
import com.amazonaws.services.s3.internal.GetObjectTaggingResponseHeaderHandler;
import com.amazonaws.services.s3.internal.InitiateMultipartUploadHeaderHandler;
import com.amazonaws.services.s3.internal.InputSubstream;
import com.amazonaws.services.s3.internal.ListPartsHeaderHandler;
import com.amazonaws.services.s3.internal.MD5DigestCalculatingInputStream;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.internal.MultiFileOutputStream;
import com.amazonaws.services.s3.internal.ObjectExpirationHeaderHandler;
import com.amazonaws.services.s3.internal.RegionalEndpointsOptionResolver;
import com.amazonaws.services.s3.internal.ResponseHeaderHandlerChain;
import com.amazonaws.services.s3.internal.S3AbortableInputStream;
import com.amazonaws.services.s3.internal.S3AccessPointBuilder;
import com.amazonaws.services.s3.internal.S3ErrorResponseHandler;
import com.amazonaws.services.s3.internal.S3MetadataResponseHandler;
import com.amazonaws.services.s3.internal.S3ObjectLambdaEndpointBuilder;
import com.amazonaws.services.s3.internal.S3ObjectLambdaOperationEndpointBuilder;
import com.amazonaws.services.s3.internal.S3ObjectResponseHandler;
import com.amazonaws.services.s3.internal.S3OutpostAccessPointBuilder;
import com.amazonaws.services.s3.internal.S3OutpostResource;
import com.amazonaws.services.s3.internal.S3QueryStringSigner;
import com.amazonaws.services.s3.internal.S3RequestEndpointResolver;
import com.amazonaws.services.s3.internal.S3RequesterChargedHeaderHandler;
import com.amazonaws.services.s3.internal.S3RestoreOutputPathHeaderHandler;
import com.amazonaws.services.s3.internal.S3Signer;
import com.amazonaws.services.s3.internal.S3StringResponseHandler;
import com.amazonaws.services.s3.internal.S3V4AuthErrorRetryStrategy;
import com.amazonaws.services.s3.internal.S3VersionHeaderHandler;
import com.amazonaws.services.s3.internal.S3XmlResponseHandler;
import com.amazonaws.services.s3.internal.ServerSideEncryptionHeaderHandler;
import com.amazonaws.services.s3.internal.ServiceUtils;
import com.amazonaws.services.s3.internal.SetObjectTaggingResponseHeaderHandler;
import com.amazonaws.services.s3.internal.SkipMd5CheckStrategy;
import com.amazonaws.services.s3.internal.UploadObjectStrategy;
import com.amazonaws.services.s3.internal.UseArnRegionResolver;
import com.amazonaws.services.s3.internal.XmlWriter;
import com.amazonaws.services.s3.internal.auth.S3SignerProvider;
import com.amazonaws.services.s3.metrics.S3ServiceMetric;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketAccelerateConfiguration;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.BucketReplicationConfiguration;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.CopyPartRequest;
import com.amazonaws.services.s3.model.CopyPartResult;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketAnalyticsConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketAnalyticsConfigurationResult;
import com.amazonaws.services.s3.model.DeleteBucketCrossOriginConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketEncryptionRequest;
import com.amazonaws.services.s3.model.DeleteBucketEncryptionResult;
import com.amazonaws.services.s3.model.DeleteBucketIntelligentTieringConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketIntelligentTieringConfigurationResult;
import com.amazonaws.services.s3.model.DeleteBucketInventoryConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketInventoryConfigurationResult;
import com.amazonaws.services.s3.model.DeleteBucketLifecycleConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketMetricsConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketMetricsConfigurationResult;
import com.amazonaws.services.s3.model.DeleteBucketOwnershipControlsRequest;
import com.amazonaws.services.s3.model.DeleteBucketOwnershipControlsResult;
import com.amazonaws.services.s3.model.DeleteBucketPolicyRequest;
import com.amazonaws.services.s3.model.DeleteBucketReplicationConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketTaggingConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketWebsiteConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectTaggingRequest;
import com.amazonaws.services.s3.model.DeleteObjectTaggingResult;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.DeletePublicAccessBlockRequest;
import com.amazonaws.services.s3.model.DeletePublicAccessBlockResult;
import com.amazonaws.services.s3.model.DeleteVersionRequest;
import com.amazonaws.services.s3.model.ExpectedBucketOwnerRequest;
import com.amazonaws.services.s3.model.ExpectedSourceBucketOwnerRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GenericBucketRequest;
import com.amazonaws.services.s3.model.GetBucketAccelerateConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketAclRequest;
import com.amazonaws.services.s3.model.GetBucketAnalyticsConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketAnalyticsConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketCrossOriginConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketEncryptionRequest;
import com.amazonaws.services.s3.model.GetBucketEncryptionResult;
import com.amazonaws.services.s3.model.GetBucketIntelligentTieringConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketIntelligentTieringConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketInventoryConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketInventoryConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketLifecycleConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetBucketLoggingConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketMetricsConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketMetricsConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketNotificationConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketOwnershipControlsRequest;
import com.amazonaws.services.s3.model.GetBucketOwnershipControlsResult;
import com.amazonaws.services.s3.model.GetBucketPolicyRequest;
import com.amazonaws.services.s3.model.GetBucketPolicyStatusRequest;
import com.amazonaws.services.s3.model.GetBucketPolicyStatusResult;
import com.amazonaws.services.s3.model.GetBucketReplicationConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketTaggingConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketWebsiteConfigurationRequest;
import com.amazonaws.services.s3.model.GetObjectAclRequest;
import com.amazonaws.services.s3.model.GetObjectLegalHoldRequest;
import com.amazonaws.services.s3.model.GetObjectLegalHoldResult;
import com.amazonaws.services.s3.model.GetObjectLockConfigurationRequest;
import com.amazonaws.services.s3.model.GetObjectLockConfigurationResult;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRetentionRequest;
import com.amazonaws.services.s3.model.GetObjectRetentionResult;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.GetPublicAccessBlockRequest;
import com.amazonaws.services.s3.model.GetPublicAccessBlockResult;
import com.amazonaws.services.s3.model.GetRequestPaymentConfigurationRequest;
import com.amazonaws.services.s3.model.GetS3AccountOwnerRequest;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.HeadBucketResult;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListBucketAnalyticsConfigurationsRequest;
import com.amazonaws.services.s3.model.ListBucketAnalyticsConfigurationsResult;
import com.amazonaws.services.s3.model.ListBucketIntelligentTieringConfigurationsRequest;
import com.amazonaws.services.s3.model.ListBucketIntelligentTieringConfigurationsResult;
import com.amazonaws.services.s3.model.ListBucketInventoryConfigurationsRequest;
import com.amazonaws.services.s3.model.ListBucketInventoryConfigurationsResult;
import com.amazonaws.services.s3.model.ListBucketMetricsConfigurationsRequest;
import com.amazonaws.services.s3.model.ListBucketMetricsConfigurationsResult;
import com.amazonaws.services.s3.model.ListBucketsRequest;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.ListNextBatchOfObjectsRequest;
import com.amazonaws.services.s3.model.ListNextBatchOfVersionsRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ListPartsRequest;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.MultiFactorAuthentication;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PartListing;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PresignedUrlDownloadRequest;
import com.amazonaws.services.s3.model.PresignedUrlDownloadResult;
import com.amazonaws.services.s3.model.PresignedUrlUploadRequest;
import com.amazonaws.services.s3.model.PresignedUrlUploadResult;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.RequestPaymentConfiguration;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.RestoreObjectRequest;
import com.amazonaws.services.s3.model.RestoreObjectResult;
import com.amazonaws.services.s3.model.RestoreRequestType;
import com.amazonaws.services.s3.model.S3AccelerateUnsupported;
import com.amazonaws.services.s3.model.S3DataSource;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParamsProvider;
import com.amazonaws.services.s3.model.SSECustomerKey;
import com.amazonaws.services.s3.model.SSECustomerKeyProvider;
import com.amazonaws.services.s3.model.SelectObjectContentEventStream;
import com.amazonaws.services.s3.model.SelectObjectContentRequest;
import com.amazonaws.services.s3.model.SelectObjectContentResult;
import com.amazonaws.services.s3.model.ServerSideEncryptionConfiguration;
import com.amazonaws.services.s3.model.SetBucketAccelerateConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketAclRequest;
import com.amazonaws.services.s3.model.SetBucketAnalyticsConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketAnalyticsConfigurationResult;
import com.amazonaws.services.s3.model.SetBucketCrossOriginConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketEncryptionRequest;
import com.amazonaws.services.s3.model.SetBucketEncryptionResult;
import com.amazonaws.services.s3.model.SetBucketIntelligentTieringConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketIntelligentTieringConfigurationResult;
import com.amazonaws.services.s3.model.SetBucketInventoryConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketInventoryConfigurationResult;
import com.amazonaws.services.s3.model.SetBucketLifecycleConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketLoggingConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketMetricsConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketMetricsConfigurationResult;
import com.amazonaws.services.s3.model.SetBucketNotificationConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketOwnershipControlsRequest;
import com.amazonaws.services.s3.model.SetBucketOwnershipControlsResult;
import com.amazonaws.services.s3.model.SetBucketPolicyRequest;
import com.amazonaws.services.s3.model.SetBucketReplicationConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketTaggingConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketWebsiteConfigurationRequest;
import com.amazonaws.services.s3.model.SetObjectAclRequest;
import com.amazonaws.services.s3.model.SetObjectLegalHoldRequest;
import com.amazonaws.services.s3.model.SetObjectLegalHoldResult;
import com.amazonaws.services.s3.model.SetObjectLockConfigurationRequest;
import com.amazonaws.services.s3.model.SetObjectLockConfigurationResult;
import com.amazonaws.services.s3.model.SetObjectRetentionRequest;
import com.amazonaws.services.s3.model.SetObjectRetentionResult;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingResult;
import com.amazonaws.services.s3.model.SetPublicAccessBlockRequest;
import com.amazonaws.services.s3.model.SetPublicAccessBlockResult;
import com.amazonaws.services.s3.model.SetRequestPaymentConfigurationRequest;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.UploadObjectRequest;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.services.s3.model.WriteGetObjectResponseRequest;
import com.amazonaws.services.s3.model.WriteGetObjectResponseResult;
import com.amazonaws.services.s3.model.analytics.AnalyticsConfiguration;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringConfiguration;
import com.amazonaws.services.s3.model.inventory.InventoryConfiguration;
import com.amazonaws.services.s3.model.metrics.MetricsConfiguration;
import com.amazonaws.services.s3.model.ownership.OwnershipControls;
import com.amazonaws.services.s3.model.transform.AclXmlFactory;
import com.amazonaws.services.s3.model.transform.BucketConfigurationXmlFactory;
import com.amazonaws.services.s3.model.transform.BucketNotificationConfigurationStaxUnmarshaller;
import com.amazonaws.services.s3.model.transform.GetBucketEncryptionStaxUnmarshaller;
import com.amazonaws.services.s3.model.transform.GetBucketPolicyStatusStaxUnmarshaller;
import com.amazonaws.services.s3.model.transform.GetPublicAccessBlockStaxUnmarshaller;
import com.amazonaws.services.s3.model.transform.HeadBucketResultHandler;
import com.amazonaws.services.s3.model.transform.MultiObjectDeleteXmlFactory;
import com.amazonaws.services.s3.model.transform.ObjectLockConfigurationXmlFactory;
import com.amazonaws.services.s3.model.transform.ObjectLockLegalHoldXmlFactory;
import com.amazonaws.services.s3.model.transform.ObjectLockRetentionXmlFactory;
import com.amazonaws.services.s3.model.transform.ObjectTaggingXmlFactory;
import com.amazonaws.services.s3.model.transform.RequestPaymentConfigurationXmlFactory;
import com.amazonaws.services.s3.model.transform.RequestXmlFactory;
import com.amazonaws.services.s3.model.transform.Unmarshallers;
import com.amazonaws.services.s3.model.transform.XmlResponsesSaxParser;
import com.amazonaws.services.s3.request.S3HandlerContextKeys;
import com.amazonaws.services.s3.waiters.AmazonS3Waiters;
import com.amazonaws.transform.Unmarshaller;
import com.amazonaws.util.AWSRequestMetrics;
import com.amazonaws.util.AwsHostNameUtils;
import com.amazonaws.util.Base16;
import com.amazonaws.util.Base64;
import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.CredentialUtils;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.LengthCheckInputStream;
import com.amazonaws.util.Md5Utils;
import com.amazonaws.util.RuntimeHttpUtils;
import com.amazonaws.util.SdkHttpUtils;
import com.amazonaws.util.ServiceClientHolderInputStream;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.Throwables;
import com.amazonaws.util.UriResourcePathUtils;
import com.amazonaws.util.ValidationUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;

@ThreadSafe
public class AmazonS3Client
extends AmazonWebServiceClient
implements AmazonS3 {
    public static final String S3_SERVICE_NAME = "s3";
    private static final String S3_SIGNER = "S3SignerType";
    private static final String S3_V4_SIGNER = "AWSS3V4SignerType";
    private static final String SERVICE_ID = "S3";
    private static final String AWS_PARTITION_KEY = "aws";
    private static final String S3_OUTPOSTS_NAME = "s3-outposts";
    private static final String S3_OBJECT_LAMBDAS_NAME = "s3-object-lambda";
    protected static final AmazonS3ClientConfigurationFactory configFactory = new AmazonS3ClientConfigurationFactory();
    private static Log log = LogFactory.getLog(AmazonS3Client.class);
    private volatile AmazonS3Waiters waiters;
    protected final AWSCredentialsProvider awsCredentialsProvider;
    protected final S3ErrorResponseHandler errorResponseHandler;
    private final S3XmlResponseHandler<Void> voidResponseHandler = new S3XmlResponseHandler(null);
    private static final BucketConfigurationXmlFactory bucketConfigurationXmlFactory;
    private static final RequestPaymentConfigurationXmlFactory requestPaymentConfigurationXmlFactory;
    private static final UseArnRegionResolver USE_ARN_REGION_RESOLVER;
    private volatile S3ClientOptions clientOptions = S3ClientOptions.builder().build();
    private volatile String clientRegion;
    private static RegionalEndpointsOptionResolver REGIONAL_ENDPOINTS_OPTION_RESOLVER;
    private static final int BUCKET_REGION_CACHE_SIZE = 300;
    private static final Map<String, String> bucketRegionCache;
    private final SkipMd5CheckStrategy skipMd5CheckStrategy;
    private final CompleteMultipartUploadRetryCondition completeMultipartUploadRetryCondition = new CompleteMultipartUploadRetryCondition();

    static Map<String, String> getBucketRegionCache() {
        return bucketRegionCache;
    }

    @Deprecated
    public AmazonS3Client() {
        this(new S3CredentialsProviderChain());
    }

    @Deprecated
    public AmazonS3Client(AWSCredentials awsCredentials) {
        this(awsCredentials, configFactory.getConfig());
    }

    @Deprecated
    public AmazonS3Client(AWSCredentials awsCredentials, ClientConfiguration clientConfiguration) {
        this(new StaticCredentialsProvider(awsCredentials), clientConfiguration);
    }

    @Deprecated
    public AmazonS3Client(AWSCredentialsProvider credentialsProvider) {
        this(credentialsProvider, configFactory.getConfig());
    }

    @Deprecated
    public AmazonS3Client(AWSCredentialsProvider credentialsProvider, ClientConfiguration clientConfiguration) {
        this(credentialsProvider, clientConfiguration, null);
    }

    @Deprecated
    public AmazonS3Client(AWSCredentialsProvider credentialsProvider, ClientConfiguration clientConfiguration, RequestMetricCollector requestMetricCollector) {
        this(credentialsProvider, clientConfiguration, requestMetricCollector, SkipMd5CheckStrategy.INSTANCE);
    }

    @SdkTestInternalApi
    AmazonS3Client(AWSCredentialsProvider credentialsProvider, ClientConfiguration clientConfiguration, RequestMetricCollector requestMetricCollector, SkipMd5CheckStrategy skipMd5CheckStrategy) {
        super(clientConfiguration, requestMetricCollector, true);
        this.awsCredentialsProvider = credentialsProvider;
        this.skipMd5CheckStrategy = skipMd5CheckStrategy;
        this.errorResponseHandler = new S3ErrorResponseHandler(clientConfiguration);
        this.init();
    }

    @Deprecated
    public AmazonS3Client(ClientConfiguration clientConfiguration) {
        this(new S3CredentialsProviderChain(), clientConfiguration);
    }

    @SdkInternalApi
    AmazonS3Client(AmazonS3ClientParams s3ClientParams) {
        super(s3ClientParams.getClientParams());
        this.awsCredentialsProvider = s3ClientParams.getClientParams().getCredentialsProvider();
        this.skipMd5CheckStrategy = SkipMd5CheckStrategy.INSTANCE;
        this.setS3ClientOptions(s3ClientParams.getS3ClientOptions());
        this.errorResponseHandler = new S3ErrorResponseHandler(s3ClientParams.getClientParams().getClientConfiguration());
        this.init();
    }

    public static AmazonS3ClientBuilder builder() {
        return AmazonS3ClientBuilder.standard();
    }

    private void init() {
        this.setEndpoint("s3.amazonaws.com");
        HandlerChainFactory chainFactory = new HandlerChainFactory();
        this.requestHandler2s.addAll(chainFactory.newRequestHandlerChain("/com/amazonaws/services/s3/request.handlers"));
        this.requestHandler2s.addAll(chainFactory.newRequestHandler2Chain("/com/amazonaws/services/s3/request.handler2s"));
        this.requestHandler2s.addAll(chainFactory.getGlobalHandlers());
    }

    @Override
    @Deprecated
    public synchronized void setEndpoint(String endpoint) {
        if (ServiceUtils.isS3AccelerateEndpoint(endpoint)) {
            throw new IllegalStateException("To enable accelerate mode, please use AmazonS3ClientBuilder.withAccelerateModeEnabled(true)");
        }
        super.setEndpoint(endpoint);
        if (!ServiceUtils.isS3USStandardEndpoint(endpoint)) {
            this.clientRegion = AwsHostNameUtils.parseRegionName(this.endpoint.getHost(), S3_SERVICE_NAME);
        }
    }

    @Override
    @Deprecated
    public synchronized void setRegion(com.amazonaws.regions.Region region) {
        if (region.getName().equalsIgnoreCase("us-east-1") && (this.clientOptions.isRegionalUsEast1EndpointEnabled() || REGIONAL_ENDPOINTS_OPTION_RESOLVER.useRegionalMode())) {
            region = RegionUtils.getRegion("us-east-1-regional");
        }
        super.setRegion(region);
        this.clientRegion = region.getName();
    }

    @Override
    public synchronized void setS3ClientOptions(S3ClientOptions clientOptions) {
        this.checkMutability();
        this.clientOptions = new S3ClientOptions(clientOptions);
    }

    @Override
    protected boolean useStrictHostNameVerification() {
        return false;
    }

    @Override
    public VersionListing listNextBatchOfVersions(VersionListing previousVersionListing) throws SdkClientException, AmazonServiceException {
        return this.listNextBatchOfVersions(new ListNextBatchOfVersionsRequest(previousVersionListing));
    }

    @Override
    public VersionListing listNextBatchOfVersions(ListNextBatchOfVersionsRequest listNextBatchOfVersionsRequest) {
        listNextBatchOfVersionsRequest = this.beforeClientExecution(listNextBatchOfVersionsRequest);
        this.rejectNull(listNextBatchOfVersionsRequest, "The request object parameter must be specified when listing the next batch of versions in a bucket");
        VersionListing previousVersionListing = listNextBatchOfVersionsRequest.getPreviousVersionListing();
        if (!previousVersionListing.isTruncated()) {
            VersionListing emptyListing = new VersionListing();
            emptyListing.setBucketName(previousVersionListing.getBucketName());
            emptyListing.setDelimiter(previousVersionListing.getDelimiter());
            emptyListing.setKeyMarker(previousVersionListing.getNextKeyMarker());
            emptyListing.setVersionIdMarker(previousVersionListing.getNextVersionIdMarker());
            emptyListing.setMaxKeys(previousVersionListing.getMaxKeys());
            emptyListing.setPrefix(previousVersionListing.getPrefix());
            emptyListing.setEncodingType(previousVersionListing.getEncodingType());
            emptyListing.setTruncated(false);
            return emptyListing;
        }
        return this.listVersions(listNextBatchOfVersionsRequest.toListVersionsRequest());
    }

    @Override
    public VersionListing listVersions(String bucketName, String prefix) throws SdkClientException, AmazonServiceException {
        return this.listVersions(new ListVersionsRequest(bucketName, prefix, null, null, null, null));
    }

    @Override
    public VersionListing listVersions(String bucketName, String prefix, String keyMarker, String versionIdMarker, String delimiter, Integer maxKeys) throws SdkClientException, AmazonServiceException {
        ListVersionsRequest request = new ListVersionsRequest().withBucketName(bucketName).withPrefix(prefix).withDelimiter(delimiter).withKeyMarker(keyMarker).withVersionIdMarker(versionIdMarker).withMaxResults(maxKeys);
        return this.listVersions(request);
    }

    @Override
    public VersionListing listVersions(ListVersionsRequest listVersionsRequest) throws SdkClientException, AmazonServiceException {
        listVersionsRequest = this.beforeClientExecution(listVersionsRequest);
        this.rejectNull(listVersionsRequest.getBucketName(), "The bucket name parameter must be specified when listing versions in a bucket");
        boolean shouldSDKDecodeResponse = listVersionsRequest.getEncodingType() == null;
        Request<ListVersionsRequest> request = this.createRequest(listVersionsRequest.getBucketName(), null, listVersionsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListObjectVersions");
        request.addParameter("versions", null);
        AmazonS3Client.addParameterIfNotNull(request, "prefix", listVersionsRequest.getPrefix());
        AmazonS3Client.addParameterIfNotNull(request, "key-marker", listVersionsRequest.getKeyMarker());
        AmazonS3Client.addParameterIfNotNull(request, "version-id-marker", listVersionsRequest.getVersionIdMarker());
        AmazonS3Client.addParameterIfNotNull(request, "delimiter", listVersionsRequest.getDelimiter());
        if (listVersionsRequest.getMaxResults() != null && listVersionsRequest.getMaxResults() >= 0) {
            request.addParameter("max-keys", listVersionsRequest.getMaxResults().toString());
        }
        request.addParameter("encoding-type", shouldSDKDecodeResponse ? "url" : listVersionsRequest.getEncodingType());
        return this.invoke(request, new Unmarshallers.VersionListUnmarshaller(shouldSDKDecodeResponse), listVersionsRequest.getBucketName(), null);
    }

    @Override
    public ObjectListing listObjects(String bucketName) throws SdkClientException, AmazonServiceException {
        return this.listObjects(new ListObjectsRequest(bucketName, null, null, null, null));
    }

    @Override
    public ObjectListing listObjects(String bucketName, String prefix) throws SdkClientException, AmazonServiceException {
        return this.listObjects(new ListObjectsRequest(bucketName, prefix, null, null, null));
    }

    @Override
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws SdkClientException, AmazonServiceException {
        listObjectsRequest = this.beforeClientExecution(listObjectsRequest);
        this.rejectNull(listObjectsRequest.getBucketName(), "The bucket name parameter must be specified when listing objects in a bucket");
        boolean shouldSDKDecodeResponse = listObjectsRequest.getEncodingType() == null;
        Request<ListObjectsRequest> request = this.createRequest(listObjectsRequest.getBucketName(), null, listObjectsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListObjects");
        AmazonS3Client.addParameterIfNotNull(request, "prefix", listObjectsRequest.getPrefix());
        AmazonS3Client.addParameterIfNotNull(request, "marker", listObjectsRequest.getMarker());
        AmazonS3Client.addParameterIfNotNull(request, "delimiter", listObjectsRequest.getDelimiter());
        if (listObjectsRequest.getMaxKeys() != null && listObjectsRequest.getMaxKeys() >= 0) {
            request.addParameter("max-keys", listObjectsRequest.getMaxKeys().toString());
        }
        request.addParameter("encoding-type", shouldSDKDecodeResponse ? "url" : listObjectsRequest.getEncodingType());
        AmazonS3Client.populateRequesterPaysHeader(request, listObjectsRequest.isRequesterPays());
        return this.invoke(request, new Unmarshallers.ListObjectsUnmarshaller(shouldSDKDecodeResponse), listObjectsRequest.getBucketName(), null);
    }

    @Override
    public ListObjectsV2Result listObjectsV2(String bucketName) throws SdkClientException, AmazonServiceException {
        return this.listObjectsV2(new ListObjectsV2Request().withBucketName(bucketName));
    }

    @Override
    public ListObjectsV2Result listObjectsV2(String bucketName, String prefix) throws SdkClientException, AmazonServiceException {
        return this.listObjectsV2(new ListObjectsV2Request().withBucketName(bucketName).withPrefix(prefix));
    }

    @Override
    public ListObjectsV2Result listObjectsV2(ListObjectsV2Request listObjectsV2Request) throws SdkClientException, AmazonServiceException {
        listObjectsV2Request = this.beforeClientExecution(listObjectsV2Request);
        this.rejectNull(listObjectsV2Request.getBucketName(), "The bucket name parameter must be specified when listing objects in a bucket");
        Request<ListObjectsV2Request> request = this.createRequest(listObjectsV2Request.getBucketName(), null, listObjectsV2Request, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListObjectsV2");
        request.addParameter("list-type", "2");
        AmazonS3Client.addParameterIfNotNull(request, "start-after", listObjectsV2Request.getStartAfter());
        AmazonS3Client.addParameterIfNotNull(request, "continuation-token", listObjectsV2Request.getContinuationToken());
        AmazonS3Client.addParameterIfNotNull(request, "delimiter", listObjectsV2Request.getDelimiter());
        AmazonS3Client.addParameterIfNotNull(request, "max-keys", listObjectsV2Request.getMaxKeys());
        AmazonS3Client.addParameterIfNotNull(request, "prefix", listObjectsV2Request.getPrefix());
        AmazonS3Client.addParameterIfNotNull(request, "encoding-type", listObjectsV2Request.getEncodingType());
        request.addParameter("fetch-owner", Boolean.toString(listObjectsV2Request.isFetchOwner()));
        AmazonS3Client.populateRequesterPaysHeader(request, listObjectsV2Request.isRequesterPays());
        boolean shouldSDKDecodeResponse = "url".equals(listObjectsV2Request.getEncodingType());
        return this.invoke(request, new Unmarshallers.ListObjectsV2Unmarshaller(shouldSDKDecodeResponse), listObjectsV2Request.getBucketName(), null);
    }

    @Override
    public ObjectListing listNextBatchOfObjects(ObjectListing previousObjectListing) throws SdkClientException, AmazonServiceException {
        return this.listNextBatchOfObjects(new ListNextBatchOfObjectsRequest(previousObjectListing));
    }

    @Override
    public ObjectListing listNextBatchOfObjects(ListNextBatchOfObjectsRequest listNextBatchOfObjectsRequest) throws SdkClientException, AmazonServiceException {
        listNextBatchOfObjectsRequest = this.beforeClientExecution(listNextBatchOfObjectsRequest);
        this.rejectNull(listNextBatchOfObjectsRequest, "The request object parameter must be specified when listing the next batch of objects in a bucket");
        ObjectListing previousObjectListing = listNextBatchOfObjectsRequest.getPreviousObjectListing();
        if (!previousObjectListing.isTruncated()) {
            ObjectListing emptyListing = new ObjectListing();
            emptyListing.setBucketName(previousObjectListing.getBucketName());
            emptyListing.setDelimiter(previousObjectListing.getDelimiter());
            emptyListing.setMarker(previousObjectListing.getNextMarker());
            emptyListing.setMaxKeys(previousObjectListing.getMaxKeys());
            emptyListing.setPrefix(previousObjectListing.getPrefix());
            emptyListing.setEncodingType(previousObjectListing.getEncodingType());
            emptyListing.setTruncated(false);
            return emptyListing;
        }
        return this.listObjects(listNextBatchOfObjectsRequest.toListObjectsRequest());
    }

    @Override
    public Owner getS3AccountOwner() throws SdkClientException, AmazonServiceException {
        return this.getS3AccountOwner(new GetS3AccountOwnerRequest());
    }

    @Override
    public Owner getS3AccountOwner(GetS3AccountOwnerRequest getS3AccountOwnerRequest) throws SdkClientException, AmazonServiceException {
        getS3AccountOwnerRequest = this.beforeClientExecution(getS3AccountOwnerRequest);
        this.rejectNull(getS3AccountOwnerRequest, "The request object parameter getS3AccountOwnerRequest must be specified.");
        Request<GetS3AccountOwnerRequest> request = this.createRequest(null, null, getS3AccountOwnerRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBuckets");
        return this.invoke(request, new Unmarshallers.ListBucketsOwnerUnmarshaller(), null, null);
    }

    @Override
    public List<Bucket> listBuckets(ListBucketsRequest listBucketsRequest) throws SdkClientException, AmazonServiceException {
        listBucketsRequest = this.beforeClientExecution(listBucketsRequest);
        this.rejectNull(listBucketsRequest, "The request object parameter listBucketsRequest must be specified.");
        Request<ListBucketsRequest> request = this.createRequest(null, null, listBucketsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBuckets");
        return this.invoke(request, new Unmarshallers.ListBucketsUnmarshaller(), null, null);
    }

    @Override
    public List<Bucket> listBuckets() throws SdkClientException, AmazonServiceException {
        return this.listBuckets(new ListBucketsRequest());
    }

    @Override
    public String getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) throws SdkClientException, AmazonServiceException {
        getBucketLocationRequest = this.beforeClientExecution(getBucketLocationRequest);
        this.rejectNull(getBucketLocationRequest, "The request parameter must be specified when requesting a bucket's location");
        String bucketName = getBucketLocationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when requesting a bucket's location");
        Request<GetBucketLocationRequest> request = this.createRequest(bucketName, null, getBucketLocationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketLocation");
        request.addParameter("location", null);
        return this.invoke(request, new Unmarshallers.BucketLocationUnmarshaller(), bucketName, null);
    }

    @Override
    public String getBucketLocation(String bucketName) throws SdkClientException, AmazonServiceException {
        return this.getBucketLocation(new GetBucketLocationRequest(bucketName));
    }

    @Override
    public Bucket createBucket(String bucketName) throws SdkClientException, AmazonServiceException {
        return this.createBucket(new CreateBucketRequest(bucketName));
    }

    @Override
    @Deprecated
    public Bucket createBucket(String bucketName, Region region) throws SdkClientException, AmazonServiceException {
        return this.createBucket(new CreateBucketRequest(bucketName, region));
    }

    @Override
    @Deprecated
    public Bucket createBucket(String bucketName, String region) throws SdkClientException, AmazonServiceException {
        return this.createBucket(new CreateBucketRequest(bucketName, region));
    }

    @Override
    public Bucket createBucket(CreateBucketRequest createBucketRequest) throws SdkClientException, AmazonServiceException {
        createBucketRequest = this.beforeClientExecution(createBucketRequest);
        this.rejectNull(createBucketRequest, "The CreateBucketRequest parameter must be specified when creating a bucket");
        String bucketName = createBucketRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when creating a bucket");
        bucketName = bucketName.trim();
        String requestRegion = createBucketRequest.getRegion();
        URI requestEndpoint = this.getCreateBucketEndpoint(requestRegion);
        BucketNameUtils.validateBucketName(bucketName);
        Request<CreateBucketRequest> request = this.createRequest(bucketName, null, createBucketRequest, HttpMethodName.PUT, requestEndpoint);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CreateBucket");
        if (createBucketRequest.getAccessControlList() != null) {
            AmazonS3Client.addAclHeaders(request, createBucketRequest.getAccessControlList());
        } else if (createBucketRequest.getCannedAcl() != null) {
            request.addHeader("x-amz-acl", createBucketRequest.getCannedAcl().toString());
        }
        if (this.getSignerRegion() != null && !this.getSignerRegion().equals("us-east-1") && StringUtils.isNullOrEmpty(requestRegion)) {
            requestRegion = AwsHostNameUtils.parseRegion(requestEndpoint.getHost(), S3_SERVICE_NAME);
        }
        if (requestRegion != null && !StringUtils.upperCase(requestRegion).equals(Region.US_Standard.toString())) {
            XmlWriter xml = new XmlWriter();
            xml.start("CreateBucketConfiguration", "xmlns", "http://s3.amazonaws.com/doc/2006-03-01/");
            xml.start("LocationConstraint").value(requestRegion).end();
            xml.end();
            request.setContent(new ByteArrayInputStream(xml.getBytes()));
        }
        if (createBucketRequest.getObjectLockEnabledForBucket()) {
            request.addHeader("x-amz-bucket-object-lock-enabled", "true");
        }
        if (createBucketRequest.getObjectOwnership() != null) {
            request.addHeader("x-amz-object-ownership", createBucketRequest.getObjectOwnership());
        }
        this.invoke(request, this.voidResponseHandler, bucketName, null);
        return new Bucket(bucketName);
    }

    private URI getCreateBucketEndpoint(String requestRegion) {
        if (requestRegion == null || requestRegion.equals(this.clientRegion) || !this.clientOptions.isForceGlobalBucketAccessEnabled()) {
            return this.endpoint;
        }
        com.amazonaws.regions.Region targetRegion = com.amazonaws.regions.Region.getRegion(Regions.fromName(requestRegion));
        return new DefaultServiceEndpointBuilder(this.getEndpointPrefix(), this.clientConfiguration.getProtocol().toString()).withRegion(targetRegion).getServiceEndpoint();
    }

    @Override
    public AccessControlList getObjectAcl(String bucketName, String key) throws SdkClientException, AmazonServiceException {
        return this.getObjectAcl(new GetObjectAclRequest(bucketName, key));
    }

    @Override
    public AccessControlList getObjectAcl(String bucketName, String key, String versionId) throws SdkClientException, AmazonServiceException {
        return this.getObjectAcl(new GetObjectAclRequest(bucketName, key, versionId));
    }

    @Override
    public AccessControlList getObjectAcl(GetObjectAclRequest getObjectAclRequest) {
        getObjectAclRequest = this.beforeClientExecution(getObjectAclRequest);
        this.rejectNull(getObjectAclRequest, "The request parameter must be specified when requesting an object's ACL");
        this.rejectNull(getObjectAclRequest.getBucketName(), "The bucket name parameter must be specified when requesting an object's ACL");
        this.rejectNull(getObjectAclRequest.getKey(), "The key parameter must be specified when requesting an object's ACL");
        return this.getAcl(getObjectAclRequest.getBucketName(), getObjectAclRequest.getKey(), getObjectAclRequest.getVersionId(), getObjectAclRequest.isRequesterPays(), getObjectAclRequest);
    }

    @Override
    public void setObjectAcl(String bucketName, String key, AccessControlList acl) throws SdkClientException, AmazonServiceException {
        this.setObjectAcl(bucketName, key, null, acl);
    }

    @Override
    public void setObjectAcl(String bucketName, String key, CannedAccessControlList acl) throws SdkClientException, AmazonServiceException {
        this.setObjectAcl(bucketName, key, null, acl);
    }

    @Override
    public void setObjectAcl(String bucketName, String key, String versionId, AccessControlList acl) throws SdkClientException, AmazonServiceException {
        this.setObjectAcl(new SetObjectAclRequest(bucketName, key, versionId, acl));
    }

    public void setObjectAcl(String bucketName, String key, String versionId, AccessControlList acl, RequestMetricCollector requestMetricCollector) throws SdkClientException, AmazonServiceException {
        this.setObjectAcl((SetObjectAclRequest)new SetObjectAclRequest(bucketName, key, versionId, acl).withRequestMetricCollector(requestMetricCollector));
    }

    @Override
    public void setObjectAcl(String bucketName, String key, String versionId, CannedAccessControlList acl) throws SdkClientException, AmazonServiceException {
        this.setObjectAcl(new SetObjectAclRequest(bucketName, key, versionId, acl));
    }

    public void setObjectAcl(String bucketName, String key, String versionId, CannedAccessControlList acl, RequestMetricCollector requestMetricCollector) {
        this.setObjectAcl((SetObjectAclRequest)new SetObjectAclRequest(bucketName, key, versionId, acl).withRequestMetricCollector(requestMetricCollector));
    }

    @Override
    public void setObjectAcl(SetObjectAclRequest setObjectAclRequest) throws SdkClientException, AmazonServiceException {
        setObjectAclRequest = this.beforeClientExecution(setObjectAclRequest);
        this.rejectNull(setObjectAclRequest, "The request must not be null.");
        this.rejectNull(setObjectAclRequest.getBucketName(), "The bucket name parameter must be specified when setting an object's ACL");
        this.rejectNull(setObjectAclRequest.getKey(), "The key parameter must be specified when setting an object's ACL");
        if (setObjectAclRequest.getAcl() != null && setObjectAclRequest.getCannedAcl() != null) {
            throw new IllegalArgumentException("Only one of the ACL and CannedACL parameters can be specified, not both.");
        }
        if (setObjectAclRequest.getAcl() != null) {
            this.setAcl(setObjectAclRequest.getBucketName(), setObjectAclRequest.getKey(), setObjectAclRequest.getVersionId(), setObjectAclRequest.getAcl(), setObjectAclRequest.isRequesterPays(), (AmazonWebServiceRequest)setObjectAclRequest);
        } else if (setObjectAclRequest.getCannedAcl() != null) {
            this.setAcl(setObjectAclRequest.getBucketName(), setObjectAclRequest.getKey(), setObjectAclRequest.getVersionId(), setObjectAclRequest.getCannedAcl(), setObjectAclRequest.isRequesterPays(), (AmazonWebServiceRequest)setObjectAclRequest);
        } else {
            throw new IllegalArgumentException("At least one of the ACL and CannedACL parameters should be specified");
        }
    }

    @Override
    public AccessControlList getBucketAcl(String bucketName) throws SdkClientException, AmazonServiceException {
        return this.getBucketAcl(new GetBucketAclRequest(bucketName));
    }

    @Override
    public AccessControlList getBucketAcl(GetBucketAclRequest getBucketAclRequest) throws SdkClientException, AmazonServiceException {
        getBucketAclRequest = this.beforeClientExecution(getBucketAclRequest);
        String bucketName = getBucketAclRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when requesting a bucket's ACL");
        return this.getAcl(bucketName, null, null, false, getBucketAclRequest);
    }

    @Override
    public void setBucketAcl(String bucketName, AccessControlList acl) throws SdkClientException, AmazonServiceException {
        this.setBucketAcl(new SetBucketAclRequest(bucketName, acl));
    }

    public void setBucketAcl(String bucketName, AccessControlList acl, RequestMetricCollector requestMetricCollector) {
        SetBucketAclRequest request = (SetBucketAclRequest)new SetBucketAclRequest(bucketName, acl).withRequestMetricCollector(requestMetricCollector);
        this.setBucketAcl(request);
    }

    @Override
    public void setBucketAcl(String bucketName, CannedAccessControlList cannedAcl) throws SdkClientException, AmazonServiceException {
        this.setBucketAcl(new SetBucketAclRequest(bucketName, cannedAcl));
    }

    public void setBucketAcl(String bucketName, CannedAccessControlList cannedAcl, RequestMetricCollector requestMetricCollector) throws SdkClientException, AmazonServiceException {
        SetBucketAclRequest request = (SetBucketAclRequest)new SetBucketAclRequest(bucketName, cannedAcl).withRequestMetricCollector(requestMetricCollector);
        this.setBucketAcl(request);
    }

    @Override
    public void setBucketAcl(SetBucketAclRequest setBucketAclRequest) throws SdkClientException, AmazonServiceException {
        setBucketAclRequest = this.beforeClientExecution(setBucketAclRequest);
        String bucketName = setBucketAclRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting a bucket's ACL");
        AccessControlList acl = setBucketAclRequest.getAcl();
        CannedAccessControlList cannedAcl = setBucketAclRequest.getCannedAcl();
        if (acl == null && cannedAcl == null) {
            throw new IllegalArgumentException("The ACL parameter must be specified when setting a bucket's ACL");
        }
        if (acl != null && cannedAcl != null) {
            throw new IllegalArgumentException("Only one of the acl and cannedAcl parameter can be specified, not both.");
        }
        if (acl != null) {
            this.setAcl(bucketName, null, null, acl, false, (AmazonWebServiceRequest)setBucketAclRequest);
        } else {
            this.setAcl(bucketName, null, null, cannedAcl, false, (AmazonWebServiceRequest)setBucketAclRequest);
        }
    }

    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String key) throws SdkClientException, AmazonServiceException {
        return this.getObjectMetadata(new GetObjectMetadataRequest(bucketName, key));
    }

    @Override
    public ObjectMetadata getObjectMetadata(GetObjectMetadataRequest getObjectMetadataRequest) throws SdkClientException, AmazonServiceException {
        getObjectMetadataRequest = this.beforeClientExecution(getObjectMetadataRequest);
        this.rejectNull(getObjectMetadataRequest, "The GetObjectMetadataRequest parameter must be specified when requesting an object's metadata");
        String bucketName = getObjectMetadataRequest.getBucketName();
        String key = getObjectMetadataRequest.getKey();
        String versionId = getObjectMetadataRequest.getVersionId();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when requesting an object's metadata");
        this.rejectNull(key, "The key parameter must be specified when requesting an object's metadata");
        Request<GetObjectMetadataRequest> request = this.createRequest(bucketName, key, getObjectMetadataRequest, HttpMethodName.HEAD);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "HeadObject");
        if (versionId != null) {
            request.addParameter("versionId", versionId);
        }
        AmazonS3Client.populateRequesterPaysHeader(request, getObjectMetadataRequest.isRequesterPays());
        this.addPartNumberIfNotNull(request, getObjectMetadataRequest.getPartNumber());
        AmazonS3Client.populateSSE_C(request, getObjectMetadataRequest.getSSECustomerKey());
        return this.invoke(request, new S3MetadataResponseHandler(), bucketName, key);
    }

    @Override
    public S3Object getObject(String bucketName, String key) throws SdkClientException, AmazonServiceException {
        return this.getObject(new GetObjectRequest(bucketName, key));
    }

    @Override
    public boolean doesBucketExist(String bucketName) throws SdkClientException, AmazonServiceException {
        try {
            ValidationUtils.assertStringNotEmpty(bucketName, "bucketName");
            this.headBucket(new HeadBucketRequest(bucketName));
            return true;
        }
        catch (AmazonServiceException ase) {
            if (ase.getStatusCode() == 301 || ase.getStatusCode() == 403) {
                return true;
            }
            if (ase.getStatusCode() == 404) {
                return false;
            }
            throw ase;
        }
    }

    @Override
    public boolean doesBucketExistV2(String bucketName) throws SdkClientException {
        try {
            ValidationUtils.assertStringNotEmpty(bucketName, "bucketName");
            this.getBucketAcl(bucketName);
            return true;
        }
        catch (AmazonServiceException ase) {
            if (ase.getStatusCode() == 301 || "AccessDenied".equals(ase.getErrorCode())) {
                return true;
            }
            if (ase.getStatusCode() == 404) {
                return false;
            }
            throw ase;
        }
    }

    @Override
    public boolean doesObjectExist(String bucketName, String objectName) throws AmazonServiceException, SdkClientException {
        try {
            ValidationUtils.assertStringNotEmpty(bucketName, "bucketName");
            ValidationUtils.assertStringNotEmpty(objectName, "objectName");
            this.getObjectMetadata(bucketName, objectName);
            return true;
        }
        catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public HeadBucketResult headBucket(HeadBucketRequest headBucketRequest) throws SdkClientException, AmazonServiceException {
        headBucketRequest = this.beforeClientExecution(headBucketRequest);
        String bucketName = headBucketRequest.getBucketName();
        this.rejectNull(bucketName, "The bucketName parameter must be specified.");
        Request<HeadBucketRequest> request = this.createRequest(bucketName, null, headBucketRequest, HttpMethodName.HEAD);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "HeadBucket");
        return this.invoke(request, new HeadBucketResultHandler(), bucketName, null);
    }

    @Override
    public void changeObjectStorageClass(String bucketName, String key, StorageClass newStorageClass) throws SdkClientException, AmazonServiceException {
        this.rejectNull(bucketName, "The bucketName parameter must be specified when changing an object's storage class");
        this.rejectNull(key, "The key parameter must be specified when changing an object's storage class");
        this.rejectNull((Object)newStorageClass, "The newStorageClass parameter must be specified when changing an object's storage class");
        this.copyObject(new CopyObjectRequest(bucketName, key, bucketName, key).withStorageClass(newStorageClass.toString()));
    }

    @Override
    public void setObjectRedirectLocation(String bucketName, String key, String newRedirectLocation) throws SdkClientException, AmazonServiceException {
        this.rejectNull(bucketName, "The bucketName parameter must be specified when changing an object's storage class");
        this.rejectNull(key, "The key parameter must be specified when changing an object's storage class");
        this.rejectNull(newRedirectLocation, "The newStorageClass parameter must be specified when changing an object's storage class");
        this.copyObject(new CopyObjectRequest(bucketName, key, bucketName, key).withRedirectLocation(newRedirectLocation));
    }

    @Override
    public S3Object getObject(GetObjectRequest getObjectRequest) throws SdkClientException, AmazonServiceException {
        getObjectRequest = this.beforeClientExecution(getObjectRequest);
        ValidationUtils.assertNotNull(getObjectRequest, "GetObjectRequest");
        ValidationUtils.assertStringNotEmpty(getObjectRequest.getBucketName(), "BucketName");
        ValidationUtils.assertStringNotEmpty(getObjectRequest.getKey(), "Key");
        Request<GetObjectRequest> request = this.createRequest(getObjectRequest.getBucketName(), getObjectRequest.getKey(), getObjectRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObject");
        request.addHandlerContext(HandlerContextKey.HAS_STREAMING_OUTPUT, Boolean.TRUE);
        if (getObjectRequest.getVersionId() != null) {
            request.addParameter("versionId", getObjectRequest.getVersionId());
        }
        this.addPartNumberIfNotNull(request, getObjectRequest.getPartNumber());
        long[] range = getObjectRequest.getRange();
        if (range != null) {
            request.addHeader("Range", "bytes=" + Long.toString(range[0]) + "-" + Long.toString(range[1]));
        }
        AmazonS3Client.populateRequesterPaysHeader(request, getObjectRequest.isRequesterPays());
        AmazonS3Client.addResponseHeaderParameters(request, getObjectRequest.getResponseHeaders());
        AmazonS3Client.addDateHeader(request, "If-Modified-Since", getObjectRequest.getModifiedSinceConstraint());
        AmazonS3Client.addDateHeader(request, "If-Unmodified-Since", getObjectRequest.getUnmodifiedSinceConstraint());
        AmazonS3Client.addStringListHeader(request, "If-Match", getObjectRequest.getMatchingETagConstraints());
        AmazonS3Client.addStringListHeader(request, "If-None-Match", getObjectRequest.getNonmatchingETagConstraints());
        AmazonS3Client.populateSSE_C(request, getObjectRequest.getSSECustomerKey());
        ProgressListener listener = getObjectRequest.getGeneralProgressListener();
        SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
        try {
            S3Object s3Object = this.invoke(request, new S3ObjectResponseHandler(), getObjectRequest.getBucketName(), getObjectRequest.getKey());
            s3Object.setBucketName(getObjectRequest.getBucketName());
            s3Object.setKey(getObjectRequest.getKey());
            boolean skipClientSideValidation = this.skipMd5CheckStrategy.skipClientSideValidation(getObjectRequest, s3Object.getObjectMetadata());
            this.postProcessS3Object(s3Object, skipClientSideValidation, listener);
            return s3Object;
        }
        catch (AmazonS3Exception ase) {
            if (ase.getStatusCode() == 412 || ase.getStatusCode() == 304) {
                SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_CANCELED_EVENT);
                return null;
            }
            SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw ase;
        }
    }

    private void postProcessS3Object(S3Object s3Object, boolean skipClientSideValidation, ProgressListener listener) {
        FilterInputStream is = s3Object.getObjectContent();
        HttpRequestBase httpRequest = s3Object.getObjectContent().getHttpRequest();
        is = new ServiceClientHolderInputStream(is, this);
        ProgressInputStream progressInputStream = new ProgressInputStream(is, listener){

            @Override
            protected void onEOF() {
                SDKProgressPublisher.publishProgress(this.getListener(), ProgressEventType.TRANSFER_COMPLETED_EVENT);
            }
        };
        is = progressInputStream;
        if (!skipClientSideValidation) {
            byte[] serverSideHash = BinaryUtils.fromHex(s3Object.getObjectMetadata().getETag());
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                is = new DigestValidationInputStream(is, digest, serverSideHash);
            }
            catch (NoSuchAlgorithmException e) {
                log.warn((Object)"No MD5 digest algorithm available.  Unable to calculate checksum and verify data integrity.", (Throwable)e);
            }
        } else {
            Object contentLength = s3Object.getObjectMetadata().getRawMetadataValue("Content-Length");
            if (contentLength != null) {
                is = new LengthCheckInputStream(is, s3Object.getObjectMetadata().getContentLength(), true);
            }
        }
        S3AbortableInputStream abortableInputStream = new S3AbortableInputStream(is, httpRequest, s3Object.getObjectMetadata().getContentLength());
        s3Object.setObjectContent(new S3ObjectInputStream(abortableInputStream, httpRequest, false));
    }

    @Override
    public ObjectMetadata getObject(final GetObjectRequest getObjectRequest, File destinationFile) throws SdkClientException, AmazonServiceException {
        this.rejectNull(destinationFile, "The destination file parameter must be specified when downloading an object directly to a file");
        S3Object s3Object = ServiceUtils.retryableDownloadS3ObjectToFile(destinationFile, new ServiceUtils.RetryableS3DownloadTask(){

            @Override
            public S3Object getS3ObjectStream() {
                return AmazonS3Client.this.getObject(getObjectRequest);
            }

            @Override
            public boolean needIntegrityCheck() {
                return !AmazonS3Client.this.skipMd5CheckStrategy.skipClientSideValidationPerRequest(getObjectRequest);
            }
        }, false);
        if (s3Object == null) {
            return null;
        }
        return s3Object.getObjectMetadata();
    }

    @Override
    public String getObjectAsString(String bucketName, String key) throws AmazonServiceException, SdkClientException {
        this.rejectNull(bucketName, "Bucket name must be provided");
        this.rejectNull(key, "Object key must be provided");
        S3Object object = this.getObject(bucketName, key);
        try {
            String string = IOUtils.toString(object.getObjectContent());
            return string;
        }
        catch (IOException e) {
            throw new SdkClientException("Error streaming content from S3 during download", e);
        }
        finally {
            IOUtils.closeQuietly(object, log);
        }
    }

    @Override
    public GetObjectTaggingResult getObjectTagging(GetObjectTaggingRequest getObjectTaggingRequest) {
        getObjectTaggingRequest = this.beforeClientExecution(getObjectTaggingRequest);
        this.rejectNull(getObjectTaggingRequest, "The request parameter must be specified when getting the object tags");
        String bucketName = ValidationUtils.assertStringNotEmpty(getObjectTaggingRequest.getBucketName(), "BucketName");
        String key = ValidationUtils.assertNotNull(getObjectTaggingRequest.getKey(), "Key");
        Request<GetObjectTaggingRequest> request = this.createRequest(bucketName, key, getObjectTaggingRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObjectTagging");
        request.addParameter("tagging", null);
        AmazonS3Client.addParameterIfNotNull(request, "versionId", getObjectTaggingRequest.getVersionId());
        AmazonS3Client.populateRequesterPaysHeader(request, getObjectTaggingRequest.isRequesterPays());
        ResponseHeaderHandlerChain<GetObjectTaggingResult> handlerChain = new ResponseHeaderHandlerChain<GetObjectTaggingResult>(new Unmarshallers.GetObjectTaggingResponseUnmarshaller(), new GetObjectTaggingResponseHeaderHandler());
        return (GetObjectTaggingResult)this.invoke(request, handlerChain, bucketName, key);
    }

    @Override
    public SetObjectTaggingResult setObjectTagging(SetObjectTaggingRequest setObjectTaggingRequest) {
        setObjectTaggingRequest = this.beforeClientExecution(setObjectTaggingRequest);
        this.rejectNull(setObjectTaggingRequest, "The request parameter must be specified setting the object tags");
        String bucketName = ValidationUtils.assertStringNotEmpty(setObjectTaggingRequest.getBucketName(), "BucketName");
        String key = ValidationUtils.assertNotNull(setObjectTaggingRequest.getKey(), "Key");
        ObjectTagging tagging = ValidationUtils.assertNotNull(setObjectTaggingRequest.getTagging(), "ObjectTagging");
        Request<SetObjectTaggingRequest> request = this.createRequest(bucketName, key, setObjectTaggingRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectTagging");
        request.addParameter("tagging", null);
        AmazonS3Client.addParameterIfNotNull(request, "versionId", setObjectTaggingRequest.getVersionId());
        byte[] content = new ObjectTaggingXmlFactory().convertToXmlByteArray(tagging);
        this.setContent(request, content, "application/xml", true);
        AmazonS3Client.populateRequesterPaysHeader(request, setObjectTaggingRequest.isRequesterPays());
        ResponseHeaderHandlerChain<SetObjectTaggingResult> handlerChain = new ResponseHeaderHandlerChain<SetObjectTaggingResult>(new Unmarshallers.SetObjectTaggingResponseUnmarshaller(), new SetObjectTaggingResponseHeaderHandler());
        return (SetObjectTaggingResult)this.invoke(request, handlerChain, bucketName, key);
    }

    @Override
    public DeleteObjectTaggingResult deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) {
        deleteObjectTaggingRequest = this.beforeClientExecution(deleteObjectTaggingRequest);
        this.rejectNull(deleteObjectTaggingRequest, "The request parameter must be specified when delete the object tags");
        String bucketName = ValidationUtils.assertStringNotEmpty(deleteObjectTaggingRequest.getBucketName(), "BucketName");
        String key = ValidationUtils.assertStringNotEmpty(deleteObjectTaggingRequest.getKey(), "Key");
        Request<DeleteObjectTaggingRequest> request = this.createRequest(bucketName, key, deleteObjectTaggingRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteObjectTagging");
        request.addParameter("tagging", null);
        AmazonS3Client.addParameterIfNotNull(request, "versionId", deleteObjectTaggingRequest.getVersionId());
        ResponseHeaderHandlerChain<DeleteObjectTaggingResult> handlerChain = new ResponseHeaderHandlerChain<DeleteObjectTaggingResult>(new Unmarshallers.DeleteObjectTaggingResponseUnmarshaller(), new DeleteObjectTaggingHeaderHandler());
        return (DeleteObjectTaggingResult)this.invoke(request, handlerChain, bucketName, key);
    }

    @Override
    public void deleteBucket(String bucketName) throws SdkClientException, AmazonServiceException {
        this.deleteBucket(new DeleteBucketRequest(bucketName));
    }

    @Override
    public void deleteBucket(DeleteBucketRequest deleteBucketRequest) throws SdkClientException, AmazonServiceException {
        deleteBucketRequest = this.beforeClientExecution(deleteBucketRequest);
        this.rejectNull(deleteBucketRequest, "The DeleteBucketRequest parameter must be specified when deleting a bucket");
        String bucketName = deleteBucketRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when deleting a bucket");
        Request<DeleteBucketRequest> request = this.createRequest(bucketName, null, deleteBucketRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucket");
        this.invoke(request, this.voidResponseHandler, bucketName, null);
        bucketRegionCache.remove(bucketName);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, File file) throws SdkClientException, AmazonServiceException {
        return this.putObject(new PutObjectRequest(bucketName, key, file).withMetadata(new ObjectMetadata()));
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata) throws SdkClientException, AmazonServiceException {
        return this.putObject(new PutObjectRequest(bucketName, key, input, metadata));
    }

    @Override
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws SdkClientException, AmazonServiceException {
        Boolean bucketKeyEnabled;
        putObjectRequest = this.beforeClientExecution(putObjectRequest);
        this.rejectNull(putObjectRequest, "The PutObjectRequest parameter must be specified when uploading an object");
        File file = putObjectRequest.getFile();
        InputStream isOrig = putObjectRequest.getInputStream();
        String bucketName = putObjectRequest.getBucketName();
        String key = putObjectRequest.getKey();
        ProgressListener listener = putObjectRequest.getGeneralProgressListener();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when uploading an object");
        this.rejectNull(key, "The key parameter must be specified when uploading an object");
        ObjectMetadata metadata = putObjectRequest.getMetadata();
        if (metadata == null) {
            metadata = new ObjectMetadata();
        }
        Request<PutObjectRequest> request = this.createRequest(bucketName, key, putObjectRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObject");
        request.addHandlerContext(HandlerContextKey.REQUIRES_LENGTH, Boolean.TRUE);
        request.addHandlerContext(HandlerContextKey.HAS_STREAMING_INPUT, Boolean.TRUE);
        Integer bufsize = Constants.getS3StreamBufferSize();
        if (bufsize != null) {
            AmazonWebServiceRequest awsreq = request.getOriginalRequest();
            awsreq.getRequestClientOptions().setReadLimit(bufsize);
        }
        if (putObjectRequest.getAccessControlList() != null) {
            AmazonS3Client.addAclHeaders(request, putObjectRequest.getAccessControlList());
        } else if (putObjectRequest.getCannedAcl() != null) {
            request.addHeader("x-amz-acl", putObjectRequest.getCannedAcl().toString());
        }
        if (putObjectRequest.getStorageClass() != null) {
            request.addHeader("x-amz-storage-class", putObjectRequest.getStorageClass());
        }
        if (putObjectRequest.getRedirectLocation() != null) {
            request.addHeader("x-amz-website-redirect-location", putObjectRequest.getRedirectLocation());
        }
        if ((bucketKeyEnabled = putObjectRequest.getBucketKeyEnabled()) != null) {
            AmazonS3Client.addHeaderIfNotNull(request, "x-amz-server-side-encryption-bucket-key-enabled", String.valueOf(bucketKeyEnabled));
        }
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-tagging", this.urlEncodeTags(putObjectRequest.getTagging()));
        AmazonS3Client.populateRequesterPaysHeader(request, putObjectRequest.isRequesterPays());
        AmazonS3Client.populateSSE_C(request, putObjectRequest.getSSECustomerKey());
        AmazonS3Client.populateSSE_KMS(request, putObjectRequest.getSSEAwsKeyManagementParams());
        AmazonS3Client.populateObjectLockHeaders(request, putObjectRequest.getObjectLockMode(), putObjectRequest.getObjectLockRetainUntilDate(), putObjectRequest.getObjectLockLegalHoldStatus());
        return this.uploadObject(isOrig, file, metadata, listener, request, putObjectRequest, this.skipMd5CheckStrategy.skipServerSideValidation(putObjectRequest), this.skipMd5CheckStrategy.skipClientSideValidationPerRequest(putObjectRequest), new PutObjectStrategy(bucketName, key), true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <RequestT, ResponseT> ResponseT uploadObject(InputStream originalStream, File file, ObjectMetadata metadata, ProgressListener listener, Request<RequestT> request, S3DataSource originalRequest, boolean skipServerSideValidation, boolean skipClientSideValidationPerRequest, UploadObjectStrategy<RequestT, ResponseT> uploadStrategy, boolean setContentTypeIfNotProvided) {
        byte[] serverSideHash;
        byte[] clientSideHash;
        ObjectMetadata returnedMetadata;
        InputStream input = this.getInputStream(originalStream, file, metadata, request, skipServerSideValidation, setContentTypeIfNotProvided);
        MD5DigestCalculatingInputStream md5DigestStream = null;
        try {
            if (metadata.getContentMD5() == null && !skipClientSideValidationPerRequest) {
                md5DigestStream = new MD5DigestCalculatingInputStream(input);
                input = md5DigestStream;
            }
            AmazonS3Client.populateRequestMetadata(request, metadata);
            request.setContent(input);
            SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
            try {
                returnedMetadata = uploadStrategy.invokeServiceCall(request);
            }
            catch (Throwable t) {
                SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
                throw Throwables.failure(t);
            }
        }
        finally {
            S3DataSource.Utils.cleanupDataSource(originalRequest, file, originalStream, input, log);
        }
        String contentMd5 = metadata.getContentMD5();
        if (md5DigestStream != null) {
            contentMd5 = Base64.encodeAsString(md5DigestStream.getMd5Digest());
        }
        String etag = returnedMetadata.getETag();
        if (contentMd5 != null && !this.skipMd5CheckStrategy.skipClientSideValidationPerPutResponse(returnedMetadata) && !Arrays.equals(clientSideHash = BinaryUtils.fromBase64(contentMd5), serverSideHash = BinaryUtils.fromHex(etag))) {
            SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw new SdkClientException("Unable to verify integrity of data upload. Client calculated content hash (contentMD5: " + contentMd5 + " in base 64) didn't match hash (etag: " + etag + " in hex) calculated by Amazon S3.  You may need to delete the data stored in Amazon S3. (metadata.contentMD5: " + metadata.getContentMD5() + ", md5DigestStream: " + md5DigestStream + uploadStrategy.md5ValidationErrorSuffix() + ")");
        }
        SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_COMPLETED_EVENT);
        return uploadStrategy.createResult(returnedMetadata, contentMd5);
    }

    private InputStream getInputStream(InputStream origStream, File file, ObjectMetadata metadata, Request<?> request, boolean skipServerSideValidation, boolean setContentTypeIfNotProvided) {
        Long contentLength;
        InputStream input = origStream;
        if (file == null) {
            if (input != null) {
                input = ReleasableInputStream.wrap(input);
            }
        } else {
            boolean calculateMD5;
            metadata.setContentLength(file.length());
            boolean bl = calculateMD5 = metadata.getContentMD5() == null;
            if (metadata.getContentType() == null && setContentTypeIfNotProvided) {
                metadata.setContentType(Mimetypes.getInstance().getMimetype(file));
            }
            if (calculateMD5 && !skipServerSideValidation) {
                try {
                    String contentMd5_b64 = Md5Utils.md5AsBase64(file);
                    metadata.setContentMD5(contentMd5_b64);
                }
                catch (Exception e) {
                    throw new SdkClientException("Unable to calculate MD5 hash: " + e.getMessage(), e);
                }
            }
            input = ResettableInputStream.newResettableInputStream(file, "Unable to find file to upload");
        }
        if (metadata.getContentType() == null && setContentTypeIfNotProvided) {
            metadata.setContentType("application/octet-stream");
        }
        if (request.getHeaders().get("x-amz-website-redirect-location") != null && input == null) {
            input = new ByteArrayInputStream(new byte[0]);
        }
        if ((contentLength = (Long)metadata.getRawMetadataValue("Content-Length")) == null) {
            log.warn((Object)"No content length specified for stream data.  Stream contents will be buffered in memory and could result in out of memory errors.");
        } else {
            long expectedLength = contentLength;
            if (expectedLength >= 0L) {
                LengthCheckInputStream lcis = new LengthCheckInputStream(input, expectedLength, false);
                input = lcis;
            }
        }
        return input;
    }

    private static PutObjectResult createPutObjectResult(ObjectMetadata metadata) {
        PutObjectResult result = new PutObjectResult();
        result.setVersionId(metadata.getVersionId());
        result.setSSEAlgorithm(metadata.getSSEAlgorithm());
        result.setSSECustomerAlgorithm(metadata.getSSECustomerAlgorithm());
        result.setSSECustomerKeyMd5(metadata.getSSECustomerKeyMd5());
        result.setExpirationTime(metadata.getExpirationTime());
        result.setExpirationTimeRuleId(metadata.getExpirationTimeRuleId());
        result.setETag(metadata.getETag());
        result.setMetadata(metadata);
        result.setRequesterCharged(metadata.isRequesterCharged());
        result.setBucketKeyEnabled(metadata.getBucketKeyEnabled());
        return result;
    }

    private static void addAclHeaders(Request<? extends AmazonWebServiceRequest> request, AccessControlList acl) {
        List<Grant> grants = acl.getGrantsAsList();
        HashMap grantsByPermission = new HashMap();
        for (Grant grant : grants) {
            if (!grantsByPermission.containsKey((Object)grant.getPermission())) {
                grantsByPermission.put(grant.getPermission(), new LinkedList());
            }
            ((Collection)grantsByPermission.get((Object)grant.getPermission())).add(grant.getGrantee());
        }
        for (Permission permission : Permission.values()) {
            if (!grantsByPermission.containsKey((Object)permission)) continue;
            Collection grantees = (Collection)grantsByPermission.get((Object)permission);
            boolean seenOne = false;
            StringBuilder granteeString = new StringBuilder();
            for (Grantee grantee : grantees) {
                if (!seenOne) {
                    seenOne = true;
                } else {
                    granteeString.append(", ");
                }
                granteeString.append(grantee.getTypeIdentifier()).append("=").append("\"").append(grantee.getIdentifier()).append("\"");
            }
            request.addHeader(permission.getHeaderName(), granteeString.toString());
        }
    }

    @Override
    public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey) throws SdkClientException, AmazonServiceException {
        return this.copyObject(new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey));
    }

    @Override
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws SdkClientException, AmazonServiceException {
        copyObjectRequest = this.beforeClientExecution(copyObjectRequest);
        this.rejectNull(copyObjectRequest.getSourceBucketName(), "The source bucket name must be specified when copying an object");
        this.rejectNull(copyObjectRequest.getSourceKey(), "The source object key must be specified when copying an object");
        this.rejectNull(copyObjectRequest.getDestinationBucketName(), "The destination bucket name must be specified when copying an object");
        this.rejectNull(copyObjectRequest.getDestinationKey(), "The destination object key must be specified when copying an object");
        String destinationKey = copyObjectRequest.getDestinationKey();
        String destinationBucketName = copyObjectRequest.getDestinationBucketName();
        Request<CopyObjectRequest> request = this.createRequest(destinationBucketName, destinationKey, copyObjectRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CopyObject");
        Boolean bucketKeyEnabled = copyObjectRequest.getBucketKeyEnabled();
        if (bucketKeyEnabled != null) {
            AmazonS3Client.addHeaderIfNotNull(request, "x-amz-server-side-encryption-bucket-key-enabled", String.valueOf(bucketKeyEnabled));
        }
        this.populateRequestWithCopyObjectParameters(request, copyObjectRequest);
        AmazonS3Client.populateSSE_KMS(request, copyObjectRequest.getSSEAwsKeyManagementParams());
        AmazonS3Client.populateObjectLockHeaders(request, copyObjectRequest.getObjectLockMode(), copyObjectRequest.getObjectLockRetainUntilDate(), copyObjectRequest.getObjectLockLegalHoldStatus());
        this.setZeroContentLength(request);
        XmlResponsesSaxParser.CopyObjectResultHandler copyObjectResultHandler = null;
        try {
            ResponseHeaderHandlerChain<XmlResponsesSaxParser.CopyObjectResultHandler> handler = new ResponseHeaderHandlerChain<XmlResponsesSaxParser.CopyObjectResultHandler>(new Unmarshallers.CopyObjectUnmarshaller(), new ServerSideEncryptionHeaderHandler(), new S3VersionHeaderHandler(), new ObjectExpirationHeaderHandler(), new S3RequesterChargedHeaderHandler());
            copyObjectResultHandler = (XmlResponsesSaxParser.CopyObjectResultHandler)this.invoke(request, handler, destinationBucketName, destinationKey);
        }
        catch (AmazonS3Exception ase) {
            if (ase.getStatusCode() == 412) {
                return null;
            }
            throw ase;
        }
        if (copyObjectResultHandler.getErrorCode() != null) {
            String errorCode = copyObjectResultHandler.getErrorCode();
            String errorMessage = copyObjectResultHandler.getErrorMessage();
            String requestId = copyObjectResultHandler.getErrorRequestId();
            String hostId = copyObjectResultHandler.getErrorHostId();
            AmazonS3Exception ase = new AmazonS3Exception(errorMessage);
            ase.setErrorCode(errorCode);
            ase.setErrorType(AmazonServiceException.ErrorType.Service);
            ase.setRequestId(requestId);
            ase.setExtendedRequestId(hostId);
            ase.setServiceName(request.getServiceName());
            ase.setStatusCode(200);
            ase.setProxyHost(this.clientConfiguration.getProxyHost());
            throw ase;
        }
        CopyObjectResult copyObjectResult = new CopyObjectResult();
        copyObjectResult.setETag(copyObjectResultHandler.getETag());
        copyObjectResult.setLastModifiedDate(copyObjectResultHandler.getLastModified());
        copyObjectResult.setVersionId(copyObjectResultHandler.getVersionId());
        copyObjectResult.setSSEAlgorithm(copyObjectResultHandler.getSSEAlgorithm());
        copyObjectResult.setSSECustomerAlgorithm(copyObjectResultHandler.getSSECustomerAlgorithm());
        copyObjectResult.setSSECustomerKeyMd5(copyObjectResultHandler.getSSECustomerKeyMd5());
        copyObjectResult.setBucketKeyEnabled(copyObjectResultHandler.getBucketKeyEnabled());
        copyObjectResult.setExpirationTime(copyObjectResultHandler.getExpirationTime());
        copyObjectResult.setExpirationTimeRuleId(copyObjectResultHandler.getExpirationTimeRuleId());
        copyObjectResult.setRequesterCharged(copyObjectResultHandler.isRequesterCharged());
        return copyObjectResult;
    }

    @Override
    public CopyPartResult copyPart(CopyPartRequest copyPartRequest) {
        copyPartRequest = this.beforeClientExecution(copyPartRequest);
        this.rejectNull(copyPartRequest.getSourceBucketName(), "The source bucket name must be specified when copying a part");
        this.rejectNull(copyPartRequest.getSourceKey(), "The source object key must be specified when copying a part");
        this.rejectNull(copyPartRequest.getDestinationBucketName(), "The destination bucket name must be specified when copying a part");
        this.rejectNull(copyPartRequest.getUploadId(), "The upload id must be specified when copying a part");
        this.rejectNull(copyPartRequest.getDestinationKey(), "The destination object key must be specified when copying a part");
        this.rejectNull(copyPartRequest.getPartNumber(), "The part number must be specified when copying a part");
        String destinationKey = copyPartRequest.getDestinationKey();
        String destinationBucketName = copyPartRequest.getDestinationBucketName();
        Request<CopyPartRequest> request = this.createRequest(destinationBucketName, destinationKey, copyPartRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "UploadPartCopy");
        this.populateRequestWithCopyPartParameters(request, copyPartRequest);
        request.addParameter("uploadId", copyPartRequest.getUploadId());
        request.addParameter("partNumber", Integer.toString(copyPartRequest.getPartNumber()));
        AmazonS3Client.populateRequesterPaysHeader(request, copyPartRequest.isRequesterPays());
        this.setZeroContentLength(request);
        XmlResponsesSaxParser.CopyObjectResultHandler copyObjectResultHandler = null;
        try {
            ResponseHeaderHandlerChain<XmlResponsesSaxParser.CopyObjectResultHandler> handler = new ResponseHeaderHandlerChain<XmlResponsesSaxParser.CopyObjectResultHandler>(new Unmarshallers.CopyObjectUnmarshaller(), new ServerSideEncryptionHeaderHandler(), new S3VersionHeaderHandler());
            copyObjectResultHandler = (XmlResponsesSaxParser.CopyObjectResultHandler)this.invoke(request, handler, destinationBucketName, destinationKey);
        }
        catch (AmazonS3Exception ase) {
            if (ase.getStatusCode() == 412) {
                return null;
            }
            throw ase;
        }
        if (copyObjectResultHandler.getErrorCode() != null) {
            String errorCode = copyObjectResultHandler.getErrorCode();
            String errorMessage = copyObjectResultHandler.getErrorMessage();
            String requestId = copyObjectResultHandler.getErrorRequestId();
            String hostId = copyObjectResultHandler.getErrorHostId();
            AmazonS3Exception ase = new AmazonS3Exception(errorMessage);
            ase.setErrorCode(errorCode);
            ase.setErrorType(AmazonServiceException.ErrorType.Service);
            ase.setRequestId(requestId);
            ase.setExtendedRequestId(hostId);
            ase.setServiceName(request.getServiceName());
            ase.setStatusCode(200);
            ase.setProxyHost(this.clientConfiguration.getProxyHost());
            throw ase;
        }
        CopyPartResult copyPartResult = new CopyPartResult();
        copyPartResult.setETag(copyObjectResultHandler.getETag());
        copyPartResult.setPartNumber(copyPartRequest.getPartNumber());
        copyPartResult.setLastModifiedDate(copyObjectResultHandler.getLastModified());
        copyPartResult.setVersionId(copyObjectResultHandler.getVersionId());
        copyPartResult.setSSEAlgorithm(copyObjectResultHandler.getSSEAlgorithm());
        copyPartResult.setSSECustomerAlgorithm(copyObjectResultHandler.getSSECustomerAlgorithm());
        copyPartResult.setSSECustomerKeyMd5(copyObjectResultHandler.getSSECustomerKeyMd5());
        copyPartResult.setBucketKeyEnabled(copyObjectResultHandler.getBucketKeyEnabled());
        return copyPartResult;
    }

    @Override
    public void deleteObject(String bucketName, String key) throws SdkClientException, AmazonServiceException {
        this.deleteObject(new DeleteObjectRequest(bucketName, key));
    }

    @Override
    public void deleteObject(DeleteObjectRequest deleteObjectRequest) throws SdkClientException, AmazonServiceException {
        deleteObjectRequest = this.beforeClientExecution(deleteObjectRequest);
        this.rejectNull(deleteObjectRequest, "The delete object request must be specified when deleting an object");
        this.rejectNull(deleteObjectRequest.getBucketName(), "The bucket name must be specified when deleting an object");
        this.rejectNull(deleteObjectRequest.getKey(), "The key must be specified when deleting an object");
        Request<DeleteObjectRequest> request = this.createRequest(deleteObjectRequest.getBucketName(), deleteObjectRequest.getKey(), deleteObjectRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteObject");
        this.invoke(request, this.voidResponseHandler, deleteObjectRequest.getBucketName(), deleteObjectRequest.getKey());
    }

    @Override
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {
        deleteObjectsRequest = this.beforeClientExecution(deleteObjectsRequest);
        Request<DeleteObjectsRequest> request = this.createRequest(deleteObjectsRequest.getBucketName(), null, deleteObjectsRequest, HttpMethodName.POST);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteObjects");
        request.addParameter("delete", null);
        if (deleteObjectsRequest.getBypassGovernanceRetention()) {
            request.addHeader("x-amz-bypass-governance-retention", "true");
        }
        if (deleteObjectsRequest.getMfa() != null) {
            this.populateRequestWithMfaDetails(request, deleteObjectsRequest.getMfa());
        }
        AmazonS3Client.populateRequesterPaysHeader(request, deleteObjectsRequest.isRequesterPays());
        byte[] content = new MultiObjectDeleteXmlFactory().convertToXmlByteArray(deleteObjectsRequest);
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        this.populateRequestHeaderWithMd5(request, content);
        ResponseHeaderHandlerChain<DeleteObjectsResponse> responseHandler = new ResponseHeaderHandlerChain<DeleteObjectsResponse>(new Unmarshallers.DeleteObjectsResultUnmarshaller(), new S3RequesterChargedHeaderHandler());
        DeleteObjectsResponse response = (DeleteObjectsResponse)this.invoke(request, responseHandler, deleteObjectsRequest.getBucketName(), null);
        if (!response.getErrors().isEmpty()) {
            Map<String, String> headers = responseHandler.getResponseHeaders();
            MultiObjectDeleteException ex = new MultiObjectDeleteException(response.getErrors(), response.getDeletedObjects());
            ex.setStatusCode(200);
            ex.setRequestId(headers.get("x-amz-request-id"));
            ex.setExtendedRequestId(headers.get("x-amz-id-2"));
            ex.setCloudFrontId(headers.get("X-Amz-Cf-Id"));
            ex.setProxyHost(this.clientConfiguration.getProxyHost());
            throw ex;
        }
        DeleteObjectsResult result = new DeleteObjectsResult(response.getDeletedObjects(), response.isRequesterCharged());
        return result;
    }

    @Override
    public void deleteVersion(String bucketName, String key, String versionId) throws SdkClientException, AmazonServiceException {
        this.deleteVersion(new DeleteVersionRequest(bucketName, key, versionId));
    }

    @Override
    public void deleteVersion(DeleteVersionRequest deleteVersionRequest) throws SdkClientException, AmazonServiceException {
        deleteVersionRequest = this.beforeClientExecution(deleteVersionRequest);
        this.rejectNull(deleteVersionRequest, "The delete version request object must be specified when deleting a version");
        String bucketName = deleteVersionRequest.getBucketName();
        String key = deleteVersionRequest.getKey();
        String versionId = deleteVersionRequest.getVersionId();
        this.rejectNull(bucketName, "The bucket name must be specified when deleting a version");
        this.rejectNull(key, "The key must be specified when deleting a version");
        this.rejectNull(versionId, "The version ID must be specified when deleting a version");
        Request<DeleteVersionRequest> request = this.createRequest(bucketName, key, deleteVersionRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteObject");
        if (versionId != null) {
            request.addParameter("versionId", versionId);
        }
        if (deleteVersionRequest.getMfa() != null) {
            this.populateRequestWithMfaDetails(request, deleteVersionRequest.getMfa());
        }
        if (deleteVersionRequest.getBypassGovernanceRetention()) {
            request.addHeader("x-amz-bypass-governance-retention", "true");
        }
        this.invoke(request, this.voidResponseHandler, bucketName, key);
    }

    @Override
    public void setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest) throws SdkClientException, AmazonServiceException {
        setBucketVersioningConfigurationRequest = this.beforeClientExecution(setBucketVersioningConfigurationRequest);
        this.rejectNull(setBucketVersioningConfigurationRequest, "The SetBucketVersioningConfigurationRequest object must be specified when setting versioning configuration");
        String bucketName = setBucketVersioningConfigurationRequest.getBucketName();
        BucketVersioningConfiguration versioningConfiguration = setBucketVersioningConfigurationRequest.getVersioningConfiguration();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting versioning configuration");
        this.rejectNull(versioningConfiguration, "The bucket versioning parameter must be specified when setting versioning configuration");
        if (versioningConfiguration.isMfaDeleteEnabled() != null) {
            this.rejectNull(setBucketVersioningConfigurationRequest.getMfa(), "The MFA parameter must be specified when changing MFA Delete status in the versioning configuration");
        }
        Request<SetBucketVersioningConfigurationRequest> request = this.createRequest(bucketName, null, setBucketVersioningConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketVersioning");
        request.addParameter("versioning", null);
        if (versioningConfiguration.isMfaDeleteEnabled() != null && setBucketVersioningConfigurationRequest.getMfa() != null) {
            this.populateRequestWithMfaDetails(request, setBucketVersioningConfigurationRequest.getMfa());
        }
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(versioningConfiguration);
        request.setContent(new ByteArrayInputStream(bytes));
        this.populateRequestHeaderWithMd5(request, bytes);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioningConfiguration(String bucketName) throws SdkClientException, AmazonServiceException {
        return this.getBucketVersioningConfiguration(new GetBucketVersioningConfigurationRequest(bucketName));
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioningConfiguration(GetBucketVersioningConfigurationRequest getBucketVersioningConfigurationRequest) throws SdkClientException, AmazonServiceException {
        getBucketVersioningConfigurationRequest = this.beforeClientExecution(getBucketVersioningConfigurationRequest);
        this.rejectNull(getBucketVersioningConfigurationRequest, "The request object parameter getBucketVersioningConfigurationRequest must be specified.");
        String bucketName = getBucketVersioningConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when querying versioning configuration");
        Request<GetBucketVersioningConfigurationRequest> request = this.createRequest(bucketName, null, getBucketVersioningConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketVersioning");
        request.addParameter("versioning", null);
        return this.invoke(request, new Unmarshallers.BucketVersioningConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(String bucketName) throws SdkClientException, AmazonServiceException {
        return this.getBucketWebsiteConfiguration(new GetBucketWebsiteConfigurationRequest(bucketName));
    }

    @Override
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(GetBucketWebsiteConfigurationRequest getBucketWebsiteConfigurationRequest) throws SdkClientException, AmazonServiceException {
        getBucketWebsiteConfigurationRequest = this.beforeClientExecution(getBucketWebsiteConfigurationRequest);
        this.rejectNull(getBucketWebsiteConfigurationRequest, "The request object parameter getBucketWebsiteConfigurationRequest must be specified.");
        String bucketName = getBucketWebsiteConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when requesting a bucket's website configuration");
        Request<GetBucketWebsiteConfigurationRequest> request = this.createRequest(bucketName, null, getBucketWebsiteConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketWebsite");
        request.addParameter("website", null);
        request.addHeader("Content-Type", "application/xml");
        try {
            return this.invoke(request, new Unmarshallers.BucketWebsiteConfigurationUnmarshaller(), bucketName, null);
        }
        catch (AmazonServiceException ase) {
            if (ase.getStatusCode() == 404) {
                return null;
            }
            throw ase;
        }
    }

    @Override
    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(String bucketName) {
        return this.getBucketLifecycleConfiguration(new GetBucketLifecycleConfigurationRequest(bucketName));
    }

    @Override
    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) {
        getBucketLifecycleConfigurationRequest = this.beforeClientExecution(getBucketLifecycleConfigurationRequest);
        this.rejectNull(getBucketLifecycleConfigurationRequest, "The request object pamameter getBucketLifecycleConfigurationRequest must be specified.");
        String bucketName = getBucketLifecycleConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name must be specifed when retrieving the bucket lifecycle configuration.");
        Request<GetBucketLifecycleConfigurationRequest> request = this.createRequest(bucketName, null, getBucketLifecycleConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketLifecycleConfiguration");
        request.addParameter("lifecycle", null);
        try {
            return this.invoke(request, new Unmarshallers.BucketLifecycleConfigurationUnmarshaller(), bucketName, null);
        }
        catch (AmazonServiceException ase) {
            switch (ase.getStatusCode()) {
                case 404: {
                    return null;
                }
            }
            throw ase;
        }
    }

    @Override
    public void setBucketLifecycleConfiguration(String bucketName, BucketLifecycleConfiguration bucketLifecycleConfiguration) {
        this.setBucketLifecycleConfiguration(new SetBucketLifecycleConfigurationRequest(bucketName, bucketLifecycleConfiguration));
    }

    @Override
    public void setBucketLifecycleConfiguration(SetBucketLifecycleConfigurationRequest setBucketLifecycleConfigurationRequest) {
        setBucketLifecycleConfigurationRequest = this.beforeClientExecution(setBucketLifecycleConfigurationRequest);
        this.rejectNull(setBucketLifecycleConfigurationRequest, "The set bucket lifecycle configuration request object must be specified.");
        String bucketName = setBucketLifecycleConfigurationRequest.getBucketName();
        BucketLifecycleConfiguration bucketLifecycleConfiguration = setBucketLifecycleConfigurationRequest.getLifecycleConfiguration();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting bucket lifecycle configuration.");
        this.rejectNull(bucketLifecycleConfiguration, "The lifecycle configuration parameter must be specified when setting bucket lifecycle configuration.");
        Request<SetBucketLifecycleConfigurationRequest> request = this.createRequest(bucketName, null, setBucketLifecycleConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketLifecycleConfiguration");
        request.addParameter("lifecycle", null);
        byte[] content = new BucketConfigurationXmlFactory().convertToXmlByteArray(bucketLifecycleConfiguration);
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        this.populateRequestHeaderWithMd5(request, content);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public void deleteBucketLifecycleConfiguration(String bucketName) {
        this.deleteBucketLifecycleConfiguration(new DeleteBucketLifecycleConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketLifecycleConfiguration(DeleteBucketLifecycleConfigurationRequest deleteBucketLifecycleConfigurationRequest) {
        deleteBucketLifecycleConfigurationRequest = this.beforeClientExecution(deleteBucketLifecycleConfigurationRequest);
        this.rejectNull(deleteBucketLifecycleConfigurationRequest, "The delete bucket lifecycle configuration request object must be specified.");
        String bucketName = deleteBucketLifecycleConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when deleting bucket lifecycle configuration.");
        Request<DeleteBucketLifecycleConfigurationRequest> request = this.createRequest(bucketName, null, deleteBucketLifecycleConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketLifecycle");
        request.addParameter("lifecycle", null);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(String bucketName) {
        return this.getBucketCrossOriginConfiguration(new GetBucketCrossOriginConfigurationRequest(bucketName));
    }

    @Override
    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(GetBucketCrossOriginConfigurationRequest getBucketCrossOriginConfigurationRequest) {
        getBucketCrossOriginConfigurationRequest = this.beforeClientExecution(getBucketCrossOriginConfigurationRequest);
        this.rejectNull(getBucketCrossOriginConfigurationRequest, "The request object parameter getBucketCrossOriginConfigurationRequest must be specified.");
        String bucketName = getBucketCrossOriginConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name must be specified when retrieving the bucket cross origin configuration.");
        Request<GetBucketCrossOriginConfigurationRequest> request = this.createRequest(bucketName, null, getBucketCrossOriginConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketCors");
        request.addParameter("cors", null);
        try {
            return this.invoke(request, new Unmarshallers.BucketCrossOriginConfigurationUnmarshaller(), bucketName, null);
        }
        catch (AmazonServiceException ase) {
            switch (ase.getStatusCode()) {
                case 404: {
                    return null;
                }
            }
            throw ase;
        }
    }

    @Override
    public void setBucketCrossOriginConfiguration(String bucketName, BucketCrossOriginConfiguration bucketCrossOriginConfiguration) {
        this.setBucketCrossOriginConfiguration(new SetBucketCrossOriginConfigurationRequest(bucketName, bucketCrossOriginConfiguration));
    }

    @Override
    public void setBucketCrossOriginConfiguration(SetBucketCrossOriginConfigurationRequest setBucketCrossOriginConfigurationRequest) {
        setBucketCrossOriginConfigurationRequest = this.beforeClientExecution(setBucketCrossOriginConfigurationRequest);
        this.rejectNull(setBucketCrossOriginConfigurationRequest, "The set bucket cross origin configuration request object must be specified.");
        String bucketName = setBucketCrossOriginConfigurationRequest.getBucketName();
        BucketCrossOriginConfiguration bucketCrossOriginConfiguration = setBucketCrossOriginConfigurationRequest.getCrossOriginConfiguration();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting bucket cross origin configuration.");
        this.rejectNull(bucketCrossOriginConfiguration, "The cross origin configuration parameter must be specified when setting bucket cross origin configuration.");
        Request<SetBucketCrossOriginConfigurationRequest> request = this.createRequest(bucketName, null, setBucketCrossOriginConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketCors");
        request.addParameter("cors", null);
        byte[] content = new BucketConfigurationXmlFactory().convertToXmlByteArray(bucketCrossOriginConfiguration);
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        this.populateRequestHeaderWithMd5(request, content);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public void deleteBucketCrossOriginConfiguration(String bucketName) {
        this.deleteBucketCrossOriginConfiguration(new DeleteBucketCrossOriginConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketCrossOriginConfiguration(DeleteBucketCrossOriginConfigurationRequest deleteBucketCrossOriginConfigurationRequest) {
        deleteBucketCrossOriginConfigurationRequest = this.beforeClientExecution(deleteBucketCrossOriginConfigurationRequest);
        this.rejectNull(deleteBucketCrossOriginConfigurationRequest, "The delete bucket cross origin configuration request object must be specified.");
        String bucketName = deleteBucketCrossOriginConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when deleting bucket cross origin configuration.");
        Request<DeleteBucketCrossOriginConfigurationRequest> request = this.createRequest(bucketName, null, deleteBucketCrossOriginConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketCors");
        request.addParameter("cors", null);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public BucketTaggingConfiguration getBucketTaggingConfiguration(String bucketName) {
        return this.getBucketTaggingConfiguration(new GetBucketTaggingConfigurationRequest(bucketName));
    }

    @Override
    public BucketTaggingConfiguration getBucketTaggingConfiguration(GetBucketTaggingConfigurationRequest getBucketTaggingConfigurationRequest) {
        getBucketTaggingConfigurationRequest = this.beforeClientExecution(getBucketTaggingConfigurationRequest);
        this.rejectNull(getBucketTaggingConfigurationRequest, "The request object parameter getBucketTaggingConfigurationRequest must be specifed.");
        String bucketName = getBucketTaggingConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name must be specified when retrieving the bucket tagging configuration.");
        Request<GetBucketTaggingConfigurationRequest> request = this.createRequest(bucketName, null, getBucketTaggingConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketTagging");
        request.addParameter("tagging", null);
        try {
            return this.invoke(request, new Unmarshallers.BucketTaggingConfigurationUnmarshaller(), bucketName, null);
        }
        catch (AmazonServiceException ase) {
            switch (ase.getStatusCode()) {
                case 404: {
                    return null;
                }
            }
            throw ase;
        }
    }

    @Override
    public void setBucketTaggingConfiguration(String bucketName, BucketTaggingConfiguration bucketTaggingConfiguration) {
        this.setBucketTaggingConfiguration(new SetBucketTaggingConfigurationRequest(bucketName, bucketTaggingConfiguration));
    }

    @Override
    public void setBucketTaggingConfiguration(SetBucketTaggingConfigurationRequest setBucketTaggingConfigurationRequest) {
        setBucketTaggingConfigurationRequest = this.beforeClientExecution(setBucketTaggingConfigurationRequest);
        this.rejectNull(setBucketTaggingConfigurationRequest, "The set bucket tagging configuration request object must be specified.");
        String bucketName = setBucketTaggingConfigurationRequest.getBucketName();
        BucketTaggingConfiguration bucketTaggingConfiguration = setBucketTaggingConfigurationRequest.getTaggingConfiguration();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting bucket tagging configuration.");
        this.rejectNull(bucketTaggingConfiguration, "The tagging configuration parameter must be specified when setting bucket tagging configuration.");
        Request<SetBucketTaggingConfigurationRequest> request = this.createRequest(bucketName, null, setBucketTaggingConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketTagging");
        request.addParameter("tagging", null);
        byte[] content = new BucketConfigurationXmlFactory().convertToXmlByteArray(bucketTaggingConfiguration);
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        this.populateRequestHeaderWithMd5(request, content);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public void deleteBucketTaggingConfiguration(String bucketName) {
        this.deleteBucketTaggingConfiguration(new DeleteBucketTaggingConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketTaggingConfiguration(DeleteBucketTaggingConfigurationRequest deleteBucketTaggingConfigurationRequest) {
        deleteBucketTaggingConfigurationRequest = this.beforeClientExecution(deleteBucketTaggingConfigurationRequest);
        this.rejectNull(deleteBucketTaggingConfigurationRequest, "The delete bucket tagging configuration request object must be specified.");
        String bucketName = deleteBucketTaggingConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when deleting bucket tagging configuration.");
        Request<DeleteBucketTaggingConfigurationRequest> request = this.createRequest(bucketName, null, deleteBucketTaggingConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketTagging");
        request.addParameter("tagging", null);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public void setBucketWebsiteConfiguration(String bucketName, BucketWebsiteConfiguration configuration) throws SdkClientException, AmazonServiceException {
        this.setBucketWebsiteConfiguration(new SetBucketWebsiteConfigurationRequest(bucketName, configuration));
    }

    @Override
    public void setBucketWebsiteConfiguration(SetBucketWebsiteConfigurationRequest setBucketWebsiteConfigurationRequest) throws SdkClientException, AmazonServiceException {
        setBucketWebsiteConfigurationRequest = this.beforeClientExecution(setBucketWebsiteConfigurationRequest);
        String bucketName = setBucketWebsiteConfigurationRequest.getBucketName();
        BucketWebsiteConfiguration configuration = setBucketWebsiteConfigurationRequest.getConfiguration();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting a bucket's website configuration");
        this.rejectNull(configuration, "The bucket website configuration parameter must be specified when setting a bucket's website configuration");
        if (configuration.getRedirectAllRequestsTo() == null) {
            this.rejectNull(configuration.getIndexDocumentSuffix(), "The bucket website configuration parameter must specify the index document suffix when setting a bucket's website configuration");
        }
        Request<SetBucketWebsiteConfigurationRequest> request = this.createRequest(bucketName, null, setBucketWebsiteConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketWebsite");
        request.addParameter("website", null);
        request.addHeader("Content-Type", "application/xml");
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(configuration);
        request.setContent(new ByteArrayInputStream(bytes));
        this.populateRequestHeaderWithMd5(request, bytes);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public void deleteBucketWebsiteConfiguration(String bucketName) throws SdkClientException, AmazonServiceException {
        this.deleteBucketWebsiteConfiguration(new DeleteBucketWebsiteConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketWebsiteConfiguration(DeleteBucketWebsiteConfigurationRequest deleteBucketWebsiteConfigurationRequest) throws SdkClientException, AmazonServiceException {
        deleteBucketWebsiteConfigurationRequest = this.beforeClientExecution(deleteBucketWebsiteConfigurationRequest);
        String bucketName = deleteBucketWebsiteConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when deleting a bucket's website configuration");
        Request<DeleteBucketWebsiteConfigurationRequest> request = this.createRequest(bucketName, null, deleteBucketWebsiteConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketWebsite");
        request.addParameter("website", null);
        request.addHeader("Content-Type", "application/xml");
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public void setBucketNotificationConfiguration(String bucketName, BucketNotificationConfiguration bucketNotificationConfiguration) throws SdkClientException, AmazonServiceException {
        this.setBucketNotificationConfiguration(new SetBucketNotificationConfigurationRequest(bucketName, bucketNotificationConfiguration));
    }

    @Override
    public void setBucketNotificationConfiguration(SetBucketNotificationConfigurationRequest setBucketNotificationConfigurationRequest) throws SdkClientException, AmazonServiceException {
        setBucketNotificationConfigurationRequest = this.beforeClientExecution(setBucketNotificationConfigurationRequest);
        this.rejectNull(setBucketNotificationConfigurationRequest, "The set bucket notification configuration request object must be specified.");
        String bucketName = setBucketNotificationConfigurationRequest.getBucketName();
        BucketNotificationConfiguration bucketNotificationConfiguration = setBucketNotificationConfigurationRequest.getNotificationConfiguration();
        Boolean skipDestinationValidation = setBucketNotificationConfigurationRequest.getSkipDestinationValidation();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting bucket notification configuration.");
        this.rejectNull(bucketNotificationConfiguration, "The notification configuration parameter must be specified when setting bucket notification configuration.");
        Request<SetBucketNotificationConfigurationRequest> request = this.createRequest(bucketName, null, setBucketNotificationConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketNotificationConfiguration");
        request.addParameter("notification", null);
        if (skipDestinationValidation != null) {
            request.addHeader("x-amz-skip-destination-validation", Boolean.toString(skipDestinationValidation));
        }
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(bucketNotificationConfiguration);
        request.setContent(new ByteArrayInputStream(bytes));
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public BucketNotificationConfiguration getBucketNotificationConfiguration(String bucketName) throws SdkClientException, AmazonServiceException {
        return this.getBucketNotificationConfiguration(new GetBucketNotificationConfigurationRequest(bucketName));
    }

    @Override
    public BucketNotificationConfiguration getBucketNotificationConfiguration(GetBucketNotificationConfigurationRequest getBucketNotificationConfigurationRequest) throws SdkClientException, AmazonServiceException {
        getBucketNotificationConfigurationRequest = this.beforeClientExecution(getBucketNotificationConfigurationRequest);
        this.rejectNull(getBucketNotificationConfigurationRequest, "The bucket request parameter must be specified when querying notification configuration");
        String bucketName = getBucketNotificationConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket request must specify a bucket name when querying notification configuration");
        Request<GetBucketNotificationConfigurationRequest> request = this.createRequest(bucketName, null, getBucketNotificationConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketNotificationConfiguration");
        request.addParameter("notification", null);
        return this.invoke(request, BucketNotificationConfigurationStaxUnmarshaller.getInstance(), bucketName, null);
    }

    @Override
    public BucketLoggingConfiguration getBucketLoggingConfiguration(String bucketName) throws SdkClientException, AmazonServiceException {
        return this.getBucketLoggingConfiguration(new GetBucketLoggingConfigurationRequest(bucketName));
    }

    @Override
    public BucketLoggingConfiguration getBucketLoggingConfiguration(GetBucketLoggingConfigurationRequest getBucketLoggingConfigurationRequest) throws SdkClientException, AmazonServiceException {
        getBucketLoggingConfigurationRequest = this.beforeClientExecution(getBucketLoggingConfigurationRequest);
        this.rejectNull(getBucketLoggingConfigurationRequest, "The request object parameter getBucketLoggingConfigurationRequest must be specifed.");
        String bucketName = getBucketLoggingConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when requesting a bucket's logging status");
        Request<GetBucketLoggingConfigurationRequest> request = this.createRequest(bucketName, null, getBucketLoggingConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketLogging");
        request.addParameter("logging", null);
        return this.invoke(request, new Unmarshallers.BucketLoggingConfigurationnmarshaller(), bucketName, null);
    }

    @Override
    public void setBucketLoggingConfiguration(SetBucketLoggingConfigurationRequest setBucketLoggingConfigurationRequest) throws SdkClientException, AmazonServiceException {
        setBucketLoggingConfigurationRequest = this.beforeClientExecution(setBucketLoggingConfigurationRequest);
        this.rejectNull(setBucketLoggingConfigurationRequest, "The set bucket logging configuration request object must be specified when enabling server access logging");
        String bucketName = setBucketLoggingConfigurationRequest.getBucketName();
        BucketLoggingConfiguration loggingConfiguration = setBucketLoggingConfigurationRequest.getLoggingConfiguration();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when enabling server access logging");
        this.rejectNull(loggingConfiguration, "The logging configuration parameter must be specified when enabling server access logging");
        Request<SetBucketLoggingConfigurationRequest> request = this.createRequest(bucketName, null, setBucketLoggingConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketLogging");
        request.addParameter("logging", null);
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(loggingConfiguration);
        request.setContent(new ByteArrayInputStream(bytes));
        this.populateRequestHeaderWithMd5(request, bytes);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public BucketAccelerateConfiguration getBucketAccelerateConfiguration(String bucketName) throws AmazonServiceException, SdkClientException {
        return this.getBucketAccelerateConfiguration(new GetBucketAccelerateConfigurationRequest(bucketName));
    }

    @Override
    public BucketAccelerateConfiguration getBucketAccelerateConfiguration(GetBucketAccelerateConfigurationRequest getBucketAccelerateConfigurationRequest) throws AmazonServiceException, SdkClientException {
        getBucketAccelerateConfigurationRequest = this.beforeClientExecution(getBucketAccelerateConfigurationRequest);
        this.rejectNull(getBucketAccelerateConfigurationRequest, "getBucketAccelerateConfigurationRequest must be specified.");
        String bucketName = getBucketAccelerateConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when querying accelerate configuration");
        Request<GetBucketAccelerateConfigurationRequest> request = this.createRequest(bucketName, null, getBucketAccelerateConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketAccelerateConfiguration");
        request.addParameter("accelerate", null);
        return this.invoke(request, new Unmarshallers.BucketAccelerateConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public void setBucketAccelerateConfiguration(String bucketName, BucketAccelerateConfiguration accelerateConfiguration) throws AmazonServiceException, SdkClientException {
        this.setBucketAccelerateConfiguration(new SetBucketAccelerateConfigurationRequest(bucketName, accelerateConfiguration));
    }

    @Override
    public void setBucketAccelerateConfiguration(SetBucketAccelerateConfigurationRequest setBucketAccelerateConfigurationRequest) throws AmazonServiceException, SdkClientException {
        setBucketAccelerateConfigurationRequest = this.beforeClientExecution(setBucketAccelerateConfigurationRequest);
        this.rejectNull(setBucketAccelerateConfigurationRequest, "setBucketAccelerateConfigurationRequest must be specified");
        String bucketName = setBucketAccelerateConfigurationRequest.getBucketName();
        BucketAccelerateConfiguration accelerateConfiguration = setBucketAccelerateConfigurationRequest.getAccelerateConfiguration();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting accelerate configuration.");
        this.rejectNull(accelerateConfiguration, "The bucket accelerate configuration parameter must be specified.");
        this.rejectNull(accelerateConfiguration.getStatus(), "The status parameter must be specified when updating bucket accelerate configuration.");
        Request<SetBucketAccelerateConfigurationRequest> request = this.createRequest(bucketName, null, setBucketAccelerateConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketAccelerateConfiguration");
        request.addParameter("accelerate", null);
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(accelerateConfiguration);
        request.setContent(new ByteArrayInputStream(bytes));
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public BucketPolicy getBucketPolicy(String bucketName) throws SdkClientException, AmazonServiceException {
        return this.getBucketPolicy(new GetBucketPolicyRequest(bucketName));
    }

    @Override
    public void deleteBucketPolicy(String bucketName) throws SdkClientException, AmazonServiceException {
        this.deleteBucketPolicy(new DeleteBucketPolicyRequest(bucketName));
    }

    @Override
    public BucketPolicy getBucketPolicy(GetBucketPolicyRequest getBucketPolicyRequest) throws SdkClientException, AmazonServiceException {
        getBucketPolicyRequest = this.beforeClientExecution(getBucketPolicyRequest);
        this.rejectNull(getBucketPolicyRequest, "The request object must be specified when getting a bucket policy");
        String bucketName = getBucketPolicyRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name must be specified when getting a bucket policy");
        Request<GetBucketPolicyRequest> request = this.createRequest(bucketName, null, getBucketPolicyRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketPolicy");
        request.addParameter("policy", null);
        BucketPolicy result = new BucketPolicy();
        try {
            String policyText = this.invoke(request, new S3StringResponseHandler(), bucketName, null);
            result.setPolicyText(policyText);
            return result;
        }
        catch (AmazonServiceException ase) {
            if (ase.getErrorCode().equals("NoSuchBucketPolicy")) {
                return result;
            }
            throw ase;
        }
    }

    @Override
    public void setBucketPolicy(String bucketName, String policyText) throws SdkClientException, AmazonServiceException {
        this.setBucketPolicy(new SetBucketPolicyRequest(bucketName, policyText));
    }

    @Override
    public void setBucketPolicy(SetBucketPolicyRequest setBucketPolicyRequest) throws SdkClientException, AmazonServiceException {
        setBucketPolicyRequest = this.beforeClientExecution(setBucketPolicyRequest);
        this.rejectNull(setBucketPolicyRequest, "The request object must be specified when setting a bucket policy");
        String bucketName = setBucketPolicyRequest.getBucketName();
        String policyText = setBucketPolicyRequest.getPolicyText();
        this.rejectNull(bucketName, "The bucket name must be specified when setting a bucket policy");
        this.rejectNull(policyText, "The policy text must be specified when setting a bucket policy");
        Request<SetBucketPolicyRequest> request = this.createRequest(bucketName, null, setBucketPolicyRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketPolicy");
        request.addParameter("policy", null);
        byte[] content = ServiceUtils.toByteArray(policyText);
        request.setContent(new ByteArrayInputStream(content));
        this.populateRequestHeaderWithMd5(request, content);
        if (setBucketPolicyRequest.getConfirmRemoveSelfBucketAccess() != null && setBucketPolicyRequest.getConfirmRemoveSelfBucketAccess().booleanValue()) {
            request.addHeader("x-amz-confirm-remove-self-bucket-access", "true");
        }
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public void deleteBucketPolicy(DeleteBucketPolicyRequest deleteBucketPolicyRequest) throws SdkClientException, AmazonServiceException {
        deleteBucketPolicyRequest = this.beforeClientExecution(deleteBucketPolicyRequest);
        this.rejectNull(deleteBucketPolicyRequest, "The request object must be specified when deleting a bucket policy");
        String bucketName = deleteBucketPolicyRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name must be specified when deleting a bucket policy");
        Request<DeleteBucketPolicyRequest> request = this.createRequest(bucketName, null, deleteBucketPolicyRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketPolicy");
        request.addParameter("policy", null);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public DeleteBucketEncryptionResult deleteBucketEncryption(String bucketName) throws SdkClientException {
        return this.deleteBucketEncryption(new DeleteBucketEncryptionRequest().withBucketName(bucketName));
    }

    @Override
    public DeleteBucketEncryptionResult deleteBucketEncryption(DeleteBucketEncryptionRequest deleteBucketEncryptionRequest) throws SdkClientException {
        deleteBucketEncryptionRequest = this.beforeClientExecution(deleteBucketEncryptionRequest);
        this.rejectNull(deleteBucketEncryptionRequest, "The request object must be specified when deleting a bucket encryption configuration");
        String bucketName = deleteBucketEncryptionRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name must be specified when deleting a bucket encryption configuration");
        Request<DeleteBucketEncryptionRequest> request = this.createRequest(bucketName, null, deleteBucketEncryptionRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketEncryption");
        request.addParameter("encryption", null);
        return this.invoke(request, new Unmarshallers.DeleteBucketEncryptionUnmarshaller(), bucketName, null);
    }

    @Override
    public GetBucketEncryptionResult getBucketEncryption(String bucketName) throws SdkClientException {
        return this.getBucketEncryption(new GetBucketEncryptionRequest().withBucketName(bucketName));
    }

    @Override
    public GetBucketEncryptionResult getBucketEncryption(GetBucketEncryptionRequest getBucketEncryptionRequest) throws SdkClientException {
        getBucketEncryptionRequest = this.beforeClientExecution(getBucketEncryptionRequest);
        this.rejectNull(getBucketEncryptionRequest, "The bucket request parameter must be specified when querying encryption configuration");
        String bucketName = getBucketEncryptionRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket request must specify a bucket name when querying encryption configuration");
        Request<GetBucketEncryptionRequest> request = this.createRequest(bucketName, null, getBucketEncryptionRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketEncryption");
        request.addParameter("encryption", null);
        return this.invoke(request, GetBucketEncryptionStaxUnmarshaller.getInstance(), bucketName, null);
    }

    @Override
    public SetBucketEncryptionResult setBucketEncryption(SetBucketEncryptionRequest setBucketEncryptionRequest) throws AmazonServiceException, SdkClientException {
        setBucketEncryptionRequest = this.beforeClientExecution(setBucketEncryptionRequest);
        this.rejectNull(setBucketEncryptionRequest, "The request object must be specified.");
        String bucketName = setBucketEncryptionRequest.getBucketName();
        ServerSideEncryptionConfiguration sseConfig = setBucketEncryptionRequest.getServerSideEncryptionConfiguration();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting bucket encryption configuration.");
        this.rejectNull(sseConfig, "The SSE configuration parameter must be specified when setting bucket encryption configuration.");
        Request<SetBucketEncryptionRequest> request = this.createRequest(bucketName, null, setBucketEncryptionRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketEncryption");
        request.addParameter("encryption", null);
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(sseConfig);
        request.setContent(new ByteArrayInputStream(bytes));
        this.populateRequestHeaderWithMd5(request, bytes);
        return this.invoke(request, new Unmarshallers.SetBucketEncryptionUnmarshaller(), bucketName, null);
    }

    @Override
    public SetPublicAccessBlockResult setPublicAccessBlock(SetPublicAccessBlockRequest setPublicAccessBlockRequest) {
        setPublicAccessBlockRequest = this.beforeClientExecution(setPublicAccessBlockRequest);
        this.rejectNull(setPublicAccessBlockRequest, "The request object must be specified.");
        String bucketName = setPublicAccessBlockRequest.getBucketName();
        PublicAccessBlockConfiguration config = setPublicAccessBlockRequest.getPublicAccessBlockConfiguration();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting public block configuration.");
        this.rejectNull(config, "The PublicAccessBlockConfiguration parameter must be specified when setting public block");
        Request<SetPublicAccessBlockRequest> request = this.createRequest(bucketName, null, setPublicAccessBlockRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutPublicAccessBlock");
        request.addParameter("publicAccessBlock", null);
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(config);
        request.setContent(new ByteArrayInputStream(bytes));
        this.populateRequestHeaderWithMd5(request, bytes);
        return this.invoke(request, new Unmarshallers.SetPublicAccessBlockUnmarshaller(), bucketName, null);
    }

    @Override
    public GetPublicAccessBlockResult getPublicAccessBlock(GetPublicAccessBlockRequest getPublicAccessBlockRequest) {
        getPublicAccessBlockRequest = this.beforeClientExecution(getPublicAccessBlockRequest);
        this.rejectNull(getPublicAccessBlockRequest, "The request object must be specified.");
        String bucketName = getPublicAccessBlockRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when getting public block configuration.");
        Request<GetPublicAccessBlockRequest> request = this.createRequest(bucketName, null, getPublicAccessBlockRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetPublicAccessBlock");
        request.addParameter("publicAccessBlock", null);
        return this.invoke(request, GetPublicAccessBlockStaxUnmarshaller.getInstance(), bucketName, null);
    }

    @Override
    public DeletePublicAccessBlockResult deletePublicAccessBlock(DeletePublicAccessBlockRequest deletePublicAccessBlockRequest) {
        deletePublicAccessBlockRequest = this.beforeClientExecution(deletePublicAccessBlockRequest);
        this.rejectNull(deletePublicAccessBlockRequest, "The request object must be specified.");
        String bucketName = deletePublicAccessBlockRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when deleting public block configuration.");
        Request<DeletePublicAccessBlockRequest> request = this.createRequest(bucketName, null, deletePublicAccessBlockRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeletePublicAccessBlock");
        request.addParameter("publicAccessBlock", null);
        return this.invoke(request, new Unmarshallers.DeletePublicAccessBlockUnmarshaller(), bucketName, null);
    }

    @Override
    public GetBucketPolicyStatusResult getBucketPolicyStatus(GetBucketPolicyStatusRequest getBucketPolicyStatusRequest) {
        getBucketPolicyStatusRequest = this.beforeClientExecution(getBucketPolicyStatusRequest);
        this.rejectNull(getBucketPolicyStatusRequest, "The request object must be specified.");
        String bucketName = getBucketPolicyStatusRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when getting bucket policy status");
        Request<GetBucketPolicyStatusRequest> request = this.createRequest(bucketName, null, getBucketPolicyStatusRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketPolicyStatus");
        request.addParameter("policyStatus", null);
        return this.invoke(request, GetBucketPolicyStatusStaxUnmarshaller.getInstance(), bucketName, null);
    }

    @Override
    public SelectObjectContentResult selectObjectContent(SelectObjectContentRequest selectRequest) throws AmazonServiceException, SdkClientException {
        selectRequest = this.beforeClientExecution(selectRequest);
        this.rejectNull(selectRequest, "The request parameter must be specified");
        this.rejectNull(selectRequest.getBucketName(), "The bucket name parameter must be specified when selecting object content.");
        this.rejectNull(selectRequest.getKey(), "The key parameter must be specified when selecting object content.");
        Request<SelectObjectContentRequest> request = this.createRequest(selectRequest.getBucketName(), selectRequest.getKey(), selectRequest, HttpMethodName.POST);
        request.addParameter("select", null);
        request.addParameter("select-type", "2");
        AmazonS3Client.populateSSE_C(request, selectRequest.getSSECustomerKey());
        this.setContent(request, RequestXmlFactory.convertToXmlByteArray(selectRequest), ContentType.APPLICATION_XML.toString(), true);
        S3Object result = this.invoke(request, new S3ObjectResponseHandler(), selectRequest.getBucketName(), selectRequest.getKey());
        ServiceClientHolderInputStream resultStream = new ServiceClientHolderInputStream(result.getObjectContent(), this);
        return new SelectObjectContentResult().withPayload(new SelectObjectContentEventStream(resultStream));
    }

    @Override
    public SetObjectLegalHoldResult setObjectLegalHold(SetObjectLegalHoldRequest setObjectLegalHoldRequest) {
        setObjectLegalHoldRequest = this.beforeClientExecution(setObjectLegalHoldRequest);
        this.rejectNull(setObjectLegalHoldRequest, "The request parameter must be specified");
        String bucketName = setObjectLegalHoldRequest.getBucketName();
        String key = setObjectLegalHoldRequest.getKey();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting the object legal hold.");
        this.rejectNull(key, "The key parameter must be specified when setting the object legal hold.");
        Request<SetObjectLegalHoldRequest> request = this.createRequest(bucketName, key, setObjectLegalHoldRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectLegalHold");
        this.setContent(request, new ObjectLockLegalHoldXmlFactory().convertToXmlByteArray(setObjectLegalHoldRequest.getLegalHold()), ContentType.APPLICATION_XML.toString(), true);
        request.addParameter("legal-hold", null);
        AmazonS3Client.addParameterIfNotNull(request, "versionId", setObjectLegalHoldRequest.getVersionId());
        AmazonS3Client.populateRequesterPaysHeader(request, setObjectLegalHoldRequest.isRequesterPays());
        ResponseHeaderHandlerChain<SetObjectLegalHoldResult> responseHandler = new ResponseHeaderHandlerChain<SetObjectLegalHoldResult>(new Unmarshallers.SetObjectLegalHoldResultUnmarshaller(), new S3RequesterChargedHeaderHandler());
        return (SetObjectLegalHoldResult)this.invoke(request, responseHandler, bucketName, key);
    }

    @Override
    public GetObjectLegalHoldResult getObjectLegalHold(GetObjectLegalHoldRequest getObjectLegalHoldRequest) {
        getObjectLegalHoldRequest = this.beforeClientExecution(getObjectLegalHoldRequest);
        this.rejectNull(getObjectLegalHoldRequest, "The request parameter must be specified");
        String bucketName = getObjectLegalHoldRequest.getBucketName();
        String key = getObjectLegalHoldRequest.getKey();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when getting the object legal hold.");
        this.rejectNull(key, "The key parameter must be specified when getting the object legal hold.");
        Request<GetObjectLegalHoldRequest> request = this.createRequest(bucketName, key, getObjectLegalHoldRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObjectLegalHold");
        request.addParameter("legal-hold", null);
        AmazonS3Client.addParameterIfNotNull(request, "versionId", getObjectLegalHoldRequest.getVersionId());
        AmazonS3Client.populateRequesterPaysHeader(request, getObjectLegalHoldRequest.isRequesterPays());
        return this.invoke(request, new Unmarshallers.GetObjectLegalHoldResultUnmarshaller(), bucketName, key);
    }

    @Override
    public SetObjectLockConfigurationResult setObjectLockConfiguration(SetObjectLockConfigurationRequest setObjectLockConfigurationRequest) {
        setObjectLockConfigurationRequest = this.beforeClientExecution(setObjectLockConfigurationRequest);
        this.rejectNull(setObjectLockConfigurationRequest, "The request parameter must be specified");
        String bucketName = setObjectLockConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting the object lock configuration");
        Request<SetObjectLockConfigurationRequest> request = this.createRequest(bucketName, null, setObjectLockConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectLockConfiguration");
        request.addParameter("object-lock", null);
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-bucket-object-lock-token", setObjectLockConfigurationRequest.getToken());
        AmazonS3Client.populateRequesterPaysHeader(request, setObjectLockConfigurationRequest.isRequesterPays());
        this.setContent(request, new ObjectLockConfigurationXmlFactory().convertToXmlByteArray(setObjectLockConfigurationRequest.getObjectLockConfiguration()), ContentType.APPLICATION_XML.toString(), true);
        ResponseHeaderHandlerChain<SetObjectLockConfigurationResult> responseHandler = new ResponseHeaderHandlerChain<SetObjectLockConfigurationResult>(new Unmarshallers.SetObjectLockConfigurationResultUnmarshaller(), new S3RequesterChargedHeaderHandler());
        return (SetObjectLockConfigurationResult)this.invoke(request, responseHandler, bucketName, null);
    }

    @Override
    public GetObjectLockConfigurationResult getObjectLockConfiguration(GetObjectLockConfigurationRequest getObjectLockConfigurationRequest) {
        getObjectLockConfigurationRequest = this.beforeClientExecution(getObjectLockConfigurationRequest);
        this.rejectNull(getObjectLockConfigurationRequest, "The request parameter must be specified");
        String bucketName = getObjectLockConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when getting the object lock configuration");
        Request<GetObjectLockConfigurationRequest> request = this.createRequest(bucketName, null, getObjectLockConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObjectLockConfiguration");
        request.addParameter("object-lock", null);
        return this.invoke(request, new Unmarshallers.GetObjectLockConfigurationResultUnmarshaller(), bucketName, null);
    }

    @Override
    public SetObjectRetentionResult setObjectRetention(SetObjectRetentionRequest setObjectRetentionRequest) {
        setObjectRetentionRequest = this.beforeClientExecution(setObjectRetentionRequest);
        this.rejectNull(setObjectRetentionRequest, "The request parameter must be specified");
        String bucketName = setObjectRetentionRequest.getBucketName();
        String key = setObjectRetentionRequest.getKey();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting the object retention.");
        this.rejectNull(key, "The key parameter must be specified when setting the object retention.");
        Request<SetObjectRetentionRequest> request = this.createRequest(bucketName, key, setObjectRetentionRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectRetention");
        request.addParameter("retention", null);
        if (setObjectRetentionRequest.getBypassGovernanceRetention()) {
            request.addHeader("x-amz-bypass-governance-retention", "true");
        }
        AmazonS3Client.addParameterIfNotNull(request, "versionId", setObjectRetentionRequest.getVersionId());
        AmazonS3Client.populateRequesterPaysHeader(request, setObjectRetentionRequest.isRequesterPays());
        this.setContent(request, new ObjectLockRetentionXmlFactory().convertToXmlByteArray(setObjectRetentionRequest.getRetention()), ContentType.APPLICATION_XML.toString(), true);
        ResponseHeaderHandlerChain<SetObjectRetentionResult> responseHandler = new ResponseHeaderHandlerChain<SetObjectRetentionResult>(new Unmarshallers.SetObjectRetentionResultUnmarshaller(), new S3RequesterChargedHeaderHandler());
        return (SetObjectRetentionResult)this.invoke(request, responseHandler, bucketName, key);
    }

    @Override
    public GetObjectRetentionResult getObjectRetention(GetObjectRetentionRequest getObjectRetentionRequest) {
        getObjectRetentionRequest = this.beforeClientExecution(getObjectRetentionRequest);
        this.rejectNull(getObjectRetentionRequest, "The request parameter must be specified");
        String bucketName = getObjectRetentionRequest.getBucketName();
        String key = getObjectRetentionRequest.getKey();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when getting the object retention.");
        this.rejectNull(key, "The key parameter must be specified when getting the object retention.");
        Request<GetObjectRetentionRequest> request = this.createRequest(bucketName, key, getObjectRetentionRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObjectRetention");
        request.addParameter("retention", null);
        AmazonS3Client.addParameterIfNotNull(request, "versionId", getObjectRetentionRequest.getVersionId());
        AmazonS3Client.populateRequesterPaysHeader(request, getObjectRetentionRequest.isRequesterPays());
        return this.invoke(request, new Unmarshallers.GetObjectRetentionResultUnmarshaller(), bucketName, key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public WriteGetObjectResponseResult writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest) {
        WriteGetObjectResponseResult writeGetObjectResponseResult;
        ObjectMetadata metadata;
        writeGetObjectResponseRequest = this.beforeClientExecution(writeGetObjectResponseRequest);
        this.rejectNull(writeGetObjectResponseRequest, "The request parameter must be specified");
        Request<WriteGetObjectResponseRequest> request = this.createRequest(null, null, writeGetObjectResponseRequest, HttpMethodName.POST);
        request.setResourcePath("/WriteGetObjectResponse");
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "WriteGetObjectResponse");
        request.addHandlerContext(HandlerContextKey.REQUIRES_LENGTH, false);
        request.addHandlerContext(HandlerContextKey.HAS_STREAMING_INPUT, true);
        request.addHandlerContext(S3HandlerContextKeys.IS_CHUNKED_ENCODING_DISABLED, true);
        request.addHandlerContext(S3HandlerContextKeys.IS_PAYLOAD_SIGNING_ENABLED, false);
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-request-route", writeGetObjectResponseRequest.getRequestRoute());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-request-token", writeGetObjectResponseRequest.getRequestToken());
        AmazonS3Client.addIntegerHeaderIfNotNull(request, "x-amz-fwd-status", writeGetObjectResponseRequest.getStatusCode());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-error-code", writeGetObjectResponseRequest.getErrorCode());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-error-message", writeGetObjectResponseRequest.getErrorMessage());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-accept-ranges", writeGetObjectResponseRequest.getAcceptRanges());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-Cache-Control", writeGetObjectResponseRequest.getCacheControl());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-Content-Disposition", writeGetObjectResponseRequest.getContentDisposition());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-Content-Encoding", writeGetObjectResponseRequest.getContentEncoding());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-Content-Language", writeGetObjectResponseRequest.getContentLanguage());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-Content-Range", writeGetObjectResponseRequest.getContentRange());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-Content-Type", writeGetObjectResponseRequest.getContentType());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-delete-marker", writeGetObjectResponseRequest.getDeleteMarker());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-ETag", writeGetObjectResponseRequest.getETag());
        AmazonS3Client.addDateHeader(request, "x-amz-fwd-header-Expires", writeGetObjectResponseRequest.getExpires());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-expiration", writeGetObjectResponseRequest.getExpiration());
        AmazonS3Client.addDateHeader(request, "x-amz-fwd-header-Last-Modified", writeGetObjectResponseRequest.getLastModified());
        AmazonS3Client.addIntegerHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-missing-meta", writeGetObjectResponseRequest.getMissingMeta());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-object-lock-mode", writeGetObjectResponseRequest.getObjectLockMode());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-object-lock-legal-hold", writeGetObjectResponseRequest.getObjectLockLegalHoldStatus());
        AmazonS3Client.addDateHeader(request, "x-amz-fwd-header-x-amz-object-lock-retain-until-date", writeGetObjectResponseRequest.getObjectLockRetainUntilDate());
        AmazonS3Client.addIntegerHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-mp-parts-count", writeGetObjectResponseRequest.getPartsCount());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-replication-status", writeGetObjectResponseRequest.getReplicationStatus());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-request-charged", writeGetObjectResponseRequest.getRequestCharged());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-restore", writeGetObjectResponseRequest.getRestore());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-server-side-encryption", writeGetObjectResponseRequest.getServerSideEncryption());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-server-side-encryption-customer-algorithm", writeGetObjectResponseRequest.getSSECustomerAlgorithm());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-server-side-encryption-aws-kms-key-id", writeGetObjectResponseRequest.getSSEKMSKeyId());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-server-side-encryption-customer-key-MD5", writeGetObjectResponseRequest.getSSECustomerKeyMD5());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-storage-class", writeGetObjectResponseRequest.getStorageClass());
        AmazonS3Client.addIntegerHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-tagging-count", writeGetObjectResponseRequest.getTagCount());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-fwd-header-x-amz-version-id", writeGetObjectResponseRequest.getVersionId());
        if (writeGetObjectResponseRequest.getBucketKeyEnabled() != null) {
            request.addHeader("x-amz-fwd-header-x-amz-server-side-encryption-bucket-key-enabled", writeGetObjectResponseRequest.getBucketKeyEnabled().toString());
        }
        if ((metadata = writeGetObjectResponseRequest.getMetadata()) == null) {
            metadata = new ObjectMetadata();
        }
        if (writeGetObjectResponseRequest.getContentLength() != null) {
            metadata.setContentLength(writeGetObjectResponseRequest.getContentLength());
        }
        InputStream originalIs = writeGetObjectResponseRequest.getInputStream();
        File originalFile = writeGetObjectResponseRequest.getFile();
        InputStream requestInputStream = null;
        try {
            requestInputStream = this.getInputStream(writeGetObjectResponseRequest.getInputStream(), writeGetObjectResponseRequest.getFile(), metadata, request, false, true);
            request.setContent(requestInputStream);
            AmazonS3Client.populateRequestMetadata(request, metadata);
            writeGetObjectResponseResult = this.invoke(request, new Unmarshallers.WriteGetObjectResponseResultUnmarshaller(), null, null);
        }
        catch (Throwable throwable) {
            S3DataSource.Utils.cleanupDataSource(writeGetObjectResponseRequest, originalFile, originalIs, requestInputStream, log);
            throw throwable;
        }
        S3DataSource.Utils.cleanupDataSource(writeGetObjectResponseRequest, originalFile, originalIs, requestInputStream, log);
        return writeGetObjectResponseResult;
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key, Date expiration) throws SdkClientException {
        return this.generatePresignedUrl(bucketName, key, expiration, HttpMethod.GET);
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key, Date expiration, HttpMethod method) throws SdkClientException {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, method);
        request.setExpiration(expiration);
        return this.generatePresignedUrl(request);
    }

    @Override
    public URL generatePresignedUrl(GeneratePresignedUrlRequest req) {
        this.rejectNull(req, "The request parameter must be specified when generating a pre-signed URL");
        req.rejectIllegalArguments();
        String bucketName = req.getBucketName();
        String key = req.getKey();
        if (req.getExpiration() == null) {
            req.setExpiration(new Date(System.currentTimeMillis() + 900000L));
        }
        HttpMethodName httpMethod = HttpMethodName.valueOf(req.getMethod().toString());
        Request<GeneratePresignedUrlRequest> request = this.createRequest(bucketName, key, req, httpMethod);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GeneratePresignedUrl");
        AmazonS3Client.addParameterIfNotNull(request, "versionId", req.getVersionId());
        if (req.isZeroByteContent()) {
            request.setContent(new ByteArrayInputStream(new byte[0]));
        }
        for (Map.Entry<String, String> entry : req.getRequestParameters().entrySet()) {
            request.addParameter(entry.getKey(), entry.getValue());
        }
        AmazonS3Client.addHeaderIfNotNull(request, "Content-Type", req.getContentType());
        AmazonS3Client.addHeaderIfNotNull(request, "Content-MD5", req.getContentMd5());
        AmazonS3Client.populateSSE_C(request, req.getSSECustomerKey());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-server-side-encryption", req.getSSEAlgorithm());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-server-side-encryption-aws-kms-key-id", req.getKmsCmkId());
        Map<String, String> customHeaders = req.getCustomRequestHeaders();
        if (customHeaders != null) {
            for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        AmazonS3Client.addResponseHeaderParameters(request, req.getResponseHeaders());
        Signer signer = this.createSigner(request, bucketName, key);
        if (request.getHandlerContext(HandlerContextKey.SIGNING_NAME) != null && !this.isSignerOverridden()) {
            String string = request.getHandlerContext(HandlerContextKey.SIGNING_NAME);
            if (signer instanceof ServiceAwareSigner) {
                ((ServiceAwareSigner)signer).setServiceName(string);
            }
        }
        if (signer instanceof Presigner) {
            ((Presigner)((Object)signer)).presignRequest(request, CredentialUtils.getCredentialsProvider(request.getOriginalRequest(), this.awsCredentialsProvider).getCredentials(), req.getExpiration());
        } else {
            this.presignRequest(request, req.getMethod(), bucketName, key, req.getExpiration(), null);
        }
        return ServiceUtils.convertRequestToUrl(request, true, false);
    }

    @Override
    public void abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest) throws SdkClientException, AmazonServiceException {
        abortMultipartUploadRequest = this.beforeClientExecution(abortMultipartUploadRequest);
        this.rejectNull(abortMultipartUploadRequest, "The request parameter must be specified when aborting a multipart upload");
        this.rejectNull(abortMultipartUploadRequest.getBucketName(), "The bucket name parameter must be specified when aborting a multipart upload");
        this.rejectNull(abortMultipartUploadRequest.getKey(), "The key parameter must be specified when aborting a multipart upload");
        this.rejectNull(abortMultipartUploadRequest.getUploadId(), "The upload ID parameter must be specified when aborting a multipart upload");
        String bucketName = abortMultipartUploadRequest.getBucketName();
        String key = abortMultipartUploadRequest.getKey();
        Request<AbortMultipartUploadRequest> request = this.createRequest(bucketName, key, abortMultipartUploadRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "AbortMultipartUpload");
        request.addParameter("uploadId", abortMultipartUploadRequest.getUploadId());
        AmazonS3Client.populateRequesterPaysHeader(request, abortMultipartUploadRequest.isRequesterPays());
        this.invoke(request, this.voidResponseHandler, bucketName, key);
    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest completeMultipartUploadRequest) throws SdkClientException, AmazonServiceException {
        XmlResponsesSaxParser.CompleteMultipartUploadHandler handler;
        completeMultipartUploadRequest = this.beforeClientExecution(completeMultipartUploadRequest);
        this.rejectNull(completeMultipartUploadRequest, "The request parameter must be specified when completing a multipart upload");
        String bucketName = completeMultipartUploadRequest.getBucketName();
        String key = completeMultipartUploadRequest.getKey();
        String uploadId = completeMultipartUploadRequest.getUploadId();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when completing a multipart upload");
        this.rejectNull(key, "The key parameter must be specified when completing a multipart upload");
        this.rejectNull(uploadId, "The upload ID parameter must be specified when completing a multipart upload");
        this.rejectNull(completeMultipartUploadRequest.getPartETags(), "The part ETags parameter must be specified when completing a multipart upload");
        int retries = 0;
        do {
            Request<CompleteMultipartUploadRequest> request = this.createRequest(bucketName, key, completeMultipartUploadRequest, HttpMethodName.POST);
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CompleteMultipartUpload");
            request.addParameter("uploadId", uploadId);
            AmazonS3Client.populateRequesterPaysHeader(request, completeMultipartUploadRequest.isRequesterPays());
            byte[] xml = RequestXmlFactory.convertToXmlByteArray(completeMultipartUploadRequest.getPartETags());
            request.addHeader("Content-Type", "application/xml");
            request.addHeader("Content-Length", String.valueOf(xml.length));
            request.setContent(new ByteArrayInputStream(xml));
            ResponseHeaderHandlerChain<XmlResponsesSaxParser.CompleteMultipartUploadHandler> responseHandler = new ResponseHeaderHandlerChain<XmlResponsesSaxParser.CompleteMultipartUploadHandler>(new Unmarshallers.CompleteMultipartUploadResultUnmarshaller(), new ServerSideEncryptionHeaderHandler(), new ObjectExpirationHeaderHandler(), new S3VersionHeaderHandler(), new S3RequesterChargedHeaderHandler());
            handler = (XmlResponsesSaxParser.CompleteMultipartUploadHandler)this.invoke(request, responseHandler, bucketName, key);
            if (handler.getCompleteMultipartUploadResult() == null) continue;
            return handler.getCompleteMultipartUploadResult();
        } while (this.shouldRetryCompleteMultipartUpload(completeMultipartUploadRequest, handler.getAmazonS3Exception(), retries++));
        throw handler.getAmazonS3Exception();
    }

    private boolean shouldRetryCompleteMultipartUpload(AmazonWebServiceRequest originalRequest, AmazonS3Exception exception, int retriesAttempted) {
        RetryPolicy retryPolicy = this.clientConfiguration.getRetryPolicy();
        if (retryPolicy == null || retryPolicy.getRetryCondition() == null) {
            return false;
        }
        if (retryPolicy == PredefinedRetryPolicies.NO_RETRY_POLICY) {
            return false;
        }
        return this.completeMultipartUploadRetryCondition.shouldRetry(originalRequest, exception, retriesAttempted);
    }

    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest initiateMultipartUploadRequest) throws SdkClientException, AmazonServiceException {
        Boolean bucketKeyEnabled;
        initiateMultipartUploadRequest = this.beforeClientExecution(initiateMultipartUploadRequest);
        this.rejectNull(initiateMultipartUploadRequest, "The request parameter must be specified when initiating a multipart upload");
        this.rejectNull(initiateMultipartUploadRequest.getBucketName(), "The bucket name parameter must be specified when initiating a multipart upload");
        this.rejectNull(initiateMultipartUploadRequest.getKey(), "The key parameter must be specified when initiating a multipart upload");
        Request<InitiateMultipartUploadRequest> request = this.createRequest(initiateMultipartUploadRequest.getBucketName(), initiateMultipartUploadRequest.getKey(), initiateMultipartUploadRequest, HttpMethodName.POST);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CreateMultipartUpload");
        request.addParameter("uploads", null);
        if (initiateMultipartUploadRequest.getStorageClass() != null) {
            request.addHeader("x-amz-storage-class", initiateMultipartUploadRequest.getStorageClass().toString());
        }
        if (initiateMultipartUploadRequest.getRedirectLocation() != null) {
            request.addHeader("x-amz-website-redirect-location", initiateMultipartUploadRequest.getRedirectLocation());
        }
        if (initiateMultipartUploadRequest.getAccessControlList() != null) {
            AmazonS3Client.addAclHeaders(request, initiateMultipartUploadRequest.getAccessControlList());
        } else if (initiateMultipartUploadRequest.getCannedACL() != null) {
            request.addHeader("x-amz-acl", initiateMultipartUploadRequest.getCannedACL().toString());
        }
        if (initiateMultipartUploadRequest.objectMetadata != null) {
            AmazonS3Client.populateRequestMetadata(request, initiateMultipartUploadRequest.objectMetadata);
        }
        if ((bucketKeyEnabled = initiateMultipartUploadRequest.getBucketKeyEnabled()) != null) {
            AmazonS3Client.addHeaderIfNotNull(request, "x-amz-server-side-encryption-bucket-key-enabled", String.valueOf(bucketKeyEnabled));
        }
        AmazonS3Client.populateRequesterPaysHeader(request, initiateMultipartUploadRequest.isRequesterPays());
        AmazonS3Client.populateSSE_C(request, initiateMultipartUploadRequest.getSSECustomerKey());
        AmazonS3Client.populateSSE_KMS(request, initiateMultipartUploadRequest.getSSEAwsKeyManagementParams());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-tagging", this.urlEncodeTags(initiateMultipartUploadRequest.getTagging()));
        AmazonS3Client.populateObjectLockHeaders(request, initiateMultipartUploadRequest.getObjectLockMode(), initiateMultipartUploadRequest.getObjectLockRetainUntilDate(), initiateMultipartUploadRequest.getObjectLockLegalHoldStatus());
        this.setZeroContentLength(request);
        request.setContent(new ByteArrayInputStream(new byte[0]));
        ResponseHeaderHandlerChain<InitiateMultipartUploadResult> responseHandler = new ResponseHeaderHandlerChain<InitiateMultipartUploadResult>(new Unmarshallers.InitiateMultipartUploadResultUnmarshaller(), new ServerSideEncryptionHeaderHandler(), new S3RequesterChargedHeaderHandler(), new InitiateMultipartUploadHeaderHandler());
        return (InitiateMultipartUploadResult)this.invoke(request, responseHandler, initiateMultipartUploadRequest.getBucketName(), initiateMultipartUploadRequest.getKey());
    }

    @Override
    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest) throws SdkClientException, AmazonServiceException {
        listMultipartUploadsRequest = this.beforeClientExecution(listMultipartUploadsRequest);
        this.rejectNull(listMultipartUploadsRequest, "The request parameter must be specified when listing multipart uploads");
        this.rejectNull(listMultipartUploadsRequest.getBucketName(), "The bucket name parameter must be specified when listing multipart uploads");
        Request<ListMultipartUploadsRequest> request = this.createRequest(listMultipartUploadsRequest.getBucketName(), null, listMultipartUploadsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListMultipartUploads");
        request.addParameter("uploads", null);
        if (listMultipartUploadsRequest.getKeyMarker() != null) {
            request.addParameter("key-marker", listMultipartUploadsRequest.getKeyMarker());
        }
        if (listMultipartUploadsRequest.getMaxUploads() != null) {
            request.addParameter("max-uploads", listMultipartUploadsRequest.getMaxUploads().toString());
        }
        if (listMultipartUploadsRequest.getUploadIdMarker() != null) {
            request.addParameter("upload-id-marker", listMultipartUploadsRequest.getUploadIdMarker());
        }
        if (listMultipartUploadsRequest.getDelimiter() != null) {
            request.addParameter("delimiter", listMultipartUploadsRequest.getDelimiter());
        }
        if (listMultipartUploadsRequest.getPrefix() != null) {
            request.addParameter("prefix", listMultipartUploadsRequest.getPrefix());
        }
        if (listMultipartUploadsRequest.getEncodingType() != null) {
            request.addParameter("encoding-type", listMultipartUploadsRequest.getEncodingType());
        }
        return this.invoke(request, new Unmarshallers.ListMultipartUploadsResultUnmarshaller(), listMultipartUploadsRequest.getBucketName(), null);
    }

    @Override
    public PartListing listParts(ListPartsRequest listPartsRequest) throws SdkClientException, AmazonServiceException {
        listPartsRequest = this.beforeClientExecution(listPartsRequest);
        this.rejectNull(listPartsRequest, "The request parameter must be specified when listing parts");
        this.rejectNull(listPartsRequest.getBucketName(), "The bucket name parameter must be specified when listing parts");
        this.rejectNull(listPartsRequest.getKey(), "The key parameter must be specified when listing parts");
        this.rejectNull(listPartsRequest.getUploadId(), "The upload ID parameter must be specified when listing parts");
        Request<ListPartsRequest> request = this.createRequest(listPartsRequest.getBucketName(), listPartsRequest.getKey(), listPartsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListParts");
        request.addParameter("uploadId", listPartsRequest.getUploadId());
        if (listPartsRequest.getMaxParts() != null) {
            request.addParameter("max-parts", listPartsRequest.getMaxParts().toString());
        }
        if (listPartsRequest.getPartNumberMarker() != null) {
            request.addParameter("part-number-marker", listPartsRequest.getPartNumberMarker().toString());
        }
        if (listPartsRequest.getEncodingType() != null) {
            request.addParameter("encoding-type", listPartsRequest.getEncodingType());
        }
        AmazonS3Client.populateRequesterPaysHeader(request, listPartsRequest.isRequesterPays());
        ResponseHeaderHandlerChain<PartListing> responseHandler = new ResponseHeaderHandlerChain<PartListing>(new Unmarshallers.ListPartsResultUnmarshaller(), new S3RequesterChargedHeaderHandler(), new ListPartsHeaderHandler());
        return (PartListing)this.invoke(request, responseHandler, listPartsRequest.getBucketName(), listPartsRequest.getKey());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UploadPartResult uploadPart(UploadPartRequest uploadPartRequest) throws SdkClientException, AmazonServiceException {
        uploadPartRequest = this.beforeClientExecution(uploadPartRequest);
        this.rejectNull(uploadPartRequest, "The request parameter must be specified when uploading a part");
        File fileOrig = uploadPartRequest.getFile();
        InputStream isOrig = uploadPartRequest.getInputStream();
        String bucketName = uploadPartRequest.getBucketName();
        String key = uploadPartRequest.getKey();
        String uploadId = uploadPartRequest.getUploadId();
        int partNumber = uploadPartRequest.getPartNumber();
        long partSize = uploadPartRequest.getPartSize();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when uploading a part");
        this.rejectNull(key, "The key parameter must be specified when uploading a part");
        this.rejectNull(uploadId, "The upload ID parameter must be specified when uploading a part");
        Request<UploadPartRequest> request = this.createRequest(bucketName, key, uploadPartRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "UploadPart");
        request.addHandlerContext(HandlerContextKey.REQUIRES_LENGTH, Boolean.TRUE);
        request.addHandlerContext(HandlerContextKey.HAS_STREAMING_INPUT, Boolean.TRUE);
        request.addParameter("uploadId", uploadId);
        request.addParameter("partNumber", Integer.toString(partNumber));
        ObjectMetadata objectMetadata = uploadPartRequest.getObjectMetadata();
        if (objectMetadata != null) {
            AmazonS3Client.populateRequestMetadata(request, objectMetadata);
        }
        AmazonS3Client.addHeaderIfNotNull(request, "Content-MD5", uploadPartRequest.getMd5Digest());
        request.addHeader("Content-Length", Long.toString(partSize));
        AmazonS3Client.populateRequesterPaysHeader(request, uploadPartRequest.isRequesterPays());
        AmazonS3Client.populateSSE_C(request, uploadPartRequest.getSSECustomerKey());
        InputStream isCurr = isOrig;
        try {
            if (fileOrig == null) {
                if (isOrig == null) {
                    throw new IllegalArgumentException("A File or InputStream must be specified when uploading part");
                }
                isCurr = ReleasableInputStream.wrap(isCurr);
                Integer bufsize = Constants.getS3StreamBufferSize();
                if (bufsize != null) {
                    AmazonWebServiceRequest awsreq = request.getOriginalRequest();
                    awsreq.getRequestClientOptions().setReadLimit(bufsize);
                }
            } else {
                try {
                    isCurr = new ResettableInputStream(fileOrig);
                }
                catch (IOException e) {
                    throw new IllegalArgumentException("Failed to open file " + fileOrig, e);
                }
            }
            isCurr = new InputSubstream(isCurr, uploadPartRequest.getFileOffset(), partSize, uploadPartRequest.isLastPart());
            MD5DigestCalculatingInputStream md5DigestStream = null;
            if (uploadPartRequest.getMd5Digest() == null && !this.skipMd5CheckStrategy.skipClientSideValidationPerRequest(uploadPartRequest)) {
                md5DigestStream = new MD5DigestCalculatingInputStream(isCurr);
                isCurr = md5DigestStream;
            }
            ProgressListener listener = uploadPartRequest.getGeneralProgressListener();
            SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_PART_STARTED_EVENT);
            UploadPartResult uploadPartResult = this.doUploadPart(bucketName, key, uploadId, partNumber, partSize, request, isCurr, md5DigestStream, listener);
            return uploadPartResult;
        }
        finally {
            S3DataSource.Utils.cleanupDataSource(uploadPartRequest, fileOrig, isOrig, isCurr, log);
        }
    }

    private UploadPartResult doUploadPart(String bucketName, String key, String uploadId, int partNumber, long partSize, Request<UploadPartRequest> request, InputStream inputStream, MD5DigestCalculatingInputStream md5DigestStream, ProgressListener listener) {
        try {
            byte[] serverSideHash;
            byte[] clientSideHash;
            request.setContent(inputStream);
            ObjectMetadata metadata = this.invoke(request, new S3MetadataResponseHandler(), bucketName, key);
            String etag = metadata.getETag();
            if (md5DigestStream != null && !this.skipMd5CheckStrategy.skipClientSideValidationPerUploadPartResponse(metadata) && !Arrays.equals(clientSideHash = md5DigestStream.getMd5Digest(), serverSideHash = BinaryUtils.fromHex(etag))) {
                String info = "bucketName: " + bucketName + ", key: " + key + ", uploadId: " + uploadId + ", partNumber: " + partNumber + ", partSize: " + partSize;
                throw new SdkClientException("Unable to verify integrity of data upload.  Client calculated content hash (contentMD5: " + Base16.encodeAsString(clientSideHash) + " in hex) didn't match hash (etag: " + etag + " in hex) calculated by Amazon S3.  You may need to delete the data stored in Amazon S3. (" + info + ")");
            }
            SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_PART_COMPLETED_EVENT);
            UploadPartResult result = new UploadPartResult();
            result.setETag(etag);
            result.setPartNumber(partNumber);
            result.setSSEAlgorithm(metadata.getSSEAlgorithm());
            result.setSSECustomerAlgorithm(metadata.getSSECustomerAlgorithm());
            result.setSSECustomerKeyMd5(metadata.getSSECustomerKeyMd5());
            result.setRequesterCharged(metadata.isRequesterCharged());
            result.setBucketKeyEnabled(metadata.getBucketKeyEnabled());
            return result;
        }
        catch (Throwable t) {
            SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_PART_FAILED_EVENT);
            SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_PART_COMPLETED_EVENT);
            throw Throwables.failure(t);
        }
    }

    @Override
    public S3ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request) {
        return (S3ResponseMetadata)this.client.getResponseMetadataForRequest(request);
    }

    @Override
    public void restoreObject(RestoreObjectRequest restoreObjectRequest) throws AmazonServiceException {
        this.restoreObjectV2(restoreObjectRequest);
    }

    @Override
    public RestoreObjectResult restoreObjectV2(RestoreObjectRequest restoreObjectRequest) throws AmazonServiceException {
        restoreObjectRequest = this.beforeClientExecution(restoreObjectRequest);
        String bucketName = restoreObjectRequest.getBucketName();
        String key = restoreObjectRequest.getKey();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when restoring a glacier object");
        this.rejectNull(key, "The key parameter must be specified when restoring a glacier object");
        if (restoreObjectRequest.getOutputLocation() != null) {
            this.rejectNull(restoreObjectRequest.getType(), "The restore request type must be specified with restores that specify OutputLocation");
            if (RestoreRequestType.SELECT.toString().equals(restoreObjectRequest.getType())) {
                this.rejectNull(restoreObjectRequest.getSelectParameters(), "The select parameters must be specified when restoring a glacier object with SELECT restore request type");
            }
        }
        Request<RestoreObjectRequest> request = this.createRestoreObjectRequest(restoreObjectRequest);
        ResponseHeaderHandlerChain<RestoreObjectResult> responseHandler = new ResponseHeaderHandlerChain<RestoreObjectResult>(new Unmarshallers.RestoreObjectResultUnmarshaller(), new S3RequesterChargedHeaderHandler(), new S3RestoreOutputPathHeaderHandler());
        return (RestoreObjectResult)this.invoke(request, responseHandler, bucketName, key);
    }

    @Override
    public void restoreObject(String bucketName, String key, int expirationInDays) throws AmazonServiceException {
        this.restoreObject(new RestoreObjectRequest(bucketName, key, expirationInDays));
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, String content) throws AmazonServiceException, SdkClientException {
        this.rejectNull(bucketName, "Bucket name must be provided");
        this.rejectNull(key, "Object key must be provided");
        this.rejectNull(content, "String content must be provided");
        byte[] contentBytes = content.getBytes(StringUtils.UTF8);
        ByteArrayInputStream is = new ByteArrayInputStream(contentBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/plain");
        metadata.setContentLength(contentBytes.length);
        return this.putObject(new PutObjectRequest(bucketName, key, is, metadata));
    }

    private void rejectNull(Object parameterValue, String errorMessage) {
        if (parameterValue == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private AccessControlList getAcl(String bucketName, String key, String versionId, boolean isRequesterPays, AmazonWebServiceRequest originalRequest) {
        if (originalRequest == null) {
            originalRequest = new GenericBucketRequest(bucketName);
        }
        Request<AmazonWebServiceRequest> request = this.createRequest(bucketName, key, originalRequest, HttpMethodName.GET);
        if (bucketName != null && key != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObjectAcl");
        } else if (bucketName != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketAcl");
        }
        request.addParameter("acl", null);
        if (versionId != null) {
            request.addParameter("versionId", versionId);
        }
        AmazonS3Client.populateRequesterPaysHeader(request, isRequesterPays);
        ResponseHeaderHandlerChain<AccessControlList> responseHandler = new ResponseHeaderHandlerChain<AccessControlList>(new Unmarshallers.AccessControlListUnmarshaller(), new S3RequesterChargedHeaderHandler());
        return (AccessControlList)this.invoke(request, responseHandler, bucketName, key);
    }

    private void setAcl(String bucketName, String key, String versionId, CannedAccessControlList cannedAcl, boolean isRequesterPays, AmazonWebServiceRequest originalRequest) {
        if (originalRequest == null) {
            originalRequest = new GenericBucketRequest(bucketName);
        }
        Request<AmazonWebServiceRequest> request = this.createRequest(bucketName, key, originalRequest, HttpMethodName.PUT);
        if (bucketName != null && key != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectAcl");
        } else if (bucketName != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketAcl");
        }
        request.addParameter("acl", null);
        request.addHeader("x-amz-acl", cannedAcl.toString());
        if (versionId != null) {
            request.addParameter("versionId", versionId);
        }
        AmazonS3Client.populateRequesterPaysHeader(request, isRequesterPays);
        this.invoke(request, this.voidResponseHandler, bucketName, key);
    }

    private void setAcl(String bucketName, String key, String versionId, AccessControlList acl, boolean isRequesterPays, AmazonWebServiceRequest originalRequest) {
        if (originalRequest == null) {
            originalRequest = new GenericBucketRequest(bucketName);
        }
        Request<AmazonWebServiceRequest> request = this.createRequest(bucketName, key, originalRequest, HttpMethodName.PUT);
        if (bucketName != null && key != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObjectAcl");
        } else if (bucketName != null) {
            request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketAcl");
        }
        request.addParameter("acl", null);
        if (versionId != null) {
            request.addParameter("versionId", versionId);
        }
        AmazonS3Client.populateRequesterPaysHeader(request, isRequesterPays);
        byte[] aclAsXml = new AclXmlFactory().convertToXmlByteArray(acl);
        request.addHeader("Content-Type", "application/xml");
        request.addHeader("Content-Length", String.valueOf(aclAsXml.length));
        request.setContent(new ByteArrayInputStream(aclAsXml));
        this.populateRequestHeaderWithMd5(request, aclAsXml);
        this.invoke(request, this.voidResponseHandler, bucketName, key);
    }

    protected Signer createSigner(Request<?> request, String bucketName, String key) {
        return this.createSigner(request, bucketName, key, false);
    }

    protected Signer createSigner(Request<?> request, String bucketName, String key, boolean isAdditionalHeadRequestToFindRegion) {
        Signer signer;
        URI uri;
        URI uRI = uri = this.clientOptions.isAccelerateModeEnabled() ? this.endpoint : request.getEndpoint();
        if (this.isAccessPointArn(bucketName)) {
            Arn resourceArn = Arn.fromString(bucketName);
            S3Resource s3Resource = S3ArnConverter.getInstance().convertArn(resourceArn);
            String region = s3Resource.getRegion();
            String regionalEndpoint = RegionUtils.getRegion(region).getServiceEndpoint(S3_SERVICE_NAME);
            signer = this.getSignerByURI(URI.create(uri.getScheme() + "://" + regionalEndpoint));
        } else {
            signer = this.getSignerByURI(uri);
        }
        if (!this.isSignerOverridden()) {
            String regionOverride;
            if (signer instanceof AWSS3V4Signer && this.bucketRegionShouldBeCached(request)) {
                String region = bucketRegionCache.get(bucketName);
                if (region != null) {
                    request.addHandlerContext(HandlerContextKey.SIGNING_REGION, region);
                    if (!this.clientOptions.isAccelerateModeEnabled()) {
                        this.resolveRequestEndpoint(request, bucketName, key, RuntimeHttpUtils.toUri(RegionUtils.getRegion(region).getServiceEndpoint(S3_SERVICE_NAME), this.clientConfiguration));
                    }
                    return this.updateSigV4SignerWithServiceAndRegion((AWSS3V4Signer)signer, request, region);
                }
                if (request.getOriginalRequest() instanceof GeneratePresignedUrlRequest) {
                    String signerRegion = this.getSignerRegion();
                    if (signerRegion == null) {
                        return this.createSigV2Signer(request, bucketName, key);
                    }
                    return this.updateSigV4SignerWithServiceAndRegion((AWSS3V4Signer)signer, request, signerRegion);
                }
                if (isAdditionalHeadRequestToFindRegion) {
                    return this.updateSigV4SignerWithServiceAndRegion((AWSS3V4Signer)signer, request, "us-east-1");
                }
            }
            if ((regionOverride = this.getSignerRegionOverride()) != null) {
                return this.updateSigV4SignerWithServiceAndRegion(new AWSS3V4Signer(), request, regionOverride);
            }
        }
        if (signer instanceof S3Signer) {
            return this.createSigV2Signer(request, bucketName, key);
        }
        return signer;
    }

    private S3Signer createSigV2Signer(Request<?> request, String bucketName, String key) {
        String resourcePath = "/" + (bucketName != null ? bucketName + "/" : "") + (key != null ? key : "");
        return new S3Signer(request.getHttpMethod().toString(), resourcePath);
    }

    private AWSS3V4Signer updateSigV4SignerWithServiceAndRegion(AWSS3V4Signer v4Signer, Request<?> request, String region) {
        String signingNameOverride = request.getHandlerContext(HandlerContextKey.SIGNING_NAME);
        if (signingNameOverride != null) {
            v4Signer.setServiceName(signingNameOverride);
        } else {
            v4Signer.setServiceName(this.getServiceNameIntern());
        }
        String signingRegionOverride = request.getHandlerContext(HandlerContextKey.SIGNING_REGION);
        if (signingRegionOverride != null) {
            v4Signer.setRegionName(signingRegionOverride);
        } else {
            v4Signer.setRegionName(region);
        }
        return v4Signer;
    }

    private String getSignerRegion() {
        String region = this.getSignerRegionOverride();
        if (region == null) {
            region = this.clientRegion;
        }
        return region;
    }

    private boolean isSignerOverridden() {
        return this.clientConfiguration != null && this.clientConfiguration.getSignerOverride() != null;
    }

    private boolean noExplicitRegionProvided(Request<?> request) {
        return this.isStandardEndpoint(request.getEndpoint()) && this.getSignerRegion() == null;
    }

    private boolean isStandardEndpoint(URI endpoint) {
        return endpoint.getHost().endsWith("s3.amazonaws.com");
    }

    protected <T> void presignRequest(Request<T> request, HttpMethod methodName, String bucketName, String key, Date expiration, String subResource) {
        this.beforeRequest(request);
        String resourcePath = "/" + (bucketName != null ? bucketName + "/" : "") + (key != null ? SdkHttpUtils.urlEncode(key, true) : "") + (subResource != null ? "?" + subResource : "");
        resourcePath = resourcePath.replaceAll("(?<=/)/", "%2F");
        new S3QueryStringSigner(methodName.toString(), resourcePath, expiration).sign(request, CredentialUtils.getCredentialsProvider(request.getOriginalRequest(), this.awsCredentialsProvider).getCredentials());
        if (request.getHeaders().containsKey("x-amz-security-token")) {
            String value = request.getHeaders().get("x-amz-security-token");
            request.addParameter("x-amz-security-token", value);
            request.getHeaders().remove("x-amz-security-token");
        }
    }

    private <T> void beforeRequest(Request<T> request) {
        if (this.requestHandler2s != null) {
            for (RequestHandler2 requestHandler2 : this.requestHandler2s) {
                requestHandler2.beforeRequest(request);
            }
        }
    }

    protected static void populateRequestMetadata(Request<?> request, ObjectMetadata metadata) {
        Map<String, String> userMetadata;
        Date httpExpiresDate;
        Map<String, Object> rawMetadata = metadata.getRawMetadata();
        if (rawMetadata != null) {
            for (Map.Entry<String, Object> entry : rawMetadata.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }
        if ((httpExpiresDate = metadata.getHttpExpiresDate()) != null) {
            request.addHeader("Expires", DateUtils.formatRFC822Date(httpExpiresDate));
        }
        if ((userMetadata = metadata.getUserMetadata()) != null) {
            for (Map.Entry<String, String> entry : userMetadata.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null) {
                    key = key.trim();
                }
                if (value != null) {
                    value = value.trim();
                }
                request.addHeader("x-amz-meta-" + key, value);
            }
        }
    }

    protected static void populateRequesterPaysHeader(Request<?> request, boolean isRequesterPays) {
        if (isRequesterPays) {
            request.addHeader("x-amz-request-payer", "requester");
        }
    }

    private void populateRequestWithMfaDetails(Request<?> request, MultiFactorAuthentication mfa) {
        if (mfa == null) {
            return;
        }
        String endpoint = request.getEndpoint().toString();
        if (endpoint.startsWith("http://")) {
            String httpsEndpoint = endpoint.replace("http://", "https://");
            request.setEndpoint(URI.create(httpsEndpoint));
            log.info((Object)"Overriding current endpoint to use HTTPS as required by S3 for requests containing an MFA header");
        }
        request.addHeader("x-amz-mfa", mfa.getDeviceSerialNumber() + " " + mfa.getToken());
    }

    private void populateRequestWithCopyObjectParameters(Request<? extends AmazonWebServiceRequest> request, CopyObjectRequest copyObjectRequest) {
        ObjectTagging newObjectTagging;
        String copySourceHeader = this.assembleCopySourceHeader(copyObjectRequest.getSourceBucketName(), copyObjectRequest.getSourceKey(), copyObjectRequest.getSourceVersionId());
        request.addHeader("x-amz-copy-source", copySourceHeader);
        AmazonS3Client.addDateHeader(request, "x-amz-copy-source-if-modified-since", copyObjectRequest.getModifiedSinceConstraint());
        AmazonS3Client.addDateHeader(request, "x-amz-copy-source-if-unmodified-since", copyObjectRequest.getUnmodifiedSinceConstraint());
        AmazonS3Client.addStringListHeader(request, "x-amz-copy-source-if-match", copyObjectRequest.getMatchingETagConstraints());
        AmazonS3Client.addStringListHeader(request, "x-amz-copy-source-if-none-match", copyObjectRequest.getNonmatchingETagConstraints());
        if (copyObjectRequest.getAccessControlList() != null) {
            AmazonS3Client.addAclHeaders(request, copyObjectRequest.getAccessControlList());
        } else if (copyObjectRequest.getCannedAccessControlList() != null) {
            request.addHeader("x-amz-acl", copyObjectRequest.getCannedAccessControlList().toString());
        }
        if (copyObjectRequest.getStorageClass() != null) {
            request.addHeader("x-amz-storage-class", copyObjectRequest.getStorageClass());
        }
        if (copyObjectRequest.getRedirectLocation() != null) {
            request.addHeader("x-amz-website-redirect-location", copyObjectRequest.getRedirectLocation());
        }
        AmazonS3Client.populateRequesterPaysHeader(request, copyObjectRequest.isRequesterPays());
        ObjectMetadata newObjectMetadata = copyObjectRequest.getNewObjectMetadata();
        if (copyObjectRequest.getMetadataDirective() != null) {
            request.addHeader("x-amz-metadata-directive", copyObjectRequest.getMetadataDirective());
        } else if (newObjectMetadata != null) {
            request.addHeader("x-amz-metadata-directive", "REPLACE");
        }
        if (newObjectMetadata != null) {
            AmazonS3Client.populateRequestMetadata(request, newObjectMetadata);
        }
        if ((newObjectTagging = copyObjectRequest.getNewObjectTagging()) != null) {
            request.addHeader("x-amz-tagging-directive", "REPLACE");
            request.addHeader("x-amz-tagging", this.urlEncodeTags(newObjectTagging));
        }
        AmazonS3Client.populateSourceSSE_C(request, copyObjectRequest.getSourceSSECustomerKey());
        AmazonS3Client.populateSSE_C(request, copyObjectRequest.getDestinationSSECustomerKey());
    }

    private void populateRequestWithCopyPartParameters(Request<?> request, CopyPartRequest copyPartRequest) {
        String copySourceHeader = this.assembleCopySourceHeader(copyPartRequest.getSourceBucketName(), copyPartRequest.getSourceKey(), copyPartRequest.getSourceVersionId());
        request.addHeader("x-amz-copy-source", copySourceHeader);
        AmazonS3Client.addDateHeader(request, "x-amz-copy-source-if-modified-since", copyPartRequest.getModifiedSinceConstraint());
        AmazonS3Client.addDateHeader(request, "x-amz-copy-source-if-unmodified-since", copyPartRequest.getUnmodifiedSinceConstraint());
        AmazonS3Client.addStringListHeader(request, "x-amz-copy-source-if-match", copyPartRequest.getMatchingETagConstraints());
        AmazonS3Client.addStringListHeader(request, "x-amz-copy-source-if-none-match", copyPartRequest.getNonmatchingETagConstraints());
        if (copyPartRequest.getFirstByte() != null && copyPartRequest.getLastByte() != null) {
            String range = "bytes=" + copyPartRequest.getFirstByte() + "-" + copyPartRequest.getLastByte();
            request.addHeader("x-amz-copy-source-range", range);
        }
        AmazonS3Client.populateSourceSSE_C(request, copyPartRequest.getSourceSSECustomerKey());
        AmazonS3Client.populateSSE_C(request, copyPartRequest.getDestinationSSECustomerKey());
    }

    private void populateRequestHeaderWithMd5(Request<?> request, byte[] content) {
        try {
            byte[] md5 = Md5Utils.computeMD5Hash(content);
            String md5Base64 = BinaryUtils.toBase64(md5);
            request.addHeader("Content-MD5", md5Base64);
        }
        catch (Exception e) {
            throw new SdkClientException("Couldn't compute md5 sum", e);
        }
    }

    private String assembleCopySourceHeader(String sourceBucketName, String sourceObjectKey, String sourceVersionId) {
        String copySourceHeader;
        if (sourceBucketName == null) {
            throw new IllegalArgumentException("Copy source bucket name should not be null");
        }
        if (sourceObjectKey == null) {
            throw new IllegalArgumentException("Copy source object key should not be null");
        }
        if (this.isArn(sourceBucketName)) {
            S3Resource s3Resource;
            Arn resourceArn = Arn.fromString(sourceBucketName);
            try {
                s3Resource = S3ArnConverter.getInstance().convertArn(resourceArn);
            }
            catch (RuntimeException e) {
                throw new IllegalArgumentException("An ARN was passed as a bucket parameter to an S3 operation, however it does not appear to be a valid S3 access point ARN.", e);
            }
            if (!S3ResourceType.ACCESS_POINT.toString().equals(s3Resource.getType())) {
                throw new IllegalArgumentException("An ARN was passed as a bucket parameter to an S3 operation, however it does not appear to be a valid S3 access point ARN.");
            }
            copySourceHeader = SdkHttpUtils.urlEncode(sourceBucketName + "/object/" + sourceObjectKey, false);
        } else {
            copySourceHeader = "/" + SdkHttpUtils.urlEncode(sourceBucketName, true) + "/" + SdkHttpUtils.urlEncode(sourceObjectKey, true);
        }
        if (sourceVersionId != null) {
            copySourceHeader = copySourceHeader + "?versionId=" + sourceVersionId;
        }
        return copySourceHeader;
    }

    private static void populateSSE_C(Request<?> request, SSECustomerKey sseKey) {
        if (sseKey == null) {
            return;
        }
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-server-side-encryption-customer-algorithm", sseKey.getAlgorithm());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-server-side-encryption-customer-key", sseKey.getKey());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-server-side-encryption-customer-key-MD5", sseKey.getMd5());
        if (sseKey.getKey() != null && sseKey.getMd5() == null) {
            String encryptionKey_b64 = sseKey.getKey();
            byte[] encryptionKey = Base64.decode(encryptionKey_b64);
            request.addHeader("x-amz-server-side-encryption-customer-key-MD5", Md5Utils.md5AsBase64(encryptionKey));
        }
    }

    private static void populateSourceSSE_C(Request<?> request, SSECustomerKey sseKey) {
        if (sseKey == null) {
            return;
        }
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-copy-source-server-side-encryption-customer-algorithm", sseKey.getAlgorithm());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-copy-source-server-side-encryption-customer-key", sseKey.getKey());
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-copy-source-server-side-encryption-customer-key-MD5", sseKey.getMd5());
        if (sseKey.getKey() != null && sseKey.getMd5() == null) {
            String encryptionKey_b64 = sseKey.getKey();
            byte[] encryptionKey = Base64.decode(encryptionKey_b64);
            request.addHeader("x-amz-copy-source-server-side-encryption-customer-key-MD5", Md5Utils.md5AsBase64(encryptionKey));
        }
    }

    private static void populateSSE_KMS(Request<?> request, SSEAwsKeyManagementParams sseParams) {
        if (sseParams != null) {
            AmazonS3Client.addHeaderIfNotNull(request, "x-amz-server-side-encryption", sseParams.getEncryption());
            AmazonS3Client.addHeaderIfNotNull(request, "x-amz-server-side-encryption-aws-kms-key-id", sseParams.getAwsKmsKeyId());
            AmazonS3Client.addHeaderIfNotNull(request, "x-amz-server-side-encryption-context", sseParams.getAwsKmsEncryptionContext());
        }
    }

    private void addPartNumberIfNotNull(Request<?> request, Integer partNumber) {
        if (partNumber != null) {
            request.addParameter("partNumber", partNumber.toString());
        }
    }

    private static void addHeaderIfNotNull(Request<?> request, String header, String value) {
        if (value != null) {
            request.addHeader(header, value);
        }
    }

    private static void addIntegerHeaderIfNotNull(Request<?> request, String header, Integer value) {
        if (value != null) {
            request.addHeader(header, Integer.toString(value));
        }
    }

    private static void addParameterIfNotNull(Request<?> request, String paramName, Integer paramValue) {
        if (paramValue != null) {
            AmazonS3Client.addParameterIfNotNull(request, paramName, paramValue.toString());
        }
    }

    private static void addParameterIfNotNull(Request<?> request, String paramName, String paramValue) {
        if (paramValue != null) {
            request.addParameter(paramName, paramValue);
        }
    }

    private static void addDateHeader(Request<?> request, String header, Date value) {
        if (value != null) {
            request.addHeader(header, ServiceUtils.formatRfc822Date(value));
        }
    }

    private static void addStringListHeader(Request<?> request, String header, List<String> values) {
        if (values != null && !values.isEmpty()) {
            request.addHeader(header, ServiceUtils.join(values));
        }
    }

    private static void addResponseHeaderParameters(Request<?> request, ResponseHeaderOverrides responseHeaders) {
        if (responseHeaders != null) {
            if (responseHeaders.getCacheControl() != null) {
                request.addParameter("response-cache-control", responseHeaders.getCacheControl());
            }
            if (responseHeaders.getContentDisposition() != null) {
                request.addParameter("response-content-disposition", responseHeaders.getContentDisposition());
            }
            if (responseHeaders.getContentEncoding() != null) {
                request.addParameter("response-content-encoding", responseHeaders.getContentEncoding());
            }
            if (responseHeaders.getContentLanguage() != null) {
                request.addParameter("response-content-language", responseHeaders.getContentLanguage());
            }
            if (responseHeaders.getContentType() != null) {
                request.addParameter("response-content-type", responseHeaders.getContentType());
            }
            if (responseHeaders.getExpires() != null) {
                request.addParameter("response-expires", responseHeaders.getExpires());
            }
        }
    }

    public String getResourceUrl(String bucketName, String key) {
        try {
            return this.getUrl(bucketName, key).toString();
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public URL getUrl(String bucketName, String key) {
        if (this.isArn(bucketName)) {
            throw new IllegalArgumentException("ARNs are not supported for getUrl in this SDK version. Please use S3Utilities in the AWS SDK for Java 2.x.");
        }
        DefaultRequest request = new DefaultRequest("Amazon S3");
        this.resolveRequestEndpoint(request, bucketName, key, this.endpoint);
        return ServiceUtils.convertRequestToUrl(request, false, false);
    }

    @Override
    public synchronized Region getRegion() {
        String authority = this.endpoint.getAuthority();
        if ("s3.amazonaws.com".equals(authority)) {
            return Region.US_Standard;
        }
        Matcher m = Region.S3_REGIONAL_ENDPOINT_PATTERN.matcher(authority);
        if (m.matches()) {
            return Region.fromValue(m.group(1));
        }
        String signerRegion = this.getSignerRegion();
        if (signerRegion != null) {
            return Region.fromValue(signerRegion);
        }
        throw new IllegalStateException("Unable to determine region from configured S3 endpoint (" + authority + ") or signing region.");
    }

    @Override
    public String getRegionName() {
        String authority = this.endpoint.getAuthority();
        if ("s3.amazonaws.com".equals(authority)) {
            return "us-east-1";
        }
        return this.getRegionNameFromAuthorityOrSigner();
    }

    private String getRegionNameFromAuthorityOrSigner() {
        String authority = this.endpoint.getAuthority();
        Matcher m = Region.S3_REGIONAL_ENDPOINT_PATTERN.matcher(authority);
        if (m.matches()) {
            try {
                return RegionUtils.getRegion(m.group(1)).getName();
            }
            catch (Exception e) {
                throw new IllegalStateException("No valid region has been specified. Unable to return region name.", e);
            }
        }
        String signerRegion = this.getSignerRegion();
        if (signerRegion != null) {
            return signerRegion;
        }
        throw new IllegalStateException("Unable to determine region from configured S3 endpoint (" + authority + ") or signing region.");
    }

    private static boolean isRegionFipsEnabled(String regionName) {
        return regionName.startsWith("fips-") || regionName.endsWith("-fips");
    }

    protected <X extends AmazonWebServiceRequest> Request<X> createRequest(String bucketName, String key, X originalRequest, HttpMethodName httpMethod) {
        return this.createRequest(bucketName, key, originalRequest, httpMethod, this.endpoint);
    }

    protected <X extends AmazonWebServiceRequest> Request<X> createRequest(String bucketName, String key, X originalRequest, HttpMethodName httpMethod, URI endpoint) {
        DefaultRequest request = new DefaultRequest(originalRequest, "Amazon S3");
        request.setHttpMethod(httpMethod);
        request.addHandlerContext(S3HandlerContextKeys.IS_CHUNKED_ENCODING_DISABLED, this.clientOptions.isChunkedEncodingDisabled());
        request.addHandlerContext(S3HandlerContextKeys.IS_PAYLOAD_SIGNING_ENABLED, this.clientOptions.isPayloadSigningEnabled());
        request.addHandlerContext(HandlerContextKey.SERVICE_ID, SERVICE_ID);
        if (originalRequest instanceof ExpectedBucketOwnerRequest) {
            ExpectedBucketOwnerRequest expectedBucketOwnerRequest = (ExpectedBucketOwnerRequest)((Object)originalRequest);
            AmazonS3Client.addHeaderIfNotNull(request, "x-amz-expected-bucket-owner", expectedBucketOwnerRequest.getExpectedBucketOwner());
        }
        if (originalRequest instanceof ExpectedSourceBucketOwnerRequest) {
            ExpectedSourceBucketOwnerRequest expectedSourceBucketOwnerRequest = (ExpectedSourceBucketOwnerRequest)((Object)originalRequest);
            AmazonS3Client.addHeaderIfNotNull(request, "x-amz-source-expected-bucket-owner", expectedSourceBucketOwnerRequest.getExpectedSourceBucketOwner());
        }
        if (this.isAccessPointArn(bucketName)) {
            Arn resourceArn = Arn.fromString(bucketName);
            S3Resource s3Resource = S3ArnConverter.getInstance().convertArn(resourceArn);
            this.validateConfiguration(s3Resource);
            com.amazonaws.regions.Region region = RegionUtils.getRegion(this.getRegionNameFromAuthorityOrSigner());
            this.validateS3ResourceArn(resourceArn, region);
            this.validateParentResourceIfNeeded((S3AccessPointResource)s3Resource, this.getRegionName());
            endpoint = this.getEndpointForAccessPoint((S3AccessPointResource)s3Resource, region.getDomain());
            String signingRegion = s3Resource.getRegion();
            request.addHandlerContext(HandlerContextKey.SIGNING_REGION, signingRegion);
            this.resolveAccessPointEndpoint(request, null, key, endpoint);
            if (this.isOutpostAccessPointArn(bucketName)) {
                request.addHandlerContext(HandlerContextKey.SIGNING_NAME, S3_OUTPOSTS_NAME);
            } else if (this.isObjectLambdasArn(bucketName)) {
                request.addHandlerContext(HandlerContextKey.SIGNING_NAME, S3_OBJECT_LAMBDAS_NAME);
            }
            return request;
        }
        if (this.isObjectLambdasRequest(originalRequest)) {
            this.validateConfigurationForObjectLambdaOperation();
            com.amazonaws.regions.Region region = RegionUtils.getRegion(this.getRegionName());
            endpoint = this.getEndpointForObjectLambdas(region.getDomain(), region.getName());
            this.resolveRequestEndpoint(request, null, null, endpoint);
            if (originalRequest instanceof WriteGetObjectResponseRequest && !this.clientConfiguration.isDisableHostPrefixInjection()) {
                WriteGetObjectResponseRequest writeGetObjectResponseRequest = (WriteGetObjectResponseRequest)originalRequest;
                this.rejectNull(writeGetObjectResponseRequest.getRequestRoute(), "requestRoute must not be null");
                String requestRoute = writeGetObjectResponseRequest.getRequestRoute() + ".";
                URI newEndpoint = UriResourcePathUtils.updateUriHost(request.getEndpoint(), requestRoute);
                this.resolveEndpointIdentity(request, null, null, newEndpoint);
            }
            request.addHandlerContext(HandlerContextKey.SIGNING_NAME, S3_OBJECT_LAMBDAS_NAME);
            return request;
        }
        String signingRegion = this.getSigningRegion();
        if (this.clientOptions.isAccelerateModeEnabled() && !(originalRequest instanceof S3AccelerateUnsupported)) {
            endpoint = this.clientOptions.isDualstackEnabled() ? RuntimeHttpUtils.toUri("s3-accelerate.dualstack.amazonaws.com", this.clientConfiguration) : RuntimeHttpUtils.toUri("s3-accelerate.amazonaws.com", this.clientConfiguration);
        }
        this.resolveRequestEndpoint(request, bucketName, key, endpoint);
        request.addHandlerContext(HandlerContextKey.SIGNING_REGION, signingRegion);
        return request;
    }

    private void validateParentResourceIfNeeded(S3AccessPointResource s3Resource, String regionName) {
        if (s3Resource.getParentS3Resource() == null) {
            return;
        }
        String type = s3Resource.getParentS3Resource().getType();
        if (S3ResourceType.fromValue(type) == S3ResourceType.OUTPOST) {
            if (this.clientOptions.isDualstackEnabled()) {
                throw new IllegalArgumentException(String.format("An ARN of type %s cannot be passed as a bucket parameter to an S3 operation if the S3 client has been configured with dualstack", type));
            }
            if (AmazonS3Client.isRegionFipsEnabled(regionName)) {
                throw new IllegalArgumentException(String.format("An ARN of type %s cannot be passed as a bucket parameter to an S3 operation if the S3 client has been configured with a FIPS enabled region.", type));
            }
        }
    }

    private URI getEndpointForAccessPoint(S3AccessPointResource s3Resource, String domain) {
        URI endpointOverride = this.isEndpointOverridden() ? this.getEndpoint() : null;
        S3Resource parentS3Resource = s3Resource.getParentS3Resource();
        String protocol = this.clientConfiguration.getProtocol().toString();
        if (parentS3Resource instanceof S3OutpostResource) {
            S3OutpostResource outpostResource = (S3OutpostResource)parentS3Resource;
            return S3OutpostAccessPointBuilder.create().withEndpointOverride(endpointOverride).withAccountId(s3Resource.getAccountId()).withOutpostId(outpostResource.getOutpostId()).withRegion(s3Resource.getRegion()).withAccessPointName(s3Resource.getAccessPointName()).withProtocol(protocol).withDomain(domain).toURI();
        }
        com.amazonaws.regions.Region clientRegion = RegionUtils.getRegion(this.getRegionName());
        boolean fipsRegionProvided = AmazonS3Client.isRegionFipsEnabled(clientRegion.getName());
        if (parentS3Resource != null && S3ResourceType.OBJECT_LAMBDAS.toString().equals(parentS3Resource.getType())) {
            return S3ObjectLambdaEndpointBuilder.create().withEndpointOverride(endpointOverride).withAccessPointName(s3Resource.getAccessPointName()).withAccountId(s3Resource.getAccountId()).withRegion(s3Resource.getRegion()).withProtocol(protocol).withDomain(domain).withFipsEnabled(fipsRegionProvided).withDualstackEnabled(this.clientOptions.isDualstackEnabled()).toURI();
        }
        return S3AccessPointBuilder.create().withEndpointOverride(endpointOverride).withAccessPointName(s3Resource.getAccessPointName()).withAccountId(s3Resource.getAccountId()).withRegion(s3Resource.getRegion()).withProtocol(protocol).withDomain(domain).withDualstackEnabled(this.clientOptions.isDualstackEnabled()).withFipsEnabled(fipsRegionProvided).toURI();
    }

    private URI getEndpointForObjectLambdas(String domain, String trimmedRegion) {
        if (this.isEndpointOverridden()) {
            return this.getEndpoint();
        }
        String protocol = null;
        if (this.clientConfiguration.getProtocol() != null) {
            protocol = this.clientConfiguration.getProtocol().toString();
        }
        return S3ObjectLambdaOperationEndpointBuilder.create().withProtocol(protocol).withDomain(domain).withRegion(trimmedRegion).toURI();
    }

    private void validateConfiguration(S3Resource s3Resource) {
        String type = s3Resource.getType();
        if (S3ResourceType.fromValue(type) != S3ResourceType.ACCESS_POINT) {
            throw new IllegalArgumentException("An unsupported ARN was passed as a bucket parameter to an S3 operation");
        }
        if (this.clientOptions.isAccelerateModeEnabled()) {
            throw new IllegalArgumentException(String.format("An ARN of type %s cannot be passed as a bucket parameter to an S3 operation if the S3 client has been configured with accelerate mode enabled.", type));
        }
        if (this.clientOptions.isPathStyleAccess()) {
            throw new IllegalArgumentException(String.format("An ARN of type %s cannot be passed as a bucket parameter to an S3 operation if the S3 client has been configured with path style addressing enabled.", type));
        }
    }

    private void validateConfigurationForObjectLambdaOperation() {
        if (this.clientOptions.isDualstackEnabled()) {
            throw new IllegalArgumentException("S3 Object Lambda does not support dualstack endpoints");
        }
        if (this.clientOptions.isAccelerateModeEnabled()) {
            throw new IllegalArgumentException("S3 Object Lambda does not support accelerate endpoints");
        }
    }

    private void validateS3ResourceArn(Arn resourceArn, com.amazonaws.regions.Region clientRegion) {
        String clientPartition;
        String string = clientPartition = clientRegion == null ? null : clientRegion.getPartition();
        if (this.isMultiRegionAccessPointArn(resourceArn.toString())) {
            throw new IllegalArgumentException("AWS SDK for Java version 1.x does not support passing a multi-region access point Amazon Resource Names (ARNs) as a bucket parameter to an S3 operation. If this functionality is required by your application, please upgrade to AWS SDK for Java version 2.x");
        }
        if (clientPartition == null || !clientPartition.equals(resourceArn.getPartition())) {
            throw new IllegalArgumentException("The partition field of the ARN being passed as a bucket parameter to an S3 operation does not match the partition the S3 client has been configured with. Provided partition: '" + resourceArn.getPartition() + "'; client partition: '" + clientPartition + "'.");
        }
        this.validateIsTrue(!AmazonS3Client.isRegionFipsEnabled(resourceArn.getRegion()), "Invalid ARN, FIPS region is not allowed in ARN. Provided arn region: '" + resourceArn.getRegion() + "'.", new Object[0]);
        if (!this.clientOptions.isForceGlobalBucketAccessEnabled() && !this.useArnRegion() || AmazonS3Client.isRegionFipsEnabled(clientRegion.getName())) {
            this.validateIsTrue(this.removeFipsIfNeeded(clientRegion.getName()).equals(resourceArn.getRegion()), "The region field of the ARN being passed as a bucket parameter to an S3 operation does not match the region the client was configured with. Provided region: '" + resourceArn.getRegion() + "'; client region: '" + clientRegion.getName() + "'.", new Object[0]);
        }
    }

    private String removeFipsIfNeeded(String region) {
        if (region.startsWith("fips-")) {
            return region.replace("fips-", "");
        }
        if (region.endsWith("-fips")) {
            return region.replace("-fips", "");
        }
        return region;
    }

    private boolean useArnRegion() {
        if (this.clientOptions.isUseArnRegion()) {
            return this.clientOptions.isUseArnRegion();
        }
        return USE_ARN_REGION_RESOLVER.useArnRegion();
    }

    private void resolveAccessPointEndpoint(Request<?> request, String bucketName, String key, URI endpoint) {
        this.resolveEndpointIdentity(request, bucketName, key, endpoint);
    }

    private void resolveEndpointIdentity(Request<?> request, String bucketName, String key, URI endpoint) {
        IdentityEndpointBuilder builder = new IdentityEndpointBuilder(endpoint);
        this.buildEndpointResolver(builder, bucketName, key).resolveRequestEndpoint(request);
    }

    private void resolveRequestEndpoint(Request<?> request, String bucketName, String key, URI endpoint) {
        ServiceEndpointBuilder builder = this.getBuilder(endpoint, endpoint.getScheme(), false);
        this.buildEndpointResolver(builder, bucketName, key).resolveRequestEndpoint(request);
    }

    private S3RequestEndpointResolver buildDefaultEndpointResolver(String protocol, String bucketName, String key) {
        ServiceEndpointBuilder builder = this.getBuilder(this.endpoint, protocol, true);
        return new S3RequestEndpointResolver(builder, this.clientOptions.isPathStyleAccess(), bucketName, key);
    }

    private ServiceEndpointBuilder getBuilder(URI endpoint, String protocol, boolean useDefaultBuilder) {
        if (this.clientOptions.isDualstackEnabled() && !this.clientOptions.isAccelerateModeEnabled()) {
            return new DualstackEndpointBuilder(this.getServiceNameIntern(), protocol, this.getRegion().toAWSRegion());
        }
        if (useDefaultBuilder) {
            return new DefaultServiceEndpointBuilder(this.getServiceName(), protocol);
        }
        return new IdentityEndpointBuilder(endpoint);
    }

    @Override
    public PresignedUrlDownloadResult download(PresignedUrlDownloadRequest presignedUrlDownloadRequest) throws SdkClientException {
        ValidationUtils.assertNotNull(presignedUrlDownloadRequest.getPresignedUrl(), "Presigned URL");
        ProgressListener listener = presignedUrlDownloadRequest.getGeneralProgressListener();
        Request<PresignedUrlDownloadRequest> request = this.createRequestForPresignedUrl(presignedUrlDownloadRequest, HttpMethodName.GET, presignedUrlDownloadRequest.getPresignedUrl());
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetObject");
        request.addHandlerContext(HandlerContextKey.HAS_STREAMING_OUTPUT, Boolean.TRUE);
        long[] range = presignedUrlDownloadRequest.getRange();
        if (range != null) {
            request.addHeader("Range", "bytes=" + Long.toString(range[0]) + "-" + Long.toString(range[1]));
        }
        try {
            SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
            S3Object s3Object = this.client.execute(request, new S3ObjectResponseHandler(), this.errorResponseHandler, this.createExecutionContext(AmazonWebServiceRequest.NOOP, new NoOpSignerProvider()), this.requestConfigWithSkipAppendUriPath(request)).getAwsResponse();
            boolean skipClientSideValidation = this.skipMd5CheckStrategy.skipClientSideValidation(presignedUrlDownloadRequest, s3Object.getObjectMetadata());
            this.postProcessS3Object(s3Object, skipClientSideValidation, listener);
            return new PresignedUrlDownloadResult().withS3Object(s3Object);
        }
        catch (AmazonS3Exception ase) {
            SDKProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw ase;
        }
    }

    @Override
    public void download(final PresignedUrlDownloadRequest presignedUrlDownloadRequest, File destinationFile) throws SdkClientException {
        ValidationUtils.assertNotNull(destinationFile, "Destination file");
        ServiceUtils.retryableDownloadS3ObjectToFile(destinationFile, new ServiceUtils.RetryableS3DownloadTask(){

            @Override
            public S3Object getS3ObjectStream() {
                return AmazonS3Client.this.download(presignedUrlDownloadRequest).getS3Object();
            }

            @Override
            public boolean needIntegrityCheck() {
                return !AmazonS3Client.this.skipMd5CheckStrategy.skipClientSideValidationPerRequest(presignedUrlDownloadRequest);
            }
        }, false);
    }

    @Override
    public PresignedUrlUploadResult upload(PresignedUrlUploadRequest presignedUrlUploadRequest) {
        presignedUrlUploadRequest = this.beforeClientExecution(presignedUrlUploadRequest);
        this.rejectNull(presignedUrlUploadRequest, "The PresignedUrlUploadRequest object cannot be null");
        this.rejectNull(presignedUrlUploadRequest.getPresignedUrl(), "Presigned URL");
        File file = presignedUrlUploadRequest.getFile();
        InputStream isOrig = presignedUrlUploadRequest.getInputStream();
        ProgressListener listener = presignedUrlUploadRequest.getGeneralProgressListener();
        ObjectMetadata metadata = presignedUrlUploadRequest.getMetadata();
        if (metadata == null) {
            metadata = new ObjectMetadata();
        }
        Request<PresignedUrlUploadRequest> request = this.createRequestForPresignedUrl(presignedUrlUploadRequest, presignedUrlUploadRequest.getHttpMethodName(), presignedUrlUploadRequest.getPresignedUrl());
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutObject");
        Integer bufsize = Constants.getS3StreamBufferSize();
        if (bufsize != null) {
            AmazonWebServiceRequest awsreq = request.getOriginalRequest();
            awsreq.getRequestClientOptions().setReadLimit(bufsize);
        }
        return this.uploadObject(isOrig, file, metadata, listener, request, presignedUrlUploadRequest, true, this.skipMd5CheckStrategy.skipClientSideValidationPerRequest(presignedUrlUploadRequest), new PresignedUrlUploadStrategy(presignedUrlUploadRequest.getPresignedUrl()), !this.isSigV2PresignedUrl(presignedUrlUploadRequest.getPresignedUrl()));
    }

    private RequestConfig requestConfigWithSkipAppendUriPath(Request request) {
        AmazonWebServiceRequestAdapter config = new AmazonWebServiceRequestAdapter(request.getOriginalRequest());
        ((RequestConfig)config).getRequestClientOptions().setSkipAppendUriPath(true);
        return config;
    }

    private <X extends AmazonWebServiceRequest> Request<X> createRequestForPresignedUrl(X originalRequest, HttpMethodName httpMethod, URL endpoint) {
        DefaultRequest request = new DefaultRequest(originalRequest, "Amazon S3");
        request.setHttpMethod(httpMethod);
        try {
            request.setEndpoint(endpoint.toURI());
        }
        catch (URISyntaxException e) {
            throw new SdkClientException(e);
        }
        if (originalRequest.getCustomRequestHeaders() != null) {
            for (Map.Entry<String, String> entry : originalRequest.getCustomRequestHeaders().entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (request.getHeaders().get("Content-Type") == null && this.isSigV2PresignedUrl(endpoint)) {
            request.addHeader("Content-Type", "");
        }
        request.addHandlerContext(S3HandlerContextKeys.IS_CHUNKED_ENCODING_DISABLED, this.clientOptions.isChunkedEncodingDisabled());
        request.addHandlerContext(S3HandlerContextKeys.IS_PAYLOAD_SIGNING_ENABLED, this.clientOptions.isPayloadSigningEnabled());
        request.addHandlerContext(HandlerContextKey.SERVICE_ID, SERVICE_ID);
        return request;
    }

    private boolean isSigV2PresignedUrl(URL presignedUrl) {
        String url = presignedUrl.toString();
        return url.contains("AWSAccessKeyId=") && !presignedUrl.toString().contains("X-Amz-Algorithm=AWS4-HMAC-SHA256");
    }

    private S3RequestEndpointResolver buildEndpointResolver(ServiceEndpointBuilder serviceEndpointBuilder, String bucketName, String key) {
        return new S3RequestEndpointResolver(serviceEndpointBuilder, this.clientOptions.isPathStyleAccess(), bucketName, key);
    }

    @Override
    protected final SignerProvider createSignerProvider(Signer signer) {
        return new S3SignerProvider(this, signer);
    }

    private <X, Y extends AmazonWebServiceRequest> X invoke(Request<Y> request, Unmarshaller<X, InputStream> unmarshaller, String bucketName, String key) {
        return this.invoke(request, new S3XmlResponseHandler<X>(unmarshaller), bucketName, key);
    }

    private <X, Y extends AmazonWebServiceRequest> X invoke(Request<Y> request, HttpResponseHandler<AmazonWebServiceResponse<X>> responseHandler, String bucket, String key) {
        return this.invoke(request, responseHandler, bucket, key, false);
    }

    private <X, Y extends AmazonWebServiceRequest> X invoke(Request<Y> request, HttpResponseHandler<AmazonWebServiceResponse<X>> responseHandler, String bucket, String key, boolean isAdditionalHeadRequestToFindRegion) {
        AmazonWebServiceRequest originalRequest = request.getOriginalRequest();
        this.checkHttps(originalRequest);
        S3SignerProvider signerProvider = new S3SignerProvider(this, this.getSigner());
        ExecutionContext executionContext = this.createExecutionContext(originalRequest, signerProvider);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        request.setAWSRequestMetrics(awsRequestMetrics);
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Response response = null;
        try {
            request.setTimeOffset(this.timeOffset);
            if (!request.getHeaders().containsKey("Content-Type")) {
                request.addHeader("Content-Type", "application/octet-stream");
            }
            if (!isAdditionalHeadRequestToFindRegion && this.shouldPerformHeadRequestToFindRegion(request, bucket)) {
                this.fetchRegionFromCache(bucket);
            }
            Signer signer = this.createSigner(request, bucket, key, isAdditionalHeadRequestToFindRegion);
            signerProvider.setSigner(signer);
            if (this.isSignerOverridden() && !(signer instanceof AWSS3V4Signer)) {
                executionContext.setAuthErrorRetryStrategy(new S3V4AuthErrorRetryStrategy(this.buildDefaultEndpointResolver(AmazonS3Client.getProtocol(request), bucket, key)));
            }
            executionContext.setCredentialsProvider(CredentialUtils.getCredentialsProvider(request.getOriginalRequest(), this.awsCredentialsProvider));
            this.validateRequestBeforeTransmit(request);
            response = this.client.execute(request, responseHandler, this.errorResponseHandler, executionContext);
            Object t = response.getAwsResponse();
            return (X)t;
        }
        catch (ResetException ex) {
            ex.setExtraInfo("If the request involves an input stream, the maximum stream buffer size can be configured via request.getRequestClientOptions().setReadLimit(int)");
            throw ex;
        }
        catch (AmazonS3Exception ase) {
            if (ase.getStatusCode() == 301 && ase.getAdditionalDetails() != null) {
                String region = ase.getAdditionalDetails().get("x-amz-bucket-region");
                bucketRegionCache.put(bucket, region);
                ase.setErrorMessage("The bucket is in this region: " + region + ". Please use this region to retry the request");
            }
            throw ase;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    private void validateRequestBeforeTransmit(Request<?> request) {
        boolean implicitCrossRegionForbidden = this.areImplicitGlobalClientsDisabled();
        boolean explicitCrossRegionEnabled = this.clientOptions.isForceGlobalBucketAccessEnabled();
        if (this.noExplicitRegionProvided(request) && implicitCrossRegionForbidden && !explicitCrossRegionEnabled) {
            String error = String.format("While the %s system property is enabled, Amazon S3 clients cannot be used without first configuring a region or explicitly enabling global bucket access discovery in the S3 client builder.", "com.amazonaws.services.s3.disableImplicitGlobalClients");
            throw new IllegalStateException(error);
        }
    }

    private boolean areImplicitGlobalClientsDisabled() {
        String setting = System.getProperty("com.amazonaws.services.s3.disableImplicitGlobalClients");
        return setting != null && !setting.equals("false");
    }

    private boolean shouldPerformHeadRequestToFindRegion(Request<?> request, String bucket) {
        return bucket != null && !this.isAccessPointArn(bucket) && !(request.getOriginalRequest() instanceof CreateBucketRequest) && this.bucketRegionShouldBeCached(request);
    }

    private boolean isAccessPointArn(String s) {
        return s != null && s.startsWith("arn:") && (this.isS3AccessPointArn(s) || this.isOutpostAccessPointArn(s) || this.isObjectLambdasArn(s));
    }

    private boolean isS3AccessPointArn(String s) {
        return s.contains(":accesspoint");
    }

    private boolean isOutpostAccessPointArn(String s) {
        return s.contains(":s3-outposts");
    }

    private boolean isObjectLambdasArn(String s) {
        return s.contains(":s3-object-lambda");
    }

    private boolean isObjectLambdasRequest(AmazonWebServiceRequest request) {
        return request instanceof WriteGetObjectResponseRequest;
    }

    private boolean isMultiRegionAccessPointArn(String s) {
        return s.contains(":global");
    }

    private boolean isArn(String s) {
        return s != null && s.startsWith("arn:");
    }

    private boolean bucketRegionShouldBeCached(Request<?> request) {
        return this.clientOptions.isForceGlobalBucketAccessEnabled() || this.noExplicitRegionProvided(request);
    }

    @Override
    public void enableRequesterPays(String bucketName) {
        RequestPaymentConfiguration configuration = new RequestPaymentConfiguration(RequestPaymentConfiguration.Payer.Requester);
        this.setRequestPaymentConfiguration(new SetRequestPaymentConfigurationRequest(bucketName, configuration));
    }

    @Override
    public void disableRequesterPays(String bucketName) {
        RequestPaymentConfiguration configuration = new RequestPaymentConfiguration(RequestPaymentConfiguration.Payer.BucketOwner);
        this.setRequestPaymentConfiguration(new SetRequestPaymentConfigurationRequest(bucketName, configuration));
    }

    @Override
    public boolean isRequesterPaysEnabled(String bucketName) {
        RequestPaymentConfiguration configuration = this.getBucketRequestPayment(new GetRequestPaymentConfigurationRequest(bucketName));
        return configuration.getPayer() == RequestPaymentConfiguration.Payer.Requester;
    }

    @Override
    public void setRequestPaymentConfiguration(SetRequestPaymentConfigurationRequest setRequestPaymentConfigurationRequest) {
        String bucketName = setRequestPaymentConfigurationRequest.getBucketName();
        RequestPaymentConfiguration configuration = setRequestPaymentConfigurationRequest.getConfiguration();
        this.rejectNull(bucketName, "The bucket name parameter must be specified while setting the Requester Pays.");
        this.rejectNull(configuration, "The request payment configuration parameter must be specified when setting the Requester Pays.");
        Request<SetRequestPaymentConfigurationRequest> request = this.createRequest(bucketName, null, setRequestPaymentConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketRequestPayment");
        request.addParameter("requestPayment", null);
        request.addHeader("Content-Type", "application/xml");
        byte[] bytes = requestPaymentConfigurationXmlFactory.convertToXmlByteArray(configuration);
        request.setContent(new ByteArrayInputStream(bytes));
        this.populateRequestHeaderWithMd5(request, bytes);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    private RequestPaymentConfiguration getBucketRequestPayment(GetRequestPaymentConfigurationRequest getRequestPaymentConfigurationRequest) {
        String bucketName = getRequestPaymentConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified while getting the Request Payment Configuration.");
        Request<GetRequestPaymentConfigurationRequest> request = this.createRequest(bucketName, null, getRequestPaymentConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketRequestPayment");
        request.addParameter("requestPayment", null);
        request.addHeader("Content-Type", "application/xml");
        return this.invoke(request, new Unmarshallers.RequestPaymentConfigurationUnmarshaller(), bucketName, null);
    }

    private void setZeroContentLength(Request<?> req) {
        req.addHeader("Content-Length", String.valueOf(0));
    }

    private void checkHttps(AmazonWebServiceRequest req) {
        CopyPartRequest cpr;
        Object p;
        if (req instanceof SSECustomerKeyProvider) {
            p = (SSECustomerKeyProvider)((Object)req);
            if (p.getSSECustomerKey() != null) {
                this.assertHttps();
            }
        } else if (req instanceof CopyObjectRequest) {
            CopyObjectRequest cor = (CopyObjectRequest)req;
            if (cor.getSourceSSECustomerKey() != null || cor.getDestinationSSECustomerKey() != null) {
                this.assertHttps();
            }
        } else if (req instanceof CopyPartRequest && ((cpr = (CopyPartRequest)req).getSourceSSECustomerKey() != null || cpr.getDestinationSSECustomerKey() != null)) {
            this.assertHttps();
        }
        if (req instanceof SSEAwsKeyManagementParamsProvider && (p = (SSEAwsKeyManagementParamsProvider)((Object)req)).getSSEAwsKeyManagementParams() != null) {
            this.assertHttps();
        }
    }

    private void assertHttps() {
        String scheme;
        URI endpoint = this.endpoint;
        String string = scheme = endpoint == null ? null : endpoint.getScheme();
        if (!Protocol.HTTPS.toString().equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("HTTPS must be used when sending customer encryption keys (SSE-C) to S3, in order to protect your encryption keys.");
        }
    }

    synchronized URI getEndpoint() {
        return this.endpoint;
    }

    private static String getProtocol(Request<?> request) {
        if (request == null || request.getEndpoint() == null) {
            return null;
        }
        return request.getEndpoint().getScheme();
    }

    protected final InitiateMultipartUploadRequest newInitiateMultipartUploadRequest(UploadObjectRequest req) {
        return (InitiateMultipartUploadRequest)((AmazonWebServiceRequest)new InitiateMultipartUploadRequest(req.getBucketName(), req.getKey(), req.getMetadata()).withRedirectLocation(req.getRedirectLocation()).withSSEAwsKeyManagementParams(req.getSSEAwsKeyManagementParams()).withSSECustomerKey(req.getSSECustomerKey()).withStorageClass(req.getStorageClass()).withAccessControlList(req.getAccessControlList()).withCannedACL(req.getCannedAcl()).withGeneralProgressListener(req.getGeneralProgressListener())).withRequestMetricCollector(req.getRequestMetricCollector());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void putLocalObject(UploadObjectRequest reqIn, OutputStream os) throws IOException {
        UploadObjectRequest req = reqIn.clone();
        File fileOrig = req.getFile();
        InputStream isOrig = req.getInputStream();
        if (isOrig == null) {
            if (fileOrig == null) {
                throw new IllegalArgumentException("Either a file lor input stream must be specified");
            }
            req.setInputStream(new FileInputStream(fileOrig));
            req.setFile(null);
        }
        try {
            IOUtils.copy(req.getInputStream(), os);
        }
        finally {
            S3DataSource.Utils.cleanupDataSource(req, fileOrig, isOrig, req.getInputStream(), log);
            IOUtils.closeQuietly(os, log);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    CompleteMultipartUploadResult uploadObject(UploadObjectRequest req) throws IOException, InterruptedException, ExecutionException {
        UploadObjectObserver observer;
        boolean defaultExecutorService;
        ExecutorService es = req.getExecutorService();
        boolean bl = defaultExecutorService = es == null;
        if (es == null) {
            es = Executors.newFixedThreadPool(this.clientConfiguration.getMaxConnections());
        }
        if ((observer = req.getUploadObjectObserver()) == null) {
            observer = new UploadObjectObserver();
        }
        observer.init(req, this, this, es);
        observer.onUploadInitiation(req);
        ArrayList<PartETag> partETags = new ArrayList<PartETag>();
        MultiFileOutputStream mfos = req.getMultiFileOutputStream();
        if (mfos == null) {
            mfos = new MultiFileOutputStream();
        }
        try {
            mfos.init(observer, req.getPartSize(), req.getDiskLimit());
            this.putLocalObject(req, mfos);
            for (Future<UploadPartResult> future : observer.getFutures()) {
                UploadPartResult partResult = future.get();
                partETags.add(new PartETag(partResult.getPartNumber(), partResult.getETag()));
            }
        }
        finally {
            if (defaultExecutorService) {
                es.shutdownNow();
            }
            mfos.cleanup();
        }
        return observer.onCompletion(partETags);
    }

    @Override
    public void setBucketReplicationConfiguration(String bucketName, BucketReplicationConfiguration configuration) throws AmazonServiceException, SdkClientException {
        this.setBucketReplicationConfiguration(new SetBucketReplicationConfigurationRequest(bucketName, configuration));
    }

    @Override
    public void setBucketReplicationConfiguration(SetBucketReplicationConfigurationRequest setBucketReplicationConfigurationRequest) throws AmazonServiceException, SdkClientException {
        setBucketReplicationConfigurationRequest = this.beforeClientExecution(setBucketReplicationConfigurationRequest);
        this.rejectNull(setBucketReplicationConfigurationRequest, "The set bucket replication configuration request object must be specified.");
        String bucketName = setBucketReplicationConfigurationRequest.getBucketName();
        BucketReplicationConfiguration bucketReplicationConfiguration = setBucketReplicationConfigurationRequest.getReplicationConfiguration();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when setting replication configuration.");
        this.rejectNull(bucketReplicationConfiguration, "The replication configuration parameter must be specified when setting replication configuration.");
        Request<SetBucketReplicationConfigurationRequest> request = this.createRequest(bucketName, null, setBucketReplicationConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketReplication");
        request.addParameter("replication", null);
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(bucketReplicationConfiguration);
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-bucket-object-lock-token", setBucketReplicationConfigurationRequest.getToken());
        request.addHeader("Content-Length", String.valueOf(bytes.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(bytes));
        this.populateRequestHeaderWithMd5(request, bytes);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public BucketReplicationConfiguration getBucketReplicationConfiguration(String bucketName) throws AmazonServiceException, SdkClientException {
        return this.getBucketReplicationConfiguration(new GetBucketReplicationConfigurationRequest(bucketName));
    }

    @Override
    public BucketReplicationConfiguration getBucketReplicationConfiguration(GetBucketReplicationConfigurationRequest getBucketReplicationConfigurationRequest) throws AmazonServiceException, SdkClientException {
        getBucketReplicationConfigurationRequest = this.beforeClientExecution(getBucketReplicationConfigurationRequest);
        this.rejectNull(getBucketReplicationConfigurationRequest, "The bucket request parameter must be specified when retrieving replication configuration");
        String bucketName = getBucketReplicationConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket request must specify a bucket name when retrieving replication configuration");
        Request<GetBucketReplicationConfigurationRequest> request = this.createRequest(bucketName, null, getBucketReplicationConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketReplication");
        request.addParameter("replication", null);
        return this.invoke(request, new Unmarshallers.BucketReplicationConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public void deleteBucketReplicationConfiguration(String bucketName) throws AmazonServiceException, SdkClientException {
        this.deleteBucketReplicationConfiguration(new DeleteBucketReplicationConfigurationRequest(bucketName));
    }

    @Override
    public void deleteBucketReplicationConfiguration(DeleteBucketReplicationConfigurationRequest deleteBucketReplicationConfigurationRequest) throws AmazonServiceException, SdkClientException {
        deleteBucketReplicationConfigurationRequest = this.beforeClientExecution(deleteBucketReplicationConfigurationRequest);
        String bucketName = deleteBucketReplicationConfigurationRequest.getBucketName();
        this.rejectNull(bucketName, "The bucket name parameter must be specified when deleting replication configuration");
        Request<DeleteBucketReplicationConfigurationRequest> request = this.createRequest(bucketName, null, deleteBucketReplicationConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketReplication");
        request.addParameter("replication", null);
        this.invoke(request, this.voidResponseHandler, bucketName, null);
    }

    @Override
    public DeleteBucketMetricsConfigurationResult deleteBucketMetricsConfiguration(String bucketName, String id) throws AmazonServiceException, SdkClientException {
        return this.deleteBucketMetricsConfiguration(new DeleteBucketMetricsConfigurationRequest(bucketName, id));
    }

    @Override
    public DeleteBucketMetricsConfigurationResult deleteBucketMetricsConfiguration(DeleteBucketMetricsConfigurationRequest deleteBucketMetricsConfigurationRequest) throws AmazonServiceException, SdkClientException {
        deleteBucketMetricsConfigurationRequest = this.beforeClientExecution(deleteBucketMetricsConfigurationRequest);
        this.rejectNull(deleteBucketMetricsConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(deleteBucketMetricsConfigurationRequest.getBucketName(), "BucketName");
        String id = ValidationUtils.assertStringNotEmpty(deleteBucketMetricsConfigurationRequest.getId(), "Metrics Id");
        Request<DeleteBucketMetricsConfigurationRequest> request = this.createRequest(bucketName, null, deleteBucketMetricsConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketMetricsConfiguration");
        request.addParameter("metrics", null);
        request.addParameter("id", id);
        return this.invoke(request, new Unmarshallers.DeleteBucketMetricsConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public GetBucketMetricsConfigurationResult getBucketMetricsConfiguration(String bucketName, String id) throws AmazonServiceException, SdkClientException {
        return this.getBucketMetricsConfiguration(new GetBucketMetricsConfigurationRequest(bucketName, id));
    }

    @Override
    public GetBucketMetricsConfigurationResult getBucketMetricsConfiguration(GetBucketMetricsConfigurationRequest getBucketMetricsConfigurationRequest) throws AmazonServiceException, SdkClientException {
        getBucketMetricsConfigurationRequest = this.beforeClientExecution(getBucketMetricsConfigurationRequest);
        this.rejectNull(getBucketMetricsConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(getBucketMetricsConfigurationRequest.getBucketName(), "BucketName");
        String id = ValidationUtils.assertStringNotEmpty(getBucketMetricsConfigurationRequest.getId(), "Metrics Id");
        Request<GetBucketMetricsConfigurationRequest> request = this.createRequest(bucketName, null, getBucketMetricsConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketMetricsConfiguration");
        request.addParameter("metrics", null);
        request.addParameter("id", id);
        return this.invoke(request, new Unmarshallers.GetBucketMetricsConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public SetBucketMetricsConfigurationResult setBucketMetricsConfiguration(String bucketName, MetricsConfiguration metricsConfiguration) throws AmazonServiceException, SdkClientException {
        return this.setBucketMetricsConfiguration(new SetBucketMetricsConfigurationRequest(bucketName, metricsConfiguration));
    }

    @Override
    public SetBucketMetricsConfigurationResult setBucketMetricsConfiguration(SetBucketMetricsConfigurationRequest setBucketMetricsConfigurationRequest) throws AmazonServiceException, SdkClientException {
        setBucketMetricsConfigurationRequest = this.beforeClientExecution(setBucketMetricsConfigurationRequest);
        new SetBucketMetricsConfigurationRequest();
        this.rejectNull(setBucketMetricsConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(setBucketMetricsConfigurationRequest.getBucketName(), "BucketName");
        MetricsConfiguration metricsConfiguration = ValidationUtils.assertNotNull(setBucketMetricsConfigurationRequest.getMetricsConfiguration(), "Metrics Configuration");
        String id = ValidationUtils.assertNotNull(metricsConfiguration.getId(), "Metrics Id");
        Request<SetBucketMetricsConfigurationRequest> request = this.createRequest(bucketName, null, setBucketMetricsConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketMetricsConfiguration");
        request.addParameter("metrics", null);
        request.addParameter("id", id);
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(metricsConfiguration);
        request.addHeader("Content-Length", String.valueOf(bytes.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(bytes));
        return this.invoke(request, new Unmarshallers.SetBucketMetricsConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public ListBucketMetricsConfigurationsResult listBucketMetricsConfigurations(ListBucketMetricsConfigurationsRequest listBucketMetricsConfigurationsRequest) throws AmazonServiceException, SdkClientException {
        listBucketMetricsConfigurationsRequest = this.beforeClientExecution(listBucketMetricsConfigurationsRequest);
        this.rejectNull(listBucketMetricsConfigurationsRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(listBucketMetricsConfigurationsRequest.getBucketName(), "BucketName");
        Request<ListBucketMetricsConfigurationsRequest> request = this.createRequest(bucketName, null, listBucketMetricsConfigurationsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBucketMetricsConfigurations");
        request.addParameter("metrics", null);
        AmazonS3Client.addParameterIfNotNull(request, "continuation-token", listBucketMetricsConfigurationsRequest.getContinuationToken());
        return this.invoke(request, new Unmarshallers.ListBucketMetricsConfigurationsUnmarshaller(), bucketName, null);
    }

    @Override
    public DeleteBucketOwnershipControlsResult deleteBucketOwnershipControls(DeleteBucketOwnershipControlsRequest deleteBucketOwnershipControlsRequest) throws AmazonServiceException, SdkClientException {
        deleteBucketOwnershipControlsRequest = this.beforeClientExecution(deleteBucketOwnershipControlsRequest);
        this.rejectNull(deleteBucketOwnershipControlsRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(deleteBucketOwnershipControlsRequest.getBucketName(), "BucketName");
        Request<DeleteBucketOwnershipControlsRequest> request = this.createRequest(bucketName, null, deleteBucketOwnershipControlsRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketOwnershipControls");
        request.addParameter("ownershipControls", null);
        return this.invoke(request, new Unmarshallers.DeleteBucketOwnershipControlsUnmarshaller(), bucketName, null);
    }

    @Override
    public GetBucketOwnershipControlsResult getBucketOwnershipControls(GetBucketOwnershipControlsRequest getBucketOwnershipControlsRequest) throws AmazonServiceException, SdkClientException {
        getBucketOwnershipControlsRequest = this.beforeClientExecution(getBucketOwnershipControlsRequest);
        this.rejectNull(getBucketOwnershipControlsRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(getBucketOwnershipControlsRequest.getBucketName(), "BucketName");
        Request<GetBucketOwnershipControlsRequest> request = this.createRequest(bucketName, null, getBucketOwnershipControlsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketOwnershipControls");
        request.addParameter("ownershipControls", null);
        return this.invoke(request, new Unmarshallers.GetBucketOwnershipControlsUnmarshaller(), bucketName, null);
    }

    @Override
    public SetBucketOwnershipControlsResult setBucketOwnershipControls(String bucketName, OwnershipControls ownershipControls) throws AmazonServiceException, SdkClientException {
        return this.setBucketOwnershipControls(new SetBucketOwnershipControlsRequest(bucketName, ownershipControls));
    }

    @Override
    public SetBucketOwnershipControlsResult setBucketOwnershipControls(SetBucketOwnershipControlsRequest setBucketOwnershipControlsRequest) throws AmazonServiceException, SdkClientException {
        setBucketOwnershipControlsRequest = this.beforeClientExecution(setBucketOwnershipControlsRequest);
        this.rejectNull(setBucketOwnershipControlsRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(setBucketOwnershipControlsRequest.getBucketName(), "BucketName");
        OwnershipControls ownershipControls = ValidationUtils.assertNotNull(setBucketOwnershipControlsRequest.getOwnershipControls(), "OwnershipControls");
        Request<SetBucketOwnershipControlsRequest> request = this.createRequest(bucketName, null, setBucketOwnershipControlsRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketOwnershipControls");
        request.addParameter("ownershipControls", null);
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(ownershipControls);
        this.setContent(request, bytes, "application/xml", true);
        return this.invoke(request, new Unmarshallers.SetBucketOwnershipControlsUnmarshaller(), bucketName, null);
    }

    @Override
    public DeleteBucketAnalyticsConfigurationResult deleteBucketAnalyticsConfiguration(String bucketName, String id) throws AmazonServiceException, SdkClientException {
        return this.deleteBucketAnalyticsConfiguration(new DeleteBucketAnalyticsConfigurationRequest(bucketName, id));
    }

    @Override
    public DeleteBucketAnalyticsConfigurationResult deleteBucketAnalyticsConfiguration(DeleteBucketAnalyticsConfigurationRequest deleteBucketAnalyticsConfigurationRequest) throws AmazonServiceException, SdkClientException {
        deleteBucketAnalyticsConfigurationRequest = this.beforeClientExecution(deleteBucketAnalyticsConfigurationRequest);
        this.rejectNull(deleteBucketAnalyticsConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(deleteBucketAnalyticsConfigurationRequest.getBucketName(), "BucketName");
        String id = ValidationUtils.assertStringNotEmpty(deleteBucketAnalyticsConfigurationRequest.getId(), "Analytics Id");
        Request<DeleteBucketAnalyticsConfigurationRequest> request = this.createRequest(bucketName, null, deleteBucketAnalyticsConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketAnalyticsConfiguration");
        request.addParameter("analytics", null);
        request.addParameter("id", id);
        return this.invoke(request, new Unmarshallers.DeleteBucketAnalyticsConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public GetBucketAnalyticsConfigurationResult getBucketAnalyticsConfiguration(String bucketName, String id) throws AmazonServiceException, SdkClientException {
        return this.getBucketAnalyticsConfiguration(new GetBucketAnalyticsConfigurationRequest(bucketName, id));
    }

    @Override
    public GetBucketAnalyticsConfigurationResult getBucketAnalyticsConfiguration(GetBucketAnalyticsConfigurationRequest getBucketAnalyticsConfigurationRequest) throws AmazonServiceException, SdkClientException {
        getBucketAnalyticsConfigurationRequest = this.beforeClientExecution(getBucketAnalyticsConfigurationRequest);
        this.rejectNull(getBucketAnalyticsConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(getBucketAnalyticsConfigurationRequest.getBucketName(), "BucketName");
        String id = ValidationUtils.assertStringNotEmpty(getBucketAnalyticsConfigurationRequest.getId(), "Analytics Id");
        Request<GetBucketAnalyticsConfigurationRequest> request = this.createRequest(bucketName, null, getBucketAnalyticsConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketAnalyticsConfiguration");
        request.addParameter("analytics", null);
        request.addParameter("id", id);
        return this.invoke(request, new Unmarshallers.GetBucketAnalyticsConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public SetBucketAnalyticsConfigurationResult setBucketAnalyticsConfiguration(String bucketName, AnalyticsConfiguration analyticsConfiguration) throws AmazonServiceException, SdkClientException {
        return this.setBucketAnalyticsConfiguration(new SetBucketAnalyticsConfigurationRequest(bucketName, analyticsConfiguration));
    }

    @Override
    public SetBucketAnalyticsConfigurationResult setBucketAnalyticsConfiguration(SetBucketAnalyticsConfigurationRequest setBucketAnalyticsConfigurationRequest) throws AmazonServiceException, SdkClientException {
        setBucketAnalyticsConfigurationRequest = this.beforeClientExecution(setBucketAnalyticsConfigurationRequest);
        this.rejectNull(setBucketAnalyticsConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(setBucketAnalyticsConfigurationRequest.getBucketName(), "BucketName");
        AnalyticsConfiguration analyticsConfiguration = ValidationUtils.assertNotNull(setBucketAnalyticsConfigurationRequest.getAnalyticsConfiguration(), "Analytics Configuration");
        String id = ValidationUtils.assertNotNull(analyticsConfiguration.getId(), "Analytics Id");
        Request<SetBucketAnalyticsConfigurationRequest> request = this.createRequest(bucketName, null, setBucketAnalyticsConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketAnalyticsConfiguration");
        request.addParameter("analytics", null);
        request.addParameter("id", id);
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(analyticsConfiguration);
        request.addHeader("Content-Length", String.valueOf(bytes.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(bytes));
        return this.invoke(request, new Unmarshallers.SetBucketAnalyticsConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public ListBucketAnalyticsConfigurationsResult listBucketAnalyticsConfigurations(ListBucketAnalyticsConfigurationsRequest listBucketAnalyticsConfigurationsRequest) throws AmazonServiceException, SdkClientException {
        listBucketAnalyticsConfigurationsRequest = this.beforeClientExecution(listBucketAnalyticsConfigurationsRequest);
        this.rejectNull(listBucketAnalyticsConfigurationsRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(listBucketAnalyticsConfigurationsRequest.getBucketName(), "BucketName");
        Request<ListBucketAnalyticsConfigurationsRequest> request = this.createRequest(bucketName, null, listBucketAnalyticsConfigurationsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBucketAnalyticsConfigurations");
        request.addParameter("analytics", null);
        AmazonS3Client.addParameterIfNotNull(request, "continuation-token", listBucketAnalyticsConfigurationsRequest.getContinuationToken());
        return this.invoke(request, new Unmarshallers.ListBucketAnalyticsConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public DeleteBucketIntelligentTieringConfigurationResult deleteBucketIntelligentTieringConfiguration(String bucketName, String id) throws AmazonServiceException, SdkClientException {
        return this.deleteBucketIntelligentTieringConfiguration(new DeleteBucketIntelligentTieringConfigurationRequest(bucketName, id));
    }

    @Override
    public DeleteBucketIntelligentTieringConfigurationResult deleteBucketIntelligentTieringConfiguration(DeleteBucketIntelligentTieringConfigurationRequest deleteBucketIntelligentTieringConfigurationRequest) throws AmazonServiceException, SdkClientException {
        deleteBucketIntelligentTieringConfigurationRequest = this.beforeClientExecution(deleteBucketIntelligentTieringConfigurationRequest);
        this.rejectNull(deleteBucketIntelligentTieringConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(deleteBucketIntelligentTieringConfigurationRequest.getBucketName(), "BucketName");
        String id = ValidationUtils.assertStringNotEmpty(deleteBucketIntelligentTieringConfigurationRequest.getId(), "IntelligentTiering Id");
        Request<DeleteBucketIntelligentTieringConfigurationRequest> request = this.createRequest(bucketName, null, deleteBucketIntelligentTieringConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketIntelligentTieringConfiguration");
        request.addParameter("intelligent-tiering", null);
        request.addParameter("id", id);
        return this.invoke(request, new Unmarshallers.DeleteBucketIntelligenTieringConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public GetBucketIntelligentTieringConfigurationResult getBucketIntelligentTieringConfiguration(String bucketName, String id) throws AmazonServiceException, SdkClientException {
        return this.getBucketIntelligentTieringConfiguration(new GetBucketIntelligentTieringConfigurationRequest(bucketName, id));
    }

    @Override
    public GetBucketIntelligentTieringConfigurationResult getBucketIntelligentTieringConfiguration(GetBucketIntelligentTieringConfigurationRequest getBucketIntelligentTieringConfigurationRequest) throws AmazonServiceException, SdkClientException {
        getBucketIntelligentTieringConfigurationRequest = this.beforeClientExecution(getBucketIntelligentTieringConfigurationRequest);
        this.rejectNull(getBucketIntelligentTieringConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(getBucketIntelligentTieringConfigurationRequest.getBucketName(), "BucketName");
        String id = ValidationUtils.assertStringNotEmpty(getBucketIntelligentTieringConfigurationRequest.getId(), "IntelligentTiering Id");
        Request<GetBucketIntelligentTieringConfigurationRequest> request = this.createRequest(bucketName, null, getBucketIntelligentTieringConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketIntelligentTieringConfiguration");
        request.addParameter("intelligent-tiering", null);
        request.addParameter("id", id);
        return this.invoke(request, new Unmarshallers.GetBucketIntelligenTieringConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public SetBucketIntelligentTieringConfigurationResult setBucketIntelligentTieringConfiguration(String bucketName, IntelligentTieringConfiguration intelligentTieringConfiguration) throws AmazonServiceException, SdkClientException {
        return this.setBucketIntelligentTieringConfiguration(new SetBucketIntelligentTieringConfigurationRequest(bucketName, intelligentTieringConfiguration));
    }

    @Override
    public SetBucketIntelligentTieringConfigurationResult setBucketIntelligentTieringConfiguration(SetBucketIntelligentTieringConfigurationRequest setBucketIntelligentTieringConfigurationRequest) throws AmazonServiceException, SdkClientException {
        setBucketIntelligentTieringConfigurationRequest = this.beforeClientExecution(setBucketIntelligentTieringConfigurationRequest);
        this.rejectNull(setBucketIntelligentTieringConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(setBucketIntelligentTieringConfigurationRequest.getBucketName(), "BucketName");
        IntelligentTieringConfiguration intelligentTieringConfiguration = ValidationUtils.assertNotNull(setBucketIntelligentTieringConfigurationRequest.getIntelligentTierinConfiguration(), "Intelligent Tiering Configuration");
        String id = ValidationUtils.assertNotNull(intelligentTieringConfiguration.getId(), "Intelligent Tiering Id");
        Request<SetBucketIntelligentTieringConfigurationRequest> request = this.createRequest(bucketName, null, setBucketIntelligentTieringConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketIntelligentTieringConfiguration");
        request.addParameter("intelligent-tiering", null);
        request.addParameter("id", id);
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(intelligentTieringConfiguration);
        request.addHeader("Content-Length", String.valueOf(bytes.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(bytes));
        return this.invoke(request, new Unmarshallers.SetBucketIntelligentTieringConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public ListBucketIntelligentTieringConfigurationsResult listBucketIntelligentTieringConfigurations(ListBucketIntelligentTieringConfigurationsRequest listBucketIntelligentTieringConfigurationsRequest) throws AmazonServiceException, SdkClientException {
        listBucketIntelligentTieringConfigurationsRequest = this.beforeClientExecution(listBucketIntelligentTieringConfigurationsRequest);
        this.rejectNull(listBucketIntelligentTieringConfigurationsRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(listBucketIntelligentTieringConfigurationsRequest.getBucketName(), "BucketName");
        Request<ListBucketIntelligentTieringConfigurationsRequest> request = this.createRequest(bucketName, null, listBucketIntelligentTieringConfigurationsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBucketIntelligentTieringConfigurations");
        request.addParameter("intelligent-tiering", null);
        AmazonS3Client.addParameterIfNotNull(request, "continuation-token", listBucketIntelligentTieringConfigurationsRequest.getContinuationToken());
        return this.invoke(request, new Unmarshallers.ListBucketIntelligenTieringConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public DeleteBucketInventoryConfigurationResult deleteBucketInventoryConfiguration(String bucketName, String id) throws AmazonServiceException, SdkClientException {
        return this.deleteBucketInventoryConfiguration(new DeleteBucketInventoryConfigurationRequest(bucketName, id));
    }

    @Override
    public DeleteBucketInventoryConfigurationResult deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest deleteBucketInventoryConfigurationRequest) throws AmazonServiceException, SdkClientException {
        deleteBucketInventoryConfigurationRequest = this.beforeClientExecution(deleteBucketInventoryConfigurationRequest);
        this.rejectNull(deleteBucketInventoryConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(deleteBucketInventoryConfigurationRequest.getBucketName(), "BucketName");
        String id = ValidationUtils.assertStringNotEmpty(deleteBucketInventoryConfigurationRequest.getId(), "Inventory id");
        Request<DeleteBucketInventoryConfigurationRequest> request = this.createRequest(bucketName, null, deleteBucketInventoryConfigurationRequest, HttpMethodName.DELETE);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteBucketInventoryConfiguration");
        request.addParameter("inventory", null);
        request.addParameter("id", id);
        return this.invoke(request, new Unmarshallers.DeleteBucketInventoryConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(String bucketName, String id) throws AmazonServiceException, SdkClientException {
        return this.getBucketInventoryConfiguration(new GetBucketInventoryConfigurationRequest(bucketName, id));
    }

    @Override
    public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest getBucketInventoryConfigurationRequest) throws AmazonServiceException, SdkClientException {
        getBucketInventoryConfigurationRequest = this.beforeClientExecution(getBucketInventoryConfigurationRequest);
        this.rejectNull(getBucketInventoryConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(getBucketInventoryConfigurationRequest.getBucketName(), "BucketName");
        String id = ValidationUtils.assertStringNotEmpty(getBucketInventoryConfigurationRequest.getId(), "Inventory id");
        Request<GetBucketInventoryConfigurationRequest> request = this.createRequest(bucketName, null, getBucketInventoryConfigurationRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetBucketInventoryConfiguration");
        request.addParameter("inventory", null);
        request.addParameter("id", id);
        return this.invoke(request, new Unmarshallers.GetBucketInventoryConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public SetBucketInventoryConfigurationResult setBucketInventoryConfiguration(String bucketName, InventoryConfiguration inventoryConfiguration) throws AmazonServiceException, SdkClientException {
        return this.setBucketInventoryConfiguration(new SetBucketInventoryConfigurationRequest(bucketName, inventoryConfiguration));
    }

    @Override
    public SetBucketInventoryConfigurationResult setBucketInventoryConfiguration(SetBucketInventoryConfigurationRequest setBucketInventoryConfigurationRequest) throws AmazonServiceException, SdkClientException {
        setBucketInventoryConfigurationRequest = this.beforeClientExecution(setBucketInventoryConfigurationRequest);
        this.rejectNull(setBucketInventoryConfigurationRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(setBucketInventoryConfigurationRequest.getBucketName(), "BucketName");
        InventoryConfiguration inventoryConfiguration = ValidationUtils.assertNotNull(setBucketInventoryConfigurationRequest.getInventoryConfiguration(), "InventoryConfiguration");
        String id = ValidationUtils.assertNotNull(inventoryConfiguration.getId(), "Inventory id");
        Request<SetBucketInventoryConfigurationRequest> request = this.createRequest(bucketName, null, setBucketInventoryConfigurationRequest, HttpMethodName.PUT);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutBucketInventoryConfiguration");
        request.addParameter("inventory", null);
        request.addParameter("id", id);
        byte[] bytes = bucketConfigurationXmlFactory.convertToXmlByteArray(inventoryConfiguration);
        request.addHeader("Content-Length", String.valueOf(bytes.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(bytes));
        return this.invoke(request, new Unmarshallers.SetBucketInventoryConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public ListBucketInventoryConfigurationsResult listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest listBucketInventoryConfigurationsRequest) throws AmazonServiceException, SdkClientException {
        listBucketInventoryConfigurationsRequest = this.beforeClientExecution(listBucketInventoryConfigurationsRequest);
        this.rejectNull(listBucketInventoryConfigurationsRequest, "The request cannot be null");
        String bucketName = ValidationUtils.assertStringNotEmpty(listBucketInventoryConfigurationsRequest.getBucketName(), "BucketName");
        Request<ListBucketInventoryConfigurationsRequest> request = this.createRequest(bucketName, null, listBucketInventoryConfigurationsRequest, HttpMethodName.GET);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListBucketInventoryConfigurations");
        request.addParameter("inventory", null);
        AmazonS3Client.addParameterIfNotNull(request, "continuation-token", listBucketInventoryConfigurationsRequest.getContinuationToken());
        return this.invoke(request, new Unmarshallers.ListBucketInventoryConfigurationsUnmarshaller(), bucketName, null);
    }

    URI resolveServiceEndpointFromBucketName(String bucketName) {
        if (this.getSignerRegion() != null || this.isSignerOverridden()) {
            return this.endpoint;
        }
        String regionStr = this.fetchRegionFromCache(bucketName);
        return this.resolveServiceEndpointFromRegion(regionStr);
    }

    private URI resolveServiceEndpointFromRegion(String regionName) {
        com.amazonaws.regions.Region region = RegionUtils.getRegion(regionName);
        if (region == null) {
            log.warn((Object)("Region information for " + regionName + " is not available. Please upgrade to latest version of AWS Java SDK"));
        }
        return region != null ? RuntimeHttpUtils.toUri(region.getServiceEndpoint(S3_SERVICE_NAME), this.clientConfiguration) : this.endpoint;
    }

    private String fetchRegionFromCache(String bucketName) {
        String bucketRegion = bucketRegionCache.get(bucketName);
        if (bucketRegion == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Bucket region cache doesn't have an entry for " + bucketName + ". Trying to get bucket region from Amazon S3."));
            }
            if ((bucketRegion = this.getBucketRegionViaHeadRequest(bucketName)) != null) {
                bucketRegionCache.put(bucketName, bucketRegion);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Region for " + bucketName + " is " + bucketRegion));
        }
        return bucketRegion;
    }

    private String getBucketRegionViaHeadRequest(String bucketName) {
        String bucketRegion;
        block3: {
            bucketRegion = null;
            try {
                HeadBucketRequest headBucketRequest = this.beforeClientExecution(new HeadBucketRequest(bucketName));
                Request<HeadBucketRequest> request = this.createRequest(bucketName, null, headBucketRequest, HttpMethodName.HEAD);
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "HeadBucket");
                HeadBucketResult result = this.invoke(request, new HeadBucketResultHandler(), bucketName, null, true);
                bucketRegion = result.getBucketRegion();
            }
            catch (AmazonS3Exception exception) {
                if (exception.getAdditionalDetails() == null) break block3;
                bucketRegion = exception.getAdditionalDetails().get("x-amz-bucket-region");
            }
        }
        if (bucketRegion == null && log.isDebugEnabled()) {
            log.debug((Object)("Not able to derive region of the " + bucketName + " from the HEAD Bucket requests."));
        }
        return bucketRegion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AmazonS3Waiters waiters() {
        if (this.waiters == null) {
            AmazonS3Client amazonS3Client = this;
            synchronized (amazonS3Client) {
                if (this.waiters == null) {
                    this.waiters = new AmazonS3Waiters(this);
                }
            }
        }
        return this.waiters;
    }

    private String urlEncodeTags(ObjectTagging tagging) {
        if (tagging == null || tagging.getTagSet() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<Tag> tagIter = tagging.getTagSet().iterator();
        while (tagIter.hasNext()) {
            Tag tag = tagIter.next();
            sb.append(SdkHttpUtils.urlEncode(tag.getKey(), false)).append('=').append(SdkHttpUtils.urlEncode(tag.getValue(), false));
            if (!tagIter.hasNext()) continue;
            sb.append("&");
        }
        return sb.toString();
    }

    private void setContent(Request<?> request, byte[] content, String contentType, boolean setMd5) {
        request.setContent(new ByteArrayInputStream(content));
        request.addHeader("Content-Length", Integer.toString(content.length));
        request.addHeader("Content-Type", contentType);
        if (setMd5) {
            try {
                byte[] md5 = Md5Utils.computeMD5Hash(content);
                String md5Base64 = BinaryUtils.toBase64(md5);
                request.addHeader("Content-MD5", md5Base64);
            }
            catch (Exception e) {
                throw new AmazonClientException("Couldn't compute md5 sum", e);
            }
        }
    }

    private Request<RestoreObjectRequest> createRestoreObjectRequest(RestoreObjectRequest restoreObjectRequest) {
        String bucketName = restoreObjectRequest.getBucketName();
        String key = restoreObjectRequest.getKey();
        String versionId = restoreObjectRequest.getVersionId();
        Request<RestoreObjectRequest> request = this.createRequest(bucketName, key, restoreObjectRequest, HttpMethodName.POST);
        request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "RestoreObject");
        request.addParameter("restore", null);
        if (versionId != null) {
            request.addParameter("versionId", versionId);
        }
        AmazonS3Client.populateRequesterPaysHeader(request, restoreObjectRequest.isRequesterPays());
        byte[] content = RequestXmlFactory.convertToXmlByteArray(restoreObjectRequest);
        this.setContent(request, content, "application/xml", true);
        return request;
    }

    private static void populateObjectLockHeaders(Request<?> request, String mode, Date retainUntil, String status) {
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-object-lock-mode", mode);
        if (retainUntil != null) {
            request.addHeader("x-amz-object-lock-retain-until-date", ServiceUtils.formatIso8601Date(retainUntil));
        }
        AmazonS3Client.addHeaderIfNotNull(request, "x-amz-object-lock-legal-hold", status);
    }

    private PresignedUrlUploadResult createPresignedUrlUploadResult(ObjectMetadata metadata, String contentMd5) {
        PresignedUrlUploadResult result = new PresignedUrlUploadResult();
        result.setMetadata(metadata);
        result.setContentMd5(contentMd5);
        return result;
    }

    private void validateIsTrue(boolean condition, String error, Object ... params) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(error, params));
        }
    }

    static {
        AwsSdkMetrics.addAll(Arrays.asList(S3ServiceMetric.values()));
        SignerFactory.registerSigner(S3_SIGNER, S3Signer.class);
        SignerFactory.registerSigner(S3_V4_SIGNER, AWSS3V4Signer.class);
        bucketConfigurationXmlFactory = new BucketConfigurationXmlFactory();
        requestPaymentConfigurationXmlFactory = new RequestPaymentConfigurationXmlFactory();
        USE_ARN_REGION_RESOLVER = new UseArnRegionResolver();
        REGIONAL_ENDPOINTS_OPTION_RESOLVER = new RegionalEndpointsOptionResolver();
        bucketRegionCache = Collections.synchronizedMap(new LinkedHashMap<String, String>(300, 1.1f, true){
            private static final long serialVersionUID = 23453L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return this.size() > 300;
            }
        });
    }

    private class PresignedUrlUploadStrategy
    implements UploadObjectStrategy<PresignedUrlUploadRequest, PresignedUrlUploadResult> {
        private final URL url;

        private PresignedUrlUploadStrategy(URL url) {
            this.url = url;
        }

        @Override
        public ObjectMetadata invokeServiceCall(Request<PresignedUrlUploadRequest> request) {
            return AmazonS3Client.this.client.execute(request, new S3MetadataResponseHandler(), AmazonS3Client.this.errorResponseHandler, AmazonS3Client.this.createExecutionContext(AmazonWebServiceRequest.NOOP, new NoOpSignerProvider()), AmazonS3Client.this.requestConfigWithSkipAppendUriPath(request)).getAwsResponse();
        }

        @Override
        public PresignedUrlUploadResult createResult(ObjectMetadata metadata, String contentMd5) {
            return AmazonS3Client.this.createPresignedUrlUploadResult(metadata, contentMd5);
        }

        @Override
        public String md5ValidationErrorSuffix() {
            return ", object presigned url: " + this.url;
        }
    }

    private class PutObjectStrategy
    implements UploadObjectStrategy<PutObjectRequest, PutObjectResult> {
        private final String bucketName;
        private final String key;

        private PutObjectStrategy(String bucketName, String key) {
            this.bucketName = bucketName;
            this.key = key;
        }

        @Override
        public ObjectMetadata invokeServiceCall(Request<PutObjectRequest> request) {
            return (ObjectMetadata)AmazonS3Client.this.invoke(request, new S3MetadataResponseHandler(), this.bucketName, this.key);
        }

        @Override
        public PutObjectResult createResult(ObjectMetadata metadata, String contentMd5) {
            PutObjectResult result = AmazonS3Client.createPutObjectResult(metadata);
            result.setContentMd5(contentMd5);
            return result;
        }

        @Override
        public String md5ValidationErrorSuffix() {
            return ", bucketName: " + this.bucketName + ", key: " + this.key;
        }
    }
}

