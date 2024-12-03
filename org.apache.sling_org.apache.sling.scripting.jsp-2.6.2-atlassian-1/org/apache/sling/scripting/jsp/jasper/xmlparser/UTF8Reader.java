/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UTFDataFormatException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;

public class UTF8Reader
extends Reader {
    private Log log = LogFactory.getLog(UTF8Reader.class);
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final boolean DEBUG_READ = false;
    protected InputStream fInputStream;
    protected byte[] fBuffer;
    protected int fOffset;
    private int fSurrogate = -1;

    public UTF8Reader(InputStream inputStream, int size) {
        this.fInputStream = inputStream;
        this.fBuffer = new byte[size];
    }

    @Override
    public int read() throws IOException {
        int c = this.fSurrogate;
        if (this.fSurrogate == -1) {
            int b0;
            int index = 0;
            int n = b0 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 0xFF;
            if (b0 == -1) {
                return -1;
            }
            if (b0 < 128) {
                c = (char)b0;
            } else if ((b0 & 0xE0) == 192) {
                int b1;
                int n2 = b1 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 0xFF;
                if (b1 == -1) {
                    this.expectedByte(2, 2);
                }
                if ((b1 & 0xC0) != 128) {
                    this.invalidByte(2, 2, b1);
                }
                c = b0 << 6 & 0x7C0 | b1 & 0x3F;
            } else if ((b0 & 0xF0) == 224) {
                int b2;
                int b1;
                int n3 = b1 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 0xFF;
                if (b1 == -1) {
                    this.expectedByte(2, 3);
                }
                if ((b1 & 0xC0) != 128) {
                    this.invalidByte(2, 3, b1);
                }
                int n4 = b2 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 0xFF;
                if (b2 == -1) {
                    this.expectedByte(3, 3);
                }
                if ((b2 & 0xC0) != 128) {
                    this.invalidByte(3, 3, b2);
                }
                c = b0 << 12 & 0xF000 | b1 << 6 & 0xFC0 | b2 & 0x3F;
            } else if ((b0 & 0xF8) == 240) {
                int uuuuu;
                int b3;
                int b2;
                int b1;
                int n5 = b1 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 0xFF;
                if (b1 == -1) {
                    this.expectedByte(2, 4);
                }
                if ((b1 & 0xC0) != 128) {
                    this.invalidByte(2, 3, b1);
                }
                int n6 = b2 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 0xFF;
                if (b2 == -1) {
                    this.expectedByte(3, 4);
                }
                if ((b2 & 0xC0) != 128) {
                    this.invalidByte(3, 3, b2);
                }
                int n7 = b3 = index == this.fOffset ? this.fInputStream.read() : this.fBuffer[index++] & 0xFF;
                if (b3 == -1) {
                    this.expectedByte(4, 4);
                }
                if ((b3 & 0xC0) != 128) {
                    this.invalidByte(4, 4, b3);
                }
                if ((uuuuu = b0 << 2 & 0x1C | b1 >> 4 & 3) > 16) {
                    this.invalidSurrogate(uuuuu);
                }
                int wwww = uuuuu - 1;
                int hs = 0xD800 | wwww << 6 & 0x3C0 | b1 << 2 & 0x3C | b2 >> 4 & 3;
                int ls = 0xDC00 | b2 << 6 & 0x3C0 | b3 & 0x3F;
                c = hs;
                this.fSurrogate = ls;
            } else {
                this.invalidByte(1, 1, b0);
            }
        } else {
            this.fSurrogate = -1;
        }
        return c;
    }

    @Override
    public int read(char[] ch, int offset, int length) throws IOException {
        int out = offset;
        if (this.fSurrogate != -1) {
            ch[offset + 1] = (char)this.fSurrogate;
            this.fSurrogate = -1;
            --length;
            ++out;
        }
        int count = 0;
        if (this.fOffset == 0) {
            if (length > this.fBuffer.length) {
                length = this.fBuffer.length;
            }
            if ((count = this.fInputStream.read(this.fBuffer, 0, length)) == -1) {
                return -1;
            }
            count += out - offset;
        } else {
            count = this.fOffset;
            this.fOffset = 0;
        }
        int total = count;
        for (int in = 0; in < total; ++in) {
            int b2;
            int b1;
            int b0 = this.fBuffer[in] & 0xFF;
            if (b0 < 128) {
                ch[out++] = (char)b0;
                continue;
            }
            if ((b0 & 0xE0) == 192) {
                b1 = -1;
                if (++in < total) {
                    b1 = this.fBuffer[in] & 0xFF;
                } else {
                    b1 = this.fInputStream.read();
                    if (b1 == -1) {
                        if (out > offset) {
                            this.fBuffer[0] = (byte)b0;
                            this.fOffset = 1;
                            return out - offset;
                        }
                        this.expectedByte(2, 2);
                    }
                    ++count;
                }
                if ((b1 & 0xC0) != 128) {
                    if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fBuffer[1] = (byte)b1;
                        this.fOffset = 2;
                        return out - offset;
                    }
                    this.invalidByte(2, 2, b1);
                }
                int c = b0 << 6 & 0x7C0 | b1 & 0x3F;
                ch[out++] = (char)c;
                --count;
                continue;
            }
            if ((b0 & 0xF0) == 224) {
                b1 = -1;
                if (++in < total) {
                    b1 = this.fBuffer[in] & 0xFF;
                } else {
                    b1 = this.fInputStream.read();
                    if (b1 == -1) {
                        if (out > offset) {
                            this.fBuffer[0] = (byte)b0;
                            this.fOffset = 1;
                            return out - offset;
                        }
                        this.expectedByte(2, 3);
                    }
                    ++count;
                }
                if ((b1 & 0xC0) != 128) {
                    if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fBuffer[1] = (byte)b1;
                        this.fOffset = 2;
                        return out - offset;
                    }
                    this.invalidByte(2, 3, b1);
                }
                b2 = -1;
                if (++in < total) {
                    b2 = this.fBuffer[in] & 0xFF;
                } else {
                    b2 = this.fInputStream.read();
                    if (b2 == -1) {
                        if (out > offset) {
                            this.fBuffer[0] = (byte)b0;
                            this.fBuffer[1] = (byte)b1;
                            this.fOffset = 2;
                            return out - offset;
                        }
                        this.expectedByte(3, 3);
                    }
                    ++count;
                }
                if ((b2 & 0xC0) != 128) {
                    if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fBuffer[1] = (byte)b1;
                        this.fBuffer[2] = (byte)b2;
                        this.fOffset = 3;
                        return out - offset;
                    }
                    this.invalidByte(3, 3, b2);
                }
                int c = b0 << 12 & 0xF000 | b1 << 6 & 0xFC0 | b2 & 0x3F;
                ch[out++] = (char)c;
                count -= 2;
                continue;
            }
            if ((b0 & 0xF8) == 240) {
                int uuuuu;
                b1 = -1;
                if (++in < total) {
                    b1 = this.fBuffer[in] & 0xFF;
                } else {
                    b1 = this.fInputStream.read();
                    if (b1 == -1) {
                        if (out > offset) {
                            this.fBuffer[0] = (byte)b0;
                            this.fOffset = 1;
                            return out - offset;
                        }
                        this.expectedByte(2, 4);
                    }
                    ++count;
                }
                if ((b1 & 0xC0) != 128) {
                    if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fBuffer[1] = (byte)b1;
                        this.fOffset = 2;
                        return out - offset;
                    }
                    this.invalidByte(2, 4, b1);
                }
                b2 = -1;
                if (++in < total) {
                    b2 = this.fBuffer[in] & 0xFF;
                } else {
                    b2 = this.fInputStream.read();
                    if (b2 == -1) {
                        if (out > offset) {
                            this.fBuffer[0] = (byte)b0;
                            this.fBuffer[1] = (byte)b1;
                            this.fOffset = 2;
                            return out - offset;
                        }
                        this.expectedByte(3, 4);
                    }
                    ++count;
                }
                if ((b2 & 0xC0) != 128) {
                    if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fBuffer[1] = (byte)b1;
                        this.fBuffer[2] = (byte)b2;
                        this.fOffset = 3;
                        return out - offset;
                    }
                    this.invalidByte(3, 4, b2);
                }
                int b3 = -1;
                if (++in < total) {
                    b3 = this.fBuffer[in] & 0xFF;
                } else {
                    b3 = this.fInputStream.read();
                    if (b3 == -1) {
                        if (out > offset) {
                            this.fBuffer[0] = (byte)b0;
                            this.fBuffer[1] = (byte)b1;
                            this.fBuffer[2] = (byte)b2;
                            this.fOffset = 3;
                            return out - offset;
                        }
                        this.expectedByte(4, 4);
                    }
                    ++count;
                }
                if ((b3 & 0xC0) != 128) {
                    if (out > offset) {
                        this.fBuffer[0] = (byte)b0;
                        this.fBuffer[1] = (byte)b1;
                        this.fBuffer[2] = (byte)b2;
                        this.fBuffer[3] = (byte)b3;
                        this.fOffset = 4;
                        return out - offset;
                    }
                    this.invalidByte(4, 4, b2);
                }
                if ((uuuuu = b0 << 2 & 0x1C | b1 >> 4 & 3) > 16) {
                    this.invalidSurrogate(uuuuu);
                }
                int wwww = uuuuu - 1;
                int zzzz = b1 & 0xF;
                int yyyyyy = b2 & 0x3F;
                int xxxxxx = b3 & 0x3F;
                int hs = 0xD800 | wwww << 6 & 0x3C0 | zzzz << 2 | yyyyyy >> 4;
                int ls = 0xDC00 | yyyyyy << 6 & 0x3C0 | xxxxxx;
                ch[out++] = (char)hs;
                ch[out++] = (char)ls;
                count -= 2;
                continue;
            }
            if (out > offset) {
                this.fBuffer[0] = (byte)b0;
                this.fOffset = 1;
                return out - offset;
            }
            this.invalidByte(1, 1, b0);
        }
        return count;
    }

    @Override
    public long skip(long n) throws IOException {
        int length;
        int count;
        long remaining = n;
        char[] ch = new char[this.fBuffer.length];
        while ((count = this.read(ch, 0, length = (long)ch.length < remaining ? ch.length : (int)remaining)) > 0 && (remaining -= (long)count) > 0L) {
        }
        long skipped = n - remaining;
        return skipped;
    }

    @Override
    public boolean ready() throws IOException {
        return false;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        throw new IOException(Localizer.getMessage("jsp.error.xml.operationNotSupported", "mark()", "UTF-8"));
    }

    @Override
    public void reset() throws IOException {
        this.fOffset = 0;
        this.fSurrogate = -1;
    }

    @Override
    public void close() throws IOException {
        this.fInputStream.close();
    }

    private void expectedByte(int position, int count) throws UTFDataFormatException {
        throw new UTFDataFormatException(Localizer.getMessage("jsp.error.xml.expectedByte", Integer.toString(position), Integer.toString(count)));
    }

    private void invalidByte(int position, int count, int c) throws UTFDataFormatException {
        throw new UTFDataFormatException(Localizer.getMessage("jsp.error.xml.invalidByte", Integer.toString(position), Integer.toString(count)));
    }

    private void invalidSurrogate(int uuuuu) throws UTFDataFormatException {
        throw new UTFDataFormatException(Localizer.getMessage("jsp.error.xml.invalidHighSurrogate", Integer.toHexString(uuuuu)));
    }
}

