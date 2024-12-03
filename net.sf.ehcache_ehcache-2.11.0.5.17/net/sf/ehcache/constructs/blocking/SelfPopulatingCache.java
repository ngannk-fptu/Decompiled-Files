/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.constructs.blocking;

import java.util.List;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfPopulatingCache
extends BlockingCache {
    private static final Logger LOG = LoggerFactory.getLogger((String)SelfPopulatingCache.class.getName());
    protected final CacheEntryFactory factory;

    public SelfPopulatingCache(Ehcache cache, CacheEntryFactory factory) throws CacheException {
        super(cache);
        this.factory = factory;
    }

    public SelfPopulatingCache(Ehcache cache, int numberOfStripes, CacheEntryFactory factory) throws CacheException {
        super(cache, numberOfStripes);
        this.factory = factory;
    }

    @Override
    public Element get(Object key) throws LockTimeoutException {
        Element element = super.get(key);
        if (element == null) {
            try {
                Object value = this.factory.createEntry(key);
                element = SelfPopulatingCache.makeAndCheckElement(key, value);
            }
            catch (Throwable throwable) {
                element = new Element(key, null);
                throw new CacheException("Could not fetch object for cache entry with key \"" + key + "\".", throwable);
            }
            finally {
                this.put(element);
            }
        }
        return element;
    }

    public void refresh() throws CacheException {
        this.refresh(true);
    }

    public void refresh(boolean quiet) throws CacheException {
        Throwable exception = null;
        Object keyWithException = null;
        List keys = this.getKeys();
        LOG.debug(this.getName() + ": found " + keys.size() + " keys to refresh");
        for (Object key : keys) {
            try {
                Ehcache backingCache = this.getCache();
                Element element = backingCache.getQuiet(key);
                if (element == null) {
                    if (!LOG.isDebugEnabled()) continue;
                    LOG.debug(this.getName() + ": entry with key " + key + " has been removed - skipping it");
                    continue;
                }
                this.refreshElement(element, backingCache, quiet);
            }
            catch (Exception e) {
                LOG.warn(this.getName() + "Could not refresh element " + key, (Throwable)e);
                keyWithException = key;
                exception = e;
            }
        }
        if (exception != null) {
            throw new CacheException(exception.getMessage() + " on refresh with key " + keyWithException, exception);
        }
    }

    public Element refresh(Object key) throws CacheException {
        return this.refresh(key, true);
    }

    public Element refresh(Object key, boolean quiet) throws CacheException {
        try {
            Ehcache backingCache = this.getCache();
            Element element = backingCache.getQuiet(key);
            if (element != null) {
                return this.refreshElement(element, backingCache, quiet);
            }
            return this.get(key);
        }
        catch (CacheException ce) {
            throw ce;
        }
        catch (Exception e) {
            throw new CacheException(e.getMessage() + " on refresh with key " + key, e);
        }
    }

    protected void refreshElement(Element element, Ehcache backingCache) throws Exception {
        this.refreshElement(element, backingCache, true);
    }

    protected Element refreshElement(Element element, Ehcache backingCache, boolean quiet) throws Exception {
        Element replacementElement;
        Object key = element.getObjectKey();
        if (LOG.isDebugEnabled()) {
            LOG.debug(this.getName() + ": refreshing element with key " + key);
        }
        if (this.factory instanceof UpdatingCacheEntryFactory) {
            replacementElement = element;
            ((UpdatingCacheEntryFactory)this.factory).updateEntryValue(key, replacementElement.getObjectValue());
        } else {
            Object value = this.factory.createEntry(key);
            replacementElement = SelfPopulatingCache.makeAndCheckElement(key, value);
        }
        if (quiet) {
            backingCache.putQuiet(replacementElement);
        } else {
            backingCache.put(replacementElement);
        }
        return replacementElement;
    }

    protected static Element makeAndCheckElement(Object key, Object value) throws CacheException {
        if (!(value instanceof Element)) {
            return new Element(key, value);
        }
        Element element = (Element)value;
        if (element.getObjectKey() == null && key == null) {
            return element;
        }
        if (element.getObjectKey() == null) {
            throw new CacheException("CacheEntryFactory returned an Element with a null key");
        }
        if (!element.getObjectKey().equals(key)) {
            throw new CacheException("CacheEntryFactory returned an Element with a different key: " + element.getObjectKey() + " compared to the key that was requested: " + key);
        }
        return element;
    }
}

