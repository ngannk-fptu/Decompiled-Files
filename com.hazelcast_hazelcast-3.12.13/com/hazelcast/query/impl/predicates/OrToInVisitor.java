/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.predicates.AbstractVisitor;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.InPredicate;
import com.hazelcast.query.impl.predicates.OrPredicate;
import com.hazelcast.util.collection.ArrayUtils;
import com.hazelcast.util.collection.InternalListMultiMap;
import java.util.List;
import java.util.Map;

public class OrToInVisitor
extends AbstractVisitor {
    private static final int MINIMUM_NUMBER_OF_OR_TO_REPLACE = 5;

    @Override
    public Predicate visit(OrPredicate orPredicate, Indexes indexes) {
        Predicate[] originalInnerPredicates = orPredicate.predicates;
        if (originalInnerPredicates == null || originalInnerPredicates.length < 5) {
            return orPredicate;
        }
        InternalListMultiMap<String, Integer> candidates = this.findAndGroupCandidates(originalInnerPredicates);
        if (candidates == null) {
            return orPredicate;
        }
        int toBeRemoved = 0;
        boolean modified = false;
        Predicate[] target = originalInnerPredicates;
        for (Map.Entry<String, List<Integer>> candidate : candidates.entrySet()) {
            String attribute = candidate.getKey();
            List<Integer> positions = candidate.getValue();
            if (positions.size() < 5) continue;
            if (!modified) {
                modified = true;
                target = ArrayUtils.createCopy(target);
            }
            toBeRemoved = this.replaceForAttribute(attribute, target, positions, toBeRemoved);
        }
        Predicate[] newInnerPredicates = this.replaceInnerPredicates(target, toBeRemoved);
        return this.getOrCreateFinalPredicate(orPredicate, originalInnerPredicates, newInnerPredicates);
    }

    private Predicate getOrCreateFinalPredicate(OrPredicate predicate, Predicate[] innerPredicates, Predicate[] newPredicates) {
        if (newPredicates == innerPredicates) {
            return predicate;
        }
        if (newPredicates.length == 1) {
            return newPredicates[0];
        }
        return new OrPredicate(newPredicates);
    }

    private int replaceForAttribute(String attribute, Predicate[] innerPredicates, List<Integer> positions, int toBeRemoved) {
        Comparable[] values = new Comparable[positions.size()];
        for (int i = 0; i < positions.size(); ++i) {
            int position = positions.get(i);
            EqualPredicate equalPredicate = (EqualPredicate)innerPredicates[position];
            values[i] = equalPredicate.value;
            innerPredicates[position] = null;
            ++toBeRemoved;
        }
        InPredicate inPredicate = new InPredicate(attribute, values);
        innerPredicates[positions.get((int)0).intValue()] = inPredicate;
        return --toBeRemoved;
    }

    private Predicate[] replaceInnerPredicates(Predicate[] innerPredicates, int toBeRemoved) {
        if (toBeRemoved == 0) {
            return innerPredicates;
        }
        int removed = 0;
        int newSize = innerPredicates.length - toBeRemoved;
        Predicate[] newPredicates = new Predicate[newSize];
        for (int i = 0; i < innerPredicates.length; ++i) {
            Predicate p = innerPredicates[i];
            if (p != null) {
                newPredicates[i - removed] = p;
                continue;
            }
            ++removed;
        }
        return newPredicates;
    }

    private InternalListMultiMap<String, Integer> findAndGroupCandidates(Predicate[] innerPredicates) {
        InternalListMultiMap<String, Integer> candidates = null;
        for (int i = 0; i < innerPredicates.length; ++i) {
            Predicate p = innerPredicates[i];
            if (!p.getClass().equals(EqualPredicate.class)) continue;
            EqualPredicate equalPredicate = (EqualPredicate)p;
            String attribute = equalPredicate.attributeName;
            if (candidates == null) {
                candidates = new InternalListMultiMap<String, Integer>();
            }
            candidates.put(attribute, i);
        }
        return candidates;
    }
}

