/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

import com.sun.xml.messaging.saaj.packaging.mime.internet.ParseException;

public class HeaderTokenizer {
    private String string;
    private boolean skipComments;
    private String delimiters;
    private int currentPos;
    private int maxPos;
    private int nextPos;
    private int peekPos;
    public static final String RFC822 = "()<>@,;:\\\"\t .[]";
    public static final String MIME = "()<>@,;:\\\"\t []/?=";
    private static final Token EOFToken = new Token(-4, null);

    public HeaderTokenizer(String header, String delimiters, boolean skipComments) {
        this.string = header == null ? "" : header;
        this.skipComments = skipComments;
        this.delimiters = delimiters;
        this.peekPos = 0;
        this.nextPos = 0;
        this.currentPos = 0;
        this.maxPos = this.string.length();
    }

    public HeaderTokenizer(String header, String delimiters) {
        this(header, delimiters, true);
    }

    public HeaderTokenizer(String header) {
        this(header, RFC822);
    }

    public Token next() throws ParseException {
        this.currentPos = this.nextPos;
        Token tk = this.getNext();
        this.nextPos = this.peekPos = this.currentPos;
        return tk;
    }

    public Token peek() throws ParseException {
        this.currentPos = this.peekPos;
        Token tk = this.getNext();
        this.peekPos = this.currentPos;
        return tk;
    }

    public String getRemainder() {
        return this.string.substring(this.nextPos);
    }

    private Token getNext() throws ParseException {
        int start;
        if (this.currentPos >= this.maxPos) {
            return EOFToken;
        }
        if (this.skipWhiteSpace() == -4) {
            return EOFToken;
        }
        boolean filter = false;
        char c = this.string.charAt(this.currentPos);
        while (c == '(') {
            start = ++this.currentPos;
            int nesting = 1;
            while (nesting > 0 && this.currentPos < this.maxPos) {
                c = this.string.charAt(this.currentPos);
                if (c == '\\') {
                    ++this.currentPos;
                    filter = true;
                } else if (c == '\r') {
                    filter = true;
                } else if (c == '(') {
                    ++nesting;
                } else if (c == ')') {
                    --nesting;
                }
                ++this.currentPos;
            }
            if (nesting != 0) {
                throw new ParseException("Unbalanced comments");
            }
            if (!this.skipComments) {
                String s = filter ? HeaderTokenizer.filterToken(this.string, start, this.currentPos - 1) : this.string.substring(start, this.currentPos - 1);
                return new Token(-3, s);
            }
            if (this.skipWhiteSpace() == -4) {
                return EOFToken;
            }
            c = this.string.charAt(this.currentPos);
        }
        if (c == '\"') {
            start = ++this.currentPos;
            while (this.currentPos < this.maxPos) {
                c = this.string.charAt(this.currentPos);
                if (c == '\\') {
                    ++this.currentPos;
                    filter = true;
                } else if (c == '\r') {
                    filter = true;
                } else if (c == '\"') {
                    ++this.currentPos;
                    String s = filter ? HeaderTokenizer.filterToken(this.string, start, this.currentPos - 1) : this.string.substring(start, this.currentPos - 1);
                    return new Token(-2, s);
                }
                ++this.currentPos;
            }
            throw new ParseException("Unbalanced quoted string");
        }
        if (c < ' ' || c >= '\u007f' || this.delimiters.indexOf(c) >= 0) {
            ++this.currentPos;
            char[] ch = new char[]{c};
            return new Token(c, new String(ch));
        }
        start = this.currentPos;
        while (this.currentPos < this.maxPos && (c = this.string.charAt(this.currentPos)) >= ' ' && c < '\u007f' && c != '(' && c != ' ' && c != '\"' && this.delimiters.indexOf(c) < 0) {
            ++this.currentPos;
        }
        return new Token(-1, this.string.substring(start, this.currentPos));
    }

    private int skipWhiteSpace() {
        while (this.currentPos < this.maxPos) {
            char c = this.string.charAt(this.currentPos);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return this.currentPos;
            }
            ++this.currentPos;
        }
        return -4;
    }

    private static String filterToken(String s, int start, int end) {
        StringBuilder sb = new StringBuilder();
        boolean gotEscape = false;
        boolean gotCR = false;
        for (int i = start; i < end; ++i) {
            char c = s.charAt(i);
            if (c == '\n' && gotCR) {
                gotCR = false;
                continue;
            }
            gotCR = false;
            if (!gotEscape) {
                if (c == '\\') {
                    gotEscape = true;
                    continue;
                }
                if (c == '\r') {
                    gotCR = true;
                    continue;
                }
                sb.append(c);
                continue;
            }
            sb.append(c);
            gotEscape = false;
        }
        return sb.toString();
    }

    public static class Token {
        private int type;
        private String value;
        public static final int ATOM = -1;
        public static final int QUOTEDSTRING = -2;
        public static final int COMMENT = -3;
        public static final int EOF = -4;

        public Token(int type, String value) {
            this.type = type;
            this.value = value;
        }

        public int getType() {
            return this.type;
        }

        public String getValue() {
            return this.value;
        }
    }
}

