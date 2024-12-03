/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import org.apache.tomcat.util.buf.AbstractChunk;
import org.apache.tomcat.util.buf.Ascii;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.StringCache;

public final class ByteChunk
extends AbstractChunk {
    private static final long serialVersionUID = 1L;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;
    private transient Charset charset;
    private byte[] buff;
    private transient ByteInputChannel in = null;
    private transient ByteOutputChannel out = null;

    public ByteChunk() {
    }

    public ByteChunk(int initial) {
        this.allocate(initial, -1);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeUTF(this.getCharset().name());
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.charset = Charset.forName(ois.readUTF());
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void recycle() {
        super.recycle();
        this.charset = null;
    }

    public void allocate(int initial, int limit) {
        if (this.buff == null || this.buff.length < initial) {
            this.buff = new byte[initial];
        }
        this.setLimit(limit);
        this.start = 0;
        this.end = 0;
        this.isSet = true;
        this.hasHashCode = false;
    }

    public void setBytes(byte[] b, int off, int len) {
        this.buff = b;
        this.start = off;
        this.end = this.start + len;
        this.isSet = true;
        this.hasHashCode = false;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        if (this.charset == null) {
            this.charset = DEFAULT_CHARSET;
        }
        return this.charset;
    }

    public byte[] getBytes() {
        return this.getBuffer();
    }

    public byte[] getBuffer() {
        return this.buff;
    }

    public void setByteInputChannel(ByteInputChannel in) {
        this.in = in;
    }

    public void setByteOutputChannel(ByteOutputChannel out) {
        this.out = out;
    }

    public void append(byte b) throws IOException {
        this.makeSpace(1);
        int limit = this.getLimitInternal();
        if (this.end >= limit) {
            this.flushBuffer();
        }
        this.buff[this.end++] = b;
    }

    public void append(ByteChunk src) throws IOException {
        this.append(src.getBytes(), src.getStart(), src.getLength());
    }

    public void append(byte[] src, int off, int len) throws IOException {
        int remain;
        this.makeSpace(len);
        int limit = this.getLimitInternal();
        if (len == limit && this.end == this.start && this.out != null) {
            this.out.realWriteBytes(src, off, len);
            return;
        }
        if (len <= limit - this.end) {
            System.arraycopy(src, off, this.buff, this.end, len);
            this.end += len;
            return;
        }
        int avail = limit - this.end;
        System.arraycopy(src, off, this.buff, this.end, avail);
        this.end += avail;
        this.flushBuffer();
        for (remain = len - avail; remain > limit - this.end; remain -= limit - this.end) {
            this.out.realWriteBytes(src, off + len - remain, limit - this.end);
        }
        System.arraycopy(src, off + len - remain, this.buff, this.end, remain);
        this.end += remain;
    }

    public void append(ByteBuffer from) throws IOException {
        int len = from.remaining();
        this.makeSpace(len);
        int limit = this.getLimitInternal();
        if (len == limit && this.end == this.start && this.out != null) {
            this.out.realWriteBytes(from);
            from.position(from.limit());
            return;
        }
        if (len <= limit - this.end) {
            from.get(this.buff, this.end, len);
            this.end += len;
            return;
        }
        int avail = limit - this.end;
        from.get(this.buff, this.end, avail);
        this.end += avail;
        this.flushBuffer();
        int fromLimit = from.limit();
        int remain = len - avail;
        avail = limit - this.end;
        while (remain >= avail) {
            from.limit(from.position() + avail);
            this.out.realWriteBytes(from);
            from.position(from.limit());
            remain -= avail;
        }
        from.limit(fromLimit);
        from.get(this.buff, this.end, remain);
        this.end += remain;
    }

    @Deprecated
    public int substract() throws IOException {
        return this.subtract();
    }

    public int subtract() throws IOException {
        if (this.checkEof()) {
            return -1;
        }
        return this.buff[this.start++] & 0xFF;
    }

    @Deprecated
    public byte substractB() throws IOException {
        return this.subtractB();
    }

    public byte subtractB() throws IOException {
        if (this.checkEof()) {
            return -1;
        }
        return this.buff[this.start++];
    }

    @Deprecated
    public int substract(byte[] dest, int off, int len) throws IOException {
        return this.subtract(dest, off, len);
    }

    public int subtract(byte[] dest, int off, int len) throws IOException {
        if (this.checkEof()) {
            return -1;
        }
        int n = len;
        if (len > this.getLength()) {
            n = this.getLength();
        }
        System.arraycopy(this.buff, this.start, dest, off, n);
        this.start += n;
        return n;
    }

    @Deprecated
    public int substract(ByteBuffer to) throws IOException {
        return this.subtract(to);
    }

    public int subtract(ByteBuffer to) throws IOException {
        if (this.checkEof()) {
            return -1;
        }
        int n = Math.min(to.remaining(), this.getLength());
        to.put(this.buff, this.start, n);
        to.limit(to.position());
        to.position(to.position() - n);
        this.start += n;
        return n;
    }

    private boolean checkEof() throws IOException {
        if (this.end - this.start == 0) {
            if (this.in == null) {
                return true;
            }
            int n = this.in.realReadBytes();
            if (n < 0) {
                return true;
            }
        }
        return false;
    }

    public void flushBuffer() throws IOException {
        if (this.out == null) {
            throw new BufferOverflowException(sm.getString("chunk.overflow", this.getLimit(), this.buff.length));
        }
        this.out.realWriteBytes(this.buff, this.start, this.end - this.start);
        this.end = this.start;
    }

    public void makeSpace(int count) {
        byte[] tmp = null;
        long desiredSize = this.end + count;
        int limit = this.getLimitInternal();
        if (desiredSize > (long)limit) {
            desiredSize = limit;
        }
        if (this.buff == null) {
            if (desiredSize < 256L) {
                desiredSize = 256L;
            }
            this.buff = new byte[(int)desiredSize];
        }
        if (desiredSize <= (long)this.buff.length) {
            return;
        }
        long newSize = desiredSize < 2L * (long)this.buff.length ? (long)this.buff.length * 2L : (long)this.buff.length * 2L + (long)count;
        if (newSize > (long)limit) {
            newSize = limit;
        }
        tmp = new byte[(int)newSize];
        System.arraycopy(this.buff, this.start, tmp, 0, this.end - this.start);
        this.buff = tmp;
        tmp = null;
        this.end -= this.start;
        this.start = 0;
    }

    public String toString() {
        try {
            return this.toString(CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
        }
        catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public String toString(CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) throws CharacterCodingException {
        if (this.isNull()) {
            return null;
        }
        if (this.end - this.start == 0) {
            return "";
        }
        return StringCache.toString(this, malformedInputAction, unmappableCharacterAction);
    }

    @Deprecated
    public String toStringInternal() {
        try {
            return this.toStringInternal(CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
        }
        catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public String toStringInternal(CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) throws CharacterCodingException {
        if (this.charset == null) {
            this.charset = DEFAULT_CHARSET;
        }
        CharBuffer cb = malformedInputAction == CodingErrorAction.REPLACE && unmappableCharacterAction == CodingErrorAction.REPLACE ? this.charset.decode(ByteBuffer.wrap(this.buff, this.start, this.end - this.start)) : this.charset.newDecoder().onMalformedInput(malformedInputAction).onUnmappableCharacter(unmappableCharacterAction).decode(ByteBuffer.wrap(this.buff, this.start, this.end - this.start));
        return new String(cb.array(), cb.arrayOffset(), cb.length());
    }

    public long getLong() {
        return Ascii.parseLong(this.buff, this.start, this.end - this.start);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ByteChunk) {
            return this.equals((ByteChunk)obj);
        }
        return false;
    }

    public boolean equals(String s) {
        byte[] b = this.buff;
        int len = this.end - this.start;
        if (b == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; ++i) {
            if (b[off++] == s.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public boolean equalsIgnoreCase(String s) {
        byte[] b = this.buff;
        int len = this.end - this.start;
        if (b == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; ++i) {
            if (Ascii.toLower(b[off++]) == Ascii.toLower(s.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public boolean equals(ByteChunk bb) {
        return this.equals(bb.getBytes(), bb.getStart(), bb.getLength());
    }

    public boolean equals(byte[] b2, int off2, int len2) {
        byte[] b1 = this.buff;
        if (b1 == null && b2 == null) {
            return true;
        }
        int len = this.end - this.start;
        if (len != len2 || b1 == null || b2 == null) {
            return false;
        }
        int off1 = this.start;
        while (len-- > 0) {
            if (b1[off1++] == b2[off2++]) continue;
            return false;
        }
        return true;
    }

    public boolean equals(CharChunk cc) {
        return this.equals(cc.getChars(), cc.getStart(), cc.getLength());
    }

    public boolean equals(char[] c2, int off2, int len2) {
        byte[] b1 = this.buff;
        if (c2 == null && b1 == null) {
            return true;
        }
        if (b1 == null || c2 == null || this.end - this.start != len2) {
            return false;
        }
        int off1 = this.start;
        int len = this.end - this.start;
        while (len-- > 0) {
            if ((char)b1[off1++] == c2[off2++]) continue;
            return false;
        }
        return true;
    }

    public boolean startsWith(String s, int pos) {
        byte[] b = this.buff;
        int len = s.length();
        if (b == null || len + pos > this.end - this.start) {
            return false;
        }
        int off = this.start + pos;
        for (int i = 0; i < len; ++i) {
            if (b[off++] == s.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public boolean startsWithIgnoreCase(String s, int pos) {
        byte[] b = this.buff;
        int len = s.length();
        if (b == null || len + pos > this.end - this.start) {
            return false;
        }
        int off = this.start + pos;
        for (int i = 0; i < len; ++i) {
            if (Ascii.toLower(b[off++]) == Ascii.toLower(s.charAt(i))) continue;
            return false;
        }
        return true;
    }

    @Override
    protected int getBufferElement(int index) {
        return this.buff[index];
    }

    public int indexOf(char c, int starting) {
        int ret = ByteChunk.indexOf(this.buff, this.start + starting, this.end, c);
        return ret >= this.start ? ret - this.start : -1;
    }

    public static int indexOf(byte[] bytes, int start, int end, char s) {
        for (int offset = start; offset < end; ++offset) {
            byte b = bytes[offset];
            if (b != s) continue;
            return offset;
        }
        return -1;
    }

    public static int findByte(byte[] bytes, int start, int end, byte b) {
        for (int offset = start; offset < end; ++offset) {
            if (bytes[offset] != b) continue;
            return offset;
        }
        return -1;
    }

    public static int findBytes(byte[] bytes, int start, int end, byte[] b) {
        for (int offset = start; offset < end; ++offset) {
            for (byte value : b) {
                if (bytes[offset] != value) continue;
                return offset;
            }
        }
        return -1;
    }

    public static byte[] convertToBytes(String value) {
        byte[] result = new byte[value.length()];
        for (int i = 0; i < value.length(); ++i) {
            result[i] = (byte)value.charAt(i);
        }
        return result;
    }

    public static interface ByteInputChannel {
        public int realReadBytes() throws IOException;
    }

    public static interface ByteOutputChannel {
        public void realWriteBytes(byte[] var1, int var2, int var3) throws IOException;

        public void realWriteBytes(ByteBuffer var1) throws IOException;
    }

    public static class BufferOverflowException
    extends IOException {
        private static final long serialVersionUID = 1L;

        public BufferOverflowException(String message) {
            super(message);
        }
    }
}

