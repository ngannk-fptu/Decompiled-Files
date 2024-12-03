/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.calendarstore.ReminderSettingCallback;
import com.atlassian.confluence.extra.calendar3.events.ReminderSettingCreated;
import com.atlassian.confluence.extra.calendar3.events.ReminderSettingUpdated;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;

public class DefaultReminderSettingCallback
implements ReminderSettingCallback {
    private final EventPublisher eventPublisher;

    public DefaultReminderSettingCallback(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void createReminderSetting(ReminderSettingCallback.ReminderSettingChange reminderSettingChange, PersistedSubCalendar subCalendar) {
        this.eventPublisher.publish((Object)new ReminderSettingCreated(reminderSettingChange, AuthenticatedUserThreadLocal.get(), subCalendar));
    }

    @Override
    public void updateReminderSetting(ReminderSettingCallback.ReminderSettingChange reminderSettingChange, PersistedSubCalendar subCalendar) {
        this.eventPublisher.publish((Object)new ReminderSettingUpdated(reminderSettingChange, AuthenticatedUserThreadLocal.get(), subCalendar));
    }
}

