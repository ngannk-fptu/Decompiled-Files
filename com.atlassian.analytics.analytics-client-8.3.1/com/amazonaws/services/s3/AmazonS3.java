/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.S3ResponseMetadata;
import com.amazonaws.services.s3.internal.S3DirectSpi;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.AccessControlList;
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
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
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
import com.amazonaws.services.s3.model.GetS3AccountOwnerRequest;
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
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.PartListing;
import com.amazonaws.services.s3.model.PresignedUrlDownloadRequest;
import com.amazonaws.services.s3.model.PresignedUrlDownloadResult;
import com.amazonaws.services.s3.model.PresignedUrlUploadRequest;
import com.amazonaws.services.s3.model.PresignedUrlUploadResult;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.RestoreObjectRequest;
import com.amazonaws.services.s3.model.RestoreObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.SelectObjectContentRequest;
import com.amazonaws.services.s3.model.SelectObjectContentResult;
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
import com.amazonaws.services.s3.waiters.AmazonS3Waiters;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

public interface AmazonS3
extends S3DirectSpi {
    public static final String ENDPOINT_PREFIX = "s3";

    public void setEndpoint(String var1);

    public void setRegion(com.amazonaws.regions.Region var1) throws IllegalArgumentException;

    public void setS3ClientOptions(S3ClientOptions var1);

    @Deprecated
    public void changeObjectStorageClass(String var1, String var2, StorageClass var3) throws SdkClientException, AmazonServiceException;

    @Deprecated
    public void setObjectRedirectLocation(String var1, String var2, String var3) throws SdkClientException, AmazonServiceException;

    public ObjectListing listObjects(String var1) throws SdkClientException, AmazonServiceException;

    public ObjectListing listObjects(String var1, String var2) throws SdkClientException, AmazonServiceException;

    public ObjectListing listObjects(ListObjectsRequest var1) throws SdkClientException, AmazonServiceException;

    public ListObjectsV2Result listObjectsV2(String var1) throws SdkClientException, AmazonServiceException;

    public ListObjectsV2Result listObjectsV2(String var1, String var2) throws SdkClientException, AmazonServiceException;

    public ListObjectsV2Result listObjectsV2(ListObjectsV2Request var1) throws SdkClientException, AmazonServiceException;

    public ObjectListing listNextBatchOfObjects(ObjectListing var1) throws SdkClientException, AmazonServiceException;

    public ObjectListing listNextBatchOfObjects(ListNextBatchOfObjectsRequest var1) throws SdkClientException, AmazonServiceException;

    public VersionListing listVersions(String var1, String var2) throws SdkClientException, AmazonServiceException;

    public VersionListing listNextBatchOfVersions(VersionListing var1) throws SdkClientException, AmazonServiceException;

    public VersionListing listNextBatchOfVersions(ListNextBatchOfVersionsRequest var1) throws SdkClientException, AmazonServiceException;

    public VersionListing listVersions(String var1, String var2, String var3, String var4, String var5, Integer var6) throws SdkClientException, AmazonServiceException;

    public VersionListing listVersions(ListVersionsRequest var1) throws SdkClientException, AmazonServiceException;

    public Owner getS3AccountOwner() throws SdkClientException, AmazonServiceException;

    public Owner getS3AccountOwner(GetS3AccountOwnerRequest var1) throws SdkClientException, AmazonServiceException;

    @Deprecated
    public boolean doesBucketExist(String var1) throws SdkClientException, AmazonServiceException;

    public boolean doesBucketExistV2(String var1) throws SdkClientException, AmazonServiceException;

    public HeadBucketResult headBucket(HeadBucketRequest var1) throws SdkClientException, AmazonServiceException;

    public List<Bucket> listBuckets() throws SdkClientException, AmazonServiceException;

    public List<Bucket> listBuckets(ListBucketsRequest var1) throws SdkClientException, AmazonServiceException;

    public String getBucketLocation(String var1) throws SdkClientException, AmazonServiceException;

    public String getBucketLocation(GetBucketLocationRequest var1) throws SdkClientException, AmazonServiceException;

    public Bucket createBucket(CreateBucketRequest var1) throws SdkClientException, AmazonServiceException;

    public Bucket createBucket(String var1) throws SdkClientException, AmazonServiceException;

    @Deprecated
    public Bucket createBucket(String var1, Region var2) throws SdkClientException, AmazonServiceException;

    @Deprecated
    public Bucket createBucket(String var1, String var2) throws SdkClientException, AmazonServiceException;

    public AccessControlList getObjectAcl(String var1, String var2) throws SdkClientException, AmazonServiceException;

    public AccessControlList getObjectAcl(String var1, String var2, String var3) throws SdkClientException, AmazonServiceException;

    public AccessControlList getObjectAcl(GetObjectAclRequest var1) throws SdkClientException, AmazonServiceException;

    public void setObjectAcl(String var1, String var2, AccessControlList var3) throws SdkClientException, AmazonServiceException;

    public void setObjectAcl(String var1, String var2, CannedAccessControlList var3) throws SdkClientException, AmazonServiceException;

    public void setObjectAcl(String var1, String var2, String var3, AccessControlList var4) throws SdkClientException, AmazonServiceException;

    public void setObjectAcl(String var1, String var2, String var3, CannedAccessControlList var4) throws SdkClientException, AmazonServiceException;

    public void setObjectAcl(SetObjectAclRequest var1) throws SdkClientException, AmazonServiceException;

    public AccessControlList getBucketAcl(String var1) throws SdkClientException, AmazonServiceException;

    public void setBucketAcl(SetBucketAclRequest var1) throws SdkClientException, AmazonServiceException;

    public AccessControlList getBucketAcl(GetBucketAclRequest var1) throws SdkClientException, AmazonServiceException;

    public void setBucketAcl(String var1, AccessControlList var2) throws SdkClientException, AmazonServiceException;

    public void setBucketAcl(String var1, CannedAccessControlList var2) throws SdkClientException, AmazonServiceException;

    public ObjectMetadata getObjectMetadata(String var1, String var2) throws SdkClientException, AmazonServiceException;

    public ObjectMetadata getObjectMetadata(GetObjectMetadataRequest var1) throws SdkClientException, AmazonServiceException;

    public S3Object getObject(String var1, String var2) throws SdkClientException, AmazonServiceException;

    @Override
    public S3Object getObject(GetObjectRequest var1) throws SdkClientException, AmazonServiceException;

    @Override
    public ObjectMetadata getObject(GetObjectRequest var1, File var2) throws SdkClientException, AmazonServiceException;

    public String getObjectAsString(String var1, String var2) throws AmazonServiceException, SdkClientException;

    public GetObjectTaggingResult getObjectTagging(GetObjectTaggingRequest var1);

    public SetObjectTaggingResult setObjectTagging(SetObjectTaggingRequest var1);

    public DeleteObjectTaggingResult deleteObjectTagging(DeleteObjectTaggingRequest var1);

    public void deleteBucket(DeleteBucketRequest var1) throws SdkClientException, AmazonServiceException;

    public void deleteBucket(String var1) throws SdkClientException, AmazonServiceException;

    @Override
    public PutObjectResult putObject(PutObjectRequest var1) throws SdkClientException, AmazonServiceException;

    public PutObjectResult putObject(String var1, String var2, File var3) throws SdkClientException, AmazonServiceException;

    public PutObjectResult putObject(String var1, String var2, InputStream var3, ObjectMetadata var4) throws SdkClientException, AmazonServiceException;

    public PutObjectResult putObject(String var1, String var2, String var3) throws AmazonServiceException, SdkClientException;

    public CopyObjectResult copyObject(String var1, String var2, String var3, String var4) throws SdkClientException, AmazonServiceException;

    public CopyObjectResult copyObject(CopyObjectRequest var1) throws SdkClientException, AmazonServiceException;

    @Override
    public CopyPartResult copyPart(CopyPartRequest var1) throws SdkClientException, AmazonServiceException;

    public void deleteObject(String var1, String var2) throws SdkClientException, AmazonServiceException;

    public void deleteObject(DeleteObjectRequest var1) throws SdkClientException, AmazonServiceException;

    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest var1) throws SdkClientException, AmazonServiceException;

    public void deleteVersion(String var1, String var2, String var3) throws SdkClientException, AmazonServiceException;

    public void deleteVersion(DeleteVersionRequest var1) throws SdkClientException, AmazonServiceException;

    public BucketLoggingConfiguration getBucketLoggingConfiguration(String var1) throws SdkClientException, AmazonServiceException;

    public BucketLoggingConfiguration getBucketLoggingConfiguration(GetBucketLoggingConfigurationRequest var1) throws SdkClientException, AmazonServiceException;

    public void setBucketLoggingConfiguration(SetBucketLoggingConfigurationRequest var1) throws SdkClientException, AmazonServiceException;

    public BucketVersioningConfiguration getBucketVersioningConfiguration(String var1) throws SdkClientException, AmazonServiceException;

    public BucketVersioningConfiguration getBucketVersioningConfiguration(GetBucketVersioningConfigurationRequest var1) throws SdkClientException, AmazonServiceException;

    public void setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest var1) throws SdkClientException, AmazonServiceException;

    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(String var1);

    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest var1);

    public void setBucketLifecycleConfiguration(String var1, BucketLifecycleConfiguration var2);

    public void setBucketLifecycleConfiguration(SetBucketLifecycleConfigurationRequest var1);

    public void deleteBucketLifecycleConfiguration(String var1);

    public void deleteBucketLifecycleConfiguration(DeleteBucketLifecycleConfigurationRequest var1);

    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(String var1);

    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(GetBucketCrossOriginConfigurationRequest var1);

    public void setBucketCrossOriginConfiguration(String var1, BucketCrossOriginConfiguration var2);

    public void setBucketCrossOriginConfiguration(SetBucketCrossOriginConfigurationRequest var1);

    public void deleteBucketCrossOriginConfiguration(String var1);

    public void deleteBucketCrossOriginConfiguration(DeleteBucketCrossOriginConfigurationRequest var1);

    public BucketTaggingConfiguration getBucketTaggingConfiguration(String var1);

    public BucketTaggingConfiguration getBucketTaggingConfiguration(GetBucketTaggingConfigurationRequest var1);

    public void setBucketTaggingConfiguration(String var1, BucketTaggingConfiguration var2);

    public void setBucketTaggingConfiguration(SetBucketTaggingConfigurationRequest var1);

    public void deleteBucketTaggingConfiguration(String var1);

    public void deleteBucketTaggingConfiguration(DeleteBucketTaggingConfigurationRequest var1);

    public BucketNotificationConfiguration getBucketNotificationConfiguration(String var1) throws SdkClientException, AmazonServiceException;

    public BucketNotificationConfiguration getBucketNotificationConfiguration(GetBucketNotificationConfigurationRequest var1) throws SdkClientException, AmazonServiceException;

    public void setBucketNotificationConfiguration(SetBucketNotificationConfigurationRequest var1) throws SdkClientException, AmazonServiceException;

    public void setBucketNotificationConfiguration(String var1, BucketNotificationConfiguration var2) throws SdkClientException, AmazonServiceException;

    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(String var1) throws SdkClientException, AmazonServiceException;

    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(GetBucketWebsiteConfigurationRequest var1) throws SdkClientException, AmazonServiceException;

    public void setBucketWebsiteConfiguration(String var1, BucketWebsiteConfiguration var2) throws SdkClientException, AmazonServiceException;

    public void setBucketWebsiteConfiguration(SetBucketWebsiteConfigurationRequest var1) throws SdkClientException, AmazonServiceException;

    public void deleteBucketWebsiteConfiguration(String var1) throws SdkClientException, AmazonServiceException;

    public void deleteBucketWebsiteConfiguration(DeleteBucketWebsiteConfigurationRequest var1) throws SdkClientException, AmazonServiceException;

    public BucketPolicy getBucketPolicy(String var1) throws SdkClientException, AmazonServiceException;

    public BucketPolicy getBucketPolicy(GetBucketPolicyRequest var1) throws SdkClientException, AmazonServiceException;

    public void setBucketPolicy(String var1, String var2) throws SdkClientException, AmazonServiceException;

    public void setBucketPolicy(SetBucketPolicyRequest var1) throws SdkClientException, AmazonServiceException;

    public void deleteBucketPolicy(String var1) throws SdkClientException, AmazonServiceException;

    public void deleteBucketPolicy(DeleteBucketPolicyRequest var1) throws SdkClientException, AmazonServiceException;

    public URL generatePresignedUrl(String var1, String var2, Date var3) throws SdkClientException;

    public URL generatePresignedUrl(String var1, String var2, Date var3, HttpMethod var4) throws SdkClientException;

    public URL generatePresignedUrl(GeneratePresignedUrlRequest var1) throws SdkClientException;

    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest var1) throws SdkClientException, AmazonServiceException;

    @Override
    public UploadPartResult uploadPart(UploadPartRequest var1) throws SdkClientException, AmazonServiceException;

    public PartListing listParts(ListPartsRequest var1) throws SdkClientException, AmazonServiceException;

    @Override
    public void abortMultipartUpload(AbortMultipartUploadRequest var1) throws SdkClientException, AmazonServiceException;

    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest var1) throws SdkClientException, AmazonServiceException;

    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest var1) throws SdkClientException, AmazonServiceException;

    public S3ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest var1);

    @Deprecated
    public void restoreObject(RestoreObjectRequest var1) throws AmazonServiceException;

    public RestoreObjectResult restoreObjectV2(RestoreObjectRequest var1) throws AmazonServiceException;

    @Deprecated
    public void restoreObject(String var1, String var2, int var3) throws AmazonServiceException;

    public void enableRequesterPays(String var1) throws AmazonServiceException, SdkClientException;

    public void disableRequesterPays(String var1) throws AmazonServiceException, SdkClientException;

    public boolean isRequesterPaysEnabled(String var1) throws AmazonServiceException, SdkClientException;

    public void setRequestPaymentConfiguration(SetRequestPaymentConfigurationRequest var1);

    public void setBucketReplicationConfiguration(String var1, BucketReplicationConfiguration var2) throws AmazonServiceException, SdkClientException;

    public void setBucketReplicationConfiguration(SetBucketReplicationConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public BucketReplicationConfiguration getBucketReplicationConfiguration(String var1) throws AmazonServiceException, SdkClientException;

    public BucketReplicationConfiguration getBucketReplicationConfiguration(GetBucketReplicationConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public void deleteBucketReplicationConfiguration(String var1) throws AmazonServiceException, SdkClientException;

    public void deleteBucketReplicationConfiguration(DeleteBucketReplicationConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public boolean doesObjectExist(String var1, String var2) throws AmazonServiceException, SdkClientException;

    public BucketAccelerateConfiguration getBucketAccelerateConfiguration(String var1) throws AmazonServiceException, SdkClientException;

    public BucketAccelerateConfiguration getBucketAccelerateConfiguration(GetBucketAccelerateConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public void setBucketAccelerateConfiguration(String var1, BucketAccelerateConfiguration var2) throws AmazonServiceException, SdkClientException;

    public void setBucketAccelerateConfiguration(SetBucketAccelerateConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public DeleteBucketMetricsConfigurationResult deleteBucketMetricsConfiguration(String var1, String var2) throws AmazonServiceException, SdkClientException;

    public DeleteBucketMetricsConfigurationResult deleteBucketMetricsConfiguration(DeleteBucketMetricsConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public GetBucketMetricsConfigurationResult getBucketMetricsConfiguration(String var1, String var2) throws AmazonServiceException, SdkClientException;

    public GetBucketMetricsConfigurationResult getBucketMetricsConfiguration(GetBucketMetricsConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public SetBucketMetricsConfigurationResult setBucketMetricsConfiguration(String var1, MetricsConfiguration var2) throws AmazonServiceException, SdkClientException;

    public SetBucketMetricsConfigurationResult setBucketMetricsConfiguration(SetBucketMetricsConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public ListBucketMetricsConfigurationsResult listBucketMetricsConfigurations(ListBucketMetricsConfigurationsRequest var1) throws AmazonServiceException, SdkClientException;

    public DeleteBucketOwnershipControlsResult deleteBucketOwnershipControls(DeleteBucketOwnershipControlsRequest var1) throws AmazonServiceException, SdkClientException;

    public GetBucketOwnershipControlsResult getBucketOwnershipControls(GetBucketOwnershipControlsRequest var1) throws AmazonServiceException, SdkClientException;

    public SetBucketOwnershipControlsResult setBucketOwnershipControls(String var1, OwnershipControls var2) throws AmazonServiceException, SdkClientException;

    public SetBucketOwnershipControlsResult setBucketOwnershipControls(SetBucketOwnershipControlsRequest var1) throws AmazonServiceException, SdkClientException;

    public DeleteBucketAnalyticsConfigurationResult deleteBucketAnalyticsConfiguration(String var1, String var2) throws AmazonServiceException, SdkClientException;

    public DeleteBucketAnalyticsConfigurationResult deleteBucketAnalyticsConfiguration(DeleteBucketAnalyticsConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public GetBucketAnalyticsConfigurationResult getBucketAnalyticsConfiguration(String var1, String var2) throws AmazonServiceException, SdkClientException;

    public GetBucketAnalyticsConfigurationResult getBucketAnalyticsConfiguration(GetBucketAnalyticsConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public SetBucketAnalyticsConfigurationResult setBucketAnalyticsConfiguration(String var1, AnalyticsConfiguration var2) throws AmazonServiceException, SdkClientException;

    public SetBucketAnalyticsConfigurationResult setBucketAnalyticsConfiguration(SetBucketAnalyticsConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public ListBucketAnalyticsConfigurationsResult listBucketAnalyticsConfigurations(ListBucketAnalyticsConfigurationsRequest var1) throws AmazonServiceException, SdkClientException;

    public DeleteBucketIntelligentTieringConfigurationResult deleteBucketIntelligentTieringConfiguration(String var1, String var2) throws AmazonServiceException, SdkClientException;

    public DeleteBucketIntelligentTieringConfigurationResult deleteBucketIntelligentTieringConfiguration(DeleteBucketIntelligentTieringConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public GetBucketIntelligentTieringConfigurationResult getBucketIntelligentTieringConfiguration(String var1, String var2) throws AmazonServiceException, SdkClientException;

    public GetBucketIntelligentTieringConfigurationResult getBucketIntelligentTieringConfiguration(GetBucketIntelligentTieringConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public SetBucketIntelligentTieringConfigurationResult setBucketIntelligentTieringConfiguration(String var1, IntelligentTieringConfiguration var2) throws AmazonServiceException, SdkClientException;

    public SetBucketIntelligentTieringConfigurationResult setBucketIntelligentTieringConfiguration(SetBucketIntelligentTieringConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public ListBucketIntelligentTieringConfigurationsResult listBucketIntelligentTieringConfigurations(ListBucketIntelligentTieringConfigurationsRequest var1) throws AmazonServiceException, SdkClientException;

    public DeleteBucketInventoryConfigurationResult deleteBucketInventoryConfiguration(String var1, String var2) throws AmazonServiceException, SdkClientException;

    public DeleteBucketInventoryConfigurationResult deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(String var1, String var2) throws AmazonServiceException, SdkClientException;

    public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public SetBucketInventoryConfigurationResult setBucketInventoryConfiguration(String var1, InventoryConfiguration var2) throws AmazonServiceException, SdkClientException;

    public SetBucketInventoryConfigurationResult setBucketInventoryConfiguration(SetBucketInventoryConfigurationRequest var1) throws AmazonServiceException, SdkClientException;

    public ListBucketInventoryConfigurationsResult listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest var1) throws AmazonServiceException, SdkClientException;

    public DeleteBucketEncryptionResult deleteBucketEncryption(String var1) throws AmazonServiceException, SdkClientException;

    public DeleteBucketEncryptionResult deleteBucketEncryption(DeleteBucketEncryptionRequest var1) throws AmazonServiceException, SdkClientException;

    public GetBucketEncryptionResult getBucketEncryption(String var1) throws AmazonServiceException, SdkClientException;

    public GetBucketEncryptionResult getBucketEncryption(GetBucketEncryptionRequest var1) throws AmazonServiceException, SdkClientException;

    public SetBucketEncryptionResult setBucketEncryption(SetBucketEncryptionRequest var1) throws AmazonServiceException, SdkClientException;

    public SetPublicAccessBlockResult setPublicAccessBlock(SetPublicAccessBlockRequest var1);

    public GetPublicAccessBlockResult getPublicAccessBlock(GetPublicAccessBlockRequest var1);

    public DeletePublicAccessBlockResult deletePublicAccessBlock(DeletePublicAccessBlockRequest var1);

    public GetBucketPolicyStatusResult getBucketPolicyStatus(GetBucketPolicyStatusRequest var1);

    public SelectObjectContentResult selectObjectContent(SelectObjectContentRequest var1) throws AmazonServiceException, SdkClientException;

    public SetObjectLegalHoldResult setObjectLegalHold(SetObjectLegalHoldRequest var1);

    public GetObjectLegalHoldResult getObjectLegalHold(GetObjectLegalHoldRequest var1);

    public SetObjectLockConfigurationResult setObjectLockConfiguration(SetObjectLockConfigurationRequest var1);

    public GetObjectLockConfigurationResult getObjectLockConfiguration(GetObjectLockConfigurationRequest var1);

    public SetObjectRetentionResult setObjectRetention(SetObjectRetentionRequest var1);

    public GetObjectRetentionResult getObjectRetention(GetObjectRetentionRequest var1);

    public WriteGetObjectResponseResult writeGetObjectResponse(WriteGetObjectResponseRequest var1);

    public PresignedUrlDownloadResult download(PresignedUrlDownloadRequest var1);

    public void download(PresignedUrlDownloadRequest var1, File var2);

    public PresignedUrlUploadResult upload(PresignedUrlUploadRequest var1);

    public void shutdown();

    public Region getRegion();

    public String getRegionName();

    public URL getUrl(String var1, String var2);

    public AmazonS3Waiters waiters();
}

