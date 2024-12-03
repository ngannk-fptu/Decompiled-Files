/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import java.util.Comparator;

public interface KeyComparator<K>
extends Comparator<K> {
    public boolean equals(K var1, K var2);

    public int hash(K var1);
}

