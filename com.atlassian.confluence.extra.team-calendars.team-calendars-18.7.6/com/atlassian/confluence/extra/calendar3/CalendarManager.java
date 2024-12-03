/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.message.Message
 *  com.google.common.base.Optional
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.CalDAVCalendarManagerInternal;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.UserCalendarPreference;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.message.Message;
import com.google.common.base.Optional;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import org.joda.time.DateTime;

public interface CalendarManager
extends CalDAVCalendarManagerInternal {
    public static final String CALENDAR_PRODUCT_ID = "-//Atlassian Confluence//Calendar Plugin 1.0//EN";
    @Deprecated
    public static final String X_CONFLUENCE_PERSON = "X-CONFLUENCE-PERSON";
    public static final String X_CONFLUENCE_SUBCALENDAR_TYPE = "X-CONFLUENCE-SUBCALENDAR-TYPE";
    public static final String X_CONFLUENCE_USER = "X-CONFLUENCE-USER";
    public static final String X_CONFLUENCE_USER_KEY = "X-CONFLUENCE-USER-KEY";
    public static final String X_EVENT_SERIES = "X-EVENT-SERIES";
    public static final String X_CONFLUENCE_CUSTOM_TYPE = "X-CONFLUENCE-CUSTOM-EVENT-TYPE";
    public static final String X_CONFLUENCE_CUSTOM_TYPE_ID = "X-CONFLUENCE-CUSTOM-TYPE-ID";
    public static final String X_CONFLUENCE_CUSTOM_TYPE_TITLE = "X-CONFLUENCE-CUSTOM-TYPE-TITLE";
    public static final String X_CONFLUENCE_CUSTOM_TYPE_ICON = "X-CONFLUENCE-CUSTOM-TYPE-ICON";
    public static final String X_CONFLUENCE_CUSTOM_TYPE_REMINDER_DURATION_MINS = "X-CONFLUENCE-CUSTOM-TYPE-REMINDER-DURATION";
    public static final int SUB_CALENDAR_NAME_MAX_LENGTH = 255;

    public boolean hasSubCalendar(String var1);

    public List<String> filterSubCalendarIds(String ... var1);

    @Deprecated
    public PersistedSubCalendar getSubCalendar(String var1);

    public Optional<PersistedSubCalendar> getPersistedSubCalendar(String var1);

    public List<PersistedSubCalendar> getSubCalendarsWithRestriction(String ... var1);

    public Set<Message> getSubCalendarWarnings(PersistedSubCalendar var1);

    public SubCalendarSummary getSubCalendarSummary(String var1);

    public void validateSubCalendar(SubCalendar var1, Map<String, List<String>> var2);

    public PersistedSubCalendar save(SubCalendar var1) throws Exception;

    public void removeSubCalendar(PersistedSubCalendar var1);

    public Set<SubCalendarEvent> getEvents(PersistedSubCalendar var1) throws Exception;

    public Set<SubCalendarEvent> getEvents(PersistedSubCalendar var1, DateTime var2, DateTime var3) throws Exception;

    public Set<SubCalendarEvent> getEvents(PersistedSubCalendar var1, Predicate<VEvent> var2, String ... var3) throws Exception;

    public String generateUid();

    public Set<SubCalendarEvent> getUpcomingEvents(PersistedSubCalendar var1, DateTime var2, DateTime var3) throws Exception;

    public SubCalendarEvent addEvent(SubCalendarEvent var1) throws Exception;

    public SubCalendarEvent updateEvent(SubCalendarEvent var1) throws Exception;

    public Optional<SubCalendarEvent> updateEventOrRemoveInvalid(SubCalendarEvent var1) throws Exception;

    public Collection<SubCalendarEvent> updateOrRemoveInvalidExistingEvents(PersistedSubCalendar var1, Collection<String> var2) throws Exception;

    public SubCalendarEvent stopEventRecurrence(PersistedSubCalendar var1, String var2, String var3) throws Exception;

    public void removeEvent(PersistedSubCalendar var1, String var2, String var3) throws Exception;

    public boolean setReminderFor(PersistedSubCalendar var1, ConfluenceUser var2, boolean var3) throws Exception;

    public boolean hasReminderFor(PersistedSubCalendar var1, ConfluenceUser var2);

    public void disableEventTypes(PersistedSubCalendar var1, List<String> var2) throws Exception;

    public CustomEventType updateCustomEventType(PersistedSubCalendar var1, String var2, String var3, String var4, int var5) throws Exception;

    public ReminderSettingEntity updateReminderForSanboxEventType(PersistedSubCalendar var1, String var2, int var3) throws Exception;

    public ReminderSettingEntity getReminderSetting(String var1, String var2, String var3);

    public CustomEventType getCustomEventType(PersistedSubCalendar var1, String var2) throws Exception;

    public Collection<CustomEventType> getCustomEventTypes(String ... var1);

    public void deleteDisableEventType(String var1, String var2) throws Exception;

    public void deleteCustomEventType(String var1, String var2) throws Exception;

    public void excludeEvent(PersistedSubCalendar var1, String var2, DateTime var3) throws Exception;

    public SubCalendarEvent moveEvent(SubCalendarEvent var1, PersistedSubCalendar var2) throws Exception;

    public SubCalendarEvent changeEvent(SubCalendarEvent var1, String var2, String var3, String var4) throws Exception;

    public Calendar createEmptyCalendarForSubCalendar(PersistedSubCalendar var1) throws Exception;

    public Calendar getSubCalendarContent(PersistedSubCalendar var1) throws Exception;

    public void setSubCalendarContent(PersistedSubCalendar var1, Calendar var2) throws Exception;

    public List<PersistedSubCalendar> flattenSubCalendars(Collection<PersistedSubCalendar> var1);

    public Set<String> getSubCalendarsInView(ConfluenceUser var1);

    public Set<String> getSubCalendarsOnSpace(String var1);

    public long countSubCalendarsOnSpace(String var1);

    public void deleteSubCalendarOnSpace(PersistedSubCalendar var1, String var2);

    public void deleteSubCalendarInView(ConfluenceUser var1);

    public boolean isPersonalCalendarEmpty(ConfluenceUser var1);

    public int getSubCalendarsCount();

    public Set<SubCalendarSummary> findSubCalendars(String var1, int var2, int var3, ConfluenceUser var4) throws InvalidSearchException;

    public List<SubCalendarSummary> getAllSubCalendars(ConfluenceUser var1);

    public List<String> getAllSubCalendarIds(String var1, int var2, int var3);

    public Set<String> getAvailableSubCalendarColorCssClasses();

    public String getRandomCalendarColor(String ... var1);

    public String getSubCalendarColorAsHexValue(String var1);

    public void refresh(PersistedSubCalendar var1);

    public void setUserPreference(ConfluenceUser var1, UserCalendarPreference var2);

    public UserCalendarPreference getUserPreference(ConfluenceUser var1);

    public Set<String> updateSubCalendarsInView(ConfluenceUser var1, String[] var2);

    public void hideSubCalendar(PersistedSubCalendar var1, ConfluenceUser var2);

    public void watchSubCalendar(PersistedSubCalendar var1, ConfluenceUser var2);

    public void unwatchSubCalendar(PersistedSubCalendar var1, ConfluenceUser var2);

    public boolean isWatching(PersistedSubCalendar var1, ConfluenceUser var2, boolean var3);

    public Map<String, Boolean> isWatching(ConfluenceUser var1, PersistedSubCalendar ... var2);

    public boolean isWatchingViaContent(PersistedSubCalendar var1, ConfluenceUser var2);

    public void hideEventsOfSubCalendar(PersistedSubCalendar var1, ConfluenceUser var2);

    public void unhideEventsOfSubCalendar(PersistedSubCalendar var1, ConfluenceUser var2);

    public boolean isEventsOfSubCalendarHidden(PersistedSubCalendar var1, ConfluenceUser var2);

    public Message getTextForSubCalendar(PersistedSubCalendar var1, Message var2);

    public List<ReminderEvent> getEventUpComingReminder();

    public List<ReminderEvent> getEventUpComingReminder(long var1);

    public Option<Map<ConfluenceUser, Collection<ReminderEvent>>> getReminderListFor(Collection<ReminderEvent> var1);

    public List<ReminderEvent> getInviteesFor(List<ReminderEvent> var1);

    public Set<String> getChildSubCalendarHasReminders(ConfluenceUser var1, String ... var2);

    public Set<String> getAllSubCalendarIdHasReminders(ConfluenceUser var1);

    public Option<PersistedSubCalendar> getChildSubCalendarByStoreKey(PersistedSubCalendar var1, String var2);

    public Option<PersistedSubCalendar> getChildSubCalendarByCustomEventTypeId(PersistedSubCalendar var1, String var2);

    public Collection<PersistedSubCalendar> getRemindedChildSubCalendar(PersistedSubCalendar var1);

    public Set<String> filterExistSubCalendarIds(String ... var1);

    public Message getSubCalendarEventWarning(PersistedSubCalendar var1, long var2, long var4) throws Exception;

    public void addCalendarsToSpaceView(Set<String> var1, String var2);

    public List<String> getEventInviteeUserIds(String var1);

    public List<String> getAllCalendarUsers(long var1, long var3) throws Exception;
}

