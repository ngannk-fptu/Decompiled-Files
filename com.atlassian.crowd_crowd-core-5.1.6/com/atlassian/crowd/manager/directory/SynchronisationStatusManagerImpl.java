/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.event.migration.XMLRestoreStartedEvent
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.crowd.model.directory.DirectorySynchronisationStatus
 *  com.atlassian.crowd.model.directory.SynchronisationStatusKey
 *  com.atlassian.crowd.service.cluster.ClusterNode
 *  com.atlassian.crowd.service.cluster.ClusterService
 *  com.atlassian.crowd.util.ExceptionUtils
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.event.migration.XMLRestoreStartedEvent;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.directory.DirectorySynchronisationInformationStore;
import com.atlassian.crowd.manager.directory.DirectorySynchronisationTokenStore;
import com.atlassian.crowd.manager.directory.InternalSynchronisationStatusManager;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.model.directory.DirectorySynchronisationStatus;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;
import com.atlassian.crowd.service.cluster.ClusterNode;
import com.atlassian.crowd.service.cluster.ClusterService;
import com.atlassian.crowd.util.ExceptionUtils;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.io.Serializable;
import java.time.Clock;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class SynchronisationStatusManagerImpl
implements InternalSynchronisationStatusManager {
    private static final Logger logger = LoggerFactory.getLogger(SynchronisationStatusManagerImpl.class);
    private final DirectorySynchronisationInformationStore store;
    private final DirectoryDao directoryDao;
    private final Clock clock;
    private final DirectorySynchronisationTokenStore directorySynchronisationTokenStore;
    private final ClusterService clusterService;

    public SynchronisationStatusManagerImpl(DirectorySynchronisationInformationStore store, EventPublisher eventPublisher, DirectoryDao directoryDao, Clock clock, DirectorySynchronisationTokenStore directorySynchronisationTokenStore, ClusterService clusterService) {
        this.store = store;
        this.directoryDao = directoryDao;
        this.clock = clock;
        this.directorySynchronisationTokenStore = directorySynchronisationTokenStore;
        this.clusterService = clusterService;
        eventPublisher.register((Object)this);
    }

    public void syncStarted(Directory directory) {
        this.store.syncStarted(directory.getId(), this.clock.millis());
    }

    public void syncStatus(long directoryId, String key, Serializable ... parameters) {
        try {
            Optional maybeEnum = SynchronisationStatusKey.fromKey((String)key);
            if (maybeEnum.isPresent()) {
                this.store.syncStatus(directoryId, (SynchronisationStatusKey)maybeEnum.get(), Arrays.asList(parameters));
            } else {
                this.store.syncStatus(directoryId, key, Arrays.asList(parameters));
            }
        }
        catch (Exception e) {
            logger.warn("Could not update synchronisation status for directory {}, status {}, status parameters {}", new Object[]{directoryId, key, parameters, e});
        }
    }

    public void syncStatus(long directoryId, SynchronisationStatusKey statusKey, List<Serializable> parameters) {
        try {
            this.store.syncStatus(directoryId, statusKey, parameters);
        }
        catch (Exception e) {
            logger.warn("Could not update synchronisation status for directory {}, status {}, status parameters {}", new Object[]{directoryId, statusKey, parameters, e});
        }
    }

    public void syncFinished(long directoryId) {
        DirectorySynchronisationRoundInformation active = this.store.getActive(directoryId);
        if (active != null) {
            SynchronisationStatusKey key = (SynchronisationStatusKey)SynchronisationStatusKey.fromKey((String)active.getStatusKey()).orElseThrow(() -> new IllegalStateException("Can't finish synchronisation status"));
            this.store.syncFinished(directoryId, this.clock.millis(), key, active.getStatusParameters());
        }
    }

    public void syncFailure(long directoryId, SynchronisationMode syncMode, Throwable throwable) {
        String errorMessage = ExceptionUtils.getMessageWithValidDbCharacters((Throwable)throwable);
        try {
            this.store.syncFailure(directoryId, syncMode, errorMessage);
        }
        catch (Exception e) {
            logger.error("Could not store sync failure for directory {}, syncMode '{}', sync error: '{}'.", new Object[]{directoryId, syncMode, errorMessage, e});
        }
    }

    public void syncFinished(long directoryId, SynchronisationStatusKey statusKey, List<Serializable> parameters) {
        this.store.syncFinished(directoryId, this.clock.millis(), statusKey, parameters);
    }

    public DirectorySynchronisationInformation getDirectorySynchronisationInformation(Directory directory) {
        DirectorySynchronisationRoundInformation active = this.getActive(directory.getId());
        DirectorySynchronisationRoundInformation last = this.store.getLast(directory.getId()).orElse(null);
        if (active == null && last == null) {
            long startTime = NumberUtils.toLong((String)directory.getValue("com.atlassian.crowd.directory.sync.laststartsynctime"), (long)0L);
            long duration = NumberUtils.toLong((String)directory.getValue("com.atlassian.crowd.directory.sync.lastdurationms"), (long)0L);
            Optional node = this.clusterService.getClusterNode();
            DirectorySynchronisationRoundInformation lastRound = startTime == 0L ? null : DirectorySynchronisationRoundInformation.builder().setStartTime(startTime).setDurationMs(duration).setNodeId((String)node.map(ClusterNode::getNodeId).orElse(null)).setNodeName((String)node.map(ClusterNode::getNodeName).orElse(null)).build();
            return new DirectorySynchronisationInformation(lastRound, null);
        }
        if (active == null) {
            return new DirectorySynchronisationInformation(last, null);
        }
        if (last != null && last.getStartTime() == active.getStartTime()) {
            return new DirectorySynchronisationInformation(last, this.getActive(directory.getId()));
        }
        return new DirectorySynchronisationInformation(last, active);
    }

    private DirectorySynchronisationRoundInformation getActive(long directoryId) {
        DirectorySynchronisationRoundInformation original = this.store.getActive(directoryId);
        if (original == null) {
            return null;
        }
        return DirectorySynchronisationRoundInformation.builder((DirectorySynchronisationRoundInformation)original).setDurationMs(this.clock.millis() - original.getStartTime()).build();
    }

    @Transactional
    public DirectorySynchronisationInformation getDirectorySynchronisationInformation(long directoryId) throws DirectoryNotFoundException {
        return this.getDirectorySynchronisationInformation(this.directoryDao.findById(directoryId));
    }

    @Transactional
    public String getLastSynchronisationTokenForDirectory(long directoryId) {
        return this.directorySynchronisationTokenStore.getLastSynchronisationTokenForDirectory(directoryId);
    }

    @Transactional
    public void storeSynchronisationTokenForDirectory(long directoryId, String synchronisationToken) {
        this.directorySynchronisationTokenStore.storeSynchronisationTokenForDirectory(directoryId, synchronisationToken);
    }

    public void removeStatusesForDirectory(long directoryId) {
        this.store.clear(directoryId);
    }

    @Override
    public Collection<DirectorySynchronisationStatus> getStalledSynchronizations() {
        return this.store.getStalledSynchronizations();
    }

    @Transactional
    public void clearSynchronisationTokenForDirectory(long directoryId) {
        this.directorySynchronisationTokenStore.clearSynchronisationTokenForDirectory(directoryId);
    }

    @EventListener
    public void handleEvent(XMLRestoreStartedEvent event) {
        this.store.clear();
    }
}

