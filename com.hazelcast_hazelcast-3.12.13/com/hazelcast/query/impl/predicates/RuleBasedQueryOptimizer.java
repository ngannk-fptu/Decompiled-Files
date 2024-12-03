/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.VisitablePredicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.predicates.CompositeIndexVisitor;
import com.hazelcast.query.impl.predicates.EvaluateVisitor;
import com.hazelcast.query.impl.predicates.FlatteningVisitor;
import com.hazelcast.query.impl.predicates.OrToInVisitor;
import com.hazelcast.query.impl.predicates.QueryOptimizer;
import com.hazelcast.query.impl.predicates.RangeVisitor;
import com.hazelcast.query.impl.predicates.Visitor;

public final class RuleBasedQueryOptimizer
implements QueryOptimizer {
    private final Visitor flatteningVisitor = new FlatteningVisitor();
    private final Visitor rangeVisitor = new RangeVisitor();
    private final Visitor orToInVisitor = new OrToInVisitor();
    private final Visitor compositeIndexVisitor = new CompositeIndexVisitor();
    private final Visitor evaluateVisitor = new EvaluateVisitor();

    @Override
    public <K, V> Predicate<K, V> optimize(Predicate<K, V> predicate, Indexes indexes) {
        Predicate optimized = predicate;
        if (optimized instanceof VisitablePredicate) {
            optimized = ((VisitablePredicate)((Object)optimized)).accept(this.flatteningVisitor, indexes);
        }
        if (optimized instanceof VisitablePredicate) {
            optimized = ((VisitablePredicate)((Object)optimized)).accept(this.rangeVisitor, indexes);
        }
        if (optimized instanceof VisitablePredicate) {
            optimized = ((VisitablePredicate)((Object)optimized)).accept(this.orToInVisitor, indexes);
        }
        if (optimized instanceof VisitablePredicate) {
            optimized = ((VisitablePredicate)((Object)optimized)).accept(this.compositeIndexVisitor, indexes);
        }
        if (optimized instanceof VisitablePredicate) {
            optimized = ((VisitablePredicate)((Object)optimized)).accept(this.evaluateVisitor, indexes);
        }
        return optimized;
    }
}

