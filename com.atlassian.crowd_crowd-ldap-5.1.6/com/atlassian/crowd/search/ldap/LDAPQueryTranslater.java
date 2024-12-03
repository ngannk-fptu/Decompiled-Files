/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.search.ldap;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.search.ldap.LDAPQuery;
import com.atlassian.crowd.search.ldap.NullResultException;
import com.atlassian.crowd.search.query.entity.EntityQuery;

public interface LDAPQueryTranslater {
    public LDAPQuery asLDAPFilter(EntityQuery var1, LDAPPropertiesMapper var2) throws NullResultException;
}

