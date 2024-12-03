/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 */
package net.java.ao.schema;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Objects;
import net.java.ao.RawEntity;
import net.java.ao.schema.TableNameConverter;

public class CachingTableNameConverter
implements TableNameConverter {
    private final LoadingCache<Class<? extends RawEntity<?>>, String> cache;

    public CachingTableNameConverter(final TableNameConverter delegateTableNameConverter) {
        Objects.requireNonNull(delegateTableNameConverter, "delegateTableNameConverter can't be null");
        this.cache = CacheBuilder.newBuilder().build(new CacheLoader<Class<? extends RawEntity<?>>, String>(){

            public String load(Class<? extends RawEntity<?>> key) throws Exception {
                return delegateTableNameConverter.getName(key);
            }
        });
    }

    @Override
    public String getName(Class<? extends RawEntity<?>> entityClass) {
        return (String)this.cache.getUnchecked(entityClass);
    }
}

