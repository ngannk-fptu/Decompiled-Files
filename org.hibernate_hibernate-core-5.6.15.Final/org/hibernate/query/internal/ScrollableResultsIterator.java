/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.internal;

import org.hibernate.Incubating;
import org.hibernate.query.spi.CloseableIterator;
import org.hibernate.query.spi.ScrollableResultsImplementor;

@Incubating
class ScrollableResultsIterator<T>
implements CloseableIterator {
    private final ScrollableResultsImplementor scrollableResults;

    ScrollableResultsIterator(ScrollableResultsImplementor scrollableResults) {
        this.scrollableResults = scrollableResults;
    }

    @Override
    public void close() {
        this.scrollableResults.close();
    }

    @Override
    public boolean hasNext() {
        return !this.scrollableResults.isClosed() && this.scrollableResults.next();
    }

    @Override
    public T next() {
        Object[] next = this.scrollableResults.get();
        if (next.length == 1) {
            return (T)next[0];
        }
        return (T)next;
    }
}

