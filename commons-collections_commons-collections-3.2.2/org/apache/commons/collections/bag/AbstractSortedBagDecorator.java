/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.util.Comparator;
import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.bag.AbstractBagDecorator;

public abstract class AbstractSortedBagDecorator
extends AbstractBagDecorator
implements SortedBag {
    protected AbstractSortedBagDecorator() {
    }

    protected AbstractSortedBagDecorator(SortedBag bag) {
        super(bag);
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

