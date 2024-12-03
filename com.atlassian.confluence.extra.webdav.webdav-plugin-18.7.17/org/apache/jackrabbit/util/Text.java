/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Properties;

public class Text {
    public static final char[] hexTable;
    public static BitSet URISave;
    public static BitSet URISaveEx;

    private Text() {
    }

    public static String md5(String data, String enc) throws UnsupportedEncodingException {
        try {
            return Text.digest("MD5", data.getBytes(enc));
        }
        catch (NoSuchAlgorithmException e) {
            throw new InternalError("MD5 digest not available???");
        }
    }

    public static String md5(String data) {
        try {
            return Text.md5(data, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new InternalError("UTF8 digest not available???");
        }
    }

    public static String digest(String algorithm, String data, String enc) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return Text.digest(algorithm, data.getBytes(enc));
    }

    public static String digest(String algorithm, byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] digest = md.digest(data);
        StringBuilder res = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            res.append(hexTable[b >> 4 & 0xF]);
            res.append(hexTable[b & 0xF]);
        }
        return res.toString();
    }

    public static String[] explode(String str, int ch) {
        return Text.explode(str, ch, false);
    }

    public static String[] explode(String str, int ch, boolean respectEmpty) {
        int pos;
        if (str == null || str.length() == 0) {
            return new String[0];
        }
        ArrayList<String> strings = new ArrayList<String>();
        int lastpos = 0;
        while ((pos = str.indexOf(ch, lastpos)) >= 0) {
            if (pos - lastpos > 0 || respectEmpty) {
                strings.add(str.substring(lastpos, pos));
            }
            lastpos = pos + 1;
        }
        if (lastpos < str.length()) {
            strings.add(str.substring(lastpos));
        } else if (respectEmpty && lastpos == str.length()) {
            strings.add("");
        }
        return strings.toArray(new String[strings.size()]);
    }

    public static String implode(String[] arr, String delim) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < arr.length; ++i) {
            if (i > 0) {
                buf.append(delim);
            }
            buf.append(arr[i]);
        }
        return buf.toString();
    }

    public static String replace(String text, String oldString, String newString) {
        if (text == null || oldString == null || newString == null) {
            throw new IllegalArgumentException("null argument");
        }
        int pos = text.indexOf(oldString);
        if (pos == -1) {
            return text;
        }
        int lastPos = 0;
        StringBuilder sb = new StringBuilder(text.length());
        while (pos != -1) {
            sb.append(text.substring(lastPos, pos));
            sb.append(newString);
            lastPos = pos + oldString.length();
            pos = text.indexOf(oldString, lastPos);
        }
        if (lastPos < text.length()) {
            sb.append(text.substring(lastPos));
        }
        return sb.toString();
    }

    public static String encodeIllegalXMLCharacters(String text) {
        return Text.encodeMarkupCharacters(text, false);
    }

    public static String encodeIllegalHTMLCharacters(String text) {
        return Text.encodeMarkupCharacters(text, true);
    }

    private static String encodeMarkupCharacters(String text, boolean isHtml) {
        if (text == null) {
            throw new IllegalArgumentException("null argument");
        }
        StringBuilder buf = null;
        int length = text.length();
        int pos = 0;
        block3: for (int i = 0; i < length; ++i) {
            char ch = text.charAt(i);
            switch (ch) {
                case '\"': 
                case '&': 
                case '\'': 
                case '<': 
                case '>': {
                    if (buf == null) {
                        buf = new StringBuilder();
                    }
                    if (i > 0) {
                        buf.append(text.substring(pos, i));
                    }
                    pos = i + 1;
                    break;
                }
                default: {
                    continue block3;
                }
            }
            if (ch == '<') {
                buf.append("&lt;");
                continue;
            }
            if (ch == '>') {
                buf.append("&gt;");
                continue;
            }
            if (ch == '&') {
                buf.append("&amp;");
                continue;
            }
            if (ch == '\"') {
                buf.append("&quot;");
                continue;
            }
            if (ch != '\'') continue;
            buf.append(isHtml ? "&#39;" : "&apos;");
        }
        if (buf == null) {
            return text;
        }
        if (pos < length) {
            buf.append(text.substring(pos));
        }
        return buf.toString();
    }

    public static String escape(String string, char escape) {
        return Text.escape(string, escape, false);
    }

    public static String escape(String string, char escape, boolean isPath) {
        BitSet validChars = isPath ? URISaveEx : URISave;
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        StringBuilder out = new StringBuilder(bytes.length);
        for (byte aByte : bytes) {
            int c = aByte & 0xFF;
            if (validChars.get(c) && c != escape) {
                out.append((char)c);
                continue;
            }
            out.append(escape);
            out.append(hexTable[c >> 4 & 0xF]);
            out.append(hexTable[c & 0xF]);
        }
        return out.toString();
    }

    public static String escape(String string) {
        return Text.escape(string, '%');
    }

    public static String escapePath(String path) {
        return Text.escape(path, '%', true);
    }

    public static String unescape(String string, char escape) {
        byte[] utf8 = string.getBytes(StandardCharsets.UTF_8);
        if (utf8.length >= 1 && utf8[utf8.length - 1] == escape || utf8.length >= 2 && utf8[utf8.length - 2] == escape) {
            throw new IllegalArgumentException("Premature end of escape sequence at end of input");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(utf8.length);
        for (int k = 0; k < utf8.length; ++k) {
            byte b = utf8[k];
            if (b == escape) {
                out.write((Text.decodeDigit(utf8[++k]) << 4) + Text.decodeDigit(utf8[++k]));
                continue;
            }
            out.write(b);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    public static String unescape(String string) {
        return Text.unescape(string, '%');
    }

    public static String escapeIllegalJcrChars(String name) {
        return Text.escapeIllegalChars(name, "%/:[]*|\t\r\n");
    }

    public static String escapeIllegalJcr10Chars(String name) {
        return Text.escapeIllegalChars(name, "%/:[]*'\"|\t\r\n");
    }

    private static String escapeIllegalChars(String name, String illegal) {
        StringBuilder buffer = new StringBuilder(name.length() * 2);
        for (int i = 0; i < name.length(); ++i) {
            char ch = name.charAt(i);
            if (illegal.indexOf(ch) != -1 || ch == '.' && name.length() < 3 || ch == ' ' && (i == 0 || i == name.length() - 1)) {
                buffer.append('%');
                buffer.append(Character.toUpperCase(Character.forDigit(ch / 16, 16)));
                buffer.append(Character.toUpperCase(Character.forDigit(ch % 16, 16)));
                continue;
            }
            buffer.append(ch);
        }
        return buffer.toString();
    }

    public static String escapeIllegalXpathSearchChars(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s.substring(0, s.length() - 1));
        char c = s.charAt(s.length() - 1);
        if (c == '!' || c == '(' || c == ':' || c == '^' || c == '[' || c == ']' || c == '{' || c == '}' || c == '?') {
            sb.append('\\');
        }
        sb.append(c);
        return sb.toString();
    }

    public static String unescapeIllegalJcrChars(String name) {
        StringBuilder buffer = new StringBuilder(name.length());
        int i = name.indexOf(37);
        while (i > -1 && i + 2 < name.length()) {
            buffer.append(name.toCharArray(), 0, i);
            int a = Character.digit(name.charAt(i + 1), 16);
            int b = Character.digit(name.charAt(i + 2), 16);
            if (a > -1 && b > -1) {
                buffer.append((char)(a * 16 + b));
                name = name.substring(i + 3);
            } else {
                buffer.append('%');
                name = name.substring(i + 1);
            }
            i = name.indexOf(37);
        }
        buffer.append(name);
        return buffer.toString();
    }

    public static String getName(String path) {
        return Text.getName(path, '/');
    }

    public static String getName(String path, char delim) {
        return path == null ? null : path.substring(path.lastIndexOf(delim) + 1);
    }

    public static String getName(String path, boolean ignoreTrailingSlash) {
        if (ignoreTrailingSlash && path != null && path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }
        return Text.getName(path);
    }

    public static String getNamespacePrefix(String qname) {
        int pos = qname.indexOf(58);
        return pos >= 0 ? qname.substring(0, pos) : "";
    }

    public static String getLocalName(String qname) {
        int pos = qname.indexOf(58);
        return pos >= 0 ? qname.substring(pos + 1) : qname;
    }

    public static boolean isSibling(String p1, String p2) {
        int pos2;
        int pos1 = p1.lastIndexOf(47);
        return pos1 == (pos2 = p2.lastIndexOf(47)) && pos1 >= 0 && p1.regionMatches(0, p2, 0, pos1);
    }

    public static boolean isDescendant(String path, String descendant) {
        String pattern = path.endsWith("/") ? path : path + "/";
        return !pattern.equals(descendant) && descendant.startsWith(pattern);
    }

    public static boolean isDescendantOrEqual(String path, String descendant) {
        if (path.equals(descendant)) {
            return true;
        }
        String pattern = path.endsWith("/") ? path : path + "/";
        return descendant.startsWith(pattern);
    }

    public static String getRelativeParent(String path, int level) {
        int idx = path.length();
        while (level > 0) {
            if ((idx = path.lastIndexOf(47, idx - 1)) < 0) {
                return "";
            }
            --level;
        }
        return idx == 0 ? "/" : path.substring(0, idx);
    }

    public static String getRelativeParent(String path, int level, boolean ignoreTrailingSlash) {
        if (ignoreTrailingSlash && path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }
        return Text.getRelativeParent(path, level);
    }

    public static String getAbsoluteParent(String path, int level) {
        int idx = 0;
        int len = path.length();
        while (level >= 0 && idx < len) {
            if ((idx = path.indexOf(47, idx + 1)) < 0) {
                idx = len;
            }
            --level;
        }
        return level >= 0 ? "" : path.substring(0, idx);
    }

    public static String replaceVariables(Properties variables, String value, boolean ignoreMissing) throws IllegalArgumentException {
        StringBuilder result = new StringBuilder();
        int p = 0;
        int q = value.indexOf("${");
        while (q != -1) {
            result.append(value.substring(p, q));
            p = q;
            if ((q = value.indexOf("}", q + 2)) == -1) continue;
            String variable = value.substring(p + 2, q);
            String replacement = variables.getProperty(variable);
            if (replacement == null) {
                if (ignoreMissing) {
                    replacement = "";
                } else {
                    throw new IllegalArgumentException("Replacement not found for ${" + variable + "}.");
                }
            }
            result.append(replacement);
            p = q + 1;
            q = value.indexOf("${", p);
        }
        result.append(value.substring(p, value.length()));
        return result.toString();
    }

    private static byte decodeDigit(byte b) {
        if (b >= 48 && b <= 57) {
            return (byte)(b - 48);
        }
        if (b >= 65 && b <= 70) {
            return (byte)(b - 55);
        }
        if (b >= 97 && b <= 102) {
            return (byte)(b - 87);
        }
        throw new IllegalArgumentException("Escape sequence is not hexadecimal: " + (char)b);
    }

    static {
        int i;
        hexTable = "0123456789abcdef".toCharArray();
        URISave = new BitSet(256);
        for (i = 97; i <= 122; ++i) {
            URISave.set(i);
        }
        for (i = 65; i <= 90; ++i) {
            URISave.set(i);
        }
        for (i = 48; i <= 57; ++i) {
            URISave.set(i);
        }
        URISave.set(45);
        URISave.set(95);
        URISave.set(46);
        URISave.set(33);
        URISave.set(126);
        URISave.set(42);
        URISave.set(39);
        URISave.set(40);
        URISave.set(41);
        URISaveEx = (BitSet)URISave.clone();
        URISaveEx.set(47);
    }
}

