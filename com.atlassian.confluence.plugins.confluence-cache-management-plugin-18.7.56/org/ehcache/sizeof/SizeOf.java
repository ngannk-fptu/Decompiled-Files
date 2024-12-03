/*
 * Decompiled with CFR 0.152.
 */
package org.ehcache.sizeof;

import org.ehcache.sizeof.ObjectGraphWalker;
import org.ehcache.sizeof.VisitorListener;
import org.ehcache.sizeof.filters.CombinationSizeOfFilter;
import org.ehcache.sizeof.filters.SizeOfFilter;
import org.ehcache.sizeof.impl.AgentSizeOf;
import org.ehcache.sizeof.impl.ReflectionSizeOf;
import org.ehcache.sizeof.impl.UnsafeSizeOf;
import org.ehcache.sizeof.util.WeakIdentityConcurrentMap;

public abstract class SizeOf {
    private final ObjectGraphWalker walker;

    public SizeOf(SizeOfFilter fieldFilter, boolean caching, boolean bypassFlyweight) {
        ObjectGraphWalker.Visitor visitor = caching ? new CachingSizeOfVisitor() : new SizeOfVisitor();
        this.walker = new ObjectGraphWalker(visitor, fieldFilter, bypassFlyweight);
    }

    public abstract long sizeOf(Object var1);

    public long deepSizeOf(VisitorListener listener, Object ... obj) {
        return this.walker.walk(listener, obj);
    }

    public long deepSizeOf(Object ... obj) {
        return this.walker.walk(null, obj);
    }

    public static SizeOf newInstance(SizeOfFilter ... filters) {
        return SizeOf.newInstance(true, true, filters);
    }

    public static SizeOf newInstance(boolean bypassFlyweight, boolean cache, SizeOfFilter ... filters) {
        CombinationSizeOfFilter filter = new CombinationSizeOfFilter(filters);
        try {
            return new AgentSizeOf(filter, cache, bypassFlyweight);
        }
        catch (UnsupportedOperationException e) {
            try {
                return new UnsafeSizeOf(filter, cache, bypassFlyweight);
            }
            catch (UnsupportedOperationException f) {
                try {
                    return new ReflectionSizeOf(filter, cache, bypassFlyweight);
                }
                catch (UnsupportedOperationException g) {
                    throw new UnsupportedOperationException("A suitable SizeOf engine could not be loaded: " + e + ", " + f + ", " + g);
                }
            }
        }
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

