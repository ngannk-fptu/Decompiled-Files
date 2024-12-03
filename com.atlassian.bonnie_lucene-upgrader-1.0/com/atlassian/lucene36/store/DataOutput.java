/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.util.BytesRef;
import com.atlassian.lucene36.util.UnicodeUtil;
import java.io.IOException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class DataOutput {
    private static int COPY_BUFFER_SIZE = 16384;
    private byte[] copyBuffer;

    public abstract void writeByte(byte var1) throws IOException;

    public void writeBytes(byte[] b, int length) throws IOException {
        this.writeBytes(b, 0, length);
    }

    public abstract void writeBytes(byte[] var1, int var2, int var3) throws IOException;

    public void writeInt(int i) throws IOException {
        this.writeByte((byte)(i >> 24));
        this.writeByte((byte)(i >> 16));
        this.writeByte((byte)(i >> 8));
        this.writeByte((byte)i);
    }

    public void writeShort(short i) throws IOException {
        this.writeByte((byte)(i >> 8));
        this.writeByte((byte)i);
    }

    public final void writeVInt(int i) throws IOException {
        while ((i & 0xFFFFFF80) != 0) {
            this.writeByte((byte)(i & 0x7F | 0x80));
            i >>>= 7;
        }
        this.writeByte((byte)i);
    }

    public void writeLong(long i) throws IOException {
        this.writeInt((int)(i >> 32));
        this.writeInt((int)i);
    }

    public final void writeVLong(long i) throws IOException {
        assert (i >= 0L);
        while ((i & 0xFFFFFFFFFFFFFF80L) != 0L) {
            this.writeByte((byte)(i & 0x7FL | 0x80L));
            i >>>= 7;
        }
        this.writeByte((byte)i);
    }

    public void writeString(String s) throws IOException {
        BytesRef utf8Result = new BytesRef(10);
        UnicodeUtil.UTF16toUTF8((CharSequence)s, 0, s.length(), utf8Result);
        this.writeVInt(utf8Result.length);
        this.writeBytes(utf8Result.bytes, 0, utf8Result.length);
    }

    public void copyBytes(DataInput input, long numBytes) throws IOException {
        assert (numBytes >= 0L) : "numBytes=" + numBytes;
        long left = numBytes;
        if (this.copyBuffer == null) {
            this.copyBuffer = new byte[COPY_BUFFER_SIZE];
        }
        while (left > 0L) {
            int toCopy = left > (long)COPY_BUFFER_SIZE ? COPY_BUFFER_SIZE : (int)left;
            input.readBytes(this.copyBuffer, 0, toCopy);
            this.writeBytes(this.copyBuffer, 0, toCopy);
            left -= (long)toCopy;
        }
    }

    @Deprecated
    public void writeChars(String s, int start, int length) throws IOException {
        int end = start + length;
        for (int i = start; i < end; ++i) {
            char code = s.charAt(i);
            if (code >= '\u0001' && code <= '\u007f') {
                this.writeByte((byte)code);
                continue;
            }
            if (code >= '\u0080' && code <= '\u07ff' || code == '\u0000') {
                this.writeByte((byte)(0xC0 | code >> 6));
                this.writeByte((byte)(0x80 | code & 0x3F));
                continue;
            }
            this.writeByte((byte)(0xE0 | code >>> 12));
            this.writeByte((byte)(0x80 | code >> 6 & 0x3F));
            this.writeByte((byte)(0x80 | code & 0x3F));
        }
    }

    @Deprecated
    public void writeChars(char[] s, int start, int length) throws IOException {
        int end = start + length;
        for (int i = start; i < end; ++i) {
            char code = s[i];
            if (code >= '\u0001' && code <= '\u007f') {
                this.writeByte((byte)code);
                continue;
            }
            if (code >= '\u0080' && code <= '\u07ff' || code == '\u0000') {
                this.writeByte((byte)(0xC0 | code >> 6));
                this.writeByte((byte)(0x80 | code & 0x3F));
                continue;
            }
            this.writeByte((byte)(0xE0 | code >>> 12));
            this.writeByte((byte)(0x80 | code >> 6 & 0x3F));
            this.writeByte((byte)(0x80 | code & 0x3F));
        }
    }

    public void writeStringStringMap(Map<String, String> map) throws IOException {
        if (map == null) {
            this.writeInt(0);
        } else {
            this.writeInt(map.size());
            for (Map.Entry<String, String> entry : map.entrySet()) {
                this.writeString(entry.getKey());
                this.writeString(entry.getValue());
            }
        }
    }
}

