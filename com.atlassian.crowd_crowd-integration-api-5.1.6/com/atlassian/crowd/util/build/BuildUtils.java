/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.util.build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildUtils {
    public static final String BUILD_VERSION = "5.1.6";
    public static final String BUILD_NUMBER = "1895";
    public static final String BUILD_DATE = "2023-11-13";
    private static final Logger log = LoggerFactory.getLogger(BuildUtils.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static Date parsedDate = null;

    public static String getBuildVersion() {
        return BUILD_VERSION;
    }

    public static String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public static String getVersion() {
        return new StringBuffer().append(BUILD_VERSION).append(" (Build:#").append(BUILD_NUMBER).append(" - ").append(BUILD_DATE).append(") ").toString();
    }

    public static Date getCurrentBuildDate() {
        if (parsedDate == null) {
            try {
                parsedDate = formatter.parse(BUILD_DATE);
            }
            catch (ParseException e) {
                log.error("Cannot Parse date: 2023-11-13.  Returning null for date");
            }
        }
        return parsedDate;
    }

    public static int getCurrentBuildYear() {
        return Integer.parseInt(BUILD_DATE.substring(0, 4));
    }
}

