/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue.deprecated;

import java.util.function.BiFunction;

@Deprecated
public interface Function2<A, B, C>
extends BiFunction<A, B, C> {
    @Override
    @Deprecated
    public C apply(A var1, B var2);
}

