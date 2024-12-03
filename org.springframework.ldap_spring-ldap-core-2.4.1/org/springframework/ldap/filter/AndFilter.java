/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

import org.springframework.ldap.filter.BinaryLogicalFilter;
import org.springframework.ldap.filter.Filter;

public class AndFilter
extends BinaryLogicalFilter {
    private static final String AMPERSAND = "&";

    @Override
    protected String getLogicalOperator() {
        return AMPERSAND;
    }

    public AndFilter and(Filter query) {
        this.append(query);
        return this;
    }
}

