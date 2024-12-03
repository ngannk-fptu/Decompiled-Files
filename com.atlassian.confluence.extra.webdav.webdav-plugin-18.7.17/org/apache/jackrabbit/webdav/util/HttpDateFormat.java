/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.util;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class HttpDateFormat
extends SimpleDateFormat {
    private static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
    public static final String MODIFICATION_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    public static final String CREATION_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public HttpDateFormat(String pattern) {
        super(pattern, Locale.ENGLISH);
        super.setTimeZone(GMT_TIMEZONE);
    }

    public static HttpDateFormat modificationDateFormat() {
        return new HttpDateFormat(MODIFICATION_DATE_PATTERN);
    }

    public static HttpDateFormat creationDateFormat() {
        return new HttpDateFormat(CREATION_DATE_PATTERN);
    }
}

