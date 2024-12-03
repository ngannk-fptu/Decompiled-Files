/*
 * Decompiled with CFR 0.152.
 */
package oshi.util;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.GlobalConfig;

@ThreadSafe
public final class FileSystemUtil {
    private static final String GLOB_PREFIX = "glob:";
    private static final String REGEX_PREFIX = "regex:";

    private FileSystemUtil() {
    }

    public static boolean isFileStoreExcluded(String path, String volume, List<PathMatcher> pathIncludes, List<PathMatcher> pathExcludes, List<PathMatcher> volumeIncludes, List<PathMatcher> volumeExcludes) {
        Path p = Paths.get(path, new String[0]);
        Path v = Paths.get(volume, new String[0]);
        if (FileSystemUtil.matches(p, pathIncludes) || FileSystemUtil.matches(v, volumeIncludes)) {
            return false;
        }
        return FileSystemUtil.matches(p, pathExcludes) || FileSystemUtil.matches(v, volumeExcludes);
    }

    public static List<PathMatcher> loadAndParseFileSystemConfig(String configPropertyName) {
        String config = GlobalConfig.get(configPropertyName, "");
        return FileSystemUtil.parseFileSystemConfig(config);
    }

    public static List<PathMatcher> parseFileSystemConfig(String config) {
        FileSystem fs = FileSystems.getDefault();
        ArrayList<PathMatcher> patterns = new ArrayList<PathMatcher>();
        for (String item : config.split(",")) {
            if (item.length() <= 0) continue;
            if (!item.startsWith(GLOB_PREFIX) && !item.startsWith(REGEX_PREFIX)) {
                item = GLOB_PREFIX + item;
            }
            patterns.add(fs.getPathMatcher(item));
        }
        return patterns;
    }

    public static boolean matches(Path text, List<PathMatcher> patterns) {
        for (PathMatcher pattern : patterns) {
            if (!pattern.matches(text)) continue;
            return true;
        }
        return false;
    }
}

