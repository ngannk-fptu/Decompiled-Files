/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.ElementTypesAreNonnullByDefault;
import com.google.common.base.ParametricNullness;
import javax.annotation.CheckForNull;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Predicate<T>
extends java.util.function.Predicate<T> {
    public boolean apply(@ParametricNullness T var1);

    public boolean equals(@CheckForNull Object var1);

    @Override
    default public boolean test(@ParametricNullness T input) {
        return this.apply(input);
    }
}

