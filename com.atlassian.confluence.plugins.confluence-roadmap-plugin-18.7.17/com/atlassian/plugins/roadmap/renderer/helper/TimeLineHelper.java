/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.time.DateUtils
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableInstant
 *  org.joda.time.Weeks
 */
package com.atlassian.plugins.roadmap.renderer.helper;

import com.atlassian.plugins.roadmap.models.Timeline;
import com.atlassian.plugins.roadmap.renderer.beans.TimelinePosition;
import com.atlassian.plugins.roadmap.renderer.beans.TimelinePositionTitle;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.Weeks;

public class TimeLineHelper {
    private static final String MONTH_KEY_PREFIX = "roadmap.editor.timeline.month";
    private static final long MILLISECONDS_A_DAY = 86400000L;
    private static final long MILLISECONDS_A_WEEK = 604800000L;

    public static List<TimelinePosition> getColumnPosition(Timeline timeline) {
        int month = TimeLineHelper.getMonth(timeline.getStartDate());
        ArrayList<TimelinePosition> roadmapColumns = new ArrayList<TimelinePosition>();
        int numberOfColumn = timeline.getDisplayOption() == Timeline.DisplayOption.MONTH ? TimeLineHelper.getNumberOfColumnInMonthTimeline(timeline) : TimeLineHelper.getNumberOfColumnInWeekTimeline(timeline);
        for (int i = 0; i < numberOfColumn; ++i) {
            TimelinePosition timelinePosition = new TimelinePosition(i, 1.0, month);
            roadmapColumns.add(timelinePosition);
        }
        return roadmapColumns;
    }

    public static TimelinePosition calculateTimelinePosition(Timeline timeline, Date date) {
        if (timeline.getDisplayOption() == Timeline.DisplayOption.MONTH) {
            return TimeLineHelper.calculateMonthTimelinePosition(timeline, date);
        }
        return TimeLineHelper.calculateWeekTimelinePosition(timeline, date);
    }

    private static TimelinePosition calculateMonthTimelinePosition(Timeline timeline, Date date) {
        int startMonthNumber = TimeLineHelper.getMonth(timeline.getStartDate());
        Date firstDateOfMonth = DateUtils.truncate((Date)date, (int)2);
        int numberOfMonths = TimeLineHelper.monthDiff(timeline.getStartDate(), date);
        long numberMilisecondFromStartMonth = date.getTime() - firstDateOfMonth.getTime();
        long numberMilisecondInMonth = TimeLineHelper.getMillisecondInMonth(date);
        Double offset = (double)numberMilisecondFromStartMonth / (double)numberMilisecondInMonth;
        return new TimelinePosition(numberOfMonths, offset, startMonthNumber);
    }

    private static TimelinePosition calculateWeekTimelinePosition(Timeline timeline, Date date) {
        Date timelineStartWeek = TimeLineHelper.getStartDateOfWeek(timeline.getStartDate());
        Date startWeek = TimeLineHelper.getStartDateOfWeek(date);
        int numberOfWeeks = TimeLineHelper.weekDiff(timelineStartWeek, startWeek);
        long numberMilisecondFromStartWeek = date.getTime() - startWeek.getTime();
        Double offset = (double)numberMilisecondFromStartWeek / 6.048E8;
        return new TimelinePosition(numberOfWeeks, offset, numberOfWeeks);
    }

    public static TimelinePositionTitle getPositionTitle(Timeline timeline, TimelinePosition timelinePosition, I18nResolver i18n) {
        if (timeline.getDisplayOption() == Timeline.DisplayOption.MONTH) {
            return TimeLineHelper.getMonthPositionTitle(timeline, timelinePosition, i18n);
        }
        return TimeLineHelper.getWeekPositionTitle(timeline, timelinePosition, i18n);
    }

    private static TimelinePositionTitle getMonthPositionTitle(Timeline timeline, TimelinePosition timelinePosition, I18nResolver i18n) {
        int month = (timelinePosition.getColumn() + timelinePosition.getColumnOffset()) % 12;
        String monthString = i18n.getText(MONTH_KEY_PREFIX + String.valueOf(month + 1));
        Calendar startTime = TimeLineHelper.getCalendar(timeline.getStartDate());
        int year = startTime.get(1) + (timelinePosition.getColumn() + timelinePosition.getColumnOffset()) / 12;
        return new TimelinePositionTitle(monthString, String.valueOf(year));
    }

    private static TimelinePositionTitle getWeekPositionTitle(Timeline timeline, TimelinePosition timelinePosition, I18nResolver i18n) {
        Date startDate = TimeLineHelper.getStartDateOfWeek(timeline.getStartDate());
        Calendar calendar = TimeLineHelper.getCalendar(startDate);
        calendar.add(5, timelinePosition.getColumn() * 7);
        String timelineTitle = TimeLineHelper.getWeekTimelineTitle(calendar, i18n);
        int year = calendar.get(1);
        return new TimelinePositionTitle(timelineTitle.toString(), String.valueOf(year));
    }

    public static int getNumberOfColumnInTimeline(Timeline timeline) {
        if (timeline.getDisplayOption() == Timeline.DisplayOption.MONTH) {
            return TimeLineHelper.getNumberOfColumnInMonthTimeline(timeline);
        }
        return TimeLineHelper.getNumberOfColumnInWeekTimeline(timeline);
    }

    private static int getNumberOfColumnInMonthTimeline(Timeline timeline) {
        return Math.max(TimeLineHelper.monthDiff(timeline.getStartDate(), timeline.getEndDate()) + 1, 1);
    }

    private static int getNumberOfColumnInWeekTimeline(Timeline timeline) {
        Date startDate = TimeLineHelper.getStartDateOfWeek(timeline.getStartDate());
        Date endDate = TimeLineHelper.getEndDateOfWeek(timeline.getEndDate());
        return TimeLineHelper.weekDiff(startDate, endDate) + 1;
    }

    private static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    private static int getMonth(Date date) {
        Calendar startTime = TimeLineHelper.getCalendar(date);
        return startTime.get(2);
    }

    private static long getMillisecondInMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int numberOfDay = calendar.getActualMaximum(5);
        return (long)numberOfDay * 86400000L;
    }

    private static int monthDiff(Date startDate, Date endDate) {
        Calendar startTime = TimeLineHelper.getCalendar(startDate);
        Calendar endTime = TimeLineHelper.getCalendar(endDate);
        int diffYear = endTime.get(1) - startTime.get(1);
        int diffMonth = diffYear * 12 + endTime.get(2) - startTime.get(2);
        return diffMonth;
    }

    private static int weekDiff(Date date1, Date date2) {
        DateTime dateTime1 = new DateTime((Object)date1);
        DateTime dateTime2 = new DateTime((Object)date2);
        return Weeks.weeksBetween((ReadableInstant)dateTime1, (ReadableInstant)dateTime2).getWeeks();
    }

    private static Date getStartDateOfWeek(Date date) {
        Calendar calendar = TimeLineHelper.getCalendar(date);
        int day = calendar.get(7);
        day = day == 1 ? -6 : 2 - day;
        calendar.add(5, day);
        calendar.set(11, calendar.getActualMinimum(11));
        calendar.set(12, calendar.getActualMinimum(12));
        calendar.set(13, calendar.getActualMinimum(13));
        calendar.set(14, calendar.getActualMinimum(14));
        return calendar.getTime();
    }

    private static Date getEndDateOfWeek(Date date) {
        Calendar calendar = TimeLineHelper.getCalendar(date);
        int day = calendar.get(7);
        day = day == 1 ? 0 : 8 - day;
        calendar.add(5, day);
        return calendar.getTime();
    }

    private static String getWeekTimelineTitle(Calendar calendar, I18nResolver i18n) {
        StringBuilder weekTimelineTitle = new StringBuilder();
        weekTimelineTitle.append(String.format("%02d", calendar.get(5)));
        weekTimelineTitle.append("-");
        weekTimelineTitle.append(i18n.getText(MONTH_KEY_PREFIX + String.valueOf(calendar.get(2) + 1)));
        return weekTimelineTitle.toString();
    }
}

