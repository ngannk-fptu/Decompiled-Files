/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import net.sf.ehcache.search.parser.MAggregate;
import net.sf.ehcache.search.parser.MAttribute;

public class MTarget {
    private final MAggregate agg;
    private final MAttribute attr;

    public MTarget() {
        this.agg = null;
        this.attr = null;
    }

    public MTarget(MAggregate agg) {
        this.agg = agg;
        this.attr = null;
    }

    public MTarget(MAttribute attr) {
        this.attr = attr;
        this.agg = null;
    }

    public MAggregate getAggregate() {
        return this.agg;
    }

    public MAttribute getAttribute() {
        return this.attr;
    }

    public boolean isAttribute() {
        return this.attr != null;
    }

    public boolean isAggregate() {
        return this.agg != null;
    }

    public boolean isStar() {
        return this.agg == null && this.attr == null;
    }

    public String toString() {
        if (this.agg != null) {
            return this.agg.toString();
        }
        if (this.attr != null) {
            return this.attr.toString();
        }
        return "*";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.agg == null ? 0 : this.agg.hashCode());
        result = 31 * result + (this.attr == null ? 0 : this.attr.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MTarget other = (MTarget)obj;
        if (this.agg == null ? other.agg != null : !this.agg.equals(other.agg)) {
            return false;
        }
        return !(this.attr == null ? other.attr != null : !this.attr.equals(other.attr));
    }
}

