/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.common.org.springframework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public abstract class StringUtils {
    private static final String FOLDER_SEPARATOR = "/";
    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
    private static final String TOP_PATH = "..";
    private static final String CURRENT_PATH = ".";

    public static boolean hasLength(String str) {
        return str != null && str.length() > 0;
    }

    public static boolean hasText(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; ++i) {
            if (Character.isWhitespace(str.charAt(i))) continue;
            return true;
        }
        return false;
    }

    public static String trimLeadingWhitespace(String str) {
        if (str.length() == 0) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
            buf.deleteCharAt(0);
        }
        return buf.toString();
    }

    public static String trimTrailingWhitespace(String str) {
        if (str.length() == 0) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int pos = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }

    public static String replace(String inString, String oldPattern, String newPattern) {
        if (inString == null) {
            return null;
        }
        if (oldPattern == null || newPattern == null) {
            return inString;
        }
        StringBuffer sbuf = new StringBuffer();
        int pos = 0;
        int index = inString.indexOf(oldPattern);
        int patLen = oldPattern.length();
        while (index >= 0) {
            sbuf.append(inString.substring(pos, index));
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));
        return sbuf.toString();
    }

    public static String delete(String inString, String pattern) {
        return StringUtils.replace(inString, pattern, "");
    }

    public static String deleteAny(String inString, String chars) {
        if (inString == null || chars == null) {
            return inString;
        }
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < inString.length(); ++i) {
            char c = inString.charAt(i);
            if (chars.indexOf(c) != -1) continue;
            out.append(c);
        }
        return out.toString();
    }

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return StringUtils.tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        StringTokenizer st = new StringTokenizer(str, delimiters);
        ArrayList<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (ignoreEmptyTokens && token.length() <= 0) continue;
            tokens.add(token);
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    public static String[] delimitedListToStringArray(String str, String delimiter) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[]{str};
        }
        ArrayList<String> result = new ArrayList<String>();
        int pos = 0;
        int delPos = 0;
        while ((delPos = str.indexOf(delimiter, pos)) != -1) {
            result.add(str.substring(pos, delPos));
            pos = delPos + delimiter.length();
        }
        if (str.length() > 0 && pos <= str.length()) {
            result.add(str.substring(pos));
        }
        return result.toArray(new String[result.size()]);
    }

    public static String[] commaDelimitedListToStringArray(String str) {
        return StringUtils.delimitedListToStringArray(str, ",");
    }

    public static Set commaDelimitedListToSet(String str) {
        TreeSet<String> set = new TreeSet<String>();
        String[] tokens = StringUtils.commaDelimitedListToStringArray(str);
        for (int i = 0; i < tokens.length; ++i) {
            set.add(tokens[i]);
        }
        return set;
    }

    public static String arrayToDelimitedString(Object[] arr, String delim) {
        if (arr == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    public static String collectionToDelimitedString(Collection c, String delim, String prefix, String suffix) {
        if (c == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        Iterator it = c.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i++ > 0) {
                sb.append(delim);
            }
            sb.append(prefix + it.next() + suffix);
        }
        return sb.toString();
    }

    public static String collectionToDelimitedString(Collection coll, String delim) {
        return StringUtils.collectionToDelimitedString(coll, delim, "", "");
    }

    public static String arrayToCommaDelimitedString(Object[] arr) {
        return StringUtils.arrayToDelimitedString(arr, ",");
    }

    public static String collectionToCommaDelimitedString(Collection coll) {
        return StringUtils.collectionToDelimitedString(coll, ",");
    }

    public static String[] addStringToArray(String[] arr, String str) {
        String[] newArr = new String[arr.length + 1];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = str;
        return newArr;
    }

    public static String[] sortStringArray(String[] source) {
        if (source == null) {
            return new String[0];
        }
        Arrays.sort(source);
        return source;
    }

    public static String unqualify(String qualifiedName) {
        return StringUtils.unqualify(qualifiedName, '.');
    }

    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }

    public static String capitalize(String str) {
        return StringUtils.changeFirstCharacterCase(true, str);
    }

    public static String uncapitalize(String str) {
        return StringUtils.changeFirstCharacterCase(false, str);
    }

    private static String changeFirstCharacterCase(boolean capitalize, String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        StringBuffer buf = new StringBuffer(strLen);
        if (capitalize) {
            buf.append(Character.toUpperCase(str.charAt(0)));
        } else {
            buf.append(Character.toLowerCase(str.charAt(0)));
        }
        buf.append(str.substring(1));
        return buf.toString();
    }

    public static String getFilename(String path) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        return separatorIndex != -1 ? path.substring(separatorIndex + 1) : path;
    }

    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath = newPath + FOLDER_SEPARATOR;
            }
            return newPath + relativePath;
        }
        return relativePath;
    }

    public static String cleanPath(String path) {
        String pathToUse = StringUtils.replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
        String[] pathArray = StringUtils.delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
        LinkedList<String> pathElements = new LinkedList<String>();
        int tops = 0;
        for (int i = pathArray.length - 1; i >= 0; --i) {
            if (CURRENT_PATH.equals(pathArray[i])) continue;
            if (TOP_PATH.equals(pathArray[i])) {
                ++tops;
                continue;
            }
            if (tops > 0) {
                --tops;
                continue;
            }
            pathElements.add(0, pathArray[i]);
        }
        return StringUtils.collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
    }

    public static boolean pathEquals(String path1, String path2) {
        return StringUtils.cleanPath(path1).equals(StringUtils.cleanPath(path2));
    }

    public static Locale parseLocaleString(String localeString) {
        String[] parts = StringUtils.tokenizeToStringArray(localeString, "_ ", false, false);
        String language = parts.length > 0 ? parts[0] : "";
        String country = parts.length > 1 ? parts[1] : "";
        String variant = parts.length > 2 ? parts[2] : "";
        return language.length() > 0 ? new Locale(language, country, variant) : null;
    }
}

