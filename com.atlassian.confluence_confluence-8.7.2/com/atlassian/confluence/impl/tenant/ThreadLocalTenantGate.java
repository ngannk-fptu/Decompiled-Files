/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.BooleanUtils
 */
package com.atlassian.confluence.impl.tenant;

import java.util.concurrent.Callable;
import org.apache.commons.lang3.BooleanUtils;

@Deprecated(forRemoval=true)
public abstract class ThreadLocalTenantGate {
    private static final ThreadLocal<Boolean> permit = new ThreadLocal();

    public static boolean hasTenantPermit() {
        return BooleanUtils.isTrue((Boolean)permit.get());
    }

    public static <T> Callable<T> wrap(boolean permitted, Callable<T> callback) {
        return () -> {
            Boolean outerPermit = permit.get();
            try {
                permit.set(permitted);
                Object v = callback.call();
                return v;
            }
            finally {
                permit.set(outerPermit);
            }
        };
    }

    public static <T> Callable<T> withTenantPermit(Callable<T> callback) {
        return ThreadLocalTenantGate.wrap(true, callback);
    }

    public static <T> Callable<T> withoutTenantPermit(Callable<T> callback) {
        return ThreadLocalTenantGate.wrap(false, callback);
    }
}

