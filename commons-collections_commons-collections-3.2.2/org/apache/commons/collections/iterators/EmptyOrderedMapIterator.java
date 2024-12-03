/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.iterators.AbstractEmptyIterator;

public class EmptyOrderedMapIterator
extends AbstractEmptyIterator
implements OrderedMapIterator,
ResettableIterator {
    public static final OrderedMapIterator INSTANCE = new EmptyOrderedMapIterator();

    protected EmptyOrderedMapIterator() {
    }
}

