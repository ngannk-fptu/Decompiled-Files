/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.johnson.event.Event
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.impl.backuprestore.restore.confluencelocker;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.JohnsonEventFactory;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.AbstractRestoreEvent;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.RestoreFailedEvent;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.RestoreInProgressEvent;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.RestoreLockDatabaseEvent;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.RestoreSucceededEvent;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.RestoreUnlockDatabaseEvent;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.johnson.Johnson;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ConfluenceLockerOnSiteRestore
implements DisposableBean,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceLockerOnSiteRestore.class);
    private final EventPublisher eventPublisher;
    private final JohnsonEventFactory johnsonEventFactory;
    private static final AtomicBoolean databaseLocked = new AtomicBoolean();
    private static final AtomicBoolean isDisplayJohnson = new AtomicBoolean();
    final AtomicReference<com.atlassian.johnson.event.Event> siteInProgressJohnsonEvent = new AtomicReference();

    public ConfluenceLockerOnSiteRestore(EventPublisher eventPublisher, JohnsonEventFactory johnsonEventFactory) {
        this.eventPublisher = eventPublisher;
        this.johnsonEventFactory = johnsonEventFactory;
    }

    public static boolean isDatabaseLocked() {
        return databaseLocked.get();
    }

    public static boolean isDisplayJohnson() {
        return isDisplayJohnson.get();
    }

    public static void assertDatabaseIsNotLocked() throws IllegalStateException {
        if (ConfluenceLockerOnSiteRestore.isDatabaseLocked()) {
            throw new IllegalStateException("Operation is not allowed. The database is locked because site restore is in the progress.");
        }
    }

    @EventListener
    public void handleEvent(ClusterEventWrapper eventWrapper) {
        Event wrappedEvent = eventWrapper.getEvent();
        if (wrappedEvent instanceof AbstractRestoreEvent) {
            AbstractRestoreEvent abstractRestoreEvent = (AbstractRestoreEvent)wrappedEvent;
            this.updateSiteRestoreState(abstractRestoreEvent);
        }
    }

    @EventListener
    public void handleEvent(AbstractRestoreEvent abstractRestoreEvent) {
        this.updateSiteRestoreState(abstractRestoreEvent);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    private void updateSiteRestoreState(AbstractRestoreEvent abstractRestoreEvent) {
        if (!JobScope.SITE.equals((Object)abstractRestoreEvent.getJobScope())) {
            return;
        }
        if (abstractRestoreEvent instanceof RestoreInProgressEvent) {
            this.handleRestoreInProgressEvent((RestoreInProgressEvent)abstractRestoreEvent);
        } else if (abstractRestoreEvent instanceof RestoreLockDatabaseEvent) {
            this.handleRestoreLockDatabaseEvent((RestoreLockDatabaseEvent)abstractRestoreEvent);
        } else if (abstractRestoreEvent instanceof RestoreUnlockDatabaseEvent) {
            this.handleRestoreUnlockDatabaseEvent((RestoreUnlockDatabaseEvent)abstractRestoreEvent);
        } else if (abstractRestoreEvent instanceof RestoreSucceededEvent) {
            this.handleRestoreSucceededEvent((RestoreSucceededEvent)abstractRestoreEvent);
        } else if (abstractRestoreEvent instanceof RestoreFailedEvent) {
            this.handleRestoreFailedEvent((RestoreFailedEvent)abstractRestoreEvent);
        } else {
            throw new IllegalArgumentException("Unexpected event class: " + abstractRestoreEvent.getClass().getName());
        }
    }

    private void handleRestoreInProgressEvent(RestoreInProgressEvent event) {
        log.debug("Received InProgressRestoreEvent. Updating restore in progress Johnson page.");
        isDisplayJohnson.set(event.isDisplayJohnson());
        databaseLocked.set(event.isDatabaseLocked());
        if (ConfluenceLockerOnSiteRestore.isDisplayJohnson()) {
            com.atlassian.johnson.event.Event johnsonEvent = this.createInProgressEventIfNotExists();
            this.updateProgress(johnsonEvent, event);
        }
    }

    private void handleRestoreLockDatabaseEvent(RestoreLockDatabaseEvent event) {
        log.info("Received RestoreLockDatabaseEvent. Locking the database.");
        databaseLocked.set(true);
    }

    private void handleRestoreUnlockDatabaseEvent(RestoreUnlockDatabaseEvent event) {
        log.info("Received RestoreUnlockDatabaseEvent. Unlocking the database.");
        databaseLocked.set(false);
    }

    private void handleRestoreSucceededEvent(RestoreSucceededEvent event) {
        log.info("Received RestoreSucceededEvent. Disabling restore in progress Johnson page and unlocking the database.");
        this.removeInProgressJohnsonEvent();
        isDisplayJohnson.set(false);
        databaseLocked.set(false);
    }

    private void handleRestoreFailedEvent(RestoreFailedEvent event) {
        log.warn("Received RestoreFailedEvent. Site failure, unlocking the database.");
        isDisplayJohnson.set(event.isDisplayJohnson());
        this.createJohnsonEventAboutSiteFailure(event);
        this.removeInProgressJohnsonEvent();
        isDisplayJohnson.set(false);
        databaseLocked.set(false);
    }

    private void createJohnsonEventAboutSiteFailure(RestoreFailedEvent event) {
        if (!ConfluenceLockerOnSiteRestore.isDisplayJohnson()) {
            return;
        }
        com.atlassian.johnson.event.Event siteRestoreFailureEvent = this.johnsonEventFactory.createRestoreFailureEvent(event.getErrorMessage());
        Johnson.getEventContainer().addEvent(siteRestoreFailureEvent);
    }

    private synchronized void removeInProgressJohnsonEvent() {
        com.atlassian.johnson.event.Event johnsonEvent = this.siteInProgressJohnsonEvent.getAndSet(null);
        if (johnsonEvent == null) {
            return;
        }
        Johnson.getEventContainer().removeEvent(johnsonEvent);
    }

    private void updateProgress(com.atlassian.johnson.event.Event johnsonEvent, RestoreInProgressEvent event) {
        if (event.getTotalNumberOfObjects() == 0L) {
            return;
        }
        johnsonEvent.setProgress((int)(event.getProcessedObjects() * 100L / event.getTotalNumberOfObjects()));
    }

    private synchronized com.atlassian.johnson.event.Event createInProgressEventIfNotExists() {
        return Objects.requireNonNullElseGet(this.siteInProgressJohnsonEvent.get(), this::createInProgressEvent);
    }

    private com.atlassian.johnson.event.Event createInProgressEvent() {
        com.atlassian.johnson.event.Event siteRestoreJohnsonEvent = this.johnsonEventFactory.createInProgressEvent();
        this.siteInProgressJohnsonEvent.set(siteRestoreJohnsonEvent);
        Johnson.getEventContainer().addEvent(siteRestoreJohnsonEvent);
        return siteRestoreJohnsonEvent;
    }
}

