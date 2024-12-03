/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.zip;

import com.atlassian.troubleshooting.spring.TomcatLogsSupportZipBundleBeans;
import java.util.regex.Pattern;

public final class ZipFileConstants {
    public static final String RECORDING_FILE_EXTENSION = ".jfr";
    public static final Pattern TOMCAT_ACCESS_LOG_PATTERN = TomcatLogsSupportZipBundleBeans.TOMCAT_ACCESS_LOG_PATTERN;

    private ZipFileConstants() {
    }
}

