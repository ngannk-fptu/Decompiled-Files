/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.spi;

import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.service.Service;
import org.hibernate.stat.Statistics;

public interface StatisticsImplementor
extends Statistics,
Service {
    public void openSession();

    public void closeSession();

    public void flush();

    public void connect();

    public void prepareStatement();

    public void closeStatement();

    public void endTransaction(boolean var1);

    public void loadEntity(String var1);

    public void fetchEntity(String var1);

    public void updateEntity(String var1);

    public void insertEntity(String var1);

    public void deleteEntity(String var1);

    public void optimisticFailure(String var1);

    public void loadCollection(String var1);

    public void fetchCollection(String var1);

    public void updateCollection(String var1);

    public void recreateCollection(String var1);

    public void removeCollection(String var1);

    public void entityCachePut(NavigableRole var1, String var2);

    public void entityCacheHit(NavigableRole var1, String var2);

    public void entityCacheMiss(NavigableRole var1, String var2);

    public void collectionCachePut(NavigableRole var1, String var2);

    public void collectionCacheHit(NavigableRole var1, String var2);

    public void collectionCacheMiss(NavigableRole var1, String var2);

    public void naturalIdCachePut(NavigableRole var1, String var2);

    public void naturalIdCacheHit(NavigableRole var1, String var2);

    public void naturalIdCacheMiss(NavigableRole var1, String var2);

    public void naturalIdQueryExecuted(String var1, long var2);

    public void queryCachePut(String var1, String var2);

    public void queryCacheHit(String var1, String var2);

    public void queryCacheMiss(String var1, String var2);

    public void queryExecuted(String var1, int var2, long var3);

    public void updateTimestampsCacheHit();

    public void updateTimestampsCacheMiss();

    public void updateTimestampsCachePut();

    default public void queryPlanCacheHit(String query) {
    }

    default public void queryPlanCacheMiss(String query) {
    }

    default public void queryCompiled(String hql, long microseconds) {
    }
}

