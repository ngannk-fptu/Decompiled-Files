/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Function
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  org.apache.commons.lang3.tuple.MutablePair
 */
package net.java.ao;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.java.ao.sql.SqlUtils;
import org.apache.commons.lang3.tuple.MutablePair;

public final class CachingSqlProcessor {
    private static final long MAXIMUM_CACHED_CLAUSES = Long.parseLong(System.getProperty("net.java.ao.CachingSqlProcessor.MAXIMUM_CACHED_CLAUSES", "100"));
    @VisibleForTesting
    final LoadingCache<ClauseAndProcessor, String> processedWhereClauses = CachingSqlProcessor.buildCache(args -> SqlUtils.processWhereClause((String)args.getLeft(), (Function<String, String>)((Function)((java.util.function.Function)args.getRight())::apply)));
    @VisibleForTesting
    final LoadingCache<ClauseAndProcessor, String> processedOnClauses = CachingSqlProcessor.buildCache(args -> SqlUtils.processOnClause((String)args.getLeft(), (Function<String, String>)((Function)((java.util.function.Function)args.getRight())::apply)));

    CachingSqlProcessor() {
    }

    public String processWhereClause(String where, java.util.function.Function<String, String> processor) {
        return (String)this.processedWhereClauses.getUnchecked((Object)new ClauseAndProcessor(where, processor));
    }

    public String processOnClause(String on, java.util.function.Function<String, String> processor) {
        return (String)this.processedOnClauses.getUnchecked((Object)new ClauseAndProcessor(on, processor));
    }

    private static LoadingCache<ClauseAndProcessor, String> buildCache(final java.util.function.Function<ClauseAndProcessor, String> loadingFunction) {
        CacheLoader<ClauseAndProcessor, String> loader = new CacheLoader<ClauseAndProcessor, String>(){

            public String load(ClauseAndProcessor key) {
                return (String)loadingFunction.apply(key);
            }
        };
        return CacheBuilder.newBuilder().maximumSize(MAXIMUM_CACHED_CLAUSES).build((CacheLoader)loader);
    }

    private static final class ClauseAndProcessor
    extends MutablePair<String, java.util.function.Function<String, String>> {
        private ClauseAndProcessor(String clause, java.util.function.Function<String, String> processor) {
            super((Object)clause, processor);
        }
    }
}

