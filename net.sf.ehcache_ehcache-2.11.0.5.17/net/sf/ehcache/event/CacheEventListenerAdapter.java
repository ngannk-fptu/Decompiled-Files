/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.event;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class CacheEventListenerAdapter
implements CacheEventListener {
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
    }

    @Override
    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
    }

    @Override
    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
    }

    @Override
    public void notifyElementExpired(Ehcache cache, Element element) {
    }

    @Override
    public void notifyElementEvicted(Ehcache cache, Element element) {
    }

    @Override
    public void notifyRemoveAll(Ehcache cache) {
    }

    @Override
    public void dispose() {
    }
}

