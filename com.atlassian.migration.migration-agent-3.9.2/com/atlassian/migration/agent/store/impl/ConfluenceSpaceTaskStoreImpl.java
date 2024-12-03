/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.AbstractSpaceTask;
import com.atlassian.migration.agent.entity.ConfluenceSpaceTask;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.store.ConfluenceSpaceTaskStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceSpaceTaskStoreImpl
implements ConfluenceSpaceTaskStore {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ConfluenceSpaceTaskStoreImpl.class);
    private final EntityManagerTemplate tmpl;
    private final MigrationAgentConfiguration config;

    public ConfluenceSpaceTaskStoreImpl(EntityManagerTemplate tmpl, MigrationAgentConfiguration config) {
        this.tmpl = tmpl;
        this.config = config;
    }

    @Override
    public Map<String, Progress> getLatestSpaceProgress(String cloudId, Collection<String> spaceKeys) {
        ArrayList queryResult = new ArrayList(spaceKeys.size());
        List partitionedKeys = Lists.partition((List)Lists.newLinkedList(spaceKeys), (int)this.config.getDBQueryParameterLimit());
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        String selectLatestTask = "select max(t.plan.createdTime) from ConfluenceSpaceTask t where t.spaceKey = task.spaceKey and t.progress.status != :stopped";
        try {
            Future tasks = forkJoinPool.submit(() -> partitionedKeys.parallelStream().flatMap(partition -> this.tmpl.query(ConfluenceSpaceTask.class, "select task from ConfluenceSpaceTask task where task.spaceKey in :spaceKeys and task.plan.cloudSite.id = :cloudId and task.plan.createdTime = (select max(t.plan.createdTime) from ConfluenceSpaceTask t where t.spaceKey = task.spaceKey and t.progress.status != :stopped)").param("cloudId", (Object)cloudId).param("stopped", (Object)ExecutionStatus.STOPPED).param("spaceKeys", partition).list().stream()).collect(Collectors.toList()));
            queryResult.addAll((Collection)((ForkJoinTask)tasks).get());
        }
        catch (Exception e) {
            log.info("error at getLatestSpaceProgress", (Throwable)e);
            throw new RuntimeException(e);
        }
        finally {
            forkJoinPool.shutdown();
        }
        return queryResult.stream().collect(Collectors.toMap(AbstractSpaceTask::getSpaceKey, Task::getProgress, (a, b) -> a));
    }
}

