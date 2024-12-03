/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.annotation.Nonnull
 *  net.java.ao.Query
 */
package com.atlassian.audit.denylist;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.audit.ao.dao.entity.AoExcludedActionsAuditEntity;
import com.atlassian.audit.coverage.SingleValueCache;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.java.ao.Query;

public class ExcludedActionsProvider {
    private static final int EXCLUDED_ACTIONS_LIMIT = 1000;
    private final ActiveObjects ao;
    private final TransactionTemplate transactionTemplate;
    private final SingleValueCache<Set<String>> actionsCache;

    public ExcludedActionsProvider(ActiveObjects ao, TransactionTemplate transactionTemplate, int refreshIntervalInSeconds) {
        this.ao = ao;
        this.transactionTemplate = transactionTemplate;
        this.actionsCache = new SingleValueCache<Set>(this::queryExcludedActions, (long)refreshIntervalInSeconds, TimeUnit.SECONDS);
    }

    @Nonnull
    public Set<String> queryExcludedActions() {
        return Arrays.stream((Object[])this.transactionTemplate.execute(() -> (AoExcludedActionsAuditEntity[])this.ao.find(AoExcludedActionsAuditEntity.class, Query.select((String)"ACTION").limit(1000)))).map(AoExcludedActionsAuditEntity::getAction).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Nonnull
    public Set<String> getCachedExcludedActions() {
        return this.actionsCache.get();
    }
}

