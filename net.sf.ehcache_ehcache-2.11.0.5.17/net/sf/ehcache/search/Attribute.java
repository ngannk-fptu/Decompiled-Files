/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search;

import java.util.Collection;
import net.sf.ehcache.search.aggregator.Aggregator;
import net.sf.ehcache.search.aggregator.Aggregators;
import net.sf.ehcache.search.expression.Between;
import net.sf.ehcache.search.expression.Criteria;
import net.sf.ehcache.search.expression.EqualTo;
import net.sf.ehcache.search.expression.GreaterThan;
import net.sf.ehcache.search.expression.GreaterThanOrEqual;
import net.sf.ehcache.search.expression.ILike;
import net.sf.ehcache.search.expression.InCollection;
import net.sf.ehcache.search.expression.IsNull;
import net.sf.ehcache.search.expression.LessThan;
import net.sf.ehcache.search.expression.LessThanOrEqual;
import net.sf.ehcache.search.expression.NotEqualTo;
import net.sf.ehcache.search.expression.NotILike;
import net.sf.ehcache.search.expression.NotNull;

public class Attribute<T> {
    private final String attributeName;

    public Attribute(String attributeName) {
        if (attributeName == null) {
            throw new NullPointerException();
        }
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public Criteria between(T min, T max) {
        return this.between(min, max, true, true);
    }

    public Criteria between(T min, T max, boolean minInclusive, boolean maxInclusive) {
        return new Between(this.attributeName, min, max, minInclusive, maxInclusive);
    }

    public Criteria in(Collection<? extends T> values) {
        return new InCollection(this.attributeName, values);
    }

    public Criteria ne(T value) {
        return new NotEqualTo(this.attributeName, value);
    }

    public Criteria lt(T value) {
        return new LessThan(this.attributeName, value);
    }

    public Criteria le(T value) {
        return new LessThanOrEqual(this.attributeName, value);
    }

    public Criteria gt(T value) {
        return new GreaterThan(this.attributeName, value);
    }

    public Criteria ge(T value) {
        return new GreaterThanOrEqual(this.attributeName, value);
    }

    public Criteria eq(T value) {
        return new EqualTo(this.attributeName, value);
    }

    public Criteria ilike(String regex) {
        return new ILike(this.attributeName, regex);
    }

    public Criteria notIlike(String regex) {
        return new NotILike(this.attributeName, regex);
    }

    public Criteria isNull() {
        return new IsNull(this.attributeName);
    }

    public Criteria notNull() {
        return new NotNull(this.attributeName);
    }

    public Aggregator count() {
        return Aggregators.count();
    }

    public Aggregator max() {
        return Aggregators.max(this);
    }

    public Aggregator min() {
        return Aggregators.min(this);
    }

    public Aggregator sum() {
        return Aggregators.sum(this);
    }

    public Aggregator average() {
        return Aggregators.average(this);
    }

    public String toString() {
        return this.attributeName;
    }

    public int hashCode() {
        return this.attributeName.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Attribute) {
            Attribute other = (Attribute)obj;
            return this.attributeName.equals(other.attributeName);
        }
        return false;
    }
}

