/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Category
 */
package com.atlassian.user.impl.ldap.search.page;

import com.atlassian.user.EntityException;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.impl.ldap.search.page.AbstractLDAPPager;
import java.util.List;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import org.apache.log4j.Category;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LDAPSingleStringPager
extends AbstractLDAPPager<String> {
    public static final Category log = Category.getInstance(LDAPSingleStringPager.class);

    public LDAPSingleStringPager(LdapSearchProperties searchProperties, LdapContextFactory repository, LDAPPagerInfo info) {
        super(searchProperties, repository, info);
        this.preload();
    }

    @Override
    protected List<String> preloadSearchResult(SearchResult result, List<String> prefetched) throws EntityException {
        try {
            Attributes entityAttributes = result.getAttributes();
            String attributeToFind = this.returningAttributes[0];
            Attribute attr = entityAttributes.get(attributeToFind);
            prefetched.add((String)attr.get());
        }
        catch (Throwable t) {
            log.error((Object)("Error converting search result: " + result + " into a string value."), t);
        }
        return prefetched;
    }
}

