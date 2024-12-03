/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ldaptemplate.support.filter.Filter
 */
package com.atlassian.user.impl.ldap.adaptor;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.search.page.Pager;
import net.sf.ldaptemplate.support.filter.Filter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface LDAPGroupAdaptor {
    public Group getGroup(String var1) throws EntityException;

    public Pager<Group> getGroups(User var1) throws EntityException;

    public Pager<Group> getGroups() throws EntityException;

    public Pager<String> findMembers(Group var1) throws EntityException;

    public Pager<String> findMemberNames(Group var1) throws EntityException;

    public boolean hasStaticGroups();

    public String getGroupDN(Group var1) throws EntityException;

    public String getGroupDN(String var1) throws EntityException;

    public boolean hasMembership(Group var1, User var2) throws EntityException;

    public LDAPPagerInfo getGroupEntries() throws EntityException;

    public LDAPPagerInfo getGroupEntries(String var1) throws EntityException;

    public LDAPPagerInfo getGroupEntries(String[] var1, Filter var2) throws EntityException;

    public LDAPPagerInfo getGroupEntries(String var1, String[] var2, Filter var3) throws EntityException;

    public LDAPPagerInfo getGroupEntriesViaMembership(User var1) throws EntityException;

    public LDAPPagerInfo getGroupEntriesViaMembership(String var1) throws EntityException;

    public LDAPPagerInfo getGroupEntriesViaMembership(String var1, String[] var2) throws EntityException;

    public LDAPPagerInfo search(Filter var1) throws RepositoryException;
}

