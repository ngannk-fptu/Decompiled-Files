/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

public class DataUtils {
    public static final String SUFFIX_ZIP = ".zip";
    public static final String SUFFIX_XML = ".xml";

    public static String getXmlFilename(String filename) {
        if (filename.toLowerCase().endsWith(SUFFIX_ZIP)) {
            return DataUtils.getXmlFilename(filename.substring(0, filename.length() - 4));
        }
        if (!filename.toLowerCase().endsWith(SUFFIX_XML)) {
            filename = filename + SUFFIX_XML;
        }
        return filename;
    }

    public static String getZipFilename(String filename) {
        if (filename.toLowerCase().endsWith(SUFFIX_XML)) {
            return DataUtils.getZipFilename(filename.substring(0, filename.length() - 4));
        }
        if (!filename.toLowerCase().endsWith(SUFFIX_ZIP)) {
            filename = filename + SUFFIX_ZIP;
        }
        return filename;
    }
}

