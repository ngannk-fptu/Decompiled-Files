/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import org.jdom2.filter.AbstractFilter;
import org.jdom2.filter.Filter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class AndFilter<T>
extends AbstractFilter<T> {
    private static final long serialVersionUID = 200L;
    private final Filter<?> base;
    private final Filter<T> refiner;

    public AndFilter(Filter<?> base, Filter<T> refiner) {
        if (base == null || refiner == null) {
            throw new NullPointerException("Cannot have a null base or refiner filter");
        }
        this.base = base;
        this.refiner = refiner;
    }

    @Override
    public T filter(Object content) {
        Object o = this.base.filter(content);
        if (o != null) {
            return this.refiner.filter(content);
        }
        return null;
    }

    public int hashCode() {
        return this.base.hashCode() ^ this.refiner.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AndFilter) {
            AndFilter them = (AndFilter)obj;
            return this.base.equals(them.base) && this.refiner.equals(them.refiner) || this.refiner.equals(them.base) && this.base.equals(them.refiner);
        }
        return false;
    }

    public String toString() {
        return new StringBuilder(64).append("[AndFilter: ").append(this.base.toString()).append(",\n").append("            ").append(this.refiner.toString()).append("]").toString();
    }
}

