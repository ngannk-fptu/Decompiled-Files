/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.xml.serializer.SerializerTrace;
import org.apache.xml.serializer.WriterChain;

final class SerializerTraceWriter
extends Writer
implements WriterChain {
    private final Writer m_writer;
    private final SerializerTrace m_tracer;
    private int buf_length;
    private byte[] buf;
    private int count;

    private void setBufferSize(int size) {
        this.buf = new byte[size + 3];
        this.buf_length = size;
        this.count = 0;
    }

    public SerializerTraceWriter(Writer out, SerializerTrace tracer) {
        this.m_writer = out;
        this.m_tracer = tracer;
        this.setBufferSize(1024);
    }

    private void flushBuffer() throws IOException {
        if (this.count > 0) {
            char[] chars = new char[this.count];
            for (int i = 0; i < this.count; ++i) {
                chars[i] = (char)this.buf[i];
            }
            if (this.m_tracer != null) {
                this.m_tracer.fireGenerateEvent(12, chars, 0, chars.length);
            }
            this.count = 0;
        }
    }

    @Override
    public void flush() throws IOException {
        if (this.m_writer != null) {
            this.m_writer.flush();
        }
        this.flushBuffer();
    }

    @Override
    public void close() throws IOException {
        if (this.m_writer != null) {
            this.m_writer.close();
        }
        this.flushBuffer();
    }

    @Override
    public void write(int c) throws IOException {
        if (this.m_writer != null) {
            this.m_writer.write(c);
        }
        if (this.count >= this.buf_length) {
            this.flushBuffer();
        }
        if (c < 128) {
            this.buf[this.count++] = (byte)c;
        } else if (c < 2048) {
            this.buf[this.count++] = (byte)(192 + (c >> 6));
            this.buf[this.count++] = (byte)(128 + (c & 0x3F));
        } else {
            this.buf[this.count++] = (byte)(224 + (c >> 12));
            this.buf[this.count++] = (byte)(128 + (c >> 6 & 0x3F));
            this.buf[this.count++] = (byte)(128 + (c & 0x3F));
        }
    }

    @Override
    public void write(char[] chars, int start, int length) throws IOException {
        int lengthx3;
        if (this.m_writer != null) {
            this.m_writer.write(chars, start, length);
        }
        if ((lengthx3 = (length << 1) + length) >= this.buf_length) {
            this.flushBuffer();
            this.setBufferSize(2 * lengthx3);
        }
        if (lengthx3 > this.buf_length - this.count) {
            this.flushBuffer();
        }
        int n = length + start;
        for (int i = start; i < n; ++i) {
            char c = chars[i];
            if (c < '\u0080') {
                this.buf[this.count++] = (byte)c;
                continue;
            }
            if (c < '\u0800') {
                this.buf[this.count++] = (byte)(192 + (c >> 6));
                this.buf[this.count++] = (byte)(128 + (c & 0x3F));
                continue;
            }
            this.buf[this.count++] = (byte)(224 + (c >> 12));
            this.buf[this.count++] = (byte)(128 + (c >> 6 & 0x3F));
            this.buf[this.count++] = (byte)(128 + (c & 0x3F));
        }
    }

    @Override
    public void write(String s) throws IOException {
        int length;
        int lengthx3;
        if (this.m_writer != null) {
            this.m_writer.write(s);
        }
        if ((lengthx3 = ((length = s.length()) << 1) + length) >= this.buf_length) {
            this.flushBuffer();
            this.setBufferSize(2 * lengthx3);
        }
        if (lengthx3 > this.buf_length - this.count) {
            this.flushBuffer();
        }
        for (int i = 0; i < length; ++i) {
            char c = s.charAt(i);
            if (c < '\u0080') {
                this.buf[this.count++] = (byte)c;
                continue;
            }
            if (c < '\u0800') {
                this.buf[this.count++] = (byte)(192 + (c >> 6));
                this.buf[this.count++] = (byte)(128 + (c & 0x3F));
                continue;
            }
            this.buf[this.count++] = (byte)(224 + (c >> 12));
            this.buf[this.count++] = (byte)(128 + (c >> 6 & 0x3F));
            this.buf[this.count++] = (byte)(128 + (c & 0x3F));
        }
    }

    @Override
    public Writer getWriter() {
        return this.m_writer;
    }

    @Override
    public OutputStream getOutputStream() {
        OutputStream retval = null;
        if (this.m_writer instanceof WriterChain) {
            retval = ((WriterChain)((Object)this.m_writer)).getOutputStream();
        }
        return retval;
    }
}

