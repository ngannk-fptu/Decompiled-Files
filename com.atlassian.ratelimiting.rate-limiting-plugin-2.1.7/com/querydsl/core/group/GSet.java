/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.types.Expression;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

abstract class GSet<T, S extends Set<T>>
extends AbstractGroupExpression<T, S> {
    private static final long serialVersionUID = -1575808026237160843L;

    public static <U> GSet<U, Set<U>> createLinked(Expression<U> expr) {
        return new GSet<U, Set<U>>((Expression)expr){

            @Override
            protected Set<U> createSet() {
                return new LinkedHashSet();
            }
        };
    }

    public static <U extends Comparable<? super U>> GSet<U, SortedSet<U>> createSorted(Expression<U> expr) {
        return new GSet<U, SortedSet<U>>((Expression)expr){

            @Override
            protected SortedSet<U> createSet() {
                return new TreeSet();
            }
        };
    }

    public static <U> GSet<U, SortedSet<U>> createSorted(Expression<U> expr, final Comparator<? super U> comparator) {
        return new GSet<U, SortedSet<U>>(expr){

            @Override
            protected SortedSet<U> createSet() {
                return new TreeSet(comparator);
            }
        };
    }

    public GSet(Expression<T> expr) {
        super(Set.class, expr);
    }

    protected abstract S createSet();

    @Override
    public GroupCollector<T, S> createGroupCollector() {
        return new GroupCollector<T, S>(){
            private final S set;
            {
                this.set = GSet.this.createSet();
            }

            @Override
            public void add(T o) {
                if (o != null) {
                    this.set.add(o);
                }
            }

            @Override
            public S get() {
                return this.set;
            }
        };
    }
}

