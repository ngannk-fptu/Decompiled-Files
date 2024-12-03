/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  net.sf.ldaptemplate.support.filter.EqualsFilter
 *  net.sf.ldaptemplate.support.filter.Filter
 */
package com.atlassian.user.impl.ldap.adaptor;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.ldap.LDAPEntity;
import com.atlassian.user.impl.ldap.LDAPGroupFactory;
import com.atlassian.user.impl.ldap.adaptor.AbstractLDAPGroupAdaptor;
import com.atlassian.user.impl.ldap.properties.LdapMembershipProperties;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.DefaultLDAPUserAdaptor;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.impl.ldap.search.LDAPUserAdaptor;
import com.atlassian.user.impl.ldap.search.LdapFilterFactory;
import com.atlassian.user.impl.ldap.search.page.LDAPEntityPager;
import com.atlassian.user.impl.ldap.search.page.LDAPMembershipToUsernamePager;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.util.LDAPUtils;
import com.atlassian.util.profiling.UtilTimerStack;
import net.sf.ldaptemplate.support.filter.EqualsFilter;
import net.sf.ldaptemplate.support.filter.Filter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LDAPStaticGroupAdaptor
extends AbstractLDAPGroupAdaptor {
    private final LDAPUserAdaptor userAdaptor;
    private final LdapMembershipProperties membershipProperties;

    public LDAPStaticGroupAdaptor(LdapContextFactory repository, LdapSearchProperties searchProperties, LDAPGroupFactory groupFactory, LdapFilterFactory filterFactory, LdapMembershipProperties membershipProperties) {
        super(repository, searchProperties, groupFactory, filterFactory);
        this.membershipProperties = membershipProperties;
        this.userAdaptor = new DefaultLDAPUserAdaptor(repository, searchProperties, filterFactory);
    }

    @Override
    public Pager<Group> getGroups(User user) throws EntityException {
        LDAPPagerInfo info = this.getGroupEntriesViaMembership(user);
        if (info == null) {
            return DefaultPager.emptyPager();
        }
        return new LDAPEntityPager<Group>(this.searchProperties, this.repository, this.groupFactory, info);
    }

    @Override
    public Pager<String> findMembers(Group group) throws EntityException {
        LDAPPagerInfo info = this.getGroupEntries(group.getName(), new String[]{this.membershipProperties.getMembershipAttribute()}, null);
        return new LDAPMembershipToUsernamePager(this.searchProperties, this.repository, info);
    }

    @Override
    public Pager<String> findMemberNames(Group group) throws EntityException {
        LDAPPagerInfo info = this.getGroupEntries(group.getName(), new String[]{this.membershipProperties.getMembershipAttribute()}, null);
        return new LDAPMembershipToUsernamePager(this.searchProperties, this.repository, info);
    }

    @Override
    public boolean hasStaticGroups() {
        return true;
    }

    @Override
    public boolean hasMembership(Group group, User user) throws RepositoryException {
        if (!(user instanceof LDAPEntity)) {
            return false;
        }
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_hasMembership(" + group + ", " + user + ")"));
        }
        Filter searchFilter = LDAPUtils.makeAndFilter((Filter)new EqualsFilter(this.searchProperties.getGroupnameAttribute(), group.getName()), (Filter)new EqualsFilter(this.membershipProperties.getMembershipAttribute(), this.getNameForMembershipComparison(user)));
        String[] attributesToReturn = new String[]{this.searchProperties.getGroupnameAttribute()};
        LDAPPagerInfo result = this.search(searchFilter, attributesToReturn);
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(this.getClass().getName() + "_hasMembership(" + group + ", " + user + ")"));
        }
        return result.getNamingEnumeration().hasMoreElements();
    }

    private String getNameForMembershipComparison(User user) {
        return this.membershipProperties.isMembershipAttributeUnqualified() ? user.getName() : ((LDAPEntity)((Object)user)).getDistinguishedName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public LDAPPagerInfo getGroupEntriesViaMembership(User user) throws EntityException {
        if (!(user instanceof LDAPEntity)) {
            this.log.info((Object)("Membership check for a non " + LDAPEntity.class.getName()));
            return this.getGroupEntriesViaMembership(user.getName());
        }
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_getLDAPGroupEntriesViaLDAPUser(" + user.getName() + ")"));
        }
        try {
            String[] attributesToReturn = new String[]{this.searchProperties.getGroupnameAttribute()};
            EqualsFilter searchTerm = new EqualsFilter(this.membershipProperties.getMembershipAttribute(), this.getNameForMembershipComparison(user));
            LDAPPagerInfo lDAPPagerInfo = this.getGroupEntries(attributesToReturn, (Filter)searchTerm);
            return lDAPPagerInfo;
        }
        finally {
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.pop((String)(this.getClass().getName() + "_getLDAPGroupEntriesViaLDAPUser(" + user.getName() + ")"));
            }
        }
    }

    @Override
    public LDAPPagerInfo getGroupEntriesViaMembership(String username) throws EntityException {
        return this.getGroupEntriesViaMembership(username, null);
    }

    @Override
    public LDAPPagerInfo getGroupEntriesViaMembership(String username, String[] attributesToReturn) throws EntityException {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_getLDAPGroupEntriesViaMembership(" + username + ")"));
        }
        try {
            if (attributesToReturn == null) {
                attributesToReturn = new String[]{this.searchProperties.getGroupnameAttribute()};
            }
            String name = this.membershipProperties.isMembershipAttributeUnqualified() ? username : this.userAdaptor.getUserDN(username);
            EqualsFilter searchTerm = new EqualsFilter(this.membershipProperties.getMembershipAttribute(), name);
            LDAPPagerInfo lDAPPagerInfo = this.getGroupEntries(attributesToReturn, (Filter)searchTerm);
            return lDAPPagerInfo;
        }
        catch (RepositoryException e) {
            throw new RepositoryException(e);
        }
        finally {
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.pop((String)(this.getClass().getName() + "_getLDAPGroupEntriesViaMembership(" + username + ")"));
            }
        }
    }
}

