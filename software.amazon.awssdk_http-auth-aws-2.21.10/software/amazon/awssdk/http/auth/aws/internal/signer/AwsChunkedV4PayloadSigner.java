/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.checksums.spi.ChecksumAlgorithm
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.Pair
 *  software.amazon.awssdk.utils.StringInputStream
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.checksums.spi.ChecksumAlgorithm;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.http.auth.aws.internal.signer.RollingSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4PayloadSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4RequestSigningResult;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.SdkChecksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.ChecksumTrailerProvider;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.ChunkedEncodedInputStream;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.SigV4ChunkExtensionProvider;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.SigV4TrailerProvider;
import software.amazon.awssdk.http.auth.aws.internal.signer.io.ChecksumInputStream;
import software.amazon.awssdk.http.auth.aws.internal.signer.io.ResettableContentStreamProvider;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.ChecksumUtil;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerUtils;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.StringInputStream;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class AwsChunkedV4PayloadSigner
implements V4PayloadSigner {
    private final CredentialScope credentialScope;
    private final int chunkSize;
    private final ChecksumAlgorithm checksumAlgorithm;
    private final List<Pair<String, List<String>>> preExistingTrailers = new ArrayList<Pair<String, List<String>>>();

    private AwsChunkedV4PayloadSigner(Builder builder) {
        this.credentialScope = (CredentialScope)Validate.paramNotNull((Object)builder.credentialScope, (String)"CredentialScope");
        this.chunkSize = Validate.isPositive((int)builder.chunkSize, (String)"ChunkSize");
        this.checksumAlgorithm = builder.checksumAlgorithm;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ContentStreamProvider sign(ContentStreamProvider payload, V4RequestSigningResult requestSigningResult) {
        SdkHttpRequest.Builder request = requestSigningResult.getSignedRequest();
        String checksum = (String)request.firstMatchingHeader("x-amz-content-sha256").orElseThrow(() -> new IllegalArgumentException("x-amz-content-sha256 must be set!"));
        ChunkedEncodedInputStream.Builder chunkedEncodedInputStreamBuilder = ChunkedEncodedInputStream.builder().inputStream(payload.newStream()).chunkSize(this.chunkSize).header(chunk -> Integer.toHexString(chunk.length).getBytes(StandardCharsets.UTF_8));
        this.preExistingTrailers.forEach(trailer -> chunkedEncodedInputStreamBuilder.addTrailer(() -> trailer));
        switch (checksum) {
            case "STREAMING-AWS4-HMAC-SHA256-PAYLOAD": {
                RollingSigner rollingSigner = new RollingSigner(requestSigningResult.getSigningKey(), requestSigningResult.getSignature());
                chunkedEncodedInputStreamBuilder.addExtension(new SigV4ChunkExtensionProvider(rollingSigner, this.credentialScope));
                break;
            }
            case "STREAMING-UNSIGNED-PAYLOAD-TRAILER": {
                this.setupChecksumTrailerIfNeeded(chunkedEncodedInputStreamBuilder);
                break;
            }
            case "STREAMING-AWS4-HMAC-SHA256-PAYLOAD-TRAILER": {
                RollingSigner rollingSigner = new RollingSigner(requestSigningResult.getSigningKey(), requestSigningResult.getSignature());
                chunkedEncodedInputStreamBuilder.addExtension(new SigV4ChunkExtensionProvider(rollingSigner, this.credentialScope));
                this.setupChecksumTrailerIfNeeded(chunkedEncodedInputStreamBuilder);
                chunkedEncodedInputStreamBuilder.addTrailer(new SigV4TrailerProvider(chunkedEncodedInputStreamBuilder.trailers(), rollingSigner, this.credentialScope));
                break;
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
        return new ResettableContentStreamProvider(chunkedEncodedInputStreamBuilder::build);
    }

    @Override
    public Publisher<ByteBuffer> signAsync(Publisher<ByteBuffer> payload, V4RequestSigningResult requestSigningResult) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeSigning(SdkHttpRequest.Builder request, ContentStreamProvider payload) {
        String checksum;
        long encodedContentLength = 0L;
        long contentLength = SignerUtils.moveContentLength(request, payload != null ? payload.newStream() : new StringInputStream(""));
        this.setupPreExistingTrailers(request);
        encodedContentLength += this.calculateExistingTrailersLength();
        switch (checksum = (String)request.firstMatchingHeader("x-amz-content-sha256").orElseThrow(() -> new IllegalArgumentException("x-amz-content-sha256 must be set!"))) {
            case "STREAMING-AWS4-HMAC-SHA256-PAYLOAD": {
                long extensionsLength = 81L;
                encodedContentLength += this.calculateChunksLength(contentLength, extensionsLength);
                break;
            }
            case "STREAMING-UNSIGNED-PAYLOAD-TRAILER": {
                if (this.checksumAlgorithm != null) {
                    encodedContentLength += this.calculateChecksumTrailerLength(ChecksumUtil.checksumHeaderName(this.checksumAlgorithm));
                }
                encodedContentLength += this.calculateChunksLength(contentLength, 0L);
                break;
            }
            case "STREAMING-AWS4-HMAC-SHA256-PAYLOAD-TRAILER": {
                long extensionsLength = 81L;
                encodedContentLength += this.calculateChunksLength(contentLength, extensionsLength);
                if (this.checksumAlgorithm != null) {
                    encodedContentLength += this.calculateChecksumTrailerLength(ChecksumUtil.checksumHeaderName(this.checksumAlgorithm));
                }
                encodedContentLength += 90L;
                break;
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
        encodedContentLength += 2L;
        if (this.checksumAlgorithm != null) {
            String checksumHeaderName = ChecksumUtil.checksumHeaderName(this.checksumAlgorithm);
            request.appendHeader("x-amz-trailer", checksumHeaderName);
        }
        request.putHeader("Content-Length", Long.toString(encodedContentLength));
        request.appendHeader("Content-Encoding", "aws-chunked");
    }

    private void setupPreExistingTrailers(SdkHttpRequest.Builder request) {
        for (String header : request.matchingHeaders("x-amz-trailer")) {
            List values = request.matchingHeaders(header);
            if (values.isEmpty()) {
                throw new IllegalArgumentException(header + " must be present in the request headers to be a valid trailer.");
            }
            this.preExistingTrailers.add((Pair<String, List<String>>)Pair.of((Object)header, (Object)values));
            request.removeHeader(header);
        }
    }

    private long calculateChunksLength(long contentLength, long extensionsLength) {
        long lengthInBytes = 0L;
        long chunkHeaderLength = Integer.toHexString(this.chunkSize).length();
        long numChunks = contentLength / (long)this.chunkSize;
        lengthInBytes += numChunks * (chunkHeaderLength + extensionsLength + 2L + (long)this.chunkSize + 2L);
        long remainingBytes = contentLength % (long)this.chunkSize;
        if (remainingBytes > 0L) {
            long remainingChunkHeaderLength = Long.toHexString(remainingBytes).length();
            lengthInBytes += remainingChunkHeaderLength + extensionsLength + 2L + remainingBytes + 2L;
        }
        return lengthInBytes += 1L + extensionsLength + 2L;
    }

    private long calculateExistingTrailersLength() {
        long lengthInBytes = 0L;
        for (Pair<String, List<String>> trailer : this.preExistingTrailers) {
            lengthInBytes += this.calculateTrailerLength(trailer);
        }
        return lengthInBytes;
    }

    private long calculateTrailerLength(Pair<String, List<String>> trailer) {
        long lengthInBytes = (long)((String)trailer.left()).length() + 1L;
        for (String value : (List)trailer.right()) {
            lengthInBytes += (long)value.length();
        }
        return (lengthInBytes += (long)(((List)trailer.right()).size() - 1)) + 2L;
    }

    private long calculateChecksumTrailerLength(String checksumHeaderName) {
        long lengthInBytes = (long)checksumHeaderName.length() + 1L;
        SdkChecksum sdkChecksum = ChecksumUtil.fromChecksumAlgorithm(this.checksumAlgorithm);
        return (lengthInBytes += (long)BinaryUtils.toBase64((byte[])sdkChecksum.getChecksumBytes()).length()) + 2L;
    }

    private void setupChecksumTrailerIfNeeded(ChunkedEncodedInputStream.Builder builder) {
        if (this.checksumAlgorithm == null) {
            return;
        }
        String checksumHeaderName = ChecksumUtil.checksumHeaderName(this.checksumAlgorithm);
        SdkChecksum sdkChecksum = ChecksumUtil.fromChecksumAlgorithm(this.checksumAlgorithm);
        ChecksumInputStream checksumInputStream = new ChecksumInputStream(builder.inputStream(), Collections.singleton(sdkChecksum));
        ChecksumTrailerProvider checksumTrailer = new ChecksumTrailerProvider(sdkChecksum, checksumHeaderName);
        builder.inputStream(checksumInputStream).addTrailer(checksumTrailer);
    }

    static class Builder {
        private CredentialScope credentialScope;
        private Integer chunkSize;
        private ChecksumAlgorithm checksumAlgorithm;

        Builder() {
        }

        public Builder credentialScope(CredentialScope credentialScope) {
            this.credentialScope = credentialScope;
            return this;
        }

        public Builder chunkSize(Integer chunkSize) {
            this.chunkSize = chunkSize;
            return this;
        }

        public Builder checksumAlgorithm(ChecksumAlgorithm checksumAlgorithm) {
            this.checksumAlgorithm = checksumAlgorithm;
            return this;
        }

        public AwsChunkedV4PayloadSigner build() {
            return new AwsChunkedV4PayloadSigner(this);
        }
    }
}

