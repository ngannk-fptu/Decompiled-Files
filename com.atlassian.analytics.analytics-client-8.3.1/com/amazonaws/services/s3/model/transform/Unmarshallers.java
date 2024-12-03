/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.internal.DeleteObjectsResponse;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketAccelerateConfiguration;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.BucketReplicationConfiguration;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.DeleteBucketAnalyticsConfigurationResult;
import com.amazonaws.services.s3.model.DeleteBucketEncryptionResult;
import com.amazonaws.services.s3.model.DeleteBucketIntelligentTieringConfigurationResult;
import com.amazonaws.services.s3.model.DeleteBucketInventoryConfigurationResult;
import com.amazonaws.services.s3.model.DeleteBucketMetricsConfigurationResult;
import com.amazonaws.services.s3.model.DeleteBucketOwnershipControlsResult;
import com.amazonaws.services.s3.model.DeleteObjectTaggingResult;
import com.amazonaws.services.s3.model.DeletePublicAccessBlockResult;
import com.amazonaws.services.s3.model.GetBucketAnalyticsConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketIntelligentTieringConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketInventoryConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketMetricsConfigurationResult;
import com.amazonaws.services.s3.model.GetBucketOwnershipControlsResult;
import com.amazonaws.services.s3.model.GetObjectLegalHoldResult;
import com.amazonaws.services.s3.model.GetObjectLockConfigurationResult;
import com.amazonaws.services.s3.model.GetObjectRetentionResult;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListBucketAnalyticsConfigurationsResult;
import com.amazonaws.services.s3.model.ListBucketIntelligentTieringConfigurationsResult;
import com.amazonaws.services.s3.model.ListBucketInventoryConfigurationsResult;
import com.amazonaws.services.s3.model.ListBucketMetricsConfigurationsResult;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.PartListing;
import com.amazonaws.services.s3.model.RequestPaymentConfiguration;
import com.amazonaws.services.s3.model.RestoreObjectResult;
import com.amazonaws.services.s3.model.SetBucketAnalyticsConfigurationResult;
import com.amazonaws.services.s3.model.SetBucketEncryptionResult;
import com.amazonaws.services.s3.model.SetBucketIntelligentTieringConfigurationResult;
import com.amazonaws.services.s3.model.SetBucketInventoryConfigurationResult;
import com.amazonaws.services.s3.model.SetBucketMetricsConfigurationResult;
import com.amazonaws.services.s3.model.SetBucketOwnershipControlsResult;
import com.amazonaws.services.s3.model.SetObjectLegalHoldResult;
import com.amazonaws.services.s3.model.SetObjectLockConfigurationResult;
import com.amazonaws.services.s3.model.SetObjectRetentionResult;
import com.amazonaws.services.s3.model.SetObjectTaggingResult;
import com.amazonaws.services.s3.model.SetPublicAccessBlockResult;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.services.s3.model.WriteGetObjectResponseResult;
import com.amazonaws.services.s3.model.transform.XmlResponsesSaxParser;
import com.amazonaws.transform.Unmarshaller;
import java.io.InputStream;
import java.util.List;

public class Unmarshallers {

    public static final class WriteGetObjectResponseResultUnmarshaller
    implements Unmarshaller<WriteGetObjectResponseResult, InputStream> {
        @Override
        public WriteGetObjectResponseResult unmarshall(InputStream inputStream) throws Exception {
            return new WriteGetObjectResponseResult();
        }
    }

    public static final class GetObjectRetentionResultUnmarshaller
    implements Unmarshaller<GetObjectRetentionResult, InputStream> {
        @Override
        public GetObjectRetentionResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseGetObjectRetentionResponse(in).getResult();
        }
    }

    public static final class SetObjectRetentionResultUnmarshaller
    implements Unmarshaller<SetObjectRetentionResult, InputStream> {
        @Override
        public SetObjectRetentionResult unmarshall(InputStream in) throws Exception {
            return new SetObjectRetentionResult();
        }
    }

    public static final class GetObjectLockConfigurationResultUnmarshaller
    implements Unmarshaller<GetObjectLockConfigurationResult, InputStream> {
        @Override
        public GetObjectLockConfigurationResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseGetObjectLockConfigurationResponse(in).getResult();
        }
    }

    public static final class SetObjectLegalHoldResultUnmarshaller
    implements Unmarshaller<SetObjectLegalHoldResult, InputStream> {
        @Override
        public SetObjectLegalHoldResult unmarshall(InputStream in) throws Exception {
            return new SetObjectLegalHoldResult();
        }
    }

    public static final class SetObjectLockConfigurationResultUnmarshaller
    implements Unmarshaller<SetObjectLockConfigurationResult, InputStream> {
        @Override
        public SetObjectLockConfigurationResult unmarshall(InputStream in) throws Exception {
            return new SetObjectLockConfigurationResult();
        }
    }

    public static final class GetObjectLegalHoldResultUnmarshaller
    implements Unmarshaller<GetObjectLegalHoldResult, InputStream> {
        @Override
        public GetObjectLegalHoldResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseGetObjectLegalHoldResponse(in).getResult();
        }
    }

    public static final class RestoreObjectResultUnmarshaller
    implements Unmarshaller<RestoreObjectResult, InputStream> {
        @Override
        public RestoreObjectResult unmarshall(InputStream in) {
            return new RestoreObjectResult();
        }
    }

    public static final class SetBucketInventoryConfigurationUnmarshaller
    implements Unmarshaller<SetBucketInventoryConfigurationResult, InputStream> {
        @Override
        public SetBucketInventoryConfigurationResult unmarshall(InputStream in) throws Exception {
            return new SetBucketInventoryConfigurationResult();
        }
    }

    public static final class DeleteBucketInventoryConfigurationUnmarshaller
    implements Unmarshaller<DeleteBucketInventoryConfigurationResult, InputStream> {
        @Override
        public DeleteBucketInventoryConfigurationResult unmarshall(InputStream in) throws Exception {
            return new DeleteBucketInventoryConfigurationResult();
        }
    }

    public static final class ListBucketInventoryConfigurationsUnmarshaller
    implements Unmarshaller<ListBucketInventoryConfigurationsResult, InputStream> {
        @Override
        public ListBucketInventoryConfigurationsResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseBucketListInventoryConfigurationsResponse(in).getResult();
        }
    }

    public static final class GetBucketInventoryConfigurationUnmarshaller
    implements Unmarshaller<GetBucketInventoryConfigurationResult, InputStream> {
        @Override
        public GetBucketInventoryConfigurationResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseGetBucketInventoryConfigurationResponse(in).getResult();
        }
    }

    public static final class SetBucketOwnershipControlsUnmarshaller
    implements Unmarshaller<SetBucketOwnershipControlsResult, InputStream> {
        @Override
        public SetBucketOwnershipControlsResult unmarshall(InputStream in) throws Exception {
            return new SetBucketOwnershipControlsResult();
        }
    }

    public static final class GetBucketOwnershipControlsUnmarshaller
    implements Unmarshaller<GetBucketOwnershipControlsResult, InputStream> {
        @Override
        public GetBucketOwnershipControlsResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseGetBucketOwnershipControlsResponse(in).getResult();
        }
    }

    public static final class DeleteBucketOwnershipControlsUnmarshaller
    implements Unmarshaller<DeleteBucketOwnershipControlsResult, InputStream> {
        @Override
        public DeleteBucketOwnershipControlsResult unmarshall(InputStream in) throws Exception {
            return new DeleteBucketOwnershipControlsResult();
        }
    }

    public static final class SetBucketMetricsConfigurationUnmarshaller
    implements Unmarshaller<SetBucketMetricsConfigurationResult, InputStream> {
        @Override
        public SetBucketMetricsConfigurationResult unmarshall(InputStream in) throws Exception {
            return new SetBucketMetricsConfigurationResult();
        }
    }

    public static final class DeleteBucketMetricsConfigurationUnmarshaller
    implements Unmarshaller<DeleteBucketMetricsConfigurationResult, InputStream> {
        @Override
        public DeleteBucketMetricsConfigurationResult unmarshall(InputStream in) throws Exception {
            return new DeleteBucketMetricsConfigurationResult();
        }
    }

    public static final class ListBucketMetricsConfigurationsUnmarshaller
    implements Unmarshaller<ListBucketMetricsConfigurationsResult, InputStream> {
        @Override
        public ListBucketMetricsConfigurationsResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseListBucketMetricsConfigurationsResponse(in).getResult();
        }
    }

    public static final class GetBucketMetricsConfigurationUnmarshaller
    implements Unmarshaller<GetBucketMetricsConfigurationResult, InputStream> {
        @Override
        public GetBucketMetricsConfigurationResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseGetBucketMetricsConfigurationResponse(in).getResult();
        }
    }

    public static final class SetBucketIntelligentTieringConfigurationUnmarshaller
    implements Unmarshaller<SetBucketIntelligentTieringConfigurationResult, InputStream> {
        @Override
        public SetBucketIntelligentTieringConfigurationResult unmarshall(InputStream in) throws Exception {
            return new SetBucketIntelligentTieringConfigurationResult();
        }
    }

    public static final class DeleteBucketIntelligenTieringConfigurationUnmarshaller
    implements Unmarshaller<DeleteBucketIntelligentTieringConfigurationResult, InputStream> {
        @Override
        public DeleteBucketIntelligentTieringConfigurationResult unmarshall(InputStream in) throws Exception {
            return new DeleteBucketIntelligentTieringConfigurationResult();
        }
    }

    public static final class ListBucketIntelligenTieringConfigurationUnmarshaller
    implements Unmarshaller<ListBucketIntelligentTieringConfigurationsResult, InputStream> {
        @Override
        public ListBucketIntelligentTieringConfigurationsResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseListBucketIntelligentTieringConfigurationResponse(in).getResult();
        }
    }

    public static final class GetBucketIntelligenTieringConfigurationUnmarshaller
    implements Unmarshaller<GetBucketIntelligentTieringConfigurationResult, InputStream> {
        @Override
        public GetBucketIntelligentTieringConfigurationResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseGetBucketIntelligentTieringConfigurationResponse(in).getResult();
        }
    }

    public static final class SetBucketAnalyticsConfigurationUnmarshaller
    implements Unmarshaller<SetBucketAnalyticsConfigurationResult, InputStream> {
        @Override
        public SetBucketAnalyticsConfigurationResult unmarshall(InputStream in) throws Exception {
            return new SetBucketAnalyticsConfigurationResult();
        }
    }

    public static final class DeleteBucketAnalyticsConfigurationUnmarshaller
    implements Unmarshaller<DeleteBucketAnalyticsConfigurationResult, InputStream> {
        @Override
        public DeleteBucketAnalyticsConfigurationResult unmarshall(InputStream in) throws Exception {
            return new DeleteBucketAnalyticsConfigurationResult();
        }
    }

    public static final class ListBucketAnalyticsConfigurationUnmarshaller
    implements Unmarshaller<ListBucketAnalyticsConfigurationsResult, InputStream> {
        @Override
        public ListBucketAnalyticsConfigurationsResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseListBucketAnalyticsConfigurationResponse(in).getResult();
        }
    }

    public static final class GetBucketAnalyticsConfigurationUnmarshaller
    implements Unmarshaller<GetBucketAnalyticsConfigurationResult, InputStream> {
        @Override
        public GetBucketAnalyticsConfigurationResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseGetBucketAnalyticsConfigurationResponse(in).getResult();
        }
    }

    public static final class DeleteObjectTaggingResponseUnmarshaller
    implements Unmarshaller<DeleteObjectTaggingResult, InputStream> {
        @Override
        public DeleteObjectTaggingResult unmarshall(InputStream in) throws Exception {
            return new DeleteObjectTaggingResult();
        }
    }

    public static final class SetObjectTaggingResponseUnmarshaller
    implements Unmarshaller<SetObjectTaggingResult, InputStream> {
        @Override
        public SetObjectTaggingResult unmarshall(InputStream in) throws Exception {
            return new SetObjectTaggingResult();
        }
    }

    public static final class GetObjectTaggingResponseUnmarshaller
    implements Unmarshaller<GetObjectTaggingResult, InputStream> {
        @Override
        public GetObjectTaggingResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseObjectTaggingResponse(in).getResult();
        }
    }

    public static final class RequestPaymentConfigurationUnmarshaller
    implements Unmarshaller<RequestPaymentConfiguration, InputStream> {
        @Override
        public RequestPaymentConfiguration unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseRequestPaymentConfigurationResponse(in).getConfiguration();
        }
    }

    public static final class BucketCrossOriginConfigurationUnmarshaller
    implements Unmarshaller<BucketCrossOriginConfiguration, InputStream> {
        @Override
        public BucketCrossOriginConfiguration unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseBucketCrossOriginConfigurationResponse(in).getConfiguration();
        }
    }

    public static final class BucketLifecycleConfigurationUnmarshaller
    implements Unmarshaller<BucketLifecycleConfiguration, InputStream> {
        @Override
        public BucketLifecycleConfiguration unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseBucketLifecycleConfigurationResponse(in).getConfiguration();
        }
    }

    public static final class DeleteObjectsResultUnmarshaller
    implements Unmarshaller<DeleteObjectsResponse, InputStream> {
        @Override
        public DeleteObjectsResponse unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseDeletedObjectsResult(in).getDeleteObjectResult();
        }
    }

    public static final class ListPartsResultUnmarshaller
    implements Unmarshaller<PartListing, InputStream> {
        @Override
        public PartListing unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseListPartsResponse(in).getListPartsResult();
        }
    }

    public static final class ListMultipartUploadsResultUnmarshaller
    implements Unmarshaller<MultipartUploadListing, InputStream> {
        @Override
        public MultipartUploadListing unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseListMultipartUploadsResponse(in).getListMultipartUploadsResult();
        }
    }

    public static final class InitiateMultipartUploadResultUnmarshaller
    implements Unmarshaller<InitiateMultipartUploadResult, InputStream> {
        @Override
        public InitiateMultipartUploadResult unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseInitiateMultipartUploadResponse(in).getInitiateMultipartUploadResult();
        }
    }

    public static final class CompleteMultipartUploadResultUnmarshaller
    implements Unmarshaller<XmlResponsesSaxParser.CompleteMultipartUploadHandler, InputStream> {
        @Override
        public XmlResponsesSaxParser.CompleteMultipartUploadHandler unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseCompleteMultipartUploadResponse(in);
        }
    }

    public static final class CopyObjectUnmarshaller
    implements Unmarshaller<XmlResponsesSaxParser.CopyObjectResultHandler, InputStream> {
        @Override
        public XmlResponsesSaxParser.CopyObjectResultHandler unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseCopyObjectResponse(in);
        }
    }

    public static final class InputStreamUnmarshaller
    implements Unmarshaller<InputStream, InputStream> {
        @Override
        public InputStream unmarshall(InputStream in) throws Exception {
            return in;
        }
    }

    public static final class DeletePublicAccessBlockUnmarshaller
    implements Unmarshaller<DeletePublicAccessBlockResult, InputStream> {
        @Override
        public DeletePublicAccessBlockResult unmarshall(InputStream in) {
            return new DeletePublicAccessBlockResult();
        }
    }

    public static final class SetPublicAccessBlockUnmarshaller
    implements Unmarshaller<SetPublicAccessBlockResult, InputStream> {
        @Override
        public SetPublicAccessBlockResult unmarshall(InputStream in) {
            return new SetPublicAccessBlockResult();
        }
    }

    public static final class SetBucketEncryptionUnmarshaller
    implements Unmarshaller<SetBucketEncryptionResult, InputStream> {
        @Override
        public SetBucketEncryptionResult unmarshall(InputStream in) {
            return new SetBucketEncryptionResult();
        }
    }

    public static final class DeleteBucketEncryptionUnmarshaller
    implements Unmarshaller<DeleteBucketEncryptionResult, InputStream> {
        @Override
        public DeleteBucketEncryptionResult unmarshall(InputStream in) {
            return new DeleteBucketEncryptionResult();
        }
    }

    public static final class BucketAccelerateConfigurationUnmarshaller
    implements Unmarshaller<BucketAccelerateConfiguration, InputStream> {
        @Override
        public BucketAccelerateConfiguration unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseAccelerateConfigurationResponse(in).getConfiguration();
        }
    }

    public static final class BucketTaggingConfigurationUnmarshaller
    implements Unmarshaller<BucketTaggingConfiguration, InputStream> {
        @Override
        public BucketTaggingConfiguration unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseTaggingConfigurationResponse(in).getConfiguration();
        }
    }

    public static final class BucketReplicationConfigurationUnmarshaller
    implements Unmarshaller<BucketReplicationConfiguration, InputStream> {
        @Override
        public BucketReplicationConfiguration unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseReplicationConfigurationResponse(in).getConfiguration();
        }
    }

    public static final class BucketWebsiteConfigurationUnmarshaller
    implements Unmarshaller<BucketWebsiteConfiguration, InputStream> {
        @Override
        public BucketWebsiteConfiguration unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseWebsiteConfigurationResponse(in).getConfiguration();
        }
    }

    public static final class BucketVersioningConfigurationUnmarshaller
    implements Unmarshaller<BucketVersioningConfiguration, InputStream> {
        @Override
        public BucketVersioningConfiguration unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseVersioningConfigurationResponse(in).getConfiguration();
        }
    }

    public static final class BucketLocationUnmarshaller
    implements Unmarshaller<String, InputStream> {
        @Override
        public String unmarshall(InputStream in) throws Exception {
            String location = new XmlResponsesSaxParser().parseBucketLocationResponse(in);
            if (location == null) {
                location = "US";
            }
            return location;
        }
    }

    public static final class BucketLoggingConfigurationnmarshaller
    implements Unmarshaller<BucketLoggingConfiguration, InputStream> {
        @Override
        public BucketLoggingConfiguration unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseLoggingStatusResponse(in).getBucketLoggingConfiguration();
        }
    }

    public static final class AccessControlListUnmarshaller
    implements Unmarshaller<AccessControlList, InputStream> {
        @Override
        public AccessControlList unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseAccessControlListResponse(in).getAccessControlList();
        }
    }

    public static final class VersionListUnmarshaller
    implements Unmarshaller<VersionListing, InputStream> {
        private final boolean shouldSDKDecodeResponse;

        public VersionListUnmarshaller(boolean shouldSDKDecodeResponse) {
            this.shouldSDKDecodeResponse = shouldSDKDecodeResponse;
        }

        @Override
        public VersionListing unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseListVersionsResponse(in, this.shouldSDKDecodeResponse).getListing();
        }
    }

    public static final class ListObjectsV2Unmarshaller
    implements Unmarshaller<ListObjectsV2Result, InputStream> {
        private final boolean shouldSDKDecodeResponse;

        public ListObjectsV2Unmarshaller(boolean shouldSDKDecodeResponse) {
            this.shouldSDKDecodeResponse = shouldSDKDecodeResponse;
        }

        @Override
        public ListObjectsV2Result unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseListObjectsV2Response(in, this.shouldSDKDecodeResponse).getResult();
        }
    }

    public static final class ListObjectsUnmarshaller
    implements Unmarshaller<ObjectListing, InputStream> {
        private final boolean shouldSDKDecodeResponse;

        public ListObjectsUnmarshaller(boolean shouldSDKDecodeResponse) {
            this.shouldSDKDecodeResponse = shouldSDKDecodeResponse;
        }

        @Override
        public ObjectListing unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseListBucketObjectsResponse(in, this.shouldSDKDecodeResponse).getObjectListing();
        }
    }

    public static final class ListBucketsOwnerUnmarshaller
    implements Unmarshaller<Owner, InputStream> {
        @Override
        public Owner unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseListMyBucketsResponse(in).getOwner();
        }
    }

    public static final class ListBucketsUnmarshaller
    implements Unmarshaller<List<Bucket>, InputStream> {
        @Override
        public List<Bucket> unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser().parseListMyBucketsResponse(in).getBuckets();
        }
    }
}

