/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import org.jdom2.filter.AbstractFilter;
import org.jdom2.filter.Filter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class NegateFilter
extends AbstractFilter<Object> {
    private static final long serialVersionUID = 200L;
    private final Filter<?> filter;

    public NegateFilter(Filter<?> filter) {
        this.filter = filter;
    }

    @Override
    public Object filter(Object content) {
        if (this.filter.matches(content)) {
            return null;
        }
        return content;
    }

    Filter<?> getBaseFilter() {
        return this.filter;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NegateFilter) {
            return this.filter.equals(((NegateFilter)obj).filter);
        }
        return false;
    }

    public int hashCode() {
        return ~this.filter.hashCode();
    }

    public String toString() {
        return new StringBuilder(64).append("[NegateFilter: ").append(this.filter.toString()).append("]").toString();
    }
}

