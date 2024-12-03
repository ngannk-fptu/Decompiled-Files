/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.io.EOFException;
import java.io.IOException;
import org.apache.fontbox.util.Charsets;

public class DataInput {
    private final byte[] inputBuffer;
    private int bufferPosition = 0;

    public DataInput(byte[] buffer) {
        this.inputBuffer = buffer;
    }

    public boolean hasRemaining() {
        return this.bufferPosition < this.inputBuffer.length;
    }

    public int getPosition() {
        return this.bufferPosition;
    }

    public void setPosition(int position) {
        this.bufferPosition = position;
    }

    public String getString() throws IOException {
        return new String(this.inputBuffer, Charsets.ISO_8859_1);
    }

    public byte readByte() throws IOException {
        try {
            byte value = this.inputBuffer[this.bufferPosition];
            ++this.bufferPosition;
            return value;
        }
        catch (RuntimeException re) {
            return -1;
        }
    }

    public int readUnsignedByte() throws IOException {
        int b = this.read();
        if (b < 0) {
            throw new EOFException();
        }
        return b;
    }

    public int peekUnsignedByte(int offset) throws IOException {
        int b = this.peek(offset);
        if (b < 0) {
            throw new EOFException();
        }
        return b;
    }

    public short readShort() throws IOException {
        return (short)this.readUnsignedShort();
    }

    public int readUnsignedShort() throws IOException {
        int b2;
        int b1 = this.read();
        if ((b1 | (b2 = this.read())) < 0) {
            throw new EOFException();
        }
        return b1 << 8 | b2;
    }

    public int readInt() throws IOException {
        int b4;
        int b3;
        int b2;
        int b1 = this.read();
        if ((b1 | (b2 = this.read()) | (b3 = this.read()) | (b4 = this.read())) < 0) {
            throw new EOFException();
        }
        return b1 << 24 | b2 << 16 | b3 << 8 | b4;
    }

    public byte[] readBytes(int length) throws IOException {
        if (length < 0) {
            throw new IOException("length is negative");
        }
        if (this.inputBuffer.length - this.bufferPosition < length) {
            throw new EOFException();
        }
        byte[] bytes = new byte[length];
        System.arraycopy(this.inputBuffer, this.bufferPosition, bytes, 0, length);
        this.bufferPosition += length;
        return bytes;
    }

    private int read() {
        try {
            int value = this.inputBuffer[this.bufferPosition] & 0xFF;
            ++this.bufferPosition;
            return value;
        }
        catch (RuntimeException re) {
            return -1;
        }
    }

    private int peek(int offset) {
        try {
            return this.inputBuffer[this.bufferPosition + offset] & 0xFF;
        }
        catch (RuntimeException re) {
            return -1;
        }
    }

    public int length() {
        return this.inputBuffer.length;
    }
}

