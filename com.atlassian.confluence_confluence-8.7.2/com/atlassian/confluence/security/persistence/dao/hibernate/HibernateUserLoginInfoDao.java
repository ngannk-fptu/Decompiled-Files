/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.security.persistence.dao.hibernate;

import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.ConfluenceLockerOnSiteRestore;
import com.atlassian.confluence.security.persistence.dao.UserLoginInfoDao;
import com.atlassian.confluence.security.persistence.dao.hibernate.UserLoginInfo;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;

public final class HibernateUserLoginInfoDao
implements UserLoginInfoDao {
    private static final Logger log = LoggerFactory.getLogger(HibernateUserLoginInfoDao.class);
    private final HibernateTemplate hibernateTemplate;

    public HibernateUserLoginInfoDao(SessionFactory sf) {
        this.hibernateTemplate = new HibernateTemplate(sf);
    }

    @Override
    public UserLoginInfo findOrCreateUserLoginInfoForUser(User user) {
        Preconditions.checkNotNull((Object)user, (Object)"User cannot be null");
        UserLoginInfo existingUserInfo = this.lookupLoginInfo(user);
        if (existingUserInfo != null) {
            return existingUserInfo;
        }
        ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
        if (confluenceUser == null) {
            throw new IllegalStateException("Could not create new UserLoginInfo. No ConfluenceUser found for " + user);
        }
        return new UserLoginInfo(confluenceUser);
    }

    @Override
    public void saveOrUpdate(UserLoginInfo loginAudit) {
        ConfluenceLockerOnSiteRestore.assertDatabaseIsNotLocked();
        this.hibernateTemplate.saveOrUpdate((Object)loginAudit);
    }

    @Override
    public void deleteUserInfoFor(User user) {
        ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
        if (confluenceUser != null) {
            List<UserLoginInfo> loginInfos = this.lookupAllLoginInfoRecords(confluenceUser);
            this.hibernateTemplate.deleteAll(loginInfos);
        } else {
            log.warn("Cannot delete UserLoginInfo. No ConfluenceUser was found for that {}", (Object)user);
        }
    }

    private @Nullable UserLoginInfo lookupLoginInfo(User user) {
        ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
        if (confluenceUser == null) {
            log.debug("Cannot lookup UserLoginInfo, no Confluenceuser found for {}", (Object)user);
            return null;
        }
        List<UserLoginInfo> loginInfos = this.lookupAllLoginInfoRecords(confluenceUser);
        if (log.isDebugEnabled() && loginInfos.size() > 1) {
            log.debug("There are {} UserLoginInfo entries for the user {}. We expect only one so the latest one will be used.", (Object)loginInfos.size(), (Object)user.getName());
        }
        if (loginInfos.isEmpty()) {
            return null;
        }
        return loginInfos.get(0);
    }

    private List<UserLoginInfo> lookupAllLoginInfoRecords(ConfluenceUser user) {
        Preconditions.checkNotNull((Object)user, (Object)"ConfluenceUser cannot be null");
        return (List)this.hibernateTemplate.execute(session -> {
            Query q = session.getNamedQuery("confluence.la_getAuditInfoByUsername");
            q.setCacheable(true);
            return q.setParameter("user", (Object)user).list();
        });
    }
}

