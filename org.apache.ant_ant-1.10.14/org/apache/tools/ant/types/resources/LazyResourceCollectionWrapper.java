/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.AbstractResourceCollectionWrapper;

public class LazyResourceCollectionWrapper
extends AbstractResourceCollectionWrapper {
    private final List<Resource> cachedResources = new ArrayList<Resource>();
    private Iterator<Resource> filteringIterator;
    private final Supplier<Iterator<Resource>> filteringIteratorSupplier = () -> new FilteringIterator(this.getResourceCollection().iterator());

    @Override
    protected Iterator<Resource> createIterator() {
        if (this.isCache()) {
            if (this.filteringIterator == null) {
                this.filteringIterator = this.filteringIteratorSupplier.get();
            }
            return new CachedIterator(this.filteringIterator);
        }
        return this.filteringIteratorSupplier.get();
    }

    @Override
    protected int getSize() {
        Iterator<Resource> it = this.createIterator();
        int size = 0;
        while (it.hasNext()) {
            it.next();
            ++size;
        }
        return size;
    }

    protected boolean filterResource(Resource r) {
        return false;
    }

    private class CachedIterator
    implements Iterator<Resource> {
        int cursor = 0;
        private final Iterator<Resource> it;

        public CachedIterator(Iterator<Resource> it) {
            this.it = it;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean hasNext() {
            List list = LazyResourceCollectionWrapper.this.cachedResources;
            synchronized (list) {
                if (LazyResourceCollectionWrapper.this.cachedResources.size() > this.cursor) {
                    return true;
                }
                if (!this.it.hasNext()) {
                    return false;
                }
                Resource r = this.it.next();
                LazyResourceCollectionWrapper.this.cachedResources.add(r);
            }
            return true;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Resource next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            List list = LazyResourceCollectionWrapper.this.cachedResources;
            synchronized (list) {
                return (Resource)LazyResourceCollectionWrapper.this.cachedResources.get(this.cursor++);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class FilteringIterator
    implements Iterator<Resource> {
        Resource next = null;
        boolean ended = false;
        protected final Iterator<Resource> it;

        FilteringIterator(Iterator<Resource> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            if (this.ended) {
                return false;
            }
            while (this.next == null) {
                if (!this.it.hasNext()) {
                    this.ended = true;
                    return false;
                }
                this.next = this.it.next();
                if (!LazyResourceCollectionWrapper.this.filterResource(this.next)) continue;
                this.next = null;
            }
            return true;
        }

        @Override
        public Resource next() {
            if (!this.hasNext()) {
                throw new UnsupportedOperationException();
            }
            Resource r = this.next;
            this.next = null;
            return r;
        }
    }
}

