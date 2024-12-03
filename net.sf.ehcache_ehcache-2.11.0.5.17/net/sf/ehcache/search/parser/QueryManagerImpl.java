/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.parser.EhcacheSearchParser;
import net.sf.ehcache.search.parser.ParseException;
import net.sf.ehcache.search.parser.ParseModel;
import net.sf.ehcache.search.parser.TokenMgrError;
import net.sf.ehcache.search.query.QueryManager;

public class QueryManagerImpl
implements QueryManager {
    private final Map<CacheManager, List<Ehcache>> cacheManagerEhcacheMap = new HashMap<CacheManager, List<Ehcache>>();

    public QueryManagerImpl(Collection<Ehcache> ehcaches) {
        for (Ehcache ehcache : ehcaches) {
            CacheManager cm = ehcache.getCacheManager();
            if (this.cacheManagerEhcacheMap.containsKey(cm)) {
                this.cacheManagerEhcacheMap.get(cm).add(ehcache);
                continue;
            }
            ArrayList<Ehcache> ehcacheList = new ArrayList<Ehcache>();
            ehcacheList.add(ehcache);
            this.cacheManagerEhcacheMap.put(cm, ehcacheList);
        }
    }

    Results search(Ehcache cache, String statement) throws SearchException {
        return this.createQuery(cache, statement).end().execute();
    }

    @Override
    public Query createQuery(String statement) throws SearchException {
        Map<String, String> cacheManagerCacheNameMap = this.extractSearchCacheName(statement);
        String cacheManagerName = cacheManagerCacheNameMap.values().iterator().next();
        String cacheName = cacheManagerCacheNameMap.keySet().iterator().next();
        if (cacheManagerCacheNameMap.size() == 0) {
            throw new SearchException("Please specify the cache's name with the FROM clause.");
        }
        return this.createQuery(this.getCache(cacheName, cacheManagerName), statement);
    }

    Map<String, String> extractSearchCacheName(String statement) throws SearchException {
        EhcacheSearchParser parser = new EhcacheSearchParser(new StringReader(statement));
        ParseModel model = null;
        try {
            model = parser.QueryStatement();
        }
        catch (ParseException p) {
            throw new SearchException(p);
        }
        catch (TokenMgrError e) {
            throw new SearchException(e);
        }
        HashMap<String, String> retMap = new HashMap<String, String>();
        String cacheName = model.getCacheName();
        String cacheManagerName = model.getCacheManagerName();
        retMap.put(cacheName, cacheManagerName);
        return retMap;
    }

    private Query createQuery(Ehcache cache, String statement) throws SearchException {
        ParseModel model;
        EhcacheSearchParser parser = new EhcacheSearchParser(new StringReader(statement));
        try {
            model = parser.QueryStatement();
        }
        catch (ParseException p) {
            throw new SearchException(p);
        }
        catch (TokenMgrError e) {
            throw new SearchException(e);
        }
        return model.getQuery(cache);
    }

    private Ehcache getCache(String cacheName, String cacheManagerName) throws CacheException {
        Ehcache cache = null;
        ArrayList<Ehcache> foundCaches = new ArrayList<Ehcache>();
        int numCachesFound = 0;
        for (List<Ehcache> ehcacheList : this.cacheManagerEhcacheMap.values()) {
            for (Ehcache c : ehcacheList) {
                if (!c.getName().equals(cacheName)) continue;
                ++numCachesFound;
                cache = c;
                foundCaches.add(c);
            }
        }
        if (numCachesFound == 0) {
            throw new CacheException("The cache '" + cacheName + "' specified with the FROM clause could not be found.");
        }
        if (numCachesFound > 1 && cacheManagerName == null) {
            throw new CacheException("More than one cache with the same name '" + cacheName + "' was found");
        }
        if (cacheManagerName == null) {
            return cache;
        }
        for (Ehcache ehcache : foundCaches) {
            if (!ehcache.getCacheManager().getName().equals(cacheManagerName)) continue;
            return ehcache;
        }
        throw new CacheException("Cache with the name " + cacheName + " was not found in " + cache.getCacheManager().getName() + " , Expected cache manager name = " + cacheManagerName);
    }
}

