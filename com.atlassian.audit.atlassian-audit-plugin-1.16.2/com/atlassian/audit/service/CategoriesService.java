/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.audit.service;

import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.audit.ao.dao.AoCachedCategoryDao;
import com.atlassian.audit.model.AuditCategory;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class CategoriesService {
    private final AoCachedCategoryDao aoCachedCategoryDao;

    public CategoriesService(AoCachedCategoryDao aoCachedCategoryDao) {
        this.aoCachedCategoryDao = Objects.requireNonNull(aoCachedCategoryDao, "aoCachedCategoryDao");
    }

    @Nonnull
    public Set<AuditCategory> getCategories() {
        return ImmutableSet.copyOf(this.aoCachedCategoryDao.getCategories());
    }
}

