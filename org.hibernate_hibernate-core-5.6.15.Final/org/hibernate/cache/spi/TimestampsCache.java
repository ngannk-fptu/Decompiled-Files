/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import java.io.Serializable;
import java.util.Set;
import java.util.function.Consumer;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.UpdateTimestampsCache;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface TimestampsCache
extends UpdateTimestampsCache {
    @Override
    public TimestampsRegion getRegion();

    public void preInvalidate(String[] var1, SharedSessionContractImplementor var2);

    public void invalidate(String[] var1, SharedSessionContractImplementor var2);

    public boolean isUpToDate(String[] var1, Long var2, SharedSessionContractImplementor var3);

    @Override
    default public void preInvalidate(Serializable[] spaces, SharedSessionContractImplementor session) {
        String[] spaceStrings = new String[spaces.length];
        System.arraycopy(spaces, 0, spaceStrings, 0, spaces.length);
        this.preInvalidate(spaceStrings, session);
    }

    @Override
    default public void invalidate(Serializable[] spaces, SharedSessionContractImplementor session) {
        String[] spaceStrings = new String[spaces.length];
        System.arraycopy(spaces, 0, spaceStrings, 0, spaces.length);
        this.invalidate(spaceStrings, session);
    }

    @Override
    default public boolean isUpToDate(Set<Serializable> spaces, Long timestamp, SharedSessionContractImplementor session) {
        final String[] spaceArray = new String[spaces.size()];
        spaces.forEach(new Consumer<Serializable>(){
            int position = 0;

            @Override
            public void accept(Serializable serializable) {
                spaceArray[this.position++] = (String)((Object)serializable);
            }
        });
        return this.isUpToDate(spaceArray, timestamp, session);
    }

    @Override
    default public void clear() throws CacheException {
        this.getRegion().clear();
    }

    @Override
    default public void destroy() {
    }
}

