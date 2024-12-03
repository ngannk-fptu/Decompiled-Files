/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.eventual;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.constructs.EhcacheDecoratorAdapter;
import net.sf.ehcache.loader.CacheLoader;
import net.sf.ehcache.store.ElementValueComparator;

public class StronglyConsistentCacheAccessor
extends EhcacheDecoratorAdapter {
    private final ElementValueComparator elementComparator;

    public StronglyConsistentCacheAccessor(Ehcache underlyingCache) throws IllegalArgumentException {
        super(underlyingCache);
        TerracottaConfiguration terracottaConfiguration = underlyingCache.getCacheConfiguration().getTerracottaConfiguration();
        if (terracottaConfiguration == null || terracottaConfiguration.getConsistency() != TerracottaConfiguration.Consistency.EVENTUAL) {
            throw new IllegalArgumentException("This decorator only accepts clustered cache with eventual consistency. " + underlyingCache.getName() + " is not such a cache.");
        }
        this.elementComparator = underlyingCache.getCacheConfiguration().getElementValueComparatorConfiguration().createElementComparatorInstance(underlyingCache.getCacheConfiguration(), underlyingCache.getCacheConfiguration().getClassLoader());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element putIfAbsent(Element element, boolean doNotNotifyCacheReplicators) throws NullPointerException {
        Object objectKey = element.getObjectKey();
        if (objectKey == null) {
            throw new NullPointerException();
        }
        this.acquireWriteLockOnKey(objectKey);
        try {
            Element current = this.getQuiet(objectKey);
            if (current == null) {
                super.put(element, doNotNotifyCacheReplicators);
            }
            Element element2 = current;
            return element2;
        }
        finally {
            this.releaseWriteLockOnKey(objectKey);
        }
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        return this.putIfAbsent(element, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean replace(Element old, Element element) throws NullPointerException, IllegalArgumentException {
        if (old.getObjectKey() == null || element.getObjectKey() == null) {
            throw new NullPointerException();
        }
        if (!old.getObjectKey().equals(element.getObjectKey())) {
            throw new IllegalArgumentException("The keys for the element arguments to replace must be equal");
        }
        Object objectKey = element.getObjectKey();
        this.acquireWriteLockOnKey(objectKey);
        try {
            if (this.elementComparator.equals(this.getQuiet(objectKey), old)) {
                super.put(element);
                boolean bl = true;
                return bl;
            }
        }
        finally {
            this.releaseWriteLockOnKey(objectKey);
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element replace(Element element) throws NullPointerException {
        Object objectKey = element.getObjectKey();
        if (objectKey == null) {
            throw new NullPointerException();
        }
        this.acquireWriteLockOnKey(objectKey);
        try {
            Element current = this.getQuiet(objectKey);
            if (current != null) {
                super.put(element);
            }
            Element element2 = current;
            return element2;
        }
        finally {
            this.releaseWriteLockOnKey(objectKey);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeElement(Element element) throws NullPointerException {
        Object objectKey = element.getObjectKey();
        if (objectKey == null) {
            throw new NullPointerException();
        }
        this.acquireWriteLockOnKey(objectKey);
        try {
            Element current = this.getQuiet(objectKey);
            if (this.elementComparator.equals(current, element)) {
                boolean bl = super.remove(objectKey);
                return bl;
            }
        }
        finally {
            this.releaseWriteLockOnKey(objectKey);
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void put(Element element, boolean doNotNotifyCacheReplicators) throws IllegalArgumentException, IllegalStateException, CacheException {
        if (element == null) {
            return;
        }
        Object objectKey = element.getObjectKey();
        this.acquireWriteLockOnKey(objectKey);
        try {
            super.put(element, doNotNotifyCacheReplicators);
        }
        finally {
            this.releaseWriteLockOnKey(objectKey);
        }
    }

    @Override
    public void put(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.put(element, false);
    }

    @Override
    public void putAll(Collection<Element> elements) throws IllegalArgumentException, IllegalStateException, CacheException {
        for (Element element : elements) {
            this.put(element);
        }
    }

    @Override
    public void putQuiet(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        if (element == null) {
            return;
        }
        Object objectKey = element.getObjectKey();
        this.acquireWriteLockOnKey(objectKey);
        try {
            super.putQuiet(element);
        }
        finally {
            this.releaseWriteLockOnKey(objectKey);
        }
    }

    @Override
    public void putWithWriter(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        if (element == null) {
            return;
        }
        Object objectKey = element.getObjectKey();
        this.acquireWriteLockOnKey(objectKey);
        try {
            super.putWithWriter(element);
        }
        finally {
            this.releaseWriteLockOnKey(objectKey);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Object key, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
        this.acquireWriteLockOnKey(key);
        try {
            boolean bl = super.remove(key, doNotNotifyCacheReplicators);
            return bl;
        }
        finally {
            this.releaseWriteLockOnKey(key);
        }
    }

    @Override
    public void removeAll(Collection<?> keys) throws IllegalStateException {
        this.removeAll(keys, false);
    }

    @Override
    public boolean remove(Object key) throws IllegalStateException {
        return this.remove(key, false);
    }

    @Override
    public void removeAll(Collection<?> keys, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
        for (Object key : keys) {
            this.remove(key);
        }
    }

    @Override
    public boolean remove(Serializable key, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
        return this.remove((Object)key, doNotNotifyCacheReplicators);
    }

    @Override
    public boolean remove(Serializable key) throws IllegalStateException {
        return this.remove((Object)key);
    }

    @Override
    public boolean removeQuiet(Object key) throws IllegalStateException {
        this.acquireWriteLockOnKey(key);
        try {
            boolean bl = super.removeQuiet(key);
            return bl;
        }
        finally {
            this.releaseWriteLockOnKey(key);
        }
    }

    @Override
    public boolean removeQuiet(Serializable key) throws IllegalStateException {
        return this.removeQuiet((Object)key);
    }

    @Override
    public boolean removeWithWriter(Object key) throws IllegalStateException, CacheException {
        this.acquireWriteLockOnKey(key);
        try {
            boolean bl = super.removeWithWriter(key);
            return bl;
        }
        finally {
            this.releaseWriteLockOnKey(key);
        }
    }

    @Override
    public Element removeAndReturnElement(Object key) throws IllegalStateException {
        this.acquireWriteLockOnKey(key);
        try {
            Element element = super.removeAndReturnElement(key);
            return element;
        }
        finally {
            this.releaseWriteLockOnKey(key);
        }
    }

    @Override
    public Element get(Object key) throws IllegalStateException, CacheException {
        this.acquireReadLockOnKey(key);
        try {
            Element element = super.get(key);
            return element;
        }
        finally {
            this.releaseReadLockOnKey(key);
        }
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) throws IllegalStateException, CacheException {
        HashMap<Object, Element> result = new HashMap<Object, Element>();
        for (Object key : keys) {
            result.put(key, this.get(key));
        }
        return result;
    }

    @Override
    public Element get(Serializable key) throws IllegalStateException, CacheException {
        return this.get((Object)key);
    }

    @Override
    public Element getQuiet(Object key) throws IllegalStateException, CacheException {
        this.acquireReadLockOnKey(key);
        try {
            Element element = super.getQuiet(key);
            return element;
        }
        finally {
            this.releaseReadLockOnKey(key);
        }
    }

    @Override
    public Element getQuiet(Serializable key) throws IllegalStateException, CacheException {
        return this.getQuiet((Object)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element getWithLoader(Object key, CacheLoader loader, Object loaderArgument) throws CacheException {
        this.acquireReadLockOnKey(key);
        try {
            Element element = super.getWithLoader(key, loader, loaderArgument);
            return element;
        }
        finally {
            this.releaseReadLockOnKey(key);
        }
    }

    @Override
    public Map getAllWithLoader(Collection keys, Object loaderArgument) throws CacheException {
        HashMap result = new HashMap(keys.size());
        for (Object key : keys) {
            Element element = this.getWithLoader(key, null, loaderArgument);
            if (element != null) {
                result.put(key, element.getObjectValue());
                continue;
            }
            result.put(key, null);
        }
        return result;
    }
}

