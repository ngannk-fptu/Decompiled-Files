/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.persistence;

import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public interface TaskMonitorRepository<M extends TaskMonitor<?>> {
    public boolean storeTaskMonitor(M var1);

    public Optional<M> getTaskMonitor(String var1);

    public boolean updateTaskMonitor(M var1);

    public void deleteTaskMonitor(M var1);

    public Collection<M> getRecentTaskMonitors();

    default public Collection<M> getRecentTaskMonitorsByNodeId(String nodeId) {
        return this.getRecentTaskMonitors().stream().filter(tm -> tm.getNodeId().map(nodeId::equals).orElse(false)).collect(Collectors.toList());
    }

    default public Collection<M> getRecentTaskMonitorsByClusteredTaskId(String clusteredTaskId) {
        return this.getRecentTaskMonitors().stream().filter(tm -> tm.getClusteredTaskId().map(clusteredTaskId::equals).orElse(false)).collect(Collectors.toList());
    }
}

