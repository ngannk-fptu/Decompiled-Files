/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import org.apache.axis.InternalException;

public class StringUtils {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    private StringUtils() {
    }

    public static boolean startsWithIgnoreWhitespaces(String prefix, String string) {
        int index2;
        int index1 = 0;
        int length1 = prefix.length();
        int length2 = string.length();
        int ch1 = 32;
        int ch2 = 32;
        for (index2 = 0; index1 < length1 && index2 < length2; ++index1, ++index2) {
            while (index1 < length1) {
                char c = prefix.charAt(index1);
                ch1 = c;
                if (!Character.isWhitespace(c)) break;
                ++index1;
            }
            while (index2 < length2) {
                char c = string.charAt(index2);
                ch2 = c;
                if (!Character.isWhitespace(c)) break;
                ++index2;
            }
            if (index1 == length1 && index2 == length2) {
                return true;
            }
            if (ch1 == ch2) continue;
            return false;
        }
        return index1 >= length1 || index2 < length2;
    }

    public static String[] split(String str, char separatorChar) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        ArrayList<String> list = new ArrayList<String>();
        int i = 0;
        int start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }
                start = ++i;
                continue;
            }
            match = true;
            ++i;
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String strip(String str) {
        return StringUtils.strip(str, null);
    }

    public static String strip(String str, String stripChars) {
        if (str == null) {
            return str;
        }
        int len = str.length();
        if (len == 0) {
            return str;
        }
        int start = StringUtils.getStripStart(str, stripChars);
        if (start == len) {
            return "";
        }
        int end = StringUtils.getStripEnd(str, stripChars);
        return start == 0 && end == len ? str : str.substring(start, end);
    }

    public static String stripStart(String str, String stripChars) {
        int start = StringUtils.getStripStart(str, stripChars);
        return start <= 0 ? str : str.substring(start);
    }

    private static int getStripStart(String str, String stripChars) {
        int start;
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return -1;
        }
        if (stripChars == null) {
            for (start = 0; start != strLen && Character.isWhitespace(str.charAt(start)); ++start) {
            }
        } else {
            if (stripChars.length() == 0) {
                return start;
            }
            while (start != strLen && stripChars.indexOf(str.charAt(start)) != -1) {
                ++start;
            }
        }
        return start;
    }

    public static String stripEnd(String str, String stripChars) {
        int end = StringUtils.getStripEnd(str, stripChars);
        return end < 0 ? str : str.substring(0, end);
    }

    private static int getStripEnd(String str, String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return -1;
        }
        if (stripChars == null) {
            while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                --end;
            }
        } else {
            if (stripChars.length() == 0) {
                return end;
            }
            while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1) {
                --end;
            }
        }
        return end;
    }

    public static String escapeNumericChar(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            StringUtils.escapeNumericChar(writer, str);
            return writer.toString();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public static void escapeNumericChar(Writer out, String str) throws IOException {
        if (str == null) {
            return;
        }
        int length = str.length();
        for (int i = 0; i < length; ++i) {
            char character = str.charAt(i);
            if (character > '\u007f') {
                out.write("&#x");
                out.write(Integer.toHexString(character).toUpperCase());
                out.write(";");
                continue;
            }
            out.write(character);
        }
    }

    public static String unescapeNumericChar(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            StringUtils.unescapeNumericChar(writer, str);
            return writer.toString();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public static void unescapeNumericChar(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        StringBuffer escapes = new StringBuffer(3);
        boolean inUnicode = false;
        for (int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (inUnicode) {
                unicode.append(ch);
                if (unicode.length() != 4) continue;
                try {
                    int value = Integer.parseInt(unicode.toString(), 16);
                    out.write((char)value);
                    unicode.setLength(0);
                    ++i;
                    inUnicode = false;
                    continue;
                }
                catch (NumberFormatException nfe) {
                    throw new InternalException(nfe);
                }
            }
            if (ch == '&') {
                if (i + 7 <= sz) {
                    escapes.append(ch);
                    escapes.append(str.charAt(i + 1));
                    escapes.append(str.charAt(i + 2));
                    if (escapes.toString().equals("&#x") && str.charAt(i + 7) == ';') {
                        inUnicode = true;
                    } else {
                        out.write(escapes.toString());
                    }
                    escapes.setLength(0);
                    i += 2;
                    continue;
                }
                out.write(ch);
                continue;
            }
            out.write(ch);
        }
    }
}

