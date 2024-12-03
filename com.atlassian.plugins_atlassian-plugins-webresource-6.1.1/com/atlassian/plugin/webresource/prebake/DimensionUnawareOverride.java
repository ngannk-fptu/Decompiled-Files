/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.webresource.prebake;

import com.atlassian.webresource.api.prebake.Dimensions;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public final class DimensionUnawareOverride {
    private static volatile Map<String, DimensionOverride> dimensionUnawareOverrides = Collections.emptyMap();

    public static void setup(Collection<DimensionOverride> overrides) {
        Preconditions.checkNotNull(overrides);
        HashMap<String, DimensionOverride> om = new HashMap<String, DimensionOverride>();
        for (DimensionOverride o : overrides) {
            om.put(o.className, o);
        }
        dimensionUnawareOverrides = Collections.unmodifiableMap(om);
    }

    public static void reset() {
        dimensionUnawareOverrides = Collections.emptyMap();
    }

    public static boolean contains(String className) {
        return dimensionUnawareOverrides.containsKey(className);
    }

    public static String key(String className) {
        return dimensionUnawareOverrides.get(className).getKey();
    }

    public static Dimensions dimensions(String className) {
        return dimensionUnawareOverrides.get(className).getDimensions();
    }

    public static int size() {
        return dimensionUnawareOverrides.size();
    }

    @Deprecated
    public static class DimensionOverride {
        private final String className;
        private final String key;
        private final Dimensions dimensions;

        public DimensionOverride(String className, String key, Collection<String> dimensions) {
            Preconditions.checkNotNull(dimensions);
            this.className = (String)Preconditions.checkNotNull((Object)className);
            this.key = (String)Preconditions.checkNotNull((Object)key);
            this.dimensions = Dimensions.empty().andExactly(key, dimensions);
        }

        public String getClassName() {
            return this.className;
        }

        public String getKey() {
            return this.key;
        }

        public Dimensions getDimensions() {
            return this.dimensions;
        }
    }
}

