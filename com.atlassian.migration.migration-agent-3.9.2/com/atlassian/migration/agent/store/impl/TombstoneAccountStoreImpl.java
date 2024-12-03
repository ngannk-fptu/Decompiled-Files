/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.TombstoneAccount;
import com.atlassian.migration.agent.store.TombstoneAccountStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TombstoneAccountStoreImpl
implements TombstoneAccountStore {
    private static final int PARTITION_SIZE = 1000;
    private final EntityManagerTemplate tmpl;

    public TombstoneAccountStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public void save(TombstoneAccount tombstoneAccount) {
        this.tmpl.persist(tombstoneAccount);
    }

    @Override
    public List<TombstoneAccount> loadByUserkeys(List<String> userkeys) {
        return Lists.partition(userkeys, (int)1000).stream().map(partitionedUserkeys -> this.tmpl.query(TombstoneAccount.class, "select tombstoneAccount from TombstoneAccount tombstoneAccount where tombstoneAccount.userKey in :userkeys").param("userkeys", partitionedUserkeys).list()).flatMap(Collection::stream).collect(Collectors.toList());
    }
}

