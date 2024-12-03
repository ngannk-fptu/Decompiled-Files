/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.lang;

import com.google.common.base.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class FunctionalInterfaces {
    private FunctionalInterfaces() {
    }

    @Nonnull
    public static <T> Supplier<T> supplierOf(@Nullable T instance) {
        return () -> instance;
    }

    @Nullable
    public static <T> Supplier<T> asSupplier(@Nullable Supplier<T> supplier) {
        return supplier;
    }

    @Nonnull
    public static <T> java.util.function.Predicate<T> not(@Nonnull java.util.function.Predicate<T> original) {
        return original.negate();
    }

    @Nonnull
    public static <T> Predicate<T> toGuavaPredicate(@Nonnull java.util.function.Predicate<T> original) {
        return original::test;
    }
}

