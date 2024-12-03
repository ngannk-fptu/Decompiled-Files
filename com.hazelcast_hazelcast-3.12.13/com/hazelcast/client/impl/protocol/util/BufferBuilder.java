/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.util;

import com.hazelcast.client.impl.protocol.util.ClientProtocolBuffer;
import com.hazelcast.client.impl.protocol.util.SafeBuffer;
import com.hazelcast.client.impl.protocol.util.UnsafeBuffer;
import com.hazelcast.util.QuickMath;
import java.util.Arrays;

public class BufferBuilder {
    public static final int INITIAL_CAPACITY = 4096;
    private static final String PROP_HAZELCAST_PROTOCOL_UNSAFE = "hazelcast.protocol.unsafe.enabled";
    private static final boolean USE_UNSAFE = Boolean.getBoolean("hazelcast.protocol.unsafe.enabled");
    private final ClientProtocolBuffer protocolBuffer;
    private int position;
    private int capacity;

    public BufferBuilder() {
        this(4096);
    }

    private BufferBuilder(int initialCapacity) {
        this.capacity = QuickMath.nextPowerOfTwo(initialCapacity);
        this.protocolBuffer = USE_UNSAFE ? new UnsafeBuffer(new byte[this.capacity]) : new SafeBuffer(new byte[this.capacity]);
    }

    public int capacity() {
        return this.capacity;
    }

    public int position() {
        return this.position;
    }

    public ClientProtocolBuffer buffer() {
        return this.protocolBuffer;
    }

    public BufferBuilder append(ClientProtocolBuffer srcBuffer, int srcOffset, int length) {
        this.ensureCapacity(length);
        srcBuffer.getBytes(srcOffset, this.protocolBuffer.byteArray(), this.position, length);
        this.position += length;
        return this;
    }

    public static ClientProtocolBuffer createBuffer(byte[] byteArray) {
        if (USE_UNSAFE) {
            return new UnsafeBuffer(byteArray);
        }
        return new SafeBuffer(byteArray);
    }

    private void ensureCapacity(int additionalCapacity) {
        int requiredCapacity = this.position + additionalCapacity;
        if (requiredCapacity < 0) {
            String s = String.format("Insufficient capacity: position=%d additional=%d", this.position, additionalCapacity);
            throw new IllegalStateException(s);
        }
        if (requiredCapacity > this.capacity) {
            int newCapacity = QuickMath.nextPowerOfTwo(requiredCapacity);
            byte[] newBuffer = Arrays.copyOf(this.protocolBuffer.byteArray(), newCapacity);
            this.capacity = newCapacity;
            this.protocolBuffer.wrap(newBuffer);
        }
    }
}

