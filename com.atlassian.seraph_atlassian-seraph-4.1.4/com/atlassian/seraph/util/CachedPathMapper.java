/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.util;

import com.atlassian.seraph.util.IPathMapper;
import com.atlassian.seraph.util.PathMapper;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachedPathMapper
implements IPathMapper {
    private static final String NULL = new String("NULL");
    private final PathMapper delegate = new PathMapper();
    private final ConcurrentMap<String, String> cacheMap;
    private final ConcurrentMap<String, Collection<String>> cacheAllMap;

    public CachedPathMapper() {
        this(new ConcurrentHashMap<String, String>(), new ConcurrentHashMap<String, Collection<String>>());
    }

    public CachedPathMapper(ConcurrentMap<String, String> cacheMap, ConcurrentMap<String, Collection<String>> cacheAllMap) {
        this.cacheMap = cacheMap;
        this.cacheAllMap = cacheAllMap;
    }

    @Override
    public String get(String path) {
        String result = (String)this.cacheMap.get(path);
        while (result == null) {
            String value = this.delegate.get(path);
            if (value == null) {
                value = NULL;
            }
            this.cacheMap.putIfAbsent(path, value);
            result = (String)this.cacheMap.get(path);
        }
        return result == NULL ? null : result;
    }

    @Override
    public Collection<String> getAll(String path) {
        Collection result = (Collection)this.cacheAllMap.get(path);
        while (result == null) {
            this.cacheAllMap.putIfAbsent(path, this.delegate.getAll(path));
            result = (Collection)this.cacheAllMap.get(path);
        }
        return result;
    }

    public void set(Map<String, String> patterns) {
        Iterator<Map.Entry<String, String>> iterator = patterns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry2;
            Map.Entry<String, String> entry = entry2 = iterator.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void put(String key, String pattern) {
        this.delegate.put(key, pattern);
        this.cacheMap.remove(key);
        this.cacheAllMap.remove(key);
    }

    public String toString() {
        return this.delegate.toString();
    }
}

