/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;
import org.apache.xerces.impl.io.MalformedByteSequenceException;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.util.MessageFormatter;

public final class UTF8Reader
extends Reader {
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final boolean DEBUG_READ = false;
    protected final InputStream fInputStream;
    protected final byte[] fBuffer;
    protected int fOffset;
    private int fSurrogate = -1;
    private final MessageFormatter fFormatter;
    private final Locale fLocale;

    public UTF8Reader(InputStream inputStream) {
        this(inputStream, 2048, (MessageFormatter)new XMLMessageFormatter(), Locale.getDefault());
    }

    public UTF8Reader(InputStream inputStream, MessageFormatter messageFormatter, Locale locale) {
        this(inputStream, 2048, messageFormatter, locale);
    }

    public UTF8Reader(InputStream inputStream, int n, MessageFormatter messageFormatter, Locale locale) {
        this(inputStream, new byte[n], messageFormatter, locale);
    }

    public UTF8Reader(InputStream inputStream, byte[] byArray, MessageFormatter messageFormatter, Locale locale) {
        this.fInputStream = inputStream;
        this.fBuffer = byArray;
        this.fFormatter = messageFormatter;
        this.fLocale = locale;
    }

    @Override
    public int read() throws IOException {
        int n = this.fSurrogate;
        if (this.fSurrogate == -1) {
            int n2;
            int n3 = 0;
            int n4 = n2 = n3 == this.fOffset ? this.fInputStream.read() : this.fBuffer[n3++] & 0xFF;
            if (n2 == -1) {
                return -1;
            }
            if (n2 < 128) {
                n = (char)n2;
            } else if ((n2 & 0xE0) == 192 && (n2 & 0x1E) != 0) {
                int n5;
                int n6 = n5 = n3 == this.fOffset ? this.fInputStream.read() : this.fBuffer[n3++] & 0xFF;
                if (n5 == -1) {
                    this.expectedByte(2, 2);
                }
                if ((n5 & 0xC0) != 128) {
                    this.invalidByte(2, 2, n5);
                }
                n = n2 << 6 & 0x7C0 | n5 & 0x3F;
            } else if ((n2 & 0xF0) == 224) {
                int n7;
                int n8;
                int n9 = n8 = n3 == this.fOffset ? this.fInputStream.read() : this.fBuffer[n3++] & 0xFF;
                if (n8 == -1) {
                    this.expectedByte(2, 3);
                }
                if ((n8 & 0xC0) != 128 || n2 == 237 && n8 >= 160 || (n2 & 0xF) == 0 && (n8 & 0x20) == 0) {
                    this.invalidByte(2, 3, n8);
                }
                int n10 = n7 = n3 == this.fOffset ? this.fInputStream.read() : this.fBuffer[n3++] & 0xFF;
                if (n7 == -1) {
                    this.expectedByte(3, 3);
                }
                if ((n7 & 0xC0) != 128) {
                    this.invalidByte(3, 3, n7);
                }
                n = n2 << 12 & 0xF000 | n8 << 6 & 0xFC0 | n7 & 0x3F;
            } else if ((n2 & 0xF8) == 240) {
                int n11;
                int n12;
                int n13;
                int n14;
                int n15 = n14 = n3 == this.fOffset ? this.fInputStream.read() : this.fBuffer[n3++] & 0xFF;
                if (n14 == -1) {
                    this.expectedByte(2, 4);
                }
                if ((n14 & 0xC0) != 128 || (n14 & 0x30) == 0 && (n2 & 7) == 0) {
                    this.invalidByte(2, 3, n14);
                }
                int n16 = n13 = n3 == this.fOffset ? this.fInputStream.read() : this.fBuffer[n3++] & 0xFF;
                if (n13 == -1) {
                    this.expectedByte(3, 4);
                }
                if ((n13 & 0xC0) != 128) {
                    this.invalidByte(3, 3, n13);
                }
                int n17 = n12 = n3 == this.fOffset ? this.fInputStream.read() : this.fBuffer[n3++] & 0xFF;
                if (n12 == -1) {
                    this.expectedByte(4, 4);
                }
                if ((n12 & 0xC0) != 128) {
                    this.invalidByte(4, 4, n12);
                }
                if ((n11 = n2 << 2 & 0x1C | n14 >> 4 & 3) > 16) {
                    this.invalidSurrogate(n11);
                }
                int n18 = n11 - 1;
                int n19 = 0xD800 | n18 << 6 & 0x3C0 | n14 << 2 & 0x3C | n13 >> 4 & 3;
                int n20 = 0xDC00 | n13 << 6 & 0x3C0 | n12 & 0x3F;
                n = n19;
                this.fSurrogate = n20;
            } else {
                this.invalidByte(1, 1, n2);
            }
        } else {
            this.fSurrogate = -1;
        }
        return n;
    }

    @Override
    public int read(char[] cArray, int n, int n2) throws IOException {
        byte by;
        int n3;
        int n4 = n;
        int n5 = 0;
        if (this.fOffset == 0) {
            if (n2 > this.fBuffer.length) {
                n2 = this.fBuffer.length;
            }
            if (this.fSurrogate != -1) {
                cArray[n4++] = (char)this.fSurrogate;
                this.fSurrogate = -1;
                --n2;
            }
            if ((n5 = this.fInputStream.read(this.fBuffer, 0, n2)) == -1) {
                return -1;
            }
            n5 += n4 - n;
        } else {
            n5 = this.fOffset;
            this.fOffset = 0;
        }
        int n6 = n5;
        for (n3 = 0; n3 < n6 && (by = this.fBuffer[n3]) >= 0; ++n3) {
            cArray[n4++] = (char)by;
        }
        while (n3 < n6) {
            by = this.fBuffer[n3];
            if (by >= 0) {
                cArray[n4++] = (char)by;
            } else {
                int n7;
                int n8;
                int n9;
                int n10 = by & 0xFF;
                if ((n10 & 0xE0) == 192 && (n10 & 0x1E) != 0) {
                    n9 = -1;
                    if (++n3 < n6) {
                        n9 = this.fBuffer[n3] & 0xFF;
                    } else {
                        n9 = this.fInputStream.read();
                        if (n9 == -1) {
                            if (n4 > n) {
                                this.fBuffer[0] = (byte)n10;
                                this.fOffset = 1;
                                return n4 - n;
                            }
                            this.expectedByte(2, 2);
                        }
                        ++n5;
                    }
                    if ((n9 & 0xC0) != 128) {
                        if (n4 > n) {
                            this.fBuffer[0] = (byte)n10;
                            this.fBuffer[1] = (byte)n9;
                            this.fOffset = 2;
                            return n4 - n;
                        }
                        this.invalidByte(2, 2, n9);
                    }
                    n8 = n10 << 6 & 0x7C0 | n9 & 0x3F;
                    cArray[n4++] = (char)n8;
                    --n5;
                } else if ((n10 & 0xF0) == 224) {
                    n9 = -1;
                    if (++n3 < n6) {
                        n9 = this.fBuffer[n3] & 0xFF;
                    } else {
                        n9 = this.fInputStream.read();
                        if (n9 == -1) {
                            if (n4 > n) {
                                this.fBuffer[0] = (byte)n10;
                                this.fOffset = 1;
                                return n4 - n;
                            }
                            this.expectedByte(2, 3);
                        }
                        ++n5;
                    }
                    if ((n9 & 0xC0) != 128 || n10 == 237 && n9 >= 160 || (n10 & 0xF) == 0 && (n9 & 0x20) == 0) {
                        if (n4 > n) {
                            this.fBuffer[0] = (byte)n10;
                            this.fBuffer[1] = (byte)n9;
                            this.fOffset = 2;
                            return n4 - n;
                        }
                        this.invalidByte(2, 3, n9);
                    }
                    n8 = -1;
                    if (++n3 < n6) {
                        n8 = this.fBuffer[n3] & 0xFF;
                    } else {
                        n8 = this.fInputStream.read();
                        if (n8 == -1) {
                            if (n4 > n) {
                                this.fBuffer[0] = (byte)n10;
                                this.fBuffer[1] = (byte)n9;
                                this.fOffset = 2;
                                return n4 - n;
                            }
                            this.expectedByte(3, 3);
                        }
                        ++n5;
                    }
                    if ((n8 & 0xC0) != 128) {
                        if (n4 > n) {
                            this.fBuffer[0] = (byte)n10;
                            this.fBuffer[1] = (byte)n9;
                            this.fBuffer[2] = (byte)n8;
                            this.fOffset = 3;
                            return n4 - n;
                        }
                        this.invalidByte(3, 3, n8);
                    }
                    n7 = n10 << 12 & 0xF000 | n9 << 6 & 0xFC0 | n8 & 0x3F;
                    cArray[n4++] = (char)n7;
                    n5 -= 2;
                } else if ((n10 & 0xF8) == 240) {
                    int n11;
                    n9 = -1;
                    if (++n3 < n6) {
                        n9 = this.fBuffer[n3] & 0xFF;
                    } else {
                        n9 = this.fInputStream.read();
                        if (n9 == -1) {
                            if (n4 > n) {
                                this.fBuffer[0] = (byte)n10;
                                this.fOffset = 1;
                                return n4 - n;
                            }
                            this.expectedByte(2, 4);
                        }
                        ++n5;
                    }
                    if ((n9 & 0xC0) != 128 || (n9 & 0x30) == 0 && (n10 & 7) == 0) {
                        if (n4 > n) {
                            this.fBuffer[0] = (byte)n10;
                            this.fBuffer[1] = (byte)n9;
                            this.fOffset = 2;
                            return n4 - n;
                        }
                        this.invalidByte(2, 4, n9);
                    }
                    n8 = -1;
                    if (++n3 < n6) {
                        n8 = this.fBuffer[n3] & 0xFF;
                    } else {
                        n8 = this.fInputStream.read();
                        if (n8 == -1) {
                            if (n4 > n) {
                                this.fBuffer[0] = (byte)n10;
                                this.fBuffer[1] = (byte)n9;
                                this.fOffset = 2;
                                return n4 - n;
                            }
                            this.expectedByte(3, 4);
                        }
                        ++n5;
                    }
                    if ((n8 & 0xC0) != 128) {
                        if (n4 > n) {
                            this.fBuffer[0] = (byte)n10;
                            this.fBuffer[1] = (byte)n9;
                            this.fBuffer[2] = (byte)n8;
                            this.fOffset = 3;
                            return n4 - n;
                        }
                        this.invalidByte(3, 4, n8);
                    }
                    n7 = -1;
                    if (++n3 < n6) {
                        n7 = this.fBuffer[n3] & 0xFF;
                    } else {
                        n7 = this.fInputStream.read();
                        if (n7 == -1) {
                            if (n4 > n) {
                                this.fBuffer[0] = (byte)n10;
                                this.fBuffer[1] = (byte)n9;
                                this.fBuffer[2] = (byte)n8;
                                this.fOffset = 3;
                                return n4 - n;
                            }
                            this.expectedByte(4, 4);
                        }
                        ++n5;
                    }
                    if ((n7 & 0xC0) != 128) {
                        if (n4 > n) {
                            this.fBuffer[0] = (byte)n10;
                            this.fBuffer[1] = (byte)n9;
                            this.fBuffer[2] = (byte)n8;
                            this.fBuffer[3] = (byte)n7;
                            this.fOffset = 4;
                            return n4 - n;
                        }
                        this.invalidByte(4, 4, n8);
                    }
                    if ((n11 = n10 << 2 & 0x1C | n9 >> 4 & 3) > 16) {
                        this.invalidSurrogate(n11);
                    }
                    int n12 = n11 - 1;
                    int n13 = n9 & 0xF;
                    int n14 = n8 & 0x3F;
                    int n15 = n7 & 0x3F;
                    int n16 = 0xD800 | n12 << 6 & 0x3C0 | n13 << 2 | n14 >> 4;
                    int n17 = 0xDC00 | n14 << 6 & 0x3C0 | n15;
                    cArray[n4++] = (char)n16;
                    if ((n5 -= 2) <= n2) {
                        cArray[n4++] = (char)n17;
                    } else {
                        this.fSurrogate = n17;
                        --n5;
                    }
                } else {
                    if (n4 > n) {
                        this.fBuffer[0] = (byte)n10;
                        this.fOffset = 1;
                        return n4 - n;
                    }
                    this.invalidByte(1, 1, n10);
                }
            }
            ++n3;
        }
        return n5;
    }

    @Override
    public long skip(long l) throws IOException {
        int n;
        int n2;
        long l2 = l;
        char[] cArray = new char[this.fBuffer.length];
        while ((n2 = this.read(cArray, 0, n = (long)cArray.length < l2 ? cArray.length : (int)l2)) > 0 && (l2 -= (long)n2) > 0L) {
        }
        long l3 = l - l2;
        return l3;
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
    public void mark(int n) throws IOException {
        throw new IOException(this.fFormatter.formatMessage(this.fLocale, "OperationNotSupported", new Object[]{"mark()", "UTF-8"}));
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

    private void expectedByte(int n, int n2) throws MalformedByteSequenceException {
        throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "ExpectedByte", new Object[]{Integer.toString(n), Integer.toString(n2)});
    }

    private void invalidByte(int n, int n2, int n3) throws MalformedByteSequenceException {
        throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidByte", new Object[]{Integer.toString(n), Integer.toString(n2)});
    }

    private void invalidSurrogate(int n) throws MalformedByteSequenceException {
        throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidHighSurrogate", new Object[]{Integer.toHexString(n)});
    }
}

