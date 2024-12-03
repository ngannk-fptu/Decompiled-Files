/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.csv;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.unbescape.csv.CsvEscapeUtil;

public final class CsvEscape {
    public static String escapeCsv(String text) {
        return CsvEscapeUtil.escape(text);
    }

    public static void escapeCsv(String text, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        CsvEscapeUtil.escape(new InternalStringReader(text), writer);
    }

    public static void escapeCsv(Reader reader, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        CsvEscapeUtil.escape(reader, writer);
    }

    public static void escapeCsv(char[] text, int offset, int len, Writer writer) throws IOException {
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
        CsvEscapeUtil.escape(text, offset, len, writer);
    }

    public static String unescapeCsv(String text) {
        return CsvEscapeUtil.unescape(text);
    }

    public static void unescapeCsv(String text, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        CsvEscapeUtil.unescape(new InternalStringReader(text), writer);
    }

    public static void unescapeCsv(Reader reader, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        CsvEscapeUtil.unescape(reader, writer);
    }

    public static void unescapeCsv(char[] text, int offset, int len, Writer writer) throws IOException {
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
        CsvEscapeUtil.unescape(text, offset, len, writer);
    }

    private CsvEscape() {
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

