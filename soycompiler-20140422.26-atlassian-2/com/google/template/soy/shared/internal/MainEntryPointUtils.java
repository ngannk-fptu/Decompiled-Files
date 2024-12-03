/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.shared.internal;

import java.io.File;
import javax.annotation.Nullable;

public class MainEntryPointUtils {
    private MainEntryPointUtils() {
    }

    public static String buildFilePath(String filePathFormat, @Nullable String locale, @Nullable String inputFilePath, String inputPathPrefix) {
        String path = filePathFormat;
        if (locale != null) {
            path = path.replace("{LOCALE}", locale);
            path = path.replace("{LOCALE_LOWER_CASE}", locale.toLowerCase().replace('-', '_'));
        }
        path = path.replace("{INPUT_PREFIX}", inputPathPrefix);
        if (inputFilePath != null) {
            inputFilePath = inputFilePath.substring(inputPathPrefix.length());
            int lastSlashIndex = inputFilePath.lastIndexOf(File.separatorChar);
            String directory = inputFilePath.substring(0, lastSlashIndex + 1);
            String fileName = inputFilePath.substring(lastSlashIndex + 1);
            int lastDotIndex = fileName.lastIndexOf(46);
            if (lastDotIndex == -1) {
                lastDotIndex = fileName.length();
            }
            String fileNameNoExt = fileName.substring(0, lastDotIndex);
            path = path.replace("{INPUT_DIRECTORY}", directory);
            path = path.replace("{INPUT_FILE_NAME}", fileName);
            path = path.replace("{INPUT_FILE_NAME_NO_EXT}", fileNameNoExt);
        }
        return path;
    }
}

