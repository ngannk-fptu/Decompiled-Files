/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.tar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class TarBuffer {
    public static final int DEFAULT_RCDSIZE = 512;
    public static final int DEFAULT_BLKSIZE = 10240;
    private InputStream inStream;
    private OutputStream outStream;
    private final int blockSize;
    private final int recordSize;
    private final int recsPerBlock;
    private final byte[] blockBuffer;
    private int currBlkIdx;
    private int currRecIdx;
    private boolean debug;

    public TarBuffer(InputStream inStream) {
        this(inStream, 10240);
    }

    public TarBuffer(InputStream inStream, int blockSize) {
        this(inStream, blockSize, 512);
    }

    public TarBuffer(InputStream inStream, int blockSize, int recordSize) {
        this(inStream, null, blockSize, recordSize);
    }

    public TarBuffer(OutputStream outStream) {
        this(outStream, 10240);
    }

    public TarBuffer(OutputStream outStream, int blockSize) {
        this(outStream, blockSize, 512);
    }

    public TarBuffer(OutputStream outStream, int blockSize, int recordSize) {
        this(null, outStream, blockSize, recordSize);
    }

    private TarBuffer(InputStream inStream, OutputStream outStream, int blockSize, int recordSize) {
        this.inStream = inStream;
        this.outStream = outStream;
        this.debug = false;
        this.blockSize = blockSize;
        this.recordSize = recordSize;
        this.recsPerBlock = this.blockSize / this.recordSize;
        this.blockBuffer = new byte[this.blockSize];
        if (this.inStream != null) {
            this.currBlkIdx = -1;
            this.currRecIdx = this.recsPerBlock;
        } else {
            this.currBlkIdx = 0;
            this.currRecIdx = 0;
        }
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public int getRecordSize() {
        return this.recordSize;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isEOFRecord(byte[] record) {
        int sz = this.getRecordSize();
        for (int i = 0; i < sz; ++i) {
            if (record[i] == 0) continue;
            return false;
        }
        return true;
    }

    public void skipRecord() throws IOException {
        if (this.debug) {
            System.err.println("SkipRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.inStream == null) {
            throw new IOException("reading (via skip) from an output buffer");
        }
        if (this.currRecIdx >= this.recsPerBlock && !this.readBlock()) {
            return;
        }
        ++this.currRecIdx;
    }

    public byte[] readRecord() throws IOException {
        if (this.debug) {
            System.err.println("ReadRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.inStream == null) {
            if (this.outStream == null) {
                throw new IOException("input buffer is closed");
            }
            throw new IOException("reading from an output buffer");
        }
        if (this.currRecIdx >= this.recsPerBlock && !this.readBlock()) {
            return null;
        }
        byte[] result = new byte[this.recordSize];
        System.arraycopy(this.blockBuffer, this.currRecIdx * this.recordSize, result, 0, this.recordSize);
        ++this.currRecIdx;
        return result;
    }

    private boolean readBlock() throws IOException {
        if (this.debug) {
            System.err.println("ReadBlock: blkIdx = " + this.currBlkIdx);
        }
        if (this.inStream == null) {
            throw new IOException("reading from an output buffer");
        }
        this.currRecIdx = 0;
        int offset = 0;
        int bytesNeeded = this.blockSize;
        while (bytesNeeded > 0) {
            long numBytes = this.inStream.read(this.blockBuffer, offset, bytesNeeded);
            if (numBytes == -1L) {
                if (offset == 0) {
                    return false;
                }
                Arrays.fill(this.blockBuffer, offset, offset + bytesNeeded, (byte)0);
                break;
            }
            offset = (int)((long)offset + numBytes);
            bytesNeeded = (int)((long)bytesNeeded - numBytes);
            if (numBytes == (long)this.blockSize || !this.debug) continue;
            System.err.println("ReadBlock: INCOMPLETE READ " + numBytes + " of " + this.blockSize + " bytes read.");
        }
        ++this.currBlkIdx;
        return true;
    }

    public int getCurrentBlockNum() {
        return this.currBlkIdx;
    }

    public int getCurrentRecordNum() {
        return this.currRecIdx - 1;
    }

    public void writeRecord(byte[] record) throws IOException {
        if (this.debug) {
            System.err.println("WriteRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.outStream == null) {
            if (this.inStream == null) {
                throw new IOException("Output buffer is closed");
            }
            throw new IOException("writing to an input buffer");
        }
        if (record.length != this.recordSize) {
            throw new IOException("record to write has length '" + record.length + "' which is not the record size of '" + this.recordSize + "'");
        }
        if (this.currRecIdx >= this.recsPerBlock) {
            this.writeBlock();
        }
        System.arraycopy(record, 0, this.blockBuffer, this.currRecIdx * this.recordSize, this.recordSize);
        ++this.currRecIdx;
    }

    public void writeRecord(byte[] buf, int offset) throws IOException {
        if (this.debug) {
            System.err.println("WriteRecord: recIdx = " + this.currRecIdx + " blkIdx = " + this.currBlkIdx);
        }
        if (this.outStream == null) {
            if (this.inStream == null) {
                throw new IOException("Output buffer is closed");
            }
            throw new IOException("writing to an input buffer");
        }
        if (offset + this.recordSize > buf.length) {
            throw new IOException("record has length '" + buf.length + "' with offset '" + offset + "' which is less than the record size of '" + this.recordSize + "'");
        }
        if (this.currRecIdx >= this.recsPerBlock) {
            this.writeBlock();
        }
        System.arraycopy(buf, offset, this.blockBuffer, this.currRecIdx * this.recordSize, this.recordSize);
        ++this.currRecIdx;
    }

    private void writeBlock() throws IOException {
        if (this.debug) {
            System.err.println("WriteBlock: blkIdx = " + this.currBlkIdx);
        }
        if (this.outStream == null) {
            throw new IOException("writing to an input buffer");
        }
        this.outStream.write(this.blockBuffer, 0, this.blockSize);
        this.outStream.flush();
        this.currRecIdx = 0;
        ++this.currBlkIdx;
        Arrays.fill(this.blockBuffer, (byte)0);
    }

    void flushBlock() throws IOException {
        if (this.debug) {
            System.err.println("TarBuffer.flushBlock() called.");
        }
        if (this.outStream == null) {
            throw new IOException("writing to an input buffer");
        }
        if (this.currRecIdx > 0) {
            this.writeBlock();
        }
    }

    public void close() throws IOException {
        if (this.debug) {
            System.err.println("TarBuffer.closeBuffer().");
        }
        if (this.outStream != null) {
            this.flushBlock();
            if (this.outStream != System.out && this.outStream != System.err) {
                this.outStream.close();
                this.outStream = null;
            }
        } else if (this.inStream != null) {
            if (this.inStream != System.in) {
                this.inStream.close();
            }
            this.inStream = null;
        }
    }
}

