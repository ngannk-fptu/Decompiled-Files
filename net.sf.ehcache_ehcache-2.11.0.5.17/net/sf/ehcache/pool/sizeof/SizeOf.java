/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.pool.sizeof;

import net.sf.ehcache.pool.Size;
import net.sf.ehcache.pool.sizeof.ObjectGraphWalker;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;
import net.sf.ehcache.util.WeakIdentityConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SizeOf {
    private static final Logger LOG = LoggerFactory.getLogger((String)SizeOf.class.getName());
    private final ObjectGraphWalker walker;

    public SizeOf(SizeOfFilter fieldFilter, boolean caching) {
        ObjectGraphWalker.Visitor visitor = caching ? new CachingSizeOfVisitor() : new SizeOfVisitor();
        this.walker = new ObjectGraphWalker(visitor, fieldFilter);
    }

    public abstract long sizeOf(Object var1);

    public Size deepSizeOf(int maxDepth, boolean abortWhenMaxDepthExceeded, Object ... obj) {
        return new Size(this.walker.walk(maxDepth, abortWhenMaxDepthExceeded, obj), true);
    }

    private class CachingSizeOfVisitor
    implements ObjectGraphWalker.Visitor {
        private final WeakIdentityConcurrentMap<Class<?>, Long> cache = new WeakIdentityConcurrentMap();

        private CachingSizeOfVisitor() {
        }

        @Override
        public long visit(Object object) {
            Class<?> klazz = object.getClass();
            Long cachedSize = this.cache.get(klazz);
            if (cachedSize == null) {
                if (klazz.isArray()) {
                    return SizeOf.this.sizeOf(object);
                }
                long size = SizeOf.this.sizeOf(object);
                this.cache.put(klazz, size);
                return size;
            }
            return cachedSize;
        }
    }

    private class SizeOfVisitor
    implements ObjectGraphWalker.Visitor {
        private SizeOfVisitor() {
        }

        @Override
        public long visit(Object object) {
            return SizeOf.this.sizeOf(object);
        }
    }
}

