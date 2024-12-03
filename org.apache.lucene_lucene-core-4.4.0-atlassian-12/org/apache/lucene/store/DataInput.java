/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.util.IOUtils;

public abstract class DataInput
implements Cloneable {
    public abstract byte readByte() throws IOException;

    public abstract void readBytes(byte[] var1, int var2, int var3) throws IOException;

    public void readBytes(byte[] b, int offset, int len, boolean useBuffer) throws IOException {
        this.readBytes(b, offset, len);
    }

    public short readShort() throws IOException {
        return (short)((this.readByte() & 0xFF) << 8 | this.readByte() & 0xFF);
    }

    public int readInt() throws IOException {
        return (this.readByte() & 0xFF) << 24 | (this.readByte() & 0xFF) << 16 | (this.readByte() & 0xFF) << 8 | this.readByte() & 0xFF;
    }

    public int readVInt() throws IOException {
        byte b = this.readByte();
        if (b >= 0) {
            return b;
        }
        int i = b & 0x7F;
        b = this.readByte();
        i |= (b & 0x7F) << 7;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= (b & 0x7F) << 14;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= (b & 0x7F) << 21;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= (b & 0xF) << 28;
        if ((b & 0xF0) == 0) {
            return i;
        }
        throw new IOException("Invalid vInt detected (too many bits)");
    }

    public long readLong() throws IOException {
        return (long)this.readInt() << 32 | (long)this.readInt() & 0xFFFFFFFFL;
    }

    public long readVLong() throws IOException {
        byte b = this.readByte();
        if (b >= 0) {
            return b;
        }
        long i = (long)b & 0x7FL;
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 7;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 14;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 21;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 28;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 35;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 42;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 49;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 56;
        if (b >= 0) {
            return i;
        }
        throw new IOException("Invalid vLong detected (negative values disallowed)");
    }

    public String readString() throws IOException {
        int length = this.readVInt();
        byte[] bytes = new byte[length];
        this.readBytes(bytes, 0, length);
        return new String(bytes, 0, length, IOUtils.CHARSET_UTF_8);
    }

    public DataInput clone() {
        try {
            return (DataInput)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error("This cannot happen: Failing to clone DataInput");
        }
    }

    public Map<String, String> readStringStringMap() throws IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        int count = this.readInt();
        for (int i = 0; i < count; ++i) {
            String key = this.readString();
            String val = this.readString();
            map.put(key, val);
        }
        return map;
    }

    public Set<String> readStringSet() throws IOException {
        HashSet<String> set = new HashSet<String>();
        int count = this.readInt();
        for (int i = 0; i < count; ++i) {
            set.add(this.readString());
        }
        return set;
    }
}

