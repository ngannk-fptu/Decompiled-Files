/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.iterators.AbstractEmptyIterator;

public class EmptyIterator
extends AbstractEmptyIterator
implements ResettableIterator {
    public static final ResettableIterator RESETTABLE_INSTANCE = new EmptyIterator();
    public static final Iterator INSTANCE = RESETTABLE_INSTANCE;

    protected EmptyIterator() {
    }
}

