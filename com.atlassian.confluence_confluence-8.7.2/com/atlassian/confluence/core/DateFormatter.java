/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.TimeZone;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.spring.container.ContainerManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    private final TimeZone timeZone;
    private final DateFormat dateFormat;
    private final DateFormat dateTimeFormat;
    private final DateFormat timeFormat;
    private final DateFormat blogDateFormat;
    private final DateFormat serverDateFormat;
    private final DateFormat serverDateTimeFormat;
    private final String blogPostDateFormat;
    private final String blogPostTimeFormat;
    private LocaleManager localeManager;

    public DateFormatter(TimeZone timeZone, FormatSettingsManager formatSettingsManager, LocaleManager localeManager) {
        this.localeManager = localeManager;
        this.timeZone = timeZone;
        this.dateFormat = this.createFormat(formatSettingsManager.getDateFormat());
        this.dateTimeFormat = this.createFormat(formatSettingsManager.getDateTimeFormat());
        this.timeFormat = this.createFormat(formatSettingsManager.getTimeFormat());
        this.blogDateFormat = this.createServerFormat("MMM dd, yyyy");
        this.serverDateFormat = this.createServerFormat(formatSettingsManager.getDateFormat());
        this.serverDateTimeFormat = this.createServerFormat(formatSettingsManager.getDateTimeFormat());
        this.blogPostDateFormat = formatSettingsManager.getEditorBlogPostDate();
        this.blogPostTimeFormat = formatSettingsManager.getEditorBlogPostTime();
    }

    public DateFormatter(java.util.TimeZone timeZone, FormatSettingsManager formatSettingsManager, LocaleManager localeManager) {
        this(TimeZone.getInstance(timeZone), formatSettingsManager, localeManager);
    }

    public String format(Date date) {
        return this.formatWith(this.dateFormat, date);
    }

    public String formatDateTime(Date date) {
        return this.formatWith(this.dateTimeFormat, date);
    }

    public String formatDateTime(Instant instant) {
        return this.formatDateTime(Date.from(instant));
    }

    public String formatTime(Date date) {
        return this.formatWith(this.timeFormat, date);
    }

    public String formatTimeMedium(Date date) {
        return this.formatWith(DateFormat.getTimeInstance(2, this.getCurrentUserLocale()), date);
    }

    public String formatDateFull(Date date) {
        DateFormat format = DateFormat.getDateInstance(0, this.getCurrentUserLocale());
        format.setLenient(false);
        format.setTimeZone(this.timeZone.getWrappedTimeZone());
        return this.formatWith(format, date);
    }

    public String format(int formatOption, Date date) {
        DateFormat format = DateFormat.getDateInstance(formatOption, this.getCurrentUserLocale());
        format.setLenient(false);
        format.setTimeZone(this.timeZone.getWrappedTimeZone());
        return this.formatWith(format, date);
    }

    public String formatServerDate(Date date) {
        return this.formatWith(this.serverDateFormat, date);
    }

    public String formatServerDateWithUserLocale(int formatOption, Date date) {
        DateFormat format = DateFormat.getDateInstance(formatOption, this.getCurrentUserLocale());
        format.setLenient(false);
        return this.formatWith(format, date);
    }

    public String formatServerDateTime(Date date) {
        return this.formatWith(this.serverDateTimeFormat, date);
    }

    public String formatBlogDate(Date date) {
        return this.formatWith(this.blogDateFormat, date);
    }

    public String formatServerDateFull(Date date) {
        DateFormat format = DateFormat.getDateInstance(0, this.getCurrentUserLocale());
        format.setLenient(false);
        format.setTimeZone(TimeZone.getDefault().getWrappedTimeZone());
        return this.formatWith(format, date);
    }

    public String formatGivenString(String formatString, Date date) {
        DateFormat format = this.createFormat(formatString);
        return this.formatWith(format, date);
    }

    public String getCurrentDateTime() {
        return this.formatDateTime(new Date());
    }

    public String getCurrentDate() {
        return this.format(new Date());
    }

    public String getDateForBlogPost(Date date) {
        DateFormat format = this.createFormat(this.blogPostDateFormat);
        return this.formatWith(format, date);
    }

    public String getTimeForBlogPost(Date date) {
        DateFormat format = this.createFormat(this.blogPostTimeFormat);
        return this.formatWith(format, date);
    }

    private String formatWith(DateFormat format, Date date) {
        try {
            return format.format(date);
        }
        catch (Exception e) {
            return "";
        }
    }

    private DateFormat createFormat(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, this.getCurrentUserLocale());
        format.setLenient(false);
        format.setTimeZone(this.timeZone.getWrappedTimeZone());
        return format;
    }

    private DateFormat createServerFormat(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, this.getCurrentUserLocale());
        format.setLenient(false);
        format.setTimeZone(TimeZone.getDefault().getWrappedTimeZone());
        return format;
    }

    private Locale getCurrentUserLocale() {
        Locale locale;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (this.localeManager == null) {
            this.localeManager = (LocaleManager)ContainerManager.getComponent((String)"localeManager");
        }
        if ((locale = this.localeManager.getLocale(user)) != null) {
            return locale;
        }
        return Locale.getDefault();
    }

    public Calendar getCalendar() {
        return Calendar.getInstance(this.timeZone.getWrappedTimeZone());
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public static String formatMillis(long millis) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.S");
        format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String result = format.format(new Date(millis));
        result = result.replaceFirst("^00:", "");
        return result;
    }
}

