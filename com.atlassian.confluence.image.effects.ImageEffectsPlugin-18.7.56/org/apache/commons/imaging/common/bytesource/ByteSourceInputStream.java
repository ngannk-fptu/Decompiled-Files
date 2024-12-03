/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.bytesource;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.bytesource.ByteSource;

public class ByteSourceInputStream
extends ByteSource {
    private static final int BLOCK_SIZE = 1024;
    private final InputStream is;
    private CacheBlock cacheHead;
    private byte[] readBuffer;
    private long streamLength = -1L;

    public ByteSourceInputStream(InputStream is, String fileName) {
        super(fileName);
        this.is = new BufferedInputStream(is);
    }

    private CacheBlock readBlock() throws IOException {
        int read;
        if (null == this.readBuffer) {
            this.readBuffer = new byte[1024];
        }
        if ((read = this.is.read(this.readBuffer)) < 1) {
            return null;
        }
        if (read < 1024) {
            byte[] result = new byte[read];
            System.arraycopy(this.readBuffer, 0, result, 0, read);
            return new CacheBlock(result);
        }
        byte[] result = this.readBuffer;
        this.readBuffer = null;
        return new CacheBlock(result);
    }

    private CacheBlock getFirstBlock() throws IOException {
        if (null == this.cacheHead) {
            this.cacheHead = this.readBlock();
        }
        return this.cacheHead;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new CacheReadingInputStream();
    }

    @Override
    public byte[] getBlock(long blockStart, int blockLength) throws IOException {
        int read;
        if (blockStart < 0L || blockLength < 0 || blockStart + (long)blockLength < 0L || blockStart + (long)blockLength > this.getLength()) {
            throw new IOException("Could not read block (block start: " + blockStart + ", block length: " + blockLength + ", data length: " + this.streamLength + ").");
        }
        InputStream cis = this.getInputStream();
        BinaryFunctions.skipBytes(cis, blockStart);
        byte[] bytes = new byte[blockLength];
        int total = 0;
        do {
            if ((read = cis.read(bytes, total, bytes.length - total)) >= 1) continue;
            throw new IOException("Could not read block.");
        } while ((total += read) < blockLength);
        return bytes;
    }

    @Override
    public long getLength() throws IOException {
        long skipped;
        if (this.streamLength >= 0L) {
            return this.streamLength;
        }
        InputStream cis = this.getInputStream();
        long result = 0L;
        while ((skipped = cis.skip(1024L)) > 0L) {
            result += skipped;
        }
        this.streamLength = result;
        return result;
    }

    @Override
    public byte[] getAll() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (CacheBlock block = this.getFirstBlock(); block != null; block = block.getNext()) {
            baos.write(block.bytes);
        }
        return baos.toByteArray();
    }

    @Override
    public String getDescription() {
        return "Inputstream: '" + this.getFileName() + "'";
    }

    private class CacheReadingInputStream
    extends InputStream {
        private CacheBlock block;
        private boolean readFirst;
        private int blockIndex;

        private CacheReadingInputStream() {
        }

        @Override
        public int read() throws IOException {
            if (null == this.block) {
                if (this.readFirst) {
                    return -1;
                }
                this.block = ByteSourceInputStream.this.getFirstBlock();
                this.readFirst = true;
            }
            if (this.block != null && this.blockIndex >= this.block.bytes.length) {
                this.block = this.block.getNext();
                this.blockIndex = 0;
            }
            if (null == this.block) {
                return -1;
            }
            if (this.blockIndex >= this.block.bytes.length) {
                return -1;
            }
            return 0xFF & this.block.bytes[this.blockIndex++];
        }

        @Override
        public int read(byte[] array, int off, int len) throws IOException {
            Objects.requireNonNull(array, "array");
            if (off < 0 || off > array.length || len < 0 || off + len > array.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (null == this.block) {
                if (this.readFirst) {
                    return -1;
                }
                this.block = ByteSourceInputStream.this.getFirstBlock();
                this.readFirst = true;
            }
            if (this.block != null && this.blockIndex >= this.block.bytes.length) {
                this.block = this.block.getNext();
                this.blockIndex = 0;
            }
            if (null == this.block) {
                return -1;
            }
            if (this.blockIndex >= this.block.bytes.length) {
                return -1;
            }
            int readSize = Math.min(len, this.block.bytes.length - this.blockIndex);
            System.arraycopy(this.block.bytes, this.blockIndex, array, off, readSize);
            this.blockIndex += readSize;
            return readSize;
        }

        @Override
        public long skip(long n) throws IOException {
            long remaining;
            int readSize;
            if (n <= 0L) {
                return 0L;
            }
            for (remaining = n; remaining > 0L; remaining -= (long)readSize) {
                if (null == this.block) {
                    if (this.readFirst) {
                        return -1L;
                    }
                    this.block = ByteSourceInputStream.this.getFirstBlock();
                    this.readFirst = true;
                }
                if (this.block != null && this.blockIndex >= this.block.bytes.length) {
                    this.block = this.block.getNext();
                    this.blockIndex = 0;
                }
                if (null == this.block || this.blockIndex >= this.block.bytes.length) break;
                readSize = Math.min((int)Math.min(1024L, remaining), this.block.bytes.length - this.blockIndex);
                this.blockIndex += readSize;
            }
            return n - remaining;
        }
    }

    private class CacheBlock {
        public final byte[] bytes;
        private CacheBlock next;
        private boolean triedNext;

        CacheBlock(byte[] bytes) {
            this.bytes = bytes;
        }

        public CacheBlock getNext() throws IOException {
            if (null != this.next) {
                return this.next;
            }
            if (this.triedNext) {
                return null;
            }
            this.triedNext = true;
            this.next = ByteSourceInputStream.this.readBlock();
            return this.next;
        }
    }
}

