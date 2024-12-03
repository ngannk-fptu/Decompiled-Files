/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.google.common.collect.Lists
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.user.dao;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.security.denormalisedpermissions.impl.user.domain.DenormalisedSid;
import com.atlassian.confluence.security.denormalisedpermissions.impl.user.domain.DenormalisedSidType;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DenormalisedSidDao {
    private static final int DEFAULT_SAFE_IN_CLAUSE_LIMIT = 1000;
    private final HibernateConfig hibernateConfig;
    private final HibernateTemplate hibernateTemplate;
    private final int IN_CLAUSE_LIMIT;

    public DenormalisedSidDao(SessionFactory sessionFactory, HibernateConfig hibernateConfig) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.hibernateConfig = hibernateConfig;
        this.IN_CLAUSE_LIMIT = this.calculateLimitForCurrentDatabase();
    }

    public List<DenormalisedSid> getExistingSids(Set<String> names, DenormalisedSidType type) {
        if (names.isEmpty()) {
            return Collections.emptyList();
        }
        List partitions = Lists.partition(new ArrayList<String>(names), (int)this.IN_CLAUSE_LIMIT);
        ArrayList<DenormalisedSid> finalList = new ArrayList<DenormalisedSid>();
        partitions.forEach(currentBatchOfNames -> finalList.addAll((Collection)this.hibernateTemplate.execute(session -> {
            String hql = "from DenormalisedSid sid where sid.type = :type and sid.name in (:names)";
            Query query = session.createQuery("from DenormalisedSid sid where sid.type = :type and sid.name in (:names)");
            query.setParameter("type", (Object)type);
            query.setParameterList("names", (Collection)currentBatchOfNames);
            query.setCacheable(false);
            return query.list();
        })));
        return finalList;
    }

    public List<Long> getExistingSidIdList(Set<String> names, DenormalisedSidType type) {
        if (names.isEmpty()) {
            return Collections.emptyList();
        }
        List partitions = Lists.partition(new ArrayList<String>(names), (int)this.IN_CLAUSE_LIMIT);
        ArrayList<Long> finalList = new ArrayList<Long>();
        partitions.forEach(currentBatchOfNames -> finalList.addAll((Collection)this.hibernateTemplate.execute(session -> {
            String hql = "select id from DenormalisedSid sid where sid.type = :type and sid.name in (:names)";
            Query query = session.createQuery("select id from DenormalisedSid sid where sid.type = :type and sid.name in (:names)");
            query.setParameter("type", (Object)type);
            query.setParameterList("names", (Collection)currentBatchOfNames);
            query.setCacheable(true);
            return query.list();
        })));
        return finalList;
    }

    public long addNewSid(String name, DenormalisedSidType type) {
        DenormalisedSid denormalisedSid = new DenormalisedSid();
        denormalisedSid.setName(name);
        denormalisedSid.setType(type);
        this.hibernateTemplate.save((Object)denormalisedSid);
        return denormalisedSid.getId();
    }

    private int calculateLimitForCurrentDatabase() {
        return this.hibernateConfig.isPostgreSql() ? Integer.MAX_VALUE : 1000;
    }
}

