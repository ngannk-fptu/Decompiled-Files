/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3;

public interface CalendarSettingsManager {
    public static final String DISPLAY_TIME_FORMAT_24 = "displayTimeFormat24";
    public static final String DEFAULT_DISPLAY_TIME_FORMAT = "displayTimeFormat12";
    public static final String PATTERN_TIME_24H = "H:mm";
    public static final String PATTERN_TIME_AM_PM = "h:mm a";

    public long getCacheExpireTime();

    public void setCacheExpireTime(long var1);

    public boolean isShowUpcommingEventBadge();

    public void setShowUpcommingEventBadge(boolean var1);

    public void setEnablePrivateUrls(boolean var1);

    public boolean arePrivateUrlsEnabled();

    default public boolean isExcludeSubscriptionsFromContent() {
        return false;
    }

    default public void setExcludeSubscriptionsFromContent(boolean enable) {
    }

    public Integer getStartDayOfWeek();

    public void setStartDayOfWeek(Integer var1);

    public void setDisplayTimeFormat(String var1);

    public String getDisplayTimeFormat();

    public void setDisplayWeekNumber(boolean var1);

    public boolean getDisplayWeekNumber();

    public int getMaxEventsToDisplayPerCalendar();

    public void setEnableSiteAdmins(boolean var1);

    public boolean areSiteAdminsEnabled();

    default public boolean isTimeFormat24Hour() {
        return DISPLAY_TIME_FORMAT_24.equals(this.getDisplayTimeFormat());
    }
}

