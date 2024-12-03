/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogContext
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.crowd.event.directory.RemoteDirectorySynchronisationFailedEvent
 *  com.atlassian.crowd.event.directory.RemoteDirectorySynchronisationFinishedEvent
 *  com.atlassian.crowd.event.directory.RemoteDirectorySynchronisedEvent
 *  com.atlassian.event.api.EventPublisher
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.util;

import com.atlassian.crowd.audit.AuditLogContext;
import com.atlassian.crowd.audit.AuditLogEventSource;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.event.directory.RemoteDirectorySynchronisationFailedEvent;
import com.atlassian.crowd.event.directory.RemoteDirectorySynchronisationFinishedEvent;
import com.atlassian.crowd.event.directory.RemoteDirectorySynchronisedEvent;
import com.atlassian.crowd.manager.directory.InternalSynchronisationStatusManager;
import com.atlassian.event.api.EventPublisher;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectorySynchronisationEventHelper {
    private static final Logger log = LoggerFactory.getLogger(DirectorySynchronisationEventHelper.class);
    public static final long UNKNOWN_TIME_TAKEN_VALUE = -1L;
    private final InternalSynchronisationStatusManager synchronisationStatusManager;
    private final AuditLogContext auditLogContext;
    private final EventPublisher eventPublisher;
    private final DirectoryInstanceLoader directoryInstanceLoader;

    public DirectorySynchronisationEventHelper(InternalSynchronisationStatusManager synchronisationStatusManager, AuditLogContext auditLogContext, EventPublisher eventPublisher, DirectoryInstanceLoader directoryInstanceLoader) {
        this.synchronisationStatusManager = synchronisationStatusManager;
        this.auditLogContext = auditLogContext;
        this.eventPublisher = eventPublisher;
        this.directoryInstanceLoader = directoryInstanceLoader;
    }

    public void publishDirectorySynchronisationEvent(Object source, Directory directory, boolean wasSuccessful, Long customTimeTakenInMs) throws Exception {
        if (wasSuccessful) {
            this.publishSuccessfulDirectorySynchronisationEvent(source, directory, customTimeTakenInMs);
        } else {
            this.publishFailedDirectorySynchronisationEvent(source, directory, customTimeTakenInMs);
        }
    }

    public void publishSuccessfulDirectorySynchronisationEvent(Object source, Directory directory, Long customTimeTakenInMs) throws Exception {
        Optional<DirectorySynchronisationRoundInformation> lastRound = this.getLastRound(directory.getId());
        RemoteDirectory remoteDirectory = this.directoryInstanceLoader.getDirectory(directory);
        long timeTakeInMs = (Long)ObjectUtils.firstNonNull((Object[])new Long[]{customTimeTakenInMs, lastRound.map(DirectorySynchronisationRoundInformation::getDurationMs).orElse(null), -1L});
        RemoteDirectorySynchronisedEvent event = new RemoteDirectorySynchronisedEvent(source, remoteDirectory, (DirectorySynchronisationRoundInformation)lastRound.orElse(null), timeTakeInMs);
        this.publishSynchronizationEvent((RemoteDirectorySynchronisationFinishedEvent)event);
    }

    public void publishFailedDirectorySynchronisationEvent(Object source, Directory directory, Long customTimeTakenInMs) throws Exception {
        Optional<DirectorySynchronisationRoundInformation> lastRound = this.getLastRound(directory.getId());
        RemoteDirectory remoteDirectory = this.directoryInstanceLoader.getDirectory(directory);
        long timeTakeInMs = (Long)ObjectUtils.firstNonNull((Object[])new Long[]{customTimeTakenInMs, lastRound.map(DirectorySynchronisationRoundInformation::getDurationMs).orElse(null), -1L});
        RemoteDirectorySynchronisationFailedEvent event = new RemoteDirectorySynchronisationFailedEvent(source, remoteDirectory, (DirectorySynchronisationRoundInformation)lastRound.orElse(null), timeTakeInMs);
        this.publishSynchronizationEvent((RemoteDirectorySynchronisationFinishedEvent)event);
    }

    private void publishSynchronizationEvent(RemoteDirectorySynchronisationFinishedEvent event) throws Exception {
        this.auditLogContext.withAuditLogSource(AuditLogEventSource.SYNCHRONIZATION, () -> {
            this.eventPublisher.publish((Object)event);
            return null;
        });
    }

    private Optional<DirectorySynchronisationRoundInformation> getLastRound(long directoryId) {
        Optional<DirectorySynchronisationRoundInformation> lastRound = Optional.empty();
        try {
            lastRound = Optional.ofNullable(this.synchronisationStatusManager.getDirectorySynchronisationInformation(directoryId)).map(DirectorySynchronisationInformation::getLastRound);
        }
        catch (Exception e) {
            log.warn("Could not get last synchronization information for directory {} to create detailed audit log for it", (Object)directoryId, (Object)e);
        }
        return lastRound;
    }
}

