/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.supporters.jirareminder;

import com.atlassian.confluence.extra.calendar3.calendarstore.AbstractJiraSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.AbstractJiraSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.querydsl.DTO.JiraReminderEventDTO;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Summary;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraReminderEventDTOConverter<T extends AbstractJiraSubCalendar> {
    private static Logger logger = LoggerFactory.getLogger(JiraReminderEventDTOConverter.class);
    private final AbstractJiraSubCalendarDataStore<T> jiraSubCalendarDataStore;
    private final Supplier<ConfluenceUser> currentLoginUser;

    public JiraReminderEventDTOConverter(AbstractJiraSubCalendarDataStore<T> jiraSubCalendarDataStore, Supplier<ConfluenceUser> currentLoginUser) {
        this.jiraSubCalendarDataStore = jiraSubCalendarDataStore;
        this.currentLoginUser = currentLoginUser;
    }

    public List<JiraReminderEventDTO> convertJiraVEventToDTO(T subCalendar, List<VEvent> vEvents) {
        ArrayList<JiraReminderEventDTO> jiraReminderEventDTOs = new ArrayList<JiraReminderEventDTO>();
        for (VEvent vEvent : vEvents) {
            DateTime endDateTimeUTC;
            DateTime startDateTimeUTC;
            boolean isAllDay;
            Description descriptionProperty;
            JiraReminderEventDTO jiraReminderEventDTO = new JiraReminderEventDTO();
            ConfluenceUser currentUser = this.currentLoginUser.get();
            String userId = currentUser.getKey().toString();
            StringBuilder keyIdBuilder = new StringBuilder(userId).append(':').append(((AbstractJiraSubCalendar)subCalendar).getId());
            jiraReminderEventDTO.setKeyId(keyIdBuilder.toString());
            jiraReminderEventDTO.setSubCalendarId(((AbstractJiraSubCalendar)subCalendar).getId());
            jiraReminderEventDTO.setUserId(userId);
            jiraReminderEventDTO.setJql(((SubCalendar)subCalendar).getSourceLocation());
            jiraReminderEventDTO.setEventType(((AbstractJiraSubCalendar)subCalendar).getType());
            Object issueKeyProperty = vEvent.getProperty("X-JIRA-ISSUE-KEY");
            Object statusFieldProperty = vEvent.getProperty("X-JIRA-STATUS");
            String issueKey = null == issueKeyProperty ? "" : StringUtils.defaultString(((Content)issueKeyProperty).getValue());
            String status = null == statusFieldProperty ? "" : StringUtils.defaultString(((Content)statusFieldProperty).getValue());
            jiraReminderEventDTO.setTicketId(issueKey);
            jiraReminderEventDTO.setStatus(status);
            jiraReminderEventDTO.setStoreKey(((SubCalendar)subCalendar).getStoreKey());
            Summary summaryProperty = vEvent.getSummary();
            if (null != summaryProperty) {
                if ("JIRA_PROJECT_RELEASES_SUB_CALENDAR_STORE".equals(jiraReminderEventDTO.getStoreKey())) {
                    String projectName = CalendarUtil.getProjectNameFromJiraRawEvent(vEvent);
                    jiraReminderEventDTO.setSummary((String)(StringUtils.isNotBlank(projectName) ? projectName + " - " + ((Content)summaryProperty).getValue() : ((Content)summaryProperty).getValue()));
                } else {
                    jiraReminderEventDTO.setSummary(((Content)summaryProperty).getValue());
                }
            }
            if (null != (descriptionProperty = vEvent.getDescription())) {
                jiraReminderEventDTO.setDescription(((Content)descriptionProperty).getValue());
            }
            Date startDate = vEvent.getStartDate().getDate();
            Date endDate = vEvent.getEndDate().getDate();
            boolean bl = isAllDay = !(startDate instanceof net.fortuna.ical4j.model.DateTime) && !(endDate instanceof net.fortuna.ical4j.model.DateTime);
            if (isAllDay) {
                startDateTimeUTC = CalendarUtil.getUtcDateTimeWithAllDay(startDate);
                endDateTimeUTC = CalendarUtil.getUtcDateTimeWithAllDay(endDate);
            } else {
                startDateTimeUTC = new DateTime((Object)CalendarUtil.getUtcTime(vEvent.getStartDate()));
                endDateTimeUTC = new DateTime((Object)CalendarUtil.getUtcTime(vEvent.getEndDate()));
            }
            jiraReminderEventDTO.setUtcStart(startDateTimeUTC.getMillis());
            jiraReminderEventDTO.setUtcEnd(endDateTimeUTC.getMillis());
            Object allDayEventProperty = vEvent.getProperty("EVENT-ALLDAY");
            if (null != allDayEventProperty) {
                jiraReminderEventDTO.setAllDay(Boolean.getBoolean(((Content)allDayEventProperty).getValue()));
            } else if (startDateTimeUTC.getHourOfDay() == 0 && startDateTimeUTC.getMinuteOfHour() == 0 && startDateTimeUTC.getSecondOfMinute() == 0 && endDateTimeUTC.getHourOfDay() == 0 && endDateTimeUTC.getMinuteOfDay() == 0 && endDateTimeUTC.getSecondOfMinute() == 0) {
                jiraReminderEventDTO.setAllDay(true);
            }
            Object assignee = vEvent.getProperty("X-JIRA-ASSIGNEE");
            if (null != assignee) {
                jiraReminderEventDTO.setAssignee(((Content)assignee).getValue());
            }
            SubCalendarEvent subCalendarEvent = new SubCalendarEvent();
            subCalendarEvent.setSubCalendar((PersistedSubCalendar)subCalendar);
            subCalendarEvent.setStartTime(startDateTimeUTC);
            subCalendarEvent.setEndTime(endDateTimeUTC);
            subCalendarEvent = this.jiraSubCalendarDataStore.transformJiraEvent(subCalendarEvent, vEvent);
            if (subCalendarEvent != null && subCalendarEvent.getExtraProperties() != null && subCalendarEvent.getExtraProperties().size() > 0) {
                if ("greenhopper-sprint".equals(subCalendarEvent.getClassName())) {
                    jiraReminderEventDTO.setJiraIssueLink(subCalendarEvent.getExtraProperties().get("viewBoardsUrl"));
                } else if ("jira-issue".equals(subCalendarEvent.getClassName())) {
                    jiraReminderEventDTO.setJiraIssueLink(subCalendarEvent.getExtraProperties().get("issueLink"));
                } else {
                    jiraReminderEventDTO.setJiraIssueLink(subCalendarEvent.getExtraProperties().get("summaryLink"));
                }
                jiraReminderEventDTO.setJiraIssueIconUrl(subCalendarEvent.getIconUrl());
            }
            jiraReminderEventDTOs.add(jiraReminderEventDTO);
        }
        return jiraReminderEventDTOs;
    }
}

