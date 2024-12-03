/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.hazelcast.internal.dynamicconfig.search;

import com.hazelcast.internal.dynamicconfig.search.ConfigSupplier;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import javax.annotation.Nonnull;

public interface Searcher<T extends IdentifiedDataSerializable> {
    public T getConfig(@Nonnull String var1, String var2, @Nonnull ConfigSupplier<T> var3);
}

