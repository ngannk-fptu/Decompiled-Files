/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind;

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.OperationsFilter;

public interface WriteBehind {
    public void start(CacheWriter var1) throws CacheException;

    public void write(Element var1);

    public void delete(CacheEntry var1);

    public void setOperationsFilter(OperationsFilter var1);

    public void stop() throws CacheException;

    public long getQueueSize();
}

