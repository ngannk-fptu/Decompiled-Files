/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.status;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugins.synchrony.api.events.SynchronyStatusDownEvent;
import com.atlassian.confluence.plugins.synchrony.api.events.SynchronyStatusRestoredEvent;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.annotations.VisibleForTesting;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchronyStatusEventPublisher")
class SynchronyStatusEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(SynchronyStatusEventPublisher.class);
    @VisibleForTesting
    static final long NO_DOWN_TIME_IN_DB = -1L;
    @VisibleForTesting
    static final BandanaContext CONFLUENCE_BANDANA_CONTEXT = new ConfluenceBandanaContext();
    @VisibleForTesting
    static final String SYNCHRONY_WENT_DOWN_TIME_KEY = "atlassian.confluence.synchrony.went.down.at.this.time";
    private final EventPublisher eventPublisher;
    private final BandanaManager bandanaManager;
    private final DateProvider dateProvider;

    @Autowired
    SynchronyStatusEventPublisher(@ComponentImport EventPublisher eventPublisher, @ComponentImport BandanaManager bandanaManager) {
        this(eventPublisher, bandanaManager, new DateProvider());
    }

    @VisibleForTesting
    SynchronyStatusEventPublisher(EventPublisher eventPublisher, BandanaManager bandanaManager, DateProvider dateProvider) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.bandanaManager = Objects.requireNonNull(bandanaManager);
        this.dateProvider = Objects.requireNonNull(dateProvider);
    }

    void decideEvents(boolean isRunningNow, boolean wasPreviouslyRunning) {
        if (isRunningNow && !wasPreviouslyRunning) {
            long downTimeInSeconds = this.calculateDownTimeInSecondsAndDeleteDownTime();
            if (-1L == downTimeInSeconds) {
                log.debug("No down time found stored in database. Not publishing events.");
                return;
            }
            log.trace("Synchrony was down for approximately {} seconds before coming back up. Publishing event.", (Object)downTimeInSeconds);
            this.eventPublisher.publish((Object)new SynchronyStatusRestoredEvent(downTimeInSeconds));
        } else if (!isRunningNow && wasPreviouslyRunning) {
            this.storeLastDownTime();
            log.trace("Synchrony has gone down unexpectedly. Publishing event.");
            this.eventPublisher.publish((Object)new SynchronyStatusDownEvent());
        } else {
            log.trace("No events published because Synchrony was either still running, or still not running since the last check.");
        }
    }

    private long calculateDownTimeInSecondsAndDeleteDownTime() {
        long downTimeInSeconds = this.calculateDownTimeInSeconds();
        if (-1L != downTimeInSeconds) {
            this.deleteDownTime();
        }
        return downTimeInSeconds;
    }

    private long calculateDownTimeInSeconds() {
        long now = this.dateProvider.getDate().getTime();
        Optional<Long> lastDownTime = this.getLastDownTime();
        if (lastDownTime.isPresent()) {
            return (now - lastDownTime.get()) / 1000L;
        }
        return -1L;
    }

    private void storeLastDownTime() {
        long now = this.dateProvider.getDate().getTime();
        log.trace("Storing approximate time Synchrony went down: {}", (Object)now);
        this.bandanaManager.setValue(CONFLUENCE_BANDANA_CONTEXT, SYNCHRONY_WENT_DOWN_TIME_KEY, (Object)now);
    }

    private Optional<Long> getLastDownTime() {
        return Optional.ofNullable((Long)this.bandanaManager.getValue(CONFLUENCE_BANDANA_CONTEXT, SYNCHRONY_WENT_DOWN_TIME_KEY));
    }

    private void deleteDownTime() {
        log.trace("Removing time Synchrony last went down.");
        this.bandanaManager.removeValue(CONFLUENCE_BANDANA_CONTEXT, SYNCHRONY_WENT_DOWN_TIME_KEY);
    }

    @VisibleForTesting
    static class DateProvider {
        DateProvider() {
        }

        Date getDate() {
            return new Date();
        }
    }
}

