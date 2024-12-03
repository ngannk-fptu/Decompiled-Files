/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.entity.MapiTaskMapping;
import com.atlassian.migration.agent.mapi.entity.MapiTaskStatus;
import com.atlassian.migration.agent.store.MapiTaskMappingStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MapiTaskMappingService {
    private final PluginTransactionTemplate ptx;
    private final MapiTaskMappingStore mapiTaskMappingStore;

    public MapiTaskMappingService(PluginTransactionTemplate ptx, MapiTaskMappingStore mapiTaskMappingStore) {
        this.ptx = ptx;
        this.mapiTaskMappingStore = mapiTaskMappingStore;
    }

    public void createTaskMapping(MapiTaskMapping mapiTaskMapping) {
        this.ptx.write(() -> {
            mapiTaskMapping.setLastUpdate(Instant.now());
            this.mapiTaskMappingStore.createTaskMapping(mapiTaskMapping);
            return null;
        });
    }

    public void updateTimestamp(MapiTaskMapping mapiTaskMapping) {
        this.ptx.write(() -> {
            mapiTaskMapping.setLastUpdate(Instant.now());
            this.mapiTaskMappingStore.update(mapiTaskMapping);
            return null;
        });
    }

    public void updateTaskMappingStatus(MapiTaskMapping mapiTaskMapping, MapiTaskStatus mapiTaskStatus) {
        this.ptx.write(() -> {
            mapiTaskMapping.setLastUpdate(Instant.now());
            mapiTaskMapping.setStatus(mapiTaskStatus.name());
            this.mapiTaskMappingStore.update(mapiTaskMapping);
            return null;
        });
    }

    public List<MapiTaskMapping> getPendingTasks() {
        return this.ptx.read(() -> this.mapiTaskMappingStore.getPendingTasks());
    }

    public Optional<MapiTaskMapping> getTaskMapping(String planId, Optional<List<MapiTaskStatus>> statuses, Optional<List<String>> commandNames) {
        return this.ptx.read(() -> {
            try {
                Optional<List<String>> statusesAsString = Optional.empty();
                if (statuses.isPresent()) {
                    statusesAsString = Optional.of(((List)statuses.get()).stream().map(Enum::name).collect(Collectors.toList()));
                }
                return Optional.of(this.mapiTaskMappingStore.getTaskMapping(planId, statusesAsString, commandNames));
            }
            catch (Exception e) {
                return Optional.empty();
            }
        });
    }
}

