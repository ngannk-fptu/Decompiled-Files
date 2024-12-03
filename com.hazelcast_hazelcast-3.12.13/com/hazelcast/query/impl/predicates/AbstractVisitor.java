/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.BetweenPredicate;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.InPredicate;
import com.hazelcast.query.impl.predicates.NotEqualPredicate;
import com.hazelcast.query.impl.predicates.NotPredicate;
import com.hazelcast.query.impl.predicates.OrPredicate;
import com.hazelcast.query.impl.predicates.Visitor;

public abstract class AbstractVisitor
implements Visitor {
    @Override
    public Predicate visit(EqualPredicate predicate, Indexes indexes) {
        return predicate;
    }

    @Override
    public Predicate visit(NotEqualPredicate predicate, Indexes indexes) {
        return predicate;
    }

    @Override
    public Predicate visit(AndPredicate predicate, Indexes indexes) {
        return predicate;
    }

    @Override
    public Predicate visit(OrPredicate predicate, Indexes indexes) {
        return predicate;
    }

    @Override
    public Predicate visit(NotPredicate predicate, Indexes indexes) {
        return predicate;
    }

    @Override
    public Predicate visit(InPredicate predicate, Indexes indexes) {
        return predicate;
    }

    @Override
    public Predicate visit(BetweenPredicate predicate, Indexes indexes) {
        return predicate;
    }
}

