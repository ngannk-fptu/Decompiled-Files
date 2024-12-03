/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.spi.feature.DatabaseAuditingFeature
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.ao.consumer;

import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.audit.ao.service.CachedActionsService;
import com.atlassian.audit.ao.service.CachedCategoriesService;
import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.spi.feature.DatabaseAuditingFeature;
import java.util.List;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DatabaseAuditConsumer
implements AuditConsumer {
    private static final Logger log = LoggerFactory.getLogger(DatabaseAuditConsumer.class);
    private final AuditEntityDao auditEntityDao;
    private final CachedActionsService cachedActionsService;
    private final CachedCategoriesService cachedCategoriesService;
    private final DatabaseAuditingFeature databaseAuditingFeature;

    public DatabaseAuditConsumer(AuditEntityDao auditEntityDao, CachedActionsService cachedActionsService, CachedCategoriesService cachedCategoriesService, DatabaseAuditingFeature databaseAuditingFeature) {
        this.auditEntityDao = Objects.requireNonNull(auditEntityDao, "auditEntityDao");
        this.cachedActionsService = Objects.requireNonNull(cachedActionsService, "cachedActionsService");
        this.cachedCategoriesService = Objects.requireNonNull(cachedCategoriesService, "cachedCategoriesService");
        this.databaseAuditingFeature = Objects.requireNonNull(databaseAuditingFeature, "databaseAuditingFeature");
    }

    public void accept(List<AuditEntity> entities) {
        Objects.requireNonNull(entities, "entities");
        log.trace("#accept entities.size={} entities={}", (Object)entities.size(), entities);
        this.auditEntityDao.save(entities);
        this.cachedActionsService.saveNewActions(entities);
        this.cachedCategoriesService.saveNewCategories(entities);
    }

    public boolean isEnabled() {
        return this.databaseAuditingFeature.isEnabled();
    }
}

