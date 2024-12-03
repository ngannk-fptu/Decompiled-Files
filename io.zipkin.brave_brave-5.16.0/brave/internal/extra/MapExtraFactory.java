/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.extra;

import brave.internal.extra.ExtraFactory;
import brave.internal.extra.MapExtra;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class MapExtraFactory<K, V, A extends MapExtra<K, V, A, F>, F extends MapExtraFactory<K, V, A, F>>
extends ExtraFactory<A, F> {
    public static final int MAX_DYNAMIC_ENTRIES = 64;
    final Map<K, Integer> initialFieldIndices;
    final int initialArrayLength;
    final int maxDynamicEntries;

    protected MapExtraFactory(Builder<K, V, A, F, ?> builder) {
        super(builder.initialState.toArray());
        LinkedHashMap<Object, Integer> initialFieldIndices = new LinkedHashMap<Object, Integer>();
        Object[] initialStateArray = (Object[])this.initialState;
        this.initialArrayLength = initialStateArray.length;
        for (int i = 0; i < this.initialArrayLength; i += 2) {
            initialFieldIndices.put(initialStateArray[i], i);
        }
        this.initialFieldIndices = Collections.unmodifiableMap(initialFieldIndices);
        this.maxDynamicEntries = builder.maxDynamicEntries;
    }

    @Override
    protected abstract A create();

    public static abstract class Builder<K, V, A extends MapExtra<K, V, A, F>, F extends MapExtraFactory<K, V, A, F>, B extends Builder<K, V, A, F, B>> {
        List<Object> initialState = new ArrayList<Object>();
        int maxDynamicEntries;

        public final B addInitialKey(K key) {
            if (key == null) {
                throw new NullPointerException("key == null");
            }
            this.initialState.add(key);
            this.initialState.add(null);
            return (B)this;
        }

        public final B maxDynamicEntries(int maxDynamicEntries) {
            if (maxDynamicEntries < 0) {
                throw new IllegalArgumentException("maxDynamicEntries < 0");
            }
            if (maxDynamicEntries > 64) {
                throw new IllegalArgumentException("maxDynamicEntries > 64");
            }
            this.maxDynamicEntries = maxDynamicEntries;
            return (B)this;
        }

        protected abstract F build();
    }
}

