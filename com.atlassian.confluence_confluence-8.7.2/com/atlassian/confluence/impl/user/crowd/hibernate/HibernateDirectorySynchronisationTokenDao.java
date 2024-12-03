/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DirectorySynchronisationToken
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.embedded.spi.DirectorySynchronisationTokenDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.annotation.Nullable
 *  javax.persistence.criteria.CriteriaBuilder
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Root
 *  javax.persistence.criteria.Selection
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.crowd.directory.DirectorySynchronisationToken;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.embedded.spi.DirectorySynchronisationTokenDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public final class HibernateDirectorySynchronisationTokenDao
implements DirectorySynchronisationTokenDao {
    private final HibernateTemplate hibernateTemplate;
    private final TransactionTemplate transactionTemplate;
    private final DirectoryDao directoryDao;

    public HibernateDirectorySynchronisationTokenDao(SessionFactory sessionFactory, TransactionTemplate transactionTemplate, DirectoryDao directoryDao) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.transactionTemplate = transactionTemplate;
        this.directoryDao = directoryDao;
    }

    @Nullable
    public String getLastSynchronisationTokenForDirectory(long directoryId) {
        return (String)DataAccessUtils.singleResult((Collection)((Collection)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(String.class);
            Root token = query.from(DirectorySynchronisationToken.class);
            query.select((Selection)token.get("synchronisationToken"));
            query.where((Expression)builder.equal((Expression)token.get("directoryId"), (Object)directoryId));
            return session.createQuery(query).getResultList();
        })));
    }

    public void storeSynchronisationTokenForDirectory(long directoryId, String synchronisationToken) throws DirectoryNotFoundException {
        Directory directory = this.directoryDao.findById(directoryId);
        this.transactionTemplate.execute(() -> this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(DirectorySynchronisationToken.class);
            Root tokenRoot = query.from(DirectorySynchronisationToken.class);
            query.where((Expression)builder.equal((Expression)tokenRoot.get("directoryId"), (Object)directoryId));
            DirectorySynchronisationToken token = (DirectorySynchronisationToken)DataAccessUtils.singleResult((Collection)session.createQuery(query).getResultList());
            if (token == null) {
                token = new DirectorySynchronisationToken(directory, synchronisationToken);
            } else {
                token.setSynchronisationToken(synchronisationToken);
            }
            session.saveOrUpdate((Object)token);
            return null;
        }));
    }

    public void clearSynchronisationTokenForDirectory(long directoryId) {
        this.transactionTemplate.execute(() -> (Long)this.hibernateTemplate.execute(session -> {
            Optional.ofNullable((DirectorySynchronisationToken)session.get(DirectorySynchronisationToken.class, (Serializable)Long.valueOf(directoryId))).ifPresent(arg_0 -> ((Session)session).remove(arg_0));
            return directoryId;
        }));
    }
}

