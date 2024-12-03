/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Iterator;

public interface SizedIterator<T>
extends Iterator<T> {
    public long getSize();
}

