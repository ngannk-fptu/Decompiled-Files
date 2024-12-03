/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.scheduler.config.IntervalScheduleInfo
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.config.Schedule$Type
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.troubleshooting.stp.scheduler;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.scheduler.config.IntervalScheduleInfo;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.troubleshooting.stp.scheduler.ScheduleFactory;
import com.google.common.base.Preconditions;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;

public class TaskSettingsStore {
    static final String PROP_ENABLED = "enabled";
    static final String PROP_FREQUENCY = "frequency";
    static final String PROP_START_TIME = "startTime";
    static final String PROP_CC_RECIPIENTS = "ccRecipients";
    private final PluginSettings pluginSettings;
    private final String prefix;
    private final ScheduleFactory scheduleFactory;

    public TaskSettingsStore(String taskId, PluginSettings pluginSettings, ScheduleFactory scheduleFactory) {
        this.pluginSettings = pluginSettings;
        this.prefix = taskId + ".";
        this.scheduleFactory = scheduleFactory;
    }

    public void deleteProperty(String propertyName) {
        this.pluginSettings.remove(this.prefix + propertyName);
    }

    public Object getProperty(String propertyName) {
        return this.pluginSettings.get(this.prefix + propertyName);
    }

    public Schedule getSchedule() {
        String[] tokens;
        String frequencyString = this.getStringProperty(PROP_FREQUENCY);
        String startTimeString = this.getStringProperty(PROP_START_TIME);
        if (frequencyString == null && startTimeString == null) {
            return null;
        }
        int startHour = 0;
        int startMinute = 0;
        long intervalMillis = StringUtils.isEmpty((CharSequence)frequencyString) || !StringUtils.isNumeric((CharSequence)frequencyString) ? TimeUnit.DAYS.toMillis(1L) : Long.parseLong(frequencyString);
        if (StringUtils.isNotEmpty((CharSequence)startTimeString) && (tokens = StringUtils.split((String)startTimeString, (char)':')).length == 2 && StringUtils.isNumeric((CharSequence)tokens[0]) && StringUtils.isNumeric((CharSequence)tokens[1])) {
            startHour = Integer.parseInt(tokens[0]);
            startMinute = Integer.parseInt(tokens[1]);
        }
        return this.scheduleFactory.createSchedule(intervalMillis, startHour, startMinute);
    }

    public void setSchedule(Schedule schedule) {
        if (schedule == null) {
            this.deleteProperty(PROP_FREQUENCY);
            this.deleteProperty(PROP_START_TIME);
        } else {
            Preconditions.checkArgument((schedule.getType() == Schedule.Type.INTERVAL ? 1 : 0) != 0, (Object)"only interval schedules are supported");
            IntervalScheduleInfo info = schedule.getIntervalScheduleInfo();
            this.setProperty(PROP_FREQUENCY, String.valueOf(info.getIntervalInMillis()));
            if (info.getFirstRunTime() != null) {
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(info.getFirstRunTime());
                this.setProperty(PROP_START_TIME, cal.get(11) + ":" + cal.get(12));
            } else {
                this.deleteProperty(PROP_START_TIME);
            }
        }
    }

    public boolean isEnabled() {
        String enabledString = this.getStringProperty(PROP_ENABLED);
        return enabledString != null && Boolean.parseBoolean(enabledString);
    }

    public void setEnabled(boolean enabled) {
        this.setProperty(PROP_ENABLED, String.valueOf(enabled));
    }

    public String getStringProperty(String propertyName) {
        Object value = this.getProperty(propertyName);
        return value == null ? null : String.valueOf(value);
    }

    public void setProperty(String propertyName, Object value) {
        this.pluginSettings.put(this.prefix + propertyName, value);
    }

    public void clear() {
        this.pluginSettings.remove(this.prefix + PROP_ENABLED);
        this.pluginSettings.remove(this.prefix + PROP_FREQUENCY);
        this.pluginSettings.remove(this.prefix + PROP_START_TIME);
        this.pluginSettings.remove(this.prefix + PROP_CC_RECIPIENTS);
    }
}

