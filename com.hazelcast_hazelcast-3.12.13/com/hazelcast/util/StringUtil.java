/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.hazelcast.util;

import com.hazelcast.internal.util.XmlUtil;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class StringUtil {
    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final Locale LOCALE_INTERNAL = Locale.US;
    public static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)(\\.(\\d+))?(-\\w+(?:-\\d+)?)?(-SNAPSHOT)?$");
    private static final String GETTER_PREFIX = "get";

    private StringUtil() {
    }

    public static String bytesToString(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, UTF8_CHARSET);
    }

    public static String bytesToString(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    public static byte[] stringToBytes(String s) {
        return s.getBytes(UTF8_CHARSET);
    }

    public static boolean isNullOrEmpty(String s) {
        if (s == null) {
            return true;
        }
        return s.isEmpty();
    }

    public static boolean isNullOrEmptyAfterTrim(String s) {
        if (s == null) {
            return true;
        }
        return s.trim().isEmpty();
    }

    public static String upperCaseInternal(String s) {
        if (StringUtil.isNullOrEmpty(s)) {
            return s;
        }
        return s.toUpperCase(LOCALE_INTERNAL);
    }

    public static String lowerCaseFirstChar(String s) {
        if (s.isEmpty()) {
            return s;
        }
        char first = s.charAt(0);
        if (Character.isLowerCase(first)) {
            return s;
        }
        return Character.toLowerCase(first) + s.substring(1);
    }

    public static String lowerCaseInternal(String s) {
        if (StringUtil.isNullOrEmpty(s)) {
            return s;
        }
        return s.toLowerCase(LOCALE_INTERNAL);
    }

    public static String timeToString(long timeMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(new Date(timeMillis));
    }

    public static String timeToStringFriendly(long timeMillis) {
        return timeMillis == 0L ? "never" : StringUtil.timeToString(timeMillis);
    }

    public static int indexOf(String input, char ch, int offset) {
        for (int i = offset; i < input.length(); ++i) {
            if (input.charAt(i) != ch) continue;
            return i;
        }
        return -1;
    }

    public static int indexOf(String input, char ch) {
        return StringUtil.indexOf(input, ch, 0);
    }

    public static int lastIndexOf(String input, char ch, int offset) {
        for (int i = input.length() - 1 - offset; i >= 0; --i) {
            if (input.charAt(i) != ch) continue;
            return i;
        }
        return -1;
    }

    public static int lastIndexOf(String input, char ch) {
        return StringUtil.lastIndexOf(input, ch, 0);
    }

    public static String[] tokenizeVersionString(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            String[] tokens = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); ++i) {
                tokens[i] = matcher.group(i + 1);
            }
            return tokens;
        }
        return null;
    }

    public static String getterIntoProperty(String getterName) {
        if (getterName == null) {
            return getterName;
        }
        int length = getterName.length();
        if (!getterName.startsWith(GETTER_PREFIX) || length <= GETTER_PREFIX.length()) {
            return getterName;
        }
        String propertyName = getterName.substring(GETTER_PREFIX.length(), length);
        char firstChar = propertyName.charAt(0);
        if (Character.isLetter(firstChar)) {
            if (Character.isLowerCase(firstChar)) {
                return getterName;
            }
            propertyName = Character.toLowerCase(firstChar) + propertyName.substring(1, propertyName.length());
        }
        return propertyName;
    }

    public static String trim(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("^\\s+|\\s+$", "");
    }

    public static String[] splitByComma(String input, boolean allowEmpty) {
        if (input == null) {
            return null;
        }
        String[] splitWithEmptyValues = StringUtil.trim(input).split("\\s*,\\s*", -1);
        return allowEmpty ? splitWithEmptyValues : StringUtil.subtraction(splitWithEmptyValues, new String[]{""});
    }

    public static String[] intersection(String[] arr1, String[] arr2) {
        if (arr1 == null || arr2 == null) {
            return null;
        }
        if (arr1.length == 0 || arr2.length == 0) {
            return new String[0];
        }
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(arr1));
        list.retainAll(Arrays.asList(arr2));
        return list.toArray(new String[0]);
    }

    public static String[] subtraction(String[] arr1, String[] arr2) {
        if (arr1 == null || arr1.length == 0 || arr2 == null || arr2.length == 0) {
            return arr1;
        }
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(arr1));
        list.removeAll(Arrays.asList(arr2));
        return list.toArray(new String[0]);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null || str2 == null ? false : str1 == str2 || StringUtil.lowerCaseInternal(str1).equals(StringUtil.lowerCaseInternal(str2));
    }

    @Deprecated
    public static String formatXml(@Nullable String input, int indent) throws IllegalArgumentException {
        return XmlUtil.format(input, indent);
    }
}

