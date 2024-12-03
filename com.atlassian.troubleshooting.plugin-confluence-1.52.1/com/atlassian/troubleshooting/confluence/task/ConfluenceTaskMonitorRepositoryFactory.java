/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.api.PluginInfo;
import com.atlassian.troubleshooting.stp.task.ClusteredTaskMonitorRepositoryFactory;
import com.atlassian.troubleshooting.stp.task.TaskMonitorFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceTaskMonitorRepositoryFactory
extends ClusteredTaskMonitorRepositoryFactory {
    @Autowired
    public ConfluenceTaskMonitorRepositoryFactory(ActiveObjects activeObjects, ClusterService clusterService, PluginInfo pluginInfo, TaskMonitorFactory taskMonitorFactory) {
        super(activeObjects, clusterService, pluginInfo, taskMonitorFactory);
    }
}

