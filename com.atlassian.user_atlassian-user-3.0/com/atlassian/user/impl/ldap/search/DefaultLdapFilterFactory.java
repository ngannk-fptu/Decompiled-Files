/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ldaptemplate.support.filter.Filter
 */
package com.atlassian.user.impl.ldap.search;

import com.atlassian.user.impl.ldap.LiteralFilter;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.search.LdapFilterFactory;
import net.sf.ldaptemplate.support.filter.Filter;

public class DefaultLdapFilterFactory
implements LdapFilterFactory {
    private final LdapSearchProperties searchProperties;

    public DefaultLdapFilterFactory(LdapSearchProperties searchProperties) {
        this.searchProperties = searchProperties;
    }

    public Filter getGroupSearchFilter() {
        return new LiteralFilter(this.searchProperties.getGroupFilter());
    }

    public Filter getUserSearchFilter() {
        return new LiteralFilter(this.searchProperties.getUserFilter());
    }
}

