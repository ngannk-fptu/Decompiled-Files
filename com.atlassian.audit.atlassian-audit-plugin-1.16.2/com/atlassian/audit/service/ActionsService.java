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
import com.atlassian.audit.ao.dao.AoCachedActionDao;
import com.atlassian.audit.model.AuditAction;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class ActionsService {
    private final AoCachedActionDao aoCachedActionDao;

    public ActionsService(AoCachedActionDao aoCachedActionDao) {
        this.aoCachedActionDao = Objects.requireNonNull(aoCachedActionDao, "aoCachedActionDao");
    }

    @Nonnull
    public Set<AuditAction> getActions() {
        return ImmutableSet.copyOf(this.aoCachedActionDao.getActions());
    }
}

