/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.iterators.AbstractEmptyIterator;

public class EmptyMapIterator
extends AbstractEmptyIterator
implements MapIterator,
ResettableIterator {
    public static final MapIterator INSTANCE = new EmptyMapIterator();

    protected EmptyMapIterator() {
    }
}

