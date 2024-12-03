/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.info;

import net.java.ao.RawEntity;
import net.java.ao.schema.info.EntityInfo;

public interface EntityInfoResolver {
    public <T extends RawEntity<K>, K> EntityInfo<T, K> resolve(Class<T> var1);
}

