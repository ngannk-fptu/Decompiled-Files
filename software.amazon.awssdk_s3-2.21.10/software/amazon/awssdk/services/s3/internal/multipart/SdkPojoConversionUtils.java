/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.services.s3.internal.multipart;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.CopyObjectResult;
import software.amazon.awssdk.services.s3.model.CopyPartResult;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class SdkPojoConversionUtils {
    private static final Logger log = Logger.loggerFor(SdkPojoConversionUtils.class);
    private static final HashSet<String> PUT_OBJECT_REQUEST_TO_UPLOAD_PART_FIELDS_TO_IGNORE = new HashSet<String>(Arrays.asList("ChecksumSHA1", "ChecksumSHA256", "ContentMD5", "ChecksumCRC32C", "ChecksumCRC32"));

    private SdkPojoConversionUtils() {
    }

    public static UploadPartRequest toUploadPartRequest(PutObjectRequest putObjectRequest, int partNumber, String uploadId) {
        UploadPartRequest.Builder builder = UploadPartRequest.builder();
        SdkPojoConversionUtils.setSdkFields(builder, (SdkPojo)putObjectRequest, PUT_OBJECT_REQUEST_TO_UPLOAD_PART_FIELDS_TO_IGNORE);
        return (UploadPartRequest)((Object)builder.uploadId(uploadId).partNumber(partNumber).build());
    }

    public static CreateMultipartUploadRequest toCreateMultipartUploadRequest(PutObjectRequest putObjectRequest) {
        CreateMultipartUploadRequest.Builder builder = CreateMultipartUploadRequest.builder();
        SdkPojoConversionUtils.setSdkFields(builder, (SdkPojo)putObjectRequest);
        return (CreateMultipartUploadRequest)((Object)builder.build());
    }

    public static HeadObjectRequest toHeadObjectRequest(CopyObjectRequest copyObjectRequest) {
        return (HeadObjectRequest)((Object)HeadObjectRequest.builder().bucket(copyObjectRequest.sourceBucket()).key(copyObjectRequest.sourceKey()).versionId(copyObjectRequest.sourceVersionId()).ifMatch(copyObjectRequest.copySourceIfMatch()).ifModifiedSince(copyObjectRequest.copySourceIfModifiedSince()).ifNoneMatch(copyObjectRequest.copySourceIfNoneMatch()).ifUnmodifiedSince(copyObjectRequest.copySourceIfUnmodifiedSince()).expectedBucketOwner(copyObjectRequest.expectedSourceBucketOwner()).sseCustomerAlgorithm(copyObjectRequest.copySourceSSECustomerAlgorithm()).sseCustomerKey(copyObjectRequest.copySourceSSECustomerKey()).sseCustomerKeyMD5(copyObjectRequest.copySourceSSECustomerKeyMD5()).build());
    }

    public static CompletedPart toCompletedPart(CopyPartResult copyPartResult, int partNumber) {
        CompletedPart.Builder builder = CompletedPart.builder();
        SdkPojoConversionUtils.setSdkFields(builder, copyPartResult);
        return (CompletedPart)builder.partNumber(partNumber).build();
    }

    public static CompletedPart toCompletedPart(UploadPartResponse partResponse, int partNumber) {
        CompletedPart.Builder builder = CompletedPart.builder();
        SdkPojoConversionUtils.setSdkFields(builder, (SdkPojo)partResponse);
        return (CompletedPart)builder.partNumber(partNumber).build();
    }

    private static void setSdkFields(SdkPojo targetBuilder, SdkPojo sourceObject) {
        SdkPojoConversionUtils.setSdkFields(targetBuilder, sourceObject, new HashSet<String>());
    }

    private static void setSdkFields(SdkPojo targetBuilder, SdkPojo sourceObject, Set<String> fieldsToIgnore) {
        Map<String, Object> sourceFields = SdkPojoConversionUtils.retrieveSdkFields(sourceObject, sourceObject.sdkFields());
        List targetSdkFields = targetBuilder.sdkFields();
        for (SdkField field : targetSdkFields) {
            if (fieldsToIgnore.contains(field.memberName())) continue;
            field.set((Object)targetBuilder, sourceFields.getOrDefault(field.memberName(), null));
        }
    }

    public static CreateMultipartUploadRequest toCreateMultipartUploadRequest(CopyObjectRequest copyObjectRequest) {
        CreateMultipartUploadRequest.Builder builder = CreateMultipartUploadRequest.builder();
        SdkPojoConversionUtils.setSdkFields(builder, (SdkPojo)copyObjectRequest);
        builder.bucket(copyObjectRequest.destinationBucket());
        builder.key(copyObjectRequest.destinationKey());
        return (CreateMultipartUploadRequest)((Object)builder.build());
    }

    public static CopyObjectResponse toCopyObjectResponse(CompleteMultipartUploadResponse response) {
        CopyObjectResponse.Builder builder = CopyObjectResponse.builder();
        SdkPojoConversionUtils.setSdkFields(builder, (SdkPojo)response);
        builder.responseMetadata(response.responseMetadata());
        builder.sdkHttpResponse(response.sdkHttpResponse());
        return (CopyObjectResponse)((Object)builder.copyObjectResult(SdkPojoConversionUtils.toCopyObjectResult(response)).build());
    }

    private static CopyObjectResult toCopyObjectResult(CompleteMultipartUploadResponse response) {
        CopyObjectResult.Builder builder = CopyObjectResult.builder();
        SdkPojoConversionUtils.setSdkFields(builder, (SdkPojo)response);
        return (CopyObjectResult)builder.build();
    }

    public static AbortMultipartUploadRequest.Builder toAbortMultipartUploadRequest(CopyObjectRequest copyObjectRequest) {
        AbortMultipartUploadRequest.Builder builder = AbortMultipartUploadRequest.builder();
        SdkPojoConversionUtils.setSdkFields(builder, (SdkPojo)copyObjectRequest);
        builder.bucket(copyObjectRequest.destinationBucket());
        builder.key(copyObjectRequest.destinationKey());
        return builder;
    }

    public static AbortMultipartUploadRequest.Builder toAbortMultipartUploadRequest(PutObjectRequest putObjectRequest) {
        AbortMultipartUploadRequest.Builder builder = AbortMultipartUploadRequest.builder();
        SdkPojoConversionUtils.setSdkFields(builder, (SdkPojo)putObjectRequest);
        return builder;
    }

    public static UploadPartCopyRequest toUploadPartCopyRequest(CopyObjectRequest copyObjectRequest, int partNumber, String uploadId, String range) {
        UploadPartCopyRequest.Builder builder = UploadPartCopyRequest.builder();
        SdkPojoConversionUtils.setSdkFields(builder, (SdkPojo)copyObjectRequest);
        return (UploadPartCopyRequest)((Object)builder.copySourceRange(range).partNumber(partNumber).uploadId(uploadId).bucket(copyObjectRequest.destinationBucket()).key(copyObjectRequest.destinationKey()).build());
    }

    public static PutObjectResponse toPutObjectResponse(CompleteMultipartUploadResponse response) {
        PutObjectResponse.Builder builder = PutObjectResponse.builder();
        SdkPojoConversionUtils.setSdkFields(builder, (SdkPojo)response);
        builder.responseMetadata(response.responseMetadata());
        builder.sdkHttpResponse(response.sdkHttpResponse());
        return (PutObjectResponse)((Object)builder.build());
    }

    private static Map<String, Object> retrieveSdkFields(SdkPojo sourceObject, List<SdkField<?>> sdkFields) {
        return sdkFields.stream().collect(HashMap::new, (map, field) -> map.put(field.memberName(), field.getValueOrDefault((Object)sourceObject)), Map::putAll);
    }
}

