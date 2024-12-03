/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.util;

import com.google.common.collect.ImmutableList;
import io.atlassian.fugue.Option;
import java.util.Optional;

public class Convert {
    @Deprecated
    public static <T> Optional<T> toOptional(Option<T> op) {
        return (Optional)op.map(Optional::of).getOrElse(Optional.empty());
    }

    @Deprecated
    public static <T> Option<T> fugueOption(Optional<T> op) {
        return op.map(Option::some).orElseGet(Option::none);
    }

    public static <T> Iterable<T> iterableOf(Optional<T> op) {
        return (Iterable)op.map(ImmutableList::of).orElseGet(ImmutableList::of);
    }
}

