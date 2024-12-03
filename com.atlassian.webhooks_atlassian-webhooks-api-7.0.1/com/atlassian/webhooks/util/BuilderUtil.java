/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks.util;

import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderUtil {
    @SafeVarargs
    public static <T> void addIf(@Nonnull Predicate<? super T> predicate, @Nonnull Collection<T> collection, T element, T ... moreElements) {
        BuilderUtil.addIfMatches(element, predicate, collection);
        if (moreElements != null) {
            for (T elem : moreElements) {
                BuilderUtil.addIfMatches(elem, predicate, collection);
            }
        }
    }

    public static <T> void addIf(@Nonnull Predicate<? super T> predicate, @Nonnull Collection<T> collection, @Nullable Iterable<T> elements) {
        if (elements != null) {
            for (T elem : elements) {
                BuilderUtil.addIfMatches(elem, predicate, collection);
            }
        }
    }

    private static <T> void addIfMatches(T element, @Nonnull Predicate<? super T> predicate, @Nonnull Collection<T> collection) {
        if (predicate.test(element)) {
            collection.add(element);
        }
    }
}

