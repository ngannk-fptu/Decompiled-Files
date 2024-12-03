/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import net.sf.ehcache.search.parser.MAttribute;

public class MOrderBy {
    private final MAttribute attr;
    private final boolean asc;

    public MOrderBy(MAttribute attr, boolean asc) {
        this.attr = attr;
        this.asc = asc;
    }

    public MAttribute getAttribute() {
        return this.attr;
    }

    public boolean isOrderAscending() {
        return this.asc;
    }

    public String toString() {
        return "order by " + this.attr + (this.asc ? " ascending" : " descending");
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.asc ? 1231 : 1237);
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
        MOrderBy other = (MOrderBy)obj;
        if (this.asc != other.asc) {
            return false;
        }
        return !(this.attr == null ? other.attr != null : !this.attr.equals(other.attr));
    }
}

