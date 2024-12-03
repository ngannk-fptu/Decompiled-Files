/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import org.apache.commons.lang.StringUtils;

public class Ical4jIoUtil {
    private static final Pattern CONTENT_TYPE_CHARSET_PATTERN = Pattern.compile("(?i)^(.*?;\\s*){1,1}charset\\s*=\\s*(.+)$");
    private static final String DEFAULT_CHARSET = "ISO-8859-1";

    private Ical4jIoUtil() {
    }

    public static CalendarBuilder newCalendarBuilder() {
        return new CalendarBuilder();
    }

    public static CalendarOutputter newCalendarOutputter() {
        return new CalendarOutputter(false);
    }

    public static String getContentTypeCharset(String contentTypeHeader) {
        return Ical4jIoUtil.getContentTypeCharset(contentTypeHeader, null);
    }

    public static String getContentTypeCharset(String contentTypeHeader, String defaultCharset) {
        Matcher contentTypeMatcher;
        if (StringUtils.isNotBlank(contentTypeHeader) && (contentTypeMatcher = CONTENT_TYPE_CHARSET_PATTERN.matcher(contentTypeHeader)).matches()) {
            return contentTypeMatcher.group(2);
        }
        return StringUtils.defaultIfEmpty(defaultCharset, DEFAULT_CHARSET);
    }
}

