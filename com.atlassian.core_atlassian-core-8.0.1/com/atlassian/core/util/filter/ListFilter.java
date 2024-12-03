/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util.filter;

import com.atlassian.core.util.filter.Filter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Deprecated
public class ListFilter<T> {
    private final Filter<T> filter;
    private static final Object MYNULL = new Object();

    public ListFilter(Filter<T> filter) {
        this.filter = filter;
    }

    public List<T> filterList(List<T> list) {
        if (list == null) {
            return list;
        }
        ArrayList<T> filteredList = new ArrayList<T>();
        Iterator<T> i = this.filterIterator(list.iterator());
        while (i.hasNext()) {
            filteredList.add(i.next());
        }
        return filteredList;
    }

    public Iterator<T> filterIterator(Iterator<T> iterator) {
        return new FilteredIterator<T>(iterator);
    }

    private class FilteredIterator<E extends T>
    implements Iterator<T> {
        private final Iterator<E> innerIterator;
        private T savedObject = this.myNull();

        public FilteredIterator(Iterator<E> innerIterator) {
            this.innerIterator = innerIterator;
        }

        @Override
        public void remove() {
            this.innerIterator.remove();
        }

        @Override
        public boolean hasNext() {
            if (this.savedObject != MYNULL) {
                return true;
            }
            while (this.innerIterator.hasNext()) {
                this.savedObject = this.innerIterator.next();
                if (!ListFilter.this.filter.isIncluded(this.savedObject)) continue;
                return true;
            }
            this.savedObject = this.myNull();
            return false;
        }

        @Override
        public T next() {
            E o;
            if (this.savedObject != MYNULL) {
                return this.clearSavedObject();
            }
            do {
                o = this.innerIterator.next();
            } while (!ListFilter.this.filter.isIncluded(o));
            return o;
        }

        private T clearSavedObject() {
            Object ret = this.savedObject;
            this.savedObject = this.myNull();
            return ret;
        }

        private T myNull() {
            return MYNULL;
        }
    }
}

