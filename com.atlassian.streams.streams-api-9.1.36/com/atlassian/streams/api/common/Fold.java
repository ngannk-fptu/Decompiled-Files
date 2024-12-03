/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.api.common;

import com.atlassian.streams.api.common.Function2;

public final class Fold {
    public static <A, B> B foldl(Iterable<A> xs, B init, Function2<A, B, B> f) {
        B intermediate = init;
        for (A x : xs) {
            intermediate = f.apply(x, intermediate);
        }
        return intermediate;
    }
}

