/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.VisitablePredicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.predicates.Visitor;
import com.hazelcast.util.collection.ArrayUtils;

public final class VisitorUtils {
    private VisitorUtils() {
    }

    public static Predicate[] acceptVisitor(Predicate[] predicates, Visitor visitor, Indexes indexes) {
        Predicate[] target = predicates;
        boolean copyCreated = false;
        for (int i = 0; i < predicates.length; ++i) {
            Predicate transformed;
            Predicate predicate = predicates[i];
            if (!(predicate instanceof VisitablePredicate) || (transformed = ((VisitablePredicate)((Object)predicate)).accept(visitor, indexes)) == predicate) continue;
            if (!copyCreated) {
                copyCreated = true;
                target = ArrayUtils.createCopy(target);
            }
            target[i] = transformed;
        }
        return target;
    }
}

