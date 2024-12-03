/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.model.email;

import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.email.ReminderEventPeriodGroup;
import com.atlassian.confluence.extra.calendar3.model.email.ReminderEventSubCalendarGroup;
import java.util.ArrayList;
import java.util.List;

public class ReminderEmailNotification {
    private List<ReminderEventPeriodGroup> reminderEventPeriodGroups = new ArrayList<ReminderEventPeriodGroup>();
    private String reminderNote;

    public List<ReminderEventPeriodGroup> getReminderEventPeriodGroups() {
        return this.reminderEventPeriodGroups;
    }

    public void setReminderEventPeriodGroups(List<ReminderEventPeriodGroup> reminderEventPeriodGroups) {
        this.reminderEventPeriodGroups = reminderEventPeriodGroups;
    }

    public String getReminderNote() {
        return this.reminderNote;
    }

    public void setReminderNote(String reminderNote) {
        this.reminderNote = reminderNote;
    }

    public int getEventCount() {
        if (this.reminderEventPeriodGroups == null || this.reminderEventPeriodGroups.size() == 0) {
            return 0;
        }
        int totalEvent = 0;
        for (ReminderEventPeriodGroup periodGroup : this.reminderEventPeriodGroups) {
            if (periodGroup.getReminderEventSubCalendarGroups() == null || periodGroup.getReminderEventSubCalendarGroups().size() == 0) continue;
            for (ReminderEventSubCalendarGroup subCalendarGroup : periodGroup.getReminderEventSubCalendarGroups()) {
                totalEvent += subCalendarGroup.getReminderEvents() != null ? subCalendarGroup.getReminderEvents().size() : 0;
            }
        }
        return totalEvent;
    }

    public ReminderEventPeriodGroup addReminderEventPeriodGroup(ReminderEvent rawReminderEvent) {
        ReminderEventPeriodGroup group = ReminderEventPeriodGroup.fromReminderEvent(rawReminderEvent);
        this.reminderEventPeriodGroups.add(group);
        return group;
    }
}

