/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharSet;
import org.apache.commons.lang.StringUtils;

public class CharSetUtils {
    public static CharSet evaluateSet(String[] set) {
        if (set == null) {
            return null;
        }
        return new CharSet(set);
    }

    public static String squeeze(String str, String set) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(set)) {
            return str;
        }
        String[] strs = new String[]{set};
        return CharSetUtils.squeeze(str, strs);
    }

    public static String squeeze(String str, String[] set) {
        if (StringUtils.isEmpty(str) || ArrayUtils.isEmpty(set)) {
            return str;
        }
        CharSet chars = CharSet.getInstance(set);
        StringBuffer buffer = new StringBuffer(str.length());
        char[] chrs = str.toCharArray();
        int sz = chrs.length;
        int lastChar = 32;
        int ch = 32;
        for (int i = 0; i < sz; ++i) {
            ch = chrs[i];
            if (chars.contains((char)ch) && ch == lastChar && i != 0) continue;
            buffer.append((char)ch);
            lastChar = ch;
        }
        return buffer.toString();
    }

    public static int count(String str, String set) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(set)) {
            return 0;
        }
        String[] strs = new String[]{set};
        return CharSetUtils.count(str, strs);
    }

    public static int count(String str, String[] set) {
        if (StringUtils.isEmpty(str) || ArrayUtils.isEmpty(set)) {
            return 0;
        }
        CharSet chars = CharSet.getInstance(set);
        int count = 0;
        char[] chrs = str.toCharArray();
        int sz = chrs.length;
        for (int i = 0; i < sz; ++i) {
            if (!chars.contains(chrs[i])) continue;
            ++count;
        }
        return count;
    }

    public static String keep(String str, String set) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0 || StringUtils.isEmpty(set)) {
            return "";
        }
        String[] strs = new String[]{set};
        return CharSetUtils.keep(str, strs);
    }

    public static String keep(String str, String[] set) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0 || ArrayUtils.isEmpty(set)) {
            return "";
        }
        return CharSetUtils.modify(str, set, true);
    }

    public static String delete(String str, String set) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(set)) {
            return str;
        }
        String[] strs = new String[]{set};
        return CharSetUtils.delete(str, strs);
    }

    public static String delete(String str, String[] set) {
        if (StringUtils.isEmpty(str) || ArrayUtils.isEmpty(set)) {
            return str;
        }
        return CharSetUtils.modify(str, set, false);
    }

    private static String modify(String str, String[] set, boolean expect) {
        CharSet chars = CharSet.getInstance(set);
        StringBuffer buffer = new StringBuffer(str.length());
        char[] chrs = str.toCharArray();
        int sz = chrs.length;
        for (int i = 0; i < sz; ++i) {
            if (chars.contains(chrs[i]) != expect) continue;
            buffer.append(chrs[i]);
        }
        return buffer.toString();
    }

    public static String translate(String str, String searchChars, String replaceChars) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        StringBuffer buffer = new StringBuffer(str.length());
        char[] chrs = str.toCharArray();
        char[] withChrs = replaceChars.toCharArray();
        int sz = chrs.length;
        int withMax = replaceChars.length() - 1;
        for (int i = 0; i < sz; ++i) {
            int idx = searchChars.indexOf(chrs[i]);
            if (idx != -1) {
                if (idx > withMax) {
                    idx = withMax;
                }
                buffer.append(withChrs[idx]);
                continue;
            }
            buffer.append(chrs[i]);
        }
        return buffer.toString();
    }
}

