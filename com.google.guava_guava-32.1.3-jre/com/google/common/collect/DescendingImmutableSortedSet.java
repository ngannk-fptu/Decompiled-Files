/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.UnmodifiableIterator;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class DescendingImmutableSortedSet<E>
extends ImmutableSortedSet<E> {
    private final ImmutableSortedSet<E> forward;

    DescendingImmutableSortedSet(ImmutableSortedSet<E> forward) {
        super(Ordering.from(forward.comparator()).reverse());
        this.forward = forward;
    }

    @Override
    public boolean contains(@CheckForNull Object object) {
        return this.forward.contains(object);
    }

    @Override
    public int size() {
        return this.forward.size();
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.forward.descendingIterator();
    }

    @Override
    ImmutableSortedSet<E> headSetImpl(E toElement, boolean inclusive) {
        return ((ImmutableSortedSet)this.forward.tailSet((Object)toElement, inclusive)).descendingSet();
    }

    @Override
    ImmutableSortedSet<E> subSetImpl(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return ((ImmutableSortedSet)this.forward.subSet((Object)toElement, toInclusive, (Object)fromElement, fromInclusive)).descendingSet();
    }

    @Override
    ImmutableSortedSet<E> tailSetImpl(E fromElement, boolean inclusive) {
        return ((ImmutableSortedSet)this.forward.headSet((Object)fromElement, inclusive)).descendingSet();
    }

    @Override
    @GwtIncompatible(value="NavigableSet")
    public ImmutableSortedSet<E> descendingSet() {
        return this.forward;
    }

    @Override
    @GwtIncompatible(value="NavigableSet")
    public UnmodifiableIterator<E> descendingIterator() {
        return this.forward.iterator();
    }

    @Override
    @GwtIncompatible(value="NavigableSet")
    ImmutableSortedSet<E> createDescendingSet() {
        throw new AssertionError((Object)"should never be called");
    }

    @Override
    @CheckForNull
    public E lower(E element) {
        return this.forward.higher(element);
    }

    @Override
    @CheckForNull
    public E floor(E element) {
        return this.forward.ceiling(element);
    }

    @Override
    @CheckForNull
    public E ceiling(E element) {
        return this.forward.floor(element);
    }

    @Override
    @CheckForNull
    public E higher(E element) {
        return this.forward.lower(element);
    }

    @Override
    int indexOf(@CheckForNull Object target) {
        int index = this.forward.indexOf(target);
        if (index == -1) {
            return index;
        }
        return this.size() - 1 - index;
    }

    @Override
    boolean isPartialView() {
        return this.forward.isPartialView();
    }
}

