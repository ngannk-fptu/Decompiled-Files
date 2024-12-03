/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.interceptor.Context$ModifyRequest
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.services.s3.internal.resource.S3ArnUtils;
import software.amazon.awssdk.services.s3.internal.resource.S3ResourceType;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public final class CopySourceInterceptor
implements ExecutionInterceptor {
    public SdkRequest modifyRequest(Context.ModifyRequest context, ExecutionAttributes executionAttributes) {
        SdkRequest request = context.request();
        if (request instanceof CopyObjectRequest) {
            return CopySourceInterceptor.modifyCopyObjectRequest((CopyObjectRequest)request);
        }
        if (request instanceof UploadPartCopyRequest) {
            return CopySourceInterceptor.modifyUploadPartCopyRequest((UploadPartCopyRequest)request);
        }
        return request;
    }

    private static SdkRequest modifyCopyObjectRequest(CopyObjectRequest request) {
        if (request.copySource() != null) {
            CopySourceInterceptor.requireNotSet(request.sourceBucket(), "sourceBucket");
            CopySourceInterceptor.requireNotSet(request.sourceKey(), "sourceKey");
            CopySourceInterceptor.requireNotSet(request.sourceVersionId(), "sourceVersionId");
            return request;
        }
        String copySource = CopySourceInterceptor.constructCopySource(CopySourceInterceptor.requireSet(request.sourceBucket(), "sourceBucket"), CopySourceInterceptor.requireSet(request.sourceKey(), "sourceKey"), request.sourceVersionId());
        return (SdkRequest)request.toBuilder().sourceBucket(null).sourceKey(null).sourceVersionId(null).copySource(copySource).build();
    }

    private static SdkRequest modifyUploadPartCopyRequest(UploadPartCopyRequest request) {
        if (request.copySource() != null) {
            CopySourceInterceptor.requireNotSet(request.sourceBucket(), "sourceBucket");
            CopySourceInterceptor.requireNotSet(request.sourceKey(), "sourceKey");
            CopySourceInterceptor.requireNotSet(request.sourceVersionId(), "sourceVersionId");
            return request;
        }
        String copySource = CopySourceInterceptor.constructCopySource(CopySourceInterceptor.requireSet(request.sourceBucket(), "sourceBucket"), CopySourceInterceptor.requireSet(request.sourceKey(), "sourceKey"), request.sourceVersionId());
        return (SdkRequest)request.toBuilder().sourceBucket(null).sourceKey(null).sourceVersionId(null).copySource(copySource).build();
    }

    private static String constructCopySource(String sourceBucket, String sourceKey, String sourceVersionId) {
        StringBuilder copySource = new StringBuilder();
        copySource.append(SdkHttpUtils.urlEncodeIgnoreSlashes((String)sourceBucket));
        S3ArnUtils.getArnType(sourceBucket).ifPresent(arnType -> {
            if (arnType == S3ResourceType.ACCESS_POINT || arnType == S3ResourceType.OUTPOST) {
                copySource.append("/object");
            }
        });
        copySource.append("/");
        copySource.append(SdkHttpUtils.urlEncodeIgnoreSlashes((String)sourceKey));
        if (sourceVersionId != null) {
            copySource.append("?versionId=");
            copySource.append(SdkHttpUtils.urlEncodeIgnoreSlashes((String)sourceVersionId));
        }
        return copySource.toString();
    }

    private static void requireNotSet(Object value, String paramName) {
        Validate.isTrue((value == null ? 1 : 0) != 0, (String)"Parameter 'copySource' must not be used in conjunction with '%s'", (Object[])new Object[]{paramName});
    }

    private static <T> T requireSet(T value, String paramName) {
        Validate.isTrue((value != null ? 1 : 0) != 0, (String)"Parameter '%s' must not be null", (Object[])new Object[]{paramName});
        return value;
    }
}

