/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import org.apache.avro.io.BinaryData;
import org.apache.avro.io.BinaryEncoder;

public class DirectBinaryEncoder
extends BinaryEncoder {
    private OutputStream out;
    private final byte[] buf = new byte[12];

    DirectBinaryEncoder(OutputStream out) {
        this.configure(out);
    }

    DirectBinaryEncoder configure(OutputStream out) {
        Objects.requireNonNull(out, "OutputStream cannot be null");
        this.out = out;
        return this;
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        this.out.write(b ? 1 : 0);
    }

    @Override
    public void writeInt(int n) throws IOException {
        int val = n << 1 ^ n >> 31;
        if ((val & 0xFFFFFF80) == 0) {
            this.out.write(val);
            return;
        }
        if ((val & 0xFFFFC000) == 0) {
            this.out.write(0x80 | val);
            this.out.write(val >>> 7);
            return;
        }
        int len = BinaryData.encodeInt(n, this.buf, 0);
        this.out.write(this.buf, 0, len);
    }

    @Override
    public void writeLong(long n) throws IOException {
        long val = n << 1 ^ n >> 63;
        if ((val & Integer.MIN_VALUE) == 0L) {
            int i = (int)val;
            while ((i & 0xFFFFFF80) != 0) {
                this.out.write((byte)((0x80 | i) & 0xFF));
                i >>>= 7;
            }
            this.out.write((byte)i);
            return;
        }
        int len = BinaryData.encodeLong(n, this.buf, 0);
        this.out.write(this.buf, 0, len);
    }

    @Override
    public void writeFloat(float f) throws IOException {
        int len = BinaryData.encodeFloat(f, this.buf, 0);
        this.out.write(this.buf, 0, len);
    }

    @Override
    public void writeDouble(double d) throws IOException {
        int len = BinaryData.encodeDouble(d, this.buf, 0);
        this.out.write(this.buf, 0, len);
    }

    @Override
    public void writeFixed(byte[] bytes, int start, int len) throws IOException {
        this.out.write(bytes, start, len);
    }

    @Override
    protected void writeZero() throws IOException {
        this.out.write(0);
    }

    @Override
    public int bytesBuffered() {
        return 0;
    }
}

