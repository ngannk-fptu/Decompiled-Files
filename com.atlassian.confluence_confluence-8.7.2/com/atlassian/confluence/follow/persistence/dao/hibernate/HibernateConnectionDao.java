/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.user.User
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.hibernate.type.Type
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.follow.persistence.dao.hibernate;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.follow.Connection;
import com.atlassian.confluence.follow.persistence.dao.ConnectionDao;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.user.User;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class HibernateConnectionDao
implements ConnectionDao {
    private final HibernateTemplate hibernateTemplate;

    public HibernateConnectionDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public PageResponse<ConfluenceUser> getFilteredFollowers(ConfluenceUser followee, LimitedRequest limitedRequest, Predicate<ConfluenceUser> predicate) {
        List results = (List)this.hibernateTemplate.execute(session -> {
            Query query = session.getNamedQuery("confluence.follow_getFollowers");
            query.setMaxResults(limitedRequest.getLimit() + 1);
            query.setFirstResult(limitedRequest.getStart());
            query.setParameter("followee", (Object)followee);
            query.setCacheable(true);
            return query.list();
        });
        return PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, (List)results, predicate);
    }

    @Override
    public PageResponse<ConfluenceUser> getFilteredFollowees(ConfluenceUser follower, LimitedRequest limitedRequest, Predicate<ConfluenceUser> predicate) {
        List results = (List)this.hibernateTemplate.execute(session -> {
            Query query = session.getNamedQuery("confluence.follow_getFollowees");
            query.setMaxResults(limitedRequest.getLimit() + 1);
            query.setFirstResult(limitedRequest.getStart());
            query.setParameter("follower", (Object)follower);
            query.setCacheable(true);
            return query.list();
        });
        return PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, (List)results, predicate);
    }

    @Override
    public boolean isUserFollowing(User follower, User followee) {
        return this.getConnection(follower, followee) != null;
    }

    private Connection getConnection(User follower, User followee) {
        ConfluenceUser followerUser = FindUserHelper.getUser(follower);
        ConfluenceUser followeeUser = FindUserHelper.getUser(followee);
        if (followerUser == null || followeeUser == null) {
            return null;
        }
        List result = Objects.requireNonNull((List)this.hibernateTemplate.execute(session -> {
            Query query = session.getNamedQuery("confluence.follow_getByFollowerAndFollowee");
            query.setParameter("follower", (Object)followerUser);
            query.setParameter("followee", (Object)followeeUser);
            query.setCacheable(true);
            return query.list();
        }));
        if (result.size() > 0) {
            return (Connection)result.iterator().next();
        }
        return null;
    }

    @Override
    public void followUser(User follower, User followee) {
        ConfluenceUser followerUser = FindUserHelper.getUser(follower);
        ConfluenceUser followeeUser = FindUserHelper.getUser(followee);
        if (followerUser == null || followeeUser == null) {
            return;
        }
        if (this.getConnection(followerUser, followeeUser) == null) {
            this.hibernateTemplate.save((Object)new Connection(followerUser, followeeUser));
        }
    }

    @Override
    public void unfollowUser(User follower, User followee) {
        Connection connection = this.getConnection(follower, followee);
        if (connection != null) {
            this.hibernateTemplate.delete((Object)connection);
        }
    }

    @Override
    public void removeAllConnectionsFor(User user) {
        ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
        if (confluenceUser == null) {
            return;
        }
        SessionFactory sessionFactory = Objects.requireNonNull(this.hibernateTemplate.getSessionFactory());
        Type userType = sessionFactory.getTypeHelper().entity(ConfluenceUserImpl.class);
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from Connection c where c.follower = :follower or c.followee = :followee", new ConfluenceUser[]{confluenceUser, confluenceUser}, new Type[]{userType, userType}));
    }
}

