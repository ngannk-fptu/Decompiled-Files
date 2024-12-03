/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.Codepoint;
import org.apache.abdera.i18n.text.DelegatingCodepointIterator;
import org.apache.abdera.i18n.text.Filter;
import org.apache.abdera.i18n.text.InvalidCharacterException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class CodepointIterator
implements Iterator<Codepoint> {
    protected int position = -1;
    protected int limit = -1;

    public static CodepointIterator forCharArray(char[] array) {
        return new CharArrayCodepointIterator(array);
    }

    public static CodepointIterator forCharSequence(CharSequence seq) {
        return new CharSequenceCodepointIterator(seq);
    }

    public static CodepointIterator forByteArray(byte[] array) {
        return new ByteArrayCodepointIterator(array);
    }

    public static CodepointIterator forByteArray(byte[] array, String charset) {
        return new ByteArrayCodepointIterator(array, charset);
    }

    public static CodepointIterator forCharBuffer(CharBuffer buffer) {
        return new CharBufferCodepointIterator(buffer);
    }

    public static CodepointIterator forReadableByteChannel(ReadableByteChannel channel) {
        return new ReadableByteChannelCodepointIterator(channel);
    }

    public static CodepointIterator forReadableByteChannel(ReadableByteChannel channel, String charset) {
        return new ReadableByteChannelCodepointIterator(channel, charset);
    }

    public static CodepointIterator forInputStream(InputStream in) {
        return new ReadableByteChannelCodepointIterator(Channels.newChannel(in));
    }

    public static CodepointIterator forInputStream(InputStream in, String charset) {
        return new ReadableByteChannelCodepointIterator(Channels.newChannel(in), charset);
    }

    public static CodepointIterator forReader(Reader in) {
        return new ReaderCodepointIterator(in);
    }

    public static CodepointIterator restrict(CodepointIterator ci, Filter filter) {
        return new RestrictedCodepointIterator(ci, filter, false);
    }

    public static CodepointIterator restrict(CodepointIterator ci, Filter filter, boolean scanning) {
        return new RestrictedCodepointIterator(ci, filter, scanning);
    }

    public static CodepointIterator restrict(CodepointIterator ci, Filter filter, boolean scanning, boolean invert) {
        return new RestrictedCodepointIterator(ci, filter, scanning, invert);
    }

    public CodepointIterator restrict(Filter filter) {
        return CodepointIterator.restrict(this, filter);
    }

    public CodepointIterator restrict(Filter filter, boolean scanning) {
        return CodepointIterator.restrict(this, filter, scanning);
    }

    public CodepointIterator restrict(Filter filter, boolean scanning, boolean invert) {
        return CodepointIterator.restrict(this, filter, scanning, invert);
    }

    protected abstract char get();

    protected abstract char get(int var1);

    @Override
    public boolean hasNext() {
        return this.remaining() > 0;
    }

    public int lastPosition() {
        int p = this.position();
        return p > -1 ? (p >= this.limit() ? p : p - 1) : -1;
    }

    public char[] nextChars() throws InvalidCharacterException {
        if (this.hasNext()) {
            if (this.isNextSurrogate()) {
                char c1 = this.get();
                if (CharUtils.isHighSurrogate(c1) && this.position() < this.limit()) {
                    char c2 = this.get();
                    if (CharUtils.isLowSurrogate(c2)) {
                        return new char[]{c1, c2};
                    }
                    throw new InvalidCharacterException(c2);
                }
                if (CharUtils.isLowSurrogate(c1) && this.position() > 0) {
                    char c2 = this.get(this.position() - 2);
                    if (CharUtils.isHighSurrogate(c2)) {
                        return new char[]{c1, c2};
                    }
                    throw new InvalidCharacterException(c2);
                }
            }
            return new char[]{this.get()};
        }
        return null;
    }

    public char[] peekChars() throws InvalidCharacterException {
        return this.peekChars(this.position());
    }

    private char[] peekChars(int pos) throws InvalidCharacterException {
        if (pos < 0 || pos >= this.limit()) {
            return null;
        }
        char c1 = this.get(pos);
        if (CharUtils.isHighSurrogate(c1) && pos < this.limit()) {
            char c2 = this.get(pos + 1);
            if (CharUtils.isLowSurrogate(c2)) {
                return new char[]{c1, c2};
            }
            throw new InvalidCharacterException(c2);
        }
        if (CharUtils.isLowSurrogate(c1) && pos > 1) {
            char c2 = this.get(pos - 1);
            if (CharUtils.isHighSurrogate(c2)) {
                return new char[]{c2, c1};
            }
            throw new InvalidCharacterException(c2);
        }
        return new char[]{c1};
    }

    @Override
    public Codepoint next() throws InvalidCharacterException {
        return this.toCodepoint(this.nextChars());
    }

    public Codepoint peek() throws InvalidCharacterException {
        return this.toCodepoint(this.peekChars());
    }

    public Codepoint peek(int index) throws InvalidCharacterException {
        return this.toCodepoint(this.peekChars(index));
    }

    private Codepoint toCodepoint(char[] chars) {
        return chars == null ? null : (chars.length == 1 ? new Codepoint(chars[0]) : CharUtils.toSupplementary(chars[0], chars[1]));
    }

    public void position(int n) {
        if (n < 0 || n > this.limit()) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        this.position = n;
    }

    public int position() {
        return this.position;
    }

    public int limit() {
        return this.limit;
    }

    public int remaining() {
        return this.limit - this.position();
    }

    private boolean isNextSurrogate() {
        if (!this.hasNext()) {
            return false;
        }
        char c = this.get(this.position());
        return CharUtils.isHighSurrogate(c) || CharUtils.isLowSurrogate(c);
    }

    public boolean isHigh(int index) {
        if (index < 0 || index > this.limit()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return CharUtils.isHighSurrogate(this.get(index));
    }

    public boolean isLow(int index) {
        if (index < 0 || index > this.limit()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return CharUtils.isLowSurrogate(this.get(index));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public static class RestrictedCodepointIterator
    extends DelegatingCodepointIterator {
        private final Filter filter;
        private final boolean scanningOnly;
        private final boolean notset;

        protected RestrictedCodepointIterator(CodepointIterator internal, Filter filter) {
            this(internal, filter, false);
        }

        protected RestrictedCodepointIterator(CodepointIterator internal, Filter filter, boolean scanningOnly) {
            this(internal, filter, scanningOnly, false);
        }

        protected RestrictedCodepointIterator(CodepointIterator internal, Filter filter, boolean scanningOnly, boolean notset) {
            super(internal);
            this.filter = filter;
            this.scanningOnly = scanningOnly;
            this.notset = notset;
        }

        public boolean hasNext() {
            boolean b = super.hasNext();
            if (this.scanningOnly) {
                try {
                    int cp = this.peek(this.position()).getValue();
                    if (b && cp != -1 && this.check(cp)) {
                        return false;
                    }
                }
                catch (InvalidCharacterException e) {
                    return false;
                }
            }
            return b;
        }

        public Codepoint next() throws InvalidCharacterException {
            Codepoint cp = super.next();
            int v = cp.getValue();
            if (v != -1 && this.check(v)) {
                if (this.scanningOnly) {
                    this.position(this.position() - 1);
                    return null;
                }
                throw new InvalidCharacterException(v);
            }
            return cp;
        }

        private boolean check(int cp) {
            boolean answer;
            boolean bl = answer = !this.filter.accept(cp);
            return !this.notset ? !answer : answer;
        }

        public char[] nextChars() throws InvalidCharacterException {
            char[] chars = super.nextChars();
            if (chars != null && chars.length > 0) {
                int cp;
                if (chars.length == 1 && this.check(chars[0])) {
                    if (this.scanningOnly) {
                        this.position(this.position() - 1);
                        return null;
                    }
                    throw new InvalidCharacterException(chars[0]);
                }
                if (chars.length == 2 && this.check(cp = CharUtils.toSupplementary(chars[0], chars[1]).getValue())) {
                    if (this.scanningOnly) {
                        this.position(this.position() - 2);
                        return null;
                    }
                    throw new InvalidCharacterException(cp);
                }
            }
            return chars;
        }
    }

    static class ReaderCodepointIterator
    extends CharArrayCodepointIterator {
        public ReaderCodepointIterator(Reader reader) {
            try {
                StringBuilder sb = new StringBuilder();
                char[] buf = new char[1024];
                int n = -1;
                while ((n = reader.read(buf)) > -1) {
                    sb.append(buf, 0, n);
                }
                this.buffer = new char[sb.length()];
                sb.getChars(0, sb.length(), this.buffer, 0);
                this.position = 0;
                this.limit = this.buffer.length;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class ReadableByteChannelCodepointIterator
    extends CharArrayCodepointIterator {
        public ReadableByteChannelCodepointIterator(ReadableByteChannel channel) {
            this(channel, Charset.defaultCharset());
        }

        public ReadableByteChannelCodepointIterator(ReadableByteChannel channel, String charset) {
            this(channel, Charset.forName(charset));
        }

        public ReadableByteChannelCodepointIterator(ReadableByteChannel channel, Charset charset) {
            try {
                ByteBuffer buf = ByteBuffer.allocate(1024);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                WritableByteChannel outc = Channels.newChannel(out);
                while (channel.read(buf) > 0) {
                    buf.flip();
                    outc.write(buf);
                }
                CharBuffer cb = charset.decode(ByteBuffer.wrap(out.toByteArray()));
                this.buffer = cb.array();
                this.position = cb.position();
                this.limit = cb.limit();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    static class CharSequenceCodepointIterator
    extends CodepointIterator {
        private CharSequence buffer;

        public CharSequenceCodepointIterator(CharSequence buffer) {
            this(buffer, 0, buffer.length());
        }

        public CharSequenceCodepointIterator(CharSequence buffer, int n, int e) {
            this.buffer = buffer;
            this.position = n;
            this.limit = Math.min(buffer.length() - n, e);
        }

        protected char get() {
            return this.buffer.charAt(this.position++);
        }

        protected char get(int index) {
            return this.buffer.charAt(index);
        }
    }

    static class CharBufferCodepointIterator
    extends CharArrayCodepointIterator {
        public CharBufferCodepointIterator(CharBuffer cb) {
            this.buffer = cb.array();
            this.position = cb.position();
            this.limit = cb.limit();
        }
    }

    static class CharArrayCodepointIterator
    extends CodepointIterator {
        protected char[] buffer;

        protected CharArrayCodepointIterator() {
        }

        public CharArrayCodepointIterator(char[] buffer) {
            this(buffer, 0, buffer.length);
        }

        public CharArrayCodepointIterator(char[] buffer, int n, int e) {
            this.buffer = buffer;
            this.position = n;
            this.limit = Math.min(buffer.length - n, e);
        }

        protected char get() {
            return this.position < this.limit ? this.buffer[this.position++] : (char)'\uffff';
        }

        protected char get(int index) {
            if (index < 0 || index >= this.limit) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return this.buffer[index];
        }
    }

    static class ByteArrayCodepointIterator
    extends CharArrayCodepointIterator {
        public ByteArrayCodepointIterator(byte[] bytes) {
            this(bytes, Charset.defaultCharset());
        }

        public ByteArrayCodepointIterator(byte[] bytes, String charset) {
            this(bytes, Charset.forName(charset));
        }

        public ByteArrayCodepointIterator(byte[] bytes, Charset charset) {
            CharBuffer cb = charset.decode(ByteBuffer.wrap(bytes));
            this.buffer = cb.array();
            this.position = cb.position();
            this.limit = cb.limit();
        }
    }
}

