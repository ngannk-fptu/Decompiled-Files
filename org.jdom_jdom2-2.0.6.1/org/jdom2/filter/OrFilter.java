/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import org.jdom2.Content;
import org.jdom2.filter.AbstractFilter;
import org.jdom2.filter.Filter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class OrFilter
extends AbstractFilter<Content> {
    private static final long serialVersionUID = 200L;
    private final Filter<?> left;
    private final Filter<?> right;

    public OrFilter(Filter<?> left, Filter<?> right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("null filter not allowed");
        }
        this.left = left;
        this.right = right;
    }

    @Override
    public Content filter(Object obj) {
        if (this.left.matches(obj) || this.right.matches(obj)) {
            return (Content)obj;
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof OrFilter) {
            OrFilter filter = (OrFilter)obj;
            if (this.left.equals(filter.left) && this.right.equals(filter.right) || this.left.equals(filter.right) && this.right.equals(filter.left)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return ~this.left.hashCode() ^ this.right.hashCode();
    }

    public String toString() {
        return new StringBuilder(64).append("[OrFilter: ").append(this.left.toString()).append(",\n").append("           ").append(this.right.toString()).append("]").toString();
    }
}

