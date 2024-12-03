/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.statistics.extended.EhcacheQueryBuilder;
import net.sf.ehcache.store.StoreOperationOutcomes;
import net.sf.ehcache.transaction.xa.XaCommitOutcome;
import net.sf.ehcache.transaction.xa.XaRecoveryOutcome;
import net.sf.ehcache.transaction.xa.XaRollbackOutcome;
import org.terracotta.context.query.Query;

enum StandardOperationStatistic {
    CACHE_GET(true, EhcacheQueryBuilder.cache(), CacheOperationOutcomes.GetOutcome.class, "get", "cache"),
    CACHE_PUT(true, EhcacheQueryBuilder.cache(), CacheOperationOutcomes.PutOutcome.class, "put", "cache"),
    CACHE_REMOVE(true, EhcacheQueryBuilder.cache(), CacheOperationOutcomes.RemoveOutcome.class, "remove", "cache"),
    HEAP_GET(StoreOperationOutcomes.GetOutcome.class, "get", "local-heap"),
    HEAP_PUT(StoreOperationOutcomes.PutOutcome.class, "put", "local-heap"),
    HEAP_REMOVE(StoreOperationOutcomes.RemoveOutcome.class, "remove", "local-heap"),
    OFFHEAP_GET(StoreOperationOutcomes.GetOutcome.class, "get", "local-offheap"),
    OFFHEAP_PUT(StoreOperationOutcomes.PutOutcome.class, "put", "local-offheap"),
    OFFHEAP_REMOVE(StoreOperationOutcomes.RemoveOutcome.class, "remove", "local-offheap"),
    DISK_GET(StoreOperationOutcomes.GetOutcome.class, "get", "local-disk"),
    DISK_PUT(StoreOperationOutcomes.PutOutcome.class, "put", "local-disk"),
    DISK_REMOVE(StoreOperationOutcomes.RemoveOutcome.class, "remove", "local-disk"),
    XA_COMMIT(XaCommitOutcome.class, "xa-commit", "xa-transactional"),
    XA_ROLLBACK(XaRollbackOutcome.class, "xa-rollback", "xa-transactional"),
    XA_RECOVERY(XaRecoveryOutcome.class, "xa-recovery", "xa-transactional"),
    SEARCH(true, (Query)EhcacheQueryBuilder.cache(), CacheOperationOutcomes.SearchOutcome.class, "search", new String[]{"cache"}){

        @Override
        boolean isSearch() {
            return true;
        }
    }
    ,
    EVICTION(false, EhcacheQueryBuilder.cache().add(EhcacheQueryBuilder.children().exclude(Ehcache.class).add(EhcacheQueryBuilder.descendants())), CacheOperationOutcomes.EvictionOutcome.class, "eviction", new String[0]),
    EXPIRY(true, EhcacheQueryBuilder.cache().children(), CacheOperationOutcomes.ExpiredOutcome.class, "expiry", new String[0]),
    CLUSTER_EVENT(CacheOperationOutcomes.ClusterEventOutcomes.class, "cluster", "cache"),
    NONSTOP(CacheOperationOutcomes.NonStopOperationOutcomes.class, "nonstop", "cache"),
    CACHE_ONE_ARG_REPLACE(true, EhcacheQueryBuilder.cache(), CacheOperationOutcomes.ReplaceOneArgOutcome.class, "replace1", "cache"),
    CACHE_TWO_ARG_REPLACE(true, EhcacheQueryBuilder.cache(), CacheOperationOutcomes.ReplaceTwoArgOutcome.class, "replace2", "cache"),
    CACHE_PUT_IF_ABSENT(true, EhcacheQueryBuilder.cache(), CacheOperationOutcomes.PutIfAbsentOutcome.class, "putIfAbsent", "cache"),
    CACHE_REMOVE_ELEMENT(true, EhcacheQueryBuilder.cache(), CacheOperationOutcomes.RemoveElementOutcome.class, "removeElement", "cache");

    private static final int THIRTY = 30;
    private static final int TEN = 10;
    private final boolean required;
    private final Query context;
    private final Class<? extends Enum> type;
    private final String name;
    private final Set<String> tags;

    private StandardOperationStatistic(Class<? extends Enum> type, String name, String ... tags) {
        this(false, type, name, tags);
    }

    private StandardOperationStatistic(boolean required, Class<? extends Enum> type, String name, String ... tags) {
        this(required, EhcacheQueryBuilder.descendants(), type, name, tags);
    }

    private StandardOperationStatistic(boolean required, Query context, Class<? extends Enum> type, String name, String ... tags) {
        this.required = required;
        this.context = context;
        this.type = type;
        this.name = name;
        this.tags = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(tags)));
    }

    final boolean required() {
        return this.required;
    }

    final Query context() {
        return this.context;
    }

    final Class<? extends Enum> type() {
        return this.type;
    }

    final String operationName() {
        return this.name;
    }

    final Set<String> tags() {
        return this.tags;
    }

    boolean isSearch() {
        return false;
    }
}

