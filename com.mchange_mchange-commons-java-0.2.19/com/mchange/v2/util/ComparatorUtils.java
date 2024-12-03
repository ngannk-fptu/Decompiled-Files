/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.util;

import java.util.Comparator;

public final class ComparatorUtils {
    public static Comparator reverse(final Comparator comparator) {
        return new Comparator(){

            public int compare(Object object, Object object2) {
                return -comparator.compare(object, object2);
            }
        };
    }
}

