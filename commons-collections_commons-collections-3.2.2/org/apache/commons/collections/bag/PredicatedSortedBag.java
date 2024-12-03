/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.util.Comparator;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.bag.PredicatedBag;

public class PredicatedSortedBag
extends PredicatedBag
implements SortedBag {
    private static final long serialVersionUID = 3448581314086406616L;

    public static SortedBag decorate(SortedBag bag, Predicate predicate) {
        return new PredicatedSortedBag(bag, predicate);
    }

    protected PredicatedSortedBag(SortedBag bag, Predicate predicate) {
        super(bag, predicate);
    }

    protected SortedBag getSortedBag() {
        return (SortedBag)this.getCollection();
    }

    public Object first() {
        return this.getSortedBag().first();
    }

    public Object last() {
        return this.getSortedBag().last();
    }

    public Comparator comparator() {
        return this.getSortedBag().comparator();
    }
}

