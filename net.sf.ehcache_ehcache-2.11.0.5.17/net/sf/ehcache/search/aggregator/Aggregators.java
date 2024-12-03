/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.aggregator;

import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.aggregator.Aggregator;
import net.sf.ehcache.search.aggregator.AggregatorInstance;
import net.sf.ehcache.search.aggregator.Average;
import net.sf.ehcache.search.aggregator.Count;
import net.sf.ehcache.search.aggregator.Max;
import net.sf.ehcache.search.aggregator.Min;
import net.sf.ehcache.search.aggregator.Sum;

public final class Aggregators {
    private Aggregators() {
    }

    public static Aggregator min(final Attribute<?> attribute) {
        return new Aggregator(){

            @Override
            public <T> AggregatorInstance<T> createInstance() {
                return new Min(attribute);
            }
        };
    }

    public static Aggregator max(final Attribute<?> attribute) {
        return new Aggregator(){

            @Override
            public <T> AggregatorInstance<T> createInstance() {
                return new Max(attribute);
            }
        };
    }

    public static Aggregator average(final Attribute<?> attribute) {
        return new Aggregator(){

            public AggregatorInstance<Double> createInstance() {
                return new Average(attribute);
            }
        };
    }

    public static Aggregator sum(final Attribute<?> attribute) {
        return new Aggregator(){

            public AggregatorInstance<Long> createInstance() {
                return new Sum(attribute);
            }
        };
    }

    public static Aggregator count() {
        return new Aggregator(){

            public AggregatorInstance<Integer> createInstance() {
                return new Count();
            }
        };
    }
}

