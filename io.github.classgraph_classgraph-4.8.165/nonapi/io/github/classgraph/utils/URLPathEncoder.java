/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import nonapi.io.github.classgraph.utils.VersionFinder;

public final class URLPathEncoder {
    private static boolean[] safe;
    private static final char[] HEXADECIMAL;
    private static final String[] SCHEME_PREFIXES;

    private URLPathEncoder() {
    }

    private static void unescapeChars(String str, boolean isQuery, ByteArrayOutputStream buf) {
        if (str.isEmpty()) {
            return;
        }
        int len = str.length();
        for (int chrIdx = 0; chrIdx < len; ++chrIdx) {
            char c = str.charAt(chrIdx);
            if (c == '%') {
                int digit2;
                char c1;
                if (chrIdx > len - 3) continue;
                int digit1 = (c1 = str.charAt(++chrIdx)) >= '0' && c1 <= '9' ? c1 - 48 : (c1 >= 'a' && c1 <= 'f' ? c1 - 97 + 10 : (c1 >= 'A' && c1 <= 'F' ? c1 - 65 + 10 : -1));
                char c2 = str.charAt(++chrIdx);
                int n = c2 >= '0' && c2 <= '9' ? c2 - 48 : (c2 >= 'a' && c2 <= 'f' ? c2 - 97 + 10 : (digit2 = c2 >= 'A' && c2 <= 'F' ? c2 - 65 + 10 : -1));
                if (digit1 < 0 || digit2 < 0) {
                    try {
                        buf.write(str.substring(chrIdx - 2, chrIdx + 1).getBytes(StandardCharsets.UTF_8));
                    }
                    catch (IOException iOException) {}
                    continue;
                }
                buf.write((byte)(digit1 << 4 | digit2));
                continue;
            }
            if (isQuery && c == '+') {
                buf.write(32);
                continue;
            }
            if (c <= '\u007f') {
                buf.write((byte)c);
                continue;
            }
            try {
                buf.write(Character.toString(c).getBytes(StandardCharsets.UTF_8));
                continue;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public static String decodePath(String str) {
        int queryIdx = str.indexOf(63);
        String partBeforeQuery = queryIdx < 0 ? str : str.substring(0, queryIdx);
        String partFromQuery = queryIdx < 0 ? "" : str.substring(queryIdx);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        URLPathEncoder.unescapeChars(partBeforeQuery, false, buf);
        URLPathEncoder.unescapeChars(partFromQuery, true, buf);
        return new String(buf.toByteArray(), StandardCharsets.UTF_8);
    }

    public static String encodePath(String path) {
        int validColonPrefixLen = 0;
        for (String scheme : SCHEME_PREFIXES) {
            if (!path.startsWith(scheme)) continue;
            validColonPrefixLen = scheme.length();
            break;
        }
        if (VersionFinder.OS == VersionFinder.OperatingSystem.Windows) {
            int i = validColonPrefixLen;
            if (i < path.length() && path.charAt(i) == '/') {
                ++i;
            }
            if (i < path.length() - 1 && Character.isLetter(path.charAt(i)) && path.charAt(i + 1) == ':') {
                validColonPrefixLen = i + 2;
            }
        }
        byte[] pathBytes = path.getBytes(StandardCharsets.UTF_8);
        StringBuilder encodedPath = new StringBuilder(pathBytes.length * 3);
        for (int i = 0; i < pathBytes.length; ++i) {
            byte pathByte = pathBytes[i];
            int b = pathByte & 0xFF;
            if (safe[b] || b == 58 && i < validColonPrefixLen) {
                encodedPath.append((char)b);
                continue;
            }
            encodedPath.append('%');
            encodedPath.append(HEXADECIMAL[(b & 0xF0) >> 4]);
            encodedPath.append(HEXADECIMAL[b & 0xF]);
        }
        return encodedPath.toString();
    }

    public static String normalizeURLPath(String urlPath) {
        String urlPathNormalized = urlPath;
        if (!(urlPathNormalized.startsWith("jrt:") || urlPathNormalized.startsWith("http://") || urlPathNormalized.startsWith("https://"))) {
            if (urlPathNormalized.startsWith("jar:")) {
                urlPathNormalized = urlPathNormalized.substring(4);
            }
            if (urlPathNormalized.startsWith("file:")) {
                urlPathNormalized = urlPathNormalized.substring(4);
            }
            String windowsDrivePrefix = "";
            if (VersionFinder.OS == VersionFinder.OperatingSystem.Windows) {
                if (urlPathNormalized.length() >= 2 && Character.isLetter(urlPathNormalized.charAt(0)) && urlPathNormalized.charAt(1) == ':') {
                    windowsDrivePrefix = urlPathNormalized.substring(0, 2);
                    urlPathNormalized = urlPathNormalized.substring(2);
                } else if (urlPathNormalized.length() >= 3 && urlPathNormalized.charAt(0) == '/' && Character.isLetter(urlPathNormalized.charAt(1)) && urlPathNormalized.charAt(2) == ':') {
                    windowsDrivePrefix = urlPathNormalized.substring(1, 3);
                    urlPathNormalized = urlPathNormalized.substring(3);
                }
            }
            urlPathNormalized = urlPathNormalized.replace("/!", "!").replace("!/", "!").replace("!", "!/");
            urlPathNormalized = windowsDrivePrefix.isEmpty() ? (urlPathNormalized.startsWith("/") ? "file:" + urlPathNormalized : "file:/" + urlPathNormalized) : "file:/" + windowsDrivePrefix + (urlPathNormalized.startsWith("/") ? urlPathNormalized : "/" + urlPathNormalized);
            if (urlPathNormalized.contains("!") && !urlPathNormalized.startsWith("jar:")) {
                urlPathNormalized = "jar:" + urlPathNormalized;
            }
        }
        return URLPathEncoder.encodePath(urlPathNormalized);
    }

    static {
        int i;
        safe = new boolean[256];
        for (i = 97; i <= 122; ++i) {
            URLPathEncoder.safe[i] = true;
        }
        for (i = 65; i <= 90; ++i) {
            URLPathEncoder.safe[i] = true;
        }
        for (i = 48; i <= 57; ++i) {
            URLPathEncoder.safe[i] = true;
        }
        URLPathEncoder.safe[43] = true;
        URLPathEncoder.safe[46] = true;
        URLPathEncoder.safe[95] = true;
        URLPathEncoder.safe[45] = true;
        URLPathEncoder.safe[36] = true;
        URLPathEncoder.safe[44] = true;
        URLPathEncoder.safe[41] = true;
        URLPathEncoder.safe[40] = true;
        URLPathEncoder.safe[39] = true;
        URLPathEncoder.safe[42] = true;
        URLPathEncoder.safe[33] = true;
        URLPathEncoder.safe[47] = true;
        HEXADECIMAL = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        SCHEME_PREFIXES = new String[]{"jrt:", "file:", "jar:file:", "jar:", "http:", "https:"};
    }
}

