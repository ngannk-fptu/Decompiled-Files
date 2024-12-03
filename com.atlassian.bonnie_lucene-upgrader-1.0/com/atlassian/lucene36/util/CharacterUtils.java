/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.Version;
import java.io.IOException;
import java.io.Reader;

public abstract class CharacterUtils {
    private static final Java4CharacterUtils JAVA_4 = new Java4CharacterUtils();
    private static final Java5CharacterUtils JAVA_5 = new Java5CharacterUtils();

    public static CharacterUtils getInstance(Version matchVersion) {
        return matchVersion.onOrAfter(Version.LUCENE_31) ? JAVA_5 : JAVA_4;
    }

    public abstract int codePointAt(char[] var1, int var2);

    public abstract int codePointAt(CharSequence var1, int var2);

    public abstract int codePointAt(char[] var1, int var2, int var3);

    public static CharacterBuffer newCharacterBuffer(int bufferSize) {
        if (bufferSize < 2) {
            throw new IllegalArgumentException("buffersize must be >= 2");
        }
        return new CharacterBuffer(new char[bufferSize], 0, 0);
    }

    public abstract boolean fill(CharacterBuffer var1, Reader var2) throws IOException;

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

        public int codePointAt(char[] chars, int offset) {
            return chars[offset];
        }

        public int codePointAt(CharSequence seq, int offset) {
            return seq.charAt(offset);
        }

        public int codePointAt(char[] chars, int offset, int limit) {
            if (offset >= limit) {
                throw new IndexOutOfBoundsException("offset must be less than limit");
            }
            return chars[offset];
        }

        public boolean fill(CharacterBuffer buffer, Reader reader) throws IOException {
            buffer.offset = 0;
            int read = reader.read(buffer.buffer);
            if (read == -1) {
                return false;
            }
            buffer.length = read;
            return true;
        }
    }

    private static final class Java5CharacterUtils
    extends CharacterUtils {
        Java5CharacterUtils() {
        }

        public int codePointAt(char[] chars, int offset) {
            return Character.codePointAt(chars, offset);
        }

        public int codePointAt(CharSequence seq, int offset) {
            return Character.codePointAt(seq, offset);
        }

        public int codePointAt(char[] chars, int offset, int limit) {
            return Character.codePointAt(chars, offset, limit);
        }

        public boolean fill(CharacterBuffer buffer, Reader reader) throws IOException {
            int offset;
            char[] charBuffer = buffer.buffer;
            buffer.offset = 0;
            if (buffer.lastTrailingHighSurrogate != '\u0000') {
                charBuffer[0] = buffer.lastTrailingHighSurrogate;
                offset = 1;
            } else {
                offset = 0;
            }
            int read = reader.read(charBuffer, offset, charBuffer.length - offset);
            if (read == -1) {
                buffer.length = offset;
                buffer.lastTrailingHighSurrogate = '\u0000';
                return offset != 0;
            }
            assert (read > 0);
            buffer.length = read + offset;
            if (buffer.length == 1 && Character.isHighSurrogate(charBuffer[buffer.length - 1])) {
                int read2 = reader.read(charBuffer, 1, charBuffer.length - 1);
                if (read2 == -1) {
                    return true;
                }
                assert (read2 > 0);
                buffer.length += read2;
            }
            buffer.lastTrailingHighSurrogate = buffer.length > 1 && Character.isHighSurrogate(charBuffer[buffer.length - 1]) ? charBuffer[--buffer.length] : (char)'\u0000';
            return true;
        }
    }
}

