/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.text.WordUtils
 */
package com.atlassian.confluence.extra.calendar3.model.email;

import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.text.WordUtils;

public class ReminderEventSubCalendarGroup {
    private String subcalendarId;
    private String subcalendarName;
    private String customEventTypeName;
    private String eventTypeNameWithCalendarName;
    private String stopRemindingLinkHtml;
    private List<ReminderEvent> reminderEvents;

    public String getSubcalendarId() {
        return this.subcalendarId;
    }

    public void setSubcalendarId(String subcalendarId) {
        this.subcalendarId = subcalendarId;
    }

    public String getSubcalendarName() {
        return this.subcalendarName;
    }

    public void setSubcalendarName(String subcalendarName) {
        this.subcalendarName = subcalendarName;
    }

    public String getCustomEventTypeName() {
        return this.customEventTypeName;
    }

    public void setCustomEventTypeName(String customEventTypeName) {
        this.customEventTypeName = customEventTypeName;
    }

    public String getEventTypeNameWithCalendarName() {
        return this.eventTypeNameWithCalendarName;
    }

    public void setEventTypeNameWithCalendarName(String eventTypeNameWithCalendarName) {
        this.eventTypeNameWithCalendarName = eventTypeNameWithCalendarName;
    }

    public String getStopRemindingLinkHtml() {
        return this.stopRemindingLinkHtml;
    }

    public void setStopRemindingLinkHtml(String stopRemindingLinkHtml) {
        this.stopRemindingLinkHtml = stopRemindingLinkHtml;
    }

    public List<ReminderEvent> getReminderEvents() {
        return this.reminderEvents;
    }

    public void setReminderEvents(List<ReminderEvent> reminderEvents) {
        this.reminderEvents = reminderEvents;
    }

    public void addReminderEvent(ReminderEvent rawReminderEvent) {
        this.reminderEvents.add(rawReminderEvent);
    }

    public ReminderEventSubCalendarGroup() {
        this.reminderEvents = new ArrayList<ReminderEvent>();
    }

    public ReminderEventSubCalendarGroup(List<ReminderEvent> reminderEvents) {
        this.reminderEvents = reminderEvents;
    }

    public static ReminderEventSubCalendarGroup fromReminderEvent(ReminderEvent rawReminderEvent) {
        ReminderEventSubCalendarGroup group = new ReminderEventSubCalendarGroup();
        group.setSubcalendarId(rawReminderEvent.getSubCalendarId());
        group.setSubcalendarName(WordUtils.capitalize((String)rawReminderEvent.getCalendarName()));
        group.setCustomEventTypeName(WordUtils.capitalize((String)rawReminderEvent.getEventTypeName()));
        group.setEventTypeNameWithCalendarName(rawReminderEvent.getEventTypeNameWithCalendarName());
        group.setStopRemindingLinkHtml(rawReminderEvent.getStopRemindHtml());
        return group;
    }
}

