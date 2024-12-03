/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DirectorySynchronisationStatusImpl
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.spi.DirectorySynchronisationStatusDao
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.crowd.model.directory.DirectorySynchronisationStatus
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Preconditions
 *  javax.persistence.criteria.CriteriaBuilder
 *  javax.persistence.criteria.CriteriaDelete
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Order
 *  javax.persistence.criteria.Root
 *  javax.persistence.criteria.Subquery
 *  org.hibernate.SessionFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.crowd.directory.DirectorySynchronisationStatusImpl;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.spi.DirectorySynchronisationStatusDao;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.crowd.model.directory.DirectorySynchronisationStatus;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;

public final class HibernateDirectorySynchronisationStatusDao
implements DirectorySynchronisationStatusDao {
    private final HibernateTemplate hibernateTemplate;
    private final TransactionTemplate transactionTemplate;

    public HibernateDirectorySynchronisationStatusDao(SessionFactory sessionFactory, TransactionTemplate transactionTemplate) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.transactionTemplate = transactionTemplate;
    }

    public Optional<DirectorySynchronisationStatus> findActiveForDirectory(long directoryId) {
        DirectorySynchronisationStatusImpl result = (DirectorySynchronisationStatusImpl)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(DirectorySynchronisationStatusImpl.class);
            Root status = query.from(DirectorySynchronisationStatusImpl.class);
            query.where((Expression)builder.and((Expression)builder.equal((Expression)status.get("directory").get("id"), (Object)directoryId), (Expression)builder.isNull((Expression)status.get("endTimestamp"))));
            query.orderBy(new Order[]{builder.desc((Expression)status.get("startTimestamp"))});
            return (DirectorySynchronisationStatusImpl)session.createQuery(query).setMaxResults(1).uniqueResult();
        });
        return Optional.ofNullable(result);
    }

    public Optional<DirectorySynchronisationStatus> findLastForDirectory(long directoryId) {
        return Optional.ofNullable((DirectorySynchronisationStatus)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(DirectorySynchronisationStatusImpl.class);
            Subquery subquery = query.subquery(Long.class);
            Root subStatus = subquery.from(DirectorySynchronisationStatusImpl.class);
            subquery.select(builder.max((Expression)subStatus.get("endTimestamp")));
            subquery.where((Expression)builder.equal((Expression)subStatus.get("directory").get("id"), (Object)directoryId));
            Root status = query.from(DirectorySynchronisationStatusImpl.class);
            query.where((Expression)builder.and((Expression)builder.equal((Expression)status.get("directory").get("id"), (Object)directoryId), (Expression)builder.equal((Expression)status.get("endTimestamp"), (Expression)subquery)));
            return (DirectorySynchronisationStatusImpl)session.createQuery(query).uniqueResult();
        }));
    }

    public DirectorySynchronisationStatus add(DirectorySynchronisationStatus status) {
        Preconditions.checkArgument((status.getId() == null ? 1 : 0) != 0, (Object)"Tried to create DirectorySynchronisationStatus with id specified");
        return (DirectorySynchronisationStatus)this.transactionTemplate.execute(() -> (DirectorySynchronisationStatusImpl)this.hibernateTemplate.execute(session -> {
            Directory directory = (Directory)session.get(DirectoryImpl.class, (Serializable)status.getDirectory().getId());
            DirectorySynchronisationStatusImpl newStatus = DirectorySynchronisationStatusImpl.builder((DirectorySynchronisationStatus)status).setDirectory(directory).build();
            session.save((Object)newStatus);
            return newStatus;
        }));
    }

    public DirectorySynchronisationStatus update(DirectorySynchronisationStatus status) throws ObjectNotFoundException {
        DirectorySynchronisationStatus updated = (DirectorySynchronisationStatus)this.transactionTemplate.execute(() -> (DirectorySynchronisationStatusImpl)this.hibernateTemplate.execute(session -> {
            DirectorySynchronisationStatusImpl impl = (DirectorySynchronisationStatusImpl)session.find(DirectorySynchronisationStatusImpl.class, (Object)status.getId());
            if (impl == null) {
                return null;
            }
            impl.setStartTimestamp(status.getStartTimestamp());
            impl.setEndTimestamp(status.getEndTimestamp());
            impl.setStatus(status.getStatus());
            impl.setStatusParameters(status.getStatusParameters());
            impl.setNodeId(status.getNodeId());
            impl.setNodeName(status.getNodeName());
            impl.setIncrementalSyncError(status.getIncrementalSyncError());
            impl.setFullSyncError(status.getFullSyncError());
            session.update((Object)impl);
            return impl;
        }));
        if (updated == null) {
            throw new ObjectNotFoundException();
        }
        return updated;
    }

    public long removeStatusesForDirectory(Long directoryId) {
        return ((Integer)this.transactionTemplate.execute(() -> (Integer)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete delete = builder.createCriteriaDelete(DirectorySynchronisationStatusImpl.class);
            Root status = delete.from(DirectorySynchronisationStatusImpl.class);
            delete.where((Expression)builder.equal((Expression)status.get("directory").get("id"), (Object)directoryId));
            return session.createQuery(delete).executeUpdate();
        }))).intValue();
    }

    public long removeAll() {
        return ((Integer)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete delete = builder.createCriteriaDelete(DirectorySynchronisationStatusImpl.class);
            delete.from(DirectorySynchronisationStatusImpl.class);
            return session.createQuery(delete).executeUpdate();
        })).intValue();
    }

    public long removeAllExcept(long directoryId, int statusId) {
        return ((Integer)this.transactionTemplate.execute(() -> (Integer)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete delete = builder.createCriteriaDelete(DirectorySynchronisationStatusImpl.class);
            Root status = delete.from(DirectorySynchronisationStatusImpl.class);
            delete.where((Expression)builder.and((Expression)builder.equal((Expression)status.get("directory").get("id"), (Object)directoryId), (Expression)builder.notEqual((Expression)status.get("id"), (Object)statusId)));
            return session.createQuery(delete).executeUpdate();
        }))).intValue();
    }

    public Collection<DirectorySynchronisationStatus> findActiveSyncsWhereNodeIdNotIn(Set<String> nodesIds) {
        List result = (List)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(DirectorySynchronisationStatusImpl.class);
            Root status = query.from(DirectorySynchronisationStatusImpl.class);
            query.where((Expression)builder.and((Expression)builder.not((Expression)status.get("nodeId").in((Collection)nodesIds)), (Expression)builder.isNull((Expression)status.get("endTimestamp"))));
            return session.createQuery(query).getResultList();
        });
        if (result == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(result);
    }
}

