/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.event;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public interface CacheEventListener
extends Cloneable {
    public void notifyElementRemoved(Ehcache var1, Element var2) throws CacheException;

    public void notifyElementPut(Ehcache var1, Element var2) throws CacheException;

    public void notifyElementUpdated(Ehcache var1, Element var2) throws CacheException;

    public void notifyElementExpired(Ehcache var1, Element var2);

    public void notifyElementEvicted(Ehcache var1, Element var2);

    public void notifyRemoveAll(Ehcache var1);

    public Object clone() throws CloneNotSupportedException;

    public void dispose();
}

