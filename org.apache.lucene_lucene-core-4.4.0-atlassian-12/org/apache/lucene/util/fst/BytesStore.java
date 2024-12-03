/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.ForwardBytesReader;
import org.apache.lucene.util.fst.ReverseBytesReader;

class BytesStore
extends DataOutput {
    private final List<byte[]> blocks = new ArrayList<byte[]>();
    private final int blockSize;
    private final int blockBits;
    private final int blockMask;
    private byte[] current;
    private int nextWrite;

    public BytesStore(int blockBits) {
        this.blockBits = blockBits;
        this.blockSize = 1 << blockBits;
        this.blockMask = this.blockSize - 1;
        this.nextWrite = this.blockSize;
    }

    public BytesStore(DataInput in, long numBytes, int maxBlockSize) throws IOException {
        int chunk;
        int blockSize = 2;
        int blockBits = 1;
        while ((long)blockSize < numBytes && blockSize < maxBlockSize) {
            blockSize *= 2;
            ++blockBits;
        }
        this.blockBits = blockBits;
        this.blockSize = blockSize;
        this.blockMask = blockSize - 1;
        for (long left = numBytes; left > 0L; left -= (long)chunk) {
            chunk = (int)Math.min((long)blockSize, left);
            byte[] block = new byte[chunk];
            in.readBytes(block, 0, block.length);
            this.blocks.add(block);
        }
        this.nextWrite = this.blocks.get(this.blocks.size() - 1).length;
    }

    public void writeByte(int dest, byte b) {
        int blockIndex = dest >> this.blockBits;
        byte[] block = this.blocks.get(blockIndex);
        block[dest & this.blockMask] = b;
    }

    @Override
    public void writeByte(byte b) {
        if (this.nextWrite == this.blockSize) {
            this.current = new byte[this.blockSize];
            this.blocks.add(this.current);
            this.nextWrite = 0;
        }
        this.current[this.nextWrite++] = b;
    }

    @Override
    public void writeBytes(byte[] b, int offset, int len) {
        while (len > 0) {
            int chunk = this.blockSize - this.nextWrite;
            if (len <= chunk) {
                System.arraycopy(b, offset, this.current, this.nextWrite, len);
                this.nextWrite += len;
                break;
            }
            if (chunk > 0) {
                System.arraycopy(b, offset, this.current, this.nextWrite, chunk);
                offset += chunk;
                len -= chunk;
            }
            this.current = new byte[this.blockSize];
            this.blocks.add(this.current);
            this.nextWrite = 0;
        }
    }

    int getBlockBits() {
        return this.blockBits;
    }

    void writeBytes(long dest, byte[] b, int offset, int len) {
        assert (dest + (long)len <= this.getPosition()) : "dest=" + dest + " pos=" + this.getPosition() + " len=" + len;
        long end = dest + (long)len;
        int blockIndex = (int)(end >> this.blockBits);
        int downTo = (int)(end & (long)this.blockMask);
        if (downTo == 0) {
            --blockIndex;
            downTo = this.blockSize;
        }
        byte[] block = this.blocks.get(blockIndex);
        while (len > 0) {
            if (len <= downTo) {
                System.arraycopy(b, offset, block, downTo - len, len);
                break;
            }
            System.arraycopy(b, offset + (len -= downTo), block, 0, downTo);
            block = this.blocks.get(--blockIndex);
            downTo = this.blockSize;
        }
    }

    public void copyBytes(long src, long dest, int len) {
        assert (src < dest);
        long end = src + (long)len;
        int blockIndex = (int)(end >> this.blockBits);
        int downTo = (int)(end & (long)this.blockMask);
        if (downTo == 0) {
            --blockIndex;
            downTo = this.blockSize;
        }
        byte[] block = this.blocks.get(blockIndex);
        while (len > 0) {
            if (len <= downTo) {
                this.writeBytes(dest, block, downTo - len, len);
                break;
            }
            this.writeBytes(dest + (long)(len -= downTo), block, 0, downTo);
            block = this.blocks.get(--blockIndex);
            downTo = this.blockSize;
        }
    }

    public void writeInt(long pos, int value) {
        int blockIndex = (int)(pos >> this.blockBits);
        int upto = (int)(pos & (long)this.blockMask);
        byte[] block = this.blocks.get(blockIndex);
        int shift = 24;
        for (int i = 0; i < 4; ++i) {
            block[upto++] = (byte)(value >> shift);
            shift -= 8;
            if (upto != this.blockSize) continue;
            upto = 0;
            block = this.blocks.get(++blockIndex);
        }
    }

    public void reverse(long srcPos, long destPos) {
        assert (srcPos < destPos);
        assert (destPos < this.getPosition());
        int srcBlockIndex = (int)(srcPos >> this.blockBits);
        int src = (int)(srcPos & (long)this.blockMask);
        byte[] srcBlock = this.blocks.get(srcBlockIndex);
        int destBlockIndex = (int)(destPos >> this.blockBits);
        int dest = (int)(destPos & (long)this.blockMask);
        byte[] destBlock = this.blocks.get(destBlockIndex);
        int limit = (int)(destPos - srcPos + 1L) / 2;
        for (int i = 0; i < limit; ++i) {
            byte b = srcBlock[src];
            srcBlock[src] = destBlock[dest];
            destBlock[dest] = b;
            if (++src == this.blockSize) {
                srcBlock = this.blocks.get(++srcBlockIndex);
                src = 0;
            }
            if (--dest != -1) continue;
            destBlock = this.blocks.get(--destBlockIndex);
            dest = this.blockSize - 1;
        }
    }

    public void skipBytes(int len) {
        while (len > 0) {
            int chunk = this.blockSize - this.nextWrite;
            if (len <= chunk) {
                this.nextWrite += len;
                break;
            }
            len -= chunk;
            this.current = new byte[this.blockSize];
            this.blocks.add(this.current);
            this.nextWrite = 0;
        }
    }

    public long getPosition() {
        return ((long)this.blocks.size() - 1L) * (long)this.blockSize + (long)this.nextWrite;
    }

    public void truncate(long newLen) {
        assert (newLen <= this.getPosition());
        assert (newLen >= 0L);
        int blockIndex = (int)(newLen >> this.blockBits);
        this.nextWrite = (int)(newLen & (long)this.blockMask);
        if (this.nextWrite == 0) {
            --blockIndex;
            this.nextWrite = this.blockSize;
        }
        this.blocks.subList(blockIndex + 1, this.blocks.size()).clear();
        this.current = (byte[])(newLen == 0L ? null : this.blocks.get(blockIndex));
        assert (newLen == this.getPosition());
    }

    public void finish() {
        if (this.current != null) {
            byte[] lastBuffer = new byte[this.nextWrite];
            System.arraycopy(this.current, 0, lastBuffer, 0, this.nextWrite);
            this.blocks.set(this.blocks.size() - 1, lastBuffer);
            this.current = null;
        }
    }

    public void writeTo(DataOutput out) throws IOException {
        for (byte[] block : this.blocks) {
            out.writeBytes(block, 0, block.length);
        }
    }

    public FST.BytesReader getForwardReader() {
        if (this.blocks.size() == 1) {
            return new ForwardBytesReader(this.blocks.get(0));
        }
        return new FST.BytesReader(){
            private byte[] current;
            private int nextBuffer;
            private int nextRead;
            {
                this.nextRead = BytesStore.this.blockSize;
            }

            @Override
            public byte readByte() {
                if (this.nextRead == BytesStore.this.blockSize) {
                    this.current = (byte[])BytesStore.this.blocks.get(this.nextBuffer++);
                    this.nextRead = 0;
                }
                return this.current[this.nextRead++];
            }

            @Override
            public void skipBytes(int count) {
                this.setPosition(this.getPosition() + (long)count);
            }

            @Override
            public void readBytes(byte[] b, int offset, int len) {
                while (len > 0) {
                    int chunkLeft = BytesStore.this.blockSize - this.nextRead;
                    if (len <= chunkLeft) {
                        System.arraycopy(this.current, this.nextRead, b, offset, len);
                        this.nextRead += len;
                        break;
                    }
                    if (chunkLeft > 0) {
                        System.arraycopy(this.current, this.nextRead, b, offset, chunkLeft);
                        offset += chunkLeft;
                        len -= chunkLeft;
                    }
                    this.current = (byte[])BytesStore.this.blocks.get(this.nextBuffer++);
                    this.nextRead = 0;
                }
            }

            @Override
            public long getPosition() {
                return ((long)this.nextBuffer - 1L) * (long)BytesStore.this.blockSize + (long)this.nextRead;
            }

            @Override
            public void setPosition(long pos) {
                int bufferIndex = (int)(pos >> BytesStore.this.blockBits);
                this.nextBuffer = bufferIndex + 1;
                this.current = (byte[])BytesStore.this.blocks.get(bufferIndex);
                this.nextRead = (int)(pos & (long)BytesStore.this.blockMask);
                assert (this.getPosition() == pos);
            }

            @Override
            public boolean reversed() {
                return false;
            }
        };
    }

    public FST.BytesReader getReverseReader() {
        return this.getReverseReader(true);
    }

    FST.BytesReader getReverseReader(boolean allowSingle) {
        if (allowSingle && this.blocks.size() == 1) {
            return new ReverseBytesReader(this.blocks.get(0));
        }
        return new FST.BytesReader(){
            private byte[] current;
            private int nextBuffer;
            private int nextRead;
            {
                this.current = BytesStore.this.blocks.size() == 0 ? null : (byte[])BytesStore.this.blocks.get(0);
                this.nextBuffer = -1;
                this.nextRead = 0;
            }

            @Override
            public byte readByte() {
                if (this.nextRead == -1) {
                    this.current = (byte[])BytesStore.this.blocks.get(this.nextBuffer--);
                    this.nextRead = BytesStore.this.blockSize - 1;
                }
                return this.current[this.nextRead--];
            }

            @Override
            public void skipBytes(int count) {
                this.setPosition(this.getPosition() - (long)count);
            }

            @Override
            public void readBytes(byte[] b, int offset, int len) {
                for (int i = 0; i < len; ++i) {
                    b[offset + i] = this.readByte();
                }
            }

            @Override
            public long getPosition() {
                return ((long)this.nextBuffer + 1L) * (long)BytesStore.this.blockSize + (long)this.nextRead;
            }

            @Override
            public void setPosition(long pos) {
                int bufferIndex = (int)(pos >> BytesStore.this.blockBits);
                this.nextBuffer = bufferIndex - 1;
                this.current = (byte[])BytesStore.this.blocks.get(bufferIndex);
                this.nextRead = (int)(pos & (long)BytesStore.this.blockMask);
                assert (this.getPosition() == pos) : "pos=" + pos + " getPos()=" + this.getPosition();
            }

            @Override
            public boolean reversed() {
                return true;
            }
        };
    }
}

