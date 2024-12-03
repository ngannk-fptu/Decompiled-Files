/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.core.tiny;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FileSize {
    private static final float KB_SIZE = 1024.0f;
    private static final float MB_SIZE = 1048576.0f;
    private static final String KB = " kB";
    private static final String MB = " MB";

    public static String format(Long filesize) {
        return FileSize.format((long)filesize);
    }

    static String format(long filesize) {
        if ((float)filesize > 1048576.0f) {
            return FileSize.formatMB(filesize);
        }
        if ((float)filesize > 1024.0f) {
            return FileSize.formatKB(filesize);
        }
        return FileSize.formatBytes(filesize);
    }

    private static String formatMB(long filesize) {
        DecimalFormat mbFormat = new DecimalFormat();
        ((NumberFormat)mbFormat).setMinimumIntegerDigits(1);
        ((NumberFormat)mbFormat).setMaximumFractionDigits(2);
        ((NumberFormat)mbFormat).setMinimumFractionDigits(2);
        float mbsize = (float)filesize / 1048576.0f;
        return mbFormat.format(mbsize) + MB;
    }

    private static String formatKB(long filesize) {
        long kbsize = Math.round((float)filesize / 1024.0f);
        return String.valueOf(kbsize) + KB;
    }

    private static String formatBytes(long filesize) {
        DecimalFormat bFormat = new DecimalFormat();
        ((NumberFormat)bFormat).setMinimumIntegerDigits(1);
        ((NumberFormat)bFormat).setMaximumFractionDigits(1);
        ((NumberFormat)bFormat).setMinimumFractionDigits(1);
        float mbsize = (float)filesize / 1024.0f;
        return bFormat.format(mbsize) + KB;
    }
}

