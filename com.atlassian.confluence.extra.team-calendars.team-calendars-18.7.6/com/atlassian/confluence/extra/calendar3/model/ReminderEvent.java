/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.joda.time.Period
 *  org.joda.time.ReadableInstant
 *  org.joda.time.ReadablePeriod
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.joda.time.format.PeriodFormatter
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.Invitee;
import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import com.atlassian.confluence.extra.calendar3.model.LightweightPersistentSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ReminderSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.querydsl.DTO.EventDTO;
import com.atlassian.confluence.extra.calendar3.querydsl.DTO.JiraReminderEventDTO;
import java.util.List;
import java.util.Set;
import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement
public class ReminderEvent
implements JsonSerializable,
Cloneable {
    private static final Logger LOG = LoggerFactory.getLogger(ReminderEvent.class);
    private static DateTimeFormatter withoutTime = DateTimeFormat.forPattern((String)"e MMM yyy");
    private static DateTimeFormatter withTime = DateTimeFormat.forPattern((String)"e MMM yyy, HH:mm");
    private Integer eventId;
    private String uidEvent;
    private String subCalendarId;
    private String parentCalendarId;
    private String parentCalendarName;
    private String subscriptionId;
    private String title;
    private String description;
    private long period;
    private long utcStart;
    private long utcEnd;
    private String storeKey;
    private String recurrenceId;
    private String calendarName;
    private String customEventTypeId;
    private String subCalendarTimeZoneId;
    private String eventTypeName;
    private String startDateTimeFollowUserTimeZone;
    private String endDateTimeFollowUserTimeZone;
    private String upComingEventTittle;
    private String inviteesName;
    private List<String> inviteeIds;
    private String eventTypeNameWithCalendarName;
    private String stopRemindHtml;
    private String eventStartDateLongFormatForDisplay;
    private String eventStartTimeForDisplay;
    private String eventEndTimeForDisplay;
    private String reminderPeriodForDisplay;
    private String eventStartDateDayOfWeekForDisplay;
    private String eventStartTimePartForDisplay;
    private String eventStartDatePartForDisplay;
    private String eventStartMonthPartForDisplay;
    private String eventEndTimePartForDisplay;
    private String eventEndDatePartForDisplay;
    private String eventEndMonthPartForDisplay;
    private boolean onlyDisplayTime;
    private boolean isStartDateEqualToTodayDate;
    private String subcalendarPreviewUrl;
    private long eventDurationInDays;
    private String remainingTime;
    private boolean isAllDay;
    private String iconUrl;
    private String mediumIconUrl;
    private DataHandler dataHandler;
    private String eventTypeIconUrl;
    private String ticketId;
    private String jiraIssueLink;
    private String viewJiraIssueText;
    private String userKey;
    private boolean isJiraIssueStoreKey;

    public String getEventStartDateLongFormatForDisplay() {
        return this.eventStartDateLongFormatForDisplay;
    }

    public void setEventStartDateLongFormatForDisplay(String eventStartDateLongFormatForDisplay) {
        this.eventStartDateLongFormatForDisplay = eventStartDateLongFormatForDisplay;
    }

    public boolean isStartDateEqualToTodayDate() {
        return this.isStartDateEqualToTodayDate;
    }

    public void setStartDateEqualToTodayDate(boolean isStartDateEqualToTodayDate) {
        this.isStartDateEqualToTodayDate = isStartDateEqualToTodayDate;
    }

    public boolean isOnlyDisplayTime() {
        return this.onlyDisplayTime;
    }

    public void setOnlyDisplayTime(boolean onlyDisplayTime) {
        this.onlyDisplayTime = onlyDisplayTime;
    }

    public String getSubcalendarPreviewUrl() {
        return this.subcalendarPreviewUrl;
    }

    public void setSubcalendarPreviewUrl(String subcalendarPreviewUrl) {
        this.subcalendarPreviewUrl = subcalendarPreviewUrl;
    }

    public long getEventDurationInDays() {
        return this.eventDurationInDays;
    }

    public void setEventDurationInDays(long eventDurationInDays) {
        this.eventDurationInDays = eventDurationInDays;
    }

    public String getRemainingTime() {
        return this.remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setRemainingTime(DateTime startDateTime, Period remainingTimeBeforeStart, PeriodFormatter periodFormatter) {
        DateTimeFormatter currentFormatter = this.isAllDay() ? withoutTime : withTime;
        StringBuilder builder = new StringBuilder();
        builder.append(currentFormatter.print((ReadableInstant)startDateTime)).append(" (").append(periodFormatter.print((ReadablePeriod)remainingTimeBeforeStart)).append(")");
        this.remainingTime = builder.toString();
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getMediumIconUrl() {
        return this.mediumIconUrl;
    }

    public void setMediumIconUrl(String mediumIconUrl) {
        this.mediumIconUrl = mediumIconUrl;
    }

    public String getEventTypeName() {
        return this.eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public Integer getEventId() {
        return this.eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public String getParentCalendarId() {
        return this.parentCalendarId;
    }

    public void setParentCalendarId(String parentCalendarId) {
        this.parentCalendarId = parentCalendarId;
    }

    public String getParentCalendarName() {
        return this.parentCalendarName;
    }

    public void setParentCalendarName(String parentCalendarName) {
        this.parentCalendarName = parentCalendarName;
    }

    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPeriod() {
        return this.period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public long getUtcStart() {
        return this.utcStart;
    }

    public void setUtcStart(long utcStart) {
        this.utcStart = utcStart;
    }

    public long getUtcEnd() {
        return this.utcEnd;
    }

    public void setUtcEnd(long utcEnd) {
        this.utcEnd = utcEnd;
    }

    public String getStoreKey() {
        return this.storeKey;
    }

    public void setStoreKey(String storeKey) {
        this.storeKey = storeKey;
    }

    public String getRecurrenceId() {
        return this.recurrenceId;
    }

    public void setRecurrenceId(String recurrenceId) {
        this.recurrenceId = recurrenceId;
    }

    public String getCalendarName() {
        return this.calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getCustomEventTypeId() {
        return this.customEventTypeId;
    }

    public void setCustomEventTypeId(String customEventTypeId) {
        this.customEventTypeId = customEventTypeId;
    }

    public String getSubCalendarTimeZoneId() {
        return this.subCalendarTimeZoneId;
    }

    public void setSubCalendarTimeZoneId(String subCalendarTimeZoneId) {
        this.subCalendarTimeZoneId = subCalendarTimeZoneId;
    }

    public String getStartDateTimeFollowUserTimeZone() {
        return this.startDateTimeFollowUserTimeZone;
    }

    public void setStartDateTimeFollowUserTimeZone(String startDateTimeFollowUserTimeZone) {
        this.startDateTimeFollowUserTimeZone = startDateTimeFollowUserTimeZone;
    }

    public String getEndDateTimeFollowUserTimeZone() {
        return this.endDateTimeFollowUserTimeZone;
    }

    public void setEndDateTimeFollowUserTimeZone(String endDateTimeFollowUserTimeZone) {
        this.endDateTimeFollowUserTimeZone = endDateTimeFollowUserTimeZone;
    }

    public String getEventStartTimeForDisplay() {
        return this.eventStartTimeForDisplay;
    }

    public void setEventStartTimeForDisplay(String eventStartTimeForDisplay) {
        this.eventStartTimeForDisplay = eventStartTimeForDisplay;
    }

    public String getEventEndTimeForDisplay() {
        return this.eventEndTimeForDisplay;
    }

    public void setEventEndTimeForDisplay(String eventEndTimeForDisplay) {
        this.eventEndTimeForDisplay = eventEndTimeForDisplay;
    }

    public String getReminderPeriodForDisplay() {
        return this.reminderPeriodForDisplay;
    }

    public void setReminderPeriodForDisplay(String reminderPeriodForDisplay) {
        this.reminderPeriodForDisplay = reminderPeriodForDisplay;
    }

    public String getEventStartTimePartForDisplay() {
        return this.eventStartTimePartForDisplay;
    }

    public void setEventStartTimePartForDisplay(String eventStartTimePartForDisplay) {
        this.eventStartTimePartForDisplay = eventStartTimePartForDisplay;
    }

    public String getEventStartDatePartForDisplay() {
        return this.eventStartDatePartForDisplay;
    }

    public void setEventStartDatePartForDisplay(String eventStartDatePartForDisplay) {
        this.eventStartDatePartForDisplay = eventStartDatePartForDisplay;
    }

    public String getEventStartMonthPartForDisplay() {
        return this.eventStartMonthPartForDisplay;
    }

    public void setEventStartMonthPartForDisplay(String eventStartMonthPartForDisplay) {
        this.eventStartMonthPartForDisplay = eventStartMonthPartForDisplay;
    }

    public String getEventEndTimePartForDisplay() {
        return this.eventEndTimePartForDisplay;
    }

    public void setEventEndTimePartForDisplay(String eventEndTimePartForDisplay) {
        this.eventEndTimePartForDisplay = eventEndTimePartForDisplay;
    }

    public String getEventEndDatePartForDisplay() {
        return this.eventEndDatePartForDisplay;
    }

    public void setEventEndDatePartForDisplay(String eventEndDatePartForDisplay) {
        this.eventEndDatePartForDisplay = eventEndDatePartForDisplay;
    }

    public String getEventEndMonthPartForDisplay() {
        return this.eventEndMonthPartForDisplay;
    }

    public void setEventEndMonthPartForDisplay(String eventEndMonthPartForDisplay) {
        this.eventEndMonthPartForDisplay = eventEndMonthPartForDisplay;
    }

    public String getEventStartDateDayOfWeekForDisplay() {
        return this.eventStartDateDayOfWeekForDisplay;
    }

    public void setEventStartDateDayOfWeekForDisplay(String eventStartDateDayOfWeekForDisplay) {
        this.eventStartDateDayOfWeekForDisplay = eventStartDateDayOfWeekForDisplay;
    }

    public String getUpComingEventTittle() {
        return this.upComingEventTittle;
    }

    public void setUpComingEventTittle(String upComingEventTittle) {
        this.upComingEventTittle = upComingEventTittle;
    }

    public String getInviteesName() {
        return this.inviteesName;
    }

    public void setInviteesName(String inviteesName) {
        this.inviteesName = inviteesName;
    }

    public List<String> getInviteeIds() {
        return this.inviteeIds;
    }

    public void setInviteeIds(List<String> inviteeIds) {
        this.inviteeIds = inviteeIds;
    }

    public String getUidEvent() {
        return this.uidEvent;
    }

    public void setUidEvent(String uidEvent) {
        this.uidEvent = uidEvent;
    }

    public String getEventTypeNameWithCalendarName() {
        return this.eventTypeNameWithCalendarName;
    }

    public void setEventTypeNameWithCalendarName(String eventTypeNameWithCalendarName) {
        this.eventTypeNameWithCalendarName = eventTypeNameWithCalendarName;
    }

    public String getStopRemindHtml() {
        return this.stopRemindHtml;
    }

    public void setStopRemindHtml(String stopRemindHtml) {
        this.stopRemindHtml = stopRemindHtml;
    }

    public boolean isAllDay() {
        return this.isAllDay;
    }

    public void setAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
    }

    public DataHandler getDataHandler() {
        return this.dataHandler;
    }

    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public String getEventTypeIconUrl() {
        return this.eventTypeIconUrl;
    }

    public void setEventTypeIconUrl(String eventTypeIconUrl) {
        this.eventTypeIconUrl = eventTypeIconUrl;
    }

    public String getTicketId() {
        return this.ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getJiraIssueLink() {
        return this.jiraIssueLink;
    }

    public void setJiraIssueLink(String jiraIssueLink) {
        this.jiraIssueLink = jiraIssueLink;
    }

    public String getViewJiraIssueText() {
        return this.viewJiraIssueText;
    }

    public void setViewJiraIssueText(String viewJiraIssueText) {
        this.viewJiraIssueText = viewJiraIssueText;
    }

    public String getUserKey() {
        return this.userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public boolean isJiraIssueStoreKey() {
        return this.isJiraIssueStoreKey;
    }

    public void setJiraIssueStoreKey(boolean isJiraIssueStoreKey) {
        this.isJiraIssueStoreKey = isJiraIssueStoreKey;
    }

    public static ReminderEvent toReminderEvent(EventDTO eventReminderDTO) {
        ReminderEvent reminderEvent = new ReminderEvent();
        reminderEvent.setEventId(eventReminderDTO.getEventId());
        reminderEvent.setSubCalendarId(eventReminderDTO.getSubCalendarId());
        reminderEvent.setPeriod(eventReminderDTO.getPeriod());
        reminderEvent.setStoreKey(eventReminderDTO.getStoreKey());
        reminderEvent.setTitle(eventReminderDTO.getSummary());
        reminderEvent.setDescription(eventReminderDTO.getDescription());
        reminderEvent.setUtcStart(eventReminderDTO.getUtcStart());
        reminderEvent.setUtcEnd(eventReminderDTO.getUtcEnd());
        reminderEvent.setParentCalendarId(eventReminderDTO.getParentSubCalendarId());
        reminderEvent.setParentCalendarName(eventReminderDTO.getParentCalendarName());
        reminderEvent.setCalendarName(eventReminderDTO.getCalendarName());
        reminderEvent.setCustomEventTypeId(eventReminderDTO.getCustomEventTypeId());
        reminderEvent.setSubCalendarTimeZoneId(eventReminderDTO.getSubCalendarTimeZoneId());
        reminderEvent.setAllDay(eventReminderDTO.isAllDay());
        return reminderEvent;
    }

    public static ReminderEvent toReminderEvent(JiraReminderEventDTO jiraReminderEventDTO) {
        ReminderEvent reminderEvent = new ReminderEvent();
        reminderEvent.setEventId(jiraReminderEventDTO.getEventId());
        reminderEvent.setTicketId(jiraReminderEventDTO.getTicketId());
        reminderEvent.setSubCalendarId(jiraReminderEventDTO.getSubCalendarId());
        reminderEvent.setCalendarName(jiraReminderEventDTO.getCalendarName());
        reminderEvent.setParentCalendarId(jiraReminderEventDTO.getParentSubCalendarId());
        reminderEvent.setParentCalendarName(jiraReminderEventDTO.getParentCalendarName());
        reminderEvent.setPeriod(jiraReminderEventDTO.getPeriod());
        reminderEvent.setStoreKey(jiraReminderEventDTO.getStoreKey());
        reminderEvent.setTitle(jiraReminderEventDTO.getSummary());
        reminderEvent.setDescription((String)StringUtils.defaultIfEmpty((CharSequence)jiraReminderEventDTO.getDescription(), (CharSequence)""));
        reminderEvent.setEventTypeName(jiraReminderEventDTO.getEventType());
        reminderEvent.setUtcStart(jiraReminderEventDTO.getUtcStart());
        reminderEvent.setUtcEnd(jiraReminderEventDTO.getUtcEnd());
        reminderEvent.setAllDay(jiraReminderEventDTO.isAllDay());
        reminderEvent.setJiraIssueLink(jiraReminderEventDTO.getJiraIssueLink());
        reminderEvent.setIconUrl(jiraReminderEventDTO.getJiraIssueIconUrl());
        reminderEvent.setUserKey(jiraReminderEventDTO.getUserId());
        return reminderEvent;
    }

    public SubCalendarEvent toLightWeightSubCalendarEvent(Set<Invitee> invitees) {
        LightweightPersistentSubCalendar subCalendar = new LightweightPersistentSubCalendar(this.getSubCalendarId());
        ReminderSubCalendarEvent subCalendarEvent = new ReminderSubCalendarEvent(this);
        subCalendarEvent.setInvitees(invitees);
        subCalendarEvent.setSubCalendar(subCalendar);
        subCalendarEvent.setCustomEventTypeId(this.getCustomEventTypeId());
        return subCalendarEvent;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            thisObject.put("eventId", (Object)this.eventId);
            if (null != this.getRecurrenceId()) {
                thisObject.put("recurId", (Object)this.getRecurrenceId());
            }
            thisObject.put("subCalendarId", (Object)this.subCalendarId);
            thisObject.put("title", (Object)this.title);
            thisObject.put("description", (Object)this.description);
            thisObject.put("period", this.period);
            thisObject.put("utcStart", this.utcStart);
            thisObject.put("utcEnd", this.utcEnd);
            thisObject.put("storeKey", (Object)this.storeKey);
            thisObject.put("calendarName", (Object)this.calendarName);
            thisObject.put("parentCalendarId", (Object)this.parentCalendarId);
            thisObject.put("parentCalendarName", (Object)this.parentCalendarName);
            thisObject.put("customEventTypeId", (Object)this.customEventTypeId);
            thisObject.put("subscriptionId", (Object)this.subscriptionId);
            thisObject.put("allDay", this.isAllDay);
            thisObject.put("userKey", (Object)this.userKey);
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObject;
    }

    public Object clone() {
        ReminderEvent reminderEvent = new ReminderEvent();
        reminderEvent.setEventId(this.getEventId());
        reminderEvent.setUidEvent(this.getUidEvent());
        reminderEvent.setSubCalendarId(this.getSubCalendarId());
        reminderEvent.setParentCalendarId(this.getParentCalendarId());
        reminderEvent.setParentCalendarName(this.getParentCalendarName());
        reminderEvent.setSubscriptionId(this.getSubscriptionId());
        reminderEvent.setTitle(this.getTitle());
        reminderEvent.setDescription(this.getDescription());
        reminderEvent.setPeriod(this.getPeriod());
        reminderEvent.setUtcStart(this.getUtcStart());
        reminderEvent.setUtcEnd(this.getUtcEnd());
        reminderEvent.setStoreKey(this.getStoreKey());
        reminderEvent.setRecurrenceId(this.getRecurrenceId());
        reminderEvent.setCalendarName(this.getCalendarName());
        reminderEvent.setCustomEventTypeId(this.getCustomEventTypeId());
        reminderEvent.setSubCalendarTimeZoneId(this.getSubCalendarTimeZoneId());
        reminderEvent.setEventTypeName(this.getEventTypeName());
        reminderEvent.setStartDateTimeFollowUserTimeZone(this.getStartDateTimeFollowUserTimeZone());
        reminderEvent.setEndDateTimeFollowUserTimeZone(this.getEndDateTimeFollowUserTimeZone());
        reminderEvent.setUpComingEventTittle(this.getUpComingEventTittle());
        reminderEvent.setInviteesName(this.getInviteesName());
        reminderEvent.setInviteeIds(this.getInviteeIds());
        reminderEvent.setStopRemindHtml(this.getStopRemindHtml());
        reminderEvent.setAllDay(this.isAllDay());
        reminderEvent.setEventTypeNameWithCalendarName(this.getEventTypeNameWithCalendarName());
        reminderEvent.setStopRemindHtml(this.getStopRemindHtml());
        reminderEvent.setEventStartDateLongFormatForDisplay(this.getEventStartDateLongFormatForDisplay());
        reminderEvent.setEventEndTimeForDisplay(this.getEventEndTimeForDisplay());
        reminderEvent.setReminderPeriodForDisplay(this.getReminderPeriodForDisplay());
        reminderEvent.setEventStartDateDayOfWeekForDisplay(this.getEventStartDateDayOfWeekForDisplay());
        reminderEvent.setEventStartTimePartForDisplay(this.getEventStartTimePartForDisplay());
        reminderEvent.setEventStartDatePartForDisplay(this.getEventStartDatePartForDisplay());
        reminderEvent.setEventStartMonthPartForDisplay(this.getEventStartMonthPartForDisplay());
        reminderEvent.setEventEndTimePartForDisplay(this.getEventEndTimePartForDisplay());
        reminderEvent.setEventEndDatePartForDisplay(this.getEventEndDatePartForDisplay());
        reminderEvent.setEventEndMonthPartForDisplay(this.getEventEndMonthPartForDisplay());
        reminderEvent.setOnlyDisplayTime(this.isOnlyDisplayTime());
        reminderEvent.setStartDateEqualToTodayDate(this.isStartDateEqualToTodayDate());
        reminderEvent.setSubcalendarPreviewUrl(this.getSubcalendarPreviewUrl());
        reminderEvent.setEventDurationInDays(this.getEventDurationInDays());
        reminderEvent.setRemainingTime(this.getRemainingTime());
        reminderEvent.setIconUrl(this.getIconUrl());
        reminderEvent.setMediumIconUrl(this.getMediumIconUrl());
        reminderEvent.setEventTypeIconUrl(this.getEventTypeIconUrl());
        reminderEvent.setTicketId(this.getTicketId());
        reminderEvent.setJiraIssueLink(this.getJiraIssueLink());
        reminderEvent.setUserKey(this.getUserKey());
        return reminderEvent;
    }
}

