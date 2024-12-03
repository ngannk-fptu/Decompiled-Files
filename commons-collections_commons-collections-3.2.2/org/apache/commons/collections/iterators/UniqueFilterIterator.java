/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.collections.iterators.FilterIterator;

public class UniqueFilterIterator
extends FilterIterator {
    public UniqueFilterIterator(Iterator iterator) {
        super(iterator, UniquePredicate.getInstance());
    }
}

