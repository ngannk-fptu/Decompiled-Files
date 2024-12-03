/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Category
 */
package com.atlassian.user.impl.ldap.search.page;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.impl.ldap.LDAPEntityFactory;
import com.atlassian.user.impl.ldap.LDAPGroupFactory;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.LDAPPagerInfo;
import com.atlassian.user.impl.ldap.search.page.LDAPEntityPager;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import org.apache.log4j.Category;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LDAPListOfGroupsPager
extends LDAPEntityPager<Group> {
    public static final Category log = Category.getInstance(LDAPListOfGroupsPager.class);

    public LDAPListOfGroupsPager(LdapSearchProperties searchProperties, LdapContextFactory contextFactory, LDAPEntityFactory<? extends Group> groupFactory, LDAPPagerInfo info) {
        super(searchProperties, contextFactory, groupFactory, info);
    }

    @Override
    protected List<Group> preloadSearchResult(SearchResult result, List<Group> prefetched) throws EntityException {
        try {
            Attributes entityAttributes = result.getAttributes();
            Attribute listOfEntitiesAttribute = entityAttributes.get(this.returningAttributes[0]);
            NamingEnumeration<?> listOfEntitiesEnumeration = listOfEntitiesAttribute.getAll();
            while (listOfEntitiesEnumeration.hasMoreElements()) {
                String groupDN = (String)listOfEntitiesEnumeration.nextElement();
                log.debug((Object)("got group dn '" + groupDN + "' from pager"));
                try {
                    Group group = ((LDAPGroupFactory)this.entityFactory).getGroup(groupDN);
                    prefetched.add(group);
                }
                catch (Exception e) {
                    log.error((Object)("Error converting DN: " + groupDN + " into a group entity."), (Throwable)e);
                }
            }
        }
        catch (Throwable e) {
            log.error((Object)("Could not covnert search result: " + result + " into a list of groups."), e);
        }
        return prefetched;
    }
}

