/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io.buffer;

import java.nio.ByteBuffer;
import java.util.List;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.util.Assert;

public class DefaultDataBufferFactory
implements DataBufferFactory {
    public static final int DEFAULT_INITIAL_CAPACITY = 256;
    private final boolean preferDirect;
    private final int defaultInitialCapacity;

    public DefaultDataBufferFactory() {
        this(false);
    }

    public DefaultDataBufferFactory(boolean preferDirect) {
        this(preferDirect, 256);
    }

    public DefaultDataBufferFactory(boolean preferDirect, int defaultInitialCapacity) {
        Assert.isTrue(defaultInitialCapacity > 0, "'defaultInitialCapacity' should be larger than 0");
        this.preferDirect = preferDirect;
        this.defaultInitialCapacity = defaultInitialCapacity;
    }

    @Override
    public DefaultDataBuffer allocateBuffer() {
        return this.allocateBuffer(this.defaultInitialCapacity);
    }

    @Override
    public DefaultDataBuffer allocateBuffer(int initialCapacity) {
        ByteBuffer byteBuffer = this.preferDirect ? ByteBuffer.allocateDirect(initialCapacity) : ByteBuffer.allocate(initialCapacity);
        return DefaultDataBuffer.fromEmptyByteBuffer(this, byteBuffer);
    }

    @Override
    public DefaultDataBuffer wrap(ByteBuffer byteBuffer) {
        ByteBuffer sliced = byteBuffer.slice();
        return DefaultDataBuffer.fromFilledByteBuffer(this, sliced);
    }

    @Override
    public DataBuffer wrap(byte[] bytes) {
        ByteBuffer wrapper = ByteBuffer.wrap(bytes);
        return DefaultDataBuffer.fromFilledByteBuffer(this, wrapper);
    }

    @Override
    public DataBuffer join(List<? extends DataBuffer> dataBuffers) {
        Assert.notEmpty(dataBuffers, "'dataBuffers' must not be empty");
        int capacity = dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum();
        DefaultDataBuffer dataBuffer = this.allocateBuffer(capacity);
        DataBuffer result = dataBuffers.stream().map(o -> o).reduce(dataBuffer, (rec$, xva$0) -> ((DataBuffer)rec$).write((DataBuffer)xva$0));
        dataBuffers.forEach(DataBufferUtils::release);
        return result;
    }

    public String toString() {
        return "DefaultDataBufferFactory (preferDirect=" + this.preferDirect + ")";
    }
}

