/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.auth;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AbstractAWSSigner;
import com.amazonaws.auth.ChunkContentIterator;
import com.amazonaws.auth.DecodedStreamBuffer;
import com.amazonaws.auth.SigningAlgorithm;
import com.amazonaws.internal.SdkInputStream;
import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class AwsChunkedEncodingInputStream
extends SdkInputStream {
    protected static final String DEFAULT_ENCODING = "UTF-8";
    private static final int DEFAULT_CHUNK_SIZE = 131072;
    private static final int DEFAULT_BUFFER_SIZE = 262144;
    private static final String CRLF = "\r\n";
    private static final String CHUNK_STRING_TO_SIGN_PREFIX = "AWS4-HMAC-SHA256-PAYLOAD";
    private static final String CHUNK_SIGNATURE_HEADER = ";chunk-signature=";
    private static final int SIGNATURE_LENGTH = 64;
    private static final byte[] FINAL_CHUNK = new byte[0];
    private InputStream is = null;
    private final int maxBufferSize;
    private final String dateTime;
    private final String keyPath;
    private final String headerSignature;
    private String priorChunkSignature;
    private final AWS4Signer aws4Signer;
    private final MessageDigest sha256;
    private final Mac hmacSha256;
    private ChunkContentIterator currentChunkIterator;
    private DecodedStreamBuffer decodedStreamBuffer;
    private boolean isAtStart = true;
    private boolean isTerminating = false;
    private static final Log log = LogFactory.getLog(AwsChunkedEncodingInputStream.class);

    public AwsChunkedEncodingInputStream(InputStream in, byte[] kSigning, String datetime, String keyPath, String headerSignature, AWS4Signer aws4Signer) {
        this(in, 262144, kSigning, datetime, keyPath, headerSignature, aws4Signer);
    }

    public AwsChunkedEncodingInputStream(InputStream in, int maxBufferSize, byte[] kSigning, String datetime, String keyPath, String headerSignature, AWS4Signer aws4Signer) {
        if (in instanceof AwsChunkedEncodingInputStream) {
            AwsChunkedEncodingInputStream originalChunkedStream = (AwsChunkedEncodingInputStream)in;
            maxBufferSize = Math.max(originalChunkedStream.maxBufferSize, maxBufferSize);
            this.is = originalChunkedStream.is;
            this.decodedStreamBuffer = originalChunkedStream.decodedStreamBuffer;
        } else {
            this.is = in;
            this.decodedStreamBuffer = null;
        }
        if (maxBufferSize < 131072) {
            throw new IllegalArgumentException("Max buffer size should not be less than chunk size");
        }
        try {
            this.sha256 = MessageDigest.getInstance("SHA-256");
            String signingAlgo = SigningAlgorithm.HmacSHA256.toString();
            this.hmacSha256 = Mac.getInstance(signingAlgo);
            this.hmacSha256.init(new SecretKeySpec(kSigning, signingAlgo));
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
        this.maxBufferSize = maxBufferSize;
        this.dateTime = datetime;
        this.keyPath = keyPath;
        this.headerSignature = headerSignature;
        this.priorChunkSignature = headerSignature;
        this.aws4Signer = aws4Signer;
    }

    @Override
    public int read() throws IOException {
        byte[] tmp = new byte[1];
        int count = this.read(tmp, 0, 1);
        if (count != -1) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"One byte read from the stream.");
            }
            int unsignedByte = tmp[0] & 0xFF;
            return unsignedByte;
        }
        return count;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count;
        this.abortIfNeeded();
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (null == this.currentChunkIterator || !this.currentChunkIterator.hasNext()) {
            if (this.isTerminating) {
                return -1;
            }
            this.isTerminating = this.setUpNextChunk();
        }
        if ((count = this.currentChunkIterator.read(b, off, len)) > 0) {
            this.isAtStart = false;
            if (log.isTraceEnabled()) {
                log.trace((Object)(count + " byte read from the stream."));
            }
        }
        return count;
    }

    @Override
    public long skip(long n) throws IOException {
        long remaining;
        int count;
        if (n <= 0L) {
            return 0L;
        }
        int toskip = (int)Math.min(262144L, n);
        byte[] temp = new byte[toskip];
        for (remaining = n; remaining > 0L && (count = this.read(temp, 0, toskip)) >= 0; remaining -= (long)count) {
        }
        return n - remaining;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readlimit) {
        this.abortIfNeeded();
        if (!this.isAtStart) {
            throw new UnsupportedOperationException("Chunk-encoded stream only supports mark() at the start of the stream.");
        }
        if (this.is.markSupported()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"AwsChunkedEncodingInputStream marked at the start of the stream (will directly mark the wrapped stream since it's mark-supported).");
            }
            this.is.mark(readlimit);
        } else {
            if (log.isDebugEnabled()) {
                log.debug((Object)"AwsChunkedEncodingInputStream marked at the start of the stream (initializing the buffer since the wrapped stream is not mark-supported).");
            }
            this.decodedStreamBuffer = new DecodedStreamBuffer(this.maxBufferSize);
        }
    }

    @Override
    public void reset() throws IOException {
        this.abortIfNeeded();
        this.currentChunkIterator = null;
        this.priorChunkSignature = this.headerSignature;
        if (this.is.markSupported()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"AwsChunkedEncodingInputStream reset (will reset the wrapped stream because it is mark-supported).");
            }
            this.is.reset();
        } else {
            if (log.isDebugEnabled()) {
                log.debug((Object)"AwsChunkedEncodingInputStream reset (will use the buffer of the decoded stream).");
            }
            if (null == this.decodedStreamBuffer) {
                throw new IOException("Cannot reset the stream because the mark is not set.");
            }
            this.decodedStreamBuffer.startReadBuffer();
        }
        this.currentChunkIterator = null;
        this.isAtStart = true;
        this.isTerminating = false;
    }

    public static long calculateStreamContentLength(long originalLength) {
        if (originalLength < 0L) {
            throw new IllegalArgumentException("Nonnegative content length expected.");
        }
        long maxSizeChunks = originalLength / 131072L;
        long remainingBytes = originalLength % 131072L;
        return maxSizeChunks * AwsChunkedEncodingInputStream.calculateSignedChunkLength(131072L) + (remainingBytes > 0L ? AwsChunkedEncodingInputStream.calculateSignedChunkLength(remainingBytes) : 0L) + AwsChunkedEncodingInputStream.calculateSignedChunkLength(0L);
    }

    private static long calculateSignedChunkLength(long chunkDataSize) {
        return (long)(Long.toHexString(chunkDataSize).length() + CHUNK_SIGNATURE_HEADER.length() + 64 + CRLF.length()) + chunkDataSize + (long)CRLF.length();
    }

    private boolean setUpNextChunk() throws IOException {
        byte[] chunkData = new byte[131072];
        int chunkSizeInBytes = 0;
        while (chunkSizeInBytes < 131072) {
            if (null != this.decodedStreamBuffer && this.decodedStreamBuffer.hasNext()) {
                chunkData[chunkSizeInBytes++] = this.decodedStreamBuffer.next();
                continue;
            }
            int bytesToRead = 131072 - chunkSizeInBytes;
            int count = this.is.read(chunkData, chunkSizeInBytes, bytesToRead);
            if (count == -1) break;
            if (null != this.decodedStreamBuffer) {
                this.decodedStreamBuffer.buffer(chunkData, chunkSizeInBytes, count);
            }
            chunkSizeInBytes += count;
        }
        if (chunkSizeInBytes == 0) {
            byte[] signedFinalChunk = this.createSignedChunk(FINAL_CHUNK);
            this.currentChunkIterator = new ChunkContentIterator(signedFinalChunk);
            return true;
        }
        if (chunkSizeInBytes < chunkData.length) {
            chunkData = Arrays.copyOf(chunkData, chunkSizeInBytes);
        }
        byte[] signedChunkContent = this.createSignedChunk(chunkData);
        this.currentChunkIterator = new ChunkContentIterator(signedChunkContent);
        return false;
    }

    private byte[] createSignedChunk(byte[] chunkData) {
        String chunkSignature;
        StringBuilder chunkHeader = new StringBuilder();
        chunkHeader.append(Integer.toHexString(chunkData.length));
        String chunkStringToSign = "AWS4-HMAC-SHA256-PAYLOAD\n" + this.dateTime + "\n" + this.keyPath + "\n" + this.priorChunkSignature + "\n" + AbstractAWSSigner.EMPTY_STRING_SHA256_HEX + "\n" + BinaryUtils.toHex(this.sha256.digest(chunkData));
        this.priorChunkSignature = chunkSignature = BinaryUtils.toHex(this.aws4Signer.signWithMac(chunkStringToSign, this.hmacSha256));
        chunkHeader.append(CHUNK_SIGNATURE_HEADER).append(chunkSignature).append(CRLF);
        try {
            byte[] header = chunkHeader.toString().getBytes(StringUtils.UTF8);
            byte[] trailer = CRLF.getBytes(StringUtils.UTF8);
            byte[] signedChunk = new byte[header.length + chunkData.length + trailer.length];
            System.arraycopy(header, 0, signedChunk, 0, header.length);
            System.arraycopy(chunkData, 0, signedChunk, header.length, chunkData.length);
            System.arraycopy(trailer, 0, signedChunk, header.length + chunkData.length, trailer.length);
            return signedChunk;
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to sign the chunked data. " + e.getMessage(), e);
        }
    }

    @Override
    protected InputStream getWrappedInputStream() {
        return this.is;
    }
}

