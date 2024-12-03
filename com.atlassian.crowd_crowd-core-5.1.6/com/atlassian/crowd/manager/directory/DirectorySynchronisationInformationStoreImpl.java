/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation$Builder
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.crowd.model.directory.DirectorySynchronisationStatus
 *  com.atlassian.crowd.model.directory.SynchronisationStatusKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.manager.directory.DirectorySynchronisationInformationStore;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.model.directory.DirectorySynchronisationStatus;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class DirectorySynchronisationInformationStoreImpl
implements DirectorySynchronisationInformationStore {
    private static final Logger log = LoggerFactory.getLogger(DirectorySynchronisationInformationStoreImpl.class);
    private static final DirectorySynchronisationInformation EMPTY_INFO = new DirectorySynchronisationInformation(null, null);
    private final ConcurrentMap<Long, DirectorySynchronisationInformation> syncStatus;

    public DirectorySynchronisationInformationStoreImpl() {
        this(new ConcurrentHashMap<Long, DirectorySynchronisationInformation>());
    }

    public DirectorySynchronisationInformationStoreImpl(ConcurrentMap<Long, DirectorySynchronisationInformation> syncStatus) {
        this.syncStatus = syncStatus;
    }

    @Override
    public DirectorySynchronisationRoundInformation getActive(long directoryId) {
        return this.get(directoryId).getActiveRound();
    }

    @Override
    public Optional<DirectorySynchronisationRoundInformation> getLast(long directoryId) {
        return Optional.ofNullable(this.get(directoryId).getLastRound());
    }

    @Override
    public void clear(long directoryId) {
        this.syncStatus.remove(directoryId);
    }

    @Override
    public void clear() {
        this.syncStatus.clear();
    }

    @Override
    public void syncStatus(long directoryId, String statusKey, List<Serializable> parameters) {
        this.updateActiveRoundUsingBuilder(directoryId, builder -> builder.setStatusKey(statusKey).setStatusParameters(parameters));
    }

    @Override
    public void syncStatus(long directoryId, SynchronisationStatusKey statusKey, List<Serializable> parameters) {
        this.syncStatus(directoryId, statusKey.getI18Key(), parameters);
    }

    @Override
    public void syncStarted(long directoryId, long timestamp) {
        this.updateActiveRound(directoryId, ignore -> new DirectorySynchronisationRoundInformation(timestamp, -1L, SynchronisationStatusKey.STARTED.getI18Key(), Collections.emptyList()));
    }

    @Override
    public void syncFinished(long directoryId, long timestamp, SynchronisationStatusKey statusKey, List<Serializable> parameters) {
        DirectorySynchronisationRoundInformation current = this.get(directoryId).getActiveRound();
        DirectorySynchronisationRoundInformation.Builder builder = current != null ? DirectorySynchronisationRoundInformation.builder((DirectorySynchronisationRoundInformation)current).setDurationMs(timestamp - current.getStartTime()) : DirectorySynchronisationRoundInformation.builder().setStartTime(timestamp).setDurationMs(0L);
        builder.setStatusKey(statusKey.getI18Key()).setStatusParameters(parameters);
        this.syncStatus.put(directoryId, new DirectorySynchronisationInformation(builder.build(), null));
    }

    @Override
    public void syncFailure(long directoryId, SynchronisationMode syncMode, String failureReason) {
        this.updateActiveRoundUsingBuilder(directoryId, syncMode == SynchronisationMode.INCREMENTAL ? builder -> builder.setIncrementalSyncError(failureReason) : builder -> builder.setFullSyncError(failureReason));
    }

    private void updateActiveRoundUsingBuilder(long directoryId, Consumer<DirectorySynchronisationRoundInformation.Builder> builderConsumer) {
        this.updateActiveRound(directoryId, active -> {
            DirectorySynchronisationRoundInformation.Builder builder = active == null ? DirectorySynchronisationRoundInformation.builder().setStartTime(System.currentTimeMillis()).setDurationMs(-1L) : DirectorySynchronisationRoundInformation.builder((DirectorySynchronisationRoundInformation)active);
            builderConsumer.accept(builder);
            return builder.build();
        });
    }

    private void updateActiveRound(long directoryId, Function<DirectorySynchronisationRoundInformation, DirectorySynchronisationRoundInformation> transformer) {
        DirectorySynchronisationInformation info = this.get(directoryId);
        this.syncStatus.put(directoryId, new DirectorySynchronisationInformation(info.getLastRound(), transformer.apply(info.getActiveRound())));
    }

    @Override
    public Collection<DirectorySynchronisationStatus> getStalledSynchronizations() {
        log.debug("Called non database implementation of getStalledSynchronizations - ignoring");
        return Collections.emptyList();
    }

    public DirectorySynchronisationInformation get(long directoryId) {
        return this.syncStatus.getOrDefault(directoryId, EMPTY_INFO);
    }
}

