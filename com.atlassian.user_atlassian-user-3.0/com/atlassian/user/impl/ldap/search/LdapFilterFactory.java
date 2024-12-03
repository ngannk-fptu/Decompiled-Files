/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ldaptemplate.support.filter.Filter
 */
package com.atlassian.user.impl.ldap.search;

import net.sf.ldaptemplate.support.filter.Filter;

public interface LdapFilterFactory {
    public Filter getGroupSearchFilter();

    public Filter getUserSearchFilter();
}

