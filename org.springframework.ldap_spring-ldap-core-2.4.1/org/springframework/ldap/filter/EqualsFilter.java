/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

import org.springframework.ldap.filter.CompareFilter;

public class EqualsFilter
extends CompareFilter {
    private static final String EQUALS_SIGN = "=";

    public EqualsFilter(String attribute, String value) {
        super(attribute, value);
    }

    public EqualsFilter(String attribute, int value) {
        super(attribute, value);
    }

    @Override
    protected String getCompareString() {
        return EQUALS_SIGN;
    }
}

