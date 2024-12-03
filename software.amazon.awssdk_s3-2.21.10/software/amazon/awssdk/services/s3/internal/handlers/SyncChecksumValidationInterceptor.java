/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.ClientType
 *  software.amazon.awssdk.core.checksums.Md5Checksum
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.core.interceptor.Context$AfterUnmarshalling
 *  software.amazon.awssdk.core.interceptor.Context$ModifyHttpRequest
 *  software.amazon.awssdk.core.interceptor.Context$ModifyHttpResponse
 *  software.amazon.awssdk.core.interceptor.ExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.core.sync.RequestBody
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpHeaders
 *  software.amazon.awssdk.utils.FunctionalUtils
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import java.io.InputStream;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.checksums.Md5Checksum;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpHeaders;
import software.amazon.awssdk.services.s3.checksums.ChecksumCalculatingInputStream;
import software.amazon.awssdk.services.s3.checksums.ChecksumValidatingInputStream;
import software.amazon.awssdk.services.s3.checksums.ChecksumsEnabledValidator;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkInternalApi
public final class SyncChecksumValidationInterceptor
implements ExecutionInterceptor {
    private static ExecutionAttribute<Boolean> SYNC_RECORDING_CHECKSUM = new ExecutionAttribute("syncRecordingChecksum");

    public Optional<RequestBody> modifyHttpContent(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        if (ChecksumsEnabledValidator.shouldRecordChecksum(context.request(), ClientType.SYNC, executionAttributes, context.httpRequest()) && context.requestBody().isPresent()) {
            Md5Checksum checksum = new Md5Checksum();
            executionAttributes.putAttribute(ChecksumsEnabledValidator.CHECKSUM, (Object)checksum);
            executionAttributes.putAttribute(SYNC_RECORDING_CHECKSUM, (Object)true);
            RequestBody requestBody = (RequestBody)context.requestBody().get();
            ChecksumCalculatingStreamProvider streamProvider = new ChecksumCalculatingStreamProvider(requestBody.contentStreamProvider(), (SdkChecksum)checksum);
            return Optional.of(RequestBody.fromContentProvider((ContentStreamProvider)streamProvider, (long)requestBody.contentLength(), (String)requestBody.contentType()));
        }
        return context.requestBody();
    }

    public Optional<InputStream> modifyHttpResponseContent(Context.ModifyHttpResponse context, ExecutionAttributes executionAttributes) {
        if (ChecksumsEnabledValidator.getObjectChecksumEnabledPerResponse(context.request(), (SdkHttpHeaders)context.httpResponse()) && context.responseBody().isPresent()) {
            Md5Checksum checksum = new Md5Checksum();
            long contentLength = context.httpResponse().firstMatchingHeader("Content-Length").map(Long::parseLong).orElse(0L);
            if (contentLength > 0L) {
                return Optional.of(new ChecksumValidatingInputStream((InputStream)context.responseBody().get(), (SdkChecksum)checksum, contentLength));
            }
        }
        return context.responseBody();
    }

    public void afterUnmarshalling(Context.AfterUnmarshalling context, ExecutionAttributes executionAttributes) {
        boolean recordingChecksum = Boolean.TRUE.equals(executionAttributes.getAttribute(SYNC_RECORDING_CHECKSUM));
        boolean responseChecksumIsValid = ChecksumsEnabledValidator.responseChecksumIsValid(context.httpResponse());
        if (recordingChecksum && responseChecksumIsValid) {
            ChecksumsEnabledValidator.validatePutObjectChecksum((PutObjectResponse)context.response(), executionAttributes);
        }
    }

    static final class ChecksumCalculatingStreamProvider
    implements ContentStreamProvider {
        private final SdkChecksum checksum;
        private InputStream currentStream;
        private final ContentStreamProvider underlyingInputStreamProvider;

        ChecksumCalculatingStreamProvider(ContentStreamProvider underlyingInputStreamProvider, SdkChecksum checksum) {
            this.underlyingInputStreamProvider = underlyingInputStreamProvider;
            this.checksum = checksum;
        }

        public InputStream newStream() {
            this.closeCurrentStream();
            this.currentStream = (InputStream)FunctionalUtils.invokeSafely(() -> new ChecksumCalculatingInputStream(this.underlyingInputStreamProvider.newStream(), this.checksum));
            return this.currentStream;
        }

        private void closeCurrentStream() {
            this.checksum.reset();
            if (this.currentStream != null) {
                FunctionalUtils.invokeSafely(this.currentStream::close);
                this.currentStream = null;
            }
        }
    }
}

