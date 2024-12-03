/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.aggregator.Aggregator;
import net.sf.ehcache.search.parser.MAttribute;
import net.sf.ehcache.search.parser.ModelElement;

public class MAggregate
implements ModelElement<Aggregator> {
    private final AggOp op;
    private final MAttribute ma;

    public MAggregate(AggOp op, MAttribute ma) {
        this.op = op;
        this.ma = ma;
    }

    public String toString() {
        return this.op.toString().toLowerCase() + "(" + this.ma + ")";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.ma == null ? 0 : this.ma.hashCode());
        result = 31 * result + (this.op == null ? 0 : this.op.hashCode());
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
        MAggregate other = (MAggregate)obj;
        if (this.ma == null ? other.ma != null : !this.ma.equals(other.ma)) {
            return false;
        }
        return this.op == other.op;
    }

    public AggOp getOp() {
        return this.op;
    }

    public MAttribute getAttribute() {
        return this.ma;
    }

    @Override
    public Aggregator asEhcacheObject(ClassLoader loader) {
        switch (this.op) {
            case Sum: {
                return ((Attribute)this.ma.asEhcacheObject(loader)).sum();
            }
            case Min: {
                return ((Attribute)this.ma.asEhcacheObject(loader)).min();
            }
            case Max: {
                return ((Attribute)this.ma.asEhcacheObject(loader)).max();
            }
            case Count: {
                return ((Attribute)this.ma.asEhcacheObject(loader)).count();
            }
            case Average: {
                return ((Attribute)this.ma.asEhcacheObject(loader)).average();
            }
        }
        throw new IllegalStateException("Unknown agg operator: " + this.op);
    }

    public static enum AggOp {
        Sum,
        Min,
        Max,
        Average,
        Count;

    }
}

