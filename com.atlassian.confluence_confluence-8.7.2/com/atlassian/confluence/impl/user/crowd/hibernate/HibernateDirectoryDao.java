/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  javax.persistence.criteria.CriteriaBuilder
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Root
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateSearch;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalGroupDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalMembershipDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalUserDao;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;

public final class HibernateDirectoryDao
implements DirectoryDao {
    private final HibernateTemplate hibernateTemplate;
    private final InternalUserDao userDao;
    private final InternalGroupDao groupDao;
    private final InternalMembershipDao internalMembershipDao;

    public HibernateDirectoryDao(SessionFactory sessionFactory, InternalUserDao userDao, InternalGroupDao groupDao, InternalMembershipDao internalMembershipDao) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.internalMembershipDao = internalMembershipDao;
    }

    public DirectoryImpl findById(long directoryId) throws DirectoryNotFoundException {
        Object directory = this.hibernateTemplate.get(DirectoryImpl.class, (Serializable)Long.valueOf(directoryId));
        if (directory == null) {
            throw new DirectoryNotFoundException(Long.valueOf(directoryId));
        }
        return (DirectoryImpl)directory;
    }

    public Directory findByName(String name) throws DirectoryNotFoundException {
        return (Directory)this.find(DirectoryImpl.class, Query::uniqueResultOptional, (builder, query) -> {
            Root directory = query.from(DirectoryImpl.class);
            query.where((Expression)builder.equal((Expression)directory.get("lowerName"), (Object)IdentifierUtils.toLowerCase((String)name)));
        }).orElseThrow(() -> new DirectoryNotFoundException(name));
    }

    public List<Directory> findAll() {
        return (List)this.hibernateTemplate.execute(session -> {
            Query query = session.createQuery("from DirectoryImpl");
            query.setCacheable(true);
            return query.list();
        });
    }

    public Directory add(Directory directory) {
        DirectoryImpl directoryToPersist = new DirectoryImpl(directory);
        directoryToPersist.setCreatedDateToNow();
        directoryToPersist.setUpdatedDateToNow();
        directoryToPersist.validate();
        this.hibernateTemplate.save((Object)directoryToPersist);
        return directoryToPersist;
    }

    public Directory update(Directory directory) throws DirectoryNotFoundException {
        DirectoryImpl directoryToUpdate = this.findById(directory.getId());
        directoryToUpdate.setUpdatedDateToNow();
        directoryToUpdate.updateDetailsFrom(directory);
        directoryToUpdate.validate();
        Iterator attrItr = directoryToUpdate.getAttributes().entrySet().iterator();
        while (attrItr.hasNext()) {
            Map.Entry attr = attrItr.next();
            if (attr.getValue() != null && !((String)attr.getValue()).equals("")) continue;
            attrItr.remove();
        }
        this.hibernateTemplate.update((Object)directoryToUpdate);
        return directoryToUpdate;
    }

    public void remove(Directory directory) {
        this.internalMembershipDao.removeAllRelationships(directory);
        this.groupDao.removeAllGroups(directory.getId());
        this.userDao.removeAllUsers(directory.getId());
        this.hibernateTemplate.delete((Object)directory);
        this.hibernateTemplate.flush();
    }

    public List<Directory> search(EntityQuery<Directory> query) {
        if (query.getEntityDescriptor().getEntityType() != Entity.DIRECTORY) {
            throw new IllegalArgumentException("DirectoryDAO can only evaluate EntityQueries for Entity.DIRECTORY");
        }
        return (List)this.hibernateTemplate.executeWithNativeSession(HibernateSearch.forEntities(query)::doInHibernate);
    }

    private <T, O> O find(Class<T> returnType, Function<Query<T>, O> resultExtractor, BiConsumer<CriteriaBuilder, CriteriaQuery<T>> queryPopulator) {
        return (O)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(returnType);
            queryPopulator.accept(builder, query);
            return resultExtractor.apply(session.createQuery(query));
        });
    }
}

