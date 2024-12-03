/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.crowd.audit.AuditLogContext
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.SynchronisableDirectory
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.crowd.event.directory.RemoteDirectorySynchronisationStartedEvent
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.crowd.manager.directory.SynchronisationStatusManager
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.crowd.model.directory.SynchronisationStatusKey
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.crowd.audit.AuditLogContext;
import com.atlassian.crowd.audit.AuditLogEventSource;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.SynchronisableDirectory;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.crowd.event.directory.RemoteDirectorySynchronisationStartedEvent;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.DirectorySynchronisationUtils;
import com.atlassian.crowd.manager.directory.DirectorySynchroniser;
import com.atlassian.crowd.manager.directory.InternalSynchronisationStatusManager;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.manager.directory.SynchronisationStatusManager;
import com.atlassian.crowd.manager.directory.TransactionalDirectoryDao;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;
import com.atlassian.crowd.util.DirectorySynchronisationEventHelper;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class DirectorySynchroniserImpl
implements DirectorySynchroniser {
    private static final Logger log = LoggerFactory.getLogger(DirectorySynchroniser.class);
    private final ClusterLockService lockService;
    private final TransactionalDirectoryDao directoryDao;
    private final InternalSynchronisationStatusManager synchronisationStatusManager;
    private final EventPublisher eventPublisher;
    private final AuditLogContext auditLogContext;
    private final DirectorySynchronisationEventHelper syncEventHelper;

    public DirectorySynchroniserImpl(ClusterLockService lockService, TransactionalDirectoryDao directoryDao, InternalSynchronisationStatusManager synchronisationStatusManager, EventPublisher eventPublisher, AuditLogContext auditLogContext, DirectorySynchronisationEventHelper syncEventHelper) {
        this.lockService = lockService;
        this.directoryDao = directoryDao;
        this.synchronisationStatusManager = synchronisationStatusManager;
        this.eventPublisher = eventPublisher;
        this.auditLogContext = auditLogContext;
        this.syncEventHelper = syncEventHelper;
        this.eventPublisher.register((Object)this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Transactional(propagation=Propagation.NEVER)
    public void synchronise(SynchronisableDirectory remoteDirectory, SynchronisationMode mode) throws DirectoryNotFoundException, OperationFailedException {
        long directoryId = remoteDirectory.getDirectoryId();
        Directory directory = this.directoryDao.findById(directoryId);
        if (!directory.isActive()) {
            log.debug("Request to synchronise directory [ {} ] in {} mode is returning silently because the directory is not active.", (Object)directoryId, (Object)mode);
            return;
        }
        log.debug("request to synchronise directory [ {} ] in {} mode", (Object)directoryId, (Object)mode);
        ClusterLock lock = this.lockService.getLockForName(DirectorySynchronisationUtils.getLockName(directoryId));
        if (lock.tryLock()) {
            boolean successful = false;
            try {
                this.synchronisationStatusManager.syncStarted(directory);
                try {
                    this.auditLogContext.withAuditLogSource(AuditLogEventSource.SYNCHRONIZATION, () -> {
                        this.eventPublisher.publish((Object)new RemoteDirectorySynchronisationStartedEvent((RemoteDirectory)remoteDirectory));
                        remoteDirectory.synchroniseCache(mode, (SynchronisationStatusManager)this.synchronisationStatusManager);
                        return null;
                    });
                    this.finishSynchronisationIfWasNotFinishedAlready(directory);
                    successful = true;
                    this.publishSynchronisationEndedEvent(directory, successful);
                }
                catch (Throwable e) {
                    try {
                        this.finishSynchronisationAsFailedIfWasNotFinishedAlready(directory, e);
                        Throwables.propagateIfPossible((Throwable)e, DirectoryNotFoundException.class, OperationFailedException.class);
                    }
                    catch (Throwable throwable) {
                        throw throwable;
                    }
                    finally {
                        this.publishSynchronisationEndedEvent(directory, successful);
                    }
                }
            }
            finally {
                lock.unlock();
            }
        } else {
            log.debug("directory [ {} ] already synchronising", (Object)directoryId);
        }
    }

    @VisibleForTesting
    void finishSynchronisationAsFailedIfWasNotFinishedAlready(Directory directory, Throwable e) {
        DirectorySynchronisationRoundInformation activeRound = this.synchronisationStatusManager.getDirectorySynchronisationInformation(directory).getActiveRound();
        if (activeRound != null) {
            this.synchronisationStatusManager.syncFailure(directory.getId(), SynchronisationMode.FULL, e);
            this.synchronisationStatusManager.syncFinished(directory.getId(), this.resolveKey(activeRound, false), (List)ImmutableList.of());
        }
    }

    @VisibleForTesting
    void finishSynchronisationIfWasNotFinishedAlready(Directory directory) {
        DirectorySynchronisationRoundInformation activeRound = this.synchronisationStatusManager.getDirectorySynchronisationInformation(directory).getActiveRound();
        if (activeRound != null) {
            this.synchronisationStatusManager.syncFinished(directory.getId(), this.resolveKey(activeRound, true), (List)ImmutableList.of());
        }
    }

    private SynchronisationStatusKey resolveKey(DirectorySynchronisationRoundInformation activeRound, boolean successful) {
        return SynchronisationStatusKey.fromKey((String)activeRound.getStatusKey()).filter(SynchronisationStatusKey::isFinal).orElse(successful ? SynchronisationStatusKey.SUCCESS_FULL : SynchronisationStatusKey.FAILURE);
    }

    private void publishSynchronisationEndedEvent(Directory directory, boolean wasSuccessful) {
        try {
            this.syncEventHelper.publishDirectorySynchronisationEvent(this, directory, wasSuccessful, null);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not publish synchronisation ended event", e);
        }
    }

    @Override
    public boolean isSynchronising(long directoryId) throws DirectoryNotFoundException {
        return this.synchronisationStatusManager.getDirectorySynchronisationInformation(directoryId).isSynchronising();
    }

    @EventListener
    public void handleEvent(DirectoryUpdatedEvent event) {
        try {
            DirectoryImpl directory = new DirectoryImpl(this.directoryDao.findById(event.getDirectoryId()));
            directory.setAttribute("configuration.change.timestamp", Long.toString(System.currentTimeMillis()));
            this.directoryDao.update((Directory)directory);
        }
        catch (DirectoryNotFoundException directoryNotFoundException) {
            // empty catch block
        }
    }
}

