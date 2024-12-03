/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.flat;

import java.util.Iterator;
import org.apache.jackrabbit.commons.predicate.Predicate;

public class FilterIterator<T>
extends org.apache.jackrabbit.commons.iterator.FilterIterator<T> {
    public FilterIterator(Iterator<T> tIterator, Predicate predicate) {
        super(tIterator, predicate);
    }
}

