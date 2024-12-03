/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryData;
import org.apache.avro.io.BufferedBinaryEncoder;

public class BlockingBinaryEncoder
extends BufferedBinaryEncoder {
    private byte[] buf;
    private int pos;
    private BlockedValue[] blockStack;
    private int stackTop = -1;
    private static final int STACK_STEP = 10;
    private byte[] headerBuffer = new byte[12];

    private boolean check() {
        assert (this.buf != null);
        assert (0 <= this.pos);
        assert (this.pos <= this.buf.length) : this.pos + " " + this.buf.length;
        assert (this.blockStack != null);
        BlockedValue prev = null;
        for (int i = 0; i <= this.stackTop; ++i) {
            BlockedValue v = this.blockStack[i];
            v.check(prev, this.pos);
            prev = v;
        }
        return true;
    }

    BlockingBinaryEncoder(OutputStream out, int blockBufferSize, int binaryEncoderBufferSize) {
        super(out, binaryEncoderBufferSize);
        this.buf = new byte[blockBufferSize];
        this.pos = 0;
        this.blockStack = new BlockedValue[0];
        this.expandStack();
        BlockedValue bv = this.blockStack[++this.stackTop];
        bv.type = null;
        bv.state = BlockedValue.State.ROOT;
        bv.lastFullItem = 0;
        bv.start = 0;
        bv.items = 1;
        assert (this.check());
    }

    private void expandStack() {
        int oldLength = this.blockStack.length;
        this.blockStack = Arrays.copyOf(this.blockStack, this.blockStack.length + 10);
        for (int i = oldLength; i < this.blockStack.length; ++i) {
            this.blockStack[i] = new BlockedValue();
        }
    }

    BlockingBinaryEncoder configure(OutputStream out, int blockBufferSize, int binaryEncoderBufferSize) {
        super.configure(out, binaryEncoderBufferSize);
        this.pos = 0;
        this.stackTop = 0;
        if (null == this.buf || this.buf.length != blockBufferSize) {
            this.buf = new byte[blockBufferSize];
        }
        assert (this.check());
        return this;
    }

    @Override
    public void flush() throws IOException {
        BlockedValue bv = this.blockStack[this.stackTop];
        if (bv.state == BlockedValue.State.ROOT) {
            super.writeFixed(this.buf, 0, this.pos);
            this.pos = 0;
        } else {
            while (bv.state != BlockedValue.State.OVERFLOW) {
                this.compact();
            }
        }
        super.flush();
        assert (this.check());
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        this.ensureBounds(1);
        this.pos += BinaryData.encodeBoolean(b, this.buf, this.pos);
    }

    @Override
    public void writeInt(int n) throws IOException {
        this.ensureBounds(5);
        this.pos += BinaryData.encodeInt(n, this.buf, this.pos);
    }

    @Override
    public void writeLong(long n) throws IOException {
        this.ensureBounds(10);
        this.pos += BinaryData.encodeLong(n, this.buf, this.pos);
    }

    @Override
    public void writeFloat(float f) throws IOException {
        this.ensureBounds(4);
        this.pos += BinaryData.encodeFloat(f, this.buf, this.pos);
    }

    @Override
    public void writeDouble(double d) throws IOException {
        this.ensureBounds(8);
        this.pos += BinaryData.encodeDouble(d, this.buf, this.pos);
    }

    @Override
    public void writeFixed(byte[] bytes, int start, int len) throws IOException {
        this.doWriteBytes(bytes, start, len);
    }

    @Override
    public void writeFixed(ByteBuffer bytes) throws IOException {
        int pos = bytes.position();
        int len = bytes.remaining();
        if (bytes.hasArray()) {
            this.doWriteBytes(bytes.array(), bytes.arrayOffset() + pos, len);
        } else {
            byte[] b = new byte[len];
            bytes.duplicate().get(b, 0, len);
            this.doWriteBytes(b, 0, len);
        }
    }

    @Override
    protected void writeZero() throws IOException {
        this.ensureBounds(1);
        this.buf[this.pos++] = 0;
    }

    @Override
    public void writeArrayStart() throws IOException {
        if (this.stackTop + 1 == this.blockStack.length) {
            this.expandStack();
        }
        BlockedValue bv = this.blockStack[++this.stackTop];
        bv.type = Schema.Type.ARRAY;
        bv.state = BlockedValue.State.REGULAR;
        bv.start = bv.lastFullItem = this.pos;
        bv.items = 0;
        assert (this.check());
    }

    @Override
    public void setItemCount(long itemCount) throws IOException {
        BlockedValue v = this.blockStack[this.stackTop];
        assert (v.type == Schema.Type.ARRAY || v.type == Schema.Type.MAP);
        assert (v.itemsLeftToWrite == 0L);
        v.itemsLeftToWrite = itemCount;
        assert (this.check());
    }

    @Override
    public void startItem() throws IOException {
        if (this.blockStack[this.stackTop].state == BlockedValue.State.OVERFLOW) {
            this.finishOverflow();
        }
        BlockedValue t = this.blockStack[this.stackTop];
        ++t.items;
        t.lastFullItem = this.pos;
        --t.itemsLeftToWrite;
        assert (this.check());
    }

    @Override
    public void writeArrayEnd() throws IOException {
        BlockedValue top = this.blockStack[this.stackTop];
        if (top.type != Schema.Type.ARRAY) {
            throw new AvroTypeException("Called writeArrayEnd outside of an array.");
        }
        if (top.itemsLeftToWrite != 0L) {
            throw new AvroTypeException("Failed to write expected number of array elements.");
        }
        this.endBlockedValue();
        assert (this.check());
    }

    @Override
    public void writeMapStart() throws IOException {
        if (this.stackTop + 1 == this.blockStack.length) {
            this.expandStack();
        }
        BlockedValue bv = this.blockStack[++this.stackTop];
        bv.type = Schema.Type.MAP;
        bv.state = BlockedValue.State.REGULAR;
        bv.start = bv.lastFullItem = this.pos;
        bv.items = 0;
        assert (this.check());
    }

    @Override
    public void writeMapEnd() throws IOException {
        BlockedValue top = this.blockStack[this.stackTop];
        if (top.type != Schema.Type.MAP) {
            throw new AvroTypeException("Called writeMapEnd outside of a map.");
        }
        if (top.itemsLeftToWrite != 0L) {
            throw new AvroTypeException("Failed to read write expected number of array elements.");
        }
        this.endBlockedValue();
        assert (this.check());
    }

    @Override
    public void writeIndex(int unionIndex) throws IOException {
        this.ensureBounds(5);
        this.pos += BinaryData.encodeInt(unionIndex, this.buf, this.pos);
    }

    @Override
    public int bytesBuffered() {
        return this.pos + super.bytesBuffered();
    }

    private void endBlockedValue() throws IOException {
        while (true) {
            assert (this.check());
            BlockedValue t = this.blockStack[this.stackTop];
            assert (t.state != BlockedValue.State.ROOT);
            if (t.state == BlockedValue.State.OVERFLOW) {
                this.finishOverflow();
            }
            assert (t.state == BlockedValue.State.REGULAR);
            if (0 >= t.items) break;
            int byteCount = this.pos - t.start;
            if (t.start == 0 && this.blockStack[this.stackTop - 1].state != BlockedValue.State.REGULAR) {
                super.writeInt(-t.items);
                super.writeInt(byteCount);
                break;
            }
            int headerSize = 0;
            headerSize += BinaryData.encodeInt(-t.items, this.headerBuffer, headerSize);
            if (this.buf.length >= this.pos + (headerSize += BinaryData.encodeInt(byteCount, this.headerBuffer, headerSize))) {
                this.pos += headerSize;
                int m = t.start;
                System.arraycopy(this.buf, m, this.buf, m + headerSize, byteCount);
                System.arraycopy(this.headerBuffer, 0, this.buf, m, headerSize);
                break;
            }
            this.compact();
        }
        --this.stackTop;
        this.ensureBounds(1);
        this.buf[this.pos++] = 0;
        assert (this.check());
        if (this.blockStack[this.stackTop].state == BlockedValue.State.ROOT) {
            this.flush();
        }
    }

    private void finishOverflow() throws IOException {
        BlockedValue s = this.blockStack[this.stackTop];
        if (s.state != BlockedValue.State.OVERFLOW) {
            throw new IllegalStateException("Not an overflow block");
        }
        assert (this.check());
        super.writeFixed(this.buf, 0, this.pos);
        this.pos = 0;
        s.state = BlockedValue.State.REGULAR;
        s.lastFullItem = 0;
        s.start = 0;
        s.items = 0;
        assert (this.check());
    }

    private void ensureBounds(int l) throws IOException {
        while (this.buf.length < this.pos + l) {
            if (this.blockStack[this.stackTop].state == BlockedValue.State.REGULAR) {
                this.compact();
                continue;
            }
            super.writeFixed(this.buf, 0, this.pos);
            this.pos = 0;
        }
    }

    private void doWriteBytes(byte[] bytes, int start, int len) throws IOException {
        if (len < this.buf.length) {
            this.ensureBounds(len);
            System.arraycopy(bytes, start, this.buf, this.pos, len);
            this.pos += len;
        } else {
            this.ensureBounds(this.buf.length);
            assert (this.blockStack[this.stackTop].state == BlockedValue.State.ROOT || this.blockStack[this.stackTop].state == BlockedValue.State.OVERFLOW);
            this.write(bytes, start, len);
        }
    }

    private void write(byte[] b, int off, int len) throws IOException {
        if (this.blockStack[this.stackTop].state == BlockedValue.State.ROOT) {
            super.writeFixed(b, off, len);
        } else {
            assert (this.check());
            while (this.buf.length < this.pos + len) {
                if (this.blockStack[this.stackTop].state == BlockedValue.State.REGULAR) {
                    this.compact();
                    continue;
                }
                super.writeFixed(this.buf, 0, this.pos);
                this.pos = 0;
                if (this.buf.length > len) continue;
                super.writeFixed(b, off, len);
                len = 0;
            }
            System.arraycopy(b, off, this.buf, this.pos, len);
            this.pos += len;
        }
        assert (this.check());
    }

    private void compact() throws IOException {
        int i;
        assert (this.check());
        BlockedValue s = null;
        for (i = 1; i <= this.stackTop; ++i) {
            s = this.blockStack[i];
            if (s.state == BlockedValue.State.REGULAR) break;
        }
        assert (s != null);
        super.writeFixed(this.buf, 0, s.start);
        if (1 < s.items) {
            super.writeInt(-(s.items - 1));
            super.writeInt(s.lastFullItem - s.start);
            super.writeFixed(this.buf, s.start, s.lastFullItem - s.start);
            s.start = s.lastFullItem;
            s.items = 1;
        }
        super.writeInt(1);
        BlockedValue n = i + 1 <= this.stackTop ? this.blockStack[i + 1] : null;
        int end = n == null ? this.pos : n.start;
        super.writeFixed(this.buf, s.lastFullItem, end - s.lastFullItem);
        System.arraycopy(this.buf, end, this.buf, 0, this.pos - end);
        for (int j = i + 1; j <= this.stackTop; ++j) {
            n = this.blockStack[j];
            n.start -= end;
            n.lastFullItem -= end;
        }
        this.pos -= end;
        assert (s.items == 1);
        s.lastFullItem = 0;
        s.start = 0;
        s.state = BlockedValue.State.OVERFLOW;
        assert (this.check());
    }

    private static class BlockedValue {
        public Schema.Type type = null;
        public State state = State.ROOT;
        public int start = 0;
        public int lastFullItem = 0;
        public int items = 1;
        public long itemsLeftToWrite;

        public boolean check(BlockedValue prev, int pos) {
            assert (this.state != State.ROOT || this.type == null);
            assert (this.state == State.ROOT || this.type == Schema.Type.ARRAY || this.type == Schema.Type.MAP);
            assert (0 <= this.items);
            assert (0 != this.items || this.start == pos);
            assert (1 < this.items || this.start == this.lastFullItem);
            assert (this.items <= 1 || this.start <= this.lastFullItem);
            assert (this.lastFullItem <= pos);
            switch (this.state) {
                case ROOT: {
                    assert (this.start == 0);
                    assert (prev == null);
                    break;
                }
                case REGULAR: {
                    assert (this.start >= 0);
                    assert (prev.lastFullItem <= this.start);
                    assert (1 <= prev.items);
                    break;
                }
                case OVERFLOW: {
                    assert (this.start == 0);
                    assert (this.items == 1);
                    assert (prev.state == State.ROOT || prev.state == State.OVERFLOW);
                    break;
                }
            }
            return false;
        }

        public static enum State {
            ROOT,
            REGULAR,
            OVERFLOW;

        }
    }
}

