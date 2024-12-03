/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.MapiTaskMapping;
import com.atlassian.migration.agent.store.MapiTaskMappingStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.QueryBuilder;
import java.util.List;
import java.util.Optional;

public class MapiTaskMappingStoreImpl
implements MapiTaskMappingStore {
    private final EntityManagerTemplate tmpl;

    public MapiTaskMappingStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public void createTaskMapping(MapiTaskMapping mapiTaskMapping) {
        this.tmpl.persist(mapiTaskMapping);
        this.tmpl.flush();
    }

    @Override
    public void update(MapiTaskMapping mapiTaskMapping) {
        this.tmpl.merge(mapiTaskMapping);
    }

    @Override
    public List<MapiTaskMapping> getPendingTasks() {
        return this.tmpl.query(MapiTaskMapping.class, "select mapiTaskMapping from MapiTaskMapping mapiTaskMapping where mapiTaskMapping.status='CHECKS_IN_PROGRESS' order by mapiTaskMapping.lastUpdate").list();
    }

    @Override
    public MapiTaskMapping getTaskMapping(String planId, Optional<List<String>> statuses, Optional<List<String>> commandNames) {
        String jpql = "select mapiTaskMapping from MapiTaskMapping mapiTaskMapping where mapiTaskMapping.planId=:planId" + this.addStatusFilter(statuses) + this.addCommandNameFilter(commandNames) + " order by mapiTaskMapping.lastUpdate desc";
        QueryBuilder<MapiTaskMapping> queryBuilder = this.tmpl.query(MapiTaskMapping.class, jpql).param("planId", (Object)planId);
        if (statuses.isPresent() && !statuses.get().isEmpty()) {
            queryBuilder = queryBuilder.param("statuses", statuses.get());
        }
        if (commandNames.isPresent() && !commandNames.get().isEmpty()) {
            queryBuilder = queryBuilder.param("commandNames", commandNames.get());
        }
        return queryBuilder.first().isPresent() ? queryBuilder.first().get() : null;
    }

    private String addStatusFilter(Optional<List<String>> statuses) {
        if (!statuses.isPresent() || statuses.get().isEmpty()) {
            return "";
        }
        return " and mapiTaskMapping.status in :statuses";
    }

    private String addCommandNameFilter(Optional<List<String>> commandNames) {
        if (!commandNames.isPresent() || commandNames.get().isEmpty()) {
            return "";
        }
        return " and mapiTaskMapping.commandName in :commandNames";
    }
}

