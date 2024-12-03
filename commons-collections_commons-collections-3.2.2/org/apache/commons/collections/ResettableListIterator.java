/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.ListIterator;
import org.apache.commons.collections.ResettableIterator;

public interface ResettableListIterator
extends ListIterator,
ResettableIterator {
    public void reset();
}

