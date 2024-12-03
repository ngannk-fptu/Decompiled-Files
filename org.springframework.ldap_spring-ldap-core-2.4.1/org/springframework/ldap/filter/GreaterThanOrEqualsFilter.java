/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

import org.springframework.ldap.filter.CompareFilter;

public class GreaterThanOrEqualsFilter
extends CompareFilter {
    private static final String GREATER_THAN_OR_EQUALS = ">=";

    public GreaterThanOrEqualsFilter(String attribute, String value) {
        super(attribute, value);
    }

    public GreaterThanOrEqualsFilter(String attribute, int value) {
        super(attribute, value);
    }

    @Override
    protected String getCompareString() {
        return GREATER_THAN_OR_EQUALS;
    }
}

