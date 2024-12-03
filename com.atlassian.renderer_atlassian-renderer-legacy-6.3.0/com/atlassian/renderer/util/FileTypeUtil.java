/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  javax.activation.FileTypeMap
 *  javax.activation.MimetypesFileTypeMap
 */
package com.atlassian.renderer.util;

import com.atlassian.plugin.util.ClassLoaderUtils;
import java.io.File;
import java.io.InputStream;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

public class FileTypeUtil {
    private static FileTypeMap fileTypeMap;

    public static String getContentType(String fileName) {
        return fileTypeMap.getContentType(fileName.toLowerCase());
    }

    public static String getContentType(File file) {
        return fileTypeMap.getContentType(file);
    }

    static {
        InputStream mimeTypesStream = ClassLoaderUtils.getResourceAsStream((String)"mime.types", FileTypeUtil.class);
        fileTypeMap = new MimetypesFileTypeMap(mimeTypesStream);
    }
}

