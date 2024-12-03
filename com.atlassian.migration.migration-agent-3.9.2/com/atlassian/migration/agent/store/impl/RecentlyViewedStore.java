/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.google.common.collect.Iterables;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecentlyViewedStore {
    private final int batchSizePerQuery;
    private final EntityManagerTemplate tmpl;

    public RecentlyViewedStore(EntityManagerTemplate tmpl, MigrationAgentConfiguration migrationAgentConfiguration) {
        this.tmpl = tmpl;
        this.batchSizePerQuery = migrationAgentConfiguration.getBatchSizePerQuery();
    }

    public int getUniqueUserViewsByPages(Set<Long> pageIds) {
        HashSet<String> uniqueUserKeys = new HashSet<String>();
        Iterable partition = Iterables.partition(pageIds, (int)this.batchSizePerQuery);
        for (List subset : partition) {
            List<String> subsetResult = this.tmpl.nativeQuery(String.class, "select distinct \"USER_KEY\" from \"AO_92296B_AORECENTLY_VIEWED\" entity where \"CONTENT_ID\" in (:subset)").param("subset", (Object)subset).list();
            uniqueUserKeys.addAll(subsetResult);
        }
        return uniqueUserKeys.size();
    }
}

