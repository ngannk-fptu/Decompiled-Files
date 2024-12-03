/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ForwardingList
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.streams.api.common;

import com.atlassian.streams.api.common.NonEmptyIterable;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;

public class ImmutableNonEmptyList<T>
extends ForwardingList<T>
implements NonEmptyIterable<T> {
    private final ImmutableList<T> delegate;

    public ImmutableNonEmptyList(T head) {
        Preconditions.checkNotNull(head, (Object)"head");
        this.delegate = ImmutableList.of(head);
    }

    public ImmutableNonEmptyList(T head, Iterable<T> tail) {
        Preconditions.checkNotNull(head, (Object)"head");
        this.delegate = ImmutableList.builder().add(head).addAll(tail).build();
    }

    public ImmutableNonEmptyList(NonEmptyIterable<T> items) {
        Preconditions.checkNotNull(items, (Object)"items");
        this.delegate = ImmutableList.copyOf(items);
    }

    public static <E> ImmutableNonEmptyList<E> of(E e) {
        return new ImmutableNonEmptyList<E>(e);
    }

    public static <E> ImmutableNonEmptyList<E> of(E e1, E e2) {
        return new ImmutableNonEmptyList<E>(e1, ImmutableList.of(e2));
    }

    public static <E> ImmutableNonEmptyList<E> of(E e, E ... others) {
        return new ImmutableNonEmptyList<E>(e, Arrays.asList(others));
    }

    public static <E> ImmutableNonEmptyList<E> of(E e, Iterable<E> others) {
        return new ImmutableNonEmptyList<E>(e, others);
    }

    public static <E> ImmutableNonEmptyList<E> copyOf(NonEmptyIterable<E> items) {
        return items instanceof ImmutableNonEmptyList ? (ImmutableNonEmptyList<Object>)items : new ImmutableNonEmptyList<E>(items);
    }

    protected List<T> delegate() {
        return this.delegate;
    }
}

