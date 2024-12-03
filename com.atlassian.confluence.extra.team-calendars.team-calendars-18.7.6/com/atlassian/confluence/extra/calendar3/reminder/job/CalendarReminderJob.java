/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.Collections2
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.reminder.job;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.events.ActiveObjectsInitializedEvent;
import com.atlassian.confluence.extra.calendar3.events.ReminderNotificationEvent;
import com.atlassian.confluence.extra.calendar3.license.LicenseVerifier;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.util.AsynchronousTaskExecutor;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="tcReminderJob")
public class CalendarReminderJob
implements InitializingBean,
DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarReminderJob.class);
    private final CalendarManager calendarManager;
    private final AsynchronousTaskExecutor executor;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisher eventPublisher;
    private volatile boolean activeObjectsInitialized = false;
    private final LicenseVerifier licenseVerifier;
    private Supplier<List<ReminderEvent>> reminderSupplier;
    private Supplier<List<ReminderEvent>> defaultReminderSupplier;

    @Autowired
    public CalendarReminderJob(@ComponentImport EventPublisher eventPublisher, @ComponentImport TransactionTemplate transactionTemplate, AsynchronousTaskExecutor executor, CalendarManager calendarManager, LicenseVerifier licenseVerifier) {
        this.eventPublisher = eventPublisher;
        this.transactionTemplate = transactionTemplate;
        this.calendarManager = calendarManager;
        this.executor = executor;
        this.licenseVerifier = licenseVerifier;
        this.defaultReminderSupplier = () -> calendarManager.getEventUpComingReminder();
        this.reminderSupplier = this.defaultReminderSupplier;
    }

    @VisibleForTesting
    public void setReminderSupplier(Supplier<List<ReminderEvent>> reminderSupplier) {
        this.defaultReminderSupplier = this.reminderSupplier;
        this.reminderSupplier = reminderSupplier;
    }

    @VisibleForTesting
    public void resetReminderSupplier() {
        this.reminderSupplier = this.defaultReminderSupplier;
    }

    @EventListener
    public void onActiveObjectsInitialized(ActiveObjectsInitializedEvent event) {
        this.activeObjectsInitialized = true;
    }

    public void execute() {
        if (this.activeObjectsInitialized && this.isValidLicense()) {
            LOGGER.info("CalendarReminderJob is running");
            this.executor.submit(new RemindEventForUserCollector());
        }
    }

    private boolean isValidLicense() {
        return !this.licenseVerifier.isLicenseInvalidated() && !this.licenseVerifier.isLicenseExpired();
    }

    private void dumpLogDebug(Map<ConfluenceUser, Collection<ReminderEvent>> reminderEventGroupByUser) {
        if (LOGGER.isDebugEnabled()) {
            Map<ConfluenceUser, Collection<ReminderEvent>> reminderList = reminderEventGroupByUser;
            LOGGER.debug("CalendarReminderJob is running with a list of reminder user : {}", (Object)reminderList.size());
            for (Map.Entry<ConfluenceUser, Collection<ReminderEvent>> reminderForUser : reminderList.entrySet()) {
                LOGGER.debug("Will remind for user : {}", (Object)reminderForUser.getKey().toString());
                for (ReminderEvent reminderEvent : reminderForUser.getValue()) {
                    LOGGER.debug("##### For event {}", (Object)reminderEvent);
                }
            }
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    private class RemindEventForUserCollector
    implements Callable<Void> {
        private RemindEventForUserCollector() {
        }

        @Override
        public Void call() throws Exception {
            return (Void)CalendarReminderJob.this.transactionTemplate.execute(() -> {
                List<ReminderEvent> reminderEvents = CalendarReminderJob.this.reminderSupplier.get();
                if (reminderEvents.size() > 0) {
                    Collection customEventTypeIds = Collections2.transform((Collection)Collections2.filter(reminderEvents, input -> StringUtils.isNotEmpty(input.getCustomEventTypeId())), ReminderEvent::getCustomEventTypeId);
                    Collection<CustomEventType> customEventTypes = CalendarReminderJob.this.calendarManager.getCustomEventTypes(customEventTypeIds.toArray(new String[0]));
                    this.getEventTypeNameFor(reminderEvents, customEventTypes);
                    CalendarReminderJob.this.calendarManager.getInviteesFor(reminderEvents);
                    Map reminderEventGroupByUser = (Map)CalendarReminderJob.this.calendarManager.getReminderListFor(reminderEvents).getOrElse(new HashMap());
                    CalendarReminderJob.this.dumpLogDebug(reminderEventGroupByUser);
                    for (Map.Entry reminderForUser : reminderEventGroupByUser.entrySet()) {
                        CalendarReminderJob.this.eventPublisher.publish((Object)new ReminderNotificationEvent((ConfluenceUser)reminderForUser.getKey(), (Collection)reminderForUser.getValue()));
                    }
                }
                return null;
            });
        }

        private void getEventTypeNameFor(Collection<ReminderEvent> reminderEvents, Collection<CustomEventType> customEventTypes) {
            for (ReminderEvent reminderEvent : reminderEvents) {
                if (StringUtils.isEmpty(reminderEvent.getCustomEventTypeId())) {
                    reminderEvent.setEventTypeName(CalendarUtil.getEventTypePropertyFromStoreKey(reminderEvent.getStoreKey()));
                    continue;
                }
                Option foundCustomeEventType = Iterables.findFirst(customEventTypes, customEventType -> customEventType.getCustomEventTypeId().equals(reminderEvent.getCustomEventTypeId()));
                CustomEventType customEventType2 = (CustomEventType)foundCustomeEventType.get();
                reminderEvent.setEventTypeName(customEventType2 == null ? "" : customEventType2.getTitle());
            }
        }
    }
}

