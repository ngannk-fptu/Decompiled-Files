/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.TypeUtil
 */
package org.eclipse.jetty.http;

import org.eclipse.jetty.util.TypeUtil;

public class HttpTokens {
    static final byte COLON = 58;
    static final byte TAB = 9;
    static final byte LINE_FEED = 10;
    static final byte CARRIAGE_RETURN = 13;
    static final byte SPACE = 32;
    static final byte[] CRLF = new byte[]{13, 10};
    public static final Token[] TOKENS = new Token[256];

    public static Token getToken(byte b) {
        return TOKENS[0xFF & b];
    }

    public static Token getToken(char c) {
        return c <= '\u00ff' ? TOKENS[c] : null;
    }

    public static char sanitizeFieldVchar(char c) {
        switch (c) {
            case '\u0000': 
            case '\n': 
            case '\r': {
                return ' ';
            }
        }
        if (HttpTokens.isIllegalFieldVchar(c)) {
            return '?';
        }
        return c;
    }

    public static boolean isIllegalFieldVchar(char c) {
        return c >= '\u0100' || c < ' ';
    }

    static {
        block8: for (int b = 0; b < 256; ++b) {
            switch (b) {
                case 10: {
                    HttpTokens.TOKENS[b] = new Token((byte)b, Type.LF);
                    continue block8;
                }
                case 13: {
                    HttpTokens.TOKENS[b] = new Token((byte)b, Type.CR);
                    continue block8;
                }
                case 32: {
                    HttpTokens.TOKENS[b] = new Token((byte)b, Type.SPACE);
                    continue block8;
                }
                case 9: {
                    HttpTokens.TOKENS[b] = new Token((byte)b, Type.HTAB);
                    continue block8;
                }
                case 58: {
                    HttpTokens.TOKENS[b] = new Token((byte)b, Type.COLON);
                    continue block8;
                }
                case 33: 
                case 35: 
                case 36: 
                case 37: 
                case 38: 
                case 39: 
                case 42: 
                case 43: 
                case 45: 
                case 46: 
                case 94: 
                case 95: 
                case 96: 
                case 124: 
                case 126: {
                    HttpTokens.TOKENS[b] = new Token((byte)b, Type.TCHAR);
                    continue block8;
                }
                default: {
                    HttpTokens.TOKENS[b] = b >= 48 && b <= 57 ? new Token((byte)b, Type.DIGIT) : (b >= 65 && b <= 90 ? new Token((byte)b, Type.ALPHA) : (b >= 97 && b <= 122 ? new Token((byte)b, Type.ALPHA) : (b >= 33 && b <= 126 ? new Token((byte)b, Type.VCHAR) : (b >= 128 ? new Token((byte)b, Type.OTEXT) : new Token((byte)b, Type.CNTL)))));
                }
            }
        }
    }

    public static class Token {
        private final Type _type;
        private final byte _b;
        private final char _c;
        private final int _x;
        private final boolean _rfc2616Token;
        private final boolean _rfc6265CookieOctet;

        private Token(byte b, Type type) {
            this._type = type;
            this._b = b;
            this._c = (char)(0xFF & b);
            char lc = this._c >= 'A' & this._c <= 'Z' ? (char)(this._c - 65 + 97) : this._c;
            this._x = this._type == Type.DIGIT || this._type == Type.ALPHA && lc >= 'a' && lc <= 'f' ? TypeUtil.convertHexDigit((byte)b) : -1;
            this._rfc2616Token = b >= 32 && b < 127 && b != 40 && b != 41 && b != 60 && b != 62 && b != 64 && b != 44 && b != 59 && b != 58 && b != 92 && b != 34 && b != 47 && b != 91 && b != 93 && b != 63 && b != 61 && b != 123 && b != 125 && b != 32;
            this._rfc6265CookieOctet = b == 33 || b >= 35 && b <= 43 || b >= 45 && b <= 58 || b >= 60 && b <= 91 || b >= 93 && b <= 126;
        }

        public Type getType() {
            return this._type;
        }

        public byte getByte() {
            return this._b;
        }

        public char getChar() {
            return this._c;
        }

        public boolean isHexDigit() {
            return this._x >= 0;
        }

        public boolean isRfc2616Token() {
            return this._rfc2616Token;
        }

        public boolean isRfc6265CookieOctet() {
            return this._rfc6265CookieOctet;
        }

        public int getHexDigit() {
            return this._x;
        }

        public String toString() {
            switch (this._type) {
                case SPACE: 
                case COLON: 
                case ALPHA: 
                case DIGIT: 
                case TCHAR: 
                case VCHAR: {
                    return this._type + "='" + this._c + "'";
                }
                case CR: {
                    return "CR=\\r";
                }
                case LF: {
                    return "LF=\\n";
                }
            }
            return String.format("%s=0x%x", new Object[]{this._type, this._b});
        }
    }

    public static enum Type {
        CNTL,
        HTAB,
        LF,
        CR,
        SPACE,
        COLON,
        DIGIT,
        ALPHA,
        TCHAR,
        VCHAR,
        OTEXT;

    }

    public static enum EndOfContent {
        UNKNOWN_CONTENT,
        NO_CONTENT,
        EOF_CONTENT,
        CONTENT_LENGTH,
        CHUNKED_CONTENT;

    }
}

