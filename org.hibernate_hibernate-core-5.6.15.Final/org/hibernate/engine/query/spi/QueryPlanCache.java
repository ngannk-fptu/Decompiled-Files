/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.hibernate.Filter;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.engine.query.spi.FilterQueryPlan;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.query.spi.NativeQueryInterpreter;
import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.FilterImpl;
import org.hibernate.internal.util.collections.BoundedConcurrentHashMap;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.internal.ParameterMetadataImpl;
import org.hibernate.stat.spi.StatisticsImplementor;

public class QueryPlanCache
implements Serializable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(QueryPlanCache.class);
    public static final int DEFAULT_PARAMETER_METADATA_MAX_COUNT = 128;
    public static final int DEFAULT_QUERY_PLAN_MAX_COUNT = 2048;
    private final SessionFactoryImplementor factory;
    private QueryPlanCreator queryPlanCreator;
    private final BoundedConcurrentHashMap queryPlanCache;
    private final BoundedConcurrentHashMap<ParameterMetadataKey, ParameterMetadataImpl> parameterMetadataCache;
    private NativeQueryInterpreter nativeQueryInterpreter;

    public QueryPlanCache(SessionFactoryImplementor factory, QueryPlanCreator queryPlanCreator) {
        Integer maxQueryPlanCount;
        this.factory = factory;
        this.queryPlanCreator = queryPlanCreator;
        Integer maxParameterMetadataCount = ConfigurationHelper.getInteger("hibernate.query.plan_parameter_metadata_max_size", factory.getProperties());
        if (maxParameterMetadataCount == null) {
            maxParameterMetadataCount = ConfigurationHelper.getInt("hibernate.query.plan_cache_max_strong_references", factory.getProperties(), 128);
        }
        if ((maxQueryPlanCount = ConfigurationHelper.getInteger("hibernate.query.plan_cache_max_size", factory.getProperties())) == null) {
            maxQueryPlanCount = ConfigurationHelper.getInt("hibernate.query.plan_cache_max_soft_references", factory.getProperties(), 2048);
        }
        this.queryPlanCache = new BoundedConcurrentHashMap(maxQueryPlanCount, 20, BoundedConcurrentHashMap.Eviction.LIRS);
        this.parameterMetadataCache = new BoundedConcurrentHashMap(maxParameterMetadataCount, 20, BoundedConcurrentHashMap.Eviction.LIRS);
        this.nativeQueryInterpreter = factory.getServiceRegistry().getService(NativeQueryInterpreter.class);
    }

    public ParameterMetadata getSQLParameterMetadata(String query, boolean isOrdinalParameterZeroBased) {
        ParameterMetadataKey key = new ParameterMetadataKey(query, isOrdinalParameterZeroBased);
        return this.parameterMetadataCache.computeIfAbsent(key, k -> this.nativeQueryInterpreter.getParameterMetadata(query));
    }

    public HQLQueryPlan getHQLQueryPlan(String queryString, boolean shallow, Map<String, Filter> enabledFilters) throws QueryException, MappingException {
        HQLQueryPlanKey key = new HQLQueryPlanKey(queryString, shallow, enabledFilters);
        HQLQueryPlan value = (HQLQueryPlan)this.queryPlanCache.get(key);
        StatisticsImplementor statistics = this.factory.getStatistics();
        boolean stats = statistics.isStatisticsEnabled();
        if (value == null) {
            long startTime = stats ? System.nanoTime() : 0L;
            LOG.tracev("Unable to locate HQL query plan in cache; generating ({0})", queryString);
            value = this.queryPlanCreator.createQueryPlan(queryString, shallow, enabledFilters, this.factory);
            if (stats) {
                long endTime = System.nanoTime();
                long microseconds = TimeUnit.MICROSECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
                statistics.queryCompiled(queryString, microseconds);
            }
            this.queryPlanCache.putIfAbsent(key, value);
        } else {
            LOG.tracev("Located HQL query plan in cache ({0})", queryString);
            if (stats) {
                statistics.queryPlanCacheHit(queryString);
            }
        }
        return value;
    }

    public FilterQueryPlan getFilterQueryPlan(String filterString, String collectionRole, boolean shallow, Map<String, Filter> enabledFilters) throws QueryException, MappingException {
        FilterQueryPlanKey key = new FilterQueryPlanKey(filterString, collectionRole, shallow, enabledFilters);
        FilterQueryPlan value = (FilterQueryPlan)this.queryPlanCache.get(key);
        StatisticsImplementor statistics = this.factory.getStatistics();
        boolean stats = statistics.isStatisticsEnabled();
        if (value == null) {
            LOG.tracev("Unable to locate collection-filter query plan in cache; generating ({0} : {1} )", collectionRole, filterString);
            value = new FilterQueryPlan(filterString, collectionRole, shallow, enabledFilters, this.factory);
            if (stats) {
                statistics.queryPlanCacheMiss(key.query);
            }
            this.queryPlanCache.putIfAbsent(key, value);
        } else {
            LOG.tracev("Located collection-filter query plan in cache ({0} : {1})", collectionRole, filterString);
            if (stats) {
                statistics.queryPlanCacheHit(key.query);
            }
        }
        return value;
    }

    public NativeSQLQueryPlan getNativeSQLQueryPlan(NativeSQLQuerySpecification spec) {
        NativeSQLQueryPlan value = (NativeSQLQueryPlan)this.queryPlanCache.get(spec);
        StatisticsImplementor statistics = this.factory.getStatistics();
        boolean stats = statistics.isStatisticsEnabled();
        if (value == null) {
            LOG.tracev("Unable to locate native-sql query plan in cache; generating ({0})", spec.getQueryString());
            value = this.nativeQueryInterpreter.createQueryPlan(spec, this.factory);
            if (stats) {
                statistics.queryPlanCacheMiss(spec.getQueryString());
            }
            this.queryPlanCache.putIfAbsent(spec, value);
        } else {
            LOG.tracev("Located native-sql query plan in cache ({0})", spec.getQueryString());
            if (stats) {
                statistics.queryPlanCacheHit(spec.getQueryString());
            }
        }
        return value;
    }

    public void cleanup() {
        LOG.trace("Cleaning QueryPlan Cache");
        this.queryPlanCache.clear();
        this.parameterMetadataCache.clear();
    }

    public NativeQueryInterpreter getNativeQueryInterpreter() {
        return this.nativeQueryInterpreter;
    }

    private static class FilterQueryPlanKey
    implements Serializable {
        private final String query;
        private final String collectionRole;
        private final boolean shallow;
        private final Set<String> filterNames;
        private final int hashCode;

        public FilterQueryPlanKey(String query, String collectionRole, boolean shallow, Map enabledFilters) {
            this.query = query;
            this.collectionRole = collectionRole;
            this.shallow = shallow;
            if (CollectionHelper.isEmpty(enabledFilters)) {
                this.filterNames = Collections.emptySet();
            } else {
                HashSet tmp = new HashSet(enabledFilters.keySet());
                this.filterNames = Collections.unmodifiableSet(tmp);
            }
            int hash = query.hashCode();
            hash = 29 * hash + collectionRole.hashCode();
            hash = 29 * hash + (shallow ? 1 : 0);
            this.hashCode = hash = 29 * hash + this.filterNames.hashCode();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            FilterQueryPlanKey that = (FilterQueryPlanKey)o;
            return this.shallow == that.shallow && this.filterNames.equals(that.filterNames) && this.query.equals(that.query) && this.collectionRole.equals(that.collectionRole);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    private static class DynamicFilterKey
    implements Serializable {
        private final String filterName;
        private final Map<String, Integer> parameterMetadata;
        private final int hashCode;

        private DynamicFilterKey(FilterImpl filter) {
            this.filterName = filter.getName();
            Map<String, ?> parameters = filter.getParameters();
            if (parameters.isEmpty()) {
                this.parameterMetadata = Collections.emptyMap();
            } else {
                this.parameterMetadata = new HashMap<String, Integer>(CollectionHelper.determineProperSizing(parameters), 0.75f);
                Iterator<Map.Entry<String, ?>> iterator = parameters.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, ?> o;
                    Map.Entry<String, ?> entry = o = iterator.next();
                    String key = entry.getKey();
                    Integer valueCount = Collection.class.isInstance(entry.getValue()) ? Integer.valueOf(((Collection)entry.getValue()).size()) : Integer.valueOf(1);
                    this.parameterMetadata.put(key, valueCount);
                }
            }
            int hash = this.filterName.hashCode();
            this.hashCode = hash = 31 * hash + this.parameterMetadata.hashCode();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            DynamicFilterKey that = (DynamicFilterKey)o;
            return this.filterName.equals(that.filterName) && this.parameterMetadata.equals(that.parameterMetadata);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    private static class HQLQueryPlanKey
    implements Serializable {
        private final String query;
        private final boolean shallow;
        private final Set<DynamicFilterKey> filterKeys;
        private final int hashCode;

        public HQLQueryPlanKey(String query, boolean shallow, Map enabledFilters) {
            this.query = query;
            this.shallow = shallow;
            if (CollectionHelper.isEmpty(enabledFilters)) {
                this.filterKeys = Collections.emptySet();
            } else {
                HashSet<DynamicFilterKey> tmp = new HashSet<DynamicFilterKey>(CollectionHelper.determineProperSizing(enabledFilters), 0.75f);
                for (Object o : enabledFilters.values()) {
                    tmp.add(new DynamicFilterKey((FilterImpl)o));
                }
                this.filterKeys = Collections.unmodifiableSet(tmp);
            }
            int hash = query.hashCode();
            hash = 29 * hash + (shallow ? 1 : 0);
            this.hashCode = hash = 29 * hash + this.filterKeys.hashCode();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            HQLQueryPlanKey that = (HQLQueryPlanKey)o;
            return this.shallow == that.shallow && this.filterKeys.equals(that.filterKeys) && this.query.equals(that.query);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    private static class ParameterMetadataKey
    implements Serializable {
        private final String query;
        private final boolean isOrdinalParameterZeroBased;
        private final int hashCode;

        public ParameterMetadataKey(String query, boolean isOrdinalParameterZeroBased) {
            this.query = query;
            this.isOrdinalParameterZeroBased = isOrdinalParameterZeroBased;
            int hash = query.hashCode();
            this.hashCode = hash = 29 * hash + (isOrdinalParameterZeroBased ? 1 : 0);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ParameterMetadataKey that = (ParameterMetadataKey)o;
            return this.isOrdinalParameterZeroBased == that.isOrdinalParameterZeroBased && this.query.equals(that.query);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    @FunctionalInterface
    public static interface QueryPlanCreator {
        public HQLQueryPlan createQueryPlan(String var1, boolean var2, Map<String, Filter> var3, SessionFactoryImplementor var4);
    }
}

