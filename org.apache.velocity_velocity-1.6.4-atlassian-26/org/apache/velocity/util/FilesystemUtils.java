/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class FilesystemUtils {
    public static final List<String> FORBIDDEN_PATH_EQUALS = Collections.singletonList("..");
    public static final List<String> FORBIDDEN_PATH_CONTAINS = Arrays.asList("../", "..\\");
    public static final List<String> FORBIDDEN_PATH_ENDINGS = Arrays.asList("/..", "\\..");

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

