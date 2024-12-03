/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Sets
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.CalendarPreferenceManager;
import com.atlassian.confluence.extra.calendar3.CalendarUserPreferenceStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarRemovedFromView;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarUnwatched;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarWatched;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.UserCalendarPreference;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="calendarPreferenceManager")
public class DefaultCalendarPreferenceManager
implements CalendarPreferenceManager {
    private final CalendarUserPreferenceStore calendarUserPreferenceStore;
    private final EventPublisher eventPublisher;

    @Autowired
    public DefaultCalendarPreferenceManager(CalendarUserPreferenceStore calendarUserPreferenceStore, @ComponentImport EventPublisher eventPublisher) {
        this.calendarUserPreferenceStore = calendarUserPreferenceStore;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void setUserPreference(ConfluenceUser user, UserCalendarPreference pref) {
        this.calendarUserPreferenceStore.setUserPreference(user, pref);
    }

    @Override
    public UserCalendarPreference getUserPreference(ConfluenceUser user) {
        UserCalendarPreference pref = this.calendarUserPreferenceStore.getUserPreference(user);
        if (null == pref) {
            pref = new UserCalendarPreference();
        }
        return pref;
    }

    @Override
    public void addToView(ConfluenceUser user, PersistedSubCalendar persistedSubCalendar) {
        UserCalendarPreference userCalendarPreference = this.getUserPreference(user);
        HashSet<String> idsOfSubCalendarsInView = new HashSet<String>(userCalendarPreference.getSubCalendarsInView());
        if (idsOfSubCalendarsInView.contains(persistedSubCalendar.getId()) || persistedSubCalendar instanceof InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar && idsOfSubCalendarsInView.contains(((InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar)persistedSubCalendar).getSubscriptionId())) {
            return;
        }
        idsOfSubCalendarsInView.add(persistedSubCalendar.getId());
        userCalendarPreference.setSubCalendarsInView(idsOfSubCalendarsInView);
        this.setUserPreference(user, userCalendarPreference);
    }

    @Override
    public void removeFromView(ConfluenceUser user, PersistedSubCalendar persistedSubCalendar) {
        UserCalendarPreference pref = this.getUserPreference(user);
        pref.setSubCalendarsInView(new HashSet<String>(Collections2.filter(pref.getSubCalendarsInView(), (Predicate)Predicates.not((Predicate)Predicates.equalTo((Object)persistedSubCalendar.getId())))));
        this.eventPublisher.publish((Object)new SubCalendarRemovedFromView((Object)this, user, persistedSubCalendar));
        this.setUserPreference(user, pref);
    }

    @Override
    public void watch(ConfluenceUser user, PersistedSubCalendar persistedSubCalendar) {
        UserCalendarPreference pref = this.getUserPreference(user);
        HashSet<String> watchedSubCalendars = new HashSet<String>(pref.getWatchedSubCalendars());
        watchedSubCalendars.add(persistedSubCalendar.getId());
        pref.setWatchedSubCalendars(watchedSubCalendars);
        this.setUserPreference(user, pref);
        this.eventPublisher.publish((Object)new SubCalendarWatched((Object)this, user, persistedSubCalendar));
    }

    @Override
    public void unwatch(ConfluenceUser user, PersistedSubCalendar persistedSubCalendar) {
        UserCalendarPreference pref = this.getUserPreference(user);
        pref.setWatchedSubCalendars(new HashSet<String>(Sets.filter(pref.getWatchedSubCalendars(), (Predicate)Predicates.not((Predicate)Predicates.equalTo((Object)persistedSubCalendar.getId())))));
        this.setUserPreference(user, pref);
        this.eventPublisher.publish((Object)new SubCalendarUnwatched((Object)this, user, persistedSubCalendar));
    }

    @Override
    public boolean isWatching(ConfluenceUser user, PersistedSubCalendar persistedSubCalendar) {
        UserCalendarPreference userCalendarPreference = this.getUserPreference(user);
        return userCalendarPreference.getWatchedSubCalendars().contains(persistedSubCalendar.getId());
    }

    @Override
    public Map<String, Boolean> isWatching(ConfluenceUser user, PersistedSubCalendar ... persistedSubCalendars) {
        HashMap<String, Boolean> result = new HashMap<String, Boolean>();
        UserCalendarPreference userCalendarPreference = this.getUserPreference(user);
        for (PersistedSubCalendar persistedSubCalendar : persistedSubCalendars) {
            Set<String> watchedSubCalendars = userCalendarPreference.getWatchedSubCalendars();
            boolean isWatched = watchedSubCalendars.contains(persistedSubCalendar.getId());
            result.put(persistedSubCalendar.getId(), isWatched);
        }
        return result;
    }

    @Override
    public void hideEvents(ConfluenceUser user, PersistedSubCalendar persistedSubCalendar) {
        UserCalendarPreference userCalendarPreference = this.getUserPreference(user);
        HashSet<String> disabledSubCalendars = new HashSet<String>(userCalendarPreference.getDisabledSubCalendars());
        disabledSubCalendars.add(persistedSubCalendar.getId());
        userCalendarPreference.setDisabledSubCalendars(disabledSubCalendars);
        this.setUserPreference(user, userCalendarPreference);
    }

    @Override
    public void unhideEvents(ConfluenceUser user, PersistedSubCalendar persistedSubCalendar) {
        UserCalendarPreference userCalendarPreference = this.getUserPreference(user);
        userCalendarPreference.setDisabledSubCalendars(new HashSet<String>(Sets.filter(userCalendarPreference.getDisabledSubCalendars(), (Predicate)Predicates.not((Predicate)Predicates.equalTo((Object)persistedSubCalendar.getId())))));
        this.setUserPreference(user, userCalendarPreference);
    }

    @Override
    public boolean isEventsHidden(ConfluenceUser user, PersistedSubCalendar persistedSubCalendar) {
        UserCalendarPreference userCalendarPreference = this.getUserPreference(user);
        return null != userCalendarPreference.getDisabledSubCalendars() && !Collections2.filter(userCalendarPreference.getDisabledSubCalendars(), (Predicate)Predicates.equalTo((Object)persistedSubCalendar.getId())).isEmpty();
    }
}

