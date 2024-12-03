/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.selectors.TokenizedPattern;
import org.apache.tools.ant.util.FileUtils;

public final class SelectorUtils {
    public static final String DEEP_TREE_MATCH = "**";
    private static final SelectorUtils instance = new SelectorUtils();
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    private SelectorUtils() {
    }

    public static SelectorUtils getInstance() {
        return instance;
    }

    public static boolean matchPatternStart(String pattern, String str) {
        return SelectorUtils.matchPatternStart(pattern, str, true);
    }

    public static boolean matchPatternStart(String pattern, String str, boolean isCaseSensitive) {
        if (str.startsWith(File.separator) != pattern.startsWith(File.separator)) {
            return false;
        }
        String[] patDirs = SelectorUtils.tokenizePathAsArray(pattern);
        String[] strDirs = SelectorUtils.tokenizePathAsArray(str);
        return SelectorUtils.matchPatternStart(patDirs, strDirs, isCaseSensitive);
    }

    static boolean matchPatternStart(String[] patDirs, String[] strDirs, boolean isCaseSensitive) {
        String patDir;
        int strIdxStart;
        int patIdxStart = 0;
        int patIdxEnd = patDirs.length - 1;
        int strIdxEnd = strDirs.length - 1;
        for (strIdxStart = 0; patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd && !(patDir = patDirs[patIdxStart]).equals(DEEP_TREE_MATCH); ++patIdxStart, ++strIdxStart) {
            if (SelectorUtils.match(patDir, strDirs[strIdxStart], isCaseSensitive)) continue;
            return false;
        }
        return strIdxStart > strIdxEnd || patIdxStart <= patIdxEnd;
    }

    public static boolean matchPath(String pattern, String str) {
        String[] patDirs = SelectorUtils.tokenizePathAsArray(pattern);
        return SelectorUtils.matchPath(patDirs, SelectorUtils.tokenizePathAsArray(str), true);
    }

    public static boolean matchPath(String pattern, String str, boolean isCaseSensitive) {
        String[] patDirs = SelectorUtils.tokenizePathAsArray(pattern);
        return SelectorUtils.matchPath(patDirs, SelectorUtils.tokenizePathAsArray(str), isCaseSensitive);
    }

    static boolean matchPath(String[] tokenizedPattern, String[] strDirs, boolean isCaseSensitive) {
        String patDir;
        int strIdxStart;
        int patIdxStart = 0;
        int patIdxEnd = tokenizedPattern.length - 1;
        int strIdxEnd = strDirs.length - 1;
        for (strIdxStart = 0; patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd && !(patDir = tokenizedPattern[patIdxStart]).equals(DEEP_TREE_MATCH); ++patIdxStart, ++strIdxStart) {
            if (SelectorUtils.match(patDir, strDirs[strIdxStart], isCaseSensitive)) continue;
            return false;
        }
        if (strIdxStart > strIdxEnd) {
            for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                if (tokenizedPattern[i].equals(DEEP_TREE_MATCH)) continue;
                return false;
            }
            return true;
        }
        if (patIdxStart > patIdxEnd) {
            return false;
        }
        while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd && !(patDir = tokenizedPattern[patIdxEnd]).equals(DEEP_TREE_MATCH)) {
            if (!SelectorUtils.match(patDir, strDirs[strIdxEnd], isCaseSensitive)) {
                return false;
            }
            --patIdxEnd;
            --strIdxEnd;
        }
        if (strIdxStart > strIdxEnd) {
            for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                if (tokenizedPattern[i].equals(DEEP_TREE_MATCH)) continue;
                return false;
            }
            return true;
        }
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int i = patIdxStart + 1; i <= patIdxEnd; ++i) {
                if (!tokenizedPattern[i].equals(DEEP_TREE_MATCH)) continue;
                patIdxTmp = i;
                break;
            }
            if (patIdxTmp == patIdxStart + 1) {
                ++patIdxStart;
                continue;
            }
            int patLength = patIdxTmp - patIdxStart - 1;
            int strLength = strIdxEnd - strIdxStart + 1;
            int foundIdx = -1;
            block6: for (int i = 0; i <= strLength - patLength; ++i) {
                for (int j = 0; j < patLength; ++j) {
                    String subPat = tokenizedPattern[patIdxStart + j + 1];
                    String subStr = strDirs[strIdxStart + i + j];
                    if (!SelectorUtils.match(subPat, subStr, isCaseSensitive)) continue block6;
                }
                foundIdx = strIdxStart + i;
                break;
            }
            if (foundIdx == -1) {
                return false;
            }
            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx + patLength;
        }
        for (int i = patIdxStart; i <= patIdxEnd; ++i) {
            if (DEEP_TREE_MATCH.equals(tokenizedPattern[i])) continue;
            return false;
        }
        return true;
    }

    public static boolean match(String pattern, String str) {
        return SelectorUtils.match(pattern, str, true);
    }

    public static boolean match(String pattern, String str, boolean caseSensitive) {
        char ch;
        char[] patArr = pattern.toCharArray();
        char[] strArr = str.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        boolean containsStar = false;
        for (char ch2 : patArr) {
            if (ch2 != '*') continue;
            containsStar = true;
            break;
        }
        if (!containsStar) {
            if (patIdxEnd != strIdxEnd) {
                return false;
            }
            for (int i = 0; i <= patIdxEnd; ++i) {
                char ch3 = patArr[i];
                if (ch3 == '?' || !SelectorUtils.different(caseSensitive, ch3, strArr[i])) continue;
                return false;
            }
            return true;
        }
        if (patIdxEnd == 0) {
            return true;
        }
        while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
            if (ch != '?' && SelectorUtils.different(caseSensitive, ch, strArr[strIdxStart])) {
                return false;
            }
            ++patIdxStart;
            ++strIdxStart;
        }
        if (strIdxStart > strIdxEnd) {
            return SelectorUtils.allStars(patArr, patIdxStart, patIdxEnd);
        }
        while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
            if (ch != '?' && SelectorUtils.different(caseSensitive, ch, strArr[strIdxEnd])) {
                return false;
            }
            --patIdxEnd;
            --strIdxEnd;
        }
        if (strIdxStart > strIdxEnd) {
            return SelectorUtils.allStars(patArr, patIdxStart, patIdxEnd);
        }
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int i = patIdxStart + 1; i <= patIdxEnd; ++i) {
                if (patArr[i] != '*') continue;
                patIdxTmp = i;
                break;
            }
            if (patIdxTmp == patIdxStart + 1) {
                ++patIdxStart;
                continue;
            }
            int patLength = patIdxTmp - patIdxStart - 1;
            int strLength = strIdxEnd - strIdxStart + 1;
            int foundIdx = -1;
            block6: for (int i = 0; i <= strLength - patLength; ++i) {
                for (int j = 0; j < patLength; ++j) {
                    char ch4 = patArr[patIdxStart + j + 1];
                    if (ch4 != '?' && SelectorUtils.different(caseSensitive, ch4, strArr[strIdxStart + i + j])) continue block6;
                }
                foundIdx = strIdxStart + i;
                break;
            }
            if (foundIdx == -1) {
                return false;
            }
            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx + patLength;
        }
        return SelectorUtils.allStars(patArr, patIdxStart, patIdxEnd);
    }

    private static boolean allStars(char[] chars, int start, int end) {
        for (int i = start; i <= end; ++i) {
            if (chars[i] == '*') continue;
            return false;
        }
        return true;
    }

    private static boolean different(boolean caseSensitive, char ch, char other) {
        return caseSensitive ? ch != other : Character.toUpperCase(ch) != Character.toUpperCase(other);
    }

    public static Vector<String> tokenizePath(String path) {
        return SelectorUtils.tokenizePath(path, File.separator);
    }

    public static Vector<String> tokenizePath(String path, String separator) {
        Vector<String> ret = new Vector<String>();
        if (FileUtils.isAbsolutePath(path)) {
            String[] s = FILE_UTILS.dissect(path);
            ret.add(s[0]);
            path = s[1];
        }
        StringTokenizer st = new StringTokenizer(path, separator);
        while (st.hasMoreTokens()) {
            ret.addElement(st.nextToken());
        }
        return ret;
    }

    static String[] tokenizePathAsArray(String path) {
        String root = null;
        if (FileUtils.isAbsolutePath(path)) {
            String[] s = FILE_UTILS.dissect(path);
            root = s[0];
            path = s[1];
        }
        char sep = File.separatorChar;
        int start = 0;
        int len = path.length();
        int count = 0;
        for (int pos = 0; pos < len; ++pos) {
            if (path.charAt(pos) != sep) continue;
            if (pos != start) {
                ++count;
            }
            start = pos + 1;
        }
        if (len != start) {
            ++count;
        }
        String[] l = new String[count + (root == null ? 0 : 1)];
        if (root != null) {
            l[0] = root;
            count = 1;
        } else {
            count = 0;
        }
        start = 0;
        for (int pos = 0; pos < len; ++pos) {
            if (path.charAt(pos) != sep) continue;
            if (pos != start) {
                String tok = path.substring(start, pos);
                l[count++] = tok;
            }
            start = pos + 1;
        }
        if (len != start) {
            String tok;
            l[count] = tok = path.substring(start);
        }
        return l;
    }

    public static boolean isOutOfDate(File src, File target, int granularity) {
        return src.exists() && (!target.exists() || src.lastModified() - (long)granularity > target.lastModified());
    }

    public static boolean isOutOfDate(Resource src, Resource target, int granularity) {
        return SelectorUtils.isOutOfDate(src, target, (long)granularity);
    }

    public static boolean isOutOfDate(Resource src, Resource target, long granularity) {
        long sourceLastModified = src.getLastModified();
        long targetLastModified = target.getLastModified();
        return src.isExists() && (sourceLastModified == 0L || targetLastModified == 0L || sourceLastModified - granularity > targetLastModified);
    }

    public static String removeWhitespace(String input) {
        StringBuilder result = new StringBuilder();
        if (input != null) {
            StringTokenizer st = new StringTokenizer(input);
            while (st.hasMoreTokens()) {
                result.append(st.nextToken());
            }
        }
        return result.toString();
    }

    public static boolean hasWildcards(String input) {
        return input.contains("*") || input.contains("?");
    }

    public static String rtrimWildcardTokens(String input) {
        return new TokenizedPattern(input).rtrimWildcardTokens().toString();
    }
}

