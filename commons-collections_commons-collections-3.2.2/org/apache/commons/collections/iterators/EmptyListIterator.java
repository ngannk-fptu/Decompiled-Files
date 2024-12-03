/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.ListIterator;
import org.apache.commons.collections.ResettableListIterator;
import org.apache.commons.collections.iterators.AbstractEmptyIterator;

public class EmptyListIterator
extends AbstractEmptyIterator
implements ResettableListIterator {
    public static final ResettableListIterator RESETTABLE_INSTANCE = new EmptyListIterator();
    public static final ListIterator INSTANCE = RESETTABLE_INSTANCE;

    protected EmptyListIterator() {
    }
}

