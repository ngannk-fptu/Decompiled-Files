/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation;

import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.aggregation.PropertyExtractor;
import com.hazelcast.mapreduce.aggregation.impl.AcceptAllSupplier;
import com.hazelcast.mapreduce.aggregation.impl.KeyPredicateSupplier;
import com.hazelcast.mapreduce.aggregation.impl.PredicateSupplier;
import com.hazelcast.query.Predicate;
import java.io.Serializable;
import java.util.Map;

@Deprecated
public abstract class Supplier<KeyIn, ValueIn, ValueOut>
implements Serializable {
    public abstract ValueOut apply(Map.Entry<KeyIn, ValueIn> var1);

    public static <KeyIn, ValueIn, ValueOut> Supplier<KeyIn, ValueIn, ValueOut> all() {
        return new AcceptAllSupplier(null);
    }

    public static <KeyIn, ValueIn, ValueOut> Supplier<KeyIn, ValueIn, ValueOut> all(PropertyExtractor<ValueIn, ValueOut> propertyExtractor) {
        return new AcceptAllSupplier(propertyExtractor);
    }

    public static <KeyIn, ValueIn, ValueOut> Supplier<KeyIn, ValueIn, ValueOut> fromPredicate(Predicate<KeyIn, ValueIn> predicate) {
        return new PredicateSupplier(predicate);
    }

    public static <KeyIn, ValueIn, ValueOut> Supplier<KeyIn, ValueIn, ValueOut> fromPredicate(Predicate<KeyIn, ValueIn> predicate, Supplier<KeyIn, ValueIn, ValueOut> chainedSupplier) {
        return new PredicateSupplier<KeyIn, ValueIn, ValueOut>(predicate, chainedSupplier);
    }

    public static <KeyIn, ValueIn, ValueOut> Supplier<KeyIn, ValueIn, ValueOut> fromKeyPredicate(KeyPredicate<KeyIn> keyPredicate) {
        return new KeyPredicateSupplier(keyPredicate);
    }

    public static <KeyIn, ValueIn, ValueOut> Supplier<KeyIn, ValueIn, ValueOut> fromKeyPredicate(KeyPredicate<KeyIn> keyPredicate, Supplier<KeyIn, ValueIn, ValueOut> chainedSupplier) {
        return new KeyPredicateSupplier<KeyIn, ValueIn, ValueOut>(keyPredicate, chainedSupplier);
    }
}

