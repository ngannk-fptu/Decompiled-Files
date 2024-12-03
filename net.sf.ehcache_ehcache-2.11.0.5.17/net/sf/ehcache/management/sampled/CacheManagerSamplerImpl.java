/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.sampled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheWriterConfiguration;
import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.management.sampled.CacheManagerSampler;
import net.sf.ehcache.management.sampled.Utils;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.aggregator.Aggregator;
import net.sf.ehcache.search.expression.Criteria;
import net.sf.ehcache.search.query.QueryManagerBuilder;
import net.sf.ehcache.statistics.StatisticsGateway;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.writer.writebehind.WriteBehindManager;

public class CacheManagerSamplerImpl
implements CacheManagerSampler {
    private static final int MAX_QUERY_RESULT_LIMIT = 1000;
    private final CacheManager cacheManager;

    public CacheManagerSamplerImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void clearAll() {
        this.cacheManager.clearAll();
    }

    @Override
    public String[] getCacheNames() throws IllegalStateException {
        return this.cacheManager.getCacheNames();
    }

    @Override
    public String getStatus() {
        return this.cacheManager.getStatus().toString();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public Map<String, long[]> getCacheMetrics() {
        HashMap<String, long[]> result = new HashMap<String, long[]>();
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            StatisticsGateway stats = cache.getStatistics();
            result.put(cacheName, new long[]{stats.cacheHitOperation().rate().value().longValue(), stats.cacheMissExpiredOperation().rate().value().longValue(), stats.cacheMissNotFoundOperation().rate().value().longValue(), stats.cachePutOperation().rate().value().longValue()});
        }
        return result;
    }

    @Override
    public long getCacheHitRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            long val = cache.getStatistics().cacheHitOperation().rate().value().longValue();
            result += val;
        }
        return result;
    }

    @Override
    public long getCacheInMemoryHitRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            long val = cache.getStatistics().localHeapHitOperation().rate().value().longValue();
            result += val;
        }
        return result;
    }

    @Override
    public long getCacheOffHeapHitRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            long val = cache.getStatistics().localOffHeapHitOperation().rate().value().longValue();
            result += val;
        }
        return result;
    }

    @Override
    public long getCacheOnDiskHitRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            long val = cache.getStatistics().localDiskHitOperation().rate().value().longValue();
            result += val;
        }
        return result;
    }

    @Override
    public long getCacheMissRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            long val = cache.getStatistics().cacheMissOperation().rate().value().longValue();
            result += val;
        }
        return result;
    }

    @Override
    public long getCacheInMemoryMissRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            long val = cache.getStatistics().localHeapMissOperation().rate().value().longValue();
            result += val;
        }
        return result;
    }

    @Override
    public long getCacheOffHeapMissRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            result += cache.getStatistics().localOffHeapMissOperation().rate().value().longValue();
        }
        return result;
    }

    @Override
    public long getCacheOnDiskMissRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            result += cache.getStatistics().localDiskMissOperation().rate().value().longValue();
        }
        return result;
    }

    @Override
    public long getCachePutRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            result += cache.getStatistics().cachePutOperation().rate().value().longValue();
        }
        return result;
    }

    @Override
    public long getCacheUpdateRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            result += cache.getStatistics().cachePutReplacedOperation().rate().value().longValue();
        }
        return result;
    }

    @Override
    public long getCacheRemoveRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            result += cache.getStatistics().cacheRemoveOperation().rate().value().longValue();
        }
        return result;
    }

    @Override
    public long getCacheEvictionRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            result += cache.getStatistics().cacheEvictionOperation().rate().value().longValue();
        }
        return result;
    }

    @Override
    public long getCacheExpirationRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            result += cache.getStatistics().cacheExpiredOperation().rate().value().longValue();
        }
        return result;
    }

    @Override
    public float getCacheAverageGetTime() {
        float result = 0.0f;
        int instances = 0;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            result += (float)cache.getStatistics().cacheSearchOperation().latency().average().value().longValue();
            ++instances;
        }
        return instances > 0 ? result / (float)instances : 0.0f;
    }

    @Override
    public long getCacheSearchRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            result += cache.getStatistics().cacheSearchOperation().rate().value().longValue();
        }
        return result;
    }

    @Override
    public long getCacheAverageSearchTime() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            result += cache.getStatistics().cacheSearchOperation().latency().average().value().longValue();
        }
        return result;
    }

    @Override
    public boolean getHasWriteBehindWriter() {
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null || !(cache.getWriterManager() instanceof WriteBehindManager) || cache.getRegisteredCacheWriter() == null) continue;
            return true;
        }
        return false;
    }

    @Override
    public long getWriterQueueLength() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            result += Math.max(cache.getStatistics().getWriterQueueLength(), 0L);
        }
        return result;
    }

    @Override
    public int getWriterMaxQueueSize() {
        int result = 0;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            CacheWriterConfiguration writerConfig = cache.getCacheConfiguration().getCacheWriterConfiguration();
            result += writerConfig.getWriteBehindMaxQueueSize() * writerConfig.getWriteBehindConcurrency();
        }
        return result;
    }

    @Override
    public long getMaxBytesLocalDisk() {
        return this.cacheManager.getConfiguration().getMaxBytesLocalDisk();
    }

    @Override
    public String getMaxBytesLocalDiskAsString() {
        return this.cacheManager.getConfiguration().getMaxBytesLocalDiskAsString();
    }

    @Override
    public void setMaxBytesLocalDisk(long maxBytes) {
        try {
            this.cacheManager.getConfiguration().setMaxBytesLocalDisk(maxBytes);
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public void setMaxBytesLocalDiskAsString(String maxBytes) {
        try {
            this.cacheManager.getConfiguration().setMaxBytesLocalDisk(maxBytes);
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getMaxBytesLocalHeap() {
        return this.cacheManager.getConfiguration().getMaxBytesLocalHeap();
    }

    @Override
    public String getMaxBytesLocalHeapAsString() {
        return this.cacheManager.getConfiguration().getMaxBytesLocalHeapAsString();
    }

    @Override
    public void setMaxBytesLocalHeap(long maxBytes) {
        try {
            this.cacheManager.getConfiguration().setMaxBytesLocalHeap(maxBytes);
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public void setMaxBytesLocalHeapAsString(String maxBytes) {
        try {
            this.cacheManager.getConfiguration().setMaxBytesLocalHeap(maxBytes);
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getMaxBytesLocalOffHeap() {
        return this.cacheManager.getConfiguration().getMaxBytesLocalOffHeap();
    }

    @Override
    public String getMaxBytesLocalOffHeapAsString() {
        return this.cacheManager.getConfiguration().getMaxBytesLocalOffHeapAsString();
    }

    @Override
    public String getName() {
        return this.cacheManager.getName();
    }

    @Override
    public String getClusterUUID() {
        return this.cacheManager.getClusterUUID();
    }

    @Override
    public String generateActiveConfigDeclaration() {
        return this.cacheManager.getActiveConfigurationText();
    }

    @Override
    public String generateActiveConfigDeclaration(String cacheName) {
        return this.cacheManager.getActiveConfigurationText(cacheName);
    }

    @Override
    public boolean getTransactional() {
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null || !cache.getCacheConfiguration().getTransactionalMode().isTransactional()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean getSearchable() {
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null || cache.getCacheConfiguration().getSearchable() == null) continue;
            return true;
        }
        return false;
    }

    private Query limitResults(Query q) {
        StoreQuery sq = (StoreQuery)((Object)q);
        int maxResults = sq.maxResults();
        if (maxResults == -1 || maxResults > 1000) {
            List<Aggregator> aggregators;
            List<StoreQuery.Ordering> orderings;
            Set<Attribute<?>> groupByAttrs;
            Criteria criteria;
            Set<Attribute<?>> attrs;
            Query newQuery = sq.getCache().createQuery().maxResults(1000);
            if (sq.requestsKeys()) {
                newQuery.includeKeys();
            }
            if (sq.requestsValues()) {
                newQuery.includeValues();
            }
            if ((attrs = sq.requestedAttributes()) != null) {
                newQuery.includeAttribute(new ArrayList(attrs).toArray(new Attribute[0]));
            }
            if ((criteria = sq.getCriteria()) != null) {
                newQuery.addCriteria(criteria);
            }
            if ((groupByAttrs = sq.groupByAttributes()) != null) {
                newQuery.addGroupBy(new ArrayList(groupByAttrs).toArray(new Attribute[0]));
            }
            if ((orderings = sq.getOrdering()) != null) {
                for (StoreQuery.Ordering ordering : orderings) {
                    newQuery.addOrderBy(ordering.getAttribute(), ordering.getDirection());
                }
            }
            if ((aggregators = sq.getAggregators()) != null) {
                newQuery.includeAggregator(aggregators.toArray(new Aggregator[0]));
            }
            ((StoreQuery)((Object)newQuery)).targets(sq.getTargets());
            q = newQuery.end();
        }
        return q;
    }

    private Object primitiveOrString(Object value) {
        if (value != null && !value.getClass().isPrimitive()) {
            value = value.toString();
        }
        return value;
    }

    @Override
    public Object[][] executeQuery(String queryString) throws SearchException {
        QueryManagerBuilder qmb = QueryManagerBuilder.newQueryManagerBuilder();
        return this.executeQuery(queryString, qmb);
    }

    Object[][] executeQuery(String queryString, QueryManagerBuilder qmb) throws SearchException {
        boolean searchable = false;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null || cache.getCacheConfiguration().getSearchable() == null) continue;
            qmb.addCache(cache);
            searchable = true;
        }
        if (!searchable) {
            throw new SearchException("There are no searchable caches");
        }
        Query q = this.limitResults(qmb.build().createQuery(queryString).end());
        StoreQuery sq = (StoreQuery)((Object)q);
        HashSet attrs = new HashSet(sq.requestedAttributes());
        if (sq.requestsKeys()) {
            attrs.add(Query.KEY);
        }
        if (sq.requestsValues()) {
            attrs.add(Query.VALUE);
        }
        HashMap<String, Attribute> attrMap = new HashMap<String, Attribute>();
        for (Attribute attribute : attrs) {
            String attrName = attribute.getAttributeName();
            attrMap.put(attrName, attribute);
        }
        String[] selectTargets = sq.getTargets();
        Results results = q.execute();
        List<Result> all = results.all();
        ArrayList<Object[]> result = new ArrayList<Object[]>(results.size());
        ArrayList<Object> row = new ArrayList<Object>();
        HashMap<String, String> typeMap = new HashMap<String, String>();
        for (Result r : all) {
            int aggregateIndex = 0;
            for (String target : selectTargets) {
                Object value;
                Attribute attr = (Attribute)attrMap.get(target);
                if (attr != null) {
                    if (attr == Query.KEY) {
                        value = this.primitiveOrString(r.getKey());
                    } else if (attr == Query.VALUE) {
                        value = this.primitiveOrString(r.getValue());
                    } else {
                        value = r.getAttribute(attr);
                        if (value != null && value.getClass().isEnum()) {
                            value = ((Enum)value).name();
                        }
                    }
                } else {
                    value = r.getAggregatorResults().get(aggregateIndex++);
                }
                row.add(value);
                if (typeMap.get(target) != null || value == null) continue;
                typeMap.put(target, value.getClass().getSimpleName());
            }
            if (row.size() <= 0) continue;
            result.add(row.toArray(new Object[0]));
            row.clear();
        }
        row.clear();
        Map<String, SearchAttribute> sas = sq.getCache().getCacheConfiguration().getSearchAttributes();
        for (String target : selectTargets) {
            SearchAttribute sa;
            String typeName = (String)typeMap.get(target);
            if (typeName == null && (sa = sas.get(target)) != null) {
                typeName = sa.getTypeName();
            }
            row.add(target + ":" + (typeName != null ? typeName : ""));
        }
        result.add(0, row.toArray(new String[0]));
        results.discard();
        return (Object[][])result.toArray((T[])new Object[all.size()][]);
    }

    @Override
    public long getTransactionCommittedCount() {
        return this.cacheManager.getTransactionController().getTransactionCommittedCount();
    }

    @Override
    public long getTransactionCommitRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            long val = cache.getStatistics().xaCommitSuccessOperation().rate().value().longValue();
            result += val;
        }
        return result;
    }

    @Override
    public long getTransactionRolledBackCount() {
        return this.cacheManager.getTransactionController().getTransactionRolledBackCount();
    }

    @Override
    public long getTransactionRollbackRate() {
        long result = 0L;
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            long val = cache.getStatistics().xaRollbackOperation().rate().value().longValue();
            result += val;
        }
        return result;
    }

    @Override
    public long getTransactionTimedOutCount() {
        return this.cacheManager.getTransactionController().getTransactionTimedOutCount();
    }

    @Override
    public boolean isEnabled() throws CacheException {
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null || !cache.isDisabled()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (String cacheName : this.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            if (cache == null) continue;
            cache.setDisabled(!enabled);
        }
    }
}

