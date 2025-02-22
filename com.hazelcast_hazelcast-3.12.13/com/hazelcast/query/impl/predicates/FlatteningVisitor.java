/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.predicates.AbstractVisitor;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.NegatablePredicate;
import com.hazelcast.query.impl.predicates.NotPredicate;
import com.hazelcast.query.impl.predicates.OrPredicate;
import com.hazelcast.util.collection.ArrayUtils;
import java.util.ArrayList;
import java.util.List;

public class FlatteningVisitor
extends AbstractVisitor {
    @Override
    public Predicate visit(AndPredicate andPredicate, Indexes indexes) {
        Predicate[] originalPredicates = andPredicate.predicates;
        List<Predicate> toBeAdded = null;
        boolean modified = false;
        Predicate[] target = originalPredicates;
        for (int i = 0; i < target.length; ++i) {
            Predicate predicate = target[i];
            if (!(predicate instanceof AndPredicate)) continue;
            Predicate[] subPredicates = ((AndPredicate)predicate).predicates;
            if (!modified) {
                modified = true;
                target = ArrayUtils.createCopy(target);
            }
            toBeAdded = this.replaceFirstAndStoreOthers(target, subPredicates, i, toBeAdded);
        }
        Predicate[] newInners = this.createNewInners(target, toBeAdded);
        if (newInners == originalPredicates) {
            return andPredicate;
        }
        return new AndPredicate(newInners);
    }

    @Override
    public Predicate visit(OrPredicate orPredicate, Indexes indexes) {
        Predicate[] originalPredicates = orPredicate.predicates;
        List<Predicate> toBeAdded = null;
        boolean modified = false;
        Predicate[] target = originalPredicates;
        for (int i = 0; i < target.length; ++i) {
            Predicate predicate = target[i];
            if (!(predicate instanceof OrPredicate)) continue;
            Predicate[] subPredicates = ((OrPredicate)predicate).predicates;
            if (!modified) {
                modified = true;
                target = ArrayUtils.createCopy(target);
            }
            toBeAdded = this.replaceFirstAndStoreOthers(target, subPredicates, i, toBeAdded);
        }
        Predicate[] newInners = this.createNewInners(target, toBeAdded);
        if (newInners == originalPredicates) {
            return orPredicate;
        }
        return new OrPredicate(newInners);
    }

    private List<Predicate> replaceFirstAndStoreOthers(Predicate[] predicates, Predicate[] subPredicates, int position, List<Predicate> store) {
        if (subPredicates == null || subPredicates.length == 0) {
            return store;
        }
        predicates[position] = subPredicates[0];
        for (int j = 1; j < subPredicates.length; ++j) {
            if (store == null) {
                store = new ArrayList<Predicate>();
            }
            store.add(subPredicates[j]);
        }
        return store;
    }

    private Predicate[] createNewInners(Predicate[] predicates, List<Predicate> toBeAdded) {
        if (toBeAdded == null || toBeAdded.size() == 0) {
            return predicates;
        }
        int newSize = predicates.length + toBeAdded.size();
        Predicate[] newPredicates = new Predicate[newSize];
        System.arraycopy(predicates, 0, newPredicates, 0, predicates.length);
        for (int i = predicates.length; i < newSize; ++i) {
            newPredicates[i] = toBeAdded.get(i - predicates.length);
        }
        return newPredicates;
    }

    @Override
    public Predicate visit(NotPredicate predicate, Indexes indexes) {
        Predicate inner = predicate.predicate;
        if (inner instanceof NegatablePredicate) {
            return ((NegatablePredicate)((Object)inner)).negate();
        }
        return predicate;
    }
}

