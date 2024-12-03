/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.checksums.ChecksumSpecs
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.internal.chunked.AwsChunkedEncodingConfig
 *  software.amazon.awssdk.core.internal.util.HttpChecksumUtils
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.auth.signer.internal;

import java.io.InputStream;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.CredentialUtils;
import software.amazon.awssdk.auth.signer.S3SignerExecutionAttribute;
import software.amazon.awssdk.auth.signer.internal.AbstractAws4Signer;
import software.amazon.awssdk.auth.signer.internal.Aws4SignerRequestParams;
import software.amazon.awssdk.auth.signer.internal.Aws4SignerUtils;
import software.amazon.awssdk.auth.signer.internal.chunkedencoding.AwsS3V4ChunkSigner;
import software.amazon.awssdk.auth.signer.internal.chunkedencoding.AwsSignedChunkedEncodingInputStream;
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams;
import software.amazon.awssdk.auth.signer.params.AwsS3V4SignerParams;
import software.amazon.awssdk.core.checksums.ChecksumSpecs;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.internal.chunked.AwsChunkedEncodingConfig;
import software.amazon.awssdk.core.internal.util.HttpChecksumUtils;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public abstract class AbstractAwsS3V4Signer
extends AbstractAws4Signer<AwsS3V4SignerParams, Aws4PresignerParams> {
    public static final String CONTENT_SHA_256_WITH_CHECKSUM = "STREAMING-AWS4-HMAC-SHA256-PAYLOAD-TRAILER";
    public static final String STREAMING_UNSIGNED_PAYLOAD_TRAILER = "STREAMING-UNSIGNED-PAYLOAD-TRAILER";
    private static final String CONTENT_SHA_256 = "STREAMING-AWS4-HMAC-SHA256-PAYLOAD";
    private static final String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";
    private static final String CONTENT_LENGTH = "Content-Length";

    public SdkHttpFullRequest sign(SdkHttpFullRequest request, ExecutionAttributes executionAttributes) {
        AwsS3V4SignerParams signingParams = this.constructAwsS3SignerParams(executionAttributes);
        return this.sign(request, signingParams);
    }

    public SdkHttpFullRequest sign(SdkHttpFullRequest request, AwsS3V4SignerParams signingParams) {
        if (CredentialUtils.isAnonymous(signingParams.awsCredentials())) {
            return request;
        }
        Aws4SignerRequestParams requestParams = new Aws4SignerRequestParams(signingParams);
        return this.doSign(request, requestParams, signingParams).build();
    }

    private AwsS3V4SignerParams constructAwsS3SignerParams(ExecutionAttributes executionAttributes) {
        AwsS3V4SignerParams.Builder signerParams = this.extractSignerParams(AwsS3V4SignerParams.builder(), executionAttributes);
        Optional.ofNullable(executionAttributes.getAttribute(S3SignerExecutionAttribute.ENABLE_CHUNKED_ENCODING)).ifPresent(signerParams::enableChunkedEncoding);
        Optional.ofNullable(executionAttributes.getAttribute(S3SignerExecutionAttribute.ENABLE_PAYLOAD_SIGNING)).ifPresent(signerParams::enablePayloadSigning);
        return signerParams.build();
    }

    public SdkHttpFullRequest presign(SdkHttpFullRequest request, ExecutionAttributes executionAttributes) {
        Aws4PresignerParams signingParams = this.extractPresignerParams(Aws4PresignerParams.builder(), executionAttributes).build();
        return this.presign(request, signingParams);
    }

    public SdkHttpFullRequest presign(SdkHttpFullRequest request, Aws4PresignerParams signingParams) {
        if (CredentialUtils.isAnonymous(signingParams.awsCredentials())) {
            return request;
        }
        signingParams = (Aws4PresignerParams)signingParams.copy(b -> {
            Aws4PresignerParams.Builder cfr_ignored_0 = (Aws4PresignerParams.Builder)b.normalizePath(false);
        });
        Aws4SignerRequestParams requestParams = new Aws4SignerRequestParams(signingParams);
        return this.doPresign(request, requestParams, signingParams).build();
    }

    @Override
    protected void processRequestPayload(SdkHttpFullRequest.Builder mutableRequest, byte[] signature, byte[] signingKey, Aws4SignerRequestParams signerRequestParams, AwsS3V4SignerParams signerParams) {
        this.processRequestPayload(mutableRequest, signature, signingKey, signerRequestParams, signerParams, (SdkChecksum)null);
    }

    @Override
    protected void processRequestPayload(SdkHttpFullRequest.Builder mutableRequest, byte[] signature, byte[] signingKey, Aws4SignerRequestParams signerRequestParams, AwsS3V4SignerParams signerParams, SdkChecksum sdkChecksum) {
        if (this.useChunkEncoding(mutableRequest, signerParams) && mutableRequest.contentStreamProvider() != null) {
            ContentStreamProvider streamProvider = mutableRequest.contentStreamProvider();
            String headerForTrailerChecksumLocation = signerParams.checksumParams() != null ? signerParams.checksumParams().checksumHeaderName() : null;
            mutableRequest.contentStreamProvider(() -> this.asChunkEncodedStream(streamProvider.newStream(), signature, signingKey, signerRequestParams, sdkChecksum, headerForTrailerChecksumLocation));
        }
    }

    @Override
    protected String calculateContentHashPresign(SdkHttpFullRequest.Builder mutableRequest, Aws4PresignerParams signerParams) {
        return UNSIGNED_PAYLOAD;
    }

    private AwsSignedChunkedEncodingInputStream asChunkEncodedStream(InputStream inputStream, byte[] signature, byte[] signingKey, Aws4SignerRequestParams signerRequestParams, SdkChecksum sdkChecksum, String checksumHeaderForTrailer) {
        AwsS3V4ChunkSigner chunkSigner = new AwsS3V4ChunkSigner(signingKey, signerRequestParams.getFormattedRequestSigningDateTime(), signerRequestParams.getScope());
        return ((AwsSignedChunkedEncodingInputStream.Builder)((AwsSignedChunkedEncodingInputStream.Builder)((AwsSignedChunkedEncodingInputStream.Builder)((AwsSignedChunkedEncodingInputStream.Builder)AwsSignedChunkedEncodingInputStream.builder().inputStream(inputStream)).sdkChecksum(sdkChecksum)).checksumHeaderForTrailer(checksumHeaderForTrailer)).awsChunkSigner(chunkSigner).headerSignature(BinaryUtils.toHex((byte[])signature)).awsChunkedEncodingConfig(AwsChunkedEncodingConfig.create())).build();
    }

    @Override
    protected String calculateContentHash(SdkHttpFullRequest.Builder mutableRequest, AwsS3V4SignerParams signerParams) {
        return this.calculateContentHash(mutableRequest, signerParams, (SdkChecksum)null);
    }

    @Override
    protected String calculateContentHash(SdkHttpFullRequest.Builder mutableRequest, AwsS3V4SignerParams signerParams, SdkChecksum contentFlexibleChecksum) {
        boolean isUnsignedStreamingTrailer = mutableRequest.firstMatchingHeader("x-amz-content-sha256").map(STREAMING_UNSIGNED_PAYLOAD_TRAILER::equals).orElse(false);
        if (!isUnsignedStreamingTrailer) {
            mutableRequest.putHeader("x-amz-content-sha256", "required");
        }
        if (this.isPayloadSigningEnabled(mutableRequest, signerParams)) {
            if (this.useChunkEncoding(mutableRequest, signerParams)) {
                String headerForTrailerChecksumLocation;
                long originalContentLength = Aws4SignerUtils.calculateRequestContentLength(mutableRequest);
                mutableRequest.putHeader("x-amz-decoded-content-length", Long.toString(originalContentLength));
                boolean isTrailingChecksum = false;
                if (signerParams.checksumParams() != null && StringUtils.isNotBlank((CharSequence)(headerForTrailerChecksumLocation = signerParams.checksumParams().checksumHeaderName())) && !HttpChecksumUtils.isHttpChecksumPresent((SdkHttpRequest)mutableRequest.build(), (ChecksumSpecs)ChecksumSpecs.builder().headerName(signerParams.checksumParams().checksumHeaderName()).build())) {
                    isTrailingChecksum = true;
                    mutableRequest.putHeader("x-amz-trailer", headerForTrailerChecksumLocation);
                    mutableRequest.appendHeader("Content-Encoding", "aws-chunked");
                }
                long calculateStreamContentLength = AwsSignedChunkedEncodingInputStream.calculateStreamContentLength(originalContentLength, AwsS3V4ChunkSigner.getSignatureLength(), AwsChunkedEncodingConfig.create(), isTrailingChecksum);
                long checksumTrailerLength = isTrailingChecksum ? AbstractAwsS3V4Signer.getChecksumTrailerLength(signerParams) : 0L;
                mutableRequest.putHeader(CONTENT_LENGTH, Long.toString(calculateStreamContentLength + checksumTrailerLength));
                return isTrailingChecksum ? CONTENT_SHA_256_WITH_CHECKSUM : CONTENT_SHA_256;
            }
            return super.calculateContentHash(mutableRequest, signerParams, contentFlexibleChecksum);
        }
        return isUnsignedStreamingTrailer ? STREAMING_UNSIGNED_PAYLOAD_TRAILER : UNSIGNED_PAYLOAD;
    }

    private boolean useChunkEncoding(SdkHttpFullRequest.Builder mutableRequest, AwsS3V4SignerParams signerParams) {
        return this.isPayloadSigningEnabled(mutableRequest, signerParams) && this.isChunkedEncodingEnabled(signerParams);
    }

    private boolean isChunkedEncodingEnabled(AwsS3V4SignerParams signerParams) {
        Boolean isChunkedEncodingEnabled = signerParams.enableChunkedEncoding();
        return isChunkedEncodingEnabled != null && isChunkedEncodingEnabled != false;
    }

    private boolean isPayloadSigningEnabled(SdkHttpFullRequest.Builder request, AwsS3V4SignerParams signerParams) {
        if (!request.protocol().equals("https") && request.contentStreamProvider() != null) {
            return true;
        }
        Boolean isPayloadSigningEnabled = signerParams.enablePayloadSigning();
        return isPayloadSigningEnabled != null && isPayloadSigningEnabled != false;
    }

    public static long getChecksumTrailerLength(AwsS3V4SignerParams signerParams) {
        return signerParams.checksumParams() == null ? 0L : (long)AwsSignedChunkedEncodingInputStream.calculateChecksumContentLength(signerParams.checksumParams().algorithm(), signerParams.checksumParams().checksumHeaderName(), 64);
    }
}

