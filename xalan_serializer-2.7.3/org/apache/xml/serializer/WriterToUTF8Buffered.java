/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.xml.serializer.WriterChain;

final class WriterToUTF8Buffered
extends Writer
implements WriterChain {
    private static final int BYTES_MAX = 16384;
    private static final int CHARS_MAX = 5461;
    private final OutputStream m_os;
    private final byte[] m_outputBytes;
    private final char[] m_inputChars;
    private int count;

    public WriterToUTF8Buffered(OutputStream out) {
        this.m_os = out;
        this.m_outputBytes = new byte[16387];
        this.m_inputChars = new char[5463];
        this.count = 0;
    }

    @Override
    public void write(int c) throws IOException {
        if (this.count >= 16384) {
            this.flushBuffer();
        }
        if (c < 128) {
            this.m_outputBytes[this.count++] = (byte)c;
        } else if (c < 2048) {
            this.m_outputBytes[this.count++] = (byte)(192 + (c >> 6));
            this.m_outputBytes[this.count++] = (byte)(128 + (c & 0x3F));
        } else if (c < 65536) {
            this.m_outputBytes[this.count++] = (byte)(224 + (c >> 12));
            this.m_outputBytes[this.count++] = (byte)(128 + (c >> 6 & 0x3F));
            this.m_outputBytes[this.count++] = (byte)(128 + (c & 0x3F));
        } else {
            this.m_outputBytes[this.count++] = (byte)(240 + (c >> 18));
            this.m_outputBytes[this.count++] = (byte)(128 + (c >> 12 & 0x3F));
            this.m_outputBytes[this.count++] = (byte)(128 + (c >> 6 & 0x3F));
            this.m_outputBytes[this.count++] = (byte)(128 + (c & 0x3F));
        }
    }

    @Override
    public void write(char[] chars, int start, int length) throws IOException {
        char c;
        int i;
        int lengthx3 = 3 * length;
        if (lengthx3 >= 16384 - this.count) {
            this.flushBuffer();
            if (lengthx3 > 16384) {
                int split = length / 5461;
                int chunks = length % 5461 > 0 ? split + 1 : split;
                int end_chunk = start;
                for (int chunk = 1; chunk <= chunks; ++chunk) {
                    int start_chunk = end_chunk;
                    end_chunk = start + (int)((long)length * (long)chunk / (long)chunks);
                    char c2 = chars[end_chunk - 1];
                    char ic = chars[end_chunk - 1];
                    if (c2 >= '\ud800' && c2 <= '\udbff') {
                        end_chunk = end_chunk < start + length ? ++end_chunk : --end_chunk;
                    }
                    int len_chunk = end_chunk - start_chunk;
                    this.write(chars, start_chunk, len_chunk);
                }
                return;
            }
        }
        int n = length + start;
        byte[] buf_loc = this.m_outputBytes;
        int count_loc = this.count;
        for (i = start; i < n && (c = chars[i]) < '\u0080'; ++i) {
            buf_loc[count_loc++] = (byte)c;
        }
        while (i < n) {
            c = chars[i];
            if (c < '\u0080') {
                buf_loc[count_loc++] = (byte)c;
            } else if (c < '\u0800') {
                buf_loc[count_loc++] = (byte)(192 + (c >> 6));
                buf_loc[count_loc++] = (byte)(128 + (c & 0x3F));
            } else if (c >= '\ud800' && c <= '\udbff') {
                char high = c;
                char low = chars[++i];
                buf_loc[count_loc++] = (byte)(0xF0 | high + 64 >> 8 & 0xF0);
                buf_loc[count_loc++] = (byte)(0x80 | high + 64 >> 2 & 0x3F);
                buf_loc[count_loc++] = (byte)(0x80 | (low >> 6 & 0xF) + (high << 4 & 0x30));
                buf_loc[count_loc++] = (byte)(0x80 | low & 0x3F);
            } else {
                buf_loc[count_loc++] = (byte)(224 + (c >> 12));
                buf_loc[count_loc++] = (byte)(128 + (c >> 6 & 0x3F));
                buf_loc[count_loc++] = (byte)(128 + (c & 0x3F));
            }
            ++i;
        }
        this.count = count_loc;
    }

    @Override
    public void write(String s) throws IOException {
        char c;
        int i;
        int length = s.length();
        int lengthx3 = 3 * length;
        if (lengthx3 >= 16384 - this.count) {
            this.flushBuffer();
            if (lengthx3 > 16384) {
                boolean start = false;
                int split = length / 5461;
                int chunks = length % 5461 > 0 ? split + 1 : split;
                int end_chunk = 0;
                for (int chunk = 1; chunk <= chunks; ++chunk) {
                    int start_chunk = end_chunk;
                    end_chunk = 0 + (int)((long)length * (long)chunk / (long)chunks);
                    s.getChars(start_chunk, end_chunk, this.m_inputChars, 0);
                    int len_chunk = end_chunk - start_chunk;
                    char c2 = this.m_inputChars[len_chunk - 1];
                    if (c2 >= '\ud800' && c2 <= '\udbff') {
                        --end_chunk;
                        --len_chunk;
                        if (chunk == chunks) {
                            // empty if block
                        }
                    }
                    this.write(this.m_inputChars, 0, len_chunk);
                }
                return;
            }
        }
        s.getChars(0, length, this.m_inputChars, 0);
        char[] chars = this.m_inputChars;
        int n = length;
        byte[] buf_loc = this.m_outputBytes;
        int count_loc = this.count;
        for (i = 0; i < n && (c = chars[i]) < '\u0080'; ++i) {
            buf_loc[count_loc++] = (byte)c;
        }
        while (i < n) {
            c = chars[i];
            if (c < '\u0080') {
                buf_loc[count_loc++] = (byte)c;
            } else if (c < '\u0800') {
                buf_loc[count_loc++] = (byte)(192 + (c >> 6));
                buf_loc[count_loc++] = (byte)(128 + (c & 0x3F));
            } else if (c >= '\ud800' && c <= '\udbff') {
                char high = c;
                char low = chars[++i];
                buf_loc[count_loc++] = (byte)(0xF0 | high + 64 >> 8 & 0xF0);
                buf_loc[count_loc++] = (byte)(0x80 | high + 64 >> 2 & 0x3F);
                buf_loc[count_loc++] = (byte)(0x80 | (low >> 6 & 0xF) + (high << 4 & 0x30));
                buf_loc[count_loc++] = (byte)(0x80 | low & 0x3F);
            } else {
                buf_loc[count_loc++] = (byte)(224 + (c >> 12));
                buf_loc[count_loc++] = (byte)(128 + (c >> 6 & 0x3F));
                buf_loc[count_loc++] = (byte)(128 + (c & 0x3F));
            }
            ++i;
        }
        this.count = count_loc;
    }

    public void flushBuffer() throws IOException {
        if (this.count > 0) {
            this.m_os.write(this.m_outputBytes, 0, this.count);
            this.count = 0;
        }
    }

    @Override
    public void flush() throws IOException {
        this.flushBuffer();
        this.m_os.flush();
    }

    @Override
    public void close() throws IOException {
        this.flushBuffer();
        this.m_os.close();
    }

    @Override
    public OutputStream getOutputStream() {
        return this.m_os;
    }

    @Override
    public Writer getWriter() {
        return null;
    }
}

