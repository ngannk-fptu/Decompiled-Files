/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.util;

import com.atlassian.confluence.extra.jira.api.services.JiraIssuesDateFormatter;
import com.atlassian.confluence.extra.jira.util.JiraIssueUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraIssueDateUtil {
    private static final Logger log = LoggerFactory.getLogger(JiraIssueUtil.class);
    public static final String MAIL_DATE_TIME_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
    public static final DateTimeFormatter MAIL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z");
    public static final String MAIL_DATE_TIME_FORMAT_3_LETTER_TIME_ZONE = "EEE, d MMM yyyy HH:mm:ss Z (z)";
    public static final DateTimeFormatter MAIL_DATE_TIME_3_LETTER_TIME_ZONE_FORMATTER = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z (z)");
    public static final String DATE_VALUE_FORMAT = "dd/MMM/yy";
    public static final DateTimeFormatter DATE_TIME_VALUE_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yy");

    public static String applyDateFixes(JiraIssuesDateFormatter jiraIssuesDateFormatter, String dateValueWithoutTimeZone, String dateValueWithTimeZone, Locale userLocal) {
        if (StringUtils.isBlank((CharSequence)dateValueWithoutTimeZone)) {
            log.debug("There is nothing to fix because the due date is null");
            return "";
        }
        if (StringUtils.isBlank((CharSequence)dateValueWithTimeZone)) {
            String dateValueWithLocaleFix = JiraIssueDateUtil.applyDateLocaleFix(jiraIssuesDateFormatter, dateValueWithoutTimeZone, userLocal);
            log.debug("The provided due date is: " + dateValueWithoutTimeZone);
            log.debug("We don't have a created timestamp, therefore only applying the locale fix: " + dateValueWithLocaleFix);
            return dateValueWithLocaleFix;
        }
        String dateValueWithTXFix = JiraIssueDateUtil.applyDateTimezoneFix(dateValueWithoutTimeZone, dateValueWithTimeZone, userLocal);
        String dateValueWithLocaleTXFix = JiraIssueDateUtil.applyDateLocaleFix(jiraIssuesDateFormatter, dateValueWithTXFix, userLocal);
        log.debug("The provided due date is: " + dateValueWithoutTimeZone);
        log.debug("The due date with the corrected timezone is: " + dateValueWithTXFix);
        log.debug("The due date with the corrected timezone and locale is: " + dateValueWithLocaleTXFix);
        return dateValueWithLocaleTXFix;
    }

    public static String applyDateLocaleFix(JiraIssuesDateFormatter jiraIssuesDateFormatter, String dateValue, Locale userLocal) {
        String convertedDate = jiraIssuesDateFormatter.reformatDateInUserLocale(dateValue, userLocal, DATE_VALUE_FORMAT);
        if (StringUtils.isNotEmpty((CharSequence)convertedDate)) {
            return convertedDate;
        }
        try {
            return jiraIssuesDateFormatter.reformatDateInDefaultLocale(dateValue, userLocal, DATE_VALUE_FORMAT);
        }
        catch (DateTimeParseException pe) {
            log.debug("Unable to parse the due date into a date", (Throwable)pe);
            return dateValue;
        }
    }

    public static String applyDateTimezoneFix(String dateValueWithoutTimeZone, String dateValueWithTimeZone, Locale userLocal) {
        if (dateValueWithTimeZone == null) {
            return dateValueWithoutTimeZone;
        }
        try {
            DateTimeFormatter mailDateFormatter = DateTimeFormatter.ofPattern(MAIL_DATE_TIME_FORMAT, userLocal);
            ZoneId createdZoneId = ZonedDateTime.parse(dateValueWithTimeZone, mailDateFormatter).getZone();
            LocalDateTime inconsistentDate = LocalDateTime.parse(dateValueWithoutTimeZone, mailDateFormatter);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(inconsistentDate.getYear(), inconsistentDate.getMonthValue(), inconsistentDate.getDayOfMonth(), inconsistentDate.getHour(), inconsistentDate.getMinute(), inconsistentDate.getSecond(), inconsistentDate.getNano(), createdZoneId);
            return zonedDateTime.format(mailDateFormatter);
        }
        catch (Exception pe) {
            log.debug("Unable to parse the due date into a date", (Throwable)pe);
            return dateValueWithoutTimeZone;
        }
    }

    public static boolean isValidDate(String dateValue, Locale userLocal) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_VALUE_FORMAT, userLocal);
            sdf.parse(dateValue);
            return true;
        }
        catch (ParseException pe) {
            log.debug("The provided date string is not a valid date.", (Throwable)pe);
            return false;
        }
    }
}

