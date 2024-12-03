/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  net.sf.ldaptemplate.support.filter.EqualsFilter
 *  net.sf.ldaptemplate.support.filter.Filter
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.ldap.search;

import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.ldap.LDAPEntity;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.impl.ldap.search.LDAPUserAdaptor;
import com.atlassian.user.impl.ldap.search.LdapFilterFactory;
import com.atlassian.user.util.LDAPUtils;
import com.atlassian.util.profiling.UtilTimerStack;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import net.sf.ldaptemplate.support.filter.EqualsFilter;
import net.sf.ldaptemplate.support.filter.Filter;
import org.apache.log4j.Logger;

public class DefaultLDAPUserAdaptor
implements LDAPUserAdaptor {
    protected final Logger log = Logger.getLogger(this.getClass());
    private final LdapContextFactory repository;
    private final LdapSearchProperties searchProperties;
    private final LdapFilterFactory filterFactory;

    public DefaultLDAPUserAdaptor(LdapContextFactory repository, LdapSearchProperties searchProperties, LdapFilterFactory filterFactory) {
        this.filterFactory = filterFactory;
        this.searchProperties = searchProperties;
        this.repository = repository;
    }

    private String[] getDefaultAttributes() {
        String[] defaultAttributesToReturn = new String[]{this.searchProperties.getUsernameAttribute(), this.searchProperties.getFirstnameAttribute(), this.searchProperties.getSurnameAttribute(), this.searchProperties.getEmailAttribute()};
        return defaultAttributesToReturn;
    }

    public LDAPPagerInfo search(Filter searchFilter) throws RepositoryException {
        return this.search(searchFilter, this.getDefaultAttributes());
    }

    public LDAPPagerInfo search(Filter userFilter, String[] attributesToReturnFromSearch) throws RepositoryException {
        DirContext ctx = null;
        SearchControls ctls = LDAPUtils.createSearchControls(attributesToReturnFromSearch, this.searchProperties.isUserSearchScopeAllDepths(), this.searchProperties.getTimeLimitMillis());
        Filter filter = LDAPUtils.makeAndFilter(this.filterFactory.getUserSearchFilter(), userFilter);
        try {
            ctx = this.repository.getLDAPContext();
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.push((String)(this.getClass().getName() + "_search_JNDI_RAW_" + filter));
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("DefaultLDAPUserAdapter.search:" + filter.encode()));
            }
            NamingEnumeration<SearchResult> userSearchEnume = ctx.search(this.searchProperties.getBaseUserNamespace(), filter.encode(), ctls);
            LDAPPagerInfo lDAPPagerInfo = new LDAPPagerInfo(userSearchEnume, filter, this.searchProperties.getBaseUserNamespace(), this.searchProperties.isUserSearchScopeAllDepths(), attributesToReturnFromSearch, this.searchProperties.getTimeLimitMillis());
            return lDAPPagerInfo;
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
        finally {
            try {
                if (ctx != null) {
                    ctx.close();
                }
            }
            catch (NamingException e) {
                this.log.warn((Object)"Exception closing context", (Throwable)e);
            }
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.pop((String)(this.getClass().getName() + "_search_JNDI_RAW_" + filter));
            }
        }
    }

    public LDAPPagerInfo getUserAttributes(String username, String[] specifiedAttributes) throws RepositoryException {
        EqualsFilter searchFilter = new EqualsFilter(this.searchProperties.getUsernameAttribute(), username);
        return this.search(LDAPUtils.makeAndFilter(this.filterFactory.getUserSearchFilter(), (Filter)searchFilter), specifiedAttributes);
    }

    public String getUserDN(User user) throws EntityException {
        if (user instanceof LDAPEntity) {
            LDAPEntity entity = (LDAPEntity)((Object)user);
            return entity.getDistinguishedName();
        }
        return this.getUserDN(user.getName());
    }

    public String getUserDN(String username) throws EntityException {
        LDAPPagerInfo ldapPagerInfo;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_getUserDN(" + username + ")"));
        }
        if ((ldapPagerInfo = this.getUserAttributes(username, this.getDefaultAttributes())).getNamingEnumeration().hasMoreElements()) {
            SearchResult result = (SearchResult)ldapPagerInfo.getNamingEnumeration().nextElement();
            String userDN = result.getName();
            if (userDN.startsWith("\"") && userDN.endsWith("\"")) {
                userDN = userDN.substring(1, userDN.length() - 1);
            }
            if (userDN.indexOf(this.searchProperties.getBaseUserNamespace()) == -1) {
                userDN = userDN + "," + this.searchProperties.getBaseUserNamespace();
            }
            try {
                ldapPagerInfo.getNamingEnumeration().close();
            }
            catch (NamingException e) {
                throw new EntityException(e);
            }
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.pop((String)(this.getClass().getName() + "_getUserDN(" + username + ")"));
            }
            return userDN;
        }
        throw new RepositoryException("Could not locate a DN for user [" + username + "]");
    }

    public StringBuffer addSearchTermToFilter(StringBuffer firstTerm, String addedFilter) {
        firstTerm = firstTerm != null ? (addedFilter.indexOf("(") == 0 && addedFilter.lastIndexOf(")") == addedFilter.length() - 1 ? new StringBuffer("(&" + firstTerm + addedFilter + ")") : new StringBuffer("(&" + firstTerm + "(" + addedFilter + "))")) : new StringBuffer(addedFilter);
        return firstTerm;
    }
}

