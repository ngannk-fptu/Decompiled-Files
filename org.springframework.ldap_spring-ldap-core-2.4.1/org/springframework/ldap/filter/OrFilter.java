/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

import org.springframework.ldap.filter.BinaryLogicalFilter;
import org.springframework.ldap.filter.Filter;

public class OrFilter
extends BinaryLogicalFilter {
    private static final String PIPE_SIGN = "|";

    public OrFilter or(Filter query) {
        this.append(query);
        return this;
    }

    @Override
    protected String getLogicalOperator() {
        return PIPE_SIGN;
    }
}

