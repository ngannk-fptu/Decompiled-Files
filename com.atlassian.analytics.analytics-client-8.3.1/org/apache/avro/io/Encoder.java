/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.avro.util.Utf8;

public abstract class Encoder
implements Flushable {
    public abstract void writeNull() throws IOException;

    public abstract void writeBoolean(boolean var1) throws IOException;

    public abstract void writeInt(int var1) throws IOException;

    public abstract void writeLong(long var1) throws IOException;

    public abstract void writeFloat(float var1) throws IOException;

    public abstract void writeDouble(double var1) throws IOException;

    public abstract void writeString(Utf8 var1) throws IOException;

    public void writeString(String str) throws IOException {
        this.writeString(new Utf8(str));
    }

    public void writeString(CharSequence charSequence) throws IOException {
        if (charSequence instanceof Utf8) {
            this.writeString((Utf8)charSequence);
        } else {
            this.writeString(charSequence.toString());
        }
    }

    public abstract void writeBytes(ByteBuffer var1) throws IOException;

    public abstract void writeBytes(byte[] var1, int var2, int var3) throws IOException;

    public void writeBytes(byte[] bytes) throws IOException {
        this.writeBytes(bytes, 0, bytes.length);
    }

    public abstract void writeFixed(byte[] var1, int var2, int var3) throws IOException;

    public void writeFixed(byte[] bytes) throws IOException {
        this.writeFixed(bytes, 0, bytes.length);
    }

    public void writeFixed(ByteBuffer bytes) throws IOException {
        int pos = bytes.position();
        int len = bytes.limit() - pos;
        if (bytes.hasArray()) {
            this.writeFixed(bytes.array(), bytes.arrayOffset() + pos, len);
        } else {
            byte[] b = new byte[len];
            bytes.duplicate().get(b, 0, len);
            this.writeFixed(b, 0, len);
        }
    }

    public abstract void writeEnum(int var1) throws IOException;

    public abstract void writeArrayStart() throws IOException;

    public abstract void setItemCount(long var1) throws IOException;

    public abstract void startItem() throws IOException;

    public abstract void writeArrayEnd() throws IOException;

    public abstract void writeMapStart() throws IOException;

    public abstract void writeMapEnd() throws IOException;

    public abstract void writeIndex(int var1) throws IOException;
}

