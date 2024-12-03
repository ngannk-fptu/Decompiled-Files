/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.predicates.AbstractVisitor;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.EvaluatePredicate;
import com.hazelcast.query.impl.predicates.InPredicate;
import com.hazelcast.query.impl.predicates.NotEqualPredicate;
import com.hazelcast.query.impl.predicates.NotPredicate;
import com.hazelcast.query.impl.predicates.OrPredicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluateVisitor
extends AbstractVisitor {
    private static final Predicate[] EMPTY_PREDICATES = new Predicate[0];

    /*
     * WARNING - void declaration
     */
    @Override
    public Predicate visit(AndPredicate andPredicate, Indexes indexes) {
        void var8_10;
        Predicate[] predicates = andPredicate.predicates;
        HashMap<String, ArrayList<EvaluatePredicate>> evaluable = null;
        boolean requiresGeneration = false;
        Predicate[] predicateArray = predicates;
        int n = predicateArray.length;
        boolean bl = false;
        while (var8_10 < n) {
            EvaluatePredicate evaluatePredicate;
            String indexName;
            InternalIndex index;
            Predicate subPredicate = predicateArray[var8_10];
            if (subPredicate instanceof EvaluatePredicate && (index = indexes.matchIndex(indexName = (evaluatePredicate = (EvaluatePredicate)subPredicate).getIndexName(), andPredicate.getClass(), QueryContext.IndexMatchHint.EXACT_NAME, -1)) != null) {
                ArrayList<EvaluatePredicate> group;
                if (evaluable == null) {
                    evaluable = new HashMap<String, ArrayList<EvaluatePredicate>>(predicates.length);
                }
                if ((group = (ArrayList<EvaluatePredicate>)evaluable.get(indexName)) == null) {
                    group = new ArrayList<EvaluatePredicate>(predicates.length);
                    evaluable.put(indexName, group);
                } else {
                    requiresGeneration = true;
                }
                group.add(evaluatePredicate);
            }
            ++var8_10;
        }
        if (!requiresGeneration) {
            return andPredicate;
        }
        ArrayList output = new ArrayList();
        for (Predicate subPredicate : predicates) {
            if (!(subPredicate instanceof EvaluatePredicate)) {
                output.add(subPredicate);
                continue;
            }
            EvaluatePredicate evaluatePredicate = (EvaluatePredicate)subPredicate;
            String indexName = evaluatePredicate.getIndexName();
            InternalIndex index = indexes.matchIndex(indexName, andPredicate.getClass(), QueryContext.IndexMatchHint.EXACT_NAME, -1);
            if (index != null) continue;
            output.add(subPredicate);
        }
        for (Map.Entry entry : evaluable.entrySet()) {
            String indexName = (String)entry.getKey();
            List group = (List)entry.getValue();
            if (group.size() == 1) {
                output.add(group.get(0));
                continue;
            }
            Predicate[] groupPredicates = new Predicate[group.size()];
            for (int i = 0; i < groupPredicates.length; ++i) {
                groupPredicates[i] = ((EvaluatePredicate)group.get(i)).getPredicate();
            }
            output.add(new EvaluatePredicate(new AndPredicate(groupPredicates), indexName));
        }
        return output.size() == 1 ? (Predicate)output.get(0) : new AndPredicate(output.toArray(EMPTY_PREDICATES));
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public Predicate visit(OrPredicate orPredicate, Indexes indexes) {
        void var8_10;
        Predicate[] predicates = orPredicate.predicates;
        HashMap<String, ArrayList<EvaluatePredicate>> evaluable = null;
        boolean requiresGeneration = false;
        Predicate[] predicateArray = predicates;
        int n = predicateArray.length;
        boolean bl = false;
        while (var8_10 < n) {
            EvaluatePredicate evaluatePredicate;
            String indexName;
            InternalIndex index;
            Predicate subPredicate = predicateArray[var8_10];
            if (subPredicate instanceof EvaluatePredicate && (index = indexes.matchIndex(indexName = (evaluatePredicate = (EvaluatePredicate)subPredicate).getIndexName(), orPredicate.getClass(), QueryContext.IndexMatchHint.EXACT_NAME, -1)) != null) {
                ArrayList<EvaluatePredicate> group;
                if (evaluable == null) {
                    evaluable = new HashMap<String, ArrayList<EvaluatePredicate>>(predicates.length);
                }
                if ((group = (ArrayList<EvaluatePredicate>)evaluable.get(indexName)) == null) {
                    group = new ArrayList<EvaluatePredicate>(predicates.length);
                    evaluable.put(indexName, group);
                } else {
                    requiresGeneration = true;
                }
                group.add(evaluatePredicate);
            }
            ++var8_10;
        }
        if (!requiresGeneration) {
            return orPredicate;
        }
        ArrayList output = new ArrayList();
        for (Predicate subPredicate : predicates) {
            if (!(subPredicate instanceof EvaluatePredicate)) {
                output.add(subPredicate);
                continue;
            }
            EvaluatePredicate evaluatePredicate = (EvaluatePredicate)subPredicate;
            String indexName = evaluatePredicate.getIndexName();
            InternalIndex index = indexes.matchIndex(indexName, orPredicate.getClass(), QueryContext.IndexMatchHint.EXACT_NAME, -1);
            if (index != null) continue;
            output.add(subPredicate);
        }
        for (Map.Entry entry : evaluable.entrySet()) {
            String indexName = (String)entry.getKey();
            List group = (List)entry.getValue();
            if (group.size() == 1) {
                output.add(group.get(0));
                continue;
            }
            Predicate[] groupPredicates = new Predicate[group.size()];
            for (int i = 0; i < groupPredicates.length; ++i) {
                groupPredicates[i] = ((EvaluatePredicate)group.get(i)).getPredicate();
            }
            output.add(new EvaluatePredicate(new OrPredicate(groupPredicates), indexName));
        }
        return output.size() == 1 ? (Predicate)output.get(0) : new OrPredicate(output.toArray(EMPTY_PREDICATES));
    }

    @Override
    public Predicate visit(NotPredicate notPredicate, Indexes indexes) {
        Predicate subPredicate = notPredicate.getPredicate();
        if (!(subPredicate instanceof EvaluatePredicate)) {
            return notPredicate;
        }
        EvaluatePredicate evaluatePredicate = (EvaluatePredicate)subPredicate;
        String indexName = evaluatePredicate.getIndexName();
        InternalIndex index = indexes.matchIndex(indexName, notPredicate.getClass(), QueryContext.IndexMatchHint.EXACT_NAME, -1);
        if (index == null) {
            return notPredicate;
        }
        return new EvaluatePredicate(new NotPredicate(evaluatePredicate.getPredicate()), indexName);
    }

    @Override
    public Predicate visit(EqualPredicate predicate, Indexes indexes) {
        InternalIndex index = indexes.matchIndex(predicate.attributeName, predicate.getClass(), QueryContext.IndexMatchHint.PREFER_UNORDERED, -1);
        if (index == null) {
            return predicate;
        }
        TypeConverter converter = index.getConverter();
        if (converter == null) {
            return predicate;
        }
        return new EvaluatePredicate(predicate, index.getName());
    }

    @Override
    public Predicate visit(NotEqualPredicate predicate, Indexes indexes) {
        InternalIndex index = indexes.matchIndex(predicate.attributeName, predicate.getClass(), QueryContext.IndexMatchHint.PREFER_UNORDERED, -1);
        if (index == null) {
            return predicate;
        }
        TypeConverter converter = index.getConverter();
        if (converter == null) {
            return predicate;
        }
        return new EvaluatePredicate(predicate, index.getName());
    }

    @Override
    public Predicate visit(InPredicate predicate, Indexes indexes) {
        InternalIndex index = indexes.matchIndex(predicate.attributeName, predicate.getClass(), QueryContext.IndexMatchHint.PREFER_UNORDERED, -1);
        if (index == null) {
            return predicate;
        }
        TypeConverter converter = index.getConverter();
        if (converter == null) {
            return predicate;
        }
        return new EvaluatePredicate(predicate, index.getName());
    }
}

