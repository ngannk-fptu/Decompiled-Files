/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.query.impl.FalsePredicate;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.BetweenPredicate;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.GreaterLessPredicate;
import com.hazelcast.query.impl.predicates.ILikePredicate;
import com.hazelcast.query.impl.predicates.InPredicate;
import com.hazelcast.query.impl.predicates.InstanceOfPredicate;
import com.hazelcast.query.impl.predicates.LikePredicate;
import com.hazelcast.query.impl.predicates.NotEqualPredicate;
import com.hazelcast.query.impl.predicates.NotPredicate;
import com.hazelcast.query.impl.predicates.OrPredicate;
import com.hazelcast.query.impl.predicates.RegexPredicate;

public final class Predicates {
    private Predicates() {
    }

    public static <K, V> Predicate<K, V> alwaysTrue() {
        return new TruePredicate();
    }

    public static <K, V> Predicate<K, V> alwaysFalse() {
        return new FalsePredicate();
    }

    public static Predicate instanceOf(Class klass) {
        return new InstanceOfPredicate(klass);
    }

    public static Predicate and(Predicate ... predicates) {
        return new AndPredicate(predicates);
    }

    public static Predicate not(Predicate predicate) {
        return new NotPredicate(predicate);
    }

    public static Predicate or(Predicate ... predicates) {
        return new OrPredicate(predicates);
    }

    public static Predicate notEqual(String attribute, Comparable value) {
        return new NotEqualPredicate(attribute, value);
    }

    public static Predicate equal(String attribute, Comparable value) {
        return new EqualPredicate(attribute, value);
    }

    public static Predicate like(String attribute, String pattern) {
        return new LikePredicate(attribute, pattern);
    }

    public static Predicate ilike(String attribute, String pattern) {
        return new ILikePredicate(attribute, pattern);
    }

    public static Predicate regex(String attribute, String pattern) {
        return new RegexPredicate(attribute, pattern);
    }

    public static Predicate greaterThan(String attribute, Comparable value) {
        return new GreaterLessPredicate(attribute, value, false, false);
    }

    public static Predicate greaterEqual(String attribute, Comparable value) {
        return new GreaterLessPredicate(attribute, value, true, false);
    }

    public static Predicate lessThan(String attribute, Comparable value) {
        return new GreaterLessPredicate(attribute, value, false, true);
    }

    public static Predicate lessEqual(String attribute, Comparable value) {
        return new GreaterLessPredicate(attribute, value, true, true);
    }

    public static Predicate between(String attribute, Comparable from, Comparable to) {
        return new BetweenPredicate(attribute, from, to);
    }

    public static Predicate in(String attribute, Comparable ... values) {
        return new InPredicate(attribute, values);
    }
}

