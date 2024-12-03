/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.archivers.zip.ZipArchiveEntry
 *  org.apache.commons.compress.archivers.zip.ZipFile
 */
package org.apache.poi.openxml4j.util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.util.ZipArchiveThresholdInputStream;

public class ZipSecureFile
extends ZipFile {
    private static final Logger LOG = LogManager.getLogger(ZipSecureFile.class);
    static double MIN_INFLATE_RATIO = 0.01;
    static final long DEFAULT_MAX_ENTRY_SIZE = 0xFFFFFFFFL;
    static long MAX_ENTRY_SIZE = 0xFFFFFFFFL;
    static final long DEFAULT_MAX_TEXT_SIZE = 0xA00000L;
    private static long MAX_TEXT_SIZE = 0xA00000L;
    private final String fileName;

    public static void setMinInflateRatio(double ratio) {
        MIN_INFLATE_RATIO = ratio;
    }

    public static double getMinInflateRatio() {
        return MIN_INFLATE_RATIO;
    }

    public static void setMaxEntrySize(long maxEntrySize) {
        if (maxEntrySize < 0L) {
            throw new IllegalArgumentException("Max entry size must be greater than or equal to zero");
        }
        if (maxEntrySize > 0xFFFFFFFFL) {
            LOG.atWarn().log("setting max entry size greater than 4Gb can be risky; set to " + maxEntrySize + " bytes");
        }
        MAX_ENTRY_SIZE = maxEntrySize;
    }

    public static long getMaxEntrySize() {
        return MAX_ENTRY_SIZE;
    }

    public static void setMaxTextSize(long maxTextSize) {
        if (maxTextSize < 0L) {
            throw new IllegalArgumentException("Max text size must be greater than or equal to zero");
        }
        if (maxTextSize > 0xA00000L) {
            LOG.atWarn().log("setting max text size greater than 10485760 can be risky; set to " + maxTextSize + " chars");
        }
        MAX_TEXT_SIZE = maxTextSize;
    }

    public static long getMaxTextSize() {
        return MAX_TEXT_SIZE;
    }

    public ZipSecureFile(File file) throws IOException {
        super(file);
        this.fileName = file.getAbsolutePath();
    }

    public ZipSecureFile(String name) throws IOException {
        super(name);
        this.fileName = new File(name).getAbsolutePath();
    }

    public ZipArchiveThresholdInputStream getInputStream(ZipArchiveEntry entry) throws IOException {
        ZipArchiveThresholdInputStream zatis = new ZipArchiveThresholdInputStream(super.getInputStream(entry));
        zatis.setEntry(entry);
        return zatis;
    }

    public String getName() {
        return this.fileName;
    }
}

