/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.Maps
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.audit.ao.dao.entity.AoAuditEntityAction;
import com.atlassian.audit.ao.dao.entity.AoCachedActionEntity;
import com.atlassian.audit.model.AuditAction;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class AoCachedActionDao {
    private static final Logger log = LoggerFactory.getLogger(AoCachedActionDao.class);
    private static final String ACTION_LIMIT_PROPERTY_KEY = "plugin.audit.action.limit";
    private static final int DEFAULT_ACTION_LIMIT = 1000;
    private final ActiveObjects ao;
    private final PropertiesProvider propertiesProvider;
    private final TransactionTemplate transactionTemplate;

    public AoCachedActionDao(ActiveObjects ao, PropertiesProvider propertiesProvider, TransactionTemplate transactionTemplate) {
        this.ao = Objects.requireNonNull(ao, "ao");
        this.propertiesProvider = Objects.requireNonNull(propertiesProvider, "propertiesProvider");
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate");
    }

    private static Map<String, Object> mapToCachedAction(AuditAction auditAction) {
        HashMap actionRow = Maps.newHashMap();
        actionRow.put("ACTION", auditAction.getAction());
        actionRow.put("ACTION_T_KEY", auditAction.getActionI18nKey().orElse(null));
        return AoCachedActionDao.castToMapStringObject(actionRow);
    }

    private static Map<String, Object> castToMapStringObject(Map<String, String> row) {
        HashMap castMap = Maps.newHashMap();
        castMap.putAll(row);
        return castMap;
    }

    public Set<AuditAction> getActions() {
        AoCachedActionEntity[] cachedActionEntities = (AoCachedActionEntity[])this.transactionTemplate.execute(() -> (AoCachedActionEntity[])this.ao.find(AoCachedActionEntity.class));
        Set<AuditAction> auditActions = Arrays.stream(cachedActionEntities).map(row -> new AuditAction(row.getAction(), row.getActionI18nKey())).collect(Collectors.toSet());
        this.logIfTheresTooManyActions(auditActions.size());
        return auditActions;
    }

    public Set<AuditAction> getActionsFromSourceOfTruth() {
        return Arrays.stream((Object[])this.transactionTemplate.execute(() -> (AoAuditEntityAction[])this.ao.find(AoAuditEntityAction.class, Query.select((String)String.format("%s, %s, max(%s) as %s", "ACTION", "ACTION_T_KEY", "ID", "ID")).group(String.format("%s, %s", "ACTION", "ACTION_T_KEY"))))).filter(row -> row.getAction() != null).map(row -> new AuditAction(row.getAction(), row.getActionI18nKey())).collect(Collectors.toSet());
    }

    public void truncateAndSave(Set<AuditAction> newActions) {
        Objects.requireNonNull(newActions, "newActions");
        this.transactionTemplate.execute(() -> {
            log.info("Removing all cached audit summaries (AKA actions) from the DB");
            this.ao.deleteWithSQL(AoCachedActionEntity.class, String.format("%s LIKE '%%'", "ACTION"), new Object[0]);
            log.info("Saving cached audit summaries (AKA actions) to the DB");
            this.ao.create(AoCachedActionEntity.class, newActions.stream().map(AoCachedActionDao::mapToCachedAction).collect(Collectors.toList()));
            return null;
        });
    }

    public void save(Set<AuditAction> actions) {
        Objects.requireNonNull(actions, "actions");
        if (actions.isEmpty()) {
            return;
        }
        this.transactionTemplate.execute(() -> {
            this.ao.create(AoCachedActionEntity.class, actions.stream().map(AoCachedActionDao::mapToCachedAction).collect(Collectors.toList()));
            return null;
        });
    }

    private void logIfTheresTooManyActions(Integer numActions) {
        int configuredActionLimit = this.propertiesProvider.getInteger(ACTION_LIMIT_PROPERTY_KEY, 1000);
        if (numActions > configuredActionLimit) {
            log.warn("Detected {} audit summaries (AKA actions) which is greater than the limit of {} this may lead to general performance degradation as audit events are saved and/or while loading the UI.", (Object)numActions, (Object)configuredActionLimit);
        }
    }
}

