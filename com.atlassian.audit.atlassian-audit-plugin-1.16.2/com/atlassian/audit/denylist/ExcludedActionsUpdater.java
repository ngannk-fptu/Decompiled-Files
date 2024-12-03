/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.Maps
 *  net.java.ao.Query
 */
package com.atlassian.audit.denylist;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.audit.ao.dao.entity.AoExcludedActionsAuditEntity;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.java.ao.Query;

public class ExcludedActionsUpdater {
    private final ActiveObjects ao;
    private final TransactionTemplate transactionTemplate;

    public ExcludedActionsUpdater(ActiveObjects ao, TransactionTemplate transactionTemplate) {
        this.ao = Objects.requireNonNull(ao);
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate);
    }

    public void updateExcludedActions(List<String> eventsToAdd, List<String> eventsToDelete) {
        this.transactionTemplate.execute(() -> {
            eventsToAdd.forEach(this::addExcludedEvent);
            eventsToDelete.forEach(this::deleteExcludedEvent);
            return null;
        });
    }

    public void addExcludedEvent(String event) {
        this.transactionTemplate.execute(() -> {
            if (this.isUnique(event)) {
                HashMap values = Maps.newHashMap();
                values.put("ACTION", event);
                this.ao.create(AoExcludedActionsAuditEntity.class, (Map)values);
            }
            return null;
        });
    }

    public void deleteExcludedEvent(String event) {
        this.transactionTemplate.execute(() -> {
            this.ao.deleteWithSQL(AoExcludedActionsAuditEntity.class, String.format("%s = ?", "ACTION"), new Object[]{event});
            return null;
        });
    }

    public void replaceExcludedActions(List<String> events) {
        this.transactionTemplate.execute(() -> {
            this.ao.deleteWithSQL(AoExcludedActionsAuditEntity.class, "1=1", new Object[0]);
            this.ao.create(AoExcludedActionsAuditEntity.class, events.stream().map(event -> {
                HashMap values = Maps.newHashMap();
                values.put("ACTION", event);
                return values;
            }).collect(Collectors.toList()));
            return null;
        });
    }

    private boolean isUnique(String event) {
        return ((AoExcludedActionsAuditEntity[])this.ao.find(AoExcludedActionsAuditEntity.class, Query.select().where(String.format("%s = ?", "ACTION"), new Object[]{event}))).length == 0;
    }
}

