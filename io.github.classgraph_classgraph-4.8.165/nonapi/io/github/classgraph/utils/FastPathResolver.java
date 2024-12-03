/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.utils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.VersionFinder;

public final class FastPathResolver {
    private static final Pattern percentMatcher = Pattern.compile("([%][0-9a-fA-F][0-9a-fA-F])+");
    private static final Pattern schemeTwoSlashMatcher = Pattern.compile("^[a-zA-Z+\\-.]+://");
    private static final Pattern schemeOneSlashMatcher = Pattern.compile("^[a-zA-Z+\\-.]+:/");

    private FastPathResolver() {
    }

    private static void translateSeparator(String path, int startIdx, int endIdx, boolean stripFinalSeparator, StringBuilder buf) {
        for (int i = startIdx; i < endIdx; ++i) {
            char c = path.charAt(i);
            if (c == '\\' || c == '/') {
                int prevChar;
                if (i >= endIdx - 1 && stripFinalSeparator) continue;
                int n = prevChar = buf.length() == 0 ? 0 : (int)buf.charAt(buf.length() - 1);
                if (prevChar == 47) continue;
                buf.append('/');
                continue;
            }
            buf.append(c);
        }
    }

    private static int hexCharToInt(char c) {
        return c >= '0' && c <= '9' ? c - 48 : (c >= 'a' && c <= 'f' ? c - 97 + 10 : c - 65 + 10);
    }

    private static void unescapePercentEncoding(String path, int startIdx, int endIdx, StringBuilder buf) {
        if (endIdx - startIdx == 3 && path.charAt(startIdx + 1) == '2' && path.charAt(startIdx + 2) == '0') {
            buf.append(' ');
        } else {
            byte[] bytes = new byte[(endIdx - startIdx) / 3];
            int i = startIdx;
            int j = 0;
            while (i < endIdx) {
                char c1 = path.charAt(i + 1);
                char c2 = path.charAt(i + 2);
                int digit1 = FastPathResolver.hexCharToInt(c1);
                int digit2 = FastPathResolver.hexCharToInt(c2);
                bytes[j] = (byte)(digit1 << 4 | digit2);
                i += 3;
                ++j;
            }
            String str = new String(bytes, StandardCharsets.UTF_8);
            str = str.replace("/", "%2F").replace("\\", "%5C");
            buf.append(str);
        }
    }

    public static String normalizePath(String path, boolean isFileOrJarURL) {
        int prevEndMatchIdx;
        boolean hasPercent;
        boolean bl = hasPercent = path.indexOf(37) >= 0;
        if (!hasPercent && path.indexOf(92) < 0 && !path.endsWith("/")) {
            return path;
        }
        int len = path.length();
        StringBuilder buf = new StringBuilder();
        if (hasPercent && isFileOrJarURL) {
            prevEndMatchIdx = 0;
            Matcher matcher = percentMatcher.matcher(path);
            while (matcher.find()) {
                int startMatchIdx = matcher.start();
                int endMatchIdx = matcher.end();
                FastPathResolver.translateSeparator(path, prevEndMatchIdx, startMatchIdx, false, buf);
                FastPathResolver.unescapePercentEncoding(path, startMatchIdx, endMatchIdx, buf);
                prevEndMatchIdx = endMatchIdx;
            }
        } else {
            FastPathResolver.translateSeparator(path, 0, len, true, buf);
            return buf.toString();
        }
        FastPathResolver.translateSeparator(path, prevEndMatchIdx, len, true, buf);
        return buf.toString();
    }

    public static String resolve(String resolveBasePath, String relativePath) {
        String pathStr;
        boolean matchedPrefix;
        if (relativePath == null || relativePath.isEmpty()) {
            return resolveBasePath == null ? "" : resolveBasePath;
        }
        String prefix = "";
        boolean isAbsolutePath = false;
        boolean isFileOrJarURL = false;
        int startIdx = 0;
        do {
            matchedPrefix = false;
            if (relativePath.regionMatches(true, startIdx, "jar:", 0, 4)) {
                matchedPrefix = true;
                startIdx = 4;
                isFileOrJarURL = true;
                continue;
            }
            if (relativePath.regionMatches(true, startIdx, "http://", 0, 7)) {
                matchedPrefix = true;
                startIdx += 7;
                prefix = prefix + "http://";
                isAbsolutePath = true;
                continue;
            }
            if (relativePath.regionMatches(true, startIdx, "https://", 0, 8)) {
                matchedPrefix = true;
                startIdx += 8;
                prefix = prefix + "https://";
                isAbsolutePath = true;
                continue;
            }
            if (relativePath.regionMatches(true, startIdx, "jrt:", 0, 5)) {
                matchedPrefix = true;
                startIdx += 4;
                prefix = prefix + "jrt:";
                isAbsolutePath = true;
                continue;
            }
            if (relativePath.regionMatches(true, startIdx, "file:", 0, 5)) {
                matchedPrefix = true;
                startIdx += 5;
                isFileOrJarURL = true;
                continue;
            }
            String relPath = startIdx == 0 ? relativePath : relativePath.substring(startIdx);
            Matcher m2 = schemeTwoSlashMatcher.matcher(relPath);
            if (m2.find()) {
                matchedPrefix = true;
                String m2Match = m2.group();
                startIdx += m2Match.length();
                prefix = prefix + m2Match;
                isAbsolutePath = true;
                continue;
            }
            Matcher m1 = schemeOneSlashMatcher.matcher(relPath);
            if (!m1.find()) continue;
            matchedPrefix = true;
            String m1Match = m1.group();
            startIdx += m1Match.length();
            prefix = prefix + m1Match;
            isAbsolutePath = true;
        } while (matchedPrefix);
        if (VersionFinder.OS == VersionFinder.OperatingSystem.Windows) {
            if (relativePath.startsWith("//", startIdx) || relativePath.startsWith("\\\\", startIdx)) {
                startIdx += 2;
                prefix = prefix + "//";
                isAbsolutePath = true;
            } else if (relativePath.length() - startIdx > 2 && Character.isLetter(relativePath.charAt(startIdx)) && relativePath.charAt(startIdx + 1) == ':') {
                isAbsolutePath = true;
            } else if (relativePath.length() - startIdx > 3 && (relativePath.charAt(startIdx) == '/' || relativePath.charAt(startIdx) == '\\') && Character.isLetter(relativePath.charAt(startIdx + 1)) && relativePath.charAt(startIdx + 2) == ':') {
                isAbsolutePath = true;
                ++startIdx;
            }
        }
        if (relativePath.length() - startIdx > 1 && (relativePath.charAt(startIdx) == '/' || relativePath.charAt(startIdx) == '\\')) {
            isAbsolutePath = true;
        }
        if (!(pathStr = FastPathResolver.normalizePath(startIdx == 0 ? relativePath : relativePath.substring(startIdx), isFileOrJarURL)).equals("/")) {
            if (pathStr.endsWith("/")) {
                pathStr = pathStr.substring(0, pathStr.length() - 1);
            }
            if (pathStr.endsWith("!")) {
                pathStr = pathStr.substring(0, pathStr.length() - 1);
            }
            if (pathStr.endsWith("/")) {
                pathStr = pathStr.substring(0, pathStr.length() - 1);
            }
            if (pathStr.isEmpty()) {
                pathStr = "/";
            }
        }
        String pathResolved = isAbsolutePath || resolveBasePath == null || resolveBasePath.isEmpty() ? FileUtils.sanitizeEntryPath(pathStr, false, true) : FileUtils.sanitizeEntryPath(resolveBasePath + (resolveBasePath.endsWith("/") ? "" : "/") + pathStr, false, true);
        return prefix.isEmpty() ? pathResolved : prefix + pathResolved;
    }

    public static String resolve(String pathStr) {
        return FastPathResolver.resolve(null, pathStr);
    }
}

