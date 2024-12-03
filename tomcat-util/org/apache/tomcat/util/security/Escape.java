/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.security;

public class Escape {
    private Escape() {
    }

    public static String htmlElementContent(String content) {
        if (content == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i);
            if (c == '<') {
                sb.append("&lt;");
                continue;
            }
            if (c == '>') {
                sb.append("&gt;");
                continue;
            }
            if (c == '\'') {
                sb.append("&#39;");
                continue;
            }
            if (c == '&') {
                sb.append("&amp;");
                continue;
            }
            if (c == '\"') {
                sb.append("&quot;");
                continue;
            }
            if (c == '/') {
                sb.append("&#47;");
                continue;
            }
            sb.append(c);
        }
        return sb.length() > content.length() ? sb.toString() : content;
    }

    public static String htmlElementContent(Object obj) {
        if (obj == null) {
            return "?";
        }
        try {
            return Escape.htmlElementContent(obj.toString());
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String xml(String content) {
        return Escape.xml(null, content);
    }

    public static String xml(String ifNull, String content) {
        return Escape.xml(ifNull, false, content);
    }

    public static String xml(String ifNull, boolean escapeCRLF, String content) {
        if (content == null) {
            return ifNull;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i);
            if (c == '<') {
                sb.append("&lt;");
                continue;
            }
            if (c == '>') {
                sb.append("&gt;");
                continue;
            }
            if (c == '\'') {
                sb.append("&apos;");
                continue;
            }
            if (c == '&') {
                sb.append("&amp;");
                continue;
            }
            if (c == '\"') {
                sb.append("&quot;");
                continue;
            }
            if (escapeCRLF && c == '\r') {
                sb.append("&#13;");
                continue;
            }
            if (escapeCRLF && c == '\n') {
                sb.append("&#10;");
                continue;
            }
            sb.append(c);
        }
        return sb.length() > content.length() ? sb.toString() : content;
    }
}

