/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ForwardingSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ForwardingNavigableSet<E>
extends ForwardingSortedSet<E>
implements NavigableSet<E> {
    protected ForwardingNavigableSet() {
    }

    @Override
    protected abstract NavigableSet<E> delegate();

    @Override
    @CheckForNull
    public E lower(@ParametricNullness E e) {
        return this.delegate().lower(e);
    }

    @CheckForNull
    protected E standardLower(@ParametricNullness E e) {
        return Iterators.getNext(this.headSet(e, false).descendingIterator(), null);
    }

    @Override
    @CheckForNull
    public E floor(@ParametricNullness E e) {
        return this.delegate().floor(e);
    }

    @CheckForNull
    protected E standardFloor(@ParametricNullness E e) {
        return Iterators.getNext(this.headSet(e, true).descendingIterator(), null);
    }

    @Override
    @CheckForNull
    public E ceiling(@ParametricNullness E e) {
        return this.delegate().ceiling(e);
    }

    @CheckForNull
    protected E standardCeiling(@ParametricNullness E e) {
        return Iterators.getNext(this.tailSet(e, true).iterator(), null);
    }

    @Override
    @CheckForNull
    public E higher(@ParametricNullness E e) {
        return this.delegate().higher(e);
    }

    @CheckForNull
    protected E standardHigher(@ParametricNullness E e) {
        return Iterators.getNext(this.tailSet(e, false).iterator(), null);
    }

    @Override
    @CheckForNull
    public E pollFirst() {
        return this.delegate().pollFirst();
    }

    @CheckForNull
    protected E standardPollFirst() {
        return Iterators.pollNext(this.iterator());
    }

    @Override
    @CheckForNull
    public E pollLast() {
        return this.delegate().pollLast();
    }

    @CheckForNull
    protected E standardPollLast() {
        return Iterators.pollNext(this.descendingIterator());
    }

    @ParametricNullness
    protected E standardFirst() {
        return this.iterator().next();
    }

    @ParametricNullness
    protected E standardLast() {
        return this.descendingIterator().next();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return this.delegate().descendingSet();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return this.delegate().descendingIterator();
    }

    @Override
    public NavigableSet<E> subSet(@ParametricNullness E fromElement, boolean fromInclusive, @ParametricNullness E toElement, boolean toInclusive) {
        return this.delegate().subSet(fromElement, fromInclusive, toElement, toInclusive);
    }

    protected NavigableSet<E> standardSubSet(@ParametricNullness E fromElement, boolean fromInclusive, @ParametricNullness E toElement, boolean toInclusive) {
        return this.tailSet(fromElement, fromInclusive).headSet(toElement, toInclusive);
    }

    @Override
    protected SortedSet<E> standardSubSet(@ParametricNullness E fromElement, @ParametricNullness E toElement) {
        return this.subSet(fromElement, true, toElement, false);
    }

    @Override
    public NavigableSet<E> headSet(@ParametricNullness E toElement, boolean inclusive) {
        return this.delegate().headSet(toElement, inclusive);
    }

    protected SortedSet<E> standardHeadSet(@ParametricNullness E toElement) {
        return this.headSet(toElement, false);
    }

    @Override
    public NavigableSet<E> tailSet(@ParametricNullness E fromElement, boolean inclusive) {
        return this.delegate().tailSet(fromElement, inclusive);
    }

    protected SortedSet<E> standardTailSet(@ParametricNullness E fromElement) {
        return this.tailSet(fromElement, true);
    }

    protected class StandardDescendingSet
    extends Sets.DescendingSet<E> {
        public StandardDescendingSet(ForwardingNavigableSet this$0) {
            super(this$0);
        }
    }
}

