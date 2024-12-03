/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Collection;

public interface BoundedCollection
extends Collection {
    public boolean isFull();

    public int maxSize();
}

