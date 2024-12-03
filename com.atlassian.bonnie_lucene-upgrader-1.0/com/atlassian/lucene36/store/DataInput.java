/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class DataInput
implements Cloneable {
    private boolean preUTF8Strings;

    public void setModifiedUTF8StringsMode() {
        this.preUTF8Strings = true;
    }

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
        int i = b & 0x7F;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.readByte();
        i |= (b & 0x7F) << 7;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.readByte();
        i |= (b & 0x7F) << 14;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.readByte();
        i |= (b & 0x7F) << 21;
        if ((b & 0x80) == 0) {
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
        long i = (long)b & 0x7FL;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 7;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 14;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 21;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 28;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 35;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 42;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 49;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 56;
        if ((b & 0x80) == 0) {
            return i;
        }
        throw new IOException("Invalid vLong detected (negative values disallowed)");
    }

    public String readString() throws IOException {
        if (this.preUTF8Strings) {
            return this.readModifiedUTF8String();
        }
        int length = this.readVInt();
        byte[] bytes = new byte[length];
        this.readBytes(bytes, 0, length);
        return new String(bytes, 0, length, "UTF-8");
    }

    private String readModifiedUTF8String() throws IOException {
        int length = this.readVInt();
        char[] chars = new char[length];
        this.readChars(chars, 0, length);
        return new String(chars, 0, length);
    }

    @Deprecated
    public void readChars(char[] buffer, int start, int length) throws IOException {
        int end = start + length;
        for (int i = start; i < end; ++i) {
            byte b = this.readByte();
            buffer[i] = (b & 0x80) == 0 ? (char)(b & 0x7F) : ((b & 0xE0) != 224 ? (char)((b & 0x1F) << 6 | this.readByte() & 0x3F) : (char)((b & 0xF) << 12 | (this.readByte() & 0x3F) << 6 | this.readByte() & 0x3F));
        }
    }

    public Object clone() {
        DataInput clone = null;
        try {
            clone = (DataInput)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        return clone;
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
}

