/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.core.exception.InfrastructureException
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableSet
 *  org.hibernate.Criteria
 *  org.hibernate.criterion.Criterion
 *  org.hibernate.criterion.Disjunction
 *  org.hibernate.criterion.Expression
 *  org.hibernate.criterion.Junction
 *  org.hibernate.criterion.MatchMode
 *  org.hibernate.criterion.Order
 *  org.hibernate.query.Query
 */
package com.atlassian.confluence.internal.audit.persistence.dao.hibernate;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.impl.audit.AuditRecordEntity;
import com.atlassian.confluence.impl.audit.AuditSearchUtils;
import com.atlassian.confluence.internal.audit.persistence.dao.AuditRecordDao;
import com.atlassian.core.exception.InfrastructureException;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.query.Query;

@Deprecated
public class HibernateAuditRecordDao
extends HibernateObjectDao
implements AuditRecordDao {
    @Override
    public void storeRecord(AuditRecordEntity auditRecordEntity) {
        if (auditRecordEntity.getCreationDate().isAfter(Instant.now())) {
            throw new IllegalArgumentException("AuditRecordEntity creation date cannot be in the future");
        }
        this.getHibernateTemplate().save((Object)auditRecordEntity);
    }

    @Override
    public PageResponse<AuditRecordEntity> getRecords(LimitedRequest request, Instant startDate, Instant endDate, boolean includeSysAdmin, String searchString) {
        List<AuditRecordEntity> results = this.getRecordsInternal(request.getStart(), request.getLimit() + 1, startDate, endDate, includeSysAdmin, searchString);
        return PageResponseImpl.filteredResponse((LimitedRequest)request, results, (Predicate)Predicates.alwaysTrue());
    }

    @Override
    public List<Long> fetchAllRecordIds() {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createNamedQuery("confluence.auditRecord_fetchAllIds", Long.class);
            query.setCacheable(false);
            return query.list();
        });
    }

    @Override
    public List<AuditRecordEntity> fetchByIds(List<Long> ids) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createNamedQuery("confluence.auditRecord_fetchByIds", AuditRecordEntity.class);
            query.setCacheable(false);
            query.setParameterList("ids", (Collection)ids);
            return query.list();
        });
    }

    @Override
    public void deleteRecords(Collection<AuditRecordEntity> recordEntities) {
        recordEntities.forEach(this::remove);
    }

    @Override
    public void cleanOldRecords(Instant before) {
        int batch_size = 1000;
        boolean has_more = true;
        while (has_more) {
            List<AuditRecordEntity> results = this.getRecordsInternal(0, 1000, Instant.EPOCH, before, true, "");
            has_more = results.size() >= 1000;
            results.stream().forEach(this::remove);
        }
    }

    private List<AuditRecordEntity> getRecordsInternal(int start, int limit, Instant startDate, Instant endDate, boolean includeSysAdmin, String searchString) {
        ImmutableSet<String> tokens = AuditSearchUtils.tokenize(searchString);
        return (List)this.getHibernateTemplate().execute(session -> {
            Criteria criteria = session.createCriteria(AuditRecordEntity.class);
            criteria.add(Expression.between((String)"creationDate", (Object)startDate, (Object)endDate));
            if (!includeSysAdmin) {
                criteria.add((Criterion)Expression.eq((String)"sysAdmin", (Object)false));
            }
            if (!tokens.isEmpty()) {
                Disjunction junction = Expression.disjunction();
                tokens.stream().forEach(arg_0 -> HibernateAuditRecordDao.lambda$getRecordsInternal$2((Junction)junction, arg_0));
                criteria.add((Criterion)junction);
            }
            if (start > 0) {
                criteria.setFirstResult(start);
            }
            if (limit > 0) {
                criteria.setMaxResults(limit);
            }
            criteria.addOrder(Order.desc((String)"creationDate"));
            criteria.setCacheable(false);
            HibernateAuditRecordDao.applyTransactionTimeout(criteria, this.getSessionFactory());
            return criteria.list();
        });
    }

    private void remove(AuditRecordEntity objectToRemove) {
        try {
            this.getHibernateTemplate().delete((Object)objectToRemove);
        }
        catch (Exception e) {
            throw new InfrastructureException((Throwable)e);
        }
    }

    @Override
    public Class getPersistentClass() {
        return AuditRecordEntity.class;
    }

    private static /* synthetic */ void lambda$getRecordsInternal$2(Junction junction, String token) {
        junction.add((Criterion)Expression.like((String)"searchString", (String)token, (MatchMode)MatchMode.ANYWHERE));
    }
}

