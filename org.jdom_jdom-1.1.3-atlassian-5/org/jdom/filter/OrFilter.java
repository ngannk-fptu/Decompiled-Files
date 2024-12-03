/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.filter;

import org.jdom.filter.AbstractFilter;
import org.jdom.filter.Filter;

final class OrFilter
extends AbstractFilter {
    private static final String CVS_ID = "@(#) $RCSfile: OrFilter.java,v $ $Revision: 1.5 $ $Date: 2007/11/10 05:29:00 $";
    private Filter left;
    private Filter right;

    public OrFilter(Filter left, Filter right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("null filter not allowed");
        }
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean matches(Object obj) {
        return this.left.matches(obj) || this.right.matches(obj);
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
        return 31 * this.left.hashCode() + this.right.hashCode();
    }

    public String toString() {
        return new StringBuffer(64).append("[OrFilter: ").append(this.left.toString()).append(",\n").append("           ").append(this.right.toString()).append("]").toString();
    }
}

