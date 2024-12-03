/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.bag.PredicatedSortedBag;
import org.apache.commons.collections.functors.InstanceofPredicate;

public class TypedSortedBag {
    public static SortedBag decorate(SortedBag bag, Class type) {
        return new PredicatedSortedBag(bag, InstanceofPredicate.getInstance(type));
    }

    protected TypedSortedBag() {
    }
}

