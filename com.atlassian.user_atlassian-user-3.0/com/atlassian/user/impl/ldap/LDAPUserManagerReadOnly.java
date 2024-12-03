/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  net.sf.ldaptemplate.support.filter.EqualsFilter
 *  net.sf.ldaptemplate.support.filter.Filter
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.ldap;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.impl.ReadOnlyUserManager;
import com.atlassian.user.impl.ldap.DefaultLDAPUserFactory;
import com.atlassian.user.impl.ldap.LDAPValidator;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.DefaultLDAPUserAdaptor;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.impl.ldap.search.LDAPUserAdaptor;
import com.atlassian.user.impl.ldap.search.LdapFilterFactory;
import com.atlassian.user.impl.ldap.search.page.LDAPEntityPager;
import com.atlassian.user.impl.ldap.search.page.LDAPSingleStringPager;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.page.Pager;
import com.atlassian.util.profiling.UtilTimerStack;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import net.sf.ldaptemplate.support.filter.EqualsFilter;
import net.sf.ldaptemplate.support.filter.Filter;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LDAPUserManagerReadOnly
extends ReadOnlyUserManager {
    protected final Logger log = Logger.getLogger(this.getClass());
    private final RepositoryIdentifier repositoryIdentifier;
    private final LdapContextFactory repository;
    private final LdapSearchProperties searchProperties;
    private final LDAPUserAdaptor userAdaptor;

    public LDAPUserManagerReadOnly(RepositoryIdentifier repositoryIdentifier, LdapContextFactory repository, LdapSearchProperties searchProperties, LdapFilterFactory filterFactory) {
        this.repositoryIdentifier = repositoryIdentifier;
        this.repository = repository;
        this.searchProperties = searchProperties;
        this.userAdaptor = new DefaultLDAPUserAdaptor(repository, searchProperties, filterFactory);
    }

    @Override
    public Pager<User> getUsers() throws EntityException {
        this.profilePush(this.getClass().getName() + "_getUsers");
        LDAPPagerInfo info = this.userAdaptor.search(null);
        LDAPEntityPager<User> pager = new LDAPEntityPager<User>(this.searchProperties, this.repository, new DefaultLDAPUserFactory(this.searchProperties), info);
        this.profilePop(this.getClass().getName() + "_getUsers");
        return pager;
    }

    @Override
    public Pager<String> getUserNames() throws EntityException {
        LDAPPagerInfo info = this.userAdaptor.search(null, new String[]{this.searchProperties.getUsernameAttribute()});
        return new LDAPSingleStringPager(this.searchProperties, this.repository, info);
    }

    @Override
    public User getUser(String username) throws EntityException {
        this.profilePush(this.getClass().getName() + "_getUser(" + username + ")");
        User user = null;
        try {
            LDAPPagerInfo info = this.userAdaptor.search((Filter)new EqualsFilter(this.searchProperties.getUsernameAttribute(), username));
            LDAPEntityPager<User> pager = new LDAPEntityPager<User>(this.searchProperties, this.repository, new DefaultLDAPUserFactory(this.searchProperties), info);
            if (pager.getCurrentPage().size() > 0) {
                user = (User)pager.getCurrentPage().get(0);
            }
        }
        catch (EntityException e) {
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
            this.log.error((Object)("Error retrieving user: '" + username + "' from LDAP server " + host + "[" + ipAddress + "]"), (Throwable)e);
        }
        this.profilePop(this.getClass().getName() + "_getUser(" + username + ")");
        return user;
    }

    @Override
    public RepositoryIdentifier getIdentifier() {
        return this.repositoryIdentifier;
    }

    @Override
    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        if (!LDAPValidator.validateLDAPEntity(entity)) {
            return null;
        }
        if (this.getUser(entity.getName()) == null) {
            return null;
        }
        return this.repositoryIdentifier;
    }

    private void profilePush(String key) {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)key);
        }
    }

    private void profilePop(String key) {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)key);
        }
    }
}

