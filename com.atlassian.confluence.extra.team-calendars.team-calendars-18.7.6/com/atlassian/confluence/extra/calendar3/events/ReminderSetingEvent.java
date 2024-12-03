/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.calendarstore.ReminderSettingCallback;
import com.atlassian.confluence.extra.calendar3.events.BaseSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;

public abstract class ReminderSetingEvent
extends BaseSubCalendarEvent<PersistedSubCalendar> {
    private ReminderSettingCallback.ReminderSettingChange reminderSettingChange;
    int newPeriodInMins;
    int oldPeriodInMins;

    public ReminderSetingEvent(ReminderSettingCallback.ReminderSettingChange reminderSettingChange, ConfluenceUser trigger, PersistedSubCalendar subCalendar) {
        super(reminderSettingChange, trigger, subCalendar);
        this.reminderSettingChange = reminderSettingChange;
        this.setOldPeriodInMins(reminderSettingChange.getOldPeriodInMins());
        this.setNewPeriodInMins(reminderSettingChange.getNewPeriodInMins());
    }

    public ReminderSettingCallback.ReminderSettingChange getReminderSettingChange() {
        return this.reminderSettingChange;
    }

    public void setReminderSettingChange(ReminderSettingCallback.ReminderSettingChange reminderSettingChange) {
        this.reminderSettingChange = reminderSettingChange;
    }

    public int getOldPeriodInMins() {
        return this.oldPeriodInMins;
    }

    public void setOldPeriodInMins(int oldPeriodInMins) {
        this.oldPeriodInMins = oldPeriodInMins;
    }

    public int getNewPeriodInMins() {
        return this.newPeriodInMins;
    }

    public void setNewPeriodInMins(int newPeriodInMins) {
        this.newPeriodInMins = newPeriodInMins;
    }
}

