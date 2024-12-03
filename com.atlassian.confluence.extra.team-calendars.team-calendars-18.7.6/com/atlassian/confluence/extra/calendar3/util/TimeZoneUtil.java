/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.user.User
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.LocalDate
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.joda.time.format.ISODateTimeFormat
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.user.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VTimeZone;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeZoneUtil {
    private static final Logger LOG = LoggerFactory.getLogger(TimeZoneUtil.class);
    public static final String DEFAULT_TIME_ZONE = "Etc/UTC";
    private static final String ICAL4J_TIMEZONE_CACHE = "ical4j.timezone.cache";
    private static final TimeZoneRegistry timeZoneRegistry = TimeZoneRegistryFactory.getInstance().createRegistry();

    private TimeZoneUtil() {
    }

    public static TimeZone getTimeZone(String timezoneId) {
        Map requestCache = RequestCacheThreadLocal.getRequestCache();
        Map timeZoneThreadLocalCache = (Map)requestCache.computeIfAbsent(ICAL4J_TIMEZONE_CACHE, key -> new HashMap());
        Optional timeZone = timeZoneThreadLocalCache.computeIfAbsent(timezoneId, TimeZoneUtil::getICal4jTimeZoneCopy);
        return timeZone.orElse(null);
    }

    private static Optional<TimeZone> getICal4jTimeZoneCopy(String timezoneId) {
        TimeZone timeZoneRegistryTimeZone = timeZoneRegistry.getTimeZone(timezoneId);
        if (timeZoneRegistryTimeZone == null) {
            LOG.debug("Timezone registry has no TimeZone for id: {}", (Object)timezoneId);
            return Optional.empty();
        }
        try {
            TimeZone timeZone = new TimeZone((VTimeZone)timeZoneRegistryTimeZone.getVTimeZone().copy());
            return Optional.of(timeZone);
        }
        catch (Exception e) {
            LOG.debug("Exception occurred when retrieving timezone {} from iCal4J timezone registry", (Object)timezoneId, (Object)e);
            return Optional.empty();
        }
    }

    public static DateTime tryParseDateTimeStringForEventEdit(LocaleManager localeManager, String date, String time, DateTimeZone userTimeZone, String timePattern) {
        try {
            if (StringUtils.isEmpty(date)) {
                LOG.debug("Could not parse Date for null or empty date string: {}", (Object)date);
                return null;
            }
            DateTime parsedDate = TimeZoneUtil.getDateFormatter(localeManager).withZone(DateTimeZone.UTC).parseDateTime(date);
            if (StringUtils.isNotBlank(time)) {
                DateTime parsedTime = TimeZoneUtil.getTimeFormatter(localeManager, timePattern).withZone(userTimeZone).parseDateTime(time);
                parsedDate = parsedDate.withZoneRetainFields(userTimeZone).withHourOfDay(parsedTime.getHourOfDay()).withMinuteOfHour(parsedTime.getMinuteOfHour());
            }
            if (parsedDate.year().get() < 1900) {
                return null;
            }
            return parsedDate;
        }
        catch (Exception iae) {
            LOG.warn("Unable to parse date " + date + (String)(StringUtils.isNotBlank(time) ? " time " + time : ""));
            LOG.debug("Exception when parsing date:", (Throwable)iae);
            return null;
        }
    }

    public static LocalDate tryParseBasicDateStringForEventEdit(String date) {
        try {
            if (StringUtils.isEmpty(date)) {
                LOG.debug("Could not parse Date for null or empty date string: {}", (Object)date);
                return null;
            }
            LocalDate localDate = ISODateTimeFormat.basicDate().parseLocalDate(date);
            if (localDate.year().get() < 1900) {
                return null;
            }
            return localDate;
        }
        catch (Exception iae) {
            LOG.warn("Unable to parse date {}", (Object)date);
            LOG.debug("Exception when parsing date:", (Throwable)iae);
            return null;
        }
    }

    public static DateTimeFormatter getDateFormatter(LocaleManager localeManager, DateTimeZone userTimeZone) {
        return TimeZoneUtil.getDateFormatter(localeManager).withZone(userTimeZone);
    }

    public static DateTimeFormatter getDateFormatter(LocaleManager localeManager) {
        return DateTimeFormat.mediumDate().withLocale(localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    public static DateTimeFormatter getTimeFormatter(LocaleManager localeManager, String pattern) {
        return DateTimeFormat.forPattern((String)pattern).withLocale(localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    public static DateTimeFormatter getTimeFormatter(LocaleManager localeManager, DateTimeZone userTimeZone, String pattern) {
        return TimeZoneUtil.getTimeFormatter(localeManager, pattern).withZone(userTimeZone);
    }
}

