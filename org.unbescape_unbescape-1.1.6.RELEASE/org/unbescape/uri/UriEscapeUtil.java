/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.uri;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

final class UriEscapeUtil {
    private static final char ESCAPE_PREFIX = '%';
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();

    private UriEscapeUtil() {
    }

    static char[] printHexa(byte b) {
        char[] result = new char[]{HEXA_CHARS_UPPER[b >> 4 & 0xF], HEXA_CHARS_UPPER[b & 0xF]};
        return result;
    }

    static byte parseHexa(char c1, char c2) {
        int j;
        byte result = 0;
        for (j = 0; j < HEXA_CHARS_UPPER.length; ++j) {
            if (c1 != HEXA_CHARS_UPPER[j] && c1 != HEXA_CHARS_LOWER[j]) continue;
            result = (byte)(result + (j << 4));
            break;
        }
        for (j = 0; j < HEXA_CHARS_UPPER.length; ++j) {
            if (c2 != HEXA_CHARS_UPPER[j] && c2 != HEXA_CHARS_LOWER[j]) continue;
            result = (byte)(result + j);
            break;
        }
        return result;
    }

    static String escape(String text, UriEscapeType escapeType, String encoding) {
        if (text == null) {
            return null;
        }
        StringBuilder strBuilder = null;
        boolean offset = false;
        int max = text.length();
        int readOffset = 0;
        for (int i = 0; i < max; ++i) {
            byte[] charAsBytes;
            int codepoint = Character.codePointAt(text, i);
            if (UriEscapeType.isAlpha(codepoint) || escapeType.isAllowed(codepoint)) continue;
            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 20);
            }
            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }
            if (Character.charCount(codepoint) > 1) {
                ++i;
            }
            readOffset = i + 1;
            try {
                charAsBytes = new String(Character.toChars(codepoint)).getBytes(encoding);
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
            }
            for (byte b : charAsBytes) {
                strBuilder.append('%');
                strBuilder.append(UriEscapeUtil.printHexa(b));
            }
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }
        return strBuilder.toString();
    }

    static void escape(Reader reader, Writer writer, UriEscapeType escapeType, String encoding) throws IOException {
        if (reader == null) {
            return;
        }
        int c2 = reader.read();
        while (c2 >= 0) {
            byte[] charAsBytes;
            int c1 = c2;
            int codepoint = UriEscapeUtil.codePointAt((char)c1, (char)(c2 = reader.read()));
            if (UriEscapeType.isAlpha(codepoint)) {
                writer.write(c1);
                continue;
            }
            if (escapeType.isAllowed(codepoint)) {
                writer.write(c1);
                continue;
            }
            if (Character.charCount(codepoint) > 1) {
                c1 = c2;
                c2 = reader.read();
            }
            try {
                charAsBytes = new String(Character.toChars(codepoint)).getBytes(encoding);
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
            }
            for (byte b : charAsBytes) {
                writer.write(37);
                writer.write(UriEscapeUtil.printHexa(b));
            }
        }
    }

    static void escape(char[] text, int offset, int len, Writer writer, UriEscapeType escapeType, String encoding) throws IOException {
        if (text == null || text.length == 0) {
            return;
        }
        int max = offset + len;
        int readOffset = offset;
        for (int i = offset; i < max; ++i) {
            byte[] charAsBytes;
            int codepoint = Character.codePointAt(text, i);
            if (UriEscapeType.isAlpha(codepoint) || escapeType.isAllowed(codepoint)) continue;
            if (i - readOffset > 0) {
                writer.write(text, readOffset, i - readOffset);
            }
            if (Character.charCount(codepoint) > 1) {
                ++i;
            }
            readOffset = i + 1;
            try {
                charAsBytes = new String(Character.toChars(codepoint)).getBytes(encoding);
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
            }
            for (byte b : charAsBytes) {
                writer.write(37);
                writer.write(UriEscapeUtil.printHexa(b));
            }
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
    }

    static String unescape(String text, UriEscapeType escapeType, String encoding) {
        if (text == null) {
            return null;
        }
        StringBuilder strBuilder = null;
        boolean offset = false;
        int max = text.length();
        int readOffset = 0;
        for (int i = 0; i < max; ++i) {
            char c = text.charAt(i);
            if (c != '%' && (c != '+' || !escapeType.canPlusEscapeWhitespace())) continue;
            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 5);
            }
            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }
            if (c == '+') {
                strBuilder.append(' ');
                readOffset = i + 1;
                continue;
            }
            byte[] bytes = new byte[(max - i) / 3];
            char aheadC = c;
            int pos = 0;
            while (i + 2 < max && aheadC == '%') {
                bytes[pos++] = UriEscapeUtil.parseHexa(text.charAt(i + 1), text.charAt(i + 2));
                if ((i += 3) >= max) continue;
                aheadC = text.charAt(i);
            }
            if (i < max && aheadC == '%') {
                throw new IllegalArgumentException("Incomplete escaping sequence in input");
            }
            try {
                strBuilder.append(new String(bytes, 0, pos, encoding));
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
            }
            readOffset = i;
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }
        return strBuilder.toString();
    }

    static void unescape(Reader reader, Writer writer, UriEscapeType escapeType, String encoding) throws IOException {
        if (reader == null) {
            return;
        }
        byte[] escapes = new byte[4];
        int c2 = reader.read();
        while (c2 >= 0) {
            int c1 = c2;
            c2 = reader.read();
            if (!(c1 == 37 && c2 >= 0 || c1 == 43 && escapeType.canPlusEscapeWhitespace())) {
                writer.write(c1);
                continue;
            }
            if (c1 == 43) {
                writer.write(32);
                continue;
            }
            int pos = 0;
            int ce0 = c1;
            int ce1 = c2;
            int ce2 = reader.read();
            while (ce0 == 37 && ce1 >= 0 && ce2 >= 0) {
                if (pos == escapes.length) {
                    byte[] newEscapes = new byte[escapes.length + 4];
                    System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                    escapes = newEscapes;
                }
                escapes[pos++] = UriEscapeUtil.parseHexa((char)ce1, (char)ce2);
                ce0 = reader.read();
                int n = ce0 < 0 ? ce0 : (ce1 = ce0 != 37 ? 0 : reader.read());
                ce2 = ce1 < 0 ? ce1 : (ce0 != 37 ? 0 : reader.read());
            }
            if (ce0 == 37) {
                throw new IllegalArgumentException("Incomplete escaping sequence in input");
            }
            c2 = ce0;
            try {
                writer.write(new String(escapes, 0, pos, encoding));
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
            }
        }
    }

    static void unescape(char[] text, int offset, int len, Writer writer, UriEscapeType escapeType, String encoding) throws IOException {
        if (text == null) {
            return;
        }
        int max = offset + len;
        int readOffset = offset;
        for (int i = offset; i < max; ++i) {
            char c = text[i];
            if (c != '%' && (c != '+' || !escapeType.canPlusEscapeWhitespace())) continue;
            if (i - readOffset > 0) {
                writer.write(text, readOffset, i - readOffset);
            }
            if (c == '+') {
                writer.write(32);
                readOffset = i + 1;
                continue;
            }
            byte[] bytes = new byte[(max - i) / 3];
            char aheadC = c;
            int pos = 0;
            while (i + 2 < max && aheadC == '%') {
                bytes[pos++] = UriEscapeUtil.parseHexa(text[i + 1], text[i + 2]);
                if ((i += 3) >= max) continue;
                aheadC = text[i];
            }
            if (i < max && aheadC == '%') {
                throw new IllegalArgumentException("Incomplete escaping sequence in input");
            }
            try {
                writer.write(new String(bytes, 0, pos, encoding));
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
            }
            readOffset = i;
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
    }

    private static int codePointAt(char c1, char c2) {
        if (Character.isHighSurrogate(c1) && c2 >= '\u0000' && Character.isLowSurrogate(c2)) {
            return Character.toCodePoint(c1, c2);
        }
        return c1;
    }

    static enum UriEscapeType {
        PATH{

            @Override
            public boolean isAllowed(int c) {
                return UriEscapeType.isPchar(c) || 47 == c;
            }
        }
        ,
        PATH_SEGMENT{

            @Override
            public boolean isAllowed(int c) {
                return UriEscapeType.isPchar(c);
            }
        }
        ,
        QUERY_PARAM{

            @Override
            public boolean isAllowed(int c) {
                if (61 == c || 38 == c || 43 == c || 35 == c) {
                    return false;
                }
                return UriEscapeType.isPchar(c) || 47 == c || 63 == c;
            }

            @Override
            public boolean canPlusEscapeWhitespace() {
                return true;
            }
        }
        ,
        FRAGMENT_ID{

            @Override
            public boolean isAllowed(int c) {
                return UriEscapeType.isPchar(c) || 47 == c || 63 == c;
            }
        };


        public abstract boolean isAllowed(int var1);

        public boolean canPlusEscapeWhitespace() {
            return false;
        }

        private static boolean isPchar(int c) {
            return UriEscapeType.isUnreserved(c) || UriEscapeType.isSubDelim(c) || 58 == c || 64 == c;
        }

        private static boolean isUnreserved(int c) {
            return UriEscapeType.isAlpha(c) || UriEscapeType.isDigit(c) || 45 == c || 46 == c || 95 == c || 126 == c;
        }

        private static boolean isReserved(int c) {
            return UriEscapeType.isGenDelim(c) || UriEscapeType.isSubDelim(c);
        }

        private static boolean isSubDelim(int c) {
            return 33 == c || 36 == c || 38 == c || 39 == c || 40 == c || 41 == c || 42 == c || 43 == c || 44 == c || 59 == c || 61 == c;
        }

        private static boolean isGenDelim(int c) {
            return 58 == c || 47 == c || 63 == c || 35 == c || 91 == c || 93 == c || 64 == c;
        }

        static boolean isAlpha(int c) {
            return c >= 65 && c <= 90 || c >= 97 && c <= 122;
        }

        private static boolean isDigit(int c) {
            return c >= 48 && c <= 57;
        }
    }
}

