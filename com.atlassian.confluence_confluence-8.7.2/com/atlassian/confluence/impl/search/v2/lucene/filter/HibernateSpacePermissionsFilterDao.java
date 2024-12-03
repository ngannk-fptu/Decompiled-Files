/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.search.v2.lucene.filter;

import com.atlassian.confluence.impl.search.v2.lucene.filter.SpacePermissionsFilterDao;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.persistence.dao.SpacePermissionDao;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class HibernateSpacePermissionsFilterDao
implements SpacePermissionsFilterDao {
    private final HibernateTemplate hibernateTemplate;
    private final SpacePermissionDao spacePermissionDao;
    private final UserAccessor userAccessor;
    private static final String ALL_SPACE_KEYS_QUERY = "select s.key from Space s";
    private static final String PERMITTED_SPACE_KEYS_QUERY_START = "select distinct space.key from SpacePermission as perm inner join perm.space as space where ";
    private static final String UNPERMITTED_SPACE_KEYS_QUERY_START = "select s.key from Space as s where s.id not in ( select distinct space.id from SpacePermission as perm inner join perm.space as space where ";
    private static final String UNPERMITTED_SPACE_KEYS_QUERY_END = ")";
    private static final SpacePermission ANONYMOUS_USE_PERMISSION = SpacePermission.createAnonymousSpacePermission("USECONFLUENCE", null);

    public HibernateSpacePermissionsFilterDao(SessionFactory sessionFactory, SpacePermissionDao spacePermissionDao, UserAccessor userAccessor) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.spacePermissionDao = spacePermissionDao;
        this.userAccessor = userAccessor;
    }

    @Override
    public List<String> getPermittedSpaceKeys(SpacePermissionQueryBuilder userPermissionQueryBuilder) {
        if (this.userHasNoUsername(userPermissionQueryBuilder.getUser())) {
            return Collections.emptyList();
        }
        return (List)this.hibernateTemplate.executeWithNativeSession(session -> {
            String hqlQuery = this.getPermittedSpaceKeysHqlQuery(userPermissionQueryBuilder);
            Query query = session.createQuery(hqlQuery);
            userPermissionQueryBuilder.substituteHqlQueryParameters(query);
            query.setCacheable(true);
            return query.list();
        });
    }

    @Override
    public List<String> getUnPermittedSpaceKeys(SpacePermissionQueryBuilder userPermissionQueryBuilder) {
        if (this.userHasNoUsername(userPermissionQueryBuilder.getUser())) {
            return this.getAllSpaceKeys();
        }
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = this.getUnPermittedSpaceKeysHqlQuery(userPermissionQueryBuilder);
            Query query = session.createQuery(hqlQuery);
            userPermissionQueryBuilder.substituteHqlQueryParameters(query);
            query.setCacheable(true);
            return query.list();
        });
    }

    private boolean userHasNoUsername(User user) {
        return user != null && StringUtils.isBlank((CharSequence)user.getName());
    }

    private List getAllSpaceKeys() {
        return (List)this.hibernateTemplate.execute(session -> {
            Query query = session.createQuery(ALL_SPACE_KEYS_QUERY);
            query.setCacheable(true);
            return query.list();
        });
    }

    private String getPermittedSpaceKeysHqlQuery(SpacePermissionQueryBuilder userPermissionQueryBuilder) {
        return PERMITTED_SPACE_KEYS_QUERY_START + userPermissionQueryBuilder.getHqlPermissionFilterString("perm");
    }

    private String getUnPermittedSpaceKeysHqlQuery(SpacePermissionQueryBuilder userPermissionQueryBuilder) {
        return UNPERMITTED_SPACE_KEYS_QUERY_START + userPermissionQueryBuilder.getHqlPermissionFilterString("perm") + UNPERMITTED_SPACE_KEYS_QUERY_END;
    }

    @Override
    @Deprecated
    public List<String> getPermittedSpaceKeysForUser(User user) {
        if (this.userHasNoUsername(user) || this.userIsAnonymousAndGlobalAnonymousAccessDisabled(user)) {
            return Collections.EMPTY_LIST;
        }
        ConfluenceUser confluenceUser = this.getConfluenceUser(user);
        return (List)this.hibernateTemplate.executeWithNativeSession(session -> this.getRequiredQuery(confluenceUser, session).list());
    }

    private boolean userIsAnonymousAndGlobalAnonymousAccessDisabled(User user) {
        return user == null && !this.spacePermissionDao.hasPermission(ANONYMOUS_USE_PERMISSION);
    }

    private Query getRequiredQuery(ConfluenceUser user, Session session) {
        Query query;
        if (user != null) {
            List<String> userGroups = this.userAccessor.getGroupNamesForUserName(user.getName());
            if (userGroups.isEmpty()) {
                query = session.getNamedQuery("confluence.spacePermissionsFilter.getPermittedSpaceKeysForUserNotInAnyGroup");
            } else {
                query = session.getNamedQuery("confluence.spacePermissionsFilter.getPermittedSpaceKeysForUser");
                query.setParameterList("groups", HibernateSpacePermissionsFilterDao.filterBlanks(userGroups));
            }
            query.setParameter("user", (Object)user);
        } else {
            query = session.getNamedQuery("confluence.spacePermissionsFilter.getPermittedSpaceKeysForAnonymousUser");
        }
        query.setParameter("permission", (Object)"VIEWSPACE");
        query.setCacheable(true);
        return query;
    }

    private ConfluenceUser getConfluenceUser(User user) {
        if (user == null || user.getName() == null) {
            return null;
        }
        if (user instanceof ConfluenceUser) {
            return (ConfluenceUser)user;
        }
        return this.userAccessor.getUserByName(user.getName());
    }

    private static List<String> filterBlanks(List<String> values) {
        ArrayList<String> result = new ArrayList<String>(values.size());
        for (String s : values) {
            if (!StringUtils.isNotBlank((CharSequence)s)) continue;
            result.add(s);
        }
        return result;
    }
}

