/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DirectorySynchronisationStatusImpl
 *  com.atlassian.crowd.directory.DirectorySynchronisationStatusImpl$Builder
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.embedded.spi.DirectorySynchronisationStatusDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.crowd.model.directory.DirectorySynchronisationStatus
 *  com.atlassian.crowd.model.directory.SynchronisationStatusKey
 *  com.atlassian.crowd.service.cluster.ClusterNode
 *  com.atlassian.crowd.service.cluster.ClusterService
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.directory.DirectorySynchronisationStatusImpl;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.embedded.spi.DirectorySynchronisationStatusDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.manager.directory.DirectorySynchronisationInformationStore;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.mapper.DirectorySynchronisationStatusMapper;
import com.atlassian.crowd.model.directory.DirectorySynchronisationStatus;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;
import com.atlassian.crowd.service.cluster.ClusterNode;
import com.atlassian.crowd.service.cluster.ClusterService;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class InDatabaseDirectorySynchronisationInformationStore
implements DirectorySynchronisationInformationStore {
    private static final Logger logger = LoggerFactory.getLogger(InDatabaseDirectorySynchronisationInformationStore.class);
    private final DirectorySynchronisationStatusDao statusDao;
    private final DirectoryDao directoryDao;
    private final ClusterService clusterService;

    public InDatabaseDirectorySynchronisationInformationStore(DirectorySynchronisationStatusDao statusDao, DirectoryDao directoryDao, ClusterService clusterService) {
        this.statusDao = statusDao;
        this.directoryDao = directoryDao;
        this.clusterService = clusterService;
    }

    @Override
    public DirectorySynchronisationRoundInformation getActive(long directoryId) {
        Optional statusForActiveRound = this.statusDao.findActiveForDirectory(directoryId);
        if (statusForActiveRound.isPresent()) {
            DirectorySynchronisationStatus st = (DirectorySynchronisationStatus)statusForActiveRound.get();
            return DirectorySynchronisationStatusMapper.mapDirectoryStatusToRoundInformation(st);
        }
        return null;
    }

    @Override
    public Optional<DirectorySynchronisationRoundInformation> getLast(long directoryId) {
        Optional statusForLastRound = this.statusDao.findLastForDirectory(directoryId);
        if (statusForLastRound.isPresent()) {
            DirectorySynchronisationStatus st = (DirectorySynchronisationStatus)statusForLastRound.get();
            logger.debug("Successfully restored last synchronisation status for directory {}", (Object)directoryId);
            return Optional.of(DirectorySynchronisationStatusMapper.mapDirectoryStatusToRoundInformation(st));
        }
        logger.debug("Didn't find status of last synchronisation for directory {}, that's normal for the very first synchronisation", (Object)directoryId);
        return Optional.empty();
    }

    @Override
    public void clear(long directoryId) {
        this.statusDao.removeStatusesForDirectory(Long.valueOf(directoryId));
        logger.debug("Cleared synchronisation statuses for directory {}", (Object)directoryId);
    }

    @Override
    public void clear() {
        this.statusDao.removeAll();
        logger.debug("Removed all synchronisation statuses");
    }

    @Override
    public void syncStatus(long directoryId, String statusKey, List<Serializable> parameters) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void syncStatus(long directoryId, SynchronisationStatusKey statusKey, List<Serializable> parameters) {
        Optional previous = this.statusDao.findActiveForDirectory(directoryId);
        try {
            if (previous.isPresent()) {
                this.statusDao.update((DirectorySynchronisationStatus)this.getStatusBuilder((DirectorySynchronisationStatus)previous.get()).setStatus(statusKey, parameters).build());
            } else {
                logger.info("Got synchronisation status update for directory {} with status {}, but didn't find any active status in the database, creating new record instead", (Object)directoryId, (Object)statusKey);
                this.statusDao.add((DirectorySynchronisationStatus)this.getStatusBuilder().setDirectory(this.directoryDao.findById(directoryId)).setStartTimestamp(Long.valueOf(System.currentTimeMillis())).setStatus(statusKey, parameters).build());
            }
        }
        catch (DirectoryNotFoundException | ObjectNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void syncStarted(long directoryId, long timestamp) {
        try {
            Optional active = this.statusDao.findActiveForDirectory(directoryId);
            if (active.isPresent()) {
                logger.warn("Found active synchronisation status during start of new synchronisation. This may indicate that the previous synchronisation didn't end up correctly");
                this.statusDao.update((DirectorySynchronisationStatus)this.getStatusBuilder((DirectorySynchronisationStatus)active.get()).setStartTimestamp(Long.valueOf(timestamp)).setEndTimestamp(null).setStatus(SynchronisationStatusKey.STARTED, Collections.emptyList()).build());
            } else {
                this.statusDao.add((DirectorySynchronisationStatus)this.getStatusBuilder().setDirectory(this.directoryDao.findById(directoryId)).setStartTimestamp(Long.valueOf(timestamp)).setStatus(SynchronisationStatusKey.STARTED, Collections.emptyList()).build());
            }
        }
        catch (DirectoryNotFoundException | ObjectNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void syncFailure(long directoryId, SynchronisationMode syncMode, String failureReason) {
        Optional previous = this.statusDao.findActiveForDirectory(directoryId);
        try {
            if (previous.isPresent()) {
                this.statusDao.update((DirectorySynchronisationStatus)this.getStatusBuilder((DirectorySynchronisationStatus)previous.get()).setSyncError(syncMode, failureReason).build());
            } else {
                logger.info("Got synchronisation failure for directory {}, but didn't find any active status in the database, creating new record instead", (Object)directoryId);
                this.statusDao.add((DirectorySynchronisationStatus)this.getStatusBuilder().setDirectory(this.directoryDao.findById(directoryId)).setStartTimestamp(Long.valueOf(System.currentTimeMillis())).setStatus(SynchronisationStatusKey.STARTED, Collections.emptyList()).setSyncError(syncMode, failureReason).build());
            }
        }
        catch (DirectoryNotFoundException | ObjectNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void syncFinished(long directoryId, long timestamp, SynchronisationStatusKey statusKey, List<Serializable> parameters) {
        Optional active = this.statusDao.findActiveForDirectory(directoryId);
        try {
            if (active.isPresent()) {
                DirectorySynchronisationStatus status = (DirectorySynchronisationStatus)active.get();
                this.statusDao.removeAllExcept(directoryId, status.getId().intValue());
                this.statusDao.update((DirectorySynchronisationStatus)this.getStatusBuilder((DirectorySynchronisationStatus)active.get()).setEndTimestamp(Long.valueOf(timestamp)).setStatus(statusKey, parameters).build());
            } else {
                logger.warn("Didn't find active synchronisation status during finish of the synchronisation");
                this.statusDao.removeStatusesForDirectory(Long.valueOf(directoryId));
                this.statusDao.add((DirectorySynchronisationStatus)this.getStatusBuilder().setDirectory(this.directoryDao.findById(directoryId)).setStartTimestamp(Long.valueOf(timestamp)).setEndTimestamp(Long.valueOf(timestamp)).setStatus(statusKey, parameters).build());
            }
        }
        catch (DirectoryNotFoundException | ObjectNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<DirectorySynchronisationStatus> getStalledSynchronizations() {
        if (!this.clusterService.isAvailable()) {
            logger.debug("Ran InDatabaseDirectorySynchronisationInformationStore#getStalledSynchronizations in non-cluster configuration");
            return ImmutableList.of();
        }
        Set activeNodesIds = this.clusterService.getInformation().getNodes().stream().map(ClusterNode::getNodeId).collect(Collectors.toSet());
        if (activeNodesIds.isEmpty()) {
            logger.warn("Crowd is running in cluster configuration but wasn't able to find any active nodes");
            return ImmutableList.of();
        }
        return this.statusDao.findActiveSyncsWhereNodeIdNotIn(activeNodesIds);
    }

    private DirectorySynchronisationStatusImpl.Builder getStatusBuilder() {
        return this.updateNodeInfo(DirectorySynchronisationStatusImpl.builder());
    }

    private DirectorySynchronisationStatusImpl.Builder getStatusBuilder(DirectorySynchronisationStatus previousStatus) {
        return this.updateNodeInfo(DirectorySynchronisationStatusImpl.builder((DirectorySynchronisationStatus)previousStatus));
    }

    private DirectorySynchronisationStatusImpl.Builder updateNodeInfo(DirectorySynchronisationStatusImpl.Builder builder) {
        Optional clusterNode = this.clusterService.getClusterNode();
        return builder.setNodeId((String)clusterNode.map(ClusterNode::getNodeId).orElse(null)).setNodeName((String)clusterNode.map(ClusterNode::getNodeName).orElse(null));
    }
}

