/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.info;

import net.java.ao.RawEntity;
import net.java.ao.schema.info.EntityInfo;
import net.java.ao.schema.info.EntityInfoResolver;

public abstract class EntityInfoResolverWrapper
implements EntityInfoResolver {
    private final EntityInfoResolver delegate;

    protected EntityInfoResolverWrapper(EntityInfoResolver delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T extends RawEntity<K>, K> EntityInfo<T, K> resolve(Class<T> type) {
        return this.delegate.resolve(type);
    }
}

