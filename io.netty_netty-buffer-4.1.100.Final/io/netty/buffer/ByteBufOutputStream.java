/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.CharsetUtil
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteBufOutputStream
extends OutputStream
implements DataOutput {
    private final ByteBuf buffer;
    private final int startIndex;
    private DataOutputStream utf8out;
    private boolean closed;

    public ByteBufOutputStream(ByteBuf buffer) {
        this.buffer = (ByteBuf)ObjectUtil.checkNotNull((Object)buffer, (String)"buffer");
        this.startIndex = buffer.writerIndex();
    }

    public int writtenBytes() {
        return this.buffer.writerIndex() - this.startIndex;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        this.buffer.writeBytes(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.buffer.writeBytes(b);
    }

    @Override
    public void write(int b) throws IOException {
        this.buffer.writeByte(b);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.buffer.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.buffer.writeByte(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.buffer.writeCharSequence(s, CharsetUtil.US_ASCII);
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.buffer.writeChar(v);
    }

    @Override
    public void writeChars(String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            this.buffer.writeChar(s.charAt(i));
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.buffer.writeDouble(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.buffer.writeFloat(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.buffer.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.buffer.writeLong(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.buffer.writeShort((short)v);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        DataOutputStream out = this.utf8out;
        if (out == null) {
            if (this.closed) {
                throw new IOException("The stream is closed");
            }
            this.utf8out = out = new DataOutputStream(this);
        }
        out.writeUTF(s);
    }

    public ByteBuf buffer() {
        return this.buffer;
    }

    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        try {
            super.close();
        }
        finally {
            if (this.utf8out != null) {
                this.utf8out.close();
            }
        }
    }
}

