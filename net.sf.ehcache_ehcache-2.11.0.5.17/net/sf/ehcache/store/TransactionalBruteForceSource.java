/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.Iterator;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.store.BruteForceSource;
import net.sf.ehcache.store.CopyStrategyHandler;
import net.sf.ehcache.transaction.SoftLockID;

class TransactionalBruteForceSource
implements BruteForceSource {
    private final BruteForceSource delegate;
    private final CopyStrategyHandler copyStrategyHandler;

    TransactionalBruteForceSource(BruteForceSource delegate, CopyStrategyHandler copyStrategyHandler) {
        this.delegate = delegate;
        this.copyStrategyHandler = copyStrategyHandler;
    }

    @Override
    public Iterable<Element> elements() {
        return new TransactionalIterable(this.delegate.elements(), this.copyStrategyHandler);
    }

    @Override
    public Searchable getSearchable() {
        return this.delegate.getSearchable();
    }

    @Override
    public Element transformForIndexing(Element element) {
        return this.copyStrategyHandler.copyElementForReadIfNeeded(element);
    }

    private static class TransactionalIterable
    implements Iterable<Element> {
        private final Iterable<Element> elements;
        private final CopyStrategyHandler copyStrategyHandler;

        public TransactionalIterable(Iterable<Element> elements, CopyStrategyHandler copyStrategyHandler) {
            this.elements = elements;
            this.copyStrategyHandler = copyStrategyHandler;
        }

        @Override
        public Iterator<Element> iterator() {
            return new TransactionalIterator(this.elements.iterator());
        }

        private class TransactionalIterator
        implements Iterator<Element> {
            private final Iterator<Element> delegate;
            private Element next = null;

            public TransactionalIterator(Iterator<Element> delegate) {
                this.delegate = delegate;
                this.next = this.getNextElement();
            }

            private Element getNextElement() {
                while (this.delegate.hasNext()) {
                    Element candidate = this.delegate.next();
                    if (candidate.getObjectValue() instanceof SoftLockID) {
                        candidate = ((SoftLockID)candidate.getObjectValue()).getOldElement();
                    }
                    if (candidate == null) continue;
                    return candidate;
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public Element next() {
                Element result = this.next;
                this.next = this.getNextElement();
                return TransactionalIterable.this.copyStrategyHandler.copyElementForReadIfNeeded(result);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
}

