/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json;

import java.io.IOException;

class JStylerObj {
    public static final MPSimple MP_SIMPLE = new MPSimple();
    public static final MPTrue MP_TRUE = new MPTrue();
    public static final MPAgressive MP_AGGRESIVE = new MPAgressive();
    public static final EscapeLT ESCAPE_LT = new EscapeLT();
    public static final Escape4Web ESCAPE4Web = new Escape4Web();

    JStylerObj() {
    }

    public static boolean isSpace(char c) {
        return c == '\r' || c == '\n' || c == '\t' || c == ' ';
    }

    public static boolean isSpecialChar(char c) {
        return c == '\b' || c == '\f' || c == '\n';
    }

    public static boolean isSpecialOpen(char c) {
        return c == '{' || c == '[' || c == ',' || c == ':';
    }

    public static boolean isSpecialClose(char c) {
        return c == '}' || c == ']' || c == ',' || c == ':';
    }

    public static boolean isSpecial(char c) {
        return c == '{' || c == '[' || c == ',' || c == '}' || c == ']' || c == ':' || c == '\'' || c == '\"';
    }

    public static boolean isUnicode(char c) {
        return c >= '\u0000' && c <= '\u001f' || c >= '\u007f' && c <= '\u009f' || c >= '\u2000' && c <= '\u20ff';
    }

    public static boolean isKeyword(String s) {
        if (s.length() < 3) {
            return false;
        }
        char c = s.charAt(0);
        if (c == 'n') {
            return s.equals("null");
        }
        if (c == 't') {
            return s.equals("true");
        }
        if (c == 'f') {
            return s.equals("false");
        }
        if (c == 'N') {
            return s.equals("NaN");
        }
        return false;
    }

    private static class Escape4Web
    implements StringProtector {
        private Escape4Web() {
        }

        @Override
        public void escape(String s, Appendable sb) {
            try {
                int len = s.length();
                block12: for (int i = 0; i < len; ++i) {
                    char ch = s.charAt(i);
                    switch (ch) {
                        case '\"': {
                            sb.append("\\\"");
                            continue block12;
                        }
                        case '\\': {
                            sb.append("\\\\");
                            continue block12;
                        }
                        case '\b': {
                            sb.append("\\b");
                            continue block12;
                        }
                        case '\f': {
                            sb.append("\\f");
                            continue block12;
                        }
                        case '\n': {
                            sb.append("\\n");
                            continue block12;
                        }
                        case '\r': {
                            sb.append("\\r");
                            continue block12;
                        }
                        case '\t': {
                            sb.append("\\t");
                            continue block12;
                        }
                        case '/': {
                            sb.append("\\/");
                            continue block12;
                        }
                        default: {
                            if (ch >= '\u0000' && ch <= '\u001f' || ch >= '\u007f' && ch <= '\u009f' || ch >= '\u2000' && ch <= '\u20ff') {
                                sb.append("\\u");
                                String hex = "0123456789ABCDEF";
                                sb.append(hex.charAt(ch >> 12 & 0xF));
                                sb.append(hex.charAt(ch >> 8 & 0xF));
                                sb.append(hex.charAt(ch >> 4 & 0xF));
                                sb.append(hex.charAt(ch >> 0 & 0xF));
                                continue block12;
                            }
                            sb.append(ch);
                        }
                    }
                }
            }
            catch (IOException e) {
                throw new RuntimeException("Impossible Error");
            }
        }
    }

    private static class EscapeLT
    implements StringProtector {
        private EscapeLT() {
        }

        @Override
        public void escape(String s, Appendable out) {
            try {
                int len = s.length();
                block11: for (int i = 0; i < len; ++i) {
                    char ch = s.charAt(i);
                    switch (ch) {
                        case '\"': {
                            out.append("\\\"");
                            continue block11;
                        }
                        case '\\': {
                            out.append("\\\\");
                            continue block11;
                        }
                        case '\b': {
                            out.append("\\b");
                            continue block11;
                        }
                        case '\f': {
                            out.append("\\f");
                            continue block11;
                        }
                        case '\n': {
                            out.append("\\n");
                            continue block11;
                        }
                        case '\r': {
                            out.append("\\r");
                            continue block11;
                        }
                        case '\t': {
                            out.append("\\t");
                            continue block11;
                        }
                        default: {
                            if (ch >= '\u0000' && ch <= '\u001f' || ch >= '\u007f' && ch <= '\u009f' || ch >= '\u2000' && ch <= '\u20ff') {
                                out.append("\\u");
                                String hex = "0123456789ABCDEF";
                                out.append(hex.charAt(ch >> 12 & 0xF));
                                out.append(hex.charAt(ch >> 8 & 0xF));
                                out.append(hex.charAt(ch >> 4 & 0xF));
                                out.append(hex.charAt(ch >> 0 & 0xF));
                                continue block11;
                            }
                            out.append(ch);
                        }
                    }
                }
            }
            catch (IOException e) {
                throw new RuntimeException("Impossible Exception");
            }
        }
    }

    public static interface StringProtector {
        public void escape(String var1, Appendable var2);
    }

    private static class MPAgressive
    implements MustProtect {
        private MPAgressive() {
        }

        @Override
        public boolean mustBeProtect(String s) {
            if (s == null) {
                return false;
            }
            int len = s.length();
            if (len == 0) {
                return true;
            }
            if (s.trim() != s) {
                return true;
            }
            char ch = s.charAt(0);
            if (JStylerObj.isSpecial(ch) || JStylerObj.isUnicode(ch)) {
                return true;
            }
            for (int i = 1; i < len; ++i) {
                ch = s.charAt(i);
                if (!JStylerObj.isSpecialClose(ch) && !JStylerObj.isUnicode(ch)) continue;
                return true;
            }
            if (JStylerObj.isKeyword(s)) {
                return true;
            }
            ch = s.charAt(0);
            if (ch >= '0' && ch <= '9' || ch == '-') {
                int p;
                for (p = 1; p < len && (ch = s.charAt(p)) >= '0' && ch <= '9'; ++p) {
                }
                if (p == len) {
                    return true;
                }
                if (ch == '.') {
                    ++p;
                }
                while (p < len && (ch = s.charAt(p)) >= '0' && ch <= '9') {
                    ++p;
                }
                if (p == len) {
                    return true;
                }
                if (ch == 'E' || ch == 'e') {
                    if (++p == len) {
                        return false;
                    }
                    ch = s.charAt(p);
                    if (ch == '+' || ch == '-') {
                        ch = s.charAt(++p);
                    }
                }
                if (p == len) {
                    return false;
                }
                while (p < len && (ch = s.charAt(p)) >= '0' && ch <= '9') {
                    ++p;
                }
                return p == len;
            }
            return false;
        }
    }

    private static class MPSimple
    implements MustProtect {
        private MPSimple() {
        }

        @Override
        public boolean mustBeProtect(String s) {
            if (s == null) {
                return false;
            }
            int len = s.length();
            if (len == 0) {
                return true;
            }
            if (s.trim() != s) {
                return true;
            }
            char ch = s.charAt(0);
            if (ch >= '0' && ch <= '9' || ch == '-') {
                return true;
            }
            for (int i = 0; i < len; ++i) {
                ch = s.charAt(i);
                if (JStylerObj.isSpace(ch)) {
                    return true;
                }
                if (JStylerObj.isSpecial(ch)) {
                    return true;
                }
                if (JStylerObj.isSpecialChar(ch)) {
                    return true;
                }
                if (!JStylerObj.isUnicode(ch)) continue;
                return true;
            }
            return JStylerObj.isKeyword(s);
        }
    }

    private static class MPTrue
    implements MustProtect {
        private MPTrue() {
        }

        @Override
        public boolean mustBeProtect(String s) {
            return true;
        }
    }

    public static interface MustProtect {
        public boolean mustBeProtect(String var1);
    }
}

