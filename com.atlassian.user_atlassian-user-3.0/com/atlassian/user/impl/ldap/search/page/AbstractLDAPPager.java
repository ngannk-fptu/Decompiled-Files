/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  net.sf.ldaptemplate.support.filter.Filter
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.ldap.search.page;

import com.atlassian.user.EntityException;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.search.page.AbstractPrefetchingPager;
import com.atlassian.user.util.EnumerationAdaptor;
import com.atlassian.user.util.LDAPUtils;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import net.sf.ldaptemplate.support.filter.Filter;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractLDAPPager<T>
extends AbstractPrefetchingPager<T>
implements Iterator<T> {
    protected final Logger log = Logger.getLogger(this.getClass());
    protected NamingEnumeration<SearchResult> enume;
    protected Filter originalQuery;
    protected LdapSearchProperties searchProperties;
    protected LdapContextFactory repository;
    protected String originalBaseSearchContext;
    protected boolean searchAllDepths;
    protected String[] returningAttributes;
    private int timeLimitMillis;
    public boolean closed;

    protected AbstractLDAPPager(LdapSearchProperties searchProperties, LdapContextFactory repository, LDAPPagerInfo info) {
        this.searchProperties = searchProperties;
        this.repository = repository;
        this.enume = info.getNamingEnumeration();
        this.originalQuery = info.getLDAPQuery();
        this.originalBaseSearchContext = info.getBaseSearchContext();
        this.searchAllDepths = info.isSearchAllDepths();
        this.returningAttributes = info.getReturningAttributes();
        this.timeLimitMillis = info.getTimeToLive();
    }

    public AbstractLDAPPager() {
    }

    protected abstract List<T> preloadSearchResult(SearchResult var1, List<T> var2) throws EntityException;

    @Override
    public void remove() {
        throw new UnsupportedOperationException("This iterator does not support removal.");
    }

    @Override
    protected void preload() {
        this.indexOfFirstItemInCurrentPage = this.idx;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_preload__(originalQuery= " + this.originalQuery + ")"));
        }
        DirContext ctx = null;
        try {
            if (this.closed) {
                ctx = this.repository.getLDAPContext();
                SearchControls ctls = LDAPUtils.createSearchControls(this.returningAttributes, this.searchAllDepths, this.timeLimitMillis);
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("AbstractLDAPPager.preload:" + this.originalQuery.encode()));
                }
                this.enume = ctx.search(this.originalBaseSearchContext, this.originalQuery.encode(), ctls);
            }
            super.preload(new EnumerationAdaptor<SearchResult>(this.enume));
        }
        catch (Exception e) {
            this.log.error((Object)"Error while paging through results", (Throwable)e);
            throw new RuntimeException("Unexpected error paging through LDAP results: " + e.getMessage(), e);
        }
        finally {
            if (this.enume != null) {
                try {
                    this.enume.close();
                }
                catch (NamingException e) {
                    this.log.error((Object)"Error closing enumeration.", (Throwable)e);
                }
            }
            if (ctx != null) {
                try {
                    ctx.close();
                }
                catch (NamingException e) {
                    this.log.error((Object)"Error closing context.", (Throwable)e);
                }
            }
            this.closed = true;
        }
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(this.getClass().getName() + "_preload__(originalQuery= " + this.originalQuery + ")"));
        }
    }

    @Override
    protected List<T> fetch(Object element, List<T> prefetched) throws EntityException {
        return this.preloadSearchResult((SearchResult)element, prefetched);
    }
}

