/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Propagation
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl;

import com.atlassian.confluence.extra.calendar3.CalendarUserPreferenceStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.UserCalendarPreference;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogStatusReporter;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogTask;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemapSubscriptionCalendarWatchersWatchdogTask
implements WatchDogTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemapSubscriptionCalendarWatchersWatchdogTask.class);
    private static final String JOB_KEY = RemapSubscriptionCalendarWatchersWatchdogTask.class.getSimpleName();
    private static final String TOTAL_FOLLOWING_USERS = JOB_KEY + ".followers";
    private static final String TOTAL_REMAPPED = JOB_KEY + ".remapped";
    private static final int BATCH_SIZE = 10;
    private final InternalSubscriptionCalendarDataStore calendarDataStore;
    private final UserAccessor userAccessor;
    private final CalendarUserPreferenceStore calendarUserPreferenceStore;
    private final TransactionalHostContextAccessor contextAccessor;
    private final PluginSettings pluginSettings;
    private List<SubCalendarSummary> summaries;

    @Autowired
    public RemapSubscriptionCalendarWatchersWatchdogTask(InternalSubscriptionCalendarDataStore calendarDataStore, @ComponentImport UserAccessor userAccessor, CalendarUserPreferenceStore calendarUserPreferenceStore, @ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport TransactionalHostContextAccessor contextAccessor) {
        this.calendarDataStore = calendarDataStore;
        this.userAccessor = userAccessor;
        this.calendarUserPreferenceStore = calendarUserPreferenceStore;
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
        this.contextAccessor = contextAccessor;
    }

    @Override
    public boolean shouldRun() {
        Optional<String> statusString = Optional.ofNullable((String)this.pluginSettings.get(JOB_KEY));
        Status status = statusString.map(Status::valueOf).orElse(Status.NOT_RUN);
        if (status == Status.RUNNING) {
            this.summaries = this.calendarDataStore.getSubCalendarSummariesByStoreKey("com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore", -1, -1);
            return true;
        }
        if (status == Status.NOT_RUN) {
            this.summaries = this.calendarDataStore.getSubCalendarSummariesByStoreKey("com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore", -1, -1);
            this.pluginSettings.put(TOTAL_FOLLOWING_USERS, (Object)Long.toString(this.summaries.stream().map(SubCalendarSummary::getCreator).distinct().count()));
            this.pluginSettings.put(TOTAL_REMAPPED, (Object)Integer.toString(0));
            this.pluginSettings.put(JOB_KEY, (Object)Status.RUNNING.toString());
            return true;
        }
        return false;
    }

    @Override
    public void run(WatchDogStatusReporter reporter) {
        HashSet subscriberCalendarIds = new HashSet();
        Map<String, String> correctMappings = this.summaries.stream().map(SubscribingSubCalendarSummary.class::cast).peek(summary -> subscriberCalendarIds.add(summary.getId())).collect(Collectors.toMap(SubCalendarSummary::getId, SubscribingSubCalendarSummary::getSubscriptionId));
        int remapped = Integer.parseInt((String)this.pluginSettings.get(TOTAL_REMAPPED));
        int total = Integer.parseInt((String)this.pluginSettings.get(TOTAL_FOLLOWING_USERS));
        while (remapped < total) {
            List followingUserIds = this.summaries.stream().map(SubCalendarSummary::getCreator).distinct().sorted().skip(remapped).limit(10L).collect(Collectors.toList());
            Exception ex = (Exception)this.contextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, () -> {
                try {
                    for (String userId : followingUserIds) {
                        ConfluenceUser creator = this.userAccessor.getUserByKey(new UserKey(userId));
                        UserCalendarPreference creatorPreferences = this.calendarUserPreferenceStore.getUserPreference(creator);
                        Set<String> watched = creatorPreferences.getWatchedSubCalendars();
                        ImmutableSet incorrectlyWatched = Sets.intersection(watched, (Set)subscriberCalendarIds).immutableCopy();
                        watched.addAll(incorrectlyWatched.stream().map(correctMappings::get).collect(Collectors.toSet()));
                        watched.removeAll((Collection<?>)incorrectlyWatched);
                        creatorPreferences.setWatchedSubCalendars(watched);
                        this.calendarUserPreferenceStore.clearUserPreferenceCache(creator);
                        this.calendarUserPreferenceStore.setUserPreference(creator, creatorPreferences);
                    }
                }
                catch (Exception e) {
                    return e;
                }
                return null;
            });
            if (ex == null) {
                this.pluginSettings.put(TOTAL_REMAPPED, (Object)Integer.toString(remapped += followingUserIds.size()));
                continue;
            }
            LOGGER.error("An Exception was thrown whilst remapping watchers. Terminating watchdog task.", (Throwable)ex);
            return;
        }
        String status = String.format("Remapped %s user preferences with incorrectly watched subscription calendars", remapped);
        LOGGER.debug(status);
        reporter.report(status);
        this.pluginSettings.put(JOB_KEY, (Object)Status.FINISHED.toString());
    }

    private static enum Status {
        NOT_RUN("NOT_RUN"),
        RUNNING("RUNNING"),
        FINISHED("FINISHED");

        private final String status;

        private Status(String status) {
            this.status = status;
        }

        public String toString() {
            return this.status;
        }
    }
}

