/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.impl.pagination;

import com.atlassian.confluence.api.impl.pagination.PagingIterator;
import java.util.Iterator;

public class PagingIterators {
    public static <T> PagingIterator<T> from(Iterator<T> it) {
        return new ForwardingPagingIterator<T>(it);
    }

    static class ForwardingPagingIterator<T>
    implements PagingIterator<T> {
        private final Iterator<T> delegate;

        ForwardingPagingIterator(Iterator<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override
        public T next() {
            return this.delegate.next();
        }

        @Override
        public void remove() {
            this.delegate.remove();
        }
    }
}

