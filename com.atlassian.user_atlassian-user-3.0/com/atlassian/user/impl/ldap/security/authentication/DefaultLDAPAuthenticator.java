/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  net.sf.ldaptemplate.support.filter.AndFilter
 *  net.sf.ldaptemplate.support.filter.EqualsFilter
 *  net.sf.ldaptemplate.support.filter.Filter
 *  org.apache.commons.lang.StringUtils
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.ldap.security.authentication;

import com.atlassian.user.EntityException;
import com.atlassian.user.impl.ldap.properties.LdapConnectionProperties;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.DefaultLDAPUserAdaptor;
import com.atlassian.user.impl.ldap.search.LDAPUserAdaptor;
import com.atlassian.user.impl.ldap.search.LdapFilterFactory;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.security.authentication.Authenticator;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import net.sf.ldaptemplate.support.filter.AndFilter;
import net.sf.ldaptemplate.support.filter.EqualsFilter;
import net.sf.ldaptemplate.support.filter.Filter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class DefaultLDAPAuthenticator
implements Authenticator {
    private static final Logger log = Logger.getLogger(DefaultLDAPAuthenticator.class);
    private final LDAPUserAdaptor userAdaptor;
    private final LdapSearchProperties searchProperties;
    private final RepositoryIdentifier repositoryIdentifier;
    private final LdapConnectionProperties connectionProperties;
    private final LdapFilterFactory filterFactory;
    private final LdapContextFactory contextFactory;

    public DefaultLDAPAuthenticator(RepositoryIdentifier repositoryIdentifier, LdapContextFactory contextFactory, LdapSearchProperties searchProperties, LdapConnectionProperties connectionProperties, LdapFilterFactory filterFactory) {
        this.repositoryIdentifier = repositoryIdentifier;
        this.filterFactory = filterFactory;
        this.searchProperties = searchProperties;
        this.connectionProperties = connectionProperties;
        this.contextFactory = contextFactory;
        this.userAdaptor = new DefaultLDAPUserAdaptor(contextFactory, searchProperties, filterFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean authenticate(String username, String password) throws EntityException {
        String userDN;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_authenticate__" + username));
        }
        if (StringUtils.isEmpty((String)password)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Cannot perform authentication on empty passwords.");
            }
            return false;
        }
        Context authCtx = null;
        try {
            userDN = this.userAdaptor.getUserDN(username);
        }
        catch (EntityException e) {
            log.error((Object)("Could not construct DN to authenticate user: " + username), (Throwable)e);
            return false;
        }
        try {
            Hashtable authEnv = this.contextFactory.getAuthenticationJndiEnvironment(userDN, password);
            authCtx = new InitialDirContext(authEnv);
            SearchControls ctls = new SearchControls();
            ctls.setReturningAttributes(new String[]{this.searchProperties.getUsernameAttribute()});
            ctls.setSearchScope(2);
            AndFilter filter = new AndFilter();
            filter.and(this.filterFactory.getUserSearchFilter());
            filter.and((Filter)new EqualsFilter(this.searchProperties.getUsernameAttribute(), username));
            if (log.isDebugEnabled()) {
                log.debug((Object)("Doing initial search to complete authentication, username: '" + username + "', " + "base: '" + this.searchProperties.getBaseUserNamespace() + "' filter: '" + filter.encode() + "'"));
            }
            authCtx.search(this.searchProperties.getBaseUserNamespace(), filter.encode(), ctls);
        }
        catch (AuthenticationException e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("LDAP authentication failed, user: '" + username + "', constructed DN: '" + userDN + "'"), (Throwable)e);
            }
            boolean bl = false;
            return bl;
        }
        catch (NamingException e) {
            log.error((Object)("LDAP authentication error, user: '" + username + "', " + "constructed DN: '" + userDN + "', connectionProperties: " + this.connectionProperties), (Throwable)e);
            boolean bl = false;
            return bl;
        }
        catch (Throwable t) {
            log.error((Object)("Error occurred in LDAP authentication for username: " + username), t);
            boolean bl = false;
            return bl;
        }
        finally {
            try {
                if (authCtx != null) {
                    authCtx.close();
                }
            }
            catch (Exception e) {
                log.warn((Object)"Exception closing LDAP connection, possible resource leak", (Throwable)e);
            }
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.pop((String)(this.getClass().getName() + "_authenticate__" + username));
            }
        }
        return true;
    }

    public RepositoryIdentifier getRepository() {
        return this.repositoryIdentifier;
    }
}

