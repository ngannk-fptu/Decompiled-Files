/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.IntPredicate;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class DefaultDataBuffer
implements DataBuffer {
    private static final int MAX_CAPACITY = Integer.MAX_VALUE;
    private static final int CAPACITY_THRESHOLD = 0x400000;
    private final DefaultDataBufferFactory dataBufferFactory;
    private ByteBuffer byteBuffer;
    private int capacity;
    private int readPosition;
    private int writePosition;

    private DefaultDataBuffer(DefaultDataBufferFactory dataBufferFactory, ByteBuffer byteBuffer) {
        ByteBuffer slice;
        Assert.notNull((Object)dataBufferFactory, "DefaultDataBufferFactory must not be null");
        Assert.notNull((Object)byteBuffer, "ByteBuffer must not be null");
        this.dataBufferFactory = dataBufferFactory;
        this.byteBuffer = slice = byteBuffer.slice();
        this.capacity = slice.remaining();
    }

    static DefaultDataBuffer fromFilledByteBuffer(DefaultDataBufferFactory dataBufferFactory, ByteBuffer byteBuffer) {
        DefaultDataBuffer dataBuffer = new DefaultDataBuffer(dataBufferFactory, byteBuffer);
        dataBuffer.writePosition(byteBuffer.remaining());
        return dataBuffer;
    }

    static DefaultDataBuffer fromEmptyByteBuffer(DefaultDataBufferFactory dataBufferFactory, ByteBuffer byteBuffer) {
        return new DefaultDataBuffer(dataBufferFactory, byteBuffer);
    }

    public ByteBuffer getNativeBuffer() {
        return this.byteBuffer;
    }

    private void setNativeBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
        this.capacity = byteBuffer.remaining();
    }

    @Override
    public DefaultDataBufferFactory factory() {
        return this.dataBufferFactory;
    }

    @Override
    public int indexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull((Object)predicate, "'predicate' must not be null");
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= this.writePosition) {
            return -1;
        }
        for (int i = fromIndex; i < this.writePosition; ++i) {
            byte b = this.byteBuffer.get(i);
            if (!predicate.test(b)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull((Object)predicate, "'predicate' must not be null");
        for (int i = Math.min(fromIndex, this.writePosition - 1); i >= 0; --i) {
            byte b = this.byteBuffer.get(i);
            if (!predicate.test(b)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int readableByteCount() {
        return this.writePosition - this.readPosition;
    }

    @Override
    public int writableByteCount() {
        return this.capacity - this.writePosition;
    }

    @Override
    public int readPosition() {
        return this.readPosition;
    }

    @Override
    public DefaultDataBuffer readPosition(int readPosition) {
        DefaultDataBuffer.assertIndex(readPosition >= 0, "'readPosition' %d must be >= 0", readPosition);
        DefaultDataBuffer.assertIndex(readPosition <= this.writePosition, "'readPosition' %d must be <= %d", readPosition, this.writePosition);
        this.readPosition = readPosition;
        return this;
    }

    @Override
    public int writePosition() {
        return this.writePosition;
    }

    @Override
    public DefaultDataBuffer writePosition(int writePosition) {
        DefaultDataBuffer.assertIndex(writePosition >= this.readPosition, "'writePosition' %d must be >= %d", writePosition, this.readPosition);
        DefaultDataBuffer.assertIndex(writePosition <= this.capacity, "'writePosition' %d must be <= %d", writePosition, this.capacity);
        this.writePosition = writePosition;
        return this;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public DefaultDataBuffer capacity(int newCapacity) {
        Assert.isTrue(newCapacity > 0, String.format("'newCapacity' %d must be higher than 0", newCapacity));
        int readPosition = this.readPosition();
        int writePosition = this.writePosition();
        int oldCapacity = this.capacity();
        if (newCapacity > oldCapacity) {
            ByteBuffer oldBuffer = this.byteBuffer;
            ByteBuffer newBuffer = DefaultDataBuffer.allocate(newCapacity, oldBuffer.isDirect());
            ((Buffer)oldBuffer).position(0).limit(oldBuffer.capacity());
            ((Buffer)newBuffer).position(0).limit(oldBuffer.capacity());
            newBuffer.put(oldBuffer);
            newBuffer.clear();
            this.setNativeBuffer(newBuffer);
        } else if (newCapacity < oldCapacity) {
            ByteBuffer oldBuffer = this.byteBuffer;
            ByteBuffer newBuffer = DefaultDataBuffer.allocate(newCapacity, oldBuffer.isDirect());
            if (readPosition < newCapacity) {
                if (writePosition > newCapacity) {
                    writePosition = newCapacity;
                    this.writePosition(writePosition);
                }
                ((Buffer)oldBuffer).position(readPosition).limit(writePosition);
                ((Buffer)newBuffer).position(readPosition).limit(writePosition);
                newBuffer.put(oldBuffer);
                newBuffer.clear();
            } else {
                this.readPosition(newCapacity);
                this.writePosition(newCapacity);
            }
            this.setNativeBuffer(newBuffer);
        }
        return this;
    }

    private static ByteBuffer allocate(int capacity, boolean direct) {
        return direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
    }

    @Override
    public byte getByte(int index) {
        DefaultDataBuffer.assertIndex(index >= 0, "index %d must be >= 0", index);
        DefaultDataBuffer.assertIndex(index <= this.writePosition - 1, "index %d must be <= %d", index, this.writePosition - 1);
        return this.byteBuffer.get(index);
    }

    @Override
    public byte read() {
        DefaultDataBuffer.assertIndex(this.readPosition <= this.writePosition - 1, "readPosition %d must be <= %d", this.readPosition, this.writePosition - 1);
        int pos = this.readPosition;
        byte b = this.byteBuffer.get(pos);
        this.readPosition = pos + 1;
        return b;
    }

    @Override
    public DefaultDataBuffer read(byte[] destination) {
        Assert.notNull((Object)destination, "'destination' must not be null");
        this.read(destination, 0, destination.length);
        return this;
    }

    @Override
    public DefaultDataBuffer read(byte[] destination, int offset, int length) {
        Assert.notNull((Object)destination, "'destination' must not be null");
        DefaultDataBuffer.assertIndex(this.readPosition <= this.writePosition - length, "readPosition %d and length %d should be smaller than writePosition %d", this.readPosition, length, this.writePosition);
        ByteBuffer tmp = this.byteBuffer.duplicate();
        int limit = this.readPosition + length;
        ((Buffer)tmp).clear().position(this.readPosition).limit(limit);
        tmp.get(destination, offset, length);
        this.readPosition += length;
        return this;
    }

    @Override
    public DefaultDataBuffer write(byte b) {
        this.ensureCapacity(1);
        int pos = this.writePosition;
        this.byteBuffer.put(pos, b);
        this.writePosition = pos + 1;
        return this;
    }

    @Override
    public DefaultDataBuffer write(byte[] source) {
        Assert.notNull((Object)source, "'source' must not be null");
        this.write(source, 0, source.length);
        return this;
    }

    @Override
    public DefaultDataBuffer write(byte[] source, int offset, int length) {
        Assert.notNull((Object)source, "'source' must not be null");
        this.ensureCapacity(length);
        ByteBuffer tmp = this.byteBuffer.duplicate();
        int limit = this.writePosition + length;
        ((Buffer)tmp).clear().position(this.writePosition).limit(limit);
        tmp.put(source, offset, length);
        this.writePosition += length;
        return this;
    }

    @Override
    public DefaultDataBuffer write(DataBuffer ... buffers) {
        if (!ObjectUtils.isEmpty(buffers)) {
            ByteBuffer[] byteBuffers = (ByteBuffer[])Arrays.stream(buffers).map(DataBuffer::asByteBuffer).toArray(ByteBuffer[]::new);
            this.write(byteBuffers);
        }
        return this;
    }

    @Override
    public DefaultDataBuffer write(ByteBuffer ... byteBuffers) {
        Assert.notEmpty((Object[])byteBuffers, "'byteBuffers' must not be empty");
        int capacity = Arrays.stream(byteBuffers).mapToInt(Buffer::remaining).sum();
        this.ensureCapacity(capacity);
        Arrays.stream(byteBuffers).forEach(this::write);
        return this;
    }

    private void write(ByteBuffer source) {
        int length = source.remaining();
        ByteBuffer tmp = this.byteBuffer.duplicate();
        int limit = this.writePosition + source.remaining();
        ((Buffer)tmp).clear().position(this.writePosition).limit(limit);
        tmp.put(source);
        this.writePosition += length;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DefaultDataBuffer slice(int index, int length) {
        this.checkIndex(index, length);
        int oldPosition = this.byteBuffer.position();
        ByteBuffer buffer = this.byteBuffer;
        try {
            ((Buffer)buffer).position(index);
            ByteBuffer slice = this.byteBuffer.slice();
            ((Buffer)slice).limit(length);
            SlicedDefaultDataBuffer slicedDefaultDataBuffer = new SlicedDefaultDataBuffer(slice, this.dataBufferFactory, length);
            return slicedDefaultDataBuffer;
        }
        finally {
            ((Buffer)buffer).position(oldPosition);
        }
    }

    @Override
    public ByteBuffer asByteBuffer() {
        return this.asByteBuffer(this.readPosition, this.readableByteCount());
    }

    @Override
    public ByteBuffer asByteBuffer(int index, int length) {
        ByteBuffer duplicate;
        this.checkIndex(index, length);
        ByteBuffer buffer = duplicate = this.byteBuffer.duplicate();
        ((Buffer)buffer).position(index);
        ((Buffer)buffer).limit(index + length);
        return duplicate.slice();
    }

    @Override
    public InputStream asInputStream() {
        return new DefaultDataBufferInputStream();
    }

    @Override
    public InputStream asInputStream(boolean releaseOnClose) {
        return new DefaultDataBufferInputStream();
    }

    @Override
    public OutputStream asOutputStream() {
        return new DefaultDataBufferOutputStream();
    }

    private void ensureCapacity(int length) {
        if (length <= this.writableByteCount()) {
            return;
        }
        int newCapacity = this.calculateCapacity(this.writePosition + length);
        this.capacity(newCapacity);
    }

    private int calculateCapacity(int neededCapacity) {
        int newCapacity;
        Assert.isTrue(neededCapacity >= 0, "'neededCapacity' must >= 0");
        if (neededCapacity == 0x400000) {
            return 0x400000;
        }
        if (neededCapacity > 0x400000) {
            int newCapacity2 = neededCapacity / 0x400000 * 0x400000;
            newCapacity2 = newCapacity2 > 0x7FBFFFFF ? Integer.MAX_VALUE : (newCapacity2 += 0x400000);
            return newCapacity2;
        }
        for (newCapacity = 64; newCapacity < neededCapacity; newCapacity <<= 1) {
        }
        return Math.min(newCapacity, Integer.MAX_VALUE);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DefaultDataBuffer)) {
            return false;
        }
        DefaultDataBuffer otherBuffer = (DefaultDataBuffer)other;
        return this.readPosition == otherBuffer.readPosition && this.writePosition == otherBuffer.writePosition && this.byteBuffer.equals(otherBuffer.byteBuffer);
    }

    public int hashCode() {
        return this.byteBuffer.hashCode();
    }

    public String toString() {
        return String.format("DefaultDataBuffer (r: %d, w %d, c %d)", this.readPosition, this.writePosition, this.capacity);
    }

    private void checkIndex(int index, int length) {
        DefaultDataBuffer.assertIndex(index >= 0, "index %d must be >= 0", index);
        DefaultDataBuffer.assertIndex(length >= 0, "length %d must be >= 0", index);
        DefaultDataBuffer.assertIndex(index <= this.capacity, "index %d must be <= %d", index, this.capacity);
        DefaultDataBuffer.assertIndex(length <= this.capacity, "length %d must be <= %d", index, this.capacity);
    }

    private static void assertIndex(boolean expression, String format, Object ... args) {
        if (!expression) {
            String message = String.format(format, args);
            throw new IndexOutOfBoundsException(message);
        }
    }

    private static class SlicedDefaultDataBuffer
    extends DefaultDataBuffer {
        SlicedDefaultDataBuffer(ByteBuffer byteBuffer, DefaultDataBufferFactory dataBufferFactory, int length) {
            super(dataBufferFactory, byteBuffer);
            this.writePosition(length);
        }

        @Override
        public DefaultDataBuffer capacity(int newCapacity) {
            throw new UnsupportedOperationException("Changing the capacity of a sliced buffer is not supported");
        }
    }

    private class DefaultDataBufferOutputStream
    extends OutputStream {
        private DefaultDataBufferOutputStream() {
        }

        @Override
        public void write(int b) throws IOException {
            DefaultDataBuffer.this.write((byte)b);
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            DefaultDataBuffer.this.write(bytes, off, len);
        }
    }

    private class DefaultDataBufferInputStream
    extends InputStream {
        private DefaultDataBufferInputStream() {
        }

        @Override
        public int available() {
            return DefaultDataBuffer.this.readableByteCount();
        }

        @Override
        public int read() {
            return this.available() > 0 ? DefaultDataBuffer.this.read() & 0xFF : -1;
        }

        @Override
        public int read(byte[] bytes, int off, int len) throws IOException {
            int available = this.available();
            if (available > 0) {
                len = Math.min(len, available);
                DefaultDataBuffer.this.read(bytes, off, len);
                return len;
            }
            return -1;
        }
    }
}

