/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 */
package com.atlassian.troubleshooting.stp.persistence;

import com.atlassian.troubleshooting.stp.persistence.TaskMonitorRepository;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InMemoryTaskMonitorRepository<M extends TaskMonitor<?>>
implements TaskMonitorRepository<M> {
    private final Cache<String, TaskMonitor<?>> inMemoryStorage;

    public InMemoryTaskMonitorRepository(TaskType taskType, long expireAfterMs) {
        this.inMemoryStorage = CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(100L).expireAfterWrite(expireAfterMs, TimeUnit.MILLISECONDS).build();
    }

    @Override
    public boolean storeTaskMonitor(M taskMonitor) {
        return Optional.ofNullable(this.inMemoryStorage.getIfPresent((Object)taskMonitor.getTaskId())).map(v -> false).orElseGet(() -> {
            this.inMemoryStorage.put((Object)taskMonitor.getTaskId(), (Object)taskMonitor);
            return true;
        });
    }

    @Override
    public Optional<M> getTaskMonitor(String taskId) {
        return Optional.ofNullable(this.inMemoryStorage.getIfPresent((Object)taskId)).map(tm -> tm);
    }

    @Override
    public boolean updateTaskMonitor(M taskMonitor) {
        this.inMemoryStorage.put((Object)taskMonitor.getTaskId(), taskMonitor);
        return true;
    }

    @Override
    public void deleteTaskMonitor(M taskMonitor) {
        this.inMemoryStorage.invalidate((Object)taskMonitor.getTaskId());
    }

    @Override
    public Collection<M> getRecentTaskMonitors() {
        return this.inMemoryStorage.asMap().values().stream().map(tm -> tm).collect(Collectors.toList());
    }
}

