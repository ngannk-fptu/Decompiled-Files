/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import org.springframework.lang.Nullable;

public abstract class PropertyAccessorUtils {
    public static String getPropertyName(String propertyPath) {
        int separatorIndex = propertyPath.endsWith("]") ? propertyPath.indexOf(91) : -1;
        return separatorIndex != -1 ? propertyPath.substring(0, separatorIndex) : propertyPath;
    }

    public static boolean isNestedOrIndexedProperty(@Nullable String propertyPath) {
        if (propertyPath == null) {
            return false;
        }
        for (int i2 = 0; i2 < propertyPath.length(); ++i2) {
            char ch = propertyPath.charAt(i2);
            if (ch != '.' && ch != '[') continue;
            return true;
        }
        return false;
    }

    public static int getFirstNestedPropertySeparatorIndex(String propertyPath) {
        return PropertyAccessorUtils.getNestedPropertySeparatorIndex(propertyPath, false);
    }

    public static int getLastNestedPropertySeparatorIndex(String propertyPath) {
        return PropertyAccessorUtils.getNestedPropertySeparatorIndex(propertyPath, true);
    }

    private static int getNestedPropertySeparatorIndex(String propertyPath, boolean last) {
        int i2;
        boolean inKey = false;
        int length = propertyPath.length();
        int n = i2 = last ? length - 1 : 0;
        while (last ? i2 >= 0 : i2 < length) {
            switch (propertyPath.charAt(i2)) {
                case '[': 
                case ']': {
                    inKey = !inKey;
                    break;
                }
                case '.': {
                    if (inKey) break;
                    return i2;
                }
            }
            if (last) {
                --i2;
                continue;
            }
            ++i2;
        }
        return -1;
    }

    public static boolean matchesProperty(String registeredPath, String propertyPath) {
        if (!registeredPath.startsWith(propertyPath)) {
            return false;
        }
        if (registeredPath.length() == propertyPath.length()) {
            return true;
        }
        if (registeredPath.charAt(propertyPath.length()) != '[') {
            return false;
        }
        return registeredPath.indexOf(93, propertyPath.length() + 1) == registeredPath.length() - 1;
    }

    public static String canonicalPropertyName(@Nullable String propertyName) {
        if (propertyName == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(propertyName);
        int searchIndex = 0;
        while (searchIndex != -1) {
            int keyEnd;
            int keyStart = sb.indexOf("[", searchIndex);
            searchIndex = -1;
            if (keyStart == -1 || (keyEnd = sb.indexOf("]", keyStart + "[".length())) == -1) continue;
            String key = sb.substring(keyStart + "[".length(), keyEnd);
            if (key.startsWith("'") && key.endsWith("'") || key.startsWith("\"") && key.endsWith("\"")) {
                sb.delete(keyStart + 1, keyStart + 2);
                sb.delete(keyEnd - 2, keyEnd - 1);
                keyEnd -= 2;
            }
            searchIndex = keyEnd + "]".length();
        }
        return sb.toString();
    }

    @Nullable
    public static String[] canonicalPropertyNames(@Nullable String[] propertyNames) {
        if (propertyNames == null) {
            return null;
        }
        String[] result = new String[propertyNames.length];
        for (int i2 = 0; i2 < propertyNames.length; ++i2) {
            result[i2] = PropertyAccessorUtils.canonicalPropertyName(propertyNames[i2]);
        }
        return result;
    }
}

