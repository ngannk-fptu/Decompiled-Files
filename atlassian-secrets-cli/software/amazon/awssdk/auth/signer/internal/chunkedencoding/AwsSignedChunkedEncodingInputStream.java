/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer.internal.chunkedencoding;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.internal.chunkedencoding.AwsChunkSigner;
import software.amazon.awssdk.core.checksums.Algorithm;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.chunked.AwsChunkedEncodingConfig;
import software.amazon.awssdk.core.internal.io.AwsChunkedEncodingInputStream;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkInternalApi
public final class AwsSignedChunkedEncodingInputStream
extends AwsChunkedEncodingInputStream {
    private static final String CHUNK_SIGNATURE_HEADER = ";chunk-signature=";
    private static final String CHECKSUM_SIGNATURE_HEADER = "x-amz-trailer-signature:";
    private String previousChunkSignature;
    private String headerSignature;
    private final AwsChunkSigner chunkSigner;

    private AwsSignedChunkedEncodingInputStream(InputStream in, SdkChecksum sdkChecksum, String checksumHeaderForTrailer, String headerSignature, AwsChunkSigner chunkSigner, AwsChunkedEncodingConfig config) {
        super(in, sdkChecksum, checksumHeaderForTrailer, config);
        this.chunkSigner = chunkSigner;
        this.previousChunkSignature = headerSignature;
        this.headerSignature = headerSignature;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static long calculateStreamContentLength(long originalLength, int signatureLength, AwsChunkedEncodingConfig config) {
        return AwsSignedChunkedEncodingInputStream.calculateStreamContentLength(originalLength, signatureLength, config, false);
    }

    public static long calculateStreamContentLength(long originalLength, int signatureLength, AwsChunkedEncodingConfig config, boolean isTrailingChecksumCalculated) {
        if (originalLength < 0L) {
            throw new IllegalArgumentException("Nonnegative content length expected.");
        }
        int chunkSize = config.chunkSize();
        long maxSizeChunks = originalLength / (long)chunkSize;
        long remainingBytes = originalLength % (long)chunkSize;
        return maxSizeChunks * AwsSignedChunkedEncodingInputStream.calculateSignedChunkLength(chunkSize, signatureLength, false) + (remainingBytes > 0L ? AwsSignedChunkedEncodingInputStream.calculateSignedChunkLength(remainingBytes, signatureLength, false) : 0L) + AwsSignedChunkedEncodingInputStream.calculateSignedChunkLength(0L, signatureLength, isTrailingChecksumCalculated);
    }

    private static long calculateSignedChunkLength(long chunkDataSize, int signatureLength, boolean isTrailingCarriageReturn) {
        return (long)(Long.toHexString(chunkDataSize).length() + CHUNK_SIGNATURE_HEADER.length() + signatureLength + "\r\n".length()) + chunkDataSize + (long)(isTrailingCarriageReturn ? 0 : "\r\n".length());
    }

    private byte[] createSignedChunk(byte[] chunkData) {
        try {
            byte[] header = this.createSignedChunkHeader(chunkData);
            byte[] trailer = this.isTrailingTerminated ? "\r\n".getBytes(StandardCharsets.UTF_8) : "".getBytes(StandardCharsets.UTF_8);
            byte[] signedChunk = new byte[header.length + chunkData.length + trailer.length];
            System.arraycopy(header, 0, signedChunk, 0, header.length);
            System.arraycopy(chunkData, 0, signedChunk, header.length, chunkData.length);
            System.arraycopy(trailer, 0, signedChunk, header.length + chunkData.length, trailer.length);
            return signedChunk;
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to sign the chunked data. " + e.getMessage()).cause(e).build();
        }
    }

    private byte[] createSignedChunkHeader(byte[] chunkData) {
        String chunkSignature;
        this.previousChunkSignature = chunkSignature = this.chunkSigner.signChunk(chunkData, this.previousChunkSignature);
        StringBuilder chunkHeader = new StringBuilder();
        chunkHeader.append(Integer.toHexString(chunkData.length));
        chunkHeader.append(CHUNK_SIGNATURE_HEADER).append(chunkSignature).append("\r\n");
        return chunkHeader.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected byte[] createFinalChunk(byte[] finalChunk) {
        return this.createChunk(FINAL_CHUNK);
    }

    @Override
    protected byte[] createChunk(byte[] chunkData) {
        return this.createSignedChunk(chunkData);
    }

    @Override
    protected byte[] createChecksumChunkHeader() {
        StringBuilder chunkHeader = new StringBuilder();
        chunkHeader.append(this.checksumHeaderForTrailer).append(":").append(BinaryUtils.toBase64(this.calculatedChecksum)).append("\r\n").append(this.createSignedChecksumChunk());
        return chunkHeader.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String createSignedChecksumChunk() {
        String chunkSignature;
        StringBuilder chunkHeader = new StringBuilder();
        this.previousChunkSignature = chunkSignature = this.chunkSigner.signChecksumChunk(this.calculatedChecksum, this.previousChunkSignature, this.checksumHeaderForTrailer);
        chunkHeader.append(CHECKSUM_SIGNATURE_HEADER).append(chunkSignature).append("\r\n");
        return chunkHeader.toString();
    }

    public static int calculateChecksumContentLength(Algorithm algorithm, String headerName, int signatureLength) {
        int originalLength = algorithm.base64EncodedLength();
        return headerName.length() + ":".length() + originalLength + "\r\n".length() + AwsSignedChunkedEncodingInputStream.calculateSignedChecksumChunkLength(signatureLength) + "\r\n".length();
    }

    private static int calculateSignedChecksumChunkLength(int signatureLength) {
        return CHECKSUM_SIGNATURE_HEADER.length() + signatureLength + "\r\n".length();
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.previousChunkSignature = this.headerSignature;
    }

    public static final class Builder
    extends AwsChunkedEncodingInputStream.Builder<Builder> {
        private AwsChunkSigner awsChunkSigner;
        private String headerSignature;

        public Builder headerSignature(String headerSignature) {
            this.headerSignature = headerSignature;
            return this;
        }

        public Builder awsChunkSigner(AwsChunkSigner awsChunkSigner) {
            this.awsChunkSigner = awsChunkSigner;
            return this;
        }

        public AwsSignedChunkedEncodingInputStream build() {
            return new AwsSignedChunkedEncodingInputStream(this.inputStream, this.sdkChecksum, this.checksumHeaderForTrailer, this.headerSignature, this.awsChunkSigner, this.awsChunkedEncodingConfig);
        }
    }
}

