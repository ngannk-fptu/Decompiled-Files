/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.bag.HashBag;
import org.apache.commons.collections.bag.PredicatedBag;
import org.apache.commons.collections.bag.PredicatedSortedBag;
import org.apache.commons.collections.bag.SynchronizedBag;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.commons.collections.bag.TransformedBag;
import org.apache.commons.collections.bag.TransformedSortedBag;
import org.apache.commons.collections.bag.TreeBag;
import org.apache.commons.collections.bag.TypedBag;
import org.apache.commons.collections.bag.TypedSortedBag;
import org.apache.commons.collections.bag.UnmodifiableBag;
import org.apache.commons.collections.bag.UnmodifiableSortedBag;

public class BagUtils {
    public static final Bag EMPTY_BAG = UnmodifiableBag.decorate(new HashBag());
    public static final Bag EMPTY_SORTED_BAG = UnmodifiableSortedBag.decorate(new TreeBag());

    public static Bag synchronizedBag(Bag bag) {
        return SynchronizedBag.decorate(bag);
    }

    public static Bag unmodifiableBag(Bag bag) {
        return UnmodifiableBag.decorate(bag);
    }

    public static Bag predicatedBag(Bag bag, Predicate predicate) {
        return PredicatedBag.decorate(bag, predicate);
    }

    public static Bag typedBag(Bag bag, Class type) {
        return TypedBag.decorate(bag, type);
    }

    public static Bag transformedBag(Bag bag, Transformer transformer) {
        return TransformedBag.decorate(bag, transformer);
    }

    public static SortedBag synchronizedSortedBag(SortedBag bag) {
        return SynchronizedSortedBag.decorate(bag);
    }

    public static SortedBag unmodifiableSortedBag(SortedBag bag) {
        return UnmodifiableSortedBag.decorate(bag);
    }

    public static SortedBag predicatedSortedBag(SortedBag bag, Predicate predicate) {
        return PredicatedSortedBag.decorate(bag, predicate);
    }

    public static SortedBag typedSortedBag(SortedBag bag, Class type) {
        return TypedSortedBag.decorate(bag, type);
    }

    public static SortedBag transformedSortedBag(SortedBag bag, Transformer transformer) {
        return TransformedSortedBag.decorate(bag, transformer);
    }
}

