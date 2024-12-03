/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.message.Message
 *  com.google.common.base.Function
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrieval;
import com.atlassian.confluence.extra.calendar3.calendarstore.CustomEventTypeSupport;
import com.atlassian.confluence.extra.calendar3.calendarstore.ReminderSettingCallback;
import com.atlassian.confluence.extra.calendar3.model.EventTypeReminder;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.message.Message;
import com.google.common.base.Function;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import org.joda.time.DateTime;

public interface CalendarDataStore<T extends PersistedSubCalendar>
extends CustomEventTypeSupport<T> {
    public void validate(SubCalendar var1, Map<String, List<String>> var2);

    public T save(SubCalendar var1) throws Exception;

    public void remove(T var1);

    public T getSubCalendar(String var1);

    public T getSubCalendar(SubCalendarEntity var1);

    public List<T> getSubCalendarsWithRestriction(String ... var1);

    public List<T> loadRestrictions(List<T> var1);

    public List<String> filterSubCalendarIds(String ... var1);

    public SubCalendarSummary getSubCalendarSummary(String var1);

    public List<SubCalendarSummary> getSubCalendarSummariesByStoreKey(String var1, int var2, int var3);

    public Set<String> getAllParentSubCalendarIds(String var1, int var2, int var3);

    public Set<String> getAllParentSubCalendarIds();

    public Calendar createEmptyCalendarForSubCalendar(T var1) throws Exception;

    public Calendar getSubCalendarContent(T var1) throws Exception;

    public List<Message> getSubCalendarWarnings(T var1);

    public void setSubCalendarContent(T var1, Calendar var2) throws Exception;

    public boolean hasSubCalendar(String var1);

    public boolean hasSubCalendar(PersistedSubCalendar var1);

    public boolean hasSubCalendar(SubCalendarEntity var1);

    public int getSubCalendarsCount();

    public Set<ConfluenceUser> getEventEditUserRestrictions(String var1);

    public Set<ConfluenceUser> getEventEditUserRestrictions(T var1);

    public Set<String> getEventEditGroupRestrictions(String var1);

    public Set<String> getEventEditGroupRestrictions(T var1);

    public Set<ConfluenceUser> getEventViewUserRestrictions(String var1);

    public Set<ConfluenceUser> getEventViewUserRestrictions(T var1);

    public Set<String> getEventViewGroupRestrictions(String var1);

    public Set<String> getEventViewGroupRestrictions(T var1);

    public void restrictEventEditToUsers(String var1, Set<ConfluenceUser> var2);

    public void restrictEventEditToGroups(String var1, Set<String> var2);

    public void restrictEventViewToUsers(String var1, Set<ConfluenceUser> var2);

    public void restrictEventViewToGroups(String var1, Set<String> var2);

    public boolean hasViewEventPrivilege(String var1, ConfluenceUser var2);

    public boolean hasViewEventPrivilege(T var1, ConfluenceUser var2);

    public boolean hasEditEventPrivilege(T var1, ConfluenceUser var2);

    public boolean hasDeletePrivilege(T var1, ConfluenceUser var2);

    public boolean hasAdminPrivilege(T var1, ConfluenceUser var2);

    public SubCalendarEvent transform(SubCalendarEvent var1, VEvent var2);

    public Message getTypeSpecificText(T var1, Message var2);

    public Collection<VEvent> query(T var1, FilterBase var2, RecurrenceRetrieval var3) throws Exception;

    public List<VEvent> getEvents(T var1) throws Exception;

    public List<VEvent> getEvents(T var1, DateTime var2, DateTime var3) throws Exception;

    public List<VEvent> getEvents(T var1, Predicate<VEvent> var2, String ... var3) throws Exception;

    public VEvent getEvent(T var1, String var2, String var3) throws Exception;

    public VEvent addEvent(T var1, VEvent var2);

    public VEvent updateEvent(T var1, VEvent var2);

    public void deleteEvent(T var1, String var2, String var3);

    public void moveEvent(T var1, String var2, PersistedSubCalendar var3);

    public void changeEvent(T var1, String var2, PersistedSubCalendar var3);

    public boolean setReminderFor(T var1, ConfluenceUser var2, boolean var3);

    public boolean hasReminderFor(T var1, ConfluenceUser var2);

    public void disableEventTypes(T var1, List<String> var2);

    public void deleteDisableEventType(String var1, String var2);

    public List<ReminderEvent> getSingleEventUpComingReminder(long var1, long var3);

    public List<VEvent> getRepeatEventUpComingReminder();

    public List<ReminderEvent> getJiraEventUpComingReminder(long var1, long var3);

    public <T> Option<T> getReminderListFor(Function<Map<String, Collection<String>>, T> var1, String ... var2);

    public Map<Integer, Collection<String>> getInviteesFor(Integer ... var1);

    public List<String> getInviteesFor(String var1);

    public ReminderSettingEntity updateReminderForSanboxEventType(Option<ReminderSettingCallback> var1, T var2, String var3, int var4);

    public ReminderSettingEntity getReminderSetting(String var1, String var2, String var3);

    public Map<String, Set<String>> getVEventUidsForUserBySubCalendar(ConfluenceUser var1);

    public Set<String> getChildSubCalendarHasReminders(ConfluenceUser var1, String ... var2);

    public Set<String> getAllSubCalendarIdHasReminders(ConfluenceUser var1);

    public Option<T> getChildSubCalendarByStoreKey(T var1, String var2);

    public Option<T> getChildSubCalendarByCustomEventTypeId(T var1, String var2);

    public Set<String> filterExistSubCalendarIds(String ... var1);

    public boolean checkExistCalendarDataStoreFromCache(String var1);

    public Message getSubCalendarEventWarning(T var1) throws Exception;

    public EventTypeReminder getEventTypeReminder(T var1);

    public boolean checkExistJiraReminderEvent(String var1);

    public void updateJiraReminderEvents(T var1, Calendar var2);

    public Set<String> getSubCalendarIdsOnSpace(String var1);

    public void removeSubCalendarFromSpaceView(T var1, String var2);

    public void addCalendarsToSpaceView(Set<String> var1, String var2);

    public void removeSubCalendarRestrictions(String var1);

    public void deleteInviteeFromAllEvents(String var1);
}

