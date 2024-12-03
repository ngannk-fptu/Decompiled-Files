/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.columns;

import com.atlassian.confluence.extra.jira.api.services.JiraIssuesDateFormatter;
import com.atlassian.confluence.extra.jira.util.JiraIssueDateUtil;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJiraIssuesDateFormatter
implements JiraIssuesDateFormatter {
    private static final Logger log = LoggerFactory.getLogger(DefaultJiraIssuesDateFormatter.class);
    private static final String STATIC_MODE_DATE_FORMAT = "MMM dd, yyyy";
    private final TimeZoneManager timeZoneManager;

    public DefaultJiraIssuesDateFormatter(TimeZoneManager timeZoneManager) {
        this.timeZoneManager = timeZoneManager;
    }

    @Override
    public String formatDate(Locale userLocale, String dateString) {
        String date = this.reformatDateInUserLocale(dateString, userLocale, STATIC_MODE_DATE_FORMAT);
        if (StringUtils.isEmpty((CharSequence)date)) {
            try {
                date = this.reformatDateInDefaultLocale(dateString, userLocale, STATIC_MODE_DATE_FORMAT);
            }
            catch (DateTimeParseException pe) {
                log.debug(dateString + " cannot be parsed ", (Throwable)pe);
                return dateString;
            }
        }
        return StringUtils.isEmpty((CharSequence)date) ? dateString : date;
    }

    @Override
    public String reformatDateInUserLocale(String value, Locale userLocale, String dateFormat) {
        try {
            return this.reformatDateInUserLocale(value, userLocale, userLocale, dateFormat);
        }
        catch (DateTimeParseException dpe) {
            return null;
        }
    }

    @Override
    public String reformatDateInDefaultLocale(String value, Locale userLocale, String dateFormat) throws DateTimeParseException {
        return this.reformatDateInUserLocale(value, Locale.US, userLocale, dateFormat);
    }

    private String reformatDateInUserLocale(String dateValue, Locale originalUserLocal, Locale targetUserLocal, String dateTimeFormat) {
        DateTimeFormatter desiredDateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat, targetUserLocal).withZone(this.timeZoneManager.getUserTimeZone().toZoneId());
        if (StringUtils.isBlank((CharSequence)dateValue)) {
            return "";
        }
        dateValue = StringUtils.trim((String)dateValue);
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateValue, JiraIssueDateUtil.MAIL_DATE_TIME_FORMATTER.withLocale(originalUserLocal));
            return zonedDateTime.format(desiredDateTimeFormatter);
        }
        catch (DateTimeParseException pe) {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateValue, JiraIssueDateUtil.MAIL_DATE_TIME_3_LETTER_TIME_ZONE_FORMATTER.withLocale(originalUserLocal));
            return zonedDateTime.format(desiredDateTimeFormatter);
        }
    }
}

