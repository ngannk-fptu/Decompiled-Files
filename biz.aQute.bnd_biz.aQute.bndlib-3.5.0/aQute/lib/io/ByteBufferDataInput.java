/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class ByteBufferDataInput
implements DataInput {
    private final ByteBuffer bb;

    public static DataInput wrap(ByteBuffer bb) {
        return new ByteBufferDataInput(bb);
    }

    private ByteBufferDataInput(ByteBuffer bb) {
        this.bb = Objects.requireNonNull(bb);
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.bb.get(b, 0, b.length);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.bb.get(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        int skipped = Math.min(n, this.bb.remaining());
        this.bb.position(this.bb.position() + skipped);
        return skipped;
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.bb.get() != 0;
    }

    @Override
    public byte readByte() throws IOException {
        return this.bb.get();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return 0xFF & this.bb.get();
    }

    @Override
    public short readShort() throws IOException {
        return this.bb.getShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return 0xFFFF & this.bb.getShort();
    }

    @Override
    public char readChar() throws IOException {
        return this.bb.getChar();
    }

    @Override
    public int readInt() throws IOException {
        return this.bb.getInt();
    }

    @Override
    public long readLong() throws IOException {
        return this.bb.getLong();
    }

    @Override
    public float readFloat() throws IOException {
        return this.bb.getFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return this.bb.getDouble();
    }

    @Override
    public String readLine() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }
}

