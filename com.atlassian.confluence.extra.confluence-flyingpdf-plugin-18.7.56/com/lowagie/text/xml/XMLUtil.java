/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml;

public class XMLUtil {
    public static String escapeXML(String s, boolean onlyASCII) {
        char[] cc = s.toCharArray();
        int len = cc.length;
        StringBuilder sb = new StringBuilder();
        block7: for (char c : cc) {
            switch (c) {
                case '<': {
                    sb.append("&lt;");
                    continue block7;
                }
                case '>': {
                    sb.append("&gt;");
                    continue block7;
                }
                case '&': {
                    sb.append("&amp;");
                    continue block7;
                }
                case '\"': {
                    sb.append("&quot;");
                    continue block7;
                }
                case '\'': {
                    sb.append("&apos;");
                    continue block7;
                }
                default: {
                    if (!(c == '\t' || c == '\n' || c == '\r' || c >= ' ' && c <= '\ud7ff' || c >= '\ue000' && c <= '\ufffd') && (c < '\u10000' || c > '\u10ffff')) continue block7;
                    if (onlyASCII && c > '\u007f') {
                        sb.append("&#").append((int)c).append(';');
                        continue block7;
                    }
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}

