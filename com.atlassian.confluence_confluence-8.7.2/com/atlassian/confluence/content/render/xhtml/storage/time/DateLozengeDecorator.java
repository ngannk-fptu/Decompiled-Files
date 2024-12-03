/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.Days
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.confluence.content.render.xhtml.storage.time;

import com.atlassian.confluence.content.render.xhtml.model.time.Time;
import com.atlassian.confluence.content.render.xhtml.storage.time.StorageTimeConstants;
import com.atlassian.confluence.content.render.xhtml.storage.time.TimeModelDecorator;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;

public class DateLozengeDecorator
implements TimeModelDecorator {
    private static final int DUE_SOON_NUMBER = 7;
    private final UserPreferencesAccessor userPreferencesAccessor;

    public DateLozengeDecorator(UserPreferencesAccessor userPreferencesAccessor) {
        this.userPreferencesAccessor = userPreferencesAccessor;
    }

    @Override
    public void decorate(Time time) {
        String dateStr = time.getDatetimeString();
        if (StringUtils.isBlank((CharSequence)dateStr)) {
            return;
        }
        DateTime dueDate = this.getDueDateInUserTimezone(dateStr);
        String cssClass = this.getDateStatusValue(dueDate, this.getCurrentDate());
        time.setCssClasses(cssClass);
    }

    DateTime getCurrentDate() {
        return new DateTime();
    }

    private DateTime getDueDateInUserTimezone(String dateStr) {
        ConfluenceUserPreferences confluenceUserPreferences = this.userPreferencesAccessor.getConfluenceUserPreferences(AuthenticatedUserThreadLocal.get());
        com.atlassian.confluence.core.TimeZone timeZone = confluenceUserPreferences.getTimeZone();
        DateTimeZone userTimeZone = DateTimeZone.forTimeZone((TimeZone)timeZone.getWrappedTimeZone());
        DateTimeFormatter df = Time.DATE_TIME_FORMATTER.withZone(userTimeZone);
        return df.parseDateTime(dateStr);
    }

    private String getDateStatusValue(DateTime dueDate, DateTime currentDate) {
        int numberDays = Days.daysBetween((ReadableInstant)currentDate, (ReadableInstant)dueDate).getDays();
        if (numberDays < 0) {
            return StorageTimeConstants.DATE_PAST_CSS_CLASS;
        }
        if (numberDays >= 0 && numberDays < 7) {
            return StorageTimeConstants.DATE_UPCOMING_CSS_CLASS;
        }
        return StorageTimeConstants.DATE_FUTURE_CSS_CLASS;
    }
}

