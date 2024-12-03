/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.audit.entity.AuditEntity
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.ao.service;

import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.audit.ao.dao.AoCachedActionDao;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.model.AuditAction;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class CachedActionsService {
    private static final Logger log = LoggerFactory.getLogger(CachedActionsService.class);
    private final AoCachedActionDao aoCachedActionDao;
    private final AtomicReference<Set<AuditAction>> preDbWriteCache;

    public CachedActionsService(AoCachedActionDao aoCachedActionDao) {
        this.aoCachedActionDao = Objects.requireNonNull(aoCachedActionDao, "aoCachedActionDao");
        this.preDbWriteCache = new AtomicReference(Collections.emptySet());
    }

    public void saveNewActions(List<AuditEntity> auditEntities) {
        Objects.requireNonNull(auditEntities, "auditEntities");
        Set allActions = auditEntities.stream().filter(auditEntity -> auditEntity.getAuditType().getAction() != null).map(auditEntity -> new AuditAction(auditEntity.getAuditType().getAction(), auditEntity.getAuditType().getActionI18nKey())).collect(Collectors.toSet());
        boolean newActionsExist = allActions.stream().anyMatch(action -> !this.preDbWriteCache.get().contains(action));
        if (!newActionsExist) {
            return;
        }
        this.preDbWriteCache.set(this.aoCachedActionDao.getActions());
        Set<AuditAction> filteredActions = allActions.stream().filter(action -> !this.preDbWriteCache.get().contains(action)).collect(Collectors.toSet());
        if (filteredActions.isEmpty()) {
            return;
        }
        try {
            this.aoCachedActionDao.save(filteredActions);
        }
        catch (Exception exception) {
            log.error("Failed to save new audit summaries (AKA actions) to the database, they were: {}", filteredActions, (Object)exception);
        }
    }

    public void rebuildCache() {
        log.info("Starting to build audit summaries (AKA actions) cache");
        Set<AuditAction> distinctPairs = this.aoCachedActionDao.getActionsFromSourceOfTruth();
        log.info("Found {} distinct audit summaries/actions+translation pairs", (Object)distinctPairs.size());
        this.aoCachedActionDao.truncateAndSave(distinctPairs);
        log.info("Finished building summaries (AKA actions) actions cache");
    }
}

