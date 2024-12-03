/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.ElementTypesAreNonnullByDefault;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class FunctionalEquivalence<F, T>
extends Equivalence<F>
implements Serializable {
    private static final long serialVersionUID = 0L;
    private final Function<? super F, ? extends @Nullable T> function;
    private final Equivalence<T> resultEquivalence;

    FunctionalEquivalence(Function<? super F, ? extends @Nullable T> function, Equivalence<T> resultEquivalence) {
        this.function = Preconditions.checkNotNull(function);
        this.resultEquivalence = Preconditions.checkNotNull(resultEquivalence);
    }

    @Override
    protected boolean doEquivalent(F a, F b) {
        return this.resultEquivalence.equivalent(this.function.apply(a), this.function.apply(b));
    }

    @Override
    protected int doHash(F a) {
        return this.resultEquivalence.hash(this.function.apply(a));
    }

    public boolean equals(@CheckForNull Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FunctionalEquivalence) {
            FunctionalEquivalence that = (FunctionalEquivalence)obj;
            return this.function.equals(that.function) && this.resultEquivalence.equals(that.resultEquivalence);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode(this.function, this.resultEquivalence);
    }

    public String toString() {
        return this.resultEquivalence + ".onResultOf(" + this.function + ")";
    }
}

