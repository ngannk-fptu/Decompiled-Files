/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.BoundType;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Multiset;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.SortedIterable;
import com.google.common.collect.SortedMultisetBridge;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated=true)
public interface SortedMultiset<E>
extends SortedMultisetBridge<E>,
SortedIterable<E> {
    @Override
    public Comparator<? super E> comparator();

    @CheckForNull
    public Multiset.Entry<E> firstEntry();

    @CheckForNull
    public Multiset.Entry<E> lastEntry();

    @CheckForNull
    public Multiset.Entry<E> pollFirstEntry();

    @CheckForNull
    public Multiset.Entry<E> pollLastEntry();

    @Override
    public NavigableSet<E> elementSet();

    @Override
    public Set<Multiset.Entry<E>> entrySet();

    @Override
    public Iterator<E> iterator();

    public SortedMultiset<E> descendingMultiset();

    public SortedMultiset<E> headMultiset(@ParametricNullness E var1, BoundType var2);

    public SortedMultiset<E> subMultiset(@ParametricNullness E var1, BoundType var2, @ParametricNullness E var3, BoundType var4);

    public SortedMultiset<E> tailMultiset(@ParametricNullness E var1, BoundType var2);
}

