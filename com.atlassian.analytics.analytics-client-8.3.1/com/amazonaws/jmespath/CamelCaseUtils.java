/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

final class CamelCaseUtils {
    private CamelCaseUtils() {
    }

    public static String toCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean foundFirstLowercase = false;
        for (char cur : str.toCharArray()) {
            if (Character.isUpperCase(cur) && !foundFirstLowercase) {
                stringBuilder.append(Character.toLowerCase(cur));
                continue;
            }
            foundFirstLowercase = true;
            stringBuilder.append(cur);
        }
        return stringBuilder.toString();
    }
}

