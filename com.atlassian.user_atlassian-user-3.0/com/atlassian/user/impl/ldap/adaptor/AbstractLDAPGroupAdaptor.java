/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.opensymphony.util.TextUtils
 *  net.sf.ldaptemplate.support.filter.EqualsFilter
 *  net.sf.ldaptemplate.support.filter.Filter
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.ldap.adaptor;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.impl.EntityMissingException;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.ldap.LDAPEntity;
import com.atlassian.user.impl.ldap.LDAPGroupFactory;
import com.atlassian.user.impl.ldap.adaptor.LDAPGroupAdaptor;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.impl.ldap.search.LdapFilterFactory;
import com.atlassian.user.impl.ldap.search.page.LDAPEntityPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.util.LDAPUtils;
import com.atlassian.util.profiling.UtilTimerStack;
import com.opensymphony.util.TextUtils;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import net.sf.ldaptemplate.support.filter.EqualsFilter;
import net.sf.ldaptemplate.support.filter.Filter;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractLDAPGroupAdaptor
implements LDAPGroupAdaptor {
    protected final Logger log = Logger.getLogger(this.getClass());
    protected final LdapSearchProperties searchProperties;
    private final LdapFilterFactory filterFactory;
    protected final LdapContextFactory repository;
    protected final LDAPGroupFactory groupFactory;

    protected AbstractLDAPGroupAdaptor(LdapContextFactory repo, LdapSearchProperties searchProperties, LDAPGroupFactory groupFactory, LdapFilterFactory filterFactory) {
        this.repository = repo;
        this.searchProperties = searchProperties;
        this.filterFactory = filterFactory;
        this.groupFactory = groupFactory;
    }

    @Override
    public Group getGroup(String name) throws EntityException {
        DirContext ctx = null;
        Group group = null;
        String filter = this.constructGroupSearchFilter(name).encode();
        String baseDn = this.searchProperties.getBaseGroupNamespace();
        try {
            NamingEnumeration<SearchResult> enume;
            ctx = this.repository.getLDAPContext();
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("AbstractLDAPGroupAdapter.getGroup:" + filter));
            }
            if ((enume = ctx.search(baseDn, filter, LDAPUtils.createSearchControls(new String[]{this.searchProperties.getGroupnameAttribute()}, this.searchProperties.isGroupSearchScopeAllDepths(), this.searchProperties.getTimeLimitMillis()))) == null) {
                Group group2 = null;
                return group2;
            }
            while (enume.hasMoreElements()) {
                SearchResult result = (SearchResult)enume.nextElement();
                Attributes attrs = result.getAttributes();
                group = this.groupFactory.getGroup(attrs, result.getName());
            }
        }
        catch (NamingException e) {
            String msg = "Exception when retrieving LDAP group {0} (base DN: {1}, filter: {2})";
            throw new RepositoryException(MessageFormat.format(msg, name, baseDn, filter), e);
        }
        finally {
            try {
                if (ctx != null) {
                    ctx.close();
                }
            }
            catch (NamingException e) {
                this.log.warn((Object)("Failed to close LDAP connection after search for group: " + name), (Throwable)e);
            }
        }
        return group;
    }

    @Override
    public Pager<Group> getGroups() throws EntityException {
        LDAPPagerInfo ldapPagerInfo = this.getGroupEntries();
        return new LDAPEntityPager<Group>(this.searchProperties, this.repository, this.groupFactory, ldapPagerInfo);
    }

    @Override
    public LDAPPagerInfo getGroupEntries() throws EntityException {
        return this.getGroupEntries("*");
    }

    @Override
    public LDAPPagerInfo getGroupEntries(String groupName) throws EntityException {
        return this.getGroupEntries(groupName, null, null);
    }

    @Override
    public LDAPPagerInfo getGroupEntries(String[] attributesToReturn, Filter additionalSearchFilter) throws EntityException {
        return this.getGroupEntries("*", attributesToReturn, additionalSearchFilter);
    }

    @Override
    public LDAPPagerInfo getGroupEntries(String groupName, String[] attributesToReturn, Filter additionalSearchFilter) throws RepositoryException {
        Filter searchFilter = this.constructGroupSearchFilter(groupName, additionalSearchFilter);
        return this.search(searchFilter, attributesToReturn);
    }

    @Override
    public LDAPPagerInfo search(Filter searchFilter) throws RepositoryException {
        return this.search(searchFilter, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LDAPPagerInfo search(Filter filter, String[] attributesToReturn) throws RepositoryException {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_search(" + filter + ")"));
        }
        try {
            if (attributesToReturn == null) {
                attributesToReturn = new String[]{this.searchProperties.getGroupnameAttribute()};
            }
            Filter groupSearchFilter = this.filterFactory.getGroupSearchFilter();
            if (filter != null) {
                groupSearchFilter = LDAPUtils.makeAndFilter(groupSearchFilter, filter);
            }
            SearchControls ctls = LDAPUtils.createSearchControls(attributesToReturn, this.searchProperties.isGroupSearchScopeAllDepths(), this.searchProperties.getTimeLimitMillis());
            NamingEnumeration<SearchResult> groupSearchEnume = null;
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.push((String)(this.getClass().getName() + "_search_JNDI_RAW_(" + groupSearchFilter + ")"));
            }
            DirContext ctx = null;
            try {
                ctx = this.repository.getLDAPContext();
                this.log.debug((Object)("Searching for groups using base name space:" + this.searchProperties.getBaseGroupNamespace() + " and encoded filter " + groupSearchFilter.encode()));
                groupSearchEnume = ctx.search(this.searchProperties.getBaseGroupNamespace(), groupSearchFilter.encode(), ctls);
                if (groupSearchEnume.hasMore()) {
                    this.log.debug((Object)"found at least one group");
                } else {
                    this.log.debug((Object)"no groups found");
                }
            }
            catch (NamingException e) {
                String host = "<unknown>";
                String ipAddress = "<unknown>";
                try {
                    String urlString = (String)this.repository.getJNDIEnv().get("java.naming.provider.url");
                    URI uri = new URI(urlString);
                    host = uri.getHost();
                    ipAddress = Inet4Address.getByName(host).getHostAddress();
                }
                catch (URISyntaxException use) {
                    this.log.debug((Object)"Error while retrieving LDAP server info", (Throwable)use);
                }
                catch (UnknownHostException uhe) {
                    this.log.debug((Object)"Error while retrieving LDAP server info", (Throwable)uhe);
                }
                this.log.error((Object)("Error searching for groups from LDAP server " + host + "[" + ipAddress + "]"));
                throw new RepositoryException(e);
            }
            finally {
                if (UtilTimerStack.isActive()) {
                    UtilTimerStack.pop((String)(this.getClass().getName() + "_search_JNDI_RAW_(" + groupSearchFilter + ")"));
                }
                try {
                    if (ctx != null) {
                        ctx.close();
                    }
                }
                catch (NamingException e) {
                    this.log.warn((Object)"Exception trying to close LDAP context, possible resource leak", (Throwable)e);
                }
            }
            LDAPPagerInfo lDAPPagerInfo = new LDAPPagerInfo(groupSearchEnume, groupSearchFilter, this.searchProperties.getBaseGroupNamespace(), this.searchProperties.isGroupSearchScopeAllDepths(), attributesToReturn, this.searchProperties.getTimeLimitMillis());
            return lDAPPagerInfo;
        }
        finally {
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.pop((String)(this.getClass().getName() + "_search(" + filter + ")"));
            }
        }
    }

    protected Filter constructGroupSearchFilter(String name) {
        return this.constructGroupSearchFilter(name, null);
    }

    protected Filter constructGroupSearchFilter(String name, Filter patternToAnd) {
        Filter searchFilter = null;
        if (TextUtils.stringSet((String)name) && !"*".equals(name)) {
            searchFilter = new EqualsFilter(this.searchProperties.getGroupnameAttribute(), name);
        }
        searchFilter = this.addGroupSearchFilter(searchFilter);
        if (patternToAnd != null) {
            return LDAPUtils.makeAndFilter(searchFilter, patternToAnd);
        }
        return searchFilter;
    }

    private Filter addGroupSearchFilter(Filter searchFilter) {
        return LDAPUtils.makeAndFilter(searchFilter, this.filterFactory.getGroupSearchFilter());
    }

    protected String getFirstPhraseFromDN(String dn) {
        String[] rdns = dn.split(",");
        String[] firstPhrase = rdns[0].split("=");
        return firstPhrase[1];
    }

    @Override
    public String getGroupDN(String groupName) throws EntityException {
        LDAPPagerInfo ldapPagerInfo = this.getGroupEntries(groupName, new String[]{"dn"}, null);
        if (!ldapPagerInfo.getNamingEnumeration().hasMoreElements()) {
            throw new EntityMissingException("Could not get DN for group [" + groupName + "]");
        }
        SearchResult result = (SearchResult)ldapPagerInfo.getNamingEnumeration().nextElement();
        String groupDN = result.getName();
        if (groupDN.indexOf(this.searchProperties.getBaseGroupNamespace()) == -1) {
            groupDN = groupDN + "," + this.searchProperties.getBaseGroupNamespace();
        }
        return groupDN;
    }

    @Override
    public String getGroupDN(Group group) throws EntityException {
        if (group instanceof LDAPEntity) {
            LDAPEntity entity = (LDAPEntity)((Object)group);
            return entity.getDistinguishedName();
        }
        throw new IllegalArgumentException("Group is not an LDAPEntity");
    }

    public LDAPGroupFactory getGroupFactory() {
        return this.groupFactory;
    }
}

