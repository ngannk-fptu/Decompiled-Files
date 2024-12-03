/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.accessmode;

import com.atlassian.confluence.cache.ThreadLocalCacheAccessor;

public class ThreadLocalReadOnlyAccessCacheInternal {
    private static final ThreadLocalCacheAccessor<Object, Boolean> cacheAccessor = ThreadLocalCacheAccessor.newInstance();

    public static boolean hasReadOnlyAccessExemption() {
        return cacheAccessor.get((Object)ReadOnlyAccessExemption.INSTANCE) == Boolean.TRUE;
    }

    public static void enableReadOnlyAccessExemption() {
        cacheAccessor.put((Object)ReadOnlyAccessExemption.INSTANCE, Boolean.TRUE);
    }

    public static void disableReadOnlyAccessExemption() {
        cacheAccessor.put((Object)ReadOnlyAccessExemption.INSTANCE, null);
    }

    private static enum ReadOnlyAccessExemption {
        INSTANCE;

    }
}

