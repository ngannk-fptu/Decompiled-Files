/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.staxex;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class Base64EncoderStream
extends FilterOutputStream {
    private byte[] buffer = new byte[3];
    private int bufsize = 0;
    private XMLStreamWriter outWriter;
    private static final char[] pem_array = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    public Base64EncoderStream(OutputStream out) {
        super(out);
    }

    public Base64EncoderStream(XMLStreamWriter outWriter, OutputStream out) {
        super(out);
        this.outWriter = outWriter;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            this.write(b[off + i]);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(int c) throws IOException {
        this.buffer[this.bufsize++] = (byte)c;
        if (this.bufsize == 3) {
            this.encode();
            this.bufsize = 0;
        }
    }

    @Override
    public void flush() throws IOException {
        if (this.bufsize > 0) {
            this.encode();
            this.bufsize = 0;
        }
        this.out.flush();
        try {
            this.outWriter.flush();
        }
        catch (XMLStreamException ex) {
            Logger.getLogger(Base64EncoderStream.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        this.flush();
        this.out.close();
    }

    private void encode() throws IOException {
        char[] buf = new char[4];
        if (this.bufsize == 1) {
            byte a = this.buffer[0];
            int b = 0;
            boolean c = false;
            buf[0] = pem_array[a >>> 2 & 0x3F];
            buf[1] = pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
            buf[2] = 61;
            buf[3] = 61;
        } else if (this.bufsize == 2) {
            byte a = this.buffer[0];
            byte b = this.buffer[1];
            int c = 0;
            buf[0] = pem_array[a >>> 2 & 0x3F];
            buf[1] = pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
            buf[2] = pem_array[(b << 2 & 0x3C) + (c >>> 6 & 3)];
            buf[3] = 61;
        } else {
            byte a = this.buffer[0];
            byte b = this.buffer[1];
            byte c = this.buffer[2];
            buf[0] = pem_array[a >>> 2 & 0x3F];
            buf[1] = pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
            buf[2] = pem_array[(b << 2 & 0x3C) + (c >>> 6 & 3)];
            buf[3] = pem_array[c & 0x3F];
        }
        try {
            this.outWriter.writeCharacters(buf, 0, 4);
        }
        catch (XMLStreamException ex) {
            Logger.getLogger(Base64EncoderStream.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }
    }
}

