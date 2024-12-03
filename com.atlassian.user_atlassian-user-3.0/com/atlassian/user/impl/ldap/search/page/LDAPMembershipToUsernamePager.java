/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.ldap.search.page;

import com.atlassian.user.EntityException;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.impl.ldap.search.page.LDAPSingleStringPager;
import com.atlassian.user.util.LDAPUtils;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LDAPMembershipToUsernamePager
extends LDAPSingleStringPager {
    private static final Logger log = Logger.getLogger(LDAPMembershipToUsernamePager.class);

    public LDAPMembershipToUsernamePager(LdapSearchProperties searchProperties, LdapContextFactory repository, LDAPPagerInfo info) {
        super(searchProperties, repository, info);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected List<String> preloadSearchResult(SearchResult result, List<String> prefetched) throws EntityException {
        DirContext ctx = null;
        try {
            Attributes entityAttributes = result.getAttributes();
            String attributeToFind = this.returningAttributes[0];
            Attribute attr = entityAttributes.get(attributeToFind);
            if (attr != null) {
                NamingEnumeration<?> interiorList = attr.getAll();
                ctx = this.repository.getLDAPContext();
                while (interiorList.hasMoreElements()) {
                    this.addIfFoundUser(prefetched, (String)interiorList.nextElement(), ctx);
                }
            }
        }
        catch (Throwable t) {
            log.error((Object)("Error converting search result: " + result + " into list of members as usernames."), t);
        }
        finally {
            this.closeContext(ctx);
        }
        return prefetched;
    }

    private void addIfFoundUser(List<String> prefetched, String dn, DirContext ctx) {
        try {
            String username = this.findByDN(dn, ctx);
            if (username != null) {
                prefetched.add(username);
            }
        }
        catch (RepositoryException e) {
            log.error((Object)("Error resolving dn [ " + dn + " ] to a username"), (Throwable)e);
        }
    }

    private String findByDN(String dn, DirContext ctx) throws RepositoryException {
        String usernameAttribure = this.searchProperties.getUsernameAttribute();
        SearchControls ctls = LDAPUtils.createSearchControls(new String[]{usernameAttribure}, true, this.searchProperties.getTimeLimitMillis());
        try {
            SearchResult sr;
            NamingEnumeration<SearchResult> userSearchEnum;
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.push((String)(this.getClass().getName() + "_search_JNDI_RAW_" + this.searchProperties.getUserFilter()));
            }
            if ((userSearchEnum = ctx.search(dn, this.searchProperties.getUserFilter(), ctls)).hasMoreElements() && (sr = userSearchEnum.next()) != null && sr.getAttributes() != null && sr.getAttributes().get(usernameAttribure) != null) {
                String username = (String)sr.getAttributes().get(usernameAttribure).get();
                if (log.isDebugEnabled()) {
                    log.debug((Object)("LDAPMembershipToUse.findByDN [ " + dn + " ] username [ " + username + " ]"));
                }
                String string = username;
                return string;
            }
            String string = null;
            return string;
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
        finally {
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.pop((String)(this.getClass().getName() + "_search_JNDI_RAW_" + this.searchProperties.getUserFilter()));
            }
        }
    }

    private void closeContext(DirContext ctx) {
        try {
            if (ctx != null) {
                ctx.close();
            }
        }
        catch (NamingException e) {
            log.warn((Object)"Exception closing context", (Throwable)e);
        }
    }
}

