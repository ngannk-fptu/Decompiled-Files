/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharsetUtils {
    private static final Pattern CHARSET_NAME_PATTERN = Pattern.compile("[ \\\"]*([^ >,;\\\"]+).*");
    private static final Pattern ISO_NAME_PATTERN = Pattern.compile(".*8859-(\\d+)");
    private static final Pattern CP_NAME_PATTERN = Pattern.compile("cp-(\\d+)");
    private static final Pattern WIN_NAME_PATTERN = Pattern.compile("win-?(\\d+)");
    private static final Map<String, Charset> COMMON_CHARSETS = new HashMap<String, Charset>();
    private static Method getCharsetICU = null;
    private static Method isSupportedICU = null;

    private static Map<String, Charset> initCommonCharsets(String ... names) {
        HashMap<String, Charset> charsets = new HashMap<String, Charset>();
        for (String name : names) {
            try {
                Charset charset = Charset.forName(name);
                COMMON_CHARSETS.put(name.toLowerCase(Locale.ENGLISH), charset);
                for (String alias : charset.aliases()) {
                    COMMON_CHARSETS.put(alias.toLowerCase(Locale.ENGLISH), charset);
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return charsets;
    }

    public static boolean isSupported(String charsetName) {
        try {
            if (isSupportedICU != null && ((Boolean)isSupportedICU.invoke(null, charsetName)).booleanValue()) {
                return true;
            }
            return Charset.isSupported(charsetName);
        }
        catch (IllegalCharsetNameException e) {
            return false;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static String clean(String charsetName) {
        try {
            return CharsetUtils.forName(charsetName).name();
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Charset forName(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        Matcher m = CHARSET_NAME_PATTERN.matcher(name);
        if (!m.matches()) {
            throw new IllegalCharsetNameException(name);
        }
        name = m.group(1);
        String lower = name.toLowerCase(Locale.ENGLISH);
        Charset charset = COMMON_CHARSETS.get(lower);
        if (charset != null) {
            return charset;
        }
        if ("none".equals(lower) || "no".equals(lower)) {
            throw new IllegalCharsetNameException(name);
        }
        Matcher iso = ISO_NAME_PATTERN.matcher(lower);
        Matcher cp = CP_NAME_PATTERN.matcher(lower);
        Matcher win = WIN_NAME_PATTERN.matcher(lower);
        if (iso.matches()) {
            name = "iso-8859-" + iso.group(1);
            charset = COMMON_CHARSETS.get(name);
        } else if (cp.matches()) {
            name = "cp" + cp.group(1);
            charset = COMMON_CHARSETS.get(name);
        } else if (win.matches()) {
            name = "windows-" + win.group(1);
            charset = COMMON_CHARSETS.get(name);
        }
        if (charset != null) {
            return charset;
        }
        if (getCharsetICU != null) {
            try {
                Charset cs = (Charset)getCharsetICU.invoke(null, name);
                if (cs != null) {
                    return cs;
                }
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
                // empty catch block
            }
        }
        return Charset.forName(name);
    }

    static {
        CharsetUtils.initCommonCharsets("Big5", "EUC-JP", "EUC-KR", "x-EUC-TW", "GB18030", "IBM855", "IBM866", "ISO-2022-CN", "ISO-2022-JP", "ISO-2022-KR", "ISO-8859-1", "ISO-8859-2", "ISO-8859-3", "ISO-8859-4", "ISO-8859-5", "ISO-8859-6", "ISO-8859-7", "ISO-8859-8", "ISO-8859-9", "ISO-8859-11", "ISO-8859-13", "ISO-8859-15", "KOI8-R", "x-MacCyrillic", "SHIFT_JIS", "UTF-8", "UTF-16BE", "UTF-16LE", "windows-1251", "windows-1252", "windows-1253", "windows-1255");
        COMMON_CHARSETS.put("iso-8851-1", COMMON_CHARSETS.get("iso-8859-1"));
        COMMON_CHARSETS.put("windows", COMMON_CHARSETS.get("windows-1252"));
        COMMON_CHARSETS.put("koi8r", COMMON_CHARSETS.get("koi8-r"));
        Class<?> icuCharset = null;
        try {
            icuCharset = CharsetUtils.class.getClassLoader().loadClass("com.ibm.icu.charset.CharsetICU");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        if (icuCharset != null) {
            try {
                getCharsetICU = icuCharset.getMethod("forNameICU", String.class);
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
            try {
                isSupportedICU = icuCharset.getMethod("isSupported", String.class);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }
}

