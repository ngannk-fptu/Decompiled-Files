/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Deprecated
public class BackupParserUtil {
    private static final String isoDateFormat = "yyyy-MM-dd";
    private static final String isoTimestampFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String legacyDateFormat = "dd MMMM yyyy";
    private static final String legacyTimestampFormat = "dd MMMM yyyy HH:mm:ss";

    public static Date parseTimestamp(String str) throws ParseException {
        try {
            return new SimpleDateFormat(isoTimestampFormat).parse(str);
        }
        catch (ParseException e) {
            return new SimpleDateFormat(legacyTimestampFormat).parse(str);
        }
    }

    public static Date parseDate(String str) throws ParseException {
        try {
            return new SimpleDateFormat(isoDateFormat).parse(str);
        }
        catch (ParseException e) {
            return new SimpleDateFormat(legacyDateFormat).parse(str);
        }
    }
}

