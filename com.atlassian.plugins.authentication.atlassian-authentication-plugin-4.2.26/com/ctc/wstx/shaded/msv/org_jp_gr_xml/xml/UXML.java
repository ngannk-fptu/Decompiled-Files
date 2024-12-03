/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_jp_gr_xml.xml;

public final class UXML {
    public static String escape(String s) {
        if (s.indexOf(60) == -1 && s.indexOf(62) == -1 && s.indexOf(38) == -1 && s.indexOf(34) == -1 && s.indexOf(39) == -1) {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        for (int j = 0; j < i; ++j) {
            char c = s.charAt(j);
            if (c == '<') {
                stringbuffer.append("&lt;");
                continue;
            }
            if (c == '>') {
                stringbuffer.append("&gt;");
                continue;
            }
            if (c == '&') {
                stringbuffer.append("&amp;");
                continue;
            }
            if (c == '\"') {
                stringbuffer.append("&quot;");
                continue;
            }
            if (c == '\'') {
                stringbuffer.append("&apos;");
                continue;
            }
            stringbuffer.append(c);
        }
        return new String(stringbuffer);
    }

    public static String escapeEntityQuot(String s) {
        if (s.indexOf(37) == -1 && s.indexOf(38) == -1 && s.indexOf(34) == -1) {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        for (int j = 0; j < i; ++j) {
            char c = s.charAt(j);
            if (c == '%') {
                stringbuffer.append("&---;");
                continue;
            }
            if (c == '&') {
                stringbuffer.append("&amp;");
                continue;
            }
            if (c == '\"') {
                stringbuffer.append("&quot;");
                continue;
            }
            stringbuffer.append(c);
        }
        return new String(stringbuffer);
    }

    public static String escapeEntityApos(String s) {
        if (s.indexOf(37) == -1 && s.indexOf(38) == -1 && s.indexOf(39) == -1) {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        for (int j = 0; j < i; ++j) {
            char c = s.charAt(j);
            if (c == '%') {
                stringbuffer.append("&#x25;");
                continue;
            }
            if (c == '&') {
                stringbuffer.append("&amp;");
                continue;
            }
            if (c == '\'') {
                stringbuffer.append("&apos;");
                continue;
            }
            stringbuffer.append(c);
        }
        return new String(stringbuffer);
    }

    public static String escapeAttrQuot(String s) {
        if (s.indexOf(60) == -1 && s.indexOf(38) == -1 && s.indexOf(34) == -1) {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        for (int j = 0; j < i; ++j) {
            char c = s.charAt(j);
            if (c == '<') {
                stringbuffer.append("&lt;");
                continue;
            }
            if (c == '&') {
                stringbuffer.append("&amp;");
                continue;
            }
            if (c == '\"') {
                stringbuffer.append("&quot;");
                continue;
            }
            stringbuffer.append(c);
        }
        return new String(stringbuffer);
    }

    public static String escapeAttrApos(String s) {
        if (s.indexOf(60) == -1 && s.indexOf(38) == -1 && s.indexOf(39) == -1) {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        for (int j = 0; j < i; ++j) {
            char c = s.charAt(j);
            if (c == '<') {
                stringbuffer.append("&lt;");
                continue;
            }
            if (c == '&') {
                stringbuffer.append("&amp;");
                continue;
            }
            if (c == '\'') {
                stringbuffer.append("&apos;");
                continue;
            }
            stringbuffer.append(c);
        }
        return new String(stringbuffer);
    }

    public static String escapeSystemQuot(String s) {
        if (s.indexOf(34) == -1) {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        for (int j = 0; j < i; ++j) {
            char c = s.charAt(j);
            if (c == '\"') {
                stringbuffer.append("&quot;");
                continue;
            }
            stringbuffer.append(c);
        }
        return new String(stringbuffer);
    }

    public static String escapeSystemApos(String s) {
        if (s.indexOf(39) == -1) {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        for (int j = 0; j < i; ++j) {
            char c = s.charAt(j);
            if (c == '\'') {
                stringbuffer.append("&apos;");
                continue;
            }
            stringbuffer.append(c);
        }
        return new String(stringbuffer);
    }

    public static String escapeCharData(String s) {
        if (s.indexOf(60) == -1 && s.indexOf(38) == -1 && s.indexOf("]]>") == -1) {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = 0;
        int j = s.length();
        for (int k = 0; k < j; ++k) {
            char c = s.charAt(k);
            if (c == '<') {
                stringbuffer.append("&lt;");
            } else if (c == '&') {
                stringbuffer.append("&amp;");
            } else if (c == '>' && i >= 2) {
                stringbuffer.append("&gt;");
            } else {
                stringbuffer.append(c);
            }
            if (c == ']') {
                ++i;
                continue;
            }
            i = 0;
        }
        return new String(stringbuffer);
    }
}

