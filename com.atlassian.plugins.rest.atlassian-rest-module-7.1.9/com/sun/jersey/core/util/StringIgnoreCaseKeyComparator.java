/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import com.sun.jersey.core.util.KeyComparator;

public class StringIgnoreCaseKeyComparator
implements KeyComparator<String> {
    public static final StringIgnoreCaseKeyComparator SINGLETON = new StringIgnoreCaseKeyComparator();

    @Override
    public int hash(String k) {
        return k.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(String x, String y) {
        return x.equalsIgnoreCase(y);
    }

    @Override
    public int compare(String o1, String o2) {
        return o1.compareToIgnoreCase(o2);
    }
}

