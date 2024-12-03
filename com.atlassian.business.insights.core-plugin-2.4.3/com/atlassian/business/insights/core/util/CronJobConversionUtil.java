/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.caesium.cron.parser.CronExpressionParser
 *  com.atlassian.scheduler.cron.CronSyntaxException
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.util;

import com.atlassian.business.insights.core.rest.model.Weekdays;
import com.atlassian.business.insights.core.service.scheduler.ExportScheduleException;
import com.atlassian.business.insights.core.service.scheduler.ScheduleConfig;
import com.atlassian.business.insights.core.util.DateConversionUtil;
import com.atlassian.scheduler.caesium.cron.parser.CronExpressionParser;
import com.atlassian.scheduler.cron.CronSyntaxException;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public final class CronJobConversionUtil {
    private CronJobConversionUtil() {
    }

    public static String buildCronjobConfig(@Nonnull ScheduleConfig config) {
        String repeatTime = config.getTime();
        LocalTime localTime = DateConversionUtil.parseTimeAsLocalTime(repeatTime);
        int hour = localTime.get(ChronoField.HOUR_OF_DAY);
        int minute = localTime.get(ChronoField.MINUTE_OF_HOUR);
        int second = localTime.get(ChronoField.SECOND_OF_MINUTE);
        String dayOfWeeks = config.getDays().stream().map(Weekdays::getAbbreviation).collect(Collectors.joining(",")).toUpperCase();
        return String.format("%d %d %d ? * %s", second, minute, hour, dayOfWeeks);
    }

    public static String validatedCronJobConfig(String cronExpression) {
        try {
            CronExpressionParser.parse((String)cronExpression);
            return cronExpression;
        }
        catch (CronSyntaxException e) {
            throw new ExportScheduleException("Invalid Cronjob expression: " + cronExpression, e);
        }
    }
}

