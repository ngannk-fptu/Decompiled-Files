/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Iterator;

public interface OrderedIterator
extends Iterator {
    public boolean hasPrevious();

    public Object previous();
}

