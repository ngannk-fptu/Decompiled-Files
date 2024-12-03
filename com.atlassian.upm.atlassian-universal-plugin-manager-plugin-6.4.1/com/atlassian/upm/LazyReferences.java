/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.LazyReference$InitializationException
 *  io.atlassian.util.concurrent.ResettableLazyReference
 */
package com.atlassian.upm;

import io.atlassian.util.concurrent.LazyReference;
import io.atlassian.util.concurrent.ResettableLazyReference;

public interface LazyReferences {
    public static <T> T safeGet(ResettableLazyReference<T> lr) {
        try {
            return (T)lr.get();
        }
        catch (LazyReference.InitializationException ie) {
            lr.reset();
            throw ie;
        }
    }
}

