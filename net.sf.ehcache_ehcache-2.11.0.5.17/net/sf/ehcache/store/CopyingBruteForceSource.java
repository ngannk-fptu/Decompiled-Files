/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.Iterator;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.store.BruteForceSource;
import net.sf.ehcache.store.CopyStrategyHandler;

class CopyingBruteForceSource
implements BruteForceSource {
    private final BruteForceSource delegate;
    private final CopyStrategyHandler copyStrategyHandler;

    CopyingBruteForceSource(BruteForceSource delegate, CopyStrategyHandler copyStrategyHandler) {
        this.delegate = delegate;
        this.copyStrategyHandler = copyStrategyHandler;
    }

    @Override
    public Iterable<Element> elements() {
        return new CopyingIterable(this.delegate.elements(), this.copyStrategyHandler);
    }

    @Override
    public Searchable getSearchable() {
        return this.delegate.getSearchable();
    }

    @Override
    public Element transformForIndexing(Element element) {
        return this.copyStrategyHandler.copyElementForReadIfNeeded(element);
    }

    private static class CopyingIterable
    implements Iterable<Element> {
        private final Iterable<Element> elements;
        private final CopyStrategyHandler copyStrategyHandler;

        public CopyingIterable(Iterable<Element> elements, CopyStrategyHandler copyStrategyHandler) {
            this.elements = elements;
            this.copyStrategyHandler = copyStrategyHandler;
        }

        @Override
        public Iterator<Element> iterator() {
            return new CopyingIterator(this.elements.iterator());
        }

        private class CopyingIterator
        implements Iterator<Element> {
            private final Iterator<Element> delegate;

            public CopyingIterator(Iterator<Element> delegate) {
                this.delegate = delegate;
            }

            @Override
            public boolean hasNext() {
                return this.delegate.hasNext();
            }

            @Override
            public Element next() {
                return CopyingIterable.this.copyStrategyHandler.copyElementForReadIfNeeded(this.delegate.next());
            }

            @Override
            public void remove() {
                this.delegate.remove();
            }
        }
    }
}

