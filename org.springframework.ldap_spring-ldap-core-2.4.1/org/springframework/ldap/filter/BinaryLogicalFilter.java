/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.springframework.ldap.filter.AbstractFilter;
import org.springframework.ldap.filter.Filter;

public abstract class BinaryLogicalFilter
extends AbstractFilter {
    private List<Filter> queryList = new LinkedList<Filter>();

    @Override
    public StringBuffer encode(StringBuffer buff) {
        if (this.queryList.size() <= 0) {
            return buff;
        }
        if (this.queryList.size() == 1) {
            Filter query = this.queryList.get(0);
            return query.encode(buff);
        }
        buff.append("(").append(this.getLogicalOperator());
        for (Filter query : this.queryList) {
            query.encode(buff);
        }
        buff.append(")");
        return buff;
    }

    protected abstract String getLogicalOperator();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BinaryLogicalFilter that = (BinaryLogicalFilter)o;
        return !(this.queryList != null ? !this.queryList.equals(that.queryList) : that.queryList != null);
    }

    @Override
    public int hashCode() {
        return this.queryList != null ? this.queryList.hashCode() : 0;
    }

    public final BinaryLogicalFilter append(Filter query) {
        this.queryList.add(query);
        return this;
    }

    public final BinaryLogicalFilter appendAll(Collection<Filter> subQueries) {
        this.queryList.addAll(subQueries);
        return this;
    }
}

