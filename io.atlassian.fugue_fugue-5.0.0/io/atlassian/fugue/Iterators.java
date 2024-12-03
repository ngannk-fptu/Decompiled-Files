/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

class Iterators {
    Iterators() {
    }

    static <A> boolean addAll(Collection<A> collectionToModify, Iterator<? extends A> iterator) {
        Objects.requireNonNull(collectionToModify);
        Objects.requireNonNull(iterator);
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= collectionToModify.add(iterator.next());
        }
        return wasModified;
    }

    static <A> Peeking<A> peekingIterator(Iterator<? extends A> iterator) {
        if (iterator instanceof PeekingImpl) {
            PeekingImpl peeking = (PeekingImpl)iterator;
            return peeking;
        }
        return new PeekingImpl<A>(iterator);
    }

    static <A> Iterator<A> singletonIterator(final A a) {
        return new Iterator<A>(){
            boolean done = false;

            @Override
            public boolean hasNext() {
                return !this.done;
            }

            @Override
            public A next() {
                if (this.done) {
                    throw new UnsupportedOperationException("Attempted to call next on empty iterator");
                }
                this.done = true;
                return a;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot call remove on this iterator");
            }
        };
    }

    public static <A> Iterator<A> emptyIterator() {
        return EmptyIterator.INSTANCE;
    }

    static abstract class Unmodifiable<E>
    implements Iterator<E> {
        protected Unmodifiable() {
        }

        @Override
        @Deprecated
        public final void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static abstract class Abstract<A>
    extends Unmodifiable<A> {
        private State state = State.NotReady;
        private A next;

        protected Abstract() {
        }

        protected abstract A computeNext();

        protected final A endOfData() {
            this.state = State.Complete;
            return null;
        }

        @Override
        public final boolean hasNext() {
            switch (this.state) {
                case Failed: {
                    throw new IllegalStateException("Failed iterator");
                }
                case Ready: {
                    return true;
                }
                case Complete: {
                    return false;
                }
            }
            return this.tryToComputeNext();
        }

        private boolean tryToComputeNext() {
            try {
                this.next = this.computeNext();
                if (this.state != State.Complete) {
                    this.state = State.Ready;
                    return true;
                }
                return false;
            }
            catch (Error | RuntimeException e) {
                this.state = State.Failed;
                throw e;
            }
        }

        @Override
        public final A next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            try {
                A a = this.next;
                return a;
            }
            finally {
                this.next = null;
                this.state = State.NotReady;
            }
        }

        private static enum State {
            Ready,
            NotReady,
            Complete,
            Failed;

        }
    }

    static interface Peeking<A>
    extends Peek<A>,
    Iterator<A> {
    }

    static interface Peek<A> {
        public A peek();
    }

    private static enum EmptyIterator implements Iterator<Object>
    {
        INSTANCE;


        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException("Attempted to call next on empty iterator");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot call remove on this iterator");
        }
    }

    private static class PeekingImpl<A>
    implements Peeking<A> {
        private final Iterator<? extends A> iterator;
        private boolean hasPeeked;
        private A peekedElement;

        public PeekingImpl(Iterator<? extends A> iterator) {
            this.iterator = Objects.requireNonNull(iterator);
        }

        @Override
        public boolean hasNext() {
            return this.hasPeeked || this.iterator.hasNext();
        }

        @Override
        public A next() {
            if (!this.hasPeeked) {
                return this.iterator.next();
            }
            A result = this.peekedElement;
            this.hasPeeked = false;
            this.peekedElement = null;
            return result;
        }

        @Override
        public void remove() {
            if (this.hasPeeked) {
                throw new IllegalStateException("Cannot remove an element after peeking");
            }
            this.iterator.remove();
        }

        @Override
        public A peek() {
            if (!this.hasPeeked) {
                this.peekedElement = this.iterator.next();
                this.hasPeeked = true;
            }
            return this.peekedElement;
        }
    }
}

