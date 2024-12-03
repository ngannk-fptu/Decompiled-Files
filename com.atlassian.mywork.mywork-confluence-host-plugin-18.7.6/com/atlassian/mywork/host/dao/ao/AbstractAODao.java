/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.fugue.Effect
 *  com.google.common.base.Supplier
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.node.JsonNodeFactory
 *  org.codehaus.jackson.node.ObjectNode
 */
package com.atlassian.mywork.host.dao.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.fugue.Effect;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class AbstractAODao<T extends RawEntity<K>, K> {
    static final int BATCH_LIMIT = 100;
    private final Class<T> type;
    protected final ActiveObjects ao;

    public AbstractAODao(Class<T> type, ActiveObjects ao) {
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

    protected int delete(Supplier<Query> querySupplier) {
        AtomicInteger deleteCount = new AtomicInteger(0);
        this.delete(querySupplier, (Effect<T[]>)((Effect)ts -> deleteCount.getAndAdd(((RawEntity[])ts).length)));
        return deleteCount.get();
    }

    protected void delete(Supplier<Query> querySupplier, Effect<T[]> preDeleteCallback) {
        RawEntity[] entities = this.ao.find(this.type, ((Query)querySupplier.get()).limit(100));
        while (entities.length > 0) {
            preDeleteCallback.apply((Object)entities);
            this.ao.delete(entities);
            entities = this.ao.find(this.type, ((Query)querySupplier.get()).limit(100));
        }
    }

    protected static ObjectNode toObjectNode(String json) {
        try {
            ObjectNode node = StringUtils.isEmpty((CharSequence)json) ? JsonNodeFactory.instance.objectNode() : (ObjectNode)new ObjectMapper().readTree(json);
            return node;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

