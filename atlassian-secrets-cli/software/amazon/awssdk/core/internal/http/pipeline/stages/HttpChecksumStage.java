/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.HttpChecksumConstant;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.checksums.ChecksumSpecs;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.async.ChecksumCalculatingAsyncRequestBody;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.MutableRequestToRequestPipeline;
import software.amazon.awssdk.core.internal.io.AwsUnsignedChunkedEncodingInputStream;
import software.amazon.awssdk.core.internal.util.ChunkContentUtils;
import software.amazon.awssdk.core.internal.util.HttpChecksumResolver;
import software.amazon.awssdk.core.internal.util.HttpChecksumUtils;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Md5Utils;

@SdkInternalApi
public class HttpChecksumStage
implements MutableRequestToRequestPipeline {
    private final ClientType clientType;

    public HttpChecksumStage(ClientType clientType) {
        this.clientType = clientType;
    }

    @Override
    public SdkHttpFullRequest.Builder execute(SdkHttpFullRequest.Builder request, RequestExecutionContext context) throws Exception {
        if (this.md5ChecksumRequired(request, context)) {
            this.addMd5ChecksumInHeader(request);
            return request;
        }
        ChecksumSpecs resolvedChecksumSpecs = HttpChecksumResolver.getResolvedChecksumSpecs(context.executionAttributes());
        if (this.flexibleChecksumInTrailerRequired(context, resolvedChecksumSpecs)) {
            this.addFlexibleChecksumInTrailer(request, context, resolvedChecksumSpecs);
            return request;
        }
        if (HttpChecksumStage.sraSigningEnabled(context)) {
            return request;
        }
        if (this.flexibleChecksumInHeaderRequired(context, resolvedChecksumSpecs)) {
            this.addFlexibleChecksumInHeader(request, context, resolvedChecksumSpecs);
            return request;
        }
        return request;
    }

    private boolean md5ChecksumRequired(SdkHttpFullRequest.Builder request, RequestExecutionContext context) {
        boolean isHttpChecksumRequired = context.executionAttributes().getAttribute(SdkInternalExecutionAttribute.HTTP_CHECKSUM_REQUIRED) != null || HttpChecksumUtils.isMd5ChecksumRequired(context.executionAttributes());
        boolean requestAlreadyHasMd5 = request.firstMatchingHeader("Content-MD5").isPresent();
        if (!isHttpChecksumRequired || requestAlreadyHasMd5) {
            return false;
        }
        if (context.requestProvider() != null) {
            throw new IllegalArgumentException("This operation requires a content-MD5 checksum, but one cannot be calculated for non-blocking content.");
        }
        return context.executionContext().interceptorContext().requestBody().isPresent();
    }

    private void addMd5ChecksumInHeader(SdkHttpFullRequest.Builder request) {
        try {
            String payloadMd5 = Md5Utils.md5AsBase64(request.contentStreamProvider().newStream());
            request.putHeader("Content-MD5", payloadMd5);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean flexibleChecksumInTrailerRequired(RequestExecutionContext context, ChecksumSpecs checksumSpecs) {
        if (HttpChecksumStage.sraSigningEnabled(context) && this.clientType == ClientType.SYNC) {
            return false;
        }
        boolean hasRequestBody = true;
        if (this.clientType == ClientType.SYNC) {
            hasRequestBody = context.executionContext().interceptorContext().requestBody().isPresent();
        } else if (this.clientType == ClientType.ASYNC) {
            hasRequestBody = context.executionContext().interceptorContext().asyncRequestBody().isPresent();
        }
        boolean isContentStreaming = context.executionContext().interceptorContext().requestBody().map(requestBody -> requestBody.contentStreamProvider() != null).orElse(false);
        return checksumSpecs != null && checksumSpecs.headerName() != null && HttpChecksumUtils.isTrailerBasedChecksumForClientType(context.executionAttributes(), context.executionContext().interceptorContext().httpRequest(), this.clientType, checksumSpecs, hasRequestBody, isContentStreaming);
    }

    private static boolean sraSigningEnabled(RequestExecutionContext context) {
        return context.executionAttributes().getAttribute(SdkInternalExecutionAttribute.AUTH_SCHEMES) != null;
    }

    private void addFlexibleChecksumInTrailer(SdkHttpFullRequest.Builder request, RequestExecutionContext context, ChecksumSpecs checksumSpecs) {
        long originalContentLength = 0L;
        int chunkSize = 0;
        if (this.clientType == ClientType.SYNC) {
            request.contentStreamProvider(new ChecksumCalculatingStreamProvider(request.contentStreamProvider(), checksumSpecs));
            originalContentLength = context.executionContext().interceptorContext().requestBody().get().optionalContentLength().orElse(0L);
            chunkSize = 131072;
        } else if (this.clientType == ClientType.ASYNC && context.requestProvider() != null) {
            context.requestProvider((AsyncRequestBody)ChecksumCalculatingAsyncRequestBody.builder().asyncRequestBody(context.requestProvider()).algorithm(checksumSpecs.algorithm()).trailerHeader(checksumSpecs.headerName()).build());
            originalContentLength = context.executionContext().interceptorContext().asyncRequestBody().get().contentLength().orElse(0L);
            chunkSize = 16384;
        }
        long checksumContentLength = ChunkContentUtils.calculateChecksumTrailerLength(checksumSpecs.algorithm(), checksumSpecs.headerName());
        long contentLen = checksumContentLength + ChunkContentUtils.calculateStreamContentLength(originalContentLength, chunkSize);
        request.putHeader("x-amz-trailer", checksumSpecs.headerName()).appendHeader("Content-encoding", "aws-chunked").putHeader("x-amz-content-sha256", "STREAMING-UNSIGNED-PAYLOAD-TRAILER").putHeader("x-amz-decoded-content-length", Long.toString(originalContentLength)).putHeader("Content-Length", Long.toString(contentLen));
    }

    private boolean flexibleChecksumInHeaderRequired(RequestExecutionContext context, ChecksumSpecs headerChecksumSpecs) {
        if (!context.executionContext().interceptorContext().requestBody().isPresent()) {
            return false;
        }
        InterceptorContext interceptorContext = context.executionContext().interceptorContext();
        boolean isContentStreaming = context.executionContext().interceptorContext().requestBody().map(requestBody -> requestBody.contentStreamProvider() != null).orElse(false);
        return headerChecksumSpecs != null && headerChecksumSpecs.algorithm() != null && !HttpChecksumUtils.isHttpChecksumPresent(interceptorContext.httpRequest(), headerChecksumSpecs) && HttpChecksumUtils.isUnsignedPayload(context.executionAttributes().getAttribute(HttpChecksumConstant.SIGNING_METHOD), interceptorContext.httpRequest().protocol(), isContentStreaming) && !headerChecksumSpecs.isRequestStreaming();
    }

    private void addFlexibleChecksumInHeader(SdkHttpFullRequest.Builder request, RequestExecutionContext context, ChecksumSpecs checksumSpecs) {
        try {
            String payloadChecksum = BinaryUtils.toBase64(HttpChecksumUtils.computeChecksum(context.executionContext().interceptorContext().requestBody().get().contentStreamProvider().newStream(), checksumSpecs.algorithm()));
            request.putHeader(checksumSpecs.headerName(), payloadChecksum);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static final class ChecksumCalculatingStreamProvider
    implements ContentStreamProvider {
        private final ContentStreamProvider underlyingInputStreamProvider;
        private final String checksumHeaderForTrailer;
        private final ChecksumSpecs checksumSpecs;
        private InputStream currentStream;
        private SdkChecksum sdkChecksum;

        ChecksumCalculatingStreamProvider(ContentStreamProvider underlyingInputStreamProvider, ChecksumSpecs checksumSpecs) {
            this.underlyingInputStreamProvider = underlyingInputStreamProvider;
            this.sdkChecksum = SdkChecksum.forAlgorithm(checksumSpecs.algorithm());
            this.checksumHeaderForTrailer = checksumSpecs.headerName();
            this.checksumSpecs = checksumSpecs;
        }

        @Override
        public InputStream newStream() {
            this.closeCurrentStream();
            this.currentStream = ((AwsUnsignedChunkedEncodingInputStream.Builder)((AwsUnsignedChunkedEncodingInputStream.Builder)((AwsUnsignedChunkedEncodingInputStream.Builder)AwsUnsignedChunkedEncodingInputStream.builder().inputStream(this.underlyingInputStreamProvider.newStream())).sdkChecksum(this.sdkChecksum)).checksumHeaderForTrailer(this.checksumHeaderForTrailer)).build();
            return this.currentStream;
        }

        private void closeCurrentStream() {
            this.sdkChecksum = SdkChecksum.forAlgorithm(this.checksumSpecs.algorithm());
            if (this.currentStream != null) {
                IoUtils.closeQuietly(this.currentStream, null);
                this.currentStream = null;
            }
        }
    }
}

