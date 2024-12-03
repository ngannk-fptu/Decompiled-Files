/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ParametricNullness;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSortedMap<K, V>
extends ForwardingMap<K, V>
implements SortedMap<K, V> {
    protected ForwardingSortedMap() {
    }

    @Override
    protected abstract SortedMap<K, V> delegate();

    @Override
    @CheckForNull
    public Comparator<? super K> comparator() {
        return this.delegate().comparator();
    }

    @Override
    @ParametricNullness
    public K firstKey() {
        return this.delegate().firstKey();
    }

    @Override
    public SortedMap<K, V> headMap(@ParametricNullness K toKey) {
        return this.delegate().headMap(toKey);
    }

    @Override
    @ParametricNullness
    public K lastKey() {
        return this.delegate().lastKey();
    }

    @Override
    public SortedMap<K, V> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
        return this.delegate().subMap(fromKey, toKey);
    }

    @Override
    public SortedMap<K, V> tailMap(@ParametricNullness K fromKey) {
        return this.delegate().tailMap(fromKey);
    }

    static int unsafeCompare(@CheckForNull Comparator<?> comparator, @CheckForNull Object o1, @CheckForNull Object o2) {
        if (comparator == null) {
            return ((Comparable)o1).compareTo(o2);
        }
        return comparator.compare(o1, o2);
    }

    /*
     * Issues handling annotations - annotations may be inaccurate
     */
    @Override
    protected boolean standardContainsKey(@CheckForNull Object key) {
        try {
            @Nullable ForwardingSortedMap self = this;
            Object ceilingKey = self.tailMap(key).firstKey();
            return ForwardingSortedMap.unsafeCompare(this.comparator(), ceilingKey, key) == 0;
        }
        catch (ClassCastException | NullPointerException | NoSuchElementException e) {
            return false;
        }
    }

    protected SortedMap<K, V> standardSubMap(K fromKey, K toKey) {
        Preconditions.checkArgument(ForwardingSortedMap.unsafeCompare(this.comparator(), fromKey, toKey) <= 0, "fromKey must be <= toKey");
        return this.tailMap(fromKey).headMap(toKey);
    }

    protected class StandardKeySet
    extends Maps.SortedKeySet<K, V> {
        public StandardKeySet(ForwardingSortedMap this$0) {
            super(this$0);
        }
    }
}

