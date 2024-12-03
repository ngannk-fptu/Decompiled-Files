/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.fileslice.reader;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessReader;
import nonapi.io.github.classgraph.utils.StringUtils;

public class RandomAccessFileChannelReader
implements RandomAccessReader {
    private final FileChannel fileChannel;
    private final long sliceStartPos;
    private final long sliceLength;
    private ByteBuffer reusableByteBuffer;
    private final byte[] scratchArr = new byte[8];
    private final ByteBuffer scratchByteBuf = ByteBuffer.wrap(this.scratchArr);
    private byte[] utf8Bytes;

    public RandomAccessFileChannelReader(FileChannel fileChannel, long sliceStartPos, long sliceLength) {
        this.fileChannel = fileChannel;
        this.sliceStartPos = sliceStartPos;
        this.sliceLength = sliceLength;
    }

    @Override
    public int read(long srcOffset, ByteBuffer dstBuf, int dstBufStart, int numBytes) throws IOException {
        if (numBytes == 0) {
            return 0;
        }
        try {
            if (srcOffset < 0L || numBytes < 0 || (long)numBytes > this.sliceLength - srcOffset) {
                throw new IOException("Read index out of bounds");
            }
            long srcStart = this.sliceStartPos + srcOffset;
            ((Buffer)dstBuf).position(dstBufStart);
            ((Buffer)dstBuf).limit(dstBufStart + numBytes);
            int numBytesRead = this.fileChannel.read(dstBuf, srcStart);
            return numBytesRead == 0 ? -1 : numBytesRead;
        }
        catch (IndexOutOfBoundsException | BufferUnderflowException e) {
            throw new IOException("Read index out of bounds");
        }
    }

    @Override
    public int read(long srcOffset, byte[] dstArr, int dstArrStart, int numBytes) throws IOException {
        if (numBytes == 0) {
            return 0;
        }
        try {
            if (srcOffset < 0L || numBytes < 0 || (long)numBytes > this.sliceLength - srcOffset) {
                throw new IOException("Read index out of bounds");
            }
            if (this.reusableByteBuffer == null || this.reusableByteBuffer.array() != dstArr) {
                this.reusableByteBuffer = ByteBuffer.wrap(dstArr);
            }
            return this.read(srcOffset, this.reusableByteBuffer, dstArrStart, numBytes);
        }
        catch (IndexOutOfBoundsException | BufferUnderflowException e) {
            throw new IOException("Read index out of bounds");
        }
    }

    @Override
    public byte readByte(long offset) throws IOException {
        if (this.read(offset, this.scratchByteBuf, 0, 1) < 1) {
            throw new IOException("Premature EOF");
        }
        return this.scratchArr[0];
    }

    @Override
    public int readUnsignedByte(long offset) throws IOException {
        if (this.read(offset, this.scratchByteBuf, 0, 1) < 1) {
            throw new IOException("Premature EOF");
        }
        return this.scratchArr[0] & 0xFF;
    }

    @Override
    public short readShort(long offset) throws IOException {
        return (short)this.readUnsignedShort(offset);
    }

    @Override
    public int readUnsignedShort(long offset) throws IOException {
        if (this.read(offset, this.scratchByteBuf, 0, 2) < 2) {
            throw new IOException("Premature EOF");
        }
        return (this.scratchArr[1] & 0xFF) << 8 | this.scratchArr[0] & 0xFF;
    }

    @Override
    public int readInt(long offset) throws IOException {
        if (this.read(offset, this.scratchByteBuf, 0, 4) < 4) {
            throw new IOException("Premature EOF");
        }
        return (this.scratchArr[3] & 0xFF) << 24 | (this.scratchArr[2] & 0xFF) << 16 | (this.scratchArr[1] & 0xFF) << 8 | this.scratchArr[0] & 0xFF;
    }

    @Override
    public long readUnsignedInt(long offset) throws IOException {
        return (long)this.readInt(offset) & 0xFFFFFFFFL;
    }

    @Override
    public long readLong(long offset) throws IOException {
        if (this.read(offset, this.scratchByteBuf, 0, 8) < 8) {
            throw new IOException("Premature EOF");
        }
        return ((long)this.scratchArr[7] & 0xFFL) << 56 | ((long)this.scratchArr[6] & 0xFFL) << 48 | ((long)this.scratchArr[5] & 0xFFL) << 40 | ((long)this.scratchArr[4] & 0xFFL) << 32 | ((long)this.scratchArr[3] & 0xFFL) << 24 | ((long)this.scratchArr[2] & 0xFFL) << 16 | ((long)this.scratchArr[1] & 0xFFL) << 8 | (long)this.scratchArr[0] & 0xFFL;
    }

    @Override
    public String readString(long offset, int numBytes, boolean replaceSlashWithDot, boolean stripLSemicolon) throws IOException {
        if (this.utf8Bytes == null || this.utf8Bytes.length < numBytes) {
            this.utf8Bytes = new byte[numBytes];
        }
        if (this.read(offset, this.utf8Bytes, 0, numBytes) < numBytes) {
            throw new IOException("Premature EOF");
        }
        return StringUtils.readString(this.utf8Bytes, 0, numBytes, replaceSlashWithDot, stripLSemicolon);
    }

    @Override
    public String readString(long offset, int numBytes) throws IOException {
        return this.readString(offset, numBytes, false, false);
    }
}

