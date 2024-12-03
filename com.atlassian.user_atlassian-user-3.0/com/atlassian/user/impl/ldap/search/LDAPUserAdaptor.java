/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ldaptemplate.support.filter.Filter
 */
package com.atlassian.user.impl.ldap.search;

import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import net.sf.ldaptemplate.support.filter.Filter;

public interface LDAPUserAdaptor {
    public LDAPPagerInfo search(Filter var1) throws EntityException;

    public LDAPPagerInfo search(Filter var1, String[] var2) throws EntityException;

    public LDAPPagerInfo getUserAttributes(String var1, String[] var2) throws EntityException;

    public String getUserDN(User var1) throws EntityException;

    public String getUserDN(String var1) throws EntityException;
}

