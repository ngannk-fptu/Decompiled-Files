/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer;

import java.util.Collection;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.writebehind.operations.SingleOperationType;

public interface CacheWriter {
    public CacheWriter clone(Ehcache var1) throws CloneNotSupportedException;

    public void init();

    public void dispose() throws CacheException;

    public void write(Element var1) throws CacheException;

    public void writeAll(Collection<Element> var1) throws CacheException;

    public void delete(CacheEntry var1) throws CacheException;

    public void deleteAll(Collection<CacheEntry> var1) throws CacheException;

    public void throwAway(Element var1, SingleOperationType var2, RuntimeException var3);
}

