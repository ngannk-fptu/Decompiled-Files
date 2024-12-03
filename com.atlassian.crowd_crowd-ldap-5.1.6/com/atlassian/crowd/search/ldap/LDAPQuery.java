/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.filter.AndFilter
 *  org.springframework.ldap.filter.Filter
 *  org.springframework.ldap.filter.HardcodedFilter
 */
package com.atlassian.crowd.search.ldap;

import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;

public class LDAPQuery {
    private final AndFilter rootFilter = new AndFilter();

    public LDAPQuery(String objectFilter) {
        this.rootFilter.and((Filter)new HardcodedFilter(objectFilter));
    }

    public void addFilter(Filter filter) {
        this.rootFilter.append(filter);
    }

    public String encode() {
        return this.rootFilter.encode();
    }
}

