/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.services.s3.internal.multipart;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.internal.multipart.GenericMultipartHelper;
import software.amazon.awssdk.services.s3.internal.multipart.MultipartConfigurationResolver;
import software.amazon.awssdk.services.s3.internal.multipart.SdkPojoConversionUtils;
import software.amazon.awssdk.services.s3.internal.multipart.UploadWithKnownContentLengthHelper;
import software.amazon.awssdk.services.s3.internal.multipart.UploadWithUnknownContentLengthHelper;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class UploadObjectHelper {
    private static final Logger log = Logger.loggerFor(UploadObjectHelper.class);
    private final S3AsyncClient s3AsyncClient;
    private final long partSizeInBytes;
    private final GenericMultipartHelper<PutObjectRequest, PutObjectResponse> genericMultipartHelper;
    private final long apiCallBufferSize;
    private final long multipartUploadThresholdInBytes;
    private final UploadWithKnownContentLengthHelper uploadWithKnownContentLength;
    private final UploadWithUnknownContentLengthHelper uploadWithUnknownContentLength;

    public UploadObjectHelper(S3AsyncClient s3AsyncClient, MultipartConfigurationResolver resolver) {
        this.s3AsyncClient = s3AsyncClient;
        this.partSizeInBytes = resolver.minimalPartSizeInBytes();
        this.genericMultipartHelper = new GenericMultipartHelper<PutObjectRequest, PutObjectResponse>(s3AsyncClient, SdkPojoConversionUtils::toAbortMultipartUploadRequest, SdkPojoConversionUtils::toPutObjectResponse);
        this.apiCallBufferSize = resolver.apiCallBufferSize();
        this.multipartUploadThresholdInBytes = resolver.thresholdInBytes();
        this.uploadWithKnownContentLength = new UploadWithKnownContentLengthHelper(s3AsyncClient, this.partSizeInBytes, this.multipartUploadThresholdInBytes, this.apiCallBufferSize);
        this.uploadWithUnknownContentLength = new UploadWithUnknownContentLengthHelper(s3AsyncClient, this.partSizeInBytes, this.multipartUploadThresholdInBytes, this.apiCallBufferSize);
    }

    public CompletableFuture<PutObjectResponse> uploadObject(PutObjectRequest putObjectRequest, AsyncRequestBody asyncRequestBody) {
        Long contentLength = asyncRequestBody.contentLength().orElseGet(putObjectRequest::contentLength);
        if (contentLength == null) {
            return this.uploadWithUnknownContentLength.uploadObject(putObjectRequest, asyncRequestBody);
        }
        return this.uploadWithKnownContentLength.uploadObject(putObjectRequest, asyncRequestBody, contentLength);
    }
}

