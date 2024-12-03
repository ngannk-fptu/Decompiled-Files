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
import com.atlassian.audit.ao.dao.entity.AoAuditEntityCategory;
import com.atlassian.audit.ao.dao.entity.AoCachedCategoryEntity;
import com.atlassian.audit.model.AuditCategory;
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
public class AoCachedCategoryDao {
    private static final Logger log = LoggerFactory.getLogger(AoCachedCategoryDao.class);
    private static final String CATEGORY_LIMIT_PROPERTY_KEY = "plugin.audit.category.limit";
    private static final int DEFAULT_CATEGORY_LIMIT = 1000;
    private final ActiveObjects ao;
    private final PropertiesProvider propertiesProvider;
    private final TransactionTemplate transactionTemplate;

    public AoCachedCategoryDao(ActiveObjects ao, PropertiesProvider propertiesProvider, TransactionTemplate transactionTemplate) {
        this.ao = Objects.requireNonNull(ao, "ao");
        this.propertiesProvider = Objects.requireNonNull(propertiesProvider, "propertiesProvider");
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate");
    }

    private static Map<String, Object> mapToCachedCategory(AuditCategory auditCategory) {
        HashMap categoryRow = Maps.newHashMap();
        categoryRow.put("CATEGORY", auditCategory.getCategory());
        categoryRow.put("CATEGORY_T_KEY", auditCategory.getCategoryI18nKey().orElse(null));
        return AoCachedCategoryDao.castToMapStringObject(categoryRow);
    }

    private static Map<String, Object> castToMapStringObject(Map<String, String> row) {
        HashMap castMap = Maps.newHashMap();
        castMap.putAll(row);
        return castMap;
    }

    public Set<AuditCategory> getCategories() {
        AoCachedCategoryEntity[] cachedCategoriesEntities = (AoCachedCategoryEntity[])this.transactionTemplate.execute(() -> (AoCachedCategoryEntity[])this.ao.find(AoCachedCategoryEntity.class));
        Set<AuditCategory> auditCategories = Arrays.stream(cachedCategoriesEntities).map(row -> new AuditCategory(row.getCategory(), row.getCategoryI18nKey())).collect(Collectors.toSet());
        this.logIfTheresTooManyCategories(auditCategories.size());
        return auditCategories;
    }

    public Set<AuditCategory> getCategoriesFromSourceOfTruth() {
        return Arrays.stream((Object[])this.transactionTemplate.execute(() -> (AoAuditEntityCategory[])this.ao.find(AoAuditEntityCategory.class, Query.select((String)String.format("%s, %s, max(%s) as %s", "CATEGORY", "CATEGORY_T_KEY", "ID", "ID")).group(String.format("%s, %s", "CATEGORY", "CATEGORY_T_KEY"))))).filter(row -> row.getCategory() != null).map(row -> new AuditCategory(row.getCategory(), row.getCategoryI18nKey())).collect(Collectors.toSet());
    }

    public void truncateAndSave(Set<AuditCategory> newCategories) {
        Objects.requireNonNull(newCategories, "newCategories");
        this.transactionTemplate.execute(() -> {
            log.info("Removing all cached audit categories from the DB");
            this.ao.deleteWithSQL(AoCachedCategoryEntity.class, String.format("%s LIKE '%%'", "CATEGORY"), new Object[0]);
            log.info("Saving cached audit categories to the DB");
            this.ao.create(AoCachedCategoryEntity.class, newCategories.stream().map(AoCachedCategoryDao::mapToCachedCategory).collect(Collectors.toList()));
            return null;
        });
    }

    public void save(Set<AuditCategory> categories) {
        Objects.requireNonNull(categories, "categories");
        if (categories.isEmpty()) {
            return;
        }
        this.transactionTemplate.execute(() -> {
            this.ao.create(AoCachedCategoryEntity.class, categories.stream().map(AoCachedCategoryDao::mapToCachedCategory).collect(Collectors.toList()));
            return null;
        });
    }

    private void logIfTheresTooManyCategories(Integer numCategories) {
        int configuredCategoryLimit = this.propertiesProvider.getInteger(CATEGORY_LIMIT_PROPERTY_KEY, 1000);
        if (numCategories > configuredCategoryLimit) {
            log.warn("Detected {} audit categories which is greater than the limit of {} this may lead to general performance degradation as audit events are saved and/or while loading the UI.", (Object)numCategories, (Object)configuredCategoryLimit);
        }
    }
}

