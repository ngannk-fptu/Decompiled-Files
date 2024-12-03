/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Comparator;
import org.apache.commons.collections.Bag;

public interface SortedBag
extends Bag {
    public Comparator comparator();

    public Object first();

    public Object last();
}

