/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.crowd.model.directory.DirectorySynchronisationStatus
 *  com.atlassian.crowd.model.directory.SynchronisationStatusKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectorySynchronisationUtils;
import com.atlassian.crowd.manager.directory.FailedSynchronisationManager;
import com.atlassian.crowd.manager.directory.InternalSynchronisationStatusManager;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.manager.directory.monitor.poller.DirectoryPollerManager;
import com.atlassian.crowd.model.directory.DirectorySynchronisationStatus;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;
import com.atlassian.crowd.util.DirectorySynchronisationEventHelper;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FailedSynchronisationManagerImpl
implements FailedSynchronisationManager {
    private static final Logger log = LoggerFactory.getLogger(FailedSynchronisationManagerImpl.class);
    private final InternalSynchronisationStatusManager synchronisationStatusManager;
    private final DirectoryPollerManager pollerManager;
    private final ClusterLockService lockService;
    private final DirectoryManager directoryManager;
    private final DirectorySynchronisationEventHelper syncEventHelper;

    public FailedSynchronisationManagerImpl(InternalSynchronisationStatusManager synchronisationStatusManager, DirectoryPollerManager pollerManager, ClusterLockService lockService, DirectoryManager directoryManager, DirectorySynchronisationEventHelper syncEventHelper) {
        this.synchronisationStatusManager = synchronisationStatusManager;
        this.pollerManager = pollerManager;
        this.lockService = lockService;
        this.directoryManager = directoryManager;
        this.syncEventHelper = syncEventHelper;
    }

    @Override
    public void finalizeSynchronisationStatuses() {
        this.directoryManager.findAllDirectories().forEach(directory -> {
            ClusterLock directoryLock = this.lockService.getLockForName(DirectorySynchronisationUtils.getLockName(directory.getId()));
            if (directoryLock.tryLock()) {
                try {
                    DirectorySynchronisationRoundInformation activeStatus = this.synchronisationStatusManager.getDirectorySynchronisationInformation(directory.getId()).getActiveRound();
                    if (activeStatus == null) return;
                    this.finalizeSynchronisationStatusAndPublishAuditEvent((Directory)directory);
                    return;
                }
                catch (DirectoryNotFoundException e) {
                    log.warn("Couldn't check synchronisation status for directory {}", (Object)directory.getId(), (Object)e);
                    return;
                }
                catch (Exception e) {
                    log.warn("Couldn't finish synchronisation status for directory {}", (Object)directory.getId(), (Object)e);
                    return;
                }
                finally {
                    directoryLock.unlock();
                }
            } else {
                log.debug("Not checking directory {}, lock unavailable", (Object)directory.getId());
            }
        });
    }

    private void finalizeSynchronisationStatusAndPublishAuditEvent(Directory directory) {
        log.info("Found not final synchronisation status for directory {}", (Object)directory.getId());
        this.setSynchronisationStatusAndPublishSynchronisationFailedEvent(directory, SynchronisationStatusKey.ABORTED);
        this.synchronisationStatusManager.clearSynchronisationTokenForDirectory(directory.getId());
        log.info("Fixed stale synchronisation status for directory {}", (Object)directory.getId());
    }

    @Override
    public int rescheduleStalledSynchronisations() {
        Collection stalledSyncsDirs = this.synchronisationStatusManager.getStalledSynchronizations().stream().map(DirectorySynchronisationStatus::getDirectory).collect(Collectors.toList());
        AtomicInteger rescheduledSyncs = new AtomicInteger(0);
        if (stalledSyncsDirs.size() > 0) {
            log.info("Found {} stalled synchronisations for directories [ {} ]. Rescheduling them to run again", (Object)stalledSyncsDirs.size(), stalledSyncsDirs.stream().map(Directory::getId).collect(Collectors.toList()));
            stalledSyncsDirs.forEach(directory -> {
                ClusterLock lock = this.lockService.getLockForName(DirectorySynchronisationUtils.getLockName(directory.getId()));
                if (lock.tryLock()) {
                    try {
                        this.setSynchronisationStatusAndPublishSynchronisationFailedEvent((Directory)directory, SynchronisationStatusKey.FAILURE);
                        this.pollerManager.triggerPoll(directory.getId(), SynchronisationMode.FULL);
                        rescheduledSyncs.incrementAndGet();
                    }
                    finally {
                        lock.unlock();
                    }
                } else {
                    log.debug("Couldn't acquire cluster lock for directory {} - ignoring", directory);
                }
            });
        } else {
            log.debug("Didn't find any stalled synchronisation");
        }
        return rescheduledSyncs.get();
    }

    private void setSynchronisationStatusAndPublishSynchronisationFailedEvent(Directory directory, SynchronisationStatusKey key) {
        this.synchronisationStatusManager.syncFinished(directory.getId(), key, Collections.emptyList());
        try {
            this.syncEventHelper.publishFailedDirectorySynchronisationEvent(this, directory, -1L);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

