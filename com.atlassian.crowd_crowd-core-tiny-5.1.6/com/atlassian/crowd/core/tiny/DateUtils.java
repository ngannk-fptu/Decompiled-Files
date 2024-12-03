/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.core.tiny;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);
    private static final long MINUTE_SECONDS = TimeUnit.MINUTES.toSeconds(1L);
    private static final long HOUR_SECONDS = TimeUnit.HOURS.toSeconds(1L);
    private static final long DAY_SECONDS = TimeUnit.DAYS.toSeconds(1L);
    private static final long WEEK_SECONDS = 7L * DAY_SECONDS;
    private final ResourceBundle resourceBundle;

    public DateUtils(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    static String getDurationPretty(long numSecs, ResourceBundle resourceBundle) {
        return DateUtils.getDurationPrettySeconds(numSecs, DAY_SECONDS, WEEK_SECONDS, resourceBundle::getString, false);
    }

    public static String getDurationPrettySecondsResolution(long numSecs, ResourceBundle resourceBundle) {
        return DateUtils.getDurationPrettySeconds(numSecs, DAY_SECONDS, WEEK_SECONDS, resourceBundle::getString, true);
    }

    public static String getDurationPretty(long numSecs, Function<String, String> i18nResolver, boolean secondsResolution) {
        return DateUtils.getDurationPrettySeconds(numSecs, DAY_SECONDS, WEEK_SECONDS, i18nResolver, secondsResolution);
    }

    static String getDurationPrettySeconds(long numSecs, long secondsPerDay, long secondsPerWeek, Function<String, String> resourceBundle, boolean secondsDuration) {
        long secondsPerYear = secondsPerWeek * 52L;
        return DateUtils.getDurationPrettySeconds(numSecs, secondsPerYear, secondsPerDay, secondsPerWeek, resourceBundle, secondsDuration);
    }

    private static String getDurationPrettySeconds(long numSecs, long secondsPerYear, long secondsPerDay, long secondsPerWeek, Function<String, String> resourceBundle, boolean secondResolution) {
        if (numSecs == 0L) {
            if (secondResolution) {
                return "0 " + DateUtils.getText(resourceBundle, "core.dateutils.seconds");
            }
            return "0 " + DateUtils.getText(resourceBundle, "core.dateutils.minutes");
        }
        StringBuilder result = new StringBuilder();
        if (numSecs >= secondsPerYear) {
            long years = numSecs / secondsPerYear;
            result.append(years).append(' ');
            if (years > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.years"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.year"));
            }
            result.append(", ");
            numSecs %= secondsPerYear;
        }
        if (numSecs >= secondsPerWeek) {
            long weeks = numSecs / secondsPerWeek;
            result.append(weeks).append(' ');
            if (weeks > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.weeks"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.week"));
            }
            result.append(", ");
            numSecs %= secondsPerWeek;
        }
        if (numSecs >= secondsPerDay) {
            long days = numSecs / secondsPerDay;
            result.append(days).append(' ');
            if (days > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.days"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.day"));
            }
            result.append(", ");
            numSecs %= secondsPerDay;
        }
        if (numSecs >= HOUR_SECONDS) {
            long hours = numSecs / HOUR_SECONDS;
            result.append(hours).append(' ');
            if (hours > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.hours"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.hour"));
            }
            result.append(", ");
            numSecs %= HOUR_SECONDS;
        }
        if (numSecs >= MINUTE_SECONDS) {
            long minute = numSecs / MINUTE_SECONDS;
            result.append(minute).append(' ');
            if (minute > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.minutes"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.minute"));
            }
            result.append(", ");
            if (secondResolution) {
                numSecs %= MINUTE_SECONDS;
            }
        }
        if (numSecs >= 1L && numSecs < MINUTE_SECONDS) {
            result.append(numSecs).append(' ');
            if (numSecs > 1L) {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.seconds"));
            } else {
                result.append(DateUtils.getText(resourceBundle, "core.dateutils.second"));
            }
            result.append(", ");
        }
        if (result.length() > 2) {
            return result.substring(0, result.length() - 2);
        }
        return result.toString();
    }

    public String formatDurationPretty(long l) {
        return DateUtils.getDurationPretty(l, this.resourceBundle);
    }

    private static String getText(Function<String, String> i18nResolver, String key) {
        try {
            return i18nResolver.apply(key);
        }
        catch (MissingResourceException e) {
            log.error("Key not found in bundle", (Throwable)e);
            return "";
        }
    }
}

