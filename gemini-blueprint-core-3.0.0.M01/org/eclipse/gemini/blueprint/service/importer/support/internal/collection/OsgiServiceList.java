/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.collection;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceProxyCreator;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicCollection;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicList;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.OsgiServiceCollection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;

public class OsgiServiceList
extends OsgiServiceCollection
implements List,
RandomAccess {
    protected List storage;

    public OsgiServiceList(Filter filter, BundleContext context, ClassLoader classLoader, ServiceProxyCreator proxyCreator, boolean useServiceReference) {
        super(filter, context, classLoader, proxyCreator, useServiceReference);
    }

    protected DynamicCollection createInternalDynamicStorage() {
        this.storage = new DynamicList();
        return (DynamicCollection)((Object)this.storage);
    }

    public Object get(int index) {
        return this.storage.get(index);
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    public ListIterator listIterator() {
        return this.listIterator(0);
    }

    public ListIterator listIterator(int index) {
        return new OsgiServiceListIterator(index);
    }

    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    public Object remove(int index) {
        throw new UnsupportedOperationException();
    }

    public Object set(int index, Object o) {
        throw new UnsupportedOperationException();
    }

    public void add(int index, Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    protected class OsgiServiceListIterator
    implements ListIterator {
        private final ListIterator iter;

        public OsgiServiceListIterator(int index) {
            this.iter = OsgiServiceList.this.storage.listIterator(index);
        }

        @Override
        public Object next() {
            return this.iter.next();
        }

        public Object previous() {
            return this.iter.previous();
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.iter.hasPrevious();
        }

        @Override
        public int nextIndex() {
            return this.iter.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.iter.previousIndex();
        }

        public void add(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void set(Object o) {
            throw new UnsupportedOperationException();
        }
    }
}

