/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.internal.eviction.EvictableEntryView;
import java.io.Serializable;
import java.util.Comparator;

public abstract class EvictionPolicyComparator<K, V, E extends EvictableEntryView<K, V>>
implements Comparator<E>,
Serializable {
    public static final int FIRST_ENTRY_HAS_HIGHER_PRIORITY_TO_BE_EVICTED = -1;
    public static final int SECOND_ENTRY_HAS_HIGHER_PRIORITY_TO_BE_EVICTED = 1;
    public static final int BOTH_OF_ENTRIES_HAVE_SAME_PRIORITY_TO_BE_EVICTED = 0;

    @Override
    public abstract int compare(E var1, E var2);

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.getClass().equals(obj.getClass());
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}

