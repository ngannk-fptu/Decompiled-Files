/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.ArrayUtils;

public class StringUtils {
    private static final int MAX_LENGTH = 9000;
    private static final int NUL = 0;
    private static final int TAB = 9;
    private static final int LF = 10;
    private static final int CR = 13;
    private static final int SPACE = 32;
    protected static Map<String, char[][]> stringCharMappings = new HashMap<String, char[][]>(4);

    private static char[][] getMappings(String encoding) {
        Object stringChars = stringCharMappings.get(encoding);
        if (stringChars == null) {
            stringChars = new char[9000][];
            if ("UTF-8".equalsIgnoreCase(encoding) || "Big5".equalsIgnoreCase(encoding) || "Windows-1252".equalsIgnoreCase(encoding)) {
                StringUtils.addMapping(8216, "'", stringChars);
                StringUtils.addMapping(8217, "'", stringChars);
                StringUtils.addMapping(8220, "\"", stringChars);
                StringUtils.addMapping(8221, "\"", stringChars);
                StringUtils.addMapping(8230, "...", stringChars);
                StringUtils.addMapping(8211, "-", stringChars);
                StringUtils.addMapping(183, "- ", stringChars);
            } else if ("ISO-8859-1".equalsIgnoreCase(encoding)) {
                StringUtils.addMapping(145, "'", stringChars);
                StringUtils.addMapping(146, "'", stringChars);
                StringUtils.addMapping(147, "\"", stringChars);
                StringUtils.addMapping(148, "\"", stringChars);
                StringUtils.addMapping(133, "...", stringChars);
                StringUtils.addMapping(150, "-", stringChars);
                StringUtils.addMapping(183, "- ", stringChars);
            }
            int[] printable = new int[]{9, 10, 13};
            for (int i = 0; i < 32; ++i) {
                if (ArrayUtils.contains((int[])printable, (int)i)) continue;
                StringUtils.addMapping(i, "", stringChars);
            }
            stringCharMappings.put(encoding, (char[][])stringChars);
        }
        return stringChars;
    }

    private static void addMapping(int charsNumericValue, String replaceStr, char[][] mappings) {
        mappings[charsNumericValue] = replaceStr.toCharArray();
    }

    public static String escapeCP1252(String s, String encoding) {
        char index;
        if (s == null) {
            return null;
        }
        int len = s.length();
        if (len == 0) {
            return s;
        }
        String trimmed = s.trim();
        if (trimmed.length() == 0 || "\"\"".equals(trimmed)) {
            return trimmed;
        }
        char[][] stringChars = StringUtils.getMappings(encoding);
        int i = 0;
        while (((index = s.charAt(i)) >= '\u2328' || stringChars[index] == null) && ++i < len) {
        }
        if (i == len) {
            return s;
        }
        StringBuilder sb = new StringBuilder(len + 40);
        char[] chars = new char[len];
        s.getChars(0, len, chars, 0);
        sb.append(chars, 0, i);
        int last = i;
        char[] subst = null;
        while (i < len) {
            char c = chars[i];
            char index2 = c;
            subst = index2 < stringChars.length ? stringChars[index2] : null;
            if (subst != null) {
                if (i > last) {
                    sb.append(chars, last, i - last);
                }
                sb.append(subst);
                last = i + 1;
            }
            ++i;
        }
        if (i > last) {
            sb.append(chars, last, i - last);
        }
        return sb.toString();
    }

    public static String crop(String original, int cropAt, String suffix) {
        if (original == null) {
            return null;
        }
        if (original.length() > cropAt) {
            original = original.substring(0, cropAt) + suffix;
        }
        return original;
    }

    public static boolean contains(String value, List<String> possiblyContains) {
        if (value == null) {
            return possiblyContains == null || possiblyContains.isEmpty();
        }
        if (possiblyContains == null || possiblyContains.isEmpty()) {
            return false;
        }
        for (String possiblyContain : possiblyContains) {
            if (!value.contains(possiblyContain)) continue;
            return true;
        }
        return false;
    }

    public static String replaceAll(String str, String oldPattern, String newPattern) {
        int i;
        if (str == null) {
            return null;
        }
        if (oldPattern == null || oldPattern.equals("")) {
            return str;
        }
        String remainder = str;
        StringBuilder buf = new StringBuilder(str.length() * 2);
        while ((i = remainder.indexOf(oldPattern)) != -1) {
            buf.append(remainder.substring(0, i));
            buf.append(newPattern);
            remainder = remainder.substring(i + oldPattern.length());
        }
        buf.append(remainder);
        return buf.toString();
    }

    public static boolean isStringAllASCII(String str) {
        if (str == null) {
            return true;
        }
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c >= '\u0000' && c <= '\u007f') continue;
            return false;
        }
        return true;
    }

    public static boolean isStringOfCharSet(String string, String charset) {
        if (string == null) {
            return true;
        }
        try {
            return string.equals(new String(string.getBytes(charset), charset));
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            return false;
        }
    }

    public static boolean isStringISO_8859_1(String string) {
        return StringUtils.isStringOfCharSet(string, "ISO-8859-1");
    }

    public static boolean equalsIgnoreLineTerminators(String s1, String s2) {
        String normalisedValue = StringUtils.normalise(s1);
        String normalisedCurrentValue = StringUtils.normalise(s2);
        return normalisedValue.equals(normalisedCurrentValue);
    }

    public static String normalise(String value) {
        return org.apache.commons.lang3.StringUtils.defaultString((String)value).replaceAll("\\r\\n?", "\n");
    }

    public static String[] splitCommaSeparatedString(String entryString) {
        return Pattern.compile("\\s*,\\s*").split(entryString);
    }

    public static String createCommaSeperatedString(Iterable<String> entries) {
        return StringUtils.createCommaSeparatedString(entries);
    }

    public static String createCommaSeparatedString(Iterable<String> entries) {
        if (entries == null) {
            return null;
        }
        return StreamSupport.stream(entries.spliterator(), false).collect(Collectors.joining(","));
    }
}

