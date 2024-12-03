/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.util.SortedMap;
import org.apache.commons.collections.functors.InstanceofPredicate;
import org.apache.commons.collections.map.PredicatedSortedMap;

public class TypedSortedMap {
    public static SortedMap decorate(SortedMap map, Class keyType, Class valueType) {
        return new PredicatedSortedMap(map, InstanceofPredicate.getInstance(keyType), InstanceofPredicate.getInstance(valueType));
    }

    protected TypedSortedMap() {
    }
}

