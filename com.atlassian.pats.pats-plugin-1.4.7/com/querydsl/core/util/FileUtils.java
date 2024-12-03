/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.util;

import java.io.File;
import java.io.IOException;

public final class FileUtils {
    public static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                FileUtils.delete(f);
            }
        }
        if ((file.isDirectory() || file.isFile()) && !file.delete()) {
            throw new IllegalStateException("Deletion of " + file.getPath() + " failed");
        }
    }

    private FileUtils() {
    }
}

