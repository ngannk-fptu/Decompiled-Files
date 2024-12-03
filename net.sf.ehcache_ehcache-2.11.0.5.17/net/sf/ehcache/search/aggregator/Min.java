/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.aggregator;

import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.aggregator.AggregatorException;
import net.sf.ehcache.search.aggregator.AggregatorInstance;

public class Min<T>
implements AggregatorInstance<T> {
    private Comparable min;
    private final Attribute<?> attribute;

    public Min(Attribute<?> attribute) {
        this.attribute = attribute;
    }

    @Override
    public Min<T> createClone() {
        return new Min<T>(this.attribute);
    }

    @Override
    public T aggregateResult() {
        return (T)this.min;
    }

    @Override
    public void accept(Object input) throws AggregatorException {
        if (input == null) {
            return;
        }
        Comparable next = Min.getComparable(input);
        if (this.min == null) {
            this.min = next;
        } else if (next.compareTo(this.min) < 0) {
            this.min = next;
        }
    }

    private static Comparable getComparable(Object o) {
        if (o instanceof Comparable) {
            return (Comparable)o;
        }
        throw new AggregatorException("Value is not Comparable: " + o.getClass());
    }

    @Override
    public Attribute getAttribute() {
        return this.attribute;
    }
}

