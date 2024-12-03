/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.predicates.Visitor;

public interface VisitablePredicate {
    public Predicate accept(Visitor var1, Indexes var2);
}

