/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Ordering;
import com.google.common.collect.ReverseNaturalOrdering;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable=true)
final class NaturalOrdering
extends Ordering<Comparable<?>>
implements Serializable {
    static final NaturalOrdering INSTANCE = new NaturalOrdering();
    @LazyInit
    @CheckForNull
    private transient Ordering<@Nullable Comparable<?>> nullsFirst;
    @LazyInit
    @CheckForNull
    private transient Ordering<@Nullable Comparable<?>> nullsLast;
    private static final long serialVersionUID = 0L;

    @Override
    public int compare(Comparable<?> left, Comparable<?> right) {
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
        return left.compareTo(right);
    }

    @Override
    public <S extends Comparable<?>> Ordering<@Nullable S> nullsFirst() {
        Ordering<@Nullable Comparable<Object>> result = this.nullsFirst;
        if (result == null) {
            result = this.nullsFirst = super.nullsFirst();
        }
        return result;
    }

    @Override
    public <S extends Comparable<?>> Ordering<@Nullable S> nullsLast() {
        Ordering<@Nullable Comparable<Object>> result = this.nullsLast;
        if (result == null) {
            result = this.nullsLast = super.nullsLast();
        }
        return result;
    }

    @Override
    public <S extends Comparable<?>> Ordering<S> reverse() {
        return ReverseNaturalOrdering.INSTANCE;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public String toString() {
        return "Ordering.natural()";
    }

    private NaturalOrdering() {
    }
}

