/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.store.DataOutput;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.util.BytesRef;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PagedBytes {
    private final List<byte[]> blocks = new ArrayList<byte[]>();
    private final List<Integer> blockEnd = new ArrayList<Integer>();
    private final int blockSize;
    private final int blockBits;
    private final int blockMask;
    private boolean didSkipBytes;
    private boolean frozen;
    private int upto;
    private byte[] currentBlock;
    private static final byte[] EMPTY_BYTES = new byte[0];

    public PagedBytes(int blockBits) {
        this.blockSize = 1 << blockBits;
        this.blockBits = blockBits;
        this.blockMask = this.blockSize - 1;
        this.upto = this.blockSize;
    }

    public void copy(IndexInput in, long byteCount) throws IOException {
        while (byteCount > 0L) {
            int left = this.blockSize - this.upto;
            if (left == 0) {
                if (this.currentBlock != null) {
                    this.blocks.add(this.currentBlock);
                    this.blockEnd.add(this.upto);
                }
                this.currentBlock = new byte[this.blockSize];
                this.upto = 0;
                left = this.blockSize;
            }
            if ((long)left < byteCount) {
                in.readBytes(this.currentBlock, this.upto, left, false);
                this.upto = this.blockSize;
                byteCount -= (long)left;
                continue;
            }
            in.readBytes(this.currentBlock, this.upto, (int)byteCount, false);
            this.upto = (int)((long)this.upto + byteCount);
            break;
        }
    }

    public void copy(BytesRef bytes) throws IOException {
        int byteCount = bytes.length;
        int bytesUpto = bytes.offset;
        while (byteCount > 0) {
            int left = this.blockSize - this.upto;
            if (left == 0) {
                if (this.currentBlock != null) {
                    this.blocks.add(this.currentBlock);
                    this.blockEnd.add(this.upto);
                }
                this.currentBlock = new byte[this.blockSize];
                this.upto = 0;
                left = this.blockSize;
            }
            if (left < byteCount) {
                System.arraycopy(bytes.bytes, bytesUpto, this.currentBlock, this.upto, left);
                this.upto = this.blockSize;
                byteCount -= left;
                bytesUpto += left;
                continue;
            }
            System.arraycopy(bytes.bytes, bytesUpto, this.currentBlock, this.upto, byteCount);
            this.upto += byteCount;
            break;
        }
    }

    public void copy(BytesRef bytes, BytesRef out) throws IOException {
        int left = this.blockSize - this.upto;
        if (bytes.length > left || this.currentBlock == null) {
            if (this.currentBlock != null) {
                this.blocks.add(this.currentBlock);
                this.blockEnd.add(this.upto);
                this.didSkipBytes = true;
            }
            this.currentBlock = new byte[this.blockSize];
            this.upto = 0;
            left = this.blockSize;
            assert (bytes.length <= this.blockSize);
        }
        out.bytes = this.currentBlock;
        out.offset = this.upto;
        out.length = bytes.length;
        System.arraycopy(bytes.bytes, bytes.offset, this.currentBlock, this.upto, bytes.length);
        this.upto += bytes.length;
    }

    public Reader freeze(boolean trim) {
        if (this.frozen) {
            throw new IllegalStateException("already frozen");
        }
        if (this.didSkipBytes) {
            throw new IllegalStateException("cannot freeze when copy(BytesRef, BytesRef) was used");
        }
        if (trim && this.upto < this.blockSize) {
            byte[] newBlock = new byte[this.upto];
            System.arraycopy(this.currentBlock, 0, newBlock, 0, this.upto);
            this.currentBlock = newBlock;
        }
        if (this.currentBlock == null) {
            this.currentBlock = EMPTY_BYTES;
        }
        this.blocks.add(this.currentBlock);
        this.blockEnd.add(this.upto);
        this.frozen = true;
        this.currentBlock = null;
        return new Reader(this);
    }

    public long getPointer() {
        if (this.currentBlock == null) {
            return 0L;
        }
        return (long)this.blocks.size() * (long)this.blockSize + (long)this.upto;
    }

    public long copyUsingLengthPrefix(BytesRef bytes) throws IOException {
        if (bytes.length >= 32768) {
            throw new IllegalArgumentException("max length is 32767 (got " + bytes.length + ")");
        }
        if (this.upto + bytes.length + 2 > this.blockSize) {
            if (bytes.length + 2 > this.blockSize) {
                throw new IllegalArgumentException("block size " + this.blockSize + " is too small to store length " + bytes.length + " bytes");
            }
            if (this.currentBlock != null) {
                this.blocks.add(this.currentBlock);
                this.blockEnd.add(this.upto);
            }
            this.currentBlock = new byte[this.blockSize];
            this.upto = 0;
        }
        long pointer = this.getPointer();
        if (bytes.length < 128) {
            this.currentBlock[this.upto++] = (byte)bytes.length;
        } else {
            this.currentBlock[this.upto++] = (byte)(0x80 | bytes.length >> 8);
            this.currentBlock[this.upto++] = (byte)(bytes.length & 0xFF);
        }
        System.arraycopy(bytes.bytes, bytes.offset, this.currentBlock, this.upto, bytes.length);
        this.upto += bytes.length;
        return pointer;
    }

    public PagedBytesDataInput getDataInput() {
        if (!this.frozen) {
            throw new IllegalStateException("must call freeze() before getDataInput");
        }
        return new PagedBytesDataInput();
    }

    public PagedBytesDataOutput getDataOutput() {
        if (this.frozen) {
            throw new IllegalStateException("cannot get DataOutput after freeze()");
        }
        return new PagedBytesDataOutput();
    }

    static /* synthetic */ byte[] access$602(PagedBytes x0, byte[] x1) {
        x0.currentBlock = x1;
        return x1;
    }

    public final class PagedBytesDataOutput
    extends DataOutput {
        public void writeByte(byte b) {
            if (PagedBytes.this.upto == PagedBytes.this.blockSize) {
                if (PagedBytes.this.currentBlock != null) {
                    PagedBytes.this.blocks.add(PagedBytes.this.currentBlock);
                    PagedBytes.this.blockEnd.add(PagedBytes.this.upto);
                }
                PagedBytes.access$602(PagedBytes.this, new byte[PagedBytes.this.blockSize]);
                PagedBytes.this.upto = 0;
            }
            ((PagedBytes)PagedBytes.this).currentBlock[((PagedBytes)PagedBytes.this).upto++] = b;
        }

        public void writeBytes(byte[] b, int offset, int length) throws IOException {
            int left;
            assert (b.length >= offset + length);
            if (length == 0) {
                return;
            }
            if (PagedBytes.this.upto == PagedBytes.this.blockSize) {
                if (PagedBytes.this.currentBlock != null) {
                    PagedBytes.this.blocks.add(PagedBytes.this.currentBlock);
                    PagedBytes.this.blockEnd.add(PagedBytes.this.upto);
                }
                PagedBytes.access$602(PagedBytes.this, new byte[PagedBytes.this.blockSize]);
                PagedBytes.this.upto = 0;
            }
            int offsetEnd = offset + length;
            while (true) {
                left = offsetEnd - offset;
                int blockLeft = PagedBytes.this.blockSize - PagedBytes.this.upto;
                if (blockLeft >= left) break;
                System.arraycopy(b, offset, PagedBytes.this.currentBlock, PagedBytes.this.upto, blockLeft);
                PagedBytes.this.blocks.add(PagedBytes.this.currentBlock);
                PagedBytes.this.blockEnd.add(PagedBytes.this.blockSize);
                PagedBytes.access$602(PagedBytes.this, new byte[PagedBytes.this.blockSize]);
                PagedBytes.this.upto = 0;
                offset += blockLeft;
            }
            System.arraycopy(b, offset, PagedBytes.this.currentBlock, PagedBytes.this.upto, left);
            PagedBytes.this.upto += left;
        }

        public long getPosition() {
            if (PagedBytes.this.currentBlock == null) {
                return 0L;
            }
            return (long)PagedBytes.this.blocks.size() * (long)PagedBytes.this.blockSize + (long)PagedBytes.this.upto;
        }
    }

    public final class PagedBytesDataInput
    extends DataInput {
        private int currentBlockIndex;
        private int currentBlockUpto;
        private byte[] currentBlock;

        PagedBytesDataInput() {
            this.currentBlock = (byte[])PagedBytes.this.blocks.get(0);
        }

        public Object clone() {
            PagedBytesDataInput clone = PagedBytes.this.getDataInput();
            clone.setPosition(this.getPosition());
            return clone;
        }

        public long getPosition() {
            return (long)this.currentBlockIndex * (long)PagedBytes.this.blockSize + (long)this.currentBlockUpto;
        }

        public void setPosition(long pos) {
            this.currentBlockIndex = (int)(pos >> PagedBytes.this.blockBits);
            this.currentBlock = (byte[])PagedBytes.this.blocks.get(this.currentBlockIndex);
            this.currentBlockUpto = (int)(pos & (long)PagedBytes.this.blockMask);
        }

        public byte readByte() {
            if (this.currentBlockUpto == PagedBytes.this.blockSize) {
                this.nextBlock();
            }
            return this.currentBlock[this.currentBlockUpto++];
        }

        public void readBytes(byte[] b, int offset, int len) {
            int left;
            int blockLeft;
            assert (b.length >= offset + len);
            int offsetEnd = offset + len;
            while ((blockLeft = PagedBytes.this.blockSize - this.currentBlockUpto) < (left = offsetEnd - offset)) {
                System.arraycopy(this.currentBlock, this.currentBlockUpto, b, offset, blockLeft);
                this.nextBlock();
                offset += blockLeft;
            }
            System.arraycopy(this.currentBlock, this.currentBlockUpto, b, offset, left);
            this.currentBlockUpto += left;
        }

        private void nextBlock() {
            ++this.currentBlockIndex;
            this.currentBlockUpto = 0;
            this.currentBlock = (byte[])PagedBytes.this.blocks.get(this.currentBlockIndex);
        }
    }

    public static final class Reader {
        private final byte[][] blocks;
        private final int[] blockEnds;
        private final int blockBits;
        private final int blockMask;
        private final int blockSize;

        public Reader(PagedBytes pagedBytes) {
            int i;
            this.blocks = new byte[pagedBytes.blocks.size()][];
            for (i = 0; i < this.blocks.length; ++i) {
                this.blocks[i] = (byte[])pagedBytes.blocks.get(i);
            }
            this.blockEnds = new int[this.blocks.length];
            for (i = 0; i < this.blockEnds.length; ++i) {
                this.blockEnds[i] = (Integer)pagedBytes.blockEnd.get(i);
            }
            this.blockBits = pagedBytes.blockBits;
            this.blockMask = pagedBytes.blockMask;
            this.blockSize = pagedBytes.blockSize;
        }

        public BytesRef fillSlice(BytesRef b, long start, int length) {
            assert (length >= 0) : "length=" + length;
            assert (length <= this.blockSize + 1);
            int index = (int)(start >> this.blockBits);
            int offset = (int)(start & (long)this.blockMask);
            b.length = length;
            if (this.blockSize - offset >= length) {
                b.bytes = this.blocks[index];
                b.offset = offset;
            } else {
                b.bytes = new byte[length];
                b.offset = 0;
                System.arraycopy(this.blocks[index], offset, b.bytes, 0, this.blockSize - offset);
                System.arraycopy(this.blocks[1 + index], 0, b.bytes, this.blockSize - offset, length - (this.blockSize - offset));
            }
            return b;
        }

        public BytesRef fill(BytesRef b, long start) {
            int index = (int)(start >> this.blockBits);
            b.bytes = this.blocks[index];
            byte[] block = b.bytes;
            int offset = (int)(start & (long)this.blockMask);
            if ((block[offset] & 0x80) == 0) {
                b.length = block[offset];
                b.offset = offset + 1;
            } else {
                b.length = (block[offset] & 0x7F) << 8 | block[1 + offset] & 0xFF;
                b.offset = offset + 2;
                assert (b.length > 0);
            }
            return b;
        }

        public int fillAndGetIndex(BytesRef b, long start) {
            int index = (int)(start >> this.blockBits);
            b.bytes = this.blocks[index];
            byte[] block = b.bytes;
            int offset = (int)(start & (long)this.blockMask);
            if ((block[offset] & 0x80) == 0) {
                b.length = block[offset];
                b.offset = offset + 1;
            } else {
                b.length = (block[offset] & 0x7F) << 8 | block[1 + offset] & 0xFF;
                b.offset = offset + 2;
                assert (b.length > 0);
            }
            return index;
        }

        public long fillAndGetStart(BytesRef b, long start) {
            int index = (int)(start >> this.blockBits);
            b.bytes = this.blocks[index];
            byte[] block = b.bytes;
            int offset = (int)(start & (long)this.blockMask);
            if ((block[offset] & 0x80) == 0) {
                b.length = block[offset];
                b.offset = offset + 1;
                start += 1L + (long)b.length;
            } else {
                b.length = (block[offset] & 0x7F) << 8 | block[1 + offset] & 0xFF;
                b.offset = offset + 2;
                start += 2L + (long)b.length;
                assert (b.length > 0);
            }
            return start;
        }

        public BytesRef fillSliceWithPrefix(BytesRef b, long start) {
            int length;
            int index = (int)(start >> this.blockBits);
            byte[] block = this.blocks[index];
            int offset = (int)(start & (long)this.blockMask);
            if ((block[offset] & 0x80) == 0) {
                length = block[offset];
                ++offset;
            } else {
                length = (block[offset] & 0x7F) << 8 | block[1 + offset] & 0xFF;
                offset += 2;
                assert (length > 0);
            }
            assert (length >= 0) : "length=" + length;
            b.length = length;
            if (this.blockSize - offset >= length) {
                b.offset = offset;
                b.bytes = this.blocks[index];
            } else {
                b.bytes = new byte[length];
                b.offset = 0;
                System.arraycopy(this.blocks[index], offset, b.bytes, 0, this.blockSize - offset);
                System.arraycopy(this.blocks[1 + index], 0, b.bytes, this.blockSize - offset, length - (this.blockSize - offset));
            }
            return b;
        }

        public byte[][] getBlocks() {
            return this.blocks;
        }

        public int[] getBlockEnds() {
            return this.blockEnds;
        }
    }
}

