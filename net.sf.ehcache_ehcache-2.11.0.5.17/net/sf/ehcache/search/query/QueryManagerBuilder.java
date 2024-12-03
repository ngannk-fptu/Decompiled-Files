/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.query;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.search.query.QueryManager;

public final class QueryManagerBuilder {
    private final Collection<Ehcache> caches = new ArrayList<Ehcache>();
    private final Class<? extends QueryManager> defaultClass;

    private QueryManagerBuilder() {
        this(QueryManagerBuilder.getImplementationClass());
    }

    QueryManagerBuilder(Class<? extends QueryManager> implementationClass) {
        this.defaultClass = implementationClass;
    }

    private static Class<? extends QueryManager> getImplementationClass() {
        try {
            return Class.forName("net.sf.ehcache.search.parser.QueryManagerImpl");
        }
        catch (ClassNotFoundException e) {
            throw new CacheException(e);
        }
    }

    public static QueryManagerBuilder newQueryManagerBuilder() {
        return new QueryManagerBuilder();
    }

    public QueryManagerBuilder addCache(Ehcache cache) {
        this.caches.add(cache);
        return this;
    }

    public QueryManagerBuilder addAllCachesCurrentlyIn(CacheManager cacheManager) {
        for (String s : cacheManager.getCacheNames()) {
            Ehcache cache = cacheManager.getEhcache(s);
            if (cache == null) continue;
            this.caches.add(cache);
        }
        return this;
    }

    public QueryManager build() {
        try {
            Constructor<? extends QueryManager> constructor = this.defaultClass.getConstructor(Collection.class);
            return constructor.newInstance(this.caches);
        }
        catch (Exception e) {
            throw new CacheException(e);
        }
    }
}

