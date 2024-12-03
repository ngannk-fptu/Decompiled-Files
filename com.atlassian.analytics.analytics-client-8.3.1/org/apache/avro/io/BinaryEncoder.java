/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.avro.io.Encoder;
import org.apache.avro.util.Utf8;

public abstract class BinaryEncoder
extends Encoder {
    @Override
    public void writeNull() throws IOException {
    }

    @Override
    public void writeString(Utf8 utf8) throws IOException {
        this.writeBytes(utf8.getBytes(), 0, utf8.getByteLength());
    }

    @Override
    public void writeString(String string) throws IOException {
        if (0 == string.length()) {
            this.writeZero();
            return;
        }
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        this.writeInt(bytes.length);
        this.writeFixed(bytes, 0, bytes.length);
    }

    @Override
    public void writeBytes(ByteBuffer bytes) throws IOException {
        int len = bytes.limit() - bytes.position();
        if (0 == len) {
            this.writeZero();
        } else {
            this.writeInt(len);
            this.writeFixed(bytes);
        }
    }

    @Override
    public void writeBytes(byte[] bytes, int start, int len) throws IOException {
        if (0 == len) {
            this.writeZero();
            return;
        }
        this.writeInt(len);
        this.writeFixed(bytes, start, len);
    }

    @Override
    public void writeEnum(int e) throws IOException {
        this.writeInt(e);
    }

    @Override
    public void writeArrayStart() throws IOException {
    }

    @Override
    public void setItemCount(long itemCount) throws IOException {
        if (itemCount > 0L) {
            this.writeLong(itemCount);
        }
    }

    @Override
    public void startItem() throws IOException {
    }

    @Override
    public void writeArrayEnd() throws IOException {
        this.writeZero();
    }

    @Override
    public void writeMapStart() throws IOException {
    }

    @Override
    public void writeMapEnd() throws IOException {
        this.writeZero();
    }

    @Override
    public void writeIndex(int unionIndex) throws IOException {
        this.writeInt(unionIndex);
    }

    protected abstract void writeZero() throws IOException;

    public abstract int bytesBuffered();
}

