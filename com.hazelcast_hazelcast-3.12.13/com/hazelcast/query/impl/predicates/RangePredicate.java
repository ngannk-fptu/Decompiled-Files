/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.Predicate;

public interface RangePredicate
extends Predicate {
    public String getAttribute();

    public Comparable getFrom();

    public boolean isFromInclusive();

    public Comparable getTo();

    public boolean isToInclusive();
}

