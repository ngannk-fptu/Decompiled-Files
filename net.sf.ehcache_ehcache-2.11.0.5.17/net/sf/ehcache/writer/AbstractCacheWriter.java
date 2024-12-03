/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer;

import java.util.Collection;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.operations.SingleOperationType;

public abstract class AbstractCacheWriter
implements CacheWriter {
    @Override
    public void write(Element element) throws CacheException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeAll(Collection<Element> elements) throws CacheException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(CacheEntry entry) throws CacheException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Collection<CacheEntry> entries) throws CacheException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void throwAway(Element element, SingleOperationType operationType, RuntimeException e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CacheWriter clone(Ehcache cache) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public void init() {
    }

    @Override
    public void dispose() throws CacheException {
    }
}

