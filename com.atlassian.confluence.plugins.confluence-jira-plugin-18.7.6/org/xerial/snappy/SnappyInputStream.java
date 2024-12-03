/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.xerial.snappy.Snappy;
import org.xerial.snappy.SnappyCodec;
import org.xerial.snappy.SnappyError;
import org.xerial.snappy.SnappyErrorCode;
import org.xerial.snappy.SnappyIOException;
import org.xerial.snappy.SnappyOutputStream;

public class SnappyInputStream
extends InputStream {
    public static final int MAX_CHUNK_SIZE = 0x20000000;
    private boolean finishedReading = false;
    protected final InputStream in;
    private final int maxChunkSize;
    private byte[] compressed;
    private byte[] uncompressed;
    private int uncompressedCursor = 0;
    private int uncompressedLimit = 0;
    private byte[] header = new byte[SnappyCodec.headerSize()];

    public SnappyInputStream(InputStream inputStream) throws IOException {
        this(inputStream, 0x20000000);
    }

    public SnappyInputStream(InputStream inputStream, int n) throws IOException {
        this.maxChunkSize = n;
        this.in = inputStream;
        this.readHeader();
    }

    @Override
    public void close() throws IOException {
        this.compressed = null;
        this.uncompressed = null;
        if (this.in != null) {
            this.in.close();
        }
    }

    protected void readHeader() throws IOException {
        int n;
        int n2;
        for (n = 0; n < this.header.length && (n2 = this.in.read(this.header, n, this.header.length - n)) != -1; n += n2) {
        }
        if (n == 0) {
            throw new SnappyIOException(SnappyErrorCode.EMPTY_INPUT, "Cannot decompress empty stream");
        }
        if (n < this.header.length || !SnappyCodec.hasMagicHeaderPrefix(this.header)) {
            this.readFully(this.header, n);
            return;
        }
    }

    private static boolean isValidHeader(byte[] byArray) throws IOException {
        SnappyCodec snappyCodec = SnappyCodec.readHeader(new ByteArrayInputStream(byArray));
        if (snappyCodec.isValidMagicHeader()) {
            if (snappyCodec.version < 1) {
                throw new SnappyIOException(SnappyErrorCode.INCOMPATIBLE_VERSION, String.format("Compressed with an incompatible codec version %d. At least version %d is required", snappyCodec.version, 1));
            }
            return true;
        }
        return false;
    }

    protected void readFully(byte[] byArray, int n) throws IOException {
        if (n == 0) {
            this.finishedReading = true;
            return;
        }
        this.compressed = new byte[Math.max(8192, n)];
        System.arraycopy(byArray, 0, this.compressed, 0, n);
        int n2 = n;
        int n3 = 0;
        while ((n3 = this.in.read(this.compressed, n2, this.compressed.length - n2)) != -1) {
            if ((n2 += n3) < this.compressed.length) continue;
            byte[] byArray2 = new byte[this.compressed.length * 2];
            System.arraycopy(this.compressed, 0, byArray2, 0, this.compressed.length);
            this.compressed = byArray2;
        }
        this.finishedReading = true;
        n3 = Snappy.uncompressedLength(this.compressed, 0, n2);
        this.uncompressed = new byte[n3];
        Snappy.uncompress(this.compressed, 0, n2, this.uncompressed, 0);
        this.uncompressedCursor = 0;
        this.uncompressedLimit = n3;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        int n3 = 0;
        while (n3 < n2) {
            if (this.uncompressedCursor >= this.uncompressedLimit) {
                if (this.hasNextChunk()) continue;
                return n3 == 0 ? -1 : n3;
            }
            int n4 = Math.min(this.uncompressedLimit - this.uncompressedCursor, n2 - n3);
            System.arraycopy(this.uncompressed, this.uncompressedCursor, byArray, n + n3, n4);
            n3 += n4;
            this.uncompressedCursor += n4;
        }
        return n3;
    }

    public int rawRead(Object object, int n, int n2) throws IOException {
        int n3 = 0;
        while (n3 < n2) {
            if (this.uncompressedCursor >= this.uncompressedLimit) {
                if (this.hasNextChunk()) continue;
                return n3 == 0 ? -1 : n3;
            }
            int n4 = Math.min(this.uncompressedLimit - this.uncompressedCursor, n2 - n3);
            Snappy.arrayCopy(this.uncompressed, this.uncompressedCursor, n4, object, n + n3);
            n3 += n4;
            this.uncompressedCursor += n4;
        }
        return n3;
    }

    public int read(long[] lArray, int n, int n2) throws IOException {
        return this.rawRead(lArray, n * 8, n2 * 8);
    }

    public int read(long[] lArray) throws IOException {
        return this.read(lArray, 0, lArray.length);
    }

    public int read(double[] dArray, int n, int n2) throws IOException {
        return this.rawRead(dArray, n * 8, n2 * 8);
    }

    public int read(double[] dArray) throws IOException {
        return this.read(dArray, 0, dArray.length);
    }

    public int read(int[] nArray) throws IOException {
        return this.read(nArray, 0, nArray.length);
    }

    public int read(int[] nArray, int n, int n2) throws IOException {
        return this.rawRead(nArray, n * 4, n2 * 4);
    }

    public int read(float[] fArray, int n, int n2) throws IOException {
        return this.rawRead(fArray, n * 4, n2 * 4);
    }

    public int read(float[] fArray) throws IOException {
        return this.read(fArray, 0, fArray.length);
    }

    public int read(short[] sArray, int n, int n2) throws IOException {
        return this.rawRead(sArray, n * 2, n2 * 2);
    }

    public int read(short[] sArray) throws IOException {
        return this.read(sArray, 0, sArray.length);
    }

    private int readNext(byte[] byArray, int n, int n2) throws IOException {
        int n3;
        int n4;
        for (n3 = 0; n3 < n2; n3 += n4) {
            n4 = this.in.read(byArray, n3 + n, n2 - n3);
            if (n4 != -1) continue;
            this.finishedReading = true;
            return n3;
        }
        return n3;
    }

    protected boolean hasNextChunk() throws IOException {
        int n;
        int n2;
        if (this.finishedReading) {
            return false;
        }
        this.uncompressedCursor = 0;
        this.uncompressedLimit = 0;
        int n3 = this.readNext(this.header, 0, 4);
        if (n3 < 4) {
            return false;
        }
        int n4 = SnappyOutputStream.readInt(this.header, 0);
        if (n4 == SnappyCodec.MAGIC_HEADER_HEAD) {
            int n5 = SnappyCodec.headerSize() - 4;
            n3 = this.readNext(this.header, 4, n5);
            if (n3 < n5) {
                throw new SnappyIOException(SnappyErrorCode.FAILED_TO_UNCOMPRESS, String.format("Insufficient header size in a concatenated block", new Object[0]));
            }
            if (SnappyInputStream.isValidHeader(this.header)) {
                return this.hasNextChunk();
            }
            return false;
        }
        if (n4 < 0) {
            throw new SnappyError(SnappyErrorCode.INVALID_CHUNK_SIZE, "chunkSize is too big or negative : " + n4);
        }
        if (n4 > this.maxChunkSize) {
            throw new SnappyError(SnappyErrorCode.FAILED_TO_UNCOMPRESS, String.format("Received chunkSize %,d is greater than max configured chunk size %,d", n4, this.maxChunkSize));
        }
        if (this.compressed == null || n4 > this.compressed.length) {
            try {
                this.compressed = new byte[n4];
            }
            catch (OutOfMemoryError outOfMemoryError) {
                throw new SnappyError(SnappyErrorCode.INVALID_CHUNK_SIZE, outOfMemoryError.getMessage());
            }
        }
        for (n3 = 0; n3 < n4 && (n2 = this.in.read(this.compressed, n3, n4 - n3)) != -1; n3 += n2) {
        }
        if (n3 < n4) {
            throw new IOException("failed to read chunk");
        }
        n2 = Snappy.uncompressedLength(this.compressed, 0, n4);
        if (this.uncompressed == null || n2 > this.uncompressed.length) {
            this.uncompressed = new byte[n2];
        }
        if (n2 != (n = Snappy.uncompress(this.compressed, 0, n4, this.uncompressed, 0))) {
            throw new SnappyIOException(SnappyErrorCode.INVALID_CHUNK_SIZE, String.format("expected %,d bytes, but decompressed chunk has %,d bytes", n2, n));
        }
        this.uncompressedLimit = n;
        return true;
    }

    @Override
    public int read() throws IOException {
        if (this.uncompressedCursor < this.uncompressedLimit) {
            return this.uncompressed[this.uncompressedCursor++] & 0xFF;
        }
        if (this.hasNextChunk()) {
            return this.read();
        }
        return -1;
    }

    @Override
    public int available() throws IOException {
        if (this.uncompressedCursor < this.uncompressedLimit) {
            return this.uncompressedLimit - this.uncompressedCursor;
        }
        if (this.hasNextChunk()) {
            return this.uncompressedLimit - this.uncompressedCursor;
        }
        return 0;
    }
}

