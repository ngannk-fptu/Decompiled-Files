/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.mapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PathMapper {
    private Map mappings = new HashMap();

    public void put(String key, String pattern) {
        if (key != null) {
            this.mappings.put(pattern, key);
        }
    }

    public String get(String path) {
        String mapped;
        if (path == null) {
            path = "/";
        }
        if ((mapped = PathMapper.findKey(path, this.mappings)) == null) {
            return null;
        }
        return (String)this.mappings.get(mapped);
    }

    private static String findKey(String path, Map mappings) {
        String result = PathMapper.findExactKey(path, mappings);
        if (result == null) {
            result = PathMapper.findComplexKey(path, mappings);
        }
        if (result == null) {
            result = PathMapper.findDefaultKey(mappings);
        }
        return result;
    }

    private static String findExactKey(String path, Map mappings) {
        if (mappings.containsKey(path)) {
            return path;
        }
        return null;
    }

    private static String findComplexKey(String path, Map mappings) {
        Iterator i = mappings.keySet().iterator();
        String result = null;
        String key = null;
        while (i.hasNext()) {
            key = (String)i.next();
            if (key.length() <= 1 || key.indexOf(63) == -1 && key.indexOf(42) == -1 || !PathMapper.match(key, path, false) || result != null && key.length() <= result.length()) continue;
            result = key;
        }
        return result;
    }

    private static String findDefaultKey(Map mappings) {
        String[] defaultKeys = new String[]{"/", "*", "/*"};
        for (int i = 0; i < defaultKeys.length; ++i) {
            if (!mappings.containsKey(defaultKeys[i])) continue;
            return defaultKeys[i];
        }
        return null;
    }

    private static boolean match(String pattern, String str, boolean isCaseSensitive) {
        char ch;
        int i;
        char[] patArr = pattern.toCharArray();
        char[] strArr = str.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        boolean containsStar = false;
        for (i = 0; i < patArr.length; ++i) {
            if (patArr[i] != '*') continue;
            containsStar = true;
            break;
        }
        if (!containsStar) {
            if (patIdxEnd != strIdxEnd) {
                return false;
            }
            for (i = 0; i <= patIdxEnd; ++i) {
                char ch2 = patArr[i];
                if (ch2 == '?') continue;
                if (isCaseSensitive && ch2 != strArr[i]) {
                    return false;
                }
                if (isCaseSensitive || Character.toUpperCase(ch2) == Character.toUpperCase(strArr[i])) continue;
                return false;
            }
            return true;
        }
        if (patIdxEnd == 0) {
            return true;
        }
        while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
            if (ch != '?') {
                if (isCaseSensitive && ch != strArr[strIdxStart]) {
                    return false;
                }
                if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart])) {
                    return false;
                }
            }
            ++patIdxStart;
            ++strIdxStart;
        }
        if (strIdxStart > strIdxEnd) {
            for (i = patIdxStart; i <= patIdxEnd; ++i) {
                if (patArr[i] == '*') continue;
                return false;
            }
            return true;
        }
        while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
            if (ch != '?') {
                if (isCaseSensitive && ch != strArr[strIdxEnd]) {
                    return false;
                }
                if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxEnd])) {
                    return false;
                }
            }
            --patIdxEnd;
            --strIdxEnd;
        }
        if (strIdxStart > strIdxEnd) {
            for (i = patIdxStart; i <= patIdxEnd; ++i) {
                if (patArr[i] == '*') continue;
                return false;
            }
            return true;
        }
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int i2 = patIdxStart + 1; i2 <= patIdxEnd; ++i2) {
                if (patArr[i2] != '*') continue;
                patIdxTmp = i2;
                break;
            }
            if (patIdxTmp == patIdxStart + 1) {
                ++patIdxStart;
                continue;
            }
            int patLength = patIdxTmp - patIdxStart - 1;
            int strLength = strIdxEnd - strIdxStart + 1;
            int foundIdx = -1;
            block8: for (int i3 = 0; i3 <= strLength - patLength; ++i3) {
                for (int j = 0; j < patLength; ++j) {
                    ch = patArr[patIdxStart + j + 1];
                    if (ch != '?' && (isCaseSensitive && ch != strArr[strIdxStart + i3 + j] || !isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart + i3 + j]))) continue block8;
                }
                foundIdx = strIdxStart + i3;
                break;
            }
            if (foundIdx == -1) {
                return false;
            }
            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx + patLength;
        }
        for (i = patIdxStart; i <= patIdxEnd; ++i) {
            if (patArr[i] == '*') continue;
            return false;
        }
        return true;
    }
}

