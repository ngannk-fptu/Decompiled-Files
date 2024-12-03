/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.convert.html.spreadsheet;

import java.util.Map;

public class MacroUtils {
    public static String getStringValue(Map args, String key) {
        return (String)args.get(key);
    }

    public static int getIntValue(Map args, String key, int defaultVal) {
        int val = defaultVal;
        String strVal = MacroUtils.getStringValue(args, key);
        if (strVal != null) {
            try {
                val = Integer.parseInt(strVal);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return val;
    }

    public static boolean getBoolValue(Map args, String key, boolean defaultVal) {
        boolean val = defaultVal;
        String strVal = MacroUtils.getStringValue(args, key);
        if (strVal != null) {
            val = Boolean.parseBoolean(strVal);
        }
        return val;
    }
}

