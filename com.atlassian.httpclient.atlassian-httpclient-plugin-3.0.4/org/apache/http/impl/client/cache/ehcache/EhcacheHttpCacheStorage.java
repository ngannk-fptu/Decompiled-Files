/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 */
package org.apache.http.impl.client.cache.ehcache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheEntrySerializer;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.DefaultHttpCacheEntrySerializer;

public class EhcacheHttpCacheStorage
implements HttpCacheStorage {
    private final Ehcache cache;
    private final HttpCacheEntrySerializer serializer;
    private final int maxUpdateRetries;

    public EhcacheHttpCacheStorage(Ehcache cache) {
        this(cache, CacheConfig.DEFAULT, new DefaultHttpCacheEntrySerializer());
    }

    public EhcacheHttpCacheStorage(Ehcache cache, CacheConfig config) {
        this(cache, config, new DefaultHttpCacheEntrySerializer());
    }

    public EhcacheHttpCacheStorage(Ehcache cache, CacheConfig config, HttpCacheEntrySerializer serializer) {
        this.cache = cache;
        this.maxUpdateRetries = config.getMaxUpdateRetries();
        this.serializer = serializer;
    }

    @Override
    public synchronized void putEntry(String key, HttpCacheEntry entry) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.serializer.writeTo(entry, bos);
        this.cache.put(new Element((Serializable)((Object)key), (Serializable)bos.toByteArray()));
    }

    @Override
    public synchronized HttpCacheEntry getEntry(String key) throws IOException {
        Element e = this.cache.get((Serializable)((Object)key));
        if (e == null) {
            return null;
        }
        byte[] data = (byte[])e.getObjectValue();
        return this.serializer.readFrom(new ByteArrayInputStream(data));
    }

    @Override
    public synchronized void removeEntry(String key) {
        this.cache.remove((Serializable)((Object)key));
    }

    @Override
    public synchronized void updateEntry(String key, HttpCacheUpdateCallback callback) throws IOException, HttpCacheUpdateException {
        int numRetries = 0;
        do {
            Element oldElement = this.cache.get((Serializable)((Object)key));
            HttpCacheEntry existingEntry = null;
            if (oldElement != null) {
                byte[] data = (byte[])oldElement.getObjectValue();
                existingEntry = this.serializer.readFrom(new ByteArrayInputStream(data));
            }
            HttpCacheEntry updatedEntry = callback.update(existingEntry);
            if (existingEntry == null) {
                this.putEntry(key, updatedEntry);
                return;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            this.serializer.writeTo(updatedEntry, bos);
            Element newElement = new Element((Serializable)((Object)key), (Serializable)bos.toByteArray());
            if (!this.cache.replace(oldElement, newElement)) continue;
            return;
        } while (++numRetries <= this.maxUpdateRetries);
        throw new HttpCacheUpdateException("Failed to update");
    }
}

