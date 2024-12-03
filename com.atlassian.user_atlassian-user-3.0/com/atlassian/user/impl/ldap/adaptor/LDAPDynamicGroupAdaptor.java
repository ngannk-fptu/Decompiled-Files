/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ldaptemplate.support.filter.EqualsFilter
 *  net.sf.ldaptemplate.support.filter.Filter
 */
package com.atlassian.user.impl.ldap.adaptor;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.ldap.LDAPGroupFactory;
import com.atlassian.user.impl.ldap.LDAPUserFactory;
import com.atlassian.user.impl.ldap.adaptor.AbstractLDAPGroupAdaptor;
import com.atlassian.user.impl.ldap.properties.LdapMembershipProperties;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.DefaultLDAPUserAdaptor;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.impl.ldap.search.LDAPUserAdaptor;
import com.atlassian.user.impl.ldap.search.LdapFilterFactory;
import com.atlassian.user.impl.ldap.search.page.LDAPListOfGroupsPager;
import com.atlassian.user.impl.ldap.search.page.LDAPSingleStringPager;
import com.atlassian.user.search.page.Pager;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import net.sf.ldaptemplate.support.filter.EqualsFilter;
import net.sf.ldaptemplate.support.filter.Filter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LDAPDynamicGroupAdaptor
extends AbstractLDAPGroupAdaptor {
    private final LdapMembershipProperties membershipProperties;
    private final LDAPUserAdaptor userAdaptor;

    public LDAPDynamicGroupAdaptor(LdapContextFactory repo, LdapSearchProperties searchProperties, LDAPGroupFactory groupFactory, LdapFilterFactory filterFactory, LDAPUserFactory userFactory, LdapMembershipProperties membershipProperties) {
        super(repo, searchProperties, groupFactory, filterFactory);
        this.membershipProperties = membershipProperties;
        this.userAdaptor = new DefaultLDAPUserAdaptor(this.repository, searchProperties, filterFactory);
    }

    public LDAPDynamicGroupAdaptor(LdapContextFactory repo, LdapSearchProperties searchProperties, LDAPGroupFactory groupFactory, LdapFilterFactory filterFactory, LdapMembershipProperties membershipProperties) {
        super(repo, searchProperties, groupFactory, filterFactory);
        this.membershipProperties = membershipProperties;
        this.userAdaptor = new DefaultLDAPUserAdaptor(this.repository, searchProperties, filterFactory);
    }

    @Override
    public Pager<Group> getGroups(User user) throws EntityException {
        LDAPPagerInfo answer = this.getGroupEntriesViaMembership(user.getName(), new String[]{this.membershipProperties.getMembershipAttribute()});
        return new LDAPListOfGroupsPager(this.searchProperties, this.repository, this.groupFactory, answer);
    }

    private String getGroupsForUserSearchString(NamingEnumeration enume) throws RepositoryException {
        String query = null;
        while (enume.hasMoreElements()) {
            SearchResult result = (SearchResult)enume.nextElement();
            Attributes attrs = result.getAttributes();
            Attribute membershipAttribute = attrs.get(this.membershipProperties.getMembershipAttribute());
            if (membershipAttribute == null) continue;
            try {
                NamingEnumeration<?> groupList = membershipAttribute.getAll();
                while (groupList.hasMoreElements()) {
                    String groupDN = (String)groupList.nextElement();
                    if (query == null) {
                        query = "(" + groupDN.split(",")[0] + ")";
                        continue;
                    }
                    query = "(|" + query + "(" + groupDN.split(",")[0] + "))";
                }
            }
            catch (NamingException e) {
                throw new RepositoryException(e);
            }
        }
        return query;
    }

    @Override
    public Pager<String> findMembers(Group group) throws EntityException {
        return this.findMemberNames(group);
    }

    private LDAPPagerInfo findMembershipEntries(Group group, String[] attributesToReturn) throws EntityException {
        EqualsFilter searchFilter = new EqualsFilter(this.membershipProperties.getMembershipAttribute(), this.getGroupDN(group));
        LDAPPagerInfo ldapPagerInfo = null;
        try {
            ldapPagerInfo = attributesToReturn == null ? this.userAdaptor.search((Filter)searchFilter) : this.userAdaptor.search((Filter)searchFilter, attributesToReturn);
        }
        catch (EntityException e) {
            this.log.fatal((Object)("Could not find users in group [" + group.getName() + "] "), (Throwable)e);
        }
        return ldapPagerInfo;
    }

    @Override
    public Pager<String> findMemberNames(Group group) throws EntityException {
        LDAPPagerInfo ldapPagerInfo = this.findMembershipEntries(group, new String[]{this.searchProperties.getUsernameAttribute()});
        return new LDAPSingleStringPager(this.searchProperties, this.repository, ldapPagerInfo);
    }

    @Override
    public boolean hasStaticGroups() {
        return false;
    }

    @Override
    public boolean hasMembership(Group group, User user) throws EntityException {
        LDAPPagerInfo wrapper = this.userAdaptor.getUserAttributes(user.getName(), new String[]{this.membershipProperties.getMembershipAttribute()});
        NamingEnumeration<SearchResult> enume = wrapper.getNamingEnumeration();
        return this.getGroupsForUserSearchString(enume).indexOf(group.getName()) != -1;
    }

    @Override
    public LDAPPagerInfo getGroupEntriesViaMembership(User user) throws EntityException {
        return this.getGroupEntriesViaMembership(user.getName());
    }

    @Override
    public LDAPPagerInfo getGroupEntriesViaMembership(String username) throws EntityException {
        return this.getGroupEntriesViaMembership(username, null);
    }

    @Override
    public LDAPPagerInfo getGroupEntriesViaMembership(String username, String[] attributesToReturn) throws EntityException {
        if (attributesToReturn == null) {
            attributesToReturn = new String[]{this.searchProperties.getGroupnameAttribute(), this.membershipProperties.getMembershipAttribute()};
        }
        return this.userAdaptor.getUserAttributes(username, attributesToReturn);
    }
}

