/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache.memcached;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.impl.client.cache.memcached.MemcachedCacheEntry;
import org.apache.http.impl.client.cache.memcached.MemcachedSerializationException;

public class MemcachedCacheEntryImpl
implements MemcachedCacheEntry {
    private String key;
    private HttpCacheEntry httpCacheEntry;

    public MemcachedCacheEntryImpl(String key, HttpCacheEntry httpCacheEntry) {
        this.key = key;
        this.httpCacheEntry = httpCacheEntry;
    }

    public MemcachedCacheEntryImpl() {
    }

    @Override
    public synchronized byte[] toByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this.key);
            oos.writeObject(this.httpCacheEntry);
            oos.close();
        }
        catch (IOException ioe) {
            throw new MemcachedSerializationException(ioe);
        }
        return bos.toByteArray();
    }

    @Override
    public synchronized String getStorageKey() {
        return this.key;
    }

    @Override
    public synchronized HttpCacheEntry getHttpCacheEntry() {
        return this.httpCacheEntry;
    }

    @Override
    public synchronized void set(byte[] bytes) {
        HttpCacheEntry entry;
        String s;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            s = (String)ois.readObject();
            entry = (HttpCacheEntry)ois.readObject();
            ois.close();
            bis.close();
        }
        catch (IOException ioe) {
            throw new MemcachedSerializationException(ioe);
        }
        catch (ClassNotFoundException cnfe) {
            throw new MemcachedSerializationException(cnfe);
        }
        this.key = s;
        this.httpCacheEntry = entry;
    }
}

