/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="calendarSettingsManager")
public class DefaultCalendarSettingsManager
implements CalendarSettingsManager {
    private static final String PRIVATE_URLS_ENABLED_KEY = "com.atlassian.confluence.extra.calendar3.admin.privateUrlsEnabled";
    private static final String FULL_HOUR_ENABLED_KEY = "com.atlassian.confluence.extra.calendar3.admin.displayTimeFormat";
    private static final String START_DAY_OF_WEEK_KEY = "com.atlassian.confluence.extra.calendar3.admin.startDayOfWeek";
    private static final String SITE_ADMINS_ENABLED_KEY = "com.atlassian.confluence.extra.calendar3.admin.siteAdminsEnabled";
    private static final String DISPLAY_WEEK_NUMBER_KEY = "com.atlassian.confluence.extra.calendar3.admin.displayWeekNumber";
    private static final String SHOW_UPCOMMING_EVENT_BADGE = "com.atlassian.confluence.extra.calendar3.admin.space.showUpcommingEventBadge";
    private static final String CACHE_EXPIRE_CONFIG_KEY = "com.atlassian.confluence.extra.calendar3.admin.cacheExpireConfig";
    private static final String EXCLUDE_SUBSCRIPTIONS_FROM_CONTENT_KEY = "com.atlassian.confluence.extra.calendar3.admin.excludeSubscriptionsFromContent";
    private final PluginSettings settings;

    @Autowired
    public DefaultCalendarSettingsManager(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.settings = pluginSettingsFactory.createGlobalSettings();
    }

    @Override
    public long getCacheExpireTime() {
        String stringConfig = (String)this.settings.get(CACHE_EXPIRE_CONFIG_KEY);
        return NumberUtils.toLong((String)stringConfig, (long)10L);
    }

    @Override
    public void setCacheExpireTime(long expireTime) {
        if (expireTime <= 0L) {
            expireTime = 10L;
        }
        this.settings.put(CACHE_EXPIRE_CONFIG_KEY, (Object)String.valueOf(expireTime));
    }

    @Override
    public boolean isExcludeSubscriptionsFromContent() {
        String excludeSubscriptionsFromContent = (String)StringUtils.defaultIfEmpty((CharSequence)((String)this.settings.get(EXCLUDE_SUBSCRIPTIONS_FROM_CONTENT_KEY)), (CharSequence)Boolean.FALSE.toString());
        return BooleanUtils.toBoolean((String)excludeSubscriptionsFromContent);
    }

    @Override
    public void setExcludeSubscriptionsFromContent(boolean enable) {
        this.settings.put(EXCLUDE_SUBSCRIPTIONS_FROM_CONTENT_KEY, (Object)String.valueOf(enable));
    }

    @Override
    public boolean isShowUpcommingEventBadge() {
        String showUpcommingEventBadge = StringUtils.defaultString((String)((String)this.settings.get(SHOW_UPCOMMING_EVENT_BADGE)), (String)Boolean.TRUE.toString());
        return BooleanUtils.toBoolean((String)showUpcommingEventBadge);
    }

    @Override
    public void setShowUpcommingEventBadge(boolean enable) {
        this.settings.put(SHOW_UPCOMMING_EVENT_BADGE, (Object)String.valueOf(enable));
    }

    @Override
    public void setEnablePrivateUrls(boolean enable) {
        this.settings.put(PRIVATE_URLS_ENABLED_KEY, (Object)String.valueOf(enable));
    }

    @Override
    public boolean arePrivateUrlsEnabled() {
        String privateUrlSetting = StringUtils.defaultString((String)((String)this.settings.get(PRIVATE_URLS_ENABLED_KEY)), (String)Boolean.TRUE.toString());
        return BooleanUtils.toBoolean((String)privateUrlSetting);
    }

    @Override
    public Integer getStartDayOfWeek() {
        String startDayOfWeek = (String)this.settings.get(START_DAY_OF_WEEK_KEY);
        if (startDayOfWeek == null) {
            return null;
        }
        return Integer.valueOf(startDayOfWeek);
    }

    @Override
    public void setStartDayOfWeek(Integer startDayOfWeek) {
        if (startDayOfWeek == null) {
            this.settings.put(START_DAY_OF_WEEK_KEY, null);
        } else {
            if (startDayOfWeek < 1 || startDayOfWeek > 7) {
                throw new IllegalArgumentException("Start day of week should be between 1 and 7.");
            }
            this.settings.put(START_DAY_OF_WEEK_KEY, (Object)startDayOfWeek.toString());
        }
    }

    @Override
    public void setDisplayTimeFormat(String displayTimeFormat) {
        this.settings.put(FULL_HOUR_ENABLED_KEY, (Object)displayTimeFormat);
    }

    @Override
    public String getDisplayTimeFormat() {
        return StringUtils.defaultString((String)((String)this.settings.get(FULL_HOUR_ENABLED_KEY)), (String)"displayTimeFormat12");
    }

    @Override
    public void setDisplayWeekNumber(boolean enable) {
        this.settings.put(DISPLAY_WEEK_NUMBER_KEY, (Object)String.valueOf(enable));
    }

    @Override
    public boolean getDisplayWeekNumber() {
        String displayWeekNumber = StringUtils.defaultString((String)((String)this.settings.get(DISPLAY_WEEK_NUMBER_KEY)), (String)Boolean.FALSE.toString());
        return BooleanUtils.toBoolean((String)displayWeekNumber);
    }

    @Override
    public int getMaxEventsToDisplayPerCalendar() {
        return Integer.getInteger("com.atlassian.confluence.extra.calendar3.display.events.calendar.maxpercalendar", 300);
    }

    @Override
    public void setEnableSiteAdmins(boolean enable) {
        this.settings.put(SITE_ADMINS_ENABLED_KEY, (Object)String.valueOf(enable));
    }

    @Override
    public boolean areSiteAdminsEnabled() {
        String siteAdminsSetting = StringUtils.defaultString((String)((String)this.settings.get(SITE_ADMINS_ENABLED_KEY)), (String)Boolean.FALSE.toString());
        return BooleanUtils.toBoolean((String)siteAdminsSetting);
    }
}

