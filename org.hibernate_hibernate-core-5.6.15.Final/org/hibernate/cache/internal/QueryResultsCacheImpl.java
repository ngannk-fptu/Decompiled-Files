/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.cache.spi.QueryKey;
import org.hibernate.cache.spi.QueryResultsCache;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.QuerySpacesHelper;
import org.hibernate.cache.spi.TimestampsCache;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.type.Type;
import org.hibernate.type.TypeHelper;

public class QueryResultsCacheImpl
implements QueryResultsCache {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(QueryResultsCacheImpl.class);
    private final QueryResultsRegion cacheRegion;
    private final TimestampsCache timestampsCache;

    QueryResultsCacheImpl(QueryResultsRegion cacheRegion, TimestampsCache timestampsCache) {
        this.cacheRegion = cacheRegion;
        this.timestampsCache = timestampsCache;
    }

    @Override
    public QueryResultsRegion getRegion() {
        return this.cacheRegion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean put(QueryKey key, List results, Type[] returnTypes, SharedSessionContractImplementor session) throws HibernateException {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Caching query results in region: %s; timestamp=%s", this.cacheRegion.getName(), session.getTransactionStartTimestamp());
        }
        ArrayList resultsCopy = CollectionHelper.arrayList(results.size());
        boolean isSingleResult = returnTypes.length == 1;
        for (Object aResult : results) {
            Serializable[] resultRowForCache = isSingleResult ? returnTypes[0].disassemble(aResult, session, null) : TypeHelper.disassemble((Object[])aResult, returnTypes, null, session, null);
            resultsCopy.add(resultRowForCache);
            if (!LOG.isTraceEnabled()) continue;
            QueryResultsCacheImpl.logCachedResultRowDetails(returnTypes, aResult);
        }
        if (LOG.isTraceEnabled()) {
            QueryResultsCacheImpl.logCachedResultDetails(key, null, returnTypes, resultsCopy);
        }
        CacheItem cacheItem = new CacheItem(session.getTransactionStartTimestamp(), resultsCopy);
        try {
            session.getEventListenerManager().cachePutStart();
            this.cacheRegion.putIntoCache(key, cacheItem, session);
        }
        finally {
            session.getEventListenerManager().cachePutEnd();
        }
        return true;
    }

    private static void logCachedResultDetails(QueryKey key, Set querySpaces, Type[] returnTypes, List result) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        LOG.trace("key.hashCode=" + key.hashCode());
        LOG.trace("querySpaces=" + querySpaces);
        if (returnTypes == null || returnTypes.length == 0) {
            LOG.trace("Unexpected returnTypes is " + (returnTypes == null ? "null" : "empty") + "! result" + (result == null ? " is null" : ".size()=" + result.size()));
        } else {
            StringBuilder returnTypeInfo = new StringBuilder();
            for (Type returnType : returnTypes) {
                returnTypeInfo.append("typename=").append(returnType.getName()).append(" class=").append(returnType.getReturnedClass().getName()).append(' ');
            }
            LOG.trace("unexpected returnTypes is " + returnTypeInfo.toString() + "! result");
        }
    }

    @Override
    public List get(QueryKey key, Set<Serializable> spaces, Type[] returnTypes, SharedSessionContractImplementor session) {
        return this.get(key, QuerySpacesHelper.INSTANCE.toStringArray(spaces), returnTypes, session);
    }

    @Override
    public List get(QueryKey key, String[] spaces, Type[] returnTypes, SharedSessionContractImplementor session) {
        CacheItem cacheItem;
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Checking cached query results in region: %s", this.cacheRegion.getName());
        }
        if ((cacheItem = this.getCachedData(key, session)) == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Query results were not found in cache");
            }
            return null;
        }
        if (!this.timestampsCache.isUpToDate(spaces, (Long)cacheItem.timestamp, session)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cached query results were not up-to-date");
            }
            return null;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Returning cached query results");
        }
        boolean singleResult = returnTypes.length == 1;
        for (int i = 0; i < cacheItem.results.size(); ++i) {
            if (singleResult) {
                returnTypes[0].beforeAssemble((Serializable)cacheItem.results.get(i), session);
                continue;
            }
            TypeHelper.beforeAssemble((Serializable[])cacheItem.results.get(i), returnTypes, session);
        }
        return this.assembleCachedResult(key, cacheItem.results, singleResult, returnTypes, session);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CacheItem getCachedData(QueryKey key, SharedSessionContractImplementor session) {
        CacheItem cachedItem = null;
        try {
            session.getEventListenerManager().cacheGetStart();
            cachedItem = (CacheItem)this.cacheRegion.getFromCache(key, session);
            session.getEventListenerManager().cacheGetEnd(cachedItem != null);
        }
        catch (Throwable throwable) {
            session.getEventListenerManager().cacheGetEnd(cachedItem != null);
            throw throwable;
        }
        return cachedItem;
    }

    private List assembleCachedResult(QueryKey key, List cached, boolean singleResult, Type[] returnTypes, SharedSessionContractImplementor session) throws HibernateException {
        ArrayList<Object> result = new ArrayList<Object>(cached.size());
        if (singleResult) {
            for (Object aCached : cached) {
                result.add(returnTypes[0].assemble((Serializable)aCached, session, null));
            }
        } else {
            for (int i = 0; i < cached.size(); ++i) {
                result.add(TypeHelper.assemble((Serializable[])cached.get(i), returnTypes, session, null));
                if (!LOG.isTraceEnabled()) continue;
                QueryResultsCacheImpl.logCachedResultRowDetails(returnTypes, result.get(i));
            }
        }
        return result;
    }

    private static void logCachedResultRowDetails(Type[] returnTypes, Object result) {
        Object[] objectArray;
        if (result instanceof Object[]) {
            objectArray = (Object[])result;
        } else {
            Object[] objectArray2 = new Object[1];
            objectArray = objectArray2;
            objectArray2[0] = result;
        }
        QueryResultsCacheImpl.logCachedResultRowDetails(returnTypes, objectArray);
    }

    private static void logCachedResultRowDetails(Type[] returnTypes, Object[] tuple) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        if (tuple == null) {
            LOG.tracef("tuple is null; returnTypes is %s", returnTypes == null ? "null" : "Type[" + returnTypes.length + "]");
            if (returnTypes != null && returnTypes.length > 1) {
                LOG.trace("Unexpected result tuple! tuple is null; should be Object[" + returnTypes.length + "]!");
            }
        } else {
            if (returnTypes == null || returnTypes.length == 0) {
                LOG.trace("Unexpected result tuple! tuple is null; returnTypes is " + (returnTypes == null ? "null" : "empty"));
            }
            LOG.tracef("tuple is Object[%s]; returnTypes is %s", tuple.length, returnTypes == null ? "null" : "Type[" + returnTypes.length + "]");
            if (returnTypes != null && tuple.length != returnTypes.length) {
                LOG.trace("Unexpected tuple length! transformer= expected=" + returnTypes.length + " got=" + tuple.length);
            } else {
                for (int j = 0; j < tuple.length; ++j) {
                    if (tuple[j] == null || returnTypes == null || returnTypes[j].getReturnedClass().isInstance(tuple[j])) continue;
                    LOG.trace("Unexpected tuple value type! transformer= expected=" + returnTypes[j].getReturnedClass().getName() + " got=" + tuple[j].getClass().getName());
                }
            }
        }
    }

    public String toString() {
        return "QueryResultsCache(" + this.cacheRegion.getName() + ')';
    }

    public static class CacheItem
    implements Serializable {
        private final long timestamp;
        private final List results;

        CacheItem(long timestamp, List results) {
            this.timestamp = timestamp;
            this.results = results;
        }
    }
}

