/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.io;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.chunked.AwsChunkedEncodingConfig;
import software.amazon.awssdk.core.internal.io.AwsChunkedEncodingInputStream;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkInternalApi
public class AwsUnsignedChunkedEncodingInputStream
extends AwsChunkedEncodingInputStream {
    private AwsUnsignedChunkedEncodingInputStream(InputStream in, AwsChunkedEncodingConfig awsChunkedEncodingConfig, SdkChecksum sdkChecksum, String checksumHeaderForTrailer) {
        super(in, sdkChecksum, checksumHeaderForTrailer, awsChunkedEncodingConfig);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected byte[] createFinalChunk(byte[] finalChunk) {
        StringBuilder chunkHeader = new StringBuilder();
        chunkHeader.append(Integer.toHexString(finalChunk.length));
        chunkHeader.append("\r\n");
        return chunkHeader.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected byte[] createChunk(byte[] chunkData) {
        StringBuilder chunkHeader = new StringBuilder();
        chunkHeader.append(Integer.toHexString(chunkData.length));
        chunkHeader.append("\r\n");
        try {
            byte[] header = chunkHeader.toString().getBytes(StandardCharsets.UTF_8);
            byte[] trailer = "\r\n".getBytes(StandardCharsets.UTF_8);
            byte[] chunk = new byte[header.length + chunkData.length + trailer.length];
            System.arraycopy(header, 0, chunk, 0, header.length);
            System.arraycopy(chunkData, 0, chunk, header.length, chunkData.length);
            System.arraycopy(trailer, 0, chunk, header.length + chunkData.length, trailer.length);
            return chunk;
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to create chunked data. " + e.getMessage()).cause(e).build();
        }
    }

    @Override
    protected byte[] createChecksumChunkHeader() {
        StringBuilder chunkHeader = new StringBuilder();
        chunkHeader.append(this.checksumHeaderForTrailer).append(":").append(BinaryUtils.toBase64(this.calculatedChecksum)).append("\r\n");
        return chunkHeader.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static final class Builder
    extends AwsChunkedEncodingInputStream.Builder<Builder> {
        public AwsUnsignedChunkedEncodingInputStream build() {
            return new AwsUnsignedChunkedEncodingInputStream(this.inputStream, this.awsChunkedEncodingConfig, this.sdkChecksum, this.checksumHeaderForTrailer);
        }
    }
}

