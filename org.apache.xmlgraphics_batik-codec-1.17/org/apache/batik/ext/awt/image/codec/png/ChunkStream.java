/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.codec.png;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.ext.awt.image.codec.png.CRC;

class ChunkStream
extends OutputStream
implements DataOutput {
    private String type;
    private ByteArrayOutputStream baos;
    private DataOutputStream dos;

    ChunkStream(String type) throws IOException {
        this.type = type;
        this.baos = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(this.baos);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.dos.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.dos.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        this.dos.write(b);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.dos.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.dos.writeByte(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.dos.writeBytes(s);
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.dos.writeChar(v);
    }

    @Override
    public void writeChars(String s) throws IOException {
        this.dos.writeChars(s);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.dos.writeDouble(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.dos.writeFloat(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.dos.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.dos.writeLong(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.dos.writeShort(v);
    }

    @Override
    public void writeUTF(String str) throws IOException {
        this.dos.writeUTF(str);
    }

    public void writeToStream(DataOutputStream output) throws IOException {
        byte[] typeSignature = new byte[]{(byte)this.type.charAt(0), (byte)this.type.charAt(1), (byte)this.type.charAt(2), (byte)this.type.charAt(3)};
        this.dos.flush();
        this.baos.flush();
        byte[] data = this.baos.toByteArray();
        int len = data.length;
        output.writeInt(len);
        output.write(typeSignature);
        output.write(data, 0, len);
        int crc = -1;
        crc = CRC.updateCRC(crc, typeSignature, 0, 4);
        crc = CRC.updateCRC(crc, data, 0, len);
        output.writeInt(~crc);
    }

    @Override
    public void close() throws IOException {
        if (this.baos != null) {
            this.baos.close();
            this.baos = null;
        }
        if (this.dos != null) {
            this.dos.close();
            this.dos = null;
        }
    }
}

