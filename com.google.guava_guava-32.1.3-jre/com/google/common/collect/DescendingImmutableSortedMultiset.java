/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.BoundType;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multiset;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class DescendingImmutableSortedMultiset<E>
extends ImmutableSortedMultiset<E> {
    private final transient ImmutableSortedMultiset<E> forward;

    DescendingImmutableSortedMultiset(ImmutableSortedMultiset<E> forward) {
        this.forward = forward;
    }

    @Override
    public int count(@CheckForNull Object element) {
        return this.forward.count(element);
    }

    @Override
    @CheckForNull
    public Multiset.Entry<E> firstEntry() {
        return this.forward.lastEntry();
    }

    @Override
    @CheckForNull
    public Multiset.Entry<E> lastEntry() {
        return this.forward.firstEntry();
    }

    @Override
    public int size() {
        return this.forward.size();
    }

    @Override
    public ImmutableSortedSet<E> elementSet() {
        return ((ImmutableSortedSet)this.forward.elementSet()).descendingSet();
    }

    @Override
    Multiset.Entry<E> getEntry(int index) {
        return (Multiset.Entry)((ImmutableCollection)((Object)this.forward.entrySet())).asList().reverse().get(index);
    }

    @Override
    public ImmutableSortedMultiset<E> descendingMultiset() {
        return this.forward;
    }

    @Override
    public ImmutableSortedMultiset<E> headMultiset(E upperBound, BoundType boundType) {
        return ((ImmutableSortedMultiset)this.forward.tailMultiset((Object)upperBound, boundType)).descendingMultiset();
    }

    @Override
    public ImmutableSortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType) {
        return ((ImmutableSortedMultiset)this.forward.headMultiset((Object)lowerBound, boundType)).descendingMultiset();
    }

    @Override
    boolean isPartialView() {
        return this.forward.isPartialView();
    }
}

