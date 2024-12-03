/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

public final class UriUtil {
    private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final Pattern PATTERN_EXCLAMATION_MARK = Pattern.compile("!/");
    private static final Pattern PATTERN_CARET = Pattern.compile("\\^/");
    private static final Pattern PATTERN_ASTERISK = Pattern.compile("\\*/");
    private static final Pattern PATTERN_CUSTOM;
    private static final String REPLACE_CUSTOM;
    private static final String WAR_SEPARATOR;

    private UriUtil() {
    }

    private static boolean isSchemeChar(char c) {
        return Character.isLetterOrDigit(c) || c == '+' || c == '-' || c == '.';
    }

    public static boolean hasScheme(CharSequence uri) {
        int len = uri.length();
        for (int i = 0; i < len; ++i) {
            char c = uri.charAt(i);
            if (c == ':') {
                return i > 0;
            }
            if (UriUtil.isSchemeChar(c)) continue;
            return false;
        }
        return false;
    }

    public static URL buildJarUrl(File jarFile) throws MalformedURLException {
        return UriUtil.buildJarUrl(jarFile, null);
    }

    public static URL buildJarUrl(File jarFile, String entryPath) throws MalformedURLException {
        return UriUtil.buildJarUrl(jarFile.toURI().toString(), entryPath);
    }

    public static URL buildJarUrl(String fileUrlString) throws MalformedURLException {
        return UriUtil.buildJarUrl(fileUrlString, null);
    }

    public static URL buildJarUrl(String fileUrlString, String entryPath) throws MalformedURLException {
        String safeString = UriUtil.makeSafeForJarUrl(fileUrlString);
        StringBuilder sb = new StringBuilder();
        sb.append(safeString);
        sb.append("!/");
        if (entryPath != null) {
            sb.append(UriUtil.makeSafeForJarUrl(entryPath));
        }
        return new URL("jar", null, -1, sb.toString());
    }

    public static URL buildJarSafeUrl(File file) throws MalformedURLException {
        String safe = UriUtil.makeSafeForJarUrl(file.toURI().toString());
        return new URL(safe);
    }

    private static String makeSafeForJarUrl(String input) {
        String tmp = PATTERN_EXCLAMATION_MARK.matcher(input).replaceAll("%21/");
        tmp = PATTERN_CARET.matcher(tmp).replaceAll("%5e/");
        tmp = PATTERN_ASTERISK.matcher(tmp).replaceAll("%2a/");
        if (PATTERN_CUSTOM != null) {
            tmp = PATTERN_CUSTOM.matcher(tmp).replaceAll(REPLACE_CUSTOM);
        }
        return tmp;
    }

    public static URL warToJar(URL warUrl) throws MalformedURLException {
        String file = warUrl.getFile();
        if (file.contains("*/")) {
            file = file.replaceFirst("\\*/", "!/");
        } else if (file.contains("^/")) {
            file = file.replaceFirst("\\^/", "!/");
        } else if (PATTERN_CUSTOM != null) {
            file = file.replaceFirst(PATTERN_CUSTOM.pattern(), "!/");
        }
        return new URL("jar", warUrl.getHost(), warUrl.getPort(), file);
    }

    public static String getWarSeparator() {
        return WAR_SEPARATOR;
    }

    public static boolean isAbsoluteURI(String path) {
        int i;
        if (path.startsWith("file:/")) {
            return true;
        }
        for (i = 0; i < path.length() && UriUtil.isSchemeChar(path.charAt(i)); ++i) {
        }
        if (i == 0) {
            return false;
        }
        return i + 2 < path.length() && path.charAt(i++) == ':' && path.charAt(i++) == '/' && path.charAt(i) == '/';
    }

    public static URI resolve(URI base, String target) throws MalformedURLException, URISyntaxException {
        if (base.getScheme().equals("jar")) {
            URI fileUri = new URI(base.getSchemeSpecificPart());
            URI fileUriResolved = fileUri.resolve(target);
            return new URI("jar:" + fileUriResolved.toString());
        }
        return base.resolve(target);
    }

    static {
        String custom = System.getProperty("org.apache.tomcat.util.buf.UriUtil.WAR_SEPARATOR");
        if (custom == null) {
            WAR_SEPARATOR = "*/";
            PATTERN_CUSTOM = null;
            REPLACE_CUSTOM = null;
        } else {
            byte[] ba;
            WAR_SEPARATOR = custom + "/";
            PATTERN_CUSTOM = Pattern.compile(Pattern.quote(WAR_SEPARATOR));
            StringBuilder sb = new StringBuilder(custom.length() * 3);
            for (byte toEncode : ba = custom.getBytes()) {
                sb.append('%');
                int low = toEncode & 0xF;
                int high = (toEncode & 0xF0) >> 4;
                sb.append(HEX[high]);
                sb.append(HEX[low]);
            }
            REPLACE_CUSTOM = sb.toString();
        }
    }
}

