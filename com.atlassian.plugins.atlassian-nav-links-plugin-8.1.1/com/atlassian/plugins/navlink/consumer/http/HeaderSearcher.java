/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.time.DateFormatUtils
 *  org.apache.http.Header
 *  org.apache.http.HeaderElement
 *  org.apache.http.HttpResponse
 */
package com.atlassian.plugins.navlink.consumer.http;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import javax.annotation.Nullable;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;

public class HeaderSearcher {
    private static final String[] ACCEPTED_DATETIME_FORMATS = new String[]{DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern()};
    private final HttpResponse httpResponse;

    public HeaderSearcher(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Nullable
    public String findFirstHeaderValue(String headerFieldName) {
        Header firstHeader = this.httpResponse.getFirstHeader(headerFieldName);
        return firstHeader != null ? firstHeader.getValue() : null;
    }

    @Nullable
    public Long findFirstHeaderValueAsLong(String headerFieldName) {
        String headerValue = this.findFirstHeaderValue(headerFieldName);
        return headerValue != null ? this.parseLong(headerValue) : null;
    }

    @Nullable
    public Long findFirstHeaderValueAsDateInMillis(String headerFieldName) {
        String headerValue = this.findFirstHeaderValue(headerFieldName);
        return headerValue != null ? this.parseDate(headerValue) : null;
    }

    @Nullable
    public Long findFirstHeaderElementAsLong(String headerFieldName, String headerElementName) {
        Header firstHeader = this.httpResponse.getFirstHeader(headerFieldName);
        if (firstHeader != null) {
            String headerElement = this.findHeaderElement(firstHeader.getElements(), headerElementName);
            return headerElement != null ? this.parseLong(headerElement) : null;
        }
        return null;
    }

    @Nullable
    private String findHeaderElement(HeaderElement[] elements, String headerElementName) {
        for (HeaderElement element : elements) {
            if (!element.getName().equalsIgnoreCase(headerElementName)) continue;
            return element.getValue();
        }
        return null;
    }

    @Nullable
    private Long parseLong(String longValue) {
        try {
            return Long.parseLong(longValue);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    private Long parseDate(String dateValue) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(ACCEPTED_DATETIME_FORMATS[0], Locale.US);
            return Instant.from(dtf.parse(dateValue)).toEpochMilli();
        }
        catch (DateTimeParseException e) {
            return null;
        }
    }
}

