/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfEncodings;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ByteBuffer
extends OutputStream {
    protected int count;
    protected byte[] buf;
    private static int byteCacheSize = 0;
    private static byte[][] byteCache = new byte[byteCacheSize][];
    public static final byte ZERO = 48;
    private static final char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final byte[] bytes = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    public static boolean HIGH_PRECISION = false;
    private static final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

    public ByteBuffer() {
        this(128);
    }

    public ByteBuffer(int size) {
        if (size < 1) {
            size = 128;
        }
        this.buf = new byte[size];
    }

    public static void setCacheSize(int size) {
        if (size > 3276700) {
            size = 3276700;
        }
        if (size <= byteCacheSize) {
            return;
        }
        byte[][] tmpCache = new byte[size][];
        System.arraycopy(byteCache, 0, tmpCache, 0, byteCacheSize);
        byteCache = tmpCache;
        byteCacheSize = size;
    }

    public static void fillCache(int decimals) {
        int step = 1;
        switch (decimals) {
            case 0: {
                step = 100;
                break;
            }
            case 1: {
                step = 10;
            }
        }
        for (int i = 1; i < byteCacheSize; i += step) {
            if (byteCache[i] != null) continue;
            ByteBuffer.byteCache[i] = ByteBuffer.convertToBytes(i);
        }
    }

    private static byte[] convertToBytes(int i) {
        int size = (int)Math.floor(Math.log(i) / Math.log(10.0));
        if (i % 100 != 0) {
            size += 2;
        }
        if (i % 10 != 0) {
            ++size;
        }
        if (i < 100) {
            ++size;
            if (i < 10) {
                ++size;
            }
        }
        byte[] cache = new byte[--size];
        --size;
        if (i < 100) {
            cache[0] = 48;
        }
        if (i % 10 != 0) {
            cache[size--] = bytes[i % 10];
        }
        if (i % 100 != 0) {
            cache[size--] = bytes[i / 10 % 10];
            cache[size--] = 46;
        }
        size = (int)Math.floor(Math.log(i) / Math.log(10.0)) - 1;
        for (int add = 0; add < size; ++add) {
            cache[add] = bytes[i / (int)Math.pow(10.0, size - add + 1) % 10];
        }
        return cache;
    }

    public ByteBuffer append_i(int b) {
        int newcount = this.count + 1;
        if (newcount > this.buf.length) {
            byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
        this.buf[this.count] = (byte)b;
        this.count = newcount;
        return this;
    }

    public ByteBuffer append(byte[] b, int off, int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0 || len == 0) {
            return this;
        }
        int newcount = this.count + len;
        if (newcount > this.buf.length) {
            byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count = newcount;
        return this;
    }

    public ByteBuffer append(byte[] b) {
        return this.append(b, 0, b.length);
    }

    public ByteBuffer append(String str) {
        if (str != null) {
            return this.append(DocWriter.getISOBytes(str));
        }
        return this;
    }

    public ByteBuffer append(char c) {
        return this.append_i(c);
    }

    public ByteBuffer append(ByteBuffer buf) {
        return this.append(buf.buf, 0, buf.count);
    }

    public ByteBuffer append(int i) {
        return this.append((double)i);
    }

    public ByteBuffer append(byte b) {
        return this.append_i(b);
    }

    public ByteBuffer appendHex(byte b) {
        this.append(bytes[b >> 4 & 0xF]);
        return this.append(bytes[b & 0xF]);
    }

    public ByteBuffer append(float i) {
        return this.append((double)i);
    }

    public ByteBuffer append(double d) {
        this.append(ByteBuffer.formatDouble(d, this));
        return this;
    }

    public static String formatDouble(double d) {
        return ByteBuffer.formatDouble(d, null);
    }

    public static String formatDouble(double d, ByteBuffer buf) {
        if (HIGH_PRECISION) {
            DecimalFormat dn = new DecimalFormat("0.######", dfs);
            String sform = dn.format(d);
            if (buf == null) {
                return sform;
            }
            buf.append(sform);
            return null;
        }
        boolean negative = false;
        if (Math.abs(d) < 1.5E-5) {
            if (buf != null) {
                buf.append((byte)48);
                return null;
            }
            return "0";
        }
        if (d < 0.0) {
            negative = true;
            d = -d;
        }
        if (d < 1.0) {
            if ((d += 5.0E-6) >= 1.0) {
                if (negative) {
                    if (buf != null) {
                        buf.append((byte)45);
                        buf.append((byte)49);
                        return null;
                    }
                    return "-1";
                }
                if (buf != null) {
                    buf.append((byte)49);
                    return null;
                }
                return "1";
            }
            if (buf != null) {
                int v = (int)(d * 100000.0);
                if (negative) {
                    buf.append((byte)45);
                }
                buf.append((byte)48);
                buf.append((byte)46);
                buf.append((byte)(v / 10000 + 48));
                if (v % 10000 != 0) {
                    buf.append((byte)(v / 1000 % 10 + 48));
                    if (v % 1000 != 0) {
                        buf.append((byte)(v / 100 % 10 + 48));
                        if (v % 100 != 0) {
                            buf.append((byte)(v / 10 % 10 + 48));
                            if (v % 10 != 0) {
                                buf.append((byte)(v % 10 + 48));
                            }
                        }
                    }
                }
                return null;
            }
            int x = 100000;
            int v = (int)(d * (double)x);
            StringBuilder res = new StringBuilder();
            if (negative) {
                res.append('-');
            }
            res.append("0.");
            while (v < x / 10) {
                res.append('0');
                x /= 10;
            }
            res.append(v);
            int cut = res.length() - 1;
            while (res.charAt(cut) == '0') {
                --cut;
            }
            res.setLength(cut + 1);
            return res.toString();
        }
        if (d <= 32767.0) {
            int v = (int)((d += 0.005) * 100.0);
            if (v < byteCacheSize && byteCache[v] != null) {
                if (buf != null) {
                    if (negative) {
                        buf.append((byte)45);
                    }
                    buf.append(byteCache[v]);
                    return null;
                }
                String tmp = PdfEncodings.convertToString(byteCache[v], null);
                if (negative) {
                    tmp = "-" + tmp;
                }
                return tmp;
            }
            if (buf != null) {
                if (v < byteCacheSize) {
                    int size = 0;
                    if (v >= 1000000) {
                        size += 5;
                    } else if (v >= 100000) {
                        size += 4;
                    } else if (v >= 10000) {
                        size += 3;
                    } else if (v >= 1000) {
                        size += 2;
                    } else if (v >= 100) {
                        ++size;
                    }
                    if (v % 100 != 0) {
                        size += 2;
                    }
                    if (v % 10 != 0) {
                        ++size;
                    }
                    byte[] cache = new byte[size];
                    int add = 0;
                    if (v >= 1000000) {
                        cache[add++] = bytes[v / 1000000];
                    }
                    if (v >= 100000) {
                        cache[add++] = bytes[v / 100000 % 10];
                    }
                    if (v >= 10000) {
                        cache[add++] = bytes[v / 10000 % 10];
                    }
                    if (v >= 1000) {
                        cache[add++] = bytes[v / 1000 % 10];
                    }
                    if (v >= 100) {
                        cache[add++] = bytes[v / 100 % 10];
                    }
                    if (v % 100 != 0) {
                        cache[add++] = 46;
                        cache[add++] = bytes[v / 10 % 10];
                        if (v % 10 != 0) {
                            cache[add++] = bytes[v % 10];
                        }
                    }
                    ByteBuffer.byteCache[v] = cache;
                }
                if (negative) {
                    buf.append((byte)45);
                }
                if (v >= 1000000) {
                    buf.append(bytes[v / 1000000]);
                }
                if (v >= 100000) {
                    buf.append(bytes[v / 100000 % 10]);
                }
                if (v >= 10000) {
                    buf.append(bytes[v / 10000 % 10]);
                }
                if (v >= 1000) {
                    buf.append(bytes[v / 1000 % 10]);
                }
                if (v >= 100) {
                    buf.append(bytes[v / 100 % 10]);
                }
                if (v % 100 != 0) {
                    buf.append((byte)46);
                    buf.append(bytes[v / 10 % 10]);
                    if (v % 10 != 0) {
                        buf.append(bytes[v % 10]);
                    }
                }
                return null;
            }
            StringBuilder res = new StringBuilder();
            if (negative) {
                res.append('-');
            }
            if (v >= 1000000) {
                res.append(chars[v / 1000000]);
            }
            if (v >= 100000) {
                res.append(chars[v / 100000 % 10]);
            }
            if (v >= 10000) {
                res.append(chars[v / 10000 % 10]);
            }
            if (v >= 1000) {
                res.append(chars[v / 1000 % 10]);
            }
            if (v >= 100) {
                res.append(chars[v / 100 % 10]);
            }
            if (v % 100 != 0) {
                res.append('.');
                res.append(chars[v / 10 % 10]);
                if (v % 10 != 0) {
                    res.append(chars[v % 10]);
                }
            }
            return res.toString();
        }
        StringBuilder res = new StringBuilder();
        if (negative) {
            res.append('-');
        }
        long v = (long)(d += 0.5);
        return res.append(v).toString();
    }

    public void reset() {
        this.count = 0;
    }

    public byte[] toByteArray() {
        byte[] newbuf = new byte[this.count];
        System.arraycopy(this.buf, 0, newbuf, 0, this.count);
        return newbuf;
    }

    public int size() {
        return this.count;
    }

    public void setSize(int size) {
        if (size > this.count || size < 0) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("the.new.size.must.be.positive.and.lt.eq.of.the.current.size"));
        }
        this.count = size;
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }

    public String toString(String enc) throws UnsupportedEncodingException {
        return new String(this.buf, 0, this.count, enc);
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.buf, 0, this.count);
    }

    @Override
    public void write(int b) {
        this.append((byte)b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        this.append(b, off, len);
    }

    public byte[] getBuffer() {
        return this.buf;
    }
}

