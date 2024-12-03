/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.aggregator;

import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.aggregator.AggregatorException;
import net.sf.ehcache.search.aggregator.AggregatorInstance;

public class Max<T>
implements AggregatorInstance<T> {
    private Comparable max;
    private final Attribute<?> attribute;

    public Max(Attribute<?> attribute) {
        this.attribute = attribute;
    }

    @Override
    public Max<T> createClone() {
        return new Max<T>(this.attribute);
    }

    @Override
    public T aggregateResult() {
        return (T)this.max;
    }

    @Override
    public void accept(Object input) throws AggregatorException {
        if (input == null) {
            return;
        }
        Comparable next = Max.getComparable(input);
        if (this.max == null) {
            this.max = next;
        } else if (next.compareTo(this.max) > 0) {
            this.max = next;
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

