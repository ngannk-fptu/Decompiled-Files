/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.List;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable=true)
final class ExplicitOrdering<T>
extends Ordering<T>
implements Serializable {
    final ImmutableMap<T, Integer> rankMap;
    private static final long serialVersionUID = 0L;

    ExplicitOrdering(List<T> valuesInOrder) {
        this(Maps.indexMap(valuesInOrder));
    }

    ExplicitOrdering(ImmutableMap<T, Integer> rankMap) {
        this.rankMap = rankMap;
    }

    @Override
    public int compare(T left, T right) {
        return this.rank(left) - this.rank(right);
    }

    private int rank(T value) {
        Integer rank = this.rankMap.get(value);
        if (rank == null) {
            throw new Ordering.IncomparableValueException(value);
        }
        return rank;
    }

    @Override
    public boolean equals(@CheckForNull Object object) {
        if (object instanceof ExplicitOrdering) {
            ExplicitOrdering that = (ExplicitOrdering)object;
            return this.rankMap.equals(that.rankMap);
        }
        return false;
    }

    public int hashCode() {
        return this.rankMap.hashCode();
    }

    public String toString() {
        return "Ordering.explicit(" + this.rankMap.keySet() + ")";
    }
}

