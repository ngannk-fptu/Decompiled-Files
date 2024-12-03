/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.Filter;

public final class UrlEncoding {
    private static final String DEFAULT_ENCODING = "UTF-8";
    public static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private UrlEncoding() {
    }

    private static void encode(Appendable sb, byte ... bytes) {
        UrlEncoding.encode(sb, 0, bytes.length, bytes);
    }

    private static void encode(Appendable sb, int offset, int length, byte ... bytes) {
        try {
            int n = offset;
            for (int i = 0; n < bytes.length && i < length; ++n, ++i) {
                byte c = bytes[n];
                sb.append("%");
                sb.append(HEX[c >> 4 & 0xF]);
                sb.append(HEX[c >> 0 & 0xF]);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encode(char ... chars) {
        return UrlEncoding.encode(chars, 0, chars.length, DEFAULT_ENCODING, new Filter[0]);
    }

    public static String encode(char[] chars, Filter Filter2) {
        return UrlEncoding.encode(chars, 0, chars.length, DEFAULT_ENCODING, new Filter[]{Filter2});
    }

    public static String encode(char[] chars, Filter ... filters) {
        return UrlEncoding.encode(chars, 0, chars.length, DEFAULT_ENCODING, filters);
    }

    public static String encode(char[] chars, String enc) {
        return UrlEncoding.encode(chars, 0, chars.length, enc, new Filter[0]);
    }

    public static String encode(char[] chars, String enc, Filter Filter2) {
        return UrlEncoding.encode(chars, 0, chars.length, enc, new Filter[]{Filter2});
    }

    public static String encode(char[] chars, String enc, Filter ... filters) {
        return UrlEncoding.encode(chars, 0, chars.length, enc, filters);
    }

    public static String encode(char[] chars, int offset, int length) {
        return UrlEncoding.encode(chars, offset, length, DEFAULT_ENCODING, new Filter[0]);
    }

    public static String encode(char[] chars, int offset, int length, String enc) {
        return UrlEncoding.encode(chars, offset, length, enc, new Filter[0]);
    }

    public static String encode(char[] chars, int offset, int length, Filter Filter2) {
        return UrlEncoding.encode(chars, offset, length, DEFAULT_ENCODING, new Filter[]{Filter2});
    }

    public static String encode(char[] chars, int offset, int length, Filter ... filters) {
        return UrlEncoding.encode(chars, offset, length, DEFAULT_ENCODING, filters);
    }

    public static String encode(char[] chars, int offset, int length, String enc, Filter Filter2) {
        return UrlEncoding.encode(chars, offset, length, enc, new Filter[]{Filter2});
    }

    public static String encode(char[] chars, int offset, int length, String enc, Filter ... filters) {
        try {
            return UrlEncoding.encode((CharSequence)CharBuffer.wrap(chars, offset, length), enc, filters);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encode(InputStream in) throws IOException {
        StringBuilder buf = new StringBuilder();
        byte[] chunk = new byte[1024];
        int r = -1;
        while ((r = in.read(chunk)) > -1) {
            UrlEncoding.encode((Appendable)buf, 0, r, chunk);
        }
        return buf.toString();
    }

    public static String encode(InputStream in, String charset) throws IOException {
        return UrlEncoding.encode(in, charset, DEFAULT_ENCODING, new Filter[0]);
    }

    public static String encode(InputStream in, String charset, Filter Filter2) throws IOException {
        return UrlEncoding.encode(in, charset, DEFAULT_ENCODING, new Filter[]{Filter2});
    }

    public static String encode(InputStream in, String charset, String enc) throws IOException {
        return UrlEncoding.encode(in, charset, enc, new Filter[0]);
    }

    public static String encode(InputStream in, String charset, String enc, Filter Filter2) throws IOException {
        return UrlEncoding.encode(in, charset, enc, new Filter[]{Filter2});
    }

    public static String encode(InputStream in, String charset, String enc, Filter ... filters) throws IOException {
        return UrlEncoding.encode((Reader)new InputStreamReader(in, charset), enc, filters);
    }

    public static String encode(InputStream in, String charset, Filter ... filters) throws IOException {
        return UrlEncoding.encode((Reader)new InputStreamReader(in, charset), DEFAULT_ENCODING, filters);
    }

    public static String encode(Reader reader) throws IOException {
        return UrlEncoding.encode(reader, DEFAULT_ENCODING, new Filter[0]);
    }

    public static String encode(Readable readable) throws IOException {
        return UrlEncoding.encode(readable, DEFAULT_ENCODING, new Filter[0]);
    }

    public static String encode(Reader reader, String enc) throws IOException {
        return UrlEncoding.encode(reader, enc, new Filter[0]);
    }

    public static String encode(Readable readable, String enc) throws IOException {
        return UrlEncoding.encode(readable, enc, new Filter[0]);
    }

    public static String encode(Reader reader, String enc, Filter Filter2) throws IOException {
        return UrlEncoding.encode(reader, enc, new Filter[]{Filter2});
    }

    public static String encode(Reader reader, Filter Filter2) throws IOException {
        return UrlEncoding.encode(reader, DEFAULT_ENCODING, new Filter[]{Filter2});
    }

    public static String encode(Reader reader, Filter ... filters) throws IOException {
        return UrlEncoding.encode(reader, DEFAULT_ENCODING, filters);
    }

    public static String encode(Readable readable, String enc, Filter Filter2) throws IOException {
        return UrlEncoding.encode(readable, enc, new Filter[]{Filter2});
    }

    public static String encode(Readable readable, Filter Filter2) throws IOException {
        return UrlEncoding.encode(readable, DEFAULT_ENCODING, new Filter[]{Filter2});
    }

    public static String encode(Readable readable, Filter ... filters) throws IOException {
        return UrlEncoding.encode(readable, DEFAULT_ENCODING, filters);
    }

    private static void processChars(StringBuilder sb, CharBuffer chars, String enc, Filter ... filters) throws IOException {
        for (int n = 0; n < chars.length(); ++n) {
            char c = chars.charAt(n);
            if (!CharUtils.isHighSurrogate(c) && UrlEncoding.check(c, filters)) {
                UrlEncoding.encode((Appendable)sb, String.valueOf(c).getBytes(enc));
                continue;
            }
            if (CharUtils.isHighSurrogate(c)) {
                if (UrlEncoding.check(c, filters)) {
                    StringBuilder buf = new StringBuilder();
                    buf.append(c);
                    buf.append(chars.charAt(++n));
                    byte[] b = buf.toString().getBytes(enc);
                    UrlEncoding.encode((Appendable)sb, b);
                    continue;
                }
                sb.append(c);
                sb.append(chars.charAt(++n));
                continue;
            }
            sb.append(c);
        }
    }

    public static String encode(Readable readable, String enc, Filter ... filters) throws IOException {
        StringBuilder sb = new StringBuilder();
        CharBuffer chars = CharBuffer.allocate(1024);
        while (readable.read(chars) > -1) {
            chars.flip();
            UrlEncoding.processChars(sb, chars, enc, filters);
        }
        return sb.toString();
    }

    public static String encode(Reader reader, String enc, Filter ... filters) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] chunk = new char[1024];
        int r = -1;
        while ((r = reader.read(chunk)) > -1) {
            UrlEncoding.processChars(sb, CharBuffer.wrap(chunk, 0, r), enc, filters);
        }
        return sb.toString();
    }

    public static String encode(byte ... bytes) {
        StringBuilder buf = new StringBuilder();
        UrlEncoding.encode((Appendable)buf, bytes);
        return buf.toString();
    }

    public static String encode(byte[] bytes, int off, int len) {
        StringBuilder buf = new StringBuilder();
        UrlEncoding.encode((Appendable)buf, off, len, bytes);
        return buf.toString();
    }

    public static String encode(CharSequence s) {
        return UrlEncoding.encode(s, Filter.NONOPFILTER);
    }

    public static String encode(CharSequence s, Filter Filter2) {
        return UrlEncoding.encode(s, new Filter[]{Filter2});
    }

    public static String encode(CharSequence s, Filter ... filters) {
        try {
            if (s == null) {
                return null;
            }
            return UrlEncoding.encode(s, "utf-8", filters);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String encode(CharSequence s, int offset, int length) {
        return UrlEncoding.encode(s, offset, length, Filter.NONOPFILTER);
    }

    public static String encode(CharSequence s, int offset, int length, Filter Filter2) {
        return UrlEncoding.encode(s, offset, length, new Filter[]{Filter2});
    }

    public static String encode(CharSequence s, int offset, int length, Filter ... filters) {
        try {
            if (s == null) {
                return null;
            }
            return UrlEncoding.encode(s, offset, length, "utf-8", filters);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static boolean check(int codepoint, Filter ... filters) {
        for (Filter Filter2 : filters) {
            if (!Filter2.accept(codepoint)) continue;
            return true;
        }
        return false;
    }

    public static String encode(CharSequence s, int offset, int length, String enc, Filter ... filters) throws UnsupportedEncodingException {
        int end = Math.min(s.length(), offset + length);
        CharSequence seq = s.subSequence(offset, end);
        return UrlEncoding.encode(seq, enc, filters);
    }

    public static String encode(CharSequence s, String enc, Filter ... filters) throws UnsupportedEncodingException {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < s.length(); ++n) {
            char c = s.charAt(n);
            if (!CharUtils.isHighSurrogate(c) && UrlEncoding.check(c, filters)) {
                UrlEncoding.encode((Appendable)sb, String.valueOf(c).getBytes(enc));
                continue;
            }
            if (CharUtils.isHighSurrogate(c)) {
                if (UrlEncoding.check(c, filters)) {
                    StringBuilder buf = new StringBuilder();
                    buf.append(c);
                    buf.append(s.charAt(++n));
                    byte[] b = buf.toString().getBytes(enc);
                    UrlEncoding.encode((Appendable)sb, b);
                    continue;
                }
                sb.append(c);
                sb.append(s.charAt(++n));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String decode(String e, String enc) throws UnsupportedEncodingException {
        DecodingReader r = new DecodingReader(e.getBytes(enc), enc);
        char[] buf = new char[e.length()];
        try {
            int l = r.read(buf);
            e = new String(buf, 0, l);
        }
        catch (Exception ex) {
            // empty catch block
        }
        return e;
    }

    public static String decode(String e) {
        try {
            return UrlEncoding.decode(e, "utf-8");
        }
        catch (Exception ex) {
            return e;
        }
    }

    private static byte decode(char c, int shift) {
        return (byte)(((c >= '0' && c <= '9' ? c - 48 : (c >= 'A' && c <= 'F' ? c - 65 + 10 : (c >= 'a' && c <= 'f' ? c - 97 + 10 : -1))) & 0xF) << shift);
    }

    private static byte decode(char c1, char c2) {
        return (byte)(UrlEncoding.decode(c1, 4) | UrlEncoding.decode(c2, 0));
    }

    public static class DecodingReader
    extends FilterReader {
        public DecodingReader(byte[] buf) throws UnsupportedEncodingException {
            this(new ByteArrayInputStream(buf));
        }

        public DecodingReader(byte[] buf, String enc) throws UnsupportedEncodingException {
            this(new ByteArrayInputStream(buf), enc);
        }

        public DecodingReader(InputStream in) throws UnsupportedEncodingException {
            this(in, UrlEncoding.DEFAULT_ENCODING);
        }

        public DecodingReader(InputStream in, String enc) throws UnsupportedEncodingException {
            this(new InputStreamReader(in, enc));
        }

        public DecodingReader(Reader in) {
            super(in);
        }

        public int read() throws IOException {
            int c = super.read();
            if (c == 37) {
                int c1 = super.read();
                int c2 = super.read();
                return UrlEncoding.decode((char)c1, (char)c2);
            }
            return c;
        }

        public synchronized int read(char[] b, int off, int len) throws IOException {
            int n = off;
            int i = -1;
            while ((i = this.read()) != -1 && n < off + len) {
                b[n++] = (char)i;
            }
            return n - off;
        }

        public int read(char[] b) throws IOException {
            return this.read(b, 0, b.length);
        }

        public long skip(long n) throws IOException {
            long i;
            for (i = 0L; i < n; ++i) {
                this.read();
            }
            return i;
        }
    }

    public static class DecodingInputStream
    extends FilterInputStream {
        public DecodingInputStream(InputStream in) {
            super(in);
        }

        public DecodingInputStream(byte[] in) {
            super(new ByteArrayInputStream(in));
        }

        public int read() throws IOException {
            int c = super.read();
            if (c == 37) {
                int c1 = super.read();
                int c2 = super.read();
                return UrlEncoding.decode((char)c1, (char)c2);
            }
            return c;
        }

        public synchronized int read(byte[] b, int off, int len) throws IOException {
            int n = off;
            int i = -1;
            while ((i = this.read()) != -1 && n < off + len) {
                b[n++] = (byte)i;
            }
            return n - off;
        }

        public int read(byte[] b) throws IOException {
            return this.read(b, 0, b.length);
        }

        public long skip(long n) throws IOException {
            long i;
            for (i = 0L; i < n; ++i) {
                this.read();
            }
            return i;
        }
    }

    public static class EncodingWriter
    extends FilterWriter {
        private final Filter[] filters;

        public EncodingWriter(OutputStream out) {
            this(new OutputStreamWriter(out));
        }

        public EncodingWriter(OutputStream out, Filter Filter2) {
            this((Writer)new OutputStreamWriter(out), Filter2);
        }

        public EncodingWriter(OutputStream out, Filter ... filters) {
            this((Writer)new OutputStreamWriter(out), filters);
        }

        public EncodingWriter(Writer out) {
            this(out, new Filter[0]);
        }

        public EncodingWriter(Writer out, Filter Filter2) {
            this(out, new Filter[]{Filter2});
        }

        public EncodingWriter(Writer out, Filter ... filters) {
            super(out);
            this.filters = filters;
        }

        public void write(char[] b, int off, int len) throws IOException {
            String enc = UrlEncoding.encode(b, off, len, this.filters);
            this.out.write(enc.toCharArray());
        }

        public void write(char[] b) throws IOException {
            String enc = UrlEncoding.encode(b, this.filters);
            this.out.write(enc.toCharArray());
        }

        public void write(int b) throws IOException {
            String enc = UrlEncoding.encode(new char[]{(char)b}, this.filters);
            this.out.write(enc.toCharArray());
        }

        public void write(String str, int off, int len) throws IOException {
            String enc = UrlEncoding.encode((CharSequence)str, off, len, this.filters);
            this.out.write(enc.toCharArray());
        }
    }

    public static class EncodingOutputStream
    extends FilterOutputStream {
        public EncodingOutputStream(OutputStream out) {
            super(out);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            String enc = UrlEncoding.encode(b, off, len);
            this.out.write(enc.getBytes(UrlEncoding.DEFAULT_ENCODING));
        }

        public void write(byte[] b) throws IOException {
            String enc = UrlEncoding.encode(b);
            this.out.write(enc.getBytes(UrlEncoding.DEFAULT_ENCODING));
        }

        public void write(int b) throws IOException {
            String enc = UrlEncoding.encode((byte)b);
            this.out.write(enc.getBytes(UrlEncoding.DEFAULT_ENCODING));
        }
    }
}

