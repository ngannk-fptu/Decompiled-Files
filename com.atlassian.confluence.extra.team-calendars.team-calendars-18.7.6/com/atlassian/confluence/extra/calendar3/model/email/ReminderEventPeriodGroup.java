/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.model.email;

import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.email.ReminderEventSubCalendarGroup;
import java.util.ArrayList;
import java.util.List;

public class ReminderEventPeriodGroup {
    private String groupKey;
    private String parentCalendarId;
    private String subCalendarId;
    private String eventDatePart;
    private String eventMonthPart;
    private String eventDayOfWeek;
    private String reminderPeriodForDisplay;
    private long eventPeriod;
    private String eventTypeNameWithCalendarName;
    private String stopRemindingLinkHtml;
    private String eventStartDateLongFormatForDisplay;
    private List<ReminderEventSubCalendarGroup> reminderEventSubCalendarGroups;

    public String getGroupKey() {
        return this.groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getParentCalendarId() {
        return this.parentCalendarId;
    }

    public void setParentCalendarId(String parentCalendarId) {
        this.parentCalendarId = parentCalendarId;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public String getEventDatePart() {
        return this.eventDatePart;
    }

    public void setEventDatePart(String eventDatePart) {
        this.eventDatePart = eventDatePart;
    }

    public String getEventMonthPart() {
        return this.eventMonthPart;
    }

    public void setEventMonthPart(String eventMonthPart) {
        this.eventMonthPart = eventMonthPart;
    }

    public String getEventDayOfWeek() {
        return this.eventDayOfWeek;
    }

    public void setEventDayOfWeek(String eventDayOfWeek) {
        this.eventDayOfWeek = eventDayOfWeek;
    }

    public String getReminderPeriodForDisplay() {
        return this.reminderPeriodForDisplay;
    }

    public void setReminderPeriodForDisplay(String reminderPeriodForDisplay) {
        this.reminderPeriodForDisplay = reminderPeriodForDisplay;
    }

    public long getEventPeriod() {
        return this.eventPeriod;
    }

    public void setEventPeriod(long eventPeriod) {
        this.eventPeriod = eventPeriod;
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

    public String getEventStartDateLongFormatForDisplay() {
        return this.eventStartDateLongFormatForDisplay;
    }

    public void setEventStartDateLongFormatForDisplay(String eventStartDateLongFormatForDisplay) {
        this.eventStartDateLongFormatForDisplay = eventStartDateLongFormatForDisplay;
    }

    public int getTotalEventsPerPeriodGroup() {
        int totalEventsPerPeriodGroup = 0;
        if (this.reminderEventSubCalendarGroups != null) {
            for (ReminderEventSubCalendarGroup subCalendarGroup : this.reminderEventSubCalendarGroups) {
                totalEventsPerPeriodGroup += subCalendarGroup.getReminderEvents() != null ? subCalendarGroup.getReminderEvents().size() : 0;
            }
        }
        return totalEventsPerPeriodGroup;
    }

    public List<ReminderEventSubCalendarGroup> getReminderEventSubCalendarGroups() {
        return this.reminderEventSubCalendarGroups;
    }

    public void setReminderEventSubCalendarGroups(List<ReminderEventSubCalendarGroup> reminderEventSubCalendarGroups) {
        this.reminderEventSubCalendarGroups = reminderEventSubCalendarGroups;
    }

    public void addReminderEventSubCalendarGroup(ReminderEventSubCalendarGroup group) {
        this.reminderEventSubCalendarGroups.add(group);
    }

    public ReminderEventSubCalendarGroup addReminderEventSubCalendarGroup(ReminderEvent rawReminderEvent) {
        ReminderEventSubCalendarGroup group = ReminderEventSubCalendarGroup.fromReminderEvent(rawReminderEvent);
        this.reminderEventSubCalendarGroups.add(group);
        return group;
    }

    public ReminderEventPeriodGroup() {
        this.reminderEventSubCalendarGroups = new ArrayList<ReminderEventSubCalendarGroup>();
    }

    public ReminderEventPeriodGroup(List<ReminderEventSubCalendarGroup> reminderEventSubCalendarGroups) {
        this.reminderEventSubCalendarGroups = reminderEventSubCalendarGroups;
    }

    public ReminderEventSubCalendarGroup getLastReminderEventSubCalendarGroup() {
        if (null == this.reminderEventSubCalendarGroups && this.reminderEventSubCalendarGroups.size() == 0) {
            return null;
        }
        return this.reminderEventSubCalendarGroups.get(this.reminderEventSubCalendarGroups.size() - 1);
    }

    public static ReminderEventPeriodGroup fromReminderEvent(ReminderEvent rawReminderEvent) {
        ReminderEventPeriodGroup group = new ReminderEventPeriodGroup();
        group.setParentCalendarId(rawReminderEvent.getParentCalendarId());
        group.setSubCalendarId(rawReminderEvent.getSubCalendarId());
        group.setEventPeriod(rawReminderEvent.getPeriod());
        group.setGroupKey(String.format("%s-%s", rawReminderEvent.getPeriod(), rawReminderEvent.getSubCalendarId()));
        group.setEventTypeNameWithCalendarName(rawReminderEvent.getEventTypeNameWithCalendarName());
        group.setStopRemindingLinkHtml(rawReminderEvent.getStopRemindHtml());
        group.setEventDayOfWeek(rawReminderEvent.getEventStartDateDayOfWeekForDisplay());
        group.setEventDatePart(rawReminderEvent.getEventStartDatePartForDisplay());
        group.setEventMonthPart(rawReminderEvent.getEventStartMonthPartForDisplay());
        group.setReminderPeriodForDisplay(rawReminderEvent.getReminderPeriodForDisplay());
        group.setEventStartDateLongFormatForDisplay(rawReminderEvent.getEventStartDateLongFormatForDisplay());
        return group;
    }

    public boolean isSameGroupKey(long eventPeriod, String subCalendarId) {
        return this.getGroupKey().equals(String.format("%s-%s", eventPeriod, subCalendarId));
    }
}

