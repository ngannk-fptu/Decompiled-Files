/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape.uri;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.unbescape.uri.UriEscapeUtil;

public final class UriEscape {
    public static final String DEFAULT_ENCODING = "UTF-8";

    public static String escapeUriPath(String text) {
        return UriEscape.escapeUriPath(text, DEFAULT_ENCODING);
    }

    public static String escapeUriPath(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static String escapeUriPathSegment(String text) {
        return UriEscape.escapeUriPathSegment(text, DEFAULT_ENCODING);
    }

    public static String escapeUriPathSegment(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static String escapeUriQueryParam(String text) {
        return UriEscape.escapeUriQueryParam(text, DEFAULT_ENCODING);
    }

    public static String escapeUriQueryParam(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static String escapeUriFragmentId(String text) {
        return UriEscape.escapeUriFragmentId(text, DEFAULT_ENCODING);
    }

    public static String escapeUriFragmentId(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void escapeUriPath(String text, Writer writer) throws IOException {
        UriEscape.escapeUriPath(text, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPath(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void escapeUriPathSegment(String text, Writer writer) throws IOException {
        UriEscape.escapeUriPathSegment(text, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPathSegment(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void escapeUriQueryParam(String text, Writer writer) throws IOException {
        UriEscape.escapeUriQueryParam(text, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriQueryParam(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void escapeUriFragmentId(String text, Writer writer) throws IOException {
        UriEscape.escapeUriFragmentId(text, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriFragmentId(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void escapeUriPath(Reader reader, Writer writer) throws IOException {
        UriEscape.escapeUriPath(reader, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPath(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(reader, writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void escapeUriPathSegment(Reader reader, Writer writer) throws IOException {
        UriEscape.escapeUriPathSegment(reader, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPathSegment(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(reader, writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void escapeUriQueryParam(Reader reader, Writer writer) throws IOException {
        UriEscape.escapeUriQueryParam(reader, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriQueryParam(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(reader, writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void escapeUriFragmentId(Reader reader, Writer writer) throws IOException {
        UriEscape.escapeUriFragmentId(reader, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriFragmentId(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(reader, writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void escapeUriPath(char[] text, int offset, int len, Writer writer) throws IOException {
        UriEscape.escapeUriPath(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPath(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void escapeUriPathSegment(char[] text, int offset, int len, Writer writer) throws IOException {
        UriEscape.escapeUriPathSegment(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPathSegment(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void escapeUriQueryParam(char[] text, int offset, int len, Writer writer) throws IOException {
        UriEscape.escapeUriQueryParam(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriQueryParam(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void escapeUriFragmentId(char[] text, int offset, int len, Writer writer) throws IOException {
        UriEscape.escapeUriFragmentId(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriFragmentId(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static String unescapeUriPath(String text) {
        return UriEscape.unescapeUriPath(text, DEFAULT_ENCODING);
    }

    public static String unescapeUriPath(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static String unescapeUriPathSegment(String text) {
        return UriEscape.unescapeUriPathSegment(text, DEFAULT_ENCODING);
    }

    public static String unescapeUriPathSegment(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static String unescapeUriQueryParam(String text) {
        return UriEscape.unescapeUriQueryParam(text, DEFAULT_ENCODING);
    }

    public static String unescapeUriQueryParam(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static String unescapeUriFragmentId(String text) {
        return UriEscape.unescapeUriFragmentId(text, DEFAULT_ENCODING);
    }

    public static String unescapeUriFragmentId(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void unescapeUriPath(String text, Writer writer) throws IOException {
        UriEscape.unescapeUriPath(text, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPath(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void unescapeUriPathSegment(String text, Writer writer) throws IOException {
        UriEscape.unescapeUriPathSegment(text, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPathSegment(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void unescapeUriQueryParam(String text, Writer writer) throws IOException {
        UriEscape.unescapeUriQueryParam(text, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriQueryParam(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void unescapeUriFragmentId(String text, Writer writer) throws IOException {
        UriEscape.unescapeUriFragmentId(text, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriFragmentId(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void unescapeUriPath(Reader reader, Writer writer) throws IOException {
        UriEscape.unescapeUriPath(reader, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPath(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(reader, writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void unescapeUriPathSegment(Reader reader, Writer writer) throws IOException {
        UriEscape.unescapeUriPathSegment(reader, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPathSegment(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(reader, writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void unescapeUriQueryParam(Reader reader, Writer writer) throws IOException {
        UriEscape.unescapeUriQueryParam(reader, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriQueryParam(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(reader, writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void unescapeUriFragmentId(Reader reader, Writer writer) throws IOException {
        UriEscape.unescapeUriFragmentId(reader, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriFragmentId(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(reader, writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void unescapeUriPath(char[] text, int offset, int len, Writer writer) throws IOException {
        UriEscape.unescapeUriPath(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPath(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void unescapeUriPathSegment(char[] text, int offset, int len, Writer writer) throws IOException {
        UriEscape.unescapeUriPathSegment(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPathSegment(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void unescapeUriQueryParam(char[] text, int offset, int len, Writer writer) throws IOException {
        UriEscape.unescapeUriQueryParam(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriQueryParam(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void unescapeUriFragmentId(char[] text, int offset, int len, Writer writer) throws IOException {
        UriEscape.unescapeUriFragmentId(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriFragmentId(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        int textLen;
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int n = textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    private UriEscape() {
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

