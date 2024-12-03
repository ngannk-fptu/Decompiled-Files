/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.filter;

import org.springframework.ldap.filter.AbstractFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.util.Assert;

public class NotFilter
extends AbstractFilter {
    private final Filter filter;

    public NotFilter(Filter filter) {
        Assert.notNull((Object)filter, (String)"Filter must not be null");
        this.filter = filter;
    }

    @Override
    public StringBuffer encode(StringBuffer buff) {
        buff.append("(!");
        this.filter.encode(buff);
        buff.append(')');
        return buff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NotFilter notFilter = (NotFilter)o;
        return !(this.filter != null ? !this.filter.equals(notFilter.filter) : notFilter.filter != null);
    }

    @Override
    public int hashCode() {
        return this.filter != null ? this.filter.hashCode() : 0;
    }
}

