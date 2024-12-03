/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspWriter
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.LinkedList;
import javax.servlet.jsp.JspWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FastByteArrayOutputStream
extends OutputStream {
    private static final Logger LOG = LogManager.getLogger(FastByteArrayOutputStream.class);
    private static final int DEFAULT_BLOCK_SIZE = 8192;
    private LinkedList<byte[]> buffers;
    private byte[] buffer;
    private int index;
    private int size;
    private int blockSize;
    private boolean closed;

    public FastByteArrayOutputStream() {
        this(8192);
    }

    public FastByteArrayOutputStream(int blockSize) {
        this.blockSize = blockSize;
        this.buffer = new byte[this.blockSize];
    }

    public void writeTo(OutputStream out) throws IOException {
        if (this.buffers != null) {
            for (byte[] bytes : this.buffers) {
                out.write(bytes, 0, this.blockSize);
            }
        }
        out.write(this.buffer, 0, this.index);
    }

    public void writeTo(RandomAccessFile out) throws IOException {
        if (this.buffers != null) {
            for (byte[] bytes : this.buffers) {
                out.write(bytes, 0, this.blockSize);
            }
        }
        out.write(this.buffer, 0, this.index);
    }

    public void writeTo(Writer out, String encoding) throws IOException {
        if (encoding != null) {
            CharsetDecoder decoder = this.getDecoder(encoding);
            CharBuffer charBuffer = CharBuffer.allocate(this.buffer.length);
            float bytesPerChar = decoder.charset().newEncoder().maxBytesPerChar();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int)((float)this.buffer.length + bytesPerChar));
            if (this.buffers != null) {
                for (byte[] bytes : this.buffers) {
                    FastByteArrayOutputStream.decodeAndWriteOut(out, bytes, bytes.length, byteBuffer, charBuffer, decoder, false);
                }
            }
            FastByteArrayOutputStream.decodeAndWriteOut(out, this.buffer, this.index, byteBuffer, charBuffer, decoder, true);
        } else {
            if (this.buffers != null) {
                for (byte[] bytes : this.buffers) {
                    this.writeOut(out, bytes, bytes.length);
                }
            }
            this.writeOut(out, this.buffer, this.index);
        }
    }

    private CharsetDecoder getDecoder(String encoding) {
        Charset charset = Charset.forName(encoding);
        return charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPLACE);
    }

    public void writeTo(JspWriter out, String encoding) throws IOException {
        try {
            this.writeTo((Writer)out, encoding);
        }
        catch (IOException e) {
            this.writeToFile();
            throw e;
        }
        catch (Throwable e) {
            this.writeToFile();
            throw new RuntimeException(e);
        }
    }

    private void writeToFile() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(File.createTempFile(this.getClass().getName() + System.currentTimeMillis(), ".log"));){
            this.writeTo(fileOutputStream);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private void writeOut(Writer out, byte[] bytes, int length) throws IOException {
        out.write(new String(bytes, 0, length));
    }

    private static void decodeAndWriteOut(Writer writer, byte[] bytes, int length, ByteBuffer in, CharBuffer out, CharsetDecoder decoder, boolean endOfInput) throws IOException {
        in.put(bytes, 0, length);
        in.flip();
        FastByteArrayOutputStream.decodeAndWriteBuffered(writer, in, out, decoder, endOfInput);
    }

    private static void decodeAndWriteBuffered(Writer writer, ByteBuffer in, CharBuffer out, CharsetDecoder decoder, boolean endOfInput) throws IOException {
        CoderResult result;
        do {
            result = FastByteArrayOutputStream.decodeAndWrite(writer, in, out, decoder, endOfInput);
            if (in.hasRemaining()) {
                in.compact();
                if (!result.isOverflow() || result.isError()) continue;
                in.flip();
                continue;
            }
            in.clear();
        } while (in.hasRemaining() && result.isOverflow() && !result.isError());
        if (result.isError() && LOG.isWarnEnabled()) {
            LOG.warn("Buffer decoding-in-to-out [{}] failed, coderResult [{}]", (Object)decoder.charset().name(), (Object)result.toString());
        }
    }

    private static CoderResult decodeAndWrite(Writer writer, ByteBuffer in, CharBuffer out, CharsetDecoder decoder, boolean endOfInput) throws IOException {
        CoderResult result = decoder.decode(in, out, endOfInput);
        out.flip();
        writer.write(out.toString());
        out.clear();
        return result;
    }

    public int getSize() {
        return this.size + this.index;
    }

    public byte[] toByteArray() {
        byte[] data = new byte[this.getSize()];
        int position = 0;
        if (this.buffers != null) {
            for (byte[] bytes : this.buffers) {
                System.arraycopy(bytes, 0, data, position, this.blockSize);
                position += this.blockSize;
            }
        }
        System.arraycopy(this.buffer, 0, data, position, this.index);
        return data;
    }

    public String toString() {
        return new String(this.toByteArray());
    }

    protected void addBuffer() {
        if (this.buffers == null) {
            this.buffers = new LinkedList();
        }
        this.buffers.addLast(this.buffer);
        this.buffer = new byte[this.blockSize];
        this.size += this.index;
        this.index = 0;
    }

    @Override
    public void write(int datum) throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        if (this.index == this.blockSize) {
            this.addBuffer();
        }
        this.buffer[this.index++] = (byte)datum;
    }

    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        if (data == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || offset + length > data.length || length < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        if (this.index + length > this.blockSize) {
            int copyLength;
            do {
                if (this.index == this.blockSize) {
                    this.addBuffer();
                }
                if (length < (copyLength = this.blockSize - this.index)) {
                    copyLength = length;
                }
                System.arraycopy(data, offset, this.buffer, this.index, copyLength);
                offset += copyLength;
                this.index += copyLength;
            } while ((length -= copyLength) > 0);
        } else {
            System.arraycopy(data, offset, this.buffer, this.index, length);
            this.index += length;
        }
    }

    @Override
    public void close() {
        this.closed = true;
    }
}

