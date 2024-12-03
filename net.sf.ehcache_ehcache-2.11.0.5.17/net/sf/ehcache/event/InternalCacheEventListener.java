/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.event;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

interface InternalCacheEventListener
extends Cloneable {
    public void notifyElementRemoved(Ehcache var1, Element var2) throws CacheException;

    public void notifyElementPut(Ehcache var1, Element var2) throws CacheException;

    public void dispose();
}

