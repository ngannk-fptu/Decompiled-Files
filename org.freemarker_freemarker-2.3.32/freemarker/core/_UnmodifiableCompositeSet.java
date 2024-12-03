/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._UnmodifiableSet;
import java.util.Iterator;
import java.util.Set;

public class _UnmodifiableCompositeSet<E>
extends _UnmodifiableSet<E> {
    private final Set<E> set1;
    private final Set<E> set2;

    public _UnmodifiableCompositeSet(Set<E> set1, Set<E> set2) {
        this.set1 = set1;
        this.set2 = set2;
    }

    @Override
    public Iterator<E> iterator() {
        return new CompositeIterator();
    }

    @Override
    public boolean contains(Object o) {
        return this.set1.contains(o) || this.set2.contains(o);
    }

    @Override
    public int size() {
        return this.set1.size() + this.set2.size();
    }

    private class CompositeIterator
    implements Iterator<E> {
        private Iterator<E> it1;
        private Iterator<E> it2;
        private boolean it1Deplected;

        private CompositeIterator() {
        }

        @Override
        public boolean hasNext() {
            if (!this.it1Deplected) {
                if (this.it1 == null) {
                    this.it1 = _UnmodifiableCompositeSet.this.set1.iterator();
                }
                if (this.it1.hasNext()) {
                    return true;
                }
                this.it2 = _UnmodifiableCompositeSet.this.set2.iterator();
                this.it1 = null;
                this.it1Deplected = true;
            }
            return this.it2.hasNext();
        }

        @Override
        public E next() {
            if (!this.it1Deplected) {
                if (this.it1 == null) {
                    this.it1 = _UnmodifiableCompositeSet.this.set1.iterator();
                }
                if (this.it1.hasNext()) {
                    return this.it1.next();
                }
                this.it2 = _UnmodifiableCompositeSet.this.set2.iterator();
                this.it1 = null;
                this.it1Deplected = true;
            }
            return this.it2.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

