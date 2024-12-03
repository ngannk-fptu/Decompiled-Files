/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TextProtocolsDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.StringUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

@SuppressFBWarnings(value={"EI_EXPOSE_REP"})
public class MemcacheEntry
implements IdentifiedDataSerializable {
    private byte[] bytes;
    private byte[] value;
    private int flag;

    public MemcacheEntry(String key, byte[] value, int flag) {
        byte[] flagBytes = StringUtil.stringToBytes(" " + flag + " ");
        byte[] valueLen = StringUtil.stringToBytes(String.valueOf(value.length));
        byte[] keyBytes = StringUtil.stringToBytes(key);
        this.value = (byte[])value.clone();
        int size = TextCommandConstants.VALUE_SPACE.length + keyBytes.length + flagBytes.length + valueLen.length + TextCommandConstants.RETURN.length + value.length + TextCommandConstants.RETURN.length;
        ByteBuffer entryBuffer = ByteBuffer.allocate(size);
        entryBuffer.put(TextCommandConstants.VALUE_SPACE);
        entryBuffer.put(keyBytes);
        entryBuffer.put(flagBytes);
        entryBuffer.put(valueLen);
        entryBuffer.put(TextCommandConstants.RETURN);
        entryBuffer.put(value);
        entryBuffer.put(TextCommandConstants.RETURN);
        this.bytes = entryBuffer.array();
        this.flag = flag;
    }

    public MemcacheEntry() {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.bytes = new byte[size];
        in.readFully(this.bytes);
        size = in.readInt();
        this.value = new byte[size];
        in.readFully(this.value);
        this.flag = in.readInt();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.bytes.length);
        out.write(this.bytes);
        out.writeInt(this.value.length);
        out.write(this.value);
        out.writeInt(this.flag);
    }

    public ByteBuffer toNewBuffer() {
        return ByteBuffer.wrap(this.bytes);
    }

    public int getFlag() {
        return this.flag;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public String getKey() {
        int start;
        for (int i = start = TextCommandConstants.VALUE_SPACE.length; i < this.bytes.length; ++i) {
            if (this.bytes[i] != 32) continue;
            return StringUtil.bytesToString(Arrays.copyOfRange(this.bytes, start, i));
        }
        return null;
    }

    public byte[] getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MemcacheEntry that = (MemcacheEntry)o;
        if (this.flag != that.flag) {
            return false;
        }
        if (!Arrays.equals(this.bytes, that.bytes)) {
            return false;
        }
        return Arrays.equals(this.value, that.value);
    }

    public int hashCode() {
        int result = this.bytes != null ? Arrays.hashCode(this.bytes) : 0;
        result = 31 * result + (this.value != null ? Arrays.hashCode(this.value) : 0);
        result = 31 * result + this.flag;
        return result;
    }

    public String toString() {
        return "MemcacheEntry{bytes=" + StringUtil.bytesToString(this.bytes) + ", flag=" + this.flag + '}';
    }

    @Override
    public int getFactoryId() {
        return TextProtocolsDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }
}

