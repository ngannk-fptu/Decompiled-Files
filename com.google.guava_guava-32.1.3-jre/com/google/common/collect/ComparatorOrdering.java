/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Ordering;
import com.google.common.collect.ParametricNullness;
import java.io.Serializable;
import java.util.Comparator;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable=true)
final class ComparatorOrdering<T>
extends Ordering<T>
implements Serializable {
    final Comparator<T> comparator;
    private static final long serialVersionUID = 0L;

    ComparatorOrdering(Comparator<T> comparator) {
        this.comparator = Preconditions.checkNotNull(comparator);
    }

    @Override
    public int compare(@ParametricNullness T a, @ParametricNullness T b) {
        return this.comparator.compare(a, b);
    }

    @Override
    public boolean equals(@CheckForNull Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ComparatorOrdering) {
            ComparatorOrdering that = (ComparatorOrdering)object;
            return this.comparator.equals(that.comparator);
        }
        return false;
    }

    public int hashCode() {
        return this.comparator.hashCode();
    }

    public String toString() {
        return this.comparator.toString();
    }
}

