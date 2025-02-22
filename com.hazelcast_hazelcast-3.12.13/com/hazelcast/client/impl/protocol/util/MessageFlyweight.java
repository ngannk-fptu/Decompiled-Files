/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.util;

import com.hazelcast.client.impl.protocol.util.ClientProtocolBuffer;
import com.hazelcast.client.impl.protocol.util.SafeBuffer;
import com.hazelcast.client.impl.protocol.util.UnsafeBuffer;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.nio.serialization.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MessageFlyweight {
    private static final long LONG_MASK = 0xFFFFFFFFL;
    private static final int INT_MASK = 65535;
    private static final short SHORT_MASK = 255;
    protected ClientProtocolBuffer buffer;
    private int offset = 0;
    private int index;

    public MessageFlyweight wrap(byte[] buffer, int offset, boolean useUnsafe) {
        this.buffer = useUnsafe ? new UnsafeBuffer(buffer) : new SafeBuffer(buffer);
        this.offset = offset;
        this.index = 0;
        return this;
    }

    public int index() {
        return this.index;
    }

    public MessageFlyweight index(int index) {
        this.index = index;
        return this;
    }

    public ClientProtocolBuffer buffer() {
        return this.buffer;
    }

    public MessageFlyweight set(boolean value) {
        this.buffer.putByte(this.index + this.offset, (byte)(value ? 1 : 0));
        ++this.index;
        return this;
    }

    public MessageFlyweight set(byte value) {
        this.buffer.putByte(this.index + this.offset, value);
        ++this.index;
        return this;
    }

    public MessageFlyweight set(int value) {
        this.buffer.putInt(this.index + this.offset, value);
        this.index += 4;
        return this;
    }

    public MessageFlyweight set(long value) {
        this.buffer.putLong(this.index + this.offset, value);
        this.index += 8;
        return this;
    }

    public MessageFlyweight set(String value) {
        this.index += this.buffer.putStringUtf8(this.index + this.offset, value);
        return this;
    }

    public MessageFlyweight set(Data data) {
        int length = data.totalSize();
        this.set(length);
        data.copyTo(this.buffer.byteArray(), this.index);
        this.index += length;
        return this;
    }

    public MessageFlyweight set(byte[] value) {
        int length = value.length;
        this.set(length);
        this.buffer.putBytes(this.index + this.offset, value);
        this.index += length;
        return this;
    }

    public MessageFlyweight set(Collection<Data> value) {
        int length = value.size();
        this.set(length);
        for (Data v : value) {
            this.set(v);
        }
        return this;
    }

    public MessageFlyweight set(Map.Entry<Data, Data> entry) {
        return this.set(entry.getKey()).set(entry.getValue());
    }

    public boolean getBoolean() {
        byte result = this.buffer.getByte(this.index + this.offset);
        ++this.index;
        return result != 0;
    }

    public byte getByte() {
        byte result = this.buffer.getByte(this.index + this.offset);
        ++this.index;
        return result;
    }

    public int getInt() {
        int result = this.buffer.getInt(this.index + this.offset);
        this.index += 4;
        return result;
    }

    public long getLong() {
        long result = this.buffer.getLong(this.index + this.offset);
        this.index += 8;
        return result;
    }

    public String getStringUtf8() {
        int length = this.buffer.getInt(this.index + this.offset);
        String result = this.buffer.getStringUtf8(this.index + this.offset, length);
        this.index += length + 4;
        return result;
    }

    public byte[] getByteArray() {
        int length = this.buffer.getInt(this.index + this.offset);
        this.index += 4;
        byte[] result = new byte[length];
        this.buffer.getBytes(this.index + this.offset, result);
        this.index += length;
        return result;
    }

    public Data getData() {
        return new HeapData(this.getByteArray());
    }

    public List<Data> getDataList() {
        int length = this.buffer.getInt(this.index + this.offset);
        this.index += 4;
        ArrayList<Data> result = new ArrayList<Data>();
        for (int i = 0; i < length; ++i) {
            result.add(this.getData());
        }
        return result;
    }

    protected int int32Get(int index) {
        return this.buffer.getInt(index + this.offset);
    }

    protected void int32Set(int index, int value) {
        this.buffer.putInt(index + this.offset, value);
    }

    protected long int64Get(int index) {
        return this.buffer.getLong(index + this.offset);
    }

    protected void int64Set(int index, long value) {
        this.buffer.putLong(index + this.offset, value);
    }

    protected short uint8Get(int index) {
        return (short)(this.buffer.getByte(index + this.offset) & 0xFF);
    }

    protected void uint8Put(int index, short value) {
        this.buffer.putByte(index + this.offset, (byte)value);
    }

    protected int uint16Get(int index) {
        return this.buffer.getShort(index + this.offset) & 0xFFFF;
    }

    protected void uint16Put(int index, int value) {
        this.buffer.putShort(index + this.offset, (short)value);
    }

    protected long uint32Get(int index) {
        return (long)this.buffer.getInt(index + this.offset) & 0xFFFFFFFFL;
    }

    protected void uint32Put(int index, long value) {
        this.buffer.putInt(index + this.offset, (int)value);
    }
}

