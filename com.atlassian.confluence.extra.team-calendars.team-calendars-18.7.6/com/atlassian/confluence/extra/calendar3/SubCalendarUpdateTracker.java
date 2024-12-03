/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.annotations.VisibleForTesting;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.PreDestroy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SubCalendarUpdateTracker {
    public static final int TIMER_WAIT_TIME = Integer.getInteger("com.atlassian.confluence.extra.calendar3.subcal.tracker.waittime", 1000);
    public static final int TIMER_SCHEDULE_TIME = Integer.getInteger("com.atlassian.confluence.extra.calendar3.subcal.tracker.scheduletime", 10000);
    private static final Logger LOG = LoggerFactory.getLogger(SubCalendarUpdateTracker.class);
    private final TransactionTemplate transactionTemplate;
    private final CalendarDataStore<PersistedSubCalendar> calendarDataStore;
    private Timer timer;
    private List<String> ids;

    @Autowired
    public SubCalendarUpdateTracker(@ComponentImport TransactionTemplate transactionTemplate, @Qualifier(value="calendarDataStore") CalendarDataStore<PersistedSubCalendar> calendarDataStore) {
        this.transactionTemplate = transactionTemplate;
        this.calendarDataStore = calendarDataStore;
        this.ids = new CopyOnWriteArrayList<String>();
    }

    @PreDestroy
    public void cancelTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    public void trackChange(PersistedSubCalendar subCalendar) {
        Objects.requireNonNull(subCalendar);
        if (this.timer == null) {
            this.timer = new Timer();
            this.timer.scheduleAtFixedRate(this.createTrackChangedTimeTask(), TIMER_WAIT_TIME, (long)TIMER_SCHEDULE_TIME);
        }
        String parentSubCalendarId = StringUtils.isNotEmpty(subCalendar.getParentId()) ? subCalendar.getParentId() : subCalendar.getId();
        this.ids.add(parentSubCalendarId);
    }

    @VisibleForTesting
    public TimerTask createTrackChangedTimeTask() {
        return new SubCalendarUpdateTrackerTask();
    }

    private class SubCalendarUpdateTrackerTask
    extends TimerTask {
        private SubCalendarUpdateTrackerTask() {
        }

        @Override
        public void run() {
            SubCalendarUpdateTracker.this.transactionTemplate.execute(() -> {
                HashSet<String> localIds = new HashSet<String>(SubCalendarUpdateTracker.this.ids);
                for (String parentSubCalendarId : localIds) {
                    try {
                        PersistedSubCalendar parentSubCalendar = SubCalendarUpdateTracker.this.calendarDataStore.getSubCalendar(parentSubCalendarId);
                        if (parentSubCalendar == null) {
                            LOG.warn("Could not load sub calendar with id [{}] so will skip track change", (Object)parentSubCalendarId);
                            continue;
                        }
                        SubCalendarUpdateTracker.this.calendarDataStore.save(parentSubCalendar);
                    }
                    catch (Exception e) {
                        LOG.error("Could not track change sub calendar with id [{}]. Exception", (Object)parentSubCalendarId, (Object)e);
                    }
                    LOG.debug("Track change sub calendar with id [{}] successfully.", (Object)parentSubCalendarId);
                }
                SubCalendarUpdateTracker.this.ids.clear();
                return null;
            });
        }
    }
}

