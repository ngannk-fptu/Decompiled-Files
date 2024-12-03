/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.webresource.api.prebake;

import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionsImpl;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public interface Dimensions {
    public static Dimensions empty() {
        return DimensionsImpl.empty();
    }

    @Nonnull
    public static Dimensions fromMap(@Nonnull Map<String, List<String>> dimensionMap) {
        Preconditions.checkNotNull(dimensionMap);
        Dimensions dims = Dimensions.empty();
        for (Map.Entry<String, List<String>> e : dimensionMap.entrySet()) {
            String key = e.getKey();
            List<String> values = e.getValue();
            if (values == null || values.isEmpty()) continue;
            dims = dims.andExactly(key, values);
        }
        return dims;
    }

    public Dimensions andExactly(String var1, String ... var2);

    public Dimensions andExactly(String var1, Collection<String> var2);

    public Dimensions andAbsent(String var1);

    public Dimensions product(Dimensions var1);

    @Nonnull
    public Dimensions whitelistValues(@Nonnull Dimensions var1);

    @Nonnull
    public Dimensions blacklistValues(@Nonnull Dimensions var1);

    public Stream<Coordinate> cartesianProduct();

    public long cartesianProductSize();
}

