/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.fugue.Effect
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Supplier
 *  com.google.common.collect.Lists
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 */
package com.atlassian.confluence.plugins.recentlyviewed.dao.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.fugue.Effect;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import net.java.ao.Query;
import net.java.ao.RawEntity;

public class AbstractAODao<T extends RawEntity<K>, K> {
    private final Class<T> type;
    protected final ActiveObjects ao;

    public AbstractAODao(Class<T> type, @ComponentImport ActiveObjects ao) {
        this.type = type;
        this.ao = ao;
    }

    protected T getAO(K key) {
        return (T)this.ao.get(this.type, key);
    }

    protected T findOnly(Query query) {
        return this.findOnly(this.type, query);
    }

    protected <U extends RawEntity<L>, L> U findOnly(Class<U> type, Query query) {
        Object[] entities = this.ao.find(type, query);
        switch (entities.length) {
            case 0: {
                return null;
            }
            case 1: {
                return (U)entities[0];
            }
        }
        throw new IllegalStateException("Found multiple items when expected just one: " + Arrays.toString(entities));
    }

    protected <U extends RawEntity<L>, L> U[] findBy(Class<U> type, Query query) {
        return this.ao.find(type, query);
    }

    protected <U extends RawEntity<L>, L> U streamOnly(Class<U> type, Query query) {
        ArrayList entities = Lists.newArrayListWithExpectedSize((int)1);
        this.ao.stream(type, query, entities::add);
        switch (entities.size()) {
            case 0: {
                return null;
            }
            case 1: {
                return (U)((RawEntity)entities.get(0));
            }
        }
        throw new IllegalStateException("Found multiple items when expected just one: " + entities);
    }

    protected int delete(Supplier<Query> querySupplier) {
        AtomicInteger deleteCount = new AtomicInteger(0);
        this.delete(querySupplier, (Effect<T[]>)((Effect)ts -> deleteCount.getAndAdd(((RawEntity[])ts).length)));
        return deleteCount.get();
    }

    protected void delete(Supplier<Query> querySupplier, Effect<T[]> preDeleteCallback) {
        RawEntity[] entities = this.ao.find(this.type, ((Query)querySupplier.get()).limit(1000));
        while (entities.length > 0) {
            preDeleteCallback.apply((Object)entities);
            this.ao.delete(entities);
            entities = this.ao.find(this.type, ((Query)querySupplier.get()).limit(1000));
        }
    }
}

