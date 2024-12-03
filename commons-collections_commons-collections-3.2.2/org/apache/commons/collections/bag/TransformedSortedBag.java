/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.util.Comparator;
import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.bag.TransformedBag;

public class TransformedSortedBag
extends TransformedBag
implements SortedBag {
    private static final long serialVersionUID = -251737742649401930L;

    public static SortedBag decorate(SortedBag bag, Transformer transformer) {
        return new TransformedSortedBag(bag, transformer);
    }

    protected TransformedSortedBag(SortedBag bag, Transformer transformer) {
        super(bag, transformer);
    }

    protected SortedBag getSortedBag() {
        return (SortedBag)this.collection;
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

