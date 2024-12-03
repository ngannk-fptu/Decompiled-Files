/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.QueryConstants;

public class EntryObject {
    PredicateBuilder qb;

    public EntryObject(PredicateBuilder qb) {
        this.qb = qb;
    }

    public EntryObject get(String attribute) {
        if (QueryConstants.KEY_ATTRIBUTE_NAME.value().equals(this.qb.getAttribute())) {
            this.qb.setAttribute(QueryConstants.KEY_ATTRIBUTE_NAME.value() + "#" + attribute);
        } else {
            this.qb.setAttribute(attribute);
        }
        return this;
    }

    public EntryObject key() {
        this.qb.setAttribute(QueryConstants.KEY_ATTRIBUTE_NAME.value());
        return this;
    }

    public PredicateBuilder is(String attribute) {
        return this.addPredicate(Predicates.equal(attribute, Boolean.valueOf(true)));
    }

    public PredicateBuilder isNot(String attribute) {
        return this.addPredicate(Predicates.notEqual(attribute, Boolean.valueOf(true)));
    }

    public PredicateBuilder equal(Comparable value) {
        return this.addPredicate(Predicates.equal(this.qb.getAttribute(), value));
    }

    public PredicateBuilder notEqual(Comparable value) {
        return this.addPredicate(Predicates.notEqual(this.qb.getAttribute(), value));
    }

    public PredicateBuilder isNull() {
        return this.addPredicate(Predicates.equal(this.qb.getAttribute(), null));
    }

    public PredicateBuilder isNotNull() {
        return this.addPredicate(Predicates.notEqual(this.qb.getAttribute(), null));
    }

    public PredicateBuilder greaterThan(Comparable value) {
        return this.addPredicate(Predicates.greaterThan(this.qb.getAttribute(), value));
    }

    public PredicateBuilder greaterEqual(Comparable value) {
        return this.addPredicate(Predicates.greaterEqual(this.qb.getAttribute(), value));
    }

    public PredicateBuilder lessThan(Comparable value) {
        return this.addPredicate(Predicates.lessThan(this.qb.getAttribute(), value));
    }

    public PredicateBuilder lessEqual(Comparable value) {
        return this.addPredicate(Predicates.lessEqual(this.qb.getAttribute(), value));
    }

    public PredicateBuilder between(Comparable from, Comparable to) {
        return this.addPredicate(Predicates.between(this.qb.getAttribute(), from, to));
    }

    public PredicateBuilder in(Comparable ... values) {
        return this.addPredicate(Predicates.in(this.qb.getAttribute(), values));
    }

    private PredicateBuilder addPredicate(Predicate predicate) {
        this.qb.lsPredicates.add(predicate);
        return this.qb;
    }
}

