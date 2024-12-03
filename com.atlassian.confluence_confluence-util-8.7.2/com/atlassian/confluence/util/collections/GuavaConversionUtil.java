/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.util.collections;

import com.atlassian.annotations.Internal;
import com.google.common.base.Predicate;
import java.util.Arrays;

@Deprecated
@Internal
public class GuavaConversionUtil {
    public static <T> Predicate[] toGuavaPredicates(java.util.function.Predicate<? super T>[] filter) {
        return (Predicate[])Arrays.stream(filter).map(p -> p::test).toArray(Predicate[]::new);
    }

    public static <T> Predicate<? super T> toGuavaPredicate(java.util.function.Predicate<? super T> filter) {
        return filter != null ? filter::test : null;
    }
}

