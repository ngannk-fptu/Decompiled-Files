/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.checksums.Algorithm;
import software.amazon.awssdk.core.exception.SdkClientException;

@SdkInternalApi
public final class ChunkContentUtils {
    public static final String HEADER_COLON_SEPARATOR = ":";
    public static final String ZERO_BYTE = "0";
    public static final String CRLF = "\r\n";
    public static final String LAST_CHUNK = "0\r\n";
    public static final long LAST_CHUNK_LEN = "0\r\n".length();

    private ChunkContentUtils() {
    }

    public static long calculateChunkLength(long originalContentLength) {
        if (originalContentLength == 0L) {
            return 0L;
        }
        return (long)(Long.toHexString(originalContentLength).length() + CRLF.length()) + originalContentLength + (long)CRLF.length();
    }

    public static long calculateStreamContentLength(long originalLength, long chunkSize) {
        if (originalLength < 0L || chunkSize == 0L) {
            throw new IllegalArgumentException(originalLength + ", " + chunkSize + "Args <= 0 not expected");
        }
        long maxSizeChunks = originalLength / chunkSize;
        long remainingBytes = originalLength % chunkSize;
        long allChunks = maxSizeChunks * ChunkContentUtils.calculateChunkLength(chunkSize);
        long remainingInChunk = remainingBytes > 0L ? ChunkContentUtils.calculateChunkLength(remainingBytes) : 0L;
        long lastByteSize = 1L + (long)CRLF.length();
        return allChunks + remainingInChunk + lastByteSize;
    }

    public static long calculateChecksumTrailerLength(Algorithm algorithm, String headerName) {
        return (long)(headerName.length() + HEADER_COLON_SEPARATOR.length()) + algorithm.base64EncodedLength().longValue() + (long)CRLF.length() + (long)CRLF.length();
    }

    public static ByteBuffer createChecksumTrailer(String computedChecksum, String trailerHeader) {
        StringBuilder headerBuilder = new StringBuilder(trailerHeader).append(HEADER_COLON_SEPARATOR).append(computedChecksum).append(CRLF).append(CRLF);
        return ByteBuffer.wrap(headerBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static ByteBuffer createChunk(ByteBuffer chunkData, boolean isLastByte) {
        int chunkLength = chunkData.remaining();
        StringBuilder chunkHeader = new StringBuilder(Integer.toHexString(chunkLength));
        chunkHeader.append(CRLF);
        try {
            byte[] header = chunkHeader.toString().getBytes(StandardCharsets.UTF_8);
            byte[] trailer = !isLastByte ? CRLF.getBytes(StandardCharsets.UTF_8) : "".getBytes(StandardCharsets.UTF_8);
            ByteBuffer chunkFormattedBuffer = ByteBuffer.allocate(header.length + chunkLength + trailer.length);
            chunkFormattedBuffer.put(header).put(chunkData).put(trailer);
            chunkFormattedBuffer.flip();
            return chunkFormattedBuffer;
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to create chunked data. " + e.getMessage()).cause(e).build();
        }
    }
}

