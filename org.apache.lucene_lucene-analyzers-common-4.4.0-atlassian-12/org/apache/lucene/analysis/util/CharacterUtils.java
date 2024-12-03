/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.util;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.util.Version;

public abstract class CharacterUtils {
    private static final Java4CharacterUtils JAVA_4 = new Java4CharacterUtils();
    private static final Java5CharacterUtils JAVA_5 = new Java5CharacterUtils();

    public static CharacterUtils getInstance(Version matchVersion) {
        return matchVersion.onOrAfter(Version.LUCENE_31) ? JAVA_5 : JAVA_4;
    }

    public static CharacterUtils getJava4Instance() {
        return JAVA_4;
    }

    public abstract int codePointAt(CharSequence var1, int var2);

    public abstract int codePointAt(char[] var1, int var2, int var3);

    public abstract int codePointCount(CharSequence var1);

    public static CharacterBuffer newCharacterBuffer(int bufferSize) {
        if (bufferSize < 2) {
            throw new IllegalArgumentException("buffersize must be >= 2");
        }
        return new CharacterBuffer(new char[bufferSize], 0, 0);
    }

    public final void toLowerCase(char[] buffer, int offset, int limit) {
        assert (buffer.length >= limit);
        assert (offset <= 0 && offset <= buffer.length);
        for (int i = offset; i < limit; i += Character.toChars(Character.toLowerCase(this.codePointAt(buffer, i, limit)), buffer, i)) {
        }
    }

    public final int toCodePoints(char[] src, int srcOff, int srcLen, int[] dest, int destOff) {
        int charCount;
        if (srcLen < 0) {
            throw new IllegalArgumentException("srcLen must be >= 0");
        }
        int codePointCount = 0;
        for (int i = 0; i < srcLen; i += charCount) {
            int cp = this.codePointAt(src, srcOff + i, srcOff + srcLen);
            charCount = Character.charCount(cp);
            dest[destOff + codePointCount++] = cp;
        }
        return codePointCount;
    }

    public final int toChars(int[] src, int srcOff, int srcLen, char[] dest, int destOff) {
        if (srcLen < 0) {
            throw new IllegalArgumentException("srcLen must be >= 0");
        }
        int written = 0;
        for (int i = 0; i < srcLen; ++i) {
            written += Character.toChars(src[srcOff + i], dest, destOff + written);
        }
        return written;
    }

    public abstract boolean fill(CharacterBuffer var1, Reader var2, int var3) throws IOException;

    public final boolean fill(CharacterBuffer buffer, Reader reader) throws IOException {
        return this.fill(buffer, reader, buffer.buffer.length);
    }

    public abstract int offsetByCodePoints(char[] var1, int var2, int var3, int var4, int var5);

    static int readFully(Reader reader, char[] dest, int offset, int len) throws IOException {
        int read;
        int r;
        for (read = 0; read < len && (r = reader.read(dest, offset + read, len - read)) != -1; read += r) {
        }
        return read;
    }

    public static final class CharacterBuffer {
        private final char[] buffer;
        private int offset;
        private int length;
        char lastTrailingHighSurrogate;

        CharacterBuffer(char[] buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
        }

        public char[] getBuffer() {
            return this.buffer;
        }

        public int getOffset() {
            return this.offset;
        }

        public int getLength() {
            return this.length;
        }

        public void reset() {
            this.offset = 0;
            this.length = 0;
            this.lastTrailingHighSurrogate = '\u0000';
        }
    }

    private static final class Java4CharacterUtils
    extends CharacterUtils {
        Java4CharacterUtils() {
        }

        @Override
        public int codePointAt(CharSequence seq, int offset) {
            return seq.charAt(offset);
        }

        @Override
        public int codePointAt(char[] chars, int offset, int limit) {
            if (offset >= limit) {
                throw new IndexOutOfBoundsException("offset must be less than limit");
            }
            return chars[offset];
        }

        @Override
        public boolean fill(CharacterBuffer buffer, Reader reader, int numChars) throws IOException {
            assert (buffer.buffer.length >= 1);
            if (numChars < 1 || numChars > buffer.buffer.length) {
                throw new IllegalArgumentException("numChars must be >= 1 and <= the buffer size");
            }
            buffer.offset = 0;
            int read = Java4CharacterUtils.readFully(reader, buffer.buffer, 0, numChars);
            buffer.length = read;
            buffer.lastTrailingHighSurrogate = '\u0000';
            return read == numChars;
        }

        @Override
        public int codePointCount(CharSequence seq) {
            return seq.length();
        }

        @Override
        public int offsetByCodePoints(char[] buf, int start, int count, int index, int offset) {
            int result = index + offset;
            if (result < 0 || result > count) {
                throw new IndexOutOfBoundsException();
            }
            return result;
        }
    }

    private static final class Java5CharacterUtils
    extends CharacterUtils {
        Java5CharacterUtils() {
        }

        @Override
        public int codePointAt(CharSequence seq, int offset) {
            return Character.codePointAt(seq, offset);
        }

        @Override
        public int codePointAt(char[] chars, int offset, int limit) {
            return Character.codePointAt(chars, offset, limit);
        }

        @Override
        public boolean fill(CharacterBuffer buffer, Reader reader, int numChars) throws IOException {
            boolean result;
            int offset;
            assert (buffer.buffer.length >= 2);
            if (numChars < 2 || numChars > buffer.buffer.length) {
                throw new IllegalArgumentException("numChars must be >= 2 and <= the buffer size");
            }
            char[] charBuffer = buffer.buffer;
            buffer.offset = 0;
            if (buffer.lastTrailingHighSurrogate != '\u0000') {
                charBuffer[0] = buffer.lastTrailingHighSurrogate;
                buffer.lastTrailingHighSurrogate = '\u0000';
                offset = 1;
            } else {
                offset = 0;
            }
            int read = Java5CharacterUtils.readFully(reader, charBuffer, offset, numChars - offset);
            buffer.length = offset + read;
            boolean bl = result = buffer.length == numChars;
            if (buffer.length < numChars) {
                return result;
            }
            if (Character.isHighSurrogate(charBuffer[buffer.length - 1])) {
                buffer.lastTrailingHighSurrogate = charBuffer[--buffer.length];
            }
            return result;
        }

        @Override
        public int codePointCount(CharSequence seq) {
            return Character.codePointCount(seq, 0, seq.length());
        }

        @Override
        public int offsetByCodePoints(char[] buf, int start, int count, int index, int offset) {
            return Character.offsetByCodePoints(buf, start, count, index, offset);
        }
    }
}

