/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import java.util.List;
import javax.ws.rs.FormParam;

public class UpdateEventParam {
    @FormParam(value="originalSubCalendarId")
    String originalSubCalendarId;
    @FormParam(value="subCalendarId")
    String subCalendarId;
    @FormParam(value="originalEventType")
    String originalEventType;
    @FormParam(value="eventType")
    String eventType;
    @FormParam(value="uid")
    String uid;
    @FormParam(value="userTimeZoneId")
    String userTimeZoneId;
    @FormParam(value="originalStartDate")
    String originalStartDate;
    @FormParam(value="what")
    String what;
    @FormParam(value="person")
    List<String> person;
    @FormParam(value="url")
    String url;
    @FormParam(value="dragAndDropUpdate")
    boolean dragAndDropUpdate;
    @FormParam(value="start")
    String startDateDnd;
    @FormParam(value="end")
    String endDateDnd;
    @FormParam(value="startDate")
    String startDate;
    @FormParam(value="startTime")
    String startTime;
    @FormParam(value="endDate")
    String endDate;
    @FormParam(value="endTime")
    String endTime;
    @FormParam(value="isSingleJiraDate")
    boolean isSingleJiraDate;
    @FormParam(value="allDayEvent")
    boolean allDayEvent;
    @FormParam(value="where")
    String where;
    @FormParam(value="description")
    String description;
    @FormParam(value="recurrenceId")
    String recurrenceId;
    @Deprecated
    @FormParam(value="freq")
    String freq;
    @Deprecated
    @FormParam(value="byday")
    String byDay;
    @Deprecated
    @FormParam(value="interval")
    String interval;
    @Deprecated
    @FormParam(value="repeatEnds")
    boolean repeatEnds;
    @FormParam(value="until")
    String until;
    @FormParam(value="rruleStr")
    String rruleStr;
    @FormParam(value="editAllInRecurrenceSeries")
    boolean editAllInRecurrenceSeries;
    @FormParam(value="customEventTypeId")
    String customEventTypeId;
    @FormParam(value="originalCustomEventTypeId")
    String originalCustomEventTypeId;
    @FormParam(value="childSubCalendarId")
    String childSubCalendarId;
    @FormParam(value="confirmRemoveInvalidUsers")
    boolean confirmRemoveInvalidUsers;

    public boolean isAllDayEvent() {
        return this.allDayEvent;
    }

    public void setAllDayEvent(boolean allDayEvent) {
        this.allDayEvent = allDayEvent;
    }

    public String getByDay() {
        return this.byDay;
    }

    public void setByDay(String byDay) {
        this.byDay = byDay;
    }

    public String getChildSubCalendarId() {
        return this.childSubCalendarId;
    }

    public void setChildSubCalendarId(String childSubCalendarId) {
        this.childSubCalendarId = childSubCalendarId;
    }

    public String getCustomEventTypeId() {
        return this.customEventTypeId;
    }

    public void setCustomEventTypeId(String customEventTypeId) {
        this.customEventTypeId = customEventTypeId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDragAndDropUpdate() {
        return this.dragAndDropUpdate;
    }

    public void setDragAndDropUpdate(boolean dragAndDropUpdate) {
        this.dragAndDropUpdate = dragAndDropUpdate;
    }

    public boolean isEditAllInRecurrenceSeries() {
        return this.editAllInRecurrenceSeries;
    }

    public void setEditAllInRecurrenceSeries(boolean editAllInRecurrenceSeries) {
        this.editAllInRecurrenceSeries = editAllInRecurrenceSeries;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDateDnd() {
        return this.endDateDnd;
    }

    public void setEndDateDnd(String endDateDnd) {
        this.endDateDnd = endDateDnd;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getFreq() {
        return this.freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getRruleStr() {
        return this.rruleStr;
    }

    public void setRruleStr(String rruleStr) {
        this.rruleStr = rruleStr;
    }

    public String getInterval() {
        return this.interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getOriginalCustomEventTypeId() {
        return this.originalCustomEventTypeId;
    }

    public void setOriginalCustomEventTypeId(String originalCustomEventTypeId) {
        this.originalCustomEventTypeId = originalCustomEventTypeId;
    }

    public String getOriginalEventType() {
        return this.originalEventType;
    }

    public void setOriginalEventType(String originalEventType) {
        this.originalEventType = originalEventType;
    }

    public String getOriginalStartDate() {
        return this.originalStartDate;
    }

    public void setOriginalStartDate(String originalStartDate) {
        this.originalStartDate = originalStartDate;
    }

    public String getOriginalSubCalendarId() {
        return this.originalSubCalendarId;
    }

    public void setOriginalSubCalendarId(String originalSubCalendarId) {
        this.originalSubCalendarId = originalSubCalendarId;
    }

    public List<String> getPerson() {
        return this.person;
    }

    public void setPerson(List<String> person) {
        this.person = person;
    }

    public String getRecurrenceId() {
        return this.recurrenceId;
    }

    public void setRecurrenceId(String recurrenceId) {
        this.recurrenceId = recurrenceId;
    }

    public boolean isRepeatEnds() {
        return this.repeatEnds;
    }

    public void setRepeatEnds(boolean repeatEnds) {
        this.repeatEnds = repeatEnds;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDateDnd() {
        return this.startDateDnd;
    }

    public void setStartDateDnd(String startDateDnd) {
        this.startDateDnd = startDateDnd;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isSingleJiraDate() {
        return this.isSingleJiraDate;
    }

    public void setSingleJiraDate(boolean singleJiraDate) {
        this.isSingleJiraDate = singleJiraDate;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUntil() {
        return this.until;
    }

    public void setUntil(String until) {
        this.until = until;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserTimeZoneId() {
        return this.userTimeZoneId;
    }

    public void setUserTimeZoneId(String userTimeZoneId) {
        this.userTimeZoneId = userTimeZoneId;
    }

    public String getWhat() {
        return this.what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getWhere() {
        return this.where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public boolean getConfirmRemoveInvalidUsers() {
        return this.confirmRemoveInvalidUsers;
    }

    public void setConfirmRemoveInvalidUsers(boolean confirmRemoveInvalidUsers) {
        this.confirmRemoveInvalidUsers = confirmRemoveInvalidUsers;
    }
}

