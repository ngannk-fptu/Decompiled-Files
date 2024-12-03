/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.lang;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SystemUtils {
    private static final Pattern REPLACE_ME_REGEX = Pattern.compile("(?<!\\$)\\$\\{\\s*(.+?)\\s*\\}");
    private static final Pattern UNESCAPE_ME_REGEX = Pattern.compile("\\$\\$\\{\\s*(.+?)\\s*\\}");

    private static String _unescape(String string) {
        Matcher matcher = UNESCAPE_ME_REGEX.matcher(string);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            String string2 = '\\' + matcher.group(0).substring(1);
            matcher.appendReplacement(stringBuffer, string2);
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static String _mapReplace(String string, Map<String, String> map) {
        Matcher matcher = REPLACE_ME_REGEX.matcher(string);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            String string2 = map.get(matcher.group(1));
            if (string2 == null) continue;
            matcher.appendReplacement(stringBuffer, string2);
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static Map<String, String> propsMap() {
        return Collections.checkedMap(System.getProperties(), String.class, String.class);
    }

    public static String mapReplace(String string, Map<String, String> map) {
        return SystemUtils._unescape(SystemUtils._mapReplace(string, map));
    }

    public static String sysPropsReplace(String string) {
        return SystemUtils.mapReplace(string, SystemUtils.propsMap());
    }

    public static String envReplace(String string) {
        return SystemUtils.mapReplace(string, System.getenv());
    }

    public static String sysPropsEnvReplace(String string) {
        String string2 = SystemUtils._mapReplace(string, SystemUtils.propsMap());
        return SystemUtils.envReplace(string2);
    }

    private SystemUtils() {
    }
}

