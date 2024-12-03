/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.bind;

abstract class WhiteSpaceProcessor {
    WhiteSpaceProcessor() {
    }

    public static String replace(String text) {
        return WhiteSpaceProcessor.replace((CharSequence)text).toString();
    }

    public static CharSequence replace(CharSequence text) {
        int i;
        for (i = text.length() - 1; i >= 0 && !WhiteSpaceProcessor.isWhiteSpaceExceptSpace(text.charAt(i)); --i) {
        }
        if (i < 0) {
            return text;
        }
        StringBuilder buf = new StringBuilder(text);
        buf.setCharAt(i--, ' ');
        while (i >= 0) {
            if (WhiteSpaceProcessor.isWhiteSpaceExceptSpace(buf.charAt(i))) {
                buf.setCharAt(i, ' ');
            }
            --i;
        }
        return new String(buf);
    }

    public static CharSequence trim(CharSequence text) {
        int end;
        int start;
        int len = text.length();
        for (start = 0; start < len && WhiteSpaceProcessor.isWhiteSpace(text.charAt(start)); ++start) {
        }
        for (end = len - 1; end > start && WhiteSpaceProcessor.isWhiteSpace(text.charAt(end)); --end) {
        }
        if (start == 0 && end == len - 1) {
            return text;
        }
        return text.subSequence(start, end + 1);
    }

    public static String collapse(String text) {
        return WhiteSpaceProcessor.collapse((CharSequence)text).toString();
    }

    public static CharSequence collapse(CharSequence text) {
        int s;
        int len = text.length();
        for (s = 0; s < len && !WhiteSpaceProcessor.isWhiteSpace(text.charAt(s)); ++s) {
        }
        if (s == len) {
            return text;
        }
        StringBuilder result = new StringBuilder(len);
        if (s != 0) {
            for (int i = 0; i < s; ++i) {
                result.append(text.charAt(i));
            }
            result.append(' ');
        }
        boolean inStripMode = true;
        for (int i = s + 1; i < len; ++i) {
            char ch = text.charAt(i);
            boolean b = WhiteSpaceProcessor.isWhiteSpace(ch);
            if (inStripMode && b) continue;
            inStripMode = b;
            if (inStripMode) {
                result.append(' ');
                continue;
            }
            result.append(ch);
        }
        len = result.length();
        if (len > 0 && result.charAt(len - 1) == ' ') {
            result.setLength(len - 1);
        }
        return result;
    }

    public static final boolean isWhiteSpace(CharSequence s) {
        for (int i = s.length() - 1; i >= 0; --i) {
            if (WhiteSpaceProcessor.isWhiteSpace(s.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static final boolean isWhiteSpace(char ch) {
        if (ch > ' ') {
            return false;
        }
        return ch == '\t' || ch == '\n' || ch == '\r' || ch == ' ';
    }

    protected static final boolean isWhiteSpaceExceptSpace(char ch) {
        if (ch >= ' ') {
            return false;
        }
        return ch == '\t' || ch == '\n' || ch == '\r';
    }
}

