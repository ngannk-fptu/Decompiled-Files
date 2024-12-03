/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractMultiset<E>
extends AbstractCollection<E>
implements Multiset<E> {
    @LazyInit
    @CheckForNull
    private transient Set<E> elementSet;
    @LazyInit
    @CheckForNull
    private transient Set<Multiset.Entry<E>> entrySet;

    AbstractMultiset() {
    }

    @Override
    public boolean isEmpty() {
        return this.entrySet().isEmpty();
    }

    @Override
    public boolean contains(@CheckForNull Object element) {
        return this.count(element) > 0;
    }

    @Override
    @CanIgnoreReturnValue
    public final boolean add(@ParametricNullness E element) {
        this.add(element, 1);
        return true;
    }

    @Override
    @CanIgnoreReturnValue
    public int add(@ParametricNullness E element, int occurrences) {
        throw new UnsupportedOperationException();
    }

    @Override
    @CanIgnoreReturnValue
    public final boolean remove(@CheckForNull Object element) {
        return this.remove(element, 1) > 0;
    }

    @Override
    @CanIgnoreReturnValue
    public int remove(@CheckForNull Object element, int occurrences) {
        throw new UnsupportedOperationException();
    }

    @Override
    @CanIgnoreReturnValue
    public int setCount(@ParametricNullness E element, int count) {
        return Multisets.setCountImpl(this, element, count);
    }

    @Override
    @CanIgnoreReturnValue
    public boolean setCount(@ParametricNullness E element, int oldCount, int newCount) {
        return Multisets.setCountImpl(this, element, oldCount, newCount);
    }

    @Override
    @CanIgnoreReturnValue
    public final boolean addAll(Collection<? extends E> elementsToAdd) {
        return Multisets.addAllImpl(this, elementsToAdd);
    }

    @Override
    @CanIgnoreReturnValue
    public final boolean removeAll(Collection<?> elementsToRemove) {
        return Multisets.removeAllImpl(this, elementsToRemove);
    }

    @Override
    @CanIgnoreReturnValue
    public final boolean retainAll(Collection<?> elementsToRetain) {
        return Multisets.retainAllImpl(this, elementsToRetain);
    }

    @Override
    public abstract void clear();

    @Override
    public Set<E> elementSet() {
        Set<E> result = this.elementSet;
        if (result == null) {
            this.elementSet = result = this.createElementSet();
        }
        return result;
    }

    Set<E> createElementSet() {
        return new ElementSet();
    }

    abstract Iterator<E> elementIterator();

    @Override
    public Set<Multiset.Entry<E>> entrySet() {
        Set<Multiset.Entry<Multiset.Entry<E>>> result = this.entrySet;
        if (result == null) {
            this.entrySet = result = this.createEntrySet();
        }
        return result;
    }

    Set<Multiset.Entry<E>> createEntrySet() {
        return new EntrySet();
    }

    abstract Iterator<Multiset.Entry<E>> entryIterator();

    abstract int distinctElements();

    @Override
    public final boolean equals(@CheckForNull Object object) {
        return Multisets.equalsImpl(this, object);
    }

    @Override
    public final int hashCode() {
        return this.entrySet().hashCode();
    }

    @Override
    public final String toString() {
        return this.entrySet().toString();
    }

    class EntrySet
    extends Multisets.EntrySet<E> {
        EntrySet() {
        }

        @Override
        Multiset<E> multiset() {
            return AbstractMultiset.this;
        }

        @Override
        public Iterator<Multiset.Entry<E>> iterator() {
            return AbstractMultiset.this.entryIterator();
        }

        @Override
        public int size() {
            return AbstractMultiset.this.distinctElements();
        }
    }

    class ElementSet
    extends Multisets.ElementSet<E> {
        ElementSet() {
        }

        @Override
        Multiset<E> multiset() {
            return AbstractMultiset.this;
        }

        @Override
        public Iterator<E> iterator() {
            return AbstractMultiset.this.elementIterator();
        }
    }
}

