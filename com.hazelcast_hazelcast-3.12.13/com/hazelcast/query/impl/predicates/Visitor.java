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

public interface Visitor {
    public Predicate visit(EqualPredicate var1, Indexes var2);

    public Predicate visit(NotEqualPredicate var1, Indexes var2);

    public Predicate visit(AndPredicate var1, Indexes var2);

    public Predicate visit(OrPredicate var1, Indexes var2);

    public Predicate visit(NotPredicate var1, Indexes var2);

    public Predicate visit(InPredicate var1, Indexes var2);

    public Predicate visit(BetweenPredicate var1, Indexes var2);
}

