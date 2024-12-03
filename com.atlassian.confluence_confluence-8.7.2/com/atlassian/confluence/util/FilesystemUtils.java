/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.HtmlUtil;
import com.google.common.base.CharMatcher;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public final class FilesystemUtils {
    private static final Set<Character> ILLEGAL_WINDOWS_FILE_NAMING_CHARS = Set.of(Character.valueOf('?'), Character.valueOf('\"'), Character.valueOf('/'), Character.valueOf('<'), Character.valueOf('>'), Character.valueOf('*'), Character.valueOf('|'), Character.valueOf(':'), Character.valueOf('\\'));
    private static final Set<Character> ILLEGAL_UNIX_FILE_NAMING_CHARS = Set.of(Character.valueOf('/'), Character.valueOf('%'));
    private static final Set<Character> NOT_RECOMMENDED_NAMING_CHARS = Set.of(Character.valueOf('+'), Character.valueOf('&'), Character.valueOf('['), Character.valueOf(']'), Character.valueOf('^'), Character.valueOf('`'), Character.valueOf('{'), Character.valueOf('}'), Character.valueOf('('), Character.valueOf(')'));
    public static final List<String> FORBIDDEN_PATH_EQUALS = List.of("..");
    public static final List<String> FORBIDDEN_PATH_CONTAINS = List.of("../", "..\\");
    public static final List<String> FORBIDDEN_PATH_ENDINGS = List.of("/..", "\\..");
    private static final Set<String> ILLEGAL_WINDOWS_FILE_NAMES = Set.of("CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9");
    private static final Set<String> ILLEGAL_UNIX_FILE_NAMES = Set.of("");

    public static boolean isSafeTitleForFilesystem(String title) {
        if (StringUtils.isEmpty((CharSequence)title)) {
            return false;
        }
        if (title.length() >= 150) {
            return false;
        }
        if (title.endsWith(" ") || title.endsWith(".")) {
            return false;
        }
        if (!CharMatcher.ascii().matchesAllOf((CharSequence)title)) {
            return false;
        }
        for (int i = 0; i < title.length(); ++i) {
            char c = title.charAt(i);
            if (c < ' ') {
                return false;
            }
            if (ILLEGAL_WINDOWS_FILE_NAMING_CHARS.contains(Character.valueOf(c))) {
                return false;
            }
            if (ILLEGAL_UNIX_FILE_NAMING_CHARS.contains(Character.valueOf(c))) {
                return false;
            }
            if (!NOT_RECOMMENDED_NAMING_CHARS.contains(Character.valueOf(c))) continue;
            return false;
        }
        if (ILLEGAL_WINDOWS_FILE_NAMES.contains(title.toUpperCase())) {
            return false;
        }
        return !ILLEGAL_UNIX_FILE_NAMES.contains(title);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean containsPathTraversal(String str) {
        if (FORBIDDEN_PATH_EQUALS.stream().anyMatch(str::equals)) return true;
        if (FORBIDDEN_PATH_CONTAINS.stream().anyMatch(str::contains)) return true;
        if (!FORBIDDEN_PATH_ENDINGS.stream().anyMatch(str::endsWith)) return false;
        return true;
    }

    public static boolean containsEncodedPathTraversal(String str) {
        return FilesystemUtils.containsPathTraversal(HtmlUtil.loopedUrlDecode(str));
    }

    public static boolean isSafePath(String path) {
        if (Paths.get(path, new String[0]).isAbsolute()) {
            return false;
        }
        Path validatePath = Paths.get("root", path);
        if (validatePath.normalize() != validatePath) {
            return false;
        }
        return !FilesystemUtils.containsPathTraversal(path);
    }

    public static boolean isSafePath(Path path) {
        if (path.isAbsolute()) {
            return false;
        }
        return FilesystemUtils.isSafePath(path.toString());
    }
}

