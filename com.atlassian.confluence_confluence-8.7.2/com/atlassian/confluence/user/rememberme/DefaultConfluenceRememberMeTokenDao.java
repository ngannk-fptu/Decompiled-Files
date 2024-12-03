/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.service.rememberme.RememberMeToken
 *  com.atlassian.seraph.spi.rememberme.RememberMeConfiguration
 *  org.hibernate.SessionFactory
 *  org.hibernate.type.LongType
 *  org.hibernate.type.StringType
 *  org.hibernate.type.Type
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.user.rememberme;

import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.user.persistence.dao.ConfluenceRememberMeToken;
import com.atlassian.confluence.user.rememberme.ConfluenceRememberMeTokenDao;
import com.atlassian.seraph.service.rememberme.RememberMeToken;
import com.atlassian.seraph.spi.rememberme.RememberMeConfiguration;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DefaultConfluenceRememberMeTokenDao
implements ConfluenceRememberMeTokenDao {
    private final HibernateTemplate hibernateTemplate;
    private final RememberMeConfiguration rememberMeConfiguration;

    public DefaultConfluenceRememberMeTokenDao(SessionFactory sessionFactory, RememberMeConfiguration rememberMeConfiguration) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.rememberMeConfiguration = rememberMeConfiguration;
    }

    @Override
    public RememberMeToken findById(Long tokenId) {
        return tokenId == null ? null : (RememberMeToken)this.hibernateTemplate.get(ConfluenceRememberMeToken.class, (Serializable)tokenId);
    }

    @Override
    public List<RememberMeToken> findForUserName(String username) {
        if (username == null) {
            return Collections.emptyList();
        }
        return (List)this.hibernateTemplate.execute(session -> session.createQuery("from ConfluenceRememberMeToken where username = :username").setParameter("username", (Object)username, (Type)StringType.INSTANCE).list());
    }

    @Override
    public void remove(Long tokenId) {
        if (tokenId != null) {
            this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from ConfluenceRememberMeToken where id = :tokenId", new Object[]{tokenId}, new Type[]{LongType.INSTANCE}));
        }
    }

    @Override
    public void removeAll() {
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from ConfluenceRememberMeToken", new Object[0], new Type[0]));
    }

    @Override
    public void removeAllForUser(String username) {
        if (username != null) {
            this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from ConfluenceRememberMeToken where username = :username", new Object[]{username}, new Type[]{StringType.INSTANCE}));
        }
    }

    @Override
    public RememberMeToken save(RememberMeToken token) {
        if (token == null) {
            return null;
        }
        ConfluenceRememberMeToken confluenceRememberMeToken = new ConfluenceRememberMeToken(token);
        this.hibernateTemplate.execute(session -> session.save((Object)confluenceRememberMeToken));
        return confluenceRememberMeToken;
    }

    @Override
    public void removeExpiredTokens() {
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from ConfluenceRememberMeToken where created < :expiredCutoff", new Object[]{System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(this.rememberMeConfiguration.getCookieMaxAgeInSeconds())}, new Type[]{LongType.INSTANCE}));
    }
}

