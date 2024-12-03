/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import java.util.Collection;
import java.util.Iterator;

public final class StringUtil {
    static final char CHAR_SPACE = ' ';
    private static final char INT_SPACE = ' ';
    static String sLF = null;
    private static final int EOS = 65536;

    public static String getLF() {
        String lf = sLF;
        if (lf == null) {
            try {
                lf = System.getProperty("line.separator");
                sLF = lf == null ? "\n" : lf;
            }
            catch (Throwable t) {
                lf = "\n";
                sLF = "\n";
            }
        }
        return lf;
    }

    public static void appendLF(StringBuilder sb) {
        sb.append(StringUtil.getLF());
    }

    public static String concatEntries(Collection<?> coll, String sep, String lastSep) {
        if (lastSep == null) {
            lastSep = sep;
        }
        int len = coll.size();
        StringBuilder sb = new StringBuilder(16 + (len << 3));
        Iterator<?> it = coll.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i != 0) {
                if (i == len - 1) {
                    sb.append(lastSep);
                } else {
                    sb.append(sep);
                }
            }
            ++i;
            sb.append(it.next());
        }
        return sb.toString();
    }

    public static String normalizeSpaces(char[] buf, int origStart, int origEnd) {
        int start;
        int end = --origEnd;
        for (start = origStart; start <= end && buf[start] == ' '; ++start) {
        }
        if (start > end) {
            return "";
        }
        while (end > start && buf[end] == ' ') {
            --end;
        }
        int i = start + 1;
        while (i < end) {
            if (buf[i] == ' ') {
                if (buf[i + 1] == ' ') break;
                i += 2;
                continue;
            }
            ++i;
        }
        if (i >= end) {
            if (start == origStart && end == origEnd) {
                return null;
            }
            return new String(buf, start, end - start + 1);
        }
        StringBuilder sb = new StringBuilder(end - start);
        sb.append(buf, start, i - start);
        while (i <= end) {
            char c;
            if ((c = buf[i++]) == ' ') {
                sb.append(' ');
                while ((c = buf[i++]) == ' ') {
                }
                sb.append(c);
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static boolean isAllWhitespace(String str) {
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            if (str.charAt(i) <= ' ') continue;
            return false;
        }
        return true;
    }

    public static boolean isAllWhitespace(char[] ch, int start, int len) {
        len += start;
        while (start < len) {
            if (ch[start] > ' ') {
                return false;
            }
            ++start;
        }
        return true;
    }

    public static boolean equalEncodings(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();
        int i1 = 0;
        int i2 = 0;
        while (i1 < len1 || i2 < len2) {
            int c2;
            int c1 = i1 >= len1 ? 65536 : (int)str1.charAt(i1++);
            if (c1 == (c2 = i2 >= len2 ? 65536 : (int)str2.charAt(i2++))) continue;
            while (c1 <= 32 || c1 == 95 || c1 == 45) {
                c1 = i1 >= len1 ? 65536 : (int)str1.charAt(i1++);
            }
            while (c2 <= 32 || c2 == 95 || c2 == 45) {
                c2 = i2 >= len2 ? 65536 : (int)str2.charAt(i2++);
            }
            if (c1 == c2) continue;
            if (c1 == 65536 || c2 == 65536) {
                return false;
            }
            if (c1 < 127) {
                if (c1 <= 90 && c1 >= 65) {
                    c1 += 32;
                }
            } else {
                c1 = Character.toLowerCase((char)c1);
            }
            if (c2 < 127) {
                if (c2 <= 90 && c2 >= 65) {
                    c2 += 32;
                }
            } else {
                c2 = Character.toLowerCase((char)c2);
            }
            if (c1 == c2) continue;
            return false;
        }
        return true;
    }

    public static boolean encodingStartsWith(String enc, String prefix) {
        int len1 = enc.length();
        int len2 = prefix.length();
        int i1 = 0;
        int i2 = 0;
        while (i1 < len1 || i2 < len2) {
            int c2;
            int c1 = i1 >= len1 ? 65536 : (int)enc.charAt(i1++);
            if (c1 == (c2 = i2 >= len2 ? 65536 : (int)prefix.charAt(i2++))) continue;
            while (c1 <= 32 || c1 == 95 || c1 == 45) {
                c1 = i1 >= len1 ? 65536 : (int)enc.charAt(i1++);
            }
            while (c2 <= 32 || c2 == 95 || c2 == 45) {
                c2 = i2 >= len2 ? 65536 : (int)prefix.charAt(i2++);
            }
            if (c1 == c2) continue;
            if (c2 == 65536) {
                return true;
            }
            if (c1 == 65536) {
                return false;
            }
            if (Character.toLowerCase((char)c1) == Character.toLowerCase((char)c2)) continue;
            return false;
        }
        return true;
    }

    public static String trimEncoding(String str, boolean upperCase) {
        char c;
        int i;
        int len = str.length();
        for (i = 0; i < len && (c = str.charAt(i)) > ' ' && Character.isLetterOrDigit(c); ++i) {
        }
        if (i == len) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        if (i > 0) {
            sb.append(str.substring(0, i));
        }
        while (i < len) {
            char c2 = str.charAt(i);
            if (c2 > ' ' && Character.isLetterOrDigit(c2)) {
                if (upperCase) {
                    c2 = Character.toUpperCase(c2);
                }
                sb.append(c2);
            }
            ++i;
        }
        return sb.toString();
    }

    public static boolean matches(String str, char[] cbuf, int offset, int len) {
        if (str.length() != len) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (str.charAt(i) == cbuf[offset + i]) continue;
            return false;
        }
        return true;
    }

    public static final boolean isSpace(char c) {
        return c <= ' ';
    }
}

