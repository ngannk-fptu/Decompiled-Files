/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.PredicatedBag;
import org.apache.commons.collections.functors.InstanceofPredicate;

public class TypedBag {
    public static Bag decorate(Bag bag, Class type) {
        return new PredicatedBag(bag, InstanceofPredicate.getInstance(type));
    }

    protected TypedBag() {
    }
}

