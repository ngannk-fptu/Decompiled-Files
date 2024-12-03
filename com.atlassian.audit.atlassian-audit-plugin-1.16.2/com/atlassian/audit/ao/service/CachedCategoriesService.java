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
import com.atlassian.audit.ao.dao.AoCachedCategoryDao;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.model.AuditCategory;
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
public class CachedCategoriesService {
    private static final Logger log = LoggerFactory.getLogger(CachedCategoriesService.class);
    private final AoCachedCategoryDao aoCachedCategoryDao;
    private final AtomicReference<Set<AuditCategory>> preDbWriteCache;

    public CachedCategoriesService(AoCachedCategoryDao aoCachedCategoryDao) {
        this.aoCachedCategoryDao = Objects.requireNonNull(aoCachedCategoryDao, "aoCachedCategoryDao");
        this.preDbWriteCache = new AtomicReference(Collections.emptySet());
    }

    public void saveNewCategories(List<AuditEntity> auditEntities) {
        Objects.requireNonNull(auditEntities, "auditEntities");
        Set allCategories = auditEntities.stream().filter(auditEntity -> auditEntity.getAuditType().getCategory() != null).map(auditEntity -> new AuditCategory(auditEntity.getAuditType().getCategory(), auditEntity.getAuditType().getCategoryI18nKey())).collect(Collectors.toSet());
        boolean newCategoriesExist = allCategories.stream().anyMatch(category -> !this.preDbWriteCache.get().contains(category));
        if (!newCategoriesExist) {
            return;
        }
        this.preDbWriteCache.set(this.aoCachedCategoryDao.getCategories());
        Set<AuditCategory> filteredCategories = allCategories.stream().filter(category -> !this.preDbWriteCache.get().contains(category)).collect(Collectors.toSet());
        if (filteredCategories.isEmpty()) {
            return;
        }
        try {
            this.aoCachedCategoryDao.save(filteredCategories);
        }
        catch (Exception exception) {
            log.error("Failed to save new audit categories to the database, they were: {}", filteredCategories, (Object)exception);
        }
    }

    public void rebuildCache() {
        log.info("Starting to build audit categories cache");
        Set<AuditCategory> distinctPairs = this.aoCachedCategoryDao.getCategoriesFromSourceOfTruth();
        log.info("Found {} distinct audit category+translation pairs", (Object)distinctPairs.size());
        this.aoCachedCategoryDao.truncateAndSave(distinctPairs);
        log.info("Finished building audit categories cache");
    }
}

