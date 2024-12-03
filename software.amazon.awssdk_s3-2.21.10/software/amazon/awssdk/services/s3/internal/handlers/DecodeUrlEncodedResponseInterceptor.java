/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkResponse
 *  software.amazon.awssdk.core.interceptor.Context$ModifyResponse
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.EncodingType;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.MultipartUpload;
import software.amazon.awssdk.services.s3.model.ObjectVersion;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public final class DecodeUrlEncodedResponseInterceptor
implements ExecutionInterceptor {
    public SdkResponse modifyResponse(Context.ModifyResponse context, ExecutionAttributes executionAttributes) {
        SdkResponse response = context.response();
        if (DecodeUrlEncodedResponseInterceptor.shouldHandle(response)) {
            if (response instanceof ListObjectsResponse) {
                return DecodeUrlEncodedResponseInterceptor.modifyListObjectsResponse((ListObjectsResponse)response);
            }
            if (response instanceof ListObjectsV2Response) {
                return DecodeUrlEncodedResponseInterceptor.modifyListObjectsV2Response((ListObjectsV2Response)response);
            }
            if (response instanceof ListObjectVersionsResponse) {
                return this.modifyListObjectVersionsResponse((ListObjectVersionsResponse)response);
            }
            if (response instanceof ListMultipartUploadsResponse) {
                return this.modifyListMultipartUploadsResponse((ListMultipartUploadsResponse)response);
            }
        }
        return response;
    }

    private static boolean shouldHandle(SdkResponse sdkResponse) {
        return sdkResponse.getValueForField("EncodingType", String.class).map(et -> EncodingType.URL.toString().equals(et)).orElse(false);
    }

    private static SdkResponse modifyListObjectsResponse(ListObjectsResponse response) {
        return (SdkResponse)response.toBuilder().delimiter(SdkHttpUtils.urlDecode((String)response.delimiter())).marker(SdkHttpUtils.urlDecode((String)response.marker())).prefix(SdkHttpUtils.urlDecode((String)response.prefix())).nextMarker(SdkHttpUtils.urlDecode((String)response.nextMarker())).contents(DecodeUrlEncodedResponseInterceptor.decodeContents(response.contents())).commonPrefixes(DecodeUrlEncodedResponseInterceptor.decodeCommonPrefixes(response.commonPrefixes())).build();
    }

    private static SdkResponse modifyListObjectsV2Response(ListObjectsV2Response response) {
        return (SdkResponse)response.toBuilder().delimiter(SdkHttpUtils.urlDecode((String)response.delimiter())).prefix(SdkHttpUtils.urlDecode((String)response.prefix())).startAfter(SdkHttpUtils.urlDecode((String)response.startAfter())).contents(DecodeUrlEncodedResponseInterceptor.decodeContents(response.contents())).commonPrefixes(DecodeUrlEncodedResponseInterceptor.decodeCommonPrefixes(response.commonPrefixes())).build();
    }

    private SdkResponse modifyListObjectVersionsResponse(ListObjectVersionsResponse response) {
        return (SdkResponse)response.toBuilder().prefix(SdkHttpUtils.urlDecode((String)response.prefix())).keyMarker(SdkHttpUtils.urlDecode((String)response.keyMarker())).delimiter(SdkHttpUtils.urlDecode((String)response.delimiter())).nextKeyMarker(SdkHttpUtils.urlDecode((String)response.nextKeyMarker())).commonPrefixes(DecodeUrlEncodedResponseInterceptor.decodeCommonPrefixes(response.commonPrefixes())).versions(DecodeUrlEncodedResponseInterceptor.decodeObjectVersions(response.versions())).build();
    }

    private SdkResponse modifyListMultipartUploadsResponse(ListMultipartUploadsResponse response) {
        return (SdkResponse)response.toBuilder().delimiter(SdkHttpUtils.urlDecode((String)response.delimiter())).keyMarker(SdkHttpUtils.urlDecode((String)response.keyMarker())).nextKeyMarker(SdkHttpUtils.urlDecode((String)response.nextKeyMarker())).prefix(SdkHttpUtils.urlDecode((String)response.prefix())).commonPrefixes(DecodeUrlEncodedResponseInterceptor.decodeCommonPrefixes(response.commonPrefixes())).uploads(DecodeUrlEncodedResponseInterceptor.decodeMultipartUpload(response.uploads())).build();
    }

    private static List<S3Object> decodeContents(List<S3Object> contents) {
        if (contents == null) {
            return null;
        }
        return Collections.unmodifiableList(contents.stream().map(o -> (S3Object)o.toBuilder().key(SdkHttpUtils.urlDecode((String)o.key())).build()).collect(Collectors.toList()));
    }

    private static List<ObjectVersion> decodeObjectVersions(List<ObjectVersion> objectVersions) {
        if (objectVersions == null) {
            return null;
        }
        return Collections.unmodifiableList(objectVersions.stream().map(o -> (ObjectVersion)o.toBuilder().key(SdkHttpUtils.urlDecode((String)o.key())).build()).collect(Collectors.toList()));
    }

    private static List<CommonPrefix> decodeCommonPrefixes(List<CommonPrefix> commonPrefixes) {
        if (commonPrefixes == null) {
            return null;
        }
        return Collections.unmodifiableList(commonPrefixes.stream().map(p -> (CommonPrefix)p.toBuilder().prefix(SdkHttpUtils.urlDecode((String)p.prefix())).build()).collect(Collectors.toList()));
    }

    private static List<MultipartUpload> decodeMultipartUpload(List<MultipartUpload> multipartUploads) {
        if (multipartUploads == null) {
            return null;
        }
        return Collections.unmodifiableList(multipartUploads.stream().map(u -> (MultipartUpload)u.toBuilder().key(SdkHttpUtils.urlDecode((String)u.key())).build()).collect(Collectors.toList()));
    }
}

