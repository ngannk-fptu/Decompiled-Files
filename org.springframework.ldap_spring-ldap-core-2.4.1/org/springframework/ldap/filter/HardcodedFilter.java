/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.filter;

import org.springframework.ldap.filter.AbstractFilter;
import org.springframework.util.StringUtils;

public class HardcodedFilter
extends AbstractFilter {
    private String filter;

    public HardcodedFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public StringBuffer encode(StringBuffer buff) {
        if (!StringUtils.hasLength((String)this.filter)) {
            return buff;
        }
        buff.append(this.filter);
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
        HardcodedFilter that = (HardcodedFilter)o;
        return !(this.filter != null ? !this.filter.equals(that.filter) : that.filter != null);
    }

    @Override
    public int hashCode() {
        return this.filter != null ? this.filter.hashCode() : 0;
    }
}

