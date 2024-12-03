/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.properties;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.unbescape.properties.PropertiesKeyEscapeLevel;
import org.unbescape.properties.PropertiesKeyEscapeUtil;
import org.unbescape.properties.PropertiesUnescapeUtil;
import org.unbescape.properties.PropertiesValueEscapeLevel;
import org.unbescape.properties.PropertiesValueEscapeUtil;

public final class PropertiesEscape {
    public static String escapePropertiesValueMinimal(String text) {
        return PropertiesEscape.escapePropertiesValue(text, PropertiesValueEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static String escapePropertiesValue(String text) {
        return PropertiesEscape.escapePropertiesValue(text, PropertiesValueEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static String escapePropertiesValue(String text, PropertiesValueEscapeLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return PropertiesValueEscapeUtil.escape(text, level);
    }

    public static void escapePropertiesValueMinimal(String text, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesValue(text, writer, PropertiesValueEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(String text, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesValue(text, writer, PropertiesValueEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(String text, Writer writer, PropertiesValueEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        PropertiesValueEscapeUtil.escape(new InternalStringReader(text), writer, level);
    }

    public static void escapePropertiesValueMinimal(Reader reader, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesValue(reader, writer, PropertiesValueEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(Reader reader, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesValue(reader, writer, PropertiesValueEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(Reader reader, Writer writer, PropertiesValueEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        PropertiesValueEscapeUtil.escape(reader, writer, level);
    }

    public static void escapePropertiesValueMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesValue(text, offset, len, writer, PropertiesValueEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(char[] text, int offset, int len, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesValue(text, offset, len, writer, PropertiesValueEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(char[] text, int offset, int len, Writer writer, PropertiesValueEscapeLevel level) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        PropertiesValueEscapeUtil.escape(text, offset, len, writer, level);
    }

    public static String escapePropertiesKeyMinimal(String text) {
        return PropertiesEscape.escapePropertiesKey(text, PropertiesKeyEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static String escapePropertiesKey(String text) {
        return PropertiesEscape.escapePropertiesKey(text, PropertiesKeyEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static String escapePropertiesKey(String text, PropertiesKeyEscapeLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return PropertiesKeyEscapeUtil.escape(text, level);
    }

    public static void escapePropertiesKeyMinimal(String text, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesKey(text, writer, PropertiesKeyEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(String text, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesKey(text, writer, PropertiesKeyEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(String text, Writer writer, PropertiesKeyEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        PropertiesKeyEscapeUtil.escape(new InternalStringReader(text), writer, level);
    }

    public static void escapePropertiesKeyMinimal(Reader reader, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesKey(reader, writer, PropertiesKeyEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(Reader reader, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesKey(reader, writer, PropertiesKeyEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(Reader reader, Writer writer, PropertiesKeyEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        PropertiesKeyEscapeUtil.escape(reader, writer, level);
    }

    public static void escapePropertiesKeyMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesKey(text, offset, len, writer, PropertiesKeyEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(char[] text, int offset, int len, Writer writer) throws IOException {
        PropertiesEscape.escapePropertiesKey(text, offset, len, writer, PropertiesKeyEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(char[] text, int offset, int len, Writer writer, PropertiesKeyEscapeLevel level) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        PropertiesKeyEscapeUtil.escape(text, offset, len, writer, level);
    }

    public static String unescapeProperties(String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf(92) < 0) {
            return text;
        }
        return PropertiesUnescapeUtil.unescape(text);
    }

    public static void unescapeProperties(String text, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (text == null) {
            return;
        }
        if (text.indexOf(92) < 0) {
            writer.write(text);
            return;
        }
        PropertiesUnescapeUtil.unescape(new InternalStringReader(text), writer);
    }

    public static void unescapeProperties(Reader reader, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        PropertiesUnescapeUtil.unescape(reader, writer);
    }

    public static void unescapeProperties(char[] text, int offset, int len, Writer writer) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        PropertiesUnescapeUtil.unescape(text, offset, len, writer);
    }

    private PropertiesEscape() {
    }

    private static final class InternalStringReader
    extends Reader {
        private String str;
        private int length;
        private int next = 0;

        public InternalStringReader(String s) {
            this.str = s;
            this.length = s.length();
        }

        @Override
        public int read() throws IOException {
            if (this.next >= this.length) {
                return -1;
            }
            return this.str.charAt(this.next++);
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (this.next >= this.length) {
                return -1;
            }
            int n = Math.min(this.length - this.next, len);
            this.str.getChars(this.next, this.next + n, cbuf, off);
            this.next += n;
            return n;
        }

        @Override
        public void close() throws IOException {
            this.str = null;
        }
    }
}

