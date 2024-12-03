/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.javascript;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.unbescape.javascript.JavaScriptEscapeLevel;
import org.unbescape.javascript.JavaScriptEscapeType;
import org.unbescape.javascript.JavaScriptEscapeUtil;

public final class JavaScriptEscape {
    public static String escapeJavaScriptMinimal(String text) {
        return JavaScriptEscape.escapeJavaScript(text, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static String escapeJavaScript(String text) {
        return JavaScriptEscape.escapeJavaScript(text, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static String escapeJavaScript(String text, JavaScriptEscapeType type, JavaScriptEscapeLevel level) {
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return JavaScriptEscapeUtil.escape(text, type, level);
    }

    public static void escapeJavaScriptMinimal(String text, Writer writer) throws IOException {
        JavaScriptEscape.escapeJavaScript(text, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(String text, Writer writer) throws IOException {
        JavaScriptEscape.escapeJavaScript(text, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(String text, Writer writer, JavaScriptEscapeType type, JavaScriptEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        JavaScriptEscapeUtil.escape(new InternalStringReader(text), writer, type, level);
    }

    public static void escapeJavaScriptMinimal(Reader reader, Writer writer) throws IOException {
        JavaScriptEscape.escapeJavaScript(reader, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(Reader reader, Writer writer) throws IOException {
        JavaScriptEscape.escapeJavaScript(reader, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(Reader reader, Writer writer, JavaScriptEscapeType type, JavaScriptEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        JavaScriptEscapeUtil.escape(reader, writer, type, level);
    }

    public static void escapeJavaScriptMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        JavaScriptEscape.escapeJavaScript(text, offset, len, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(char[] text, int offset, int len, Writer writer) throws IOException {
        JavaScriptEscape.escapeJavaScript(text, offset, len, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(char[] text, int offset, int len, Writer writer, JavaScriptEscapeType type, JavaScriptEscapeLevel level) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
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
        JavaScriptEscapeUtil.escape(text, offset, len, writer, type, level);
    }

    public static String unescapeJavaScript(String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf(92) < 0) {
            return text;
        }
        return JavaScriptEscapeUtil.unescape(text);
    }

    public static void unescapeJavaScript(String text, Writer writer) throws IOException {
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
        JavaScriptEscapeUtil.unescape(new InternalStringReader(text), writer);
    }

    public static void unescapeJavaScript(Reader reader, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        JavaScriptEscapeUtil.unescape(reader, writer);
    }

    public static void unescapeJavaScript(char[] text, int offset, int len, Writer writer) throws IOException {
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
        JavaScriptEscapeUtil.unescape(text, offset, len, writer);
    }

    private JavaScriptEscape() {
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

