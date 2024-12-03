/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.internal;

public final class AliasConstantsHelper {
    private static final int MAX_POOL_SIZE = 40;
    private static final String[] pool = AliasConstantsHelper.initPool(40);

    public static String get(int i) {
        if (i < 40 && i >= 0) {
            return pool[i];
        }
        return AliasConstantsHelper.internalAlias(i);
    }

    private static String[] initPool(int maxPoolSize) {
        String[] pool = new String[maxPoolSize];
        for (int i = 0; i < maxPoolSize; ++i) {
            pool[i] = AliasConstantsHelper.internalAlias(i);
        }
        return pool;
    }

    private static String internalAlias(int i) {
        return Integer.toString(i) + '_';
    }
}

