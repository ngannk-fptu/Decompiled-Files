/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.projection;

import com.hazelcast.projection.Projection;
import com.hazelcast.projection.impl.IdentityProjection;
import com.hazelcast.projection.impl.MultiAttributeProjection;
import com.hazelcast.projection.impl.SingleAttributeProjection;

public final class Projections {
    private Projections() {
    }

    public static <T> Projection<T, T> identity() {
        return IdentityProjection.INSTANCE;
    }

    public static <I, O> Projection<I, O> singleAttribute(String attributePath) {
        return new SingleAttributeProjection(attributePath);
    }

    public static <I> Projection<I, Object[]> multiAttribute(String ... attributePaths) {
        return new MultiAttributeProjection(attributePaths);
    }
}

