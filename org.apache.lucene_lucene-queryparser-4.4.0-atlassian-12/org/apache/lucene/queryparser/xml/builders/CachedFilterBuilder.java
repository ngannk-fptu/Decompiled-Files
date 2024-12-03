/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.CachingWrapperFilter
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.QueryWrapperFilter
 */
package org.apache.lucene.queryparser.xml.builders;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.FilterBuilder;
import org.apache.lucene.queryparser.xml.FilterBuilderFactory;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.queryparser.xml.QueryBuilderFactory;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.w3c.dom.Element;

public class CachedFilterBuilder
implements FilterBuilder {
    private final QueryBuilderFactory queryFactory;
    private final FilterBuilderFactory filterFactory;
    private LRUCache<Object, Filter> filterCache;
    private final int cacheSize;

    public CachedFilterBuilder(QueryBuilderFactory queryFactory, FilterBuilderFactory filterFactory, int cacheSize) {
        this.queryFactory = queryFactory;
        this.filterFactory = filterFactory;
        this.cacheSize = cacheSize;
    }

    @Override
    public synchronized Filter getFilter(Element e) throws ParserException {
        Element childElement = DOMUtils.getFirstChildOrFail(e);
        if (this.filterCache == null) {
            this.filterCache = new LRUCache(this.cacheSize);
        }
        QueryBuilder qb = this.queryFactory.getQueryBuilder(childElement.getNodeName());
        Query cacheKey = null;
        Query q = null;
        Filter f = null;
        if (qb != null) {
            cacheKey = q = qb.getQuery(childElement);
        } else {
            f = this.filterFactory.getFilter(childElement);
            cacheKey = f;
        }
        Object cachedFilter = (Filter)this.filterCache.get(cacheKey);
        if (cachedFilter != null) {
            return cachedFilter;
        }
        cachedFilter = qb != null ? new QueryWrapperFilter(q) : new CachingWrapperFilter(f);
        this.filterCache.put(cacheKey, (Filter)cachedFilter);
        return cachedFilter;
    }

    static class LRUCache<K, V>
    extends LinkedHashMap<K, V> {
        protected int maxsize;

        public LRUCache(int maxsize) {
            super(maxsize * 4 / 3 + 1, 0.75f, true);
            this.maxsize = maxsize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return this.size() > this.maxsize;
        }
    }
}

