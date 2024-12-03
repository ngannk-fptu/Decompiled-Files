/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Category
 */
package com.atlassian.user.impl.ldap.search.page;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.impl.ldap.LDAPEntityFactory;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.impl.ldap.search.page.AbstractLDAPPager;
import java.util.List;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import org.apache.log4j.Category;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LDAPEntityPager<T extends Entity>
extends AbstractLDAPPager<T> {
    public static final Category log = Category.getInstance(LDAPEntityPager.class);
    protected LDAPEntityFactory<? extends T> entityFactory;

    public LDAPEntityPager(LdapSearchProperties searchProperties, LdapContextFactory repository, LDAPEntityFactory<? extends T> entityFactory, LDAPPagerInfo info) {
        super(searchProperties, repository, info);
        this.entityFactory = entityFactory;
        this.preload();
    }

    @Override
    protected List<T> preloadSearchResult(SearchResult result, List<T> prefetched) throws EntityException {
        try {
            Attributes entityAttributes = result.getAttributes();
            T entity = this.entityFactory.getEntity(entityAttributes, result.getName());
            prefetched.add(entity);
        }
        catch (Throwable e) {
            log.error((Object)("There was an error converting the SearchResult: " + result + " into an entity or entities."), e);
        }
        return prefetched;
    }
}

