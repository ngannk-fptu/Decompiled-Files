/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ldaptemplate.support.filter.Filter
 */
package com.atlassian.user.impl.ldap.search;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;
import net.sf.ldaptemplate.support.filter.Filter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LDAPPagerInfo {
    private NamingEnumeration<SearchResult> enume;
    private Filter relatingSearchQuery;
    private String originalBaseSearchContext;
    private boolean searchAllDepths;
    private String[] returningAttributes;
    private final int timeToLive;

    public LDAPPagerInfo(NamingEnumeration<SearchResult> enume, Filter originalSearchQuery, String originalBaseSearchContext, boolean searchAllDepths, String[] returningAttributes, int timeToLive) {
        this.enume = enume;
        this.relatingSearchQuery = originalSearchQuery;
        this.originalBaseSearchContext = originalBaseSearchContext;
        this.searchAllDepths = searchAllDepths;
        this.returningAttributes = returningAttributes;
        this.timeToLive = timeToLive;
    }

    public NamingEnumeration<SearchResult> getNamingEnumeration() {
        return this.enume;
    }

    public Filter getLDAPQuery() {
        return this.relatingSearchQuery;
    }

    public String getBaseSearchContext() {
        return this.originalBaseSearchContext;
    }

    public void setLDAPQuery(Filter relatingQuery) {
        this.relatingSearchQuery = relatingQuery;
    }

    public boolean isSearchAllDepths() {
        return this.searchAllDepths;
    }

    public String[] getReturningAttributes() {
        return this.returningAttributes;
    }

    public int getTimeToLive() {
        return this.timeToLive;
    }
}

